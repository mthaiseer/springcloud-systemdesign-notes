# Counting Principles — CP Foundation

> **Goal:** Learn the two basic counting rules used throughout combinatorics: **Addition (OR)** and **Multiplication (AND)**.

## 1. Core Idea

Counting asks:

```text
How many possible outcomes?
        ↓
Identify choices
        ↓
AND or OR?
        ↓
Multiply or Add
```

```text
Counting Principles
       │
   ┌───┴───┐
   ↓       ↓
Addition  Multiplication
  OR          AND
   +            ×
```

---

## 2. Golden Rule — AND vs OR

```text
OR  → ADD
AND → MULTIPLY
```

Example with `18` girls and `15` boys:

```text
one girl AND one boy
→ 18 × 15
```

```text
one girl OR one boy
→ 18 + 15
```

> For **OR**, simple addition assumes the alternatives do not overlap. If they overlap, handle the overlap separately.

---

## 3. Multiplication Principle

If:

```text
Step 1 → m ways
AND
Step 2 → n ways
```

then:

```text
Total = m × n
```

### Example — One Girl AND One Boy

```text
18 girl choices
15 boy choices
```

For every girl, there are `15` possible boys:

```text
Total
= 18 × 15
= 270
```

Small visualization:

```text
Girls: G1 G2 G3
Boys : B1 B2

G1 → B1, B2
G2 → B1, B2
G3 → B1, B2

3 × 2 = 6
```

### Multiple stages

If:

```text
Step 1 → a ways
Step 2 → b ways
Step 3 → c ways
```

and all steps are required:

```text
Total = a × b × c
```

Example:

```text
3 shirts
4 trousers
2 shoes

shirt AND trousers AND shoes

3 × 4 × 2 = 24 outfits
```

---

## 4. Addition Principle

If:

```text
Choice A → m ways
OR
Choice B → n ways
```

and the outcomes do not overlap:

```text
Total = m + n
```

### Example — Choose One Student

```text
18 girls
15 boys
```

Choose:

```text
one girl OR one boy
```

Therefore:

```text
Total
= 18 + 15
= 33
```

Visualization:

```text
       Choose one student
              │
       ┌──────┴──────┐
       ↓             ↓
    Girl            Boy
   18 ways         15 ways
       \             /
        \           /
         18 + 15
            ↓
           33
```

---

## 5. How to Recognize the Rule

Ask:

```text
Do I need ALL choices?
```

```text
YES → AND → MULTIPLY
```

Otherwise:

```text
Am I choosing ONE among separate alternatives?
```

```text
YES → OR → ADD
```

Decision diagram:

```text
          COUNT OUTCOMES
                │
                ↓
       How are choices joined?
          /             \
        AND              OR
         │                │
         ↓                ↓
     MULTIPLY            ADD
         ×                +
```

Examples:

```text
pizza AND drink
→ multiply
```

```text
pizza OR burger
→ add
```

---

## 6. Combining AND + OR

Real problems can contain both.

Example:

```text
Main:
3 pizzas OR 2 burgers

Drink:
4 choices
```

First resolve the `OR`:

```text
3 + 2 = 5 main choices
```

Then:

```text
main AND drink
```

Therefore:

```text
(3 + 2) × 4
= 5 × 4
= 20
```

Model:

```text
Pizza OR Burger
    3 + 2
      ↓
      5
      │
     AND
      │
4 drink choices
      ↓
    5 × 4
      ↓
     20
```

This is a useful CP habit:

```text
Break into stages
      ↓
OR groups → +
      ↓
required stages → ×
```

---


## 7. Dependent Choices — Choices Can Decrease

The multiplication principle does **not** mean every step must have the same number of choices.

The number of available choices can change after an earlier choice.

### Example — President AND Vice-President

Suppose there are:

```text
5 people
```

Choose:

```text
1 president AND 1 vice-president
```

President:

```text
5 choices
```

After choosing the president, that person cannot be chosen again.

Vice-president:

```text
4 choices
```

Therefore:

```text
Total
= 5 × 4
= 20
```

Visual:

```text
5 choices
   ↓ choose president
4 choices remain
   ↓ choose vice-president
5 × 4 = 20
```

### General Pattern

```text
Step 1 → n choices
Step 2 → n-1 choices
Step 3 → n-2 choices
...
```

So:

```text
n × (n-1) × (n-2) × ...
```

This naturally leads to:

```text
Factorial → Permutations
```

> **Key idea:** For AND, multiply the number of choices **available at each step**. Those counts may decrease after earlier selections.

---
## 8. Common Mistakes

### Mistake 1 — Multiplying an OR choice

```text
18 girls OR 15 boys
```

Wrong:

```text
18 × 15
```

Correct:

```text
18 + 15
```

### Mistake 2 — Adding an AND choice

```text
girl choice AND boy choice
```

Wrong:

```text
18 + 15
```

Correct:

```text
18 × 15
```

### Mistake 3 — Ignoring overlap

Simple addition assumes no common outcomes.

If sets overlap:

```text
|A ∪ B| ≠ |A| + |B|
```

because common outcomes are counted twice.

Instead:

```text
|A ∪ B|
=
|A| + |B| - |A ∩ B|
```

This leads to **Inclusion–Exclusion**.

---

## 9. Where This Leads Next

```text
       Counting Principles
               │
      ┌────────┴────────┐
      ↓                 ↓
 Addition           Multiplication
      └────────┬────────┘
               ↓
           Factorial
               ↓
     Permutations / nPr
               ↓
     Combinations / nCr
               ↓
    Inclusion–Exclusion
               ↓
         Probability
```

Before using formulas such as `nPr` or `nCr`, first ask:

```text
What choices am I making?

AND or OR?
```

---

## 10. Final Memory Card

```text
OR → ADD

A OR B
→ ways(A) + ways(B)

when A and B do not overlap
```

```text
AND → MULTIPLY

A AND B
→ ways(A) × ways(B)
```

Example:

```text
18 girls
15 boys

girl AND boy
→ 18 × 15
→ 270

girl OR boy
→ 18 + 15
→ 33
```

### Contest Recognition

```text
HOW MANY WAYS?
      ↓
identify choices
      ↓
  AND or OR?
   /      \
 AND      OR
  ↓        ↓
  ×        +
```

> **Core idea:** **AND → multiply. OR → add.** For OR, check whether the groups overlap before simply adding.
