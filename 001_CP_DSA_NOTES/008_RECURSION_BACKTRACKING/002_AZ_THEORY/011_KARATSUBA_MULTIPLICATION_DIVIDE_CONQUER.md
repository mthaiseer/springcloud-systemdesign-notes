# Karatsuba Multiplication — Divide & Conquer
## From Four Recursive Products to Three
### TLE-Style Self-Study Note with Prerequisites, Algebra Derivation, Recursion Trees, Dry Run, Proof, and Complexity

> **Core idea**
>
> Splitting a problem into smaller subproblems does **not** automatically make it faster.
>
> The important question is:
>
> ```text
> HOW MANY recursive subproblems are created?
> ```
>
> Ordinary divide-and-conquer multiplication creates **4** recursive multiplications:
>
> ```text
> T(n) = 4T(n/2) + O(n) = Θ(n²)
> ```
>
> Karatsuba uses an algebraic identity to reduce this to **3**:
>
> ```text
> T(n) = 3T(n/2) + O(n)
>      = Θ(n^log₂3)
>      ≈ Θ(n^1.585)
> ```

---

# Table of Contents

1. [Prerequisite 1 — What Does Input Size Mean?](#1-prerequisite-1--what-does-input-size-mean)
2. [Prerequisite 2 — Base-B Representation](#2-prerequisite-2--base-b-representation)
3. [Prerequisite 3 — Positional Value and Digit Shifts](#3-prerequisite-3--positional-value-and-digit-shifts)
4. [Prerequisite 4 — Modulo and Division by a Power of the Base](#4-prerequisite-4--modulo-and-division-by-a-power-of-the-base)
5. [Prerequisite 5 — Ordinary Multiplication](#5-prerequisite-5--ordinary-multiplication)
6. [Why Schoolbook Multiplication Is Θ(n²)](#6-why-schoolbook-multiplication-is-θn²)
7. [Divide & Conquer Framework](#7-divide--conquer-framework)
8. [Step 1 — Split Each Number](#8-step-1--split-each-number)
9. [First Attempt — Four Recursive Products](#9-first-attempt--four-recursive-products)
10. [Why Four Products Do Not Improve Complexity](#10-why-four-products-do-not-improve-complexity)
11. [Karatsuba's Key Algebra Trick](#11-karatsubas-key-algebra-trick)
12. [Three Recursive Products](#12-three-recursive-products)
13. [Complete Karatsuba Formula](#13-complete-karatsuba-formula)
14. [Recursive Contract](#14-recursive-contract)
15. [Base Case and Termination](#15-base-case-and-termination)
16. [Karatsuba Recursion Tree](#16-karatsuba-recursion-tree)
17. [Full Dry Run — 1234 × 5678](#17-full-dry-run--1234--5678)
18. [Four-Product vs Karatsuba Comparison](#18-four-product-vs-karatsuba-comparison)
19. [Why Karatsuba Is Correct](#19-why-karatsuba-is-correct)
20. [Time Complexity](#20-time-complexity)
21. [Recurrence Tree Analysis](#21-recurrence-tree-analysis)
22. [Space Complexity](#22-space-complexity)
23. [Odd Digit Counts](#23-odd-digit-counts)
24. [Unequal Length Inputs](#24-unequal-length-inputs)
25. [Leading Zeros and Positional Width](#25-leading-zeros-and-positional-width)
26. [Why the Middle Block Cannot Be Concatenated](#26-why-the-middle-block-cannot-be-concatenated)
27. [Implementation Model](#27-implementation-model)
28. [Pseudocode](#28-pseudocode)
29. [Important Implementation Details](#29-important-implementation-details)
30. [Common Mistakes](#30-common-mistakes)
31. [Recognition Checklist](#31-recognition-checklist)
32. [Final Mental Model](#32-final-mental-model)

---

# 1. Prerequisite 1 — What Does Input Size Mean?

Karatsuba is useful when multiplying **large numbers represented by their digits**.

Suppose:

```text
X = 12345678901234567890
Y = 98765432109876543210
```

These values may not fit inside an ordinary built-in integer type.

Therefore, when analyzing Karatsuba:

```text
n = number of digits
```

not:

```text
n = numeric value of X
```

---

## Why this matters

If multiplication of two arbitrarily large numbers were treated as:

```cpp
X * Y
```

in constant time, there would be nothing to optimize.

But for large-digit arithmetic:

```text
addition/subtraction of n-digit numbers = O(n)
single-digit multiplication             = O(1)
large-number multiplication             = what we are analyzing
```

So the real question is:

```text
How many digit operations are required?
```

---

# 2. Prerequisite 2 — Base-B Representation

Any nonnegative integer can be represented in a positional base `B >= 2`.

For `n` digits:

```text
X = x0 + x1*B + x2*B² + ... + x(n-1)*B^(n-1)
```

where:

```text
0 <= xi < B
```

and:

```text
x0 = least significant digit
```

---

## Decimal example

For:

```text
X = 1234
```

in base `10`:

```text
1234
=
4
+ 3 * 10
+ 2 * 10²
+ 1 * 10³
```

So:

```text
x0 = 4
x1 = 3
x2 = 2
x3 = 1
```

ASCII positional view:

```text
decimal number:       1    2    3    4
position from right:  3    2    1    0

value:
1 * 10³
2 * 10²
3 * 10¹
4 * 10⁰
```

---

## Binary example

For:

```text
101101₂
```

the base is:

```text
B = 2
```

and:

```text
101101₂
=
1*2⁵ + 0*2⁴ + 1*2³ + 1*2² + 0*2¹ + 1*2⁰
```

Karatsuba is not inherently decimal.

It works with a fixed base `B`.

---

# 3. Prerequisite 3 — Positional Value and Digit Shifts

Multiplying by:

```text
B^m
```

shifts a number left by `m` digit positions in base `B`.

---

## Decimal example

```text
34 * 10²
=
34 * 100
=
3400
```

Think:

```text
34
↓ shift by 2 decimal positions
3400
```

---

## Binary example

```text
101₂ * 2³
=
101000₂
```

This is a three-bit positional shift.

---

## Important distinction

In Karatsuba, expressions such as:

```text
B^m * Z
```

represent a **digit-position shift**.

They are not arbitrary expensive large-number multiplications.

When working with a digit representation, shifting by a whole number of base positions can be implemented structurally.

---

# 4. Prerequisite 4 — Modulo and Division by a Power of the Base

Suppose we choose:

```text
q = B^m
```

Then any number `X` can be split into:

```text
low  = X mod q
high = floor(X / q)
```

and reconstructed as:

```text
X = low + q * high
```

---

## Example — 1234 split after two decimal digits

```text
B = 10
m = 2
q = 10² = 100
```

Then:

```text
X_L = 1234 mod 100
    = 34
```

and:

```text
X_R = floor(1234 / 100)
    = 12
```

Therefore:

```text
1234 = 34 + 100 * 12
```

Diagram:

```text
          1234
        /      \
      12        34
     high      low

1234 = 12 * 100 + 34
```

---

# 5. Prerequisite 5 — Ordinary Multiplication

Suppose:

```text
X = x0 + x1*B + ... + x(n-1)*B^(n-1)

Y = y0 + y1*B + ... + y(n-1)*B^(n-1)
```

Then:

```text
X * Y
```

contains every pair:

```text
xi * yj
```

with positional weight:

```text
B^(i+j)
```

So:

```text
XY = Σ Σ xi*yj*B^(i+j)
```

---

## Tiny example

Let:

```text
X = 12
Y = 34
```

Using decimal digits:

```text
12 = 2 + 1*10
34 = 4 + 3*10
```

Multiply:

```text
(2 + 1*10)(4 + 3*10)
```

Expand:

```text
2*4
+ 2*3*10
+ 1*10*4
+ 1*3*100
```

So:

```text
8
+ 60
+ 40
+ 300
=
408
```

---

# 6. Why Schoolbook Multiplication Is Θ(n²)

If both numbers have `n` digits:

```text
X has n digits
Y has n digits
```

then each digit of `X` interacts with every digit of `Y`.

Number of digit pairs:

```text
n * n = n²
```

So ordinary multiplication requires:

```text
Θ(n²)
```

single-digit products.

---

## Grid visualization

For:

```text
X digits: x0 x1 x2 x3
Y digits: y0 y1 y2 y3
```

the products are:

```text
          y0      y1      y2      y3

x0      x0y0    x0y1    x0y2    x0y3
x1      x1y0    x1y1    x1y2    x1y3
x2      x2y0    x2y1    x2y2    x2y3
x3      x3y0    x3y1    x3y2    x3y3
```

That is:

```text
4 * 4 = 16
```

products.

For `n` digits:

```text
n²
```

products.

---

# 7. Divide & Conquer Framework

As with Merge Sort, use:

```text
DIVIDE
CONQUER
COMBINE
```

But this time the object is a number rather than an array range.

---

## State

The recursive state is:

```text
(X, Y)
```

meaning:

```text
multiply X and Y exactly
```

---

## Divide

Split both into:

```text
low part
high part
```

using the same split width `m`.

---

## Conquer

Recursively multiply smaller pieces.

---

## Combine

Use algebra to reconstruct:

```text
X * Y
```

from the smaller products.

---

# 8. Step 1 — Split Each Number

Let:

```text
n = maximum digit count of X and Y
m = floor(n / 2)
q = B^m
```

Write:

```text
X = X_L + q * X_R
Y = Y_L + q * Y_R
```

where:

```text
X_L = X mod q
X_R = X div q

Y_L = Y mod q
Y_R = Y div q
```

---

## Example

Take:

```text
X = 1234
Y = 5678
B = 10
```

We have:

```text
n = 4
m = 2
q = 100
```

Split:

```text
1234
├── high = 12
└── low  = 34

5678
├── high = 56
└── low  = 78
```

Therefore:

```text
1234 = 34 + 100*12
5678 = 78 + 100*56
```

---

# 9. First Attempt — Four Recursive Products

Start from:

```text
X = X_L + qX_R
Y = Y_L + qY_R
```

Multiply:

```text
XY
=
(X_L + qX_R)(Y_L + qY_R)
```

Expand step by step:

```text
XY
=
X_LY_L
+ qX_LY_R
+ qX_RY_L
+ q²X_RY_R
```

Group the middle terms:

```text
XY
=
X_LY_L
+ q(X_LY_R + X_RY_L)
+ q²X_RY_R
```

---

## Name the blocks

Define:

```text
Z_L = X_LY_L

Z_M = X_LY_R + X_RY_L

Z_R = X_RY_R
```

Then:

```text
XY = Z_L + qZ_M + q²Z_R
```

---

## But count the actual multiplications

To obtain these blocks:

```text
Z_L:
X_L * Y_L                → 1 multiplication

Z_M:
X_L * Y_R                → 1 multiplication
X_R * Y_L                → 1 multiplication

Z_R:
X_R * Y_R                → 1 multiplication
```

Total:

```text
4 recursive multiplications
```

This is the key point.

---

# 10. Why Four Products Do Not Improve Complexity

The recurrence becomes:

```text
T(n)
=
4T(n/2)
+ O(n)
```

The `O(n)` term includes operations such as:

```text
splitting
adding
subtracting
digit shifting
combining
```

---

## Recursion tree

```text
                         size n
                    /    /   \    \
                   /    /     \    \
               n/2   n/2    n/2   n/2

            4 recursive children
```

At the next level:

```text
4² = 16
```

children.

At level `d`:

```text
4^d
```

subproblems.

Depth:

```text
log₂ n
```

Leaves:

```text
4^(log₂ n)
```

Use:

```text
a^(log_b n) = n^(log_b a)
```

So:

```text
4^(log₂ n)
=
n^(log₂ 4)
=
n²
```

Thus:

```text
T(n) = Θ(n²)
```

No asymptotic improvement.

---

# 11. Karatsuba's Key Algebra Trick

The expensive part is the middle term:

```text
X_LY_R + X_RY_L
```

Naively, it requires two recursive multiplications.

Karatsuba asks:

```text
Can we recover this sum without computing
those two products separately?
```

The answer is yes.

---

## Compute one different product

Let:

```text
P
=
(X_L + X_R)(Y_L + Y_R)
```

Expand:

```text
P
=
X_LY_L
+ X_LY_R
+ X_RY_L
+ X_RY_R
```

But we already compute:

```text
Z_L = X_LY_L
Z_R = X_RY_R
```

So:

```text
P
=
Z_L
+ (X_LY_R + X_RY_L)
+ Z_R
```

Therefore:

```text
X_LY_R + X_RY_L
=
P - Z_L - Z_R
```

So define:

```text
Z_M = P - Z_L - Z_R
```

That gives the entire middle term.

---

# 12. Three Recursive Products

Karatsuba computes only:

```text
1. Z_L = X_L * Y_L

2. Z_R = X_R * Y_R

3. P = (X_L + X_R) * (Y_L + Y_R)
```

Then:

```text
Z_M = P - Z_L - Z_R
```

No separate recursive calls for:

```text
X_L * Y_R
X_R * Y_L
```

This is the optimization.

---

## Visual comparison

Naive split:

```text
                      X * Y
          /         /       \         \
     X_LY_L    X_LY_R    X_RY_L    X_RY_R

                  4 calls
```

Karatsuba:

```text
                      X * Y
                /       |       \
           X_LY_L     X_RY_R      P

             P = (X_L+X_R)(Y_L+Y_R)

                  3 calls
```

---

# 13. Complete Karatsuba Formula

Split:

```text
X = X_L + B^m X_R
Y = Y_L + B^m Y_R
```

Compute:

```text
Z_L = X_LY_L
Z_R = X_RY_R

P = (X_L + X_R)(Y_L + Y_R)
```

Recover:

```text
Z_M = P - Z_L - Z_R
```

Combine:

```text
XY
=
Z_L
+ B^m Z_M
+ B^(2m) Z_R
```

This is the Karatsuba formula.

---

# 14. Recursive Contract

Define:

```text
karatsuba(X, Y)
```

to mean:

```text
return the exact product X * Y
```

This is the recursive promise.

When recursive calls return, trust that:

```text
karatsuba(X_L, Y_L)
```

returns exactly:

```text
X_L * Y_L
```

and similarly for the other calls.

---

# 15. Base Case and Termination

A recursive algorithm must guarantee that every child is smaller.

A conceptual base case is:

```text
if X == 0 or Y == 0:
    return 0
```

and for sufficiently small digit counts:

```text
use ordinary schoolbook multiplication
```

The source lesson uses a small teaching cutoff such as:

```text
n <= 3
```

---

## Why use a small cutoff?

The product-of-sums call is:

```text
karatsuba(X_L + X_R, Y_L + Y_R)
```

A sum may require one extra digit.

Example:

```text
99 + 99 = 198
```

Two-digit pieces produced a three-digit sum.

For large `n`, this still remains substantially smaller than the original problem.

A fixed small cutoff makes termination simple and avoids excessive recursion overhead.

---

# 16. Karatsuba Recursion Tree

For a size-`n` problem:

```text
                            K(n)
                    /         |         \
                   /          |          \
               K(n/2)      K(n/2)      K(n/2)
               Z_L           Z_R           P
```

Each child again produces three children:

```text
                             K(n)
                    /          |          \
                K(n/2)      K(n/2)      K(n/2)
               / | \         / | \        / | \
              ...           ...          ...
```

At depth `d`:

```text
number of nodes = 3^d
```

Depth is about:

```text
log₂ n
```

This difference from the 4-child tree changes the exponent.

---

# 17. Full Dry Run — 1234 × 5678

Use:

```text
X = 1234
Y = 5678
B = 10
```

Digit count:

```text
n = 4
```

Choose:

```text
m = 2
q = 10² = 100
```

---

## Step 1 — Divide

```text
X_L = 1234 mod 100 = 34
X_R = 1234 div 100 = 12

Y_L = 5678 mod 100 = 78
Y_R = 5678 div 100 = 56
```

Diagram:

```text
                1234
              /      \
            12        34
           X_R       X_L

                5678
              /      \
            56        78
           Y_R       Y_L
```

Reconstruction:

```text
1234 = 34 + 100*12
5678 = 78 + 100*56
```

---

## Step 2 — First recursive product

```text
Z_L = X_L * Y_L
```

So:

```text
Z_L = 34 * 78
    = 2652
```

---

## Step 3 — Second recursive product

```text
Z_R = X_R * Y_R
```

So:

```text
Z_R = 12 * 56
    = 672
```

---

## Step 4 — Third recursive product

Compute sums:

```text
X_L + X_R
=
34 + 12
=
46
```

and:

```text
Y_L + Y_R
=
78 + 56
=
134
```

Then:

```text
P
=
46 * 134
=
6164
```

Notice:

```text
134
```

has three digits.

This is why the product-of-sums branch may be slightly larger than exactly `n/2`.

---

## Step 5 — Recover middle term

Formula:

```text
Z_M = P - Z_L - Z_R
```

Substitute:

```text
Z_M
=
6164 - 2652 - 672
```

First:

```text
6164 - 2652 = 3512
```

Then:

```text
3512 - 672 = 2840
```

Therefore:

```text
Z_M = 2840
```

---

## Step 6 — Verify only for learning

The original middle term is:

```text
X_LY_R + X_RY_L
```

So:

```text
34*56 + 12*78
=
1904 + 936
=
2840
```

Correct.

But Karatsuba does **not** recursively compute these two products separately.

That is the whole optimization.

---

## Step 7 — Combine

Formula:

```text
XY
=
Z_L
+ B^m Z_M
+ B^(2m) Z_R
```

Here:

```text
B^m = 100
B^(2m) = 10000
```

Therefore:

```text
XY
=
2652
+ 100*2840
+ 10000*672
```

Calculate each shifted block:

```text
Z_L             =       2,652
100 * Z_M       =     284,000
10000 * Z_R     =   6,720,000
```

Add:

```text
    2,652
  284,000
6,720,000
---------
7,006,652
```

Therefore:

```text
1234 * 5678 = 7006652
```

---

# 18. Four-Product vs Karatsuba Comparison

| Method | Recursive products | Recurrence | Complexity |
|---|---:|---|---|
| Schoolbook | digit-by-digit | direct | Θ(n²) |
| Straight divide & conquer | 4 | `4T(n/2)+O(n)` | Θ(n²) |
| Karatsuba | 3 | `3T(n/2)+O(n)` | Θ(n^log₂3) |

The important change is:

```text
4 recursive calls
→
3 recursive calls
```

at **every level** of the recursion tree.

---

# 19. Why Karatsuba Is Correct

We prove the recursive contract:

```text
karatsuba(X, Y) returns X * Y
```

---

## Step 1 — Split identities

By construction:

```text
X = X_L + B^m X_R

Y = Y_L + B^m Y_R
```

---

## Step 2 — Recursive products are correct

Assume recursive calls correctly return:

```text
Z_L = X_LY_L

Z_R = X_RY_R

P = (X_L + X_R)(Y_L + Y_R)
```

---

## Step 3 — Expand P

```text
P
=
X_LY_L
+ X_LY_R
+ X_RY_L
+ X_RY_R
```

Since:

```text
Z_L = X_LY_L
Z_R = X_RY_R
```

subtract both:

```text
P - Z_L - Z_R
=
X_LY_R + X_RY_L
```

Therefore:

```text
Z_M
=
X_LY_R + X_RY_L
```

---

## Step 4 — Substitute into combine formula

The algorithm returns:

```text
Z_L + B^m Z_M + B^(2m) Z_R
```

Replace each block:

```text
=
X_LY_L
+ B^m(X_LY_R + X_RY_L)
+ B^(2m)X_RY_R
```

Factor:

```text
=
(X_L + B^mX_R)
(Y_L + B^mY_R)
```

But:

```text
X_L + B^mX_R = X
Y_L + B^mY_R = Y
```

Therefore:

```text
returned value = XY
```

So the algorithm is correct.

---

# 20. Time Complexity

A non-base call performs:

```text
3 recursive multiplications
```

on roughly half-size inputs.

Outside recursion it performs digit operations such as:

```text
splitting
addition
subtraction
shifting
combining
```

These take linear time in the digit count.

Therefore the standard balanced recurrence is:

```text
T(n)
=
3T(n/2)
+ O(n)
```

---

## Result

By the Master Theorem:

```text
a = 3
b = 2
f(n) = O(n)
```

Calculate:

```text
n^(log_b a)
=
n^(log₂ 3)
```

and:

```text
log₂ 3 ≈ 1.585
```

Therefore:

```text
T(n)
=
Θ(n^log₂3)
≈
Θ(n^1.585)
```

This is subquadratic.

---

# 21. Recurrence Tree Analysis

At level `i`:

```text
number of subproblems = 3^i
```

Each has size approximately:

```text
n / 2^i
```

Nonrecursive work per node:

```text
O(n / 2^i)
```

Total work at level `i`:

```text
3^i * O(n / 2^i)
```

So:

```text
O(n * (3/2)^i)
```

Unlike Merge Sort, the work per level is not constant `O(n)`.

It increases geometrically.

---

## Tree

```text
Level 0:

                    n
                  O(n)


Level 1:

            n/2     n/2     n/2

total nonrecursive work:

3 * O(n/2)
=
O(3n/2)


Level 2:

      9 subproblems of size n/4

total:

9 * O(n/4)
=
O(9n/4)
=
O(n * (3/2)²)
```

---

## Bottom level

Depth:

```text
h ≈ log₂ n
```

Number of leaves:

```text
3^h
=
3^(log₂ n)
```

Identity:

```text
a^(log_b n)
=
n^(log_b a)
```

Therefore:

```text
3^(log₂ n)
=
n^(log₂ 3)
```

So:

```text
Θ(n^1.585)
```

leaf-scale work appears.

The geometric sum over all levels has the same asymptotic order.

---

# 22. Space Complexity

The recursion depth is:

```text
O(log n)
```

But stack depth alone is not the entire memory usage.

A realistic implementation also stores:

```text
split pieces
temporary sums
partial products
combined results
```

With depth-first recursive evaluation and temporary storage reused or released:

```text
O(n)
+ O(n/2)
+ O(n/4)
+ ...
```

This geometric series is:

```text
O(n)
```

So a standard implementation can use:

```text
O(n)
```

auxiliary digit storage, plus:

```text
O(log n)
```

call frames.

Overall:

```text
O(n)
```

auxiliary storage in the usual model.

---

# 23. Odd Digit Counts

Suppose:

```text
n = 5
```

Then:

```text
m = floor(5/2)
  = 2
```

So the low part contains two base-`B` positions.

The high part contains the remaining three.

Example decimal structure:

```text
abcde
```

Split:

```text
high = abc
low  = de
```

This is completely valid.

The halves do not need identical digit counts.

---

## Algebra still works

```text
X = X_L + B²X_R
```

So combine with:

```text
B^m
```

and:

```text
B^(2m)
```

Do not replace `B^(2m)` with `B^n` blindly.

For odd `n`:

```text
2m != n
```

---

# 24. Unequal Length Inputs

Suppose:

```text
X has 8 digits
Y has 5 digits
```

Choose:

```text
n = max(length(X), length(Y))
```

Conceptually pad the shorter number with leading zeros.

Example:

```text
X = 12345678
Y =    98765
```

Think:

```text
X = 12345678
Y = 00098765
```

The numeric value of `Y` has not changed.

Use the same:

```text
m
q = B^m
```

for both numbers.

---

# 25. Leading Zeros and Positional Width

Suppose:

```text
X = 1203
m = 2
q = 100
```

Then:

```text
X_L = 3
X_R = 12
```

Even though `X_L` has numeric value:

```text
3
```

its positional block still represents the final **two decimal positions**:

```text
03
```

So reconstruction remains:

```text
1203 = 3 + 100*12
```

not:

```text
3 + 10*12
```

The split width is determined by:

```text
m
```

not by the number of visible nonzero digits in the low part.

---

# 26. Why the Middle Block Cannot Be Concatenated

For:

```text
1234 * 5678
```

we found:

```text
Z_L = 2652
Z_M = 2840
Z_R = 672
```

These are not fixed-width decimal chunks that can simply be concatenated.

Correct combination is:

```text
Z_L
+ 100*Z_M
+ 10000*Z_R
```

The shifted values overlap numerically:

```text
       2652
     284000
    6720000
```

Carries must be handled through addition.

Do **not** think:

```text
672 | 2840 | 2652
```

as literal string concatenation.

---

# 27. Implementation Model

The lecture's algorithm is about **arbitrary-precision multiplication**.

Therefore, the mathematical quantities:

```text
X
Y
X_L
X_R
Y_L
Y_R
Z_L
Z_M
Z_R
P
```

may all exceed `long long`.

A faithful implementation needs an exact digit representation such as:

```text
vector<int> digits
string digits
custom BigInteger
library arbitrary-precision integer
```

depending on the environment.

---

## Important warning

Writing:

```cpp
long long karatsuba(long long x, long long y)
```

does not turn Karatsuba into arbitrary-precision multiplication.

If the inputs fit safely inside `long long`, ordinary hardware multiplication is normally the appropriate tool.

The point of Karatsuba is large-number arithmetic.

---

# 28. Pseudocode

```text
karatsuba(X, Y):

    if X == 0 or Y == 0:
        return 0

    n = max(number_of_base_B_digits(X),
            number_of_base_B_digits(Y))

    if n <= SMALL_CUTOFF:
        return schoolbook_multiply(X, Y)

    m = floor(n / 2)
    q = B^m

    X_L = X mod q
    X_R = X div q

    Y_L = Y mod q
    Y_R = Y div q

    Z_L = karatsuba(X_L, Y_L)
    Z_R = karatsuba(X_R, Y_R)

    P = karatsuba(
            X_L + X_R,
            Y_L + Y_R
        )

    Z_M = P - Z_L - Z_R

    return Z_L
         + shift_digits(Z_M, m)
         + shift_digits(Z_R, 2*m)
```

Here:

```text
shift_digits(V, k)
```

means:

```text
V * B^k
```

implemented as a positional shift in the digit representation.

---

# 29. Important Implementation Details

## 29.1 Same split width for both numbers

Use the same:

```text
m
q = B^m
```

for `X` and `Y`.

Do not independently split each number based on its own length.

The algebraic combine formula assumes the same positional shift.

---

## 29.2 Store Z_L and Z_R

Correct:

```text
Z_L = karatsuba(...)
Z_R = karatsuba(...)
P   = karatsuba(...)

Z_M = P - Z_L - Z_R
```

Do not recompute:

```text
Z_L
Z_R
```

inside the middle-term expression.

That would add recursive calls and destroy the intended optimization.

---

## 29.3 Use exact arithmetic

Do not use floating-point `pow()` to create exact positional shifts in large-integer arithmetic.

And remember:

```cpp
^
```

in C++ means:

```text
bitwise XOR
```

not exponentiation.

---

## 29.4 Use a practical cutoff

Real Karatsuba implementations usually switch back to schoolbook multiplication for small sizes.

Why?

Because recursion, temporary allocations, and additions have overhead.

Karatsuba becomes useful only once the operands are sufficiently large.

---

# 30. Common Mistakes

## Mistake 1 — Thinking "divide and conquer = faster"

False.

The four-product version gives:

```text
T(n) = 4T(n/2) + O(n)
```

which is still:

```text
Θ(n²)
```

Always count recursive calls.

---

## Mistake 2 — Saying the middle block uses one multiplication

The expression:

```text
X_LY_R + X_RY_L
```

contains **two** multiplications.

Named expressions are not the same as operation counts.

---

## Mistake 3 — Forgetting the algebra derivation

Do not memorize only:

```text
P - Z_L - Z_R
```

Understand why:

```text
P
=
(X_L+X_R)(Y_L+Y_R)
```

contains:

```text
Z_L + middle + Z_R
```

---

## Mistake 4 — Using different split widths

The split must use the same:

```text
q = B^m
```

for both operands.

---

## Mistake 5 — Assuming even digit count

Odd lengths are valid.

Use:

```text
m = floor(n/2)
```

and the same reconstruction identities.

---

## Mistake 6 — Using B^n for the high block

The general high shift is:

```text
B^(2m)
```

For odd `n`:

```text
2m != n
```

---

## Mistake 7 — Treating low value's visible digit count as its width

Example:

```text
1203
```

with `m=2` has low block value:

```text
3
```

but it still occupies two decimal positions conceptually:

```text
03
```

---

## Mistake 8 — Concatenating result blocks

Karatsuba combines by:

```text
shift + addition
```

not string concatenation.

---

## Mistake 9 — Using fixed-width integers for arbitrary precision

If the problem is truly about huge numbers, every intermediate value must be represented exactly.

---

## Mistake 10 — Calling the optimization memoization

Karatsuba is not caching repeated states.

It changes the algebra so that two cross-products never need to be computed individually.

This is:

```text
algebraic reuse
```

not:

```text
memoization
```

---

# 31. Recognition Checklist

When you see an expensive divide-and-conquer recurrence, ask:

### 1. What is the recursive contract?

For Karatsuba:

```text
multiply X and Y exactly
```

### 2. How do I divide?

```text
X = low + shift*high
Y = low + shift*high
```

### 3. How many recursive calls does the obvious expansion create?

```text
4
```

### 4. What recurrence does that produce?

```text
4T(n/2) + O(n)
```

### 5. Can some child results be reconstructed algebraically?

Yes.

The cross-term sum:

```text
X_LY_R + X_RY_L
```

can be recovered from:

```text
(X_L+X_R)(Y_L+Y_R)
```

plus already-known products.

### 6. What does this save?

```text
4 recursive multiplications
→
3 recursive multiplications
```

### 7. Does the saving happen only once?

No.

It happens at every recursive level.

That is why the exponent changes.

---

# 32. Final Mental Model

Karatsuba should not be remembered as merely:

```text
split each number into two halves
```

The important chain is:

```text
1. Split:
   X = X_L + B^m X_R
   Y = Y_L + B^m Y_R

2. Naive expansion needs four products:
   X_LY_L
   X_LY_R
   X_RY_L
   X_RY_R

3. Notice we only need the SUM of the two cross products.

4. Compute:
   P = (X_L + X_R)(Y_L + Y_R)

5. Recover:
   Z_M = P - Z_L - Z_R

6. Therefore only three recursive multiplications remain.

7. Combine with positional shifts.
```

ASCII summary:

```text
                         X * Y
                           |
                         SPLIT
                           |
         +-----------------+-----------------+
         |                 |                 |
      X_LY_L           X_RY_R      (X_L+X_R)(Y_L+Y_R)
         |                 |                 |
        Z_L               Z_R                P
         \                 |                 /
          \                |                /
           +---------------+---------------+
                           |
                  Z_M = P-Z_L-Z_R
                           |
                         COMBINE
                           |
      Z_L + B^m*Z_M + B^(2m)*Z_R
                           |
                          X*Y
```

The reusable Divide-and-Conquer lesson is:

```text
Do not only ask:

"Can I split the problem?"

Also ask:

"Do I really need every recursive answer separately,
or can some required information be reconstructed
from fewer recursive calls?"
```

For Karatsuba:

```text
4 calls → Θ(n²)
3 calls → Θ(n^1.585)
```

That one missing recursive call changes the asymptotic complexity.
