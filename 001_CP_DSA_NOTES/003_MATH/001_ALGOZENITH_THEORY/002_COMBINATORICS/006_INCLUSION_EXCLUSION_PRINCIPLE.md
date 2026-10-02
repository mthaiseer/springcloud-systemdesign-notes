# Inclusion–Exclusion Principle (IEP) — Visual Math Modelling

> **Goal:** Count objects satisfying **at least one** condition when conditions overlap.

## 1. Core Idea — Why IEP?

Suppose:

```text
A = objects satisfying condition A
B = objects satisfying condition B
```

We want `A OR B`.

If we calculate:

```text
|A| + |B|
```

the common part is counted twice:

```text
        A                 B
     _______           _______
   /         \_______/         \
  /           \ BOTH /          \
  \           /     \           /
   \_________/       \_________/

       counted once + once
             = twice
```

So remove one copy:

```text
|A ∪ B| = |A| + |B| - |A ∩ B|
```

Mental model:

```text
ADD everything
      ↓
overlap counted too much
      ↓
SUBTRACT overlap
```

---

## 2. Three Sets

For `A`, `B`, `C`:

```text
|A ∪ B ∪ C|

= |A| + |B| + |C|
- |A∩B| - |A∩C| - |B∩C|
+ |A∩B∩C|
```

Why add the triple intersection back?

An element in all three sets is:

```text
added       3 times
subtracted  3 times
-------------------
current     0 times
```

But it should be counted once:

```text
+ triple intersection
```

So remember:

```text
1-set intersections → +
2-set intersections → -
3-set intersections → +
4-set intersections → -
...
```

Pattern:

```text
+ - + - + - ...
```

---

# 3. Example — Die Rolled 4 Times

## Problem

A die is rolled `4` times. Count outcomes where:

```text
largest appearing number ≠ 4
```

## Step 1 — Total

Each roll has `6` choices:

```text
Total = 6⁴
```

## Step 2 — Count the Bad Case

Bad means:

```text
maximum = 4
```

This means:

```text
all values ≤ 4
AND
at least one value = 4
```

All rolls from `{1,2,3,4}`:

```text
4⁴
```

But this includes sequences containing no `4`:

```text
all rolls from {1,2,3}
→ 3⁴
```

Therefore:

```text
maximum exactly 4
=
4⁴ - 3⁴
```

Visual:

```text
all values ≤ 4
      4⁴
       │
       ├── contains a 4     ← max = 4
       │
       └── contains no 4
               ↓
              3⁴

max = 4
= 4⁴ - 3⁴
```

## Step 3 — Remove Bad Outcomes

```text
answer
=
total - bad

= 6⁴ - (4⁴ - 3⁴)

= 6⁴ - 4⁴ + 3⁴
```

Numerically:

```text
1296 - 256 + 81
= 1121
```

### Recognition

For a sequence of length `N`:

```text
maximum exactly K
=
K^N - (K-1)^N
```

Think:

```text
EXACTLY K
=
AT MOST K
-
AT MOST K-1
```

---

# 4. Example — Avoiding Forbidden Substrings

## Problem

Arrange:

```text
a b c d e f g
```

such that neither `"cad"` nor `"beg"` appears as a consecutive block.

## Step 1 — Total

All letters are distinct:

```text
Total = 7!
```

Define bad sets:

```text
A = contains "cad"
B = contains "beg"
```

We need:

```text
valid
=
Total - |A ∪ B|
```

## Step 2 — Count A

Compress `"cad"` into one object:

```text
[cad] b e f g
```

Now there are `5` objects:

```text
|A| = 5!
```

## Step 3 — Count B

Similarly:

```text
[beg] a c d f
```

So:

```text
|B| = 5!
```

## Step 4 — Find the Overlap

If both appear:

```text
[cad] [beg] f
```

There are `3` objects:

```text
|A ∩ B| = 3!
```

Why do we need this?

If we do:

```text
7! - 5! - 5!
```

arrangements containing **both** blocks are removed twice.

So add them back once.

## Step 5 — Final Model

```text
|A ∪ B|
=
5! + 5! - 3!
```

Therefore:

```text
valid
=
7! - (5! + 5! - 3!)

= 7! - 2×5! + 3!

= 4806
```

Visual:

```text
ALL = 7!
   │
   ├── bad "cad" → 5!
   │
   └── bad "beg" → 5!
            │
        BOTH removed twice
            ↓
          + 3!

valid = 7! - 5! - 5! + 3!
```

### Recognition

```text
AVOID several bad conditions
          ↓
define bad sets
          ↓
answer = TOTAL - union(bad)
          ↓
use IEP
```

---

# 5. Example — Divisible by 2, 3, or 5

## Problem

Count integers from `1...1000` divisible by at least one of:

```text
2, 3, 5
```

Define:

```text
A = multiples of 2
B = multiples of 3
C = multiples of 5
```

We need:

```text
|A ∪ B ∪ C|
```

## Step 1 — Individual Sets

Number of multiples of `d` from `1...N`:

```text
floor(N/d)
```

Therefore:

```text
|A| = floor(1000/2) = 500
|B| = floor(1000/3) = 333
|C| = floor(1000/5) = 200
```

## Step 2 — Pair Intersections

Divisible by both `2` and `3` means divisible by:

```text
LCM(2,3) = 6
```

So:

```text
|A∩B| = floor(1000/6) = 166
```

Similarly:

```text
|A∩C|
= floor(1000 / LCM(2,5))
= floor(1000/10)
= 100
```

```text
|B∩C|
= floor(1000 / LCM(3,5))
= floor(1000/15)
= 66
```

## Step 3 — Triple Intersection

Divisible by all three:

```text
LCM(2,3,5) = 30
```

Therefore:

```text
|A∩B∩C|
=
floor(1000/30)
=
33
```

## Step 4 — Apply IEP

```text
|A ∪ B ∪ C|

= 500 + 333 + 200
- 166 - 100 - 66
+ 33

= 734
```

Visual:

```text
multiples of 2      +500
multiples of 3      +333
multiples of 5      +200
                     ↓
pair overlap counted twice

multiples of 6      -166
multiples of 10     -100
multiples of 15      -66
                     ↓
triple overlap removed too much

multiples of 30      +33
                     ↓
                    734
```

### Important Transformation

```text
divisible by A AND B
        ↓
divisible by LCM(A,B)
```

Therefore:

```text
count divisible by all chosen divisors
=
floor(N / LCM(chosen divisors))
```

---

# 6. General IEP / Bitmask Form

For conditions:

```text
A1, A2, ..., AK
```

IEP says:

```text
+ intersections of 1 set
- intersections of 2 sets
+ intersections of 3 sets
- intersections of 4 sets
...
```

Each non-empty subset of conditions represents one intersection.

```text
subset size odd  → ADD
subset size even → SUBTRACT
```

Example:

```text
{A}       size 1 → +
{A,B}     size 2 → -
{A,B,C}   size 3 → +
```

This leads naturally to the CP implementation:

```text
for mask = 1 ... (1<<K)-1
```

and:

```text
popcount(mask) odd  → +
popcount(mask) even → -
```

For divisibility problems, compute the LCM of the divisors selected by the mask.

---

# 7. Recognition Patterns

| Problem clue | Think |
|---|---|
| `A OR B` with overlap | `A + B - both` |
| at least one condition | Union → IEP |
| avoid multiple bad conditions | `Total - union(bad)` |
| divisible by any of several values | IEP + LCM |
| forbidden blocks | Compress blocks + IEP |
| maximum exactly `K` | `≤K - ≤K-1` |
| small `K` overlapping conditions | Bitmask IEP |

---

# 8. Common Mistakes

### 1. Adding overlapping sets directly

Wrong:

```text
|A ∪ B| = |A| + |B|
```

Correct:

```text
|A ∪ B|
=
|A| + |B| - |A∩B|
```

### 2. Forgetting triple overlap

For three sets:

```text
+ singles
- pairs
+ triple
```

### 3. Using product instead of LCM

For:

```text
divisible by 4 AND 6
```

use:

```text
LCM(4,6) = 12
```

not:

```text
4×6 = 24
```

Thus:

```text
count = floor(N/12)
```

---

# 9. Final Memory Card

```text
INCLUSION–EXCLUSION
=
FIX OVERCOUNTING
```

Two sets:

```text
A OR B
=
A + B - BOTH
```

Three sets:

```text
A OR B OR C
=
singles
- pairs
+ triple
```

General:

```text
subset size 1 → +
subset size 2 → -
subset size 3 → +
subset size 4 → -
...
```

## Contest Modelling Flow

```text
WHAT ARE THE CONDITIONS?
          ↓
TURN THEM INTO SETS
          ↓
DO THEY OVERLAP?
          ↓
         YES
          ↓
COUNT SINGLE SETS
          ↓
COUNT INTERSECTIONS
          ↓
+ singles
- pairs
+ triples
- quadruples ...
```

### Three Important CP Forms

```text
AT LEAST ONE
→ union
→ IEP
```

```text
AVOID BAD CONDITIONS
→ total - union(bad)
→ IEP
```

```text
DIVISIBLE BY ALL CHOSEN VALUES
→ intersection
→ LCM
```

> **Contest habit:** When you see **OR**, **at least one**, or **avoid multiple conditions**, first ask whether the cases overlap. If they do, think **Inclusion–Exclusion**.
