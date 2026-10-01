# Permutations — CP Foundation

> **Goal:** Count ordered arrangements. Use permutations when **changing the order creates a different outcome**.

---

## Table of Contents

1. [What Is a Permutation?](#1-what-is-a-permutation)
2. [Arranging All N Distinct Objects](#2-arranging-all-n-distinct-objects)
3. [Arranging R Objects from N](#3-arranging-r-objects-from-n)
4. [Why the nPr Formula Works](#4-why-the-npr-formula-works)
5. [Example — 5-Letter Password](#5-example--5-letter-password)
6. [Permutations with Repeated Elements](#6-permutations-with-repeated-elements)
7. [Recognition Patterns](#7-recognition-patterns)
8. [Common Mistakes](#8-common-mistakes)
9. [C++ Basics](#9-c-basics)
10. [Final Memory Card](#10-final-memory-card)

---

# 1. What Is a Permutation?

A **permutation** is an arrangement where:

```text
ORDER MATTERS
```

Example:

```text
{A, B, C}
```

Choose and arrange `2` letters:

```text
AB
BA
AC
CA
BC
CB
```

There are:

```text
6 arrangements
```

Notice:

```text
AB ≠ BA
```

because their order is different.

### Recognition

```text
same selected objects
      +
different order
      ↓
different outcome
      ↓
PERMUTATION
```

---

# 2. Arranging All N Distinct Objects

Suppose there are `N` distinct objects and all must be arranged.

For the first position:

```text
N choices
```

After choosing one:

```text
N - 1 choices
```

Then:

```text
N - 2 choices
```

Continue until:

```text
1 choice
```

Therefore:

```text
N × (N-1) × (N-2) × ... × 1
```

This is:

```text
N!
```

So:

```text
P(N) = N!
```

### Example — Arrange `{1,2,3,4}`

```text
position:   1   2   3   4
choices:    4   3   2   1
```

Therefore:

```text
4 × 3 × 2 × 1
= 4!
= 24
```

So there are:

```text
24 arrangements
```

---

# 3. Arranging R Objects from N

Now suppose:

```text
N distinct objects exist
```

but we only need to fill:

```text
R ordered positions
```

Example:

```text
N = 4
R = 3

objects = {A,B,C,D}
```

Position choices:

```text
position 1 → 4
position 2 → 3
position 3 → 2
```

Therefore:

```text
4 × 3 × 2
= 24
```

General pattern:

```text
N × (N-1) × (N-2) × ... × (N-R+1)
```

This is written:

```text
P(N,R)
```

or:

```text
nPr
```

Formula:

```text
        N!
P(N,R) = ───────
        (N-R)!
```

---

# 4. Why the nPr Formula Works

Start from:

```text
N!
```

Expand:

```text
N!
=
N × (N-1) × ... × (N-R+1)
×
(N-R) × ... × 1
```

The last part is:

```text
(N-R)!
```

Therefore:

```text
N!
=
[N × (N-1) × ... × (N-R+1)]
× (N-R)!
```

Divide by `(N-R)!`:

```text
N!
────────
(N-R)!

=
N × (N-1) × ... × (N-R+1)
```

Hence:

```text
        N!
nPr = ───────
      (N-R)!
```

### Example

Arrange `3` letters from:

```text
{A,B,C,D}
```

Then:

```text
N = 4
R = 3
```

Using choices:

```text
4 × 3 × 2 = 24
```

Using formula:

```text
4P3
=
4! / (4-3)!
=
4! / 1!
=
24
```

---

# 5. Example — 5-Letter Password

Create a `5`-letter password from `26` English letters with:

```text
no repetition
```

Positions:

```text
P1   P2   P3   P4   P5
│    │    │    │    │
26   25   24   23   22
```

Therefore:

```text
ways
=
26 × 25 × 24 × 23 × 22
```

This is:

```text
26P5
```

Formula:

```text
26P5
=
26! / (26-5)!
=
26! / 21!
```

### Recognition

```text
5 ordered positions
+
26 available symbols
+
no repetition
      ↓
26P5
```

---

# 6. Permutations with Repeated Elements

So far, the objects were distinct.

Now suppose some objects are identical.

Example:

```text
AAB
```

If we temporarily treat the two `A`s as different:

```text
A₁ A₂ B
```

we would count:

```text
3! = 6
```

But swapping:

```text
A₁ ↔ A₂
```

does not create a new visible arrangement.

The identical `A`s can be internally rearranged in:

```text
2!
```

ways that look exactly the same.

Therefore:

```text
unique arrangements
=
3! / 2!
=
3
```

They are:

```text
AAB
ABA
BAA
```

---

## General Formula

Suppose there are `N` total objects with repeated groups:

```text
A identical objects
B identical objects
C identical objects
...
```

Then:

```text
                     N!
unique permutations = ─────────────
                     A! × B! × C! ...
```

### Why divide?

```text
N!
```

initially treats identical objects as if they were distinct.

But swapping identical objects produces duplicate arrangements.

So:

```text
count everything
      ↓
N!
      ↓
divide duplicate internal arrangements
      ↓
N! / (A! × B! × ...)
```

---

## Example — `AAABBBBCCCCCC`

Counts:

```text
A → 3
B → 4
C → 6
```

Total:

```text
N = 3 + 4 + 6 = 13
```

If all letters were distinct:

```text
13!
```

But:

```text
A's can swap in 3! indistinguishable ways
B's can swap in 4! indistinguishable ways
C's can swap in 6! indistinguishable ways
```

Therefore:

```text
             13!
ways = ─────────────
       3! × 4! × 6!
```

---

# 7. Recognition Patterns

### Pattern 1 — Arrange all distinct objects

```text
N objects
all arranged
order matters

→ N!
```

### Pattern 2 — Choose R and arrange them

```text
N available
R ordered positions
no repetition

→ nPr
```

```text
nPr = N! / (N-R)!
```

### Pattern 3 — Repeated identical objects

```text
N total objects
some are identical

→ divide by duplicate arrangements
```

```text
N! / (c1! × c2! × ...)
```

### Quick Decision

```text
Does order matter?
      │
     YES
      ↓
Are all N objects used?
   /             \
 YES              NO
  ↓                ↓
 N!               nPr

If identical objects exist:
divide by factorial of each repeated count
```

---

# 8. Common Mistakes

## Mistake 1 — Using permutation when order does not matter

If:

```text
AB and BA
```

represent the **same** selection, this is not a permutation problem.

That leads to **combinations**.

---

## Mistake 2 — Forgetting choices decrease without repetition

For a 5-letter password:

Wrong:

```text
26^5
```

if repetition is forbidden.

Correct:

```text
26 × 25 × 24 × 23 × 22
```

---

## Mistake 3 — Using `N!` when only `R` positions are filled

```text
N = 10
R = 3
```

Use:

```text
10P3
=
10 × 9 × 8
```

not:

```text
10!
```

---

## Mistake 4 — Treating identical objects as distinct

For:

```text
AAB
```

wrong:

```text
3!
```

Correct:

```text
3! / 2!
```

---

# 9. C++ Basics

## Factorial

```cpp
long long factorial(int n) {
    long long ans = 1;

    for (int i = 2; i <= n; i++) {
        ans *= i;
    }

    return ans;
}
```

## nPr

Instead of calculating two complete factorials, directly multiply the required `R` factors:

```cpp
long long nPr(int n, int r) {
    if (r < 0 || r > n)
        return 0;

    long long ans = 1;

    for (int i = 0; i < r; i++) {
        ans *= (n - i);
    }

    return ans;
}
```

Example:

```cpp
cout << nPr(4, 3);
```

Output:

```text
24
```

> For large `N`, factorials grow extremely quickly. If a problem asks for the answer modulo some value, modular combinatorics is needed.

---

# 10. Final Memory Card

```text
PERMUTATION
=
ORDER MATTERS
```

```text
ARRANGE ALL N DISTINCT OBJECTS

N!
```

```text
ARRANGE R FROM N DISTINCT OBJECTS

nPr

     N!
= ───────
  (N-R)!

= N × (N-1) × ... × (N-R+1)
```

```text
REPEATED IDENTICAL OBJECTS

          N!
──────────────────────
c1! × c2! × c3! × ...
```

### Core Recognition

```text
"arrange"
"order"
"ranking"
"1st, 2nd, 3rd"
"ordered positions"
"password without repetition"
        ↓
check whether
ORDER MATTERS
        ↓
PERMUTATION
```

### Connection to Counting Principles

```text
available choices per position

N
↓
N-1
↓
N-2
↓
...
```

Multiply those choices:

```text
N × (N-1) × (N-2) × ...
```

That is where permutations come from.

> **Contest habit:** Before using `nPr`, ask: **If I swap two selected objects, does the outcome change?** If yes, order matters.
