# Part 20 — Diophantine Modeling

> **Goal:** recognize when a Codeforces story is really asking whether an integer equation has a solution, transform the story into `ax + by = c` or a related form, and solve it using divisibility, gcd, modular arithmetic, bounds, and construction.
>
> **Core workflow:** `Story → Count variables → Integer equation → gcd/divisibility → bounds/sign constraints → construct or reject`
>
> **Recognition question:** **Can I express the required total as a linear combination of fixed integer contributions, where the unknown counts themselves must be integers?**

## Table of Contents

- [20.0 Diophantine Mental Model](#200-diophantine-mental-model)
- [20.1 Story to Integer Equation](#201-story-to-integer-equation)
- [20.2 Linear Diophantine Equation](#202-linear-diophantine-equation)
- [20.3 The gcd Existence Criterion](#203-the-gcd-existence-criterion)
- [20.4 Why gcd Controls Reachability](#204-why-gcd-controls-reachability)
- [20.5 Bézout Identity](#205-bézout-identity)
- [20.6 Extended Euclidean Algorithm](#206-extended-euclidean-algorithm)
- [20.7 Constructing One Solution](#207-constructing-one-solution)
- [20.8 Generating All Integer Solutions](#208-generating-all-integer-solutions)
- [20.9 Non-Negative Solution Constraints](#209-non-negative-solution-constraints)
- [20.10 Positive Solution Constraints](#2010-positive-solution-constraints)
- [20.11 Modular Equation View](#2011-modular-equation-view)
- [20.12 Modular Inverse Form](#2012-modular-inverse-form)
- [20.13 Fixed Total Count Transformation](#2013-fixed-total-count-transformation)
- [20.14 Two Package Sizes and Exact Total](#2014-two-package-sizes-and-exact-total)
- [20.15 Coins and Purchase Modeling](#2015-coins-and-purchase-modeling)
- [20.16 Movement and Reachability](#2016-movement-and-reachability)
- [20.17 Parity as a Tiny Diophantine Constraint](#2017-parity-as-a-tiny-diophantine-constraint)
- [20.18 Difference and Sum Equations](#2018-difference-and-sum-equations)
- [20.19 Bounded Diophantine Equations](#2019-bounded-diophantine-equations)
- [20.20 Counting Valid Solutions](#2020-counting-valid-solutions)
- [20.21 Three or More Variables](#2021-three-or-more-variables)
- [20.22 Frobenius and Large Reachability Intuition](#2022-frobenius-and-large-reachability-intuition)
- [20.23 When gcd Is Not Enough](#2023-when-gcd-is-not-enough)
- [20.24 60-Second Diophantine Discovery Workflow](#2024-60-second-diophantine-discovery-workflow)
- [20.25 Codeforces Recognition Map](#2025-codeforces-recognition-map)
- [20.26 Common Mistakes](#2026-common-mistakes)
- [20.27 Fast Revision Card](#2027-fast-revision-card)

---

## 20.0 Diophantine Mental Model

A **Diophantine equation** is an equation where the unknowns must be integers.

Typical CF form:

```text
a*x + b*y = c

x,y ∈ integers
```

Often the story hides the variables.

### Real-world scenario — boxes of chocolates

A shop sells only:

```text
small box = 6 chocolates
large box = 9 chocolates
```

Can you buy exactly:

```text
30 chocolates?
```

Let:

```text
x = number of 6-boxes
y = number of 9-boxes
```

Then:

```text
6x + 9y = 30
x,y >= 0
```

The story has disappeared.

Now it is pure mathematics.

One solution:

```text
x=2
y=2

6*2 + 9*2
=12+18
=30
```

### Core transformation

```text
STORY
  ↓
"What quantities can I choose?"
  ↓
integer variables x,y,...
  ↓
"What does one choice contribute?"
  ↓
a*x + b*y + ...
  ↓
required total
  ↓
INTEGER EQUATION
```

**Memory hook:** `DIOPHANTINE = COUNT SOMETHING, SO THE UNKNOWN CANNOT BE FRACTIONAL.`

---

## 20.1 Story to Integer Equation

The most important skill is not Extended Euclid.

It is recognizing the hidden equation.

### Example — animal legs

Suppose a farm has:

```text
20 animals
56 legs

chickens -> 2 legs
cows     -> 4 legs
```

Let:

```text
x = chickens
y = cows
```

Two facts become two equations:

```text
x+y=20
2x+4y=56
```

### Eliminate one variable

From:

```text
x=20-y
```

Substitute:

```text
2(20-y)+4y=56

40-2y+4y=56

2y=16

y=8
```

Then:

```text
x=12
```

### Real-world modeling pattern

Words such as:

```text
number of
exactly
total
packs
moves
coins
steps
operations
```

often suggest integer-count variables.

### Contest question

Ask:

```text
"What are the unknown COUNTS?"
```

Then write the equation before thinking about algorithms.

---

## 20.2 Linear Diophantine Equation

Canonical form:

```text
ax + by = c
```

where:

```text
a,b,c are known integers
x,y are unknown integers
```

Example:

```text
6x + 9y = 30
```

### Ordinary algebra vs Diophantine algebra

Over real numbers, infinitely many points lie on:

```text
6x+9y=30
```

For example:

```text
x=0.5
y=3
```

satisfies the equation over reals:

```text
3+27=30
```

But it is invalid if `x` counts boxes.

Diophantine problems require lattice points:

```text
(x,y) ∈ Z²
```

and CF frequently adds:

```text
x>=0
y>=0
```

### Visual model

```text
real line solutions:
------------------------------ continuous

integer solutions:
      •       •       •
      lattice points only
```

The integer restriction is the entire reason gcd and modular arithmetic appear.

---

## 20.3 The gcd Existence Criterion

For:

```text
ax + by = c
```

an integer solution exists **iff**:

```text
gcd(a,b) divides c
```

That is:

```text
c % gcd(a,b) == 0
```

### Example 1 — possible

```text
6x + 9y = 30
```

Compute:

```text
gcd(6,9)=3
30 % 3 = 0
```

Therefore integer solutions exist.

### Example 2 — impossible

```text
6x + 9y = 20
```

Again:

```text
gcd(6,9)=3
```

but:

```text
20 % 3 = 2
```

So no integer solution exists.

### Real-world scenario — package totals

Packages contain:

```text
6 or 9 bottles
```

Any combination gives:

```text
6x+9y
=3(2x+3y)
```

Therefore every reachable total is divisible by `3`.

A total of `20` is not.

Impossible immediately.

---

## 20.4 Why gcd Controls Reachability

Let:

```text
g = gcd(a,b)
```

Then:

```text
a=gA
b=gB
```

So:

```text
ax+by
=
gAx+gBy
=
g(Ax+By)
```

Therefore every possible value of:

```text
ax+by
```

is divisible by `g`.

So if:

```text
g ∤ c
```

the equation is impossible.

### But why is divisibility also sufficient?

Bézout's identity tells us there exist integers:

```text
u,v
```

such that:

```text
au+bv=g
```

If:

```text
c=g*k
```

multiply by `k`:

```text
a(uk)+b(vk)=c
```

So a solution exists.

### Proof shape

```text
g | a
g | b
   ↓
g | (ax+by)

necessary

Bézout:
au+bv=g

if c=gk:
a(uk)+b(vk)=c

sufficient
```

This gives the exact iff condition.

---

## 20.5 Bézout Identity

For integers `a,b`, there exist integers `x,y` such that:

```text
ax+by=gcd(a,b)
```

### Example

```text
a=18
b=30

gcd=6
```

One representation:

```text
18*2 + 30*(-1)
=36-30
=6
```

So:

```text
x=2
y=-1
```

### Real-world interpretation

Suppose operations change a score by:

```text
+18
or
+30
```

If reverse operations are also allowed, then integer combinations can create the gcd-sized fundamental change:

```text
+18 +18 -30 = +6
```

Thus `6` is the smallest fundamental linear-combination unit.

### Important

Bézout solutions may contain negative values.

That proves an **integer** solution exists, but not necessarily a valid non-negative counting solution.

---

## 20.6 Extended Euclidean Algorithm

Euclid computes:

```text
gcd(a,b)
```

Extended Euclid additionally finds:

```text
x,y
```

such that:

```text
ax+by=gcd(a,b)
```

### Example

Find coefficients for:

```text
30 and 18
```

Euclid:

```text
30 = 1*18 + 12
18 = 1*12 + 6
12 = 2*6  + 0
```

Therefore:

```text
gcd=6
```

Back-substitute:

```text
6
=18-12
```

and:

```text
12=30-18
```

so:

```text
6
=18-(30-18)
=2*18-30
```

Therefore:

```text
30*(-1) + 18*(2) = 6
```

### Recursive mental model

```text
extendedGcd(a,b)

solve:
b*x1 + (a%b)*y1 = g

since:
a%b = a - floor(a/b)*b

substitute and recover coefficients
for a and b
```

### CF use

Extended Euclid is useful when you need:

```text
actual x,y
modular inverse
all solutions
bounded solutions
```

For a simple YES/NO existence check, gcd alone may be enough.

---

## 20.7 Constructing One Solution

Suppose Extended Euclid gives:

```text
a*x0 + b*y0 = g
```

where:

```text
g=gcd(a,b)
```

We need:

```text
ax+by=c
```

If:

```text
c % g != 0
```

stop: impossible.

Otherwise multiply by:

```text
k=c/g
```

Then:

```text
x=x0*k
y=y0*k
```

is one solution.

### Example

Solve:

```text
18x+30y=42
```

We know:

```text
18*2 + 30*(-1)=6
```

and:

```text
42/6=7
```

Multiply:

```text
x=14
y=-7
```

Verify:

```text
18*14 + 30*(-7)
=252-210
=42
```

This is an integer solution.

It is not a non-negative solution, so further shifting may be required.

---

## 20.8 Generating All Integer Solutions

Suppose:

```text
ax+by=c
```

has one solution:

```text
(x0,y0)
```

Let:

```text
g=gcd(a,b)
```

Then all integer solutions are:

```text
x = x0 + (b/g)t
y = y0 - (a/g)t
```

for any integer:

```text
t ∈ Z
```

### Why?

Substitute:

```text
a[x0+(b/g)t] + b[y0-(a/g)t]
```

Expand:

```text
ax0 + by0
+ abt/g
- abt/g
```

The extra terms cancel:

```text
=c
```

### Example

For:

```text
18x+30y=42
```

one solution:

```text
x0=14
y0=-7
g=6
```

So:

```text
b/g=5
a/g=3
```

All solutions:

```text
x=14+5t
y=-7-3t
```

Try:

```text
t=-2
```

Then:

```text
x=4
y=-1
```

Try:

```text
t=-3
```

Then:

```text
x=-1
y=2
```

There is no non-negative pair here.

This illustrates why integer existence and non-negative existence are different questions.

---

## 20.9 Non-Negative Solution Constraints

Counting problems usually require:

```text
x>=0
y>=0
```

Start from:

```text
x=x0+(b/g)t
y=y0-(a/g)t
```

Assume `a,b>0`.

For `x>=0`:

```text
x0+(b/g)t >= 0
```

So:

```text
t >= -x0/(b/g)
```

For `y>=0`:

```text
y0-(a/g)t >= 0
```

So:

```text
t <= y0/(a/g)
```

Thus valid integer `t` must lie in an interval:

```text
lower <= t <= upper
```

### Real-world scenario — box counts

Negative boxes make no sense.

So after gcd says:

```text
integer solution exists
```

you must still enforce:

```text
number of small boxes >=0
number of large boxes >=0
```

### Contest lesson

```text
gcd divides c
```

answers:

```text
"Does ANY integer solution exist?"
```

It does **not** automatically answer:

```text
"Does a valid count solution exist?"
```

---

## 20.10 Positive Solution Constraints

Sometimes the story says both types must be used.

Then:

```text
x>=1
y>=1
```

Transform:

```text
x=X+1
y=Y+1
```

where:

```text
X,Y>=0
```

Original:

```text
ax+by=c
```

becomes:

```text
a(X+1)+b(Y+1)=c
```

So:

```text
aX+bY=c-a-b
```

Now solve a standard non-negative equation.

### Real-world scenario — use both bus types

Suppose:

```text
small bus carries 20
large bus carries 30
```

and at least one of each must be used.

Equation:

```text
20x+30y=P
x,y>=1
```

Shift:

```text
x=X+1
y=Y+1
```

Then:

```text
20X+30Y=P-50
X,Y>=0
```

This often simplifies boundary handling.

---

## 20.11 Modular Equation View

From:

```text
ax+by=c
```

take modulo `b`:

```text
ax ≡ c (mod b)
```

The `by` term disappears because:

```text
by ≡ 0 (mod b)
```

### Example

```text
7x+5y=24
```

Modulo `5`:

```text
7x ≡ 24 (mod 5)
```

Reduce:

```text
2x ≡ 4 (mod 5)
```

Try or invert:

```text
x ≡ 2 (mod 5)
```

Smallest non-negative candidate:

```text
x=2
```

Then:

```text
14+5y=24
5y=10
y=2
```

Solution:

```text
(x,y)=(2,2)
```

### Real-world scenario

If one package contributes multiples of `5`, modulo `5` makes that package invisible and isolates the other count.

This is often faster than solving two variables directly.

---

## 20.12 Modular Inverse Form

For:

```text
ax ≡ c (mod m)
```

if:

```text
gcd(a,m)=1
```

then `a` has an inverse modulo `m`.

Multiply by:

```text
a^(-1)
```

to obtain:

```text
x ≡ c*a^(-1) (mod m)
```

### Example

```text
3x ≡ 4 (mod 7)
```

Inverse of `3 mod 7` is `5` because:

```text
3*5=15 ≡ 1 (mod 7)
```

Therefore:

```text
x ≡ 4*5
  ≡ 20
  ≡ 6 (mod 7)
```

Check:

```text
3*6=18 ≡ 4 (mod 7)
```

### When gcd is not 1

For:

```text
ax ≡ c (mod m)
```

solutions exist iff:

```text
gcd(a,m) | c
```

Again the Diophantine criterion appears.

---

## 20.13 Fixed Total Count Transformation

A very common CF form gives:

```text
x+y=n
```

and another weighted condition:

```text
ax+by=S
```

Substitute:

```text
y=n-x
```

Then:

```text
ax+b(n-x)=S
```

Expand:

```text
ax+bn-bx=S
```

Collect:

```text
(a-b)x=S-bn
```

Therefore:

```text
x = (S-bn)/(a-b)
```

Then:

```text
y=n-x
```

### Real-world scenario — tickets

Exactly:

```text
10 tickets
```

Adult ticket:

```text
8 lei
```

Child ticket:

```text
5 lei
```

Total:

```text
62 lei
```

Equations:

```text
x+y=10
8x+5y=62
```

Substitute:

```text
8x+5(10-x)=62

8x+50-5x=62

3x=12

x=4
y=6
```

### Recognition trigger

When the statement gives both:

```text
total number of objects
total weighted value
```

eliminate one variable immediately.

---

## 20.14 Two Package Sizes and Exact Total

Suppose package sizes are:

```text
a and b
```

and target:

```text
c
```

Model:

```text
ax+by=c
x,y>=0
```

### Example

```text
6x+9y=42
```

First:

```text
gcd(6,9)=3
3 | 42
```

Integer solutions exist.

Try modulo:

```text
6x ≡ 42 (mod 9)

6x ≡ 6 (mod 9)
```

Divide gcd `3`:

```text
2x ≡ 2 (mod 3)
```

Thus:

```text
x ≡ 1 (mod 3)
```

Try:

```text
x=1
```

Then:

```text
6+9y=42
9y=36
y=4
```

Valid:

```text
x=1,y=4
```

### Real-world scenario — water bottle packs

Packs contain either `6` or `9` bottles. Exact-total questions are direct non-negative Diophantine problems.

---

## 20.15 Coins and Purchase Modeling

Suppose you have coin denominations:

```text
a,b
```

and want exact amount:

```text
c
```

Unlimited coins:

```text
ax+by=c
x,y>=0
```

### Example

Coins:

```text
4 and 7
```

Target:

```text
23
```

Equation:

```text
4x+7y=23
```

Modulo `4`:

```text
7y ≡ 23 (mod 4)

3y ≡ 3 (mod 4)

y ≡ 1 (mod 4)
```

Try:

```text
y=1
```

Then:

```text
4x+7=23
4x=16
x=4
```

So:

```text
4*4 + 7*1 = 23
```

### Important variant

If coin counts are limited:

```text
0<=x<=X
0<=y<=Y
```

gcd alone is insufficient.

You must enforce bounds.

---

## 20.16 Movement and Reachability

Movement problems often hide a linear combination.

Suppose allowed moves on a number line are:

```text
+a
-b
```

After:

```text
x forward moves
y backward moves
```

position is:

```text
ax-by
```

To reach target `T`:

```text
ax-by=T
```

This is still:

```text
ax+b(-y)=T
```

a Diophantine equation.

### Real-world scenario — elevator-like jumps

Allowed jumps:

```text
+6 floors
-4 floors
```

Can an unrestricted sequence produce net movement:

```text
2 floors?
```

Equation:

```text
6x-4y=2
```

Since:

```text
gcd(6,4)=2
```

and:

```text
2 | 2
```

integer reachability is possible.

Indeed:

```text
6-4=2
```

### Warning

If the process cannot go below floor `0`, or move counts are bounded, path constraints add more conditions beyond the equation.

---

## 20.17 Parity as a Tiny Diophantine Constraint

Parity is often a simplified divisibility condition.

Example:

```text
2x+4y
```

is always even.

Therefore:

```text
2x+4y=17
```

is impossible.

This is just:

```text
gcd(2,4)=2
2 ∤ 17
```

### Another example

Suppose each operation changes a score by:

```text
+3 or +5
```

Both are odd.

If exactly `k` operations are performed:

```text
3x+5y=S
x+y=k
```

Modulo `2`:

```text
3x+5y
≡ x+y
≡ k
(mod 2)
```

Therefore:

```text
S % 2 = k % 2
```

is necessary.

### Real-world scenario

If every purchased item has an odd price and you buy exactly `k` items, the parity of the total is fixed by `k`.

Parity observations are often Diophantine equations viewed modulo `2`.

---

## 20.18 Difference and Sum Equations

A common form:

```text
x+y=S
x-y=D
```

Add:

```text
2x=S+D
```

So:

```text
x=(S+D)/2
```

Subtract:

```text
2y=S-D
```

So:

```text
y=(S-D)/2
```

### Integer condition

Need:

```text
S+D even
S-D even
```

Equivalent to:

```text
S and D have the same parity
```

### Example

Total games:

```text
S=10
```

Difference between wins and losses:

```text
D=4
```

Then:

```text
wins=(10+4)/2=7
losses=(10-4)/2=3
```

### Real-world scenario — account deposits and withdrawals

If you know:

```text
total transaction count
difference between deposits and withdrawals
```

the same equations recover both counts.

---

## 20.19 Bounded Diophantine Equations

Now require:

```text
Lx <= x <= Rx
Ly <= y <= Ry
```

All integer solutions:

```text
x=x0+(b/g)t
y=y0-(a/g)t
```

Each bound becomes a bound on `t`.

### Example structure

From:

```text
x>=Lx
```

derive:

```text
t >= something
```

From:

```text
x<=Rx
```

derive:

```text
t <= something
```

Do the same for `y`.

Finally intersect:

```text
t ∈ [Tmin,Tmax]
```

If:

```text
Tmin <= Tmax
```

a bounded solution exists.

### Real-world scenario — inventory

Need exact total value:

```text
6x+9y=42
```

but inventory says:

```text
0<=x<=2
0<=y<=5
```

A mathematically valid solution outside inventory is useless.

Bounds are part of the model, not an implementation detail.

---

## 20.20 Counting Valid Solutions

Once all solutions are parameterized by:

```text
t
```

and constraints reduce to:

```text
Tmin <= t <= Tmax
```

the number of integer solutions is:

```text
Tmax-Tmin+1
```

if:

```text
Tmin<=Tmax
```

otherwise:

```text
0
```

### Real-world scenario — package combinations

If valid package combinations correspond to:

```text
t=2,3,4,5
```

then there are:

```text
5-2+1=4
```

different purchasing plans.

### Mathematical transformation

```text
2-variable equation
       ↓
one known solution
       ↓
all solutions parameterized by t
       ↓
bounds become interval for t
       ↓
count integers in interval
```

This is a powerful reduction.

---

## 20.21 Three or More Variables

A form such as:

```text
ax+by+cz=T
```

has more freedom.

One practical approach when one variable has a small range:

```text
iterate z
```

For each fixed `z`:

```text
ax+by=T-cz
```

which becomes a two-variable Diophantine equation.

### Example

```text
4x+7y+10z=31
x,y,z>=0
```

Try possible `z`:

```text
z=0:
4x+7y=31

z=1:
4x+7y=21

z=2:
4x+7y=11

z=3:
remaining=1
```

For `z=1`:

```text
4x+7y=21
```

Choose:

```text
x=0,y=3
```

So:

```text
(x,y,z)=(0,3,1)
```

### Contest lesson

Do not force a complicated closed form if one dimension is small enough to enumerate.

Mathematical modeling can reduce:

```text
3D search
```

to:

```text
small loop + O(log M) arithmetic test
```

---

## 20.22 Frobenius and Large Reachability Intuition

For positive coprime integers:

```text
gcd(a,b)=1
```

the largest non-negative integer that **cannot** be represented as:

```text
ax+by
x,y>=0
```

is:

```text
ab-a-b
```

This is the two-coin Frobenius result.

### Example

```text
a=4
b=7
```

Largest impossible amount:

```text
4*7-4-7
=17
```

Therefore every integer:

```text
>=18
```

can be represented using non-negative `4`s and `7`s.

Examples:

```text
18=4+7+7
19=4+4+4+7
20=4*5
21=7*3
```

### Real-world scenario — package sizes

With coprime package sizes, sufficiently large totals eventually become reachable.

### Important

This formula is specifically for:

```text
two positive coprime denominations
```

Do not apply it blindly to more variables or non-coprime values.

---

## 20.23 When gcd Is Not Enough

This is one of the most important sections.

### Case 1 — non-negative restriction

```text
6x+10y=2
```

gcd:

```text
gcd(6,10)=2
2 | 2
```

So an integer solution exists.

But with:

```text
x,y>=0
```

the smallest positive contribution is `6`.

No non-negative solution exists.

### Case 2 — upper bounds

```text
4x+7y=100
```

may have non-negative solutions, but perhaps:

```text
x<=2
y<=2
```

makes them impossible.

### Case 3 — exact number of operations

Additional equation:

```text
x+y=k
```

can destroy otherwise valid solutions.

### Case 4 — path/order constraints

Equation says net movement is possible, but intermediate states may be forbidden.

### Checklist

After gcd passes, ask:

```text
integer?
non-negative?
positive?
bounded?
exact count?
ordering/path restrictions?
```

**Memory hook:** `GCD OPENS THE DOOR; CONSTRAINTS DECIDE WHETHER YOU MAY WALK THROUGH IT.`

---

## 20.24 60-Second Diophantine Discovery Workflow

```text
PROBLEM STORY
     |
     v
Are there unknown COUNTS of fixed actions/items?
     |
   +---+---+
   |       |
  NO      YES
           |
           v
Write contribution equation
           |
           v
      ax + by = c
           |
           v
      g = gcd(a,b)
           |
      +----+----+
      |         |
   g ∤ c      g | c
      |         |
 impossible     v
          What domain?
       /      |      \
 integer   x,y>=0   bounded
    |          |        |
 Bézout    shift t   intersect
 existence   range     t ranges
       \       |       /
        \      |      /
           construct
              |
              v
        verify original story
```

### Fast contest questions

```text
1. What are the unknown counts?
2. What does one unit of each choice contribute?
3. What exact total must be reached?
4. Can I write ax+by=c?
5. Does gcd(a,b) divide c?
6. Do I need actual x,y or only YES/NO?
7. Must x,y be non-negative or positive?
8. Are there upper bounds?
9. Is x+y also fixed?
10. Can modulo eliminate one variable?
11. Can one variable be enumerated cheaply?
12. Are there path/order constraints beyond the equation?
```

---

## 20.25 Codeforces Recognition Map

| Statement clue | Mathematical model |
|---|---|
| use `x` items of size `a` and `y` of size `b` | `ax+by=c` |
| exact total using two move sizes | linear Diophantine |
| can target be reached by integer combinations? | `gcd(a,b) | c` |
| actual coefficients needed | Extended Euclid |
| all solutions needed | `x=x0+(b/g)t`, `y=y0-(a/g)t` |
| counts cannot be negative | constrain `t` |
| both types must be used | shift `x=X+1`, `y=Y+1` |
| one term disappears modulo another | modular equation |
| `ax ≡ c (mod m)` | gcd/inverse |
| exactly `n` objects and weighted total `S` | substitute `y=n-x` |
| sum and difference known | `(S±D)/2` + parity |
| movement with `+a` and `-b` | `ax-by=T` |
| only parity matters | equation modulo `2` |
| bounded inventory | bounded Diophantine |
| count all valid pairs | parameter `t` and count interval |
| 3 variables, one small | enumerate one, solve remaining two |
| two coprime denominations, large target | Frobenius intuition |
| gcd passes but story still fails | inspect non-negative/bounds/path constraints |

---

## 20.26 Common Mistakes

### 1. Checking only gcd

```text
gcd(a,b) | c
```

guarantees an integer solution, not necessarily:

```text
x,y>=0
```

### 2. Forgetting that counts must be integers

A real-valued algebraic solution is invalid for counts.

### 3. Dividing an equation without checking divisibility

From:

```text
3x=10
```

you cannot conclude an integer `x`.

### 4. Forgetting sign restrictions

Extended Euclid commonly returns negative coefficients.

### 5. Confusing one solution with all solutions

One solution:

```text
(x0,y0)
```

does not describe the full solution set.

Use:

```text
x=x0+(b/g)t
y=y0-(a/g)t
```

### 6. Incorrect floor/ceil with negative numbers

When deriving `t` bounds, mathematical floor/ceil are not always the same as truncating integer division in C++.

### 7. Using modular inverse when gcd is not 1

An inverse of `a mod m` exists only if:

```text
gcd(a,m)=1
```

unless the congruence is first reduced appropriately.

### 8. Ignoring an extra equation

If the problem also gives:

```text
x+y=n
```

use it. The problem may collapse to O(1).

### 9. Brute forcing a huge range unnecessarily

Before looping to `10^9`, try:

```text
gcd
modulo
substitution
parameterization
```

### 10. Modeling net movement but ignoring forbidden intermediate states

Reachability equation and path feasibility are not always the same problem.

---

## 20.27 Fast Revision Card

```text
========================================================
PART 20 — DIOPHANTINE MODELING
========================================================

CORE FORM

a*x + b*y = c

x,y must be integers

--------------------------------------------

STORY -> EQUATION

x = count of action/item A
y = count of action/item B

one A contributes a
one B contributes b

total:
a*x+b*y=c

--------------------------------------------

INTEGER EXISTENCE

g = gcd(a,b)

solution exists
IFF

g | c

--------------------------------------------

BEZOUT

a*x0+b*y0=g

if:
c=g*k

then:
x=x0*k
y=y0*k

is one solution

--------------------------------------------

ALL SOLUTIONS

x = x0 + (b/g)t
y = y0 - (a/g)t

t ∈ Z

--------------------------------------------

NON-NEGATIVE

require:

x>=0
y>=0

convert both inequalities
into bounds on t

intersect t ranges

--------------------------------------------

POSITIVE COUNTS

x>=1, y>=1

set:

x=X+1
y=Y+1

then solve for:
X,Y>=0

--------------------------------------------

MODULAR VIEW

a*x+b*y=c

mod b:

a*x ≡ c (mod b)

--------------------------------------------

FIXED TOTAL COUNT

x+y=n
a*x+b*y=S

y=n-x

(a-b)x=S-bn

x=(S-bn)/(a-b)

then:
y=n-x

--------------------------------------------

SUM + DIFFERENCE

x+y=S
x-y=D

x=(S+D)/2
y=(S-D)/2

need correct parity

--------------------------------------------

MOVEMENT

+a and -b

target T:

a*x-b*y=T

--------------------------------------------

COUNT SOLUTIONS

all valid solutions correspond to:

Tmin <= t <= Tmax

count:

max(0, Tmax-Tmin+1)

--------------------------------------------

GCD IS NOT ENOUGH WHEN:

x,y >= 0
x,y > 0
upper bounds exist
x+y fixed
path/order restricted

--------------------------------------------

CORE CONTEST QUESTION

"Can I turn the story into an equation
whose unknowns are integer COUNTS?"

If YES:

equation
  ↓
gcd / modulo
  ↓
bounds
  ↓
construction
========================================================
```
