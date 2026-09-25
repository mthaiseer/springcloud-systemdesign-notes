# Form 3 — Product Constraint

> **Goal:** Learn to recognize, derive, visualize, and apply **Product
> Constraints** in competitive programming: exact products, bounded
> products, divisibility, factor decomposition, and prime-exponent
> invariants.

------------------------------------------------------------------------

## 1. What is a Product Constraint?

``` text
x * y = P
x * y <= P
x * y >= P
K | (x * y)
x1 * x2 * ... * xn = P
```

Typical Codeforces wording:

``` text
"product is P"
"multiply to P"
"split N into factors"
"product is divisible by K"
"find a multiple"
"make the product a perfect square"
```

Core mental model:

``` text
PRODUCT
   ↓
FACTORS
```

For an exact integer product:

``` text
x * y = P
    ↓ divide by x
y = P / x
```

but integer `y` requires:

``` text
x != 0
P % x == 0
```

------------------------------------------------------------------------

## 2. Simple Example

``` text
x * y = 24
x = 6
```

Substitute:

``` text
6*y = 24
```

Divide by `6`:

``` text
y = 24/6
y = 4
```

Therefore:

``` text
KNOWN PRODUCT / KNOWN FACTOR = MISSING FACTOR
```

General form:

``` text
x*y=P
 ↓
y=P/x
```

------------------------------------------------------------------------

## 3. Real-World Example — Rectangle Area

``` text
area = width * height

48 = 6 * h
```

Divide by `6`:

``` text
h = 48/6
  = 8
```

Visual:

``` text
          width = 6
      <-------------->
      +--------------+
      |              |
      |              | height = 8
      |              |
      +--------------+

area = 6 * 8 = 48
```

------------------------------------------------------------------------

## 4. Exact Product — Factor Pair Form

``` text
x*y=P
```

Fix `x`:

``` text
y=P/x
```

For integers:

``` text
P%x == 0
```

Example:

``` text
P=36

1*36
2*18
3*12
4*9
6*6
```

Only search to `sqrt(P)` because every divisor above `sqrt(P)` is paired
with one below it.

``` text
x*y=P
x<=y
   ↓
x<=sqrt(P)
```

------------------------------------------------------------------------

## 5. Product Upper Bound

For positive `x`:

``` text
x*y <= P
```

Divide by positive `x`:

``` text
y <= P/x
```

For integer `y`:

``` text
y <= floor(P/x)
```

Example:

``` text
3*y <= 10

y <= floor(10/3)
y <= 3
```

> **Sign warning:** dividing an inequality by a negative number reverses
> the inequality.

------------------------------------------------------------------------

## 6. Product Lower Bound

For positive `x`:

``` text
x*y >= P
```

Therefore:

``` text
y >= ceil(P/x)
```

Example:

``` text
3*y >= 10

y >= ceil(10/3)
y >= 4
```

For positive integers:

``` text
ceil(P/x) = (P+x-1)/x
```

------------------------------------------------------------------------

## 7. Divisibility Form

``` text
K | (x*y)
```

means:

``` text
(x*y)%K == 0
```

Instead of blindly multiplying, ask:

``` text
Which factors required by K
are already supplied by x?

Which factors are still missing from y?
```

Example:

``` text
K = 12 = 2²*3
x = 6  = 2*3
```

`x` supplies:

``` text
2¹*3¹
```

Still missing:

``` text
2¹
```

So `y=2` is sufficient:

``` text
6*2=12
```

------------------------------------------------------------------------

## 8. Prime-Exponent Model

Write:

``` text
N = product of p^e[p]
```

Multiplication adds exponents:

``` text
12 = 2²*3¹
18 = 2¹*3²

12*18
= 2^(2+1) * 3^(1+2)
= 2³*3³
```

Mental transformation:

``` text
MULTIPLICATION OF NUMBERS
          ↓
ADDITION OF PRIME EXPONENTS
```

Useful consequences:

``` text
perfect square
→ every total exponent even

perfect cube
→ every total exponent divisible by 3

nth power
→ every total exponent divisible by n
```

------------------------------------------------------------------------

## 9. Product Invariant Under Factor Transfer

If:

``` text
x*y=P
```

and `g|x`, move factor `g` from `x` to `y`:

``` text
x' = x/g
y' = y*g
```

Then:

``` text
x'*y'
= (x/g)*(y*g)
= x*y
= P
```

So:

``` text
FACTOR DISTRIBUTION may change
GLOBAL PRODUCT remains unchanged
```

------------------------------------------------------------------------

## 10. Overflow Warning

``` text
10^9 * 10^9 = 10^18
```

Use `long long`.

For values near `10^18`, even `long long` multiplication can overflow.

Instead of:

``` cpp
x * x <= P
```

prefer:

``` cpp
x <= P / x
```

when values are positive.

For unavoidable large multiplication, consider `__int128`.

------------------------------------------------------------------------

## 11. Algorithmic Reduction Matrix

| Mathematical Condition          | Meaning                    | Typical Technique                 |          Complexity |
|---------------------------------|----------------------------|-----------------------------------|--------------------:|
| `x*y=P`                         | Exact factor pair          | Divisor enumeration               |        `O(sqrt(P))` |
| `x*y=P`, array lookup           | Need `P/x`                 | Hash/frequency map                |         `O(n)` avg. |
| `x*y<=P`, positive values       | Partner upper bound        | Sort + 2 pointers / BS            |        `O(n log n)` |
| `x*y>=P`, positive values       | Partner lower bound        | Sort + BS / 2 pointers            |        `O(n log n)` |
| \`K                             | x\*y\`                     | Required factors must be supplied | GCD / factorization |
| product is square               | Prime exponent totals even | Factorization + parity            |              varies |
| `a*b*c=N`                       | Factor decomposition       | Divisor search                    |        `O(sqrt(N))` |
| Product preserved by operations | Exponent totals invariant  | Prime counting                    |              varies |

------------------------------------------------------------------------

## 12. Codeforces Mental Triggers

### Trigger 1 — “Product equals P”

``` text
x*y=P
  ↓
fix x
  ↓
y=P/x
  ↓
integer y?
  ↓
P%x==0
  ↓
divisor search
```

### Trigger 2 — “Product divisible by K”

``` text
K | x*y
   ↓
what does x already supply?
   ↓
what factor is missing?
   ↓
GCD / prime factors
```

### Trigger 3 — “Redistribute factors”

``` text
factor transfers
      ↓
global product unchanged
      ↓
prime-exponent totals unchanged
      ↓
check whether totals can be distributed as required
```

------------------------------------------------------------------------

## 13. Standard C++ Snippets

### Factor-pair enumeration

``` cpp
vector<pair<long long,long long>> factorPairs(long long P) {
    vector<pair<long long,long long>> ans;

    for (long long x=1; x<=P/x; ++x) {
        if (P%x==0)
            ans.push_back({x,P/x});
    }

    return ans;
}
```

``` text
Time:  O(sqrt(P))
Space: O(number of pairs)
```

### Prime factorization

``` cpp
map<long long,int> factorize(long long x) {
    map<long long,int> cnt;

    for (long long p=2; p<=x/p; ++p) {
        while (x%p==0) {
            ++cnt[p];
            x/=p;
        }
    }

    if (x>1)
        ++cnt[x];

    return cnt;
}
```

------------------------------------------------------------------------

## How to Study the Codeforces Variants

For every problem below, follow this order:

``` text
1. What is the problem asking?
        ↓
2. Tiny concrete example
        ↓
3. What can the operation/condition do?
        ↓
4. What cannot change?
        ↓
5. Remove story nouns
        ↓
6. Write the first equation
        ↓
7. Derive it one step at a time
        ↓
8. Extract the observation
        ↓
9. Choose the algorithm
        ↓
10. Horizontal dry run
```

Do not memorize the final formula first.

``` text
STORY → EXAMPLE → MATH → DERIVATION → OBSERVATION → ALGORITHM
```

------------------------------------------------------------------------

# 14. Curated Codeforces Benchmarks

| Problem                                      | Rating | Product Variant          | Key Observation                                                  | Technique                                              | Link                                               |
|----------------------------------------------|-------:|--------------------------|------------------------------------------------------------------|--------------------------------------------------------|----------------------------------------------------|
| CF 1294C — Product of Three Numbers          |   1300 | `a*b*c=n`                | Pick two divisors; third factor is forced                        | Divisor search                                         | <https://codeforces.com/problemset/problem/1294/C> |
| CF 1881D — Divide and Equalize               |   1300 | Global product invariant | Every prime's total exponent must divide evenly among `n` values | Prime factorization                                    | <https://codeforces.com/problemset/problem/1881/D> |
| CF 1744E1 — Divisible Numbers (Easy Version) |   1500 | \`(a\*b)                 | (x\*y)\`                                                         | Fix one factor and derive the missing required divisor | GCD + divisor reasoning                            |

------------------------------------------------------------------------

# 15. Variant 1 — CF 1294C: Product of Three Numbers

Problem: <https://codeforces.com/problemset/problem/1294/C>

## What Is This Problem Actually Asking?

You are given one integer `n`.

You must find **three pairwise-different integers**:

``` text
a, b, c
```

such that:

``` text
a > 1
b > 1
c > 1

a != b
a != c
b != c

a*b*c = n
```

### Step 1 — Tiny Example

Take:

``` text
n = 64
```

A valid decomposition is:

``` text
64 = 2 * 4 * 8
```

Check:

``` text
2,4,8 > 1       ✓
all are different ✓
2*4*8 = 64       ✓
```

### Step 2 — Do We Need to Search Three Variables?

No.

Start:

``` text
a*b*c = n
```

Choose a divisor `a`:

``` text
remaining = n/a
```

Now:

``` text
b*c = remaining
```

Choose divisor `b` of `remaining`:

``` text
c = remaining/b
```

So:

``` text
3 unknowns
   ↓ choose a
2 unknowns
   ↓ choose b
1 forced value
   ↓
c
```

### Step 3 — Why Divisors?

For integer factors:

``` text
n % a == 0
```

and after removing `a`:

``` text
remaining % b == 0
```

Only then is:

``` text
c = remaining/b
```

an integer.

### Step 4 — Final Observation

``` text
PRODUCT DECOMPOSITION
        ↓
pick one divisor
        ↓
shrink remaining product
        ↓
pick second divisor
        ↓
third factor is forced
```

So this is a **sequential divisor extraction** problem, not a
three-variable brute force problem.

## A. Remove Story Nouns

``` text
number n          → target product
three numbers     → factors a,b,c
different         → pairwise distinct
greater than 1    → non-trivial factors
```

Abstract problem:

``` text
Find a,b,c such that:

a*b*c=n

a,b,c > 1
a,b,c pairwise distinct
```

## B. Define Variables

``` text
n = target product
a = first extracted factor
b = second extracted factor
c = remaining factor
```

## C. Core Mathematical Constraint

``` text
a*b*c=n
```

Once `a,b` are known:

``` text
c=n/(a*b)
```

provided:

``` text
n%(a*b)==0
```

## D. Algebraic Derivation

``` text
a*b*c=n
```

Choose divisor `a`:

``` text
remaining=n/a
```

Now:

``` text
b*c=remaining
```

Choose divisor `b`:

``` text
c=remaining/b
```

So:

``` text
3 unknown factors
      ↓ choose a
2 unknown factors
      ↓ choose b
1 forced factor
      ↓
c=n/(a*b)
```

## E. Key Observation

``` text
product decomposition
       ↓
every selected divisor shrinks the remaining product
       ↓
do not brute-force three variables
```

## F. Solution

``` text
1. Find non-trivial divisor a of n.
2. remaining=n/a.
3. Find divisor b of remaining with b!=a.
4. c=remaining/b.
5. Verify c>1 and c differs from a,b.
```

## G. Horizontal Dry Run

``` text
n = 64

a = 2
remaining = 64/2 = 32

choose b = 4

c = 32/4 = 8

a:        2
b:        4
c:        8
product:  2*4*8 = 64
distinct: YES
```

## H. Pseudocode

``` text
READ n

find divisor a>1

remaining=n/a

find divisor b of remaining
with b>1 and b!=a

IF found:
    c=remaining/b

    IF c>1 and c!=a and c!=b:
        PRINT YES
        PRINT a,b,c
        stop

PRINT NO
```

## I. Complexity

``` text
Time:  O(sqrt(n))
Space: O(1)
```

## J. Variant Lesson

``` text
BASE:
a*b*c=n

EXTRA:
three distinct non-trivial factors

TRANSFORMATION:
choose a → remaining=n/a
choose b → c=remaining/b

OBSERVATION:
chosen factors force the remaining factor

ALGORITHM:
sequential divisor extraction
```

------------------------------------------------------------------------

# 16. Variant 2 — CF 1881D: Divide and Equalize

Problem: <https://codeforces.com/problemset/problem/1881/D>

## What Is This Problem Actually Asking?

You have `n` numbers. The allowed operation lets prime factors be
redistributed between the numbers.

The target is:

``` text
all n numbers become equal
```

The easiest way to understand the condition is to ignore the formula
first.

### Step 1 — Tiny Example

``` text
a = [50, 200]
n = 2
```

Prime factorization:

``` text
50  = 2^1 * 5^2
200 = 2^3 * 5^2
```

Count prime copies globally:

``` text
prime 2:
1 + 3 = 4 copies

prime 5:
2 + 2 = 4 copies
```

### Step 2 — Split Those Copies Equally

We need `2` equal final numbers.

For prime `2`:

``` text
4 copies / 2 numbers
= 2 copies each
```

For prime `5`:

``` text
4 copies / 2 numbers
= 2 copies each
```

So each final number receives:

``` text
2^2 * 5^2 = 100
```

Therefore:

``` text
[50,200]
    ↓ redistribute factors
[100,100]
```

### Step 3 — What Does Not Change?

Factors can move, but the **total number of copies of each prime** does
not change.

That is the invariant.

``` text
simulate factor transfers? NO
        ↓
count global prime exponents
```

### Step 4 — Derive the Formula

Let:

``` text
E[p] = total exponent of prime p
```

If every final number is equal and each contains `q` copies of `p`,
then:

``` text
q + q + ... + q
     n times
=
n*q
```

But all available copies total:

``` text
E[p]
```

Therefore:

``` text
E[p] = n*q
```

Solve:

``` text
q = E[p]/n
```

`q` must be an integer.

Therefore:

``` text
E[p] % n == 0
```

for every prime.

### Step 5 — Impossible Example

``` text
a = [2,4]
n = 2
```

Factorization:

``` text
2 = 2^1
4 = 2^2
```

Total copies of prime `2`:

``` text
E[2] = 1+2 = 3
```

Trying to distribute equally:

``` text
3/2 = 1.5
```

Impossible.

Equivalent test:

``` text
3 % 2 != 0
```

So:

``` text
NO
```

### Step 6 — Final Observation

``` text
operation redistributes factors
        ↓
prime-exponent totals are invariant
        ↓
equal numbers need equal exponent counts
        ↓
every E[p] must split into n equal integer parts
        ↓
E[p] % n == 0
```

## A. Remove Story Nouns

``` text
array values       → numbers containing prime factors
allowed operation  → redistribute prime factors
make equal         → each final value receives equal exponent counts
```

The global product is the invariant:

``` text
P=a[1]*a[2]*...*a[n]
```

## B. Define Variables

``` text
n       = number of values
cnt[p]  = total exponent of prime p across all values
q       = exponent of p in each final equal value
```

## C. Core Mathematical Constraint

For a prime `p`, suppose the global product contains exponent:

``` text
E=cnt[p]
```

If all `n` final numbers are equal:

``` text
E = n*q
```

Therefore:

``` text
E%n==0
```

for every prime.

## D. Algebraic Derivation

``` text
a[i] = product p^(e[i][p])
```

Across all elements:

``` text
E[p]
=
e[1][p]+e[2][p]+...+e[n][p]
```

Equal final values require:

``` text
each gets q copies of p

q+q+...+q
   n times
=
n*q
=
E[p]
```

Thus:

``` text
q=E[p]/n
```

must be integer:

``` text
E[p]%n==0
```

## E. Key Observation

``` text
simulate factor transfers? NO
          ↓
global product invariant
          ↓
prime exponent totals invariant
          ↓
equal distribution possible iff
every exponent total is divisible by n
```

## F. Solution

``` text
1. Factorize every a[i].
2. Add prime exponents globally.
3. For every prime p:
       if cnt[p]%n != 0:
           NO
4. Otherwise YES.
```

## G. Horizontal Dry Run

``` text
a = [50, 200]
n = 2

50  = 2¹ * 5²
200 = 2³ * 5²

prime:       2    5
total exp:   4    4
divide by n: 2    2

both divisible → YES

final equal value:
2²*5² = 100

50*200 = 100*100
```

## H. Pseudocode

``` text
READ n
cnt = empty map

FOR each x:
    factorize x

    FOR each (prime, exponent):
        cnt[prime] += exponent

FOR each prime:
    IF cnt[prime]%n != 0:
        PRINT NO
        stop

PRINT YES
```

## I. Complexity

Basic trial division:

``` text
Time:  O(sum sqrt(a[i])) worst-case basic implementation
Space: O(number of distinct primes)
```

## J. Variant Lesson

``` text
BASE:
P = product of all a[i]

INVARIANT:
P does not change

PRIME SPACE:
multiplication → addition of exponents

TARGET:
equal values

CONDITION:
cnt[p]%n==0 for every p

ALGORITHM:
factorization + exponent counting
```

------------------------------------------------------------------------

# 17. Variant 3 — CF 1744E1: Divisible Numbers (Easy Version)

Problem: <https://codeforces.com/problemset/problem/1744/E1>

## What Is This Problem Actually Asking?

You are given:

``` text
a,b,c,d
```

Find:

``` text
x,y
```

such that:

``` text
a < x <= c
b < y <= d
```

and:

``` text
a*b divides x*y
```

The difficult part is that both `x` and `y` are unknown.

The key strategy is:

``` text
fix x
  ↓
find what factors x already supplies
  ↓
make y supply only the missing factors
```

### Step 1 — Tiny Example

Take:

``` text
a=3
b=4
c=5
d=7
```

Then:

``` text
x ∈ {4,5}
y ∈ {5,6,7}
```

Required divisor:

``` text
P=a*b=12
```

Need:

``` text
12 | x*y
```

Try:

``` text
x=4
```

Factor view:

``` text
12 = 2^2 * 3
4  = 2^2
```

`x=4` already supplies:

``` text
2^2
```

Still missing:

``` text
3
```

So `y` only needs to be divisible by `3`.

In:

``` text
{5,6,7}
```

choose:

``` text
y=6
```

Check:

``` text
x*y = 4*6 = 24
24 % 12 = 0
```

So `(4,6)` works.

### Step 2 — Generalize “Already Supplied”

Let:

``` text
P=a*b
```

For fixed `x`, the common part between `x` and `P` is:

``` text
g = gcd(x,P)
```

This is the part of `P` already supplied by `x`.

Therefore the remaining required factor is:

``` text
need = P/g
```

or:

``` text
need = P/gcd(x,P)
```

### Step 3 — Why Does This Formula Work?

Write:

``` text
x = g*x'
P = g*p'
```

where:

``` text
g = gcd(x,P)
```

After removing the common factor:

``` text
gcd(x',p') = 1
```

Requirement:

``` text
P | x*y
```

Substitute:

``` text
g*p' | g*x'*y
```

Cancel `g`:

``` text
p' | x'*y
```

Since:

``` text
gcd(x',p')=1
```

`x'` cannot supply the remaining factors of `p'`.

Therefore:

``` text
p' | y
```

and because:

``` text
p'=P/g
```

we obtain:

``` text
need=P/gcd(x,P)
```

### Step 4 — Jump Directly to y

Now `y` must satisfy:

``` text
y>b
need | y
```

Multiples of `need` are:

``` text
need, 2*need, 3*need, ...
```

The first multiple strictly greater than `b` is:

``` text
y=(floor(b/need)+1)*need
```

With integer division:

``` text
y=(b/need+1)*need
```

Then check:

``` text
y<=d
```

### Step 5 — Full Mental Pipeline

``` text
(a*b) | (x*y)
        ↓
P=a*b
        ↓
fix x
        ↓
g=gcd(x,P)
        ↓
need=P/g
        ↓
need | y
        ↓
jump to first multiple of need > b
        ↓
check y<=d
```

So instead of brute-forcing both variables:

``` text
enumerate x + mathematically derive y
```

## A. Remove Story Nouns

Abstract problem:

``` text
Given a,b,c,d.

Find x,y such that:

a < x <= c
b < y <= d

and

a*b divides x*y
```

## B. Define Variables

``` text
P = a*b

x = first chosen number
y = second chosen number

condition:
P | x*y
```

## C. Core Mathematical Constraint

``` text
P | x*y
```

Fix `x`.

Let:

``` text
g=gcd(x,P)
```

`g` represents the part of the required factorization of `P` already
supplied by `x`.

Missing mandatory factor:

``` text
need=P/g
```

Then choose `y` divisible by `need`.

## D. Algebraic Derivation

Write:

``` text
x=g*x'
P=g*p'
```

where:

``` text
gcd(x',p')=1
```

Requirement:

``` text
P | x*y
```

Substitute:

``` text
g*p' | g*x'*y
```

Cancel `g`:

``` text
p' | x'*y
```

Since:

``` text
gcd(x',p')=1
```

the missing factor must divide `y`:

``` text
p' | y
```

and:

``` text
p'=P/g
```

Therefore:

``` text
need=P/gcd(x,P)
```

## E. Key Observation

``` text
P | x*y
   ↓
fix x
   ↓
supplied = gcd(x,P)
   ↓
missing = P/gcd(x,P)
   ↓
find a multiple of missing in y's allowed range
```

## F. Find the First Valid Multiple

Need:

``` text
y>b
need | y
```

First multiple strictly greater than `b`:

``` text
y = (floor(b/need)+1)*need
```

If:

``` text
y<=d
```

it works.

## G. Horizontal Dry Run

``` text
a=3
b=4
c=5
d=7

P=a*b=12

try x=4

g=gcd(4,12)=4

need=12/4=3

first multiple of 3 > 4:

y=(4/3+1)*3
 =(1+1)*3
 =6

check:
4*6=24
24%12=0

answer:
x=4, y=6
```

## H. Pseudocode

``` text
P=a*b

FOR x=a+1 ... c:

    g=gcd(x,P)

    need=P/g

    y=(b/need+1)*need

    IF y<=d:
        PRINT x,y
        stop

PRINT -1,-1
```

## I. Complexity

For the easy-version enumeration:

``` text
Time:  O((c-a) log P)
Space: O(1)
```

## J. Variant Lesson

``` text
BASE:
P | x*y

FIX:
x

SUPPLIED FACTOR:
gcd(x,P)

MISSING FACTOR:
P/gcd(x,P)

TARGET:
find multiple of missing factor in (b,d]

ALGORITHM:
enumerate x + GCD + next multiple
```

------------------------------------------------------------------------

# 18. Compare the Three Product Variants

| Problem   | Base Form | Extra Constraint             | Derived Form            | Observation                    | Algorithm                        |
|-----------|-----------|------------------------------|-------------------------|--------------------------------|----------------------------------|
| CF 1294C  | `a*b*c=n` | Distinct factors `>1`        | `c=n/(a*b)`             | Picked factors force remainder | Divisor extraction               |
| CF 1881D  | `P=∏a[i]` | Redistribute factors equally | `cnt[p]%n=0`            | Product becomes exponent sums  | Prime factorization              |
| CF 1744E1 | \`P       | x\*y\`                       | `x,y` in bounded ranges | `need=P/gcd(x,P)`              | Fixed `x` reveals missing factor |

------------------------------------------------------------------------

# 19. Product Constraint — Variant Recognition Map

``` text
                         PRODUCT CONSTRAINT
                                |
          +---------------------+----------------------+
          |                     |                      |
       EXACT                  BOUNDED              DIVISIBILITY
       x*y=P                 x*y<=P                K | x*y
          |                     |                      |
       y=P/x              y<=floor(P/x)         missing factors
          |                     |                      |
    divisor check          sign/domain check       GCD / primes
          |
     divisors <= sqrt(P)


MULTI-FACTOR
a*b*c=N
    |
pick factors
    |
remainder forced


GLOBAL PRODUCT
P=∏a[i]
    |
prime factorize
    |
multiplication → exponent addition
    |
exponent totals become invariants
```

------------------------------------------------------------------------

# 20. Instant Recognition Drill

| Statement Phrase           | Mathematical Translation       |
|----------------------------|--------------------------------|
| “multiply to `P`”          | `x*y=P`                        |
| “product at most `P`”      | `x*y<=P`                       |
| “product at least `P`”     | `x*y>=P`                       |
| “product divisible by `K`” | \`K                            |
| “split `N` into factors”   | `a*b*...=N`                    |
| “product is a square”      | all total prime exponents even |

### Mental drills

``` text
1. x*y=60, x=5 → y?
2. x*y=60 → integer y requires what?
3. 7*y>=30 → minimum integer y?
4. Product is a perfect square → exponent condition?
5. a*b*c=N, a and b known → c?
```

### Answers

``` text
1. y=60/5=12

2. 60%x==0

3. y>=ceil(30/7)=5

4. Every prime exponent is even.

5. c=N/(a*b), provided a*b divides N.
```

------------------------------------------------------------------------

# 21. Mathematical Form to Memorize

``` text
FORM:
Product Constraint

EXACT:
x*y=P
→ y=P/x
→ integer y requires P%x==0

UPPER:
x*y<=P, x>0
→ y<=floor(P/x)

LOWER:
x*y>=P, x>0
→ y>=ceil(P/x)

DIVISIBILITY:
K | x*y
→ find supplied vs missing factors

MULTI-FACTOR:
a*b*c=N
→ choose a,b
→ c=N/(a*b)

PRIME MODEL:
A*B
→ prime exponents add

GLOBAL PRODUCT:
∏a[i]
→ total prime exponents are conserved
```

Core comparison:

``` text
SUM:
missing = total-known

PRODUCT:
missing factor = total/known
```

Always check:

``` text
exact divisibility?
sign?
zero?
overflow?
prime-factor interpretation?
```

------------------------------------------------------------------------

# 22. Pattern Recognition

### SIGNAL

``` text
"product"
"multiply"
"factor"
"divisible"
"multiple"
"perfect square/cube"
"split into factors"
```

### THINK

``` text
Can I isolate a missing factor?

Does known factor divide target?

Can I enumerate divisors only to sqrt(N)?

Would GCD expose already supplied factors?

Would prime-exponent space simplify the product?

Is product preserved under operations?

Can multiplication overflow?
```

### TYPICAL SOLUTIONS

``` text
divisor enumeration
prime factorization
GCD / LCM
prime-exponent counting
hash/frequency lookup
binary search / two pointers
constructive factor extraction
```

------------------------------------------------------------------------

# 23. Contest Mental Compression

``` text
READ ENGLISH
     ↓
REMOVE STORY NOUNS
     ↓
identify factors + target
     ↓
WRITE PRODUCT CONDITION
     ↓
x*y=P
x*y<=P
K | x*y
     ↓
DIVIDE / FACTORIZE
     ↓
missing factor
or
missing prime exponents
     ↓
CHECK
divisibility / sign / bounds / overflow
     ↓
OBSERVATION
     ↓
divisors / GCD / primes / lookup / 2ptr
```

Ultra-compressed:

``` text
"product" → factors → divide/factorize → missing factor/exponent → algorithm
```

------------------------------------------------------------------------

# 24. Final One-Line Takeaway

**When a product is constrained, think in factors: isolate by division
for exact products, and switch to GCD or prime-exponent space when
divisibility is the real condition.**
