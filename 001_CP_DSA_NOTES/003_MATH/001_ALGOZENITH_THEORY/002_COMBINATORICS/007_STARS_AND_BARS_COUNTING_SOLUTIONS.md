# Stars and Bars — Counting Integer Solutions

> **Goal:** Count solutions of equations like  
> `x1 + x2 + ... + xr = n`  
> when the variables must be **non-negative** or **positive integers**.

---

# 1. What Problem Does Stars and Bars Solve?

Suppose:

```text
x1 + x2 + x3 = 4
```

with:

```text
x1, x2, x3 ≥ 0
```

We are asking:

```text
How many different ways can we distribute
4 identical items among 3 variables?
```

Think of the `4` items as stars:

```text
★ ★ ★ ★
```

We need to divide them into `3` groups:

```text
x1 | x2 | x3
```

To create `3` groups, we need:

```text
2 bars
```

because:

```text
3 groups → 2 separators
```

This is the core idea of **Stars and Bars**.

---

# 2. Understanding One Arrangement

Consider:

```text
★ ★ | ★ | ★
```

Read it from left to right:

```text
★ ★   |   ★   |   ★
 ↑↑        ↑       ↑
 x1        x2      x3
```

Therefore:

```text
x1 = 2
x2 = 1
x3 = 1
```

Check:

```text
2 + 1 + 1 = 4
```

So:

```text
★ ★ | ★ | ★
```

represents exactly one solution:

```text
(2,1,1)
```

---

# 3. Why Can a Variable Be Zero?

Suppose:

```text
| ★ ★ ★ | ★
```

There are no stars before the first bar.

Therefore:

```text
x1 = 0
x2 = 3
x3 = 1
```

So adjacent bars or bars at the ends naturally represent zero values.

Another example:

```text
★ ★ || ★ ★
```

means:

```text
x1 = 2
x2 = 0
x3 = 2
```

This is why ordinary Stars and Bars naturally handles:

```text
xi ≥ 0
```

---

# 4. Deriving the Non-Negative Formula

Consider:

```text
x1 + x2 + ... + xr = n
```

with:

```text
xi ≥ 0
```

We need:

```text
n stars
```

because the total being distributed is `n`.

We need:

```text
r - 1 bars
```

because `r` variables require `r` groups.

So the full sequence contains:

```text
n stars + (r-1) bars
```

Total positions:

```text
n + r - 1
```

Now choose which positions contain the bars:

```text
choose r-1 bar positions
from n+r-1 positions
```

Therefore:

```text
             n+r-1
answer = C(       )
              r-1
```

or:

```text
answer = C(n+r-1, r-1)
```

---

# 5. Example — x1 + x2 + x3 = 4

Given:

```text
n = 4
r = 3
```

Stars:

```text
★ ★ ★ ★
```

Bars needed:

```text
r - 1
= 3 - 1
= 2
```

Total symbols:

```text
4 stars + 2 bars
= 6
```

Choose positions for the `2` bars:

```text
C(6,2)
= 15
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

### Formula Check

```text
C(n+r-1, r-1)

= C(4+3-1, 3-1)

= C(6,2)

= 15
```

---

# 6. Why Is This a Combination?

Suppose the six positions are:

```text
_ _ _ _ _ _
```

We only need to decide:

```text
Which 2 positions contain bars?
```

Example:

```text
★ ★ | ★ | ★
```

Once the bar positions are selected, every other position is automatically a star.

So:

```text
choose 2 positions from 6
```

which is:

```text
C(6,2)
```

There is no separate ordering of the identical stars or identical bars.

---

# 7. Positive Solutions — xi > 0

Now consider:

```text
x1 + x2 + x3 = 6
```

with:

```text
x1, x2, x3 > 0
```

This means every variable must receive **at least 1**.

We cannot allow:

```text
x1 = 0
```

or:

```text
x2 = 0
```

etc.

So first give one item to every variable.

---

# 8. Give Everyone One First

Start with:

```text
x1 + x2 + x3 = 6
```

Minimum required:

```text
x1 ≥ 1
x2 ≥ 1
x3 ≥ 1
```

Give one to each:

```text
x1 → 1
x2 → 1
x3 → 1
```

Used:

```text
1 + 1 + 1 = 3
```

Remaining:

```text
6 - 3 = 3
```

Now distribute these remaining `3` items freely.

Define:

```text
x1 = y1 + 1
x2 = y2 + 1
x3 = y3 + 1
```

where:

```text
y1, y2, y3 ≥ 0
```

---

# 9. Algebraic Transformation

Start:

```text
x1 + x2 + x3 = 6
```

Substitute:

```text
x1 = y1 + 1
x2 = y2 + 1
x3 = y3 + 1
```

Then:

```text
(y1+1) + (y2+1) + (y3+1) = 6
```

Group terms:

```text
y1 + y2 + y3 + 3 = 6
```

Move `3`:

```text
y1 + y2 + y3 = 3
```

Now we have a normal non-negative Stars and Bars problem.

---

# 10. Count the Positive Solutions

For:

```text
y1 + y2 + y3 = 3
```

we have:

```text
n = 3
r = 3
```

Therefore:

```text
C(3+3-1, 3-1)

= C(5,2)

= 10
```

So:

```text
x1 + x2 + x3 = 6
xi > 0
```

has:

```text
10 solutions
```

---

# 11. Deriving the General Positive Formula

Start:

```text
x1 + x2 + ... + xr = n
```

with:

```text
xi > 0
```

Every variable needs at least `1`.

So define:

```text
xi = yi + 1
```

for every variable.

There are `r` variables, so we reserve:

```text
r × 1 = r
```

items.

Remaining total:

```text
n - r
```

Therefore:

```text
y1 + y2 + ... + yr = n-r
```

with:

```text
yi ≥ 0
```

Apply the non-negative formula:

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
Positive solutions
=
C(n-1, r-1)
```

---

# 12. Non-Negative vs Positive

## Non-Negative

```text
x1 + x2 + ... + xr = n

xi ≥ 0
```

Direct Stars and Bars:

```text
stars = n
bars  = r-1
```

Answer:

```text
C(n+r-1, r-1)
```

---

## Positive

```text
x1 + x2 + ... + xr = n

xi > 0
```

First reserve `1` for every variable:

```text
remaining = n-r
```

Then Stars and Bars:

```text
C(n-1, r-1)
```

---

# 13. Visual Comparison

```text
NON-NEGATIVE

x1 + x2 + x3 = 4
xi ≥ 0

★ ★ | ★ | ★

zero is allowed
bars may touch / appear at ends

answer:
C(n+r-1, r-1)
```

versus:

```text
POSITIVE

x1 + x2 + x3 = 6
xi ≥ 1

first give:

x1 ← 1
x2 ← 1
x3 ← 1

remaining = 6-3 = 3

then distribute remaining freely

answer:
C(n-1, r-1)
```

---

# 14. General Lower Bound — xi ≥ L

The same idea extends naturally.

Suppose:

```text
x1 + x2 + ... + xr = n
```

and every variable must satisfy:

```text
xi ≥ L
```

Give each variable `L` first.

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

Now:

```text
yi ≥ 0
```

Apply Stars and Bars:

```text
answer
=
C((n-rL)+r-1, r-1)
```

provided:

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

because there are not enough items to satisfy the minimum.

---

# 15. Recognition Patterns

| Problem form | Transformation | Answer |
|---|---|---|
| `x1+...+xr=n`, `xi≥0` | Direct Stars & Bars | `C(n+r-1,r-1)` |
| `x1+...+xr=n`, `xi≥1` | Give each `1` | `C(n-1,r-1)` |
| `x1+...+xr=n`, `xi≥L` | `xi=yi+L` | `C(n-rL+r-1,r-1)` |

Typical problem language:

```text
distribute N identical balls among R boxes
```

```text
number of non-negative integer solutions
```

```text
number of positive integer solutions
```

```text
split N identical items among R people
```

These should make you think:

```text
STARS AND BARS
```

---

# 16. Common Mistakes

## Mistake 1 — Using it when objects are distinct

Stars and Bars models:

```text
identical items
```

If the objects themselves are distinct, this simple formula does not directly apply.

---

## Mistake 2 — Mixing Positive and Non-Negative

```text
xi ≥ 0
```

means:

```text
C(n+r-1, r-1)
```

But:

```text
xi ≥ 1
```

requires giving everyone `1` first:

```text
C(n-1, r-1)
```

---

## Mistake 3 — Forgetting Feasibility

Example:

```text
x1+x2+x3 = 2
xi > 0
```

Minimum required:

```text
1+1+1 = 3
```

But:

```text
2 < 3
```

Therefore:

```text
0 solutions
```

---

# 17. Final Memory Card

```text
STARS AND BARS

x1 + x2 + ... + xr = n
```

### Non-Negative

```text
xi ≥ 0

n stars
r-1 bars

total symbols:
n+r-1

choose bar positions:
C(n+r-1, r-1)
```

### Positive

```text
xi ≥ 1

give 1 to every variable
        ↓
use r items
        ↓
remaining = n-r
        ↓
ordinary Stars & Bars
        ↓
C(n-1, r-1)
```

### General Minimum L

```text
xi ≥ L

give L to each variable
        ↓
remaining = n-rL
        ↓
Stars & Bars
```

## Contest Thinking Flow

```text
x1 + x2 + ... + xr = n
          ↓
integer solutions?
          ↓
check lower bound
     /           \
   xi≥0          xi≥L
    ↓              ↓
 direct       give L first
    ↓              ↓
Stars & Bars   remaining n-rL
     \            /
      \          /
       choose bars
```

> **Core idea:** Stars are the items being distributed. Bars divide those stars among the variables. If every variable has a minimum requirement, **give the minimum first**, then apply ordinary Stars and Bars to what remains.
