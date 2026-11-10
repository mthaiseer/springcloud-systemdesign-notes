# Number Theory 01 - Units Digits and Last Two Digits

## Table of Contents

- [1. How to use this note](#1-how-to-use-this-note)
- [2. Prerequisites](#2-prerequisites)
- [3. Why only the last digit matters](#3-why-only-the-last-digit-matters)
- [4. Discovering a repeating cycle](#4-discovering-a-repeating-cycle)
- [5. Complete units-digit cycle table](#5-complete-units-digit-cycle-table)
- [6. Using remainders to choose a cycle position](#6-using-remainders-to-choose-a-cycle-position)
- [7. Worked examples with a single power](#7-worked-examples-with-a-single-power)
- [8. Working backwards to find the base digit](#8-working-backwards-to-find-the-base-digit)
- [9. Adding and multiplying last digits](#9-adding-and-multiplying-last-digits)
- [10. Worked examples with sums and products](#10-worked-examples-with-sums-and-products)
- [11. Subtraction and borrowing](#11-subtraction-and-borrowing)
- [12. Power towers explained](#12-power-towers-explained)
- [13. Worked examples with power towers](#13-worked-examples-with-power-towers)
- [14. Understanding the last two digits](#14-understanding-the-last-two-digits)
- [15. Discovering last-two-digit patterns](#15-discovering-last-two-digit-patterns)
- [16. Worked examples with last two digits](#16-worked-examples-with-last-two-digits)
- [17. Choosing the right method](#17-choosing-the-right-method)
- [18. Practice problems](#18-practice-problems)
- [19. Practice solutions](#19-practice-solutions)
- [20. Source coverage and corrections](#20-source-coverage-and-corrections)

## 1. How to use this note

This is a student-friendly rewrite of **NT1.pdf - Vedantu Olympiad Mathematics, Number Theory, Session 01**. It preserves the mathematical topics and distinct problems, combines repeated question/solution slides, and adds the background needed to understand each method.

Read the prerequisites first. For each worked problem, cover the answer, attempt the first step, and then compare your reasoning. You do not need to calculate enormous powers.

**Our goal:** find a small piece of information about a huge number: its final digit or final two digits.

Unless stated otherwise, bases and exponents in the worked problems are positive integers. The cycle tables start at exponent **1**, not exponent 0.


### Reading the notation

This version uses ordinary Markdown and plain-text mathematics. No LaTeX renderer is required.

| Notation | Read it as |
|---|---|
| `2^5` | 2 raised to the power 5 |
| `3 × 4` | 3 multiplied by 4 |
| `17 mod 4` | Remainder when 17 is divided by 4 |
| `U(347)` | Units digit of 347 |
| `a >= 2` | a is at least 2 |
| `a^(b^c)` | a raised to the entire exponent b^c |

Here `^` denotes a mathematical power. It is not multiplication. In programming languages its meaning can differ; no code is needed for this lesson.

**Study order:** understand the small example, discover the pattern, then use the shortcut on a large exponent.

## 2. Prerequisites

Learn these in order. Each is used later in this lesson.

- [2.0 Numbers, digits, and symbols](#20-numbers-digits-and-symbols)
- [2.1 Place value and the units digit](#21-place-value-and-the-units-digit)
- [2.2 Powers mean repeated multiplication](#22-powers-mean-repeated-multiplication)
- [2.3 Two exponent rules we will use](#23-two-exponent-rules-we-will-use)
- [2.4 Division with a remainder](#24-division-with-a-remainder)
- [2.5 Even and odd numbers](#25-even-and-odd-numbers)
- [2.6 Why the last two digits determine the remainder modulo 4](#26-why-the-last-two-digits-determine-the-remainder-modulo-4)
- [2.7 Reading brackets and operation order](#27-reading-brackets-and-operation-order)
- [2.8 Factors, multiples, and divisibility](#28-factors-multiples-and-divisibility)
- [2.9 Expanding brackets without skipping steps](#29-expanding-brackets-without-skipping-steps)
- [2.10 Powers of products](#210-powers-of-products)
- [2.11 Remainders as endings](#211-remainders-as-endings)
- [2.12 Cycle position and starting point](#212-cycle-position-and-starting-point)
- [2.13 Prerequisite checkpoint](#213-prerequisite-checkpoint)

### 2.0 Numbers, digits, and symbols

**Step 1 - Distinguish a number from a digit.**

The digits are 0 through 9. A number can contain several digits: 347 is one number made of three digits.

**Step 2 - Understand the numbers used here.**

Positive integers are 1, 2, 3, and so on. Integers also include 0 and negative whole numbers. The powers in this lesson normally use positive integer bases and positive integer exponents.

**Step 3 - Understand a variable.**

A letter stands for a number. In `a^n`, the letter `a` represents the base and `n` represents the exponent. If `a = 3` and `n = 4`, then `a^n = 3^4`.

**Step 4 - Understand equality.**

An equals sign means the complete values are equal. For example, `2^3 = 8`. Having the same last digit is weaker: 18 and 28 end in the same digit, but they are not equal.

**Check:** In `7^3`, the base is 7 and the exponent is 3. Its value is 343, and its units digit is 3.

### 2.1 Place value and the units digit

In the number 347:

| Hundreds | Tens | Units |
|---:|---:|---:|
| 3 | 4 | 7 |

This means:

`347 = 3 × 100 + 4 × 10 + 7`

The **units digit**, also called the **last digit**, is 7. We write `U(347) = 7`.

The **last two digits** are 47. For a two-digit ending below 10, keep the leading zero: the last two digits of 1207 are **07**.

### 2.2 Powers mean repeated multiplication

In `3^4`, 3 is the **base** and 4 is the **exponent**:

`3^4 = 3 × 3 × 3 × 3 = 81`

Do not confuse `3^4` with `3 × 4 = 12`.

| Expression | Meaning | Value |
|---|---|---:|
| `2^1` | One factor of 2 | 2 |
| `2^2` | `2 × 2` | 4 |
| `2^3` | `2 × 2 × 2` | 8 |
| `2^4` | `2 × 2 × 2 × 2` | 16 |

For a nonzero base, `a^0 = 1`. Handle exponent 0 separately; do not apply a positive-exponent cycle blindly.

### 2.3 Two exponent rules we will use

**Multiplying powers with the same base adds the exponents:**

`a^m × a^n = a^(m + n)`

For example, `2^3 × 2^2` contains five factors of 2, so it equals `2^5`.

**Raising a power to another power multiplies the exponents:**

`(a^m)^n = a^(m × n)`

For example:

`(2^3)^2 = 2^3 × 2^3 = 2^6`

This second rule will help us rewrite `2^1120` later.

### 2.4 Division with a remainder

When we divide 14 by 4, three full groups of 4 fit, with 2 left over:

`14 = 4 × 3 + 2`

| Term | In this example | Meaning |
|---|---:|---|
| Dividend | 14 | Number being divided |
| Divisor | 4 | Size of each group |
| Quotient | 3 | Number of complete groups |
| Remainder | 2 | Amount left over |

In general:

`N = d × q + r; 0 <= r < d`

We say “`N` modulo `d`” to mean this remainder, and write `N mod d`.

Examples: `14 mod 4 = 2`, `28 mod 4 = 0`, and `17 mod 10 = 7`.

**Divisible** means the remainder is zero. Thus 28 is divisible by 4 because `28 = 4 × 7`.

### 2.5 Even and odd numbers

An even number is divisible by 2. An odd number leaves remainder 1 when divided by 2.

| Type | Examples | General form |
|---|---|---|
| Even | 2, 4, 6, 8 | `2 × k` |
| Odd | 1, 3, 5, 7 | `2 × k + 1` |

Here `k` is an integer chosen to represent the number. For positive odd numbers, `k` can be 0, 1, 2, …

Multiplying even numbers gives an even number. Multiplying odd numbers gives an odd number. Therefore, for a positive exponent:

- An even base raised to that exponent is even.
- An odd base raised to that exponent is odd.

Example: `2022^2023` is even because it is a product of even factors. We do not need its value.

### 2.6 Why the last two digits determine the remainder modulo 4

Write `2341 = 2300 + 41`. The number 2300 is divisible by 4 because `100 = 4 × 25`.

So 2341 and 41 leave the same remainder when divided by 4:

`41 = 4 × 10 + 1`

Therefore `2341 mod 4 = 1`.

> To find an integer's remainder modulo 4, you may examine its last two digits. Its last digit alone is not enough: 12 and 22 both end in 2, but their remainders modulo 4 are 0 and 2.

### 2.7 Reading brackets and operation order

In `(A + B) × (C + D)`, add inside each bracket, then multiply the bracket results.

Example: `(4 + 1) × (6 + 9) = 5 × 15 = 75`.

In powers, brackets also matter: `(2^3)^2 = 64`, whereas `2^(3^2) = 512`. We will return to this distinction in power towers.

### 2.8 Factors, multiples, and divisibility

**Step 1 - Begin with an exact multiplication.**

`24 = 4 × 6`.

So 4 and 6 are factors of 24. The number 24 is a multiple of 4 and a multiple of 6.

**Step 2 - Translate this into division.**

`24 ÷ 4 = 6` with no remainder. We say 24 is divisible by 4.

**Step 3 - Notice that two factors of 2 make a factor of 4.**

`24 = 2 × 2 × 6 = 4 × 6`.

This explains why a product containing at least two factors of 2 is divisible by 4. Merely having one factor of 2 guarantees evenness, but not divisibility by 4: 6 is even and is not divisible by 4.

**Example:** `2022^2 = (2 × 1011) × (2 × 1011)`. Regroup the two factors of 2 to get `4 × 1011 × 1011`. Therefore the square is divisible by 4.

### 2.9 Expanding brackets without skipping steps

**Step 1 - Multiply everything inside a bracket.**

`3 × (10 + 2) = 3 × 10 + 3 × 2 = 30 + 6 = 36`.

**Step 2 - Apply the same idea when both factors have brackets.**

To expand `(20 + 3) × (10 + 7)`, multiply each term in the first bracket by each term in the second:

1. `20 × 10 = 200`.
2. `20 × 7 = 140`.
3. `3 × 10 = 30`.
4. `3 × 7 = 21`.
5. Add: `200 + 140 + 30 + 21 = 391`.

This is the distributive rule. It explains why most terms disappear when we track only the final digit.

**Step 3 - Understand a square of a sum.**

`(100 + 23)^2` means `(100 + 23) × (100 + 23)`.

The four products are 10000, 2300, 2300, and 529. Their sum is `10000 + 4600 + 529`. The middle term must not be omitted.

### 2.10 Powers of products

**Step 1 - Write out a small example.**

`(2 × 3)^2 = (2 × 3) × (2 × 3)`.

**Step 2 - Regroup equal factors.**

`(2 × 2) × (3 × 3) = 2^2 × 3^2`.

**Step 3 - State the pattern.**

`(a × b)^n = a^n × b^n` for a positive integer exponent n.

This is why `(2 × 1011)^2023` contains 2023 factors of 2. It certainly contains the two factors of 2 needed for divisibility by 4.

### 2.11 Remainders as endings

**Step 1 - Divide by 10 for one digit.**

`347 = 10 × 34 + 7`, so its units digit is the remainder 7.

**Step 2 - Divide by 100 for two digits.**

`347 = 100 × 3 + 47`, so its final two digits are 47.

**Step 3 - Keep a leading zero when needed.**

`1207 = 100 × 12 + 7`. The remainder is the number 7, but the requested two-digit ending is written **07**.

**Step 4 - Connect equal remainders.**

23 and 3 differ by 20, a multiple of 10, so they have the same remainder modulo 10. Likewise, 123 and 23 differ by 100 and have the same remainder modulo 100.

You may see this called modular arithmetic. Here it simply means calculating with remainders.

### 2.12 Cycle position and starting point

**Step 1 - Number the entries starting at 1.**

In the cycle `2, 4, 8, 6`, position 1 contains 2, position 2 contains 4, position 3 contains 8, and position 4 contains 6.

**Step 2 - Count full repetitions.**

The next four positions repeat the same list. Position 6 is two positions after a complete group of four, so it contains 4.

**Step 3 - Record where the cycle begins.**

Some patterns begin repeating later. The final-two-digit pattern of powers of 6 starts repeating at exponent 2, not exponent 1. Always record both the cycle length and the starting exponent.

### 2.13 Prerequisite checkpoint

Try these before continuing:

1. What does `2^4` mean?
2. What is the remainder when 23 is divided by 4?
3. Why is `2022^3` even?
4. Why is `2022^3` divisible by 4?
5. How do `(2^3)^2` and `2^(3^2)` differ?
6. What are the final two digits of 1007?

**Answers with reasons:**

1. Four factors of 2: `2 × 2 × 2 × 2 = 16`.
2. `23 = 4 × 5 + 3`, so the remainder is 3.
3. Its factors include an even number, so the product is even.
4. Each of its three factors of 2022 contributes a factor of 2; two of those make 4.
5. The first is `8^2 = 64`; the second is `2^9 = 512`.
6. **07**, because `1007 = 100 × 10 + 7`.

## 3. Why only the last digit matters

**Before you start:** Place value, repeated multiplication, and expanding brackets (Sections 2.1, 2.2, and 2.9).

**Goal:** Understand why replacing a base by its last digit preserves the answer.

**Question:** to find the last digit of `173^37`, must we use all of 173?

No. We can work with its last digit, 3.

### Discover the idea with a small multiplication

1. Write `23 = 20 + 3` and `17 = 10 + 7`.
2. Multiply:

   `(20 + 3) × (10 + 7) = 200 + 140 + 30 + 21`

3. The first three terms end in 0.
4. Only `3 × 7 = 21` affects the units digit.
5. Therefore `23 × 17` ends in 1.

The same reasoning works every time we multiply. A power is repeated multiplication, so:

`U(a^n) = U(U(a)^n)`

In words: **replace the base by its units digit, but keep the exponent**.

For example:

`U(173^37) = U(3^37)`

This does **not** say that `173^37 = 3^37`. Only their last digits are equal.

## 4. Discovering a repeating cycle

**Before you start:** Powers and the base-reduction rule from Section 3.

**Goal:** Generate a pattern by multiplying one small digit at a time.

Let us build the last digits of powers of 2 without calculating large numbers.

| Exponent | Calculation using the previous last digit | New last digit |
|---:|---|---:|
| 1 | Start with 2 | 2 |
| 2 | `2 × 2 = 4` | 4 |
| 3 | `4 × 2 = 8` | 8 |
| 4 | `8 × 2 = 16` | 6 |
| 5 | `6 × 2 = 12` | 2 |
| 6 | `2 × 2 = 4` | 4 |

At exponent 5 we return to 2. Since the next step always multiplies by 2, the same sequence starts again.

**Cycle:** `2,4,8,6` - four positions.

| Exponents | Last digit |
|---|---:|
| 1 and 5 | 2 |
| 2 and 6 | 4 |
| 3 and 7 | 8 |
| 4 and 8 | 6 |

Now discover the cycle for 3:

1. Start with 3.
2. `3 × 3 = 9`: keep 9.
3. `9 × 3 = 27`: keep 7.
4. `7 × 3 = 21`: keep 1.
5. `1 × 3 = 3`: back to the start.

**Cycle:** `3,9,7,1`.

> A cycle is a repeating list. Its length tells us how many positions occur before the list starts again.

## 5. Complete units-digit cycle table

**Before you start:** Cycle length and cycle positions from Sections 2.12 and 4.

**Goal:** Recognize which digits need no exponent calculation, parity, or a remainder.

The first entry in each row corresponds to exponent 1.

| Last digit of base | Repeating units digits | Cycle length | What you need about the exponent |
|---:|---|---:|---|
| 0 | 0 | 1 | Only that it is positive |
| 1 | 1 | 1 | Only that it is positive |
| 2 | 2, 4, 8, 6 | 4 | Remainder modulo 4 |
| 3 | 3, 9, 7, 1 | 4 | Remainder modulo 4 |
| 4 | 4, 6 | 2 | Odd or even |
| 5 | 5 | 1 | Only that it is positive |
| 6 | 6 | 1 | Only that it is positive |
| 7 | 7, 9, 3, 1 | 4 | Remainder modulo 4 |
| 8 | 8, 4, 2, 6 | 4 | Remainder modulo 4 |
| 9 | 9, 1 | 2 | Odd or even |

### Why the shorter cycles work

- **0:** multiplying a number ending in 0 by another such number still gives a last digit of 0.
- **1:** `1 × 1 = 1`.
- **5:** `5 × 5 = 25`, so the last digit stays 5.
- **6:** `6 × 6 = 36`, so the last digit stays 6.
- **4:** `4 × 4 = 16` gives 6, then `6 × 4 = 24` returns to 4.
- **9:** `9 × 9 = 81` gives 1, then `1 × 9 = 9` returns to 9.

### Build the cycle for 7

1. Start with 7: the first entry is 7.
2. Multiply `7 × 7 = 49`: keep 9.
3. Multiply `9 × 7 = 63`: keep 3.
4. Multiply `3 × 7 = 21`: keep 1.
5. Multiply `1 × 7 = 7`: the starting entry returns.

The cycle is **7, 9, 3, 1**. For example, exponent 3 selects the third entry, so `7^3` ends in 3.

### Build the cycle for 8

1. Start with 8.
2. Multiply `8 × 8 = 64`: keep 4.
3. Multiply `4 × 8 = 32`: keep 2.
4. Multiply `2 × 8 = 16`: keep 6.
5. Multiply `6 × 8 = 48`: keep 8, returning to the start.

The cycle is **8, 4, 2, 6**. For example, `8^3` ends in 2.

### Quick examples

**Find `U(271^127)`.**

1. The base ends in 1.
2. Every positive power of a number ending in 1 ends in 1.

**Answer: 1.**

**Find `U(239^729)`.**

1. The base ends in 9.
2. Powers of 9 have cycle `9,1`.
3. The exponent 729 is odd, so choose the first entry.

**Answer: 9.**

## 6. Using remainders to choose a cycle position

**Before you start:** Division with a remainder and cycles (Sections 2.4 and 4).

**Goal:** Turn a large exponent into a small cycle position.

Suppose the cycle has length 4 and the exponent is 50.

`50 = 4 × 12 + 2`

The first 48 positions form 12 complete cycles. Two positions remain, so choose the **second** entry of the next cycle.

| Exponent remainder modulo 4 | Cycle position |
|---:|---:|
| 1 | First |
| 2 | Second |
| 3 | Third |
| 0 | Fourth |

### Why remainder 0 means the final position

Take exponent 8. There are exactly two complete cycles. Position 8 is the **end** of the second cycle, not the beginning of a nonexistent “zeroth” cycle position.

For powers of 2, position 8 therefore has last digit 6.

> For positive exponents, remainder 0 means the last entry of the cycle. It does not mean “use exponent 0.”

### What do forms such as 4k + 1 mean?

They describe remainders:

| Form | Example exponents | Remainder modulo 4 |
|---|---|---:|
| `4 × k + 1` | 1, 5, 9, 13 | 1 |
| `4 × k + 2` | 2, 6, 10, 14 | 2 |
| `4 × k + 3` | 3, 7, 11, 15 | 3 |
| `4 × k` | 4, 8, 12, 16 | 0 |

For instance, `13 = 4 × 3 + 1`, so `2^13` ends like `2^1`, giving 2. Similarly, `23 = 4 × 5 + 3`, so `2^23` ends like `2^3`, giving 8.

## 7. Worked examples with a single power

**Before you start:** The cycle table and remainder selection (Sections 5 and 6).

**Goal:** Apply the same four actions: base digit, cycle, remainder, answer.

### Example 1 - Last digit of 2 to the power 50

**Problem:** Find the last digit of `2^50`.

1. The base digit is 2.
2. Its cycle is `2,4,8,6`, of length 4.
3. Divide the exponent: `50 = 4 × 12 + 2`.
4. Remainder 2 means the second entry, which is 4.

> **Answer: 4.** We found the last digit without calculating `2^50`.

### Example 2 - Last digit of 1989 to the power 1989

**Problem:** Find `U(1989^1989)`.

1. Replace the base by its last digit: use `9^1989`.
2. Powers of 9 alternate: `9,1,9,1,...`.
3. The exponent 1989 is odd.
4. Odd positions contain 9.

> **Answer: 9.**

### Example 3 - Last digit of 3 to the power 28

**Problem:** Find `U(3^28)`.

1. The cycle for 3 is `3,9,7,1`.
2. `28 = 4 × 7 + 0`.
3. Remainder 0 means the fourth entry.
4. The fourth entry is 1.

> **Answer: 1.** The remainder is 0; the answer is not 0.

### Example 4 - A large base and a large exponent

**Problem:** Find `U(2357^2341)`.

1. The base ends in 7, whose cycle is `7,9,3,1`.
2. `2341 = 4 × 585 + 1`.
3. Remainder 1 selects the first entry.

> **Answer: 7.**

### More small examples from the lesson

| Problem | Exponent calculation | Selected entry | Answer |
|---|---|---|---:|
| `U(2^10)` | `10 = 4 × 2 + 2` | Second in `2,4,8,6` | 4 |
| `U(2^36)` | `36 = 4 × 9` | Fourth in `2,4,8,6` | 6 |
| `U(173^37)` | `37 = 4 × 9 + 1` | First in `3,9,7,1` | 3 |

## 8. Working backwards to find the base digit

**Before you start:** Even and odd numbers, cycles, and remainder selection.

**Goal:** Test possible base digits when the final digit of the power is already known.

**Problem:** If `n^2023` has units digit 2, what can the units digit of `n` be?

This time the output digit is known, and the input digit is unknown.

**Step 1 - Find the exponent's cycle position.**

`2023 = 4 × 505 + 3`

For four-entry cycles, we need the third entry. Also, 2023 is odd.

**Step 2 - Eliminate impossible base digits.**

An odd number raised to a positive power stays odd. Since the result ends in 2, the base must be even. Its possible last digits are 0, 2, 4, 6, and 8.

**Step 3 - Check each even digit.**

| Last digit of `n` | Reason | Last digit of `n^2023` |
|---:|---|---:|
| 0 | Fixed digit | 0 |
| 2 | Third entry of `2,4,8,6` | 8 |
| 4 | Odd exponent | 4 |
| 6 | Fixed digit | 6 |
| 8 | Third entry of `8,4,2,6` | 2 |

Only 8 produces the required last digit.

> **Answer: the units digit of `n` is 8.** This determines its final digit, not the whole number; 8, 18, 28, … all work.

## 9. Adding and multiplying last digits

**Before you start:** Place value and expanding brackets (Sections 2.1 and 2.9).

**Goal:** Combine endings without losing the original operation order.

### Addition

**Step 1 - Separate tens from units.**

47 is 40 + 7. The number 38 is 30 + 8.

**Step 2 - Add the units.**

`7 + 8 = 15`. Keep 5 and carry 1 ten.

**Step 3 - Check the complete addition.**

`47 + 38 = 85`. Its units digit is also 5.

The units digits give `7 + 8 = 15`, which also ends in 5. A carry affects the next column, not the final digit we keep.

`U(A + B) = U(U(A) + U(B))`

### Multiplication

1. The last digits of 23 and 17 are 3 and 7.
2. Multiply them: `3 × 7 = 21`.
3. Keep the units digit, 1.
4. Check: `23 × 17 = 391`, which ends in 1.

The other products in the bracket expansion are multiples of 10, so they cannot change that final digit.

`U(A × B) = U(U(A) × U(B))`

### The method for an expression

1. Find the last digit of each power separately.
2. Keep the original brackets and operations.
3. Perform the smaller additions or multiplications.
4. Keep the final digit of the result.

You may discard tens after any addition or multiplication. For example, a bracket ending in 15 can be replaced by 5 when only a units digit is needed.

## 10. Worked examples with sums and products

**Before you start:** Units-digit cycles, addition, multiplication, and bracket order.

**Goal:** Solve each power first, then combine its ending with the others.

### Example 5 - Sum of two powers

**Problem:** Find the last digit of `17^1989 + 89^2009`.

1. For `17^1989`, use base digit 7 and cycle `7,9,3,1`.
2. `1989 = 4 × 497 + 1`, so this term ends in 7.
3. For `89^2009`, use base digit 9. The exponent is odd, so this term ends in 9.
4. Add the final digits: `7 + 9 = 16`.
5. Keep the last digit of 16.

> **Answer: 6.**

### Example 6 - Two brackets multiplied

**Problem:** Find the last digit of

`(54^99 + 99^54) × (144^144 + 199^199)`

**Step 1 - Find each term's last digit.**

| Term | Base digit | Exponent type | Last digit |
|---|---:|---|---:|
| `54^99` | 4 | Odd | 4 |
| `99^54` | 9 | Even | 1 |
| `144^144` | 4 | Even | 6 |
| `199^199` | 9 | Odd | 9 |

**Step 2 - Work inside the brackets.**

The first bracket ends like `4 + 1 = 5`.

The second bracket ends like `6 + 9 = 15`, so its final digit is 5.

**Step 3 - Multiply the bracket endings.**

`5 × 5 = 25`

> **Answer: 5.** Using `5 × 15 = 75` also gives the same final digit.

### Example 7 - Product of two powers

**Problem:** Find the last digit of `27^50 × 73^100`.

1. `27^50` has the same final digit as `7^50`.
2. `50 = 4 × 12 + 2`: choose the second entry of `7,9,3,1`, giving 9.
3. `73^100` has the same final digit as `3^100`.
4. `100 = 4 × 25`: choose the fourth entry of `3,9,7,1`, giving 1.
5. Multiply: `9 × 1 = 9`.

> **Answer: 9.**

### Example 8 - A sum of two products

**Problem:** Find the last digit of

`162^162 × 197^150 + 543^540 × 698^638`

**Step 1 - Calculate each exponent remainder.**

1. `162 = 4 × 40 + 2`.
2. `150 = 4 × 37 + 2`.
3. `540 = 4 × 135 + 0`.
4. `638 = 4 × 159 + 2`.

**Step 2 - Choose the matching cycle entries.**

| Power | Relevant cycle | Exponent remainder modulo 4 | Last digit |
|---|---|---:|---:|
| `162^162` | `2,4,8,6` | 2 | 4 |
| `197^150` | `7,9,3,1` | 2 | 9 |
| `543^540` | `3,9,7,1` | 0 | 1 |
| `698^638` | `8,4,2,6` | 2 | 4 |

**Step 3 - Multiply before adding.**

`4 × 9 + 1 × 4 = 36 + 4 = 40`

**Step 4 - Keep the last digit.**

> **Answer: 0.**

## 11. Subtraction and borrowing

**Before you start:** Subtraction, place value, and the meaning of a remainder modulo 10.

**Goal:** Handle borrowing when the difference of two final digits is negative.

Subtraction needs one extra step when the smaller calculation is negative.

For example, `25-19 = 6`. Subtracting just the final digits gives `5-9 = -4`. Add 10 to obtain the corresponding digit: `-4 + 10 = 6`.

This is the same borrowing used in column subtraction: `15-9 = 6`.

### Example 9 - Difference of two powers

**Problem:** Find the units digit of `5^2018-3^2018`.

1. Every positive power of 5 ends in 5.
2. The cycle for 3 is `3,9,7,1`.
3. `2018 = 4 × 504 + 2`, so `3^2018` ends in 9.
4. Subtract the endings: `5-9 = -4`.
5. Add 10: `-4 + 10 = 6`.

> **Answer: 6.** Here the original difference is positive because `5^2018>3^2018`.

When subtracting two individual units digits, one addition of 10 is enough if their difference is negative. For longer expressions, take the final remainder modulo 10 in the range 0 to 9.

## 12. Power towers explained

**Before you start:** Powers, brackets, and the distinction between a power of a power and a tower.

**Goal:** Identify the complete exponent before looking for its parity or remainder.

A power tower has another power in its exponent:

`a^(b^c)`

Read it from the top: the exponent on `a` is the entire number `b^c`.

### A small comparison

| Expression | First step | Result |
|---|---|---:|
| `2^(3^2)` | `3^2 = 9`, then `2^9` | 512 |
| `(2^3)^2` | `2^3 = 8`, then `8^2` | 64 |

The expressions are different. The rule `(a^b)^c = a^(b × c)` does not turn `a^(b^c)` into `a^(b × c)`.

### How to solve a tower without calculating it

Call the entire exponent `E`. First inspect the outer base.

| Outer base's last digit | Information needed about `E` |
|---|---|
| 0, 1, 5, 6 | Is `E` positive? |
| 4, 9 | Is `E` odd or even? |
| 2, 3, 7, 8 | What is `E mod 4`? |

> Let the outer cycle tell you what to find about the exponent. Usually you need only parity or a small remainder.

## 13. Worked examples with power towers

**Before you start:** Section 12 plus parity and factors of 4 from Sections 2.5 and 2.8.

**Goal:** Find only the information the outer cycle requires.

### Example 10 - A fixed final digit

**Problem:** Find the last digit of `6^(2022^2023)`.

1. Set `E = 2022^2023`.
2. We know `E` is positive.
3. Every positive power of 6 ends in 6.

> **Answer: 6.** No further information about `E` is needed.

### Example 11 - Only parity is needed

**Problem:** Find the last digit of `4^(2022^2023)`.

1. Set `E = 2022^2023`.
2. Powers of 4 have final digits `4,6`: odd exponent gives 4, even exponent gives 6.
3. The number `E` is a product of 2023 even factors, so it is even.
4. Choose the even-exponent ending.

> **Answer: 6.**

### Example 12 - An odd exponent inside a tower

**Problem:** Find the last digit of `2019^(2019^2019)`.

1. The outer base ends in 9.
2. A power of 9 ends in 9 for odd exponents and in 1 for even exponents.
3. The entire exponent is `E = 2019^2019`.
4. It is odd because it is a product of odd factors.

> **Answer: 9.**

### Handwritten extension - A four-entry outer cycle

**Problem:** Find the last digit of `3^(2022^2023)`.

**Step 1 - Identify what is needed.**

The cycle for 3 is `3,9,7,1`. We need `E mod 4`, where `E = 2022^2023`.

**Step 2 - Show that `E` is divisible by 4.**

Since `2022 = 2 × 1011`:

`E = (2 × 1011)^2023 = 2^2023 × 1011^2023`

There are at least two factors of 2, so there is a factor `2 × 2 = 4`. Thus `E mod 4 = 0`.

**Step 3 - Choose the fourth entry.**

The fourth entry of `3,9,7,1` is 1.

> **Answer: 1.**

### Handwritten homework - Tower with outer base 2

**Problem:** Find the last digit of `2^(2022^2023)`.

1. Set `E = 2022^2023` so the outer expression becomes `2^E`.
2. The units-digit cycle for 2 is 2, 4, 8, 6; it has length 4.
3. As established above, E contains at least two factors of 2, so E is divisible by 4.
4. The remainder is 0, which selects the fourth entry.

**Answer: 6.**

**Why “even” alone is not enough here:** even exponents can leave remainder 0 or 2 modulo 4. For example, `3^2` ends in 9, but `3^4` ends in 1.

## 14. Understanding the last two digits

**Before you start:** Remainders modulo 100, leading zeros, and expanding squares.

**Goal:** Keep two digits throughout a last-two-digit question.

For one final digit, discard multiples of 10. For two final digits, discard multiples of 100.

Example:

`12347 = 100 × 123 + 47`

The remainder modulo 100 is 47, so the last two digits are 47.

### Why we may reduce the base to its last two digits

**Step 1 - Separate complete hundreds from the ending.**

`123 = 100 + 23`.

**Step 2 - Expand the square using Section 2.9.**

`123^2 = (100 + 23)^2 = 10000 + 4600 + 529`

**Step 3 - Remove complete hundreds.**

10000 and 4600 are multiples of 100, so they contribute nothing to the remainder modulo 100.

**Step 4 - Reduce the remaining term.**

`529 = 100 × 5 + 29`. Its remainder modulo 100 is 29.

**Answer:** the last two digits of `123^2` are **29**.

Thus `123^2` and `23^2` have the same last two digits.

This works for every positive integer exponent: **replace the base by its last two digits, then keep only the last two digits after each multiplication**.

### One digit is not enough for a two-digit question

The numbers 7 and 17 have the same units digit, but:

`7^2 = 49; 17^2 = 289`

Their last two digits are 49 and 89, which differ.

> For last-two-digit problems, keep two digits of the base. Do not reduce 17 to 7.

## 15. Discovering last-two-digit patterns

**Before you start:** The two-digit reduction rule from Section 14 and cycle starting points.

**Goal:** Build each pattern from actual multiplications before using it.

### 15.1 Powers of 5 end in 25 from exponent 2 onward

| Exponent | Power | Last two digits |
|---:|---:|---:|
| 1 | 5 | 05 |
| 2 | 25 | 25 |
| 3 | 125 | 25 |
| 4 | 625 | 25 |
| 5 | 3125 | 25 |

Powers of 5 end in **25 for every exponent at least 2**. The pattern follows because `25 × 5 = 125`, returning to 25.

1. Start at `5^2 = 25`.
2. Multiply its ending by 5: `25 × 5 = 125`.
3. Keep 25 again.
4. Repeat: the ending stays 25.

**Example:** `5^2023` ends in **25**, because `2023 >= 2`.

### 15.2 Powers of 6 have a five-entry cycle after exponent 1

| Exponent | Calculation | Last two digits |
|---:|---|---:|
| 1 | `6` | 06 |
| 2 | `6 × 6 = 36` | 36 |
| 3 | `36 × 6 = 216` | 16 |
| 4 | `16 × 6 = 96` | 96 |
| 5 | `96 × 6 = 576` | 76 |
| 6 | `76 × 6 = 456` | 56 |
| 7 | `56 × 6 = 336` | 36 |

The repeating list **starts at exponent 2**:

`36,16,96,76,56`

For `n >= 2`, the correct remainder mapping is:

| Exponent remainder modulo 5 | Ending for exponents at least 2 |
|---:|---:|
| 0 | 76 |
| 1 | 56 |
| 2 | 36 |
| 3 | 16 |
| 4 | 96 |

Notice that exponent 1 is exceptional: `6^1` ends in 06, but `6^6` ends in 56. Do not extend the repeating portion backwards.

### 15.3 Powers of 7 have a four-entry cycle

| Exponent | Calculation | Last two digits |
|---:|---|---:|
| 1 | `7` | 07 |
| 2 | `7 × 7 = 49` | 49 |
| 3 | `49 × 7 = 343` | 43 |
| 4 | `43 × 7 = 301` | 01 |
| 5 | `1 × 7 = 7` | 07 |

The cycle is **07, 49, 43, 01**, starting at exponent 1.

| Exponent remainder modulo 4 | Last two digits |
|---:|---:|
| 1 | 07 |
| 2 | 49 |
| 3 | 43 |
| 0 | 01 |

### 15.4 Every positive power of 76 ends in 76

1. Start with `76^1 = 76`.
2. Calculate `76 × 76 = 5776`.
3. Keep the final two digits: 76.
4. The same multiplication of endings occurs at the next step.

Therefore `76^n` ends in **76** for every positive integer `n`.

**Example:** `76^56` ends in **76**. This fact is useful when a large expression can be rewritten as a power of something ending in 76.

## 16. Worked examples with last two digits

**Before you start:** Two-digit patterns and the rule (a^m)^n = a^(m × n).

**Goal:** Choose a cycle or rewrite a power to reach a stable ending.

### Example 13 - Last two digits of 7 to the power 511

**Problem:** Find the last two digits of `7^511`.

1. Use the cycle `07,49,43,01`, of length 4.
2. Divide the exponent:

   `511 = 4 × 127 + 3`

3. Remainder 3 selects the third entry, 43.

> **Answer: 43.**

### Example 14 - Last two digits of 2 to the power 1120

**Problem:** Find the last two digits of `2^1120`.

We will build a useful smaller power, then reuse it.

**Step 1 - Start with a familiar power.**

`2^10 = 1024`

Its last two digits are 24.

**Step 2 - Square to obtain exponent 20.**

`2^20 = (2^10)^2`

For the final two digits, square 24:

`24^2 = 576`

So `2^20` ends in 76.

**Step 3 - Rewrite the original exponent.**

`1120 = 20 × 56`

Using `(a^m)^n = a^(m × n)`:

`2^1120 = (2^20)^56`

**Step 4 - Replace the inner base by its ending.**

The last two digits are the same as those of `76^56`.

**Step 5 - Apply the stable ending.**

Every positive power of 76 ends in 76.

> **Answer: 76.** We did not assume that the units-digit cycle for 2 also works for two digits.

### Example 15 - Last two digits of 6 to the power 112

**Problem:** Find the last two digits of `6^112`.

**Step 1 - Recall the starting point of the cycle.**

At exponents `2,3,4,5,6`, the endings are `36,16,96,76,56`.

**Step 2 - Compare the exponent with the starting exponent 2.**

`112-2 = 110 = 5 × 22`

So exponent 112 is exactly 22 full cycles after exponent 2.

**Step 3 - Use the ending at exponent 2.**

`6^2 = 36`

> **Answer: 36.** Equivalently, `112 mod 5 = 2`, and the mapping for powers of 6 assigns remainder 2 to 36.

## 17. Choosing the right method

**Before you start:** The methods in Sections 3 through 16.

**Goal:** Identify what to track before beginning a calculation.

| What the question asks | First action | Next action |
|---|---|---|
| Units digit of `a^n` | Keep last digit of `a` | Use its cycle and the exponent remainder |
| Base digit from a known output digit | Find the exponent's cycle position | Test possible base digits |
| Units digit of a sum or product | Find each term's last digit | Preserve operations and brackets |
| Units digit of a positive difference | Find both endings | Subtract; if negative, add 10 |
| Units digit of a power tower | Inspect the outer base's cycle | Find only the needed exponent information |
| Last two digits | Keep the base's last two digits | Build a two-digit pattern or a useful power |

### Common mistakes to avoid

| Mistake | Why it fails | Better habit |
|---|---|---|
| Replacing the exponent by its units digit | Cycle positions depend on the cycle length | Take the remainder modulo that length |
| Treating remainder 0 as exponent 0 | Cycle tables start at exponent 1 | Choose the final entry of the cycle |
| Writing `7^(4 × k + 1) = 7` | Only the last digits match | Write `U(7^(4 × k + 1)) = 7` |
| Treating every even exponent the same | A four-entry cycle distinguishes remainders 0 and 2 | Calculate modulo 4 when required |
| Reading `a^(b^c)` as `(a^b)^c` | These are different expressions | Put brackets around the whole exponent |
| Reusing a one-digit cycle for two digits | More information is needed | Build the cycle modulo 100 |
| Starting the cycle of `6^n` at exponent 1 | Its two-digit cycle starts at exponent 2 | Record the starting exponent explicitly |
| Calculating the complete huge power | It creates unnecessary work | Track only the required ending |

## 18. Practice problems

**Before you start:** Complete Sections 2 through 16 first.

**Goal:** Attempt each problem with the answer covered; write your cycle and remainder.

These are **additional practice**, not extra questions claimed to be in the PDF. Try them before reading the solutions.

| No. | Problem | Skill |
|---:|---|---|
| 1 | Find `U(146^85)`. | Fixed final digit |
| 2 | Find `U(234^17)`. | Two-entry cycle |
| 3 | Find `U(128^23)`. | Four-entry cycle |
| 4 | Find `U(73^40)`. | Remainder 0 |
| 5 | If `n^2023` ends in 3, find the last digit of `n`. | Reverse reasoning |
| 6 | Find `U(12^15 + 19^20)`. | Sum |
| 7 | Find `U(23^14 × 17^9)`. | Product |
| 8 | Find `U(7^10-3^9)`. | Difference |
| 9 | Find `U(4^(3^5))`. | Tower and parity |
| 10 | Find `U(2^(6^3))`. | Tower modulo 4 |
| 11 | Find the last two digits of `7^2025`. | Two-digit cycle |
| 12 | Find the last two digits of `6^26`. | Cycle starting at exponent 2 |
| 13 | Find the last two digits of `5^17`. | Stable ending |
| 14 | Find the last two digits of `176^23`. | Reducing the base modulo 100 |
| 15 | Find the last two digits of `2^60`. | Reusing `2^20` |

## 19. Practice solutions

**Before you start:** An attempt at the corresponding question in Section 18.

**Goal:** Compare your reasoning step by step, not just your final answer.

### Problem 1

1. The base 146 ends in 6.
2. Every positive power of a number ending in 6 ends in 6.

**Answer: 6.**

### Problem 2

1. The base 234 ends in 4.
2. The cycle is `4,6`.
3. Exponent 17 is odd, so select 4.

**Answer: 4.**

### Problem 3

1. Use the cycle for 8: `8,4,2,6`.
2. `23 = 4 × 5 + 3`.
3. Choose the third entry.

**Answer: 2.**

### Problem 4

1. Use the cycle for 3: `3,9,7,1`.
2. `40 mod 4 = 0`.
3. Choose the fourth entry.

**Answer: 1.**

### Problem 5

1. Exponent 2023 is odd and leaves remainder 3 modulo 4.
2. An even base cannot produce an odd last digit.
3. For base digits 1, 3, 5, 7, 9, the output digits are respectively 1, 7, 5, 3, 9.
4. Only base digit 7 produces 3.

**Answer: 7.**

### Problem 6

1. `15 mod 4 = 3`, so `12^15` ends in 8.
2. The even power `19^20` ends in 1.
3. `8 + 1 = 9`.

**Answer: 9.**

### Problem 7

1. `14 mod 4 = 2`, so `23^14` ends in 9.
2. `9 mod 4 = 1`, so `17^9` ends in 7.
3. `9 × 7 = 63`.

**Answer: 3.**

### Problem 8

1. `10 mod 4 = 2`, so `7^10` ends in 9.
2. `9 mod 4 = 1`, so `3^9` ends in 3.
3. `9-3 = 6`.

**Answer: 6.**

### Problem 9

1. The exponent `3^5` is odd.
2. An odd power of 4 ends in 4.

**Answer: 4.**

### Problem 10

1. The outer base 2 has cycle length 4.
2. The exponent is `6^3 = 216`, divisible by 4.
3. Choose the fourth entry of `2,4,8,6`.

**Answer: 6.**

### Problem 11

1. `2025 = 4 × 506 + 1`.
2. Choose the first entry of `07,49,43,01`.

**Answer: 07.** Keep two digits in a two-digit answer.

### Problem 12

1. The cycle for powers of 6 begins at exponent 2.
2. Exponent 26 is `26-6 = 20 = 5 × 4` positions after exponent 6.
3. Therefore it has the same ending as `6^6`, which is 56.

**Answer: 56.** It is not 06 even though `26 mod 5 = 1`.

### Problem 13

1. `5^2` ends in 25.
2. Multiplying an ending of 25 by 5 gives 125, ending in 25 again.
3. Every exponent at least 2 therefore gives 25.

**Answer: 25.**

### Problem 14

1. Keep the last two digits of 176: 76.
2. `76 × 76` ends in 76.
3. Every positive power keeps this ending.

**Answer: 76.**

### Problem 15

1. `60 = 20 × 3`, so `2^60 = (2^20)^3`.
2. `2^20` ends in 76.
3. Every positive power of 76 ends in 76.

**Answer: 76.**

## 20. Source coverage and corrections

### Coverage of the uploaded lesson

Page numbers below refer to the 54 pages of **NT1.pdf**.

| PDF pages | Mathematical content | Location in this note |
|---|---|---|
| 3-8 | Base reduction, units-digit tables, cycle positions, small examples | Sections 3-7 |
| 10-14 | `2^50` and `1989^1989` | Examples 1-2 |
| 17-20 | `3^28` and reverse problem for `n^2023` | Example 3 and Section 8 |
| 22-25 | Sum of powers and product of brackets | Examples 5-6 |
| 28-31 | Product of powers and sum of products | Examples 7-8 |
| 32-33 | `5^2018-3^2018` | Example 9 |
| 35-39 | Towers with bases 6 and 4; handwritten extensions | Examples 10-11 and Section 13 extensions |
| 41-42 | Tower with base 2019 | Example 12 |
| 43 | Last-two-digit rules | Sections 14-15 |
| 45-48 | `7^511` and `2^1120` | Examples 13-14 |
| 50-51 | `6^112` | Example 15 |

Title pages, repeated prompts, break slides, and closing slides have been omitted. The assignment mentioned on page 53 is not included in the uploaded PDF; the practice in Section 18 is newly added.

### Corrections and clarifications

1. **Page 20 contains an unrelated solution paragraph.** Its selected answer 8 for the reverse problem is correct, but the printed explanation repeats the earlier `3^28` solution. Section 8 supplies the correct reasoning.
2. **Page 23 uses equality where only a units-digit statement is intended.** In general, `7^(4 × k + 1)` is not equal to 7. The correct statement is `U(7^(4 × k + 1)) = 7`.
3. **Page 43 needs clear starting positions.** The two-digit cycle `07,49,43,01` for powers of 7 starts at exponent 1; the cycle `36,16,96,76,56` for powers of 6 starts at exponent 2.
4. **Positive-exponent conditions matter.** The fixed units digits and stable ending of 76 apply to positive exponents; exponent 0 must be treated separately. Powers of 5 have ending 25 only from exponent 2 onward.

The explanations, prerequisite examples, and additional practice have been expanded for self-study. The original lesson's mathematical scope is units digits and last two digits; it does not cover all of number theory.
