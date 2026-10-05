# Combinatorics & Probability — Level 3
## Combinatorics 2 — Stars & Bars, Independent Choices & Matching

> **Goal:** make every formula easy to reconstruct from the story instead of memorizing it.
>
> **Study flow:** **notation → concept simplified → derivation → example → C++ / implementation idea → recognition model**.
>
> **Math rendering:** display equations use fenced `math` blocks only to avoid Markdown/LaTeX rendering issues.
>
> **Lecture scope:** Stars & Bars, non-negative/positive/lower-bounded integer solutions, Distributing Apples, independent choices, subset counting, and the matching/counting idea used in *The Intriguing Obsession*.

---

# Clickable Table of Contents

- [0. Mathematical Notation & Prerequisites](#0-mathematical-notation--prerequisites)
  - [0.1 Combination Notation](#01-combination-notation)
  - [0.2 Factorial](#02-factorial)
  - [0.3 Variables x_i](#03-variables-x_i)
  - [0.4 Sigma Notation](#04-sigma-notation)
  - [0.5 Product Rule — Independent Choices](#05-product-rule--independent-choices)
  - [0.6 Identical vs Distinct](#06-identical-vs-distinct)
- [1. Stars & Bars — Core Idea](#1-stars--bars--core-idea)
- [2. Non-Negative Integer Solutions](#2-non-negative-integer-solutions)
- [3. Positive Integer Solutions](#3-positive-integer-solutions)
- [4. Lower-Bounded Integer Solutions](#4-lower-bounded-integer-solutions)
- [5. Distributing Apples](#5-distributing-apples)
- [6. At Most M Objects](#6-at-most-m-objects)
- [7. Independent Choices](#7-independent-choices)
- [8. Counting Non-Empty Subsets](#8-counting-non-empty-subsets)
- [9. Sum of Powers of Two](#9-sum-of-powers-of-two)
- [10. Matching Two Groups](#10-matching-two-groups)
- [11. The Intriguing Obsession — Three Independent Pairings](#11-the-intriguing-obsession--three-independent-pairings)
- [12. Practice Problems Mentioned in the Lecture](#12-practice-problems-mentioned-in-the-lecture)
- [13. Technique Selection](#13-technique-selection)
- [14. Final Don't-Memorize Model](#14-final-dont-memorize-model)

---

# 0. Mathematical Notation & Prerequisites

Before Stars & Bars, make the notation visual.

---

## 0.1 Combination Notation

Notation:

```math
\binom{n}{r}
```

Read:

```text
"n choose r"
```

Meaning:

```text
number of ways to choose r positions/items
from n positions/items
when order does not matter
```

Formula:

```math
\binom{n}{r}
=
\frac{n!}{r!(n-r)!}
```

### Example

Choose `2` people from `4`:

```math
\binom42
=
\frac{4!}{2!2!}
=
6
```

Possible pairs:

```text
AB
AC
AD
BC
BD
CD
```

### Why this matters here

Stars & Bars eventually becomes:

```text
choose positions for bars
```

So the final answer naturally becomes a binomial coefficient.

---

## 0.2 Factorial

Notation:

```math
n!
```

Read:

```text
"n factorial"
```

Meaning:

```math
n!=n(n-1)(n-2)\cdots2\cdot1
```

Example:

```math
5!
=
5\cdot4\cdot3\cdot2\cdot1
=
120
```

Special value:

```math
0!=1
```

---

## 0.3 Variables x_i

Notation:

```math
x_1,x_2,\ldots,x_k
```

Read:

```text
x1, x2, ..., xk
```

In distribution problems:

```text
x_i = number of objects placed in container i
```

Example with `3` boxes:

```text
x1 = objects in box 1
x2 = objects in box 2
x3 = objects in box 3
```

If total objects are `5`:

```math
x_1+x_2+x_3=5
```

This equation is just another way of saying:

```text
distribute 5 identical objects
among 3 distinct boxes
```

---

## 0.4 Sigma Notation

Notation:

```math
\sum_{i=1}^{k}x_i
```

Read:

```text
"sum x_i from i = 1 to k"
```

Meaning:

```math
x_1+x_2+\cdots+x_k
```

So:

```math
\sum_{i=1}^{k}x_i=n
```

means:

```text
the amounts in all k boxes add up to n
```

---

### Example

```math
\sum_{i=1}^{3}x_i=7
```

means:

```math
x_1+x_2+x_3=7
```

---

## 0.5 Product Rule — Independent Choices

Suppose:

```text
Choice A has x possibilities
Choice B has y possibilities
```

and the choices are independent.

Then total possibilities:

```math
x\cdot y
```

For three independent choices:

```math
x\cdot y\cdot z
```

### Example

```text
3 shirts
2 trousers
```

Outfits:

```text
3 × 2 = 6
```

This multiplication principle is used later when three independent bridge/matching choices are combined.

---

## 0.6 Identical vs Distinct

This distinction is critical.

### Identical objects

Objects cannot be distinguished.

Example:

```text
5 identical apples
```

Swapping two apples creates no new arrangement.

---

### Distinct containers

Containers are different.

Example:

```text
child 1
child 2
child 3
```

Distribution:

```text
(2,1,0)
```

is different from:

```text
(1,2,0)
```

because different children receive the apples.

---

# 1. Stars & Bars — Core Idea

## 1.1 Concept Simplified

Imagine:

```text
n identical stars
```

representing objects.

To split them among:

```text
k distinct boxes
```

we insert:

```text
k-1 bars
```

as separators.

---

### Example — 3 objects, 3 boxes

Stars:

```text
* * *
```

Need:

```text
2 bars
```

One arrangement:

```text
* | | * *
```

means:

```text
box 1 → 1
box 2 → 0
box 3 → 2
```

So this string represents:

```text
(1,0,2)
```

Another:

```text
| * * | *
```

represents:

```text
(0,2,1)
```

---

## 1.2 Why k-1 Bars?

To create `k` groups:

```text
group 1 | group 2 | ... | group k
```

we need separators between consecutive groups.

Number of separators:

```text
k-1
```

Example:

```text
3 boxes
→ 2 separators
```

---

## 1.3 Derivation

Total symbols:

```text
n stars
+
k-1 bars
```

Therefore:

```text
n+k-1 symbols
```

Choose which positions contain the bars:

```math
\binom{n+k-1}{k-1}
```

Equivalently, choose star positions:

```math
\binom{n+k-1}{n}
```

By symmetry:

```math
\binom{n+k-1}{k-1}
=
\binom{n+k-1}{n}
```

---

## 1.4 Example — 3 Identical Objects, 3 Boxes

Formula:

```math
\binom{3+3-1}{3-1}
=
\binom52
=
10
```

So there are:

```text
10 distributions
```

Examples include:

```text
(3,0,0)
(2,1,0)
(2,0,1)
(1,2,0)
(1,1,1)
...
```

---

## 1.5 C++

Assume we already have an `nCr()` helper:

```cpp
long long starsAndBars(
    int objects,
    int boxes,
    const Combinatorics& comb
) {
    return comb.nCr(objects + boxes - 1, boxes - 1);
}
```

---

## 1.6 Recognition Model

```text
identical objects
+
distinct boxes
+
boxes may be empty
        |
        v
stars = objects
bars  = separators
        |
        v
n stars + (k-1) bars
        |
        v
choose bar positions
        |
        v
C(n+k-1, k-1)
```

**Memory anchor:** `k` boxes need `k-1` separators.

---

# 2. Non-Negative Integer Solutions

We now express Stars & Bars algebraically.

---

## 2.1 What It Asks

Count solutions of:

```math
x_1+x_2+\cdots+x_k=n
```

with:

```math
x_i\ge0
```

Notation:

```math
x_i\ge0
```

means:

```text
every variable may be 0 or larger
```

---

## 2.2 Concept Simplified

Interpret:

```text
n = number of identical objects
k = number of distinct boxes
x_i = objects in box i
```

Since:

```text
x_i may be 0
```

a box is allowed to be empty.

Therefore this is exactly the basic Stars & Bars model.

---

## 2.3 Formula

```math
x_1+x_2+\cdots+x_k=n,
\qquad x_i\ge0
```

Number of solutions:

```math
\binom{n+k-1}{k-1}
```

---

## 2.4 Step-by-Step Example

Count:

```math
x_1+x_2+x_3=4
```

with:

```math
x_1,x_2,x_3\ge0
```

Objects:

```text
4 stars
```

Boxes:

```text
3
```

Bars:

```text
2
```

Total positions:

```text
4 + 2 = 6
```

Choose the `2` bar positions:

```math
\binom62=15
```

Answer:

```text
15
```

---

## 2.5 Recognition Model

```text
sum of k variables = n
all variables >= 0
        |
        v
basic Stars & Bars
        |
        v
C(n+k-1, k-1)
```

---

# 3. Positive Integer Solutions

Now every variable must receive at least `1`.

---

## 3.1 What It Asks

Count:

```math
x_1+x_2+\cdots+x_k=n
```

with:

```math
x_i\ge1
```

Meaning:

```text
every box must contain at least one object
```

---

## 3.2 Concept Simplified — Give Everyone One First

The condition:

```text
x_i >= 1
```

is annoying because ordinary Stars & Bars expects:

```text
>= 0
```

So give one object to every box first.

Define:

```math
x_i=1+y_i
```

Then:

```math
y_i\ge0
```

---

## 3.3 Derivation

Start:

```math
x_1+x_2+\cdots+x_k=n
```

Substitute:

```math
x_i=1+y_i
```

Then:

```math
(1+y_1)+(1+y_2)+\cdots+(1+y_k)=n
```

There are `k` ones:

```math
k+y_1+y_2+\cdots+y_k=n
```

So:

```math
y_1+y_2+\cdots+y_k=n-k
```

with:

```math
y_i\ge0
```

Apply basic Stars & Bars:

```math
\binom{(n-k)+k-1}{k-1}
```

Simplify:

```math
\binom{n-1}{k-1}
```

---

## 3.4 Example

Count positive solutions:

```math
x_1+x_2+x_3=7
```

Each variable:

```text
>= 1
```

Give one to each variable:

```text
used = 3
```

Remaining:

```text
7-3 = 4
```

Now solve:

```math
y_1+y_2+y_3=4
```

with:

```text
y_i >= 0
```

Answer:

```math
\binom{4+3-1}{2}
=
\binom62
=
15
```

So:

```text
15 positive solutions
```

---

## 3.5 C++

```cpp
long long positiveSolutions(
    int total,
    int variables,
    const Combinatorics& comb
) {
    if (total < variables)
        return 0;

    return comb.nCr(total - 1, variables - 1);
}
```

---

## 3.6 Recognition Model

```text
x_i >= 1
      |
      v
reserve 1 for every variable
      |
      v
subtract k from total
      |
      v
remaining variables >= 0
      |
      v
basic Stars & Bars
```

**Memory anchor:** positive lower bound `1` → pre-give one to everyone.

---

# 4. Lower-Bounded Integer Solutions

This generalizes the previous section.

---

## 4.1 What It Asks

Count:

```math
x_1+x_2+\cdots+x_k=n
```

subject to:

```math
x_i\ge a_i
```

Each variable may have a different minimum.

---

## 4.2 Concept Simplified — Pay the Minimum First

For each variable:

```text
minimum requirement = a_i
```

Give that amount first.

Define:

```math
x_i=a_i+y_i
```

Then:

```math
y_i\ge0
```

---

## 4.3 Derivation

Substitute:

```math
(a_1+y_1)+(a_2+y_2)+\cdots+(a_k+y_k)=n
```

Move all fixed lower bounds:

```math
y_1+y_2+\cdots+y_k
=
n-\sum_{i=1}^{k}a_i
```

Define remaining amount:

```math
R=n-\sum_{i=1}^{k}a_i
```

Then:

```math
y_1+\cdots+y_k=R,
\qquad y_i\ge0
```

Number of solutions:

```math
\binom{R+k-1}{k-1}
```

Therefore:

```math
\binom{
n-\sum a_i+k-1
}{
k-1
}
```

provided:

```text
n >= sum(a_i)
```

Otherwise:

```text
0 solutions
```

---

## 4.4 Step-by-Step Example

Count:

```math
x_1+x_2+x_3=5
```

with:

```text
x1 >= 2
x2 >= 0
x3 >= 1
```

Lower bounds:

```text
a1 = 2
a2 = 0
a3 = 1
```

Total minimum already required:

```text
2 + 0 + 1 = 3
```

Remaining:

```text
5 - 3 = 2
```

Define:

```text
x1 = 2 + y1
x2 = 0 + y2
x3 = 1 + y3
```

Then:

```math
y_1+y_2+y_3=2
```

with all:

```text
>= 0
```

Answer:

```math
\binom{2+3-1}{2}
=
\binom42
=
6
```

---

## 4.5 C++

```cpp
long long lowerBoundSolutions(
    int total,
    const vector<int>& lower,
    const Combinatorics& comb
) {
    long long required = 0;

    for (int x : lower)
        required += x;

    if (required > total)
        return 0;

    int k = (int)lower.size();
    int remaining = total - required;

    return comb.nCr(remaining + k - 1, k - 1);
}
```

---

## 4.6 Recognition Model

```text
x_i >= a_i
      |
      v
give a_i to variable i first
      |
      v
remaining total
= n - sum(a_i)
      |
      v
all new variables >= 0
      |
      v
Stars & Bars
```

**Memory anchor:** arbitrary lower bounds → subtract all compulsory amounts first.

---

# 5. Distributing Apples

The lecture uses **Distributing Apples** as a direct Stars & Bars application.

---

## 5.1 Concept Simplified

Suppose:

```text
m identical apples
n distinct children
```

A child may receive:

```text
0 or more apples
```

Let:

```text
x_i = apples received by child i
```

Then:

```math
x_1+x_2+\cdots+x_n=m
```

with:

```math
x_i\ge0
```

So:

```text
identical objects = m apples
distinct boxes    = n children
```

---

## 5.2 Formula

```math
\binom{m+n-1}{n-1}
```

Equivalent:

```math
\binom{m+n-1}{m}
```

---

## 5.3 Example — 3 Apples, 2 Children

Equation:

```math
x_1+x_2=3
```

Possible distributions:

```text
(0,3)
(1,2)
(2,1)
(3,0)
```

Count:

```text
4
```

Formula:

```math
\binom{3+2-1}{2-1}
=
\binom41
=
4
```

---

## 5.4 C++

```cpp
long long distributeApples(
    int apples,
    int children,
    const Combinatorics& comb
) {
    return comb.nCr(apples + children - 1, children - 1);
}
```

---

## 5.5 Recognition Model

```text
identical apples
+
distinct children
+
zero allowed
        |
        v
Stars & Bars
```

---

# 6. At Most M Objects

Sometimes the total is not exactly fixed.

Example form:

```text
distribute at most M identical objects
among k distinct boxes
```

---

## 6.1 Concept Simplified

"At most `M`" means the total used may be:

```text
0
1
2
...
M
```

For exactly `s` objects:

```math
\binom{s+k-1}{k-1}
```

So total:

```math
\sum_{s=0}^{M}
\binom{s+k-1}{k-1}
```

---

## 6.2 Hockey-Stick Identity

This sum simplifies to:

```math
\sum_{s=0}^{M}
\binom{s+k-1}{k-1}
=
\binom{M+k}{k}
```

---

## 6.3 Example — At Most 2 Objects Into 3 Boxes

### Use 0 objects

```math
\binom{0+2}{2}
=
1
```

### Use 1 object

```math
\binom{1+2}{2}
=
3
```

### Use 2 objects

```math
\binom{2+2}{2}
=
6
```

Total:

```text
1 + 3 + 6 = 10
```

Identity:

```math
\binom{2+3}{3}
=
\binom53
=
10
```

---

## 6.4 Recognition Model

```text
at most M objects
      |
      v
sum exact totals 0..M
      |
      v
sum of Stars & Bars terms
      |
      v
hockey-stick identity
```

---

# 7. Independent Choices

The lecture then moves to counting where decisions can be made independently.

---

## 7.1 Concept Simplified

If one decision does not restrict another decision:

```text
multiply the numbers of possibilities
```

This is the multiplication principle.

---

## 7.2 Example — Binary Choices

Suppose every one of `n` positions can independently be:

```text
0 or 1
```

Each position:

```text
2 choices
```

Total:

```text
2 × 2 × ... × 2
```

`n` times.

Therefore:

```math
2^n
```

---

### Example for n = 3

Binary strings:

```text
000
001
010
011
100
101
110
111
```

Count:

```text
8 = 2³
```

---

## 7.3 General Product Rule

If decisions have:

```text
c1 choices
c2 choices
...
ck choices
```

and are independent:

```math
\text{total}
=
c_1c_2\cdots c_k
```

---

## 7.4 Recognition Model

```text
choice 1 does not affect choice 2
choice 2 does not affect choice 3
...
        |
        v
independent choices
        |
        v
multiply counts
```

**Memory anchor:** independent stages → multiply.

---

# 8. Counting Non-Empty Subsets

This is one of the lecture's independent-choice examples.

---

## 8.1 Concept Simplified

For every array element:

```text
two choices:
1. include it
2. exclude it
```

For `n` elements:

```math
2^n
```

subsets total.

This includes:

```text
the empty subset
```

So non-empty subsets:

```math
2^n-1
```

---

## 8.2 Example — n = 3

Set:

```text
{A,B,C}
```

All subsets:

```text
{}
{A}
{B}
{C}
{A,B}
{A,C}
{B,C}
{A,B,C}
```

Total:

```text
8
```

Non-empty:

```text
7
```

Formula:

```math
2^3-1=7
```

---

## 8.3 C++

```cpp
long long nonEmptySubsets(int n) {
    return (1LL << n) - 1;
}
```

Use this exact bit shift only when `n` is small enough for the integer type.

For large `n` under modulo:

```cpp
long long ans =
    (binpow(2, n, MOD) - 1 + MOD) % MOD;
```

---

## 8.4 Recognition Model

```text
subset
      |
      v
each element:
take / don't take
      |
      v
2 choices independently
      |
      v
2^n total
      |
      v
subtract empty subset
```

---

# 9. Sum of Powers of Two

The lecture also illustrates the geometric sum:

```math
2^0+2^1+\cdots+2^n
```

---

## 9.1 Formula

```math
\sum_{i=0}^{n}2^i
=
2^{n+1}-1
```

---

## 9.2 Why?

Let:

```math
S=1+2+4+\cdots+2^n
```

Multiply by `2`:

```math
2S=2+4+8+\cdots+2^{n+1}
```

Subtract:

```math
2S-S
=
2^{n+1}-1
```

So:

```math
S=2^{n+1}-1
```

---

## 9.3 Example

For:

```text
n = 3
```

```math
2^0+2^1+2^2+2^3
=
1+2+4+8
=
15
```

Formula:

```math
2^4-1
=
16-1
=
15
```

---

## 9.4 Recognition Model

```text
1 + 2 + 4 + ... + 2^n
        |
        v
geometric progression
        |
        v
2^(n+1)-1
```

---

# 10. Matching Two Groups

This section builds the exact counting block used later for bridge construction.

Suppose there are:

```text
a nodes in group A
b nodes in group B
```

and we want exactly:

```text
i non-overlapping bridges
```

where each selected node is used once.

---

## 10.1 Step 1 — Choose Nodes From A

Choose `i` endpoints:

```math
\binom ai
```

---

## 10.2 Step 2 — Choose Nodes From B

Choose `i` endpoints:

```math
\binom bi
```

---

## 10.3 Step 3 — Pair the Chosen Nodes

Suppose chosen nodes are:

```text
A1, A2, ..., Ai
```

and:

```text
B1, B2, ..., Bi
```

For `A1`:

```text
i choices
```

For `A2`:

```text
i-1 choices
```

...

So pairings:

```math
i!
```

---

## 10.4 Formula for Exactly i Bridges

```math
\binom ai
\binom bi
i!
```

Possible values of `i`:

```text
0 ... min(a,b)
```

because we cannot use more nodes than exist in the smaller group.

Therefore total matchings between the two groups:

```math
F(a,b)
=
\sum_{i=0}^{\min(a,b)}
\binom ai
\binom bi
i!
```

---

## 10.5 Step-by-Step Example — a = 2, b = 2

### 0 bridges

```math
\binom20\binom20 0!
=
1
```

### 1 bridge

```math
\binom21\binom21 1!
=
2\cdot2
=
4
```

### 2 bridges

```math
\binom22\binom22 2!
=
1\cdot1\cdot2
=
2
```

Total:

```text
1 + 4 + 2 = 7
```

So:

```math
F(2,2)=7
```

---

## 10.6 C++

```cpp
long long pairWays(
    int a,
    int b,
    const Combinatorics& comb
) {
    long long ans = 0;

    for (int i = 0; i <= min(a, b); ++i) {
        long long ways =
            comb.nCr(a, i);

        ways =
            (__int128)ways
            * comb.nCr(b, i)
            % comb.MOD;

        ways =
            (__int128)ways
            * comb.fact[i]
            % comb.MOD;

        ans += ways;

        if (ans >= comb.MOD)
            ans -= comb.MOD;
    }

    return ans;
}
```

Complexity:

```text
O(min(a,b))
```

after factorial/nCr precomputation.

---

## 10.7 Recognition Model

```text
build i disjoint pairs
between group A and group B
        |
        v
choose i from A
        |
        v
choose i from B
        |
        v
pair them in i! ways
        |
        v
C(a,i) C(b,i) i!
```

**Memory anchor:** choose left endpoints × choose right endpoints × match them.

---

# 11. The Intriguing Obsession — Three Independent Pairings

The lecture's bridge diagrams split the construction into pairings between color groups.

Suppose group sizes are:

```text
a, b, c
```

For any two groups define:

```math
F(x,y)
=
\sum_{i=0}^{\min(x,y)}
\binom xi
\binom yi
i!
```

---

## 11.1 Concept Simplified

There are three color-pair relationships:

```text
A ↔ B
B ↔ C
C ↔ A
```

The lecture treats these as independent counting events.

So:

```text
ways for A-B
×
ways for B-C
×
ways for C-A
```

---

## 11.2 Formula

```math
\text{answer}
=
F(a,b)\cdot F(b,c)\cdot F(c,a)
```

under the required modulus.

---

## 11.3 Example — a = b = c = 1

First:

```math
F(1,1)
=
\binom10\binom10 0!
+
\binom11\binom11 1!
```

```text
= 1 + 1
= 2
```

The three pair types each have:

```text
2 possibilities
```

Therefore:

```text
2 × 2 × 2
= 8
```

This small example demonstrates the lecture's "independent events → multiply" idea.

---

## 11.4 C++

```cpp
long long solveThreeGroups(
    int a,
    int b,
    int c,
    const Combinatorics& comb
) {
    long long ab = pairWays(a, b, comb);
    long long bc = pairWays(b, c, comb);
    long long ca = pairWays(c, a, comb);

    return (__int128)ab * bc % comb.MOD * ca % comb.MOD;
}
```

---

## 11.5 Complexity

Pair calculations:

```text
O(min(a,b))
+
O(min(b,c))
+
O(min(c,a))
```

after factorial precomputation.

---

## 11.6 Recognition Model

```text
complex construction
        |
        v
split into independent pair types
        |
        v
count each pair type separately
        |
        v
multiply results
```

Within each pair type:

```text
choose endpoints
×
pair endpoints
```

---

# 12. Practice Problems Mentioned in the Lecture

The lecture explicitly lists these practice directions.

## Stars & Bars

```text
Distributing Apples
Array
```

Use these to practice:

```text
exact total
positive constraints
lower bounds
at-most constraints
```

---

## Independent Choices

```text
Lucky Numbers
Number of subsets of an array with at least 1 element
Monotonic Remuneration
The Intriguing Obsession
```

Recognition target:

```text
Can the problem be split into independent decisions?
```

---

## Additional Homework / Practice

```text
Anagrams
Right Triangles
Count Ways to Make Array With Product
```

For these titles, the lecture page lists them as further problems; their full statements are not developed in the supplied slides, so this note does not invent missing problem details.

---

# 13. Technique Selection

| Problem signal | Think |
|---|---|
| identical objects into distinct boxes | Stars & Bars |
| `x1 + ... + xk = n`, `xi >= 0` | `C(n+k-1,k-1)` |
| `xi >= 1` | give each variable `1` first |
| `xi >= ai` | subtract all lower bounds |
| distribute apples among children | Stars & Bars |
| "at most M" total objects | sum exact totals / hockey-stick |
| independent decisions | multiply counts |
| subset of `n` items | `2^n` |
| non-empty subset | `2^n - 1` |
| `1+2+4+...+2^n` | geometric sum |
| match `i` nodes from two groups | `C(a,i)C(b,i)i!` |
| several independent matching types | multiply their totals |

---

# 14. Final Don't-Memorize Model

```text
STARS & BARS
------------
n identical objects
k distinct boxes
empty allowed

n stars
k-1 bars

total positions:
n+k-1

choose bars:
C(n+k-1, k-1)


NON-NEGATIVE EQUATION
---------------------
x1 + ... + xk = n
xi >= 0

→ basic Stars & Bars


POSITIVE EQUATION
-----------------
xi >= 1

give everyone 1 first

remaining:
n-k

→ Stars & Bars


GENERAL LOWER BOUND
-------------------
xi >= ai

set:
xi = ai + yi

remaining:
n - Σai

yi >= 0

→ Stars & Bars


INDEPENDENT CHOICES
-------------------
choice counts:
c1, c2, ..., ck

total:
c1 × c2 × ... × ck


SUBSETS
-------
each element:
pick / don't pick

2 choices each

total:
2^n

non-empty:
2^n - 1


PAIR MATCHING
-------------
exactly i pairs:

choose i from left
×
choose i from right
×
pair them

C(a,i) C(b,i) i!


THREE GROUPS
------------
count AB
count BC
count CA

independent
→ multiply
```

> **Final memory anchor:**  
> **Stars & Bars = distribute identical objects. Independent choices = multiply. Matching = choose endpoints, then pair them.**
