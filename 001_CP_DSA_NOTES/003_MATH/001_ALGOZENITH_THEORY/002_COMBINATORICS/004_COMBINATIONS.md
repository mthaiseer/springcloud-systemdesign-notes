# Combinations — CP Foundation

> **Goal:** Count selections where **order does not matter**.

## 1. What Is a Combination?

A combination is a selection where:

```text
ORDER DOES NOT MATTER
```

Example:

```text
{A, B, C}, choose 2

AB, AC, BC
```

Here:

```text
AB = BA
```

So `BA` is not a new selection.

```text
AB ≠ BA → Permutation
AB = BA → Combination
```

---

## 2. Choosing R Objects from N

Choose `R` objects from `N` distinct objects:

```text
C(N,R) = nCr
```

Formula:

```text
           N!
nCr = ─────────────
      R! × (N-R)!
```

Recognition:

```text
choose / select / committee / team
              ↓
       order irrelevant
              ↓
             nCr
```

---

## 3. Why the nCr Formula Works

Permutation counts the selected objects in every possible order:

```text
        N!
nPr = ───────
      (N-R)!
```

Suppose we selected:

```text
A, B, C
```

Permutation counts:

```text
ABC ACB BAC BCA CAB CBA
```

But for combinations these are all the **same group**.

Number of orders of `R` selected objects:

```text
R!
```

So divide them out:

```text
nCr = nPr / R!
```

Therefore:

```text
           N!
nCr = ─────────────
      R! × (N-R)!
```

Mental model:

```text
nPr
↓
same group counted R! times
↓
divide by R!
↓
nCr
```

---

## 4. Example — Choose 3 from 10

Choose `3` students from `10`:

```text
           10!
10C3 = ───────────
        3! × 7!
```

Cancel `7!`:

```text
        10 × 9 × 8
10C3 = ────────────
         3 × 2 × 1

      = 120
```

So:

```text
10C3 = 120
```

---

## 5. Committee — Exact Requirement

Given:

```text
6 men
8 women
```

Choose exactly:

```text
2 men AND 4 women
```

Men:

```text
6C2 = 15
```

Women:

```text
8C4 = 70
```

Both groups are required:

```text
6C2 × 8C4
= 15 × 70
= 1050
```

Pattern:

```text
x from group A AND y from group B

→ C(A,x) × C(B,y)
```

---

## 6. Committee — At Least

Form a committee of `6` with:

```text
at least 3 women
```

Translate first:

```text
at least 3
=
3 OR 4 OR 5 OR 6 women
```

| Women | Men | Ways |
|---:|---:|---:|
| 3 | 3 | `8C3 × 6C3 = 1120` |
| 4 | 2 | `8C4 × 6C2 = 1050` |
| 5 | 1 | `8C5 × 6C1 = 336` |
| 6 | 0 | `8C6 × 6C0 = 28` |

Add the separate cases:

```text
1120 + 1050 + 336 + 28
= 2534
```

Key modelling:

```text
inside each case:
women AND men
→ multiply

between cases:
3 OR 4 OR 5 OR 6 women
→ add
```

---

## 7. Committee — At Most

Now require:

```text
at most 3 women
```

Translate:

```text
0 OR 1 OR 2 OR 3 women
```

| Women | Men | Ways |
|---:|---:|---:|
| 0 | 6 | `8C0 × 6C6 = 1` |
| 1 | 5 | `8C1 × 6C5 = 48` |
| 2 | 4 | `8C2 × 6C4 = 420` |
| 3 | 3 | `8C3 × 6C3 = 1120` |

Therefore:

```text
1 + 48 + 420 + 1120
= 1589
```

---

## 8. Recognition Patterns

```text
EXACTLY K
→ only K
```

```text
AT LEAST K
→ K, K+1, K+2, ...
```

```text
AT MOST K
→ 0, 1, 2, ..., K
```

For multiple groups:

```text
fixed choice inside one case
→ multiply

different valid cases
→ add
```

Example:

```text
exactly 2 men AND 4 women
→ 6C2 × 8C4
```

```text
3 women OR 4 women OR 5 women...
→ add the cases
```

---

## 9. Useful Identity

```text
nCr = nC(n-r)
```

Example:

```text
10C3 = 10C7
```

Why?

Choosing `3` objects to include is equivalent to choosing the other `7` objects to exclude.

So computationally use:

```text
r = min(r, n-r)
```

---

## 10. Common Mistakes

### nCr vs nPr

Ask:

```text
If I swap two selected objects,
does the result change?
```

```text
YES → nPr
NO  → nCr
```

### Multiplying alternative cases

Wrong:

```text
3 women × 4 women × 5 women
```

These are alternatives:

```text
3 OR 4 OR 5
```

so their counts are **added**.

### Forgetting total size

For a committee of `6`:

```text
women + men = 6
```

Therefore:

```text
4 women → 2 men
5 women → 1 man
```

Use this equation to generate cases quickly.

---

## 11. C++ Basics

For small values:

```cpp
long long nCr(int n, int r) {
    if (r < 0 || r > n)
        return 0;

    r = min(r, n - r);

    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}
```

Example:

```cpp
cout << nCr(10, 3);  // 120
```

For large `N` when the answer is required modulo a prime, use **factorials + modular inverses**.

---

## 12. Final Memory Card

```text
COMBINATION
=
ORDER DOES NOT MATTER
```

```text
           N!
nCr = ─────────────
      R! × (N-R)!
```

```text
WHY R!?

nPr counts each selected group
in R! different orders.

nCr = nPr / R!
```

```text
EXACT:
x from A AND y from B
→ C(A,x) × C(B,y)
```

```text
AT LEAST K
→ K, K+1, ...
→ add cases
```

```text
AT MOST K
→ 0, 1, ..., K
→ add cases
```

### Final Recognition

```text
Does order matter?
       /     \
     YES      NO
      ↓        ↓
     nPr      nCr
```

> **Contest habit:** Decide **order matters or not** first. Then translate **exactly / at least / at most** into cases.
