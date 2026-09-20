# CF Mathematical Modeling Mastery (800 → 1900)

> **Core skill:** `Statement → Variables → Conditions → Simplify → Recognize form → Algorithm → Proof → C++`
>
> **Do not ask first "which algorithm is this?" Ask "what is the mathematical structure?"**

**Honest scope note.** Parts 0–26 and 28–37 are complete. Part 27 (the problem library) contains **18 fully decoded problems** in the mandatory 13-step format, spanning 800→1900. The 180+ target is too large for one file-generation pass; the band index at the end of Part 27 lists what to extend next. Ratings are approximate (CF re-rates); verify on the site.


## Table of Contents

- [Part 0. How to Mathematically Read a Codeforces Problem](#part-0-how-to-mathematically-read-a-codeforces-problem)
- [Part 1. Arithmetic Foundations](#part-1-arithmetic-foundations)
- [Part 2. Algebra for Competitive Programming](#part-2-algebra-for-competitive-programming)
  - [Algebra Form 1. Rearranging Equations](#algebra-form-1-rearranging-equations)
  - [Algebra Form 2. Variable Isolation](#algebra-form-2-variable-isolation)
  - [Algebra Form 3. Substitution](#algebra-form-3-substitution)
  - [Algebra Form 4. Difference of Squares](#algebra-form-4-difference-of-squares)
  - [Algebra Form 5. Expansions](#algebra-form-5-expansions)
  - [Algebra Form 6. Pairwise Sum Transformations](#algebra-form-6-pairwise-sum-transformations)
  - [Algebra Form 7. Linear Equations `ax+by=c`](#algebra-form-7-linear-equations-axbyc)
  - [Algebra Form 8. Systems of Equations](#algebra-form-8-systems-of-equations)
  - [Algebra Form 9. Inequality Modeling](#algebra-form-9-inequality-modeling)
  - [Algebra Form 10. Bounding](#algebra-form-10-bounding)
- [Part 3. Number Theory Foundations](#part-3-number-theory-foundations)
- [Part 4. Modular Arithmetic](#part-4-modular-arithmetic)
- [Part 5. Parity](#part-5-parity)
- [Part 6. Counting & Combinatorics](#part-6-counting--combinatorics)
- [Part 7. Sequences & Series](#part-7-sequences--series)
- [Part 8. Coordinate & Distance Mathematics](#part-8-coordinate--distance-mathematics)
- [Part 9. Min/Max Transformations](#part-9-minmax-transformations)
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
- [P01. Watermelon (CF 4A)](#p01-watermelon-cf-4a)
- [P02. Yet Another Two Integers Problem (CF 1409A)](#p02-yet-another-two-integers-problem-cf-1409a)
- [P03. Friends and Candies (CF 1538B)](#p03-friends-and-candies-cf-1538b)
- [P04. EhAb AnD gCd (CF 1325A)](#p04-ehab-and-gcd-cf-1325a)
- [P05. Odd Divisor (CF 1475A)](#p05-odd-divisor-cf-1475a)
- [P06. Honest Coach (CF 1360B)](#p06-honest-coach-cf-1360b)
- [P07. Required Remainder (CF 1374A)](#p07-required-remainder-cf-1374a)
- [P08. New Year's Number (CF 1475B)](#p08-new-years-number-cf-1475b)
- [P09. Exciting Bets (CF 1543A)](#p09-exciting-bets-cf-1543a)
- [P10. K-divisible Sum (CF 1476A)](#p10-k-divisible-sum-cf-1476a)
- [P11. Distance and Axis (CF 1401A)](#p11-distance-and-axis-cf-1401a)
- [P12. K-th Not Divisible by n (CF 1352C)](#p12-k-th-not-divisible-by-n-cf-1352c)
- [P13. Sweet Problem (CF 1263A)](#p13-sweet-problem-cf-1263a)
- [P14. Same Differences (CF 1520D)](#p14-same-differences-cf-1520d)
- [P15. Number of Pairs (CF 1538C)](#p15-number-of-pairs-cf-1538c)
- [P16. Pair of Topics (CF 1324D)](#p16-pair-of-topics-cf-1324d)
- [P17. Maximum Median (CF 1201C)](#p17-maximum-median-cf-1201c)
- [P18. Modulo Sum (CF 577B)](#p18-modulo-sum-cf-577b)
- [Problem index by band (to extend)](#problem-index-by-band-to-extend)
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
| distance | absolute difference `|x-y|` |
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

### 1.1 Integer division, quotient, remainder

**Definition.** For `b>0`: `a = q*b + r`, `0 <= r < b`. `q = floor(a/b)`, `r = a mod b`.
**Intuition.** `q` = how many full groups of size `b`; `r` = leftover.
**Signals.** "groups of", "each box holds", "every k-th", "remainder".
**Example.** `17 = 3*5 + 2`: 3 full boxes of 5, 2 left.
**C++ notes.** `/` truncates toward 0, `%` can be **negative** for negative `a`. Normalize: `((a % m) + m) % m`.

### 1.2 Floor, ceiling, ceil-division

```text
ceil(a/b) = (a + b - 1) / b     for a >= 0, b > 0   (integer division)
ceil(a/b) = floor((a-1)/b) + 1  for a >= 1
floor(a/b + 0.5) is rounding; avoid doubles: use (2a+b)/(2b)
```

**Derivation.** Write `a = qb + r`. If `r=0`: `(a+b-1)/b = (qb+b-1)/b = q`. If `r>0`: `(qb+r+b-1)/b = q+1` since `r+b-1 >= b`.
**Signals.** "minimum number of trips/boxes/moves so that everything is covered".
**Example.** 23 items, boxes of 5 → `(23+4)/5 = 5`.
**Mistake.** `ceil(a/b)` with `a/b` already integer-divided in C++ → wrong; `ceil(1.0*a/b)` risks precision for `1e18`.

### 1.3 Absolute value, min/max

```text
|x| = max(x, -x)
|x-y| = distance on number line
max(a,b) = (a+b+|a-b|)/2      min(a,b) = (a+b-|a-b|)/2
```

### 1.4 Intervals and inequalities

```text
L <= x <= R      intersect two constraints:  max(L1,L2) <= x <= min(R1,R2)
empty if max(L) > min(R)
```

### 1.5 Powers, logarithms, bounds

```text
2^10 ~ 1e3, 2^20 ~ 1e6, 2^30 ~ 1e9, 2^60 ~ 1e18
log2(1e9) ~ 30, so "repeated doubling/halving" takes ~30 steps.
```

### 1.6 Overflow

| Quantity | Max magnitude | Type |
|---|---|---|
| `n*(n+1)/2`, n=2e5 | 2e10 | `long long` |
| sum of `2e5` values of `1e9` | 2e14 | `long long` |
| product of two 1e9 | 1e18 | `long long` (fits, < 9.2e18) |
| `C(n,2)`, n=2e5 | 2e10 | `long long` |

**Rule:** the moment a formula multiplies two input-size numbers, use `long long`.

```text
ASCII: ceil-division as packing
items:  ●●●●● ●●●●● ●●●●● ●●●●● ●●●
boxes:   [5]    [5]    [5]    [5]  [3]   -> 5 boxes = ceil(23/5)
```

**Representative problems:** CF 4A (parity), CF 1409A (ceil), CF 1476A (ceil + lower bound).

---

## Part 2. Algebra for Competitive Programming

### Algebra Form 1. Rearranging Equations

```text
x + y = S
x - y = D
add:       2x = S + D   ->  x = (S+D)/2
subtract:  2y = S - D   ->  y = (S-D)/2
```

**Feasibility:** `S+D` even (equivalently `S`, `D` same parity), and `x,y` in range (`x>=0`, `y>=0` ⇒ `S>=D`).
**Signals:** "sum is S and difference is D", "two numbers, given sum and max−min".
**Example:** S=10, D=4 → x=7, y=3.

### Algebra Form 2. Variable Isolation

```text
a*x + b = c   ->  a*x = c-b  ->  x = (c-b)/a   requires (c-b) % a == 0
```

Turns a loop over `x` into O(1). **Signal:** unknown appears linearly.

### Algebra Form 3. Substitution

Two unknowns, one relation → one unknown. Example: `x + y = n`, minimize `3x + 5y` ⇒ substitute `y = n-x`, cost `= 5n - 2x`, linear in `x` ⇒ extremes only.

### Algebra Form 4. Difference of Squares

```text
a^2 - b^2 = (a-b)(a+b)
```

If `N = a^2 - b^2` then `N = d*e` with `d=a-b`, `e=a+b`, same parity. Enumerate divisors `d<=sqrt N`, `a=(d+e)/2`, `b=(e-d)/2`.

### Algebra Form 5. Expansions

```text
(a+b)^2 = a^2 + 2ab + b^2
(a-b)^2 = a^2 - 2ab + b^2
(a+b+c)^2 = a^2+b^2+c^2 + 2(ab+bc+ca)
=> sum_{i<j} A_i A_j = ((sum A)^2 - sum A^2) / 2
```

The last identity replaces O(n²) pair enumeration by O(n).

### Algebra Form 6. Pairwise Sum Transformations

```text
sum_{i<j} (A_i - A_j)^2 = n * sum A_i^2 - (sum A_i)^2
```

**Derivation.** `sum_{i<j}(A_i^2 + A_j^2) = (n-1) sum A^2`; `sum_{i<j} 2 A_i A_j = (sum A)^2 - sum A^2`. Subtract: `(n-1)ΣA² - (ΣA)² + ΣA² = nΣA² - (ΣA)²`.
Also `sum_{i<j} |A_i - A_j|` after sorting = `sum_k A_k * (2k - n - 1)` (1-indexed): each element is added `k-1` times and subtracted `n-k` times.

### Algebra Form 7. Linear Equations `ax+by=c`

Integer solutions exist iff `gcd(a,b) | c`. (Part 20.)

### Algebra Form 8. Systems of Equations

`k` independent equations determine `k` unknowns. If unknowns > equations, one free parameter remains → search that parameter within bounds.

### Algebra Form 9. Inequality Modeling

```text
x >= L,  x <= R   =>  L <= x <= R
several constraints => intersect intervals
"at least k of ..." => sum of indicators >= k
```

### Algebra Form 10. Bounding

Most min-answers are solved by **two steps**:

```text
(1) Prove answer >= X     (lower bound: something must be paid)
(2) Construct a solution using exactly X
=> answer = X
```

Example (CF 1263A): can't do better than `(a+b+c)/2` because each move uses 2 candies; can't do better than `a+b+c-max` because the largest pile needs partners. Take the min of the two bounds; a construction shows it is achievable.


---

## Part 3. Number Theory Foundations

| Concept | Definition | Modeling sentence |
|---|---|---|
| divisor `d | n` | `n % d == 0` | "each group has equal size d" |
| multiple | `n = k*d` | "every d-th", "period d" |
| prime | exactly 2 divisors | "no split possible" |
| gcd(a,b) | largest common divisor | "both numbers must be divisible by / split into equal pieces" |
| lcm(a,b) | smallest common multiple | "both events coincide", "divisible by both" |

**Key identities**

```text
gcd(a,b) = gcd(b, a mod b)          (Euclid, O(log))
gcd(a,b) * lcm(a,b) = a*b
lcm(a,b) = a / gcd(a,b) * b         (divide first to avoid overflow)
gcd(a,b) = gcd(a, b-a) = gcd(a, b - k*a)     <- makes gcd an INVARIANT of subtraction ops
a x + b y = c solvable in integers  <=>  gcd(a,b) | c        (Bezout)
```

**Divisor enumeration** – O(√n): for `d` from 1 while `d*d<=n`, both `d` and `n/d`.
**Number of divisors** – `prod (e_i+1)` from `n = prod p_i^e_i`. **Sum** – `prod (p^(e+1)-1)/(p-1)`.
**Perfect square ⇔ every exponent even ⇔ divisor count odd** (divisors pair `d ↔ n/d`, except `d=√n`).
**Sieve / SPF:** `spf[x]` smallest prime factor; factor by repeated `x /= spf[x]` in O(log x).

```cpp
vector<int> spf(N + 1, 0);
for (int i = 2; i <= N; i++) {
    if (spf[i] != 0) continue;          // i is composite, already marked
    for (int j = i; j <= N; j += i)
        if (spf[j] == 0) spf[j] = i;    // smallest prime factor of j
}
```

**Modeling examples**

```text
"Both numbers must divide X"        -> X is a common multiple, candidates related to lcm
"Split into equal parts, both"      -> part size divides gcd
"Every number becomes multiple of g"-> g | gcd of all
"a+b = x, minimize lcm(a,b)"        -> a=1? no: a=1,b=x-1 gives lcm=x-1; (CF 1325A)
```

**Mistakes:** overflow in `a*b/gcd`; treating 1 as prime; forgetting `d=n/d` duplicate when enumerating divisors.
**Problems:** CF 1543A, 1325A, 1475A, 1474B.

---

## Part 4. Modular Arithmetic

```text
a ≡ b (mod m)  <=>  m | (a-b)  <=>  a mod m = b mod m
(a+b) mod m = ((a mod m)+(b mod m)) mod m         same for -, *
division: a/b mod p = a * b^(p-2) mod p           (p prime, b not multiple of p)
Fermat: a^(p-1) ≡ 1 (mod p)
```

**Recognition**

```text
repeated cyclic process                   -> modulo
"same remainder"                          -> (A-B) % M == 0
"last digit", "every k-th"                -> mod 10 / mod k
huge exponent                             -> fast power
counting answer "mod 998244353"           -> every op mod, inverse via Fermat
```

**Fast exponentiation (binary):** `a^e`: process bits of `e`; O(log e).

```cpp
long long power(long long a, long long e, long long mod) {
    long long result = 1;
    a %= mod;
    while (e > 0) {
        if (e & 1) result = result * a % mod;   // current bit is 1
        a = a * a % mod;                        // square the base
        e >>= 1;
    }
    return result;
}
```

**Prefix modulo:** subarray `(l,r]` divisible by `m` ⇔ `P[r] ≡ P[l] (mod m)`. Count pairs of equal prefix remainders.
**Pigeonhole + modulo:** among any `m` numbers some non-empty subsequence has sum ≡ 0 (mod m) (prefix sums `P0..Pm`: m+1 values, m remainders).
**Mistakes:** negative remainder; `a*b` overflow before `%` (use `long long`, or `__int128` if mod ~1e18); dividing without inverse.
**Problems:** CF 577B (Part 27), CF 1475B.

---

## Part 5. Parity

```text
even+even=even   even+odd=odd   odd+odd=even
even*any=even    odd*odd=odd
parity of sum = (# odd terms) mod 2
```

**Tools:** parity invariant, parity flip, checkerboard parity (`(x+y)%2`), odd/even counts, permutation parity (each swap flips it).
**Signal:** operation changes every affected quantity by a fixed amount (±1, ±2, ±even).
**Collapse example:** "Can you make all equal by adding 2 to any element?" → parity of every element is preserved, so all elements must already share parity.

```text
Watermelon: split n into two even positive parts.
even+even=even -> n even; positive -> each >=2 -> n>=4.  (n=2 fails.)
```

**Mistake:** `x%2==1` fails for negative `x` (C++ gives -1); test `x%2!=0` or `x&1`.
**Problems:** CF 4A, 1401A.

---

## Part 6. Counting & Combinatorics

```text
Addition principle:       disjoint cases -> add
Multiplication principle: independent choices -> multiply
n! = n(n-1)...1           P(n,k) = n!/(n-k)!         C(n,k) = n!/(k!(n-k)!)
C(n,2) = n(n-1)/2         C(n,3) = n(n-1)(n-2)/6
Pascal: C(n,k) = C(n-1,k-1) + C(n-1,k)
Equal pairs from frequency f: f(f-1)/2
Complement: desired = total - bad
Multiset permutations: n!/(c1! c2! ...)
```

**Stars and bars**

```text
x1+...+k xk = n, xi>=0   ->  C(n+k-1, k-1)
xi >= 1                  ->  C(n-1, k-1)   (substitute yi = xi-1)
xi >= li                 ->  subtract sum li from n first
```

```text
n=5, k=3, xi>=0:   ***|*|*  ~ (3,1,1)      choose positions of 2 bars among 7 slots = C(7,2)=21
```

**Inclusion–Exclusion**

```text
|A∪B|   = |A|+|B|-|A∩B|
|A∪B∪C| = |A|+|B|+|C| -|A∩B|-|A∩C|-|B∩C| + |A∩B∩C|
general: sum over non-empty masks S of (-1)^(|S|+1) |∩_{i in S} A_i|      (2^k terms)
```

**Pigeonhole:** `n+1` objects in `n` boxes ⇒ some box has 2. Signals: "prove/decide existence", "among any ... there exist two with ...", prefix remainders.
**Double counting:** count pairs (x, group) two ways. Example: `sum over pairs i<j [A_i=A_j]` = `sum over values f_v(f_v-1)/2`.
**Contribution technique**

```text
Instead of: for each subarray, compute value
Do:         for each element, count subarrays where it plays the role, multiply
```

Example: sum of all subarray sums: element at index `i` (1-indexed) lies in `i*(n-i+1)` subarrays ⇒ `sum A_i * i * (n-i+1)`.
More: sum over pairs `|Ai-Aj|` (sorted coefficient trick, Part 2.6); sum over bits `2^b * (#numbers with bit b set)` for OR/XOR contributions; "number of subarrays where max is A_i" = `left_i * right_i` (monotonic stack).
**Modular combinatorics:** precompute `fact`, `inv_fact` up to N mod p; `C(n,k)=fact[n]*ifact[k]*ifact[n-k]`.
**Mistakes:** `n(n-1)/2` overflowing `int`; overcounting ordered vs unordered; forgetting complement is easier when "at least one".
**Problems:** CF 1520D, 1538C, 1324D.

---

## Part 7. Sequences & Series

```text
AP:  a_n = a + (n-1)d        S_n = n/2 * (2a + (n-1)d) = n(a + a_n)/2
1+2+...+n = n(n+1)/2
GP:  a_n = a r^(n-1)         S_n = a (r^n - 1)/(r - 1)   (r != 1)
1+2+4+...+2^(k-1) = 2^k - 1
1^2+...+n^2 = n(n+1)(2n+1)/6
harmonic: sum_{i=1}^n n/i ~ n ln n  (why sieve-like loops are O(n log n))
```

**Signals.** "1st day 1, 2nd day 2, ...", "each step adds one more than last" → AP/triangular. "doubling" → GP, `≈ log2` steps. Triangular bound: find smallest `k` with `k(k+1)/2 >= n` → `k ~ sqrt(2n)`.
**Mistake:** GP sum with `r=1` division by zero; overflow of `2^k` for `k>=63`.

---

## Part 8. Coordinate & Distance Mathematics

```text
Number line: |x-y|
Manhattan:  |x1-x2| + |y1-y2|
Chebyshev:  max(|x1-x2|, |y1-y2|)     (king moves)
Euclid^2:   (x1-x2)^2 + (y1-y2)^2     (compare squared; avoid sqrt)
Interval overlap: [l1,r1] & [l2,r2] = [max(l1,l2), min(r1,r2)], nonempty iff max<=min
```

**Median minimizes `Σ|x - a_i|`.** *Proof:* for two points `a<=b`, `|x-a|+|x-b| >= b-a` with equality iff `x∈[a,b]`. Pair smallest with largest, second smallest with second largest, …; the median lies in every pair interval ⇒ all lower bounds tight simultaneously.

**Mean minimizes `Σ(x-a_i)^2`:** derivative `2Σ(x-a_i)=0 ⇒ x=mean`. Or: `Σ(x-a_i)^2 = n(x-μ)^2 + Σ(a_i-μ)^2`.

**Rotation trick (Manhattan → Chebyshev):** `u=x+y, v=x-y`: `|dx|+|dy| = max(|du|,|dv|)`.

```text
grid movement: one step changes x+y by ±1 -> parity of (x+y) flips each step
reach (a,b) from (0,0) in exactly k steps <=> |a|+|b| <= k and (k-|a|-|b|) even
```

Coordinate normalization: shift so start = origin; compression maps large coordinates to ranks.
**Problems:** CF 1401A, 1201C (uses median).

---

## Part 9. Min/Max Transformations

```text
max(a,b) = (a+b+|a-b|)/2      min(a,b) = (a+b-|a-b|)/2
max(a,b)+min(a,b) = a+b       max*min = a*b
max_i(a_i - i) style:  separate variables:  max over pairs (a_i+i)-(a_j+j)
```

* **Minimize the maximum / maximize the minimum:** guess `X`, test feasibility, binary search (Part 25).
* **Balancing:** to minimize `max(x, S-x)` set `x≈S/2`.
* **Extremal argument:** look at the max / min element – it constrains everything (e.g. largest pile in CF 1263A).
* **Bound by both sides:** `answer = min(bound1, bound2)` where each bound is a resource limit.

---

## Part 10. Invariants

> **Invariant:** a quantity unchanged by every allowed operation. **Monovariant:** a quantity that only moves one way.

Template for each form:

```text
Before operation   |   After operation   |   What changed?   |   What did NOT change?
```

| Form | Operation | Preserved | Consequence |
|---|---|---|---|
| **1. Sum** | `Ai+=x, Aj-=x` | `ΣA` | target all-equal needs `ΣA % n == 0` (CF 1538B) |
| **2. Parity** | `Ai±=2` | `Ai mod 2` | reachable only if same parity |
| **3. XOR** | `Ai^=x, Aj^=x` | total XOR | final XOR = initial XOR |
| **4. GCD** | `Ai-=Aj` (or `Ai=Ai-k*Aj`) | `gcd(all)` | final elements all multiples of g |
| **5. Modulo** | `Ai+=m` | `Ai mod m` | classes never merge |
| **6. Difference** | add `c` to all | `Ai-Aj` | only relative structure matters |
| **7. Count** | swap two elements | multiset | frequency same before/after |
| **8. Ordering** | swap only equal-parity neighbors | relative order of opposite parity | check the parity-subsequence sorted |
| **9. Coloring** | domino tiles 2 adjacent cells | #black - #white | balanced board needed |
| **10. Monovariant** | each step reduces `ΣA` | strictly decreasing | process terminates; bound #steps |

**Worked derivations**

```text
Form 4:  op: Ai := Ai - Aj.   Let g = gcd(Ai, Aj).
         g | Ai and g | Aj  ->  g | Ai-Aj.  So g still divides everything.
         Reverse op (Ai := Ai + Aj) shows gcd cannot shrink. gcd(Ai,Aj) = gcd(Ai-Aj, Aj). Preserved.
Form 9:  chessboard minus 2 opposite corners: 32 white... 30 black/32 white; each domino covers 1 each -> impossible.
```

**Mistake:** confusing "necessary" with "sufficient" – an invariant proves impossibility; you still need construction for possibility.
**Problems:** CF 1538B, 1401A, 4A.

---

## Part 11. Operation Modeling

```text
State before op -> algebraic form -> delta -> check parity -> sum -> modulo
-> gcd -> ordering/counts -> invariant -> reachable states
```

| Operation | Delta | Preserved |
|---|---|---|
| `Ai+=x, Aj-=x` | `0` on sum | sum |
| `Ai+=2` | `+2` | parity of `Ai` |
| `Ai+=1, Aj+=1` | `+2` on sum | parity of sum |
| `Ai:=Ai-Aj` | — | gcd |
| `Ai^=x, Aj^=x` | — | XOR of all |
| `swap(Ai,Aj)` | — | multiset |
| `Ai*=2` | — | odd part of each element |
| `Ai:=Ai+Ai+1` (merge) | — | sum |
| `remove k, add k-1` | `-1` | count decreases by one each step (monovariant) |

**Reachability in linear ops:** if operation adds vector `v`, reachable = `{start + t*v}`; with two vectors it is a lattice ⇒ gcd of coefficients decides.
**Example.** `Ai+=1, Aj-=1` any `i≠j`: reachable arrays = same sum, same length (when values may go negative).

---

## Part 12. Decoupling Variables

```text
u = x+y, v = x-y   =>  x=(u+v)/2, y=(u-v)/2      (need u≡v mod 2)
```

Use when a constraint mixes `x,y` but is simple in `u,v` (e.g. `|x|+|y|<=k` becomes `max(|u|,|v|)<=k`, a square).

Decoupling catalog:

* **x/y independence:** grid path counts, `dx` and `dy` handled separately.
* **Bit-by-bit:** AND/OR/XOR act per bit; solve 30 independent 0/1 problems.
* **Digit-by-digit:** carries link digits – only decouple when no carries.
* **Prime-factor independence:** `gcd`, `lcm`, divisibility act per prime exponent (`min`, `max`, `≤`).
* **Frequency decoupling:** positions don't matter → work with counts.

---

## Part 13. Frequency Modeling

```text
array -> cnt[v] -> counting problem
```

* equal pairs: `Σ cnt[v](cnt[v]-1)/2`
* complementary pairs `Ai+Aj=K`: `Σ cnt[v]*cnt[K-v]` (`v<K-v`), plus `C(cnt[K/2],2)` if `K` even
* mex: smallest `v` with `cnt[v]=0`
* permutation check: all `cnt[v]=1` for `v=1..n`
* multiset equality: compare count maps
* frequency parity: odd-count values determine XOR-like behaviour

Ordering is irrelevant when the operation/target is symmetric in positions.

---

## Part 14. Sorting as a Mathematical Transformation

After sorting: `a1<=a2<=...<=an` creates monotonic structure.

* **Adjacent differences** give min gap (CF 1360B).
* **Pair extremes** (smallest with largest) balances sums; **pair neighbors** minimizes gaps.
* **Median** = position `⌈n/2⌉`.
* **Rearrangement inequality:** `Σ a_i b_σ(i)` maximized with same order, minimized with opposite order.
* **Two pointers** on sorted arrays turn `O(n²)` pair conditions into `O(n)` (CF 1538C, 1324D).
* **Sorted prefix/suffix:** "take k largest" = last `k` elements.

---

## Part 15. Prefix Mathematics

```text
P[i] = A1+...+Ai         sum(l,r) = P[r]-P[l-1]
```

Same idea for XOR (`X[r]^X[l-1]`), counts, min/max (prefix max), 2D (`P[i][j]`, inclusion–exclusion on rectangles), modulo, difference arrays.

**Global repeated queries → subtraction.** Equation form: "subarray sum = K" ⇔ `P[r]-P[l-1]=K` ⇔ `P[l-1]=P[r]-K` → hash map lookup. "Sum divisible by m" ⇔ equal prefix remainders. "Equal number of 0 and 1" ⇔ map 0→-1 and look for `P[l-1]=P[r]`.

2D: `S(x1..x2,y1..y2) = P[x2][y2]-P[x1-1][y2]-P[x2][y1-1]+P[x1-1][y1-1]`.

---

## Part 16. Difference Arrays

```text
D[i] = A[i]-A[i-1]      A[i] = D[1]+...+D[i]
add v on [l,r]:   D[l]+=v,  D[r+1]-=v
```

```text
A:      0 0 5 5 5 0 0        add 5 on [3,5]
D:      0 0 5 0 0 -5 0       (two events: +5 at 3, -5 at 6)
```

Sweep view: each range is `(+v at l, -v at r+1)`; prefix sum of events = current value. AP range update: second difference (`D2`). "Repeated range updates then final read" → difference array.

---

## Part 17. Bitwise Mathematical Modeling

Integer = vector of independent bits. Key question: **can each bit be solved independently?**

```text
x&y bit b = 1 iff both 1        x|y: either        x^y: exactly one (parity)
a^a = 0, a^0 = a                      => XOR cancels pairs
a+b = (a^b) + 2(a&b)                  a|b = (a^b) + (a&b)
Σ over pairs of (Ai xor Aj): per bit b, ones*zeros*2^b
```

Bit contribution: `answer = Σ_b 2^b * (count of structures with bit b)`.
Subset masks: `for mask in [0,2^n)`; inclusion–exclusion over masks; `mask & (mask-1)` clears lowest set bit; popcount parity = XOR of bits.
**Mistake:** `1<<b` with `b>=31` needs `1LL<<b`.

---

## Part 18. Greedy Through Mathematical Proofs

Every greedy needs three answers:

```text
Why this choice?   What inequality proves it?   What if we swap?
```

**Exchange argument template:** take an optimal solution that differs from greedy at the first place; swap; show cost does not increase.

Example (cheapest first): buy items with budget `B`, maximize count. Take sorted ascending; any optimal set with `k` items has cost ≥ sum of the `k` smallest ⇒ greedy count is optimal.
Example (CF 1360B): min difference of pair from sorted array is adjacent: `a_j-a_i >= a_{i+1}-a_i` for `j>i`.
Others: largest first (fill big before small), interval greedy (earliest end), resource balancing (always feed the smallest).

---

## Part 19. Constructive Mathematics

```text
Required properties -> Necessary conditions -> Invariant/bound
-> Build simplest structure -> Verify mathematically
```

Patterns: alternating `a,b,a,b`; cyclic shift permutation `2,3,...,n,1` (no fixed point); gcd constructions (`1, x-1`; consecutive integers are coprime; `n, n-1`); parity constructions (put odds first); modulo construction (`a_i = i*m`); prefix/suffix (`0,1,...,k` to get mex `k+1`).
**Check both**: necessary conditions rule out impossible; the construction proves rest possible. Test on tiny brute-force.

---

## Part 20. Diophantine Modeling

```text
ax + by = c
1) solvable iff gcd(a,b) | c
2) extended Euclid gives (x0,y0) with a x0 + b y0 = gcd
3) scale by c/g; general solution: x = x0 + (b/g)t, y = y0 - (a/g)t
4) non-negative: bound t
```

**Simple alternative (CF-friendly):** if `a` large, enumerate `x` from `0` to `c/a` and check `(c-ax)%b==0`; `O(c/a)`.
Signals: "coins of value a and b to pay exactly c", "packs of 3 and 7". Example: 2020a+2021b=n ⇒ substitute (CF 1475B).

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

Coordinates, Manhattan distance, parity of cells (`(x+y)&1`), reachable set `{|dx|+|dy|<=k, parity matches}`, rectangle area `w*h`, perimeter `2(w+h)`, lattice points on segment `gcd(|dx|,|dy|)+1`, slope compare via cross-multiplication (avoid floats), overlap area of two rectangles = `max(0,overlapX)*max(0,overlapY)`, coordinate compression to rank space.

---

## Part 22. Game Mathematics

```text
Position is LOSING if all moves lead to WINNING positions; WINNING if some move leads to LOSING.
```

Backward reasoning from terminal positions; find periodic pattern by brute-force table for small n, then generalize.
Take-away `{1..k}` game: losing iff `n % (k+1) == 0` (mirror strategy).
Parity games: winner decided by parity of total moves when the total is fixed.
**Nim:** piles `a_i`, first player wins iff `XOR a_i != 0`. **Grundy:** independent games' values combine by XOR; `g(pos)=mex{g(next)}`.

---

## Part 23. Recurrences

```text
Process -> f(n) = f(n-1) + ... ; compute iteratively O(n)
Fibonacci: f(n)=f(n-1)+f(n-2)
Linear recurrence of order k -> k x k matrix, power by fast exponentiation O(k^3 log n)
```

Signals: "number of ways for length n where last step ...", huge `n`. Introduction: `[f(n+1),f(n)]^T = M^n [f(1),f(0)]^T`, `M=[[1,1],[1,0]]`.

---

## Part 24. Expectation / Probability Basics

```text
P(A) = favorable/total          P(not A) = 1 - P(A)
independent: P(A∩B)=P(A)P(B)
Linearity: E[X+Y] = E[X]+E[Y]   ALWAYS (no independence needed)
E[count] = Σ P(indicator_i = 1)
```

Expected contribution: for each element/pair, compute probability it contributes, sum. In modular problems `P = a * b^{-1} mod p`.

---

## Part 25. Mathematical Optimization

```text
optimization -> guess X -> can(X)? -> monotone? -> binary search
```

Monotone means `can(X)` true ⇒ `can(X+1)` (or reversed). Check by asking: "if X works, does a weaker requirement work?"
Discrete convexity: unimodal cost `f(x)` ⇒ ternary/binary search on `f(x+1)-f(x)`. Minimizing `Σ|x-a_i|` ⇒ median. Balancing two quantities ⇒ meet near `S/2`. Every optimum needs a lower bound + achieving construction.

```cpp
// find the maximum x with can(x) == true   (can is monotone: true ... true false ... false)
long long lo = 0, hi = INF;
while (lo < hi) {
    long long mid = lo + (hi - lo + 1) / 2;   // upper mid avoids infinite loop
    if (can(mid)) lo = mid;
    else          hi = mid - 1;
}
// answer = lo
```


---

## Part 26. Common Codeforces Mathematical Forms

Each form: Recognition → Model → Transformation → Why → Visual → Example → Mistakes → Complexity → Problems.

### Form 1. Sum Constraint

**Recognition Signals:** "total is S", "sum of all", "equalize"

**Mathematical Model:** `ΣA = S`; target all equal `t` ⇒ `n·t = S`

**Core Transformation:** `t = S/n`, need `S % n == 0`

**Why It Works:** A sum-preserving operation can't change `S`; equal array has sum `n t`.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""total is S"" -> unknowns named -> `ΣA = S`; target all equal `t` ⇒ `n·t = S` -> `t = S/n`, need `S % n == 0` -> O(cheap) check -> code
```

**Example:** S=12,n=4 → t=3; S=13 → impossible

**Common Mistakes:** Using float average; forgetting sum is preserved only if ops are sum-neutral.

**Complexity:** O(n)

**Representative Problems:** CF 1538B, 1263A

### Form 2. Difference Constraint

**Recognition Signals:** "differ by", "gap", `|Ai-Aj|=d`

**Mathematical Model:** `Ai - Aj = d`

**Core Transformation:** `Ai = Aj + d` ⇒ lookup in map

**Why It Works:** Isolate one variable so pair search becomes point lookup.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""differ by"" -> unknowns named -> `Ai - Aj = d` -> `Ai = Aj + d` ⇒ lookup in map -> O(cheap) check -> code
```

**Example:** A=[1,5,3], d=2 → (1,3),(3,5)

**Common Mistakes:** Counting `(i,j)` and `(j,i)` twice; `d=0` case.

**Complexity:** O(n)–O(n log n)

**Representative Problems:** CF 1520D (variant)

### Form 3. Product Constraint

**Recognition Signals:** "product", `Ai*Aj=K`

**Mathematical Model:** `Ai*Aj = K`

**Core Transformation:** `Aj = K/Ai`, need `K % Ai == 0`; or factor into primes

**Why It Works:** Divisor-pair correspondence; enumerate divisors ≤√K.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""product"" -> unknowns named -> `Ai*Aj = K` -> `Aj = K/Ai`, need `K % Ai == 0`; or factor into primes -> O(cheap) check -> code
```

**Example:** K=12 → (1,12),(2,6),(3,4)

**Common Mistakes:** Overflow `Ai*Aj`; division by 0.

**Complexity:** O(√K) or O(n)

**Representative Problems:** CF 1475A (odd/even split)

### Form 4. Ratio Constraint

**Recognition Signals:** "twice as many", "ratio a:b"

**Mathematical Model:** `x/y = a/b` ⇒ `x b = y a`

**Core Transformation:** Cross-multiply (no floats); `x=a t, y=b t` with `t` integer

**Why It Works:** Ratio in lowest terms `a'/b'` means `x=a' t`.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""twice as many"" -> unknowns named -> `x/y = a/b` ⇒ `x b = y a` -> Cross-multiply (no floats); `x=a t, y=b t` with `t` integer -> O(cheap) check -> code
```

**Example:** x:y = 2:3, x+y=20 → t=4 → 8,12

**Common Mistakes:** Comparing floats; not reducing by gcd.

**Complexity:** O(1)

**Representative Problems:** —

### Form 5. Parity Constraint

**Recognition Signals:** "even/odd", "alternating", "can't be split"

**Mathematical Model:** `x mod 2 = p`

**Core Transformation:** Reduce every quantity to `mod 2`; count odds

**Why It Works:** Ops by even amounts preserve parity; sum of parities = parity of sum.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""even/odd"" -> unknowns named -> `x mod 2 = p` -> Reduce every quantity to `mod 2`; count odds -> O(cheap) check -> code
```

**Example:** n=6 even, split into two even positive → yes

**Common Mistakes:** Negative `%`; forgetting minimal positive size (n=2).

**Complexity:** O(1)–O(n)

**Representative Problems:** CF 4A, 1401A

### Form 6. Divisibility Constraint

**Recognition Signals:** "divisible by d", "multiple of"

**Mathematical Model:** `x = d k`

**Core Transformation:** Round: `x = ceil(a/d)*d` (smallest multiple ≥ a)

**Why It Works:** Multiples of `d` form an AP with difference `d`.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""divisible by d"" -> unknowns named -> `x = d k` -> Round: `x = ceil(a/d)*d` (smallest multiple ≥ a) -> O(cheap) check -> code
```

**Example:** d=5,a=23 → 25

**Common Mistakes:** Off-by-one at exact multiples; overflow.

**Complexity:** O(1)

**Representative Problems:** CF 1476A, 1374A

### Form 7. GCD Constraint

**Recognition Signals:** "common divisor", "equal parts", subtraction ops

**Mathematical Model:** `g | Ai ∀i`

**Core Transformation:** `g = gcd(A1..An)`; ops like `Ai-=Aj` preserve it

**Why It Works:** gcd(a,b)=gcd(a,b-ka).

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""common divisor"" -> unknowns named -> `g | Ai ∀i` -> `g = gcd(A1..An)`; ops like `Ai-=Aj` preserve it -> O(cheap) check -> code
```

**Example:** gcd(12,18)=6

**Common Mistakes:** Assuming gcd of many is pairwise; gcd(0,x)=x.

**Complexity:** O(n log V)

**Representative Problems:** CF 1543A, 1325A

### Form 8. LCM Constraint

**Recognition Signals:** "divisible by both", "synchronize", "every a and every b"

**Mathematical Model:** `X mod a = X mod b = 0`

**Core Transformation:** `X = k·lcm(a,b)`, `lcm = a/gcd·b`

**Why It Works:** Common multiples are exactly multiples of lcm.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""divisible by both"" -> unknowns named -> `X mod a = X mod b = 0` -> `X = k·lcm(a,b)`, `lcm = a/gcd·b` -> O(cheap) check -> code
```

**Example:** a=4,b=6 → lcm 12

**Common Mistakes:** Overflow of `a*b`; lcm of many grows fast (cap it).

**Complexity:** O(log V)

**Representative Problems:** CF 1325A

### Form 9. Modulo Constraint

**Recognition Signals:** "remainder", "mod k", "each k-th"

**Mathematical Model:** `x mod m = r`

**Core Transformation:** Largest ≤n: `k = n - ((n-r) mod m)`; class representative

**Why It Works:** Values with same remainder are `r + m t`.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""remainder"" -> unknowns named -> `x mod m = r` -> Largest ≤n: `k = n - ((n-r) mod m)`; class representative -> O(cheap) check -> code
```

**Example:** n=7,m=5,r=3 → 3

**Common Mistakes:** Negative remainder; `r>=m`.

**Complexity:** O(1)

**Representative Problems:** CF 1374A, 577B

### Form 10. Equal Frequency

**Recognition Signals:** "equal numbers of", "same count"

**Mathematical Model:** `cntA = cntB`

**Core Transformation:** Assign +1 to A, −1 to B; need `Σ=0`

**Why It Works:** Prefix balance zero ⇒ segment balanced.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""equal numbers of"" -> unknowns named -> `cntA = cntB` -> Assign +1 to A, −1 to B; need `Σ=0` -> O(cheap) check -> code
```

**Example:** `ABBA` → +1−1−1+1=0 ✓

**Common Mistakes:** Off-by-one in prefix map (needs `P0=0`).

**Complexity:** O(n)

**Representative Problems:** prefix equal-balance (Part 15)

### Form 11. Pair Counting

**Recognition Signals:** "number of pairs (i<j)"

**Mathematical Model:** `C(n,2)` or Σ over frequency

**Core Transformation:** Group by key; `f(f-1)/2` per group

**Why It Works:** Pairs live inside groups.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""number of pairs (i<j)"" -> unknowns named -> `C(n,2)` or Σ over frequency -> Group by key; `f(f-1)/2` per group -> O(cheap) check -> code
```

**Example:** [1,1,2,2,2] → 1+3=4

**Common Mistakes:** `int` overflow; ordered vs unordered.

**Complexity:** O(n)

**Representative Problems:** CF 1520D

### Form 12. Complement Pair

**Recognition Signals:** `Ai+Aj=K`

**Mathematical Model:** `Aj = K - Ai`

**Core Transformation:** Frequency lookup; handle `Ai=K/2` with `C(f,2)`

**Why It Works:** Isolation of second variable.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"`Ai+Aj=K`" -> unknowns named -> `Aj = K - Ai` -> Frequency lookup; handle `Ai=K/2` with `C(f,2)` -> O(cheap) check -> code
```

**Example:** K=10,[3,7,5,5] → (3,7),(5,5)

**Common Mistakes:** Double counting; self pairing.

**Complexity:** O(n)

**Representative Problems:** CF 1538C (range version)

### Form 13. Difference Pair

**Recognition Signals:** `Ai-Aj=K`

**Mathematical Model:** `Aj=Ai-K`

**Core Transformation:** Hash lookup / two-pointer on sorted

**Why It Works:** Monotone in sorted order.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"`Ai-Aj=K`" -> unknowns named -> `Aj=Ai-K` -> Hash lookup / two-pointer on sorted -> O(cheap) check -> code
```

**Example:** K=2,[1,3,5] → 2 pairs

**Common Mistakes:** `K=0` special.

**Complexity:** O(n log n)

**Representative Problems:** —

### Form 14. Equal Remainders

**Recognition Signals:** "same remainder mod m"

**Mathematical Model:** `Ai ≡ Aj (mod m)`

**Core Transformation:** `m | (Ai-Aj)`; bucket by remainder

**Why It Works:** Congruence classes partition integers.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""same remainder mod m"" -> unknowns named -> `Ai ≡ Aj (mod m)` -> `m | (Ai-Aj)`; bucket by remainder -> O(cheap) check -> code
```

**Example:** m=3,[1,4,7,2] → class 1 has 3

**Common Mistakes:** Negative remainders.

**Complexity:** O(n)

**Representative Problems:** CF 577B

### Form 15. Consecutive Values

**Recognition Signals:** "consecutive integers", "forms 1..k"

**Mathematical Model:** after sorting `a_{i+1}=a_i+1`

**Core Transformation:** Check `max-min = n-1` and all distinct

**Why It Works:** n distinct ints spanning `n` values are consecutive.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""consecutive integers"" -> unknowns named -> after sorting `a_{i+1}=a_i+1` -> Check `max-min = n-1` and all distinct -> O(cheap) check -> code
```

**Example:** [3,5,4] → 5-3=2=n-1 ✓

**Common Mistakes:** Duplicates.

**Complexity:** O(n)

**Representative Problems:** permutation checks

### Form 16. Arithmetic Progression

**Recognition Signals:** "increase by d each step"

**Mathematical Model:** `a_k = a+(k-1)d`

**Core Transformation:** Sum `n(2a+(n-1)d)/2`; find `n` from quadratic bound

**Why It Works:** Pair first+last.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""increase by d each step"" -> unknowns named -> `a_k = a+(k-1)d` -> Sum `n(2a+(n-1)d)/2`; find `n` from quadratic bound -> O(cheap) check -> code
```

**Example:** 1+2+…+100=5050

**Common Mistakes:** Overflow; solving quadratic with float.

**Complexity:** O(1) / O(log)

**Representative Problems:** Part 7

### Form 17. Geometric / Doubling

**Recognition Signals:** "doubles", "halves"

**Mathematical Model:** `a_k = a r^k`

**Core Transformation:** steps ≈ `log_r`; sum `(r^k-1)/(r-1)`

**Why It Works:** Exponential growth reaches bound in ~log steps.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""doubles"" -> unknowns named -> `a_k = a r^k` -> steps ≈ `log_r`; sum `(r^k-1)/(r-1)` -> O(cheap) check -> code
```

**Example:** 1→1e9 by ×2 takes 30 steps

**Common Mistakes:** `2^k` overflow.

**Complexity:** O(log)

**Representative Problems:** CF 1475A

### Form 18. Median Optimization

**Recognition Signals:** "minimum total distance", "gather at a point"

**Mathematical Model:** `min_x Σ|x-ai|`

**Core Transformation:** `x = median` of sorted array

**Why It Works:** See Part 8 proof.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""minimum total distance"" -> unknowns named -> `min_x Σ|x-ai|` -> `x = median` of sorted array -> O(cheap) check -> code
```

**Example:** [1,2,10] → x=2, cost 9

**Common Mistakes:** Using mean; even n any point in `[a_{n/2},a_{n/2+1}]`.

**Complexity:** O(n log n)

**Representative Problems:** CF 1201C

### Form 19. Prefix Equation

**Recognition Signals:** "subarray with sum K"

**Mathematical Model:** `P[r]-P[l-1]=K`

**Core Transformation:** `P[l-1]=P[r]-K` map lookup

**Why It Works:** Subtraction telescopes.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""subarray with sum K"" -> unknowns named -> `P[r]-P[l-1]=K` -> `P[l-1]=P[r]-K` map lookup -> O(cheap) check -> code
```

**Example:** [1,2,3],K=3 → (1,2),(3)

**Common Mistakes:** Missing `P0=0`.

**Complexity:** O(n)

**Representative Problems:** Part 15

### Form 20. Contribution Counting

**Recognition Signals:** "sum over all subarrays/pairs"

**Mathematical Model:** `Σ_i A_i * ways_i`

**Core Transformation:** count structures containing each element

**Why It Works:** Sum swap: `Σ_structures Σ_elements = Σ_elements Σ_structures`.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""sum over all subarrays/pairs"" -> unknowns named -> `Σ_i A_i * ways_i` -> count structures containing each element -> O(cheap) check -> code
```

**Example:** n=3 subarray sums: coeffs 3,4,3

**Common Mistakes:** Wrong count of ways; overflow.

**Complexity:** O(n)

**Representative Problems:** Part 6

### Form 21. Pigeonhole

**Recognition Signals:** "prove/decide existence", huge n vs small modulus

**Mathematical Model:** `n objects > m boxes`

**Core Transformation:** If `n>=m` answer is yes

**Why It Works:** Prefix remainders `P0..Pn` have a repeat.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""prove/decide existence"" -> unknowns named -> `n objects > m boxes` -> If `n>=m` answer is yes -> O(cheap) check -> code
```

**Example:** n=5,m=3 → YES

**Common Mistakes:** Applying when `n<m`.

**Complexity:** O(1) then DP

**Representative Problems:** CF 577B

### Form 22. Inclusion-Exclusion

**Recognition Signals:** "none of", "at least one of", "divisible by any of"

**Mathematical Model:** `|∪Ai|`

**Core Transformation:** alternate signs over masks

**Why It Works:** Counts overlaps exactly once.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""none of"" -> unknowns named -> `|∪Ai|` -> alternate signs over masks -> O(cheap) check -> code
```

**Example:** multiples of 2 or 3 up to 10: 5+3-1=7

**Common Mistakes:** Sign errors; `2^k` blowup for large k.

**Complexity:** O(2^k)

**Representative Problems:** —

### Form 23. Invariant

**Recognition Signals:** "any number of times", "can transform"

**Mathematical Model:** `I(before)=I(after)`

**Core Transformation:** Find preserved quantity

**Why It Works:** Ops keep `I`; target has different `I` ⇒ impossible.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""any number of times"" -> unknowns named -> `I(before)=I(after)` -> Find preserved quantity -> O(cheap) check -> code
```

**Example:** See Part 10

**Common Mistakes:** Only necessary, need sufficiency proof.

**Complexity:** O(1)–O(n)

**Representative Problems:** CF 1538B

### Form 24. Monovariant

**Recognition Signals:** "process ends?", "how many steps at most"

**Mathematical Model:** strictly decreasing potential

**Core Transformation:** Bound steps by initial potential

**Why It Works:** Integer, bounded below ⇒ terminates.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""process ends?"" -> unknowns named -> strictly decreasing potential -> Bound steps by initial potential -> O(cheap) check -> code
```

**Example:** Each step reduces ΣA by ≥1

**Common Mistakes:** Potential not strictly monotone.

**Complexity:** O(potential)

**Representative Problems:** —

### Form 25. Reachability

**Recognition Signals:** "can reach", "is it possible"

**Mathematical Model:** state graph

**Core Transformation:** Characterize reachable set by invariants

**Why It Works:** Necessary via invariant, sufficient via construction.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""can reach"" -> unknowns named -> state graph -> Characterize reachable set by invariants -> O(cheap) check -> code
```

**Example:** Coins a,b make all multiples of gcd

**Common Mistakes:** Simulating instead of characterizing.

**Complexity:** O(1)

**Representative Problems:** CF 1401A

### Form 26. Constructive Equation

**Recognition Signals:** "construct any"

**Mathematical Model:** find `x` with `f(x)=target`

**Core Transformation:** Choose simplest satisfying family

**Why It Works:** Verify each property algebraically.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""construct any"" -> unknowns named -> find `x` with `f(x)=target` -> Choose simplest satisfying family -> O(cheap) check -> code
```

**Example:** `a+b=x`, min lcm → `1, x-1`

**Common Mistakes:** Missing edge cases (n=1,2).

**Complexity:** O(n)

**Representative Problems:** CF 1325A

### Form 27. Bounding

**Recognition Signals:** "minimum/maximum possible"

**Mathematical Model:** `lower <= ans <= upper`

**Core Transformation:** Prove lower bound, build matching solution

**Why It Works:** Bounds meet ⇒ equality.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""minimum/maximum possible"" -> unknowns named -> `lower <= ans <= upper` -> Prove lower bound, build matching solution -> O(cheap) check -> code
```

**Example:** CF 1263A

**Common Mistakes:** Bound not tight.

**Complexity:** O(1)

**Representative Problems:** CF 1263A

### Form 28. Extremal Principle

**Recognition Signals:** "largest/smallest element"

**Mathematical Model:** look at max/min

**Core Transformation:** Its constraint dominates

**Why It Works:** An optimal structure must handle the extremum.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""largest/smallest element"" -> unknowns named -> look at max/min -> Its constraint dominates -> O(cheap) check -> code
```

**Example:** largest pile needs partners

**Common Mistakes:** Assuming uniqueness.

**Complexity:** O(n)

**Representative Problems:** CF 1263A

### Form 29. Coordinate Transformation

**Recognition Signals:** "|x|+|y|", diagonal moves

**Mathematical Model:** `u=x+y,v=x-y`

**Core Transformation:** Manhattan → Chebyshev

**Why It Works:** Rotation makes constraints axis-aligned.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""|x|+|y|"" -> unknowns named -> `u=x+y,v=x-y` -> Manhattan → Chebyshev -> O(cheap) check -> code
```

**Example:** (3,1)→u=4,v=2

**Common Mistakes:** Parity of `u,v` mismatch.

**Complexity:** O(1)

**Representative Problems:** Part 12

### Form 30. Bit Independence

**Recognition Signals:** AND/OR/XOR

**Mathematical Model:** per-bit problems

**Core Transformation:** Solve 30 bit-problems, combine `2^b`

**Why It Works:** Bitwise ops don't mix positions.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"AND/OR/XOR" -> unknowns named -> per-bit problems -> Solve 30 bit-problems, combine `2^b` -> O(cheap) check -> code
```

**Example:** XOR of pairs by bit counts

**Common Mistakes:** Assuming independence with `+`.

**Complexity:** O(30 n)

**Representative Problems:** Part 17

### Form 31. Prime Factor Independence

**Recognition Signals:** gcd/lcm/divisibility

**Mathematical Model:** exponent vectors

**Core Transformation:** Per prime: `min`/`max`/compare

**Why It Works:** gcd=min exponent, lcm=max exponent.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"gcd/lcm/divisibility" -> unknowns named -> exponent vectors -> Per prime: `min`/`max`/compare -> O(cheap) check -> code
```

**Example:** 12=2²·3, 18=2·3²

**Common Mistakes:** Recomputing factorization every query.

**Complexity:** O(n log V)

**Representative Problems:** Part 3

### Form 32. Frequency Compression

**Recognition Signals:** order irrelevant

**Mathematical Model:** `cnt[v]`

**Core Transformation:** Work over distinct values

**Why It Works:** Symmetric problem depends only on counts.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"order irrelevant" -> unknowns named -> `cnt[v]` -> Work over distinct values -> O(cheap) check -> code
```

**Example:** [1,1,2] → {1:2,2:1}

**Common Mistakes:** Values too large for array → map.

**Complexity:** O(n)

**Representative Problems:** Part 13

### Form 33. Permutation Mathematics

**Recognition Signals:** "permutation of 1..n"

**Mathematical Model:** each value once

**Core Transformation:** `Σ=n(n+1)/2`, cycles, fixed points

**Why It Works:** Bijection.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""permutation of 1..n"" -> unknowns named -> each value once -> `Σ=n(n+1)/2`, cycles, fixed points -> O(cheap) check -> code
```

**Example:** [2,3,1]: one 3-cycle

**Common Mistakes:** 0- vs 1-index.

**Complexity:** O(n)

**Representative Problems:** Part 19

### Form 34. Mex Mathematics

**Recognition Signals:** "mex"

**Mathematical Model:** smallest missing

**Core Transformation:** `mex ≤ n`; need `0..k-1` present

**Why It Works:** Counts of `0..k-1` all ≥1.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""mex"" -> unknowns named -> smallest missing -> `mex ≤ n`; need `0..k-1` present -> O(cheap) check -> code
```

**Example:** [0,1,3] → 2

**Common Mistakes:** Confusing mex with min.

**Complexity:** O(n)

**Representative Problems:** —

### Form 35. Interval Mathematics

**Recognition Signals:** segments, overlaps

**Mathematical Model:** `[l,r]`

**Core Transformation:** Intersection `[max l, min r]`, sort by endpoint, sweep

**Why It Works:** Intervals order structure.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"segments" -> unknowns named -> `[l,r]` -> Intersection `[max l, min r]`, sort by endpoint, sweep -> O(cheap) check -> code
```

**Example:** [1,5]&[3,8]→[3,5]

**Common Mistakes:** Open vs closed ends.

**Complexity:** O(n log n)

**Representative Problems:** Part 16

### Form 36. Grid Parity

**Recognition Signals:** grid moves, tilings

**Mathematical Model:** `(x+y)%2`

**Core Transformation:** Reachability by parity and Manhattan

**Why It Works:** Each step flips color.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
"grid moves" -> unknowns named -> `(x+y)%2` -> Reachability by parity and Manhattan -> O(cheap) check -> code
```

**Example:** (0,0)→(1,2) in 3 steps ✓

**Common Mistakes:** Ignoring obstacles.

**Complexity:** O(1)

**Representative Problems:** CF 1401A

### Form 37. Cyclic / Modulo Process

**Recognition Signals:** "wraps around", "every k-th"

**Mathematical Model:** index `i mod n`

**Core Transformation:** Position after `t` steps: `(s+t·d) mod n`; cycle length `n/gcd(d,n)`

**Why It Works:** Orbit under addition mod n.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""wraps around"" -> unknowns named -> index `i mod n` -> Position after `t` steps: `(s+t·d) mod n`; cycle length `n/gcd(d,n)` -> O(cheap) check -> code
```

**Example:** n=6,d=4 → orbit size 3

**Common Mistakes:** Off-by-one 0/1 index.

**Complexity:** O(1)

**Representative Problems:** Part 4

### Form 38. Binary Search Equation

**Recognition Signals:** "maximize the minimum"

**Mathematical Model:** `can(X)` monotone

**Core Transformation:** Binary search over answer

**Why It Works:** Feasibility is monotone.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""maximize the minimum"" -> unknowns named -> `can(X)` monotone -> Binary search over answer -> O(cheap) check -> code
```

**Example:** CF 1201C

**Common Mistakes:** Non-monotone `can`.

**Complexity:** O(n log V)

**Representative Problems:** CF 1201C

### Form 39. Stars and Bars

**Recognition Signals:** "distribute identical objects"

**Mathematical Model:** `Σxi=n`

**Core Transformation:** `C(n+k-1,k-1)`

**Why It Works:** Bars separate stars.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""distribute identical objects"" -> unknowns named -> `Σxi=n` -> `C(n+k-1,k-1)` -> O(cheap) check -> code
```

**Example:** n=4,k=3 → C(6,2)=15

**Common Mistakes:** Mixed lower bounds.

**Complexity:** O(1) with precomputed factorials

**Representative Problems:** Part 6

### Form 40. Diophantine Equation

**Recognition Signals:** "pay exactly", "packs of"

**Mathematical Model:** `ax+by=c`

**Core Transformation:** gcd test, bounded enumeration

**Why It Works:** Bézout.

**Visual Example:**

```text
Statement -> Variables -> Equation -> Transformation -> Simplified condition -> Algorithm
""pay exactly"" -> unknowns named -> `ax+by=c` -> gcd test, bounded enumeration -> O(cheap) check -> code
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
| Manhattan reach | BFS on grid | `|dx|+|dy|<=k` + parity | B |
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
| Divisibility | integrality | show `d | expr` | `gcd | difference` |
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

