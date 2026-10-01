# Introduction to Mathematics in Programming

> **Goal:** Build the mathematical toolkit needed to recognize patterns,
> model problems, derive formulas, and write efficient solutions in
> **DSA** and **Competitive Programming (CP)**.

------------------------------------------------------------------------

## Table of Contents

1.  [Big Picture](#1-big-picture)
2.  [Why Mathematics Matters](#2-why-mathematics-matters)
3.  [Level 1 --- Core Foundation](#3-level-1--core-foundation)
4.  [Level 2 --- Intermediate Toolkit](#4-level-2--intermediate-toolkit)
5.  [Level 3 --- Advanced CP
    Mathematics](#5-level-3--advanced-cp-mathematics)
6.  [How Math Appears in Problems](#6-how-math-appears-in-problems)
7.  [Recommended Learning Order](#7-recommended-learning-order)
8.  [Recognition Cheat Sheet](#8-recognition-cheat-sheet)

------------------------------------------------------------------------

# 1. Big Picture

Mathematics in DSA/CP is not mainly about memorizing formulas.

The important skill is:

``` text
Problem Story
     ↓
Remove story nouns
     ↓
Define variables
     ↓
Find mathematical relation
     ↓
Simplify / transform
     ↓
Choose algorithm
     ↓
Implement
```

The main mathematical branches used in programming are:

``` text
                         MATH IN DSA & CP
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
   Number Theory          Combinatorics         Modular Arithmetic
   gcd / lcm / primes     nCr / nPr / counting  remainder / inverse
          │                     │                     │
          └──────────────┬──────┴──────────────┬──────┘
                         │                     │
                    Probability           Geometry
                    expectation           points / lines
                    variance              distance / area
                         │                     │
                         └──────────┬──────────┘
                                    │
                           Advanced Techniques
                       matrices / game theory / FFT
```

------------------------------------------------------------------------

# 2. Why Mathematics Matters

Math helps convert a slow or complicated idea into a simpler model.

### Example --- repeated addition

Suppose we need:

``` text
7 + 7 + 7 + 7 + 7
```

Instead of five additions:

``` text
5 × 7 = 35
```

This is the core idea behind mathematical optimization:

``` text
Repeated work
     ↓
Discover structure
     ↓
Formula / property
     ↓
Faster algorithm
```

In CP, the same idea appears in:

-   prefix sums instead of repeatedly calculating ranges,
-   formulas instead of simulation,
-   GCD instead of testing every divisor,
-   fast exponentiation instead of multiplying `a` exactly `b` times,
-   combinatorics instead of generating every arrangement.

------------------------------------------------------------------------

# 3. Level 1 --- Core Foundation

These topics should become very comfortable before advanced mathematics.

## 3.1 Arithmetic & Algebra

### Know

-   fractions and ratios
-   powers and roots
-   logarithm basics
-   equations and inequalities
-   floor / ceil
-   absolute value
-   min / max
-   algebraic rearrangement

### CP example

Given:

``` text
a[j] - a[i] = j - i
```

Group terms belonging to the same index:

``` text
a[j] - j = a[i] - i
```

Define:

``` text
key[i] = a[i] - i
```

Now the pair condition becomes:

``` text
key[j] = key[i]
```

So an algebra problem can become a **frequency/hash-map problem**.

------------------------------------------------------------------------

## 3.2 Number Theory

Number theory studies properties of integers.

### Core topics

``` text
Divisibility
    │
    ├── GCD
    ├── LCM
    ├── Prime Numbers
    ├── Prime Factorization
    └── Divisors
```

### Essential identity

``` text
gcd(a,b) × lcm(a,b) = a × b
```

Therefore:

``` cpp
lcm = a / gcd(a,b) * b;
```

Dividing first helps reduce overflow risk.

### Common recognition clues

If a problem says:

-   "divisible by"
-   "common divisor"
-   "make all numbers equal using factors"
-   "prime factors"
-   "periods coincide"

think about **GCD, LCM, divisibility, or prime factorization**.

------------------------------------------------------------------------

## 3.3 Modular Arithmetic

Modulo means working with remainders.

``` text
17 % 5 = 2
```

Think of a clock:

``` text
       0
   11      1
 10          2
 9            3
  8          4
    7      5
       6
```

After reaching the modulus, values wrap around.

### Core rules

``` text
(a + b) % MOD
= ((a % MOD) + (b % MOD)) % MOD

(a × b) % MOD
= ((a % MOD) × (b % MOD)) % MOD
```

Subtraction needs care:

``` cpp
(a - b + MOD) % MOD
```

### Learn later

-   binary exponentiation
-   modular inverse
-   Fermat's Little Theorem
-   Euler's Totient Function

------------------------------------------------------------------------

## 3.4 Combinatorics

Combinatorics answers:

> **How many ways can something happen?**

### Permutation --- order matters

Choose and arrange `r` objects from `n`:

``` text
nPr = n! / (n-r)!
```

Example:

``` text
ABC ≠ BAC
```

### Combination --- order does not matter

Choose `r` objects from `n`:

``` text
nCr = n! / (r!(n-r)!)
```

Example:

``` text
{A,B} = {B,A}
```

### Decision diagram

``` text
Selecting objects?
      │
      ├── Does order matter? ── YES ──> Permutation
      │
      └── NO ─────────────────────────> Combination
```

Other important ideas:

-   multiplication principle
-   pigeonhole principle
-   inclusion-exclusion
-   binomial coefficients
-   Pascal's triangle

------------------------------------------------------------------------

## 3.5 Bit Manipulation

Computers store integers in binary.

``` text
13 = 1101₂
```

### Core operators

| Operation | C++ | Typical use |
| :--- | :---: | :--- |
| AND | `&` | Test / clear bits |
| OR | `\|` | Set bits |
| XOR | `^` | Toggle / cancellation |
| Left shift | `<<` | Multiply by powers of 2 |
| Right shift | `>>` | Divide by powers of 2 |

### Example --- test bit `k`

``` cpp
if (x & (1LL << k)) {
    // bit k is ON
}
```

### Important CP uses

``` text
Bit Manipulation
      ├── subsets / bitmasks
      ├── parity
      ├── powers of two
      ├── XOR properties
      └── state compression
```

------------------------------------------------------------------------

## 3.6 Basic Probability & Statistics

### Probability

``` text
P(event) = favorable outcomes / total outcomes
```

Example: fair die, probability of an even number:

``` text
3 / 6 = 1 / 2
```

### Expected value

If outcome `xᵢ` occurs with probability `pᵢ`:

``` text
E[X] = Σ xᵢpᵢ
```

### Statistics basics

Know:

-   mean
-   median
-   frequency
-   variance
-   expected value

These appear in randomized problems, games, probability DP, and
stream/statistics problems.

------------------------------------------------------------------------

## 3.7 Geometry Basics

Start with **coordinate geometry**, not advanced theorem memorization.

For two points:

``` text
A(x1,y1)
B(x2,y2)
```

Distance:

``` text
d = √((x2-x1)² + (y2-y1)²)
```

### Useful topics

-   points and vectors
-   distance
-   slope
-   orientation / cross product
-   line intersection
-   triangle area
-   circles

``` text
             B(x2,y2)
              *
             /|
            / |
           /  | Δy
          /   |
         *----*
      A(x1,y1)  Δx
```

------------------------------------------------------------------------

# 4. Level 2 --- Intermediate Toolkit

After the foundation, add these topics.

## 4.1 Recurrences & Mathematical Induction

A recurrence defines a value using previous values.

Example:

``` text
F(n) = F(n-1) + F(n-2)
```

This connects naturally to:

-   recursion,
-   dynamic programming,
-   matrix exponentiation.

Induction is useful for **proving that an algorithm or formula works**.

------------------------------------------------------------------------

## 4.2 Inclusion--Exclusion

When two counted groups overlap:

``` text
|A ∪ B| = |A| + |B| - |A ∩ B|
```

Diagram:

``` text
       _______     _______
      /       \___/       \
     /    A   / X \   B    \
     \       \___/         /
      \_______/ \_________/

X = counted in both
```

So subtract the overlap once.

------------------------------------------------------------------------

## 4.3 Fast Exponentiation

Computing:

``` text
a^n
```

with `n` multiplications is `O(n)`.

Binary exponentiation repeatedly halves the exponent:

``` text
a^13
13 = 1101₂
```

Complexity:

``` text
O(log n)
```

This is fundamental for modular arithmetic and matrix exponentiation.

------------------------------------------------------------------------

## 4.4 Matrix Exponentiation

Useful when a recurrence can be represented as a matrix transition.

For Fibonacci:

``` text
| F(n+1) |   |1 1| | F(n)   |
| F(n)   | = |1 0| | F(n-1) |
```

Then:

``` text
matrix^n
```

can be computed with binary exponentiation in roughly:

``` text
O(k³ log n)
```

for a `k × k` matrix using standard multiplication.

------------------------------------------------------------------------

## 4.5 Discrete Mathematics

Important areas:

-   sets
-   relations
-   logic
-   Boolean algebra
-   counting principles
-   inclusion-exclusion
-   graph concepts

This is the mathematical language behind many DSA problems.

------------------------------------------------------------------------

# 5. Level 3 --- Advanced CP Mathematics

These are useful after the core toolkit is strong.

## 5.1 Advanced Number Theory

Study:

``` text
Sieve of Eratosthenes
Prime factorization
Euler's Totient φ(n)
Fermat's Little Theorem
Modular inverse
Extended Euclidean Algorithm
Chinese Remainder Theorem
```

------------------------------------------------------------------------

## 5.2 Advanced Combinatorics

Useful topics include:

-   Catalan numbers
-   stars and bars
-   derangements
-   Stirling numbers
-   generating functions

Do not prioritize all of these before mastering basic counting and
`nCr`.

------------------------------------------------------------------------

## 5.3 Game Theory

A common CP model:

``` text
Current State
     ↓
Possible Moves
     ↓
Winning / Losing State
```

Core topics:

-   Nim
-   XOR strategy
-   Grundy numbers
-   Sprague--Grundy theorem

------------------------------------------------------------------------

## 5.4 FFT / Polynomial Techniques

FFT can multiply large polynomials efficiently.

Naive multiplication:

``` text
O(n²)
```

FFT-based multiplication:

``` text
O(n log n)
```

This is an advanced topic. Learn it only when your contest level
requires it.

------------------------------------------------------------------------

# 6. How Math Appears in Problems

The statement rarely says:

> "Use number theory."

Instead, recognize the hidden mathematical structure.

| Problem clue | Think about |
| :--- | :--- |
| Divisible / remainder | Modulo |
| Common divisor | GCD |
| Repeating cycles meet | LCM |
| Prime factors | Sieve / factorization |
| Choose `k` objects | Combinations |
| Arrangements | Permutations |
| All subsets | Bitmask / powers of 2 |
| Huge exponent | Binary exponentiation |
| Repeated recurrence | DP / matrix exponentiation |
| Random outcome | Probability / expectation |
| Points / distance / area | Geometry |
| Winning moves | Game theory |
| Overlapping groups | Inclusion-exclusion |

The key contest habit is:

``` text
WORDS
  ↓
VARIABLES
  ↓
RELATION
  ↓
PROPERTY / FORMULA
  ↓
ALGORITHM
```

------------------------------------------------------------------------

# 7. Recommended Learning Order

For DSA + CP, a practical order is:

``` text
1. Arithmetic + Algebra
          ↓
2. GCD / LCM / Divisibility
          ↓
3. Prime Numbers + Factorization
          ↓
4. Modular Arithmetic
          ↓
5. Binary Exponentiation
          ↓
6. Combinatorics
          ↓
7. Bit Manipulation
          ↓
8. Probability + Expectation
          ↓
9. Coordinate Geometry
          ↓
10. Inclusion–Exclusion
          ↓
11. Advanced Number Theory
          ↓
12. Matrix Exponentiation
          ↓
13. Game Theory
          ↓
14. FFT / Advanced Combinatorics
```

For each topic, use the same cycle:

``` text
Learn concept
    ↓
Derive formula
    ↓
Dry run
    ↓
Implement
    ↓
Solve 3–5 pattern-wise problems
    ↓
Mixed practice
    ↓
Contest recognition
```

------------------------------------------------------------------------

# 8. Recognition Cheat Sheet

Before coding a mathematical problem, ask:

``` text
1. What exactly must I calculate?
2. What are the variables?
3. What equation/constraint connects them?
4. Can I rearrange or simplify it?
5. Is divisibility/parity/modulo involved?
6. Is this counting arrangements or selections?
7. Is repeated work replaceable by a formula?
8. Is there an invariant?
9. Can preprocessing help?
10. What is the required complexity?
```

## Final Mental Model

``` text
            MATHEMATICS
                 │
      ┌──────────┴──────────┐
      │                     │
   MODELING              TOOLKIT
      │                     │
variables/equations    number theory
constraints           modulo
invariants             combinatorics
transformations        probability
      │                geometry
      └──────────┬──────────┘
                 │
          EFFICIENT ALGORITHM
                 │
              C++ CODE
```

> **Main takeaway:** For DSA and CP, mathematics is primarily a
> **problem-modeling tool**. Learn the properties, but train yourself to
> recognize *when and why* each property transforms a problem into
> something easier.
