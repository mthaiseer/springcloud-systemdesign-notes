# Math for CP Beginner — Modulo, GCD & LCM

> **Goal:** Build the mathematical model behind modulo, modular arithmetic, GCD, LCM, and their common properties.
>
> **Don't memorize formulas first.** Understand what each operation means, derive the formula with a small example, then remember the recognition pattern.

---

## Table of Contents

0. [Math Preliminaries](#0-math-preliminaries)
   - [Variables and Subscripts](#01-variables-and-subscripts)
   - [Distributive Law](#02-distributive-law)
   - [Factoring a Common Factor](#03-factoring-a-common-factor)
   - [Division Algorithm](#04-division-algorithm)
   - [Multiples of M Modulo M](#05-multiples-of-m-modulo-m)
   - [Congruence](#06-congruence)
1. [Modulo — Preliminary Model](#1-modulo--preliminary-model)
2. [Why Modulo Creates Cycles](#2-why-modulo-creates-cycles)
3. [Modular Addition](#3-modular-addition)
4. [Modular Subtraction](#4-modular-subtraction)
5. [Modular Multiplication](#5-modular-multiplication)
6. [Modulo in Large Computations](#6-modulo-in-large-computations)
7. [GCD — Meaning](#7-gcd--meaning)
8. [Euclidean Algorithm](#8-euclidean-algorithm)
9. [Why Euclidean Algorithm Works](#9-why-euclidean-algorithm-works)
10. [LCM — Meaning](#10-lcm--meaning)
11. [Prime-Exponent Model of GCD and LCM](#11-prime-exponent-model-of-gcd-and-lcm)
12. [Why GCD × LCM = A × B](#12-why-gcd--lcm--a--b)
13. [Common GCD/LCM Properties](#13-common-gcdlcm-properties)
14. [OEIS Recognition Tool](#14-oeis-recognition-tool)
15. [Final Recognition Sheet](#15-final-recognition-sheet)

---


# 0. Math Preliminaries

Before modular arithmetic, these small algebra ideas make every derivation easier.

## 0.1 Variables and Subscripts

A symbol such as \(q_1\) means **q subscript 1**. It is simply a variable name.

For example:

\[
A=q_1M+r_1
\]

means:

- \(A\) = original number
- \(M\) = modulus/divisor
- \(q_1\) = quotient
- \(r_1\) = remainder

Similarly:

\[
B=q_2M+r_2
\]

The subscripts `1` and `2` only distinguish the quotient/remainder belonging to \(A\) from those belonging to \(B\).

---

## 0.2 Distributive Law

Basic rule:

\[
a(b+c)=ab+ac
\]

For two brackets:

\[
(a+b)(c+d)
\]

Expand the first term:

\[
= a(c+d)+b(c+d)
\]

Distribute again:

\[
= ac+ad+bc+bd
\]

### Example

\[
(2+3)(4+5)
\]

\[
=2(4+5)+3(4+5)
\]

\[
=2\cdot4+2\cdot5+3\cdot4+3\cdot5
\]

\[
=8+10+12+15
\]

\[
=45
\]

This exact algebra is used later when expanding:

\[
(q_1M+r_1)(q_2M+r_2)
\]

---

## 0.3 Factoring a Common Factor

Factoring is the reverse of distribution.

Start with:

\[
Ma+Mb+Mc
\]

Every term contains \(M\).

Take \(M\) outside:

\[
Ma+Mb+Mc=M(a+b+c)
\]

### Example

\[
3x+3y+3z
\]

\[
=3(x+y+z)
\]

This matters in modulo because any expression of the form:

\[
M\times(\text{integer})
\]

is completely divisible by \(M\).

---

## 0.4 Division Algorithm

When positive integer \(A\) is divided by positive integer \(M\):

\[
A=qM+r
\]

where:

\[
0\le r<M
\]

Here:

- \(q\) = quotient
- \(r\) = remainder

Therefore:

\[
A\bmod M=r
\]

### Example — \(17\div5\)

\[
17=3\cdot5+2
\]

So:

\[
q=3
\]

and:

\[
r=2
\]

Hence:

\[
17\bmod5=2
\]

Mental model:

```text
A = complete groups of M + remainder
  = q × M                + r
```

---

## 0.5 Multiples of \(M\) Modulo \(M\)

Any multiple of \(M\) has remainder `0` when divided by \(M\).

\[
M\bmod M=0
\]

\[
2M\bmod M=0
\]

\[
3M\bmod M=0
\]

More generally:

\[
(kM)\bmod M=0
\]

for integer \(k\).

Example:

\[
20=4\cdot5
\]

Therefore:

\[
20\bmod5=0
\]

This is the key reason complete multiples of \(M\) can disappear in modular derivations.

---

## 0.6 Congruence

Notation:

\[
A\equiv B\pmod M
\]

means:

> \(A\) and \(B\) have the same remainder when divided by \(M\).

Example:

\[
17\bmod5=2
\]

and:

\[
7\bmod5=2
\]

Therefore:

\[
17\equiv7\pmod5
\]

Another way to see it:

\[
17-7=10
\]

and \(10\) is divisible by \(5\).

So:

\[
A\equiv B\pmod M
\]

also means:

\[
M\mid(A-B)
\]

---

# 1. Modulo — Preliminary Model

For positive integers:

```text
A % M
```

means:

> Divide `A` by `M` and keep the **remainder**.

The division algorithm says:

$$
A = qM + r
$$

where:

$$
0 \le r < M
$$

The remainder `r` is:

$$
\boxed{A \bmod M}
$$

## Example — `17 % 5`

Divide:

```text
17 = 3 × 5 + 2
```

Therefore:

$$
17\bmod5=2
$$

Visual model:

```text
17 objects
|
| remove groups of 5
v

5 + 5 + 5 + 2
            ^
            |
         remainder

17 % 5 = 2
```

---

# 2. Why Modulo Creates Cycles

Take:

```text
M = 3
```

Then:

```text
0 % 3 = 0
1 % 3 = 1
2 % 3 = 2
3 % 3 = 0
4 % 3 = 1
5 % 3 = 2
6 % 3 = 0
```

So the remainder pattern is:

```text
0 1 2 | 0 1 2 | 0 1 2 | ...
```

Modulo `M` always produces a value in:

$$
\boxed{0,1,2,\ldots,M-1}
$$

## Clock Model

A 12-hour clock is modulo 12.

```text
13 o'clock -> 1 o'clock
```

because:

$$
13\bmod12=1
$$

The course uses this repeating-cycle idea as the basic intuition for modulo.

---

## Even / Odd

Modulo 2 gives only:

```text
0 or 1
```

Therefore:

```cpp
n % 2 == 0   // even
n % 2 == 1   // odd, for positive n
```

Example:

```text
14 % 2 = 0 -> even
15 % 2 = 1 -> odd
```

---

# 3. Modular Addition

Formula:

$$
\boxed{(A+B)\bmod M=((A\bmod M)+(B\bmod M))\bmod M}
$$

## Why?

Write:

$$
A=q_1M+r_1
$$

and:

$$
B=q_2M+r_2
$$

where:

$$
r_1=A\bmod M
$$

$$
r_2=B\bmod M
$$

Add them:

$$
A+B=(q_1M+r_1)+(q_2M+r_2)
$$

Group the multiples of `M`:

$$
A+B=(q_1+q_2)M+(r_1+r_2)
$$

The first part:

$$
(q_1+q_2)M
$$

is completely divisible by `M`, so it contributes remainder `0`.

Therefore only:

$$
r_1+r_2
$$

matters.

Hence:

$$
(A+B)\bmod M=(r_1+r_2)\bmod M
$$

Substitute:

$$
r_1=A\bmod M
$$

$$
r_2=B\bmod M
$$

Therefore:

$$
\boxed{(A+B)\bmod M=((A\bmod M)+(B\bmod M))\bmod M}
$$

---

## Example — `7 + 5`, modulo `4`

Direct method:

$$
(7+5)\bmod4
$$

First:

$$
7+5=12
$$

Then:

$$
12\bmod4=0
$$

Now reduce first:

$$
7\bmod4=3
$$

$$
5\bmod4=1
$$

Add the remainders:

$$
3+1=4
$$

Apply modulo again:

$$
4\bmod4=0
$$

Therefore:

$$
(7+5)\bmod4
=
((7\bmod4)+(5\bmod4))\bmod4
=
0
$$

### Why the final `% M`?

Because:

```text
A % M < M
B % M < M
```

but their sum can be:

```text
>= M
```

Example:

```text
3 + 3 = 6
```

for `M = 4`.

So we normalize again:

```text
6 % 4 = 2
```

---

## C++

```cpp
long long addMod(long long a, long long b, long long mod) {
    return ((a % mod) + (b % mod)) % mod;
}
```

---

# 4. Modular Subtraction

Formula:

$$
\boxed{(A-B)\bmod M=((A\bmod M)-(B\bmod M)+M)\bmod M}
$$

The important new problem is:

```text
subtraction can become negative
```

## Example — `9 - 11`, modulo `3`

Normal subtraction:

$$
9-11=-2
$$

We want the equivalent remainder in:

$$
[0,M-1]
$$

For `M = 3`:

```text
valid residues = 0, 1, 2
```

Start:

$$
-2
$$

Add one complete modulus:

$$
-2+3=1
$$

Therefore:

$$
-2\equiv1\pmod3
$$

because the two numbers differ by `3`.

Step by step:

$$
9\bmod3=0
$$

$$
11\bmod3=2
$$

Subtract:

$$
0-2=-2
$$

Add `M`:

$$
-2+3=1
$$

Normalize:

$$
1\bmod3=1
$$

Hence:

$$
(9-11)\bmod3=1
$$

in the normalized modular range.

---

## Why adding `M` does not change the residue

Suppose:

$$
x\bmod M=r
$$

Then:

$$
x=qM+r
$$

Add `M`:

$$
x+M=qM+r+M
$$

$$
x+M=(q+1)M+r
$$

The remainder is still:

$$
r
$$

Therefore:

$$
\boxed{x\equiv x+M\pmod M}
$$

---

## Safe C++ Pattern

```cpp
long long subMod(long long a, long long b, long long mod) {
    return ((a % mod - b % mod) + mod) % mod;
}
```

For more general expressions where the intermediate value may be below `-mod`, normalize with:

```cpp
x = ((x % mod) + mod) % mod;
```

---

# 5. Modular Multiplication

Formula:

\[
\boxed{(A\times B)\bmod M
=
\big((A\bmod M)(B\bmod M)\big)\bmod M}
\]

The formula becomes easy once we use the **division algorithm + distributive law + common-factor extraction**.

## Step 1 — Write each number as quotient × modulus + remainder

From the division algorithm:

\[
A=q_1M+r_1
\]

and:

\[
B=q_2M+r_2
\]

where:

\[
r_1=A\bmod M
\]

and:

\[
r_2=B\bmod M
\]

So:

```text
A = complete multiples of M + remainder r1
B = complete multiples of M + remainder r2
```

---

## Step 2 — Multiply \(A\) and \(B\)

Substitute their representations:

\[
AB=(q_1M+r_1)(q_2M+r_2)
\]

Do not jump directly to the expanded expression.

Use:

\[
(a+b)(c+d)=ac+ad+bc+bd
\]

Map the terms:

```text
a = q1M
b = r1
c = q2M
d = r2
```

Therefore:

\[
AB
=
(q_1M)(q_2M)
+
(q_1M)(r_2)
+
(r_1)(q_2M)
+
(r_1)(r_2)
\]

Simplify each multiplication:

\[
AB
=
q_1q_2M^2
+
q_1Mr_2
+
q_2Mr_1
+
r_1r_2
\]

---

## Step 3 — Find the common factor \(M\)

Look at the first three terms:

\[
q_1q_2M^2
+
q_1Mr_2
+
q_2Mr_1
\]

Each contains at least one \(M\).

Rewrite:

\[
q_1q_2M^2=M(q_1q_2M)
\]

\[
q_1Mr_2=M(q_1r_2)
\]

\[
q_2Mr_1=M(q_2r_1)
\]

Therefore:

\[
AB
=
M(q_1q_2M)
+
M(q_1r_2)
+
M(q_2r_1)
+
r_1r_2
\]

Factor out \(M\):

\[
AB
=
M(q_1q_2M+q_1r_2+q_2r_1)
+
r_1r_2
\]

This now has exactly the familiar form:

\[
\text{number}=M\times(\text{integer})+\text{remainder part}
\]

---

## Step 4 — Apply modulo \(M\)

The first part is a complete multiple of \(M\):

\[
M(q_1q_2M+q_1r_2+q_2r_1)
\]

Therefore its remainder modulo \(M\) is:

\[
0
\]

So only:

\[
r_1r_2
\]

can affect the final remainder.

Hence:

\[
AB\bmod M=(r_1r_2)\bmod M
\]

Recall:

\[
r_1=A\bmod M
\]

and:

\[
r_2=B\bmod M
\]

Substitute:

\[
\boxed{
(A\times B)\bmod M
=
\big((A\bmod M)(B\bmod M)\big)\bmod M
}
\]

---

## Numerical Dry Run — \(17\times13\bmod5\)

### Step 1 — Divide each number by \(5\)

For \(17\):

\[
17=3\cdot5+2
\]

Therefore:

\[
q_1=3,\qquad r_1=2
\]

For \(13\):

\[
13=2\cdot5+3
\]

Therefore:

\[
q_2=2,\qquad r_2=3
\]

So:

\[
17=(3\cdot5)+2
\]

\[
13=(2\cdot5)+3
\]

### Step 2 — Multiply

\[
17\cdot13
=
(3\cdot5+2)(2\cdot5+3)
\]

Expand:

\[
=(3\cdot5)(2\cdot5)
 +(3\cdot5)(3)
 +(2)(2\cdot5)
 +(2)(3)
\]

\[
=150+45+20+6
\]

\[
=221
\]

### Step 3 — Separate multiples of \(5\)

\[
221=215+6
\]

\[
=5\cdot43+6
\]

But `6` is still larger than the valid remainder range \(0\) to \(4\):

\[
6=5+1
\]

Therefore:

\[
221=5\cdot44+1
\]

Hence:

\[
221\bmod5=1
\]

Using only the remainders gives the same result:

\[
r_1r_2=2\cdot3=6
\]

\[
6\bmod5=1
\]

Therefore:

\[
\boxed{17\cdot13\bmod5=1}
\]

---

## C++

```cpp
long long mulMod(long long a, long long b, long long mod) {
    return ((a % mod) * (b % mod)) % mod;
}
```

If `(a % mod) * (b % mod)` may overflow `long long`, use a wider intermediate:

```cpp
long long mulMod(long long a, long long b, long long mod) {
    return (__int128)(a % mod) * (b % mod) % mod;
}
```

## Recognition Model

```text
A = q1*M + r1
B = q2*M + r2
        |
        v
multiply the two expressions
        |
        v
expand using distributive law
        |
        v
all terms except r1*r2 contain M
        |
        v
multiples of M -> remainder 0
        |
        v
only r1*r2 matters
        |
        v
(A*B) mod M = (r1*r2) mod M
```

---

# 6. Modulo in Large Computations

A common CP instruction is:

```text
Print the answer modulo 1e9+7.
```

The source uses factorial as the motivating example.

Instead of first constructing a gigantic factorial:

```text
1 × 2 × 3 × ... × 100
```

we can keep reducing after multiplication.

```cpp
const long long MOD = 1'000'000'007;

long long ans = 1;

for (long long i = 1; i <= 100; ++i) {
    ans = (ans * i) % MOD;
}
```

The mathematical reason is modular multiplication:

$$
(ab)\bmod M
=
((a\bmod M)(b\bmod M))\bmod M
$$

So after each multiplication we only need the current remainder.

Mental model:

```text
huge arithmetic expression
        |
        v
problem asks answer mod M
        |
        v
reduce during valid +, -, × operations
        |
        v
keep numbers manageable
```

> Modular **division** is different and is intentionally outside the scope of these beginner notes.

---

# 7. GCD — Meaning

The **Greatest Common Divisor** of `A` and `B` is the largest positive integer dividing both numbers.

Example:

```text
A = 8
B = 12
```

Divisors of `8`:

```text
1, 2, 4, 8
```

Divisors of `12`:

```text
1, 2, 3, 4, 6, 12
```

Common divisors:

```text
1, 2, 4
```

Largest:

```text
4
```

Therefore:

$$
\boxed{\gcd(8,12)=4}
$$

---

# 8. Euclidean Algorithm

The source first explains GCD using repeated subtraction:

$$
\boxed{\gcd(A,B)=\gcd(A-B,B)}
$$

when:

$$
A\ge B
$$

## Example — `gcd(12,8)`

Start:

```text
(12,8)
```

Subtract `8` from `12`:

```text
(4,8)
```

Reorder:

```text
(8,4)
```

Subtract:

```text
(4,4)
```

Subtract:

```text
(0,4)
```

Therefore:

$$
\gcd(12,8)=4
$$

But repeated subtraction can be slow.

Instead of subtracting `B` repeatedly, modulo performs all those subtractions in one operation.

Example:

$$
12\bmod8=4
$$

So:

$$
\gcd(12,8)=\gcd(8,4)
$$

Then:

$$
8\bmod4=0
$$

Therefore:

$$
\boxed{\gcd(12,8)=4}
$$

---

## C++ — Euclidean Algorithm

```cpp
long long gcdEuclid(long long a, long long b) {
    while (b != 0) {
        long long r = a % b;
        a = b;
        b = r;
    }

    return a;
}
```

Or simply:

```cpp
long long g = std::gcd(a, b);
```

Complexity:

$$
O(\log(\min(A,B)))
$$

---

# 9. Why Euclidean Algorithm Works

Suppose:

$$
\gcd(A,B)=G
$$

Then `G` divides both:

$$
G\mid A
$$

and:

$$
G\mid B
$$

So we can write:

$$
A=aG
$$

$$
B=bG
$$

Subtract:

$$
A-B=aG-bG
$$

Factor `G`:

$$
A-B=(a-b)G
$$

Therefore `G` also divides:

$$
A-B
$$

So subtracting one number from the other preserves the common divisor structure.

Hence:

$$
\boxed{\gcd(A,B)=\gcd(A-B,B)}
$$

Repeated subtraction is compressed by:

$$
A=qB+r
$$

where:

$$
r=A\bmod B
$$

Thus:

$$
\boxed{\gcd(A,B)=\gcd(B,A\bmod B)}
$$

This is the Euclidean Algorithm used in CP.

---

# 10. LCM — Meaning

The **Least Common Multiple** of `A` and `B` is the smallest positive integer divisible by both.

Example:

```text
A = 8
B = 12
```

Multiples of `8`:

```text
8, 16, 24, 32, ...
```

Multiples of `12`:

```text
12, 24, 36, ...
```

First common multiple:

```text
24
```

Therefore:

$$
\boxed{\operatorname{lcm}(8,12)=24}
$$

For two positive integers:

$$
\boxed{
\operatorname{lcm}(A,B)
=
\frac{A\times B}{\gcd(A,B)}
}
$$

In code, prefer:

```cpp
long long lcm(long long a, long long b) {
    return (a / std::gcd(a, b)) * b;
}
```

rather than:

```cpp
a * b / gcd(a,b)
```

because dividing first can reduce overflow risk.

---

# 11. Prime-Exponent Model of GCD and LCM

This is the cleanest model.

Any positive integer can be represented by prime powers:

$$
N=p_1^{e_1}p_2^{e_2}\cdots p_k^{e_k}
$$

Suppose:

$$
A=2^3\times5^2\times7^1\times11^2
$$

and:

$$
B=2^5\times5^3\times7^2\times11^1
$$

For each prime, compare exponents.

| Prime | exponent in `A` | exponent in `B` | GCD takes | LCM takes |
|---:|---:|---:|---:|---:|
| 2 | 3 | 5 | `min = 3` | `max = 5` |
| 5 | 2 | 3 | `min = 2` | `max = 3` |
| 7 | 1 | 2 | `min = 1` | `max = 2` |
| 11 | 2 | 1 | `min = 1` | `max = 2` |

Therefore:

$$
\gcd(A,B)
=
2^3\times5^2\times7^1\times11^1
$$

and:

$$
\operatorname{lcm}(A,B)
=
2^5\times5^3\times7^2\times11^2
$$

## Why GCD takes `min`

A common divisor must divide **both** numbers.

For prime `2`:

```text
A contains 2³
B contains 2⁵
```

Can the GCD contain `2⁴`?

No:

```text
2⁴ does not divide A
```

So the largest exponent common to both is:

$$
\min(3,5)=3
$$

Therefore GCD uses the **minimum exponent**.

---

## Why LCM takes `max`

The LCM must be divisible by **both** numbers.

Again:

```text
A contains 2³
B contains 2⁵
```

To be divisible by `B`, the LCM needs at least:

$$
2^5
$$

So:

$$
\max(3,5)=5
$$

Therefore LCM uses the **maximum exponent**.

Mental model:

```text
GCD -> what can BOTH share?
       take MIN exponent

LCM -> what is enough to contain BOTH?
       take MAX exponent
```

---

# 12. Why GCD × LCM = A × B

For two positive integers:

$$
\boxed{
\gcd(A,B)\times\operatorname{lcm}(A,B)=A\times B
}
$$

The prime-exponent model makes this easy.

Suppose a prime `p` has:

```text
exponent a in A
exponent b in B
```

Then its exponent in:

$$
A\times B
$$

is:

$$
a+b
$$

In:

$$
\gcd(A,B)
$$

the exponent is:

$$
\min(a,b)
$$

In:

$$
\operatorname{lcm}(A,B)
$$

the exponent is:

$$
\max(a,b)
$$

Multiply GCD and LCM.

The resulting exponent of `p` is:

$$
\min(a,b)+\max(a,b)
$$

But for any two values:

$$
\min(a,b)+\max(a,b)=a+b
$$

Therefore every prime has exactly the same exponent on both sides.

Hence:

$$
\boxed{
\gcd(A,B)\operatorname{lcm}(A,B)=AB
}
$$

---

## Small Example — `8` and `12`

Factorize:

$$
8=2^3
$$

$$
12=2^2\times3
$$

GCD:

$$
\gcd(8,12)=2^{\min(3,2)}=2^2=4
$$

LCM:

$$
\operatorname{lcm}(8,12)
=
2^{\max(3,2)}\times3
$$

$$
=2^3\times3
$$

$$
=24
$$

Now:

$$
\gcd(8,12)\times\operatorname{lcm}(8,12)
$$

$$
=4\times24
$$

$$
=96
$$

and:

$$
8\times12=96
$$

So:

$$
4\times24=8\times12
$$

---

# 13. Common GCD/LCM Properties

## Property 1 — Consecutive integers are coprime

$$
\boxed{\gcd(A,A+1)=1}
$$

### Why?

Suppose some number `d` divides both:

$$
d\mid A
$$

and:

$$
d\mid(A+1)
$$

Then `d` must divide their difference:

$$
(A+1)-A=1
$$

So:

$$
d\mid1
$$

The only positive divisor of `1` is:

$$
1
$$

Therefore:

$$
\boxed{\gcd(A,A+1)=1}
$$

Example:

```text
gcd(10,11) = 1
gcd(24,25) = 1
gcd(100,101) = 1
```

---

## Property 2 — GCD is associative

$$
\boxed{
\gcd(A,B,C)
=
\gcd(A,\gcd(B,C))
}
$$

So an array GCD can be accumulated:

```cpp
long long g = 0;

for (long long x : a) {
    g = std::gcd(g, x);
}
```

---

## Property 3 — LCM is associative

Similarly:

$$
\boxed{
\operatorname{lcm}(A,B,C)
=
\operatorname{lcm}(A,\operatorname{lcm}(B,C))
}
$$

So:

```cpp
long long l = 1;

for (long long x : a) {
    l = std::lcm(l, x);
}
```

subject to overflow constraints.

---

## Property 4 — GCD cannot exceed the smaller number

$$
\boxed{
\gcd(A,B)\le\min(A,B)
}
$$

Why?

A common divisor must divide the smaller number.

A divisor of a positive integer cannot be larger than the number itself.

Example:

```text
A = 12
B = 18

gcd = 6
min(A,B) = 12

6 <= 12
```

---

## Property 5 — Prime exponent representation

If:

$$
A=\prod p^{a_p}
$$

and:

$$
B=\prod p^{b_p}
$$

then:

$$
\boxed{
\gcd(A,B)=\prod p^{\min(a_p,b_p)}
}
$$

and:

$$
\boxed{
\operatorname{lcm}(A,B)=\prod p^{\max(a_p,b_p)}
}
$$

This is one of the most useful number-theory recognition models in CP.

---

# 14. OEIS Recognition Tool

The course also introduces the **Online Encyclopedia of Integer Sequences (OEIS)**.

The idea is:

```text
problem
   |
   v
brute force small N
   |
   v
generate sequence
   |
   v
recognize/search sequence
   |
   v
discover possible formula/pattern
```

Example workflow:

```text
N = 1 -> 1
N = 2 -> 3
N = 3 -> 6
N = 4 -> 10
N = 5 -> 15
```

You may recognize:

$$
1,3,6,10,15,\ldots
$$

as triangular numbers:

$$
\frac{N(N+1)}{2}
$$

In contests, sequence lookup can help with **pattern discovery**, but you should still understand or prove the formula before relying on it.

---

# 15. Final Recognition Sheet

## Modulo

When you see:

```text
x % M
```

think:

```text
x = qM + r
         |
         v
       x % M
```

with:

$$
0\le r<M
$$

---

## Modular Addition

$$
\boxed{
(A+B)\bmod M
=
((A\bmod M)+(B\bmod M))\bmod M
}
$$

Mental model:

```text
throw away complete groups of M
        |
        v
only remainders matter
```

---

## Modular Subtraction

$$
\boxed{
(A-B)\bmod M
=
((A\bmod M)-(B\bmod M)+M)\bmod M
}
$$

Mental model:

```text
subtract residues
      |
      v
negative?
      |
      v
add M
      |
      v
normalize with % M
```

---

## Modular Multiplication

$$
\boxed{
(A\times B)\bmod M
=
((A\bmod M)(B\bmod M))\bmod M
}
$$

Mental model:

```text
reduce factors
     |
     v
multiply residues
     |
     v
reduce again
```

---

## GCD

When you see:

```text
largest number dividing both
```

think:

```text
GCD
 |
 v
Euclidean Algorithm
 |
 v
gcd(a,b) = gcd(b,a%b)
```

---

## LCM

When you see:

```text
smallest number divisible by both
```

think:

```text
LCM
 |
 v
A / gcd(A,B) * B
```

---

## Prime-Exponent Model

```text
prime p:

A contains p^a
B contains p^b

        |
   +----+----+
   |         |
  GCD       LCM
   |         |
 min(a,b)  max(a,b)
```

---

## Master Model

```text
             NUMBER THEORY
                   |
        +----------+----------+
        |                     |
      MODULO                DIVISIBILITY
        |                     |
  +-----+-----+           +---+---+
  |     |     |           |       |
 add   sub   mul         GCD     LCM
  |     |     |           |       |
reduce residues        min exp  max exp
        |                  \       /
        |                   \     /
        |                    \   /
        |                 GCD × LCM
        |                      |
        |                      v
        |                    A × B
        |
        v
large computations modulo M
```

> **Core habit:** Whenever you see a number-theory formula, ask:
>
> 1. What does it mean?
> 2. Can I express the numbers as quotient + remainder or prime powers?
> 3. What survives after removing complete multiples?
> 4. Can I verify it with one small example?
>
> That model is more useful in contests than memorizing isolated formulas.
