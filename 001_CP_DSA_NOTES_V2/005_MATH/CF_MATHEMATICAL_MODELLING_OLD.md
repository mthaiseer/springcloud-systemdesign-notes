# CF Mathematical Modeling Mastery (Rating 800–1900)

*Statement → Mathematics → Pattern → Algorithm → C++17*

## TABLE OF CONTENTS

* [PART 0 — HOW TO MATHEMATICALLY READ A CODEFORCES PROBLEM](#part-0--how-to-mathematically-read-a-codeforces-problem)

  * [The Story Stripping Framework](#the-story-stripping-framework)

  * [Statement Language Translation Dictionary](#statement-language-translation-dictionary)

* [PART 1 — MATHEMATICAL FOUNDATIONS REQUIRED FOR CF](#part-1--mathematical-foundations-required-for-cf)

  * [1. Arithmetic Foundations](#1-arithmetic-foundations)

* [PART 2 — ALGEBRA FOR COMPETITIVE PROGRAMMING](#part-2--algebra-for-competitive-programming)

  * [Form 1: Rearranging Equations](#form-1-rearranging-equations)

  * [Form 2: Variable Isolation](#form-2-variable-isolation)

  * [Form 3: Substitution](#form-3-substitution)

  * [Form 4: Difference of Squares](#form-4-difference-of-squares)

  * [Form 5: Expansions](#form-5-expansions)

  * [Form 6: Pairwise Sum Transformations](#form-6-pairwise-sum-transformations)

  * [Form 7: Linear Diophantine Foundations](#form-7-linear-diophantine-foundations)

  * [Form 8: Systems of Equations](#form-8-systems-of-equations)

  * [Form 9: Inequality Modeling](#form-9-inequality-modeling)

  * [Form 10: Bounding & Extremal Bounds](#form-10-bounding--extremal-bounds)

* [PART 3 — NUMBER THEORY FOUNDATIONS](#part-3--number-theory-foundations)

  * [Divisibility, Factors, Primes, GCD, LCM, and Sieves](#divisibility-factors-primes-gcd-lcm-and-sieves)

* [PART 4 — MODULAR ARITHMETIC](#part-4--modular-arithmetic)

* [PART 5 — PARITY AS A MODELING TOOL](#part-5--parity-as-a-modeling-tool)

* [PART 6 — COUNTING & COMBINATORICS](#part-6--counting--combinatorics)

* [PART 7 — SEQUENCES & SERIES](#part-7--sequences--series)

* [PART 8 — COORDINATE & DISTANCE MATHEMATICS](#part-8--coordinate--distance-mathematics)

* [PART 9 — MIN/MAX MATHEMATICAL TRANSFORMATIONS](#part-9--minmax-mathematical-transformations)

* [PART 10 — INVARIANTS](#part-10--invariants)

* [PART 11 — OPERATION MODELING](#part-11--operation-modeling)

* [PART 12 — DECOUPLING VARIABLES](#part-12--decoupling-variables)

* [PART 13 — FREQUENCY MODELING](#part-13--frequency-modeling)

* [PART 14 — SORTING AS A MATHEMATICAL TRANSFORMATION](#part-14--sorting-as-a-mathematical-transformation)

* [PART 15 — PREFIX MATHEMATICS](#part-15--prefix-mathematics)

* [PART 16 — DIFFERENCE ARRAYS AS DISCRETE DERIVATIVES](#part-16--difference-arrays-as-discrete-derivatives)

* [PART 17 — BITWISE MATHEMATICAL MODELING](#part-17--bitwise-mathematical-modeling)

* [PART 18 — GREEDY THROUGH MATHEMATICAL PROOFS](#part-18--greedy-through-mathematical-proofs)

* [PART 19 — CONSTRUCTIVE MATHEMATICS](#part-19--constructive-mathematics)

* [PART 20 — DIOPHANTINE MODELING](#part-20--diophantine-modeling)

* [PART 21 — GEOMETRIC / GRID MODELING](#part-21--geometric--grid-modeling)

* [PART 22 — GAME MATHEMATICS](#part-22--game-mathematics)

* [PART 23 — RECURRENCES](#part-23--recurrences)

* [PART 24 — EXPECTATION / PROBABILITY BASICS](#part-24--expectation--probability-basics)

* [PART 25 — MATHEMATICAL OPTIMIZATION](#part-25--mathematical-optimization)

* [PART 26 — COMMON CODEFORCES MATHEMATICAL FORMS](#part-26--common-codeforces-mathematical-forms)

* [PART 27 — PROBLEM MODELING LIBRARY (800–1900)](#part-27--problem-modeling-library-8001900)

* [PART 28 — SAME PROBLEM, MULTIPLE MODELS](#part-28--same-problem-multiple-models)

* [PART 29 — CONSTRAINTS → EXPECTED MATHEMATICS](#part-29--constraints--expected-mathematics)

* [PART 30 — HOW TO DISCOVER THE EQUATION](#part-30--how-to-discover-the-equation)

* [PART 31 — HOW TO DISCOVER INVARIANTS](#part-31--how-to-discover-invariants)

* [PART 32 — HOW TO KNOW BRUTE FORCE CAN BE REPLACED BY MATH](#part-32--how-to-know-brute-force-can-be-replaced-by-math)

* [PART 33 — MATHEMATICAL PROOF TOOLKIT](#part-33--mathematical-proof-toolkit)

* [PART 34 — 60-SECOND CONTEST MODELING CHECKLIST](#part-34--60-second-contest-modeling-checklist)

* [PART 35 — RATING-WISE MODELING EXPECTATIONS](#part-35--rating-wise-modeling-expectations)

* [PART 36 — MASTER PATTERN INDEX](#part-36--master-pattern-index)

* [PART 37 — FINAL MATHEMATICAL MODELING WORKFLOW](#part-37--final-mathematical-modeling-workflow)

## PART 0 — HOW TO MATHEMATICALLY READ A CODEFORCES PROBLEM

Competitive programming problems, especially on Codeforces, are often wrapped in elaborate flavor text ("Alice and Bob are playing with candies..."). The first critical skill of a high-performing contestant is **Story Stripping**: converting unstructured story text into precise algebraic objects, constraints, operations, and invariants.

### The Story Stripping Framework

```
            Story / Flavor Text
                     │
                     ▼
             Identify Objects (Arrays, Grids, Trees)
                     │
                     ▼
           Extract Variables (n, k, A_i, L, R)
                     │
                     ▼
         Translate Allowed Operations (Algebraic Deltas)
                     │
                     ▼
           Identify Constraints & Bounds
                     │
                     ▼
            Formulate Target Objective
                     │
                     ▼
     Find Invariants / Equations / Inequalities
                     │
                     ▼
          Algebraic Simplification / Transformation
                     │
                     ▼
            Derive Optimal Algorithm

```

### Statement Language Translation Dictionary

| Statement Phrase | Mathematical Representation | Algorithmic Consequence | 
 | ----- | ----- | ----- | 
| "Exactly $K$" | $X = K$ | Equality equation | 
| "At least $K$" | $X \ge K$ | Lower bound constraint | 
| "At most $K$" / "No more than $K$" | $X \le K$ | Upper bound constraint | 
| "No less than $K$" | $X \ge K$ | Lower bound constraint | 
| "$A$ is divisible by $B$" | $A \pmod B = 0 \iff \exists k \in \mathbb{Z}: A = k B$ | Multiples, GCD/LCM | 
| "Remainder when $A$ is divided by $B$" | $R = A \pmod B, 0 \le R < B$ | Modular arithmetic | 
| "Equal number of Type A and Type B" | $\text{count}(A) = \text{count}(B) \iff \text{count}(A) - \text{count}(B) = 0$ | Prefix difference / Balance | 
| "Pair of elements" | Unordered pair $(i, j)$ with $1 \le i < j \le n$ | Combinatorics $\binom{n}{2}$ or double counting | 
| "Consecutive / Contiguous subarray" | Subarray $A[l \dots r]$ | Prefix sums, range queries | 
| "Adjacent elements" | Indices $i$ and $i+1$ | Local differences $A_{i+1} - A_i$ | 
| "Distance between $x$ and $y$" | $\Vert{}x - y\Vert{}$ | Absolute value, median minimization | 
| "Minimum operations to..." | $\min \text{steps} \iff \text{Lower Bound Proof} + \text{Construction}$ | Greedy / Shortest path / Math bound | 
| "Can reach state B from state A?" | Feasibility: Is $B \in \text{Reachable}(A)$? | Invariant preservation / Modulo test | 
| "Repeatedly perform operation" | Monovariant / Invariant analysis | State transition convergence | 
| "Rearrange the array in any order" | Permutation $\pi$; array is a **multiset** | Order irrelevant, sort or frequency count | 
| "Choose any subset" | Elements $\{A_{i_1}, A_{i_2}, \dots, A_{i_k}\}$ | Bitmask ($2^n$), subset sum, greedy | 
| "Choose a subsequence" | Ordered selection preserving relative indices | Dynamic programming / Greedy | 
| "Distribute $N$ identical items into $K$ bins" | $x_1 + x_2 + \dots + x_K = N, x_i \ge 0$ | Stars and Bars: $\binom{N + K - 1}{K - 1}$ | 
| "Regardless of order" | Order invariance | Count frequencies, multiset hashing | 
| "Simultaneously apply to all" | Parallel transformation | Global delta tracking / Lazy tag | 
| "Eventually stops" | Monovariant strictly decreasing / bounded below | Proof of termination | 
| "Maximum possible value" | $\max f(x) \text{ s.t. constraints}$ | Extremal argument, binary search on answer | 
| "Bitwise AND of all elements is non-zero" | $\exists k : \text{bit } k \text{ is } 1 \text{ for all elements}$ | Independent bit analysis | 
| "Bitwise XOR sum is zero" | $A_1 \oplus A_2 \oplus \dots \oplus A_n = 0$ | XOR basis, parity cancellation | 
| "Lexicographically smallest" | Prioritize optimizing early elements $A_1, A_2, \dots$ | Greedy left-to-right construction | 

## PART 1 — MATHEMATICAL FOUNDATIONS REQUIRED FOR CF

### 1. Arithmetic Foundations

#### Definitions & Core Operations

* **Integers (**$\mathbb{Z}$**)**: $\{\dots, -2, -1, 0, 1, 2, \dots\}$.

* **Absolute Value**:
  

  $$
  |x| = \begin{cases} x & \text{if } x \ge 0 \\ -x & \text{if } x < 0 \end{cases}
  $$

  
  *Key Identity*: $|x - y| = \max(x - y, y - x)$.

* **Integer Division**: For $a \in \mathbb{Z}, b \in \mathbb{Z}^+$, there exist unique $q, r \in \mathbb{Z}$ such that $a = b q + r$ with $0 \le r < b$.

  * $q = \lfloor a / b \rfloor$ is the floor quotient.

  * $r = a \bmod b$ is the non-negative remainder.

#### Floor, Ceiling, and Ceil Division Transformation

* **Floor (**$\lfloor x \rfloor$**)**: Greatest integer $\le x$.

* **Ceiling (**$\lceil x \rceil$**)**: Least integer $\ge x$.

* **Ceil Division Formula for Integers**:
  For $a \ge 0, b > 0$:
  

  $$
  \left\lceil \frac{a}{b} \right\rceil = \left\lfloor \frac{a + b - 1}{b} \right\rfloor
  $$

  
  *Derivation*: Let $a = k b + r$ where $0 \le r < b$.

  * If $r = 0$: $\lfloor (kb + b - 1)/b \rfloor = \lfloor k + (b-1)/b \rfloor = k = \lceil a/b \rceil$.

  * If $r > 0$: $a + b - 1 = kb + r + b - 1$. Since $1 \le r \le b-1$, $b \le r + b - 1 \le 2b - 2$. Thus $\lfloor (a+b-1)/b \rfloor = k + 1 = \lceil a/b \rceil$.

#### Interval & Inequality Transformations

* $[L, R]$ contains $R - L + 1$ integers.

* Intersection of $[L_1, R_1]$ and $[L_2, R_2]$ is $[\max(L_1, L_2), \min(R_1, R_2)]$. Valid if $\max(L_1, L_2) \le \min(R_1, R_2)$.

#### Min/Max Algebraic Representations

$$
\max(a, b) = \frac{a + b + |a - b|}{2}, \quad \min(a, b) = \frac{a + b - |a - b|}{2}
$$

#### Overflow and Scientific Bounds

* $2^{31} - 1 \approx 2.14 \times 10^9$ (32-bit signed `int`).

* $2^{63} - 1 \approx 9.22 \times 10^{18}$ (64-bit signed `long long`).

* Always use `long long` when multiplying two numbers $\ge 10^5$ or accumulating sums of arrays of size $\ge 10^5$.

```
// Fast non-overflow integer ceiling division
template<typename T>
T ceil_div(T a, T b) {
    return a / b + ((a % b != 0) ^ ((a ^ b) < 0));
}
// For positive integers:
long long ceil_div_pos(long long a, long long b) {
    return (a + b - 1) / b;
}

```

## PART 2 — ALGEBRA FOR COMPETITIVE PROGRAMMING

### Form 1: Rearranging Equations

Given a system:

$$
x + y = S
$$

$$
x - y = D
$$


Adding equations: $2x = S + D \implies x = \frac{S + D}{2}$.
Subtracting equations: $2y = S - D \implies y = \frac{S - D}{2}$.
**Feasibility Condition**: $x, y$ are integers if and only if $S$ and $D$ have the **same parity** ($(S + D) \bmod 2 = 0$) and $S \ge D$.

### Form 2: Variable Isolation

Convert multi-variable constraints into single-variable lookup keys.
Suppose we need to find pairs $(i, j)$ satisfying:

$$
A_i + i = A_j - j \implies A_i + i = A_j - j
$$


Define $B_i = A_i + i$ and $C_j = A_j - j$. The problem reduces to counting matches between $B_i$ and $C_j$ using a frequency map in $O(n)$ time instead of $O(n^2)$ loop search.

### Form 3: Substitution

Eliminate dependent variables.
If $z = k - x - y$, substitute $z$ into $f(x, y, z)$ to convert a 3D search space into a 2D constraint optimization.

### Form 4: Difference of Squares

$$
a^2 - b^2 = (a - b)(a + b)
$$


*CF Application*: Determining if $N$ can be written as $a^2 - b^2$.
Since $N = (a - b)(a + b)$, let $u = a - b, v = a + b$. Then $u \cdot v = N$ and $v - u = 2b$.
Thus, $N$ is expressible as a difference of squares if and only if $N$ has two factors $u, v$ with the same parity. This implies $N$ is either **odd** or **divisible by 4**.

### Form 5: Expansions

$$
(a + b)^2 = a^2 + 2ab + b^2
$$

$$
(a - b)^2 = a^2 - 2ab + b^2
$$

### Form 6: Pairwise Sum Transformations

Consider evaluating the total squared pairwise differences of an array $A$:

$$
S = \sum_{1 \le i < j \le n} (A_i - A_j)^2
$$


Expanding the square:

$$
S = \sum_{1 \le i < j \le n} (A_i^2 - 2A_i A_j + A_j^2)
$$


Notice that each $A_i^2$ appears in $n - 1$ terms:

$$
\sum_{1 \le i < j \le n} A_i^2 + A_j^2 = (n - 1) \sum_{i=1}^n A_i^2
$$


Furthermore:

$$
\left( \sum_{i=1}^n A_i \right)^2 = \sum_{i=1}^n A_i^2 + 2 \sum_{1 \le i < j \le n} A_i A_j \implies 2 \sum_{1 \le i < j \le n} A_i A_j = \left( \sum_{i=1}^n A_i \right)^2 - \sum_{i=1}^n A_i^2
$$


Substituting back:

$$
S = (n - 1) \sum_{i=1}^n A_i^2 - \left( \left( \sum_{i=1}^n A_i \right)^2 - \sum_{i=1}^n A_i^2 \right) = n \sum_{i=1}^n A_i^2 - \left( \sum_{i=1}^n A_i \right)^2
$$


*Complexity*: Reduced from $O(n^2)$ pairwise evaluation to $O(n)$ single-pass evaluation!

### Form 7: Linear Diophantine Foundations

$$
a x + b y = c
$$


Has integer solutions $(x, y)$ if and only if $\gcd(a, b) \mid c$.

### Form 8: Systems of Equations

$$
a_1 x + b_1 y = c_1
$$

$$
a_2 x + b_2 y = c_2
$$


Using Cramer's rule:

$$
D = a_1 b_2 - a_2 b_1, \quad D_x = c_1 b_2 - c_2 b_1, \quad D_y = a_1 c_2 - a_2 c_1
$$


If $D \ne 0$, unique rational solution $x = D_x / D, y = D_y / D$. Integer solution requires $D \mid D_x$ and $D \mid D_y$.

### Form 9: Inequality Modeling

System of interval constraints:

$$
L_i \le x \le R_i \quad \forall i \in \{1, \dots, n\}
$$


Feasible region for $x$:

$$
x \in \left[ \max_{i} L_i, \min_{i} R_i \right]
$$


Valid if and only if $\max_i L_i \le \min_i R_i$.

### Form 10: Bounding & Extremal Bounds

To maximize/minimize a target $X$:

1. Prove mathematically that $X \ge \text{LB}$ (Lower Bound).

2. Construct an explicit configuration achieving $X = \text{LB}$.

3. Hence $\min X = \text{LB}$.

## PART 3 — NUMBER THEORY FOUNDATIONS

### Divisibility, Factors, Primes, GCD, LCM, and Sieves

#### Fundamental Theorem of Arithmetic

Every integer $n > 1$ can be uniquely factorized as:

$$
n = p_1^{e_1} p_2^{e_2} \cdots p_k^{e_k}
$$

* **Number of Divisors**: $d(n) = \prod_{i=1}^k (e_i + 1)$.

* **Sum of Divisors**: $\sigma(n) = \prod_{i=1}^k \frac{p_i^{e_i + 1} - 1}{p_i - 1}$.

#### Divisor Count Parity Property

$d(n)$ is **odd** if and only if $n$ is a **perfect square**.
*Proof*: Divisors come in pairs $(d, n/d)$. $d = n/d \iff n = d^2$.

#### GCD and LCM

$$
\gcd(a, b) \cdot \text{lcm}(a, b) = a \cdot b
$$

$$
\gcd(a, b) = \gcd(a, b - a) = \gcd(a, b \bmod a)
$$

```
Euclidean Algorithm Graph:
(120, 45) ──> (45, 30) ──> (30, 15) ──> (15, 0) ──> GCD = 15

```

#### Sieve of Eratosthenes & Smallest Prime Factor (SPF)

```
const int MAXV = 1000000;
int spf[MAXV + 1];

void build_spf() {
    for (int i = 1; i <= MAXV; i++) spf[i] = i;
    for (int i = 2; i * i <= MAXV; i++) {
        if (spf[i] == i) {
            for (int j = i * i; j <= MAXV; j += i)
                if (spf[j] == j) spf[j] = i;
        }
    }
}

// O(log n) prime factorization using SPF
vector<int> get_prime_factors(int x) {
    vector<int> factors;
    while (x > 1) {
        factors.push_back(spf[x]);
        x /= spf[x];
    }
    return factors;
}

```

## PART 4 — MODULAR ARITHMETIC

### Core Congruence Properties

$$
a \equiv b \pmod M \iff (a - b) \bmod M = 0 \iff M \mid (a - b)
$$

* $(a + b) \bmod M = ((a \bmod M) + (b \bmod M)) \bmod M$

* $(a - b) \bmod M = ((a \bmod M) - (b \bmod M) + M) \bmod M$

* $(a \cdot b) \bmod M = ((a \bmod M) \cdot (b \bmod M)) \bmod M$

### Modular Inverse & Fermat's Little Theorem

If $p$ is prime and $\gcd(a, p) = 1$:

$$
a^{p-1} \equiv 1 \pmod p \implies a \cdot a^{p-2} \equiv 1 \pmod p \implies a^{-1} \equiv a^{p-2} \pmod p
$$

```
long long power(long long base, long long exp, long long mod) {
    long long res = 1;
    base %= mod;
    while (exp > 0) {
        if (exp % 2 == 1) res = (__int128)res * base % mod;
        base = (__int128)base * base % mod;
        exp /= 2;
    }
    return res;
}

long long modInverse(long long n, long long p) {
    return power(n, p - 2, p);
}

```

### Prefix Modulo & Equal Remainder Transformation

If $P_i = \left( \sum_{k=1}^i A_k \right) \bmod M$, then:

$$
\text{Sum}(l, r) \equiv 0 \pmod M \iff P_r = P_{l-1}
$$


Thus, finding contiguous subarrays with sum divisible by $M$ reduces to finding pairs of equal prefix remainders!

## PART 5 — PARITY AS A MODELING TOOL

### Parity Arithmetic Rules

* $\text{Even} \pm \text{Even} = \text{Even}$

* $\text{Odd} \pm \text{Odd} = \text{Even}$

* $\text{Even} \pm \text{Odd} = \text{Odd}$

* $\text{Even} \times \text{Anything} = \text{Even}$

* $\text{Odd} \times \text{Odd} = \text{Odd}$

### Parity Invariant Rule

An operation that changes two elements $A_i, A_j$ by $\pm 1$ maintains $\sum A_i \pmod 2$.
An operation that replaces $(A_i, A_j)$ with $|A_i - A_j|$ preserves the parity of the total sum:

$$
(A_i + A_j) \equiv (A_i - A_j) \equiv |A_i - A_j| \pmod 2
$$

## PART 6 — COUNTING & COMBINATORICS

### Fundamental Principles

* **Addition Rule**: Disjoint sets $|A \cup B| = |A| + |B|$.

* **Multiplication Rule**: Independent choices $|A \times B| = |A| \cdot |B|$.

* **Permutations**: $P(n, k) = \frac{n!}{(n - k)!}$.

* **Combinations**: $\binom{n}{k} = \frac{n!}{k!(n - k)!}$.

### Pair Counting Identity

$$
\binom{n}{2} = \frac{n(n - 1)}{2}
$$


If an array has frequencies $f(v)$ for each distinct value $v$, the total number of equal pairs is:

$$
\sum_{v} \binom{f(v)}{2} = \sum_{v} \frac{f(v)(f(v) - 1)}{2}
$$

### Stars and Bars Theorem

The number of ways to place $N$ identical items into $K$ distinct bins is:

* Non-negative bins ($x_i \ge 0$):
  

  $$
  \binom{N + K - 1}{K - 1}
  $$

* Positive bins ($x_i \ge 1$):
  

  $$
  \binom{N - 1}{K - 1}
  $$

### Contribution Technique (Double Counting)

Instead of counting elements within structures $\sum_{s \in S} |s|$, compute the number of structures containing each element:

$$
\text{Total} = \sum_{e \in E} \text{count}(\text{structures containing } e)
$$

## PART 7 — SEQUENCES & SERIES

### Arithmetic Progression (AP)

$$
A_n = a + (n - 1)d
$$

$$
S_n = \frac{n}{2} (2a + (n - 1)d) = n \cdot \frac{a + A_n}{2}
$$


Special case: $1 + 2 + \dots + n = \frac{n(n + 1)}{2}$.

### Geometric Progression (GP)

$$
G_n = a \cdot r^{n-1}
$$

$$
S_n = a \frac{r^n - 1}{r - 1} \quad (r \ne 1)
$$

## PART 8 — COORDINATE & DISTANCE MATHEMATICS

### Distance Metrics

* **Manhattan Distance**: $D_M((x_1, y_1), (x_2, y_2)) = |x_1 - x_2| + |y_1 - y_2|$.

* **Chebyshev Distance**: $D_C((x_1, y_1), (x_2, y_2)) = \max(|x_1 - x_2|, |y_1 - y_2|)$.

### Manhattan to Chebyshev Coordinate Transformation

Let $u = x + y, v = x - y$.

$$
D_M((x_1, y_1), (x_2, y_2)) = \max(|u_1 - u_2|, |v_1 - v_2|)
$$


*Proof*:

$$
|x_1 - x_2| + |y_1 - y_2| = \max(x_1 - x_2 + y_1 - y_2, x_1 - x_2 - y_1 + y_2, -x_1 + x_2 + y_1 - y_2, -x_1 + x_2 - y_1 + y_2)
$$

$$
= \max((x_1 + y_1) - (x_2 + y_2), (x_1 - y_1) - (x_2 - y_2), -(x_1 - y_1) + (x_2 - y_2), -(x_1 + y_1) + (x_2 + y_2))
$$

$$
= \max(|u_1 - u_2|, |v_1 - v_2|)
$$


This transforms 2D $L_1$ distance constraints into independent 1D coordinate maximums!

### Median Minimization Theorem

The point $x$ that minimizes $\sum_{i=1}^n |x - A_i|$ is the **median** of $A$.
*Proof*: Consider sorted $A_1 \le A_2 \le \dots \le A_n$.
Pair terms: $|x - A_1| + |x - A_n| \ge A_n - A_1$, with equality for any $x \in [A_1, A_n]$.
Nesting this for all pairs $(A_i, A_{n+1-i})$ forces $x$ to lie in the innermost interval, which is $A_{(n+1)/2}$.

## PART 9 — MIN/MAX MATHEMATICAL TRANSFORMATIONS

### Absolute Value Removal Identical Forms

$$
\max(a, b) = \frac{a + b + |a - b|}{2}
$$

$$
|x| \le k \iff -k \le x \le k
$$

$$
\min(a, b) \ge k \iff a \ge k \text{ AND } b \ge k
$$

$$
\max(a, b) \le k \iff a \le k \text{ AND } b \le k
$$

## PART 10 — INVARIANTS

An **invariant** is a property of a system that remains unchanged under a specified set of allowable state transformation operations.

### Invariant Types & Examples

1. **Sum Invariant**: Operation: $A_i \leftarrow A_i + c, A_j \leftarrow A_j - c \implies \sum A_k = C$.

2. **Parity Invariant**: Operation: $A_i \leftarrow A_i - 1, A_j \leftarrow A_j - 1 \implies \sum A_k \bmod 2 = C$.

3. **XOR Invariant**: Operation: $A_i \leftarrow A_i \oplus x, A_j \leftarrow A_j \oplus x \implies \bigoplus A_k = C$.

4. **GCD Invariant**: Operation: $A_i \leftarrow A_i - A_j \implies \gcd(A_1, \dots, A_n) = C$.

5. **Modulo Invariant**: Operation: $A_i \leftarrow A_i + K \implies A_i \bmod K = C$.

6. **Difference Invariant**: Operation: Increment all elements by 1 $\implies A_i - A_j = C$.

7. **Coloring / Checkerboard Invariant**: Grid steps maintain $(x + y) \bmod 2$.

## PART 11 — OPERATION MODELING

When faced with "You may perform the following operation any number of times":

```
               Operation Specified
                        │
                        ▼
            Express Algebraically
           (e.g., A_i += 1, A_j -= 1)
                        │
                        ▼
         Evaluate Delta System Effects
        ΔSum, ΔParity, ΔGCD, ΔXOR, ΔFreq
                        │
                        ▼
         Extract Preserved Quantities
                 (Invariants)
                        │
                        ▼
        Define Reachable Target Space

```

## PART 12 — DECOUPLING VARIABLES

When an expression couples multiple indices, use algebraic substitution to separate dimensions.
Example:

$$
\max_{i, j} (A_i + A_j + i - j) \implies \max_{i} (A_i + i) + \max_{j} (A_j - j)
$$


This decouples an $O(n^2)$ search into two independent $O(n)$ maximum searches.

## PART 13 — FREQUENCY MODELING

Convert array $A = [A_1, A_2, \dots, A_n]$ into a frequency map $F[v] = \text{count of } v$.

* Order is stripped.

* Pair counting becomes $\sum \binom{F[v]}{2}$.

* Complementary pair counting ($A_i + A_j = K$) becomes $\sum_v F[v] \cdot F[K - v]$.

## PART 14 — SORTING AS A MATHEMATICAL TRANSFORMATION

Sorting an array forces $A_1 \le A_2 \le \dots \le A_n$, establishing:

1. Non-negative adjacent differences: $D_i = A_{i+1} - A_i \ge 0$.

2. Monotonic telescoping sums: $\sum_{i=1}^{n-1} (A_{i+1} - A_i) = A_n - A_1$.

3. Optimal pairing bounds (Rearrangement Inequality): $\sum A_i B_i$ is maximized when both $A$ and $B$ are sorted in the same direction.

## PART 15 — PREFIX MATHEMATICS

### Prefix Sums

$$
P_i = \sum_{k=1}^i A_k \implies \sum_{k=l}^r A_k = P_r - P_{l-1}
$$

### Prefix XOR

$$
X_i = A_1 \oplus A_2 \oplus \dots \oplus A_i \implies A_l \oplus \dots \oplus A_r = X_r \oplus X_{l-1}
$$

## PART 16 — DIFFERENCE ARRAYS AS DISCRETE DERIVATIVES

To apply range update $+x$ on interval $[L, R]$ in $O(1)$:

$$
D[L] \leftarrow D[L] + x
$$

$$
D[R + 1] \leftarrow D[R + 1] - x
$$


Recover original array via prefix sums: $A_i = \sum_{k=1}^i D[k]$.

## PART 17 — BITWISE MATHEMATICAL MODELING

Decompose integer operations into 30 or 60 independent single-bit problems.

$$
A_i = \sum_{b=0}^{30} \text{bit}_b(A_i) \cdot 2^b
$$


Since bitwise operations ($\text{AND}, \text{OR}, \text{XOR}$) do not carry over between bit positions, evaluate each bit $b \in [0, 30]$ independently!

## PART 18 — GREEDY THROUGH MATHEMATICAL PROOFS

### Exchange Argument Template

1. Assume an optimal solution $S^*$ differs from greedy choice $S_G$.

2. Identify the first inversion between $S^*$ and $S_G$.

3. Swap adjacent elements in $S^*$.

4. Prove algebraically that $\text{Cost}(S^*_{\text{swapped}}) \le \text{Cost}(S^*)$.

5. Conclude greedy choice is optimal.

## PART 19 — CONSTRUCTIVE MATHEMATICS

To construct objects satisfying conditions:

1. Determine necessary bounds (e.g., parity, total sum).

2. Design the simplest structured candidate (e.g., sorted array, alternating $1, -1$, cyclic shift).

3. Algebraically verify that the simple structure fulfills all constraints.

## PART 20 — DIOPHANTINE MODELING

For $a x + b y = c$:

* General solution: $x = x_0 + k \frac{b}{g}, y = y_0 - k \frac{a}{g}$ where $g = \gcd(a, b)$ and $k \in \mathbb{Z}$.

* Non-negative solutions require bound intersection on $k$.

## PART 21 — GEOMETRIC / GRID MODELING

### Grid Parity

Grid cell $(r, c)$ has color/parity $(r + c) \bmod 2$.
Orthogonal moves always flip cell parity: $(r+1 + c) \bmod 2 \ne (r + c) \bmod 2$.

## PART 22 — GAME MATHEMATICS

### Nim & Grundy Values

For impartial games under normal play convention:

* State is losing (P-position) if $\bigoplus_{i=1}^k G_i = 0$.

* State is winning (N-position) if $\bigoplus_{i=1}^k G_i \ne 0$.

## PART 23 — RECURRENCES

Linear Recurrence: $F_n = a F_{n-1} + b F_{n-2}$.
Matrix Exponentiation for $O(\log n)$ computation:

$$
\begin{pmatrix} F_n \\ F_{n-1} \end{pmatrix} = \begin{pmatrix} a & b \\ 1 & 0 \end{pmatrix}^{n-1} \begin{pmatrix} F_1 \\ F_0 \end{pmatrix}
$$

## PART 24 — EXPECTATION / PROBABILITY BASICS

### Linearity of Expectation

$$
E[X_1 + X_2 + \dots + X_n] = E[X_1] + E[X_2] + \dots + E[X_n]
$$


**Crucial**: Holds even if $X_i$ are dependent variables!

## PART 25 — MATHEMATICAL OPTIMIZATION

```
                 Optimization Target
                          │
                          ▼
             Is Predicate Monotonic?
             P(X) = true for X <= K
            P(X) = false for X > K
                          │
         ┌────────────────┴────────────────┐
        YES                                NO
         │                                 │
         ▼                                 ▼
   Binary Search                      Mathematical
    On Answer                     Transformation / DP

```

## PART 26 — COMMON CODEFORCES MATHEMATICAL FORMS

Here are 5 core forms selected from the master pattern library.

### Form 1 — Sum Constraint ($\sum A_i = K$)

* **Signals**: "Total sum is fixed", "Redistribute values".

* **Model**: $\sum_{i=1}^n A_i = K$. Mean $\mu = K/n$.

* **Transformation**: If equalizing, target is $\lfloor K/n \rfloor$ and $\lceil K/n \rceil$.

### Form 5 — Parity Constraint

* **Signals**: "Add/subtract 2", "Flip signs of two elements".

* **Model**: Operation maintains $A_i \bmod 2$.

### Form 11 — Pair Counting

* **Signals**: "Count pairs $(i, j)$ such that...", "Matching elements".

* **Model**: $\sum_v \frac{f(v)(f(v)-1)}{2}$.

### Form 12 — Complement Pair ($A_i + A_j = K$)

* **Signals**: "Pairs summing to $K$".

* **Model**: For each $v$, count pairs with $K - v$. Total = $\frac{1}{2} \sum_v f(v) \cdot f(K - v)$ (adjusting for $v = K/2$).

### Form 30 — Bit Independence

* **Signals**: Bitwise AND/OR/XOR operations across array.

* **Model**: Evaluate bit $b$ across all elements independently.

## PART 27 — PROBLEM MODELING LIBRARY (800–1900)

### Problem 1: Watermelon — CF 4A

**Link**: [Codeforces 4A](https://codeforces.com/problemset/problem/4/A?utm_source=gemini)

**Rating**: 800

**Primary Form**: Parity Constraint & Inequality Modeling

#### 1. Story Removed

Determine if a positive integer $w$ can be partitioned into two positive even integers $x$ and $y$.

#### 2. Variables

* $w$: Total weight ($1 \le w \le 100$).

* $x, y$: Part weights ($x, y \in \mathbb{Z}^+$).

#### 3. Constraints

* $x \ge 2$, $y \ge 2$.

* $x \bmod 2 = 0$, $y \bmod 2 = 0$.

* $w \le 100 \implies O(1)$ checks.

#### 4. Direct Mathematical Model

$$
\begin{cases} x + y = w \\ x = 2a, \, a \ge 1 \\ y = 2b, \, b \ge 1 \end{cases}
$$

#### 5. Transformation

$$
2a + 2b = w \implies 2(a + b) = w \implies w \bmod 2 = 0
$$


Min values: $a = 1, b = 1 \implies w_{\min} = 2(1 + 1) = 4$.

#### 6. Feasibility Conditions

1. $w \bmod 2 = 0$

2. $w \ge 4$

#### 7. Core Observation

The weight $w$ must be even and at least 4 (since $2+2=4$ is the smallest split).

#### 8. Mathematical Proof

If $w$ is even and $w \ge 4$, let $x = 2$ and $y = w - 2$.
Since $w$ is even, $w - 2$ is even. Since $w \ge 4$, $w - 2 \ge 2$. Both conditions satisfied.

#### 9. Statement → Math → Algorithm

$$
\text{"Divide into two even parts"} \longrightarrow x + y = w, \, x,y \text{ even} \longrightarrow w \text{ even } \land w \ge 4 \longrightarrow \text{Output YES/NO}
$$

#### 10. Dry Run

* $w = 8 \implies 8 \bmod 2 = 0$ and $8 \ge 4 \implies \text{YES}$ ($x=2, y=6$).

* $w = 2 \implies 2 \ge 4$ is FALSE $\implies \text{NO}$.

#### 11. Pseudocode

```
if w % 2 == 0 and w >= 4: return "YES"
else: return "NO"

```

#### 12. C++17 Solution

```
#include <iostream>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    int w;
    if (cin >> w) {
        if (w % 2 == 0 && w >= 4) {
            cout << "YES\n";
        } else {
            cout << "NO\n";
        }
    }
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Partitioning into even entities requires total sum to be even and at least the minimum sum of basic units ($2 + 2 = 4$).

### Problem 2: Domino piling — CF 50A

**Link**: [Codeforces 50A](https://codeforces.com/problemset/problem/50/A?utm_source=gemini)

**RatingWait**: 800

**Primary Form**: Bounding & Area Tiling

#### 1. Story Removed

Find maximum number of $2 \times 1$ rectangles that fit without overlap in an $M \times N$ grid.

#### 2. Variables

* $M, N$: Grid dimensions ($1 \le M, N \le 16$).

#### 3. Constraints

$M, N \le 16 \implies O(1)$ arithmetic formula.

#### 4. Direct Mathematical Model

* Grid Area = $M \cdot N$.

* Domino Area = $2$.

* Upper bound: $\lfloor \frac{M \cdot N}{2} \rfloor$.

#### 5. Transformation

Can we always achieve $\lfloor \frac{M \cdot N}{2} \rfloor$?

* If $M$ or $N$ is even, say $M = 2k$, grid decomposes into $k$ subgrids of $2 \times N$. Each $2 \times N$ holds $N$ dominoes. Total $= k \cdot N = \frac{M \cdot N}{2}$.

* If both $M, N$ are odd, $(M-1) \times N$ is covered fully, leaving $1 \times N$. The $1 \times N$ strip holds $\lfloor N/2 \rfloor$ dominoes. Total area used $= (M-1)N + 2 \lfloor N/2 \rfloor = M \cdot N - 1$.

#### 6. Feasibility Conditions

Answer is always $\lfloor \frac{M \cdot N}{2} \rfloor$.

#### 7. Core Observation

Area upper bound is reachable by greedy strip-tiling.

#### 8. Mathematical Proof

Each domino occupies 2 squares. Maximum dominoes $\le \lfloor (M \cdot N) / 2 \rfloor$. Tile construction matches this bound exactly.

#### 9. Statement → Math → Algorithm

$$
\text{"Tile } M \times N \text{ with } 2 \times 1\text{"} \longrightarrow \text{Max dominoes} = \left\lfloor \frac{M \cdot N}{2} \right\rfloor \longrightarrow \text{Output } (M \cdot N) / 2
$$

#### 10. Dry Run

$M = 3, N = 3 \implies 3 \cdot 3 = 9 \implies \lfloor 9 / 2 \rfloor = 4$.

#### 11. Pseudocode

```
return (M * N) / 2

```

#### 12. C++17 Solution

```
#include <iostream>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    int M, N;
    if (cin >> M >> N) {
        cout << (M * N) / 2 << "\n";
    }
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Tiling with size 2 pieces in 2D grids achieves the total area floor bound.

### Problem 3: Odd Divisor — CF 1475A

**Link**: [Codeforces 1475A](https://codeforces.com/problemset/problem/1475/A?utm_source=gemini)

**Rating**: 900

**Primary Form**: Prime Factorization / Powers of Two

#### 1. Story Removed

Determine whether integer $n$ has an odd divisor $d > 1$.

#### 2. Variables

* $n$: Positive integer ($2 \le n \le 10^{14}$).

#### 3. Constraints

$n \le 10^{14} \implies O(\log n)$ or $O(1)$ bitwise check.

#### 4. Direct Mathematical Model

By Fundamental Theorem of Arithmetic:

$$
n = 2^k \cdot p_1^{e_1} p_2^{e_2} \cdots p_m^{e_m}
$$


where $p_i$ are odd primes.
An odd divisor $d > 1$ exists if and only if at least one $e_i > 0$.

#### 5. Transformation

$$
\exists d > 1 \text{ odd s.t. } d \mid n \iff n \ne 2^k \text{ for any } k \ge 0
$$

#### 6. Feasibility Conditions

$n$ is NOT a power of two $\iff (n \mathbin{\&} (n - 1)) \ne 0$.

#### 7. Core Observation

Numbers without odd divisors $> 1$ are powers of two.

#### 8. Mathematical Proof

If $n = 2^k$, every divisor of $n$ is of the form $2^j$ ($0 \le j \le k$). All positive divisors except 1 are even. Thus no odd divisor $>1$ exists.
If $n$ is not a power of two, $n = 2^k \cdot m$ where $m > 1$ is odd. $m$ is an odd divisor $> 1$.

#### 9. Statement → Math → Algorithm

$$
\text{"Odd divisor } > 1\text{"} \longrightarrow n \ne 2^k \longrightarrow (n \mathbin{\&} (n - 1)) \ne 0 \longrightarrow \text{YES/NO}
$$

#### 10. Dry Run

* $n = 6 = 2^1 \cdot 3 \implies 6 \mathbin{\&} 5 = 4 \ne 0 \implies \text{YES}$.

* $n = 4 = 2^2 \implies 4 \mathbin{\&} 3 = 0 \implies \text{NO}$.

#### 11. Pseudocode

```
if (n & (n - 1)) != 0: return "YES"
else: return "NO"

```

#### 12. C++17 Solution

```
#include <iostream>

using namespace std;

void solve() {
    long long n;
    cin >> n;
    if (n & (n - 1)) {
        cout << "YES\n";
    } else {
        cout << "NO\n";
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    int t;
    if (cin >> t) {
        while (t--) solve();
    }
    return 0;
}

```

#### 13. What I Should Recognize Next Time

"Has an odd factor $> 1$" is equivalent to "Is not a power of 2", checked via `(n & (n - 1)) != 0`.

### Problem 4: Same Parity Summands — CF 1352B

**Link**: [Codeforces 1352B](https://codeforces.com/problemset/problem/1352/B?utm_source=gemini)

**Rating**: 1200

**Primary Form**: Parity & Constructive Bounding

#### 1. Story Removed

Represent integer $n$ as sum of $k$ positive integers, all odd or all even.

#### 2. Variables

* $n, k$: Integers ($1 \le n, k \le 10^9$).

#### 3. Constraints

$n, k \le 10^9 \implies O(1)$ algebraic construction.

#### 4. Direct Mathematical Model

Two cases:

* Case 1 (All $k$ terms odd): $x_1 = x_2 = \dots = x_{k-1} = 1$, $x_k = n - (k - 1)$.
  Requires $x_k > 0$ and $x_k$ odd $\implies n - (k - 1) > 0$ and $(n - k + 1) \bmod 2 = 1$.

* Case 2 (All $k$ terms even): $x_1 = x_2 = \dots = x_{k-1} = 2$, $x_k = n - 2(k - 1)$.
  Requires $x_k > 0$ and $x_k$ even $\implies n - 2(k - 1) > 0$ and $(n - 2k + 2) \bmod 2 = 0 \iff n \bmod 2 = 0$.

#### 5. Transformation

1. Try odd construction: $rem1 = n - (k - 1)$. If $rem1 > 0$ and $rem1 \bmod 2 == 1$, output $k-1$ ones and $rem1$.

2. Try even construction: $rem2 = n - 2(k - 1)$. If $rem2 > 0$ and $rem2 \bmod 2 == 0$, output $k-1$ twos and $rem2$.

3. Otherwise NO.

#### 6. Feasibility Conditions

$rem1 > 0 \land rem1 \text{ odd}$ OR $rem2 > 0 \land rem2 \text{ even}$.

#### 7. Core Observation

Greedy assignment (making $k-1$ numbers minimal value 1 or 2) leaves the remaining budget to test parity compatibility.

#### 8. Mathematical Proof

If a valid sequence of $k$ odd numbers exists, their sum $n \ge k$. The minimal odd sequence sum is $k \cdot 1 = k$. If $n - k$ is even, setting $x_k = 1 + (n - k)$ preserves oddness and fulfills sum $n$. Same holds for evens with minimal sum $2k$.

#### 9. Statement → Math → Algorithm

$$
\text{Test minimal constructions: } (k-1) \times 1 + \text{rem} \text{ or } (k-1) \times 2 + \text{rem} \longrightarrow \text{Verify feasibility} \longrightarrow \text{Construct array}
$$

#### 10. Dry Run

$n = 10, k = 3$:

* Odd try: $rem1 = 10 - 2 = 8$ (even, invalid).

* Even try: $rem2 = 10 - 4 = 6 > 0$ and even $\implies$ Output $[2, 2, 6]$. Valid!

#### 11. Pseudocode

```
if (n - (k - 1) > 0 and (n - (k - 1)) % 2 == 1):
    return YES, [1]*(k-1) + [n - (k - 1)]
else if (n - 2*(k - 1) > 0 and (n - 2*(k - 1)) % 2 == 0):
    return YES, [2]*(k-1) + [n - 2*(k - 1)]
else:
    return NO

```

#### 12. C++17 Solution

```
#include <iostream>

using namespace std;

void solve() {
    int n, k;
    cin >> n >> k;
    
    int n1 = n - (k - 1);
    if (n1 > 0 && n1 % 2 == 1) {
        cout << "YES\n";
        for (int i = 0; i < k - 1; i++) cout << 1 << " ";
        cout << n1 << "\n";
        return;
    }
    
    int n2 = n - 2 * (k - 1);
    if (n2 > 0 && n2 % 2 == 0) {
        cout << "YES\n";
        for (int i = 0; i < k - 1; i++) cout << 2 << " ";
        cout << n2 << "\n";
        return;
    }
    
    cout << "NO\n";
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    int t;
    if (cin >> t) {
        while (t--) solve();
    }
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Constructive fixed-parity sum problems are tested by greedy minimal elements ($1$ for odds, $2$ for evens) and assigning remaining balance to the final term.

### Problem 5: Product of Three Numbers — CF 1294C

**Link**: [Codeforces 1294C](https://codeforces.com/problemset/problem/1294/C?utm_source=gemini)

**Rating**: 1300

**Primary Form**: Factorization / Greedy Selection

#### 1. Story Removed

Find three distinct integers $a, b, c \ge 2$ such that $a \cdot b \cdot c = n$.

#### 2. Variables

* $n$: Integer ($2 \le n \le 10^9$).

* $a, b, c$: Target distinct factors $\ge 2$.

#### 3. Constraints

$n \le 10^9 \implies O(\sqrt{n})$ trial division.

#### 4. Direct Mathematical Model

$$
a \cdot b \cdot c = n \quad \text{s.t.} \quad 2 \le a < b < c
$$

#### 5. Transformation

1. Find smallest divisor $a \ge 2$ of $n$.

2. Let $n' = n / a$.

3. Find smallest divisor $b \ge a + 1$ of $n'$.

4. Let $c = n' / b$.

5. Check if $c \ge b + 1$.

#### 6. Feasibility Conditions

$a \ge 2$, $b \ge a + 1$, $c \ge b + 1$.

#### 7. Core Observation

Greedy choice of minimal valid $a$ and minimal valid $b$ maximizes remaining value for $c$, optimizing the distinctness condition $c > b$.

#### 8. Mathematical Proof

If any solution $(a', b', c')$ exists, replacing $a'$ with the minimal divisor $a$ leaves $n/a \ge n/a'$, making it strictly easier to find distinct $b$ and $c$.

#### 9. Statement → Math → Algorithm

$$
a \cdot b \cdot c = n \longrightarrow \text{Greedy smallest } a \mid n \longrightarrow \text{Greedy smallest } b \mid (n/a) \longrightarrow c = n/(ab) > b \longrightarrow \text{Output } a, b, c
$$

#### 10. Dry Run

$n = 64$:

* Smallest factor $a \ge 2$: $a = 2$. Remaining $n' = 32$.

* Smallest factor $b \ge 3$ of 32: $b = 4$. Remaining $c = 32 / 4 = 8$.

* Check $c = 8 > b = 4 \implies$ Valid! Output $[2, 4, 8]$.

#### 11. Pseudocode

```
a = find_smallest_factor(n, start=2)
if a exists:
    n' = n / a
    b = find_smallest_factor(n', start=a+1)
    if b exists:
        c = n' / b
        if c > b: return YES, a, b, c
return NO

```

#### 12. C++17 Solution

```
#include <iostream>

using namespace std;

void solve() {
    int n;
    cin >> n;
    int a = -1, b = -1, c = -1;
    
    for (int i = 2; i * i <= n; i++) {
        if (n % i == 0) {
            a = i;
            break;
        }
    }
    
    if (a != -1) {
        int rem = n / a;
        for (int i = a + 1; i * i <= rem; i++) {
            if (rem % i == 0) {
                b = i;
                c = rem / i;
                break;
            }
        }
    }
    
    if (a != -1 && b != -1 && c > b) {
        cout << "YES\n" << a << " " << b << " " << c << "\n";
    } else {
        cout << "NO\n";
    }
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    int t;
    if (cin >> t) {
        while (t--) solve();
    }
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Factoring a number into distinct parts is maximized by greedily stripping off the smallest possible valid divisors.

### Problem 6: Maximum Sum of Products — CF 1519D

**Link**: [Codeforces 1519D](https://codeforces.com/problemset/problem/1519/D?utm_source=gemini)

**Rating**: 1600

**Primary Form**: Prefix Contribution & Reversal Invariance

#### 1. Story Removed

Given two arrays $A$ and $B$ of size $n$. You can reverse **at most one** contiguous subarray of $A$. Maximize $\sum_{i=1}^n A_i B_i$.

#### 2. Variables

* $n$: Array size ($1 \le n \le 5000$).

* $A_i, B_i$: Array elements.

#### 3. Constraints

$n \le 5000 \implies O(n^2)$ time complexity is allowed!

#### 4. Direct Mathematical Model

Base sum without reversal:

$$
S_0 = \sum_{i=1}^n A_i B_i
$$


If we reverse subarray $A[l \dots r]$, new sum is:

$$
S(l, r) = S_0 - \sum_{k=l}^r A_k B_k + \sum_{k=l}^r A_{r - (k - l)} B_k
$$

#### 5. Transformation

Notice that a subarray reversal centered at index $mid$ can be expanded outward!

* **Odd length subarray** centered at $i$: Expand radii $k = 1, 2, \dots$
  

  $$
  \Delta(i - k, i + k) = \Delta(i - k + 1, i + k - 1) + (A_{i+k} B_{i-k} + A_{i-k} B_{i+k}) - (A_{i-k} B_{i-k} + A_{i+k} B_{i+k})
  $$

* **Even length subarray** centered between $i$ and $i+1$: Expand radii $k = 0, 1, \dots$

#### 6. Feasibility Conditions

Interval $[l, r]$ satisfies $1 \le l \le r \le n$.

#### 7. Core Observation

Center-expansion DP computes the sum delta for all $\approx n^2/2$ intervals in $O(1)$ per expansion step!

#### 8. Mathematical Proof

By expanding around centers $mid$, $S(l-1, r+1)$ is derived from $S(l, r)$ by adjusting only two boundary terms, enabling $O(n^2)$ total operations.

#### 9. Statement → Math → Algorithm

$$
\text{Maximize } \sum A_{\pi(i)} B_i \longrightarrow \text{Base sum } S_0 + \text{Expand around all centers } mid \longrightarrow O(n^2) \text{ Center Expansion} \longrightarrow \max S(l, r)
$$

#### 10. Dry Run

Let $A = [2, 3, 2, 4], B = [1, 3, 2, 4]$. $S_0 = 2(1) + 3(3) + 2(2) + 4(4) = 2 + 9 + 4 + 16 = 31$.
Expand around center $i=2, k=1 \implies l=1, r=3$:
New term swaps $A_1 \leftrightarrow A_3$: $\Delta = (A_3 B_1 + A_1 B_3) - (A_1 B_1 + A_3 B_3) = (2(1) + 2(2)) - (2(1) + 2(2)) = 0$.

#### 11. Pseudocode

```
S0 = sum(A[i] * B[i])
max_sum = S0

for center from 0 to n-1:
    // Odd lengths
    cur = S0
    l = center - 1, r = center + 1
    while l >= 0 and r < n:
        cur += A[r]*B[l] + A[l]*B[r] - A[l]*B[l] - A[r]*B[r]
        max_sum = max(max_sum, cur)
        l--, r++
        
    // Even lengths
    cur = S0
    l = center, r = center + 1
    while l >= 0 and r < n:
        cur += A[r]*B[l] + A[l]*B[r] - A[l]*B[l] - A[r]*B[r]
        max_sum = max(max_sum, cur)
        l--, r++

```

#### 12. C++17 Solution

```
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    
    int n;
    if (!(cin >> n)) return 0;
    
    vector<long long> a(n), b(n);
    for (int i = 0; i < n; i++) cin >> a[i];
    for (int i = 0; i < n; i++) cin >> b[i];
    
    long long base_sum = 0;
    for (int i = 0; i < n; i++) {
        base_sum += a[i] * b[i];
    }
    
    long long max_sum = base_sum;
    
    for (int center = 0; center < n; center++) {
        // Odd length reversals centered at 'center'
        long long cur = base_sum;
        int l = center - 1, r = center + 1;
        while (l >= 0 && r < n) {
            cur += a[r] * b[l] + a[l] * b[r] - a[l] * b[l] - a[r] * b[r];
            max_sum = max(max_sum, cur);
            l--; r++;
        }
        
        // Even length reversals centered between 'center' and 'center + 1'
        cur = base_sum;
        l = center; r = center + 1;
        while (l >= 0 && r < n) {
            cur += a[r] * b[l] + a[l] * b[r] - a[l] * b[l] - a[r] * b[r];
            max_sum = max(max_sum, cur);
            l--; r++;
        }
    }
    
    cout << max_sum << "\n";
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Subarray reversal impact on linear form $\sum A_i B_i$ can be efficiently computed for all ranges using center expansion in $O(n^2)$.

### Problem 7: Compatible Numbers — CF 165E

**Link**: [Codeforces 165E](https://codeforces.com/problemset/problem/165/E?utm_source=gemini)

**Rating**: 1900

**Primary Form**: Bitmask SOS DP / Decoupling Bits

#### 1. Story Removed

Given an array $A$ of $n$ integers. For each $A_i$, find any element $A_j$ in the array such that $A_i \mathbin{\&} A_j = 0$, or report $-1$.

#### 2. Variables

* $n$: Array length ($1 \le n \le 10^6$).

* $A_i$: Array elements ($1 \le A_i \le 4 \times 10^6$).

* Max bit width $K = 22$ ($2^{22} > 4 \times 10^6$).

#### 3. Constraints

$n \le 10^6, A_i < 2^{22} \implies O(n + K \cdot 2^K)$ memory/time using Sum Over Subsets (SOS) DP.

#### 4. Direct Mathematical Model

$$
A_i \mathbin{\&} A_j = 0 \iff A_j \subseteq \sim A_i
$$


where $\sim A_i$ is the bitwise NOT of $A_i$ restricted to $K = 22$ bits ($\text{mask} = (2^{22} - 1) \oplus A_i$).

#### 5. Transformation

Define $F[\text{mask}]$ as an element present in the array $A$ that is a **submask** of $\text{mask}$ ($\text{element} \subseteq \text{mask}$).
If $F[\sim A_i]$ is non-empty, then $F[\sim A_i] \mathbin{\&} A_i = 0$!

#### 6. Feasibility Conditions

$\text{DP}[\text{mask}]$ propagates submask presence across $K = 22$ dimensions.

#### 7. Core Observation

$A_i \mathbin{\&} A_j = 0 \iff A_j$ is a submask of bitwise complement $\sim A_i$. Fast submask lookup is provided by SOS DP.

#### 8. Mathematical Proof

If $A_j \subseteq \sim A_i$, then everywhere $A_i$ has bit 1, $\sim A_i$ has bit 0, forcing $A_j$ to have bit 0. Hence $A_i \mathbin{\&} A_j = 0$. SOS DP computes submask reachability in $O(K \cdot 2^K)$.

#### 9. Statement → Math → Algorithm

$$
A_i \mathbin{\&} A_j = 0 \longrightarrow A_j \subseteq (\sim A_i) \longrightarrow \text{SOS DP over } 2^{22} \text{ masks} \longrightarrow \text{Query } F[\sim A_i]
$$

#### 10. Dry Run

$A_i = 0b101 (5)$. Complement $\sim 5 = 0b010 (2)$ (assuming 3 bits).
If array contains $2 (0b010)$, $F[0b010] = 2$.
Query for $5 \implies F[\sim 5] = F[2] = 2$.
Verification: $5 \mathbin{\&} 2 = 0b101 \mathbin{\&} 0b010 = 0$. Correct!

#### 11. Pseudocode

```
Initialize DP[mask] = -1 for all masks < 2^22
For each element x in A:
    DP[x] = x

For i from 0 to 21:
    For mask from 0 to 2^22 - 1:
        if (mask & (1 << i)):
            if DP[mask ^ (1 << i)] != -1:
                DP[mask] = DP[mask ^ (1 << i)]

For x in A:
    comp = ((1 << 22) - 1) ^ x
    ans.append(DP[comp])

```

#### 12. C++17 Solution

```
#include <iostream>
#include <vector>

using namespace std;

const int BITS = 22;
const int MAXM = 1 << BITS;
int dp[MAXM];

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
    
    int n;
    if (!(cin >> n)) return 0;
    
    vector<int> a(n);
    for (int i = 0; i < MAXM; i++) dp[i] = -1;
    
    for (int i = 0; i < n; i++) {
        cin >> a[i];
        dp[a[i]] = a[i];
    }
    
    // SOS DP: Propagate presence down submasks
    for (int i = 0; i < BITS; i++) {
        for (int mask = 0; mask < MAXM; mask++) {
            if (mask & (1 << i)) {
                if (dp[mask ^ (1 << i)] != -1) {
                    dp[mask] = dp[mask ^ (1 << i)];
                }
            }
        }
    }
    
    int full_mask = MAXM - 1;
    for (int i = 0; i < n; i++) {
        int comp = full_mask ^ a[i];
        cout << dp[comp] << (i == n - 1 ? "" : " ");
    }
    cout << "\n";
    
    return 0;
}

```

#### 13. What I Should Recognize Next Time

Condition $A_i \mathbin{\&} A_j = 0$ is a SOS DP submask reachability query on the bitwise complement $\sim A_i$.

## PART 28 — SAME PROBLEM, MULTIPLE MODELS

Consider the problem: **"Count pairs** $(i, j)$ **with** $i < j$ **such that** $A_i + A_j = K$**."**

### Model 1: Frequency Hash Map

* **Concept**: Maintain dynamic frequency count while iterating right-to-left.

* **Formula**: $\text{ans} = \sum_{j=1}^n \text{Freq}[K - A_j]$.

* **Time Complexity**: $O(n)$.

* **Best Used When**: Online stream processing or large values requiring `std::unordered_map`.

### Model 2: Sorting + Two Pointers

* **Concept**: Sort $A$ to establish monotonic inequalities $A_1 \le A_2 \le \dots \le A_n$.

* **Formula**: Pointer $L$ at start, $R$ at end. If $A_L + A_R > K \implies R--$; if $< K \implies L++$.

* **Time Complexity**: $O(n \log n)$.

* **Best Used When**: Memory is strictly constrained $O(1)$ auxiliary space.

### Model 3: Combinatorial Frequency Table

* **Concept**: Precompute static frequencies $F[v]$ for all elements.

* **Formula**:
  

  $$
  \text{ans} = \sum_{v < K/2} F[v] \cdot F[K - v] + \begin{cases} \binom{F[K/2]}{2} & \text{if } K \text{ is even} \\ 0 & \text{otherwise} \end{cases}
  $$

* **Time Complexity**: $O(n + \max A_i)$.

* **Best Used When**: Counting globally across small bounded integers ($A_i \le 10^6$).

## PART 29 — CONSTRAINTS → EXPECTED MATHEMATICS

| Constraint | Complexity | Implied Mathematical Structure | 
 | ----- | ----- | ----- | 
| $N \le 10$ | $O(N!)$ | Permutations, brute-force search space | 
| $N \le 20$ | $O(2^N)$ | Subsets, bitmask DP, inclusion-exclusion | 
| $N \le 40$ | $O(2^{N/2})$ | Meet-in-the-middle, split search space | 
| $N \le 500$ | $O(N^3)$ | All-pairs shortest path, 3D DP, matrix operations | 
| $N \le 5000$ | $O(N^2)$ | Dynamic programming, pairwise center expansion | 
| $N \le 2 \times 10^5$ | $O(N \log N)$ or $O(N)$ | Sorting, prefix sums, binary search, segment tree | 
| $N \le 10^7$ | $O(N)$ | Linear sieve, difference arrays, two pointers | 
| $N \le 10^{12}$ | $O(\sqrt{N})$ | Trial division factorization, integer square root | 
| $N \le 10^{18}$ | $O(\log N)$ | Fast exponentiation, Euclidean GCD, matrix exponentiation | 

## PART 30 — HOW TO DISCOVER THE EQUATION

When stuck, systematically ask these 5 mathematical discovery questions:

1. **What is invariant?** Does the sum, parity, product, XOR, or GCD remain constant after operations?

2. **Can I isolate variables?** Transform $f(i, j) = 0$ into $g(i) = h(j)$.

3. **What happens at the boundaries?** Test $N=1, 2$, or extreme values $A_i = 0, \max A_i$.

4. **Is the problem bit-independent?** Can operations be split across binary digit places?

5. **Can I rephrase target as complement?** Is $\text{Valid} = \text{Total} - \text{Invalid}$ easier to compute?

## PART 31 — HOW TO DISCOVER INVARIANTS

### Operation-Delta Analysis Table

| Allowed Operation | System Delta ($\Delta$) | Invariant Preserved | 
 | ----- | ----- | ----- | 
| $A_i \leftarrow A_i + 1, A_j \leftarrow A_j - 1$ | $\Delta \text{Sum} = +1 - 1 = 0$ | Total Sum $\sum A_k$ | 
| $A_i \leftarrow A_i - 1, A_j \leftarrow A_j - 1$ | $\Delta \text{Sum} = -2$ | Sum Parity $\sum A_k \bmod 2$ | 
| $A_i \leftarrow A_i \oplus x, A_j \leftarrow A_j \oplus x$ | $\Delta \text{XOR} = x \oplus x = 0$ | Total XOR Sum $\bigoplus A_k$ | 
| $A_i \leftarrow A_i - A_j$ | $\gcd(A_i - A_j, A_j) = \gcd(A_i, A_j)$ | Array GCD $\gcd(A_1, \dots, A_n)$ | 
| Swap adjacent $A_i, A_{i+1}$ | Frequencies unchanged | Multiset identity & element counts | 
| Replace $(a, b)$ with $a + b$ | $\Delta \text{Count} = -1, \Delta \text{Sum} = 0$ | Total Sum | 

## PART 32 — HOW TO KNOW BRUTE FORCE CAN BE REPLACED BY MATH

```
                  Brute Force Approach
                           │
                           ▼
          Identify Pattern of Redundant Operations
                           │
       ┌───────────────────┼───────────────────┐
       ▼                   ▼                   ▼
  Enumerating         Repeated            Simulating
   All Pairs          Addition           Redistribution
       │                   │                   │
       ▼                   ▼                   ▼
 Use Frequency Map   Use Multiplication   Use Sum Invariant
 / Algebraic Form     / AP Formula           & Ceiling Div

```

## PART 33 — MATHEMATICAL PROOF TOOLKIT

1. **Proof by Invariant**: Show initial state has invariant $I_0$. Prove all valid operations preserve $I_0$. If target state has $I_{\text{target}} \ne I_0$, target is impossible.

2. **Proof by Contradiction**: Assume statement is false, show it violates basic axioms or constraints.

3. **Exchange Argument**: Prove greedy choice by showing swapping it with any non-greedy choice worsens/doesn't improve the result.

4. **Extremal Principle**: Consider the maximum or minimum element to force structural constraints.

## PART 34 — 60-SECOND CONTEST MODELING CHECKLIST

```
[ ] 1. Strip story text; extract N, K, A_i, L, R and write variable bounds.
[ ] 2. What is the target? (Minimization, Maximization, Feasibility Count).
[ ] 3. Write operations algebraically (e.g., A_i = A_i + x).
[ ] 4. Test basic invariants: ΔSum, ΔParity, ΔXOR, ΔGCD, ΔMod.
[ ] 5. Can indices be decoupled? (A_i + i vs A_j - j).
[ ] 6. Does sorting simplify the constraint? (a_1 <= a_2 <= ... <= a_n).
[ ] 7. Can bit positions [0...30] be evaluated independently?
[ ] 8. Is binary search applicable? (Is predicate monotonically increasing/decreasing?).
[ ] 9. What is the complexity bound from N? (O(N), O(N log N), O(N^2)).
[ ] 10. Check potential integer overflow (use long long / __int128).

```

## PART 35 — RATING-WISE MODELING EXPECTATIONS

* **800–1000**: Direct formula, single-variable parity check, basic area/tiling bounds, simple min/max.

* **1100–1200**: Greedy with algebraic bounds, basic combinatorics $\binom{n}{2}$, prefix sum lookup.

* **1300–1400**: Decoupling 2D constraints into independent 1D problems, invariants over operations, GCD/LCM properties.

* **1500–1600**: Pairwise algebraic expansion, SOS bitwise operations, median minimization, contribution counting.

* **1700–1800**: Advanced modular arithmetic, game theory Grundy reduction, combinatorics with Inclusion-Exclusion.

* **1900**: Multi-form hybrid modeling (e.g., SOS DP + Number Theory, Matrix Exponentiation + Invariants).

## PART 36 — MASTER PATTERN INDEX

| Statement Signal | Immediate Mathematical Model | 
 | ----- | ----- | 
| "Equalize array via $+1/-1$ on pairs" | Sum Invariant: $\sum A_i$ must be divisible by $n$ | 
| "Minimize sum of absolute differences $\sum \Vert{}x - A_i\Vert{}$" | Set $x = \text{Median}(A)$ | 
| "Count pairs with $A_i + A_j = K$" | Map frequency lookup or $\frac{1}{2} \sum F[v] \cdot F[K-v]$ | 
| "Subarray sum divisible by $K$" | Prefix remainders equal: $P_r \equiv P_{l-1} \pmod K$ | 
| "Range addition $+v$ on $[L, R]$" | Difference Array: $D[L] += v, D[R+1] -= v$ | 
| "Bitwise AND non-zero across subset" | Evaluate each bit position $b \in [0, 30]$ independently | 
| "Partition into 2 subset with equal sum" | Subset sum DP / Feasibility check $\sum A_i \bmod 2 == 0$ | 
| "Manhattan distance $\Vert{}x_1 - x_2\Vert{} + \Vert{}y_1 - y_2\Vert{}$" | Transform coordinates: $u = x+y, v = x-y \implies \max(\vert{}u_1-u_2\vert{}, \vert{}v_1-v_2\vert{})$ | 
| "Number of ways to split $N$ into $K$ parts" | Stars and Bars: $\binom{N-1}{K-1}$ or $\binom{N+K-1}{K-1}$ | 

## PART 37 — FINAL MATHEMATICAL MODELING WORKFLOW

```
                        CODEFORCES PROBLEM
                                │
                                ▼
                        STRIP STORY TEXT
                                │
                                ▼
                     DEFINE VARIABLES & BOUNDS
                                │
                                ▼
                   WRITE ALGEBRAIC EQUATIONS
                                │
                ┌───────────────┴───────────────┐
                ▼                               ▼
         STATIC EQUATIONS              OPERATIONS SPECIFIED
                │                               │
                ▼                               ▼
       DECOUPLE VARIABLES /              FIND SYSTEM INVARIANTS
       SORT / TRANSITION                 (Sum, Parity, XOR, GCD)
                │                               │
                └───────────────┬───────────────┘
                                ▼
                    CHECK INDEPENDENT DIMENSIONS
                    (Bits, Coordinates, Modulo)
                                │
                                ▼
                       DERIVE ALGORITHM
                   (Prefix, Hash, DP, Binary Search)
                                │
                                ▼
                      PROVE CORRECTNESS
                                │
                                ▼
                     IMPLEMENT C++17 SOLUTION

```

> **Final Coach Advice**: When reading any Codeforces problem from 800 to 1900, never jump straight to code. First, strip the story, write the mathematical equations, discover the preserved invariant, and let the mathematical structure dictate your algorithm.