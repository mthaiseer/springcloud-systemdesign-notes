# TLE Eliminators — Binary Search on Decimals
## TLE-Style Notes — Real Search Space + Precision + Error + Fixed Iterations

> **Source:** TLE Eliminators lecture **Binary Search on Decimals & Problem Solving — Priyansh Agarwal**.
>
> **Goal:** understand how ordinary integer binary search becomes binary search over real numbers without memorizing magic iteration counts.
>
> **Core flow**
>
> ```text
> integer boundary search
>        |
>        v
> monotonic predicate
>        |
>        v
> real-valued search space
>        |
>        v
> precision / error requirement
>        |
>        v
> derive number of iterations
>        |
>        v
> fixed-iteration binary search
> ```
>
> **Problem flow**
>
> ```text
> What It Asks
> → Small Dry Run
> → Search Space
> → Predicate
> → Monotonicity
> → Precision Math
> → Detailed Dry Run
> → Algorithm
> → C++17
> → Complexity
> → Recognition Model
> ```
>
> **Important terminology note:** the lecture examples test correctness using
>
> ```text
> |realAnswer - output| <= tolerance
> ```
>
> so these notes use that exact error formula.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Integer Boundary Search](#11-integer-boundary-search)
  - [1.2 Monotonic Predicate](#12-monotonic-predicate)
  - [1.3 Sqrt Predicate — y² <= x](#13-sqrt-predicate--y²--x)
  - [1.4 Last True vs First True](#14-last-true-vs-first-true)
  - [1.5 Why Real Numbers Are Different](#15-why-real-numbers-are-different)
  - [1.6 Midpoint on Real Numbers](#16-midpoint-on-real-numbers)
  - [1.7 Decimal Precision](#17-decimal-precision)
  - [1.8 Error Tolerance](#18-error-tolerance)
  - [1.9 Infinite Candidates → Finite Precision](#19-infinite-candidates--finite-precision)
  - [1.10 Math — How Many Iterations?](#110-math--how-many-iterations)
  - [1.11 Why Fixed Iterations Make the Code Stop](#111-why-fixed-iterations-make-the-code-stop)
  - [1.12 Output Formatting Is Not Accuracy](#112-output-formatting-is-not-accuracy)
  - [1.13 Floating-Point Type](#113-floating-point-type)
  - [1.14 Recognition Checklist](#114-recognition-checklist)
- [2. Warm-Up — Integer Sqrt(x)](#2-warm-up--integer-sqrtx)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run](#22-small-dry-run)
  - [2.3 Predicate + Monotonicity](#23-predicate--monotonicity)
  - [2.4 Algorithm](#24-algorithm)
  - [2.5 C++17](#25-c17)
- [3. Main Problem — Sqrt(x) on Real Space](#3-main-problem--sqrtx-on-real-space)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run First](#32-small-dry-run-first)
  - [3.3 Why low = mid + 1 Does Not Work](#33-why-low--mid--1-does-not-work)
  - [3.4 Search Space](#34-search-space)
  - [3.5 Predicate](#35-predicate)
  - [3.6 Monotonicity Proof](#36-monotonicity-proof)
  - [3.7 Precision Requirement](#37-precision-requirement)
  - [3.8 Detailed Binary Search Dry Run](#38-detailed-binary-search-dry-run)
  - [3.9 Algorithm](#39-algorithm)
  - [3.10 C++17](#310-c17)
  - [3.11 Complexity](#311-complexity)
  - [3.12 Recognition Model](#312-recognition-model)
- [4. Precision and Iteration Cheat Sheet](#4-precision-and-iteration-cheat-sheet)
- [5. Common Mistakes](#5-common-mistakes)
- [6. Final Mental Model](#6-final-mental-model)

---

# 0. Class Map

```text
BINARY SEARCH
│
├── Integer search space
│   ├── candidates are discrete
│   ├── next integer = mid + 1
│   └── previous integer = mid - 1
│
└── Real / decimal search space
    ├── infinitely many candidates
    ├── no universal "next real"
    ├── use low = mid / high = mid
    ├── require precision / tolerance
    └── run fixed number of iterations
```

For square root:

```text
candidate y
    |
    v
is y² <= x ?
    |
    +------+
    |      |
   YES     NO
    |      |
    v      v
move      move
right     left

T T T T T T F F F F
          ^
          |
      boundary = sqrt(x)
```

---

# 1. Prerequisites

## 1.1 Integer Boundary Search

Before decimals, be comfortable with binary search over integers.

Example:

```text
Find the largest integer y
such that:

y <= 6
```

Candidate integers:

```text
0 1 2 3 4 5 6 7 8 9
```

Predicate:

```text
y <= 6
```

Boolean values:

```text
0 1 2 3 4 5 6 7 8 9
T T T T T T T F F F
            ^
            |
        last True
```

Binary search finds the boundary.

### Tiny dry run

```text
low = 0
high = 9
```

Mid:

```text
mid = 4
```

```text
4 <= 6
→ True
```

Since we need the **largest True**:

```text
move right
low = 5
```

Eventually:

```text
answer = 6
```

This exact boundary idea remains the same for decimals.

---

## 1.2 Monotonic Predicate

A predicate returns:

```text
True / False
```

Binary search requires these results to have only one transition.

Valid shapes:

```text
T T T T T F F F
```

or:

```text
F F F F T T T T
```

Invalid shape:

```text
T F T F F T
```

because there is no single boundary.

### Dry run

Predicate:

```text
P(y) = (y <= 5)
```

For:

```text
y = 1 2 3 4 5 6 7
```

we get:

```text
T T T T T F F
```

So:

```text
binary search works
```

---

## 1.3 Sqrt Predicate — y² <= x

The lecture begins with integer square root:

```text
Given positive integer x,
find the highest integer y
such that:

y² <= x
```

Take:

```text
x = 37
```

Check candidates:

```text
y = 1
1² = 1 <= 37
True

y = 5
5² = 25 <= 37
True

y = 6
6² = 36 <= 37
True

y = 7
7² = 49 > 37
False
```

So:

```text
1 2 3 4 5 6 7 8 ...
T T T T T T F F ...
          ^
          |
       last True
```

Answer:

```text
6
```

ASCII:

```text
0----1----2----3----4----5----6----7----8---->

                         TRUE | FALSE
                              ^
                              |
                        boundary
```

---

## 1.4 Last True vs First True

For this lecture, square root uses:

```text
T T T T T F F F
```

so we want:

```text
LAST TRUE
```

Mental rule:

```text
T T T T F F F
      ^
      |
largest feasible value
```

Another problem may instead give:

```text
F F F F T T T
        ^
        |
smallest feasible value
```

which is:

```text
FIRST TRUE
```

For decimal square root:

```text
highest real y with y² <= x
```

we still want:

```text
LAST TRUE
```

The candidate type changes from:

```text
integer
```

to:

```text
real
```

but the predicate shape does not change.

---

## 1.5 Why Real Numbers Are Different

### Mini Dry Run

Suppose:

```text
low = 2
high = 3
```

Between `2` and `3` there are infinitely many real numbers:

```text
2.1
2.01
2.001
2.0001
2.25
2.5
2.75
2.999999
...
```

The binary-search sequence itself may look like:

```text
2.5
2.25
2.125
2.0625
...
```

ASCII:

```text
2-------------------------------------------3
  |   |   |   |   |   |   |   |   |   |
 infinitely many real values
```

After:

```text
mid = 2.5
```

there is no unique:

```text
"next real number"
```

It could be:

```text
2.6
2.51
2.501
2.500001
...
```

Therefore:

```text
mid + 1
```

or:

```text
low = mid + 1
```

has no general meaning on a continuous real search space.

---

## 1.6 Midpoint on Real Numbers

The midpoint formula stays the same:

```text
mid = (low + high) / 2
```

### Mini Dry Run

```text
low = 2
high = 5
```

Then:

```text
mid = (2 + 5) / 2
    = 3.5
```

ASCII:

```text
2---------------3.5---------------5
low             mid              high
```

If:

```text
can(3.5) = True
```

and we want the highest True:

```text
low = 3.5
```

New interval:

```text
[3.5, 5]
```

For real binary search:

```text
predicate(mid) = True
→ low = mid

predicate(mid) = False
→ high = mid
```

We keep shrinking the interval containing the boundary.

---

## 1.7 Decimal Precision

### Mini Dry Run

Suppose we need:

```text
3 decimal places
```

Then relevant scale is:

```text
10^-3
=
0.001
```

If current search interval is:

```text
[3.141, 3.142]
```

its width is:

```text
0.001
```

So we have reached approximately the needed resolution.

Suppose the answer must be correct up to:

```text
5 decimal places
```

The relevant decimal scale is:

```text
10^-5
=
0.00001
```

For:

```text
10 / 3
=
3.333333333...
```

values very close to the real answer can be accepted under the required tolerance.

The lecture uses this idea to motivate:

```text
precision
```

rather than exact equality.

### Scale table

```text
1 decimal place  → 10^-1 = 0.1
2 decimal places → 10^-2 = 0.01
3 decimal places → 10^-3 = 0.001
4 decimal places → 10^-4 = 0.0001
5 decimal places → 10^-5 = 0.00001
6 decimal places → 10^-6 = 0.000001
```

This table is worth memorizing.

---

## 1.8 Error Tolerance

The lecture examples use:

```text
|realAnswer - output|
<=
tolerance
```

### Mini Dry Run

Real answer:

```text
3.14159
```

Output:

```text
3.14150
```

Difference:

```text
|3.14159 - 3.14150|
=
0.00009
```

If tolerance is:

```text
0.001
```

then:

```text
0.00009 <= 0.001
```

So the output is acceptable.

A second lecture-style check gives the same rule:

```text
tolerance = 0.01
realAnswer = 0.012
output = 0.020

|0.012 - 0.020|
=
0.008
<=
0.01
```

### Number-line visualization

```text
realAnswer - eps       realAnswer       realAnswer + eps
       |                   |                   |
-------[===================●===================]-------
          accepted output region
```

The goal is not:

```text
find the mathematically exact infinite decimal
```

The goal is:

```text
shrink uncertainty enough
that the output is inside the accepted error range
```

---

## 1.9 Infinite Candidates → Finite Precision

Real numbers give infinitely many candidates.

The lecture makes the required search effectively finite by fixing:

```text
number of decimal places
```

If:

```text
k = decimal places
```

then grid spacing is:

```text
10^-k
```

and the approximate number of precision-sized slots in `[L,R]` is:

```text
(R - L) * 10^k
```

### Mini Dry Run

```text
L = 0
R = 2
k = 2
```

Resolution:

```text
10^-2
=
0.01
```

Conceptual grid:

```text
0.00, 0.01, 0.02, ..., 1.99, 2.00
```

Approximate slots:

```text
(2 - 0) * 10²
=
200
```

Another lecture-scale check:

```text
L = 0
R = 5
k = 2

(5 - 0) * 10²
=
500
```

So the bridge is:

```text
infinite real numbers
        |
        v
required decimal precision
        |
        v
finite resolution to distinguish
```

---

## 1.10 Math — How Many Iterations?

### Mini Dry Run

Suppose:

```text
low = 0
high = 8
```

Initial width:

```text
8
```

After each iteration:

```text
8
→ 4
→ 2
→ 1
→ 0.5
→ 0.25
```

After 5 iterations:

```text
width = 8 / 2^5
      = 0.25
```

This shows exactly why:

```text
remaining width
=
initial width / 2^iterations
```

This is the most important math prerequisite for decimal binary search.

Initial interval width:

```text
D = high - low
```

Each binary-search iteration halves it.

After:

```text
t
```

iterations:

```text
remaining width
=
D / 2^t
```

If we need resolution:

```text
10^-k
```

require:

```text
D / 2^t
<=
10^-k
```

Now derive `t`.

Start:

```text
D / 2^t
<=
10^-k
```

Multiply by:

```text
2^t
```

```text
D
<=
2^t * 10^-k
```

Multiply by:

```text
10^k
```

```text
D * 10^k
<=
2^t
```

Take:

```text
log2
```

```text
log2(D * 10^k)
<=
t
```

Therefore:

```text
t
>=
log2(
    (high - low) * 10^k
)
```

Use:

```text
ceil(...)
```

for an integer iteration count.

### Tiny dry run

Suppose:

```text
low = 0
high = 100

k = 3
```

Then:

```text
D = 100
```

Required resolution:

```text
0.001
```

Need:

```text
100 / 2^t
<=
0.001
```

So:

```text
2^t
>=
100 / 0.001
=
100000
```

Since:

```text
2^16 = 65536
2^17 = 131072
```

we need:

```text
17 iterations
```

plus a few extra iterations if desired.

### Useful log facts from the lecture

```text
log2(1000)
≈
10
```

and:

```text
log(a*b)
=
log(a)
+
log(b)
```

So multiplying the search resolution by another factor of about:

```text
1000
```

adds about:

```text
10
```

binary-search iterations.

---

## 1.11 Why Fixed Iterations Make the Code Stop

### Mini Dry Run

Suppose:

```text
low = 3.162277
high = 3.162278
```

These values are already extremely close.

Instead of waiting for:

```text
low == high
```

we decide beforehand:

```text
run 70 iterations
```

After enough halvings, the interval becomes much smaller than the required tolerance, and the loop always terminates.

Integer binary search can stop using:

```text
low <= high
```

because integer candidates are discrete.

With floating point, values can become extremely close:

```text
low  = 3.162277660168...
high = 3.162277660169...
```

Repeated midpoint calculations may eventually be limited by floating-point representation.

The lecture recommends:

```text
1. calculate the required number of iterations
2. add a few extra iterations
3. use a for-loop
```

Example:

```cpp
for (int iteration = 0;
     iteration < totalIterations;
     ++iteration) {

    ...
}
```

This guarantees termination.

### Mental model

```text
DO NOT ask:

"Are low and high exactly equal?"

ASK:

"Have I halved the interval enough times
for the required precision?"
```

---

## 1.12 Output Formatting Is Not Accuracy

These are different:

```text
COMPUTATION ACCURACY
```

and:

```text
PRINTING FORMAT
```

### Mini Dry Run

Suppose the true value is roughly:

```text
3.162277...
```

but your algorithm incorrectly computes:

```text
3.1
```

Printing:

```cpp
fixed << setprecision(6)
```

produces:

```text
3.100000
```

It now has six printed digits after the decimal point, but it is still wrong.

So:

```text
binary search iterations
→ create accuracy

setprecision(k)
→ controls display
```

You need both.

---

## 1.13 Floating-Point Type

### Mini Dry Run

Suppose the answer is:

```text
1 / 3
=
0.333333333333...
```

A floating-point variable stores only a finite approximation.

So the machine keeps something like:

```text
0.333333333333...
```

up to the precision supported by the type.

That is why we compare with a tolerance instead of expecting infinite exact digits.

The lecture notes that floating-point types have finite precision and mentions:

```text
double
```

and:

```text
long double
```

as higher-precision storage choices.

For contest code involving decimal binary search, the important idea is:

```text
do not expect infinitely precise real arithmetic
```

That is another reason fixed-iteration binary search is preferable.

Typical C++ types:

```cpp
double
long double
```

For these notes we use:

```cpp
long double
```

to give comfortable precision for the demonstrated problem.

---

## 1.14 Recognition Checklist

### Mini Dry Run

Problem asks:

```text
Find maximum real x
such that:

x*x <= 50

answer accepted within 1e-6
```

Recognition:

```text
real answer
+
monotonic predicate
+
precision requirement
```

Therefore:

```text
Binary Search on Decimals
```

Think **Binary Search on Decimals** when:

```text
answer is a real number
+
there is a monotonic feasibility condition
+
problem accepts an error tolerance
or asks for k decimal places
```

Before coding, answer:

```text
1. What is candidate real value X?

2. What are LOW and HIGH?

3. What is predicate(X)?

4. Is it:
   TTTTFFFF
   or
   FFFFTTTT?

5. Do I want:
   last True
   or
   first True?

6. What precision is required?

7. What is epsilon / decimal scale?

8. How many halvings are enough?

9. Am I using fixed iterations?

10. Am I confusing setprecision
    with actual accuracy?
```

---

# 2. Warm-Up — Integer Sqrt(x)

## 2.1 What It Asks

Given positive integer:

```text
x
```

find:

```text
the largest INTEGER y
```

such that:

```text
y² <= x
```

This is a:

```text
LAST TRUE
```

problem.

### What Result Is Expected?

If:

```text
x = 37
```

then:

```text
6² = 36 <= 37
7² = 49 > 37
```

So expected result:

```text
6
```

This is **not** asking for:

```text
sqrt(37) ≈ 6.082...
```

It asks specifically for the highest valid integer.

---

## 2.2 Small Dry Run

Take:

```text
x = 37
```

Check:

```text
5² = 25 <= 37
True

6² = 36 <= 37
True

7² = 49 > 37
False
```

Therefore:

```text
answer = 6
```

Boolean view:

```text
y:
0 1 2 3 4 5 6 7 8 ...
T T T T T T T F F ...
            ^
            |
        last True
```

---

## 2.3 Predicate + Monotonicity

### Section Dry Run

For:

```text
x = 20
```

test:

```text
y = 4
4² = 16 <= 20
True

y = 5
5² = 25 > 20
False
```

So around the boundary:

```text
... 3 4 | 5 6 ...
... T T | F F ...
```

Expected result:

```text
4
```

Define:

```text
can(y)
=
(y² <= x)
```

If:

```text
can(y) = True
```

then every smaller non-negative value is also True.

Why?

If:

```text
z <= y
```

then:

```text
z² <= y²
```

and if:

```text
y² <= x
```

then:

```text
z² <= x
```

Therefore:

```text
True region
```

is on the left.

Shape:

```text
T T T T T F F F
```

We search for:

```text
last True
```

---

## 2.4 Algorithm

### Section Dry Run

For:

```text
x = 15
```

start:

```text
low = 0
high = 15
```

Suppose first midpoint is:

```text
7
```

Since:

```text
7² > 15
```

move:

```text
high left
```

Eventually the last True becomes:

```text
3
```

because:

```text
3² = 9 <= 15
4² = 16 > 15
```

```text
low = 0
high = x
answer = 0

while low <= high:

    mid = low + (high-low)/2

    if mid² <= x:

        answer = mid
        low = mid + 1

    else:

        high = mid - 1
```

---

## 2.5 C++17

### Expected Code Result

Input:

```text
37
```

Expected output:

```text
6
```

Input:

```text
16
```

Expected output:

```text
4
```

```cpp
#include <bits/stdc++.h>
using namespace std;

long long integerSqrt(long long x) {
    long long low = 0;
    long long high = x;
    long long answer = 0;

    while (low <= high) {
        long long mid =
            low + (high - low) / 2;

        bool valid =
            (mid == 0)
            ||
            (mid <= x / mid);

        if (valid) {
            answer = mid;
            low = mid + 1;
        }
        else {
            high = mid - 1;
        }
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long long x;
    cin >> x;

    cout << integerSqrt(x) << '\n';
}
```

Why:

```text
mid <= x / mid
```

instead of:

```text
mid * mid <= x
```

for integers?

Because:

```text
mid * mid
```

can overflow when `mid` is large.

---

# 3. Main Problem — Sqrt(x) on Real Space

## 3.1 What It Asks

Now solve the same idea over:

```text
REAL NUMBERS
```

Find the highest real number:

```text
k
```

such that:

```text
k² <= x
```

with the required decimal precision / tolerance.

The mathematical boundary is:

```text
sqrt(x)
```

but we are learning how to locate it through binary search.

### What Result Is Expected?

If:

```text
x = 10
```

then:

```text
sqrt(10)
≈
3.162277660...
```

If asked for:

```text
6 decimal places
```

a suitable expected printed result is approximately:

```text
3.162278
```

The important point is:

```text
we want a real approximation
close enough to the true boundary
```

---

## 3.2 Small Dry Run First

Take:

```text
x = 10
```

We know the boundary is between:

```text
3 and 4
```

because:

```text
3² = 9 <= 10
True

4² = 16 > 10
False
```

Now test:

```text
3.5
```

```text
3.5²
=
12.25
>
10
```

False.

So boundary is:

```text
[3, 3.5]
```

Test:

```text
3.25
```

```text
3.25²
=
10.5625
>
10
```

False.

Now boundary:

```text
[3, 3.25]
```

Test:

```text
3.125
```

```text
3.125²
=
9.765625
<=
10
```

True.

Now:

```text
[3.125, 3.25]
```

We keep halving.

ASCII:

```text
3-------------------------------4
T                               F

3---------------3.5
T                F

3------3.25
T       F

3.125--3.25
  T      F

...
```

---

## 3.3 Why low = mid + 1 Does Not Work

### Section Dry Run

Suppose:

```text
low = 3
high = 4
mid = 3.5
```

If `mid` is too large:

```text
high = 3.5
```

so the new interval is:

```text
[3, 3.5]
```

If `mid` were valid and we incorrectly used integer logic:

```text
low = mid + 1
```

we would get:

```text
low = 4.5
```

which jumps over the real answer near:

```text
3.162...
```

The same issue appears at:

```text
mid = 3.125
```

because:

```text
mid + 1
=
4.125
```

again skips the real boundary.

Could we use:

```text
low = mid + 0.1
```

instead?

That also depends on the requested precision. For six decimal places we might need a scale like:

```text
0.000001
```

and for another problem the scale may differ.

There is no universal:

```text
+1
```

or fixed decimal step for real numbers.

Therefore:

```text
True
→ low = mid

False
→ high = mid
```

and precision is controlled by:

```text
number of halvings
```

not by a manually chosen increment.

---

## 3.4 Search Space

For positive:

```text
x >= 1
```

a simple safe search interval is:

```text
low = 0
high = x
```

because:

```text
sqrt(x) <= x
```

for:

```text
x >= 1
```

### Section Dry Run

For:

```text
x = 10
```

search:

```text
[0,10]
```

The actual boundary:

```text
sqrt(10)
≈
3.162...
```

is definitely inside the interval.

The range is wider than necessary, but valid.

If the problem provides explicit:

```text
L and R
```

use those directly.

The lecture's general form is:

```text
find the highest real number
between L and R
for which a monotonic predicate is True
```

---

## 3.5 Predicate

Define:

```text
can(k)
=
(k² <= x)
```

### Section Dry Run

For:

```text
x = 10
```

check several candidates:

```text
k = 2
2² = 4 <= 10
True

k = 3
3² = 9 <= 10
True

k = 3.1
3.1² = 9.61 <= 10
True

k = 3.2
3.2² = 10.24 > 10
False

k = 4
4² = 16 > 10
False
```

So the boundary lies inside:

```text
[3.1, 3.2]
```

and the predicate shape is:

```text
... T T T T | F F F F ...
              ^
              |
          sqrt(10)
```

---

## 3.6 Monotonicity Proof

### Section Dry Run

For:

```text
x = 10
```

if:

```text
3.0² <= 10
```

then every smaller non-negative value also works:

```text
2.9² < 3.0² <= 10
2.0² < 3.0² <= 10
1.0² < 3.0² <= 10
```

So once a value is True, everything to its left is also True.

Suppose:

```text
k² <= x
```

so:

```text
can(k) = True
```

For any non-negative:

```text
z <= k
```

we have:

```text
z² <= k²
```

Since:

```text
k² <= x
```

therefore:

```text
z² <= x
```

So:

```text
can(z) = True
```

This proves:

```text
all smaller values are True
```

Once:

```text
k² > x
```

larger non-negative values also remain False.

Therefore:

```text
T T T T T F F F
```

and we want the:

```text
last True
```

---

## 3.7 Precision Requirement

Suppose the answer must be accurate to:

```text
6 decimal places
```

Relevant scale:

```text
epsilon
=
10^-6
=
0.000001
```

Conceptually we want:

```text
high - low
<=
epsilon
```

so the true boundary is trapped inside a sufficiently small interval.

### Section Dry Run

Suppose:

```text
low  = 3.1622771
high = 3.1622778
```

Width:

```text
high - low
=
0.0000007
=
7e-7
```

Compare:

```text
7e-7 < 1e-6
```

So the current uncertainty is already small enough for a six-decimal-style requirement.

---

## 3.8 Detailed Binary Search Dry Run

Use:

```text
x = 10
low = 0
high = 10
```

Predicate:

```text
mid² <= 10
```

---

### Iteration 1

```text
mid
=
(0 + 10) / 2
=
5
```

Check:

```text
5²
=
25
>
10
```

False.

Move:

```text
high = 5
```

Range:

```text
[0,5]
```

---

### Iteration 2

```text
mid
=
(0 + 5) / 2
=
2.5
```

Check:

```text
2.5²
=
6.25
<=
10
```

True.

Move:

```text
low = 2.5
```

Range:

```text
[2.5,5]
```

---

### Iteration 3

```text
mid
=
(2.5 + 5) / 2
=
3.75
```

Check:

```text
3.75²
=
14.0625
>
10
```

False.

Move:

```text
high = 3.75
```

Range:

```text
[2.5,3.75]
```

---

### Iteration 4

```text
mid
=
(2.5 + 3.75) / 2
=
3.125
```

Check:

```text
3.125²
=
9.765625
<=
10
```

True.

Move:

```text
low = 3.125
```

Range:

```text
[3.125,3.75]
```

---

### Iteration 5

```text
mid
=
(3.125 + 3.75) / 2
=
3.4375
```

Check:

```text
3.4375²
=
11.81640625
>
10
```

False.

Move:

```text
high = 3.4375
```

Range:

```text
[3.125,3.4375]
```

---

### Iteration 6

```text
mid
=
(3.125 + 3.4375) / 2
=
3.28125
```

Check:

```text
3.28125²
=
10.7666015625
>
10
```

False.

Move:

```text
high = 3.28125
```

Range:

```text
[3.125,3.28125]
```

---

The interval keeps halving:

```text
[0,10]
→ [0,5]
→ [2.5,5]
→ [2.5,3.75]
→ [3.125,3.75]
→ [3.125,3.4375]
→ [3.125,3.28125]
→ ...
```

Eventually both ends approach:

```text
3.162277...
```

ASCII:

```text
0--------------------------------------10
                    5 F

0-------------------5
         2.5 T

2.5-----------------5
          3.75 F

2.5------3.75
     3.125 T

3.125----3.75
      3.4375 F

...
```

---

## 3.9 Algorithm

### Section Dry Run

For:

```text
x = 10
k = 3 decimal places
```

Start:

```text
low = 0
high = 10
```

After enough iterations, suppose:

```text
low  = 3.1621
high = 3.1624
```

Width:

```text
0.0003
```

which is below:

```text
0.001
```

So this is enough for roughly 3-decimal resolution.

```text
INPUT:
x
required decimal places k

low = 0
high = x

width =
high - low

iterations =
ceil(
    log2(
        width * 10^k
    )
)
+ a few extra iterations

repeat iterations times:

    mid =
        (low + high) / 2

    if mid² <= x:

        // mid is feasible
        // answer may be larger

        low = mid

    else:

        // mid is too large

        high = mid

answer ≈ low
```

For a general `TTTTFFFF` real predicate:

```text
True
→ low = mid

False
→ high = mid
```

For the opposite monotonic shape:

```text
FFFFTTTT
```

the movement direction changes according to which boundary is required.

---

## 3.10 C++17

### Expected Code Result

Input:

```text
10 6
```

Expected output approximately:

```text
3.162278
```

Input:

```text
25 4
```

Expected output:

```text
5.0000
```

```cpp
#include <bits/stdc++.h>
using namespace std;

long double squareRootDecimal(
    long double x,
    int decimalPlaces
) {
    long double low = 0.0L;

    long double high =
        max(1.0L, x);

    long double width =
        high - low;

    long double scale =
        powl(10.0L, decimalPlaces);

    long double candidateCount =
        max(
            1.0L,
            width * scale
        );

    int iterations =
        (int)ceill(
            log2l(candidateCount)
        )
        + 5;

    for (int iteration = 0;
         iteration < iterations;
         ++iteration) {

        long double mid =
            (low + high) / 2.0L;

        if (mid * mid <= x) {
            low = mid;
        }
        else {
            high = mid;
        }
    }

    return low;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    long double x;
    int decimalPlaces;

    cin >> x >> decimalPlaces;

    long double answer =
        squareRootDecimal(
            x,
            decimalPlaces
        );

    cout << fixed
         << setprecision(decimalPlaces)
         << answer
         << '\n';

    return 0;
}
```

### Code meaning

```text
powl(10, decimalPlaces)
```

creates:

```text
10^k
```

The iteration formula comes from:

```text
(high-low) / 2^t
<=
10^-k
```

The extra:

```text
+5
```

is simply a small safety margin after the mathematically required count.

---

## 3.11 Complexity

Let:

```text
D = high - low
k = required decimal places
```

Required iterations:

```text
O(
    log2(
        D * 10^k
    )
)
```

Using log rules:

```text
log2(D * 10^k)
=
log2(D)
+
k * log2(10)
```

So every extra decimal digit adds only a constant number of binary-search iterations.

Each iteration does:

```text
O(1)
```

work.

Therefore:

```text
Time:
O(
    log(
        (high-low) * 10^k
    )
)

Space:
O(1)
```

### Section Dry Run

From the iteration math in §1.10:

```text
D = 100
k = 3
```

needs about:

```text
17 iterations
```

because:

```text
2^17 = 131072 > 100000
```

So even many decimal candidates still require only a small number of binary-search steps.

---

## 3.12 Recognition Model

### Section Dry Run

Statement:

```text
Find the largest real radius R
such that a feasibility condition holds.
Error <= 1e-6.
```

Recognition:

```text
real answer
+
monotonic condition
+
precision tolerance
```

Expected technique:

```text
REAL BINARY SEARCH
```

Then:

```text
1. derive low/high
2. define monotonic predicate
3. determine True/False shape
4. calculate enough iterations
5. use low=mid / high=mid
6. print required precision
```

In general, if a problem asks for a maximum/minimum real `X` under a monotonic condition and accepts an error tolerance, use the same model.

---

# 4. Precision and Iteration Cheat Sheet

| Required scale | Decimal places | Rough interpretation |
|---|---:|---|
| `1e-1` | 1 | tenths |
| `1e-2` | 2 | hundredths |
| `1e-3` | 3 | thousandths |
| `1e-4` | 4 | 4 decimal places |
| `1e-5` | 5 | 5 decimal places |
| `1e-6` | 6 | common contest tolerance |

### Iteration formula

```text
D = high - low
epsilon = 10^-k
```

Need:

```text
D / 2^t <= epsilon
```

Therefore:

```text
t
>=
log2(D / epsilon)
=
log2(D * 10^k)
```

The full derivation is in §1.10.

### Quick Dry Run

If:

```text
precision = 4 decimals
initial width = 16
```

then:

```text
epsilon = 1e-4
```

Need:

```text
16 / 2^t <= 1e-4

2^t >= 160000
```

Since:

```text
2^17 = 131072
2^18 = 262144
```

we need about:

```text
18 iterations
```

before adding a small safety margin.

### Lecture large-range example

The lecture uses a very large range together with six decimal places to emphasize that even huge real answer spaces need only on the order of tens of iterations, not millions.

Key takeaway:

```text
huge numerical range
does NOT imply
huge binary-search iteration count
```

because the range is halved every time.

---

# 5. Common Mistakes

### Mistake 1 — Using `mid + 1` on decimals

Dry run:

```text
sqrt(10)
≈
3.162...

mid = 3.1
```

If valid, doing:

```text
low = mid + 1
```

gives:

```text
4.1
```

which destroys the search.

Correct:

```text
low = mid
```

Why?

```text
there is no universal next real number
```

---

### Mistake 2 — Using a magic epsilon step

Example:

```cpp
low = mid + 0.000001;
```

This hard-codes the precision into the boundary movement.

Better:

```text
low = mid
high = mid

+
fixed number of halvings
```

---

### Mistake 3 — No monotonicity proof

Being able to compute:

```text
predicate(mid)
```

does not automatically mean binary search is valid.

You need:

```text
TTTTFFFF
```

or:

```text
FFFFTTTT
```

---

### Mistake 4 — Confusing precision with printing

```cpp
setprecision(6)
```

does not make the calculation accurate.

It only controls:

```text
how many digits are printed
```

---

### Mistake 5 — Using exact equality for doubles

Avoid thinking:

```text
low == high
```

must eventually become true.

Use:

```text
fixed iterations
```

based on the required precision.

---

### Mistake 6 — Too few iterations

If:

```text
high - low
```

is huge and you need many decimal places, a tiny fixed count may leave too much uncertainty.

Use:

```text
log2(
    (high-low) * 10^k
)
```

to understand what is enough.

---

### Mistake 7 — Wrong boundary direction

For:

```text
TTTTFFFF
```

and:

```text
highest True
```

use:

```text
True  → move low right
False → move high left
```

For another predicate shape, derive the direction again.

---

# 6. Final Mental Model

```text
                   REAL BINARY SEARCH
                          |
                          v
                 candidate real X
                          |
                          v
                  predicate can(X)
                          |
                          v
                prove monotonicity
                          |
              T T T T | F F F F
                      ^
                      |
                   boundary
                          |
                          v
                 choose [low,high]
                          |
                          v
                decimal requirement k
                          |
                          v
              epsilon ≈ 10^(-k)
                          |
                          v
         derive number of halvings needed
                          |
                          v
                 fixed for-loop
                          |
                          v
          low and high squeeze boundary
```

For square root:

```text
can(y)
=
(y² <= x)
```

Shape:

```text
T T T T T T F F F
          ^
          |
       sqrt(x)
```

Update:

```text
True
→ low = mid

False
→ high = mid
```

Precision math:

```text
initial width = D

after t steps:

width
=
D / 2^t
```

Need:

```text
D / 2^t
<=
10^-k
```

So:

```text
t
>=
log2(D * 10^k)
```

## 60-Second Recognition

```text
REAL ANSWER?
      |
      v
MONOTONIC CONDITION?
      |
      v
ERROR / DECIMAL REQUIREMENT?
      |
      v
YES
      |
      v
BINARY SEARCH ON DECIMALS
```

## One sentence to remember

```text
For decimals, we do not move to the "next value";
we repeatedly halve the uncertainty until it is smaller
than the required precision.
```
