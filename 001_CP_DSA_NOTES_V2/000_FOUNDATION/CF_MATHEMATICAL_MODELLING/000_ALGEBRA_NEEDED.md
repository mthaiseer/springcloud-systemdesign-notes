# CF Algebra Toolkit --- Mathematical Decoding for Div. 2

> Goal: decode story → variables → algebra → hidden constraint →
> algorithmic observation.\
> Revision format: **Core Idea → Simple Example → Real-World Mapping →
> How It Appears in CF → Dry Run → Recognition → 10-Second Revision**.

## Table of Contents

1.  [Basic Equation Solving](#01-basic-equation-solving)
2.  [Moving Terms / Rearrangement](#02-moving-terms--rearrangement)
3.  [Substitution](#03-substitution)
4.  [Sum ↔ Missing Value](#04-sum--missing-value)
5.  [Difference Transformation](#05-difference-transformation)
6.  [Absolute-Value Expansion](#06-absolute-value-expansion)
7.  [Inequality Manipulation](#07-inequality-manipulation)
8.  [Interval Intersection](#08-interval-intersection)
9.  [Ratio / Cross Multiplication](#09-ratio--cross-multiplication)
10. [Linear Equations](#10-linear-equations)
11. [Operation Equation](#11-operation-equation-start--kdelta--target)
12. [Integer / Divisibility
    Constraints](#12-integer--divisibility-constraints)
13. [Modulo Algebra](#13-modulo-algebra)
14. [Parity Algebra](#14-parity-algebra)
15. [GCD / LCM Algebra](#15-gcd--lcm-algebra)
16. [Floor / Ceil Transformations](#16-floor--ceil-transformations)
17. [Min / Max Transformations](#17-min--max-transformations)
18. [Pair-Equation Rearrangement](#18-pair-equation-rearrangement)
19. [Common-Expression / Key
    Extraction](#19-common-expression--key-extraction)
20. [Factorization](#20-factorization)
21. [Product Constraints](#21-product-constraints)
22. [Arithmetic Progression](#22-arithmetic-progression)
23. [Geometric Growth / Powers](#23-geometric-growth--powers)
24. [Counting Identities](#24-counting-identities)
25. [Contribution Algebra](#25-contribution-algebra)
26. [Prefix Algebra](#26-prefix-algebra)
27. [XOR Identities](#27-xor-identities)
28. [Coordinate / Distance Algebra](#28-coordinate--distance-algebra)
29. [Invariants](#29-invariants)
30. [Monotonic Inequalities](#30-monotonic-inequalities)
31. [Remainder-Class Pairing](#31-remainder-class-pairing)
32. [Bounds / Feasibility](#32-bounds--feasibility)
33. [Change of Variables /
    Normalization](#33-change-of-variables--normalization)
34. [Telescoping / Cancellation](#34-telescoping--cancellation)
35. [Useful Algebraic Identities](#35-useful-algebraic-identities)

------------------------------------------------------------------------

## 01. Basic Equation Solving

### Core Idea

Isolate the unknown.

``` text
ax+b=c
→ ax=c-b
→ x=(c-b)/a
```

### Simple Example

``` text
3x+5=20 → 3x=15 → x=5
```

### Real-World Mapping

Three identical tickets plus a 5-lei fee cost 20 lei.

``` text
3x+5=20 → x=5
```

### How It Appears in CF

A target score is reached after `k` equal gains.

``` text
start + k*d = target
```

**Dry Run**

``` text
4+3k=19 → 3k=15 → k=5
```

### Recognition

``` text
unknown inside equation → isolate it
```

### 10-Second Revision

``` text
undo +/− first → undo ×/÷ → check integer/domain
```

------------------------------------------------------------------------

## 02. Moving Terms / Rearrangement

### Core Idea

Move related terms to expose a useful structure.

``` text
a+b=c+d
→ a-c=d-b
```

### Simple Example

``` text
x+7=20 → x=20-7=13
```

### Real-World Mapping

Balance scale: moving 7 kg from one side means subtracting 7 from both
sides.

### How It Appears in CF

``` text
a[j]-a[i]=j-i
→ a[j]-j=a[i]-i
```

**Dry Run**

``` text
i=2, a[i]=5 → a[i]-i=3
j=6, a[j]=9 → a[j]-j=3
→ pair satisfies relation ✓
```

### Recognition

``` text
terms mix i/j or values/indices → group same-index terms
```

### 10-Second Revision

``` text
rearrange until both sides have the same form
```

------------------------------------------------------------------------

## 03. Substitution

### Core Idea

Replace a variable/expression with an equal expression to reduce
unknowns.

``` text
x=expression
→ replace x elsewhere
```

### Simple Example

``` text
x+y=10, y=3
→ x+3=10
→ x=7
```

### Real-World Mapping

Burger + drink = 30; burger = 2 × drink.

``` text
B+D=30
B=2D
→ 2D+D=30
→ D=10, B=20
```

### How It Appears in CF

**Form 1 --- Sum + Difference**

``` text
x+y=S
x-y=D
→ x=(S+D)/2
→ y=(S-D)/2
```

**Dry Run**

``` text
S=10,D=4 → x=7,y=3 → 7+3=10, 7-3=4 ✓
```

**Form 2 --- Pair Sum**

``` text
a[i]+a[j]=S
→ a[j]=S-a[i]
→ need=S-a[i]
```

**Dry Run**

``` text
a=[2,7,3,8], S=10
2 → need 8 ✓
7 → need 3 ✓
```

**Form 3 --- Operation**

``` text
start+k*d=target
→ k=(target-start)/d
```

**Dry Run**

``` text
5+3k=17 → k=4 ✓
5+3k=16 → k=11/3 → impossible
```

### Recognition

``` text
isolate → substitute → reduce variables → inspect new constraint
```

### 10-Second Revision

``` text
pair sum → need=S-a[i]
operation → k=(T-start)/d
watch integer/parity/divisibility
```

------------------------------------------------------------------------

## 04. Sum ↔ Missing Value

### Core Idea

If total and all-but-one parts are known, derive the missing part.

``` text
total = known + missing
→ missing = total-known
```

### Simple Example

``` text
x+y=20, x=8 → y=12
```

### Real-World Mapping

100 lei budget, 65 spent → 35 remaining.

### How It Appears in CF

``` text
missing = targetSum - currentSum
```

**Dry Run**

``` text
target=15, array=[2,4,3]
current=9 → missing=15-9=6
```

### Recognition

``` text
total / remaining / missing / complement
```

### 10-Second Revision

``` text
missing = total - everything already known
```

------------------------------------------------------------------------

## 05. Difference Transformation

### Core Idea

Translate "more/less/gap" into subtraction.

``` text
a is d more than b
→ a-b=d
→ a=b+d
```

### Simple Example

``` text
a-b=5, b=8 → a=13
```

### Real-World Mapping

One person is 5 years older than another.

### How It Appears in CF

``` text
target-current = required change
```

**Dry Run**

``` text
current=12,target=20 → diff=8
operation adds 2 → 8/2=4 operations
```

### Recognition

``` text
gap / ahead / behind / increase / decrease
```

### 10-Second Revision

``` text
difference usually becomes target-current or a-b
```

------------------------------------------------------------------------

## 06. Absolute-Value Expansion

### Core Idea

Absolute difference is distance on a line.

``` text
|x-a|≤k
↔ -k≤x-a≤k
↔ a-k≤x≤a+k
```

### Simple Example

``` text
|x-10|≤3 → 7≤x≤13
```

### Real-World Mapping

Arrival within 15 minutes of 10:00 → 09:45--10:15.

### How It Appears in CF

``` text
|A[i]-B[i]|≤k
→ A[i]-k≤B[i]≤A[i]+k
```

**Dry Run**

``` text
A[i]=8,k=2 → B[i]∈[6,10]
B[i]=9 ✓, B[i]=12 ✗
```

### Recognition

``` text
distance / differs by at most / within k
```

### 10-Second Revision

``` text
|x-a|≤k → interval [a-k,a+k]
```

------------------------------------------------------------------------

## 07. Inequality Manipulation

### Core Idea

Manipulate inequalities like equations, but multiplying/dividing by a
negative flips the sign.

``` text
2x+3≤11 → 2x≤8 → x≤4
-2x≤8 → x≥-4
```

### Real-World Mapping

Budget cannot exceed 100 lei.

### How It Appears in CF

``` text
start+k*d ≤ limit
→ k*d ≤ limit-start
→ k ≤ (limit-start)/d   (d>0)
```

**Dry Run**

``` text
5+3k≤20 → 3k≤15 → k≤5
```

### Recognition

``` text
at most / at least / no more / no less
```

### 10-Second Revision

``` text
negative × or ÷ → flip < ↔ >
```

------------------------------------------------------------------------

## 08. Interval Intersection

### Core Idea

All constraints must hold simultaneously.

``` text
x∈[L1,R1], x∈[L2,R2]
→ x∈[max(L1,L2), min(R1,R2)]
```

Feasible iff:

``` text
maxL ≤ minR
```

### Real-World Mapping

Shop open 09--18; you are free 14--20 → common window 14--18.

### How It Appears in CF

Each condition gives a valid range.

**Dry Run**

``` text
[2,8] ∩ [5,10]
→ [max(2,5),min(8,10)]
→ [5,8]
```

### Recognition

``` text
several lower/upper bounds on same variable
```

### 10-Second Revision

``` text
L=max(all L), R=min(all R), valid iff L≤R
```

------------------------------------------------------------------------

## 09. Ratio / Cross Multiplication

### Core Idea

Avoid floating point by cross multiplying.

``` text
a/b = p/q
→ aq = bp
```

### Simple Example

``` text
x/y=2/3 → 3x=2y
```

### Real-World Mapping

Rice:water = 1:2; 3 cups rice → 6 cups water.

### How It Appears in CF

Check whether two pairs have the same ratio.

**Dry Run**

``` text
a=4,b=6,p=2,q=3
4/6 = 2/3
→ 4*3 = 6*2
→ 12=12 ✓
```

### Recognition

``` text
proportion / same rate / ratio
```

### 10-Second Revision

``` text
a/b=p/q → aq=bp; use wide integer type if products can grow
```

------------------------------------------------------------------------

## 10. Linear Equations

### Core Idea

Variables appear only to power 1.

``` text
ax+by=c
```

For two equations, eliminate or substitute.

### Simple Example

``` text
x+y=10
x-y=2
→ 2x=12 → x=6,y=4
```

### Real-World Mapping

Two ticket types with known total count and total cost.

### How It Appears in CF

Find non-negative integer counts `x,y`.

``` text
ax+by=S
```

**Dry Run**

``` text
2x+3y=7
try y=1 → 2x=4 → x=2
→ (x,y)=(2,1)
```

### Recognition

``` text
two item types / counts / total cost / total units
```

### 10-Second Revision

``` text
solve algebraically, then enforce integer + non-negative constraints
```

------------------------------------------------------------------------

## 11. Operation Equation: start + kΔ = target

### Core Idea

Model repeated identical change instead of simulating.

``` text
final = start + k*delta
```

### Real-World Mapping

Account starts at 20 lei and gains 5 lei/day.

### How It Appears in CF

``` text
start+kΔ=target
→ k=(target-start)/Δ
```

**Dry Run**

``` text
start=4, Δ=3, target=19
→ k=(19-4)/3=5 ✓
```

### Recognition

``` text
same change every operation
```

### 10-Second Revision

``` text
one-operation delta → multiply by k → solve for k
```

------------------------------------------------------------------------

## 12. Integer / Divisibility Constraints

### Core Idea

Algebraic solutions must respect discrete objects.

``` text
x=A/B
```

For integer `x`:

``` text
A%B==0
```

### Real-World Mapping

11 people cannot be split equally into 2 whole-person groups.

### How It Appears in CF

``` text
k=(target-start)/d
```

**Dry Run**

``` text
target-start=12,d=3 → 12%3=0 → k=4 ✓
11%3≠0 → impossible
```

### Recognition

``` text
number of operations/items/groups must be integer
```

### 10-Second Revision

``` text
division in derivation → immediately ask “must this divide exactly?”
```

------------------------------------------------------------------------

## 13. Modulo Algebra

### Core Idea

Modulo keeps remainder classes.

``` text
(a+b)%m = ((a%m)+(b%m))%m
(a*b)%m = ((a%m)*(b%m))%m
```

Also:

``` text
a≡b (mod m) ↔ m | (a-b)
```

### Real-World Mapping

Clock arithmetic: 10 + 5 hours → 3 on a 12-hour clock.

### How It Appears in CF

Pair sum divisible by `m`:

``` text
(a+b)%m=0
```

**Dry Run**

``` text
m=5, a=7 → a%5=2
need remainder = 3
8%5=3
7+8=15 → divisible ✓
```

### Recognition

``` text
cyclic / divisible / same remainder / repeat every m
```

### 10-Second Revision

``` text
replace huge values by remainder classes when only modulo matters
```

------------------------------------------------------------------------

## 14. Parity Algebra

### Core Idea

Parity is modulo 2.

``` text
E+E=E
O+O=E
E+O=O
```

Equivalent:

``` text
(a+b)%2=0 → same parity
(a+b)%2=1 → different parity
```

### Real-World Mapping

Alternating black/white squares on a checkerboard.

### How It Appears in CF

Can repeated `+2` reach target?

**Dry Run**

``` text
start=3 → 5 → 7 → 9
all odd
target=8 → impossible
```

### Recognition

``` text
odd/even or operations changing by even amounts
```

### 10-Second Revision

``` text
even delta preserves parity; odd delta flips parity
```

------------------------------------------------------------------------

## 15. GCD / LCM Algebra

### Core Idea

GCD = largest common unit; LCM = first common multiple.

``` text
gcd(a,b)
lcm(a,b)=a/gcd(a,b)*b
```

### Real-World Mapping

12 m and 18 m ropes → largest equal piece = 6 m.\
Events every 6 and 8 minutes → meet again after 24.

### How It Appears in CF

All differences must be multiples of one step.

``` text
g = gcd(|a2-a1|, |a3-a1|, ...)
```

**Dry Run**

``` text
a=[5,11,17]
diffs=[6,12]
gcd=6
→ common step 6
```

### Recognition

``` text
common step → GCD
first simultaneous cycle → LCM
```

### 10-Second Revision

``` text
differences + common movement often signal GCD
```

------------------------------------------------------------------------

## 16. Floor / Ceil Transformations

### Core Idea

Discrete groups often require floor or ceiling.

For positive integers:

``` text
ceil(a/b) = (a+b-1)/b
floor(a/b) = a/b   // integer division
```

### Real-World Mapping

23 people, 10 seats/bus → 3 buses.

### How It Appears in CF

Minimum operations when one operation handles at most `k`.

**Dry Run**

``` text
need=23,k=10
ceil(23/10)
=(23+10-1)/10
=32/10
=3
```

### Recognition

``` text
minimum groups/batches/operations to cover amount
```

### 10-Second Revision

``` text
“how many batches needed?” → ceil
```

------------------------------------------------------------------------

## 17. Min / Max Transformations

### Core Idea

Rewrite optimization using extremes or known optimal points.

``` text
max pair difference = max(a)-min(a)
min Σ|a[i]-x| → median
```

### Real-World Mapping

Meeting location minimizing total walking distance → median location.

### How It Appears in CF

Maximum possible difference among all pairs.

**Dry Run**

``` text
a=[8,2,10,5]
min=2,max=10
max difference=10-2=8
```

### Recognition

``` text
maximum gap → extremes
absolute-distance sum → median
```

### 10-Second Revision

``` text
before enumerating pairs, ask whether only min/max/median matters
```

------------------------------------------------------------------------

## 18. Pair-Equation Rearrangement

### Core Idea

Turn a relation between `i,j` into "what partner do I need?"

``` text
f(a[i],a[j])=C
→ isolate a[j]
```

### Real-World Mapping

If two payments must total 100, payment 35 needs partner 65.

### How It Appears in CF

``` text
a[i]+a[j]=S
→ a[j]=S-a[i]
```

**Dry Run**

``` text
S=10, current=4
→ need=6
freq[6]=3
→ current can pair with 3 previous 6s
```

### Recognition

``` text
count pairs satisfying equation
```

### 10-Second Revision

``` text
pair condition → isolate partner → lookup/count partner
```

------------------------------------------------------------------------

## 19. Common-Expression / Key Extraction

### Core Idea

Rearrange until both indices produce the same expression.

``` text
F(a[i],i)=F(a[j],j)
→ key[t]=F(a[t],t)
```

### Real-World Mapping

People belong to the same group if they produce the same derived ID.

### How It Appears in CF

``` text
a[j]-a[i]=j-i
→ a[j]-j=a[i]-i
→ key[i]=a[i]-i
```

**Dry Run**

``` text
a=[4,6,5] using 1-based i
keys=[3,4,2]
→ no equal keys → no valid pair
```

### Recognition

``` text
pair equation mixes value + index
```

### 10-Second Revision

``` text
rearrange → same expression both sides → frequency of key
```

------------------------------------------------------------------------

## 20. Factorization

### Core Idea

Turn sums/differences into products to expose divisibility or
candidates.

``` text
a²-b²=(a-b)(a+b)
xy+xz=x(y+z)
```

### Real-World Mapping

Area `xy+xz` = common width `x` × combined length `(y+z)`.

### How It Appears in CF

``` text
x²-y²=N
→ (x-y)(x+y)=N
```

**Dry Run**

``` text
N=15
factor pair 3*5
x-y=3
x+y=5
→ 2x=8 → x=4
→ y=1
→ 16-1=15 ✓
```

### Recognition

``` text
polynomial-looking expression / divisors / product target
```

### 10-Second Revision

``` text
factor before brute forcing variables
```

------------------------------------------------------------------------

## 21. Product Constraints

### Core Idea

A product target converts variables into divisor relationships.

``` text
xy=P
→ x divides P
→ y=P/x
```

### Real-World Mapping

Rectangle area 24 → integer sides are divisor pairs.

### How It Appears in CF

Find integer pair with product `P`.

**Dry Run**

``` text
P=24
x=6 → y=24/6=4
→ 6*4=24
```

Prime-exponent form:

``` text
P = ∏ p^e
```

### Recognition

``` text
product fixed → divisors / prime factorization
```

### 10-Second Revision

``` text
xy=P → enumerate divisors, not arbitrary x,y
```

------------------------------------------------------------------------

## 22. Arithmetic Progression

### Core Idea

Constant additive difference.

``` text
a_n = a_1+(n-1)d
S_n = n(a_1+a_n)/2
```

### Real-World Mapping

Savings increase by 5 lei each day: 10,15,20,...

### How It Appears in CF

Sum `1+2+...+n`.

**Dry Run**

``` text
n=5
S=5*(1+5)/2
=15
```

Another form:

``` text
k+(k+1)+...+(k+n-1)
```

### Recognition

``` text
consecutive values / constant difference
```

### 10-Second Revision

``` text
constant +d → AP; avoid O(n) summation when formula suffices
```

------------------------------------------------------------------------

## 23. Geometric Growth / Powers

### Core Idea

Constant multiplicative growth.

``` text
a, ar, ar², ...
a_n=a_1*r^(n-1)
```

### Real-World Mapping

Amount doubles each round: 1,2,4,8,16,...

### How It Appears in CF

Repeated doubling until reaching target.

**Dry Run**

``` text
start=3,target=20
3→6→12→24
3 doublings
```

Algebraically:

``` text
3*2^k ≥ 20
```

### Recognition

``` text
double / halve / multiply each operation
```

### 10-Second Revision

``` text
add repeatedly → linear; multiply repeatedly → exponential
```

------------------------------------------------------------------------

## 24. Counting Identities

### Core Idea

Replace enumeration with formulas.

``` text
pairs = C(n,2)=n(n-1)/2
ordered pairs = n(n-1)
```

Useful:

``` text
1+2+...+n=n(n+1)/2
```

### Real-World Mapping

5 people shake hands once each → `C(5,2)=10`.

### How It Appears in CF

Equal-value pairs:

``` text
answer += C(freq[x],2)
```

**Dry Run**

``` text
freq[x]=4
pairs=4*3/2=6
```

### Recognition

``` text
all pairs / choose 2 / frequencies
```

### 10-Second Revision

``` text
unordered pair count → n(n-1)/2
```

------------------------------------------------------------------------

## 25. Contribution Algebra

### Core Idea

Reverse the viewpoint: count how often one element contributes.

### Real-World Mapping

Instead of listing every team containing one player, count choices
before × choices after.

### How It Appears in CF

For 1-based `i`, subarrays containing `a[i]`:

``` text
left choices  = i
right choices = n-i+1

count = i(n-i+1)
```

Contribution to sum of all subarray sums:

``` text
a[i]*i*(n-i+1)
```

**Dry Run**

``` text
n=4,i=2,a[i]=5
count=2*(4-2+1)=6
contribution=5*6=30
```

### Recognition

``` text
sum over all pairs/subarrays → ask “how often does one item appear?”
```

### 10-Second Revision

``` text
total answer = Σ(value × number of structures containing it)
```

------------------------------------------------------------------------

## 26. Prefix Algebra

### Core Idea

Store cumulative values so ranges become subtraction.

Using 0-based half-open prefix:

``` text
pref[0]=0
pref[i+1]=pref[i]+a[i]

sum(L,R inclusive)=pref[R+1]-pref[L]
```

### Real-World Mapping

Spending through June minus spending through March = April--June
spending.

### How It Appears in CF

Many range-sum queries.

**Dry Run**

``` text
a=[2,5,3,4]
pref=[0,2,7,10,14]

sum(1,3)=pref[4]-pref[1]
=14-2=12
```

### Recognition

``` text
many range sums / prefix-vs-suffix
```

### 10-Second Revision

``` text
range = prefix(right) - prefix(before left)
```

------------------------------------------------------------------------

## 27. XOR Identities

### Core Idea

XOR cancels equal values.

``` text
x^x=0
x^0=x
x^y^x=y
```

Prefix XOR:

``` text
px[0]=0
px[i+1]=px[i]^a[i]

xor(L,R)=px[R+1]^px[L]
```

### Real-World Mapping

Think of toggles: applying the same toggle twice cancels it.

### How It Appears in CF

All values appear twice except one.

**Dry Run**

``` text
[4,7,4,2,2]
4^7^4^2^2
=(4^4)^(2^2)^7
=0^0^7
=7
```

### Recognition

``` text
pairs cancel / toggles / range XOR
```

### 10-Second Revision

``` text
same XOR same = 0 → cancellation is the key
```

------------------------------------------------------------------------

## 28. Coordinate / Distance Algebra

### Core Idea

Translate movement into distance.

Line:

``` text
|x1-x2|
```

Grid with 4-direction moves:

``` text
|x1-x2|+|y1-y2|
```

### Real-World Mapping

City blocks: horizontal distance + vertical distance.

### How It Appears in CF

Minimum 4-direction moves.

**Dry Run**

``` text
(2,3) → (7,1)
distance=|7-2|+|1-3|
=5+2=7
```

### Recognition

``` text
positions / movement / cells / coordinates
```

### 10-Second Revision

``` text
line → absolute difference
4-neighbor grid → Manhattan distance
```

------------------------------------------------------------------------

## 29. Invariants

### Core Idea

Find a property that operations cannot change.

### Real-World Mapping

Moving money between two wallets changes distribution but not total
money.

### How It Appears in CF

Operation:

``` text
a[i]--
a[j]++
```

Sum change:

``` text
-1+1=0
```

So:

``` text
Σa[i] = invariant
```

**Dry Run**

``` text
[2,5,1], sum=8
move 1 from second to first
→ [3,4,1], sum=8
```

Another:

``` text
x += 2 → parity invariant
```

### Recognition

``` text
transformation/reachability problem → ask what cannot change
```

### 10-Second Revision

``` text
model one operation → compute change in sum/parity/mod/GCD/XOR/etc.
```

------------------------------------------------------------------------

## 30. Monotonic Inequalities

### Core Idea

A feasibility condition may switch only once.

``` text
NO NO NO YES YES YES
         ↑ boundary
```

### Real-World Mapping

If a 50-seat bus is sufficient, a 60-seat bus is also sufficient.

### How It Appears in CF

Find minimum capacity `x` such that `can(x)` is true.

**Dry Run**

``` text
x:      1 2 3 4 5 6
can(x): N N N Y Y Y

minimum feasible x=4
```

### Recognition

``` text
minimum X / maximum X + feasibility test
```

### 10-Second Revision

``` text
if x works, ask whether every larger (or smaller) x also works
```

------------------------------------------------------------------------

# Extra Forms Worth Adding

These five fill common algebra gaps in Div. 2 decoding.

## 31. Remainder-Class Pairing

### Core Idea

For divisibility, values can often be replaced by remainders.

``` text
(a+b)%m=0
```

If:

``` text
r=a%m
```

needed partner remainder:

``` text
(m-r)%m
```

### Real-World Mapping

On a 10-position cycle, remainder 3 needs remainder 7 to complete 10.

### How It Appears in CF

Count pairs whose sum is divisible by `m`.

**Dry Run**

``` text
m=5
a=7 → r=2
need=(5-2)%5=3
b=8 → 8%5=3
→ pair valid
```

### Recognition

``` text
pair + divisible by m → pair remainder classes
```

### 10-Second Revision

``` text
remainder r pairs with (m-r)%m
```

------------------------------------------------------------------------

## 32. Bounds / Feasibility

### Core Idea

Derive the minimum and maximum possible value before searching.

``` text
L ≤ x ≤ R
```

### Real-World Mapping

A bag must weigh at least 5 kg and at most 20 kg.

### How It Appears in CF

If `x` items each contribute between `lo` and `hi`:

``` text
x*lo ≤ total ≤ x*hi
```

**Dry Run**

``` text
x=4, each in [2,5]
possible total range=[8,20]
target=17 ✓
target=23 ✗
```

### Recognition

``` text
“is it possible?” → derive min/max achievable bounds
```

### 10-Second Revision

``` text
before constructing, check target lies inside achievable range
```

------------------------------------------------------------------------

## 33. Change of Variables / Normalization

### Core Idea

Subtract a common baseline or define a simpler variable.

``` text
b[i]=a[i]-c
```

### Real-World Mapping

Compare salaries by "amount above 3000" instead of absolute salary.

### How It Appears in CF

Make all elements equal to `x`.

``` text
difference[i]=a[i]-x
```

**Dry Run**

``` text
a=[7,10,8], baseline=7
b=[0,3,1]
```

Now only excess amounts matter.

### Recognition

``` text
large common offset / compare relative rather than absolute values
```

### 10-Second Revision

``` text
remove irrelevant common baseline
```

------------------------------------------------------------------------

## 34. Telescoping / Cancellation

### Core Idea

Adjacent differences cancel when summed.

``` text
(a2-a1)+(a3-a2)+...+(an-a[n-1])
= an-a1
```

### Real-World Mapping

Daily balance changes summed over a month equal final balance minus
initial balance.

### How It Appears in CF

Sum of consecutive differences.

**Dry Run**

``` text
a=[2,5,9,12]

(5-2)+(9-5)+(12-9)
=3+4+3
=10

12-2=10
```

### Recognition

``` text
chain of neighboring differences
```

### 10-Second Revision

``` text
middle +x and -x terms cancel → endpoints remain
```

------------------------------------------------------------------------

## 35. Useful Algebraic Identities

### Core Idea

Memorize a small identity set that collapses expressions.

``` text
(a+b)² = a²+2ab+b²
(a-b)² = a²-2ab+b²
a²-b² = (a-b)(a+b)

(a+b)²-(a²+b²)=2ab
```

Important pair-product identity:

``` text
Σ(i<j) a[i]a[j]
=
((Σa[i])² - Σa[i]²)/2
```

### Real-World Mapping

Total pair interactions can be recovered from the square of the total
instead of enumerating every pair.

### How It Appears in CF

Compute sum of products over every unordered pair.

**Dry Run**

``` text
a=[1,2,3]

direct:
1*2 + 1*3 + 2*3
=2+3+6
=11

formula:
sum=6
sumSquares=1+4+9=14

(6²-14)/2
=(36-14)/2
=11
```

### Recognition

``` text
all pair products / squares / difference of squares
```

### 10-Second Revision

``` text
expand/factor identities can turn O(n²) pair algebra into O(n)
```

------------------------------------------------------------------------

# Final Contest Decode Card

``` text
STORY
  ↓
REMOVE NOUNS
  ↓
DEFINE VARIABLES
  ↓
WRITE EXACT GOAL
  ↓
MODEL ONE OPERATION / RELATION
  ↓
WRITE EQUATION OR INEQUALITY
  ↓
REARRANGE / SUBSTITUTE / FACTOR
  ↓
CHECK:
integer?
parity?
divisibility?
modulo?
bounds?
invariant?
monotonicity?
  ↓
CHANGE VIEW:
pair → needed partner / key / frequency
range → prefix
all structures → contribution
cyclic → remainder
movement → distance
repeated operation → start+kΔ
  ↓
ALGORITHM
```

## High-Priority Revision Order

``` text
Tier 1 — must become automatic
1  Basic equations
2  Rearrangement
3  Substitution
4  Sum/missing
5  Difference
6  Absolute value
7  Inequalities
8  Intervals
11 Operation equation
12 Divisibility
13 Modulo
14 Parity
16 Floor/Ceil
18 Pair rearrangement
19 Key extraction

Tier 2 — frequent Div.2 B/C
9  Ratio
15 GCD/LCM
17 Min/Max
20 Factorization
21 Product
24 Counting
25 Contribution
26 Prefix
27 XOR
29 Invariants
30 Monotonicity
31 Remainder pairing
32 Bounds

Tier 3 — important supporting forms
10 Linear equations
22 AP
23 Powers
28 Distance
33 Normalization
34 Telescoping
35 Identities
```
