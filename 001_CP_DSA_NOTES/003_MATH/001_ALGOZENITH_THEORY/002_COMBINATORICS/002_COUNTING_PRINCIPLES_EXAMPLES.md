# Counting Principles — Pattern Examples

> **Goal:** Apply the counting foundation from `001_COUNTING_PRINCIPLES` to common CP-style constraints without repeating the basic AND/OR theory.

---

## Table of Contents

1. [License Plates — Distinct Choices + Leading Constraint](#1-license-plates--distinct-choices--leading-constraint)
2. [Palindromes — Count Only Free Positions](#2-palindromes--count-only-free-positions)
3. [No Consecutive Repeats — Previous Choice Restriction](#3-no-consecutive-repeats--previous-choice-restriction)
4. [Pattern Recognition](#4-pattern-recognition)
5. [Final Memory Card](#5-final-memory-card)

---

# 1. License Plates — Distinct Choices + Leading Constraint

## Problem

A license plate contains:

```text
2 distinct English letters
followed by
3 distinct digits
```

Constraint:

```text
first digit ≠ 0
```

Find the number of possible plates.

---

## Step 1 — Model the Positions

```text
L1  L2  D1  D2  D3
```

All five positions must be filled.

Now count the **available choices at each position**.

### Letters

```text
L1 → 26 choices
L2 → 25 choices
```

Why `25`?

```text
L2 must differ from L1
```

Therefore:

```text
letter ways
= 26 × 25
= 650
```

### Digits

For `D1`:

```text
D1 ≠ 0
```

so:

```text
D1 → 9 choices
     {1,...,9}
```

For `D2`, any digit except `D1` is allowed.

There are `10` digits total:

```text
10 - 1 = 9 choices
```

So:

```text
D2 → 9 choices
```

For `D3`, two digits have already been used:

```text
10 - 2 = 8 choices
```

So:

```text
D3 → 8 choices
```

Therefore:

```text
digit ways
= 9 × 9 × 8
= 648
```

---

## Final Count

```text
total
= letter ways × digit ways

= (26 × 25) × (9 × 9 × 8)

= 650 × 648

= 421,200
```

### Choice Diagram

```text
L1      L2      D1      D2      D3
│       │       │       │       │
26      25       9       9       8
 \       \       |       /       /
  └───────┴──────┴──────┴───────┘
                  ↓
       26 × 25 × 9 × 9 × 8
                  ↓
              421,200
```

### Recognition

```text
distinct positions
      ↓
choices decrease

special leading restriction
      ↓
handle that position separately
```

---

# 2. Palindromes — Count Only Free Positions

## Problem

How many `5`-letter palindromes can be formed using the `26` English letters?

Example shape:

```text
A B C B A
```

---

## Step 1 — Find Independent Positions

For:

```text
_ _ _ _ _
1 2 3 4 5
```

Palindrome constraints give:

```text
position 5 = position 1
position 4 = position 2
```

So positions `4` and `5` are **not new choices**.

Only:

```text
position 1
position 2
position 3
```

are free.

Each has:

```text
26 choices
```

Therefore:

```text
total
= 26 × 26 × 26
= 26³
= 17,576
```

### Visual

```text
positions:    1   2   3   4   5
              ↓   ↓   ↓   ↑   ↑
choices:      A   B   C   B   A

free:         ✓   ✓   ✓
determined:               ✓   ✓
```

The key observation is:

```text
Do NOT count every position.

Count only positions that can be chosen freely.
```

---

## Generalize to N Letters

### Even `N`

Example:

```text
N = 6

A B C C B A
```

Only the first half is free:

```text
N/2 positions
```

Therefore:

```text
ways = 26^(N/2)
```

---

### Odd `N`

Example:

```text
N = 5

A B C B A
```

The first half plus the middle position are free:

```text
floor(N/2) + 1
```

Therefore:

```text
ways = 26^(floor(N/2)+1)
```

A cleaner single formula is:

```text
ways = 26^ceil(N/2)
```

because:

```text
even N → ceil(N/2) = N/2

odd N  → ceil(N/2) = floor(N/2)+1
```

For integer arithmetic:

```text
ceil(N/2) = (N + 1) / 2
```

So:

```text
ways = 26^((N+1)/2)
```

where integer division is used.

### Recognition

```text
symmetry / palindrome
        ↓
some positions determine others
        ↓
count only independent positions
```

---

# 3. No Consecutive Repeats — Previous Choice Restriction

## Problem

Form a `10`-digit number using only:

```text
{1, 3, 5, 7, 9}
```

such that:

```text
no two consecutive digits are equal
```

---

## Step 1 — First Position

There are `5` available odd digits:

```text
D1 → 5 choices
```

---

## Step 2 — Every Later Position

Suppose the previous digit was:

```text
5
```

The next digit can be:

```text
1, 3, 7, 9
```

but not:

```text
5
```

Therefore:

```text
each later position → 4 choices
```

For a 10-digit number:

```text
D1  D2  D3  ...  D10

 5   4   4   ...   4
```

There is:

```text
1 first position
+
9 remaining positions
```

Therefore:

```text
total
= 5 × 4^9
```

---

## Generalize to N Digits

```text
first position
→ 5 choices

remaining N-1 positions
→ 4 choices each
```

Therefore:

```text
ways = 5 × 4^(N-1)
```

### Why does the count stay `4`?

The actual forbidden digit changes:

```text
previous = 1 → cannot choose 1
previous = 7 → cannot choose 7
...
```

but the **number of available choices** remains:

```text
5 - 1 = 4
```

That is what matters for counting.

### Visual

```text
D1        D2        D3            DN
│         │         │             │
5         4         4      ...    4
│         │         │             │
└─────────┴─────────┴─────────────┘
                  ↓
             5 × 4^(N-1)
```

### Recognition

```text
sequence of length N
+
current choice cannot equal previous
        ↓
first position has K choices
        ↓
each later position has K-1 choices
        ↓
K × (K-1)^(N-1)
```

For this problem:

```text
K = 5

5 × 4^(N-1)
```

---

# 4. Pattern Recognition

These three examples represent three useful counting forms:

| Pattern | What changes? | Model |
|---|---|---|
| **Distinct choices** | Used choices disappear | `K × (K-1) × (K-2)...` |
| **Symmetry / palindrome** | Some positions are forced | Count only free positions |
| **No adjacent repeat** | Only previous choice is forbidden | `K × (K-1)^(N-1)` |

### Compare Carefully

#### All digits distinct

```text
5 choices
↓
4 choices
↓
3 choices
↓
2 choices
...
```

Choices keep decreasing because **all previously used values remain forbidden**.

#### Only consecutive digits different

```text
5 choices
↓
4 choices
↓
4 choices
↓
4 choices
...
```

Only **one value — the immediately previous one — is forbidden**.

This distinction is important.

---

# 5. Final Memory Card

```text
FORM 1 — DISTINCT

Each used value remains unavailable.

K × (K-1) × (K-2) × ...
```

```text
FORM 2 — PALINDROME / SYMMETRY

Later positions are determined.

Count only free positions.

Alphabet size = K

ways = K^ceil(N/2)
```

```text
FORM 3 — NO CONSECUTIVE REPEAT

First position:
K choices

Every later position:
K-1 choices

ways = K × (K-1)^(N-1)
```

### Contest Thinking

```text
HOW MANY WAYS?
      ↓
draw positions
      ↓
for each position ask:
"How many choices are still free?"
      ↓
identify what previous choices forbid
      ↓
multiply available choices
```

> **Core habit:** Do not memorize the final formula first. **Draw the positions → count the choices available at each position → multiply.**
