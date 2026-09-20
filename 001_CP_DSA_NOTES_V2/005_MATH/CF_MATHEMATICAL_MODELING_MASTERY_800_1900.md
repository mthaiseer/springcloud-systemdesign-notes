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
vector<int> spf(N+1);
for (int i=2;i<=N;i++) if(!spf[i]) for(int j=i;j<=N;j+=i) if(!spf[j]) spf[j]=i;
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
long long pw(long long a,long long e,long long m){long long r=1;a%=m;
  while(e){if(e&1)r=r*a%m;a=a*a%m;e>>=1;}return r;}
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
long long eg(long long a,long long b,long long&x,long long&y){
  if(!b){x=1;y=0;return a;}
  long long x1,y1,g=eg(b,a%b,x1,y1); x=y1; y=x1-(a/b)*y1; return g;}
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
long long lo=0, hi=INF;               // find max x with can(x)
while(lo<hi){ long long mid=lo+(hi-lo+1)/2; if(can(mid)) lo=mid; else hi=mid-1; }
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

Each problem hides the algorithm behind steps 1–8: read only those and re-derive the solution before looking at code.

## P01. Watermelon (CF 4A)

Problem Link: [Watermelon](https://codeforces.com/problemset/problem/4/A)

**Rating:** 800 (approximate)

**Primary Mathematical Form:** Parity Constraint

**Secondary Forms:** Bounding

### 1. Story Removed

Given integer w. Decide whether w = a + b with a, b both even and positive.

### 2. Variables

```text
w = weight; a, b = the two parts (integers).
```

### 3. Constraints

w <= 100. Anything works; O(1) formula.

### 4. Direct Mathematical Model

```text
Need: a+b=w, a mod 2=0, b mod 2=0, a>=1, b>=1.
```

### 5. Transformation

```text
a,b even, positive ⇒ a>=2, b>=2 ⇒ w=a+b>=4.
Sum of two evens is even ⇒ w even.
Conversely, w even, w>=4 ⇒ take a=2, b=w-2 (even, >=2).
```

### 6. Feasibility Conditions

```text
w % 2 == 0 and w >= 4
```

### 7. Core Observation

even+even=even, and 'positive even' means at least 2.

### 8. Mathematical Invariant / Proof

Necessity: derived above. Sufficiency: explicit construction (2, w-2).

### 9. Statement → Math → Algorithm

```text
Story -> w=a+b -> a,b even >=2 -> w even and w>=4 -> O(1) test
```

### 10. Dry Run

w=8: even, >=4 → (2,6) → YES. w=2: even but <4 → NO. w=7: odd → NO.

### 11. Pseudocode

```text
read w
print (w%2==0 and w>=4) ? YES : NO
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int w; std::cin>>w; puts((w%2==0 && w>=4)?"YES":"NO"); }
```

### 13. What I Should Recognize Next Time

```text
Whenever I see "split into parts with a property":
→ write the smallest allowed part (here 2), then parity of the sum.
```

## P02. Yet Another Two Integers Problem (CF 1409A)

Problem Link: [Yet Another Two Integers Problem](https://codeforces.com/problemset/problem/1409/A)

**Rating:** 800 (approximate)

**Primary Mathematical Form:** Arithmetic / Ceil Division

**Secondary Forms:** Bounding

### 1. Story Removed

Given a,b. One move changes a by any integer in [-10,10]. Minimum moves to reach b.

### 2. Variables

```text
a, b; d = |a-b|.
```

### 3. Constraints

a,b <= 1e9; t <= 2e4 tests. Need O(1) per test.

### 4. Direct Mathematical Model

```text
Each move changes the gap d by at most 10. Need smallest k with 10k >= d.
```

### 5. Transformation

```text
k = ceil(d / 10) = (d + 9) / 10
```

### 6. Feasibility Conditions

```text
Always possible (d=0 ⇒ 0 moves).
```

### 7. Core Observation

Every move covers at most 10 units; use maximal steps, last one partial.

### 8. Mathematical Invariant / Proof

Lower bound: k moves cover <=10k, so k>=d/10. Construction: k-1 full moves + one partial move covering the rest (<=10).

### 9. Statement → Math → Algorithm

```text
Story -> gap d -> each move reduces d by <=10 -> k>=d/10 -> ceil -> O(1)
```

### 10. Dry Run

a=13,b=42 → d=29 → (29+9)/10=3. a=5,b=5 → 0.

### 11. Pseudocode

```text
d=|a-b|; print (d+9)/10
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int t; scanf("%d",&t); while(t--){ long long a,b; scanf("%lld %lld",&a,&b);
  long long d=llabs(a-b); printf("%lld\n",(d+9)/10);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever "each move changes by at most K":
→ answer = ceil(distance / K).
```

## P03. Friends and Candies (CF 1538B)

Problem Link: [Friends and Candies](https://codeforces.com/problemset/problem/1538/B)

**Rating:** 800 (approximate)

**Primary Mathematical Form:** Sum Constraint

**Secondary Forms:** Invariant (Sum)

### 1. Story Removed

n friends with a_i candies. Choose k friends, pool their candies, redistribute arbitrarily among ALL friends. Minimum k so all equal.

### 2. Variables

```text
n; A_i; S = ΣA_i; t = target each.
```

### 3. Constraints

n <= 2e5, sum fits in long long. O(n).

### 4. Direct Mathematical Model

```text
Redistribution never changes total S. Equal ⇒ n·t = S.
```

### 5. Transformation

```text
Need S % n == 0, t = S/n.
Friends with a_i <= t don't need to give; friends with a_i > t MUST be chosen.
Choosing exactly those makes all equal (their excess redistributes to deficient ones).
```

### 6. Feasibility Conditions

```text
S % n == 0, else −1.
```

### 7. Core Observation

Total is invariant; only above-average friends must be included.

### 8. Mathematical Invariant / Proof

Any friend with a_i>t must be chosen (else keeps a_i≠t). Choosing all of them is enough: pooled excess = total deficit of others.

### 9. Statement → Math → Algorithm

```text
Story -> sum invariant S -> n t = S -> t integer? -> count a_i>t -> O(n)
```

### 10. Dry Run

A=[4,5,2,5], S=16, t=4 → a_i>4: two 5s → answer 2.

### 11. Pseudocode

```text
S=ΣA; if S%n: -1 else count(A_i > S/n)
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int T; scanf("%d",&T); while(T--){ int n; scanf("%d",&n);
  vector<long long> a(n); long long s=0; for(auto&x:a){scanf("%lld",&x); s+=x;}
  if(s%n){puts("-1"); continue;} long long t=s/n; int c=0; for(auto x:a) c+=x>t;
  printf("%d\n",c);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever "redistribute / equalize":
→ sum is invariant → target = average → integrality check → count those above.
```

## P04. EhAb AnD gCd (CF 1325A)

Problem Link: [EhAb AnD gCd](https://codeforces.com/problemset/problem/1325/A)

**Rating:** 800 (approximate)

**Primary Mathematical Form:** Constructive Equation

**Secondary Forms:** GCD/LCM Constraint

### 1. Story Removed

Given x. Output any positive integers a,b with lcm(a,b) + gcd(a,b) = x.

### 2. Variables

```text
x; a, b unknown; g=gcd(a,b), L=lcm(a,b).
```

### 3. Constraints

x <= 1e9, t <= 1e4. Must be O(1): a construction, not a search.

### 4. Direct Mathematical Model

```text
L + g = x, with g | a, g | b, L multiple of both.
```

### 5. Transformation

```text
Try the simplest family: a=1. Then gcd(1,b)=1 and lcm(1,b)=b.
Equation becomes b + 1 = x ⇒ b = x-1.
```

### 6. Feasibility Conditions

```text
x >= 2 guaranteed ⇒ b = x-1 >= 1.
```

### 7. Core Observation

Choose a=1 so both gcd and lcm collapse to trivial values.

### 8. Mathematical Invariant / Proof

gcd(1,x-1)=1, lcm(1,x-1)=x-1, sum=x. Any valid pair is accepted (checker).

### 9. Statement → Math → Algorithm

```text
Story -> lcm+gcd=x -> pick a=1 (kills gcd) -> b=x-1 -> O(1)
```

### 10. Dry Run

x=2 → (1,1): lcm 1 + gcd 1 = 2 ✓. x=14 → (1,13): 13+1=14 ✓.

### 11. Pseudocode

```text
print 1, x-1
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long x; scanf("%lld",&x); printf("1 %lld\n",x-1);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever "construct any" with gcd/lcm in the equation:
→ try 1 (and equal numbers). 1 makes gcd=1, lcm=other.
```

## P05. Odd Divisor (CF 1475A)

Problem Link: [Odd Divisor](https://codeforces.com/problemset/problem/1475/A)

**Rating:** 900 (approximate)

**Primary Mathematical Form:** Number Theory / Powers of Two

**Secondary Forms:** Parity Constraint

### 1. Story Removed

Given n. Does n have an odd divisor greater than 1?

### 2. Variables

```text
n up to 1e14.
```

### 3. Constraints

n <= 1e14, t <= 1e4 ⇒ O(√n) too slow-ish; need O(1)/O(log).

### 4. Direct Mathematical Model

```text
Exists odd d>1 with d | n.
```

### 5. Transformation

```text
Write n = 2^k · m with m odd. If m>1 then m itself is an odd divisor >1.
If m=1 then n=2^k, all divisors are powers of two: only odd divisor is 1.
⇒ answer NO iff n is a power of two.
```

### 6. Feasibility Conditions

```text
n & (n-1) != 0 ⇒ YES.
```

### 7. Core Observation

Prime factor independence: only the exponent-of-2 part is irrelevant.

### 8. Mathematical Invariant / Proof

Divisors of 2^k are 2^j; only odd one is 1. Otherwise m>1 odd divides n.

### 9. Statement → Math → Algorithm

```text
Story -> odd divisor >1 -> n=2^k·m -> m>1? -> power-of-two test -> O(1)
```

### 10. Dry Run

n=6=2·3 → 3 odd → YES. n=16 → NO. n=5 → YES.

### 11. Pseudocode

```text
if (n & (n-1)) == 0: NO else YES
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long n; scanf("%lld",&n);
  puts((n&(n-1))?"YES":"NO"); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever "odd divisor" / "not a power of two":
→ factor n = 2^k · odd; check the odd part.
```

## P06. Honest Coach (CF 1360B)

Problem Link: [Honest Coach](https://codeforces.com/problemset/problem/1360/B)

**Rating:** 800 (approximate)

**Primary Mathematical Form:** Sorting as Transformation

**Secondary Forms:** Greedy

### 1. Story Removed

Split n athletes (strengths s_i) into two non-empty teams A, B minimizing |max(A) - min(B)|.

### 2. Variables

```text
s_i; max(A)=x, min(B)=y.
```

### 3. Constraints

n <= 50 (tiny). Generic O(n log n).

### 4. Direct Mathematical Model

```text
Minimize |max(A)-min(B)| over partitions.
```

### 5. Transformation

```text
Sort: s1<=...<=sn. Put the smallest k in A, the rest in B ⇒ value s_{k+1}-s_k.
Any partition has |max(A)-min(B)| >= min adjacent gap (any two distinct athletes bound a gap).
```

### 6. Feasibility Conditions

```text
Always n>=2.
```

### 7. Core Observation

The optimum equals the minimum gap between adjacent elements in sorted order.

### 8. Mathematical Invariant / Proof

Lower bound: for any pair (a∈A,b∈B), |a-b| >= smallest adjacent difference in sorted order. Upper bound: cut the sorted array at the minimizing gap.

### 9. Statement → Math → Algorithm

```text
Story -> minimize |maxA-minB| -> sort -> gap between neighbors -> min adjacent diff -> O(n log n)
```

### 10. Dry Run

[3,1,2,6,4] sorted [1,2,3,4,6]: gaps 1,1,1,2 → answer 1.

### 11. Pseudocode

```text
sort s; ans=min(s[i+1]-s[i])
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int t; scanf("%d",&t); while(t--){ int n; scanf("%d",&n); vector<int> s(n);
  for(auto&x:s) scanf("%d",&x); sort(s.begin(),s.end()); int ans=INT_MAX;
  for(int i=0;i+1<n;i++) ans=min(ans,s[i+1]-s[i]); printf("%d\n",ans);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever "minimize difference between two chosen groups' extremes":
→ sort, look at adjacent gaps.
```

## P07. Required Remainder (CF 1374A)

Problem Link: [Required Remainder](https://codeforces.com/problemset/problem/1374/A)

**Rating:** 1000 (approximate)

**Primary Mathematical Form:** Modulo Constraint

**Secondary Forms:** Divisibility

### 1. Story Removed

Given x,y,n. Find the maximum k with 0<=k<=n and k mod x = y.

### 2. Variables

```text
x, y (y<x), n (n>=y guaranteed).
```

### 3. Constraints

up to 1e9, t <= 5e4; O(1).

### 4. Direct Mathematical Model

```text
k = q·x + y, 0<=k<=n, maximize q.
```

### 5. Transformation

```text
q·x + y <= n ⇒ q <= (n-y)/x ⇒ q = floor((n-y)/x).
k = floor((n-y)/x)·x + y.
```

### 6. Feasibility Conditions

```text
n>=y guaranteed, so q>=0.
```

### 7. Core Observation

Numbers with remainder y form the AP y, y+x, y+2x, ...; take the last one <= n.

### 8. Mathematical Invariant / Proof

Monotone in q; largest feasible q is the floor.

### 9. Statement → Math → Algorithm

```text
Story -> k≡y (mod x) -> k=qx+y -> qx+y<=n -> q=floor((n-y)/x) -> O(1)
```

### 10. Dry Run

x=7,y=5,n=12345 → q=floor(12340/7)=1762 → k=1762·7+5=12339.

### 11. Pseudocode

```text
print (n-y)/x*x + y
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long x,y,n; scanf("%lld %lld %lld",&x,&y,&n);
  printf("%lld\n",(n-y)/x*x+y);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever "largest/smallest number ≤ n with remainder r":
→ AP y + q·x, solve for q by floor/ceil.
```

## P08. New Year's Number (CF 1475B)

Problem Link: [New Year's Number](https://codeforces.com/problemset/problem/1475/B)

**Rating:** 900 (approximate)

**Primary Mathematical Form:** Diophantine Equation

**Secondary Forms:** Modulo Constraint

### 1. Story Removed

Given n. Is n = 2020a + 2021b for non-negative integers a,b?

### 2. Variables

```text
n; a,b >= 0.
```

### 3. Constraints

n <= 1e6, t <= 1e4. O(1) or O(n/2020) enumeration both fine.

### 4. Direct Mathematical Model

```text
2020a + 2021b = n.
```

### 5. Transformation

```text
Let t=a+b, then n = 2020(a+b) + b = 2020 t + b.
So b = n - 2020 t. Need 0 <= b <= t.
Choose t = floor(n/2020): then b = n mod 2020. Need n mod 2020 <= floor(n/2020).
```

### 6. Feasibility Conditions

```text
(n % 2020) <= (n / 2020)
```

### 7. Core Observation

Rewrite 2021 = 2020 + 1 so both coins become 'one 2020 plus optional +1'.

### 8. Mathematical Invariant / Proof

Any solution has n mod 2020 ≡ b (mod 2020). Smallest b is n mod 2020 (larger b only demands larger t). Feasible iff that b ≤ t = floor(n/2020).

### 9. Statement → Math → Algorithm

```text
Story -> 2020a+2021b=n -> t=a+b -> n=2020t+b -> b=n%2020 -> b<=t? -> O(1)
```

### 10. Dry Run

n=4041: t=2, b=1<=2 ✓ (a=1,b=1). n=4042: t=2,b=2<=2 ✓. n=2021: t=1,b=1 ✓. n=4039: t=1, b=2019 >1 ✗.

### 11. Pseudocode

```text
print (n%2020 <= n/2020) ? YES : NO
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ int n; scanf("%d",&n); puts((n%2020<=n/2020)?"YES":"NO"); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever two coin values differ by 1 (a and a+1):
→ let t=count of coins; total = a·t + b (b = number of larger coins), bound b<=t.
```

## P09. Exciting Bets (CF 1543A)

Problem Link: [Exciting Bets](https://codeforces.com/problemset/problem/1543/A)

**Rating:** 900 (approximate)

**Primary Mathematical Form:** GCD Constraint

**Secondary Forms:** Modulo Constraint / Invariant

### 1. Story Removed

Given a,b. One move: both +1 or both −1 (−1 only if both >0). Maximize gcd(a,b) and output minimum moves for it.

### 2. Variables

```text
a,b; d=|a-b|.
```

### 3. Constraints

up to 1e18 ⇒ long long, O(1).

### 4. Direct Mathematical Model

```text
Moves keep a−b constant. gcd(a+k,b+k) divides (a−b).
```

### 5. Transformation

```text
gcd(x,y) | (x−y)=±d. So gcd ≤ d.
If d=0: gcd can grow forever ⇒ answer '0 0'.
Else max is d, reached when a+k ≡ 0 (mod d): k = (d − a mod d) mod d forward, or a mod d backward.
moves = min(a mod d, d − a mod d).
```

### 6. Feasibility Conditions

```text
a==b → 0 0 (infinite). Else (d, min(r, d-r)), r=a%d.
```

### 7. Core Observation

Difference invariant caps the gcd at d; a shift aligns a to a multiple of d.

### 8. Mathematical Invariant / Proof

Upper bound gcd<=d from divisibility. Achieve by moving a to nearest multiple of d (then b is also a multiple since b=a±d). Backward moves feasible because a>=r.

### 9. Statement → Math → Algorithm

```text
Story -> difference a-b invariant -> gcd | d -> max=d -> shift to multiple -> min(r,d-r) -> O(1)
```

### 10. Dry Run

a=8,b=5: d=3, r=8%3=2 → moves min(2,1)=1 → 3 1.

### 11. Pseudocode

```text
if a==b: '0 0' else d=|a-b|; r=a%d; print d, min(r,d-r)
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long a,b; scanf("%lld %lld",&a,&b);
  if(a==b){puts("0 0"); continue;} long long d=llabs(a-b), r=a%d; printf("%lld %lld\n",d,std::min(r,d-r)); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever an operation shifts both numbers equally:
→ difference is invariant → gcd divides the difference.
```

## P10. K-divisible Sum (CF 1476A)

Problem Link: [K-divisible Sum](https://codeforces.com/problemset/problem/1476/A)

**Rating:** 1000 (approximate)

**Primary Mathematical Form:** Bounding

**Secondary Forms:** Divisibility / Ceil Division

### 1. Story Removed

Given n,k. Choose positive array of length n with sum divisible by k minimizing the max element.

### 2. Variables

```text
n,k; S=sum; M=max.
```

### 3. Constraints

up to 1e9 ⇒ formula.

### 4. Direct Mathematical Model

```text
S = k·c (c>=1), S >= n (positive). Minimize M >= ceil(S/n).
```

### 5. Transformation

```text
Smallest valid S: least multiple of k that is >= n: S = ceil(n/k)·k.
M = ceil(S/n).
```

### 6. Feasibility Conditions

```text
Always.
```

### 7. Core Observation

Smaller S can't hurt: max ≥ average.

### 8. Mathematical Invariant / Proof

Lower bound: M >= S/n >= S_min/n. Construction: spread S_min as evenly as possible (values ceil/floor of S/n; all ≥1 since S≥n).

### 9. Statement → Math → Algorithm

```text
Story -> S multiple of k, S>=n -> S_min=ceil(n/k)k -> M=ceil(S_min/n) -> O(1)
```

### 10. Dry Run

n=4,k=3: S=6, M=ceil(6/4)=2. n=8,k=8: S=8, M=1.

### 11. Pseudocode

```text
S=((n+k-1)/k)*k; print (S+n-1)/n
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long n,k; scanf("%lld %lld",&n,&k);
  long long S=(n+k-1)/k*k; printf("%lld\n",(S+n-1)/n); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever "minimize max, sum constrained":
→ max ≥ ceil(sum/n); choose smallest legal sum; spread evenly.
```

## P11. Distance and Axis (CF 1401A)

Problem Link: [Distance and Axis](https://codeforces.com/problemset/problem/1401/A)

**Rating:** 1100 (approximate)

**Primary Mathematical Form:** Grid Parity

**Secondary Forms:** Parity Constraint / Bounding

### 1. Story Removed

O is the origin, A is at x=n on a line. Find an integer point B with |OB − AB| = k. You may move A by ±1 per step. Minimum steps until such B exists.

### 2. Variables

```text
n = position of A (n>=0); k; B = b (integer).
```

### 3. Constraints

n,k <= 1e6, t <= 6000 ⇒ O(1) per test.

### 4. Direct Mathematical Model

```text
|OB| − |AB| = ±k  i.e. | |b| − |n−b| | = k.
```

### 5. Transformation

```text
Triangle inequality: | |OB| − |AB| | <= OA = n ⇒ need k <= n.
For 0<=b<=n: OB−AB = b − (n−b) = 2b − n = ±k ⇒ b = (n±k)/2, integer iff (n+k) even (⇔ (n−k) even).
So: if k<=n and (n−k) even ⇒ 0 steps.
If k<=n and (n−k) odd ⇒ move A one step (parity flips) ⇒ 1 step.
If k>n ⇒ move A right to n=k ⇒ k−n steps (then n=k, difference 0, even).
```

### 6. Feasibility Conditions

```text
n<k: k−n;  else (n−k) % 2
```

### 7. Core Observation

Two obstacles only: too close (n<k, triangle inequality) or wrong parity (b must be an integer).

### 8. Mathematical Invariant / Proof

Necessity: triangle inequality and integrality of b. Sufficiency: explicit b=(n+k)/2. Lower bound for n<k: each step changes n by 1.

### 9. Statement → Math → Algorithm

```text
Story -> |OB-AB|=k -> triangle: k<=n -> 2b-n=±k -> parity of n-k -> cases -> O(1)
```

### 10. Dry Run

n=4,k=0: (4−0)%2=0. n=5,k=8: 3. n=0,k=1000000: 1000000. n=1,k=0: 1 (parity).

### 11. Pseudocode

```text
if n<k: k-n else (n-k)%2
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ int n,k; scanf("%d %d",&n,&k);
  printf("%d\n", n<k? k-n : (n-k)%2); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever a distance-difference equation on a line appears:
→ triangle inequality gives a size bound; halving gives an integrality (parity) condition.
```

## P12. K-th Not Divisible by n (CF 1352C)

Problem Link: [K-th Not Divisible by n](https://codeforces.com/problemset/problem/1352/C)

**Rating:** 1200 (approximate)

**Primary Mathematical Form:** Divisibility / Counting

**Secondary Forms:** Bounding / Binary Search

### 1. Story Removed

Given n,k. Find k-th positive integer not divisible by n.

### 2. Variables

```text
n>=2; k<=1e9.
```

### 3. Constraints

values up to 1e9·… → long long; O(1).

### 4. Direct Mathematical Model

```text
Numbers grouped in blocks of size n; each block has n−1 non-multiples.
```

### 5. Transformation

```text
After q full blocks of (n−1) valid numbers: (q = (k−1)/(n−1)). Answer = k + q.
Check: each skipped multiple pushes the answer up by one.
```

### 6. Feasibility Conditions

```text
Always.
```

### 7. Core Observation

Among 1..x there are x − floor(x/n) non-multiples; need this = k.

### 8. Mathematical Invariant / Proof

Induction on skipped multiples: answer = k + (# multiples of n ≤ answer). With q=(k−1)/(n−1) multiples passed.

### 9. Statement → Math → Algorithm

```text
Story -> count non-multiples -> blocks of n-1 valid -> q=(k-1)/(n-1) -> k+q -> O(1)
```

### 10. Dry Run

n=3,k=7: valid 1,2,4,5,7,8,10 → 10. Formula: q=6/2=3 → 10 ✓.

### 11. Pseudocode

```text
print k + (k-1)/(n-1)
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long n,k; scanf("%lld %lld",&n,&k);
  printf("%lld\n",k+(k-1)/(n-1)); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever "k-th number not divisible by n":
→ blocks of n numbers hold n−1 valid; answer = k + (k−1)/(n−1).
```

## P13. Sweet Problem (CF 1263A)

Problem Link: [Sweet Problem](https://codeforces.com/problemset/problem/1263/A)

**Rating:** 1200 (approximate)

**Primary Mathematical Form:** Bounding

**Secondary Forms:** Extremal Principle

### 1. Story Removed

Three piles of candies a,b,c. Each day eat one candy from each of two different piles. Maximum days.

### 2. Variables

```text
a,b,c; S=a+b+c; M=max.
```

### 3. Constraints

up to 1e8, O(1).

### 4. Direct Mathematical Model

```text
Each day removes exactly 2 candies from 2 distinct piles.
```

### 5. Transformation

```text
Bound 1: days <= S/2 (2 per day).
Bound 2: the largest pile M needs partners: days <= S−M (each day uses at most one candy from M, and every day uses ≥1 candy outside M... days ≤ (others' total) if M dominates).
Answer = min(floor(S/2), S−M).
```

### 6. Feasibility Conditions

```text
Always.
```

### 7. Core Observation

Either resources (total) or the dominant pile bounds the result.

### 8. Mathematical Invariant / Proof

If M <= S−M pair greedily largest two ⇒ floor(S/2). Else pair M with others, S−M days.

### 9. Statement → Math → Algorithm

```text
Story -> 2 candies/day -> S/2 bound -> largest-pile bound -> min of bounds -> O(1)
```

### 10. Dry Run

(1,1,1): S=3,M=1 → min(1,2)=1. (1,2,3): S=6,M=3 → min(3,3)=3. (1,1,10): min(6,2)=2.

### 11. Pseudocode

```text
print min(S/2, S-M)
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
int main(){ int t; scanf("%d",&t); while(t--){ long long a,b,c; scanf("%lld %lld %lld",&a,&b,&c);
  long long S=a+b+c, M=std::max({a,b,c}); printf("%lld\n",std::min(S/2,S-M)); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever "each step consumes from two different piles":
→ min(total/2, total − max).
```

## P14. Same Differences (CF 1520D)

Problem Link: [Same Differences](https://codeforces.com/problemset/problem/1520/D)

**Rating:** 1200 (approximate)

**Primary Mathematical Form:** Pair Counting

**Secondary Forms:** Difference Constraint / Algebra

### 1. Story Removed

Array a of n. Count pairs i<j with a_j − a_i = j − i.

### 2. Variables

```text
a_i, index i.
```

### 3. Constraints

n <= 2e5 ⇒ O(n²) too slow; O(n) with hashing.

### 4. Direct Mathematical Model

```text
a_j − a_i = j − i.
```

### 5. Transformation

```text
Rearrange: a_j − j = a_i − i. Define b_i = a_i − i.
Count pairs with b_i = b_j.
```

### 6. Feasibility Conditions

```text
Answer = Σ_v f_v (f_v−1)/2.
```

### 7. Core Observation

Separating variables i and j on opposite sides decouples the pair condition.

### 8. Mathematical Invariant / Proof

Equivalence: equations equal after moving terms. Pairs in same b-group are exactly valid pairs.

### 9. Statement → Math → Algorithm

```text
Story -> a_j-a_i=j-i -> a_j-j = a_i-i -> b_i=a_i-i -> equal pairs -> frequency map -> O(n)
```

### 10. Dry Run

a=[3,5,1,4,6,6], i=1..6 → b=[2,3,-2,0,1,0]; equal pairs: b=0 twice ⇒ 1.

### 11. Pseudocode

```text
map cnt; for i: ans += cnt[a_i-i]++
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int t; scanf("%d",&t); while(t--){ int n; scanf("%d",&n); map<long long,long long> c; long long ans=0;
  for(int i=1;i<=n;i++){ long long a; scanf("%lld",&a); ans+=c[a-i]++; } printf("%lld\n",ans);} }
```

### 13. What I Should Recognize Next Time

```text
Whenever a pair condition mixes value and index:
→ move all terms of j to one side, all i to the other; count equal keys.
```

## P15. Number of Pairs (CF 1538C)

Problem Link: [Number of Pairs](https://codeforces.com/problemset/problem/1538/C)

**Rating:** 1300 (approximate)

**Primary Mathematical Form:** Sorting as Transformation

**Secondary Forms:** Pair Counting / Two Pointers

### 1. Story Removed

Array a. Count pairs i<j with l <= a_i + a_j <= r.

### 2. Variables

```text
a_i; l,r.
```

### 3. Constraints

n <= 2e5 ⇒ O(n log n).

### 4. Direct Mathematical Model

```text
L <= a_i+a_j <= R, unordered pairs.
```

### 5. Transformation

```text
Count(≤R) − Count(≤L−1). Order is irrelevant ⇒ sort.
For each i, j>i with a_j <= X − a_i is a prefix in sorted order ⇒ upper_bound.
```

### 6. Feasibility Conditions

```text
ans = f(r) − f(l−1)
```

### 7. Core Observation

Inequality on sum + sorted array ⇒ monotone boundary.

### 8. Mathematical Invariant / Proof

Pair set is symmetric ⇒ sorting doesn't change the count; for fixed i the valid j form a contiguous range because a_j sorted.

### 9. Statement → Math → Algorithm

```text
Story -> count pairs with L<=sum<=R -> complement/prefix trick f(R)-f(L-1) -> sort -> upper_bound -> O(n log n)
```

### 10. Dry Run

[5,1,2], L=4,R=7: sorted [1,2,5]; sums 3,6,7 → in range: 6,7 → 2.

### 11. Pseudocode

```text
sort a; f(X)=Σ_i (upper_bound(a[i+1..], X-a[i]) − (i+1)); ans=f(r)−f(l−1)
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int t; scanf("%d",&t); while(t--){ int n; long long l,r; scanf("%d %lld %lld",&n,&l,&r);
  vector<long long> a(n); for(auto&x:a) scanf("%lld",&x); sort(a.begin(),a.end());
  auto f=[&](long long X){ long long c=0; for(int i=0;i<n;i++) c+= upper_bound(a.begin()+i+1,a.end(),X-a[i])-(a.begin()+i+1); return c; };
  printf("%lld\n",f(r)-f(l-1)); } }
```

### 13. What I Should Recognize Next Time

```text
Whenever "count pairs with sum in [L,R]":
→ sort, count ≤R minus ≤L−1, binary search / two pointers.
```

## P16. Pair of Topics (CF 1324D)

Problem Link: [Pair of Topics](https://codeforces.com/problemset/problem/1324/D)

**Rating:** 1400 (approximate)

**Primary Mathematical Form:** Algebra / Transformation

**Secondary Forms:** Sorting, Pair Counting

### 1. Story Removed

Two arrays a,b. Count i<j with a_i + a_j > b_i + b_j.

### 2. Variables

```text
a_i, b_i; c_i = a_i − b_i.
```

### 3. Constraints

n <= 2e5 ⇒ O(n log n).

### 4. Direct Mathematical Model

```text
a_i+a_j > b_i+b_j.
```

### 5. Transformation

```text
Move b to left: (a_i−b_i)+(a_j−b_j) > 0 ⇒ c_i + c_j > 0.
Unordered pair ⇒ sort c; for each i count j>i with c_j > −c_i.
```

### 6. Feasibility Conditions

```text
ans=Σ_i #{j>i : c_j > −c_i}
```

### 7. Core Observation

Decouple i and j: condition depends on one derived value per index.

### 8. Mathematical Invariant / Proof

Algebraic equivalence; counting unordered pairs is order-independent.

### 9. Statement → Math → Algorithm

```text
Story -> a_i+a_j>b_i+b_j -> c_i=a_i-b_i -> c_i+c_j>0 -> sort -> upper_bound -> O(n log n)
```

### 10. Dry Run

a=[4,8,2,6,2],b=[4,5,4,1,3]: c=[0,3,-2,5,-1] → pairs with sum>0: (0,3),(0,5),(3,-2),(3,-1),(3,5),(-2,5),(5,-1) = 7.

### 11. Pseudocode

```text
c=a-b; sort; for i: ans += n − upper_bound(c[i+1..], −c[i])
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int n; scanf("%d",&n); vector<long long> a(n),c(n); for(auto&x:a) scanf("%lld",&x);
  for(int i=0;i<n;i++){ long long b; scanf("%lld",&b); c[i]=a[i]-b; } sort(c.begin(),c.end());
  long long ans=0; for(int i=0;i<n;i++) ans+= c.end()-upper_bound(c.begin()+i+1,c.end(),-c[i]);
  printf("%lld\n",ans); }
```

### 13. What I Should Recognize Next Time

```text
Whenever a pair condition has form f(i)+f(j) with data spread across two arrays:
→ merge into one array c_i, then sorted pair counting.
```

## P17. Maximum Median (CF 1201C)

Problem Link: [Maximum Median](https://codeforces.com/problemset/problem/1201/C)

**Rating:** 1400 (approximate)

**Primary Mathematical Form:** Binary Search Equation

**Secondary Forms:** Median Optimization / Greedy

### 1. Story Removed

Array of odd length n, at most k +1 operations on any elements. Maximize the median.

### 2. Variables

```text
a sorted; m=(n−1)/2 (0-index median); x = target median.
```

### 3. Constraints

n <= 2e5, k <= 1e9 ⇒ O(n log V).

### 4. Direct Mathematical Model

```text
Median ≥ x ⇔ elements at indices ≥ m all ≥ x.
```

### 5. Transformation

```text
Cost(x)=Σ_{i>=m} max(0, x − a_i) ≤ k. Cost is monotone in x ⇒ binary search x in [a_m, a_m + k].
```

### 6. Feasibility Conditions

```text
max x with cost(x) ≤ k
```

### 7. Core Observation

Only upper half matters; raising smaller elements is wasteful.

### 8. Mathematical Invariant / Proof

Median ≥ x needs (n+1)/2 elements ≥ x; cheapest to use the largest ones already near x. Monotone ⇒ binary search valid.

### 9. Statement → Math → Algorithm

```text
Story -> maximize median -> guess x -> can(x): cost of upper half -> monotone -> binary search -> O(n log V)
```

### 10. Dry Run

[1,3,5], k=2: sorted, m=1. cost(4)=(4−3)+0=1 ✓, cost(5)=2 ✓, cost(6)=3+1=4 ✗ ⇒ answer 5.

### 11. Pseudocode

```text
sort a; lo=a[m],hi=a[m]+k; binary search max x with cost(x)<=k
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int n; long long k; scanf("%d %lld",&n,&k); vector<long long> a(n); for(auto&x:a) scanf("%lld",&x);
  sort(a.begin(),a.end()); int m=(n-1)/2; long long lo=a[m],hi=a[m]+k;
  while(lo<hi){ long long mid=lo+(hi-lo+1)/2, cost=0; for(int i=m;i<n;i++) if(a[i]<mid) cost+=mid-a[i];
    if(cost<=k) lo=mid; else hi=mid-1; } printf("%lld\n",lo); }
```

### 13. What I Should Recognize Next Time

```text
Whenever "maximize median / minimum with limited increments":
→ binary search on the target; cost is a sum of shortfalls.
```

## P18. Modulo Sum (CF 577B)

Problem Link: [Modulo Sum](https://codeforces.com/problemset/problem/577/B)

**Rating:** 1900 (approximate)

**Primary Mathematical Form:** Pigeonhole

**Secondary Forms:** Equal Remainders / Modulo Constraint

### 1. Story Removed

n numbers, modulus m. Is there a non-empty subsequence with sum divisible by m?

### 2. Variables

```text
a_i mod m; n,m.
```

### 3. Constraints

n <= 1e6, m <= 1e3 ⇒ pigeonhole for n>=m, else O(n·m)=O(m²).

### 4. Direct Mathematical Model

```text
Non-empty subset S with Σ_{i∈S} a_i ≡ 0 (mod m).
```

### 5. Transformation

```text
If n >= m: consider prefix sums P_0..P_n mod m: n+1 > m values ⇒ two equal ⇒ a contiguous block sums ≡ 0. So YES.
If n < m ≤ 1e3: dp over remainders; dp[r]=reachable subset-sum remainder.
```

### 6. Feasibility Conditions

```text
n>=m ⇒ YES; else O(n·m) DP.
```

### 7. Core Observation

Large n forces a repeat (pigeonhole); small n is cheap enough for DP.

### 8. Mathematical Invariant / Proof

Pigeonhole on prefix remainders. DP: dp'[ (r + a_i) % m ] |= dp[r], plus starting from a_i alone.

### 9. Statement → Math → Algorithm

```text
Story -> subset sum ≡0 -> pigeonhole for n>=m -> else DP over remainders -> O(m²)
```

### 10. Dry Run

n=3,m=5,[1,2,3]: dp remainders → {1,2,3} → {3,4,0}: 2+3=5 ✓ YES.

### 11. Pseudocode

```text
if n>=m YES; dp[]={0}; for each a: new=dp ∪ {(r+a)%m} ∪ {a%m}; if dp[0] YES
```

### 12. C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;
int main(){ int n,m; scanf("%d %d",&n,&m); if(n>=m){ puts("YES"); return 0; }
  vector<char> dp(m,0); for(int i=0;i<n;i++){ int a; scanf("%d",&a); a%=m; vector<char> nd=dp;
    nd[a]=1; for(int r=0;r<m;r++) if(dp[r]) nd[(r+a)%m]=1; dp=nd; if(dp[0]){ puts("YES"); return 0; } }
  puts("NO"); }
```

### 13. What I Should Recognize Next Time

```text
Whenever "sum divisible by m" and n ≥ m:
→ pigeonhole on prefix remainders ⇒ YES immediately; else DP over remainders.
```

## Problem index by band (to extend)

| Band | Decoded above | Next to add (same 13-step format) |
|---|---|---|
| 800–1000 | P01–P10 | more parity / ceil / gcd single-observation problems |
| 1100–1200 | P11–P14 | stars-and-bars, simple prefix, greedy-with-bounds |
| 1300–1400 | P15–P17 | prefix-remainder counts, invariant on arrays, contribution |
| 1500–1600 | — | number-theory transformations, contribution, difference arrays |
| 1700–1800 | — | bit-by-bit independence, combinatorics with modulo |
| 1900 | P18 | combined-form problems |

Ask for the next batch band by band; keep the same verification discipline (brute-force small cases).


---

## Part 28. Same Problem, Multiple Models

| Problem | Model A | Model B | Which is simpler |
|---|---|---|---|
| Same Differences (P14) | enumerate pairs `a_j-a_i=j-i` O(n²) | `b_i=a_i-i` + frequency O(n) | B (algebra removes the pair) |
| Number of Pairs (P15) | count pair sums by frequency of values | sort + binary search | B when values are huge |
| Friends and Candies (P03) | simulate redistribution | sum invariant | B (no simulation) |
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

**These are clues, not rules.** A tiny `n` can hide a formula; large `n` with small `m` (P18) flips to pigeonhole.

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

