# CF Mathematical Modeling Mastery (800 → 1900)

> **V2 — Real-World + Visual Mathematical Modeling Edition**
>
> Every topic keeps the original mathematical material and adds a retention layer:
> **simple story → real numbers → manual steps → ASCII mathematical model → CF recognition signal**.
> The goal is not merely to memorize formulas, but to recognize the mathematical form hidden inside a Codeforces statement.


> **Core skill:** `Statement → Variables → Conditions → Simplify → Recognize form → Algorithm → Proof → C++`
>
> **Do not ask first "which algorithm is this?" Ask "what is the mathematical structure?"**

**Honest scope note.** Parts 0–26 and 28–37 are complete. Part 27 (the problem library) contains **18 fully decoded problems** in the mandatory 13-step format, spanning 800→1900. The 180+ target is too large for one file-generation pass; the band index at the end of Part 27 lists what to extend next. Ratings are approximate (CF re-rates); verify on the site.


## Table of Contents

- [Part 0. How to Mathematically Read a Codeforces Problem](#part-0-how-to-mathematically-read-a-codeforces-problem)
- [Part 1. Arithmetic Foundations](#part-1-arithmetic-foundations)
- [Part 2. Algebra for Competitive Programming](#part-2-algebra-for-competitive-programming)
  - [Algebra Form 1. Rearranging equations (sum and difference)](#algebra-form-1-rearranging-equations-sum-and-difference)
  - [Algebra Form 2. Isolating a variable](#algebra-form-2-isolating-a-variable)
  - [Algebra Form 3. Substitution](#algebra-form-3-substitution)
  - [Algebra Form 4. Difference of squares](#algebra-form-4-difference-of-squares)
  - [Algebra Form 5. Expansions](#algebra-form-5-expansions)
  - [Algebra Form 6. Pairwise sums](#algebra-form-6-pairwise-sums)
  - [Algebra Form 7. Linear equation `ax + by = c`](#algebra-form-7-linear-equation-ax--by--c)
  - [Algebra Form 8. Systems of equations](#algebra-form-8-systems-of-equations)
  - [Algebra Form 9. Inequalities (intersection of constraints)](#algebra-form-9-inequalities-intersection-of-constraints)
  - [Algebra Form 10. Bounding (lower bound + construction)](#algebra-form-10-bounding-lower-bound--construction)
- [Part 3. Number Theory Foundations](#part-3-number-theory-foundations)
- [Part 4. Modular Arithmetic](#part-4-modular-arithmetic)
- [Part 5. Parity](#part-5-parity)
- [Part 6. Counting & Combinatorics](#part-6-counting--combinatorics)
- [Part 7. Sequences & Series](#part-7-sequences--series)
- [Part 8. Coordinate & Distance Mathematics](#part-8-coordinate--distance-mathematics)
- [Part 9. Min/Max Mathematical Transformations](#part-9-minmax-mathematical-transformations)
- [Part 10. Invariants](#part-10-invariants)
- [Part 11. Operation Modeling](#part-11-operation-modeling)
- [Part 12. Decoupling Variables](#part-12-decoupling-variables)
- [Part 13. Frequency Modeling](#part-13-frequency-modeling)
- [Part 14. Sorting as a Mathematical Transformation](#part-14-sorting-as-a-mathematical-transformation)
- [Part 15. Prefix Mathematics](#part-15-prefix-mathematics)
- [Part 16. Difference Arrays](#part-16-difference-arrays)
- [Part 17. Bitwise Mathematical Modeling](#part-17-bitwise-mathematical-modeling)
- [Part 18. Greedy Through Mathematical Proofs](#part-18-greedy-through-mathematical-proofs)
- [Part 19. Constructive Mathematics](#part-19-constructive-mathematics)
- [Part 20. Diophantine Modeling](#part-20-diophantine-modeling)
- [Part 21. Geometric / Grid Modeling](#part-21-geometric--grid-modeling)
- [Part 22. Game Mathematics](#part-22-game-mathematics)
- [Part 23. Recurrences](#part-23-recurrences)
- [Part 24. Expectation / Probability Basics](#part-24-expectation--probability-basics)
- [Part 25. Mathematical Optimization](#part-25-mathematical-optimization)
- [Part 26. Common Codeforces Mathematical Forms](#part-26-common-codeforces-mathematical-forms)
  - [Form 1. Sum Constraint](#form-1-sum-constraint)
  - [Form 2. Difference Constraint](#form-2-difference-constraint)
  - [Form 3. Product Constraint](#form-3-product-constraint)
  - [Form 4. Ratio Constraint](#form-4-ratio-constraint)
  - [Form 5. Parity Constraint](#form-5-parity-constraint)
  - [Form 6. Divisibility Constraint](#form-6-divisibility-constraint)
  - [Form 7. GCD Constraint](#form-7-gcd-constraint)
  - [Form 8. LCM Constraint](#form-8-lcm-constraint)
  - [Form 9. Modulo Constraint](#form-9-modulo-constraint)
  - [Form 10. Equal Frequency](#form-10-equal-frequency)
  - [Form 11. Pair Counting](#form-11-pair-counting)
  - [Form 12. Complement Pair](#form-12-complement-pair)
  - [Form 13. Difference Pair](#form-13-difference-pair)
  - [Form 14. Equal Remainders](#form-14-equal-remainders)
  - [Form 15. Consecutive Values](#form-15-consecutive-values)
  - [Form 16. Arithmetic Progression](#form-16-arithmetic-progression)
  - [Form 17. Geometric / Doubling](#form-17-geometric--doubling)
  - [Form 18. Median Optimization](#form-18-median-optimization)
  - [Form 19. Prefix Equation](#form-19-prefix-equation)
  - [Form 20. Contribution Counting](#form-20-contribution-counting)
  - [Form 21. Pigeonhole](#form-21-pigeonhole)
  - [Form 22. Inclusion-Exclusion](#form-22-inclusion-exclusion)
  - [Form 23. Invariant](#form-23-invariant)
  - [Form 24. Monovariant](#form-24-monovariant)
  - [Form 25. Reachability](#form-25-reachability)
  - [Form 26. Constructive Equation](#form-26-constructive-equation)
  - [Form 27. Bounding](#form-27-bounding)
  - [Form 28. Extremal Principle](#form-28-extremal-principle)
  - [Form 29. Coordinate Transformation](#form-29-coordinate-transformation)
  - [Form 30. Bit Independence](#form-30-bit-independence)
  - [Form 31. Prime Factor Independence](#form-31-prime-factor-independence)
  - [Form 32. Frequency Compression](#form-32-frequency-compression)
  - [Form 33. Permutation Mathematics](#form-33-permutation-mathematics)
  - [Form 34. Mex Mathematics](#form-34-mex-mathematics)
  - [Form 35. Interval Mathematics](#form-35-interval-mathematics)
  - [Form 36. Grid Parity](#form-36-grid-parity)
  - [Form 37. Cyclic / Modulo Process](#form-37-cyclic--modulo-process)
  - [Form 38. Binary Search Equation](#form-38-binary-search-equation)
  - [Form 39. Stars and Bars](#form-39-stars-and-bars)
  - [Form 40. Diophantine Equation](#form-40-diophantine-equation)
- [Part 27. Problem Modeling Library](#part-27-problem-modeling-library)
  - [Pattern A: Parity & Formula Bounds (Lower Bound + Construction)](#pattern-a-parity--formula-bounds-lower-bound--construction)
  - [Pattern B: Invariants (Sum / GCD / Difference)](#pattern-b-invariants-sum--gcd--difference)
  - [Pattern C: Number Theory, Modulo & Diophantine Formulas](#pattern-c-number-theory-modulo--diophantine-formulas)
  - [Pattern D: Pair Conditions -> Algebra + Sorting/Frequency](#pattern-d-pair-conditions---algebra--sortingfrequency)
  - [Pattern E: Binary Search on the Answer](#pattern-e-binary-search-on-the-answer)
- [Part 28. Same Problem, Multiple Models](#part-28-same-problem-multiple-models)
- [Part 29. Constraints → Expected Mathematics](#part-29-constraints--expected-mathematics)
- [Part 30. How to Discover the Equation](#part-30-how-to-discover-the-equation)
- [Part 31. How to Discover Invariants (Operation-Delta Analysis)](#part-31-how-to-discover-invariants-operation-delta-analysis)
- [Part 32. Brute Force → Math](#part-32-brute-force--math)
- [Part 33. Mathematical Proof Toolkit](#part-33-mathematical-proof-toolkit)
- [Part 34. 60-Second Contest Modeling Checklist](#part-34-60-second-contest-modeling-checklist)
- [Part 35. Rating-Wise Modeling Expectations](#part-35-rating-wise-modeling-expectations)
- [Part 36. Master Pattern Index](#part-36-master-pattern-index)
- [Part 37. Final Mathematical Modeling Workflow](#part-37-final-mathematical-modeling-workflow)

---

## Part 0. How to Mathematically Read a Codeforces Problem

```text
Story -> Objects -> Variables -> Allowed Operations -> Constraints
      -> Target -> Equation/Inequality/State -> Invariant
      -> Simplification -> Algorithm
```

**Step-by-step procedure**

1. **Delete nouns that carry no numbers.** "Alice buys apples" → "integer a".
2. **Name every quantity** (`n`, `A_i`, `x`). Decide which are given, which are unknown.
3. **Write the target** as one line: "find min k such that ...", "does there exist ...", "count pairs ...".
4. **Write each sentence of the statement as a formula.** Use the table below.
5. **Read constraints last, but always** – they tell you the allowed complexity and whether values overflow.
6. **If there is an operation**, write it as `before → after` and compute the delta (Part 11).
7. **Try to simplify**: isolate a variable, sort, take prefix sums, change coordinates.
8. **Name the form** (Part 26), then derive the algorithm and prove it.

### Translation table: statement language → mathematics

| Statement language | Mathematical interpretation |
|---|---|
| exactly | equality `=` |
| at least / no less than / not smaller | `>=` |
| at most / no more than / not exceeding | `<=` |
| strictly greater / less | `>` / `<` |
| between l and r inclusive | `l <= x <= r` |
| divisible by | `a % b == 0` |
| remainder / leaves r | `a mod m = r` |
| even / odd | `x mod 2 = 0 / 1` |
| last digit | `x mod 10` |
| number of digits | `floor(log10 x)+1` |
| equal number of A and B | `cntA = cntB` (i.e. `sum of +1/-1 = 0`) |
| pair (i,j), i<j | choose 2 / relation between two variables |
| unordered pair | `C(n,2)`; ordered → `n(n-1)` |
| consecutive / adjacent | `i` and `i+1` |
| subarray / segment | contiguous interval `[l,r]` → prefix sums |
| subsequence | ordered selection, `2^n` subsets |
| subset | bitmask / combinatorics |
| distance | absolute difference `\|x-y\|` |
| minimum number of operations | lower bound + construction |
| maximum possible minimum | binary search on answer |
| can transform / is it possible | reachability → invariant |
| repeatedly perform / any number of times | invariant / state transition |
| rearrange / permute | multiset (order irrelevant) |
| permutation of 1..n | each value exactly once: sum `n(n+1)/2`, counts all 1 |
| distribute / split among | partition / stars and bars |
| regardless of order | multiset, counting |
| simultaneously | one step transforms all elements at once |
| eventually / forever | convergence, cycle, invariant |
| minimize the maximum | binary search / balancing |
| cyclic / wraps around | modulo `n` |
| sum of all elements | `S = sum A_i` (often invariant) |
| product / ratio | prime factorization / cross-multiplication |
| gcd / common divisor | gcd |
| divisible by both | lcm |
| number of ways | combinatorics / DP |
| modulo 998244353 | counting with modular arithmetic |
| any valid answer | constructive; find necessary+sufficient conditions |
| the array is good if | write "good" as a formula first |
| sorted / non-decreasing | `a1<=a2<=...` |
| mex | smallest missing non-negative → frequency |
| at every moment / prefix | prefix condition, `prefix[i] >= 0` |
| symmetric / palindrome | `a_i = a_{n+1-i}` pairing |
| grid, move 1 step | Manhattan distance, parity of `x+y` |
| equalize / make all equal | average, sum invariant |

### Worked reading (1 minute)

#### 🌍 Real-World Example — Worked reading (1 minute)

**Think of this:** Imagine a small everyday situation where the quantities in **Worked reading (1 minute)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
"Alice has a candies, Bob has b. Each move: someone gives 1 candy to the other.
 Can they end with equal candies?"

Objects: a, b.   Operation: a-=1,b+=1 (or reverse).
Delta of a+b: -1+1 = 0     -> S = a+b invariant
Target: a' = b' = S/2      -> need S even
Answer: (a+b) % 2 == 0
```

---

## Part 1. Arithmetic Foundations

> **Who this is for:** you are new to competitive-programming math. Every idea below is explained in the same six steps, so you always know where to look:
>
> 1. **What is it?** (plain words)
> 2. **Picture** (everyday example)
> 3. **Rule** (the formula)
> 4. **Worked example** (numbers, step by step)
> 5. **Where it shows up in Codeforces**
> 6. **Watch out** (mistakes) + **C++ note**

### 1.0 Reading the symbols (cheat sheet)

| Symbol / word | Say it as | Meaning | Example |
|---|---|---|---|
| `a % b` | "a mod b" | remainder after dividing `a` by `b` | `17 % 5 = 2` |
| `a / b` (C++ ints) | "integer division" | quotient, fraction thrown away | `17 / 5 = 3` |
| `floor(x)` | "round down" | biggest integer `<= x` | `floor(3.7) = 3` |
| `ceil(x)` | "round up" | smallest integer `>= x` | `ceil(3.2) = 4` |
| `abs(x)` or `\|x\|` | "absolute value" | distance from 0, never negative | `\|-5\| = 5` |
| `a \| b` | "a divides b" | `b` is a multiple of `a` | `3 \| 12` |
| `Σ` | "sum of" | add many terms | `Σ A_i = A_1 + ... + A_n` |
| `≡` | "congruent" | same remainder | `17 ≡ 2 (mod 5)` |
| `=>` | "implies" | if left is true, right is true | |
| `<=>` | "if and only if" | both directions true | |
| `n!` | "n factorial" | `n * (n-1) * ... * 1` | `4! = 24` |

---

### 1.1 Quotient and remainder

#### 🌍 Real-World Example — Packing Chocolates into Boxes

**Think of this:** You have **17 chocolates**. Each box holds exactly **5 chocolates**. How many full boxes can you fill, and how many chocolates remain?

**Step-by-step with real numbers:**

1. 17 chocolates total
2. 5 chocolates / box
3. 17 = 5 × 3 + 2
4. Full boxes = 3
5. Left over = 2

**📐 Mathematical Model / Visual**

```text
17 items
   ↓ divide into groups of 5
[5] [5] [5] [2 left]
 q=3          r=2

17 = 5q + r = 5×3 + 2
```

**What the math means:** `a = bq + r` means: split `a` items into groups of size `b`; `q` is the number of complete groups and `r` is what is left.

**🧠 CF Recognition:** Words such as **groups, batches, complete sets, leftover, remainder, every k-th position** often suggest `/` and `%`.


**What is it?**
Dividing `a` by `b` gives two answers: how many *complete groups* of size `b` fit (quotient) and how many items are *left over* (remainder).

**Picture.** 17 candies, boxes hold 5.

```text
[ooooo] [ooooo] [ooooo]  oo
  box1    box2    box3   left over

quotient  q = 3   (full boxes)
remainder r = 2   (candies left)
```

**Rule.**

```text
a = q * b + r        with  0 <= r < b
q = a / b            (C++ integer division)
r = a % b
```

**Worked example.** `a = 17`, `b = 5`.

```text
Step 1: how many 5s fit in 17?   3  (3*5 = 15, 4*5 = 20 is too big)
Step 2: what is left?            17 - 15 = 2
Result: 17 = 3*5 + 2
```

**Where it shows up in CF.** Words like "groups of", "each box holds", "every k-th", "remainder", "cycle", "wraps around".

**Watch out.**
- In C++ `%` can be **negative** for negative numbers: `-7 % 5 = -2`.
- Safe non-negative remainder: `((a % m) + m) % m`.

---

### 1.2 Floor, ceiling and ceil-division

#### 🌍 Real-World Example — Taxis for a Group

**Think of this:** There are **17 people** and each taxi can carry **4 people**. How many taxis are needed?

**Step-by-step with real numbers:**

1. 4 taxis carry 16 people
2. 1 person is still waiting
3. That person needs another taxi
4. Answer = 5 taxis = ceil(17/4)

**📐 Mathematical Model / Visual**

```text
17 people
[4] [4] [4] [4] [1]
 ↑   ↑   ↑   ↑   ↑
 taxi             taxi

ceil(17/4)=5
```

**What the math means:** Ceiling means **how many whole containers/operations are required when a partial final group still counts**.

**🧠 CF Recognition:** Look for **minimum groups, buses, pages, packets, operations of size k** → often `ceil(n/k) = (n+k-1)/k`.


**What is it?**
`floor` rounds down, `ceil` rounds up. **Ceil division** answers: *"how many boxes do I need to hold everything?"*

**Picture.** 23 items, boxes of 5.

```text
[ooooo][ooooo][ooooo][ooooo][ooo..]
   1      2      3      4     5      -> 5 boxes needed
```

`23 / 5 = 4.6`, but you cannot buy 0.6 of a box, so round **up** to 5.

**Rule (memorize this).**

```text
ceil(a / b) = (a + b - 1) / b        for a >= 0, b > 0, integer division
```

**Why it works (two cases).**

```text
Case 1: a divides evenly, a = q*b
        (q*b + b - 1) / b = q + (b-1)/b = q      (the extra b-1 is too small to add a box)

Case 2: a = q*b + r with r > 0
        (q*b + r + b - 1) / b = q + 1            (r + b - 1 >= b, so it adds exactly one box)
```

**Worked example.** `a = 23`, `b = 5`.

```text
(23 + 5 - 1) / 5 = 27 / 5 = 5
```

**Where it shows up in CF.** "Minimum number of trips / boxes / moves so that everything is covered", "each move changes by at most K" (CF 1409A).

**Watch out.**
- `ceil(a / b)` in C++ with integers does **not** work, because `a / b` already rounded down.
- Do not use `double` for big numbers (`1e18` loses precision). Use the integer formula.

**Try yourself.** `ceil(10/3)`? `ceil(12/4)`? `ceil(1/100)`?
*(answers: 4, 3, 1)*

---

### 1.3 Absolute value, min and max

#### 🌍 Real-World Example — Distance Between Two Houses

**Think of this:** Two houses are at positions **3** and **8** on one straight road. How far apart are they?

**Step-by-step with real numbers:**

1. From 3 to 8 is 5 steps
2. 8 - 3 = 5
3. If order is reversed, 3 - 8 = -5, but distance cannot be negative
4. Use |8-3| = 5

**📐 Mathematical Model / Visual**

```text
0--1--2--3--4--5--6--7--8
         A              B
         <---- 5 ----->

Distance = |A-B|
```

**What the math means:** `|x-y|` removes direction and keeps only the **gap/distance**.

**🧠 CF Recognition:** Words **distance, difference, gap, moves by ±1, make equal** should trigger absolute difference.


**What is it?**
`|x|` is the **distance from 0**. `|x - y|` is the **distance between x and y** on a number line. `min` and `max` pick the smaller or larger of two.

**Picture.**

```text
 -5   -4   -3   -2   -1    0    1    2    3
  *                        |              *
 x=-5                     zero           y=3

|x - y| = |-5 - 3| = 8   (8 steps between them)
```

**Rules.**

```text
|x|        = x   if x >= 0,  else -x
|x - y|    = |y - x|                     (order does not matter)
max(a, b)  = (a + b + |a - b|) / 2
min(a, b)  = (a + b - |a - b|) / 2
max(a, b) + min(a, b) = a + b
```

**Worked example.** `a = 3`, `b = 8`.

```text
|a - b| = 5
max = (3 + 8 + 5) / 2 = 8
min = (3 + 8 - 5) / 2 = 3
```

**Where it shows up in CF.** "distance", "difference", "how far apart", "minimum total moves".

**Watch out.** In C++ use `abs()` for `int` and `llabs()` (or `abs` on `long long`) for `long long`.

---

### 1.4 Intervals and inequalities

#### 🌍 Real-World Example — 1.4 Intervals and inequalities

**Think of this:** Imagine a small everyday situation where the quantities in **1.4 Intervals and inequalities** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
An interval is a range of allowed values. `L <= x <= R` means "x is at least L **and** at most R".

**Picture.**

```text
constraint 1:   3 <= x <= 10        [3 ................. 10]
constraint 2:   6 <= x <= 15                [6 ................. 15]
both together:  6 <= x <= 10                [6 ........ 10]
```

**Rule (intersection).**

```text
L = max(L1, L2)
R = min(R1, R2)
if L > R  ->  no valid x (empty)
else      ->  x can be any value from L to R
```

**Worked example.** Ranges `[3,10]` and `[6,15]`: `L = max(3,6) = 6`, `R = min(10,15) = 10` -> valid `x` in `[6,10]` (5 integers: 6,7,8,9,10, count = `R - L + 1`).

**Where it shows up in CF.** "at least", "at most", "between", "no more than". Each phrase gives one bound.

| Phrase | Inequality |
|---|---|
| at least k | `x >= k` |
| at most k | `x <= k` |
| strictly more than k | `x > k` |
| between l and r inclusive | `l <= x <= r` |

**Watch out.** Number of integers in `[L, R]` is `R - L + 1` (not `R - L`).

---

### 1.5 Powers, logarithms and size estimates

#### 🌍 Real-World Example — 1.5 Powers, logarithms and size estimates

**Think of this:** Imagine a small everyday situation where the quantities in **1.5 Powers, logarithms and size estimates** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
`2^k` = 2 multiplied by itself `k` times. `log2(n)` answers: *"how many times can I double 1 to reach n?"* or *"how many times can I halve n until 1?"*

**Picture.**

```text
1 -> 2 -> 4 -> 8 -> 16 -> 32 -> ...     (k doublings give 2^k)
1,000,000,000 halved again and again reaches 1 after about 30 steps
```

**Numbers to memorize.**

```text
2^10 ~ 1e3      2^20 ~ 1e6      2^30 ~ 1e9      2^60 ~ 1e18
log2(1e9) ~ 30      log2(1e18) ~ 60
```

**Where it shows up in CF.** "repeatedly doubles", "repeatedly halves", "each step divides by 2" -> the process takes only about **30 to 60 steps**, so you may simulate it.

**Watch out.** `1 << 31` overflows `int`. Use `1LL << b` for `b >= 31`.

---

### 1.6 Overflow: choosing `int` or `long long`

#### 🌍 Real-World Example — 1.6 Overflow: choosing int or long long

**Think of this:** Imagine a small everyday situation where the quantities in **1.6 Overflow: choosing int or long long** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
`int` holds up to about `2.1e9`. If your answer can be bigger, the number wraps around to garbage.

**Rule.** Estimate the *largest possible value* before choosing the type.

| Quantity (n = 2e5, values up to 1e9) | Biggest value | Type |
|---|---|---|
| `n * (n + 1) / 2` | about 2e10 | `long long` |
| sum of `n` numbers of size 1e9 | about 2e14 | `long long` |
| product of two numbers up to 1e9 | about 1e18 | `long long` (fits, max 9.2e18) |
| number of pairs `n(n-1)/2` | about 2e10 | `long long` |
| `a + b` with `a, b <= 1e9` | 2e9 | `long long` (close to the `int` limit) |

**Worked example.**

```cpp
int a = 1000000000, b = 1000000000;
long long bad  = a * b;           // WRONG: multiplied as int first, overflows
long long good = 1LL * a * b;     // RIGHT: 1LL makes the multiplication long long
```

**Rule of thumb.** If a formula multiplies two input-size numbers, use `long long`.

---

### 1.7 Rounding without decimals

#### 🌍 Real-World Example — 1.7 Rounding without decimals

**Think of this:** Imagine a small everyday situation where the quantities in **1.7 Rounding without decimals** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
Sometimes a statement says "round to the nearest integer". Use integers only.

```text
round(a / b) = (2*a + b) / (2*b)      for a >= 0, b > 0
```

**Worked example.** `round(7 / 2) = (14 + 2) / 4 = 4` (3.5 rounds to 4).

---

### 1.8 Section summary (what to remember)

```text
"boxes needed"                 -> ceil division  (a + b - 1) / b
"remainder", "every k-th"      -> a % b
"distance"                     -> |x - y|
"at least / at most"           -> intervals, intersect with max/min
"doubles / halves"             -> about 30 to 60 steps
"answer may be large"          -> long long
```

**Practice problems (in this file):** CF 4A (parity), CF 1409A (ceil), CF 1476A (ceil + lower bound).


---

## Part 2. Algebra for Competitive Programming

> **Why algebra?** Many statements hide an equation. If you can move terms around, an `O(n^2)` loop over pairs often becomes an `O(n)` formula.
> Every form below has: **Idea / Rule / Worked example / Where it shows up / Watch out.**

### Algebra Form 1. Rearranging equations (sum and difference)

#### 🌍 Real-World Example — Algebra Form 1. Rearranging equations (sum and difference)

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 1. Rearranging equations (sum and difference)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Idea.** If you know a sum and a difference of two numbers, you can find both numbers.

**Rule.**

```text
x + y = S
x - y = D
-----------
add both:        2x = S + D    ->  x = (S + D) / 2
subtract them:   2y = S - D    ->  y = (S - D) / 2
```

**Checks.**

```text
S + D must be even          (so x is an integer)
x >= 0 and y >= 0           (if the numbers must be non-negative)
```

**Worked example.** `S = 10`, `D = 4`.

```text
x = (10 + 4) / 2 = 7
y = (10 - 4) / 2 = 3         check: 7 + 3 = 10, 7 - 3 = 4
```

**Where it shows up.** "two numbers with given sum and difference", "sum and max-min are known".

---

### Algebra Form 2. Isolating a variable

#### 🌍 Real-World Example — Algebra Form 2. Isolating a variable

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 2. Isolating a variable** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Idea.** If an unknown appears once, solve for it. This replaces a loop over all values of the unknown.

**Rule.**

```text
a*x + b = c
a*x     = c - b
x       = (c - b) / a        valid only if (c - b) % a == 0
```

**Worked example.** `3x + 5 = 20` -> `3x = 15` -> `x = 5`. For `3x + 5 = 21`: `16 % 3 != 0`, so no integer `x`.

**Watch out.** Always check divisibility, and `a != 0`.

---

### Algebra Form 3. Substitution

#### 🌍 Real-World Example — Algebra Form 3. Substitution

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 3. Substitution** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Idea.** Use one equation to remove a variable.

**Worked example.** Minimize `3x + 5y` with `x + y = n`.

```text
y = n - x
cost = 3x + 5(n - x) = 5n - 2x
```

The cost only depends on `x` and decreases as `x` grows, so take `x` as large as allowed.

---

### Algebra Form 4. Difference of squares

#### 🌍 Real-World Example — Square Garden with a Square Pond

**Think of this:** A square garden has side **4 m**. A square pond inside has side **1 m**. What area remains outside the pond?

**Step-by-step with real numbers:**

1. Garden area = 4² = 16
2. Pond area = 1² = 1
3. Remaining area = 16 - 1 = 15
4. Factor form: (4-1)(4+1)=3×5=15

**📐 Mathematical Model / Visual**

```text
Outer square: 4×4
+-----------+
|           |
|  +--+     |   remove 1×1 pond
|  +--+     |
|           |
+-----------+

remaining = a²-b²
          = (a-b)(a+b)
```

**What the math means:** A difference of two square areas can be turned into a **product of two factors**.

**🧠 CF Recognition:** If CF gives `N = a²-b²`, think **factor pair**: `N=(a-b)(a+b)` and check parity.


**Rule.**

```text
a^2 - b^2 = (a - b) * (a + b)
```

**Idea.** If `N = a^2 - b^2`, then `N` splits into two factors `d = a - b` and `e = a + b`.

```text
N = d * e                 (d <= e, d and e have the same parity)
a = (d + e) / 2
b = (e - d) / 2
```

**Worked example.** `N = 15 = 3 * 5`: `a = 4`, `b = 1`, check `16 - 1 = 15`.

---

### Algebra Form 5. Expansions

#### 🌍 Real-World Example — Every Unique Pair of Friends

**Think of this:** Alice, Bob and John have values **2, 3, 4**. Add the product for every unique pair.

**Step-by-step with real numbers:**

1. Alice×Bob = 2×3 = 6
2. Alice×John = 2×4 = 8
3. Bob×John = 3×4 = 12
4. Pair total = 26
5. (2+3+4)²=81
6. Self-squares = 4+9+16=29
7. 81-29=52 counts every pair twice
8. 52/2=26

**📐 Mathematical Model / Visual**

```text
(2+3+4)²
= 2²+3²+4²
  +2(2×3 + 2×4 + 3×4)

81 = 29 + 2(PAIR_SUM)
PAIR_SUM = (81-29)/2 = 26
```

**What the math means:** Squaring the total produces **self-products + both orders of every cross-product**. Remove self-products and divide by 2.

**🧠 CF Recognition:** Statement says **for every pair `i<j`, add `A[i]*A[j]`** → use `((ΣA)²-ΣA²)/2` instead of O(n²).


```text
(a + b)^2 = a^2 + 2ab + b^2
(a - b)^2 = a^2 - 2ab + b^2
(a + b + c)^2 = a^2 + b^2 + c^2 + 2(ab + bc + ca)
```

**Useful consequence (sum of all pair products).**

```text
sum over i<j of A_i * A_j  =  ( (sum A)^2 - (sum of A_i^2) ) / 2
```

**Worked example.** `A = [1, 2, 4]`.

```text
sum A = 7, sum of squares = 1 + 4 + 16 = 21
formula: (49 - 21) / 2 = 14
direct:  1*2 + 1*4 + 2*4 = 2 + 4 + 8 = 14
```

An `O(n^2)` pair loop becomes `O(n)`.

---

### Algebra Form 6. Pairwise sums

#### 🌍 Real-World Example — Algebra Form 6. Pairwise sums

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 6. Pairwise sums** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Goal.** Compute `sum over all pairs i<j of (A_i - A_j)^2` fast.

**Result.**

```text
sum_{i<j} (A_i - A_j)^2  =  n * (sum of A_i^2)  -  (sum of A_i)^2
```

**Derivation, step by step.** Let `Q = sum of A_i^2` and `S = sum of A_i`.

```text
Step 1: expand each pair
        (A_i - A_j)^2 = A_i^2 + A_j^2 - 2*A_i*A_j

Step 2: add the squares over all pairs
        each A_k^2 appears in (n - 1) pairs
        sum of (A_i^2 + A_j^2) = (n - 1) * Q

Step 3: add the cross terms over all pairs
        sum of 2*A_i*A_j = S^2 - Q            (from Form 5)

Step 4: subtract
        (n - 1)*Q - (S^2 - Q) = n*Q - S^2
```

**Check.** `A = [1, 2, 4]`, `n = 3`.

```text
direct:  (1-2)^2 + (1-4)^2 + (2-4)^2 = 1 + 9 + 4 = 14
formula: 3 * 21 - 7^2 = 63 - 49 = 14
```

**Second useful identity: sum of absolute differences.** Sort `A`. With 1-indexed position `k`:

```text
sum_{i<j} |A_i - A_j|  =  sum over k of  A_k * (2k - n - 1)
```

**Why.** In sorted order, `A_k` is the larger element in `k - 1` pairs (added) and the smaller in `n - k` pairs (subtracted). Net count: `(k - 1) - (n - k) = 2k - n - 1`.

**Check.** `A = [1, 2, 4]`.

```text
k=1: 1 * (2 - 3 - 1) = -2
k=2: 2 * (4 - 3 - 1) =  0
k=3: 4 * (6 - 3 - 1) =  8
total = 6      direct: 1 + 3 + 2 = 6
```

---

### Algebra Form 7. Linear equation `ax + by = c`

#### 🌍 Real-World Example — Algebra Form 7. Linear equation ax + by = c

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 7. Linear equation ax + by = c** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Rule.** Integer solutions exist exactly when `gcd(a, b)` divides `c`. Details in Part 20.

**Worked example.** `4x + 6y = 10`: `gcd = 2` divides 10 -> solvable (`x=1, y=1`). `4x + 6y = 9`: `2` does not divide 9 -> impossible.

---

### Algebra Form 8. Systems of equations

#### 🌍 Real-World Example — Algebra Form 8. Systems of equations

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 8. Systems of equations** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Idea.** `k` independent equations fix `k` unknowns. If you have more unknowns than equations, one variable stays free: loop over that one only (within its bounds).

---

### Algebra Form 9. Inequalities (intersection of constraints)

#### 🌍 Real-World Example — Algebra Form 9. Inequalities (intersection of constraints)

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 9. Inequalities (intersection of constraints)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
x >= L,   x <= R        ->  L <= x <= R
several ranges          ->  L = max of lower bounds,  R = min of upper bounds
empty if L > R
"at least k of them"    ->  count of true conditions >= k
```

---

### Algebra Form 10. Bounding (lower bound + construction)

#### 🌍 Real-World Example — Algebra Form 10. Bounding (lower bound + construction)

**Think of this:** Imagine a small everyday situation where the quantities in **Algebra Form 10. Bounding (lower bound + construction)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Idea.** Many "minimum" or "maximum" answers are found in two steps.

```text
Step 1 (lower bound):  prove the answer cannot be smaller than X.
Step 2 (construction): build a solution that achieves exactly X.
Result:                the answer is X.
```

**Worked example (CF 1263A).** Piles `a, b, c`, each day eat one candy from two different piles.

```text
Bound 1: each day uses 2 candies          ->  days <= S / 2
Bound 2: the largest pile M needs partners ->  days <= S - M
answer = min(S / 2, S - M)      (and a construction shows it is reachable)
```


---

## Part 3. Number Theory Foundations

> Same six-step layout as Part 1: **What is it? / Picture / Rule / Worked example / Where it shows up / Watch out.**

### 3.1 Divisors and multiples

#### 🌍 Real-World Example — 3.1 Divisors and multiples

**Think of this:** Imagine a small everyday situation where the quantities in **3.1 Divisors and multiples** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
`d` is a **divisor** of `n` if `n` splits into equal groups of size `d` with nothing left over. Then `n` is a **multiple** of `d`.

**Picture.** 12 items in groups of 4:

```text
[oooo] [oooo] [oooo]     3 groups, 0 left   -> 4 divides 12
```

**Rule.**

```text
d divides n   <=>   n % d == 0   <=>   n = d * k  for some integer k
```

**Worked example.** Divisors of 12: try `d = 1..12`, keep those where `12 % d == 0`: **1, 2, 3, 4, 6, 12**.

**Fast way (O(√n)).** Divisors come in pairs `(d, n/d)`. Only test `d` up to `√n`.

```cpp
for (long long d = 1; d * d <= n; d++) {
    if (n % d == 0) {
        // d is a divisor
        if (d != n / d) {
            // n / d is a different divisor
        }
    }
}
```

**Where it shows up in CF.** "divisible by", "every k-th", "equal groups", "period".

**Watch out.** When `d * d == n`, do not count the same divisor twice.

---

### 3.2 Prime numbers and factorization

#### 🌍 Real-World Example — 3.2 Prime numbers and factorization

**Think of this:** Imagine a small everyday situation where the quantities in **3.2 Prime numbers and factorization** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?**
A **prime** has exactly two divisors: 1 and itself (2, 3, 5, 7, 11, ...). Every number is a product of primes in exactly one way.

**Picture.**

```text
60 = 2 * 30 = 2 * 2 * 15 = 2 * 2 * 3 * 5      ->   60 = 2^2 * 3 * 5
```

**Rules.**

```text
number of divisors of n = (e1 + 1) * (e2 + 1) * ...      where n = p1^e1 * p2^e2 * ...
n is a perfect square    <=>   every exponent is even   <=>   n has an odd number of divisors
```

**Worked example.** `60 = 2^2 * 3^1 * 5^1` -> divisors = `(2+1)(1+1)(1+1) = 12`.

**Trial division (factor one number, O(√n)).**

```cpp
for (long long p = 2; p * p <= n; p++) {
    while (n % p == 0) {
        // p is a prime factor
        n /= p;
    }
}
if (n > 1) {
    // what remains is one big prime factor
}
```

**Sieve with smallest prime factor (many numbers up to N).**

```cpp
vector<int> spf(N + 1, 0);
for (int i = 2; i <= N; i++) {
    if (spf[i] != 0) continue;           // already marked: i is composite
    for (int j = i; j <= N; j += i)
        if (spf[j] == 0) spf[j] = i;     // smallest prime factor of j
}
// factor x quickly: while (x > 1) { p = spf[x]; x /= p; }
```

**Where it shows up in CF.** "prime", "number of divisors", "odd divisor" (CF 1475A), "perfect square".

**Watch out.** 1 is **not** prime.

---

### 3.3 GCD and LCM

#### 🌍 Real-World Example — Cutting Ribbons / Repeating Buses

**Think of this:** Ribbon lengths are **48 cm** and **18 cm**. You want the longest equal pieces with no waste. Separately, buses arrive every **4** and **6** minutes.

**Step-by-step with real numbers:**

1. Ribbon: largest length dividing both = gcd(48,18)=6 cm
2. Bus: first time divisible by both 4 and 6 = lcm(4,6)=12 min

**📐 Mathematical Model / Visual**

```text
CUTTING: 48 -> [6][6][6][6][6][6][6][6]
         18 -> [6][6][6]
         largest common piece = GCD

REPEATING: bus A 0--4--8--12
           bus B 0-----6-----12
                         meet = LCM
```

**What the math means:** GCD models the **largest common unit**; LCM models the **first common repetition/time**.

**🧠 CF Recognition:** **equal largest pieces / divides all** → GCD. **events repeat / first together** → LCM.


**What is it?**
- **gcd(a, b)** = the biggest number that divides both.
- **lcm(a, b)** = the smallest positive number that both divide.

**Picture.** `a = 4`, `b = 6`.

```text
multiples of 4:  4  8  12  16  20  24 ...
multiples of 6:  6  12  18  24 ...
first common multiple: 12   -> lcm = 12
common divisors: 1, 2      -> gcd = 2
```

**Rules.**

```text
gcd(a, b) = gcd(b, a % b)          (Euclid: repeat until the second is 0)
gcd(a, b) * lcm(a, b) = a * b
lcm(a, b) = a / gcd(a, b) * b      (divide first: avoids overflow)
gcd(a, b) = gcd(a, b - a)          (subtracting keeps gcd)
```

**Worked example (Euclid).** `gcd(48, 18)`:

```text
gcd(48, 18) -> gcd(18, 12) -> gcd(12, 6) -> gcd(6, 0) = 6
```

**Where it shows up in CF.**

| Statement says | Think |
|---|---|
| "divisible by both A and B" | multiples of `lcm(A, B)` |
| "split into equal parts for both" | part size divides `gcd(A, B)` |
| operation `A_i -= A_j` | `gcd` of the array never changes |
| both numbers shift by the same amount | `a - b` fixed, gcd divides it (CF 1543A) |
| `lcm(a,b) + gcd(a,b) = x` | try `a = 1` (CF 1325A) |

**Watch out.** `a * b` can overflow; use `a / gcd(a,b) * b`.

**Try yourself.** `gcd(30, 12)`? `lcm(6, 8)`? *(answers: 6, 24)*

---

### 3.4 Extra facts

#### 🌍 Real-World Example — 3.4 Extra facts

**Think of this:** Imagine a small everyday situation where the quantities in **3.4 Extra facts** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


- **Sum of divisors:** `prod (p^(e+1) - 1) / (p - 1)` over the factorization.
- **Bezout:** `a*x + b*y = c` has integer solutions exactly when `gcd(a, b)` divides `c` (Part 20).
- **Coprime:** `gcd(a, b) = 1`. Consecutive numbers are always coprime.

**Problems using this part:** CF 1543A, 1325A, 1475A, 1474B.


---

## Part 4. Modular Arithmetic

> **What is it in one sentence?** Working with **remainders** instead of full numbers.

### 4.1 The idea: clock arithmetic

#### 🌍 Real-World Example — Clock Wrapping

**Think of this:** It is **9 o'clock**. What time is it 5 hours later on a 12-hour clock?

**Step-by-step with real numbers:**

1. 9+5=14
2. Clock has only 12 positions
3. 14 wraps to 2
4. 14 mod 12 = 2

**📐 Mathematical Model / Visual**

```text
       12
   11      1
10           2  <- 9+5
9             3
 8           4
   7   6   5

(9+5) mod 12 = 2
```

**What the math means:** Modulo means **wrap around after a fixed cycle length**.

**🧠 CF Recognition:** Look for **circular arrays, clocks, weekdays, repeated positions, remainder classes**.


**Picture.** A 12-hour clock. 9 o'clock + 5 hours = 2 o'clock, because `14 % 12 = 2`.

```text
   11 12  1
 10        2
 9          3        9 + 5 = 14  ->  14 % 12 = 2
  8        4
    7  6  5
```

**Rules.**

```text
a ≡ b (mod m)   means   a % m == b % m   means   m divides (a - b)
(a + b) % m = ((a % m) + (b % m)) % m
(a * b) % m = ((a % m) * (b % m)) % m
(a - b) % m = ((a % m) - (b % m) + m) % m        <- the + m keeps it non-negative
```

**Worked example.** `m = 7`: `(23 + 40) % 7`.

```text
23 % 7 = 2,   40 % 7 = 5,   (2 + 5) % 7 = 0      (and 63 % 7 = 0 ✓)
```

**Where it shows up in CF.** "remainder", "divisible", "last digit" (`% 10`), "even/odd" (`% 2`), "every k-th", "cyclic", "answer modulo 998244353".

**Recognition table.**

| Statement clue | Math |
|---|---|
| "same remainder" | `(A - B) % M == 0` |
| "repeated cyclic process" | position `(s + t*d) % n` |
| "sum of subarray divisible by m" | equal prefix remainders |
| "answer modulo p" | apply `% p` after every step |

**Watch out.** Negative remainders in C++, and `a * b` overflow before `%` (use `long long`).

### 4.2 Fast exponentiation

#### 🌍 Real-World Example — 4.2 Fast exponentiation

**Think of this:** Imagine a small everyday situation where the quantities in **4.2 Fast exponentiation** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**What is it?** Compute `a^e mod m` in `O(log e)` steps by squaring.

**Picture.** `3^13`: write 13 in binary `1101` -> `3^13 = 3^8 * 3^4 * 3^1`.

```cpp
long long power(long long a, long long e, long long mod) {
    long long result = 1;
    a %= mod;
    while (e > 0) {
        if (e & 1) result = result * a % mod;   // current bit is 1
        a = a * a % mod;                        // square the base
        e >>= 1;                                // next bit
    }
    return result;
}
```

### 4.3 Division modulo a prime (inverse)

#### 🌍 Real-World Example — Clock Wrapping

**Think of this:** It is **9 o'clock**. What time is it 5 hours later on a 12-hour clock?

**Step-by-step with real numbers:**

1. 9+5=14
2. Clock has only 12 positions
3. 14 wraps to 2
4. 14 mod 12 = 2

**📐 Mathematical Model / Visual**

```text
       12
   11      1
10           2  <- 9+5
9             3
 8           4
   7   6   5

(9+5) mod 12 = 2
```

**What the math means:** Modulo means **wrap around after a fixed cycle length**.

**🧠 CF Recognition:** Look for **circular arrays, clocks, weekdays, repeated positions, remainder classes**.


```text
Fermat:    a^(p-1) ≡ 1 (mod p)        (p prime, a not a multiple of p)
inverse:   a^(-1) ≡ a^(p-2) (mod p)
a / b  mod p  =  a * power(b, p - 2, p) % p
```

### 4.4 Prefix remainders and pigeonhole

#### 🌍 Real-World Example — Bank Account Statement

**Think of this:** Your cumulative deposits after days 1..5 are built from daily amounts `[10,20,5,15,10]`. How much was deposited on days 2..4?

**Step-by-step with real numbers:**

1. Prefix totals = [10,30,35,50,60]
2. Total through day 4 = 50
3. Total before day 2 = 10
4. Days 2..4 = 50-10 = 40

**📐 Mathematical Model / Visual**

```text
daily :  10   20    5   15   10
prefix:  10   30   35   50   60
               <------>
range 2..4 = P[4]-P[1] = 50-10 = 40
```

**What the math means:** A prefix stores **everything from the beginning up to i**. Subtract two prefixes to isolate the middle interval.

**🧠 CF Recognition:** Many **static range-sum queries** or equations involving subarray sums should trigger prefix sums.


**Idea.** Prefix sums `P_0 = 0, P_1, ..., P_n`. A subarray `(l, r]` has sum divisible by `m` exactly when `P_r % m == P_l % m`.

**Pigeonhole.** If `n >= m`, then `n + 1` prefix values sit in only `m` remainder boxes, so two must match. So a non-empty subarray with sum divisible by `m` **always** exists (CF 577B).

**Problems using this part:** CF 577B, 1475B, 1374A.


---

## Part 5. Parity

> **What is it in one sentence?** Parity = "is it even or odd?" = the remainder mod 2. Many hard-looking problems collapse into one parity check.

### 5.1 The rules

#### 🌍 Real-World Example — 5.1 The rules

**Think of this:** Imagine a small everyday situation where the quantities in **5.1 The rules** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
even + even = even        odd + odd = even        even + odd = odd
even * anything = even    odd * odd = odd
sum of numbers is even  <=>  the count of odd numbers is even
```

**Picture.**

```text
2 + 4 = 6     (even)         3 + 5 = 8     (even)
2 + 3 = 5     (odd)          3 * 5 = 15    (odd)
```

### 5.2 How parity turns into a solution

#### 🌍 Real-World Example — 5.2 How parity turns into a solution

**Think of this:** Imagine a small everyday situation where the quantities in **5.2 How parity turns into a solution** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Steps to use every time.**

1. Find the quantity that the operation changes.
2. Ask: does one operation flip its parity, or keep it?
3. Compare the parity of the start and of the target.

**Worked example (CF 4A Watermelon).** Split `w` into two positive even parts.

```text
even + even = even         ->  w must be even
each part >= 2             ->  w >= 4
w = 8 -> (2, 6) works;   w = 2 -> impossible;   w = 7 -> odd, impossible
```

**Worked example (invariant).** "Add 2 to any element any number of times. Can you make all elements equal?"

```text
adding 2 never changes A_i % 2
so elements with different parity can never become equal
answer: YES only if all elements already have the same parity (and then more checks)
```

### 5.3 Where it shows up in CF

| Clue | Parity idea |
|---|---|
| grid step by step | each step flips `(x + y) % 2` |
| add/subtract 2 | parity of each element is fixed |
| swap two elements | parity of the permutation flips |
| pair elements | need an even count |

**Watch out.** In C++ `x % 2 == 1` fails for negative `x`; use `x % 2 != 0` or `x & 1`.

**Problems using this part:** CF 4A, 1401A.


---

## Part 6. Counting & Combinatorics

> **What is it in one sentence?** Counting *how many ways* without listing them.

### 6.1 Two basic rules

#### 🌍 Real-World Example — 6.1 Two basic rules

**Think of this:** Imagine a small everyday situation where the quantities in **6.1 Two basic rules** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Addition rule.** If choices are separate cases, **add**.
**Multiplication rule.** If choices happen one after another, **multiply**.

```text
3 shirts and 2 pants                    ->  3 * 2 = 6 outfits           (multiply)
either 3 shirts or 2 hats (not both)    ->  3 + 2 = 5 choices           (add)
```

### 6.2 Factorial, permutation, combination

#### 🌍 Real-World Example — 6.2 Factorial, permutation, combination

**Think of this:** Imagine a small everyday situation where the quantities in **6.2 Factorial, permutation, combination** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
n! = n * (n-1) * ... * 1                    5! = 120
P(n, k) = n! / (n-k)!                        ordered choices of k from n
C(n, k) = n! / (k! * (n-k)!)                 unordered choices of k from n
```

**Worked example.** Choose 2 people from 5 for a team (order does not matter): `C(5,2) = 5*4/2 = 10`.

### 6.3 Pairs (the most useful formula in CF)

#### 🌍 Real-World Example — Handshakes at a Meeting

**Think of this:** Four people A, B, C, D each shake hands with every other person exactly once. How many handshakes occur?

**Step-by-step with real numbers:**

1. A shakes with B,C,D → 3
2. B still needs C,D → 2
3. C still needs D → 1
4. Total = 3+2+1 = 6
5. Formula = 4×3/2 = 6

**📐 Mathematical Model / Visual**

```text
A -- B
| \  |
|  \ |
C -- D
(+ the two diagonals)

ordered counts = 4×3
but AB and BA are same handshake
=> 4×3/2 = 6
```

**What the math means:** `n(n-1)/2` counts **unordered pairs of distinct objects**.

**🧠 CF Recognition:** Words **choose two, every pair, handshake, connect every two, i<j** → think `C(n,2)`.


```text
C(n, 2) = n * (n - 1) / 2       number of unordered pairs from n items
```

**Picture.** 4 items A, B, C, D:

```text
AB AC AD BC BD CD      ->  6 pairs = 4*3/2
```

**Equal pairs from frequency.** If a value appears `f` times, pairs of equal elements = `f * (f - 1) / 2`.

```text
[1, 1, 2, 2, 2]   ->   value 1: 1 pair,  value 2: 3 pairs   ->  total 4
```

**Use `long long`.** `C(2e5, 2)` is about `2e10`.

### 6.4 Complement counting

#### 🌍 Real-World Example — 6.4 Complement counting

**Think of this:** Imagine a small everyday situation where the quantities in **6.4 Complement counting** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
desired = total - bad
```

Use it when "at least one" is hard but "none" is easy.

### 6.5 Stars and bars (distribute identical objects)

#### 🌍 Real-World Example — 6.5 Stars and bars (distribute identical objects)

**Think of this:** Imagine a small everyday situation where the quantities in **6.5 Stars and bars (distribute identical objects)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
x1 + x2 + ... + xk = n,   xi >= 0     ->   C(n + k - 1, k - 1)
same but xi >= 1                       ->   C(n - 1, k - 1)
```

**Picture.** `n = 5` stars into `k = 3` boxes using 2 bars:

```text
***|*|*     ->  (3, 1, 1)
```

### 6.6 Inclusion-Exclusion

#### 🌍 Real-World Example — Students Playing Two Sports

**Think of this:** In a class, **10** students play football, **8** play cricket, and **3** play both. How many play at least one?

**Step-by-step with real numbers:**

1. 10+8=18 counts the 3 dual-sport students twice
2. Subtract that overlap once
3. 18-3=15

**📐 Mathematical Model / Visual**

```text
Football:  [------]
             [XXX]  <- both = 3
Cricket:        [------]

10 + 8 - 3 = 15
```

**What the math means:** When adding overlapping groups, the intersection gets counted twice, so subtract it once.

**🧠 CF Recognition:** Words **A or B, at least one, union, overlap** → inclusion-exclusion.


```text
|A ∪ B|     = |A| + |B| - |A ∩ B|
|A ∪ B ∪ C| = |A| + |B| + |C| - |A∩B| - |A∩C| - |B∩C| + |A∩B∩C|
```

**Worked example.** Numbers `1..10` divisible by 2 or 3: `5 + 3 - 1 = 7`.

### 6.7 Pigeonhole and contribution

#### 🌍 Real-World Example — Birth Months

**Think of this:** There are **13 people** but only **12 birth months**. Show that two people must share a birth month.

**Step-by-step with real numbers:**

1. 12 months are 12 boxes
2. Place each person into their birth-month box
3. After 12 people, each box could have one
4. The 13th person must enter an occupied box

**📐 Mathematical Model / Visual**

```text
people: 13 objects
months: 12 boxes

[Jan][Feb]...[Dec]
  1    1       1
+ one extra person -> collision guaranteed
```

**What the math means:** More objects than containers guarantees at least one container receives multiple objects.

**🧠 CF Recognition:** Look for **guarantee duplicate / same remainder / same category** with limited buckets.


- **Pigeonhole:** `n + 1` objects in `n` boxes -> some box holds 2.
- **Contribution technique:** instead of looping over all subarrays/pairs, ask *"for each element, in how many structures does it appear?"* and add up.

```text
element at index i (1-indexed) lies in  i * (n - i + 1)  subarrays
sum of all subarray sums = Σ A_i * i * (n - i + 1)
```

- **Multiset permutations:** arrangements of `n` items with repeats: `n! / (c1! c2! ...)`.
- **Modular combinatorics:** precompute `fact` and `inv_fact`, then `C(n,k) = fact[n] * inv_fact[k] * inv_fact[n-k] mod p`.

**Watch out.** Ordered vs unordered pairs; forgetting `long long`.

**Problems using this part:** CF 1520D, 1538C, 1324D.


---

## Part 7. Sequences & Series

> **What is it?** A sequence is a list of numbers that follows a rule. Two rules appear constantly: *add the same amount each time* (arithmetic) and *multiply by the same amount each time* (geometric).

### 7.1 Arithmetic progression (AP)

#### 🌍 Real-World Example — 7.1 Arithmetic progression (AP)

**Think of this:** Imagine a small everyday situation where the quantities in **7.1 Arithmetic progression (AP)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Picture.** `2, 5, 8, 11, 14` (add 3 each time).

**Rules.**

```text
n-th term:   a_n = a + (n - 1) * d
sum of n:    S_n = n * (first + last) / 2
special:     1 + 2 + ... + n = n * (n + 1) / 2
```

**Worked example.** Sum of `1..100`.

```text
pair first and last:  1 + 100 = 101,  2 + 99 = 101, ...   50 pairs
S = 50 * 101 = 5050         formula: 100 * 101 / 2 = 5050
```

**Where it shows up.** "day 1 gets 1, day 2 gets 2, ...", "each step adds one more than the last" (triangular numbers). To find the smallest `k` with `k(k+1)/2 >= n`, note `k` is about `sqrt(2n)`.

### 7.2 Geometric progression (GP)

#### 🌍 Real-World Example — 7.2 Geometric progression (GP)

**Think of this:** Imagine a small everyday situation where the quantities in **7.2 Geometric progression (GP)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Picture.** `1, 2, 4, 8, 16` (multiply by 2).

**Rules.**

```text
n-th term:   a_n = a * r^(n-1)
sum of n:    S_n = a * (r^n - 1) / (r - 1)         (r != 1)
special:     1 + 2 + 4 + ... + 2^(k-1) = 2^k - 1
```

**Where it shows up.** "doubles each step" -> only about `log2(value)` steps.

### 7.3 Other sums to remember

#### 🌍 Real-World Example — 7.3 Other sums to remember

**Think of this:** Imagine a small everyday situation where the quantities in **7.3 Other sums to remember** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
1^2 + 2^2 + ... + n^2 = n(n+1)(2n+1) / 6
n/1 + n/2 + ... + n/n  is about  n * ln(n)         (why sieve loops cost O(n log n))
```

**Watch out.** `2^k` overflows at `k >= 63`; GP formula divides by `r - 1`.

---

## Part 8. Coordinate & Distance Mathematics

> **What is it?** Turning "how far", "which cells", "where do they meet" into formulas on numbers.

### 8.1 Distance formulas

#### 🌍 Real-World Example — Walking City Blocks

**Think of this:** You are at `(1,2)` and a shop is at `(4,6)`. Streets allow only horizontal/vertical movement.

**Step-by-step with real numbers:**

1. Horizontal difference = |4-1| = 3
2. Vertical difference = |6-2| = 4
3. Total shortest walk = 3+4 = 7

**📐 Mathematical Model / Visual**

```text
(1,6) ------- (4,6) SHOP
  |              ^
  | 4 blocks     | 3 horizontal
  |
(1,2) START

Manhattan = |dx|+|dy| = 7
```

**What the math means:** When diagonal movement is forbidden, horizontal and vertical costs simply add.

**🧠 CF Recognition:** Grid with **up/down/left/right moves** → Manhattan distance.


```text
number line:     |x - y|
Manhattan:       |x1 - x2| + |y1 - y2|          (grid moves up/down/left/right)
Chebyshev:       max(|x1 - x2|, |y1 - y2|)      (king moves)
Euclidean^2:     (x1 - x2)^2 + (y1 - y2)^2      (compare squares, skip sqrt)
```

**Worked example.** Points `(1, 2)` and `(4, 6)`: Manhattan `= 3 + 4 = 7`, Chebyshev `= max(3, 4) = 4`.

### 8.2 Interval overlap

#### 🌍 Real-World Example — 8.2 Interval overlap

**Think of this:** Imagine a small everyday situation where the quantities in **8.2 Interval overlap** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
[l1, r1] and [l2, r2]
intersection = [max(l1, l2), min(r1, r2)]
non-empty when max(l1, l2) <= min(r1, r2)
```

### 8.3 Median minimizes total distance

#### 🌍 Real-World Example — Friends Choosing a Meeting House

**Think of this:** Friends live at house numbers **1, 2, 10**. They want a meeting house minimizing total walking distance.

**Step-by-step with real numbers:**

1. Meet at 1: 0+1+9=10
2. Meet at 2: 1+0+8=9
3. Meet at 10: 9+8+0=17
4. Median is 2, giving minimum 9

**📐 Mathematical Model / Visual**

```text
1---2----------------10
    ^
  median

cost(x)=|1-x|+|2-x|+|10-x|
minimum at x=2
```

**What the math means:** For sum of absolute distances on a line, moving toward the **median balances how many points lie on each side**.

**🧠 CF Recognition:** If asked to minimize `Σ|A[i]-x|`, think **median**, not mean.


**Question.** Choose `x` to minimize `|x - a_1| + ... + |x - a_n|`.

**Answer.** `x` = the median of the sorted values.

**Why (two points).** For `a <= b`: `|x - a| + |x - b| >= b - a`, with equality when `x` is between them. Pair smallest with largest, second smallest with second largest, and so on. The median lies inside every pair's interval, so every pair is minimal at the same time.

**Worked example.** `[1, 2, 10]`: median `2` -> cost `1 + 0 + 8 = 9`. Try `x = 5`: `4 + 3 + 5 = 12`, larger.

**Mean and squares.** The mean minimizes `(x - a_1)^2 + ... + (x - a_n)^2`.

### 8.4 Grid movement

#### 🌍 Real-World Example — 8.4 Grid movement

**Think of this:** Imagine a small everyday situation where the quantities in **8.4 Grid movement** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
one step changes (x + y) by exactly 1, so it flips the parity of (x + y)
reach (a, b) from (0, 0) in exactly k steps  <=>  |a| + |b| <= k  and  (k - |a| - |b|) is even
```

### 8.5 Rotating coordinates

#### 🌍 Real-World Example — 8.5 Rotating coordinates

**Think of this:** Imagine a small everyday situation where the quantities in **8.5 Rotating coordinates** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


```text
u = x + y,   v = x - y      ->   |dx| + |dy| = max(|du|, |dv|)
```

Manhattan distance becomes Chebyshev distance.

**Problems using this part:** CF 1401A, 1201C.

---

## Part 9. Min/Max Mathematical Transformations

**Identities.**

```text
max(a, b) = (a + b + |a - b|) / 2
min(a, b) = (a + b - |a - b|) / 2
max(a, b) + min(a, b) = a + b
max(a, b) * min(a, b) = a * b
```

**Ideas that appear often.**

| Idea | What to do |
|---|---|
| minimize the maximum | guess the answer `X`, test if possible, binary search |
| maximize the minimum | same, other direction |
| balance two quantities | make them close to `total / 2` |
| extremal argument | look at the largest or smallest element first |
| two upper bounds | answer is the `min` of the bounds |

**Separating variables.** To maximize `(a_i + i) - (a_j + j)`, take the max of `a_i + i` and the min of `a_j + j` separately.

---

## Part 10. Invariants

> **What is it?** An **invariant** is something an operation never changes. If the start and the target have different invariant values, the target is unreachable.

**Checklist for every operation.**

```text
1. Write "before" and "after".
2. Compute what changed (the delta).
3. Whatever has delta 0 is an invariant.
```

| Type | Operation | What never changes | Consequence |
|---|---|---|---|
| Sum | `A_i += x`, `A_j -= x` | total sum | equal array needs `sum % n == 0` |
| Parity | `A_i += 2` | `A_i % 2` | parity of each element is fixed |
| XOR | `A_i ^= x`, `A_j ^= x` | XOR of all | final XOR = initial XOR |
| GCD | `A_i -= A_j` | gcd of all | all elements stay multiples of gcd |
| Modulo | `A_i += m` | `A_i % m` | remainder classes never merge |
| Difference | add `c` to every element | `A_i - A_j` | only relative shape matters |
| Count | swap two elements | the multiset | frequencies same |
| Ordering | swap equal-parity neighbours | order of odd-vs-even elements | check sortedness inside classes |
| Coloring | domino on a chessboard | black count minus white count | needs balanced board |
| Monovariant | each step lowers the sum | strictly decreasing quantity | process must stop |

**Worked example (GCD).**

```text
Operation:  A_i := A_i - A_j.   Let g = gcd(A_i, A_j).
g divides A_i and A_j, so g divides A_i - A_j.
So gcd cannot get smaller. Doing the reverse (add back) shows it cannot get bigger.
Result: gcd of the array is invariant.
```

**Watch out.** An invariant proves *impossible*. To prove *possible* you must also give a construction.

**Problems using this part:** CF 1538B, 1401A, 4A.

---

## Part 11. Operation Modeling

> **When the statement says "you may perform this operation any number of times".**

**Method.**

```text
1. Write the operation as a formula (before -> after).
2. Compute the delta of: sum, parity, mod, gcd, XOR, counts, order.
3. Whatever has delta 0 is an invariant.
4. Describe what states are reachable, then check the target.
```

| Operation | Delta | What is preserved |
|---|---|---|
| `A_i += x`, `A_j -= x` | sum change 0 | sum |
| `A_i += 2` | +2 | parity of `A_i` |
| `A_i += 1`, `A_j += 1` | sum +2 | parity of the sum |
| `A_i := A_i - A_j` | | gcd |
| `A_i ^= x`, `A_j ^= x` | | XOR of all |
| swap `A_i`, `A_j` | | multiset |
| `A_i *= 2` | | odd part of `A_i` |
| merge two into their sum | count -1 | sum (count decreases: monovariant) |

**Worked example.** `A_i += 1`, `A_j -= 1` for any `i != j`: the sum is fixed and the length is fixed. Any array with the same sum and length is reachable (if negative values are allowed).

---

## Part 12. Decoupling Variables

> **What is it?** Split one hard problem with linked variables into several small independent problems.

**Coordinate change.**

```text
u = x + y,   v = x - y      ->   x = (u + v) / 2,   y = (u - v) / 2      (need u and v same parity)
```

Constraint `|x| + |y| <= k` becomes `max(|u|, |v|) <= k`, a simple square.

**Kinds of decoupling.**

| Kind | Meaning |
|---|---|
| x and y separately | horizontal and vertical movement are independent |
| bit by bit | AND / OR / XOR act on each bit alone |
| digit by digit | only when there are no carries |
| prime by prime | gcd uses `min` of exponents, lcm uses `max` |
| by frequency | when positions do not matter, use counts |

---

## Part 13. Frequency Modeling

> **What is it?** Turn an array into "how many of each value" when order does not matter.

```text
array  ->  cnt[value]  ->  formula on counts
```

| Question | Formula from counts |
|---|---|
| equal pairs | `sum of cnt[v] * (cnt[v] - 1) / 2` |
| pairs with `A_i + A_j = K` | `sum of cnt[v] * cnt[K - v]` for `v < K - v`, plus `C(cnt[K/2], 2)` if `K` is even |
| mex (smallest missing value) | first `v` with `cnt[v] = 0` |
| is it a permutation of `1..n` | every `cnt[v] = 1` |
| same multiset | equal count maps |

**Worked example.** `[1, 1, 2, 2, 2]` -> `cnt = {1: 2, 2: 3}` -> pairs `1 + 3 = 4`.

---

## Part 14. Sorting as a Mathematical Transformation

> **What is it?** Sorting gives you `a_1 <= a_2 <= ... <= a_n`, which turns messy conditions into simple ones.

| After sorting | You get |
|---|---|
| neighbours | the smallest difference is between adjacent elements (CF 1360B) |
| extremes | pair smallest with largest to balance sums |
| middle element | the median |
| a condition `a_i + a_j <= X` | for each `i`, valid `j` form a contiguous range (binary search or two pointers) |
| "take the k largest" | the last `k` elements |

**Rearrangement idea.** The sum of products `a_i * b_i` is largest when both arrays are sorted the same way, smallest when opposite.

**Problems using this part:** CF 1360B, 1538C, 1324D.

---

## Part 15. Prefix Mathematics

> **What is it?** Precompute running totals so any range sum becomes one subtraction.

```text
P[i] = A_1 + ... + A_i          P[0] = 0
sum of A[l..r] = P[r] - P[l - 1]
```

**Picture.**

```text
A:  3  1  4  1  5
P:  0  3  4  8  9  14           sum of A[2..4] = P[4] - P[1] = 9 - 3 = 6   (1 + 4 + 1)
```

**Same idea for other things.**

| Prefix of | Range answer |
|---|---|
| sums | `P[r] - P[l-1]` |
| XOR | `X[r] ^ X[l-1]` |
| counts of a value | count in range by subtraction |
| remainders | subarray divisible by `m` when `P[r] % m == P[l-1] % m` |

**Turning a condition into an equation.**

```text
subarray sum equals K   ->   P[r] - P[l-1] = K   ->   P[l-1] = P[r] - K      (look up in a hash map)
equal number of 0 and 1 ->   replace 0 by -1, need P[r] = P[l-1]
```

**2D rectangle sum.**

```text
S = P[x2][y2] - P[x1-1][y2] - P[x2][y1-1] + P[x1-1][y1-1]
```

**Watch out.** Start the map with `P[0] = 0`.


## Part 16. Difference Arrays

> **What is it?** The opposite of prefix sums. Store *changes* between neighbours so that "add `v` to a whole range" costs two updates.

```text
D[i] = A[i] - A[i-1]              A[i] = D[1] + D[2] + ... + D[i]
add v to A[l..r]:   D[l] += v,   D[r+1] -= v
```

**Picture.** Add 5 on `[3, 5]` in an array of 7 zeros.

```text
D:      0  0  +5  0  0  -5  0
prefix: 0  0   5  5  5   0  0     <- final A
```

**Where it shows up.** "many range updates, then read the array" -> difference array. Think of the `+v` at `l` and `-v` at `r+1` as sweep-line events.

---

## Part 17. Bitwise Mathematical Modeling

> **What is it?** An integer is a row of independent bits. Ask: *can each bit be solved alone?*

**Rules.**

```text
x & y : bit is 1 if both are 1        x | y : if either is 1        x ^ y : if exactly one is 1
a ^ a = 0,   a ^ 0 = a               (XOR cancels equal pairs)
a + b = (a ^ b) + 2 * (a & b)
```

**Bit-by-bit counting.** For each bit `b`, count how many numbers have it set (`ones`) and how many do not (`zeros`).

```text
sum over pairs of (A_i ^ A_j) = sum over b of  2^b * ones_b * zeros_b
```

**Worked example.** `[1, 2, 3]`. Bit 0: ones = 2 (1, 3), zeros = 1 -> `1 * 2 * 1 = 2`. Bit 1: ones = 2 (2, 3), zeros = 1 -> `2 * 2 * 1 = 4`. Total `6`. Check: `1^2 = 3`, `1^3 = 2`, `2^3 = 1` -> `6`.

**Masks and subsets.** `for (mask = 0; mask < (1 << n); mask++)` lists all subsets of `n` items (`n <= 20`).

**Watch out.** Use `1LL << b` when `b >= 31`.

---

## Part 18. Greedy Through Mathematical Proofs

> **What is it?** A greedy algorithm is correct only if you can prove the greedy choice is never worse.

**For every greedy, answer three questions.**

```text
1. Why this choice?
2. What inequality proves it is not worse?
3. What happens if we swap it with another choice? (exchange argument)
```

**Exchange argument, in words.** Take any optimal solution. If it differs from greedy at some place, swap that choice to the greedy one. Show the cost does not get worse. Repeat until the solution equals greedy.

**Worked example (cheapest first).** Buy as many items as possible with budget `B`.

```text
Sort prices ascending. Any set of k items costs at least the sum of the k cheapest.
So if greedy cannot afford k items, no set can.
```

**Worked example (CF 1360B).** In sorted order, `a_j - a_i >= a_{i+1} - a_i` for `j > i`, so the smallest difference is between neighbours.

---

## Part 19. Constructive Mathematics

> **What is it?** "Output any array / string / permutation that satisfies the rules."

**Method.**

```text
1. List the required properties.
2. Find necessary conditions (bounds, parity, sum).
3. Choose the simplest family of objects.
4. Verify every property by algebra.
5. Test on small cases with a brute-force checker.
```

**Simple families to try first.**

| Need | Try |
|---|---|
| gcd / lcm equation | `1`, `x - 1`, or equal numbers |
| permutation with no fixed point | shift `2, 3, ..., n, 1` |
| parity pattern | odds first, then evens |
| mex equal to `k` | `0, 1, ..., k-1` |
| coprime pair | consecutive numbers `n, n - 1` |

---

## Part 20. Diophantine Modeling

> **What is it?** Find integers `x, y` with `a*x + b*y = c`.

**Rules.**

```text
1. Solvable in integers  <=>  gcd(a, b) divides c
2. Extended Euclid finds x0, y0 with a*x0 + b*y0 = gcd(a, b)
3. Scale by c / gcd to get one solution (x0', y0')
4. All solutions:  x = x0' + (b/g)*t,   y = y0' - (a/g)*t   for integer t
```

**Easy method for contests.** If `a` is large, loop `x = 0 .. c / a` and check `(c - a*x) % b == 0`.

**Worked example (CF 1475B).** `2020a + 2021b = n`.

```text
t = a + b          ->   n = 2020*t + b
b = n % 2020,  t = n / 2020      valid when  b <= t
```

**Extended Euclid code.**

```cpp
// returns g = gcd(a, b) and sets x, y so that a*x + b*y = g
long long extgcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }
    long long x1, y1;
    long long g = extgcd(b, a % b, x1, y1);
    x = y1;
    y = x1 - (a / b) * y1;
    return g;
}
```

---

## Part 21. Geometric / Grid Modeling

> **What is it?** Turning grid and coordinate statements into short formulas.

| Topic | Formula |
|---|---|
| Manhattan distance | `\|dx\| + \|dy\|` |
| cell color / parity | `(x + y) % 2` |
| cells reachable in `k` steps | `\|dx\| + \|dy\| <= k` and same parity as `k` |
| rectangle area | `w * h` |
| rectangle perimeter | `2 * (w + h)` |
| lattice points on a segment | `gcd(\|dx\|, \|dy\|) + 1` |
| compare slopes | cross-multiply, avoid floats |
| coordinate compression | replace values by their ranks |

**Overlap of two rectangles.**

```text
overlap_x = max(0, min(r1, r2) - max(l1, l2))
overlap_y = max(0, min(t1, t2) - max(b1, b2))
overlap area = overlap_x * overlap_y
```

**Worked example.** Segment from `(0, 0)` to `(6, 4)`: `gcd(6, 4) + 1 = 3` lattice points.

---

## Part 22. Game Mathematics

> **What is it?** Decide who wins with perfect play.

**Definitions.**

```text
losing position:  every move leads to a winning position for the opponent
winning position: some move leads to a losing position
```

**Method.** Start from the end (terminal positions), label backwards, then look for a repeating pattern.

**Worked example.** A pile of `n`, each turn remove 1 to `k` stones, the player who cannot move loses.

```text
losing positions:  n % (k + 1) == 0
strategy: after the opponent removes x, you remove k + 1 - x
```

**Nim.** Several piles, remove any number from one pile.

```text
first player wins  <=>  XOR of pile sizes != 0
```

**Grundy value.** `g(position) = mex of g of the next positions`. Independent games combine with XOR.

---

## Part 23. Recurrences

> **What is it?** Describe a step-by-step process by a formula for the next value.

```text
f(n) = f(n-1) + f(n-2)         (Fibonacci)
```

**Method.**

```text
1. Define f(n) in words.
2. Say what the last step can be.
3. Write f(n) from smaller values.
4. Compute in a loop from small n to large n.
```

**Huge `n`.** A linear recurrence of order `k` is one `k x k` matrix raised to the power `n` with fast exponentiation: time `O(k^3 log n)`.

```text
[f(n+1)]   [1 1]^n  [f(1)]
[f(n)  ] = [1 0]    [f(0)]
```

---

## Part 24. Expectation / Probability Basics

**Rules.**

```text
P(A) = favorable outcomes / all outcomes
P(not A) = 1 - P(A)
independent events:  P(A and B) = P(A) * P(B)
```

**Linearity of expectation (the key tool).**

```text
E[X + Y] = E[X] + E[Y]          always true, even if X and Y are dependent
E[number of successes] = sum of P(item i succeeds)
```

**Worked example.** Flip 10 fair coins. Expected heads `= 10 * 1/2 = 5` (each coin contributes `1/2`).

**Modular answers.** A fraction `a/b` mod `p` is `a * b^(p-2) mod p`.

---

## Part 25. Mathematical Optimization

> **What is it?** Finding the best value by reasoning about how the answer changes.

**Binary search on the answer.**

```text
1. Guess X.
2. Write can(X): is X achievable?
3. If can(X) true implies can(X-1) true (monotone), binary search works.
```

```cpp
// find the maximum x with can(x) == true  (can is true ... true false ... false)
long long lo = 0, hi = INF;
while (lo < hi) {
    long long mid = lo + (hi - lo + 1) / 2;   // upper mid avoids an infinite loop
    if (can(mid)) lo = mid;
    else          hi = mid - 1;
}
// answer = lo
```

**Other tools.**

| Situation | Tool |
|---|---|
| minimize total distance | median |
| minimize sum of squares | mean |
| two quantities to balance | meet near `total / 2` |
| any optimum | prove a lower bound, then show a construction |


---

## Part 26. Common Codeforces Mathematical Forms

Each form: Recognition → Model → Transformation → Why → Visual → Example → Mistakes → Complexity → Problems.

### Form 1. Sum Constraint

#### 🌍 Real-World Example — Form 1. Sum Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 1. Sum Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "total is S", "sum of all", "equalize"

**Mathematical Model:** `ΣA = S`; target all equal `t` ⇒ `n·t = S`

**Core Transformation:** `t = S/n`, need `S % n == 0`

**Why It Works:** A sum-preserving operation can't change `S`; equal array has sum `n t`.

**Visual Example:**

```text
Statement:     "total is S", "sum of all", "equalize"
        |
        v
Math model:    ΣA = S; target all equal t ⇒ n·t = S
        |
        v
Transform:     t = S/n, need S % n == 0
        |
        v
Check on:      S=12,n=4 → t=3; S=13 → impossible
        |
        v
Algorithm:     complexity O(n)
```

**Example:** S=12,n=4 → t=3; S=13 → impossible

**Common Mistakes:** Using float average; forgetting sum is preserved only if ops are sum-neutral.

**Complexity:** O(n)

**Representative Problems:** CF 1538B, 1263A

### Form 2. Difference Constraint

#### 🌍 Real-World Example — Form 2. Difference Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 2. Difference Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "differ by", "gap", `|Ai-Aj|=d`

**Mathematical Model:** `Ai - Aj = d`

**Core Transformation:** `Ai = Aj + d` ⇒ lookup in map

**Why It Works:** Isolate one variable so pair search becomes point lookup.

**Visual Example:**

```text
Statement:     "differ by", "gap", |Ai-Aj|=d
        |
        v
Math model:    Ai - Aj = d
        |
        v
Transform:     Ai = Aj + d ⇒ lookup in map
        |
        v
Check on:      A=[1,5,3], d=2 → (1,3),(3,5)
        |
        v
Algorithm:     complexity O(n)–O(n log n)
```

**Example:** A=[1,5,3], d=2 → (1,3),(3,5)

**Common Mistakes:** Counting `(i,j)` and `(j,i)` twice; `d=0` case.

**Complexity:** O(n)–O(n log n)

**Representative Problems:** CF 1520D (variant)

### Form 3. Product Constraint

#### 🌍 Real-World Example — Form 3. Product Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 3. Product Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "product", `Ai*Aj=K`

**Mathematical Model:** `Ai*Aj = K`

**Core Transformation:** `Aj = K/Ai`, need `K % Ai == 0`; or factor into primes

**Why It Works:** Divisor-pair correspondence; enumerate divisors ≤√K.

**Visual Example:**

```text
Statement:     "product", Ai*Aj=K
        |
        v
Math model:    Ai*Aj = K
        |
        v
Transform:     Aj = K/Ai, need K % Ai == 0; or factor into primes
        |
        v
Check on:      K=12 → (1,12),(2,6),(3,4)
        |
        v
Algorithm:     complexity O(√K) or O(n)
```

**Example:** K=12 → (1,12),(2,6),(3,4)

**Common Mistakes:** Overflow `Ai*Aj`; division by 0.

**Complexity:** O(√K) or O(n)

**Representative Problems:** CF 1475A (odd/even split)

### Form 4. Ratio Constraint

#### 🌍 Real-World Example — Form 4. Ratio Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 4. Ratio Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "twice as many", "ratio a:b"

**Mathematical Model:** `x/y = a/b` ⇒ `x b = y a`

**Core Transformation:** Cross-multiply (no floats); `x=a t, y=b t` with `t` integer

**Why It Works:** Ratio in lowest terms `a'/b'` means `x=a' t`.

**Visual Example:**

```text
Statement:     "twice as many", "ratio a:b"
        |
        v
Math model:    x/y = a/b ⇒ x b = y a
        |
        v
Transform:     Cross-multiply (no floats); x=a t, y=b t with t integer
        |
        v
Check on:      x:y = 2:3, x+y=20 → t=4 → 8,12
        |
        v
Algorithm:     complexity O(1)
```

**Example:** x:y = 2:3, x+y=20 → t=4 → 8,12

**Common Mistakes:** Comparing floats; not reducing by gcd.

**Complexity:** O(1)

**Representative Problems:** —

### Form 5. Parity Constraint

#### 🌍 Real-World Example — Form 5. Parity Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 5. Parity Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "even/odd", "alternating", "can't be split"

**Mathematical Model:** `x mod 2 = p`

**Core Transformation:** Reduce every quantity to `mod 2`; count odds

**Why It Works:** Ops by even amounts preserve parity; sum of parities = parity of sum.

**Visual Example:**

```text
Statement:     "even/odd", "alternating", "can't be split"
        |
        v
Math model:    x mod 2 = p
        |
        v
Transform:     Reduce every quantity to mod 2; count odds
        |
        v
Check on:      n=6 even, split into two even positive → yes
        |
        v
Algorithm:     complexity O(1)–O(n)
```

**Example:** n=6 even, split into two even positive → yes

**Common Mistakes:** Negative `%`; forgetting minimal positive size (n=2).

**Complexity:** O(1)–O(n)

**Representative Problems:** CF 4A, 1401A

### Form 6. Divisibility Constraint

#### 🌍 Real-World Example — Form 6. Divisibility Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 6. Divisibility Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "divisible by d", "multiple of"

**Mathematical Model:** `x = d k`

**Core Transformation:** Round: `x = ceil(a/d)*d` (smallest multiple ≥ a)

**Why It Works:** Multiples of `d` form an AP with difference `d`.

**Visual Example:**

```text
Statement:     "divisible by d", "multiple of"
        |
        v
Math model:    x = d k
        |
        v
Transform:     Round: x = ceil(a/d)*d (smallest multiple ≥ a)
        |
        v
Check on:      d=5,a=23 → 25
        |
        v
Algorithm:     complexity O(1)
```

**Example:** d=5,a=23 → 25

**Common Mistakes:** Off-by-one at exact multiples; overflow.

**Complexity:** O(1)

**Representative Problems:** CF 1476A, 1374A

### Form 7. GCD Constraint

#### 🌍 Real-World Example — Form 7. GCD Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 7. GCD Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "common divisor", "equal parts", subtraction ops

**Mathematical Model:** `g | Ai ∀i`

**Core Transformation:** `g = gcd(A1..An)`; ops like `Ai-=Aj` preserve it

**Why It Works:** gcd(a,b)=gcd(a,b-ka).

**Visual Example:**

```text
Statement:     "common divisor", "equal parts", subtraction ops
        |
        v
Math model:    g | Ai ∀i
        |
        v
Transform:     g = gcd(A1..An); ops like Ai-=Aj preserve it
        |
        v
Check on:      gcd(12,18)=6
        |
        v
Algorithm:     complexity O(n log V)
```

**Example:** gcd(12,18)=6

**Common Mistakes:** Assuming gcd of many is pairwise; gcd(0,x)=x.

**Complexity:** O(n log V)

**Representative Problems:** CF 1543A, 1325A

### Form 8. LCM Constraint

#### 🌍 Real-World Example — Form 8. LCM Constraint

**Think of this:** Imagine a small everyday situation where the quantities in **Form 8. LCM Constraint** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "divisible by both", "synchronize", "every a and every b"

**Mathematical Model:** `X mod a = X mod b = 0`

**Core Transformation:** `X = k·lcm(a,b)`, `lcm = a/gcd·b`

**Why It Works:** Common multiples are exactly multiples of lcm.

**Visual Example:**

```text
Statement:     "divisible by both", "synchronize", "every a and every b"
        |
        v
Math model:    X mod a = X mod b = 0
        |
        v
Transform:     X = k·lcm(a,b), lcm = a/gcd·b
        |
        v
Check on:      a=4,b=6 → lcm 12
        |
        v
Algorithm:     complexity O(log V)
```

**Example:** a=4,b=6 → lcm 12

**Common Mistakes:** Overflow of `a*b`; lcm of many grows fast (cap it).

**Complexity:** O(log V)

**Representative Problems:** CF 1325A

### Form 9. Modulo Constraint

#### 🌍 Real-World Example — Clock Wrapping

**Think of this:** It is **9 o'clock**. What time is it 5 hours later on a 12-hour clock?

**Step-by-step with real numbers:**

1. 9+5=14
2. Clock has only 12 positions
3. 14 wraps to 2
4. 14 mod 12 = 2

**📐 Mathematical Model / Visual**

```text
       12
   11      1
10           2  <- 9+5
9             3
 8           4
   7   6   5

(9+5) mod 12 = 2
```

**What the math means:** Modulo means **wrap around after a fixed cycle length**.

**🧠 CF Recognition:** Look for **circular arrays, clocks, weekdays, repeated positions, remainder classes**.


**Recognition Signals:** "remainder", "mod k", "each k-th"

**Mathematical Model:** `x mod m = r`

**Core Transformation:** Largest ≤n: `k = n - ((n-r) mod m)`; class representative

**Why It Works:** Values with same remainder are `r + m t`.

**Visual Example:**

```text
Statement:     "remainder", "mod k", "each k-th"
        |
        v
Math model:    x mod m = r
        |
        v
Transform:     Largest ≤n: k = n - ((n-r) mod m); class representative
        |
        v
Check on:      n=7,m=5,r=3 → 3
        |
        v
Algorithm:     complexity O(1)
```

**Example:** n=7,m=5,r=3 → 3

**Common Mistakes:** Negative remainder; `r>=m`.

**Complexity:** O(1)

**Representative Problems:** CF 1374A, 577B

### Form 10. Equal Frequency

#### 🌍 Real-World Example — Form 10. Equal Frequency

**Think of this:** Imagine a small everyday situation where the quantities in **Form 10. Equal Frequency** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "equal numbers of", "same count"

**Mathematical Model:** `cntA = cntB`

**Core Transformation:** Assign +1 to A, −1 to B; need `Σ=0`

**Why It Works:** Prefix balance zero ⇒ segment balanced.

**Visual Example:**

```text
Statement:     "equal numbers of", "same count"
        |
        v
Math model:    cntA = cntB
        |
        v
Transform:     Assign +1 to A, −1 to B; need Σ=0
        |
        v
Check on:      ABBA → +1−1−1+1=0 ✓
        |
        v
Algorithm:     complexity O(n)
```

**Example:** `ABBA` → +1−1−1+1=0 ✓

**Common Mistakes:** Off-by-one in prefix map (needs `P0=0`).

**Complexity:** O(n)

**Representative Problems:** prefix equal-balance (Part 15)

### Form 11. Pair Counting

#### 🌍 Real-World Example — Handshakes at a Meeting

**Think of this:** Four people A, B, C, D each shake hands with every other person exactly once. How many handshakes occur?

**Step-by-step with real numbers:**

1. A shakes with B,C,D → 3
2. B still needs C,D → 2
3. C still needs D → 1
4. Total = 3+2+1 = 6
5. Formula = 4×3/2 = 6

**📐 Mathematical Model / Visual**

```text
A -- B
| \  |
|  \ |
C -- D
(+ the two diagonals)

ordered counts = 4×3
but AB and BA are same handshake
=> 4×3/2 = 6
```

**What the math means:** `n(n-1)/2` counts **unordered pairs of distinct objects**.

**🧠 CF Recognition:** Words **choose two, every pair, handshake, connect every two, i<j** → think `C(n,2)`.


**Recognition Signals:** "number of pairs (i<j)"

**Mathematical Model:** `C(n,2)` or Σ over frequency

**Core Transformation:** Group by key; `f(f-1)/2` per group

**Why It Works:** Pairs live inside groups.

**Visual Example:**

```text
Statement:     "number of pairs (i<j)"
        |
        v
Math model:    C(n,2) or Σ over frequency
        |
        v
Transform:     Group by key; f(f-1)/2 per group
        |
        v
Check on:      [1,1,2,2,2] → 1+3=4
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [1,1,2,2,2] → 1+3=4

**Common Mistakes:** `int` overflow; ordered vs unordered.

**Complexity:** O(n)

**Representative Problems:** CF 1520D

### Form 12. Complement Pair

#### 🌍 Real-World Example — Form 12. Complement Pair

**Think of this:** Imagine a small everyday situation where the quantities in **Form 12. Complement Pair** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** `Ai+Aj=K`

**Mathematical Model:** `Aj = K - Ai`

**Core Transformation:** Frequency lookup; handle `Ai=K/2` with `C(f,2)`

**Why It Works:** Isolation of second variable.

**Visual Example:**

```text
Statement:     Ai+Aj=K
        |
        v
Math model:    Aj = K - Ai
        |
        v
Transform:     Frequency lookup; handle Ai=K/2 with C(f,2)
        |
        v
Check on:      K=10,[3,7,5,5] → (3,7),(5,5)
        |
        v
Algorithm:     complexity O(n)
```

**Example:** K=10,[3,7,5,5] → (3,7),(5,5)

**Common Mistakes:** Double counting; self pairing.

**Complexity:** O(n)

**Representative Problems:** CF 1538C (range version)

### Form 13. Difference Pair

#### 🌍 Real-World Example — Form 13. Difference Pair

**Think of this:** Imagine a small everyday situation where the quantities in **Form 13. Difference Pair** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** `Ai-Aj=K`

**Mathematical Model:** `Aj=Ai-K`

**Core Transformation:** Hash lookup / two-pointer on sorted

**Why It Works:** Monotone in sorted order.

**Visual Example:**

```text
Statement:     Ai-Aj=K
        |
        v
Math model:    Aj=Ai-K
        |
        v
Transform:     Hash lookup / two-pointer on sorted
        |
        v
Check on:      K=2,[1,3,5] → 2 pairs
        |
        v
Algorithm:     complexity O(n log n)
```

**Example:** K=2,[1,3,5] → 2 pairs

**Common Mistakes:** `K=0` special.

**Complexity:** O(n log n)

**Representative Problems:** —

### Form 14. Equal Remainders

#### 🌍 Real-World Example — Form 14. Equal Remainders

**Think of this:** Imagine a small everyday situation where the quantities in **Form 14. Equal Remainders** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "same remainder mod m"

**Mathematical Model:** `Ai ≡ Aj (mod m)`

**Core Transformation:** `m | (Ai-Aj)`; bucket by remainder

**Why It Works:** Congruence classes partition integers.

**Visual Example:**

```text
Statement:     "same remainder mod m"
        |
        v
Math model:    Ai ≡ Aj (mod m)
        |
        v
Transform:     m | (Ai-Aj); bucket by remainder
        |
        v
Check on:      m=3,[1,4,7,2] → class 1 has 3
        |
        v
Algorithm:     complexity O(n)
```

**Example:** m=3,[1,4,7,2] → class 1 has 3

**Common Mistakes:** Negative remainders.

**Complexity:** O(n)

**Representative Problems:** CF 577B

### Form 15. Consecutive Values

#### 🌍 Real-World Example — Form 15. Consecutive Values

**Think of this:** Imagine a small everyday situation where the quantities in **Form 15. Consecutive Values** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "consecutive integers", "forms 1..k"

**Mathematical Model:** after sorting `a_{i+1}=a_i+1`

**Core Transformation:** Check `max-min = n-1` and all distinct

**Why It Works:** n distinct ints spanning `n` values are consecutive.

**Visual Example:**

```text
Statement:     "consecutive integers", "forms 1..k"
        |
        v
Math model:    after sorting a_{i+1}=a_i+1
        |
        v
Transform:     Check max-min = n-1 and all distinct
        |
        v
Check on:      [3,5,4] → 5-3=2=n-1 ✓
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [3,5,4] → 5-3=2=n-1 ✓

**Common Mistakes:** Duplicates.

**Complexity:** O(n)

**Representative Problems:** permutation checks

### Form 16. Arithmetic Progression

#### 🌍 Real-World Example — Form 16. Arithmetic Progression

**Think of this:** Imagine a small everyday situation where the quantities in **Form 16. Arithmetic Progression** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "increase by d each step"

**Mathematical Model:** `a_k = a+(k-1)d`

**Core Transformation:** Sum `n(2a+(n-1)d)/2`; find `n` from quadratic bound

**Why It Works:** Pair first+last.

**Visual Example:**

```text
Statement:     "increase by d each step"
        |
        v
Math model:    a_k = a+(k-1)d
        |
        v
Transform:     Sum n(2a+(n-1)d)/2; find n from quadratic bound
        |
        v
Check on:      1+2+…+100=5050
        |
        v
Algorithm:     complexity O(1) / O(log)
```

**Example:** 1+2+…+100=5050

**Common Mistakes:** Overflow; solving quadratic with float.

**Complexity:** O(1) / O(log)

**Representative Problems:** Part 7

### Form 17. Geometric / Doubling

#### 🌍 Real-World Example — Form 17. Geometric / Doubling

**Think of this:** Imagine a small everyday situation where the quantities in **Form 17. Geometric / Doubling** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "doubles", "halves"

**Mathematical Model:** `a_k = a r^k`

**Core Transformation:** steps ≈ `log_r`; sum `(r^k-1)/(r-1)`

**Why It Works:** Exponential growth reaches bound in ~log steps.

**Visual Example:**

```text
Statement:     "doubles", "halves"
        |
        v
Math model:    a_k = a r^k
        |
        v
Transform:     steps ≈ log_r; sum (r^k-1)/(r-1)
        |
        v
Check on:      1→1e9 by ×2 takes 30 steps
        |
        v
Algorithm:     complexity O(log)
```

**Example:** 1→1e9 by ×2 takes 30 steps

**Common Mistakes:** `2^k` overflow.

**Complexity:** O(log)

**Representative Problems:** CF 1475A

### Form 18. Median Optimization

#### 🌍 Real-World Example — Form 18. Median Optimization

**Think of this:** Imagine a small everyday situation where the quantities in **Form 18. Median Optimization** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "minimum total distance", "gather at a point"

**Mathematical Model:** `min_x Σ|x-ai|`

**Core Transformation:** `x = median` of sorted array

**Why It Works:** See Part 8 proof.

**Visual Example:**

```text
Statement:     "minimum total distance", "gather at a point"
        |
        v
Math model:    min_x Σ|x-ai|
        |
        v
Transform:     x = median of sorted array
        |
        v
Check on:      [1,2,10] → x=2, cost 9
        |
        v
Algorithm:     complexity O(n log n)
```

**Example:** [1,2,10] → x=2, cost 9

**Common Mistakes:** Using mean; even n any point in `[a_{n/2},a_{n/2+1}]`.

**Complexity:** O(n log n)

**Representative Problems:** CF 1201C

### Form 19. Prefix Equation

#### 🌍 Real-World Example — Bank Account Statement

**Think of this:** Your cumulative deposits after days 1..5 are built from daily amounts `[10,20,5,15,10]`. How much was deposited on days 2..4?

**Step-by-step with real numbers:**

1. Prefix totals = [10,30,35,50,60]
2. Total through day 4 = 50
3. Total before day 2 = 10
4. Days 2..4 = 50-10 = 40

**📐 Mathematical Model / Visual**

```text
daily :  10   20    5   15   10
prefix:  10   30   35   50   60
               <------>
range 2..4 = P[4]-P[1] = 50-10 = 40
```

**What the math means:** A prefix stores **everything from the beginning up to i**. Subtract two prefixes to isolate the middle interval.

**🧠 CF Recognition:** Many **static range-sum queries** or equations involving subarray sums should trigger prefix sums.


**Recognition Signals:** "subarray with sum K"

**Mathematical Model:** `P[r]-P[l-1]=K`

**Core Transformation:** `P[l-1]=P[r]-K` map lookup

**Why It Works:** Subtraction telescopes.

**Visual Example:**

```text
Statement:     "subarray with sum K"
        |
        v
Math model:    P[r]-P[l-1]=K
        |
        v
Transform:     P[l-1]=P[r]-K map lookup
        |
        v
Check on:      [1,2,3],K=3 → (1,2),(3)
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [1,2,3],K=3 → (1,2),(3)

**Common Mistakes:** Missing `P0=0`.

**Complexity:** O(n)

**Representative Problems:** Part 15

### Form 20. Contribution Counting

#### 🌍 Real-World Example — Form 20. Contribution Counting

**Think of this:** Imagine a small everyday situation where the quantities in **Form 20. Contribution Counting** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "sum over all subarrays/pairs"

**Mathematical Model:** `Σ_i A_i * ways_i`

**Core Transformation:** count structures containing each element

**Why It Works:** Sum swap: `Σ_structures Σ_elements = Σ_elements Σ_structures`.

**Visual Example:**

```text
Statement:     "sum over all subarrays/pairs"
        |
        v
Math model:    Σ_i A_i * ways_i
        |
        v
Transform:     count structures containing each element
        |
        v
Check on:      n=3 subarray sums: coeffs 3,4,3
        |
        v
Algorithm:     complexity O(n)
```

**Example:** n=3 subarray sums: coeffs 3,4,3

**Common Mistakes:** Wrong count of ways; overflow.

**Complexity:** O(n)

**Representative Problems:** Part 6

### Form 21. Pigeonhole

#### 🌍 Real-World Example — Birth Months

**Think of this:** There are **13 people** but only **12 birth months**. Show that two people must share a birth month.

**Step-by-step with real numbers:**

1. 12 months are 12 boxes
2. Place each person into their birth-month box
3. After 12 people, each box could have one
4. The 13th person must enter an occupied box

**📐 Mathematical Model / Visual**

```text
people: 13 objects
months: 12 boxes

[Jan][Feb]...[Dec]
  1    1       1
+ one extra person -> collision guaranteed
```

**What the math means:** More objects than containers guarantees at least one container receives multiple objects.

**🧠 CF Recognition:** Look for **guarantee duplicate / same remainder / same category** with limited buckets.


**Recognition Signals:** "prove/decide existence", huge n vs small modulus

**Mathematical Model:** `n objects > m boxes`

**Core Transformation:** If `n>=m` answer is yes

**Why It Works:** Prefix remainders `P0..Pn` have a repeat.

**Visual Example:**

```text
Statement:     "prove/decide existence", huge n vs small modulus
        |
        v
Math model:    n objects > m boxes
        |
        v
Transform:     If n>=m answer is yes
        |
        v
Check on:      n=5,m=3 → YES
        |
        v
Algorithm:     complexity O(1) then DP
```

**Example:** n=5,m=3 → YES

**Common Mistakes:** Applying when `n<m`.

**Complexity:** O(1) then DP

**Representative Problems:** CF 577B

### Form 22. Inclusion-Exclusion

#### 🌍 Real-World Example — Students Playing Two Sports

**Think of this:** In a class, **10** students play football, **8** play cricket, and **3** play both. How many play at least one?

**Step-by-step with real numbers:**

1. 10+8=18 counts the 3 dual-sport students twice
2. Subtract that overlap once
3. 18-3=15

**📐 Mathematical Model / Visual**

```text
Football:  [------]
             [XXX]  <- both = 3
Cricket:        [------]

10 + 8 - 3 = 15
```

**What the math means:** When adding overlapping groups, the intersection gets counted twice, so subtract it once.

**🧠 CF Recognition:** Words **A or B, at least one, union, overlap** → inclusion-exclusion.


**Recognition Signals:** "none of", "at least one of", "divisible by any of"

**Mathematical Model:** `|∪Ai|`

**Core Transformation:** alternate signs over masks

**Why It Works:** Counts overlaps exactly once.

**Visual Example:**

```text
Statement:     "none of", "at least one of", "divisible by any of"
        |
        v
Math model:    |∪Ai|
        |
        v
Transform:     alternate signs over masks
        |
        v
Check on:      multiples of 2 or 3 up to 10: 5+3-1=7
        |
        v
Algorithm:     complexity O(2^k)
```

**Example:** multiples of 2 or 3 up to 10: 5+3-1=7

**Common Mistakes:** Sign errors; `2^k` blowup for large k.

**Complexity:** O(2^k)

**Representative Problems:** —

### Form 23. Invariant

#### 🌍 Real-World Example — Form 23. Invariant

**Think of this:** Imagine a small everyday situation where the quantities in **Form 23. Invariant** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "any number of times", "can transform"

**Mathematical Model:** `I(before)=I(after)`

**Core Transformation:** Find preserved quantity

**Why It Works:** Ops keep `I`; target has different `I` ⇒ impossible.

**Visual Example:**

```text
Statement:     "any number of times", "can transform"
        |
        v
Math model:    I(before)=I(after)
        |
        v
Transform:     Find preserved quantity
        |
        v
Check on:      See Part 10
        |
        v
Algorithm:     complexity O(1)–O(n)
```

**Example:** See Part 10

**Common Mistakes:** Only necessary, need sufficiency proof.

**Complexity:** O(1)–O(n)

**Representative Problems:** CF 1538B

### Form 24. Monovariant

#### 🌍 Real-World Example — Form 24. Monovariant

**Think of this:** Imagine a small everyday situation where the quantities in **Form 24. Monovariant** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "process ends?", "how many steps at most"

**Mathematical Model:** strictly decreasing potential

**Core Transformation:** Bound steps by initial potential

**Why It Works:** Integer, bounded below ⇒ terminates.

**Visual Example:**

```text
Statement:     "process ends?", "how many steps at most"
        |
        v
Math model:    strictly decreasing potential
        |
        v
Transform:     Bound steps by initial potential
        |
        v
Check on:      Each step reduces ΣA by ≥1
        |
        v
Algorithm:     complexity O(potential)
```

**Example:** Each step reduces ΣA by ≥1

**Common Mistakes:** Potential not strictly monotone.

**Complexity:** O(potential)

**Representative Problems:** —

### Form 25. Reachability

#### 🌍 Real-World Example — Elevator with Fixed Jumps

**Think of this:** An elevator starts at floor **5** and can move exactly **+3 floors** per operation. Can it reach floor 20? What about 21?

**Step-by-step with real numbers:**

1. After k moves: floor = 5+3k
2. For 20: k=(20-5)/3=5 → integer → YES
3. For 21: k=(21-5)/3=16/3 → not integer → NO

**📐 Mathematical Model / Visual**

```text
5 -> 8 -> 11 -> 14 -> 17 -> 20 -> 23
                         ^
                    20 reachable
21 lies between steps -> unreachable
```

**What the math means:** Repeated fixed change creates an arithmetic form `start + k*step`.

**🧠 CF Recognition:** **Can we reach exactly X?** → isolate operation count `k` and check that it is a valid integer/non-negative.


**Recognition Signals:** "can reach", "is it possible"

**Mathematical Model:** state graph

**Core Transformation:** Characterize reachable set by invariants

**Why It Works:** Necessary via invariant, sufficient via construction.

**Visual Example:**

```text
Statement:     "can reach", "is it possible"
        |
        v
Math model:    state graph
        |
        v
Transform:     Characterize reachable set by invariants
        |
        v
Check on:      Coins a,b make all multiples of gcd
        |
        v
Algorithm:     complexity O(1)
```

**Example:** Coins a,b make all multiples of gcd

**Common Mistakes:** Simulating instead of characterizing.

**Complexity:** O(1)

**Representative Problems:** CF 1401A

### Form 26. Constructive Equation

#### 🌍 Real-World Example — Form 26. Constructive Equation

**Think of this:** Imagine a small everyday situation where the quantities in **Form 26. Constructive Equation** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "construct any"

**Mathematical Model:** find `x` with `f(x)=target`

**Core Transformation:** Choose simplest satisfying family

**Why It Works:** Verify each property algebraically.

**Visual Example:**

```text
Statement:     "construct any"
        |
        v
Math model:    find x with f(x)=target
        |
        v
Transform:     Choose simplest satisfying family
        |
        v
Check on:      a+b=x, min lcm → 1, x-1
        |
        v
Algorithm:     complexity O(n)
```

**Example:** `a+b=x`, min lcm → `1, x-1`

**Common Mistakes:** Missing edge cases (n=1,2).

**Complexity:** O(n)

**Representative Problems:** CF 1325A

### Form 27. Bounding

#### 🌍 Real-World Example — Form 27. Bounding

**Think of this:** Imagine a small everyday situation where the quantities in **Form 27. Bounding** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "minimum/maximum possible"

**Mathematical Model:** `lower <= ans <= upper`

**Core Transformation:** Prove lower bound, build matching solution

**Why It Works:** Bounds meet ⇒ equality.

**Visual Example:**

```text
Statement:     "minimum/maximum possible"
        |
        v
Math model:    lower <= ans <= upper
        |
        v
Transform:     Prove lower bound, build matching solution
        |
        v
Check on:      CF 1263A
        |
        v
Algorithm:     complexity O(1)
```

**Example:** CF 1263A

**Common Mistakes:** Bound not tight.

**Complexity:** O(1)

**Representative Problems:** CF 1263A

### Form 28. Extremal Principle

#### 🌍 Real-World Example — Form 28. Extremal Principle

**Think of this:** Imagine a small everyday situation where the quantities in **Form 28. Extremal Principle** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "largest/smallest element"

**Mathematical Model:** look at max/min

**Core Transformation:** Its constraint dominates

**Why It Works:** An optimal structure must handle the extremum.

**Visual Example:**

```text
Statement:     "largest/smallest element"
        |
        v
Math model:    look at max/min
        |
        v
Transform:     Its constraint dominates
        |
        v
Check on:      largest pile needs partners
        |
        v
Algorithm:     complexity O(n)
```

**Example:** largest pile needs partners

**Common Mistakes:** Assuming uniqueness.

**Complexity:** O(n)

**Representative Problems:** CF 1263A

### Form 29. Coordinate Transformation

#### 🌍 Real-World Example — Form 29. Coordinate Transformation

**Think of this:** Imagine a small everyday situation where the quantities in **Form 29. Coordinate Transformation** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "|x|+|y|", diagonal moves

**Mathematical Model:** `u=x+y,v=x-y`

**Core Transformation:** Manhattan → Chebyshev

**Why It Works:** Rotation makes constraints axis-aligned.

**Visual Example:**

```text
Statement:     "|x|+|y|", diagonal moves
        |
        v
Math model:    u=x+y,v=x-y
        |
        v
Transform:     Manhattan → Chebyshev
        |
        v
Check on:      (3,1)→u=4,v=2
        |
        v
Algorithm:     complexity O(1)
```

**Example:** (3,1)→u=4,v=2

**Common Mistakes:** Parity of `u,v` mismatch.

**Complexity:** O(1)

**Representative Problems:** Part 12

### Form 30. Bit Independence

#### 🌍 Real-World Example — Form 30. Bit Independence

**Think of this:** Imagine a small everyday situation where the quantities in **Form 30. Bit Independence** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** AND/OR/XOR

**Mathematical Model:** per-bit problems

**Core Transformation:** Solve 30 bit-problems, combine `2^b`

**Why It Works:** Bitwise ops don't mix positions.

**Visual Example:**

```text
Statement:     AND/OR/XOR
        |
        v
Math model:    per-bit problems
        |
        v
Transform:     Solve 30 bit-problems, combine 2^b
        |
        v
Check on:      XOR of pairs by bit counts
        |
        v
Algorithm:     complexity O(30 n)
```

**Example:** XOR of pairs by bit counts

**Common Mistakes:** Assuming independence with `+`.

**Complexity:** O(30 n)

**Representative Problems:** Part 17

### Form 31. Prime Factor Independence

#### 🌍 Real-World Example — Form 31. Prime Factor Independence

**Think of this:** Imagine a small everyday situation where the quantities in **Form 31. Prime Factor Independence** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** gcd/lcm/divisibility

**Mathematical Model:** exponent vectors

**Core Transformation:** Per prime: `min`/`max`/compare

**Why It Works:** gcd=min exponent, lcm=max exponent.

**Visual Example:**

```text
Statement:     gcd/lcm/divisibility
        |
        v
Math model:    exponent vectors
        |
        v
Transform:     Per prime: min/max/compare
        |
        v
Check on:      12=2²·3, 18=2·3²
        |
        v
Algorithm:     complexity O(n log V)
```

**Example:** 12=2²·3, 18=2·3²

**Common Mistakes:** Recomputing factorization every query.

**Complexity:** O(n log V)

**Representative Problems:** Part 3

### Form 32. Frequency Compression

#### 🌍 Real-World Example — Form 32. Frequency Compression

**Think of this:** Imagine a small everyday situation where the quantities in **Form 32. Frequency Compression** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** order irrelevant

**Mathematical Model:** `cnt[v]`

**Core Transformation:** Work over distinct values

**Why It Works:** Symmetric problem depends only on counts.

**Visual Example:**

```text
Statement:     order irrelevant
        |
        v
Math model:    cnt[v]
        |
        v
Transform:     Work over distinct values
        |
        v
Check on:      [1,1,2] → {1:2,2:1}
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [1,1,2] → {1:2,2:1}

**Common Mistakes:** Values too large for array → map.

**Complexity:** O(n)

**Representative Problems:** Part 13

### Form 33. Permutation Mathematics

#### 🌍 Real-World Example — Form 33. Permutation Mathematics

**Think of this:** Imagine a small everyday situation where the quantities in **Form 33. Permutation Mathematics** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "permutation of 1..n"

**Mathematical Model:** each value once

**Core Transformation:** `Σ=n(n+1)/2`, cycles, fixed points

**Why It Works:** Bijection.

**Visual Example:**

```text
Statement:     "permutation of 1..n"
        |
        v
Math model:    each value once
        |
        v
Transform:     Σ=n(n+1)/2, cycles, fixed points
        |
        v
Check on:      [2,3,1]: one 3-cycle
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [2,3,1]: one 3-cycle

**Common Mistakes:** 0- vs 1-index.

**Complexity:** O(n)

**Representative Problems:** Part 19

### Form 34. Mex Mathematics

#### 🌍 Real-World Example — Form 34. Mex Mathematics

**Think of this:** Imagine a small everyday situation where the quantities in **Form 34. Mex Mathematics** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "mex"

**Mathematical Model:** smallest missing

**Core Transformation:** `mex ≤ n`; need `0..k-1` present

**Why It Works:** Counts of `0..k-1` all ≥1.

**Visual Example:**

```text
Statement:     "mex"
        |
        v
Math model:    smallest missing
        |
        v
Transform:     mex ≤ n; need 0..k-1 present
        |
        v
Check on:      [0,1,3] → 2
        |
        v
Algorithm:     complexity O(n)
```

**Example:** [0,1,3] → 2

**Common Mistakes:** Confusing mex with min.

**Complexity:** O(n)

**Representative Problems:** —

### Form 35. Interval Mathematics

#### 🌍 Real-World Example — Form 35. Interval Mathematics

**Think of this:** Imagine a small everyday situation where the quantities in **Form 35. Interval Mathematics** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** segments, overlaps

**Mathematical Model:** `[l,r]`

**Core Transformation:** Intersection `[max l, min r]`, sort by endpoint, sweep

**Why It Works:** Intervals order structure.

**Visual Example:**

```text
Statement:     segments, overlaps
        |
        v
Math model:    [l,r]
        |
        v
Transform:     Intersection [max l, min r], sort by endpoint, sweep
        |
        v
Check on:      [1,5]&[3,8]→[3,5]
        |
        v
Algorithm:     complexity O(n log n)
```

**Example:** [1,5]&[3,8]→[3,5]

**Common Mistakes:** Open vs closed ends.

**Complexity:** O(n log n)

**Representative Problems:** Part 16

### Form 36. Grid Parity

#### 🌍 Real-World Example — Form 36. Grid Parity

**Think of this:** Imagine a small everyday situation where the quantities in **Form 36. Grid Parity** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** grid moves, tilings

**Mathematical Model:** `(x+y)%2`

**Core Transformation:** Reachability by parity and Manhattan

**Why It Works:** Each step flips color.

**Visual Example:**

```text
Statement:     grid moves, tilings
        |
        v
Math model:    (x+y)%2
        |
        v
Transform:     Reachability by parity and Manhattan
        |
        v
Check on:      (0,0)→(1,2) in 3 steps ✓
        |
        v
Algorithm:     complexity O(1)
```

**Example:** (0,0)→(1,2) in 3 steps ✓

**Common Mistakes:** Ignoring obstacles.

**Complexity:** O(1)

**Representative Problems:** CF 1401A

### Form 37. Cyclic / Modulo Process

#### 🌍 Real-World Example — Clock Wrapping

**Think of this:** It is **9 o'clock**. What time is it 5 hours later on a 12-hour clock?

**Step-by-step with real numbers:**

1. 9+5=14
2. Clock has only 12 positions
3. 14 wraps to 2
4. 14 mod 12 = 2

**📐 Mathematical Model / Visual**

```text
       12
   11      1
10           2  <- 9+5
9             3
 8           4
   7   6   5

(9+5) mod 12 = 2
```

**What the math means:** Modulo means **wrap around after a fixed cycle length**.

**🧠 CF Recognition:** Look for **circular arrays, clocks, weekdays, repeated positions, remainder classes**.


**Recognition Signals:** "wraps around", "every k-th"

**Mathematical Model:** index `i mod n`

**Core Transformation:** Position after `t` steps: `(s+t·d) mod n`; cycle length `n/gcd(d,n)`

**Why It Works:** Orbit under addition mod n.

**Visual Example:**

```text
Statement:     "wraps around", "every k-th"
        |
        v
Math model:    index i mod n
        |
        v
Transform:     Position after t steps: (s+t·d) mod n; cycle length
               n/gcd(d,n)
        |
        v
Check on:      n=6,d=4 → orbit size 3
        |
        v
Algorithm:     complexity O(1)
```

**Example:** n=6,d=4 → orbit size 3

**Common Mistakes:** Off-by-one 0/1 index.

**Complexity:** O(1)

**Representative Problems:** Part 4

### Form 38. Binary Search Equation

#### 🌍 Real-World Example — Form 38. Binary Search Equation

**Think of this:** Imagine a small everyday situation where the quantities in **Form 38. Binary Search Equation** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "maximize the minimum"

**Mathematical Model:** `can(X)` monotone

**Core Transformation:** Binary search over answer

**Why It Works:** Feasibility is monotone.

**Visual Example:**

```text
Statement:     "maximize the minimum"
        |
        v
Math model:    can(X) monotone
        |
        v
Transform:     Binary search over answer
        |
        v
Check on:      CF 1201C
        |
        v
Algorithm:     complexity O(n log V)
```

**Example:** CF 1201C

**Common Mistakes:** Non-monotone `can`.

**Complexity:** O(n log V)

**Representative Problems:** CF 1201C

### Form 39. Stars and Bars

#### 🌍 Real-World Example — Form 39. Stars and Bars

**Think of this:** Imagine a small everyday situation where the quantities in **Form 39. Stars and Bars** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "distribute identical objects"

**Mathematical Model:** `Σxi=n`

**Core Transformation:** `C(n+k-1,k-1)`

**Why It Works:** Bars separate stars.

**Visual Example:**

```text
Statement:     "distribute identical objects"
        |
        v
Math model:    Σxi=n
        |
        v
Transform:     C(n+k-1,k-1)
        |
        v
Check on:      n=4,k=3 → C(6,2)=15
        |
        v
Algorithm:     complexity O(1) with precomputed factorials
```

**Example:** n=4,k=3 → C(6,2)=15

**Common Mistakes:** Mixed lower bounds.

**Complexity:** O(1) with precomputed factorials

**Representative Problems:** Part 6

### Form 40. Diophantine Equation

#### 🌍 Real-World Example — Form 40. Diophantine Equation

**Think of this:** Imagine a small everyday situation where the quantities in **Form 40. Diophantine Equation** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


**Recognition Signals:** "pay exactly", "packs of"

**Mathematical Model:** `ax+by=c`

**Core Transformation:** gcd test, bounded enumeration

**Why It Works:** Bézout.

**Visual Example:**

```text
Statement:     "pay exactly", "packs of"
        |
        v
Math model:    ax+by=c
        |
        v
Transform:     gcd test, bounded enumeration
        |
        v
Check on:      2020a+2021b=4041 ✓
        |
        v
Algorithm:     complexity O(c/a)
```

**Example:** 2020a+2021b=4041 ✓

**Common Mistakes:** x,y must be ≥0.

**Complexity:** O(c/a)

**Representative Problems:** CF 1475B


---

## Part 27. Problem Modeling Library

Each problem uses one fixed layout: Link, Summary, Core Invariant, Step-by-Step Logic, ASCII Trace with pseudocode, then the C++17 solution.

**Contents**

- [Pattern A: Parity & Formula Bounds (Lower Bound + Construction)](#pattern-a-parity--formula-bounds-lower-bound--construction)
  - [Watermelon (CF 4A)](#watermelon-parity-constraint--codeforces--800)
  - [Yet Another Two Integers Problem (CF 1409A)](#yet-another-two-integers-problem-arithmetic--ceil-division--codeforces--800)
  - [K-divisible Sum (CF 1476A)](#k-divisible-sum-bounding--codeforces--1000)
  - [Sweet Problem (CF 1263A)](#sweet-problem-bounding--codeforces--1200)
  - [Distance and Axis (CF 1401A)](#distance-and-axis-grid-parity--codeforces--1100)
- [Pattern B: Invariants (Sum / GCD / Difference)](#pattern-b-invariants-sum--gcd--difference)
  - [Friends and Candies (CF 1538B)](#friends-and-candies-sum-constraint--codeforces--800)
  - [Exciting Bets (CF 1543A)](#exciting-bets-gcd-constraint--codeforces--900)
  - [EhAb AnD gCd (CF 1325A)](#ehab-and-gcd-constructive-equation--codeforces--800)
- [Pattern C: Number Theory, Modulo & Diophantine Formulas](#pattern-c-number-theory-modulo--diophantine-formulas)
  - [Odd Divisor (CF 1475A)](#odd-divisor-number-theory--powers-of-two--codeforces--900)
  - [Required Remainder (CF 1374A)](#required-remainder-modulo-constraint--codeforces--1000)
  - [New Year's Number (CF 1475B)](#new-years-number-diophantine-equation--codeforces--900)
  - [K-th Not Divisible by n (CF 1352C)](#k-th-not-divisible-by-n-divisibility--counting--codeforces--1200)
  - [Modulo Sum (CF 577B)](#modulo-sum-pigeonhole--codeforces--1900)
- [Pattern D: Pair Conditions -> Algebra + Sorting/Frequency](#pattern-d-pair-conditions---algebra--sortingfrequency)
  - [Same Differences (CF 1520D)](#same-differences-pair-counting--codeforces--1200)
  - [Number of Pairs (CF 1538C)](#number-of-pairs-sorting-as-transformation--codeforces--1300)
  - [Pair of Topics (CF 1324D)](#pair-of-topics-algebra--transformation--codeforces--1400)
  - [Honest Coach (CF 1360B)](#honest-coach-sorting-as-transformation--codeforces--800)
- [Pattern E: Binary Search on the Answer](#pattern-e-binary-search-on-the-answer)
  - [Maximum Median (CF 1201C)](#maximum-median-binary-search-equation--codeforces--1400)

### Pattern A: Parity & Formula Bounds (Lower Bound + Construction)

#### 🌍 Real-World Example — Pattern A: Parity & Formula Bounds (Lower Bound + Construction)

**Think of this:** Imagine a small everyday situation where the quantities in **Pattern A: Parity & Formula Bounds (Lower Bound + Construction)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


Many 800–1200 problems reduce to one bound argument: prove `answer >= X` (a resource or geometric limit), then show a construction that reaches `X`.

* **Signals:** "minimum moves", "maximum days", "each step changes by at most K", "split into even parts".
* **Tools:** ceil division `(a+b-1)/b`, parity (`x%2`), `min` of two independent bounds, largest-element (extremal) argument.
* **Workflow:** write the resource limit -> write the limiting element -> take `min` -> check tiny cases.
* **Pitfall:** forgetting the smallest legal value (e.g. `w>=4`) or overflow in `n*k`.

Problem Link: [Watermelon](https://codeforces.com/problemset/problem/4/A)

**Problem Summary:** Given weight `w`, decide if it can be split into two positive even parts. Output `YES` or `NO`.

#### Watermelon (Parity Constraint / Codeforces / 800)

* **Core Invariant / Key Insight:** Even+even is even and each part is at least 2, so the condition is `w % 2 == 0 && w >= 4`.

* **Key Formula:**
```text
w = a + b,  a, b even, a >= 2, b >= 2
=> w even  and  w >= 4
```

* **Step-by-Step Logic:**
1. Read `w`.
2. Check parity: `w % 2 == 0`.
3. Check minimum: `w >= 4` (parts `2` and `w-2`). Output in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        w = 8

Step 1:         parity check
                8 % 2 = 0  --> even OK

Step 2:         size check
                8 >= 4 --> OK, split = (2, 6)

Step 3:         w = 2 --> even but 2 < 4 --> NO

Final Answer:   w=8 -> YES,  w=2 -> NO,  w=7 -> NO

Pseudocode:
    read w
    if w % 2 == 0 and w >= 4: print YES
    else: print NO
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int w;
    cin >> w;

    // even + even = even, and each part >= 2  =>  w even and w >= 4
    if (w % 2 == 0 && w >= 4) cout << "YES\n";
    else cout << "NO\n";
    return 0;
}
```

Problem Link: [Yet Another Two Integers Problem](https://codeforces.com/problemset/problem/1409/A)

**Problem Summary:** Given `a` and `b`, one move changes `a` by an integer in `[-10,10]`. Output the minimum moves to reach `b`.

#### Yet Another Two Integers Problem (Arithmetic / Ceil Division / Codeforces / 800)

* **Core Invariant / Key Insight:** Each move covers at most 10 units of the gap `d = |a-b|`, so the answer is `ceil(d/10) = (d+9)/10`.

* **Key Formula:**
```text
d = |a - b|
k = ceil(d / 10) = (d + 9) / 10
```

* **Step-by-Step Logic:**
1. Read `a, b`; compute `d = |a-b|`.
2. Lower bound: `k` moves cover at most `10k`, so `k >= d/10`.
3. Output `(d + 9) / 10` (k-1 full jumps + one partial) in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = 13, b = 42

Step 1:         d = |13 - 42| = 29

Step 2:         ceil(29/10) = (29 + 9) / 10 = 38 / 10 = 3

Step 3:         moves: 13 -> 23 -> 33 -> 42

Final Answer:   3

Pseudocode:
    d = abs(a - b)
    print (d + 9) / 10
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long a, b;
        scanf("%lld %lld", &a, &b);

        long long d = llabs(a - b);
        printf("%lld\n", (d + 9) / 10);   // ceil(d / 10)
    }
    return 0;
}
```

Problem Link: [K-divisible Sum](https://codeforces.com/problemset/problem/1476/A)

**Problem Summary:** Given `n, k`, choose a positive array of length `n` with sum divisible by `k` minimizing the maximum. Output that maximum.

#### K-divisible Sum (Bounding / Codeforces / 1000)

* **Core Invariant / Key Insight:** Use the smallest legal sum `S = ceil(n/k)*k` and spread it evenly: `max = ceil(S/n)`.

* **Key Formula:**
```text
S = ceil(n / k) * k          (smallest valid sum, S >= n)
answer = ceil(S / n) = (S + n - 1) / n
```

* **Step-by-Step Logic:**
1. Compute `S = ((n + k - 1) / k) * k`.
2. Note `S >= n` guarantees positivity.
3. Print `(S + n - 1) / n` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 4, k = 3

Step 1:         S = ceil(4/3) * 3 = 6

Step 2:         spread 6 over 4 --> [2, 2, 1, 1]

Final Answer:   ceil(6/4) = 2

Pseudocode:
    S = (n + k - 1) / k * k
    print (S + n - 1) / n
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long n, k;
        scanf("%lld %lld", &n, &k);

        long long S = (n + k - 1) / k * k;   // smallest multiple of k that is >= n
        printf("%lld\n", (S + n - 1) / n);  // ceil(S / n)
    }
    return 0;
}
```

Problem Link: [Sweet Problem](https://codeforces.com/problemset/problem/1263/A)

**Problem Summary:** Given three candy piles, each day eat one from two different piles. Output the maximum days.

#### Sweet Problem (Bounding / Codeforces / 1200)

* **Core Invariant / Key Insight:** Two bounds: `S/2` (2 per day) and `S - M` (largest pile needs partners); answer `min(S/2, S-M)`.

* **Key Formula:**
```text
S = a + b + c,   M = max(a, b, c)
days <= S / 2        (2 candies per day)
days <= S - M        (largest pile needs partners)
answer = min(S/2, S - M)
```

* **Step-by-Step Logic:**
1. Compute `S = a+b+c`, `M = max(a,b,c)`.
2. Bound 1: `S / 2`. Bound 2: `S - M`.
3. Print the minimum in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        (a,b,c) = (1, 1, 10)

Step 1:         S = 12, M = 10

Step 2:         S/2 = 6,  S - M = 2

Final Answer:   min(6, 2) = 2

Pseudocode:
    S = a + b + c; M = max(a, b, c)
    print min(S / 2, S - M)
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long a, b, c;
        scanf("%lld %lld %lld", &a, &b, &c);

        long long S = a + b + c;
        long long M = max({a, b, c});
        printf("%lld\n", min(S / 2, S - M));
    }
    return 0;
}
```

Problem Link: [Distance and Axis](https://codeforces.com/problemset/problem/1401/A)

**Problem Summary:** Given `n` (position of A) and `k`, find the minimum ±1 moves of A so a point `B` with `|OB - AB| = k` exists. Output the count.

#### Distance and Axis (Grid Parity / Codeforces / 1100)

* **Core Invariant / Key Insight:** Triangle inequality forces `k <= n`; halving forces `(n-k)` even. So the answer is `k-n` if `n<k`, else `(n-k) % 2`.

* **Key Formula:**
```text
| |OB| - |AB| | <= OA = n   =>   need  k <= n
2b - n = +-k  =>  b = (n +- k) / 2   =>  need (n - k) even
answer = k - n  (n < k),   (n - k) mod 2  (n >= k)
```

* **Step-by-Step Logic:**
1. Read `n, k`.
2. If `n < k` print `k - n`.
3. Else print `(n - k) % 2` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 5, k = 8

Step 1:         n < k --> need A at 8

Step 2:         steps = 8 - 5 = 3

Step 3:         n = 1, k = 0 --> (1 - 0) % 2 = 1

Final Answer:   3   (and 1 for the second case)

Pseudocode:
    if n < k: print k - n
    else: print (n - k) % 2
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n, k;
        scanf("%d %d", &n, &k);

        if (n < k) printf("%d\n", k - n);
        else       printf("%d\n", (n - k) % 2);
    }
    return 0;
}
```

### Pattern B: Invariants (Sum / GCD / Difference)

#### 🌍 Real-World Example — Pattern B: Invariants (Sum / GCD / Difference)

**Think of this:** Imagine a small everyday situation where the quantities in **Pattern B: Invariants (Sum / GCD / Difference)** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


An invariant is a quantity the operation cannot change. Write `before -> after`, compute the delta, keep what is zero.

* **Signals:** "any number of times", "redistribute", "add/subtract to both".
* **Tools:** sum invariant (`S % n`), difference invariant (`a-b`), gcd divides the difference.
* **Workflow:** delta analysis -> derive necessary condition -> construct for sufficiency.
* **Pitfall:** an invariant is only necessary; verify sufficiency.

Problem Link: [Friends and Candies](https://codeforces.com/problemset/problem/1538/B)

**Problem Summary:** Given candies `a_i` for `n` friends, choose the fewest friends whose pooled candies are redistributed so everyone is equal. Output that count or `-1`.

#### Friends and Candies (Sum Constraint / Codeforces / 800)

* **Core Invariant / Key Insight:** Redistribution keeps `S = sum(a_i)` fixed, so equal means `S % n == 0` and target `t = S/n`; exactly the friends with `a_i > t` must be chosen.

* **Key Formula:**
```text
S = sum(a_i)          (invariant)
n * t = S  =>  t = S / n,  need S mod n = 0
answer = #{ i : a_i > t }
```

* **Step-by-Step Logic:**
1. Compute `S = sum(a_i)`.
2. If `S % n != 0` print `-1`; else `t = S / n`.
3. Count `a_i > t` and print it in O(n).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = [4, 5, 2, 5], n = 4

Step 1:         S = 16, S % 4 = 0 OK

Step 2:         t = 16 / 4 = 4

Step 3:         a_i > 4 : 5 (idx2), 5 (idx4) --> count = 2

Final Answer:   2

Pseudocode:
    S = sum(a)
    if S % n != 0: print -1
    else: print count(a_i > S / n)
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n;
        scanf("%d", &n);

        vector<long long> a(n);
        long long sum = 0;
        for (auto &x : a) {
            scanf("%lld", &x);
            sum += x;
        }

        if (sum % n != 0) {           // equal split impossible
            puts("-1");
            continue;
        }

        long long target = sum / n;
        int cnt = 0;
        for (auto x : a) cnt += (x > target);
        printf("%d\n", cnt);
    }
    return 0;
}
```

Problem Link: [Exciting Bets](https://codeforces.com/problemset/problem/1543/A)

**Problem Summary:** Given `a, b`, a move adds or subtracts 1 from both (subtract only if both > 0). Output the maximum gcd and the minimum moves to reach it.

#### Exciting Bets (GCD Constraint / Codeforces / 900)

* **Core Invariant / Key Insight:** `a - b` never changes, so `gcd | d = |a-b|`; the max is `d`, reached by moving `a` to the nearest multiple of `d`.

* **Key Formula:**
```text
a - b = const  (invariant)
gcd(a+k, b+k) divides d = |a - b|   =>   max gcd = d
moves = min(a mod d, d - (a mod d))
```

* **Step-by-Step Logic:**
1. If `a == b` print `0 0` (gcd can grow forever).
2. Else `d = |a-b|`, `r = a % d`.
3. Print `d` and `min(r, d - r)` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = 8, b = 5

Step 1:         d = 3   (difference invariant)

Step 2:         r = 8 % 3 = 2

Step 3:         moves = min(2, 3-2) = 1  (add 1: 9,6 -> gcd 3)

Final Answer:   3 1

Pseudocode:
    if a == b: print 0 0
    d = abs(a-b); r = a % d
    print d, min(r, d - r)
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long a, b;
        scanf("%lld %lld", &a, &b);

        if (a == b) {
            puts("0 0");                 // gcd can grow forever
            continue;
        }

        long long d = llabs(a - b);
        long long r = a % d;
        printf("%lld %lld\n", d, min(r, d - r));
    }
    return 0;
}
```

Problem Link: [EhAb AnD gCd](https://codeforces.com/problemset/problem/1325/A)

**Problem Summary:** Given `x`, output any positive `a, b` with `lcm(a,b) + gcd(a,b) = x`.

#### EhAb AnD gCd (Constructive Equation / Codeforces / 800)

* **Core Invariant / Key Insight:** Take `a = 1`: then `gcd = 1` and `lcm = b`, so `b = x - 1`.

* **Key Formula:**
```text
a = 1  =>  gcd(1, b) = 1,  lcm(1, b) = b
b + 1 = x  =>  b = x - 1
```

* **Step-by-Step Logic:**
1. Read `x`.
2. Choose `a = 1` (collapses gcd and lcm).
3. Output `1` and `x - 1` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        x = 14

Step 1:         choose a = 1
                gcd(1, b) = 1,  lcm(1, b) = b

Step 2:         b + 1 = 14 --> b = 13

Final Answer:   (1, 13)   check: 13 + 1 = 14

Pseudocode:
    print 1, x - 1
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long x;
        scanf("%lld", &x);

        // a = 1  =>  gcd = 1, lcm = b  =>  b + 1 = x
        printf("1 %lld\n", x - 1);
    }
    return 0;
}
```

### Pattern C: Number Theory, Modulo & Diophantine Formulas

#### 🌍 Real-World Example — Clock Wrapping

**Think of this:** It is **9 o'clock**. What time is it 5 hours later on a 12-hour clock?

**Step-by-step with real numbers:**

1. 9+5=14
2. Clock has only 12 positions
3. 14 wraps to 2
4. 14 mod 12 = 2

**📐 Mathematical Model / Visual**

```text
       12
   11      1
10           2  <- 9+5
9             3
 8           4
   7   6   5

(9+5) mod 12 = 2
```

**What the math means:** Modulo means **wrap around after a fixed cycle length**.

**🧠 CF Recognition:** Look for **circular arrays, clocks, weekdays, repeated positions, remainder classes**.


Convert conditions on divisibility and remainders into arithmetic progressions, floor/ceil, or gcd tests.

* **Signals:** "remainder", "divisor", "k-th number not divisible", "pay exactly with coins".
* **Tools:** `k = qx + y`, `n = 2^k * odd`, `t=a+b` substitution, pigeonhole on prefix remainders.
* **Workflow:** isolate the free integer -> bound it -> closed form or small DP.
* **Pitfall:** `long long`, negative remainders, off-by-one at exact multiples.

Problem Link: [Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

**Problem Summary:** Given `n`, decide whether `n` has an odd divisor greater than 1.

#### Odd Divisor (Number Theory / Powers of Two / Codeforces / 900)

* **Core Invariant / Key Insight:** Write `n = 2^k * m` with `m` odd; the answer is `NO` only when `m = 1`, i.e. `n & (n-1) == 0`.

* **Key Formula:**
```text
n = 2^k * m   (m odd)
odd divisor > 1 exists  <=>  m > 1  <=>  n & (n - 1) != 0
```

* **Step-by-Step Logic:**
1. Read `n` (up to `1e14`, use `long long`).
2. Test whether `n` is a power of two: `n & (n - 1) == 0`.
3. Print `NO` if power of two else `YES` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 6

Step 1:         6 = 2 * 3 --> odd part 3 > 1
                6 & 5 = 0b110 & 0b101 = 0b100 != 0

Step 2:         n = 16 --> 16 & 15 = 0 --> power of two

Final Answer:   6 -> YES,  16 -> NO

Pseudocode:
    if (n & (n - 1)) == 0: print NO
    else: print YES
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long n;
        scanf("%lld", &n);

        // no odd divisor > 1  <=>  n is a power of two
        bool isPow2 = (n & (n - 1)) == 0;
        puts(isPow2 ? "NO" : "YES");
    }
    return 0;
}
```

Problem Link: [Required Remainder](https://codeforces.com/problemset/problem/1374/A)

**Problem Summary:** Given `x, y, n`, find the largest `k <= n` with `k mod x = y`.

#### Required Remainder (Modulo Constraint / Codeforces / 1000)

* **Core Invariant / Key Insight:** Valid numbers are `y, y+x, y+2x, ...`; take `k = (n-y)/x * x + y`.

* **Key Formula:**
```text
k = q*x + y,   k <= n
q = floor((n - y) / x)
k = q*x + y
```

* **Step-by-Step Logic:**
1. Read `x, y, n` as `long long`.
2. Compute `q = (n - y) / x` (largest step count).
3. Print `q * x + y` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        x = 7, y = 5, n = 12345

Step 1:         q = (12345 - 5) / 7 = 12340 / 7 = 1762

Step 2:         k = 1762 * 7 + 5 = 12339

Final Answer:   12339

Pseudocode:
    q = (n - y) / x
    print q * x + y
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long x, y, n;
        scanf("%lld %lld %lld", &x, &y, &n);

        long long q = (n - y) / x;       // largest number of steps
        printf("%lld\n", q * x + y);
    }
    return 0;
}
```

Problem Link: [New Year's Number](https://codeforces.com/problemset/problem/1475/B)

**Problem Summary:** Given `n`, decide if `n = 2020a + 2021b` for non-negative `a, b`.

#### New Year's Number (Diophantine Equation / Codeforces / 900)

* **Core Invariant / Key Insight:** With `t = a+b`, `n = 2020t + b`; take `t = n / 2020`, `b = n % 2020` and require `b <= t`.

* **Key Formula:**
```text
n = 2020a + 2021b = 2020(a + b) + b
t = floor(n / 2020),  b = n mod 2020
YES  <=>  b <= t
```

* **Step-by-Step Logic:**
1. Read `n`.
2. Compute `t = n / 2020` and `b = n % 2020`.
3. Print `YES` iff `b <= t` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 4041

Step 1:         t = 4041 / 2020 = 2,  b = 4041 % 2020 = 1

Step 2:         b = 1 <= t = 2 --> OK (a=1, b=1)

Step 3:         n = 4039 --> t = 1, b = 2019 > 1 --> NO

Final Answer:   4041 -> YES,  4039 -> NO

Pseudocode:
    if n % 2020 <= n / 2020: print YES else NO
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n;
        scanf("%d", &n);

        int total = n / 2020;            // t = a + b
        int extra = n % 2020;            // b
        puts(extra <= total ? "YES" : "NO");
    }
    return 0;
}
```

Problem Link: [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C)

**Problem Summary:** Given `n, k`, find the k-th positive integer not divisible by `n`.

#### K-th Not Divisible by n (Divisibility / Counting / Codeforces / 1200)

* **Core Invariant / Key Insight:** Every block of `n` numbers holds `n-1` valid ones, so answer `= k + (k-1)/(n-1)`.

* **Key Formula:**
```text
each block of n numbers has (n - 1) valid ones
q = (k - 1) / (n - 1)
answer = k + q
```

* **Step-by-Step Logic:**
1. Read `n, k`.
2. Count skipped multiples `q = (k - 1) / (n - 1)`.
3. Print `k + q` in O(1).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 3, k = 7

Step 1:         valid: 1 2 4 5 7 8 10
                7th valid = 10

Step 2:         q = (7-1)/(3-1) = 3

Final Answer:   7 + 3 = 10

Pseudocode:
    print k + (k - 1) / (n - 1)
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        long long n, k;
        scanf("%lld %lld", &n, &k);

        printf("%lld\n", k + (k - 1) / (n - 1));
    }
    return 0;
}
```

Problem Link: [Modulo Sum](https://codeforces.com/problemset/problem/577/B)

**Problem Summary:** Given `n` numbers and `m`, decide if a non-empty subsequence has sum divisible by `m`.

#### Modulo Sum (Pigeonhole / Codeforces / 1900)

* **Core Invariant / Key Insight:** If `n >= m`, prefix remainders `P_0..P_n` must repeat, so `YES`; otherwise DP over remainders in `O(n*m)`.

* **Key Formula:**
```text
P_i = (a_1 + ... + a_i) mod m,   P_0 = 0
n >= m  =>  n + 1 values in m boxes  =>  P_i = P_j  =>  YES
n <  m  =>  DP over remainders, O(n * m)
```

* **Step-by-Step Logic:**
1. If `n >= m` print `YES`.
2. Else keep reachable-remainder set `dp` (start empty).
3. For each `a`: add `a % m` and `(r + a) % m` for all `r`; stop if `0` reachable; O(m^2).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        n = 3, m = 5, a = [1, 2, 3]

Step 1:         n < m --> use DP, dp = {}

Step 2:         a=1: dp = {1}
                a=2: dp = {1, 2, 3}

Step 3:         a=3: dp = {1,2,3} + {4, 0, 1} -> contains 0

Final Answer:   YES   (2 + 3 = 5)

Pseudocode:
    if n >= m: YES
    dp = {}
    for a: dp = dp U {a%m} U {(r+a)%m for r in dp}
           if 0 in dp: YES
    NO
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n, m;
    scanf("%d %d", &n, &m);

    if (n >= m) {                        // pigeonhole on prefix remainders
        puts("YES");
        return 0;
    }

    vector<char> dp(m, 0);               // dp[r] = some subsequence has sum % m == r
    for (int i = 0; i < n; i++) {
        int a;
        scanf("%d", &a);
        a %= m;

        vector<char> nd = dp;
        nd[a] = 1;                       // take a alone
        for (int r = 0; r < m; r++)
            if (dp[r]) nd[(r + a) % m] = 1;
        dp = nd;

        if (dp[0]) {
            puts("YES");
            return 0;
        }
    }
    puts("NO");
    return 0;
}
```

### Pattern D: Pair Conditions -> Algebra + Sorting/Frequency

#### 🌍 Real-World Example — Pattern D: Pair Conditions -> Algebra + Sorting/Frequency

**Think of this:** Imagine a small everyday situation where the quantities in **Pattern D: Pair Conditions -> Algebra + Sorting/Frequency** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


If a condition couples `i` and `j`, separate the variables so each index has one derived key, then count with a map or sorted array.

* **Signals:** "count pairs", `a_i + a_j`, `a_j - a_i = j - i`, unordered pairs.
* **Tools:** move terms across `=`/`>`, `b_i = a_i - i`, `c_i = a_i - b_i`, sort + `upper_bound`, `f(r)-f(l-1)`.
* **Workflow:** algebra -> key per index -> frequency (equality) or sort (inequality).
* **Pitfall:** double counting ordered pairs; `int` overflow of pair counts.

Problem Link: [Same Differences](https://codeforces.com/problemset/problem/1520/D)

**Problem Summary:** Given array `a`, count pairs `i<j` with `a_j - a_i = j - i`.

#### Same Differences (Pair Counting / Codeforces / 1200)

* **Core Invariant / Key Insight:** Rearrange to `a_j - j = a_i - i`; count equal values of `b_i = a_i - i`.

* **Key Formula:**
```text
a_j - a_i = j - i
=> a_j - j = a_i - i
b_i = a_i - i   =>   count pairs with b_i = b_j
```

* **Step-by-Step Logic:**
1. Define `b_i = a_i - i`.
2. Scan left to right with a hash map of counts.
3. Add `cnt[b_i]` before incrementing; O(n).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = [3, 5, 1, 4, 6, 6]   (1-indexed)

Step 1:         b = a_i - i = [2, 3, -2, 0, 1, 0]

Step 2:         map: {2:1, 3:1, -2:1, 0:1, 1:1}
                b_6 = 0 seen once --> ans += 1

Final Answer:   1

Pseudocode:
    ans = 0
    for i in 1..n:
        ans += cnt[a_i - i]
        cnt[a_i - i] += 1
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n;
        scanf("%d", &n);

        map<long long, long long> cnt;
        long long ans = 0;
        for (int i = 1; i <= n; i++) {
            long long a;
            scanf("%lld", &a);
            ans += cnt[a - i]++;         // pairs with equal key a_i - i
        }
        printf("%lld\n", ans);
    }
    return 0;
}
```

Problem Link: [Number of Pairs](https://codeforces.com/problemset/problem/1538/C)

**Problem Summary:** Given array `a` and `l, r`, count pairs `i<j` with `l <= a_i + a_j <= r`.

#### Number of Pairs (Sorting as Transformation / Codeforces / 1300)

* **Core Invariant / Key Insight:** Pairs are unordered, so sort; answer is `f(r) - f(l-1)` where `f(X)` counts pairs with sum `<= X` via `upper_bound`.

* **Key Formula:**
```text
count( L <= a_i + a_j <= R ) = f(R) - f(L - 1)
f(X) = #{ i < j : a_i + a_j <= X }   (sorted, upper_bound)
```

* **Step-by-Step Logic:**
1. Sort `a`.
2. For each `i`, count `j>i` with `a_j <= X - a_i` (binary search).
3. Print `f(r) - f(l - 1)` in O(n log n).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = [5, 1, 2], l = 4, r = 7

Step 1:         sorted = [1, 2, 5]

Step 2:         f(7): i=0 -> j in {1,2}: 2 ; i=1 -> j=2: 1  --> 3

Step 3:         f(3): i=0 -> a_j <= 2 : 1 ; i=1 -> a_j <= 1 : 0 --> 1

Final Answer:   3 - 1 = 2

Pseudocode:
    sort(a)
    f(X) = sum_i ( upper_bound(a[i+1..], X - a[i]) - (i+1) )
    print f(r) - f(l - 1)
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n;
        long long l, r;
        scanf("%d %lld %lld", &n, &l, &r);

        vector<long long> a(n);
        for (auto &x : a) scanf("%lld", &x);
        sort(a.begin(), a.end());

        // f(X) = number of pairs i < j with a[i] + a[j] <= X
        auto f = [&](long long X) {
            long long cnt = 0;
            for (int i = 0; i < n; i++) {
                auto it = upper_bound(a.begin() + i + 1, a.end(), X - a[i]);
                cnt += it - (a.begin() + i + 1);
            }
            return cnt;
        };

        printf("%lld\n", f(r) - f(l - 1));
    }
    return 0;
}
```

Problem Link: [Pair of Topics](https://codeforces.com/problemset/problem/1324/D)

**Problem Summary:** Given arrays `a, b`, count pairs `i<j` with `a_i + a_j > b_i + b_j`.

#### Pair of Topics (Algebra / Transformation / Codeforces / 1400)

* **Core Invariant / Key Insight:** Move terms: `(a_i - b_i) + (a_j - b_j) > 0`; with `c_i = a_i - b_i` count pairs with `c_i + c_j > 0` after sorting.

* **Key Formula:**
```text
a_i + a_j > b_i + b_j
=> (a_i - b_i) + (a_j - b_j) > 0
c_i = a_i - b_i   =>   count pairs with c_i + c_j > 0
```

* **Step-by-Step Logic:**
1. Build `c_i = a_i - b_i` and sort.
2. For each `i`, count `j>i` with `c_j > -c_i` (`upper_bound`).
3. Sum the counts in O(n log n).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = [4,8,2,6,2], b = [4,5,4,1,3]

Step 1:         c = [0, 3, -2, 5, -1]
                sorted c = [-2, -1, 0, 3, 5]

Step 2:         i=-2: need c_j > 2  --> {3,5} = 2
                i=-1: need c_j > 1  --> {3,5} = 2
                i= 0: need c_j > 0  --> {3,5} = 2
                i= 3: need c_j > -3 --> {5}   = 1
                i= 5: no j > i      --> 0

Final Answer:   2 + 2 + 2 + 1 = 7

Pseudocode:
    c = a - b; sort(c)
    for i: ans += (n) - upper_bound(c[i+1..], -c[i])
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    scanf("%d", &n);

    vector<long long> a(n), c(n);
    for (auto &x : a) scanf("%lld", &x);
    for (int i = 0; i < n; i++) {
        long long b;
        scanf("%lld", &b);
        c[i] = a[i] - b;                 // c_i = a_i - b_i
    }
    sort(c.begin(), c.end());

    long long ans = 0;
    for (int i = 0; i < n; i++) {
        // count j > i with c[j] > -c[i]
        auto it = upper_bound(c.begin() + i + 1, c.end(), -c[i]);
        ans += c.end() - it;
    }
    printf("%lld\n", ans);
    return 0;
}
```

Problem Link: [Honest Coach](https://codeforces.com/problemset/problem/1360/B)

**Problem Summary:** Given strengths `s_i`, split into two non-empty teams minimizing `|max(A) - min(B)|`. Output the minimum value.

#### Honest Coach (Sorting as Transformation / Codeforces / 800)

* **Core Invariant / Key Insight:** After sorting, the best split is at the smallest adjacent gap, and every split is at least that large.

* **Key Formula:**
```text
sorted: s_1 <= s_2 <= ... <= s_n
answer = min over i of (s_{i+1} - s_i)
```

* **Step-by-Step Logic:**
1. Read and sort `s`.
2. Scan adjacent differences `s[i+1] - s[i]`.
3. Output the minimum in O(n log n).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        s = [3, 1, 2, 6, 4]

Step 1:         sort --> [1, 2, 3, 4, 6]

Step 2:         gaps = [1, 1, 1, 2]

Final Answer:   min gap = 1

Pseudocode:
    sort(s)
    ans = INF
    for i in 0..n-2: ans = min(ans, s[i+1] - s[i])
    print ans
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int t;
    scanf("%d", &t);
    while (t--) {
        int n;
        scanf("%d", &n);

        vector<int> s(n);
        for (auto &x : s) scanf("%d", &x);
        sort(s.begin(), s.end());

        int ans = INT_MAX;
        for (int i = 0; i + 1 < n; i++)
            ans = min(ans, s[i + 1] - s[i]);   // smallest adjacent gap
        printf("%d\n", ans);
    }
    return 0;
}
```

### Pattern E: Binary Search on the Answer

#### 🌍 Real-World Example — Pattern E: Binary Search on the Answer

**Think of this:** Imagine a small everyday situation where the quantities in **Pattern E: Binary Search on the Answer** are actual counts, distances, groups, prices, positions, or repeated operations.

**📐 Mathematical Model / Visual**

```text
real quantities
      ↓
name the changing/unknown values
      ↓
write the exact relation from this topic
      ↓
test it using small concrete numbers
      ↓
use the same relation in the CF problem
```

**🧠 CF Recognition:** When a statement has the same relationship between quantities, translate the story into this mathematical form before choosing an algorithm.


When 'can we reach value `x`?' is monotone, binary search `x` and compute a cheap feasibility cost.

* **Signals:** "maximize the minimum/median", limited operations `k`.
* **Tools:** `cost(x)` sum of shortfalls, monotonic check, `lo + (hi-lo+1)/2`.
* **Workflow:** guess `x` -> compute cost -> compare with budget -> shrink.
* **Pitfall:** wrong upper bound (`a[m]+k`), overflow, infinite loop with wrong mid.

Problem Link: [Maximum Median](https://codeforces.com/problemset/problem/1201/C)

**Problem Summary:** Given odd-length array and `k` increments, maximize the median. Output it.

#### Maximum Median (Binary Search Equation / Codeforces / 1400)

* **Core Invariant / Key Insight:** Median `>= x` iff every element from the median index onward is `>= x`; cost is monotone in `x`, so binary search on `x`.

* **Key Formula:**
```text
median >= x  <=>  a_i >= x for all i >= m,   m = (n-1)/2
cost(x) = sum_{i >= m} max(0, x - a_i)
answer = max x with cost(x) <= k     (cost is monotone)
```

* **Step-by-Step Logic:**
1. Sort `a`; median index `m = (n-1)/2`.
2. `cost(x) = sum_{i>=m} max(0, x - a_i)`.
3. Binary search largest `x` with `cost(x) <= k`; O(n log V).

* **ASCII Execution Trace / Visual Dry Run:**
```text
Initial:        a = [1, 3, 5], k = 2, m = 1

Step 1:         cost(4) = (4-3) + 0 = 1 <= 2  OK
Step 2:         cost(5) = (5-3) + 0 = 2 <= 2  OK
Step 3:         cost(6) = 3 + 1 = 4  > 2  NO

Final Answer:   5

Pseudocode:
    sort(a); m = (n-1)/2
    lo = a[m]; hi = a[m] + k
    while lo < hi:
        mid = (lo + hi + 1) / 2
        if cost(mid) <= k: lo = mid else hi = mid - 1
    print lo
```

* **C++17 Solution:**
```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    long long k;
    scanf("%d %lld", &n, &k);

    vector<long long> a(n);
    for (auto &x : a) scanf("%lld", &x);
    sort(a.begin(), a.end());

    int m = (n - 1) / 2;                 // median index

    auto cost = [&](long long x) {       // operations to lift upper half to x
        long long c = 0;
        for (int i = m; i < n; i++)
            if (a[i] < x) c += x - a[i];
        return c;
    };

    long long lo = a[m], hi = a[m] + k;
    while (lo < hi) {
        long long mid = lo + (hi - lo + 1) / 2;
        if (cost(mid) <= k) lo = mid;
        else                hi = mid - 1;
    }
    printf("%lld\n", lo);
    return 0;
}
```


---

## Part 28. Same Problem, Multiple Models

| Problem | Model A | Model B | Which is simpler |
|---|---|---|---|
| Same Differences (CF 1520D) | enumerate pairs `a_j-a_i=j-i` O(n²) | `b_i=a_i-i` + frequency O(n) | B (algebra removes the pair) |
| Number of Pairs (CF 1538C) | count pair sums by frequency of values | sort + binary search | B when values are huge |
| Friends and Candies (CF 1538B) | simulate redistribution | sum invariant | B (no simulation) |
| Subarray sum divisible by m | enumerate all (l,r) | prefix remainders equal | B |
| Manhattan reach | BFS on grid | `\|dx\|+\|dy\|<=k` + parity | B |
| Sum over subarrays | loop all subarrays | contribution `i(n-i+1)` | B |
| Distinct pair counting | pairs O(n²) | `Σ f(f-1)/2` | B |

**How to choose:** prefer the model that (1) removes a loop dimension, (2) has a proof of two lines, (3) uses only `long long`. If two models tie, pick the one whose correctness you can verify by brute force on tiny input.

Additional pairs: *prefix sum vs equation* (`P[r]-P[l-1]=K` gives a hash-map algorithm); *2D coordinates vs transformed coordinates* (Manhattan ⇒ Chebyshev via `u=x+y, v=x-y`); *simulation vs invariant* (simulate 1e9 steps ⇒ find the invariant).

---

## Part 29. Constraints → Expected Mathematics

| Constraint | Likely approach |
|---|---|
| n ≤ 10 | permutations `n!` / brute force |
| n ≤ 20 | subset / bitmask `2^n` |
| n ≤ 40 | meet-in-the-middle |
| n ≤ 500 | `O(n³)` |
| n ≤ 5000 | `O(n²)` |
| n ≤ 2e5 | `O(n log n)`, `O(n)` |
| n ≤ 1e6 | `O(n)` / sieve |
| value ≤ 1e6 | frequency array / sieve |
| value ≤ 1e9, n small | factor via `√`, values as keys in map |
| value ≤ 1e18 | formula, `long long`, `__int128` for products |
| many range queries | prefix sums |
| many range updates | difference array |
| huge exponent | fast exponentiation |
| answer "mod 998244353" | combinatorics + inverses |
| `t` up to 1e4–1e5 tests, sum n bounded | per-test O(n) |
| `k ≤ log`, `k ≤ 30` | bit-by-bit |
| `m ≤ 1e3` | DP over remainders |

**These are clues, not rules.** A tiny `n` can hide a formula; large `n` with small `m` (CF 577B) flips to pigeonhole.

---

## Part 30. How to Discover the Equation

Ask in order:

```text
 1. What quantities exist?              9. Can I count instead of simulate?
 2. Which quantities change?           10. Can I sort and create inequalities?
 3. Which stay constant?               11. Can I transform coordinates?
 4. What is the operation, precisely?  12. Can I solve each bit independently?
 5. Can I write before/after?          13. Can I solve each prime independently?
 6. Is there parity?                   14. Is this actually an equation?
 7. Is there modulo?                   15. Is there a lower/upper bound?
 8. Is gcd preserved?
```

**Worked example A (Sweet Problem).** Quantities `a,b,c`. Operation removes 1 from two piles ⇒ total drops by 2 ⇒ days ≤ `S/2` (Q9,Q15). Also largest pile `M` needs distinct partners ⇒ days ≤ `S-M`. Answer `min` (Q15).

**Worked example B (Same Differences).** Quantities `a_i, i`. Condition mixes `i` and `j` ⇒ separate variables (Q14, algebra): `a_j-j=a_i-i`.

**Worked example C (Odd Divisor).** Quantities: `n`. Q13: independence per prime ⇒ only the exponent of 2 matters.

---

## Part 31. How to Discover Invariants (Operation-Delta Analysis)

Recipe: write `state → state'`, compute Δ of candidate quantities, keep those with Δ=0 (invariant) or Δ of fixed sign (monovariant).

```text
Op 1: Ai += 1, Aj -= 1        Δsum = +1-1 = 0                     => sum invariant
Op 2: Ai += 2                 Δ(Ai mod 2) = 2 mod 2 = 0           => parity of Ai invariant
Op 3: Ai += 1, Aj += 1        Δsum = 2                            => parity of sum invariant
Op 4: Ai := Ai - Aj           gcd(Ai-Aj,Aj)=gcd(Ai,Aj)            => gcd invariant
Op 5: Ai ^= x, Aj ^= x        Δxor = x^x = 0                      => total XOR invariant
Op 6: swap(Ai,Aj)             multiset unchanged                  => frequencies invariant
Op 7: Ai += m                 Δ(Ai mod m) = 0                     => residue classes invariant
Op 8: add c to every element  Ai-Aj unchanged                     => differences invariant
Op 9: Ai:=Ai*2                v2 changes by 1, odd part fixed     => odd part invariant
Op 10: replace (x,y) by (x+y) count -1 each time, sum fixed       => sum invariant + count monovariant
Op 11: rotate array           cyclic order fixed                  => circular sequence invariant
Op 12: swap adjacent equal-parity  relative order of opposite parity fixed => ordering invariant
```

For each, ask afterward: *Is the invariant also sufficient?* (Is every state with the same invariant reachable?) If yes → answer is "invariants match". If no → add more structure or find a construction.

---

## Part 32. Brute Force → Math

```text
Enumerate all pairs           -> frequency / contribution / algebra
Repeated addition             -> multiplication / AP formula
Repeated doubling             -> powers / logarithms
Repeated cyclic operations    -> modulo, orbit length
Simulating redistribution     -> invariant (sum, gcd)
Testing all distributions     -> combinatorics (stars and bars)
Searching all coordinates     -> distance transformation / median
Repeated range updates        -> difference array
Repeated range sums           -> prefix sums
Testing every candidate answer-> binary search on monotone predicate
Trying all divisors of many   -> sieve / gcd structure
```

Signal phrase: **"n ≤ 2e5 but the naive method is O(n²)"** ⇒ some part of the pair/segment structure must decouple.

---

## Part 33. Mathematical Proof Toolkit

| Method | When | Structure | Mini example |
|---|---|---|---|
| Invariant | "impossible?" | find `I`, show unchanged, show target has other `I` | odd n cannot equalize sum |
| Contradiction | claim "no such object" | assume it exists, derive impossible | n=2 for Watermelon |
| Constructive | "exists / construct" | give explicit object, verify | (1, x−1) |
| Exchange argument | greedy | swap adjacent decision, cost ≤ | sort ascending minimizes |
| Induction | recurrence / all n | base + step | 1+…+n formula |
| Extremal | "take max/min" | look at extreme, it's constrained | largest pile |
| Pigeonhole | forced repeat | count objects vs boxes | prefix remainders |
| Parity | "can/cannot" | count parity change per op | (n−k) even |
| Divisibility | integrality | show `d \| expr` | `gcd \| difference` |
| Lower bound + construction | min answer | (i) ≥X, (ii) achieve X | K-divisible Sum |
| Necessity & sufficiency | "iff" conditions | prove both directions separately | Odd Divisor |

Always test the final claim with a 20-line brute force on small inputs before coding the fast solution.

---

## Part 34. 60-Second Contest Modeling Checklist

```text
[ ] Remove story              [ ] Check frequencies
[ ] Write variables           [ ] Check sorting
[ ] Write target              [ ] Check prefix
[ ] Translate constraints     [ ] Check contribution
[ ] Operation algebraically   [ ] Check coordinates
[ ] Check sum                 [ ] Check bits
[ ] Check difference          [ ] Find invariant
[ ] Check parity              [ ] Find bound (lower & upper)
[ ] Check modulo              [ ] Derive algorithm
[ ] Check gcd                 [ ] Brute-force small, then code
```

---

## Part 35. Rating-Wise Modeling Expectations

```text
800–1000   single observation / arithmetic / parity
1100–1200  basic algebra + counting + greedy math
1300–1400  multiple conditions + invariant + prefix/frequency
1500–1600  transformations + contribution + number theory
1700–1800  deeper invariant + combinatorics + decoupling
1900       multiple forms combined + non-obvious transformation
```

Not absolute: an 1100 can hide a clever invariant; a 1500 can be plain implementation.

---

## Part 36. Master Pattern Index

| Signal | First mathematical thought |
|---|---|
| equalize array | average / sum invariant |
| pair sum | complement `K − Ai` |
| many pairs | contribution / frequency |
| repeated operations | invariant |
| cyclic | modulo |
| grid movement | Manhattan + parity |
| distribute objects | stars and bars |
| minimum distance | median |
| minimum squared distance | mean |
| divisible by both | lcm |
| common divisor | gcd |
| choose subset | bitmask / combinatorics |
| repeated doubling | powers of two, `log` steps |
| permutation | frequency + positions |
| rearrange freely | multiset |
| range updates | difference array |
| range queries | prefix sum |
| subarray sum = K / divisible | prefix map / equal remainders |
| maximize the minimum | binary search on answer |
| minimum operations | lower bound + construction |
| "any valid" | simplest family (1, equal, alternating) |
| pair condition mixing i and j | separate variables |
| pair with sum ≥ L | sort + two pointers |
| n ≥ modulus | pigeonhole |
| XOR / AND / OR | per bit |
| mex | frequency prefix |
| game with piles | Nim XOR |
| ratio | cross multiply, reduce by gcd |
| "sum of all subarrays/pairs" | contribution technique |
| large n, tiny value range | frequency array |
| huge answer mod p | factorials + inverses |

---

## Part 37. Final Mathematical Modeling Workflow

```text
                 CODEFORCES PROBLEM
                        │
                        ▼
                 REMOVE THE STORY
                        │
                        ▼
              DEFINE VARIABLES/SETS
                        │
                        ▼
             WRITE EXACT CONDITIONS
                        │
                        ▼
        ┌───────────────┴───────────────┐
        ▼                               ▼
   EQUATIONS                       OPERATIONS
        │                               │
        ▼                               ▼
  TRANSFORM/SOLVE                FIND INVARIANTS
        │                               │
        └───────────────┬───────────────┘
                        ▼
                 CHECK STRUCTURE
                        │
       ┌────────────────┼────────────────┐
       ▼                ▼                ▼
    PARITY           MODULO             GCD
       │                │                │
       ├────────────────┼────────────────┤
       ▼                ▼                ▼
 FREQUENCY          SORTING            BITS
       │                │                │
       └────────────────┼────────────────┘
                        ▼
             COUNT / BOUND / DECOUPLE
                        │
                        ▼
               DERIVE ALGORITHM
                        │
                        ▼
                  PROVE IT
                        │
                        ▼
                IMPLEMENT C++17
```

> Do not ask first, "Which algorithm is this?"
> Ask: **"What is the mathematical structure of this problem?"**

```text
Mathematical structure
        ↓
Data structure / algorithm
```
