# Number Theory 01 - Visual Learning Notes

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

**Goal:** find the last digit or last two digits of a huge number without calculating the whole number.

```text
LOOK at a small example
          |
          v
NOTICE what repeats
          |
          v
FOLLOW a worked problem
          |
          v
TRY a similar problem yourself
```

The diagrams use plain text inside fenced blocks to keep their alignment. Read `^` as “raised to the power” and `*` as “multiplied by.” These are mathematical notes, not programming code.

| Symbol | Meaning | Example |
|---|---|---|
| `a^n` | Multiply n copies of a | `2^3 = 2 * 2 * 2` |
| `U(number)` | Units digit | `U(347) = 7` |
| `mod` | Remainder after division | `14 mod 4 = 2` |
| `>=` | At least | `n >= 2` |

**Scope:** the units-digit and last-two-digit lesson in NT1.pdf. Positive integer bases and exponents are used unless stated otherwise.

## 2. Prerequisites

Study one small idea at a time. You can return here when a later example needs it.

- [2.1 Numbers and place value](#21-numbers-and-place-value)
- [2.2 Variables and equality](#22-variables-and-equality)
- [2.3 Powers and exponents](#23-powers-and-exponents)
- [2.4 Rules for combining powers](#24-rules-for-combining-powers)
- [2.5 Division and remainders](#25-division-and-remainders)
- [2.6 Factors, multiples, and even or odd](#26-factors-multiples-and-even-or-odd)
- [2.7 Why two factors of 2 matter](#27-why-two-factors-of-2-matter)
- [2.8 Brackets and expansion](#28-brackets-and-expansion)
- [2.9 Remainders as endings](#29-remainders-as-endings)
- [2.10 Finding the remainder modulo 4](#210-finding-the-remainder-modulo-4)
- [2.11 Cycle position and starting point](#211-cycle-position-and-starting-point)

### 2.1 Numbers and place value

A **digit** is one symbol from 0 to 9. The number 347 contains three digits.

```text
                3       4       7
                |       |       |
                v       v       v
Place:       hundreds   tens   units
Value:          300      40      7

347 = 300 + 40 + 7
```

**Step 1:** look at the rightmost digit: 7. That is the units digit.

**Step 2:** look at the rightmost pair: 47. Those are the last two digits.

```text
1207 = [12][07]
           ^^ last TWO digits: 07
            ^ last ONE digit:   7
```

### 2.2 Variables and equality

A letter is a placeholder for a number. Positive integers are 1, 2, 3, and so on; integers also include zero and negative whole numbers.

```text
a^n       choose a = 3 and n = 4       3^4

18 and 28 have the same last digit.
          BUT 18 is not equal to 28.

U(18) = U(28) = 8     <-- only the endings match
```

An equals sign means the values on both sides are equal. Always distinguish a number from its ending.

### 2.3 Powers and exponents

```text
           3^4
           | |
           | +---- exponent: how many copies?
           +------ base: which number?

3^4 = [3] * [3] * [3] * [3] = 81
       1     2     3     4     copies
```

`3^4` is not `3 * 4`. One is 81; the other is 12.

For a nonzero base, exponent zero gives 1: `7^0 = 1`. The cycles below start at exponent **1**, so handle exponent zero separately. We do not use `0^0` in this lesson.

### 2.4 Rules for combining powers

**Rule A: same base, multiplied powers - add exponents.**

```text
2^3 * 2^2
   |     |
   v     v
(2*2*2)*(2*2) = five copies of 2 = 2^5

a^m * a^n = a^(m+n)
```

**Rule B: a power raised to a power - multiply exponents.**

```text
(2^3)^2 = (2*2*2) * (2*2*2)
          3 copies   3 copies
                    |
                    v
              3 * 2 = 6 copies

(a^m)^n = a^(m*n)
```

**Rule C: a product raised to a power - each factor repeats.**

```text
(2*3)^2 = (2*3)*(2*3)
        = (2*2)*(3*3)
        = 2^2 * 3^2

(a*b)^n = a^n * b^n
```

### 2.5 Division and remainders

**Question:** how many complete groups of four fit into 14?

```text
14 objects:

[o o o o] [o o o o] [o o o o]   o o
 group 1   group 2   group 3    left over

14 = 4 * 3 + 2
     |   |   |
     |   |   +---- remainder
     |   +-------- complete groups (quotient)
     +------------ group size (divisor)
```

14 is the **dividend**, the number being divided. Thus `14 mod 4 = 2`.

The remainder must be smaller than the divisor. If four objects remained, we could form another complete group.

```text
number = divisor * quotient + remainder

For a positive divisor:
0 <= remainder < divisor
```

### 2.6 Factors, multiples, and even or odd

```text
24 = 4 * 6
     |   |
     +---+---- factors of 24

24 is a multiple of 4.
24 / 4 leaves NO remainder: divisible by 4.
```

```text
Even: [o o] [o o] [o o]       6 = 2*3
Odd:  [o o] [o o] [o o] o     7 = 2*3 + 1
                        ^ one left over
```

**Step 1:** an even number divides into pairs with nothing left over.

**Step 2:** an odd number leaves one object unpaired.

**Step 3:** a positive power of an even number is even. A positive power of an odd number is odd.

| Product | Small example | Result type |
|---|---|---|
| Even * integer | `6 * 3 = 18` | Even |
| Odd * odd | `3 * 5 = 15` | Odd |

### 2.7 Why two factors of 2 matter

```text
One factor of 2:    6 = 2 * 3       even
Two factors of 2:  12 = 2 * 2 * 3
                       \___/
                         4         divisible by 4
```

For `2022^2`, each copy of 2022 contributes a factor of 2:

```text
2022 * 2022 = (2*1011) * (2*1011)
            = (2*2) * (1011*1011)
            = 4 * (1011*1011)
```

Therefore any power `2022^n` with `n >= 2` is divisible by 4. Being even alone would not prove this: 6 is even but not divisible by 4.

### 2.8 Brackets and expansion

First work inside brackets, then perform the remaining operations. Powers come before multiplication; multiplication comes before addition and subtraction.

```text
(4+1) * (6+9)
  |       |
  v       v
  5   *  15 = 75
```

To expand two sums, multiply each term in the first by each term in the second:

```text
(20+3) * (10+7)

             10       7
          +--------+--------+
       20 |  200   |  140   |
          +--------+--------+
        3 |   30   |   21   |
          +--------+--------+

Add all four cells: 200 + 140 + 30 + 21 = 391
```

A square of a sum uses the same four products:

`(100+23)^2 = 10000 + 2300 + 2300 + 529`.

### 2.9 Remainders as endings

```text
347 = 10 * 34 + 7       keep one digit:   7
347 = 100 * 3 + 47      keep two digits: 47
```

Numbers with the same remainder modulo 10 have the same last digit. Numbers with the same remainder modulo 100 have the same last two digits.

**Example:** 123 and 23 differ by 100, so their two-digit endings match.

### 2.10 Finding the remainder modulo 4

The last two digits are enough because 100 is divisible by 4.

```text
2341 = [2300] + [41]
         |       |
         |       +---- 41 = 4*10 + 1
         +------------ 2300 = 4*575

No remainder from 2300.
Remainder 1 from 41.
So 2341 mod 4 = 1.
```

The last digit alone is not enough: 12 and 22 end in 2, but their remainders modulo 4 are 0 and 2.

### 2.11 Cycle position and starting point

```text
Position:   1   2   3   4  |  5   6   7   8
Digit:      2   4   8   6  |  2   4   8   6
           first group     second group
```

A **cycle** is a repeating list. Its **length** is the number of positions in one complete repeat. Here the length is 4.

Always note where a cycle starts. Some two-digit patterns start repeating only after the first exponent.

**Try first:** what are the digits at positions 6 and 8?

**Check:** 4 and 6. They occupy the second and fourth positions of the second group.

## 3. Why only the last digit matters

**You need:** place value and bracket expansion.

**Question:** can we find the last digit of `23 * 17` using only 3 and 7?

```text
23 * 17 = (20+3) * (10+7)
        = 200 + 140 + 30 + 21
          \___________/    |
           all end in 0    last digit 1
```

1. All products involving a tens part end in 0.
2. The product of the units digits is `3 * 7 = 21`.
3. Therefore the complete answer ends in 1.

A power repeats this multiplication rule:

```text
173^37   --same final digit-->   3^37
 ^                              ^
keep only the base's final digit
```

**Important:** keep the original exponent. Only the ending is preserved; the two full numbers are not equal.

## 4. Discovering a repeating cycle

**You need:** powers and the last-digit multiplication rule.

Build the cycle for 2 one multiplication at a time:

```text
Start: 2

2 * 2 =  4   keep 4
4 * 2 =  8   keep 8
8 * 2 = 16   keep 6
6 * 2 = 12   keep 2  <-- back to the start

      +-----------------------+
      |                       |
      v                       |
     [2] --> [4] --> [8] --> [6]
```

**Why repeat?** Once the current last digit returns to 2, multiplying by the same number produces the same next digits again.

For 3, use the same process:

```text
3*3 = 9    9*3 = 27    7*3 = 21    1*3 = 3
keep 9     keep 7      keep 1      restart

Cycle: [3] [9] [7] [1]
```

**Try:** what is the fifth cycle entry for 3?

**Check:** 3, because the first four entries complete one group.

## 5. Complete units-digit cycle table

**You need:** cycle length and position.

| Base ending | Cycle from exponent 1 | Length |
|---:|---|---:|
| 0 | 0 | 1 |
| 1 | 1 | 1 |
| 2 | 2, 4, 8, 6 | 4 |
| 3 | 3, 9, 7, 1 | 4 |
| 4 | 4, 6 | 2 |
| 5 | 5 | 1 |
| 6 | 6 | 1 |
| 7 | 7, 9, 3, 1 | 4 |
| 8 | 8, 4, 2, 6 | 4 |
| 9 | 9, 1 | 2 |

### See why the cycles work

```text
Fixed endings:
0*0 = 0     1*1 = 1     5*5 = 25     6*6 = 36
keep 0      keep 1      keep 5       keep 6

Two positions:
4*4 = 16 -> keep 6 -> 6*4 = 24 -> keep 4
9*9 = 81 -> keep 1 -> 1*9 =  9 -> keep 9

Four positions:
7*7 = 49 -> 9*7 = 63 -> 3*7 = 21 -> 1*7 = 7
keep 9      keep 3      keep 1      restart

8*8 = 64 -> 4*8 = 32 -> 2*8 = 16 -> 6*8 = 48
keep 4      keep 2      keep 6      restart
```

### Two small applications

**Find the units digit of `271^127`.**

1. 271 ends in 1.
2. The cycle contains only 1.
3. Any positive exponent selects 1.

**Answer: 1.**

**Find the units digit of `239^729`.**

```text
239 ends in 9.
Cycle positions:  odd  even  odd  even
Digits:            9     1    9     1
729 is odd -> choose 9.
```

**Answer: 9.**

## 6. Using remainders to choose a cycle position

**You need:** complete groups and remainders.

For `2^50`, one group has four positions:

```text
50 = 4 * 12 + 2
     \____/   |
       48     2 more positions

12 full groups finish at exponent 48.

Exponent:    49     50     51     52
Position:     1      2      3      4
Last digit:   2     [4]     8      6
                    ^
                stop here
```

**Answer: 4.** We used the second entry after 12 complete groups.

### What if the remainder is zero?

```text
Exponent:  1  2  3  4 | 5  6  7  8
Digit:     2  4  8  6 | 2  4  8 [6]
                                ^
                     end of a complete group

8 = 4*2 + 0 -> choose position 4, NOT position 0
```

| Exponent remainder | Cycle position |
|---:|---|
| 1 | First |
| 2 | Second |
| 3 | Third |
| 0 | Fourth |

The forms `4*k+1`, `4*k+2`, and `4*k+3` mean k complete groups plus 1, 2, or 3 positions. A positive exponent of the form `4*k` ends a complete group.

**Try:** find the last digit of `2^23`.

**Check:** `23 = 4*5+3`; third entry of 2, 4, 8, 6 gives **8**.

## 7. Worked examples with a single power

**You need:** base ending, cycle, and remainder.

### Example 1 - Find the last digit of 2^50

```text
Base digit       2
Cycle            [2] [4] [8] [6]
Exponent         50 = 4*12 + 2
Position         second
Answer           4
```

The two leftover positions select the second digit, as shown in Section 6.

### Example 2 - Find the last digit of 1989^1989

1. Keep the base ending: 9.
2. Use the cycle 9, 1.
3. The exponent 1989 is odd.

```text
9^odd  -> 9
9^even -> 1
```

**Answer: 9.**

### Example 3 - Find the last digit of 3^28

```text
28 = 4*7 + 0       seven complete groups

Cycle: [3] [9] [7] [1]
                    ^ end of the group
```

Remainder zero selects the fourth entry. **Answer: 1.**

### Example 4 - Find the last digit of 2357^2341

1. Keep the base ending: 7.
2. Its cycle is 7, 9, 3, 1.
3. `2341 = 4*585+1`: one position remains.

```text
Cycle: [7] [9] [3] [1]
        ^ first position
```

**Answer: 7.**

### More small lesson examples

| Power | Exponent grouping | Ending |
|---|---|---:|
| `2^10` | `10 = 4*2+2`: second entry | 4 |
| `2^13` | `13 = 4*3+1`: first entry | 2 |
| `2^36` | `36 = 4*9`: fourth entry | 6 |
| `173^37` | Use 3; `37 = 4*9+1` | 3 |

## 8. Working backwards to find the base digit

**Problem:** `n^2023` ends in 2. What is the last digit of n?

**Step 1 - Find the cycle position.**

`2023 = 4*505+3`: third position for four-entry cycles. The exponent is also odd.

**Step 2 - Remove odd base digits.**

An odd base raised to a positive exponent remains odd. Our answer ends in an even digit, so the base is even.

```text
Possible base endings: 0, 2, 4, 6, 8
                         |
                         v
                    test each one
```

| Base ending | Ending after exponent 2023 | Keep it? |
|---:|---:|---|
| 0 | 0: fixed | No |
| 2 | 8: third cycle entry | No |
| 4 | 4: odd exponent | No |
| 6 | 6: fixed | No |
| 8 | 2: third cycle entry | Yes |

**Answer: n ends in 8.** This does not determine the complete number: 8, 18, 28, and so on all work.

## 9. Adding and multiplying last digits

**You need:** place value and operation order.

### Addition: keep the final digit after carrying

```text
47 + 38
 |    |
 v    v
 7 +  8 = 15
           |
           +--> keep 5; the 1 is carried to tens

Check: 47 + 38 = 85 -> last digit 5
```

### Multiplication: multiply the endings

```text
23 * 17
 |    |
 v    v
 3 *  7 = 21 -> keep 1

Check: 23 * 17 = 391 -> last digit 1
```

**Method:** find each power's ending, preserve brackets and operations, then keep the final digit after combining.

## 10. Worked examples with sums and products

### Example 5 - Find the last digit of 17^1989 + 89^2009

```text
17^1989                         89^2009
   |                               |
base ends in 7                  base ends in 9
1989 = 4*497+1                 2009 is odd
   |                               |
   v                               v
   7               +               9
                   |
                   v
                  16 -> keep 6
```

**Answer: 6.** The first term uses position 1 of 7, 9, 3, 1; the second uses the odd position of 9, 1.

### Example 6 - Two brackets multiplied

Find the last digit of `(54^99 + 99^54) * (144^144 + 199^199)`.

**Step 1 - Find the four endings.**

| Power | Reason | Ending |
|---|---|---:|
| `54^99` | 4 to an odd power | 4 |
| `99^54` | 9 to an even power | 1 |
| `144^144` | 4 to an even power | 6 |
| `199^199` | 9 to an odd power | 9 |

**Step 2 - Follow the brackets.**

```text
(4 + 1) * (6 + 9)
   |         |
   v         v
   5    *   15
             |
           keep 5

   5 * 5 = 25 -> keep 5
```

**Answer: 5.**

### Example 7 - Find the last digit of 27^50 * 73^100

```text
27^50                        73^100
  |                             |
use 7                        use 3
50 = 4*12+2                 100 = 4*25
second entry                fourth entry
  |                             |
  9             *               1 = 9
```

**Answer: 9.**

### Example 8 - A sum of two products

Find the last digit of `162^162 * 197^150 + 543^540 * 698^638`.

**Step 1 - Reduce each power separately.**

| Power | Exponent grouping | Ending |
|---|---|---:|
| `162^162` | `162 = 4*40+2`; cycle for 2, entry 2 | 4 |
| `197^150` | `150 = 4*37+2`; cycle for 7, entry 2 | 9 |
| `543^540` | `540 = 4*135`; cycle for 3, entry 4 | 1 |
| `698^638` | `638 = 4*159+2`; cycle for 8, entry 2 | 4 |

**Step 2 - Multiply before adding.**

```text
4 * 9     +     1 * 4
  |               |
  v               v
 36       +       4 = 40 -> keep 0
```

**Answer: 0.**

## 11. Subtraction and borrowing

**You need:** column subtraction and remainders modulo 10.

```text
  25          units: 5 - 9 cannot stay nonnegative
- 19          borrow 1 ten: 15 - 9 = 6
----
   6

Using remainders: 5 - 9 = -4
                         |
                      add 10
                         v
                         6
```

### Example 9 - Find the last digit of 5^2018 - 3^2018

1. Every positive power of 5 ends in 5.
2. `2018 = 4*504+2`, so the cycle 3, 9, 7, 1 selects 9.
3. Subtract the endings and handle borrowing:

```text
5 - 9 = -4
-4 + 10 = 6
```

**Answer: 6.** The complete difference is positive. When subtracting two individual digits, one addition of 10 is enough if their difference is negative.

## 12. Power towers explained

**You need:** powers and careful bracket reading.

```text
2^(3^2)                    (2^3)^2
    |                        |
  first                    first
  3^2 = 9                  2^3 = 8
    |                        |
    v                        v
  2^9 = 512                8^2 = 64
```

These are different expressions. In `a^(b^c)`, the **whole exponent** is `b^c`.

### Ask only what the outer base needs

```text
Expression: a^E
              ^ E may itself be a huge power

Base ends in 0,1,5,6 -> Is E positive?
Base ends in 4,9     -> Is E odd or even?
Base ends in 2,3,7,8 -> What is E mod 4?
```

**Key idea:** you rarely need the exact value of E.

## 13. Worked examples with power towers

### Example 10 - Find the last digit of 6^(2022^2023)

```text
E = 2022^2023 -> positive

6^E -> fixed units digit 6
```

**Answer: 6.** A positive exponent is all this cycle needs.

### Example 11 - Find the last digit of 4^(2022^2023)

```text
E = 2022 * 2022 * ... * 2022
     even   even        even
                  |
                  v
                E is even

Powers of 4: odd -> 4; even -> 6
```

**Answer: 6.** We use the parity of the entire exponent.

### Example 12 - Find the last digit of 2019^(2019^2019)

1. The outer base ends in 9.
2. Set `E = 2019^2019`.
3. A product of odd factors is odd.

```text
E is odd -> 9^E -> last digit 9
```

**Answer: 9.**

### Handwritten extension - Outer base 3

Find the last digit of `3^(2022^2023)`.

**Step 1:** the cycle for 3 has four entries; find `E mod 4`.

**Step 2:** show that E contains a factor of 4.

```text
E = 2022^2023
  = (2*1011)^2023

Among its factors:
     2 * 2 * remaining factors
     \___/
       4

E is divisible by 4 -> remainder 0
```

**Step 3:** choose the fourth entry of 3, 9, 7, 1.

**Answer: 1.**

### Handwritten homework - Outer base 2

Find the last digit of `2^(2022^2023)`.

1. The exponent E is the same as above.
2. It leaves remainder 0 modulo 4.
3. Choose the fourth entry of 2, 4, 8, 6.

**Answer: 6.**

**Watch out:** “even” alone is not enough for a four-entry cycle. Exponents 2 and 4 are both even, but `3^2` ends in 9 and `3^4` ends in 1.

## 14. Understanding the last two digits

**You need:** place value, remainders modulo 100, and bracket expansion.

```text
12347 = [123 hundreds] + [47]
        100 * 123       remainder

Keep the remainder modulo 100: 47
```

### Why keeping the base's last two digits works

```text
123^2 = (100+23)*(100+23)
      = 10000 + 2300 + 2300 + 529
        \________________/    |
         multiples of 100     529 = 500 + 29
                                         |
                                         v
                                        keep 29
```

Thus `123^2` and `23^2` have the same final two digits. Repeating this multiplication argument lets us reduce the base to its final two digits for any positive integer exponent.

**Important:** one digit does not contain enough information.

```text
 7^2 =  49 -> last two digits 49
17^2 = 289 -> last two digits 89
```

## 15. Discovering last-two-digit patterns

**You need:** keep the remainder modulo 100 after each multiplication.

### 15.1 Powers of 5

```text
Exponent:     1     2      3      4       5
Value:        5    25    125    625    3125
Ending:      05    25     25     25      25
```

1. Start at `5^2 = 25`.
2. Multiply the ending by 5: `25*5 = 125`.
3. Keep 25 again; each new multiplication repeats this.

**Rule:** `5^n` ends in 25 for `n >= 2`. For example, `5^2023` ends in **25**.

### 15.2 Powers of 6: notice the starting point

```text
Exponent 1: 06  <-- outside the repeating group

Exponent:    2     3     4     5     6  |  7
Ending:     36    16    96    76    56  | 36
           \________________________/   restart
                  five entries
```

Build each entry by multiplying the previous ending by 6:

```text
36*6 = 216 -> 16
16*6 =  96 -> 96
96*6 = 576 -> 76
76*6 = 456 -> 56
56*6 = 336 -> 36  <-- repeats
```

| Exponent remainder modulo 5 | Ending when exponent >= 2 |
|---:|---:|
| 0 | 76 |
| 1 | 56 |
| 2 | 36 |
| 3 | 16 |
| 4 | 96 |

Do not use 06 for exponent 6. The repeating part begins at exponent 2.

### 15.3 Powers of 7

```text
7^1:                     07
7^2:  7*7 =  49 -> keep 49
7^3: 49*7 = 343 -> keep 43
7^4: 43*7 = 301 -> keep 01
7^5:  1*7 =   7 -> keep 07  <-- restart

Cycle: [07] [49] [43] [01]
```

The cycle begins at exponent 1. Remainders 1, 2, 3, 0 modulo 4 select 07, 49, 43, 01 respectively.

### 15.4 Powers of 76

```text
Start: 76
       |
       v
76*76 = 5776 -> keep 76
                       |
                       +--> multiply by 76 again
                            same ending returns
```

**Rule:** every positive power of 76 ends in 76. In particular, `76^56` ends in **76**.

## 16. Worked examples with last two digits

### Example 13 - Last two digits of 7^511

```text
511 = 4*127 + 3

Cycle: [07] [49] [43] [01]
                  ^
              third entry
```

**Answer: 43.** There are three positions after the complete groups.

### Example 14 - Last two digits of 2^1120

**How do we choose a starting point?** We know `2^10 = 1024`. Squaring it gives a manageable ending and exponent 20, which divides 1120 exactly.

**Step 1 - Find the ending of 2^20.**

```text
2^10 = 1024 -> keep 24
                   |
                 square
                   v
2^20 ends like 24^2 = 576 -> keep 76
```

**Step 2 - Split the large exponent into groups of 20.**

```text
1120 = 20*56

2^1120 = (2^20)^56
             |
       inner base ends in 76
             v
Find the ending of 76^56
```

**Step 3 - Use the stable ending.** Every positive power of 76 ends in 76.

**Answer: 76.**

### Example 15 - Last two digits of 6^112

**Step 1:** start from exponent 2, where the ending is 36.

```text
Start exponent:     2
Target exponent:  112
Distance:         110 = 5*22
                         |
                   22 full cycles

Exponent 2                          Exponent 112
Ending 36  --22 complete cycles-->   Ending 36
```

**Step 2:** complete cycles return to the same ending.

**Answer: 36.**

## 17. Choosing the right method

```text
What do you need?
|
+-- ONE final digit
|   |
|   +-- One power -> base digit -> cycle -> position
|   +-- Sum/product -> each ending -> combine
|   +-- Difference -> endings -> subtract -> borrow
|   +-- Tower -> outer cycle -> information about E
|
+-- TWO final digits
    |
    +-- Keep base modulo 100
    +-- Build a two-digit cycle
    +-- Or rewrite using a useful power
```

### Five traps to check

1. **Remainder 0:** select the final cycle entry, not exponent zero.
2. **Tower brackets:** `a^(b^c)` differs from `(a^b)^c`.
3. **One digit versus two:** a units-digit cycle does not automatically solve a two-digit question.
4. **Starting point:** the two-digit cycle for powers of 6 begins at exponent 2.
5. **Equal endings:** they do not mean equal complete numbers.

**Explain aloud:** why can we avoid calculating the huge power? Because the required ending depends on a small repeating pattern or a small remainder.

## 18. Practice problems

Cover the solutions. For each problem, write the pattern, the needed exponent information, and the answer. The methods are deliberately not labelled.

| No. | Problem |
|---:|---|
| 1 | Find the units digit of `146^85`. |
| 2 | Find the units digit of `234^17`. |
| 3 | Find the units digit of `128^23`. |
| 4 | Find the units digit of `73^40`. |
| 5 | If `n^2023` ends in 3, find the last digit of n. |
| 6 | Find the units digit of `12^15 + 19^20`. |
| 7 | Find the units digit of `23^14 * 17^9`. |
| 8 | Find the units digit of `7^10 - 3^9`. |
| 9 | Find the units digit of `4^(3^5)`. |
| 10 | Find the units digit of `2^(6^3)`. |
| 11 | Find the last two digits of `7^2025`. |
| 12 | Find the last two digits of `6^26`. |
| 13 | Find the last two digits of `5^17`. |
| 14 | Find the last two digits of `176^23`. |
| 15 | Find the last two digits of `2^60`. |

These are additional practice questions; they are not presented as extra questions from the PDF.

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
