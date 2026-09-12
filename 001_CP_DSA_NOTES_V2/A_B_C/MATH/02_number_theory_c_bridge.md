# 10 — Number Theory C Bridge
## Codeforces Div2 C Pattern Recognition Handbook

**Goal:** bridge the gap between basic A/B number theory and the combinations that frequently appear in Div2 C.

This guide assumes you already know:

```text
parity
divisibility
gcd / lcm
basic prime factorization
powers of two
modulo
binary exponentiation
basic counting
```

The new goal is not to learn isolated formulas.

The goal is to recognize:

```text
NUMBER THEORY PROPERTY
        +
ANOTHER TECHNIQUE
        =
DIV2 C SOLUTION
```

Typical combinations:

```text
gcd + differences
prefix sum + modulo
factorization + greedy
divisors + counting
residue classes + frequency
sieve/SPF + queries
modular inverse + combinatorics
Diophantine equations + construction
number theory + binary search
```

---

# Table of Contents

1. [Div2 C Number-Theory Mindset](#1-div2-c-number-theory-mindset)
2. [GCD of Differences](#2-gcd-of-differences)
3. [Normalize by Subtracting a Reference](#3-normalize-by-subtracting-a-reference)
4. [Prefix Sum + Modulo](#4-prefix-sum--modulo)
5. [Same-Remainder / Complement-Remainder Counting](#5-same-remainder--complement-remainder-counting)
6. [Sieve and Smallest Prime Factor](#6-sieve-and-smallest-prime-factor)
7. [Prime-Exponent Representation](#7-prime-exponent-representation)
8. [Divisor Enumeration and Divisor Pairing](#8-divisor-enumeration-and-divisor-pairing)
9. [Count Divisors from Prime Exponents](#9-count-divisors-from-prime-exponents)
10. [LCM / GCD Constraint Modeling](#10-lcm--gcd-constraint-modeling)
11. [Modular Inverse](#11-modular-inverse)
12. [Basic nCr Mod Prime](#12-basic-ncr-mod-prime)
13. [Linear Diophantine Equations](#13-linear-diophantine-equations)
14. [Chinese Remainder Intuition](#14-chinese-remainder-intuition)
15. [Number Theory + Greedy](#15-number-theory--greedy)
16. [Number Theory + Binary Search](#16-number-theory--binary-search)
17. [Number Theory + Constructive](#17-number-theory--constructive)
18. [Inclusion–Exclusion on Prime Factors](#18-inclusionexclusion-on-prime-factors)
19. [Squarefree / Perfect-Square Exponent Parity](#19-squarefree--perfect-square-exponent-parity)
20. [GCD Graph / Connectivity Thinking](#20-gcd-graph--connectivity-thinking)
21. [60-Second Recognition Map](#21-60-second-recognition-map)
22. [Common Wrong Ideas](#22-common-wrong-ideas)
23. [Practice Ladder](#23-practice-ladder)
24. [Contest Notebook Template](#24-contest-notebook-template)

---

# 1. Div2 C Number-Theory Mindset

A-level number theory often asks:

```text
is n divisible by k?
is n prime?
what is gcd(a,b)?
```

B-level often asks:

```text
apply one transformation
then use parity/modulo/gcd
```

C-level often asks:

```text
first compress the data mathematically
then solve a second problem
```

Examples:

```text
array values
   ↓ subtract one reference
differences
   ↓ gcd
one invariant
```

or:

```text
subarray sums
   ↓ prefix sums
equal prefix remainder
   ↓
subarray divisible by k
```

The main C skill is therefore:

> Find the mathematical state that is much smaller than the original state.

---

# 2. GCD of Differences

## Core idea

If all numbers must become congruent modulo some `g`, then their pairwise differences must be divisible by `g`.

For values:

```text
a1, a2, a3, ..., an
```

if:

```text
ai ≡ aj (mod g)
```

then:

```text
ai - aj ≡ 0 (mod g)
```

So `g` must divide every difference.

Therefore:

```text
g = gcd(|a2-a1|, |a3-a1|, ..., |an-a1|)
```

is the largest common modulus preserved by all values.

---

## Real-world mapping

Imagine clocks showing:

```text
14, 20, 32
```

You want a cycle length `g` such that all positions land at the same point on a circular dial.

Subtract the first:

```text
20-14 = 6
32-14 = 18
```

Now ask:

```text
what cycle size divides both 6 and 18?
```

Answer:

```text
gcd(6,18)=6
```

ASCII:

```text
14 -------- 20 ---------------- 32
 |           |                   |
 +----6------+------12-----------+

relative gaps:
6, 18

common step:
gcd(6,18)=6
```

---

## Why subtract one reference?

You do not need all O(n²) pair differences.

Because:

```text
(ai-aj) = (ai-a1) - (aj-a1)
```

So if a number divides every `ai-a1`, it divides every pairwise difference.

---

## C++ template

```cpp
long long g = 0;

for (int i = 1; i < n; ++i) {
    g = std::gcd(g, llabs(a[i] - a[0]));
}
```

---

## Recognition triggers

```text
make all equal modulo something
same remainder
add/subtract same multiples
common step size
differences matter, absolute position does not
```

---

## Div2 C upgrade

Often the first step is:

```text
normalize -> gcd differences
```

and then you must:

```text
count divisors of g
factorize g
check some candidate divisors
construct values using g
```

---

# 3. Normalize by Subtracting a Reference

## Core idea

Absolute values are often noise.

If only relative differences matter, choose one reference and subtract it.

Example:

```text
A = [101, 107, 113, 125]
```

Subtract `101`:

```text
B = [0, 6, 12, 24]
```

Now structure is obvious:

```text
all are multiples of 6
```

Diagram:

```text
BEFORE:
101 107 113 125

subtract 101
      ↓

AFTER:
0   6   12   24
    ^    ^    ^
  clean multiples
```

---

## Recognition sentence

> If shifting every value by the same amount does not change feasibility, normalize around one element.

---

## Common uses

```text
gcd of differences
equalizing arrays
translation-invariant geometry
same-remainder constraints
operation counts depending on gaps
```

---

# 4. Prefix Sum + Modulo

This is one of the most important C-level bridges.

## Core identity

Subarray sum:

```text
sum(l..r) = pref[r] - pref[l-1]
```

We want this divisible by `k`:

```text
pref[r] - pref[l-1] ≡ 0 (mod k)
```

Therefore:

```text
pref[r] ≡ pref[l-1] (mod k)
```

So instead of checking every subarray, count equal prefix remainders.

---

## ASCII derivation

```text
prefix A ----------- prefix B

pref[i]             pref[j]

subarray sum = pref[j] - pref[i]

if:

pref[j] % k == pref[i] % k

then:

(pref[j] - pref[i]) % k == 0
```

---

## Example

```text
A = [3, 1, 2, 7]
k = 3
```

Prefix sums:

```text
pref:
0, 3, 4, 6, 13
```

Remainders mod 3:

```text
0, 0, 1, 0, 1
```

Group equal remainders:

```text
remainder 0 -> 3 occurrences
remainder 1 -> 2 occurrences
```

Each pair of equal prefix remainders defines a divisible subarray.

Count:

```text
C(3,2) + C(2,2)
= 3 + 1
= 4
```

---

## C++ pattern

```cpp
vector<long long> freq(k, 0);

long long pref = 0;
long long ans = 0;

freq[0] = 1;

for (long long x : a) {
    pref = (pref + x) % k;
    if (pref < 0) pref += k;

    ans += freq[pref];
    freq[pref]++;
}
```

---

## Recognition triggers

```text
subarray
sum divisible by k
sum % k = target
many subarrays
prefix property
```

---

## C-level extension

For:

```text
subarray sum ≡ x (mod k)
```

need:

```text
pref[r] - pref[l-1] ≡ x
```

so:

```text
pref[l-1] ≡ pref[r] - x
```

Now you count complement remainders.

---

# 5. Same-Remainder / Complement-Remainder Counting

## Pattern 1 — same remainder

If condition is:

```text
(a-b) divisible by k
```

then:

```text
a % k == b % k
```

So group by remainder.

Example:

```text
k = 5
A = [2,7,12,4,9]
```

Remainders:

```text
2,2,2,4,4
```

Pairs with divisible difference:

```text
C(3,2) + C(2,2)
= 3 + 1
= 4
```

---

## Pattern 2 — complement remainder

If condition is:

```text
(a+b) divisible by k
```

then:

```text
(a%k + b%k) % k = 0
```

Complement:

```text
need = (k-r)%k
```

ASCII:

```text
r       complement
0   <-> 0
1   <-> k-1
2   <-> k-2
...
```

---

## Recognition sentence

> If the condition contains `a±b` divisible by `k`, stop thinking about full values and count remainder classes.

---

# 6. Sieve and Smallest Prime Factor

## Why this becomes a C tool

For one number:

```text
trial division O(sqrt n)
```

may be fine.

For many values up to around `10^6` or `10^7`:

```text
factor each independently
```

can become too slow.

Precompute smallest prime factor:

```text
SPF[x] = smallest prime dividing x
```

Then factorization becomes repeated division.

---

## SPF picture

For:

```text
84
```

SPF chain:

```text
84 --/2--> 42
42 --/2--> 21
21 --/3--> 7
7  --/7--> 1
```

Factorization:

```text
84 = 2² * 3 * 7
```

---

## C++ SPF

```cpp
const int N = 1'000'000;
vector<int> spf(N + 1);

void build_spf() {
    for (int i = 0; i <= N; ++i)
        spf[i] = i;

    for (int i = 2; 1LL * i * i <= N; ++i) {
        if (spf[i] == i) {
            for (long long j = 1LL * i * i; j <= N; j += i) {
                if (spf[j] == j)
                    spf[j] = i;
            }
        }
    }
}
```

Factor:

```cpp
vector<pair<int,int>> factorize(int x) {
    vector<pair<int,int>> f;

    while (x > 1) {
        int p = spf[x];
        int e = 0;

        while (x % p == 0) {
            x /= p;
            ++e;
        }

        f.push_back({p,e});
    }

    return f;
}
```

---

## Recognition triggers

```text
many factorization queries
n values <= 1e6/1e7
prime factors repeatedly needed
count distinct prime factors
build divisor counts
```

---

# 7. Prime-Exponent Representation

Every positive integer:

```text
n = p1^e1 * p2^e2 * ... * pk^ek
```

This representation turns many number-theory questions into operations on exponents.

Example:

```text
360 = 2^3 * 3^2 * 5^1
```

---

## GCD and LCM become exponent min/max

For each prime `p`:

```text
gcd exponent = min(eA, eB)
lcm exponent = max(eA, eB)
```

Example:

```text
A = 72  = 2^3 * 3^2
B = 120 = 2^3 * 3^1 * 5

gcd:
2^min(3,3) * 3^min(2,1)
= 2^3 * 3
= 24

lcm:
2^3 * 3^2 * 5
= 360
```

ASCII:

```text
prime     A exp    B exp
2           3        3
3           2        1
5           0        1

GCD -> column MIN
LCM -> column MAX
```

---

## Why this matters for C

Some problems that look like multiplying huge values are actually:

```text
combine prime exponent vectors
```

---

# 8. Divisor Enumeration and Divisor Pairing

## Core idea

If:

```text
d | n
```

then:

```text
n/d
```

is also a divisor.

So divisors come in pairs around `sqrt(n)`.

Example `n=36`:

```text
1 * 36
2 * 18
3 * 12
4 * 9
6 * 6
```

Only check:

```text
d <= sqrt(n)
```

---

## C++ template

```cpp
vector<long long> divisors;

for (long long d = 1; d * d <= n; ++d) {
    if (n % d == 0) {
        divisors.push_back(d);

        if (d * d != n)
            divisors.push_back(n / d);
    }
}
```

---

## Recognition triggers

```text
choose factor
a*b=n
rectangle dimensions
possible step sizes
candidate gcd values
```

---

## C-level pattern

Often:

```text
derive one number g
then test only divisors of g
```

This changes:

```text
search all X
```

into:

```text
search divisors(X)
```

which is dramatically smaller.

---

# 9. Count Divisors from Prime Exponents

If:

```text
n = p1^e1 * p2^e2 * ... * pk^ek
```

a divisor may choose exponent:

```text
0..ei
```

for each prime.

Therefore:

```text
tau(n) = (e1+1)(e2+1)...(ek+1)
```

---

## Example

```text
72 = 2^3 * 3^2
```

Possible exponent choices:

```text
2 exponent: 0,1,2,3 -> 4 choices
3 exponent: 0,1,2   -> 3 choices
```

Total divisors:

```text
4*3 = 12
```

ASCII grid:

```text
          3^0  3^1  3^2
2^0        •    •    •
2^1        •    •    •
2^2        •    •    •
2^3        •    •    •

4 x 3 = 12
```

---

# 10. LCM / GCD Constraint Modeling

## Typical C transformation

Statement may say:

```text
choose x such that
gcd(x,a)=g
lcm(x,a)=L
```

Use identity:

```text
gcd(a,b) * lcm(a,b) = a*b
```

for positive integers.

So:

```text
g * L = a*x
```

hence:

```text
x = g*L/a
```

Then verify actual gcd/lcm constraints.

---

## Important lesson

An identity may give a candidate, but feasibility still needs checking.

```text
derive candidate
      ↓
verify divisibility
      ↓
verify gcd/lcm
```

---

# 11. Modular Inverse

## Why division is different modulo M

Ordinary:

```text
a / b
```

Modulo:

```text
a * b^{-1} mod M
```

Need modular inverse `b^-1` satisfying:

```text
b * b^-1 ≡ 1 (mod M)
```

---

## Fermat case

If `M` is prime and `b % M != 0`:

```text
b^(M-1) ≡ 1 (mod M)
```

Thus:

```text
b^(M-2) ≡ b^-1 (mod M)
```

So:

```cpp
inv = modpow(b, MOD-2);
```

---

## Example mod 7

Inverse of 3:

```text
3 * 5 = 15 ≡ 1 mod 7
```

Therefore:

```text
3^-1 ≡ 5
```

---

## Recognition triggers

```text
answer modulo prime
division in formula
factorials / combinations
probability modulo prime
```

---

## Warning

Do not blindly use Fermat when modulus is composite.

---

# 12. Basic nCr Mod Prime

For:

```text
nCr = n! / (r!(n-r)!)
```

mod prime:

```text
nCr = fact[n] * invfact[r] * invfact[n-r] mod MOD
```

---

## Precompute

```cpp
fact[0] = 1;
for (int i = 1; i <= N; ++i)
    fact[i] = fact[i-1] * i % MOD;

invfact[N] = modpow(fact[N], MOD-2);

for (int i = N; i >= 1; --i)
    invfact[i-1] = invfact[i] * i % MOD;
```

Query:

```cpp
long long C(int n, int r) {
    if (r < 0 || r > n) return 0;

    return fact[n] * invfact[r] % MOD
         * invfact[n-r] % MOD;
}
```

---

## Recognition triggers

```text
choose r positions
choose k objects
number of subsets of fixed size
ways to select
answer modulo 1e9+7
```

---

# 13. Linear Diophantine Equations

## Core equation

```text
ax + by = c
```

Integer solution exists iff:

```text
gcd(a,b) | c
```

This condition alone solves many feasibility problems.

---

## Why?

All numbers of form:

```text
ax + by
```

are multiples of:

```text
gcd(a,b)
```

Conversely, Bézout gives:

```text
ax0 + by0 = gcd(a,b)
```

Scale by:

```text
c/gcd(a,b)
```

when divisible.

---

## Example

Can:

```text
6x + 9y = 30
```

have integer solution?

```text
gcd(6,9)=3
30 % 3 = 0
```

YES.

Can:

```text
6x + 9y = 31
```

?

```text
31 % 3 != 0
```

NO.

---

## Real-world mapping

You have packages of size 6 and 9.

Can you hit exactly 30 units?

The gcd is the fundamental step size reachable by integer combinations.

---

## Div2 C warning

Problems may require:

```text
x >= 0
y >= 0
```

Existence of integer solutions is not automatically enough.

Non-negativity may need additional reasoning.

---

# 14. Chinese Remainder Intuition

You do not need full advanced CRT for most A/B/C, but understand the shape.

Example:

```text
x ≡ 1 mod 3
x ≡ 2 mod 5
```

List:

```text
mod 3 condition:
1,4,7,10,13,...

mod 5 condition:
2,7,12,17,...

first common:
7
```

Because 3 and 5 are coprime, there is one solution modulo:

```text
3*5 = 15
```

So:

```text
x ≡ 7 mod 15
```

---

## Recognition trigger

```text
multiple simultaneous remainder constraints
periods aligning
two cycles
```

---

# 15. Number Theory + Greedy

This is a common C shape.

## Pattern

```text
first convert each value into a mathematical "need"
then greedily schedule/process needs
```

Example:

```text
need[i] = (k - a[i]%k)%k
```

Now original values no longer matter.

You solve a problem on:

```text
need frequencies
```

ASCII:

```text
raw array
   ↓ mod k
remainders
   ↓ convert
needed increments
   ↓ count/sort
greedy
```

---

## Recognition sentence

> Before greedy, ask whether each element can be reduced to a remainder, deficit, exponent count, divisor class, or gcd relation.

---

# 16. Number Theory + Binary Search

## Pattern

You seek minimum `X`.

For candidate `X`, count something with:

```text
floor(X/a[i])
```

or divisibility counts.

If count is monotonic:

```text
X grows -> count never decreases
```

binary search.

Example machine-like form:

```text
produced(X) = sum floor(X/t[i])
```

---

## Number-theory clue

Floor division converts a huge process into counting.

```text
time simulation
      ↓
floor(X / period)
      ↓
monotonic count
      ↓
binary search
```

---

# 17. Number Theory + Constructive

## Pattern

Need to print values satisfying divisibility/gcd/parity conditions.

Do not search.

Build around the mathematical condition.

Example goal:

```text
gcd(a,b)=g
```

Simple construction:

```text
a = g
b = 2g
```

because:

```text
gcd(g,2g)=g
```

If also need:

```text
a+b = n
```

then construction becomes an equation problem.

---

## Recognition sentence

> For constructive number theory, encode the required gcd/mod/parity directly into the values you print.

---

# 18. Inclusion–Exclusion on Prime Factors

Suppose you want numbers `<= N` coprime to some value with distinct prime factors:

```text
p1,p2,...,pk
```

Count numbers divisible by any prime.

For 2 primes:

```text
|A ∪ B|
= |A| + |B| - |A∩B|
```

For 3:

```text
singles
- pairs
+ triples
```

ASCII:

```text
+ divisible by p1
+ divisible by p2
+ divisible by p3

- divisible by p1*p2
- divisible by p1*p3
- divisible by p2*p3

+ divisible by p1*p2*p3
```

---

## Bitmask implementation idea

For `k` distinct primes:

```text
mask from 1 to (1<<k)-1
```

product selected primes.

If selected count odd:

```text
add N/product
```

else:

```text
subtract N/product
```

---

# 19. Squarefree / Perfect-Square Exponent Parity

## Perfect-square rule

In prime factorization:

```text
n is a perfect square
```

iff every exponent is even.

Example:

```text
144 = 2^4 * 3^2
```

all even -> square.

But:

```text
72 = 2^3 * 3^2
```

one odd exponent -> not square.

---

## Squarefree kernel

Keep primes whose exponent is odd.

Example:

```text
72 = 2^3 * 3^2

odd exponent primes:
2

kernel = 2
```

Multiplying by a square does not change exponent parity.

This lets you group numbers by their "square-equivalent" core.

ASCII:

```text
prime exponent
2 -> 3 -> odd -> keep
3 -> 2 -> even -> remove

72 -> kernel 2
```

---

## Recognition triggers

```text
product should be perfect square
pair numbers so product is square
multiply by square
same square class
```

---

# 20. GCD Graph / Connectivity Thinking

Sometimes elements are connected if:

```text
gcd(a[i], a[j]) > 1
```

Do not compare all pairs.

Prime factors define hidden groups.

Example:

```text
6  = 2*3
10 = 2*5
15 = 3*5
```

Graph:

```text
6 ----10
|      |
|      |
15 ----
```

Connections exist through shared primes.

Alternative view:

```text
prime 2 -> {6,10}
prime 3 -> {6,15}
prime 5 -> {10,15}
```

This suggests:

```text
factorization + DSU
```

for harder C/D problems.

---

# 21. 60-Second Recognition Map

```text
                    NUMBER THEORY SIGNAL
                           |
          +----------------+----------------+
          |                                 |
     pair/relation                       many values
          |                                 |
   difference? sum?                    repeated factoring?
     /         \                           |
   gcd         mod                      sieve/SPF
    |           |
gcd(diff)   residue classes
               |
         prefix/subarray?
               |
        prefix modulo
```

Second-stage questions:

```text
Need count?
    -> frequency / combinations

Need smallest X?
    -> monotonic + binary search

Need construct?
    -> encode gcd/mod/parity directly

Need many candidate divisors?
    -> factorize / enumerate divisors

Need product square?
    -> exponent parity

Need choose under two conditions?
    -> transform to remainder/exponent then greedy
```

---

# 22. Common Wrong Ideas

## Wrong 1 — check only small primes

Example:

```text
22 = 2 * 11
```

Checking only 3,5,7 misses prime 11.

Use factor structure, not a hardcoded prime list.

---

## Wrong 2 — compare all pairs for gcd differences

Do:

```text
gcd(|a[i]-a[0]|)
```

not O(n²) pair checks.

---

## Wrong 3 — enumerate all subarrays for divisibility

Use:

```text
equal prefix remainders
```

---

## Wrong 4 — factor every number with O(sqrt n) when there are many queries

Use sieve/SPF if constraints allow.

---

## Wrong 5 — divide directly under modulo

Use modular inverse when valid.

---

## Wrong 6 — assume gcd condition gives non-negative Diophantine solution

It guarantees integer solution, not automatically:

```text
x,y >= 0
```

---

## Wrong 7 — forget negative modulo normalization

Prefer:

```cpp
r = ((x % k) + k) % k;
```

when negatives are possible.

---

# 23. Practice Ladder

Use these as topic anchors. Solve without opening tags.

## Level 1 — bridge from A/B

1. CF 1475A — Odd Divisor  
   https://codeforces.com/problemset/problem/1475/A

2. CF 1325A — EhAb AnD gCd  
   https://codeforces.com/problemset/problem/1325/A

3. CF 1370A — Maximum GCD  
   https://codeforces.com/problemset/problem/1370/A

4. CF 230B — T-primes  
   https://codeforces.com/problemset/problem/230/B

5. CF 1294C — Product of Three Numbers  
   https://codeforces.com/problemset/problem/1294/C

6. CSES — Counting Divisors  
   https://cses.fi/problemset/task/1713

---

## Level 2 — gcd / differences / residues

1. CF 1593D1 — All are Same  
   https://codeforces.com/problemset/problem/1593/D1

2. CF 1618C — Paint the Array  
   https://codeforces.com/problemset/problem/1618/C

3. CSES — Subarray Divisibility  
   https://cses.fi/problemset/task/1662

4. CSES — Common Divisors  
   https://cses.fi/problemset/task/1081

5. CF 1374D — Zero Remainder Array  
   https://codeforces.com/problemset/problem/1374/D

6. CF 1520D — Same Differences  
   https://codeforces.com/problemset/problem/1520/D

---

## Level 3 — factorization / divisor structure

1. CF 26A — Almost Prime  
   https://codeforces.com/problemset/problem/26/A

2. CF 1609A — Divide and Multiply  
   https://codeforces.com/problemset/problem/1609/A

3. CSES — Divisor Analysis  
   https://cses.fi/problemset/task/2182

4. CSES — Prime Multiples  
   https://cses.fi/problemset/task/2185

5. CSES — Counting Coprime Pairs  
   https://cses.fi/problemset/task/2417

6. CF 1499D — The Number of Pairs  
   https://codeforces.com/problemset/problem/1499/D

---

## Level 4 — modular arithmetic / combinatorics

1. CSES — Exponentiation  
   https://cses.fi/problemset/task/1095

2. CSES — Exponentiation II  
   https://cses.fi/problemset/task/1712

3. CSES — Creating Strings II  
   https://cses.fi/problemset/task/1715

4. CSES — Distributing Apples  
   https://cses.fi/problemset/task/1716

5. CF 1097B — Petr and a Combination Lock  
   https://codeforces.com/problemset/problem/1097/B

---

## Level 5 — mixed C-style combinations

1. CF 1624C — Division by Two and Permutation  
   https://codeforces.com/problemset/problem/1624/C

2. CF 1366D — Two Divisors  
   https://codeforces.com/problemset/problem/1366/D

3. CF 1499D — The Number of Pairs  
   https://codeforces.com/problemset/problem/1499/D

4. CSES — Subarray Divisibility  
   https://cses.fi/problemset/task/1662

5. CSES — Prime Multiples  
   https://cses.fi/problemset/task/2185

6. CSES — Counting Coprime Pairs  
   https://cses.fi/problemset/task/2417

---

# 24. Contest Notebook Template

For every number-theory C problem:

```text
Problem:
Rating:

RAW CONDITION:
________________________________

CAN I REDUCE VALUES TO:
[ ] parity
[ ] remainder
[ ] gcd
[ ] difference
[ ] prime exponents
[ ] divisor set
[ ] squarefree kernel

REFERENCE NORMALIZATION:
________________________________

NUMBER-THEORY STATE:
________________________________

SECOND TECHNIQUE:
[ ] frequency
[ ] prefix sum
[ ] greedy
[ ] binary search
[ ] constructive
[ ] DSU
[ ] combinatorics

KEY EQUATION:
________________________________

WHY NECESSARY?
________________________________

WHY SUFFICIENT?
________________________________

SMALLEST COUNTEREXAMPLE TO WRONG IDEA:
________________________________

RECOGNITION SENTENCE:
"When I see __________________,
I will test __________________."
```

---

# Final Recognition Summary

The Div2 C number-theory jump is usually not:

```text
learn much harder formulas
```

It is:

```text
basic number theory
        +
correct transformation
        +
one standard technique
```

The highest-value combinations to make automatic are:

```text
1. gcd of differences
2. prefix modulo
3. residue frequency
4. SPF / factorization
5. divisor enumeration
6. prime-exponent modeling
7. modular inverse / nCr
8. Diophantine gcd condition
9. inclusion-exclusion over prime factors
10. number theory + greedy / binary search / constructive
```

Your contest mental pipeline should become:

```text
LONG STATEMENT
      ↓
WHAT RELATION IS NUMBER-THEORETIC?
      ↓
MOD / GCD / FACTORS / DIVISORS / EXPONENTS
      ↓
COMPRESS THE STATE
      ↓
WHAT SECOND TECHNIQUE FINISHES IT?
      ↓
COUNT / GREEDY / PREFIX / BS / CONSTRUCT
      ↓
PROVE
      ↓
CODE
```
