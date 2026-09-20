# AD-HOC & PATTERNS — Patternwise Problems

A compact Codeforces-focused recognition library organized like the Prefix Sum pattern notes.

<a id="toc"></a>

## Table of Contents

### LEVEL 1 — Core Recognition
- [Form 1 — Sort + Rearrange / Pairing](#form-1)
- [Form 2 — Sort + Greedy Cheapest First](#form-2)
- [Form 3 — Median / Absolute Difference](#form-3)
- [Form 4 — Reachable Range / Missing Value](#form-4)
- [Form 5 — Modulo Extremum](#form-5)
- [Form 6 — Periodicity / Modulo Cycle](#form-6)

### LEVEL 2 — Operation Reasoning
- [Form 7 — Operation Effect / Divisibility](#form-7)
- [Form 8 — Boundary / Corner Observation](#form-8)
- [Form 9 — Invariant](#form-9)
- [Form 10 — Necessary + Sufficient Condition](#form-10)
- [Form 11 — Small Candidate Set / Bounded Simulation](#form-11)

### LEVEL 3 — Constructive Ad-hoc
- [Form 12 — Construct Valid Answer](#form-12)
- [Form 13 — Repair Invalid Local State](#form-13)
- [Form 14 — Two-Ends Construction](#form-14)
- [Form 15 — Binary / String Construction](#form-15)

---
## LEVEL 1 — Core Recognition

<a id="form-1"></a>

### Form 1 — Sort + Rearrange / Pairing
**Recognition:** Sorting removes irrelevant ordering and lets you pair/reassign elements based on relative order.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Restore the Weather](https://codeforces.com/problemset/problem/1833/B) | CF |
| 2 | [Minimize Inversions](https://codeforces.com/problemset/problem/1918/B) | CF |
| 3 | [Array Cloning Technique](https://codeforces.com/problemset/problem/1665/B) | CF |
| 4 | [Mean Inequality](https://codeforces.com/problemset/problem/1526/A) | CF |
| 5 | [Shoe Shuffling](https://codeforces.com/problemset/problem/1691/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-2"></a>

### Form 2 — Sort + Greedy Cheapest First
**Recognition:** You have a limited budget/resources. Sort by cost and repeatedly take the cheapest feasible choice.

**Problems: 4**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Teleporters — Easy Version](https://codeforces.com/problemset/problem/1791/G1) | CF |
| 2 | [Dragons](https://codeforces.com/problemset/problem/230/A) | CF |
| 3 | [Cheap Travel](https://codeforces.com/problemset/problem/466/A) | CF |
| 4 | [BerSU Ball](https://codeforces.com/problemset/problem/489/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-3"></a>

### Form 3 — Median / Absolute Difference
**Recognition:** When minimizing

```math
\sum |a_i-x|
```

think **median**, not mean.

**Problems: 4**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Stick Lengths](https://cses.fi/problemset/task/1074) | CSES |
| 2 | [Minimum Moves to Equal Array Elements II](https://leetcode.com/problems/minimum-moves-to-equal-array-elements-ii/) | LC |
| 3 | [Minimum Cost to Make Array Equal](https://leetcode.com/problems/minimum-cost-to-make-array-equal/) | LC |
| 4 | [Collecting Packages](https://codeforces.com/problemset/problem/1294/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-4"></a>

### Form 4 — Reachable Range / Missing Value
**Recognition:** Maintain a continuous reachable interval such as `[1...S]`. Determine whether the next value extends it or creates a gap.

**Problems: 4**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Missing Coin Sum](https://cses.fi/problemset/task/2183) | CSES |
| 2 | [First Missing Positive](https://leetcode.com/problems/first-missing-positive/) | LC |
| 3 | [Maximum Consecutive Values You Can Make](https://leetcode.com/problems/maximum-number-of-consecutive-values-you-can-make/) | LC |
| 4 | [Smallest Missing Integer Greater Than Sequential Prefix Sum](https://leetcode.com/problems/smallest-missing-integer-greater-than-sequential-prefix-sum/) | LC |


[↑ Back to TOC](#toc)

---

<a id="form-5"></a>

### Form 5 — Modulo Extremum
**Recognition:** Know the possible remainder range:

```math
0 \le x\bmod a < a
```

so the maximum possible remainder is `a-1`.

**Problems: 4**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Modulo Summation](https://atcoder.jp/contests/abc103/tasks/abc103_c) | AtCoder |
| 2 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF |
| 3 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF |
| 4 | [Grasshopper on a Line](https://codeforces.com/problemset/problem/1837/A) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-6"></a>

### Form 6 — Periodicity / Modulo Cycle
**Recognition:** Write the first few states. If behavior repeats every `k` operations, reduce a huge `n` using:

```
```

```
n % k
```

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Odd Grasshopper](https://codeforces.com/problemset/problem/1607/B) | CF |
| 2 | [Candies and Two Sisters](https://codeforces.com/problemset/problem/1335/A) | CF |
| 3 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF |
| 4 | [Again Twenty Five!](https://codeforces.com/problemset/problem/630/A) | CF |
| 5 | [Vanya and Food Processor](https://codeforces.com/problemset/problem/677/B) | CF |


[↑ Back to TOC](#toc)

---

# LEVEL 2 — Operation Reasoning

<a id="form-7"></a>

### Form 7 — Operation Effect / Divisibility
**Recognition:** Before simulating an operation, ask:

> What property does one operation actually change?

Often this reduces to parity, divisibility, factors, or powers of two.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | Multiple Powers of Two — from your TLE module | HackerRank |
| 2 | [Unit Array](https://codeforces.com/problemset/problem/1834/A) | CF |
| 3 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF |
| 4 | [Even Array](https://codeforces.com/problemset/problem/1367/B) | CF |
| 5 | [Forbidden Integer](https://codeforces.com/problemset/problem/1845/A) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-8"></a>

### Form 8 — Boundary / Corner Observation
**Recognition:** Instead of analyzing the entire matrix/array, determine whether only the first/last row, column, endpoints, or corners decide feasibility.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Rectangle Filling](https://codeforces.com/problemset/problem/1966/B) | CF |
| 2 | [Not Shading](https://codeforces.com/problemset/problem/1627/A) | CF |
| 3 | [Dora and Search](https://codeforces.com/problemset/problem/1793/C) | CF |
| 4 | [Image](https://codeforces.com/problemset/problem/1721/A) | CF |
| 5 | [Remove Prefix](https://codeforces.com/problemset/problem/1714/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-9"></a>

### Form 9 — Invariant
**Recognition:** Operations appear complicated, but some quantity/property **cannot change**.

Think:

```
```

```
sum
parity
XOR
difference
count
relative order
```

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Goals of Victory](https://codeforces.com/problemset/problem/1877/A) | CF |
| 2 | [Array Coloring](https://codeforces.com/problemset/problem/1857/A) | CF |
| 3 | [We Need the Zero](https://codeforces.com/problemset/problem/1805/A) | CF |
| 4 | [Prof. Slim](https://codeforces.com/problemset/problem/1670/A) | CF |
| 5 | [Make It Increasing](https://codeforces.com/problemset/problem/1438/A) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-10"></a>

### Form 10 — Necessary + Sufficient Condition
**Recognition:** Don't construct/simulate immediately. First ask:

```
```

```
What MUST be true?       → necessary
If it is true,
can I ALWAYS solve it?   → sufficient
```

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Odd Divisor](https://codeforces.com/problemset/problem/1475/A) | CF |
| 2 | [Array with Odd Sum](https://codeforces.com/problemset/problem/1296/A) | CF |
| 3 | [Forbidden Integer](https://codeforces.com/problemset/problem/1845/A) | CF |
| 4 | [2023](https://codeforces.com/problemset/problem/1916/A) | CF |
| 5 | [Balanced Array](https://codeforces.com/problemset/problem/1343/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-11"></a>

### Form 11 — Small Candidate Set / Bounded Simulation
**Recognition:** Constraints look huge, but only a tiny number of meaningful states/candidates need checking.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Don't Try to Count](https://codeforces.com/problemset/problem/1881/A) | CF |
| 2 | [Three Doors](https://codeforces.com/problemset/problem/1709/A) | CF |
| 3 | [Buy a Shovel](https://codeforces.com/problemset/problem/732/A) | CF |
| 4 | [Forbidden Integer](https://codeforces.com/problemset/problem/1845/A) | CF |
| 5 | [Lucky Division](https://codeforces.com/problemset/problem/122/A) | CF |


[↑ Back to TOC](#toc)

---

# LEVEL 3 — Constructive Ad-hoc

<a id="form-12"></a>

### Form 12 — Construct Valid Answer
**Recognition:** There may be many correct answers. You don't need to find an optimum—just construct **one valid configuration**.

**Problems: 6**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Balanced Array](https://codeforces.com/problemset/problem/1343/B) | CF |
| 2 | [Forbidden Integer](https://codeforces.com/problemset/problem/1845/A) | CF |
| 3 | [Divisible Array](https://codeforces.com/problemset/problem/1828/A) | CF |
| 4 | [United We Stand](https://codeforces.com/problemset/problem/1859/A) | CF |
| 5 | [Sequence Game](https://codeforces.com/problemset/problem/1862/B) | CF |
| 6 | [Co-prime Array](https://codeforces.com/problemset/problem/660/A) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-13"></a>

### Form 13 — Repair Invalid Local State
**Recognition:** Most of the current answer is already valid. Find a local violation and insert/change something to repair it.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Co-prime Array](https://codeforces.com/problemset/problem/660/A) | CF |
| 2 | [Sequence Game](https://codeforces.com/problemset/problem/1862/B) | CF |
| 3 | [Avoid Local Maximums](https://codeforces.com/problemset/problem/1635/B) | CF |
| 4 | [Shoe Shuffling](https://codeforces.com/problemset/problem/1691/B) | CF |
| 5 | [Array Cloning Technique](https://codeforces.com/problemset/problem/1665/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-14"></a>

### Form 14 — Two-Ends Construction
**Recognition:** Conditions involve pairs/endpoints. Maintain `l` and `r` and make decisions from both sides.

**Problems: 5**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Qingshan Loves Strings 2](https://codeforces.com/problemset/problem/1889/A) | CF |
| 2 | [Dora and Search](https://codeforces.com/problemset/problem/1793/C) | CF |
| 3 | [Shoe Shuffling](https://codeforces.com/problemset/problem/1691/B) | CF |
| 4 | [Mean Inequality](https://codeforces.com/problemset/problem/1526/A) | CF |
| 5 | [Restore the Weather](https://codeforces.com/problemset/problem/1833/B) | CF |


[↑ Back to TOC](#toc)

---

<a id="form-15"></a>

### Form 15 — Binary / String Construction
**Recognition:** Rather than brute-forcing strings, exploit counts, alternating structure, runs, mismatches, or required local patterns.

**Problems: 6**

| # | Problem | Platform |
| ---: | --- | :---: |
| 1 | [Qingshan Loves Strings 2](https://codeforces.com/problemset/problem/1889/A) | CF |
| 2 | [Reverse Binary Strings](https://codeforces.com/problemset/problem/1437/B) | CF |
| 3 | [AB Balance](https://codeforces.com/problemset/problem/1606/A) | CF |
| 4 | [Binary String Minimizing](https://codeforces.com/problemset/problem/1256/B) | CF |
| 5 | [Make it Alternating](https://codeforces.com/problemset/problem/1879/C) | CF |
| 6 | [Binary String To Subsequences](https://codeforces.com/problemset/problem/1399/D) | CF |

---

[↑ Back to TOC](#toc)