# Form 4 --- Ratio Constraint

> **Goal:** Recognize ratio/proportion constraints, remove division
> safely, derive integer equations, and reduce them to GCD
> normalization, scaling, interval counting, or factor normalization.

## Table of Contents

-   [1. What is a Ratio Constraint?](#1-what-is-a-ratio-constraint)
-   [2. Simple Example](#2-simple-example)
-   [3. Real-World Example --- Recipe
    Proportion](#3-real-world-example--recipe-proportion)
-   [4. Core Algebra --- Cross
    Multiplication](#4-core-algebra--cross-multiplication)
-   [5. Normalized Ratio](#5-normalized-ratio)
-   [6. Scaling Form](#6-scaling-form)
-   [7. Fixed Multiplier Ratio](#7-fixed-multiplier-ratio)
-   [8. Power Ratio](#8-power-ratio)
-   [9. Ratio + Bounds](#9-ratio--bounds)
-   [10. Ratio Comparison Without Floating
    Point](#10-ratio-comparison-without-floating-point)
-   [11. Ratio Invariant](#11-ratio-invariant)
-   [12. Important Domain & Overflow
    Warnings](#12-important-domain--overflow-warnings)
-   [13. Algorithmic Reduction Matrix](#13-algorithmic-reduction-matrix)
-   [14. Codeforces Mental Triggers](#14-codeforces-mental-triggers)
-   [15. Standard C++ Snippets](#15-standard-c-snippets)
-   [16. Curated Codeforces
    Benchmarks](#16-curated-codeforces-benchmarks)
-   [17. CF 337B --- Routine
    Problem](#17-variant-1--cf-337b-routine-problem)
    -   [What this problem is all
        about](#what-this-problem-is-all-about)
    -   [Mathematical model](#c-algebraic-derivation)
-   [18. CF 573A --- Bear and
    Poker](#18-variant-2--cf-573a-bear-and-poker)
    -   [What this problem is all
        about](#what-this-problem-is-all-about-1)
    -   [Mathematical model](#c-ratio-formulation)
-   [19. CF 2044E --- Insane
    Problem](#19-variant-3--cf-2044e-insane-problem)
    -   [What this problem is all
        about](#what-this-problem-is-all-about-2)
    -   [Mathematical model](#c-algebraic-derivation-1)
-   [20. Compare the Three Ratio
    Variants](#20-compare-the-three-ratio-variants)
-   [21. Ratio Constraint --- Recognition
    Map](#21-ratio-constraint--recognition-map)
-   [22. Instant Recognition Drill](#22-instant-recognition-drill)
-   [23. Mathematical Form to
    Memorize](#23-mathematical-form-to-memorize)
-   [24. When NOT to Divide](#24-when-not-to-divide)
-   [25. Pattern Recognition](#25-pattern-recognition)
-   [26. Contest Mental Compression](#26-contest-mental-compression)
-   [27. Final One-Line Takeaway](#27-final-one-line-takeaway)

------------------------------------------------------------------------

------------------------------------------------------------------------

## 1. What is a Ratio Constraint?

``` text
x : y = a : b
x / y = a / b
x / y = k
y / x = k^t
```

Typical Codeforces signals:

``` text
"ratio is..."
"same proportion"
"preserve aspect ratio"
"one value is k times another"
"y/x is a power of k"
"scale both dimensions equally"
```

Core transformation:

``` text
x/y = a/b
    ↓ cross multiply
b*x = a*y
```

------------------------------------------------------------------------

## 2. Simple Example

``` text
x:y = 2:3
x = 8
```

Write:

``` text
8/y = 2/3
```

Cross multiply:

``` text
8*3 = 2*y
24 = 2y
y = 12
```

Therefore:

``` text
8:12 = 2:3
```

------------------------------------------------------------------------

## 3. Real-World Example --- Recipe Proportion

``` text
water:syrup = 4:1
water = 12
```

``` text
12/syrup = 4/1
12*1 = 4*syrup
syrup = 3
```

Visual:

``` text
base:    4 : 1
          ↓ ×3
scaled: 12 : 3
```

Mental model:

``` text
SAME RATIO
    ↓
SAME SCALE FACTOR
```

------------------------------------------------------------------------

## 4. Core Algebra --- Cross Multiplication

Start:

``` text
x/y = a/b
```

Require:

``` text
y != 0
b != 0
```

Multiply by `y`:

``` text
x = a*y/b
```

Multiply by `b`:

``` text
b*x = a*y
```

Therefore:

``` text
x/y = a/b
⇔
b*x = a*y
```

Example:

``` text
6/9 = 2/3

6*3 = 9*2
18 = 18
```

This avoids floating-point equality.

------------------------------------------------------------------------

## 5. Normalized Ratio

Equivalent ratios:

``` text
2:3
4:6
6:9
20:30
```

Canonical form:

``` text
g = gcd(x,y)

x' = x/g
y' = y/g
```

Example:

``` text
20:30

gcd=10

20/10 : 30/10
= 2:3
```

So for positive integer pairs:

``` text
same ratio
    ↓
normalize both with GCD
    ↓
compare reduced pairs
```

------------------------------------------------------------------------

## 6. Scaling Form

If:

``` text
x:y = a:b
```

then for some scale `t`:

``` text
x=a*t
y=b*t
```

Example:

``` text
ratio 3:5

t=1 → (3,5)
t=2 → (6,10)
t=3 → (9,15)
t=4 → (12,20)
```

``` text
(a,b)
  ↓ ×t
(a*t,b*t)
```

------------------------------------------------------------------------

## 7. Fixed Multiplier Ratio

``` text
y/x = k
```

Multiply by `x`:

``` text
y=k*x
```

Thus choosing `x` forces `y`.

Example:

``` text
y/x=4
x=7

y=4*7=28
```

------------------------------------------------------------------------

## 8. Power Ratio

``` text
y/x = k^t
```

where `t>=0`.

Multiply by `x`:

``` text
y=x*k^t
```

Possible ratios:

``` text
1, k, k², k³, ...
```

For fixed:

``` text
p=k^t
```

we get:

``` text
y=p*x
```

------------------------------------------------------------------------

## 9. Ratio + Bounds

Suppose:

``` text
Lx <= x <= Rx
Ly <= y <= Ry

y=p*x
p>0
```

Substitute:

``` text
Ly <= p*x <= Ry
```

Divide by positive `p`:

``` text
Ly/p <= x <= Ry/p
```

For integer `x`:

``` text
ceil(Ly/p) <= x <= floor(Ry/p)
```

Intersect with original bounds:

``` text
L=max(Lx, ceil(Ly/p))
R=min(Rx, floor(Ry/p))
```

Number of valid values:

``` text
max(0, R-L+1)
```

This is a major CP transformation:

``` text
RATIO
  ↓
MULTIPLICATIVE EQUATION
  ↓
INTEGER INTERVAL
```

------------------------------------------------------------------------

## 10. Ratio Comparison Without Floating Point

For positive denominators:

``` text
a/b < c/d
⇔
a*d < c*b
```

Equality:

``` text
a/b = c/d
⇔
a*d = c*b
```

Example:

``` text
7/10 ? 2/3

7*3 = 21
2*10 = 20

21 > 20

therefore:
7/10 > 2/3
```

------------------------------------------------------------------------

## 11. Ratio Invariant

Multiply both parts by the same non-zero factor:

``` text
x:y
 ↓ ×k
kx:ky
```

Then:

``` text
kx/ky = x/y
```

So ratio is preserved.

Likewise, dividing both by a common factor preserves the ratio. This is
why GCD normalization works.

------------------------------------------------------------------------

## 12. Important Domain & Overflow Warnings

### Division by zero

``` text
x/y
```

requires:

``` text
y != 0
```

### Inequality sign

For:

``` text
x/y <= k
```

if `y>0`:

``` text
x <= k*y
```

if `y<0`, the inequality reverses:

``` text
x >= k*y
```

### Overflow

Cross products such as:

``` text
a*d
c*b
```

can overflow. Use `long long` or `__int128` as required.

------------------------------------------------------------------------

## 13. Algorithmic Reduction Matrix

  -----------------------------------------------------------------------
  Mathematical     Meaning          Typical                    Complexity
  Condition                         Technique        
  ---------------- ---------------- ---------------- --------------------
  `x/y=a/b`        Equal ratios     Cross                          `O(1)`
                                    multiplication   

  `x:y=a:b`        Same proportion  GCD                        `O(log V)`
                                    normalization    

  `x=a*t, y=b*t`   Scaled ratio     Solve/count                    `O(1)`
                                    valid `t`        

  `y=k*x`          Fixed multiplier Substitution                   `O(1)`

  `y/x=k^t`        Power ratio      Enumerate powers         `O(log_k M)`

  `y=p*x` with     Count            Interval               `O(1)` per `p`
  ranges           proportional     intersection     
                   pairs                             

  `a/b ? c/d`      Exact ratio      Cross                          `O(1)`
                   comparison       multiplication   

  Ratio differs    Multiplicative   Strip factors    `O(log V)` per value
  only by allowed  normalization                     
  primes                                             

  Aspect ratio     Same shape under Cross products +               `O(1)`
  preserved        scaling          limiting         
                                    dimension        
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 14. Codeforces Mental Triggers

### Trigger 1 --- "Same proportion / preserve ratio"

``` text
x:y=a:b
   ↓
x/y=a/b
   ↓
b*x=a*y
```

Ask:

``` text
Can I remove floating point completely?
```

### Trigger 2 --- "One is k times the other"

``` text
y/x=k
  ↓
y=k*x
```

With bounds:

``` text
Ly<=k*x<=Ry
```

derive an interval for `x`.

### Trigger 3 --- "Ratio is a power of k"

``` text
y/x=k^t
    ↓
p=k^t
    ↓
y=p*x
    ↓
convert y-bounds into x-bounds
```

------------------------------------------------------------------------

## 15. Standard C++ Snippets

### Equal ratio

``` cpp
bool sameRatio(long long a, long long b,
               long long c, long long d) {
    return (__int128)a*d == (__int128)c*b;
}
```

### Normalize positive ratio

``` cpp
pair<long long,long long> normalize(long long x, long long y) {
    long long g = std::gcd(x,y);
    return {x/g,y/g};
}
```

### Positive ceiling division

``` cpp
long long ceilDiv(long long a,long long b) {
    return (a+b-1)/b;
}
```

------------------------------------------------------------------------

# 16. Curated Codeforces Benchmarks

  ------------------------------------------------------------------------------------------------------------------------
  Problem     Rating Ratio Variant    Key            Technique          Link
                                      Mathematical                      
                                      Observation                       
  --------- -------- ---------------- -------------- ------------------ --------------------------------------------------
  CF 337B       1400 Aspect ratio     Compare        Cross              https://codeforces.com/problemset/problem/337/B
  ---                                 proportions    multiplication +   
  Routine                             using `a*d`    GCD                
  Problem                             and `b*c`                         

  CF 573A       1300 Allowed          Strip factors  Factor             https://codeforces.com/problemset/problem/573/A
  --- Bear           multiplicative   `2` and `3`;   normalization      
  and Poker          ratio            invariant                         
                                      cores must                        
                                      match                             

  CF 2044E      1300 `y/x=k^n`        For each       Power              https://codeforces.com/problemset/problem/2044/E
  ---                                 power, convert enumeration +      
  Insane                              `y=p*x` into   bounds             
  Problem                             interval                          
                                      intersection                      
  ------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------

[↑ Back to Table of Contents](#table-of-contents)

# 17. Variant 1 --- CF 337B: Routine Problem

Problem: https://codeforces.com/problemset/problem/337/B

## What This Problem Is All About

**In simple words:** Your monitor and the movie have different
width-to-height ratios. You are allowed to resize the movie, but you
must scale its width and height by the **same factor**, so the movie's
shape cannot be distorted. Make the movie as large as possible while
still fitting completely on the monitor, then calculate what fraction of
the monitor remains empty.

``` text
MONITOR:
a : b

MOVIE:
c : d

allowed:
(c,d) → (c*t,d*t)

not allowed:
change width and height independently
```

The real mathematical question is:

``` text
How much can ratio c:d be scaled
inside ratio a:b
without changing c:d?
```

This is why the story reduces to **ratio comparison** rather than
geometry simulation.

**What you must output:** the fraction

``` text
empty screen area
-----------------
total screen area
```

in irreducible form.

**Main recognition trigger:**

``` text
"preserve the original frame ratio"
                ↓
same scale factor on both dimensions
                ↓
compare a/b with c/d
                ↓
cross multiply: a*d vs b*c
```

## A. Remove Story Nouns

``` text
monitor dimensions → container a,b
movie dimensions   → required ratio c,d
zoom proportionally → same scale on both dimensions
empty screen       → unused area
```

Abstract problem:

``` text
container ratio = a:b
object ratio    = c:d

Scale c:d proportionally.
Fit it completely inside a:b.
Maximize occupied area.
Find unused area / total area.
```

## B. Define Variables

``` text
screen = a*b
movie dimensions = c*t by d*t
```

Fit conditions:

``` text
c*t <= a
d*t <= b
```

Therefore:

``` text
t <= a/c
t <= b/d
```

Largest valid scale:

``` text
t=min(a/c,b/d)
```

## C. Algebraic Derivation

Compare:

``` text
a/c ? b/d
```

Remove division:

``` text
a*d ? b*c
```

Define:

``` text
x=a*d
y=b*c
```

The occupied fraction becomes:

``` text
min(x,y)/max(x,y)
```

Therefore empty fraction:

``` text
1 - min(x,y)/max(x,y)
```

Put over common denominator:

``` text
(max(x,y)-min(x,y))/max(x,y)
```

So:

``` text
num = |a*d-b*c|
den = max(a*d,b*c)
```

Finally reduce by:

``` text
g=gcd(num,den)
```

## D. Observation

The geometry collapses to one ratio comparison:

``` text
a/b versus c/d
```

which becomes:

``` text
a*d versus b*c
```

No floating point is needed.

## E. Solution

``` text
x=a*d
y=b*c

num=abs(x-y)
den=max(x,y)

g=gcd(num,den)

print num/g over den/g
```

## F. Horizontal Dry Run

``` text
a=1  b=1  c=3  d=2

a*d = 2
b*c = 3

occupied = 2/3
empty    = 1-2/3
         = 1/3

answer = 1/3
```

## G. Pseudocode

``` text
READ a,b,c,d

x=a*d
y=b*c

num=ABS(x-y)
den=MAX(x,y)

g=GCD(num,den)

PRINT num/g "/" den/g
```

## H. Variant Lesson

``` text
RATIO:
a/b versus c/d

REMOVE DIVISION:
a*d versus b*c

OBSERVATION:
ratio comparison becomes integer multiplication

FINAL:
reduce fraction with GCD
```

------------------------------------------------------------------------

[↑ Back to Table of Contents](#table-of-contents)

# 18. Variant 2 --- CF 573A: Bear and Poker

Problem: https://codeforces.com/problemset/problem/573/A

## What This Problem Is All About

**In simple words:** There are several poker bids. A player may
repeatedly multiply their own bid by `2` or by `3`. You need to decide
whether all bids can eventually become exactly the same value.

Example idea:

``` text
50   → ×2 → 100 → ×3 → 300
75   → ×2 → 150 → ×2 → 300
150  → ×2 → 300
```

The important restriction is that operations can introduce only prime
factors:

``` text
2 and 3
```

They cannot change any other prime-factor part of a number.

So instead of simulating operations, write each number conceptually as:

``` text
a[i] = core[i] * 2^p * 3^q
```

Remove every factor `2` and `3`. If all remaining `core[i]` values are
equal, the bids are compatible; otherwise they can never become equal.

**What you must output:**

``` text
Yes → all bids can be made equal
No  → impossible
```

**Main recognition trigger:**

``` text
"you may repeatedly multiply by 2 or 3"
                    ↓
only powers of 2 and 3 may change
                    ↓
remove those factors
                    ↓
compare invariant cores
```

## A. Remove Story Nouns

``` text
bids                → integers a[i]
double               → multiply by 2
triple               → multiply by 3
make all equal       → reach common value
```

Abstractly:

``` text
Each value may be multiplied by 2^p * 3^q.
Can all values become equal?
```

## B. Define Variables

Write every number as:

``` text
a[i] = core[i] * 2^p * 3^q
```

where `core[i]` contains no factor `2` or `3`.

## C. Ratio Formulation

For two values:

``` text
x = r*2^a*3^b
y = s*2^c*3^d
```

Their ratio is:

``` text
x/y
=
(r/s)*2^(a-c)*3^(b-d)
```

Allowed operations affect only powers of `2` and `3`.

They cannot repair a mismatch in:

``` text
r/s
```

Therefore all invariant cores must be equal:

``` text
r=s
```

## D. Algebraic Derivation

Normalize:

``` text
while x%2==0:
    x/=2

while x%3==0:
    x/=3
```

What remains cannot be changed by allowed multipliers.

That remainder is the multiplicative invariant.

## E. Observation

Do not simulate operations.

``` text
allowed multipliers = 2 and 3
        ↓
remove every factor 2 and 3
        ↓
compare what cannot change
```

## F. Horizontal Dry Run

``` text
values: 75      150      75      50

75  = 25*3
150 = 25*2*3
75  = 25*3
50  = 25*2

core:   25       25      25      25

all same → YES
```

## G. Pseudocode

``` text
FUNCTION NORMALIZE(x):
    WHILE x%2==0:
        x=x/2

    WHILE x%3==0:
        x=x/3

    RETURN x


target=NORMALIZE(a[0])

FOR each x:
    IF NORMALIZE(x) != target:
        PRINT No
        stop

PRINT Yes
```

## H. Complexity

``` text
Time:  O(n log A)
Space: O(1) extra
```

## I. Variant Lesson

``` text
RATIO DIFFERENCE:
may contain only powers of 2 and 3

NORMALIZATION:
strip factors 2 and 3

INVARIANT:
remaining core

ALGORITHM:
normalize + compare
```

------------------------------------------------------------------------

[↑ Back to Table of Contents](#table-of-contents)

# 19. Variant 3 --- CF 2044E: Insane Problem

Problem: https://codeforces.com/problemset/problem/2044/E

## What This Problem Is All About

**In simple words:** You have one interval for `x` and another interval
for `y`. Count all ordered pairs `(x,y)` for which the ratio `y/x` is
exactly some non-negative power of `k`.

Required:

``` text
l1 <= x <= r1
l2 <= y <= r2

and

y/x = k^n
for some n >= 0
```

At first this looks like a two-variable pair-counting problem over
ranges as large as `10^9`, so enumerating `x` or `y` is impossible.

The key is to enumerate the **ratio**, not the values:

``` text
p = k^n

1, k, k², k³, ...
```

There are only logarithmically many relevant powers.

For each fixed `p`:

``` text
y/x = p
  ↓
y = p*x
```

Now `y`'s allowed interval tells us exactly which `x` values are
possible:

``` text
l2 <= p*x <= r2
        ↓
ceil(l2/p) <= x <= floor(r2/p)
```

Intersect that with `[l1,r1]` and count the integers.

**What you must output:** the total number of valid ordered pairs
`(x,y)`.

**Main recognition trigger:**

``` text
y/x = k^n
    ↓
enumerate p=k^n
    ↓
y=p*x
    ↓
convert y-range into x-range
    ↓
interval intersection + counting
```

## A. Remove Story Nouns

``` text
Choose x,y with:

l1 <= x <= r1
l2 <= y <= r2

and:

y/x = k^n
```

## B. Define Variables

``` text
p=k^n
```

Then:

``` text
y/x=p
```

Multiply by `x`:

``` text
y=p*x
```

## C. Algebraic Derivation

Start with `y` bounds:

``` text
l2 <= y <= r2
```

Substitute:

``` text
l2 <= p*x <= r2
```

Because:

``` text
p>0
```

divide by `p`:

``` text
l2/p <= x <= r2/p
```

For integer `x`:

``` text
ceil(l2/p) <= x <= floor(r2/p)
```

Original x-range:

``` text
l1 <= x <= r1
```

Intersect:

``` text
L=max(l1,ceil(l2/p))
R=min(r1,floor(r2/p))
```

Count:

``` text
max(0,R-L+1)
```

## D. Observation

``` text
y/x=k^n
    ↓
p=k^n
    ↓
y=p*x
    ↓
y-bounds become x-bounds
    ↓
interval intersection
```

Do not enumerate `(x,y)` pairs.

Only logarithmically many powers of `k` are relevant.

## E. Horizontal Dry Run

``` text
k=3
x in [5,7]
y in [15,63]

p:             1       3       9       27
x from y:    [15,63] [5,21]  [2,7]   [1,2]
with [5,7]:    none    [5,7]   [5,7]   none
count:           0       3       3       0

total = 6
```

Pairs:

``` text
p=3:
(5,15) (6,18) (7,21)

p=9:
(5,45) (6,54) (7,63)
```

## F. Pseudocode

``` text
READ k,l1,r1,l2,r2

answer=0
p=1

WHILE p<=r2:

    L=MAX(l1, CEIL(l2/p))
    R=MIN(r1, FLOOR(r2/p))

    IF L<=R:
        answer += R-L+1

    IF p > r2/k:
        BREAK

    p=p*k

PRINT answer
```

## G. Complexity

``` text
Time:  O(log_k(r2))
Space: O(1)
```

## H. Variant Lesson

``` text
RATIO:
y/x=k^n

SUBSTITUTE:
p=k^n

REMOVE DIVISION:
y=p*x

USE BOUNDS:
l2<=p*x<=r2

ISOLATE x:
ceil(l2/p)<=x<=floor(r2/p)

INTERSECT:
with [l1,r1]

ALGORITHM:
enumerate powers + count interval
```

------------------------------------------------------------------------

[↑ Back to Table of Contents](#table-of-contents)

# 20. Compare the Three Ratio Variants

  ------------------------------------------------------------------------------------
  Problem   Ratio Form  Extra        Transformation   Observation      Algorithm
                        Constraint                                     
  --------- ----------- ------------ ---------------- ---------------- ---------------
  CF 337B   `a/b` vs    Preserve     `a*d` vs `b*c`   Cross            Arithmetic +
            `c/d`       aspect ratio                  multiplication   GCD
                                                      removes division 

  CF 573A   Ratio       Only ×2 and  Strip factors    Remaining core   Normalization
            differs by  ×3 allowed   `2,3`            is invariant     
            `2^p3^q`                                                   

  CF 2044E  `y/x=k^n`   Both values  `y=p*x`          Ratio becomes    Powers +
                        bounded                       interval         interval
                                                      constraint       intersection
  ------------------------------------------------------------------------------------

------------------------------------------------------------------------

# 21. Ratio Constraint --- Recognition Map

``` text
                          RATIO CONSTRAINT
                                 |
        +------------------------+-----------------------+
        |                        |                       |
   EQUAL RATIOS              FIXED SCALE             POWER RATIO
   x/y=a/b                  y=k*x                  y/x=k^t
        |                        |                       |
 cross multiply             substitution              p=k^t
        |                        |                       |
 b*x=a*y                  apply bounds               y=p*x
                                                         |
                                                interval intersection


NORMALIZATION
x:y
 |
g=gcd(x,y)
 |
(x/g):(y/g)
 |
canonical ratio


ALLOWED MULTIPLIERS
×2, ×3, ...
 |
strip allowed factors
 |
compare invariant cores
```

------------------------------------------------------------------------

# 22. Instant Recognition Drill

  English Phrase                    Mathematical Translation
  --------------------------------- --------------------------
  "same ratio"                      `x/y=a/b`
  "same proportion"                 `b*x=a*y`
  "one is k times the other"        `y=k*x`
  "ratio is a power of k"           `y/x=k^t`
  "scale both dimensions equally"   `(x,y)=(a*t,b*t)`
  "compare two fractions"           cross multiply
  "reduce the ratio"                divide both by GCD

### Mental drills

``` text
1. x:y=2:5 and x=8 → y?
2. Is 6:9 equal to 10:15?
3. Normalize 42:56.
4. y/x=3^t → remove division.
5. y=4x and 20<=y<=40 → range for x?
```

### Answers

``` text
1. y=20

2.
6*15=90
9*10=90
YES

3.
gcd(42,56)=14
42/14 : 56/14
=3:4

4.
y=x*3^t

5.
20<=4x<=40
5<=x<=10
```

------------------------------------------------------------------------

# 23. Mathematical Form to Memorize

``` text
FORM:
Ratio Constraint

EQUAL:
x/y=a/b
→ b*x=a*y

NORMALIZE:
g=gcd(x,y)
→ (x/g,y/g)

SCALE:
x:y=a:b
→ x=a*t
→ y=b*t

FIXED MULTIPLIER:
y/x=k
→ y=k*x

POWER RATIO:
y/x=k^t
→ y=x*k^t

BOUNDED:
y=p*x
Ly<=y<=Ry
→ Ly<=p*x<=Ry
→ ceil(Ly/p)<=x<=floor(Ry/p)

COMPARE:
a/b ? c/d
→ a*d ? c*b
```

Core mental model:

``` text
RATIO
  ↓
REMOVE DIVISION
  ↓
CROSS MULTIPLY / SCALE / NORMALIZE
  ↓
INTEGER RELATION
```

------------------------------------------------------------------------

# 24. When NOT to Divide

Do not blindly manipulate:

``` text
x/y <= k
```

### If `y>0`

``` text
x/y <= k
→ x <= k*y
```

### If `y<0`

The inequality reverses:

``` text
x/y <= k
→ x >= k*y
```

### If `y=0`

The ratio is undefined.

Contest rule:

``` text
BEFORE MULTIPLYING AN INEQUALITY BY A DENOMINATOR:
CHECK ITS SIGN.
```

------------------------------------------------------------------------

# 25. Pattern Recognition

### SIGNAL

``` text
ratio
fraction
proportion
same shape
scale
k times
multiple
power ratio
```

### MATH

``` text
x/y=a/b
y=k*x
y=x*k^t
```

### THINK

``` text
Can I remove division?
Can I cross multiply?
Can I normalize using GCD?
Is there a common scale t?
If one variable is fixed, is the other forced?
Can bounds become an interval after substitution?
Are only certain multiplicative factors allowed?
```

### TYPICAL SOLUTIONS

``` text
cross multiplication
GCD normalization
interval intersection
power enumeration
factor stripping
number theory
binary search / two pointers for monotonic ratio ordering
```

------------------------------------------------------------------------

# 26. Contest Mental Compression

``` text
READ ENGLISH
     ↓
REMOVE STORY NOUNS
     ↓
identify numerator / denominator quantities
     ↓
WRITE RATIO
     ↓
x/y=a/b
or
y/x=k^t
     ↓
REMOVE DIVISION
     ↓
b*x=a*y
or
y=x*k^t
     ↓
ADD BOUNDS / ALLOWED FACTORS
     ↓
NORMALIZE / INTERSECT / COUNT
     ↓
ALGORITHM
```

Ultra-compressed:

``` text
"ratio" → fraction → remove division → normalize/scale → apply bounds → algorithm
```

------------------------------------------------------------------------

# 27. Final One-Line Takeaway

**When a ratio appears, remove division first: cross-multiply for
equality, normalize with GCD for canonical form, or rewrite it as a
scale equation so bounds become integer constraints.**
