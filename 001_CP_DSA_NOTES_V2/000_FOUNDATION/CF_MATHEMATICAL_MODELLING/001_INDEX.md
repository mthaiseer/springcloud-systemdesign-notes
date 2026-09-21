# CF Mathematical Modeling Master Index

> **Purpose:** Lightweight navigation file for the Mathematical Modeling curriculum. Each major section can now be maintained as a separate small Markdown file.

> **Core workflow:** `Statement → Variables → Conditions → Simplify → Recognize Form → Algorithm → Proof → C++`

## Master Table of Contents

- [Part 0. How to Mathematically Read a Codeforces Problem](#part-0-how-to-mathematically-read-a-codeforces-problem)
  - [Translation table: statement language → mathematics](#translation-table-statement-language-mathematics)
  - [Worked reading (1 minute)](#worked-reading-1-minute)
- [Part 1. Arithmetic Foundations](#part-1-arithmetic-foundations)
  - [1.0 Reading the symbols (cheat sheet)](#10-reading-the-symbols-cheat-sheet)
  - [1.1 Quotient and remainder](#11-quotient-and-remainder)
  - [1.2 Floor, ceiling and ceil-division](#12-floor-ceiling-and-ceil-division)
  - [1.3 Absolute value, min and max](#13-absolute-value-min-and-max)
  - [1.4 Intervals and inequalities](#14-intervals-and-inequalities)
  - [1.5 Powers, logarithms and size estimates](#15-powers-logarithms-and-size-estimates)
  - [1.6 Overflow: choosing `int` or `long long`](#16-overflow-choosing-int-or-long-long)
  - [1.7 Rounding without decimals](#17-rounding-without-decimals)
  - [1.8 Section summary (what to remember)](#18-section-summary-what-to-remember)
- [Part 2. Algebra for Competitive Programming](#part-2-algebra-for-competitive-programming)
  - [Algebra Form 1. Rearranging equations (sum and difference)](#algebra-form-1-rearranging-equations-sum-and-difference)
  - [Algebra Form 2. Isolating a variable](#algebra-form-2-isolating-a-variable)
  - [Algebra Form 3. Substitution](#algebra-form-3-substitution)
  - [Algebra Form 4. Difference of squares](#algebra-form-4-difference-of-squares)
  - [Algebra Form 5. Expansions](#algebra-form-5-expansions)
  - [Algebra Form 6. Pairwise sums](#algebra-form-6-pairwise-sums)
  - [Algebra Form 7. Linear equation `ax + by = c`](#algebra-form-7-linear-equation-ax-by-c)
  - [Algebra Form 8. Systems of equations](#algebra-form-8-systems-of-equations)
  - [Algebra Form 9. Inequalities (intersection of constraints)](#algebra-form-9-inequalities-intersection-of-constraints)
  - [Algebra Form 10. Bounding (lower bound + construction)](#algebra-form-10-bounding-lower-bound-construction)
- [Part 3. Number Theory Foundations](#part-3-number-theory-foundations)
  - [3.1 Divisors and multiples](#31-divisors-and-multiples)
  - [3.2 Prime numbers and factorization](#32-prime-numbers-and-factorization)
  - [3.3 GCD and LCM](#33-gcd-and-lcm)
  - [3.4 Extra facts](#34-extra-facts)
- [Part 4. Modular Arithmetic](#part-4-modular-arithmetic)
  - [4.1 The idea: clock arithmetic](#41-the-idea-clock-arithmetic)
  - [4.2 Fast exponentiation](#42-fast-exponentiation)
  - [4.3 Division modulo a prime (inverse)](#43-division-modulo-a-prime-inverse)
  - [4.4 Prefix remainders and pigeonhole](#44-prefix-remainders-and-pigeonhole)
- [Part 5. Parity](#part-5-parity)
  - [5.1 The rules](#51-the-rules)
  - [5.2 How parity turns into a solution](#52-how-parity-turns-into-a-solution)
  - [5.3 Where it shows up in CF](#53-where-it-shows-up-in-cf)
- [Part 6. Counting & Combinatorics](#part-6-counting-combinatorics)
  - [6.1 Two basic rules](#61-two-basic-rules)
  - [6.2 Factorial, permutation, combination](#62-factorial-permutation-combination)
  - [6.3 Pairs (the most useful formula in CF)](#63-pairs-the-most-useful-formula-in-cf)
  - [6.4 Complement counting](#64-complement-counting)
  - [6.5 Stars and bars (distribute identical objects)](#65-stars-and-bars-distribute-identical-objects)
  - [6.6 Inclusion-Exclusion](#66-inclusion-exclusion)
  - [6.7 Pigeonhole and contribution](#67-pigeonhole-and-contribution)
- [Part 7. Sequences & Series](#part-7-sequences-series)
  - [7.1 Arithmetic progression (AP)](#71-arithmetic-progression-ap)
  - [7.2 Geometric progression (GP)](#72-geometric-progression-gp)
  - [7.3 Other sums to remember](#73-other-sums-to-remember)
- [Part 8. Coordinate & Distance Mathematics](#part-8-coordinate-distance-mathematics)
  - [8.1 Distance formulas](#81-distance-formulas)
  - [8.2 Interval overlap](#82-interval-overlap)
  - [8.3 Median minimizes total distance](#83-median-minimizes-total-distance)
  - [8.4 Grid movement](#84-grid-movement)
  - [8.5 Rotating coordinates](#85-rotating-coordinates)
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
- [Part 21. Geometric / Grid Modeling](#part-21-geometric-grid-modeling)
- [Part 22. Game Mathematics](#part-22-game-mathematics)
- [Part 23. Recurrences](#part-23-recurrences)
- [Part 24. Expectation / Probability Basics](#part-24-expectation-probability-basics)
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
  - [Form 17. Geometric / Doubling](#form-17-geometric-doubling)
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
  - [Form 37. Cyclic / Modulo Process](#form-37-cyclic-modulo-process)
  - [Form 38. Binary Search Equation](#form-38-binary-search-equation)
  - [Form 39. Stars and Bars](#form-39-stars-and-bars)
  - [Form 40. Diophantine Equation](#form-40-diophantine-equation)
- [Part 27. Problem Modeling Library](#part-27-problem-modeling-library)
  - [Pattern A: Parity & Formula Bounds (Lower Bound + Construction)](#pattern-a-parity-formula-bounds-lower-bound-construction)
  - [Pattern B: Invariants (Sum / GCD / Difference)](#pattern-b-invariants-sum-gcd-difference)
  - [Pattern C: Number Theory, Modulo & Diophantine Formulas](#pattern-c-number-theory-modulo-diophantine-formulas)
  - [Pattern D: Pair Conditions -> Algebra + Sorting/Frequency](#pattern-d-pair-conditions-algebra-sortingfrequency)
  - [Pattern E: Binary Search on the Answer](#pattern-e-binary-search-on-the-answer)
- [Part 28. Same Problem, Multiple Models](#part-28-same-problem-multiple-models)
- [Part 29. Constraints → Expected Mathematics](#part-29-constraints-expected-mathematics)
- [Part 30. How to Discover the Equation](#part-30-how-to-discover-the-equation)
- [Part 31. How to Discover Invariants (Operation-Delta Analysis)](#part-31-how-to-discover-invariants-operation-delta-analysis)
- [Part 32. Brute Force → Math](#part-32-brute-force-math)
- [Part 33. Mathematical Proof Toolkit](#part-33-mathematical-proof-toolkit)
- [Part 34. 60-Second Contest Modeling Checklist](#part-34-60-second-contest-modeling-checklist)
- [Part 35. Rating-Wise Modeling Expectations](#part-35-rating-wise-modeling-expectations)
- [Part 36. Master Pattern Index](#part-36-master-pattern-index)
- [Part 37. Final Mathematical Modeling Workflow](#part-37-final-mathematical-modeling-workflow)

---

## Suggested Tiny-File Split

Create one Markdown file per **Part**. Keep the listed subtopics inside that file. This keeps theory focused and makes revision/search much faster.

### Part 0. How to Mathematically Read a Codeforces Problem

- Translation table: statement language → mathematics
- Worked reading (1 minute)
### Part 1. Arithmetic Foundations

- 1.0 Reading the symbols (cheat sheet)
- 1.1 Quotient and remainder
- 1.2 Floor, ceiling and ceil-division
- 1.3 Absolute value, min and max
- 1.4 Intervals and inequalities
- 1.5 Powers, logarithms and size estimates
- 1.6 Overflow: choosing `int` or `long long`
- 1.7 Rounding without decimals
- 1.8 Section summary (what to remember)
### Part 2. Algebra for Competitive Programming

- Algebra Form 1. Rearranging equations (sum and difference)
- Algebra Form 2. Isolating a variable
- Algebra Form 3. Substitution
- Algebra Form 4. Difference of squares
- Algebra Form 5. Expansions
- Algebra Form 6. Pairwise sums
- Algebra Form 7. Linear equation `ax + by = c`
- Algebra Form 8. Systems of equations
- Algebra Form 9. Inequalities (intersection of constraints)
- Algebra Form 10. Bounding (lower bound + construction)
### Part 3. Number Theory Foundations

- 3.1 Divisors and multiples
- 3.2 Prime numbers and factorization
- 3.3 GCD and LCM
- 3.4 Extra facts
### Part 4. Modular Arithmetic

- 4.1 The idea: clock arithmetic
- 4.2 Fast exponentiation
- 4.3 Division modulo a prime (inverse)
- 4.4 Prefix remainders and pigeonhole
### Part 5. Parity

- 5.1 The rules
- 5.2 How parity turns into a solution
- 5.3 Where it shows up in CF
### Part 6. Counting & Combinatorics

- 6.1 Two basic rules
- 6.2 Factorial, permutation, combination
- 6.3 Pairs (the most useful formula in CF)
- 6.4 Complement counting
- 6.5 Stars and bars (distribute identical objects)
- 6.6 Inclusion-Exclusion
- 6.7 Pigeonhole and contribution
### Part 7. Sequences & Series

- 7.1 Arithmetic progression (AP)
- 7.2 Geometric progression (GP)
- 7.3 Other sums to remember
### Part 8. Coordinate & Distance Mathematics

- 8.1 Distance formulas
- 8.2 Interval overlap
- 8.3 Median minimizes total distance
- 8.4 Grid movement
- 8.5 Rotating coordinates
### Part 9. Min/Max Mathematical Transformations

### Part 10. Invariants

### Part 11. Operation Modeling

### Part 12. Decoupling Variables

### Part 13. Frequency Modeling

### Part 14. Sorting as a Mathematical Transformation

### Part 15. Prefix Mathematics

### Part 16. Difference Arrays

### Part 17. Bitwise Mathematical Modeling

### Part 18. Greedy Through Mathematical Proofs

### Part 19. Constructive Mathematics

### Part 20. Diophantine Modeling

### Part 21. Geometric / Grid Modeling

### Part 22. Game Mathematics

### Part 23. Recurrences

### Part 24. Expectation / Probability Basics

### Part 25. Mathematical Optimization

### Part 26. Common Codeforces Mathematical Forms

- Form 1. Sum Constraint
- Form 2. Difference Constraint
- Form 3. Product Constraint
- Form 4. Ratio Constraint
- Form 5. Parity Constraint
- Form 6. Divisibility Constraint
- Form 7. GCD Constraint
- Form 8. LCM Constraint
- Form 9. Modulo Constraint
- Form 10. Equal Frequency
- Form 11. Pair Counting
- Form 12. Complement Pair
- Form 13. Difference Pair
- Form 14. Equal Remainders
- Form 15. Consecutive Values
- Form 16. Arithmetic Progression
- Form 17. Geometric / Doubling
- Form 18. Median Optimization
- Form 19. Prefix Equation
- Form 20. Contribution Counting
- Form 21. Pigeonhole
- Form 22. Inclusion-Exclusion
- Form 23. Invariant
- Form 24. Monovariant
- Form 25. Reachability
- Form 26. Constructive Equation
- Form 27. Bounding
- Form 28. Extremal Principle
- Form 29. Coordinate Transformation
- Form 30. Bit Independence
- Form 31. Prime Factor Independence
- Form 32. Frequency Compression
- Form 33. Permutation Mathematics
- Form 34. Mex Mathematics
- Form 35. Interval Mathematics
- Form 36. Grid Parity
- Form 37. Cyclic / Modulo Process
- Form 38. Binary Search Equation
- Form 39. Stars and Bars
- Form 40. Diophantine Equation
### Part 27. Problem Modeling Library

- Pattern A: Parity & Formula Bounds (Lower Bound + Construction)
- Pattern B: Invariants (Sum / GCD / Difference)
- Pattern C: Number Theory, Modulo & Diophantine Formulas
- Pattern D: Pair Conditions -> Algebra + Sorting/Frequency
- Pattern E: Binary Search on the Answer
### Part 28. Same Problem, Multiple Models

### Part 29. Constraints → Expected Mathematics

### Part 30. How to Discover the Equation

### Part 31. How to Discover Invariants (Operation-Delta Analysis)

### Part 32. Brute Force → Math

### Part 33. Mathematical Proof Toolkit

### Part 34. 60-Second Contest Modeling Checklist

### Part 35. Rating-Wise Modeling Expectations

### Part 36. Master Pattern Index

### Part 37. Final Mathematical Modeling Workflow


---

## Recommended Naming Convention

```text
000_HOW_TO_READ_CF_MATHEMATICALLY.md
001_ARITHMETIC_FOUNDATIONS.md
002_ALGEBRA_FOR_CP.md
003_NUMBER_THEORY_FOUNDATIONS.md
...
026_COMMON_CF_MATHEMATICAL_FORMS.md
027_PROBLEM_MODELING_LIBRARY.md
...
037_FINAL_MODELING_WORKFLOW.md
```
