# Stars and Bars — Counting Integer Solutions

> **Goal:** Count integer solutions of  
> `x1 + x2 + ... + xr = n`  
> when variables have lower-bound constraints such as `xi ≥ 0`, `xi ≥ 1`, or `xi ≥ L`.

---

# 1. When Do We Use Stars and Bars?

Typical forms:

```text
x1 + x2 + ... + xr = n
```

or:

```text
distribute n identical items among r groups
```

Think:

```text
n identical items
        ↓
represent them as STARS

r variables / groups
        ↓
separate them using r-1 BARS
```

So:

```text
STARS = items being distributed
BARS  = separators between variables
```

---

# 2. Core Visual Model

Example:

```text
x1 + x2 + x3 = 4
xi ≥ 0
```

We have:

```text
4 stars:

★ ★ ★ ★
```

and `3` variables:

```text
x1 | x2 | x3
```

To create `3` groups, we need:

```text
3 - 1 = 2 bars
```

One arrangement:

```text
★ ★ | ★ | ★
```

means:

```text
x1 = 2
x2 = 1
x3 = 1
```

because:

```text
★ ★   |   ★   |   ★
 ↑↑        ↑       ↑
 x1        x2      x3
```

Thus:

```text
★ ★ | ★ | ★
↔
(2,1,1)
```

Every valid stars-and-bars arrangement corresponds to exactly one solution.

---

## Why Can a Variable Be Zero?

Example:

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

So:

```text
bar at an end
or
two adjacent bars
        ↓
some variable = 0
```

This is why ordinary Stars and Bars naturally handles:

```text
xi ≥ 0
```

---

# 3. Case 1 — Non-Negative Solutions

Given:

```text
x1 + x2 + ... + xr = n
xi ≥ 0
```

## Step 1 — Convert to Stars and Bars

Total value:

```text
n
```

so we need:

```text
n stars
```

Number of variables:

```text
r
```

so we need:

```text
r - 1 bars
```

Therefore the sequence contains:

```text
n + (r-1)
=
n+r-1
```

symbols.

---

## Step 2 — Count the Arrangements

We only need to choose where the bars go.

```text
total positions = n+r-1
bars            = r-1
```

Therefore:

```text
answer
=
C(n+r-1, r-1)
```

Equivalent form:

```text
C(n+r-1, n)
```

because choosing the bar positions automatically determines the star positions.

---

## Example — x1 + x2 + x3 = 4

Here:

```text
n = 4
r = 3
```

So:

```text
stars = 4
bars  = 2
```

Total positions:

```text
4 + 2 = 6
```

Choose the `2` bar positions:

```text
C(6,2)
=
15
```

Therefore:

```text
x1 + x2 + x3 = 4
xi ≥ 0
```

has:

```text
15 solutions
```

### Why Combination?

Imagine:

```text
_ _ _ _ _ _
```

Choose any `2` positions for bars.

For example:

```text
★ ★ | ★ | ★
```

Once the bars are fixed, every remaining position is a star.

So the problem is simply:

```text
choose r-1 positions
from n+r-1 positions
```

---

# 4. Case 2 — Positive Solutions

Now:

```text
x1 + x2 + ... + xr = n
xi > 0
```

or equivalently:

```text
xi ≥ 1
```

Every variable must receive at least `1`.

The key idea is:

```text
GIVE EVERY VARIABLE 1 FIRST
```

---

## Example — x1 + x2 + x3 = 6

We require:

```text
x1 ≥ 1
x2 ≥ 1
x3 ≥ 1
```

Give one to each:

```text
x1 ← 1
x2 ← 1
x3 ← 1
```

Used:

```text
1 + 1 + 1 = 3
```

Remaining:

```text
6 - 3 = 3
```

Now the remaining `3` can be distributed freely.

---

## Algebraic Transformation

Define:

```text
x1 = y1 + 1
x2 = y2 + 1
x3 = y3 + 1
```

where:

```text
yi ≥ 0
```

Substitute:

```text
(y1+1) + (y2+1) + (y3+1) = 6
```

Collect constants:

```text
y1 + y2 + y3 + 3 = 6
```

Therefore:

```text
y1 + y2 + y3 = 3
```

Now it is an ordinary non-negative Stars and Bars problem:

```text
C(3+3-1, 3-1)
=
C(5,2)
=
10
```

Therefore there are:

```text
10 positive solutions
```

---

## General Derivation

For:

```text
x1 + x2 + ... + xr = n
xi ≥ 1
```

write:

```text
xi = yi + 1
```

Since there are `r` variables, we reserve:

```text
r × 1 = r
```

items.

Remaining:

```text
n-r
```

So:

```text
y1 + y2 + ... + yr = n-r
```

with:

```text
yi ≥ 0
```

Apply Stars and Bars:

```text
C((n-r)+r-1, r-1)
```

Simplify:

```text
(n-r)+r-1
=
n-1
```

Therefore:

```text
answer
=
C(n-1, r-1)
```

---

# 5. Case 3 — General Lower Bound xi ≥ L

Suppose:

```text
x1 + x2 + ... + xr = n
```

with:

```text
xi ≥ L
```

The same idea applies:

```text
give every variable L first
```

Define:

```text
xi = yi + L
```

Then:

```text
(y1+L) + ... + (yr+L) = n
```

So:

```text
y1 + ... + yr + rL = n
```

Therefore:

```text
y1 + ... + yr = n-rL
```

where:

```text
yi ≥ 0
```

Now apply ordinary Stars and Bars:

```text
answer
=
C((n-rL)+r-1, r-1)
```

### Feasibility Check

Before using the formula, check:

```text
n ≥ rL
```

If:

```text
n < rL
```

then:

```text
answer = 0
```

because there are not enough items to satisfy the minimum requirements.

Example:

```text
x1+x2+x3 = 2
xi ≥ 1
```

Minimum required:

```text
1+1+1 = 3
```

but:

```text
2 < 3
```

Therefore:

```text
0 solutions
```

---

# 6. Formula & Recognition Table

| Problem | Transformation | Answer |
|---|---|---|
| `x1+...+xr=n`, `xi≥0` | Direct Stars & Bars | `C(n+r-1,r-1)` |
| `x1+...+xr=n`, `xi≥1` | Give each variable `1` | `C(n-1,r-1)` |
| `x1+...+xr=n`, `xi≥L` | Set `xi=yi+L` | `C(n-rL+r-1,r-1)` |

For the last case:

```text
require n ≥ rL
```

otherwise the answer is `0`.

---

# 7. How to Recognize It in a Problem

Look for phrases such as:

```text
number of non-negative integer solutions
```

```text
number of positive integer solutions
```

```text
distribute N identical balls among R boxes
```

```text
distribute N identical items among R people/groups
```

Then model:

```text
amount distributed = stars
number of groups    = variables
separators          = groups - 1
```

---

# 8. Common Mistakes

## Mistake 1 — Objects Are Not Identical

Basic Stars and Bars models:

```text
identical items
```

If the objects are distinct, this simple formula does not directly apply.

---

## Mistake 2 — Mixing xi ≥ 0 and xi ≥ 1

Non-negative:

```text
xi ≥ 0
→ direct Stars and Bars
→ C(n+r-1,r-1)
```

Positive:

```text
xi ≥ 1
→ give everyone 1 first
→ C(n-1,r-1)
```

---

## Mistake 3 — Forgetting the Minimum Requirement

For:

```text
xi ≥ L
```

first reserve:

```text
rL
```

items.

If:

```text
n < rL
```

there are no solutions.

---

# 9. Final Contest Memory Card

```text
x1 + x2 + ... + xr = n
```

Ask:

```text
What is the minimum value of each xi?
```

### If xi ≥ 0

```text
n stars
r-1 bars

total positions = n+r-1

answer:
C(n+r-1, r-1)
```

### If xi ≥ 1

```text
give 1 to each
      ↓
use r items
      ↓
remaining = n-r
      ↓
ordinary Stars & Bars
      ↓
answer = C(n-1,r-1)
```

### If xi ≥ L

```text
give L to each
      ↓
use rL items
      ↓
remaining = n-rL
      ↓
ordinary Stars & Bars
```

## Modelling Flow

```text
x1 + x2 + ... + xr = n
          ↓
integer solutions / identical distribution?
          ↓
YES
          ↓
find lower bound L
          ↓
give L to every variable
          ↓
remaining = n-rL
          ↓
convert to yi ≥ 0
          ↓
Stars & Bars
```

> **Core intuition:** `n` identical items become **stars**. The `r` variables are `r` groups, so we need `r-1` **bars** to separate them. If every variable has a minimum value, satisfy that minimum first and distribute only what remains.
