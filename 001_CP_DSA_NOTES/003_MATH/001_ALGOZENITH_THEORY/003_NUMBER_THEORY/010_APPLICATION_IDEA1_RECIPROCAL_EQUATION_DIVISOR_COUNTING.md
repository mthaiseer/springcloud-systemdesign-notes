# Application Idea 1 — Reciprocal Equation → Factorization → Divisor Counting

> **Problem:** For fixed positive integer `n`, count integer pairs `(a,b)` satisfying
>
> ```text
> 1/a + 1/b = 1/n
> ```
>
> **Core model:** Do not try values of `a` and `b`. Transform the equation until the variables appear as a **product**, then count divisors.

---

## Table of Contents

1. [What Is the Problem Asking?](#1-what-is-the-problem-asking)
2. [Preliminary — Adding Fractions](#2-preliminary--adding-fractions)
3. [Transform the Equation](#3-transform-the-equation)
4. [Why Add n²?](#4-why-add-n)
5. [Convert to a Divisor Problem](#5-convert-to-a-divisor-problem)
6. [Full Example — n = 2](#6-full-example--n--2)
7. [Positive vs Integer Solutions](#7-positive-vs-integer-solutions)
8. [Count Without Building n²](#8-count-without-building-n)
9. [C++ Implementation](#9-c-implementation)
10. [Complexity](#10-complexity)
11. [Final Recognition Model](#11-final-recognition-model)

---

# 1. What Is the Problem Asking?

We need pairs `(a,b)` such that:

```text
1/a + 1/b = 1/n
```

At first this looks like a fraction problem with two unknowns.

The useful target is:

```text
something involving a
×
something involving b
=
constant
```

Why?

Because an equation such as:

```text
x × y = K
```

naturally becomes a **divisor counting** problem.

---

# 2. Preliminary — Adding Fractions

For:

```text
1/a + 1/b
```

the common denominator is `ab`.

Convert each fraction:

```text
1/a = b/(ab)

1/b = a/(ab)
```

Therefore:

```text
1/a + 1/b

= b/(ab) + a/(ab)

= (a+b)/(ab)
```

So the original equation becomes:

```text
(a+b)/(ab) = 1/n
```

Cross multiply:

```text
n(a+b) = ab
```

Expand:

```text
na + nb = ab
```

Move everything:

```text
ab - na - nb = 0
```

Now we need to factor this expression.

---

# 3. Transform the Equation

Start:

```text
ab - na - nb = 0
```

We want something resembling:

```text
(a - n)(b - n)
```

Expand that expression separately:

```text
(a-n)(b-n)

= ab - an - bn + n²
```

Our current expression already contains:

```text
ab - an - bn
```

It is missing only:

```text
+n²
```

So add `n²` to both sides:

```text
ab - na - nb       = 0

ab - na - nb + n²  = n²
```

Now factor:

```text
ab - na - nb + n²
```

Group:

```text
a(b-n) - n(b-n)
```

Take `(b-n)` common:

```text
(a-n)(b-n)
```

Therefore:

```text
(a-n)(b-n) = n²
```

This is the key transformation.

---

# 4. Why Add `n²`?

This is not a random trick.

We recognize the pattern:

```text
xy - cx - cy
```

and want:

```text
(x-c)(y-c)
```

Expand:

```text
(x-c)(y-c)
= xy - cx - cy + c²
```

So:

```text
xy - cx - cy = 0
```

can be completed by adding `c²`:

```text
xy - cx - cy + c² = c²
```

giving:

```text
(x-c)(y-c) = c²
```

For this problem:

```text
x → a
y → b
c → n
```

Hence:

```text
ab - na - nb = 0

↓

(a-n)(b-n) = n²
```

### Recognition pattern

```text
ab - na - nb
      |
      v
looks almost like
(a-n)(b-n)
      |
      v
missing +n²
      |
      v
add n² to both sides
```

---

# 5. Convert to a Divisor Problem

Let:

```text
x = a - n
y = b - n
```

Then:

```text
xy = n²
```

Now every factor pair of `n²` gives a solution.

If `d` divides `n²`, choose:

```text
x = d

y = n²/d
```

Convert back:

```text
a = n + d

b = n + n²/d
```

So each divisor `d` gives one **ordered** pair `(a,b)`.

Therefore, for positive `a,b`:

```text
number of ordered pairs
=
number of positive divisors of n²
=
tau(n²)
```

---

# 6. Full Example — `n = 2`

Solve:

```text
1/a + 1/b = 1/2
```

Transform:

```text
(a-2)(b-2) = 2²
```

So:

```text
(a-2)(b-2) = 4
```

Positive divisors of `4`:

```text
1, 2, 4
```

Use each divisor as `a-2`.

### d = 1

```text
a - 2 = 1
b - 2 = 4/1 = 4

a = 3
b = 6
```

Pair:

```text
(3,6)
```

Check:

```text
1/3 + 1/6
= 2/6 + 1/6
= 3/6
= 1/2
```

### d = 2

```text
a - 2 = 2
b - 2 = 4/2 = 2

a = 4
b = 4
```

Pair:

```text
(4,4)
```

### d = 4

```text
a - 2 = 4
b - 2 = 4/4 = 1

a = 6
b = 3
```

Pair:

```text
(6,3)
```

Therefore:

```text
positive ordered pairs:

(3,6)
(4,4)
(6,3)

answer = 3
```

And:

```text
tau(2²)
= tau(4)
= 3
```

---

# 7. Positive vs Integer Solutions

This distinction matters.

## Case 1 — Positive integer `a,b`

From:

```text
(a-n)(b-n) = n² > 0
```

positive solutions use the **positive divisors** of `n²`.

Therefore:

```text
answer = tau(n²)
```

---

## Case 2 — All integer `a,b`

For every positive divisor pair:

```text
(x,y)
```

there is also:

```text
(-x,-y)
```

because:

```text
(-x)(-y) = xy = n²
```

So mathematically there are:

```text
2 × tau(n²)
```

factor pairs for `(x,y)`.

However, the original equation contains:

```text
1/a and 1/b
```

so `a = 0` or `b = 0` is invalid.

The negative divisor choice:

```text
x = -n
y = -n
```

gives:

```text
a = x+n = 0
b = y+n = 0
```

which is not allowed.

Therefore, if the problem truly allows **all nonzero integer** `a,b`:

```text
answer = 2 × tau(n²) - 1
```

> Always check whether the problem asks for **positive integer pairs** or **all nonzero integer pairs**.

---

# 8. Count Without Building `n²`

Suppose:

```text
n = p1^e1 × p2^e2 × ... × pk^ek
```

Then:

```text
n²
= p1^(2e1) × p2^(2e2) × ... × pk^(2ek)
```

Recall the divisor-count formula:

```text
If X = p1^a1 × p2^a2 × ...,

tau(X) = (a1+1)(a2+1)...
```

Therefore:

```text
tau(n²)
=
(2e1+1)(2e2+1)...(2ek+1)
```

## Example — `n = 12`

Factorize:

```text
12 = 2² × 3¹
```

Square the exponents:

```text
12² = 2⁴ × 3²
```

Therefore:

```text
tau(12²)

= (4+1)(2+1)

= 5 × 3

= 15
```

So:

```text
1/a + 1/b = 1/12
```

has:

```text
15 positive ordered pairs
```

No need to calculate or factor `144` separately.

---

# 9. C++ Implementation

## Positive Ordered Pairs

Factorize `n` directly and use:

```text
tau(n²) = product(2e + 1)
```

```cpp
long long countPositivePairs(long long n) {
    long long ans = 1;

    for (long long p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;

        int exponent = 0;

        while (n % p == 0) {
            n /= p;
            ++exponent;
        }

        ans *= (2LL * exponent + 1);
    }

    // One prime factor remains with exponent 1.
    if (n > 1) {
        ans *= 3;  // 2*1 + 1
    }

    return ans;
}
```

### Dry run — `n = 12`

```text
12 = 2² × 3¹

for p = 2:
exponent = 2
ans *= 2×2+1
ans = 5

remaining prime = 3
exponent = 1

ans *= 2×1+1
ans = 5×3
ans = 15
```

---

## All Nonzero Integer Ordered Pairs

```cpp
long long countIntegerPairs(long long n) {
    long long positive = countPositivePairs(n);

    return 2 * positive - 1;
}
```

The `-1` removes the invalid pair:

```text
(a,b) = (0,0)
```

generated by:

```text
(a-n, b-n) = (-n,-n)
```

---

# 10. Complexity

Using trial division to factorize `n`:

```text
O(sqrt(n))
```

After factorization, divisor counting is proportional only to the number of distinct prime factors.

We do **not** need to:

```text
enumerate a
enumerate b
build n²
enumerate every divisor
```

if only the count is required.

---

# 11. Final Recognition Model

When you see:

```text
1/a + 1/b = 1/n
```

think:

```text
      1/a + 1/b = 1/n
               |
               v
         (a+b)/ab = 1/n
               |
               v
          n(a+b) = ab
               |
               v
        ab - na - nb = 0
               |
               v
       add n² to both sides
               |
               v
      (a-n)(b-n) = n²
               |
               v
          product = constant
               |
               v
          divisor counting
```

## One-line mental model

```text
Fractions
→ cross multiply
→ complete the product
→ factor pair
→ count divisors
```

## Formula Sheet

For positive ordered pairs:

```text
answer = tau(n²)
```

If:

```text
n = p1^e1 × p2^e2 × ... × pk^ek
```

then:

```text
answer
=
(2e1+1)(2e2+1)...(2ek+1)
```

For all **nonzero integer ordered pairs**:

```text
answer = 2 × tau(n²) - 1
```

> **Don't memorize `(a-n)(b-n)=n²` as a magic trick.** Recognize the algebraic pattern: `ab - na - nb` is almost the expansion of `(a-n)(b-n)`; it is missing exactly `n²`.
