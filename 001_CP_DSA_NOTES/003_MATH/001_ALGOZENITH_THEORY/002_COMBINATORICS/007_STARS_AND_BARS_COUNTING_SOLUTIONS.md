# Stars and Bars — Don't Memorize the Formula, Build the Model

> **Goal:** Recognize when a problem is really asking you to distribute a total among groups, then derive the answer instead of memorizing formulas.

---

## Table of Contents

1. [The One Mental Model](#1-the-one-mental-model)
2. [Build It From Zero](#2-build-it-from-zero)
3. [Case 1 — Variables Can Be Zero](#3-case-1--variables-can-be-zero)
4. [Case 2 — Variables Must Be Positive](#4-case-2--variables-must-be-positive)
5. [Case 3 — General Lower Bound](#5-case-3--general-lower-bound)
6. [Story → Mathematical Model](#6-story--mathematical-model)
7. [How to Derive Instead of Memorize](#7-how-to-derive-instead-of-memorize)
8. [Recognition Patterns](#8-recognition-patterns)
9. [When Stars and Bars Does NOT Directly Work](#9-when-stars-and-bars-does-not-directly-work)
10. [C++ Template](#10-c-template)
11. [Contest Memory Card](#11-contest-memory-card)

---

# 1. The One Mental Model

Suppose:

```text
x1 + x2 + ... + xr = n
```

Interpret it as:

```text
total amount = n
      ↓
n identical items
      ↓
distribute among r variables / groups
```

Represent each item by a star:

```text
★ ★ ★ ★ ...
```

We need separators to split the stars into `r` groups.

```text
group 1 | group 2 | group 3
```

For `r` groups:

```text
bars = r - 1
```

So the entire idea is:

```text
n identical items          → n stars
r variables / groups       → r - 1 bars
arrange stars + bars       → one solution
```

Do **not** begin with a formula. First build this picture.

---

# 2. Build It From Zero

Consider:

```text
x1 + x2 + x3 = 4
xi ≥ 0
```

We are distributing `4` identical units among `3` variables.

```text
4 units
↓
★ ★ ★ ★
```

Three variables need two separators:

```text
x1 | x2 | x3
```

One possible arrangement is:

```text
★ ★ | ★ | ★
```

Read the groups:

```text
★ ★   |   ★   |   ★
 ↑↑        ↑       ↑
 x1        x2      x3

x1 = 2
x2 = 1
x3 = 1
```

Therefore:

```text
★ ★ | ★ | ★
        ↕
     (2,1,1)
```

Another arrangement:

```text
| ★ ★ ★ | ★
```

means:

```text
x1 = 0
x2 = 3
x3 = 1
```

And:

```text
★ ★ || ★ ★
```

means:

```text
x1 = 2
x2 = 0
x3 = 2
```

### Key observation

```text
bar at an end       → a group gets 0
adjacent bars       → a group gets 0
```

So ordinary Stars and Bars naturally allows:

```text
xi ≥ 0
```

---

# 3. Case 1 — Variables Can Be Zero

Given:

```text
x1 + x2 + ... + xr = n
xi ≥ 0
```

## Step 1 — Create the objects

```text
n stars
```

## Step 2 — Create the groups

`r` groups need:

```text
r - 1 bars
```

## Step 3 — Count total symbols

```text
n stars + (r-1) bars
= n+r-1 symbols
```

## Step 4 — What are we actually choosing?

Once the positions of the bars are chosen, all other positions are stars.

```text
_ _ _ _ _ _
```

For `x1+x2+x3=4`:

```text
stars = 4
bars  = 2
total = 6 positions
```

Choose `2` positions for the bars:

```text
C(6,2) = 15
```

Hence:

```text
x1+x2+x3=4, xi≥0
→ 15 solutions
```

### General derivation

```text
total positions = n+r-1
choose bar positions = r-1

answer = C(n+r-1, r-1)
```

The formula is just the **count of the visual model**.

---

# 4. Case 2 — Variables Must Be Positive

Now suppose:

```text
x1 + x2 + x3 = 6
xi ≥ 1
```

Do not search your memory for another formula.

Ask:

> What must happen before I freely distribute anything?

Every variable must already receive `1`.

## Step 1 — Give the minimum first

```text
x1 ← 1
x2 ← 1
x3 ← 1
```

Used:

```text
1 + 1 + 1 = 3
```

Original total:

```text
6
```

Remaining:

```text
6 - 3 = 3
```

Now distribute only these remaining `3` units freely.

```text
remaining 3
    ↓
★ ★ ★

3 variables
    ↓
2 bars
```

So:

```text
total positions = 3 + 2 = 5
answer = C(5,2) = 10
```

## Algebra says exactly the same thing

Write:

```text
x1 = y1 + 1
x2 = y2 + 1
x3 = y3 + 1
```

Then:

```text
(y1+1)+(y2+1)+(y3+1)=6
```

so:

```text
y1+y2+y3=3
yi ≥ 0
```

We have converted the problem back to the **same basic model**.

### General derivation

For:

```text
x1 + ... + xr = n
xi ≥ 1
```

give `1` to all `r` variables:

```text
used      = r
remaining = n-r
```

Now:

```text
remaining stars = n-r
bars            = r-1
```

Total positions:

```text
(n-r)+(r-1)
= n-1
```

Therefore:

```text
answer = C(n-1, r-1)
```

Again, this is not a separate formula to memorize.

---

# 5. Case 3 — General Lower Bound

Suppose:

```text
x1 + x2 + ... + xr = n
xi ≥ L
```

Same model.

## Step 1 — Satisfy the compulsory part

Give every variable `L`:

```text
x1 gets L
x2 gets L
...
xr gets L
```

Total already used:

```text
r × L = rL
```

## Step 2 — Find what remains

```text
remaining = n-rL
```

## Step 3 — Distribute the remainder freely

Set:

```text
xi = yi + L
```

Then:

```text
y1 + y2 + ... + yr = n-rL
yi ≥ 0
```

Now it is the original Stars and Bars model:

```text
stars = n-rL
bars  = r-1
```

Therefore:

```text
answer = C((n-rL)+(r-1), r-1)
```

### Feasibility comes first

If:

```text
n < rL
```

there are not enough units to satisfy the minimum.

```text
answer = 0
```

Example:

```text
x1+x2+x3 = 5
xi ≥ 2
```

Minimum required:

```text
2+2+2 = 6
```

but:

```text
5 < 6
```

Therefore:

```text
0 solutions
```

---

# 6. Story → Mathematical Model

This is the important CP skill.

## Example — Distribute 7 identical candies among 3 children

Each child may receive zero candies.

Remove the story nouns:

```text
candies   → total amount
children  → groups
```

Define:

```text
x1 = candies child 1 receives
x2 = candies child 2 receives
x3 = candies child 3 receives
```

Model:

```text
x1+x2+x3 = 7
xi ≥ 0
```

Visualize:

```text
7 stars + 2 bars

★ ★ | ★ ★ ★ | ★ ★
```

This particular arrangement means:

```text
(2,3,2)
```

Count all possible arrangements:

```text
C(7+3-1, 3-1)
= C(9,2)
= 36
```

---

## Example — Each child must receive at least 2 candies

Model:

```text
x1+x2+x3 = 10
xi ≥ 2
```

Do not memorize a new case.

Give `2` first:

```text
child 1: ★ ★
child 2: ★ ★
child 3: ★ ★
```

Used:

```text
3 × 2 = 6
```

Remaining:

```text
10 - 6 = 4
```

Now distribute the remaining `4` freely:

```text
★ ★ ★ ★ + 2 bars
```

Count:

```text
C(4+2,2)
= C(6,2)
= 15
```

---

# 7. How to Derive Instead of Memorize

When you see a candidate Stars and Bars problem, run this sequence:

```text
What is being distributed?
        ↓
Are the distributed units identical?
        ↓
What are the groups / variables?
        ↓
Can a group receive zero?
        ↓
If not, what minimum must each group receive?
        ↓
Give that minimum FIRST
        ↓
How much remains?
        ↓
remaining amount = stars
groups - 1       = bars
        ↓
choose positions of bars
```

The algebraic version is:

```text
x1 + x2 + ... + xr = n
xi ≥ L
        ↓
xi = yi + L
        ↓
y1 + ... + yr = n-rL
yi ≥ 0
        ↓
stars = n-rL
bars  = r-1
```

Then derive:

```text
total symbols
= (n-rL)+(r-1)
```

and choose the `r-1` bar positions.

---

# 8. Recognition Patterns

Think **Stars and Bars** when the statement looks like:

```text
count non-negative integer solutions
```

or:

```text
count positive integer solutions
```

or:

```text
distribute N identical objects among R groups
```

or after modelling you obtain:

```text
x1+x2+...+xr = n
```

with simple lower bounds.

### Fast recognition

```text
fixed total
+
identical units
+
multiple groups
+
integer allocations
        ↓
possible Stars and Bars
```

---

# 9. When Stars and Bars Does NOT Directly Work

## Distinct objects

If the objects are different:

```text
ball A ≠ ball B ≠ ball C
```

ordinary Stars and Bars does not directly model the choices.

It assumes the distributed units are identical.

## Upper bounds

Example:

```text
x1+x2+x3 = n
0 ≤ xi ≤ 5
```

The lower bound is easy, but the upper bound adds another restriction.

Basic Stars and Bars alone is not enough; such problems often need another technique such as Inclusion-Exclusion.

So recognize:

```text
lower bounds
→ shift variables

upper bounds
→ additional work
```

---

# 10. C++ Template

Stars and Bars eventually becomes an `nCr` calculation.

For small values where the exact answer fits in `long long`:

```cpp
long long nCr(long long n, long long r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);

    long long ans = 1;

    for (long long i = 1; i <= r; i++) {
        ans = ans * (n - i + 1) / i;
    }

    return ans;
}
```

General lower-bound model:

```cpp
long long starsAndBars(long long n, long long r, long long L) {
    long long remaining = n - r * L;

    if (remaining < 0) return 0;

    return nCr(remaining + r - 1, r - 1);
}
```

Example:

```cpp
// x1 + x2 + x3 = 10
// xi >= 2

cout << starsAndBars(10, 3, 2);  // 15
```

For large constraints or answers modulo a prime, use your precomputed factorial + inverse-factorial `nCr` implementation.

---

# 11. Contest Memory Card

Do **not** memorize three unrelated formulas.

Remember only this:

```text
x1+x2+...+xr = n
        ↓
find minimum L for every xi
        ↓
give L to everyone first
        ↓
used = rL
        ↓
remaining = n-rL
        ↓
remaining identical units = STARS
r groups                  = r-1 BARS
        ↓
arrange stars and bars
```

### Why combination?

```text
[ stars + bars ] create a sequence

Choose which positions contain bars.
Everything else automatically becomes a star.
```

Therefore:

```text
positions = remaining + r - 1
bars      = r - 1
```

So the formula is **derived**:

```text
C(remaining+r-1, r-1)
```

not memorized.

---

## Final Recognition Sentence

> **Stars and Bars = distribute a fixed number of identical units among groups. Satisfy compulsory minimums first, then represent the remaining units as stars and separate the groups using bars.**
