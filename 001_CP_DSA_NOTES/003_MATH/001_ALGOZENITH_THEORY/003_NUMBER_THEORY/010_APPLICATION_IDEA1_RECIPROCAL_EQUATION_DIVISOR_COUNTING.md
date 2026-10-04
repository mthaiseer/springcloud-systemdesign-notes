# Application Idea 1 — Reciprocal Equation → Factorization → Divisor Counting

> **Problem:** For a fixed positive integer \(n\), count integer pairs \((a,b)\) satisfying
>
$$ > \frac{1}{a}+\frac{1}{b}=\frac{1}{n} > $$
> **Don't memorize the final identity.** Model the algebra until the equation becomes a product, then recognize divisor counting.

---

## Table of Contents

1. [Problem Model](#1-problem-model)
2. [Preliminary — Adding Fractions](#2-preliminary--adding-fractions)
3. [Transform the Equation](#3-transform-the-equation)
4. [Why Add n²?](#4-why-add-n²)
5. [Convert to a Divisor Problem](#5-convert-to-a-divisor-problem)
6. [Full Dry Run — n = 2](#6-full-dry-run--n--2)
7. [Positive vs Integer Solutions](#7-positive-vs-integer-solutions)
8. [Count Directly from Prime Factorization](#8-count-directly-from-prime-factorization)
9. [C++ Implementation](#9-c-implementation)
10. [Complexity](#10-complexity)
11. [Final Recognition Model](#11-final-recognition-model)

---

# 1. Problem Model

We are given:

$$ \frac{1}{a}+\frac{1}{b}=\frac{1}{n} $$

Instead of trying possible values of \(a\) and \(b\), our target is:

$$ (\text{expression containing }a) (\text{expression containing }b) = \text{constant} $$

because an equation of the form

$$ xy=K $$

can be solved by considering the divisors of \(K\).

---

# 2. Preliminary — Adding Fractions

Start with:

$$ \frac{1}{a}+\frac{1}{b} $$

The common denominator is \(ab\).

Convert each fraction:

$$ \frac{1}{a} = \frac{b}{ab} $$

and

$$ \frac{1}{b} = \frac{a}{ab} $$

Therefore:

$$ \begin{aligned} \frac{1}{a}+\frac{1}{b} &= \frac{b}{ab}+\frac{a}{ab}\\ &= \frac{a+b}{ab} \end{aligned} $$

So the original equation becomes:

$$ \frac{a+b}{ab}=\frac{1}{n} $$

Cross multiply:

$$ n(a+b)=ab $$

Expand:

$$ na+nb=ab $$

Move everything to one side:

$$ ab-na-nb=0 $$

Now the goal is to factor this expression.

---

# 3. Transform the Equation

We currently have:

$$ ab-na-nb=0 $$

Look at the expansion:

$$ \begin{aligned} (a-n)(b-n) &=a(b-n)-n(b-n)\\ &=ab-an-nb+n^2 \end{aligned} $$

Compare:

$$ ab-an-nb $$

with:

$$ ab-an-nb+n^2 $$

The missing term is exactly:

$$ n^2 $$

So add \(n^2\) to **both sides**:

$$ ab-na-nb+n^2=n^2 $$

Now factor the left side:

$$ \begin{aligned} ab-na-nb+n^2 &=a(b-n)-n(b-n)\\ &=(a-n)(b-n) \end{aligned} $$

Therefore:

$$ \boxed{(a-n)(b-n)=n^2} $$

This is the key identity.

---

# 4. Why Add \(n^2\)?

This is not a random trick.

Suppose you see:

$$ xy-cx-cy $$

We want to recognize:

$$ (x-c)(y-c) $$

Expand it:

$$ \begin{aligned} (x-c)(y-c) &=xy-cx-cy+c^2 \end{aligned} $$

So the original expression is missing \(c^2\).

Hence:

$$ xy-cx-cy=0 $$

Add \(c^2\) to both sides:

$$ xy-cx-cy+c^2=c^2 $$

Factor:

$$ \boxed{(x-c)(y-c)=c^2} $$

For our problem:

$$ x=a,\qquad y=b,\qquad c=n $$

so:

$$ \boxed{(a-n)(b-n)=n^2} $$

### Recognition

```text
ab - na - nb
      |
      v
almost (a-n)(b-n)
      |
      v
missing +n²
      |
      v
add n² to both sides
      |
      v
(a-n)(b-n) = n²
```

---

# 5. Convert to a Divisor Problem

Define:

$$ x=a-n $$

and

$$ y=b-n $$

Then:

$$ xy=n^2 $$

Now the algebra problem has become a factor-pair problem.

For every positive divisor \(d\mid n^2\), choose:

$$ x=d $$

Then:

$$ y=\frac{n^2}{d} $$

Since:

$$ a=x+n,\qquad b=y+n $$

we obtain:

$$ \boxed{ a=n+d,\qquad b=n+\frac{n^2}{d} } $$

Therefore every positive divisor of \(n^2\) produces one ordered positive pair \((a,b)\).

Hence:

$$ \boxed{\text{positive ordered pairs}=\tau(n^2)} $$

where \(\tau(m)\) means the number of positive divisors of \(m\).

---

# 6. Full Dry Run — \(n=2\)

Solve:

$$ \frac1a+\frac1b=\frac12 $$

Transform:

$$ (a-2)(b-2)=2^2 $$

Therefore:

$$ (a-2)(b-2)=4 $$

Positive divisors of \(4\):

$$ 1,\;2,\;4 $$

## Divisor \(d=1\)

$$ a-2=1 $$

so:

$$ a=3 $$

and:

$$ b-2=\frac41=4 $$

so:

$$ b=6 $$

Pair:

$$ (3,6) $$

Check:

$$ \begin{aligned} \frac13+\frac16 &=\frac26+\frac16\\ &=\frac36\\ &=\frac12 \end{aligned} $$

## Divisor \(d=2\)

$$ a-2=2 $$

$$ a=4 $$

and:

$$ b-2=\frac42=2 $$

$$ b=4 $$

Pair:

$$ (4,4) $$

## Divisor \(d=4\)

$$ a-2=4 $$

$$ a=6 $$

and:

$$ b-2=\frac44=1 $$

$$ b=3 $$

Pair:

$$ (6,3) $$

Thus:

$$ (3,6),\;(4,4),\;(6,3) $$

and:

$$ \boxed{\text{answer}=3} $$

This agrees with:

$$ \tau(2^2)=\tau(4)=3 $$

---

# 7. Positive vs Integer Solutions

## Positive integers \(a,b\)

Positive divisors of \(n^2\) give all positive solutions.

Therefore:

$$ \boxed{\text{answer}=\tau(n^2)} $$

## All nonzero integers \(a,b\)

Since:

$$ xy=n^2>0 $$

we can have:

$$ x>0,\;y>0 $$

or:

$$ x<0,\;y<0 $$

Thus positive and negative divisors initially give:

$$ 2\tau(n^2) $$

factor pairs.

But:

$$ x=-n,\qquad y=-n $$

gives:

$$ a=x+n=0 $$

and:

$$ b=y+n=0 $$

The original fractions \(1/a\) and \(1/b\) are undefined at zero.

Therefore this one pair must be removed:

$$ \boxed{\text{nonzero integer ordered pairs}=2\tau(n^2)-1} $$

---

# 8. Count Directly from Prime Factorization

Suppose:

$$ n=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k} $$

Then:

$$ n^2 = p_1^{2e_1} \times p_2^{2e_2} \times \cdots \times p_k^{2e_k} $$

Recall:

$$ \tau\left(p_1^{a_1} \times p_2^{a_2} \times \cdots \times p_k^{a_k}\right) = (a_1+1)(a_2+1)\cdots(a_k+1) $$

Therefore:

$$ \boxed{\tau(n^2) = (2e_1+1)(2e_2+1)\cdots(2e_k+1)} $$

## Example — \(n=12\)

Factorize:

$$ 12=2^2\times3^1 $$

Therefore:

$$ 12^2=2^4\times3^2 $$

Number of divisors:

$$ \begin{aligned} \tau(12^2) &=(4+1)(2+1)\\ &=5\times3\\ &=15 \end{aligned} $$

Hence:

$$ \boxed{15} $$

positive ordered pairs satisfy:

$$ \frac1a+\frac1b=\frac1{12} $$

Notice that we do not actually need to construct and factorize \(n^2\).  
Factorize \(n\), double each exponent, and apply the divisor-count formula.

---

# 9. C++ Implementation

## Positive Ordered Pairs

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

        // In n², exponent becomes 2 * exponent.
        // Number of choices = 2 * exponent + 1.
        ans *= (2LL * exponent + 1);
    }

    // Remaining prime has exponent 1 in n,
    // therefore exponent 2 in n²:
    // choices = 2 + 1 = 3.
    if (n > 1) {
        ans *= 3;
    }

    return ans;
}
```

### Dry Run — `n = 12`

```text
12 = 2² × 3¹

prime 2:
e = 2
contribution = 2e+1
             = 5

prime 3:
e = 1
contribution = 2e+1
             = 3

answer = 5 × 3
       = 15
```

## All Nonzero Integer Ordered Pairs

```cpp
long long countIntegerPairs(long long n) {
    long long positive = countPositivePairs(n);

    return 2 * positive - 1;
}
```

---

# 10. Complexity

Trial-division factorization of \(n\):

$$ O(\sqrt n) $$

The divisor count is then obtained directly from the prime exponents.

We do **not** need to:

```text
try every a
try every b
construct n²
enumerate every divisor
```

---

# 11. Final Recognition Model

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
         ab-na-nb = 0
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

## Mental Model

```text
Fractions
    ↓
common denominator
    ↓
cross multiply
    ↓
complete the product
    ↓
factor-pair equation
    ↓
count divisors
```

## Final Formulas

For positive ordered pairs:

$$ \boxed{\text{answer}=\tau(n^2)} $$

If:

$$ n=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k} $$

then:

$$ \boxed{ \text{answer} = \prod_{i=1}^{k}(2e_i+1) } $$

For all nonzero integer ordered pairs:

$$ \boxed{ \text{answer}=2\tau(n^2)-1 } $$

> **Don't memorize**
>
$$ > (a-n)(b-n)=n^2 > $$
>
> as a magic formula. Recognize that
>
$$ > ab-na-nb > $$
>
> is almost the expansion of
>
$$ > (a-n)(b-n) > $$
>
> and is missing exactly \(n^2\).
