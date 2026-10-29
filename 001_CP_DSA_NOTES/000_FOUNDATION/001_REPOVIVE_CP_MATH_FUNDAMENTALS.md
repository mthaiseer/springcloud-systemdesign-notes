# 001 — CP MATH FUNDAMENTALS · Visual Self-Study Master Notes (C++17)

> **Visual Edition V4** · Learn from first principles · Tiny examples → observation → formula → dry run → C++

> **Purpose:** Learn the mathematics behind competitive-programming solutions, not just memorize formulas. Based on the **60-page *Repovive CP Maths*** handout, expanded with beginner-friendly prerequisites, small examples, derivations, dry runs, working C++17 functions, common mistakes and recognition cues.
>
> **Source vs expansion:** The **topic order and core subjects** follow the PDF. Worked proofs, extra examples, implementations and practice questions are **teaching additions**. Four topics marked **SUPPLEMENT** were only placeholders or incomplete in the original: binomial theorem (p.40), Stars and Bars (p.41), rotations (p.59), and Big-O (p.60). A few ambiguous/inaccurate handwritten statements are clarified rather than copied literally.

## How to use these visual notes

| Symbol | Meaning | What you should do |
|---|---|---|
| 🧱 | **Prerequisite** | Understand the small building block first |
| 🔎 | **Discover** | Look for a pattern before reading a formula |
| 🧮 | **Derivation** | Follow one mathematical step at a time |
| 🧪 | **Dry run** | Track variables or values in a table |
| 💻 | **C++17** | Understand code and verify constraints |
| ⚠️ | **Pitfall** | Check edge cases, overflow and assumptions |
| 🧠 | **Recognition** | Know when to use the technique in a new problem |

## How to learn from this note

1. Read **Prerequisites** even if they look easy: a one-line gap often blocks a harder problem.
2. Cover the next line of a **Discovery / Derivation** and predict it yourself.
3. Work through the **Dry run** with pencil; track variables horizontally.
4. Type the **C++** function without copying it, then test small and edge cases.
5. Use **🧠 Recognize it when...** during mixed Codeforces/LeetCode practice.
6. Attempt the short questions at the end; explanations above should be sufficient.

**C++ convention.** Snippets below are functions to add to this skeleton, unless shown as a complete program:

```cpp
#include <bits/stdc++.h>
using namespace std;
using ll = long long;

// Paste a function from the chapter here.
int main() {
    // Call it with your own test values.
}
```

### C++ number types — read this first

| Type | Typical range | When to use |
|---|---|---|
| `int` | about ±2.1 billion | indexes, counters, small values |
| `long long` | about ±9.22 × 10¹⁸ | sums, large products when safe |

**⚠️ Important:** `1LL * a * b` performs `long long` multiplication, **not unlimited-precision multiplication**.

**Overflow checklist**

1. Read the maximum input values.
2. Check intermediate expressions—not only the final result.
3. Divide before multiplying or cancel common factors where appropriate.
4. All examples use standard C++17 integral types.


## Contents

1. [Arithmetic, division, modulo and cycles](#1-arithmetic-division-modulo-and-cycles)
2. [Exponents, roots and binary exponentiation](#2-exponents-roots-and-binary-exponentiation)
3. [Logarithms, binary length and halving](#3-logarithms-binary-length-and-halving)
4. [Algebra, inequalities, equations and formulas](#4-algebra-inequalities-equations-and-formulas)
5. [Sequences and progressions](#5-sequences-and-progressions)
6. [Summations, telescoping, nested loops and prefix sums](#6-summations-telescoping-nested-loops-and-prefix-sums)
7. [Number bases and two's complement](#7-number-bases-and-twos-complement)
8. [Bitwise operations and subset masks](#8-bitwise-operations-and-subset-masks)
9. [Boolean logic and De Morgan's laws](#9-boolean-logic-and-de-morgans-laws)
10. [Sets and set operations](#10-sets-and-set-operations)
11. [Counting principles and inclusion–exclusion](#11-counting-principles-and-inclusionexclusion)
12. [Factorials, permutations and derangements](#12-factorials-permutations-and-derangements)
13. [Combinations, Pascal's triangle and repetition](#13-combinations-pascals-triangle-and-repetition)
14. [Elementary probability](#14-elementary-probability)
15. [Divisibility, factors and prime numbers](#15-divisibility-factors-and-prime-numbers)
16. [GCD, LCM and the Euclidean algorithm](#16-gcd-lcm-and-the-euclidean-algorithm)
17. [Modular arithmetic, inverse and fast power](#17-modular-arithmetic-inverse-and-fast-power)
18. [Coordinate and grid geometry](#18-coordinate-and-grid-geometry)
19. [Complexity and the final learning checklist](#19-complexity-and-the-final-learning-checklist)

---

## 1. Arithmetic, division, modulo and cycles

*Source: p.3.*

> 🧭 **Concept map:** `Division → quotient + remainder → ceil division → repeating cycles`
>
> 🎯 **Guiding question:** How can a repeated action be reduced to just the leftover part?

### 1.1 Prerequisites — quotient, remainder and integer types

Division answers two different questions:

- **How many full groups?** Quotient, `a / b` for nonnegative integers.
- **How many left over?** Remainder, `a % b`.

**🔎 Tiny example:** 17 candies, 5 candies per full bag:

```text
17 = 5 + 5 + 5 + 2
   = 5 × 3 + 2
       │       └─ remainder r = 2
       └───────── quotient  q = 3
```

This gives the fundamental identity **`a = b*q + r`**, with `0 <= r < b` when `a >= 0` and `b > 0`.

### 1.2 Floor and ceil division — derive the formulas

**Problem:** How many bags are needed to pack *all* 17 candies, allowing a partly filled bag?

1. `17 / 5 = 3` full bags.
2. `17 % 5 = 2` candies remain.
3. Remaining candies need one extra bag.
4. Answer `3 + 1 = 4 = ceil(17/5)`.

For nonnegative `a` and positive `b`:

- Floor: `floor(a/b) = a/b` using integer division.
- Ceil: `ceil(a/b) = a/b + (a%b != 0)`.
- Equivalent `ceil(a/b) = (a+b-1)/b` **only when** `a+b-1` fits the integer type.

| `a` | `b` | Full groups `a/b` | Remainder `a%b` | Total bags |
|---:|---:|---:|---:|---:|
| 15 | 5 | 3 | 0 | 3 |
| 17 | 5 | 3 | 2 | 4 |
| 1 | 5 | 0 | 1 | 1 |
| 0 | 5 | 0 | 0 | 0 |

```cpp
ll ceilDivNonnegative(ll items, ll capacity) {
    // Preconditions: items >= 0, capacity > 0
    return items / capacity + (items % capacity != 0);
}
```

**🧠 Recognize:** arranging students into rooms, pages to print, pizzas to buy, minimum moves of fixed size.

### 1.3 Remainder as a repeating cycle

The days of a week repeat every 7 days. Instead of moving 100 times, discard complete cycles.

**🔎 Example:** Today is Wednesday. What day is it after 100 days?

1. A complete 7-day cycle returns to Wednesday.
2. `100 = 7×14 + 2`.
3. Only `100 % 7 = 2` steps matter.
4. Wednesday → Thursday → **Friday**.

Use zero-based IDs: Monday=0, ..., Sunday=6. `next = (today + steps % 7) % 7`.

**🧪 Dry run:** `today=2`, `steps=100` → `100%7=2` → `(2+2)%7=4` → Friday.

```cpp
int dayAfter(int today, ll days) {  // today in [0,6], days >= 0
    return int((today + days % 7) % 7);
}
```

This idea applies to circular arrays, clocks, alternating turns and periodic patterns.

### 1.4 Last digit, parity, divisibility and digital root

- `n % 10` extracts the final decimal digit of nonnegative `n`.
- `n / 10` removes the final decimal digit.
- `n % 2 == 0` checks evenness; remainder 1 means odd for nonnegative `n`.
- `n % d == 0` means `d` divides `n` exactly (provided `d != 0`).

**Why does repeated digit sum use modulo 9?** Since `10 = 9+1`, we have `10 ≡ 1 (mod 9)`. Thus `10^k ≡ 1 (mod 9)` for any nonnegative integer `k`.

For `n = 9875`:

```text
9875 = 9×1000 + 8×100 + 7×10 + 5
     ≡ 9 + 8 + 7 + 5 = 29 (mod 9)
29 → 2+9=11 → 1+1=2
```

For `n > 0`: **digital root = `1 + (n-1)%9`**. Special case: digital root of 0 is 0.

```cpp
int digitalRoot(ll n) {  // n >= 0
    return n == 0 ? 0 : int(1 + (n - 1) % 9);
}
```

**⚠️ Mistakes to avoid:** C++ division of negative numbers truncates toward zero, not toward mathematical negative infinity; a negative `%` can be negative.

- The above ceil and modulo examples deliberately assume nonnegative inputs.

**✍️ Check yourself:** How many capacity-8 boxes for 65 items? How many days beyond Monday is 50 days?


[↑ Contents](#contents)

---

## 2. Exponents, roots and binary exponentiation

*Source: pp.4–6.*

> 🧭 **Concept map:** `Repeated multiplication → squares of powers → binary exponent → roots`
>
> 🎯 **Guiding question:** How can an enormous exponent be computed with only logarithmically many steps?

### 2.1 Prerequisites — powers mean repeated multiplication

`base^exponent` answers: how many times do we multiply the **base**?

`2^5 = 2×2×2×2×2 = 32`.

| Rule | Why | Example |
|---|---|---|
| `x^a · x^b = x^(a+b)` | combine repeated factors | `2^3·2^2=2^5` |
| `x^a / x^b = x^(a-b)` | cancel common factors | `2^5/2^2=2^3` |
| `(x^a)^b = x^(ab)` | repeat the whole power `b` times | `(2^3)^2=2^6` |
| `x^0 = 1` | identity of multiplication, for `x ≠ 0` | `7^0=1` |
| `x^(-a) = 1/x^a` | inverse of multiplication | `2^-3=1/8` |
| `x^(1/k)` | kth root (appropriate domain) | `8^(1/3)=2` |

**Root intuition:** `sqrt(16)=4` means `4×4=16`; cube root `cbrt(8)=2` means `2×2×2=8`.

### 2.2 Discovery — why the naive method is slow

Computing `3^13` directly takes 12 multiplications. Computing `3^1,000,000,000` that way is infeasible.

Observe how squaring creates large powers fast:

```text
3¹ = 3
  └─ square → 3² = 9
               └─ square → 3⁴ = 81
                            └─ square → 3⁸ = 6561

13 = 8 + 4 + 1
3¹³ = 3⁸ × 3⁴ × 3¹ = 6561 × 81 × 3
```

Every positive exponent is the sum of distinct powers of two (its **binary representation**). Multiply only the needed powers.

### 2.3 Derive binary exponentiation

Suppose the exponent is `e`:

1. If `e` is **even**, `base^e = (base×base)^(e/2)`.
2. If `e` is **odd**, save one `base` in the answer: `base^e = base × (base×base)^((e-1)/2)`.
3. Update `base = base×base` and `e = floor(e/2)`.
4. Repeat until `e=0`.

**🧪 Dry run — `3^13`:** (`answer` starts at 1)

| Before `e` | Before `base` | Odd? | New `answer` | Next `base` | Next `e` |
|---:|---:|---|---:|---:|---:|
| 13 | 3 | yes | 3 | 9 | 6 |
| 6 | 9 | no | 3 | 81 | 3 |
| 3 | 81 | yes | 243 | 6561 | 1 |
| 1 | 6561 | yes | **1,594,323** | — | 0 |

```cpp
ll binaryPower(ll base, ll exponent) { // exponent >= 0; result must fit ll
    ll answer = 1;
    while (exponent > 0) {
        if (exponent % 2 == 1) answer *= base;
        exponent /= 2;
        if (exponent > 0) base *= base;
    }
    return answer;
}
```

**⏱️ Complexity derivation:** `e → floor(e/2) → floor(e/4) → ... → 0`; after about `log2(e)+1` rounds, it is zero.

- **Time `O(log e)`, extra space `O(1)`**.
- Compare with naive `O(e)`.

**Integer-overflow warning:** `3^40` exceeds signed 64-bit. When a problem asks `base^e mod m`, use **modular fast power** in §17 instead.

### 2.4 Exact integer roots vs floating point

**Problem:** Floating-point roots are approximate, even when the true answer is an integer.

1. Use `sqrtl(n)` only to obtain a **candidate** root.
2. Adjust that candidate if rounding put it one step too high or low.
3. Check exactness with division and remainder, rather than risk `root * root` overflow.

**Goal:** Confirm `root² = n` using integer arithmetic.

```cpp
bool isPerfectSquare(ll n) {
    if (n < 0) return false;
    if (n == 0) return true;

    ll root = (ll)sqrtl((long double)n); // approximate root
    // root*root > n is equivalent to root > n/root (for root>0).
    while (root > 0 && root > n / root) --root;
    while (root + 1 <= n / (root + 1)) ++root;

    return root > 0 && n / root == root && n % root == 0;
}
```

**🧪 Dry run — exact perfect-square test**

| `n` | Candidate root | Check | Answer |
|---:|---:|---|---|
| 36 | 6 | `36/6=6`, `36%6=0` | Perfect square |
| 35 | 5 | `35/5=7` | Not a perfect square |

The adjustment loops handle small rounding errors. For 64-bit inputs the number of adjustments is tiny.

**🧠 Recognize it when...** the exponent is huge, operations repeatedly double, or an answer uses powers of two.

**✍️ Check yourself:** Explain how to compute `5^11` using `11 = 8+2+1`. Why does binary exponentiation take `O(log e)`?


[↑ Contents](#contents)

---

## 3. Logarithms, binary length and halving

*Source: pp.7–10.*

> 🧭 **Concept map:** `Powers → inverse question (log) → repeated halving → bit length`
>
> 🎯 **Guiding question:** How many times does a value double or halve?

### 3.1 Prerequisite — reverse the exponent question

An exponent asks, “What is `2^k`?” A logarithm asks the reverse: **“Which `k` makes `2^k = n`?”**

`2^3 = 8` ⇔ **`log₂(8)=3`**.

More generally, `log_b(x)=k` exactly when `b^k=x`, with `b>0`, `b≠1`, `x>0`.

```text
Multiply by 2:  1 → 2 → 4 → 8 → 16
Steps:           0   1   2   3    4
                                  log₂16=4
```

### 3.2 Derive the logarithm rules

Start with a single idea: **a logarithm records an exponent**.

#### Rule 1 — Multiplication becomes addition

1. Suppose `x=b^p` and `y=b^q`.
2. Multiply: `x×y = b^p × b^q = b^(p+q)`.
3. The exponent of the product is `p+q`.

```text
log_b(x × y) = log_b(x) + log_b(y)
```

#### Rule 2 — Division becomes subtraction

1. Divide: `x/y = b^p/b^q = b^(p-q)`.
2. The exponent is `p-q`.

```text
log_b(x/y) = log_b(x) - log_b(y)
```

#### Rule 3 — Power moves in front

1. `(b^p)^k = b^(p×k)`.
2. The exponent becomes `k` times larger.

```text
log_b(x^k) = k × log_b(x)
```

#### Rule 4 — Change the logarithm base

1. Write `b^k=x`.
2. Take a logarithm of both sides: `k×log(b)=log(x)`.
3. Divide by `log(b)`:

```text
log_b(x) = log(x) / log(b)
```

> 🧱 **Prerequisite:** `b>0`, `b≠1`, and log arguments must be positive.

| Formula | Small example |
|---|---|
| `log₂(8×4)=log₂8+log₂4` | `5=3+2` |
| `log₂(32/4)=log₂32-log₂4` | `3=5-2` |
| `log₂(8²)=2 log₂8` | `6=2×3` |

### 3.3 Where `O(log n)` comes from

**Problem:** How many times can we divide 100 by 2 before reaching 0?

| Iteration | `n` before | `n` after `n/2` |
|---:|---:|---:|
| 1 | 100 | 50 |
| 2 | 50 | 25 |
| 3 | 25 | 12 |
| 4 | 12 | 6 |
| 5 | 6 | 3 |
| 6 | 3 | 1 |
| 7 | 1 | 0 |

**Derive the number of steps**

1. Start with `n` items.
2. After 1 halving: approximately `n/2` remain.
3. After 2 halvings: approximately `n/4` remain.
4. After `k` halvings: approximately `n/2^k` remain.
5. To reach one item, solve `n/2^k ≈ 1`.

```text
n / 2^k ≈ 1
2^k ≈ n
k   ≈ log₂(n)
```

**🧠 Recognize:** Binary search, binary exponentiation and repeated division all use this pattern.

### 3.4 Bits needed, decimal digits and base conversion

A positive `n` needs `floor(log₂n)+1` binary bits. Example: `13 = (1101)₂`, so 4 bits.

- Positive `n`: binary bits `floor(log₂n)+1`.
- Positive `n`: decimal digits `floor(log₁₀n)+1`.
- `n=0`: by convention its written form contains one digit in either base.

Use integer loops instead of floating `log` when exactness matters at large powers of a base.

```cpp
int bitLength(unsigned long long n) {
    int bits = 0;
    do { ++bits; n /= 2; } while (n != 0);
    return bits;
}
int decimalDigits(ll n) {  // works for negative ll too
    int digits = 0;
    do { ++digits; n /= 10; } while (n != 0);
    return digits;
}
```

**🧪 Dry run — bit length of 13:** `13 → 6 → 3 → 1 → 0`, count **4**. (Do not confuse *number of bits* with *number of shifts until value becomes 1*.)

**🧠 Recognize:** binary search, divide-and-conquer, bits of an integer, digits of a number, repeated doubling or halving.


[↑ Contents](#contents)

---

## 4. Algebra, inequalities, equations and formulas

*Source: pp.11–13.*

> 🧭 **Concept map:** `Unknown variable → equation → transformation → verify`
>
> 🎯 **Guiding question:** How do you turn a word problem into a solvable equation?

### 4.1 Prerequisites — variable, expression, equation

- **Variable:** a symbol with an unknown value, e.g. `x`.
- **Expression:** `3*x+4` (no equality claim).
- **Equation:** `3*x+4=19` (two sides are equal).
- **Inequality:** `3*x+4>=19` (one side is at least the other).

**🧪 Solve `3x+4=19` — one operation at a time**

```text
3x + 4 = 19   Start
3x     = 15   Subtract 4 from BOTH sides
x      =  5   Divide BOTH sides by 3
```

**Verify:** `3×5+4=19` ✓. A valid transformation preserves equality.

### 4.2 Substitution, distribution and factoring

These three operations appear repeatedly in contest math:

| Tool | What you do | Tiny example |
|---|---|---|
| **Substitution** | Replace a variable by its value | `x=5 → 3x+2=17` |
| **Distribute** | Multiply every term inside brackets | `3(x+2)=3x+6` |
| **Factor** | Pull out a common multiplier | `6x+9=3(2x+3)` |

**Step-by-step factoring `n²+2n`:**

1. Both terms contain `n`.
2. Take `n` outside the parentheses.
3. The remaining terms are `n` and `2`.

```text
n² + 2n = n × n + n × 2 = n(n + 2)
```

**🧠 Recognize:** Look for repeated terms, common factors, or variables you can eliminate.

### 4.3 Inequality — remember to flip for negative multipliers

Solve `-2x < 6`:

1. Divide both sides by `-2`.
2. Dividing by a negative reverses the ordering.
3. Thus **`x > -3`**, not `x < -3`.

**🔎 Why?** Since `2<4`, multiplying by `-1` gives `-2 > -4`. Negative scaling reverses left and right on the number line.

### 4.4 Two equations: substitution and elimination

**Problem:** `x+y=10`, `x-y=4`. Find `x,y`.

**Substitution:**
1. From first equation: `y=10-x`.
2. Put into second: `x-(10-x)=4`.
3. Simplify: `2x-10=4`, then `2x=14`, so `x=7`.
4. Substitute: `y=10-7=3`.

**Elimination:** add both equations: `(x+y)+(x-y)=10+4` → `2x=14` → `x=7`, `y=3`.

**Dry-run check:** `7+3=10`, `7-3=4` → both original equations hold.

### 4.5 Quadratic equation and discriminant

For `ax²+bx+c=0`, `a≠0`:

`x = (-b ± sqrt(b²-4ac)) / (2a)`.

The expression **`D=b²-4ac`** tells the number of real roots:

- `D>0`: two distinct real roots.
- `D=0`: one repeated real root.
- `D<0`: no real roots.

**🔎 Example:** `x²-5x+6=0` → `D=25-24=1` → `x=(5±1)/2` → **2 and 3**. Verify `(x-2)(x-3)=0`.

### 4.6 Standard sums: derive rather than memorize

`1+2+...+n` can be paired from both ends:

```text
S = 1 + 2 + 3 + ... + (n-1) + n
S = n + (n-1) + ... + 2 + 1
--------------------------------
2S = (n+1) + (n+1) + ... n times
2S = n(n+1)     ⇒     S = n(n+1)/2
```

Other useful identities (proofs by patterns/induction are beyond this section; examples verify them):

- `1²+2²+...+n² = n(n+1)(2n+1)/6`.
- `1³+2³+...+n³ = [n(n+1)/2]²`.
- Number of unordered pairs among `n` distinct objects = `n(n-1)/2`.

**Overflow-safe implementation:**

```cpp
ll sumFirstN(ll n) { // assumes result fits long long
    ll a = n, b = n + 1;
    if (a % 2 == 0) a /= 2;
    else b /= 2;
    return a * b;
}
```

**🧠 Recognize:** two unknown quantities with two constraints, minimize number of boxes/moves, sum of first `n`, equations derived from rates or prices.

**✍️ Check yourself:** Solve `2x+y=11`, `x-y=1`. Find the sum 1 to 100.


[↑ Contents](#contents)

---

## 5. Sequences and progressions

*Source: pp.14–17.*

> 🧭 **Concept map:** `Fixed difference → AP | Fixed ratio → GP → nth term and sum`
>
> 🎯 **Guiding question:** Is the sequence growing by addition or by multiplication?

### 5.1 Prerequisites — sequence, position, difference, ratio

A **sequence** is an ordered list `a₁,a₂,...`. Its **nth term** describes the value at a position, not the number of items.

- Sequence `2,4,6,8,...`: `a₁=2`, `a₄=8`, and `aₙ=2n`.
- A **common difference** adds the same value each step.
- A **common ratio** multiplies by the same value each step.

### 5.2 Arithmetic progression (AP): repeated addition

Example `5,8,11,14,17,...`. Here `first=5`, `difference=3`.

```text
term 1: 5               = 5 + 0×3
term 2: 5+3             = 5 + 1×3
term 3: 5+3+3           = 5 + 2×3
term n: 5+(n-1) copies  = 5 + (n-1)×3
```

So **`aₙ = first + (n-1)*difference`**.

**🧪 Dry run:** fourth term: `5+(4-1)*3=14`.

#### Derive the AP sum visually

Pair terms from opposite ends:

```text
5    8   11   14   17
│    │    │    │    │
└───────────────┘  5 + 17 = 22
     └──────┘     8 + 14 = 22
          11       Middle term when n is odd
```

**Step 1:** Write the sequence forward and backward.

**Step 2:** Every matching pair sums to `first + last`.

**Step 3:** Double the total to count exactly `count` pairs:

```text
2 × sum = count × (first + last)
sum     = count × (first + last) / 2
```

**🧪 Dry run:** `5,8,11,14,17` → `count=5`, `first=5`, `last=17` → `sum=5×22/2=55`.

### 5.3 Geometric progression (GP): repeated multiplication

Example `3,6,12,24,48,...`: `first=3`, ratio `r=2`.

`a₁=3×2⁰`, `a₂=3×2¹`, `a₃=3×2²`; therefore **`aₙ=first×r^(n-1)`**.

#### Derive the GP sum by cancellation

For a geometric sequence starting at `a` with common ratio `r`:

```text
 S = a + ar + ar² + ... + arⁿ⁻¹
rS =     ar + ar² + ... + arⁿ⁻¹ + arⁿ
-----------------------------------------
rS - S = arⁿ - a
```

**Step 1:** Multiply the original sum by `r`.

**Step 2:** Subtract the two expressions. All middle terms cancel.

**Step 3:** Factor and solve for `S`:

```text
(r - 1) × S = a × (rⁿ - 1)
S = a × (rⁿ - 1) / (r - 1), for r != 1
```

If `r=1`, every term is `a`; sum is simply `n*a`.

**🧪 Dry run:** `1+2+4+8+16` → `a=1`, `r=2`, `n=5`; `S=(2⁵-1)/(2-1)=31`.

**Power-of-two connection:** levels of a binary tree can contain `1,2,4,...,2^(h-1)` nodes; total maximum is `2^h-1` for `h` levels.

### 5.4 C++ formula (valid range and overflow matter)

```cpp
ll apNth(ll first, ll difference, ll position) { // position >= 1
    // Preconditions: multiplication and addition both fit long long.
    return first + (position - 1) * difference;
}
ll apSum(ll first, ll difference, ll count) { // count >= 0
    // Preconditions: intermediate values and final sum fit long long.
    if (count == 0) return 0;
    if (count == 1) return first;

    ll last = apNth(first, difference, count);
    ll pairSum = first + last;
    if (count % 2 == 0) return (count / 2) * pairSum;
    return count * (pairSum / 2); // odd count -> pairSum is even
}
ll gpNth(ll first, ll ratio, ll position) { // position >= 1
    return first * binaryPower(ratio, position - 1); // §2 function
}
```

#### Why divide before multiplying?

**Start with the AP sum formula:**

```text
sum = count × (first + last) / 2
```

**Step 1 — Find an even factor.** At least one of `count` and `first + last` is even for an integer AP.

| Situation | Divide first | Then multiply |
|---|---|---|
| `count` is even | `count / 2` | `(count / 2) × (first + last)` |
| `count` is odd | `(first + last) / 2` | `count × ((first + last) / 2)` |

**Step 2 — Tiny dry run: AP = `5, 8, 11, 14, 17`.**

| Variable | Value |
|---|---:|
| `count` | 5 (odd) |
| `first` | 5 |
| `last` | 17 |
| `pairSum` | 22 |
| `pairSum / 2` | 11 |
| **Final sum** | **`5 × 11 = 55`** |

**Step 3 — Why this helps.** Dividing first avoids unnecessarily creating a large product.

> ⚠️ **Still check overflow:** `first + last`, `(position-1) * difference`, the addition in `apNth`, and the final result must each fit `long long`. The `gpNth` function uses `binaryPower` from Chapter 2.

**🧠 Recognize the pattern**

- Equal difference between consecutive numbers → **AP**.
- Equal ratio, repeated doubling or branching → **GP**.


[↑ Contents](#contents)

---

## 6. Summations, telescoping, nested loops and prefix sums

*Source: pp.15–20.*

> 🧭 **Concept map:** `Sigma → loop counts → cancellation → prefix sums`
>
> 🎯 **Guiding question:** How can repeated summation be turned into a formula or precomputation?

### 6.1 Prerequisites — sigma notation and array indexing

The sigma `Σ` is shorthand for **add the terms generated by an index**.

`Σ(i=1..5) i = 1+2+3+4+5 = 15`.

For `Σ(k=2..4) (2k+1)`, plug in `k=2,3,4`: `5+7+9=21`. The letter `i`/`k` is a placeholder, not a fixed value.

**Three useful properties:**

1. Constant factor: `Σ(c*aᵢ)=c*Σaᵢ`.
2. Sum splitting: `Σ(aᵢ+bᵢ)=Σaᵢ+Σbᵢ`.
3. Index shifting: `Σ(i=1..n) aᵢ = Σ(j=0..n-1) a_(j+1)`; this changes the label, not the values being added.

**Discovery:** `Σ(i=1..n)(3i+2) = 3Σi + 2Σ1 = 3n(n+1)/2 + 2n`.

### 6.2 Nested loops — translate iterations into a sum

Case A: each outer iteration runs the inner loop `n` times.

```cpp
for (int i = 0; i < n; ++i)
    for (int j = 0; j < n; ++j)
        ++counter;
```

Total work `n+n+...+n` (`n` times) = `n²`: **`O(n²)`**.

Case B: inner loop runs only up to the current outer index:

```cpp
for (int i = 1; i <= n; ++i)
    for (int j = 1; j <= i; ++j)
        ++counter;
```

**Dry run for `n=5`:**

| Outer `i` | Inner `j` values | Iterations |
|---:|---|---:|
| 1 | 1 | 1 |
| 2 | 1,2 | 2 |
| 3 | 1,2,3 | 3 |
| 4 | 1,2,3,4 | 4 |
| 5 | 1,2,3,4,5 | 5 |
| **Total** | | **15** |

`1+2+3+4+5 = 5×6/2`. For general `n`: `n(n+1)/2`, still **`O(n²)`**.

If inner loop runs `n-i+1` times instead, the order reverses (`n,n-1,...,1`), but the total is the same.

### 6.3 Telescoping sum — watch terms cancel

Consider `Σ(i=1..4) [i²-(i-1)²]`:

```text
(1²-0²) + (2²-1²) + (3²-2²) + (4²-3²)
         -1²   +1²  cancel
                    -2² +2² cancel
                             -3² +3² cancel
= 4² - 0² = 16
```

General pattern: `Σ(i=1..n)(aᵢ-a_(i-1)) = aₙ-a₀`.

**🧠 Recognize:** adjacent differences, consecutive fractions that decompose into differences, sequence endpoints.

### 6.4 Prefix sums — answer range queries quickly

**Problem:** Given `[1,2,3,4,5]`, calculate sum of values at indices `l=2` through `r=4` (zero-based).

Naive addition: `3+4+5=12`; repeating this for many queries is expensive.

Create `prefix[k] = sum of the first k elements`, with `prefix[0]=0`:

```text
index          0   1   2   3   4
array          1   2   3   4   5
prefix    0    1   3   6  10  15
          ^    ^   ^   ^   ^   ^
          0    1   2   3   4   5   (elements included)
```

To find `[l..r]`:

1. `prefix[r+1]` includes elements `0..r`.
2. `prefix[l]` includes elements `0..l-1`.
3. Subtract the unwanted left portion: **`sum(l,r)=prefix[r+1]-prefix[l]`**.

**🧪 Dry run:** `[2..4]` → `prefix[5]-prefix[2] = 15-3 = 12`.

```cpp
vector<ll> buildPrefix(const vector<int>& a) {
    vector<ll> prefix(a.size() + 1, 0);
    for (int i = 0; i < (int)a.size(); ++i)
        prefix[i + 1] = prefix[i] + a[i];
    return prefix;
}
ll rangeSum(const vector<ll>& prefix, int left, int right) {
    // Valid when 0 <= left <= right < original array length.
    return prefix[right + 1] - prefix[left];
}
```

**⏱️ Complexity:** build once in `O(n)`, answer each query in **`O(1)`**. For `q` queries: `O(n+q)` instead of potentially `O(nq)`.

### 6.5 When the common formulas help

| Sum | Formula | Example `n=3` |
|---|---|---|
| `1+...+n` | `n(n+1)/2` | 6 |
| `1²+...+n²` | `n(n+1)(2n+1)/6` | 14 |
| `1³+...+n³` | `[n(n+1)/2]²` | 36 |
| `1+2+4+...+2^(n-1)` | `2^n-1` | 7 |

**🧠 Recognize:** many interval-sum queries, repeated subarray totals, loop-count proofs, cancellation of adjacent terms.


[↑ Contents](#contents)

---

## 7. Number bases and two's complement

*Source: pp.21–23.*

> 🧭 **Concept map:** `Positional value → convert base → binary/hex → signed representation`
>
> 🎯 **Guiding question:** What does each digit mean in a different number base?

### 7.1 Prerequisites — place value

In decimal, `523 = 5×10² + 2×10¹ + 3×10⁰`. A base is simply the size of each place-value jump.

For binary (`base 2`), allowed digits are `0` and `1`:

`(1011)₂ = 1×2³ + 0×2² + 1×2¹ + 1×2⁰ = 8+0+2+1 = 11`.

Hexadecimal (`base 16`) uses `0–9,A–F` where `A=10`, ..., `F=15`:

`(2A)₁₆ = 2×16 + 10 = 42`.

### 7.2 Convert decimal to another base by repeated division

**Problem:** Convert `13` to binary.

1. Divide 13 by 2 → quotient 6, remainder 1 (last bit).
2. Divide 6 by 2 → quotient 3, remainder 0.
3. Divide 3 by 2 → quotient 1, remainder 1.
4. Divide 1 by 2 → quotient 0, remainder 1.
5. Read remainders **bottom to top**: `1101`.

| Before `n` | `n/2` | `n%2` |
|---:|---:|---:|
| 13 | 6 | 1 |
| 6 | 3 | 0 |
| 3 | 1 | 1 |
| 1 | 0 | 1 |

```cpp
string toBase(unsigned long long n, int base) { // 2 <= base <= 16
    const string digits = "0123456789ABCDEF";
    if (n == 0) return "0";
    string answer;
    while (n > 0) {
        answer.push_back(digits[n % base]);
        n /= base;
    }
    reverse(answer.begin(), answer.end());
    return answer;
}
```

**⏱️ Complexity:** repeated division decreases `n` by a factor of `base`: **`O(log_base n)`** steps.

### 7.3 Binary ↔ hexadecimal: group exactly 4 bits

Because `16=2⁴`, every hexadecimal digit represents 4 binary bits.

```text
binary  1101 0110
          D    6
hexadecimal D6
```

Converting `0x3F` to decimal: `3×16 + 15=63`. Prefix `0x` denotes hexadecimal in C++.

### 7.4 Binary addition and two's complement

Binary addition follows decimal carrying, except the base is 2:

- `0+0=0`
- `0+1=1`
- `1+1=10₂` (write 0, carry 1)
- `1+1+1=11₂` (write 1, carry 1)

**Signed negative numbers** are generally represented using *two's complement* in standard modern systems.

Example using 8 bits, represent `-5`:

```text
+5        = 00000101
invert    = 11111010
add 1     = 11111011  (-5 in 8-bit two's complement)
```

`+5 + (-5)` gives `1 00000000`; discard the carry beyond 8 bits → `00000000`.

> ⚠️ **C++ safety:** The 8-bit two's-complement diagram is for understanding representation, **not** a recipe for implementing negatives. Use signed arithmetic for negative values and unsigned types for bit masking. Never assume signed overflow wraps around.

**🧠 Recognize:** bitwise tasks, binary representation, extracting digits in a base, hexadecimal color or address values.


[↑ Contents](#contents)

---

## 8. Bitwise operations and subset masks

*Source: pp.24–27, with related examples on p.26.*

> 🧭 **Concept map:** `Bits → masks → bit tricks → XOR → subset enumeration`
>
> 🎯 **Guiding question:** Can an entire state or subset be encoded in a few bits?

### 8.1 Prerequisites — a number is a sequence of bits

Take `10 = 1010₂` and `12 = 1100₂`. Apply an operation **independently to each bit position**.

```text
A = 1010  (10)
B = 1100  (12)
----------------
&   1000  (8)   both bits must be 1
|   1110  (14)  at least one bit is 1
^   0110  (6)   exactly one bit is 1
```

| `a` | `b` | `a&b` | `a\|b` | `a^b` |
|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 |
| 0 | 1 | 0 | 1 | 1 |
| 1 | 0 | 0 | 1 | 1 |
| 1 | 1 | 1 | 1 | 0 |

### 8.2 Shifts — move the bits left/right

For a nonnegative unsigned value, `x << k` multiplies by `2^k` **if it fits in the type**. `x >> k` divides by `2^k` (integer floor).

Example: `5 = 0101₂`; `5 << 2 = 10100₂ = 20`, `5 >> 1 = 0010₂ = 2`.

### 8.3 Five standard bit checks (derive one by one)

To access **bit `k`** (index 0 is rightmost), build a mask `1ULL << k`. Example for `n=10=1010₂`, `k=1`: mask `0010₂`.

1. **Check:** `(n & mask) != 0`. For `1010 & 0010 = 0010`, so bit 1 is set.
2. **Set:** `n | mask`. OR with 1 forces this bit to 1.
3. **Clear:** `n & ~mask`. Invert the mask, then AND to remove just this bit.
4. **Toggle:** `n ^ mask`. XOR with 1 flips just that bit.
5. **Check even/odd:** `n&1` checks the rightmost bit.

```cpp
using ull = unsigned long long;
bool isBitSet(ull n, int k) { return (n & (1ULL << k)) != 0; }
ull setBit(ull n, int k)   { return n | (1ULL << k); }
ull clearBit(ull n, int k) { return n & ~(1ULL << k); }
ull toggleBit(ull n, int k){ return n ^ (1ULL << k); }
```

**Precondition:** `0 <= k < 64`; otherwise the shift is invalid/undefined.

### 8.4 The `n&(n-1)` observation

Take `n=12 = 1100₂`. Then `n-1 = 1011₂`:

```text
 n   = 1100
 n-1 = 1011
 AND = 1000
```

Why? Subtracting 1 changes the **rightmost set bit** to 0 and changes all trailing zero bits to 1. AND removes that rightmost 1.

Consequences:

- `n & (n-1)` clears the least significant set bit.
- For `n>0`, `n & (n-1) == 0` iff `n` has exactly one set bit, i.e. a power of 2.
- Repeatedly clearing the lowest set bit counts ones in **`O(number of set bits)`**.

```cpp
int countSetBits(ull n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1);
        ++count;
    }
    return count;
}

bool isPowerOfTwo(ull n) {
    return n != 0 && (n & (n - 1)) == 0;
}
```

**Dry run `n=13=1101₂`:** `1101→1100→1000→0000` → **3** set bits.

**Isolate the rightmost 1:** with unsigned arithmetic `n & (~n + 1)`; for `n=12`, result `0100₂ = 4`.

### 8.5 XOR cancellation

XOR is especially useful because `x^x=0`, `x^0=x`, and the order of XOR operations does not matter.

If every number occurs twice except one, XOR all numbers:

`[4,1,2,1,2] → 4^1^2^1^2 → 4^(1^1)^(2^2) → 4`.

```cpp
int singleNumber(const vector<int>& a) {
    int result = 0;
    for (int x : a) result ^= x;
    return result;
}
```

### 8.6 Subsets: include/exclude becomes a bitmask

For a set `{A,B,C}`, each item has two choices: include (1) or exclude (0). Thus `2×2×2=2³=8` subsets.

```text
mask   bits   subset
 0     000    {}
 1     001    {A}
 2     010    {B}
 3     011    {A,B}
 4     100    {C}
 5     101    {A,C}
 6     110    {B,C}
 7     111    {A,B,C}
```

**Dry run `mask=5=101₂`:** bit0=1 → A included; bit1=0 → B excluded; bit2=1 → C included.

```cpp
void printAllSubsets(const vector<int>& a) { // practical only for small n
    int n = (int)a.size();
    if (n >= 63) return; // 2^n subsets would also be too many
    for (ull mask = 0; mask < (1ULL << n); ++mask) {
        cout << "{";
        bool first = true;
        for (int i = 0; i < n; ++i) {
            if ((mask & (1ULL << i)) != 0) {
                if (!first) cout << ",";
                cout << a[i];
                first = false;
            }
        }
        cout << "}\n";
    }
}
```

**⏱️ Complexity:** `2^n` masks, check `n` items each → `O(n·2^n)` time, `O(1)` extra memory aside from output.

**🧠 Recognize:** flags/permissions, powers of two, subsets of `n<=20`, one unpaired element, toggles and XOR properties.


[↑ Contents](#contents)

---

## 9. Boolean logic and De Morgan's laws

*Source: p.28.*

> 🧭 **Concept map:** `True/false → AND/OR/NOT → De Morgan → safe conditions`
>
> 🎯 **Guiding question:** How can a complex condition be simplified correctly?

### 9.1 Prerequisite — truth values

A Boolean condition is either `true` or `false`. In C++:

- `&&`: AND → both conditions must hold.
- `||`: OR → at least one condition must hold.
- `!`: NOT → reverse the truth value.
- `^` on `bool` implements exclusive OR, but `^` on integers is **bitwise XOR**, not exponentiation.

| `A` | `B` | `A&&B` | `A\|\|B` | `A^B` |
|---|---|---|---|---|
| F | F | F | F | F |
| F | T | F | T | T |
| T | F | F | T | T |
| T | T | T | T | F |

### 9.2 De Morgan's law — negate the entire condition

Consider `!(age >= 18 && hasId)` meaning **not both requirements are satisfied**.

It can happen because `age < 18` **or** `!hasId`.

Hence:

- `!(A && B) == (!A || !B)`.
- `!(A || B) == (!A && !B)`.

A simple memory aid: **NOT moves inside, flipping AND ↔ OR**, while negating each part.

**🔎 Example:** `!(x < 5 || x > 10)` = `x >= 5 && x <= 10` (inside inclusive range).

### 9.3 Operator precedence and readable C++

Use parentheses rather than relying on precedence, especially if mixing `&&`, `||`, comparisons and bit operations.

```cpp
bool eligible(int age, bool hasId, bool banned) {
    return (age >= 18) && hasId && (!banned);
}

bool outsideRange(int x, int low, int high) {
    return (x < low) || (x > high);
}
```

**🧪 Dry run:** `age=20, hasId=true, banned=true` → true && true && false = **false**.

**🧠 Recognize:** multiple constraints in statements, interval conditions, negating invalid states, simplifying an if condition.


[↑ Contents](#contents)

---

## 10. Sets and set operations

*Source: p.29.*

> 🧭 **Concept map:** `Distinct members → set operations → power set → overlap`
>
> 🎯 **Guiding question:** Which elements are unique, shared or missing?

### 10.1 Prerequisites — membership, distinctness, order

A mathematical **set** is a collection with **no duplicates**. `{1,1,2,3}` denotes the same set as `{1,2,3}`. Set order is irrelevant mathematically.

In C++:

- `std::set<int>` stores unique elements **in sorted order** (`O(log n)` typical insert/find).
- `std::unordered_set<int>` stores unique elements with average `O(1)` insert/find, no sorting guarantee; worst case can be slower.

### 10.2 Set vocabulary and operations

Take `A={1,2,3}` and `B={2,3,4}`.

| Operation | Meaning | Result |
|---|---|---|
| `A ∪ B` union | In A **or** B | `{1,2,3,4}` |
| `A ∩ B` intersection | In **both** | `{2,3}` |
| `A \ B` difference | In A but **not** B | `{1}` |
| Complement `U\A` | In universe U but not A | depends on `U` |
| Subset `A⊆C` | Every A element belongs to C | e.g. `{1,2}⊆A` |
| Cardinality `\|A\|` | number of distinct members | `\|A\|=3` |

**Visualize the overlap:**

```text
A only       In both A and B       B only
 {1}              {2,3}             {4}
  └──────────── A ∪ B ──────────────┘
```

The intersection contains only what is shared. The union contains **everything**, counted once.

### 10.3 Power set: all possible subsets

For `{1,2,3}`, every element is independently in/out → `2³=8` subsets, including empty and full. This is exactly the bitmask approach from §8.

### 10.4 Inclusion–exclusion preview

Counting `|A|+|B|` counts elements in the intersection twice. Remove the duplicate once:

**`|A∪B| = |A| + |B| - |A∩B|`.**

For the example: `3+3-2=4` distinct members. We revisit this principle with counting in §11.

```cpp
vector<int> uniqueSorted(vector<int> values) {
    sort(values.begin(), values.end());
    values.erase(unique(values.begin(), values.end()), values.end());
    return values;
}
```

**🧪 Dry run:** `[3,1,3,2,2]` → sort `[1,2,2,3,3]` → unique `[1,2,3]`.

**🧠 Recognize:** distinct values, membership queries, union/intersection, duplicate removal, subset problems.


[↑ Contents](#contents)

---

## 11. Counting principles and inclusion–exclusion

*Source: pp.30–33.*

> 🧭 **Concept map:** `OR/AND counting → complement → overlap → pigeonhole`
>
> 🎯 **Guiding question:** Should you add, multiply, subtract the opposite, or remove duplicates?

### 11.1 Prerequisites — distinguish **OR** from **AND**

Counting problems ask **how many possible outcomes** exist, not what a particular outcome is.

- **Addition rule (OR):** choose **one of two non-overlapping possibilities** → `m+n` ways.
- **Multiplication rule (AND):** make a choice for stage 1 **and then** stage 2 → `m×n` ways.

**Example OR:** choose one shirt from 3 red shirts or 4 blue shirts → `3+4=7` options (assuming the alternatives don't overlap).

**Example AND:** choose one of 3 shirts AND one of 2 pants → `3×2=6` outfits.

```text
Shirt 1 ─┬─ Pant A
         └─ Pant B
Shirt 2 ─┬─ Pant A     3 groups × 2 choices = 6
         └─ Pant B
Shirt 3 ─┬─ Pant A
         └─ Pant B
```

If the OR alternatives **overlap**, plain addition double-counts: use inclusion–exclusion.

### 11.2 Complement counting — count the easier opposite

**Problem:** How many three-digit integers have *not all digits equal*?

1. Three-digit integers run from 100 to 999 → `900` integers.
2. The unwanted case “all digits equal” is `{111,222,...,999}` → `9` integers.
3. Desired count = `900-9=891`.

**General rule:** `count(wanted) = count(all) - count(not wanted)`.

**Another example:** 4-character passwords from 26 letters + 10 digits (repeats allowed), containing **at least one digit**:

1. All passwords: `36⁴`.
2. No digit (letters only): `26⁴`.
3. At least one digit: **`36⁴ - 26⁴`**.

**🧠 Recognize:** *at least one*, *not all*, *anything except*, *some forbidden property*.

### 11.3 Avoid double counting unordered pairs

**Problem:** `n` people each shake hands once with everyone else. Number of handshakes?

1. Each person has `n-1` partners → `n(n-1)` **directed** descriptions.
2. But handshake A–B is the same as B–A.
3. Every handshake was counted exactly twice.
4. Answer **`n(n-1)/2`**.

**Dry run `n=4`:** AB, AC, AD, BC, BD, CD → **6**.

### 11.4 Pigeonhole principle — why a repetition is inevitable

If `n+1` objects are assigned to `n` boxes, at least one box contains two or more objects.

**🔎 Example:** 13 people, 12 possible birth months → at least **two** people share a birth month.

**🔎 Why?** If every month had at most one person, there could be at most 12 people. But 13 people exist → contradiction.

Generalized form: among `N` objects placed in `k` boxes, **some box has at least `ceil(N/k)`** objects.

### 11.5 Inclusion–exclusion: count overlapping sets correctly

**Problem:** Integers 1..30 divisible by 2 **or** by 3.

1. Multiples of 2: `floor(30/2)=15`.
2. Multiples of 3: `floor(30/3)=10`.
3. Multiples of **both** are multiples of `LCM(2,3)=6`: `floor(30/6)=5`.
4. Add and subtract overlap: `15+10-5=20`.

| Set | Count | Why |
|---|---:|---|
| Multiples of 2 | 15 | 2,4,...,30 |
| Multiples of 3 | 10 | 3,6,...,30 |
| Counted twice | 5 | 6,12,18,24,30 |
| **Union** | **20** | 15+10−5 |

For three sets:

`|A∪B∪C| = |A|+|B|+|C| - |A∩B|-|A∩C|-|B∩C| + |A∩B∩C|`.

**Why add the triple overlap back?** It was added 3 times, then subtracted 3 times → counted zero times; it must be included once.

### 11.6 Number of divisors from prime exponents

If `n=p₁^a₁ · p₂^a₂ · ... · pₖ^aₖ`, each divisor chooses an exponent from `0..aᵢ` for each prime independently.

So **`divisorCount(n) = (a₁+1)(a₂+1)...(aₖ+1)`**.

**Dry run `72=2³×3²`:** choose exponent of 2 among `{0,1,2,3}` = 4 choices; exponent of 3 among `{0,1,2}` = 3 → `4×3=12` positive divisors.

```cpp
ll countMultiplesUpTo(ll n, ll d) { // n >= 0, d > 0
    return n / d;
}
ll divisibleByEither(ll n, ll a, ll b) { // a,b > 0
    ll g = std::gcd(a, b);
    // If lcm > n then no integer in [1,n] can be multiple of both.
    ll both = (a / g > n / b) ? 0 : n / ((a / g) * b);
    return n / a + n / b - both;
}
```

**🧠 Recognize:** AND/OR, at least one, groups/categories, guaranteed collision, overlapping multiples, divisor counting.


[↑ Contents](#contents)

---

## 12. Factorials, permutations and derangements

*Source: pp.34–36.*

> 🧭 **Concept map:** `Arrangement → factorial → repeated elements → circular → derangement`
>
> 🎯 **Guiding question:** Does switching the positions change the result?

### 12.1 Prerequisites — counting arrangements

**Permutation** means an arrangement where **order matters**. Consider three distinct colored balls R, G, B.

```text
RGB   RBG
GRB   GBR
BRG   BGR
```

Three choices for the first position, then two for the second, then one for the last → `3×2×1=6` arrangements.

**Factorial:** `n! = n×(n-1)×...×2×1`; special case `0!=1` (one empty arrangement).

**Why `n!`?** Arrange `n` distinct objects into `n` positions: `n` choices for slot 1, `n-1` for slot 2, ..., `1` for final slot → multiply them.

### 12.2 Partial permutation: choose **and arrange** `r` objects

**Problem:** From five different people, select president, vice-president and secretary.

Positions are different, so the order matters:

1. President: 5 choices.
2. Vice-president: 4 remaining choices.
3. Secretary: 3 remaining choices.
4. Total = `5×4×3 = 60`.

Formula: **`P(n,r) = n!/(n-r)!`** for `0<=r<=n`. This cancels the tail of the factorial, leaving `r` factors.

| Problem | Correct model | Count |
|---|---|---:|
| Arrange all 4 distinct books | `4!` | 24 |
| Award gold/silver among 4 runners | `4×3` | 12 |
| Select 2 teammates out of 4 | Combination, not permutation | 6 |

### 12.3 Repetition allowed versus forbidden

**With repetition:** If 3 digit positions can each use any digit 0..9, there are `10×10×10=10³=1000` **digit strings** (including those starting with 0).

**Without repetition:** `10×9×8=720` 3-digit strings with no repeated digits.

**Careful:** If the problem means **three-digit integers**, the first digit cannot be 0.

- Without repetitions the count becomes `9×9×8=648` (first: 1..9; second: remaining digits including 0; third: remaining 8).

**🧠 Recognize:** repeated allowed → powers `n^r`; repeated forbidden → falling product `P(n,r)`.

### 12.4 Equal objects: multiset permutations

When objects repeat, swapping identical objects creates no new arrangement.

**Problem:** How many distinct arrangements of `AAB`?

- If letters were all distinct, `3!=6`.
- The two A's can swap in `2!` ways without producing a new string.
- Answer `3!/2!=3`: `AAB, ABA, BAA`.

General count for `n` objects where each symbol appears `c₁,c₂,...,cₖ` times:

**`n! / (c₁! c₂! ... cₖ!)`**, with `c₁+...+cₖ=n`.

**🧪 Dry run:** `BOOK` has two O's → `4!/2!=12` arrangements.

### 12.5 Circular permutations: rotations are equivalent

For `n` distinct people seated around a round table, if only relative order matters and rotations of the entire circle are identical, fix one person's position.

- Fix person A as an anchor.
- Arrange remaining `n-1` people → **`(n-1)!`**.

**🧪 Dry run:** 4 people around a table → fix one person → arrange the other 3 → `3! = 6`.

> ⚠️ **⚠️ Important:** Rotations are treated as identical here. Reflections are **not** automatically identical—only identify mirror arrangements when the problem says so.

### 12.6 Derangements: nobody stays in their original slot

A **derangement** is a permutation in which no element appears at its original position.

Three people originally at `[A,B,C]`. All 6 permutations are:

```text
ABC  ← all fixed, invalid
ACB  ← A fixed, invalid
BAC  ← C fixed, invalid
BCA  ← none fixed, valid
CAB  ← none fixed, valid
CBA  ← B fixed, invalid
```

So `D(3)=2`.

**Derive recurrence by examining where object 1 goes:**

1. Object 1 moves into one of `n-1` other places; call the selected position `j`.
2. Either the object originally in position `j` goes back to position 1; that leaves `D(n-2)` possibilities.
3. Or it does not; after reducing the arrangement, there are `D(n-1)` possibilities.
4. Sum and multiply by the `n-1` possible choices of `j`:

**`D(n)=(n-1)[D(n-1)+D(n-2)]`**, with `D(0)=1`, `D(1)=0`.

**🧪 Dry run:** `D(2)=1×(0+1)=1`; `D(3)=2×(1+0)=2`; `D(4)=3×(2+1)=9`.

```cpp
vector<ll> derangements(int n) { // values grow fast: suitable only for small n
    vector<ll> dp(n + 1);
    dp[0] = 1;
    if (n >= 1) dp[1] = 0;
    for (int i = 2; i <= n; ++i)
        dp[i] = (i - 1LL) * (dp[i - 1] + dp[i - 2]);
    return dp;
}
```

**⏱️ Complexity:** `O(n)` time and space.

- For large `n`, a problem will often supply a modulus; use modular DP to avoid overflow.
- The classic approximation `D(n)≈n!/e` is helpful for intuition, **not** an exact substitute in code.

**🧠 Recognize:** rankings, seating, passwords, distinct vs repeated objects, arranging selected items, “no one gets their own item”.

**✍️ Check yourself:** How many 4-letter strings from A, B, C with repetitions? How many arrangements of `BANANA`?


[↑ Contents](#contents)

---

## 13. Combinations, Pascal's triangle and repetition

*Source: pp.37–42. Binomial theorem and Stars and Bars are supplements; the handout flags them as TODO.*

> 🧭 **Concept map:** `Selection → combination → Pascal → repetition → Stars and Bars`
>
> 🎯 **Guiding question:** Does order matter, and can a choice be repeated?

### 13.1 Prerequisite — distinguish selection from arrangement

Suppose Alice and Bob are selected as a team. `Alice,Bob` and `Bob,Alice` describe the **same team**. Order does **not** matter → **combination**.

But `Alice=leader, Bob=assistant` differs from the reverse → **permutation**.

**One decision question:** If I swap two selected positions, does the outcome change? **Yes** → permutation. **No** → combination.

### 13.2 Derive the combination formula, don't memorize blindly

**Problem:** Choose 3 people from 5 to form an unordered team.

1. First count **ordered** selections: `P(5,3)=5×4×3=60`.
2. Every group of three can appear in `3!=6` orders.
3. All these six orders describe **one** unordered team.
4. Divide by 6: `60/6=10`.

Thus **`C(n,r)=P(n,r)/r! = n!/[r!(n-r)!]`** for `0<=r<=n`.

**🧪 Dry run — choose 2 from {1,2,3,4}:**

`{1,2}, {1,3}, {1,4}, {2,3}, {2,4}, {3,4}` → `6 = C(4,2)`.

### 13.3 Symmetry, boundaries and Pascal identity

- **Symmetry:** `C(n,r)=C(n,n-r)` because choosing `r` people to include is equivalent to choosing `n-r` people to leave out.
- **Boundary:** `C(n,0)=C(n,n)=1` (one way: choose none or all).
- **Invalid:** `C(n,r)=0` for `r<0` or `r>n`.
- **Single selection:** `C(n,1)=n`.

**Pascal's identity:**

`C(n,r) = C(n-1,r-1) + C(n-1,r)`.

- **🔎 Why?** Choose one special person X.
- Any selected group of size `r` either **includes X** (choose `r-1` from remaining `n-1`) or **excludes X** (choose all `r` from remaining `n-1`).
- These cases don't overlap, so add counts.

### 13.4 Pascal triangle — build combinations by addition

```text
n=0                   1
n=1                 1   1
n=2               1   2   1
n=3             1   3   3   1
n=4           1   4   6   4   1
               ...
```

Each inner entry is the sum of two entries above. `C(4,2)=6` is the center of row 4.

**Row sum:** `Σ(r=0..n) C(n,r)=2^n` because every item can independently be included or excluded.

```cpp
vector<vector<ll>> pascal(int n) { // choose n small enough for ll
    vector<vector<ll>> c(n + 1, vector<ll>(n + 1, 0));
    for (int i = 0; i <= n; ++i) {
        c[i][0] = c[i][i] = 1;
        for (int j = 1; j < i; ++j)
            c[i][j] = c[i - 1][j - 1] + c[i - 1][j];
    }
    return c;
}
```

**⏱️ Complexity:** `O(n²)` time and space; good for moderate `n` when many combinations are queried.

### 13.5 Computing `C(n,r)` without creating huge factorials

For one query with small `r`, use `r=min(r,n-r)` then multiply/divide incrementally.

**Example `C(10,3)`:**

`C(10,3) = (8×9×10)/(1×2×3)`. Process one fraction at a time:

`1×8/1=8` → `8×9/2=36` → `36×10/3=120`.

**How the incremental formula arises**

```text
answer starts at 1
for i = 1 to r:
    answer = answer × (n - r + i) / i
```

Each step produces an integer. However, multiplying before dividing can create an intermediate value that does not fit `long long`.

**Fix:** Cancel common factors using GCD **before multiplying**.

```cpp
ll chooseSmall(int n, int r) { // exact result must fit long long
    if (r < 0 || r > n) return 0;
    r = min(r, n - r);
    ll answer = 1;

    for (int i = 1; i <= r; ++i) {
        ll numerator = n - r + i;
        ll denominator = i;

        ll g = gcd(numerator, denominator);
        numerator /= g;
        denominator /= g;

        g = gcd(answer, denominator);
        answer /= g;
        denominator /= g; // becomes 1 because this step is exact

        answer *= numerator; // no oversized intermediate product
    }
    return answer;
}
```

**🧪 Dry run:**

| `i` | New numerator / denominator | `answer` before | Cancel denominator | `answer` after |
|---:|---|---:|---|---:|
| 1 | `8/1` | 1 | Nothing | 8 |
| 2 | `9/2` | 8 | Cancel 2 with 8 → `4×9` | 36 |
| 3 | `10/3` | 36 | Cancel 3 with 36 → `12×10` | **120** |

#### Why GCD cancellation works

1. At step `i`, the intended value is `C(n-r+i, i)`—an **integer**.
2. First cancel common factors between the new numerator and denominator.
3. Any denominator still left must divide the existing `answer` exactly.
4. Multiply only **after** both cancellations.

> ✅ **Result:** When the exact answer fits `long long`, this method avoids an oversized intermediate product. **Time:** `O(r log n)` with GCD.

### 13.6 [SUPPLEMENT] Binomial theorem — expand `(a+b)^n`

*The source p.40 says TODO; this is added instruction.*

**Discover the coefficients**

1. Expand `(a+b)^n` by choosing one term from each of the `n` brackets.
2. To produce `a^(n-r) × b^r`, choose `b` from **exactly `r` brackets**.
3. The number of ways to choose those brackets is `C(n,r)`.
4. Therefore, `C(n,r)` is the coefficient of that term.

Therefore:

`(a+b)^n = Σ(r=0..n) C(n,r) a^(n-r) b^r`.

**🔎 Example:** `(a+b)^3 = a³ + 3a²b + 3ab² + b³`.

Setting `a=b=1` yields `2^n = Σ C(n,r)`, explaining the Pascal row sum.

### 13.7 Choosing with repetition — combinations of types

**Problem:** Choose 3 fruits from {apple, orange, banana}; unlimited copies of each fruit and order irrelevant.

- Ordinary combinations `C(3,3)=1` wrongly assume each type can be chosen at most once.
- With repetition, `[apple,apple,orange]` is valid.
- The number of **multisets** of size `r` from `n` types is **`C(n+r-1,r)`**.

Here `C(3+3-1,3)=C(5,3)=10`.

### 13.8 [SUPPLEMENT] Stars and Bars — derive the formula

*The PDF p.41 mentions the topic but leaves the derivation as TODO.*

Represent `r` identical objects (stars) distributed among `n` labeled boxes using `n-1` separators (bars).

Example: 5 candies among 3 children, allowing zero candies:

```text
** | *** |        → child1=2, child2=3, child3=0
```

**Count the possible arrangements**

1. Put down `r` identical stars.
2. Add `n-1` bars to separate them into `n` labeled boxes.
3. Total positions = `r + n - 1`.
4. Choose the `n-1` bar positions—or equivalently the `r` star positions.

**Nonnegative solutions** of `x₁+...+xₙ=r`:

**`C(r+n-1,n-1)`**.

**Positive solutions** (`xᵢ>=1`): give one item to every child first. Remaining `r-n` items are distributed nonnegatively:

**`C(r-1,n-1)`** when `r>=n`; otherwise zero.

**🧪 Dry run:** `x+y+z=5`, `x,y,z>=1` → remove 1 each → `x'+y'+z'=2`, nonnegative → `C(2+3-1,3-1)=C(4,2)=6`.

**⚠️ Caution:** Stars and Bars applies to **identical** items going into labeled containers, with no upper bound on a container except the total.

- Distinct items or individual capacities change the counting method.

**🧠 Recognize:** selecting a team vs ranking winners, combinations of item types, sums of nonnegative integers, binomial coefficients.


[↑ Contents](#contents)

---

## 14. Elementary probability

*Source: pp.43–44.*

> 🧭 **Concept map:** `Outcomes → favorable count → probability → complement`
>
> 🎯 **Guiding question:** Which possible outcomes satisfy the event?

### 14.1 Prerequisites — sample space, event and equal likelihood

- **Experiment:** e.g. roll a fair six-sided die.
- **Sample space:** `S={1,2,3,4,5,6}` (every possible result).
- **Event:** a subset of outcomes, e.g. `E={2,4,6}` for even results.
- If outcomes are **equally likely**, `P(E)=number of favorable outcomes / number of all outcomes`.

For an even die result: `P(E)=3/6=1/2`.

**Bounds:** Always `0<=P(E)<=1`; impossible event has probability 0; certain event has probability 1.

### 14.2 Compute probability from a simple event

**Problem:** Roll one fair die. What is `P(roll>4)`?

1. List all outcomes: `{1,2,3,4,5,6}`.
2. Favorable outcomes: `{5,6}`.
3. Count favorable = 2, total = 6.
4. Probability `2/6=1/3`.

**🧪 Dry run:** `P(roll=4)=1/6`; `P(roll is even)=3/6=1/2`; sum of mutually exclusive events `P(roll=4 or roll=5)=2/6`.

### 14.3 Complement probability

If an event does not happen, its **complement** happens. Exactly one of the two occurs:

**`P(not E)=1-P(E)`**.

**🔎 Example:** If probability of rain is 0.3, probability of no rain is `1-0.3=0.7`.

**At least one success in independent trials:** It is often easier to calculate the complement, *no successes*.

For 3 fair coin flips:

1. Probability every flip is a tail = `(1/2)³=1/8`.
2. Thus probability of **at least one head** = `1-1/8=7/8`.

Independence matters for multiplying probabilities. Two dependent events cannot necessarily be multiplied this way.

### 14.4 Probability versus modular arithmetic

**First ask: what type of answer does the problem expect?**

| Required output | Method | Example |
|---|---|---|
| Decimal approximation | `double` / `long double` | `1.0 / 3` |
| Reduced fraction | GCD on numerator and denominator | `2/6 → 1/3` |
| Fraction modulo a prime | Modular inverse (Chapter 17) | `a × inverse(b) % mod` |

> ⚠️ Plain integer division `a / b` is **not** modular division.

```cpp
double probability(int favorable, int total) { // 0 <= favorable <= total
    return (double)favorable / total;           // total > 0
}

double atLeastOneSuccess(double successP, int trials) {
    // Independent trials, same chance each trial.
    return 1.0 - pow(1.0 - successP, trials);
}
```

**🧠 Recognize:** equally likely cases, dice/coins/cards, at least one success, complementary cases, favorable/total counting.


[↑ Contents](#contents)

---

## 15. Divisibility, factors and prime numbers

*Source: pp.45–47.*

> 🧭 **Concept map:** `Factors → sqrt pairs → primality → sieve → prime powers`
>
> 🎯 **Guiding question:** What can be learned by finding the prime structure of an integer?

### 15.1 Prerequisites — factors, multiples, prime and composite

If `a=b*k` for an integer `k`, then `b` **divides** `a`, written `b | a`.

For `a=12`, divisors are `{1,2,3,4,6,12}`. Multiples of 12 are `{12,24,36,...}`.

- **Prime:** positive integer greater than 1 with exactly two positive divisors: 1 and itself. Examples: 2,3,5,7,11.
- **Composite:** integer greater than 1 with more than two positive divisors. Examples: 4,6,8,9.
- **1 is neither prime nor composite.**
- **2 is the only even prime.**

**Quick divisibility tests:** divisible by 2 if last digit even; by 5 if last digit 0/5; by 10 if last digit 0; by 3 if digit sum divisible by 3; by 9 if digit sum divisible by 9.

**Why digit sum works for 9:** every power of 10 is congruent to 1 modulo 9, as seen in §1.

### 15.2 Why factors come in pairs

Consider divisors of 36:

```text
1 × 36
2 × 18
3 × 12
4 ×  9
6 ×  6  ← meet at sqrt(36)
```

If `d` divides `n`, then `n/d` is also a divisor. One member of the pair must be at most `sqrt(n)` (unless they are equal at a perfect square).

Therefore check only `d*d <= n`, or safer in integer arithmetic **`d <= n/d`** for `d>0`.

### 15.3 Enumerate divisors in `O(sqrt n)`

**Dry run `n=12`:**

| Try `d` | Does `12%d==0`? | Pair added |
|---:|---|---|
| 1 | yes | 1, 12 |
| 2 | yes | 2, 6 |
| 3 | yes | 3, 4 |
| stop | next d=4, and `4>12/4` | — |

Sorted divisors: `1,2,3,4,6,12`.

```cpp
vector<ll> divisors(ll n) { // n >= 1
    vector<ll> result;
    for (ll d = 1; d <= n / d; ++d) {
        if (n % d == 0) {
            result.push_back(d);
            if (d != n / d) result.push_back(n / d);
        }
    }
    sort(result.begin(), result.end());
    return result;
}
```

**Avoid duplicate square root:** for `n=36`, the pair at `d=6` is `(6,6)`, so add 6 only once.

### 15.4 Primality test by trial division

**Problem:** Is 29 prime?

1. We need only try divisors through `sqrt(29)≈5.3`.
2. Test `2` → no. Test `3` → no. Test `4` → no. Test `5` → no.
3. No divisor found → **29 is prime**.

For `n=35`, testing 5 discovers `35%5==0` → composite.

```cpp
bool isPrime(ll n) {
    if (n < 2) return false;
    for (ll d = 2; d <= n / d; ++d)
        if (n % d == 0) return false;
    return true;
}
```

**Time:** `O(sqrt n)` for a single check, `O(1)` extra space. For millions of repeated prime queries under a fixed limit, use the sieve instead.

### 15.5 Sieve of Eratosthenes — find all primes to `N`

The idea is to start with every integer `>=2` as potentially prime. Whenever `p` is prime, mark multiples `p²,p²+p,...` as composite.

**🔎 Why start crossing out at `p²`?**

1. Multiples `2p, 3p, ...` below `p²` contain a factor smaller than `p`.
2. Those were already crossed out in an earlier sieve step.
3. The first multiple that could still be unmarked is `p²`.

Example: when `p=5`, `10,15,20` were already removed by 2 or 3. Start at **25**.

**Dry run up to 20:**

1. Candidate 2: mark 4,6,8,10,12,14,16,18,20.
2. Candidate 3: mark 9,12,15,18 (begin at 9).
3. Candidate 4 was marked composite, skip.
4. Stop crossing off at primes `p` with `p²>20`.
5. Primes remain `2,3,5,7,11,13,17,19`.

```cpp
vector<bool> primeSieve(int limit) { // limit >= 0
    vector<bool> prime(limit + 1, true);
    prime[0] = false;
    if (limit >= 1) prime[1] = false;
    for (int p = 2; (ll)p * p <= limit; ++p) {
        if (!prime[p]) continue;
        for (ll multiple = (ll)p * p; multiple <= limit; multiple += p)
            prime[(int)multiple] = false;
    }
    return prime;
}
```

**Complexity:** `O(N log log N)` time and `O(N)` space for the usual sieve.

### 15.6 Prime factorization — build an integer from prime powers

Every integer `n>1` has a unique prime factorization (ignoring order).

Example `72 = 2×2×2×3×3 = 2³×3²`.

**Dry run by repeated division:**

```text
72 ÷2 = 36   → one factor 2
36 ÷2 = 18   → second factor 2
18 ÷2 =  9   → third factor 2
 9 ÷3 =  3   → one factor 3
 3 ÷3 =  1   → second factor 3
```

```cpp
vector<pair<ll,int>> primeFactorization(ll n) { // n >= 1
    vector<pair<ll,int>> factors;
    for (ll p = 2; p <= n / p; ++p) {
        if (n % p != 0) continue;
        int exponent = 0;
        while (n % p == 0) {
            n /= p;
            ++exponent;
        }
        factors.push_back({p, exponent});
    }
    if (n > 1) factors.push_back({n, 1});
    return factors;
}
```

**⏱️ Complexity:** worst-case `O(sqrt n)` time for simple trial division.

- Factorization is useful for divisor counts (§11), GCD/LCM reasoning (§16), and many number-theory problems.

**🧠 Recognize:** primality, prime factors, count factors, perfect numbers, divisibility constraints, many prime queries.


[↑ Contents](#contents)

---

## 16. GCD, LCM and the Euclidean algorithm

*Source: pp.47–52.*

> 🧭 **Concept map:** `Common factors → Euclid → GCD → LCM → synchronization`
>
> 🎯 **Guiding question:** Do we need the largest common unit or the earliest common multiple?

### 16.1 Prerequisites — common factors and common multiples

**GCD** of two numbers is their **greatest positive common divisor**. **LCM** is their **least positive common multiple** (when both inputs are nonzero).

For 12 and 18:

- Divisors of 12: `1,2,3,4,6,12`.
- Divisors of 18: `1,2,3,6,9,18`.
- Greatest shared divisor = **GCD(12,18)=6**.
- Multiples of 12: `12,24,36,...`; of 18: `18,36,...` → **LCM(12,18)=36**.

**Coprime** numbers have GCD=1. For example 8 and 15 are coprime, even though neither is prime.

### 16.2 Why the Euclidean algorithm works

**Derive Euclid's rule without skipping steps**

1. Divide `a` by `b`: `a = b×q + r`.
2. Suppose `d` divides both `a` and `b`.
3. Then `d` divides their difference `a - b×q = r`.
4. Conversely, if `d` divides `b` and `r`, it divides `b×q+r=a`.
5. Therefore, the two pairs have **exactly the same common divisors**.

```text
gcd(a, b) = gcd(b, a % b)
```

The second argument gets smaller each step, so the process finishes.

This lets us replace the larger number by a smaller remainder until one side is zero.

**Dry run `gcd(48,18)`:**

| `a` | `b` | `a%b` | Next pair |
|---:|---:|---:|---|
| 48 | 18 | 12 | (18,12) |
| 18 | 12 | 6 | (12,6) |
| 12 | 6 | 0 | (6,0) |
| 6 | 0 | — | stop: **6** |

```cpp
ll gcdEuclid(ll a, ll b) { // for nonnegative a,b
    while (b != 0) {
        ll remainder = a % b;
        a = b;
        b = remainder;
    }
    return a;
}
```

C++17 already provides **`std::gcd(a,b)`**, which handles negative inputs by magnitude in normal representable cases. `gcd(0,b)=|b|`; `gcd(0,0)=0` by C++ convention.

**Complexity:** Euclid takes `O(log(min(a,b)))` time in the usual asymptotic analysis, `O(1)` space.

### 16.3 LCM formula — derive it from prime factors

If `a` and `b` have prime decompositions, GCD takes **minimum** prime exponents and LCM takes **maximum** exponents.

Example:

`12=2²×3¹`, `18=2¹×3²`.

- GCD: `2^min(2,1) × 3^min(1,2) = 2×3=6`.
- LCM: `2^max(2,1) × 3^max(1,2) = 4×9=36`.

For any two exponents `u,v`, `min(u,v)+max(u,v)=u+v`. Across all primes this gives:

**`gcd(a,b) × lcm(a,b) = |a*b|`** (for positive inputs as used here).

Thus `lcm(a,b)=(a/gcd(a,b))*b`. Divide **before** multiplying to lower overflow risk.

```cpp
ll lcmPositive(ll a, ll b) { // a > 0, b > 0, answer fits ll
    return (a / std::gcd(a, b)) * b;
}
```

### 16.4 GCD/LCM of an entire array

Because GCD and LCM associate across multiple inputs:

`gcd(a,b,c)=gcd(gcd(a,b),c)`.

For `[12,18,30]`:

- `gcd(12,18)=6`; then `gcd(6,30)=6` → array GCD **6**.
- `lcm(12,18)=36`; then `lcm(36,30)=180` → array LCM **180**.

```cpp
ll gcdArray(const vector<ll>& a) {
    ll answer = 0;
    for (ll x : a) answer = std::gcd(answer, x);
    return answer;
}
```

### 16.5 GCD to simplify fractions

Reduce `8/12`: common factor `gcd(8,12)=4`; divide numerator and denominator by 4:

`8/12 = (8/4)/(12/4) = 2/3`.

This works because division by the same nonzero quantity does not change the fraction.

### 16.6 LCM as the first synchronized event

**Problem:** One light flashes every 4 seconds, another every 6. Both flash at t=0. When do they next flash together?

- First light: 4,8,12,16,...
- Second light: 6,12,18,...
- First positive common time: **LCM(4,6)=12 seconds**.

### 16.7 Recognition guide: when GCD vs LCM?

| Problem story | Likely tool | Why |
|---|---|---|
| Cut ropes of lengths 12 and 18 into **largest equal integer pieces** | GCD = 6 | Piece size must divide both |
| Simplify fraction `42/56` | GCD = 14 | Remove largest shared factor |
| Two events repeat every 4 and 6 time units; first coincidence | LCM = 12 | Must be a multiple of both |
| Smallest quantity divisible by `a` and `b` | LCM | Minimum shared multiple |
| Maximum size of uniform square tiles fitting both sides | GCD | Side length divides both dimensions |

**Optional context:** p.50 mentions extended Euclid (useful later for modular inverse) and p.52 sketches binary/Stein GCD.

- These are advanced alternatives, not prerequisites for the basic approach; `std::gcd` is preferred in ordinary C++17 solutions.

**✍️ Check yourself:** `gcd(84,30)`? `lcm(8,12)`? Why is `gcd(15,8)=1` even though 15 is composite?


[↑ Contents](#contents)

---

## 17. Modular arithmetic, inverse and fast power

*Source: pp.53–55; inverse details below are an explicit teaching expansion.*

> 🧭 **Concept map:** `Remainders → mod arithmetic → fast power → inverses`
>
> 🎯 **Guiding question:** How do we keep huge arithmetic inside a fixed modulus safely?

### 17.1 Prerequisites — remainder and equivalence classes

With modulus `m>0`, two integers are **congruent modulo `m`** if their difference is divisible by `m`:

**`a ≡ b (mod m)` iff `m | (a-b)`**.

Examples: `12≡2 (mod 5)` because `12-2=10` is divisible by 5; also `-3≡2 (mod 5)` because `-3-2=-5` is divisible by 5.

Think of a clock with `m` positions: numbers separated by `m` land at the same position.

### 17.2 Normalize a negative remainder in C++

Mathematical canonical remainder for `m>0` is from `0` through `m-1`. But C++ `-2 % 5` is **`-2`**, not 3.

Normalize by:

**`((a % m) + m) % m`**.

**🧪 Dry run:** `a=-2`, `m=5`: `a%m=-2` → add 5 → 3 → `%5=3`.

```cpp
ll normalizeMod(ll value, ll mod) { // mod > 0
    ll result = value % mod;
    if (result < 0) result += mod;
    return result;
}
```

This avoids overflow risk from blindly calculating `(a%m+m)` when the modulus itself is extremely large.

### 17.3 Modular addition, subtraction and multiplication

The most important property: reducing inputs before a compatible operation does **not** change the final remainder.

- `(a+b)%m = ((a%m)+(b%m))%m`.
- `(a-b)%m = ((a%m)-(b%m)+m)%m` when residues are in `[0,m)` and intermediate addition fits.
- `(a*b)%m = ((a%m)*(b%m))%m`.

**🔎 Why can we multiply the remainders?**

1. Write `a = q₁×m + r₁` and `b = q₂×m + r₂`.
2. Multiply the two expressions.
3. Every term except `r₁×r₂` contains a factor of `m`.
4. Such terms leave remainder zero when divided by `m`.

```text
(a × b) % m = (r₁ × r₂) % m
```

This explains **why reducing the inputs first is mathematically valid**. It does not guarantee the product fits the C++ type—see §17.4.

**Dry run under mod 7:**

`a=16`, `b=12`, residues are 2 and 5.

| Operation | Direct | Residues first | Answer |
|---|---|---|---|
| Add | `28%7` | `(2+5)%7` | 0 |
| Subtract | `4%7` | `(2-5+7)%7` | 4 |
| Multiply | `192%7` | `(2×5)%7` | 3 |

**⚠️ Important:** A problem sets the modulus. `1,000,000,007` is common, **not universal**. Read the statement before choosing `m`.

### 17.4 Safe modular operations for a common CP prime

- When `0<=a,b<mod` and `mod<=1,000,000,007`, their product fits signed 64-bit.
- **For larger moduli**, avoid direct `a*b`; use repeated doubling with overflow-safe modular addition (shown below).
- Both methods use standard C++17.

```cpp
ll addMod(ll a, ll b, ll mod) { // 0<=a,b<mod, mod<=1e9+7
    return (a + b) % mod;
}
ll mulMod(ll a, ll b, ll mod) { // 0<=a,b<mod, mod<=1e9+7
    return (a * b) % mod;
}
ll subMod(ll a, ll b, ll mod) { // 0<=a,b<mod, mod<=1e9+7
    return (a - b + mod) % mod;
}
```

**Only when the modulus can be as large as `LLONG_MAX`:** the following optional helper adds two normalized residues *without ever forming* `a+b` if it would be too large.

- Repeated doubling then multiplies safely in `O(log b)` time:

```cpp
ll addModLarge(ll a, ll b, ll mod) { // 0<=a,b<mod, mod>0
    return a >= mod - b ? a - (mod - b) : a + b;
}
ll mulModLarge(ll a, ll b, ll mod) { // 0<=a,b<mod, mod>0
    ll answer = 0;
    while (b > 0) {
        if (b % 2 == 1) answer = addModLarge(answer, a, mod);
        b /= 2;
        if (b > 0) a = addModLarge(a, a, mod);
    }
    return answer;
}
```

**Small dry run:** `mulModLarge(6, 5, 7)`:

| `b` | `a` | Odd? | `answer` after step |
|---:|---:|---|---:|
| 5 | 6 | yes | 6 |
| 2 | 5 (`6+6 mod 7`) | no | 6 |
| 1 | 3 (`5+5 mod 7`) | yes | **2** (`6+3 mod 7`) |

This gives `6×5 mod 7 = 2`. For normal CP moduli such as `1e9+7`, use the shorter `mulMod` function above.

### 17.5 Fast modular exponentiation: never construct the huge power

**Problem:** Compute `3^13 mod 7`. Creating `3^13` works for this tiny example, but not for exponent `10^18`.

Use the squaring pattern from §2 and take `%mod` after multiplication:

| Before exponent | Base mod 7 | Multiply answer? | Answer mod 7 | Next base mod 7 |
|---:|---:|---|---:|---:|
| 13 | 3 | yes | 3 | 2 (`3²%7`) |
| 6 | 2 | no | 3 | 4 (`2²%7`) |
| 3 | 4 | yes | 5 (`3×4%7`) | 2 (`4²%7`) |
| 1 | 2 | yes | **3** (`5×2%7`) | — |

So `3^13 mod 7 = 3`.

```cpp
ll modPower(ll base, ll exponent, ll mod) {
    // Preconditions: exponent >= 0, 1 <= mod <= 1'000'000'007
    base = normalizeMod(base, mod);
    ll answer = 1 % mod;
    while (exponent > 0) {
        if (exponent % 2 == 1) answer = answer * base % mod;
        exponent /= 2;
        if (exponent > 0) base = base * base % mod;
    }
    return answer;
}
```

**⏱️ Complexity:** `O(log exponent)` time, `O(1)` extra space. `mod=1` correctly gives 0.

### 17.6 Modular **division** is not integer division

**Problem:** Find `7 / 3 (mod 11)`.

Ordinary integers cannot simply divide remainders: `(7/3)%11` gives `2` in C++, but this is not modular division.

To divide by `b` modulo `m`, find `inv(b)` such that:

`b * inv(b) ≡ 1 (mod m)`.

Then `a / b (mod m)` means **`a * inv(b) (mod m)`**.

Try the small value `b=3`, `m=11`: `3×4=12≡1 (mod 11)`. Thus `inv(3)=4`.

So `7/3 mod 11 = 7×4 mod 11 = 28%11 = 6`.

**Verification:** `3×6=18≡7 (mod 11)` → answer correct.

### 17.7 When does an inverse exist?

`b` has a multiplicative inverse modulo `m` **if and only if** `gcd(b,m)=1`.

- `3` modulo `11`: GCD=1 → inverse exists.
- `2` modulo `6`: GCD=2 → **no inverse**; don't use modular division.

For a **prime modulus** `p` and `b%p != 0`, Fermat's little theorem says:

`b^(p-1) ≡ 1 (mod p)`.

Write `b^(p-1) = b × b^(p-2)`, so:

`b × b^(p-2) ≡ 1 (mod p)`.

Therefore the inverse is **`b^(p-2) mod p`**.

```cpp
ll inversePrimeMod(ll b, ll prime) {
    // Preconditions: prime is prime, prime>=2, b % prime != 0,
    // and prime <= 1'000'000'007 for this modPower implementation.
    return modPower(b, prime - 2, prime);
}
ll dividePrimeMod(ll a, ll b, ll prime) {
    return normalizeMod(a, prime) * inversePrimeMod(b, prime) % prime;
}
```

**Dry run `inv(3) mod 7`:** Need `3^(7-2)=3^5 mod 7`; `3^5=243`; `243%7=5`. Check `3×5=15≡1 (mod 7)`.

For a **nonprime modulus** with `gcd(b,m)=1`, an inverse still exists, but Fermat's `b^(m-2)` formula is generally **wrong**. Extended Euclid is a standard general method.

### 17.8 Super power / exponent represented by digits

The source sketches multiplying the contributions of digits for a large exponent. If exponent `E` is so large it arrives as digits `d₁...dₖ`, process digits left to right:

- Prefix exponent `E_old` becomes `10×E_old + digit`.
- Thus `a^(10E_old+digit) = (a^E_old)^10 × a^digit`.
- Reduce every step modulo `m`.

```cpp
ll powerFromDecimalDigits(ll base, const vector<int>& digits, ll mod) {
    ll answer = 1 % mod;
    for (int digit : digits) {
        answer = modPower(answer, 10, mod) * modPower(base, digit, mod) % mod;
    }
    return answer;
}
```

**🧠 Recognize:** very large exponent, answer modulo `m`, permutations/combinations modulo a prime, repeated products, handling negative remainders.

**✍️ Check yourself:** Compute `2^10 mod 7`. Is dividing by 2 modulo 6 always valid? Why not?


[↑ Contents](#contents)

---

## 18. Coordinate and grid geometry

*Source: pp.56–59; rotation is a supplement because p.59 labels it TODO.*

> 🧭 **Concept map:** `Coordinates → distances → area → slopes → grid geometry`
>
> 🎯 **Guiding question:** Can the geometry be expressed through integer differences?

### 18.1 Prerequisites — coordinates, axes and quadrants

A point `(x,y)` tells us how far to move from the origin `(0,0)`:

- Positive `x`: right; negative `x`: left.
- Positive `y`: up; negative `y`: down.

```text
               y
               ↑
       QII     |      QI
     (-x,+y)   |    (+x,+y)
  ─────────────0────────────→ x
       QIII    |      QIV
     (-x,-y)   |    (+x,-y)
               |
```

**🧪 Dry run:** `(3,-2)` is 3 units right, 2 units down → quadrant IV. A point on an axis is not in any of the four open quadrants.

### 18.2 Euclidean distance — derive using Pythagoras

**Problem:** Find straight-line distance from `A=(1,2)` to `B=(4,6)`.

1. Horizontal displacement `dx = 4-1 = 3`.
2. Vertical displacement `dy = 6-2 = 4`.
3. These are perpendicular sides of a right triangle.
4. By Pythagoras: `distance² = dx² + dy² = 9+16=25`.
5. Therefore **distance = `sqrt(25)=5`**.

General formula:

**`distance(A,B) = sqrt((x₂-x₁)²+(y₂-y₁)²)`**.

```cpp
long double euclideanDistance(ll x1, ll y1, ll x2, ll y2) {
    long double dx = (long double)x2 - x1;
    long double dy = (long double)y2 - y1;
    return sqrtl(dx * dx + dy * dy);
}
```

**When to avoid floating point:** If comparing squared distances, compare `dx²+dy²` as integers **when the constraints guarantee it fits**.

- For example, when each coordinate lies in `[-10^9,10^9]`, `|dx|,|dy| <= 2×10^9`, so the sum of squares is at most `8×10^18`, which fits `long long`.
- For larger differences, this calculation could overflow; use a problem-appropriate exact method rather than silently overflowing.
- Taking square roots can introduce precision issues.

```cpp
ll squaredDistance(ll x1, ll y1, ll x2, ll y2) {
    // Only when dx*dx + dy*dy fits long long.
    ll dx = x2 - x1, dy = y2 - y1;
    return dx * dx + dy * dy;
}
```

### 18.3 Manhattan distance — movement on a grid

If allowed to move only up/down/left/right, you cannot take the diagonal shortcut. You must make `abs(x₂-x₁)` horizontal moves and `abs(y₂-y₁)` vertical moves.

**`Manhattan(A,B) = |x₂-x₁| + |y₂-y₁|`**.

**🧪 Dry run:** `(1,2)→(4,6)` requires 3 right moves + 4 up moves = **7 moves**.

```text
A=(1,2) ──→──→──→ (4,2)
                    |
                    ↑ 4 steps
                    |
                  B=(4,6)
```

A path can weave, but any shortest path still needs exactly 3 horizontal and 4 vertical changes.

```cpp
ll manhattanDistance(ll x1, ll y1, ll x2, ll y2) {
    return llabs(x2 - x1) + llabs(y2 - y1); // assume differences/sum fit ll
}
```

**🧠 Recognize:** robots on grids, chessboard-like movement (with specified moves), shortest path without obstacles using four directions.

### 18.4 Triangle validity — three inequalities

Three lengths `a,b,c` form a **nondegenerate** triangle precisely when:

- `a+b>c`
- `a+c>b`
- `b+c>a`

**Why strict?** If `a+b=c`, the sides lie on a straight line: they do not enclose a triangle of positive area.

If the sides are sorted `a<=b<=c`, the first two inequalities are automatically satisfied for positive lengths; only **`a+b>c`** needs checking.

**🧪 Dry run:** `[2,3,6]`: `2+3=5`, which is not greater than 6 → invalid. `[3,4,5]`: `3+4=7>5` → valid.

```cpp
bool validTriangle(ll a, ll b, ll c) {
    if (a <= 0 || b <= 0 || c <= 0) return false;
    array<ll, 3> sides = {a, b, c};
    sort(sides.begin(), sides.end());
    // smallest + middle > largest, rewritten without addition.
    return sides[0] > sides[2] - sides[1];
}
```

### 18.5 Rectangle, triangle and circle formulas

| Figure | Area | Perimeter/circumference |
|---|---|---|
| Rectangle width `w`, height `h` | `w*h` | `2(w+h)` |
| Triangle base `b`, height `h` | `b*h/2` | `a+b+c` |
| Circle radius `r` | `πr²` | `2πr` |

**Axis-aligned rectangle from two opposite corners:** `width=abs(x₂-x₁)`, `height=abs(y₂-y₁)`, so area = width × height. This differs from the area of a tilted rectangle.

**🧪 Dry run:** corners `(2,1)` and `(7,4)` → width=5, height=3 → **area=15**, perimeter=16.

For Euclidean plane problems, do not round area or distance unless output requirements instruct it.

### 18.6 Slope and collinearity — prefer integer cross product

Slope of a nonvertical line through two different points is `(y₂-y₁)/(x₂-x₁)`. Division fails for a vertical line (`x₂=x₁`) and floating-point slopes can be inexact.

**Better test for three collinear points A,B,C:** two displacement vectors are parallel iff their 2D cross product is zero:

**`(x₂-x₁)(y₃-y₁) - (y₂-y₁)(x₃-x₁) = 0`**.

**Derive the integer test step by step**

1. Vector `AB` is `(x₂-x₁, y₂-y₁)`.
2. Vector `AC` is `(x₃-x₁, y₃-y₁)`.
3. Parallel vectors have a zero 2D cross product.
4. Compute this expression using integers:

```text
(x₂-x₁) × (y₃-y₁) - (y₂-y₁) × (x₃-x₁)
```

5. **Zero → collinear.** Nonzero → not collinear.

Unlike dividing slopes, this also works for vertical lines (subject to overflow limits).

**🧪 Dry run:** A=(1,2), B=(2,4), C=(3,6): `1×4-2×2=0` → collinear.

```cpp
struct Point { ll x, y; };

bool collinear(Point a, Point b, Point c) {
    // Assumption: all coordinates are in [-1e9, 1e9].
    ll dx1 = b.x - a.x, dy1 = b.y - a.y;
    ll dx2 = c.x - a.x, dy2 = c.y - a.y;
    return dx1 * dy2 == dy1 * dx2;
}
```

**Why the bound matters:** Under `|coordinate| <= 10^9`, each difference has absolute value at most `2×10^9`.

- Each product therefore has absolute value at most `4×10^18`, which fits signed `long long`.
- Comparing the two products directly avoids subtracting potentially large terms.
- If the problem allows much larger coordinates, this short version is **not** safe: verify bounds or use an exact big-integer technique instead.

### 18.7 Midpoint — average the coordinates

The midpoint of `(x₁,y₁)` and `(x₂,y₂)` is:

**`((x₁+x₂)/2, (y₁+y₂)/2)`**.

**🔎 Why?** Halfway in the x-direction is `x₁+(x₂-x₁)/2=(x₁+x₂)/2`; same for y.

**🧪 Dry run:** midpoint of `(2,3)` and `(8,7)` → `((2+8)/2,(3+7)/2)=(5,5)`.

For integer coordinates, a midpoint may be fractional: `(0,0)` and `(1,1)` → `(0.5,0.5)`. Don't accidentally use integer division.

### 18.8 Grid area vs grid perimeter

For a shape made from filled unit cells:

- **Area:** count filled cells.
- **Perimeter:** count only cell sides exposed to empty space or outside the grid.

**🔎 Example:** two adjacent unit cells:

```text
+---+---+
| # | # |
+---+---+
```

Area = 2. Each cell has four sides → 8 initially, but the shared edge lies inside; remove **2** side contributions → perimeter **6**.

**General formula:** `perimeter = 4×numberOfFilledCells - 2×numberOfSharedEdges`.

```cpp
int gridPerimeter(const vector<string>& g) { // '#' filled, '.' empty
    int rows = (int)g.size();
    if (rows == 0) return 0;
    int cols = (int)g[0].size();
    int perimeter = 0;
    const int dr[4] = {-1, 1, 0, 0};
    const int dc[4] = {0, 0, -1, 1};
    for (int r = 0; r < rows; ++r) {
        for (int c = 0; c < cols; ++c) {
            if (g[r][c] != '#') continue;
            for (int k = 0; k < 4; ++k) {
                int nr = r + dr[k], nc = c + dc[k];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols
                    || g[nr][nc] != '#') ++perimeter;
            }
        }
    }
    return perimeter;
}
```

**⏱️ Complexity:** `O(rows×cols)` time and `O(1)` auxiliary space.

### 18.9 [SUPPLEMENT] Rotations by 90° around the origin

*Page 59 has “Rotation — TODO”; formulas below are newly added.*

- **90° counterclockwise:** `(x,y) → (-y,x)`.
- **90° clockwise:** `(x,y) → (y,-x)`.
- **180°:** `(x,y) → (-x,-y)`.

**Discover 90° CCW:** the unit vector `(1,0)` along positive x goes to `(0,1)` along positive y, while `(0,1)` goes to `(-1,0)`. Combining contributions gives `(-y,x)`.

**🧪 Dry run:** `(2,3)` → CCW `(-3,2)`; clockwise `(3,-2)`; 180° `(-2,-3)`.

**Note:** Rotating around a point `P` instead of origin requires: subtract P, rotate, then add P back.

**🧠 Recognize:** coordinate movements, grid geometry, validity of shapes, distances, polygon orientation, rotations.


[↑ Contents](#contents)

---

## 19. Complexity and the final learning checklist

*Source: p.60 contains only the “Big O notation” heading; this is a SUPPLEMENT teaching section.*

> 🧭 **Concept map:** `Input constraints → operation count → Big-O → feasibility`
>
> 🎯 **Guiding question:** Will the algorithm finish fast enough for the input size?

### 19.1 Prerequisites — time vs space, input size `n`

**Time complexity** describes how the amount of work grows as the input gets larger. **Space complexity** describes extra memory used, often excluding the input itself.

- Big-O is an **upper bound on growth rate**, not a stopwatch measurement or an exact number of operations.
- Ignore constant factors and lower-order terms when expressing the main growth.

Example: if an algorithm performs `3n+10` primitive actions, we call it `O(n)`, because for large `n` the linear part dominates.

### 19.2 Recognize common growth rates by code shape

| Growth | Example | Why |
|---|---|---|
| `O(1)` | array element `a[i]` | fixed work |
| `O(log n)` | binary search / halve n | n shrinks by a factor |
| `O(n)` | one pass over array | each item visited once |
| `O(n log n)` | efficient comparison sorting | roughly `log n` levels × n work |
| `O(n²)` | two full nested loops | n choices for i and n for j |
| `O(n³)` | three full nested loops | n×n×n |
| `O(2^n)` | generate all subsets | each element in/out |
| `O(n!)` | generate all permutations | n×(n−1)×...×1 |

**Same result, different costs:** To answer a sum over many intervals, scanning each interval may take `O(n)` *per query*.

- Building prefix sums takes `O(n)` once and `O(1)` per query.
- Choosing the math representation changes the algorithm.

### 19.3 A loop doesn't necessarily mean linear time

```cpp
// O(log n) because i doubles each time.
for (long long i = 1; i <= n; i *= 2) {
    // constant work
}
```

**Dry run `n=20`:** `i=1,2,4,8,16`, then 32 stops → only 5 iterations.

Compare:

```cpp
// O(n)
for (int i = 0; i < n; ++i) { /* constant work */ }

// O(n^2)
for (int i = 0; i < n; ++i)
    for (int j = 0; j < n; ++j) { /* constant work */ }
```

### 19.4 How to estimate feasibility using constraints

Suppose a task has `n=100,000`:

- `O(n)` = about 100,000 iterations: typically suitable.
- `O(n log₂n)` ≈ 1.7 million units of work: often suitable.
- `O(n²)` ≈ 10 billion pair-iterations: usually far too much.

These are **rough operation scales**, not guarantees. Actual feasibility depends on the language, time limit and what each iteration does.

For `n=20`, `2^n≈1,048,576` masks, often feasible; for `n=50`, `2^n` is astronomically large for enumeration.

### 19.5 Complexity quick-check questions

1. Does the number shrink/double by a constant factor? → think **logarithmic**.
2. Does a loop touch each element once? → think **linear**.
3. Are all pairs examined? → think **quadratic**.
4. Are all subsets/permutations explicitly generated? → think **exponential/factorial**.
5. Do I need many repeated range totals or repeated primality tests? → consider **precomputation**.


[↑ Contents](#contents)

---

## 20. Mastery workflow — turn the notes into working knowledge

**Do not memorize the whole document in one pass.** Use short cycles:

1. **Read** one major topic (for instance GCD/LCM).
2. **Re-derive** its formula using a 2–5 item example without looking.
3. **Dry run** the code and explain every update.
4. **Implement** one function from memory.
5. **Apply** the idea to 2–3 representative problems, then one mixed/unseen problem.
6. **Record the observation** that allowed you to recognize the technique.
7. Review only the concepts you failed after one day and one week.

### Study checklist (mark only when you can explain and code)

- [ ] Quotient/remainder, ceil groups, cycles, digital root
- [ ] Exponent laws, roots, binary exponentiation
- [ ] Logarithm rules, repeated halving, bit/decimal digit count
- [ ] Linear equations, inequalities, quadratic discriminant, basic sums
- [ ] Arithmetic and geometric sequences, nth terms and sums
- [ ] Sigma properties, telescoping, nested loop counts, prefix sums
- [ ] Decimal/binary/hex conversion and two's complement intuition
- [ ] AND/OR/XOR, shifts, bit operations, popcount, subset masks
- [ ] Boolean operators, truth tables, De Morgan's laws
- [ ] Union, intersection, difference, subset and distinctness
- [ ] Add vs multiply, complement counting, pigeonhole, IEP
- [ ] Factorial, permutations, repeats, multisets, circles, derangements
- [ ] Combinations, Pascal triangle, repeated choice, Stars and Bars
- [ ] Probability, favorable/total, complement
- [ ] Divisors, trial primality, sieve, prime factorization
- [ ] GCD/LCM, Euclid, array GCD, synchronization and uniform partitioning
- [ ] Modulo normalization, fast power, inverse and modular division
- [ ] Distance, triangle condition, areas, collinearity, grid perimeter
- [ ] Time/space complexity and matching algorithms to constraints

### 20-question active-recall set

Try these without looking at earlier sections. Write the math model, solve, then state which technique you recognized.

| # | Question | Primary topic |
|---:|---|---|
| 1 | Place 53 students in vans of capacity 8. Minimum vans? | ceil division |
| 2 | Today is Friday. What day after 100 days? | modulo cycle |
| 3 | Express `2^13` as a product of powers whose exponents are powers of 2. | binary exponentiation |
| 4 | How many bits does positive integer 31 need? What about 32? | logarithms/binary |
| 5 | Solve `x+y=17` and `x-y=5`. | algebra |
| 6 | Find the 10th term of `4,7,10,...`. | AP |
| 7 | Sum `2+4+8+16+32`. | GP |
| 8 | Array `[4,1,7,2]`: sum at zero-based indices 1 through 3 via prefix. | prefix sums |
| 9 | Convert decimal 26 to binary and hexadecimal. | number bases |
| 10 | List subsets represented by mask `101₂` of `{A,B,C}`. | bitmask |
| 11 | Simplify `!(x<3 \|\| x>7)`. | Boolean logic |
| 12 | How many values in `A={1,2,4}`, `B={2,3,4}`'s union? | sets |
| 13 | How many integers in `1..60` are divisible by 2 or 5? | IEP |
| 14 | How many distinct rearrangements of `AABC`? | multiset perm. |
| 15 | How many 3-person teams can be chosen from 6 people? | combinations |
| 16 | Solve `x+y+z=6` for nonnegative integer triples. | Stars and Bars |
| 17 | Probability of getting at least one head in two fair flips? | complement |
| 18 | Find divisors of 20 and determine if 29 is prime. | divisibility/primes |
| 19 | Compute `gcd(24,36)`, `lcm(24,36)` and `3^5 mod 7`. | GCD/LCM/mod |
| 20 | Distance by grid moves from `(−2,1)` to `(3,4)`. | Manhattan |

<details>
<summary><strong>Answer key — open after attempting</strong></summary>

| # | Answer and quick reasoning |
|---:|---|
| 1 | `53/8=6` remainder 5 → **7 vans**. |
| 2 | `100%7=2`: Friday → Sunday. |
| 3 | `13=8+4+1` → `2^13=2^8×2^4×2^1`. |
| 4 | 31=`11111₂` → **5 bits**; 32=`100000₂` → **6 bits**. |
| 5 | Add equations: `2x=22` → **x=11, y=6**. |
| 6 | `4+(10-1)×3=31`. |
| 7 | `2×(2^5-1)=62`. |
| 8 | Prefix `[0,4,5,12,14]`; `prefix[4]-prefix[1]=14-4=10`. |
| 9 | 26=`11010₂`=`1A₁₆`. |
| 10 | Bit 0=A, bit 2=C → **{A,C}**. |
| 11 | `x>=3 && x<=7`. |
| 12 | `{1,2,3,4}` → **4**. |
| 13 | `60/2 + 60/5 - 60/10 = 30+12-6 = 36`. |
| 14 | `4!/2!=12`. |
| 15 | `C(6,3)=20`. |
| 16 | `C(6+3-1,3-1)=C(8,2)=28`. |
| 17 | `1-P(TT)=1-1/4=3/4`. |
| 18 | `{1,2,4,5,10,20}`; 29 is prime. |
| 19 | GCD **12**, LCM **72**; `3^5=243`, `243%7=5`. |
| 20 | `|3-(-2)|+|4-1|=5+3=8`. |

</details>

### Final pattern-recognition sheet

| Statement clue | Ask yourself | Likely tool |
|---|---|---|
| “Minimum containers/moves of size k” | Full groups + leftover? | Ceil division |
| “After a huge number of turns” | What is the repeat period? | Modulo |
| “Raise to a very large power” | Can exponent be halved? | Binary exponentiation |
| “Range sum, many queries” | Can I precompute cumulative totals? | Prefix sums |
| “Number of valid arrangements” | Does order matter? Are repeats allowed? | Permutation / combination |
| “At least one...” | Is the opposite easier? | Complement counting |
| “Divisible by a or b” | Am I double-counting both? | Inclusion–exclusion |
| “Largest equal pieces” | What divides every length? | GCD |
| “First time cycles align” | What is a shared multiple? | LCM |
| “Divide under mod” | Does denominator have an inverse? | Modular inverse |
| “Walk on a grid, four directions” | Must I change x and y separately? | Manhattan distance |
| “All subsets of n objects” | Could I use in/out bits? | Bitmask, `2^n` |

---

### Source and accuracy notes

- **Primary source:** *Repovive CP Maths.pdf*, 60-page handwritten reference supplied with this request. Topic order is preserved, with nearby source concepts grouped into chapters.
- **Instruction added:** explanations, proofs, edge cases, practice, C++17 code, recognition cues and the active-recall checklist are expanded for independent learning; they are not verbatim transcriptions.
- **Supplement sections:** Binomial theorem p.40; Stars and Bars p.41; rotations p.59; Big-O p.60. These were labeled as TODO or left without lessons in the handout.
- **Convention clarifications:** C++ negative `%` differs from canonical modulo; modular division requires an inverse; exact integer math needs overflow care; ordinary combinations differ from arrangements; square root is not an exact-integer method by itself.
- **Suggested next practice:** After finishing each chapter, solve 2–3 form-wise problems, then one unseen mixed problem; note the observation, not just the formula.

