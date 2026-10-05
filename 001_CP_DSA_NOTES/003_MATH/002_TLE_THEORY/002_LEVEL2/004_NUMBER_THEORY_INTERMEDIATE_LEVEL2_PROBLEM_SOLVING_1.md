# Number Theory — Intermediate Level 2
## Problem Solving 2: Mere Array, Strong Elements & Xor Pyramid

> **Goal:** understand the observation behind each solution instead of memorizing the final condition.
>
> **Flow:** prerequisites → what the problem asks → model → derivation → dry run → C++ → recognition.
>
> **Math rendering:** all display formulas use fenced `math` blocks to avoid broken `$$` rendering.

---

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
  - [0.1 GCD Basics](#01-gcd-basics)
  - [0.2 Divisibility](#02-divisibility)
  - [0.3 Sorting and Target Array](#03-sorting-and-target-array)
  - [0.4 Prefix and Suffix GCD](#04-prefix-and-suffix-gcd)
  - [0.5 XOR Basics](#05-xor-basics)
  - [0.6 Binomial Coefficients](#06-binomial-coefficients)
  - [0.7 Pascal Triangle](#07-pascal-triangle)
  - [0.8 Power of 2 in a Factorial](#08-power-of-2-in-a-factorial)
- [1. Mere Array — CF 1401C](#1-mere-array--cf-1401c)
- [2. Strong Elements — CodeChef STRNG](#2-strong-elements--codechef-strng)
- [3. Xor Pyramid — CSES 2419](#3-xor-pyramid--cses-2419)
- [4. Final Recognition Sheet](#4-final-recognition-sheet)
- [5. Master Don't-Memorize Model](#5-master-dont-memorize-model)

---

# 0. Prerequisites

# 0.1 GCD Basics

`gcd(a,b)` is the greatest positive integer dividing both `a` and `b`.

Example:

```text
a = 8
b = 12

common divisors = 1, 2, 4

gcd(8,12) = 4
```

Useful properties:

```math
\gcd(a,0)=a
```

```math
\gcd(a,b,c)=\gcd(\gcd(a,b),c)
```

GCD is associative, so we can combine many values one by one.

C++:

```cpp
long long g = std::gcd(a, b);
```

---

# 0.2 Divisibility

`x` divides `y` when:

```text
y % x == 0
```

Mathematically:

```math
x \mid y
```

Example:

```text
2 divides 8
because
8 % 2 = 0
```

Important GCD observation:

If `m` divides `x`, then:

```math
\gcd(m,x)=m
```

provided `m <= x`.

Example:

```text
m = 2
x = 8

gcd(2,8) = 2
```

This becomes the key observation in **Mere Array**.

---

# 0.3 Sorting and Target Array

If the problem asks:

```text
Can I make the array non-decreasing?
```

a useful technique is:

```text
1. Copy the array.
2. Sort the copy.
3. Compare current position with required position.
```

Example:

```text
original:
4 3 6 6 2 9

sorted target:
2 3 4 6 6 9
```

Then ask:

```text
Which mismatched elements are actually movable?
```

This is more useful than blindly simulating swaps.

---

# 0.4 Prefix and Suffix GCD

Suppose:

```text
a = [a0, a1, a2, ..., a(n-1)]
```

Prefix GCD:

```text
pref[i] = gcd of elements before/through a position
```

A convenient implementation is:

```text
pref[0] = 0

pref[1] = gcd(a0)
pref[2] = gcd(a0,a1)
pref[3] = gcd(a0,a1,a2)
...
```

C++:

```cpp
pref[i + 1] = gcd(pref[i], a[i]);
```

Suffix GCD:

```text
suff[n] = 0

suff[i] = gcd(a[i], a[i+1], ..., a[n-1])
```

C++:

```cpp
suff[i] = gcd(a[i], suff[i + 1]);
```

Why initialize with `0`?

Because:

```math
\gcd(x,0)=x
```

---

## GCD of the Array Except Index i

Suppose we remove `a[i]`.

Left side GCD:

```text
pref[i]
```

Right side GCD:

```text
suff[i+1]
```

Therefore:

```math
G_{\text{rest}}
=
\gcd(pref[i],suff[i+1])
```

This gives the GCD of **all elements except `a[i]` in O(1)**.

This is the main prerequisite for **Strong Elements**.

---

# 0.5 XOR Basics

XOR is written as:

```text
^
```

in C++.

Important identities:

```math
x \oplus x = 0
```

```math
x \oplus 0 = x
```

XOR is associative:

```math
(a\oplus b)\oplus c
=
a\oplus(b\oplus c)
```

and commutative:

```math
a\oplus b=b\oplus a
```

---

## Even/Odd Occurrence Rule

If the same value is XORed an even number of times:

```text
x ^ x = 0
```

so it disappears.

If it appears an odd number of times:

```text
x ^ x ^ x
= 0 ^ x
= x
```

Therefore:

```text
even occurrences → cancel
odd occurrences  → survive
```

This is the central observation in **Xor Pyramid**.

---

# 0.6 Binomial Coefficients

The binomial coefficient:

```math
\binom{n}{r}
```

means the number of ways to choose `r` items from `n`.

Formula:

```math
\binom{n}{r}
=
\frac{n!}{r!(n-r)!}
```

Example:

```math
\binom{4}{2}
=
\frac{4!}{2!2!}
=
6
```

For Xor Pyramid, we do **not** need the full value.

We only need:

```text
Is C(n,r) odd or even?
```

---

# 0.7 Pascal Triangle

Pascal triangle starts:

```text
            1
          1   1
        1   2   1
      1   3   3   1
    1   4   6   4   1
```

Row `k` contains:

```math
\binom{k}{0},
\binom{k}{1},
\binom{k}{2},
\dots,
\binom{k}{k}
```

These are also the coefficients of:

```math
(a+b)^k
```

Example:

```math
(a+b)^3
=
a^3+3a^2b+3ab^2+b^3
```

Coefficients:

```text
1 3 3 1
```

The Xor Pyramid produces exactly this same coefficient pattern.

---

# 0.8 Power of 2 in a Factorial

We will use:

```text
v2(n!) = number of factors of 2 inside n!
```

Example:

```text
4! = 24
   = 2³ × 3

v2(4!) = 3
```

Formula:

```math
v_2(n!)
=
\left\lfloor\frac n2\right\rfloor
+
\left\lfloor\frac n4\right\rfloor
+
\left\lfloor\frac n8\right\rfloor
+\cdots
```

Why?

```text
multiples of 2 → contribute at least one 2
multiples of 4 → contribute one extra 2
multiples of 8 → contribute another extra 2
...
```

Example:

```text
n = 8

floor(8/2) = 4
floor(8/4) = 2
floor(8/8) = 1

v2(8!) = 4 + 2 + 1 = 7
```

C++:

```cpp
long long powerOf2InFactorial(long long n) {
    long long cnt = 0;

    while (n > 0) {
        n /= 2;
        cnt += n;
    }

    return cnt;
}
```

This will let us test whether a binomial coefficient is odd.

---

# 1. Mere Array — CF 1401C

**Problem Link:** https://codeforces.com/problemset/problem/1401/C

The lecture's first problem is **Mere Array**. fileciteturn31file0L5-L8

---

## 1.1 What Does the Problem Ask?

We have an array:

```text
a[0], a[1], ..., a[n-1]
```

Let:

```text
mn = minimum value in the whole array
```

We may swap `a[i]` and `a[j]` only when:

```math
\gcd(a_i,a_j)=mn
```

Question:

```text
Can we make the array non-decreasing?
```

---

# 1.2 First Observation — The Minimum Is Special

Let:

```text
mn = minimum element
```

Take an element `x` such that:

```text
x is divisible by mn
```

Then:

```math
x = k\cdot mn
```

Therefore:

```math
\gcd(x,mn)=mn
```

So `x` can swap with the minimum element.

Example:

```text
mn = 2

x = 8

gcd(2,8) = 2
```

So `8` is movable through the minimum `2`.

---

# 1.3 Which Elements Are Movable?

If:

```text
a[i] % mn == 0
```

then `a[i]` can interact with the minimum.

These elements are **movable**.

If:

```text
a[i] % mn != 0
```

then `mn` does not divide `a[i]`.

Could `a[i]` participate in an allowed swap?

An allowed swap requires:

```math
\gcd(a_i,a_j)=mn
```

But if the GCD were `mn`, then `mn` would divide `a[i]`.

Contradiction.

Therefore:

```text
not divisible by mn
→ cannot take part in any valid swap
→ fixed at its current position
```

This is the key observation.

---

# 1.4 Why All Multiples of mn Can Be Rearranged

Suppose:

```text
x % mn = 0
y % mn = 0
```

Both can swap with the minimum.

So the minimum acts like a temporary buffer:

```text
x   y   mn
```

Swap `y` with `mn`:

```text
x   mn  y
```

Swap `x` with `mn`:

```text
mn  x   y
```

Swap the old minimum position appropriately:

```text
y   x   mn
```

Conceptually:

```text
all elements divisible by mn
can be permuted among their positions
```

We do not need to construct the swaps.

---

# 1.5 Transform Into a Target Check

Create:

```text
b = sorted copy of a
```

For every position `i`:

### If already correct

```text
a[i] == b[i]
```

nothing needs to move.

### If incorrect

```text
a[i] != b[i]
```

then the current value must be movable:

```text
a[i] % mn == 0
```

If not:

```text
NO
```

Otherwise continue.

---

# 1.6 Dry Run — YES

Input:

```text
a = [4, 3, 6, 6, 2, 9]
```

Minimum:

```text
mn = 2
```

Sorted target:

```text
b = [2, 3, 4, 6, 6, 9]
```

Compare:

| i | `a[i]` | `b[i]` | mismatch? | `a[i] % 2` | movable? |
|---:|---:|---:|:---:|---:|:---:|
| 0 | 4 | 2 | Yes | 0 | Yes |
| 1 | 3 | 3 | No | 1 | irrelevant |
| 2 | 6 | 4 | Yes | 0 | Yes |
| 3 | 6 | 6 | No | 0 | Yes |
| 4 | 2 | 6 | Yes | 0 | Yes |
| 5 | 9 | 9 | No | 1 | irrelevant |

Every mismatched value is movable.

Therefore:

```text
YES
```

Notice:

```text
3 and 9 are not divisible by 2
```

but they are already in their required sorted positions, so they do not need to move.

---

# 1.7 Dry Run — NO

Take:

```text
a = [7, 5, 2, 2, 4]
```

Minimum:

```text
mn = 2
```

Sorted target:

```text
b = [2, 2, 4, 5, 7]
```

At position `0`:

```text
a[0] = 7
b[0] = 2
```

So `7` must move.

But:

```text
7 % 2 = 1
```

Therefore `7` is frozen.

So sorting is impossible:

```text
NO
```

---

# 1.8 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        vector<long long> a(n);

        for (auto &x : a)
            cin >> x;

        vector<long long> b = a;
        sort(b.begin(), b.end());

        long long mn = *min_element(a.begin(), a.end());

        bool ok = true;

        for (int i = 0; i < n; ++i) {
            if (a[i] != b[i] && a[i] % mn != 0) {
                ok = false;
                break;
            }
        }

        cout << (ok ? "YES\n" : "NO\n");
    }
}
```

Complexity:

```text
sorting: O(n log n)
check:   O(n)

total:   O(n log n)
```

---

# 1.9 Don't-Memorize Model

Do not memorize:

```text
if mismatch and a[i] % min != 0 → NO
```

Derive it:

```text
swap allowed only when gcd = global minimum
                    |
                    v
which values can interact with minimum?
                    |
                    v
multiples of minimum
                    |
        +-----------+-----------+
        |                       |
 divisible by min         not divisible
        |                       |
     movable                  frozen
        |
        v
compare against sorted target
        |
        v
every mismatch must be movable
```

---

# 2. Strong Elements — CodeChef STRNG

**Problem Link:** https://www.codechef.com/practice/course/number-theory/INTNT01/problems/STRNG

The lecture's second problem is **Strong Elements**. fileciteturn31file0L16-L18

---

# 2.1 What Is a Strong Index?

We have:

```text
a[0], a[1], ..., a[n-1]
```

Index `i` is **strong** if changing only `a[i]` can change the GCD of the entire array.

We need:

```text
number of strong indices
```

A brute-force idea would be:

```text
for every i:
    remove/change a[i]
    recompute gcd of all other elements
```

That would cost roughly:

```text
O(n²)
```

We need to avoid recomputing GCD from scratch.

---

# 2.2 First Case — Overall GCD Is Not 1

Let:

```math
G=\gcd(a_0,a_1,\dots,a_{n-1})
```

Suppose:

```text
G != 1
```

Pick any index `i`.

Change:

```text
a[i] = 1
```

Then the new GCD becomes:

```math
\gcd(\dots,1,\dots)=1
```

The old GCD was greater than `1`.

So the GCD changed.

Therefore:

```text
if overall GCD != 1
→ every index is strong
→ answer = n
```

---

# 2.3 Hard Case — Overall GCD Is 1

Now suppose:

```math
G=1
```

For index `i`, let:

```text
restGCD = gcd of every element except a[i]
```

Call it:

```math
R_i
```

After changing `a[i]` to some new value `x`, the new array GCD is:

```math
\gcd(R_i,x)
```

Now there are two possibilities.

---

## Case A — restGCD = 1

```math
R_i=1
```

Then for every possible new value `x`:

```math
\gcd(1,x)=1
```

So the whole-array GCD remains `1`.

Therefore index `i` is:

```text
NOT strong
```

---

## Case B — restGCD > 1

Suppose:

```math
R_i>1
```

Set:

```text
new a[i] = R_i
```

Then:

```math
\gcd(R_i,R_i)=R_i
```

So the new whole-array GCD becomes greater than `1`.

Old GCD:

```text
1
```

New GCD:

```text
R_i > 1
```

Therefore index `i` is:

```text
strong
```

So when overall GCD is `1`:

```text
index i is strong
iff
gcd(all elements except i) > 1
```

---

# 2.4 How to Get GCD Except i in O(1)

Build:

```text
prefix GCD
suffix GCD
```

Using:

```text
pref[0] = 0
pref[i+1] = gcd(pref[i], a[i])
```

and:

```text
suff[n] = 0
suff[i] = gcd(a[i], suff[i+1])
```

Then:

```math
R_i
=
\gcd(pref[i],suff[i+1])
```

This is exactly the GCD of everything except `a[i]`.

---

# 2.5 Dry Run — Only One Strong Index

Take:

```text
a = [2, 3, 4, 6, 10]
```

Overall:

```math
\gcd(2,3,4,6,10)=1
```

So inspect each index.

### Remove 2

Remaining:

```text
3,4,6,10
```

GCD:

```text
1
```

Not strong.

### Remove 3

Remaining:

```text
2,4,6,10
```

GCD:

```text
2
```

Greater than `1`.

So index of `3` is strong.

### Remove 4

Remaining:

```text
2,3,6,10
```

GCD:

```text
1
```

Not strong.

### Remove 6

Remaining GCD:

```text
1
```

Not strong.

### Remove 10

Remaining GCD:

```text
1
```

Not strong.

Answer:

```text
1
```

---

# 2.6 Prefix/Suffix Dry Run

For:

```text
a = [2, 3, 4, 6, 10]
```

Prefix:

```text
pref[0] = 0
pref[1] = gcd(0,2)   = 2
pref[2] = gcd(2,3)   = 1
pref[3] = gcd(1,4)   = 1
pref[4] = gcd(1,6)   = 1
pref[5] = gcd(1,10)  = 1
```

Suffix:

```text
suff[5] = 0
suff[4] = gcd(10,0)   = 10
suff[3] = gcd(6,10)   = 2
suff[2] = gcd(4,2)    = 2
suff[1] = gcd(3,2)    = 1
suff[0] = gcd(2,1)    = 1
```

For `i = 1`, where:

```text
a[1] = 3
```

GCD excluding index `1`:

```math
R_1
=
\gcd(pref[1],suff[2])
```

```math
R_1
=
\gcd(2,2)
=
2
```

So index `1` is strong.

---

# 2.7 Dry Run — Overall GCD > 1

Take:

```text
a = [6, 10, 14]
```

Overall:

```math
\gcd(6,10,14)=2
```

Since overall GCD is not `1`:

```text
change any chosen element to 1
→ new GCD becomes 1
```

Therefore:

```text
all 3 indices are strong
```

Answer:

```text
3
```

---

# 2.8 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n;
        cin >> n;

        vector<long long> a(n);

        for (auto &x : a)
            cin >> x;

        vector<long long> pref(n + 1, 0);
        vector<long long> suff(n + 1, 0);

        for (int i = 0; i < n; ++i)
            pref[i + 1] = std::gcd(pref[i], a[i]);

        for (int i = n - 1; i >= 0; --i)
            suff[i] = std::gcd(a[i], suff[i + 1]);

        long long totalGCD = pref[n];

        if (totalGCD != 1) {
            cout << n << '\n';
            continue;
        }

        int strong = 0;

        for (int i = 0; i < n; ++i) {
            long long restGCD =
                std::gcd(pref[i], suff[i + 1]);

            if (restGCD > 1)
                ++strong;
        }

        cout << strong << '\n';
    }
}
```

Complexity:

```text
prefix GCD:  O(n log A)
suffix GCD:  O(n log A)
all checks:  O(n log A)

overall:     O(n log A)
```

where `A` is the maximum array value.

---

# 2.9 Don't-Memorize Model

```text
Can changing a[i] change whole-array GCD?
                    |
                    v
What part can a[i] NOT control?
                    |
                    v
GCD of all OTHER elements
                    |
          +---------+---------+
          |                   |
      restGCD = 1         restGCD > 1
          |                   |
new gcd always 1        choose new a[i]
          |              = restGCD
          |                   |
      not strong            strong
```

Then optimize:

```text
need gcd excluding every index
        |
        v
prefix GCD + suffix GCD
        |
        v
O(1) exclusion query
```

---

# 3. Xor Pyramid — CSES 2419

**Problem Link:** https://cses.fi/problemset/task/2419

The lecture's third problem is **Xor Pyramid**. fileciteturn31file0L26-L28

---

# 3.1 What Does the Problem Ask?

The bottom row contains:

```text
a0 a1 a2 ... a(n-1)
```

Every number above is the XOR of the two numbers directly below it.

Example:

```text
bottom:
a0   a1   a2   a3

next:
 a0^a1   a1^a2   a2^a3

next:
   ...       ...

top:
one value
```

We need the top value.

Direct simulation takes:

```text
n + (n-1) + ... + 1
```

operations:

```math
O(n^2)
```

Too slow for large `n`.

We need to determine how many times each bottom value contributes to the top.

---

# 3.2 Start With Small n

## n = 2

Bottom:

```text
a0   a1
```

Top:

```math
a_0\oplus a_1
```

Coefficients:

```text
1 1
```

This is Pascal row:

```text
1 1
```

---

## n = 3

Bottom:

```text
a0   a1   a2
```

Second row:

```text
a0^a1     a1^a2
```

Top:

```math
(a_0\oplus a_1)
\oplus
(a_1\oplus a_2)
```

Rearrange:

```math
a_0
\oplus
a_1
\oplus
a_1
\oplus
a_2
```

Since:

```math
a_1\oplus a_1=0
```

top becomes:

```math
a_0\oplus a_2
```

The contribution counts before cancellation are:

```text
1 2 1
```

which is Pascal row:

```text
1 2 1
```

---

## n = 4

The contribution counts are:

```text
1 3 3 1
```

So top is conceptually:

```text
a0 repeated 1 time
a1 repeated 3 times
a2 repeated 3 times
a3 repeated 1 time
```

All counts are odd.

Therefore:

```math
top
=
a_0\oplus a_1\oplus a_2\oplus a_3
```

---

# 3.3 General Formula

For an array of length `n`, the number of times `a[i]` contributes is:

```math
\binom{n-1}{i}
```

Therefore the top is:

```text
XOR a[i]
only when C(n-1,i) is odd
```

Why only parity?

Because XOR obeys:

```text
even copies → cancel
odd copies  → one copy remains
```

So we do **not** need the actual binomial coefficient.

We only need:

```text
odd or even?
```

---

# 3.4 How to Test Whether nCr Is Odd

Let:

```math
N=n-1
```

Coefficient for index `i`:

```math
\binom{N}{i}
=
\frac{N!}{i!(N-i)!}
```

A number is even if its prime factorisation contains at least one factor `2`.

So compute the power of `2` in the coefficient.

Let:

```math
c
=
v_2(N!)
-
v_2(i!)
-
v_2((N-i)!)
```

Then:

```text
c = 0  → coefficient is odd
c > 0  → coefficient is even
```

Therefore:

```text
if c == 0:
    answer ^= a[i]
```

---

# 3.5 Why This Works

Suppose:

```math
\binom{N}{i}
=
\frac{N!}{i!(N-i)!}
```

Count powers of `2`.

Numerator contributes:

```math
v_2(N!)
```

Denominator removes:

```math
v_2(i!)
+
v_2((N-i)!)
```

So remaining exponent of `2` is:

```math
c
=
v_2(N!)
-
v_2(i!)
-
v_2((N-i)!)
```

If no factor `2` remains:

```text
c = 0
```

the coefficient is odd.

Otherwise it is even.

---

# 3.6 Detailed Dry Run — n = 5

Bottom:

```text
a0 a1 a2 a3 a4
```

We need Pascal row:

```text
N = n-1 = 4
```

The row is:

```text
1 4 6 4 1
```

We will derive the parity instead of computing the values.

First:

```math
v_2(4!)
=
\left\lfloor\frac42\right\rfloor
+
\left\lfloor\frac44\right\rfloor
=
2+1
=
3
```

---

## i = 0

```math
c
=
v_2(4!)
-
v_2(0!)
-
v_2(4!)
```

```math
c=3-0-3=0
```

Odd.

So:

```text
take a0
```

---

## i = 1

```math
c
=
v_2(4!)
-
v_2(1!)
-
v_2(3!)
```

We have:

```math
v_2(3!)
=
\left\lfloor\frac32\right\rfloor
=
1
```

Therefore:

```math
c=3-0-1=2
```

Even.

Skip `a1`.

---

## i = 2

```math
v_2(2!)=1
```

So:

```math
c=3-1-1=1
```

Even.

Skip `a2`.

---

## i = 3

Symmetric with `i=1`.

Even.

Skip `a3`.

---

## i = 4

Symmetric with `i=0`.

Odd.

Take `a4`.

Therefore:

```math
top=a_0\oplus a_4
```

This matches Pascal parity:

```text
1 4 6 4 1

odd even even even odd
```

---

# 3.7 CSES Sample Intuition — n = 8

Here:

```text
N = n-1 = 7
```

Pascal row `7`:

```text
1 7 21 35 35 21 7 1
```

Every coefficient is odd.

Therefore every bottom value survives once in the final XOR.

For the sample bottom row:

```text
2 10 5 12 9 5 1 5
```

the answer is:

```text
2 ^ 10 ^ 5 ^ 12 ^ 9 ^ 5 ^ 1 ^ 5
```

which gives the pyramid peak.

---

# 3.8 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

long long powerOf2InFactorial(long long n) {
    long long cnt = 0;

    while (n > 0) {
        n /= 2;
        cnt += n;
    }

    return cnt;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);

    for (auto &x : a)
        cin >> x;

    int N = n - 1;

    long long v2N = powerOf2InFactorial(N);

    long long answer = 0;

    for (int i = 0; i < n; ++i) {
        long long twos =
            v2N
            - powerOf2InFactorial(i)
            - powerOf2InFactorial(N - i);

        if (twos == 0)
            answer ^= a[i];
    }

    cout << answer << '\n';
}
```

Complexity:

```text
for each i:
    powerOf2InFactorial → O(log n)

n positions:
    O(n log n)

extra space:
    O(1) besides input
```

This follows the parity-by-power-of-2 approach developed in the lecture.

---

# 3.9 Don't-Memorize Model

Do not memorize:

```text
XOR a[i] when C(n-1,i) is odd
```

Build the observation:

```text
XOR pyramid
     |
     v
How many paths make a[i] reach the top?
     |
     v
Pascal / binomial coefficient
     |
     v
a[i] appears C(n-1,i) times
     |
     v
XOR only cares about parity
     |
     +-------------+
     |             |
   even           odd
     |             |
 cancels        survives
                   |
                   v
          Is C(n-1,i) odd?
                   |
                   v
         count powers of 2
```

---

# 4. Final Recognition Sheet

| Problem signal | Recognition |
|---|---|
| swaps allowed through a GCD condition | identify which values are movable |
| global minimum appears in GCD rule | test divisibility by minimum |
| need sorted final state | compare with sorted copy |
| GCD after excluding each index | prefix GCD + suffix GCD |
| changing one element may change global GCD | inspect GCD of all other elements |
| repeated XOR layers | count contribution of each original value |
| contribution counts form Pascal triangle | binomial coefficients |
| repeated XOR of same value | only odd/even count matters |
| need parity of `nCr` | count power of 2 |
| power of prime in factorial | repeated floor division |

---

# 5. Master Don't-Memorize Model

## Mere Array

```text
What can move?
     |
     v
must interact through gcd = minimum
     |
     v
multiples of minimum are movable
others are frozen
     |
     v
compare with sorted target
```

## Strong Elements

```text
Can a[i] change global GCD?
     |
     v
remove its influence
     |
     v
GCD of all OTHER values
     |
     +----------------+
     |                |
     1               >1
     |                |
 cannot change      can change
     |
prefix/suffix GCD
```

## Xor Pyramid

```text
Repeated XOR
     |
     v
count contribution paths
     |
     v
Pascal coefficient
     |
     v
XOR cares only about parity
     |
     v
odd coefficient?
     |
     v
check power of 2 in nCr
```

> **Final memory anchors**
>
> - **Mere Array:** movable = multiples of the global minimum.
> - **Strong Elements:** remove one index mentally; inspect the GCD of everything else.
> - **Xor Pyramid:** Pascal gives the count, XOR keeps only odd counts.
