# Combinatorics & Probability — Level 3
## Combinatorics 1 — Binomial Coefficients, Fast nCr, Identities & Arrangements

> **Goal:** understand what each formula counts before using it.
>
> **Study flow:** prerequisites → mathematical notation → derivation → example → C++ → recognition.
>
> **Math rendering:** display equations use fenced `math` blocks only to avoid Markdown/LaTeX rendering errors.
>
> **Lecture scope:** binomial coefficients, fast `C(n,r)` modulo a prime, factorial/inverse-factorial precomputation, important binomial identities, arrangements of distinct/similar elements, and the lecture-listed applications: Creating Strings 2, Kth String in Dictionary, Unique Paths, and grouping items together.

---

# Clickable Table of Contents

- [0. Mathematical Notation & Prerequisites](#0-mathematical-notation--prerequisites)
  - [0.1 Factorial](#01-factorial)
  - [0.2 Permutation vs Combination](#02-permutation-vs-combination)
  - [0.3 Binomial-Coefficient Notation](#03-binomial-coefficient-notation)
  - [0.4 Sigma Notation](#04-sigma-notation)
  - [0.5 Modular Congruence](#05-modular-congruence)
  - [0.6 Modular Inverse](#06-modular-inverse)
  - [0.7 Binary Exponentiation](#07-binary-exponentiation)
- [1. Binomial Coefficients — Deriving nCr](#1-binomial-coefficients--deriving-ncr)
- [2. Computing nCr Modulo a Prime — Many Queries](#2-computing-ncr-modulo-a-prime--many-queries)
- [3. O(N) Factorial & Inverse-Factorial Precomputation](#3-on-factorial--inverse-factorial-precomputation)
- [4. Computing nCr Once — O(r) Method](#4-computing-ncr-once--or-method)
- [5. Important Binomial Identities](#5-important-binomial-identities)
  - [5.1 Symmetry](#51-symmetry)
  - [5.2 Pascal Identity](#52-pascal-identity)
  - [5.3 Sum of a Binomial Row](#53-sum-of-a-binomial-row)
  - [5.4 K Specific Items Must Be Included](#54-k-specific-items-must-be-included)
  - [5.5 K Specific Items Must Be Excluded](#55-k-specific-items-must-be-excluded)
  - [5.6 Where nCr Is Largest](#56-where-ncr-is-largest)
- [6. Arrangements — Distinct Elements](#6-arrangements--distinct-elements)
- [7. Arrangements — Repeated / Similar Elements](#7-arrangements--repeated--similar-elements)
- [8. Application — Creating Strings 2](#8-application--creating-strings-2)
- [9. Application — Kth String in Dictionary](#9-application--kth-string-in-dictionary)
- [10. Application — Unique Paths](#10-application--unique-paths)
- [11. Application — K Specific Items Always Together](#11-application--k-specific-items-always-together)
- [12. Technique Selection](#12-technique-selection)
- [13. Final Don't-Memorize Model](#13-final-dont-memorize-model)

---

# 0. Mathematical Notation & Prerequisites

Before `nCr`, make the notation automatic.

---

## 0.1 Factorial

Notation:

```math
n!
```

Read as:

```text
"n factorial"
```

Meaning:

```math
n!=n(n-1)(n-2)\cdots2\cdot1
```

Special value:

```math
0!=1
```

### Example

```math
5!=5\cdot4\cdot3\cdot2\cdot1=120
```

### Why factorial appears in counting

If `n` distinct objects are placed in `n` ordered positions:

```text
position 1 → n choices
position 2 → n-1 choices
position 3 → n-2 choices
...
```

So total:

```math
n(n-1)\cdots1=n!
```

---

## 0.2 Permutation vs Combination

### Permutation

Order matters.

Example:

Choose `2` from:

```text
A, B, C
```

Ordered outcomes:

```text
AB, AC, BA, BC, CA, CB
```

Count:

```text
6
```

Notation:

```math
P(n,r)
```

Formula:

```math
P(n,r)
=
n(n-1)\cdots(n-r+1)
=
\frac{n!}{(n-r)!}
```

---

### Combination

Order does **not** matter.

For the same example:

```text
AB = BA
AC = CA
BC = CB
```

Unique choices:

```text
AB, AC, BC
```

Count:

```text
3
```

Notation:

```math
C(n,r)
```

or:

```math
\binom{n}{r}
```

---

## 0.3 Binomial-Coefficient Notation

These mean the same thing:

```text
nCr
C(n,r)
```

and:

```math
\binom{n}{r}
```

Read:

```text
"n choose r"
```

Meaning:

```text
number of ways to choose r items
from n distinct items
when order does not matter
```

Example:

```math
\binom{5}{2}=10
```

Meaning:

```text
10 ways to choose 2 objects from 5.
```

---

## 0.4 Sigma Notation

Notation:

```math
\sum_{r=0}^{n} f(r)
```

Read:

```text
"sum f(r) for r from 0 to n"
```

Example:

```math
\sum_{r=0}^{3}\binom{3}{r}
```

means:

```math
\binom30+\binom31+\binom32+\binom33
```

Numerically:

```text
1 + 3 + 3 + 1 = 8
```

---

## 0.5 Modular Congruence

Notation:

```math
a\equiv b\pmod M
```

Read:

```text
"a is congruent to b modulo M"
```

Meaning:

```text
a and b leave the same remainder when divided by M
```

Example:

```math
17\equiv2\pmod5
```

because:

```text
17 % 5 = 2
```

---

## 0.6 Modular Inverse

Notation:

```math
a^{-1}\pmod M
```

This does **not** mean normal decimal reciprocal.

It means a number `x` such that:

```math
a\cdot x\equiv1\pmod M
```

Example:

```text
inverse of 3 modulo 7 = 5
```

because:

```text
3 × 5 = 15
15 % 7 = 1
```

For prime modulus `M`, when `a` is not divisible by `M`:

```math
a^{-1}\equiv a^{M-2}\pmod M
```

This is Fermat's Little Theorem applied to modular division.

---

## 0.7 Binary Exponentiation

We frequently need:

```text
a^b mod M
```

with a huge exponent.

Binary exponentiation repeatedly halves `b`.

Complexity:

```text
O(log b)
```

C++:

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

This is used to calculate modular inverses.

---

# 1. Binomial Coefficients — Deriving nCr

## 1.1 What Does It Ask?

We have:

```text
n distinct items
```

and want to choose:

```text
r items
```

where order does not matter.

Count:

```math
\binom{n}{r}
```

---

## 1.2 Observation

First temporarily count **ordered** selections.

For the first selected item:

```text
n choices
```

Second:

```text
n-1 choices
```

Continue for `r` positions:

```math
n(n-1)(n-2)\cdots(n-r+1)
```

This is:

```math
\frac{n!}{(n-r)!}
```

But this overcounts each unordered group.

---

## 1.3 Why Divide by r!

Suppose the selected set is:

```text
{1,2,3}
```

Ordered versions include:

```text
123
132
213
231
312
321
```

There are:

```math
3!=6
```

orders of the exact same chosen set.

So divide ordered count by:

```math
r!
```

Therefore:

```math
\binom{n}{r}
=
\frac{n!}{r!(n-r)!}
```

---

## 1.4 Step-by-Step Example — Choose 3 From 5

Ordered picks:

```text
5 × 4 × 3
= 60
```

Each chosen group of `3` is counted:

```text
3! = 6
```

times.

Therefore:

```math
\binom53
=
\frac{60}{6}
=
10
```

Using factorial formula:

```math
\binom53
=
\frac{5!}{3!2!}
=
\frac{120}{6\cdot2}
=
10
```

---

## 1.5 C++

For very small values only:

```cpp
long long combinationSmall(int n, int r) {
    if (r < 0 || r > n)
        return 0;

    long long numerator = 1;
    long long denominator = 1;

    for (int i = 0; i < r; ++i)
        numerator *= (n - i);

    for (int i = 1; i <= r; ++i)
        denominator *= i;

    return numerator / denominator;
}
```

This is not suitable when factorial/product values overflow.

---

## 1.6 Recognition Model

```text
Choose r from n
       |
       v
Does order matter?
       |
      NO
       |
       v
count ordered selections
       |
       v
divide by r! duplicate orders
       |
       v
n! / (r!(n-r)!)
```

**Memory anchor:** combination = ordered selection / internal arrangements.

---

# 2. Computing nCr Modulo a Prime — Many Queries

Lecture setting:

```text
n,r up to around 1e6
many queries
answer modulo a prime such as 1e9+7
```

---

## 2.1 What It Asks

For many queries:

```text
(n,r)
```

compute:

```math
\binom nr\bmod M
```

Direct factorial values are enormous.

We must perform the calculation under modulo.

---

## 2.2 Observation

Formula:

```math
\binom nr
=
\frac{n!}{r!(n-r)!}
```

Under modulo, we cannot directly write:

```text
fact[n] / fact[r]
```

Division becomes multiplication by modular inverse.

So:

```math
\binom nr
\equiv
n!
\cdot(r!)^{-1}
\cdot((n-r)!)^{-1}
\pmod M
```

---

## 2.3 What We Precompute

Store:

```text
fact[i]    = i! mod M
invFact[i] = inverse(i!) mod M
```

Then each query is:

```math
\binom nr
\equiv
fact[n]\cdot invFact[r]\cdot invFact[n-r]
\pmod M
```

---

## 2.4 Example — C(5,2) mod 1e9+7

```text
fact[5] = 120
fact[2] = 2
fact[3] = 6
```

Normal answer:

```text
120 / (2×6)
= 10
```

Under modulo:

```text
120
× inverse(2)
× inverse(6)
mod M
```

gives the same integer result:

```text
10
```

---

## 2.5 C++ Query Formula

```cpp
long long nCr(
    int n,
    int r,
    const vector<long long>& fact,
    const vector<long long>& invFact,
    long long MOD
) {
    if (r < 0 || r > n)
        return 0;

    return (__int128)fact[n]
         * invFact[r] % MOD
         * invFact[n - r] % MOD;
}
```

After precomputation:

```text
each query = O(1)
```

---

## 2.6 Recognition Model

```text
many nCr queries
       |
       v
factorial formula repeats
       |
       v
precompute factorials
and inverse factorials
       |
       v
each nCr = 3 table lookups/multiplies
```

---

# 3. O(N) Factorial & Inverse-Factorial Precomputation

The lecture first shows that calculating every inverse factorial independently would cost roughly:

```text
O(N log M)
```

because each modular inverse uses binary exponentiation.

Then it gives the O(N) trick.

---

## 3.1 Factorials

Base:

```math
0!=1
```

Recurrence:

```math
i!=i(i-1)!
```

So:

```cpp
fact[0] = 1;

for (int i = 1; i <= N; ++i)
    fact[i] = fact[i - 1] * i % MOD;
```

Time:

```text
O(N)
```

---

## 3.2 Inverse Factorial Trick

We know:

```math
i!=(i+1)!/(i+1)
```

Invert both sides:

```math
(i!)^{-1}
=
((i+1)!)^{-1}(i+1)
```

Therefore:

```math
invFact[i]
=
invFact[i+1]\cdot(i+1)
\pmod M
```

So only calculate the largest inverse factorial once:

```text
invFact[N]
=
inverse(fact[N])
```

using Fermat:

```math
invFact[N]
=
fact[N]^{M-2}\bmod M
```

Then fill downward in O(N).

---

## 3.3 Small Example — N = 5

Factorials:

```text
fact[0] = 1
fact[1] = 1
fact[2] = 2
fact[3] = 6
fact[4] = 24
fact[5] = 120
```

Compute only:

```text
invFact[5] = inverse(120)
```

Then:

```text
invFact[4] = invFact[5] × 5
invFact[3] = invFact[4] × 4
invFact[2] = invFact[3] × 3
...
```

all modulo `M`.

---

## 3.4 C++

```cpp
struct Combinatorics {
    long long MOD;
    vector<long long> fact, invFact;

    Combinatorics(int n, long long mod)
        : MOD(mod), fact(n + 1), invFact(n + 1) {

        fact[0] = 1;

        for (int i = 1; i <= n; ++i)
            fact[i] = (__int128)fact[i - 1] * i % MOD;

        invFact[n] = binpow(fact[n], MOD - 2, MOD);

        for (int i = n - 1; i >= 0; --i)
            invFact[i] =
                (__int128)invFact[i + 1] * (i + 1) % MOD;
    }

    long long nCr(int n, int r) const {
        if (r < 0 || r > n)
            return 0;

        return (__int128)fact[n]
             * invFact[r] % MOD
             * invFact[n - r] % MOD;
    }
};
```

Precomputation:

```text
O(N + log MOD)
```

Each query:

```text
O(1)
```

---

## 3.5 Recognition Model

```text
Need every inverse factorial
        |
        v
don't exponentiate N times
        |
        v
compute invFact[N] once
        |
        v
walk backward:
invFact[i] = invFact[i+1](i+1)
```

**Memory anchor:** one inverse at the top, then propagate downward.

---

# 4. Computing nCr Once — O(r) Method

The lecture distinguishes two use cases:

```text
many queries
→ preprocess

one query
→ O(r) multiplicative formula
```

---

## 4.1 What It Asks

Suppose:

```text
n can be very large
r is small
```

Example lecture shape:

```text
n up to 1e9
r <= 20
```

Precomputing factorials up to `n` is impossible.

---

## 4.2 Observation

Use:

```math
\binom nr
=
\frac{n(n-1)\cdots(n-r+1)}
     {1\cdot2\cdots r}
```

Only `r` numerator terms are needed.

Also use symmetry:

```math
\binom nr=\binom n{n-r}
```

so first:

```text
r = min(r, n-r)
```

---

## 4.3 Example — C(10,3)

Numerator:

```text
10 × 9 × 8
= 720
```

Denominator:

```text
1 × 2 × 3
= 6
```

So:

```text
720 / 6 = 120
```

Thus:

```math
\binom{10}{3}=120
```

---

## 4.4 C++ Under Prime Modulus

```cpp
long long nCrSmallR(
    long long n,
    long long r,
    long long MOD
) {
    if (r < 0 || r > n)
        return 0;

    r = min(r, n - r);

    long long numerator = 1;
    long long denominator = 1;

    for (long long i = 0; i < r; ++i) {
        numerator =
            (__int128)numerator * ((n - i) % MOD) % MOD;

        denominator =
            (__int128)denominator * (i + 1) % MOD;
    }

    long long invDen =
        binpow(denominator, MOD - 2, MOD);

    return (__int128)numerator * invDen % MOD;
}
```

Time:

```text
O(r + log MOD)
```

No O(n) precomputation.

---

## 4.5 Recognition Model

```text
Need only one nCr
n huge, r small
       |
       v
don't build factorial table
       |
       v
multiply only r numerator terms
       |
       v
divide by r! using modular inverse
```

---

# 5. Important Binomial Identities

---

## 5.1 Symmetry

Formula:

```math
\binom nr=\binom n{n-r}
```

### Meaning

Choosing `r` items to keep is equivalent to choosing:

```text
n-r items to discard
```

### Example

Choose `2` from `5`:

```math
\binom52=10
```

Equivalent:

```text
discard 3 from 5
```

so:

```math
\binom53=10
```

---

## 5.2 Pascal Identity

Formula:

```math
\binom nr
=
\binom{n-1}{r-1}
+
\binom{n-1}{r}
```

### Meaning

Pick one special item.

Every size-`r` selection belongs to exactly one case:

```text
Case 1: special item IS selected
Case 2: special item is NOT selected
```

If selected:

```text
need r-1 more from n-1
```

Count:

```math
\binom{n-1}{r-1}
```

If not selected:

```text
need all r from n-1
```

Count:

```math
\binom{n-1}{r}
```

Add them.

---

### Example — C(5,2)

Choose whether person `A` is included.

Included:

```text
choose 1 of remaining 4
→ C(4,1)=4
```

Excluded:

```text
choose 2 of remaining 4
→ C(4,2)=6
```

Total:

```text
4 + 6 = 10
```

So:

```math
\binom52
=
\binom41+\binom42
=
10
```

---

## 5.3 Sum of a Binomial Row

Formula:

```math
\sum_{r=0}^{n}\binom nr=2^n
```

### Meaning

Left side counts subsets grouped by their size:

```text
size 0 → C(n,0)
size 1 → C(n,1)
...
size n → C(n,n)
```

Right side counts subsets another way.

Each of `n` items has two independent choices:

```text
pick
don't pick
```

Therefore:

```text
2 × 2 × ... × 2
= 2^n
```

---

### Example — n = 3

By size:

```text
C(3,0) = 1
C(3,1) = 3
C(3,2) = 3
C(3,3) = 1
```

Total:

```text
1+3+3+1 = 8
```

And:

```text
2³ = 8
```

---

## 5.4 K Specific Items Must Be Included

Suppose:

```text
n total items
choose r
k specific items are mandatory
```

The `k` required items are already chosen.

Still need:

```text
r-k
```

items from the remaining:

```text
n-k
```

Therefore:

```math
\binom{n-k}{r-k}
```

---

### Example

```text
n = 7
r = 4
k = 2 mandatory items
```

Already chosen:

```text
2
```

Need:

```text
2 more
```

from:

```text
5 remaining
```

Answer:

```math
\binom52=10
```

---

## 5.5 K Specific Items Must Be Excluded

Suppose `k` specific items can never be selected.

Available items:

```text
n-k
```

Still choose:

```text
r
```

Therefore:

```math
\binom{n-k}{r}
```

---

### Example

```text
n = 7
r = 3
k = 2 forbidden items
```

Available:

```text
5
```

Choose:

```text
3
```

Answer:

```math
\binom53=10
```

---

## 5.6 Where nCr Is Largest

For fixed `n`, binomial coefficients rise toward the center and then fall symmetrically.

The maximum occurs around:

```text
r = n/2
```

More precisely:

```text
r = floor(n/2)
```

and for odd `n`, the two central values are equal.

### Example — n = 6

Row:

```text
1, 6, 15, 20, 15, 6, 1
```

Maximum:

```text
20
```

at:

```text
r = 3 = n/2
```

This matters when estimating the largest possible `nCr`.

---

# 6. Arrangements — Distinct Elements

## 6.1 What It Asks

How many ways can `n` distinct objects be arranged?

---

## 6.2 Observation

First position:

```text
n choices
```

Second:

```text
n-1
```

Continue:

```text
n × (n-1) × ... × 1
```

Therefore:

```math
n!
```

---

## 6.3 Example — 3 Distinct Objects

Objects:

```text
A, B, C
```

Arrangements:

```text
ABC
ACB
BAC
BCA
CAB
CBA
```

Count:

```text
6 = 3!
```

---

## 6.4 C++

```cpp
long long permutationsDistinct(int n) {
    long long ans = 1;

    for (int i = 2; i <= n; ++i)
        ans *= i;

    return ans;
}
```

For large values under modulo, use precomputed factorials.

---

# 7. Arrangements — Repeated / Similar Elements

## 7.1 What It Asks

What if some objects are identical?

Example:

```text
A, A, B
```

If we pretend the two `A`s are distinct:

```text
A1, A2, B
```

we would get:

```text
3! = 6
```

But swapping:

```text
A1 and A2
```

does not create a new visible arrangement.

Each visible arrangement is overcounted by:

```text
2!
```

So:

```text
3! / 2!
= 3
```

Actual strings:

```text
AAB
ABA
BAA
```

---

## 7.2 General Formula

Suppose total length is:

```math
N=a_1+a_2+\cdots+a_k
```

where identical type `i` occurs:

```text
a_i times
```

Then:

```math
\text{arrangements}
=
\frac{N!}{a_1!a_2!\cdots a_k!}
```

---

## 7.3 Example — AABBC

Counts:

```text
A → 2
B → 2
C → 1
```

Total:

```text
5
```

Answer:

```math
\frac{5!}{2!2!1!}
=
\frac{120}{4}
=
30
```

---

## 7.4 Recognition Model

```text
arrange N positions
       |
       v
pretend everything distinct
→ N!
       |
       v
identical copies create duplicate permutations
       |
       v
divide by each count factorial
```

---

# 8. Application — Creating Strings 2

The lecture lists **Creating Strings 2** as an application of repeated-element arrangements.

---

## 8.1 What It Asks

Given a string, count distinct permutations.

Example from the lecture:

```text
aabac
```

Character counts:

```text
a → 3
b → 1
c → 1
```

Total length:

```text
5
```

---

## 8.2 Observation

If all five positions were distinct:

```text
5!
```

But the three `a`s are indistinguishable.

Their internal:

```text
3!
```

permutations do not create new strings.

Therefore:

```math
\frac{5!}{3!}
```

---

## 8.3 Dry Run

```text
5! = 120
3! = 6
```

Therefore:

```text
120 / 6 = 20
```

Answer:

```text
20 distinct strings
```

The lecture illustrates exactly this `5!/3! = 20` example.

---

## 8.4 C++ Under Prime Modulus

```cpp
long long countDistinctPermutations(
    const string& s,
    const Combinatorics& comb
) {
    vector<int> freq(26, 0);

    for (char c : s)
        ++freq[c - 'a'];

    long long ans = comb.fact[s.size()];

    for (int f : freq) {
        ans =
            (__int128)ans * comb.invFact[f] % comb.MOD;
    }

    return ans;
}
```

Complexity after factorial precomputation:

```text
O(n + alphabet_size)
```

---

# 9. Application — Kth String in Dictionary

The lecture models fixed-length lowercase strings as a **base-26 number system**.

---

## 9.1 What It Asks

For strings of fixed length `n` over:

```text
a ... z
```

ordered lexicographically, find the `k`-th string.

For length `3`, the order starts:

```text
1  aaa
2  aab
3  aac
...
26 aaz
27 aba
28 abb
...
```

---

## 9.2 Observation

There are:

```text
26 choices per position
```

So fixed-length strings behave like digits in base `26`.

Map:

```text
0 → a
1 → b
2 → c
...
25 → z
```

Because `k` is 1-indexed, convert:

```text
k-1
```

to base `26`.

Pad to exactly `n` digits.

---

## 9.3 Dry Run — n = 3, k = 28

Convert:

```text
k-1 = 27
```

Base `26`:

```text
27 = 1×26 + 1
```

Digits for length `3`:

```text
0,1,1
```

Map:

```text
0 → a
1 → b
1 → b
```

Answer:

```text
abb
```

This matches the lecture's ordering around positions 27 and 28.

---

## 9.4 C++

```cpp
string kthStringBase26(int n, long long k) {
    --k;  // convert to zero-based index

    string s(n, 'a');

    for (int i = n - 1; i >= 0; --i) {
        int digit = k % 26;
        s[i] = char('a' + digit);
        k /= 26;
    }

    return s;
}
```

This direct integer version is valid when the index range fits the chosen integer type.

---

## 9.5 Recognition Model

```text
fixed-length strings
each position has 26 choices
       |
       v
base-26 representation
       |
       v
k is 1-indexed
→ convert k-1
       |
       v
digits 0..25
→ letters a..z
```

---

# 10. Application — Unique Paths

The lecture compares grid DP with a direct combinatorics formula.

---

## 10.1 What It Asks

Move from the top-left to bottom-right of an:

```text
n × m
```

grid using only:

```text
Right
Down
```

How many paths?

---

## 10.2 Observation

Every valid path must use exactly:

```text
m-1 Right moves
n-1 Down moves
```

Total moves:

```math
(n-1)+(m-1)=n+m-2
```

So a path is just an arrangement of:

```text
R and D
```

with fixed counts.

---

## 10.3 Derivation

Choose which positions contain the `Down` moves:

```math
\binom{n+m-2}{n-1}
```

Equivalent, choose `Right` positions:

```math
\binom{n+m-2}{m-1}
```

These are equal by symmetry.

---

## 10.4 Dry Run — 3 × 4 Grid

Need:

```text
2 Down
3 Right
```

Total:

```text
5 moves
```

Choose locations of the `2` Down moves:

```math
\binom52=10
```

So:

```text
10 paths
```

---

## 10.5 C++

With precomputed combinations:

```cpp
long long uniquePaths(
    int n,
    int m,
    const Combinatorics& comb
) {
    return comb.nCr(n + m - 2, n - 1);
}
```

Complexity:

```text
O(1) per query
```

after factorial precomputation.

Compare:

```text
DP → O(nm)
Combinatorics → O(1) query
```

---

## 10.6 Recognition Model

```text
path consists of fixed counts
of only two move types
       |
       v
total move positions
       |
       v
choose positions of one move type
       |
       v
nCr
```

---

# 11. Application — K Specific Items Always Together

> **Lecture exercise expansion:** the lecture lists this problem title. The derivation below uses the standard interpretation: `n` distinct items are arranged, and a particular set of `k` distinct items must stay consecutive.

---

## 11.1 What It Asks

Arrange:

```text
n distinct items
```

such that:

```text
k specified items
```

always appear together.

---

## 11.2 Observation — Compress the Group

Treat the `k` items as one super-item.

Originally:

```text
n items
```

Compressing `k` items into one removes:

```text
k-1 objects
```

So number of objects to arrange becomes:

```text
n-k+1
```

Their outer arrangements:

```math
(n-k+1)!
```

But the `k` items can also be internally arranged:

```math
k!
```

Therefore:

```math
\text{answer}
=
(n-k+1)!\,k!
```

---

## 11.3 Dry Run — n = 5, k = 3

Suppose:

```text
A,B,C
```

must stay together, with other items:

```text
D,E
```

Treat:

```text
[ABC], D, E
```

as three objects.

Outer arrangements:

```text
3! = 6
```

Inside the block:

```text
ABC can be arranged in 3! = 6 ways
```

Total:

```text
6 × 6 = 36
```

Formula:

```math
(5-3+1)!\cdot3!
=
3!\cdot3!
=
36
```

---

## 11.4 C++

```cpp
long long groupedArrangements(
    int n,
    int k,
    const vector<long long>& fact,
    long long MOD
) {
    return (__int128)fact[n - k + 1]
         * fact[k] % MOD;
}
```

---

## 11.5 Recognition Model

```text
specific items must stay consecutive
        |
        v
compress them into one block
        |
        v
arrange outer objects
        |
        v
multiply by internal arrangements
```

**Memory anchor:** together → compress into one object.

---

# 12. Technique Selection

| Problem signal | Think |
|---|---|
| choose `r` from `n`, order irrelevant | `C(n,r)` |
| order matters | permutation |
| many `nCr` queries, bounded `n` | factorial + inverse factorial |
| one `nCr`, huge `n`, small `r` | multiplicative O(r) |
| choose vs discard | `C(n,r)=C(n,n-r)` |
| split by whether special item chosen | Pascal identity |
| count all subsets by size | sum of `nCr` = `2^n` |
| `k` mandatory items | `C(n-k,r-k)` |
| `k` forbidden items | `C(n-k,r)` |
| arrange distinct objects | `n!` |
| repeated identical objects | divide by multiplicity factorials |
| fixed counts of R/D moves | binomial coefficient |
| lexicographic fixed-length alphabet strings | base representation |
| specific items must stay together | compress into one block |

---

# 13. Final Don't-Memorize Model

```text
COMBINATION
-----------
choose r from n
order doesn't matter

ordered choices / r!
→ n! / (r!(n-r)!)


MANY nCr QUERIES
----------------
fact[]
invFact[]

one inverse at N
then build invFact downward

each query O(1)


ONE nCr QUERY
-------------
n huge
r small

multiply r numerator terms
divide by r!


SYMMETRY
--------
choose r
=
discard n-r


PASCAL
------
special item:
pick it
or
don't pick it


ALL SUBSETS
-----------
each item:
pick / not pick

2 choices per item
→ 2^n


MANDATORY ITEMS
---------------
k already chosen
→ choose r-k from n-k


FORBIDDEN ITEMS
---------------
remove k from available pool
→ choose r from n-k


DISTINCT ARRANGEMENTS
---------------------
n!


REPEATED ARRANGEMENTS
---------------------
total!
/
product of frequency factorials


UNIQUE PATHS
------------
fixed number of R and D moves
→ choose positions
→ nCr


ITEMS TOGETHER
--------------
compress group
→ arrange outer objects
× arrange inside group
```

> **Final memory anchor:**  
> **Combinatorics is usually about identifying what is being chosen or arranged, whether order matters, and which choices are already forced.**
