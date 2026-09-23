# Part 26 — Common Codeforces Mathematical Forms: Visual Modeling Mastery V2

> **Purpose:** Fast mathematical modeling for Codeforces A/B/C/D-style problems. For every form, train the same chain: **story → variables → equation/state → transformation → algorithm**. The new **Fast Intuition Layer** adds a real-world analogy, ASCII model, mini dry run, and one-line contest memory rule; the original detailed foundation, recognition triggers, worked example, related problem, pseudocode, and traps are preserved below it.

> **5-minute contest habit:** (1) circle the target, (2) replace nouns with variables, (3) write the condition mathematically, (4) isolate the unknown or invariant, (5) identify the matching form, (6) only then choose the algorithm.

## Table of Contents
1. [Form 1: Sum Constraint](#form-1-sum-constraint)
2. [Form 2: Difference Constraint](#form-2-difference-constraint)
3. [Form 3: Product Constraint](#form-3-product-constraint)
4. [Form 4: Ratio Constraint](#form-4-ratio-constraint)
5. [Form 5: Parity Constraint](#form-5-parity-constraint)
6. [Form 6: Divisibility Constraint](#form-6-divisibility-constraint)
7. [Form 7: GCD Constraint](#form-7-gcd-constraint)
8. [Form 8: LCM Constraint](#form-8-lcm-constraint)
9. [Form 9: Modulo Constraint](#form-9-modulo-constraint)
10. [Form 10: Equal Frequency](#form-10-equal-frequency)
11. [Form 11: Pair Counting](#form-11-pair-counting)
12. [Form 12: Complement Pair](#form-12-complement-pair)
13. [Form 13: Difference Pair](#form-13-difference-pair)
14. [Form 14: Equal Remainders](#form-14-equal-remainders)
15. [Form 15: Consecutive Values](#form-15-consecutive-values)
16. [Form 16: Arithmetic Progression](#form-16-arithmetic-progression)
17. [Form 17: Geometric / Doubling](#form-17-geometric--doubling)
18. [Form 18: Median Optimization](#form-18-median-optimization)
19. [Form 19: Prefix Equation](#form-19-prefix-equation)
20. [Form 20: Contribution Counting](#form-20-contribution-counting)
21. [Form 21: Pigeonhole Principle](#form-21-pigeonhole-principle)
22. [Form 22: Inclusion-Exclusion](#form-22-inclusion-exclusion)
23. [Form 23: Invariant](#form-23-invariant)
24. [Form 24: Monovariant](#form-24-monovariant)
25. [Form 25: Reachability](#form-25-reachability)
26. [Form 26: Constructive Equation](#form-26-constructive-equation)
27. [Form 27: Bounding](#form-27-bounding)
28. [Form 28: Extremal Principle](#form-28-extremal-principle)
29. [Form 29: Coordinate Transformation](#form-29-coordinate-transformation)
30. [Form 30: Bit Independence](#form-30-bit-independence)
31. [Form 31: Prime Factor Independence](#form-31-prime-factor-independence)
32. [Form 32: Frequency Compression](#form-32-frequency-compression)
33. [Form 33: Permutation Mathematics](#form-33-permutation-mathematics)
34. [Form 34: Mex Mathematics](#form-34-mex-mathematics)
35. [Form 35: Interval Mathematics](#form-35-interval-mathematics)
36. [Form 36: Grid Parity](#form-36-grid-parity)
37. [Form 37: Cyclic / Modulo Process](#form-37-cyclic--modulo-process)
38. [Form 38: Binary Search Equation](#form-38-binary-search-equation)
39. [Form 39: Stars and Bars](#form-39-stars-and-bars)
40. [Form 40: Diophantine Equation](#form-40-diophantine-equation)

---

## Form 1: Sum Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Sum Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Shopping budget**

```text
REAL WORLD
    |
    v
budget = item1 + item2
    |
    v
100 = 35 + x  ->  x = 65
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `budget = item1 + item2`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Isolate the missing addend: need = target - current.**


### 1. Concept & Mathematical Foundation

The **Sum Constraint** form addresses problems where equations or inequalities involve the sum of elements, such as $A[i] + A[j] = K$ or subsegment sums $\sum_{i=L}^R A[i] = K$.

Instead of checking all pairs or subsegments in $O(N^2)$ or $O(N^3)$, algebraic restructuring is applied:

* **Pair Sums:** Rewrite $A[i] + A[j] = K$ as $A[i] = K - A[j]$. Use a frequency map or hash map to query the presence of $(K - A[j])$ in $O(1)$ time.

* **Subsegment Sums:** Convert to prefix sums where $P[R] - P[L-1] = K$. Restructure as $P[L-1] = P[R] - K$.

* **Sorted Two-Pointer:** If order does not matter, sort the array and use two pointers moving inwards: if $A[L] + A[R] < K$, increment $L$; if $A[L] + A[R] > K$, decrement $R$.

### 2. Recognition Triggers

* "Find two/three elements that sum up to $K$."

* "Count contiguous subarrays whose sum equals $K$ (or is divisible by $K$)."

* Constraints: $N \le 2 \cdot 10^5$ (requiring $O(N)$ or $O(N \log N)$ solutions).

### 3. Complexity Target

* **Naive:** $O(N^2)$ for pairs, $O(N^3)$ for subarrays.

* **Optimal:** $O(N)$ time with $O(N)$ auxiliary space (Hash Map / Prefix Sum), or $O(N \log N)$ time with $O(1)$ auxiliary space (Two Pointers after Sorting).

### 4. Dry-Run Walkthrough

**Problem:** Count pairs $(i, j)$ with $i < j$ such that $A[i] + A[j] = 9$.

**Array:** `A = [2, 7, 11, 15]`

1. `seen = {}`, `count = 0`

2. `i = 0, A[0] = 2`: complement $= 9 - 2 = 7$. Not in `seen`. Add `seen[2] = 1`.

3. `i = 1, A[1] = 7`: complement $= 9 - 7 = 2$. `seen[2]` exists with count $1$. `count += 1` (pair $(0, 1)$ found). Add `seen[7] = 1`.

4. `i = 2, A[2] = 11`: complement $= 9 - 11 = -2$. Not in `seen`. Add `seen[11] = 1`.

5. `i = 3, A[3] = 15`: complement $= 9 - 15 = -6$. Not in `seen`. Add `seen[15] = 1`.

**Total Valid Pairs:** $1$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 560 - Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/?utm_source=gemini) / Codeforces 1520D (Sum/Index Transformation)

* **Statement:** Given an array of integers `nums` and an integer `k`, return the total number of subarrays whose sum equals `k`.

### 6. Pseudocode

```
function subarraySum(nums, k):
    prefix_map = {0: 1}  # Base case: empty prefix sum is 0
    current_sum = 0
    total_count = 0
    
    for num in nums:
        current_sum += num
        target = current_sum - k
        
        if target in prefix_map:
            total_count += prefix_map[target]
            
        prefix_map[current_sum] = prefix_map.get(current_sum, 0) + 1
        
    return total_count

```

### 7. Common Edge Cases & Traps

* **Negative Integers:** Two-pointer approaches fail if the array contains negative numbers (monotonicity breaks). Use prefix sum + hash map instead.

* **Integer Overflow:** Sums can exceed $2^{31} - 1$. Always use 64-bit integers (`long long` in C++).

* **Self-Pairing:** When storing frequency before querying, ensure $A[i]$ doesn't match with itself unless explicit duplicates exist.

## Form 2: Difference Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Difference Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Age gap**

```text
REAL WORLD
    |
    v
older - younger = gap
    |
    v
35 - x = 8  ->  x = 27
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `older - younger = gap`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Move index/value terms to the same side; equal transformed values can be grouped.**


### 1. Concept & Mathematical Foundation

The **Difference Constraint** deals with relations of the form $A[i] - A[j] = K$ or $A[i] - A[j] \le K$.

Algebraic Transformations:

1. **Rearrangement:** Transform $A[i] - A[j] = K$ to $A[i] - K = A[j]$ or $A[i] - i = A[j] - j$.

2. **Index Alignment:** Equations like $A[i] - A[j] = i - j$ can be rewritten as $A[i] - i = A[j] - j$. Defining $B[i] = A[i] - i$ reduces the problem to finding equal elements ($B[i] = B[j]$).

3. **Sliding Window / Two Pointers:** For $|A[i] - A[j]| \le K$ on sorted arrays, maintain two indices $L$ and $R$ where $A[R] - A[L] \le K$.

### 2. Recognition Triggers

* Conditions given as $A[i] - A[j] = i - j$ or $A[i] - A[j] = K$.

* System of inequalities of the form $X_i - X_j \le W_{ij}$ (can be mapped to Shortest Path / System of Difference Constraints via Bellman-Ford).

### 3. Complexity Target

* **Frequency Map / Transformation:** $O(N)$ time, $O(N)$ space.

* **Sorting + Two Pointers:** $O(N \log N)$ time, $O(1)$ auxiliary space.

### 4. Dry-Run Walkthrough

**Problem:** Count pairs $(i, j)$ with $i < j$ such that $A[i] - A[j] = i - j$.

**Array:** `A = [3, 5, 1, 4, 6]` (1-indexed)

1. Rewrite equation: $A[i] - i = A[j] - j$.

2. Compute transformed array $B[i] = A[i] - i$:

   * $B[1] = 3 - 1 = 2$

   * $B[2] = 5 - 2 = 3$

   * $B[3] = 1 - 3 = -2$

   * $B[4] = 4 - 4 = 0$

   * $B[5] = 6 - 5 = 1$

3. Count frequencies of elements in $B$: All elements are distinct, so count $= 0$.
   If $B = [2, 3, 2, 0, 2]$, frequency of $2$ is $3$. Number of valid pairs $= \binom{3}{2} = 3$.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1520D - Same Differences](https://codeforces.com/problemset/problem/1520/D?utm_source=gemini)

* **Statement:** Find the number of pairs $(i, j)$ such that $i < j$ and $A[i] - A[j] = i - j$.

### 6. Pseudocode

```
function countSameDifferences(A):
    freq = map()
    ans = 0
    
    for i from 0 to length(A) - 1:
        val = A[i] - i
        ans += freq.get(val, 0)
        freq[val] = freq.get(val, 0) + 1
        
    return ans

```

### 7. Common Edge Cases & Traps

* **Negative Results in Transformation:** $A[i] - i$ can be negative. Standard array indexing will fail; use a hash map or offset array.

* **Combinatorial Overflow:** $\binom{\text{count}}{2}$ can exceed $2^{31}-1$. Use 64-bit integers for the answer.

## Form 3: Product Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Product Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Packing boxes**

```text
REAL WORLD
    |
    v
boxes * itemsPerBox = total
    |
    v
4 * x = 24  ->  x = 6
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `boxes * itemsPerBox = total`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Products suggest factors, divisors, logs, or a multiplicative window.**


### 1. Concept & Mathematical Foundation

Constraints involving products (e.g., $A[i] \cdot A[j] = K$ or $\prod A_i \le K$) grow exponentially or involve factor analysis.

Key Principles:

1. **Divisor Enumeration:** For $A[i] \cdot A[j] = K$, $A[i]$ must be a factor of $K$. The number of factors of $K$ up to $10^{12}$ is relatively small ($D(K) \le 6720$ for $K \le 10^{12}$).

2. **Logarithmic Transformation:** Convert product constraints into sum constraints:
   

   $$
   \log(A_1 \cdot A_2 \cdots A_k) = \log(A_1) + \log(A_2) + \dots + \log(A_k)
   $$

3. **Bounding:** Products grow rapidly. If $A[i] \ge 2$, a product exceeds $10^9$ in at most 30 steps.

### 2. Recognition Triggers

* "Find pairs/subarrays where product equals $K$."

* "Maximize/minimize product under non-negative constraints."

* Constraints involving products bounded by $10^5$ or $10^9$.

### 3. Complexity Target

* Divisor enumeration per query: $O(\sqrt{K})$.

* Bounded product DP/Sliding Window: $O(N \log (\text{MAX\_VAL}))$.

### 4. Dry-Run Walkthrough

**Problem:** Count pairs $(i, j)$ such that $A[i] \cdot A[j] = 12$.

**Array:** `A = [2, 3, 4, 6]`

1. `freq = {2: 1, 3: 1, 4: 1, 6: 1}`

2. Process elements:

   * $A[0] = 2$: Target $= 12 / 2 = 6$. `freq[6]` $= 1$. Pair $(2, 6)$ valid.

   * $A[1] = 3$: Target $= 12 / 3 = 4$. `freq[4]` $= 1$. Pair $(3, 4)$ valid.

3. Total valid pairs $= 2$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 713 - Subarray Product Less Than K](https://leetcode.com/problems/subarray-product-less-than-k/?utm_source=gemini)

* **Statement:** Count contiguous subarrays where the product of all elements is strictly less than $K$.

### 6. Pseudocode

```
function numSubarrayProductLessThanK(nums, k):
    if k <= 1:
        return 0
        
    prod = 1
    ans = 0
    left = 0
    
    for right from 0 to length(nums) - 1:
        prod *= nums[right]
        while prod >= k and left <= right:
            prod /= nums[left]
            left += 1
        ans += (right - left + 1)
        
    return ans

```

### 7. Common Edge Cases & Traps

* **Zeros in Array:** $0$ invalidates division $K / A[i]$ and makes product sliding windows non-monotonic. Handle $0$ separately.

* **Precision Loss with Logarithms:** Using `double` for log sums can introduce floating-point errors. Use integers or epsilon thresholds ($\epsilon = 10^{-9}$).

## Form 4: Ratio Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Ratio Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Recipe scaling**

```text
REAL WORLD
    |
    v
flour / water = p / q
    |
    v
300/200 = 3/2  ->  300*2 = 200*3
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `flour / water = p / q`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Remove division with cross multiplication before comparing ratios.**


### 1. Concept & Mathematical Foundation

Ratio constraints involve fractions $\frac{A[i]}{A[j]} = \frac{P}{Q}$ or maximizing average ratios $\frac{\sum A_i}{\sum B_i}$.

Techniques:

1. **Cross Multiplication:** Always eliminate division to avoid floating-point inaccuracies:
   

   $$
   \frac{A[i]}{A[j]} = \frac{P}{Q} \iff A[i] \cdot Q = A[j] \cdot P
   $$

2. **Fraction Reduction:** Simplify $\frac{P}{Q}$ using $\gcd(P, Q)$ to standard form $(\frac{P}{\gcd(P,Q)}, \frac{Q}{\gcd(P,Q)})$.

3. **Binary Search on Answer (Fractional Programming):** To check if $\frac{\sum A_i}{\sum B_i} \ge K$:
   

   $$
   \sum A_i - K \sum B_i \ge 0 \iff \sum (A_i - K \cdot B_i) \ge 0
   $$

### 2. Recognition Triggers

* "Maximize $\frac{\sum A_i}{\sum B_i}$."

* "Find pairs with ratio equal to $X/Y$."

### 3. Complexity Target

* Fractional Programming: $O(N \log(\text{Precision}))$.

* Frequency Map Matching: $O(N \log N)$.

### 4. Dry-Run Walkthrough

**Problem:** Determine if there exists a subset where $\frac{\sum A_i}{\sum B_i} \ge 2$.

`A = [4, 6]`, `B = [1, 4]`, Target ratio $K = 2$.

1. Transform to $C[i] = A[i] - K \cdot B[i]$:

   * $C[0] = 4 - 2(1) = 2$

   * $C[1] = 6 - 2(4) = -2$

2. Sum of subset $\{C[0]\} = 2 \ge 0$.

3. Since max subset sum $\ge 0$, ratio $\ge 2$ is possible.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1042D / LeetCode 1802\] / \[Codeforces 489C\]

* **Representative Problem:** \[Codeforces 1197D\] / Standard Fractional Programming (0-1 Knapsack Average)

### 6. Pseudocode

```
function checkRatioPossible(A, B, TargetK, N):
    C = array of size N
    for i from 0 to N - 1:
        C[i] = A[i] - TargetK * B[i]
        
    sort(C in descending order)
    
    current_sum = 0
    for i from 0 to N - 1:
        current_sum += C[i]
        if current_sum >= 0 and i > 0:
            return True
            
    return False

```

### 7. Common Edge Cases & Traps

* **Division by Zero:** Check if denominator $B[j] = 0$.

* **Floating-Point Precision:** Never use `A[i] / A[j] == K`. Always use cross-multiplication or binary search over floating values with precision bounds.

## Form 5: Parity Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Parity Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Pairing socks**

```text
REAL WORLD
    |
    v
odd/even is value mod 2
    |
    v
7 mod 2 = 1; 8 mod 2 = 0
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `odd/even is value mod 2`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **When only odd/even matters, throw away magnitude and keep mod 2.**


### 1. Concept & Mathematical Foundation

Parity isolates properties of numbers modulo 2 ($0$ for Even, $1$ for Odd).

Key Parity Rules:

* $\text{Even} \pm \text{Even} = \text{Even}$

* $\text{Odd} \pm \text{Odd} = \text{Even}$

* $\text{Even} \pm \text{Odd} = \text{Odd}$

* Sum parity depends solely on the number of **Odd** elements: if count of Odds is odd, sum is Odd; if count of Odds is even, sum is Even.

### 2. Recognition Triggers

* Operations like $A[i] + A[j]$ or $|A[i] - A[j]|$.

* Parity of total array sum invariant under parity-preserving transformations.

* Bipartite graphs and grid movement ($(x+y) \bmod 2$).

### 3. Complexity Target

* $O(N)$ or $O(1)$ based on counting odd/even counts or modulo arithmetic.

### 4. Dry-Run Walkthrough

**Problem:** Can we make all elements even by repeatedly replacing $(A[i], A[j])$ with $A[i] + A[j]$?

`A = [1, 3, 4, 6]`

Odd count $= 2$ (elements 1, 3).

Pairing 1 and 3 gives $1+3=4$ (Even). Array becomes `[4, 4, 6]`. All elements are now even.

**Rule:** Possible if and only if total count of odd elements is **even**.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1607B - Odd Grasshopper](https://codeforces.com/problemset/problem/1607/B?utm_source=gemini)

### 6. Pseudocode

```
function solveGrasshopper(x0, n):
    rem = n % 4
    delta = 0
    if rem == 1: delta = -n
    elif rem == 2: delta = 1
    elif rem == 3: delta = n + 1
    
    if x0 % 2 != 0:
        return x0 - delta
    else:
        return x0 + delta

```

### 7. Common Edge Cases & Traps

* **Negative Modulo in C++:** `-3 % 2` yields `-1` in C++. Always use `(x % 2 + 2) % 2` to determine parity safely.

## Form 6: Divisibility Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Divisibility Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Packing equally**

```text
REAL WORLD
    |
    v
total % groupSize = 0
    |
    v
24 % 6 = 0
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `total % groupSize = 0`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Translate “evenly”, “multiple”, and “divides” directly into modulo 0.**


### 1. Concept & Mathematical Foundation

Condition: $A[i]$ is divisible by $K$ ($A[i] \equiv 0 \pmod K$).

Key Principles:

1. **Prime Factorization:** $A$ divides $B \iff$ every prime exponent in $A$'s factorization is $\le$ the corresponding exponent in $B$'s.

2. **Modulo Class Grouping:** Group elements by $A[i] \bmod K$.

3. **Transitivity:** If $A \mid B$ and $B \mid C$, then $A \mid C$.

### 2. Recognition Triggers

* "Find pairs where $A[i]$ is divisible by $A[j]$."

* "Select a subset where every element divides the next."

### 3. Complexity Target

* Dynamic Programming over Divisors/Multiples: $O(N \log N)$ or $O(N \sqrt{\text{MAX}})$.

### 4. Dry-Run Walkthrough

**Problem:** Longest divisibility chain in `A = [2, 4, 8, 3, 9]`.

1. Sort `A`: `[2, 3, 4, 8, 9]`

2. `dp[x]` stores longest chain ending at $x$:

   * $x=2: dp[2] = 1$

   * $x=3: dp[3] = 1$

   * $x=4: dp[4] = dp[2] + 1 = 2$

   * $x=8: dp[8] = dp[4] + 1 = 3$

   * $x=9: dp[9] = dp[3] + 1 = 2$

3. Max chain length $= 3$ (`[2, 4, 8]`).

### 5. Classic Example Problem

* **Problem:** [LeetCode 368 - Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset/?utm_source=gemini)

### 6. Pseudocode

```
function largestDivisibleSubset(nums):
    sort(nums)
    N = length(nums)
    dp = array of size N initialized to 1
    
    for i from 0 to N - 1:
        for j from 0 to i - 1:
            if nums[i] % nums[j] == 0:
                dp[i] = max(dp[i], dp[j] + 1)
                
    return max(dp)

```

### 7. Common Edge Cases & Traps

* **Element** $1$**:** $1$ divides every integer; handle $1$ as a universal base case.

* **Duplicates:** Ensure duplicate values are accounted for without causing infinite DP loops.

## Form 7: GCD Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** GCD Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Largest equal tile size**

```text
REAL WORLD
    |
    v
tile divides every length
    |
    v
gcd(12,18)=6
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `tile divides every length`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **GCD is the largest common unit that divides every relevant quantity.**


### 1. Concept & Mathematical Foundation

Properties of $\gcd(A_1, A_2, \dots, A_k)$:

1. **Monotonicity:** Adding elements to a set can only decrease or keep the GCD constant ($\gcd(A, B) \le \min(A, B)$).

2. **Logarithmic Transitions:** A prefix GCD array $G[i] = \gcd(A[1] \dots A[i])$ changes value at most $O(\log(\max A))$ times.

3. **Euclidean Algorithm:** $\gcd(a, b) = \gcd(b, a \bmod b)$ operates in $O(\log(\min(a, b)))$ time.

### 2. Recognition Triggers

* "Subarray with GCD equal to $K$ or $1$."

* "Maximize GCD after removing/changing one element."

### 3. Complexity Target

* Sparse Table / Segment Tree for Range GCD Queries: $O(N \log N + Q \log(\text{MAX}))$.

* Frequency table of multiples: $O(\text{MAX} \log (\text{MAX}))$.

### 4. Dry-Run Walkthrough

**Problem:** Check if array `A = [12, 18, 24]` has subsegment GCD equal to $6$.

1. $\gcd(12, 18) = 6$.

2. $\gcd(6, 24) = 6$.

3. Total array GCD $= 6$. Output: `True`.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1458A - Row GCD](https://codeforces.com/problemset/problem/1458/A?utm_source=gemini)

* **Key Identity:** $\gcd(a_1+b, a_2+b, \dots, a_n+b) = \gcd(a_1+b, |a_2-a_1|, |a_3-a_1|, \dots, |a_n-a_1|)$.

### 6. Pseudocode

```
function solveRowGCD(A, B):
    g = 0
    for i from 1 to length(A) - 1:
        g = gcd(g, abs(A[i] - A[0]))
        
    results = []
    for x in B:
        results.append(gcd(A[0] + x, g))
        
    return results

```

### 7. Common Edge Cases & Traps

* $\gcd(0, x) = x$**:** Base case for GCD accumulators must be initialized to $0$.

* **Large Inputs:** $\gcd$ operations take $O(\log(\text{VAL}))$ time; avoid repeated redundant GCD computations inside tight loops.

## Form 8: LCM Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** LCM Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Repeating bus schedules**

```text
REAL WORLD
    |
    v
first simultaneous repeat = LCM
    |
    v
4-min and 6-min buses meet every 12 min
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `first simultaneous repeat = LCM`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **LCM is the earliest common multiple / synchronization time.**


### 1. Concept & Mathematical Foundation

Relation with GCD:

$$
\text{lcm}(a, b) = \frac{a \cdot b}{\gcd(a, b)}
$$

Properties:

1. **Growth Rate:** LCM of multiple numbers grows exponentially fast. $\text{lcm}(1, 2, \dots, 40) > 10^{14}$.

2. **Prime Factorization:** $\text{lcm}(a, b) = \prod p_i^{\max(e_a, e_b)}$.

### 2. Recognition Triggers

* "Find minimal $N$ divisible by all $A[i]$."

* Constraints on LCM $\le 10^9$ or $10^{18}$.

### 3. Complexity Target

* Pairwise LCM calculation: $O(\log(\min(a, b)))$.

* Subset LCM: Fast prime factorization or bounded search.

### 4. Dry-Run Walkthrough

**Problem:** Compute LCM of `[4, 6, 10]`.

1. $\text{lcm}(4, 6) = \frac{4 \times 6}{\gcd(4, 6)} = \frac{24}{2} = 12$.

2. $\text{lcm}(12, 10) = \frac{12 \times 10}{\gcd(12, 10)} = \frac{120}{2} = 60$.

3. Result $= 60$.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1389B / Codeforces 1475G\] / \[Codeforces 1665C\]

* **Representative Problem:** [Codeforces 1765M - Minimum LCM](https://codeforces.com/problemset/problem/1765/M?utm_source=gemini)

### 6. Pseudocode

```
function minLCM(n):
    # Find largest proper divisor of n
    largest_divisor = 1
    for d = 2 to sqrt(n):
        if n % d == 0:
            largest_divisor = n / d
            break
            
    a = largest_divisor
    b = n - a
    return a, b

```

### 7. Common Edge Cases & Traps

* **Overflow in Multiplication:** $a \cdot b$ can overflow before division by $\gcd(a, b)$. Always compute `(a / gcd(a, b)) * b`.

## Form 9: Modulo Constraint

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Modulo Constraint problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Clock arithmetic**

```text
REAL WORLD
    |
    v
state = value mod M
    |
    v
17 mod 5 = 2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `state = value mod M`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Modulo compresses infinitely many values into M remainder states.**


### 1. Concept & Mathematical Foundation

Operations performed under modulo $M$:

1. **Modular Congruence:** $A \equiv B \pmod M \iff (A - B) \bmod M = 0$.

2. **Prefix Modulo:** $(P[R] - P[L-1]) \bmod M = 0 \iff P[R] \bmod M = P[L-1] \bmod M$.

3. **Modular Inverse:** $A / B \pmod M = A \cdot B^{M-2} \pmod M$ (for prime $M$ via Fermat's Little Theorem).

### 2. Recognition Triggers

* "Subarray sum divisible by $K$."

* "Count pairs $(i, j)$ where $(A[i] + A[j]) \bmod M = 0$."

### 3. Complexity Target

* $O(N)$ using Frequency Array of remainders $[0 \dots M-1]$.

### 4. Dry-Run Walkthrough

**Problem:** Count pairs where $(A[i] + A[j]) \bmod 5 = 0$.

`A = [1, 2, 3, 4, 9]`

1. Compute remainders modulo 5: `rem = [1, 2, 3, 4, 4]`

2. Frequency map of remainders: `{1: 1, 2: 1, 3: 1, 4: 2}`

3. Pair combinations:

   * Remainder $1$ pairs with $4$: $freq[1] \times freq[4] = 1 \times 2 = 2$.

   * Remainder $2$ pairs with $3$: $freq[2] \times freq[3] = 1 \times 1 = 1$.

4. Total Pairs $= 3$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 974 - Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/?utm_source=gemini)

### 6. Pseudocode

```
function subarraysDivByK(nums, k):
    remainder_counts = {0: 1}
    current_sum = 0
    ans = 0
    
    for x in nums:
        current_sum += x
        rem = (current_sum % k + k) % k # Normalize negative remainders
        
        if rem in remainder_counts:
            ans += remainder_counts[rem]
            
        remainder_counts[rem] = remainder_counts.get(rem, 0) + 1
        
    return ans

```

### 7. Common Edge Cases & Traps

* **Negative Modulo Handling:** In languages like C++/Java, `-7 % 5 = -2`. Convert to positive remainder: `((x % M) + M) % M`.

## Form 10: Equal Frequency

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Equal Frequency problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Balanced votes**

```text
REAL WORLD
    |
    v
count(A)-count(B)=0
    |
    v
A,B,A,B -> 2-2=0
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `count(A)-count(B)=0`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Balance becomes a zero difference; equal prefix states define a balanced segment.**


### 1. Concept & Mathematical Foundation

Problems requiring segments/subsets where distinct elements appear with equal frequencies (or specific balanced distributions).

Techniques:

1. **Difference Vectors / State Compression:** If tracking frequencies of 2 characters ($c_1, c_2$), define state $S[i] = \text{count}(c_1) - \text{count}(c_2)$. Equal frequency occurs when $S[R] = S[L-1]$.

2. **Hash State Mapping:** For $K$ elements, maintain difference array relative to the first element's frequency: $D[i] = (\text{freq}_2 - \text{freq}_1, \text{freq}_3 - \text{freq}_1, \dots)$. Hash this vector to find identical prefix states.

### 2. Recognition Triggers

* "Find longest subarray with equal count of 0s and 1s (or multiple values)."

### 3. Complexity Target

* $O(N)$ with Prefix Hash Map / Vector Hashing.

### 4. Dry-Run Walkthrough

**Problem:** Longest subarray with equal number of `0`s and `1`s.

`A = [0, 1, 0, 0, 1, 1]`

1. Map `0` to `-1` and `1` to `+1`: `A' = [-1, 1, -1, -1, 1, 1]`

2. Prefix sums $P$: `[0, -1, 0, -1, -2, -1, 0]` (with $P[0]=0$)

3. First/Last occurrences of prefix values:

   * Value `0`: indices $0$ and $6 \implies \text{length} = 6 - 0 = 6$.

4. Max Length $= 6$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 525 - Contiguous Array](https://leetcode.com/problems/contiguous-array/?utm_source=gemini)

### 6. Pseudocode

```
function findMaxLength(nums):
    first_seen = {0: -1}
    max_len = 0
    prefix = 0
    
    for i from 0 to length(nums) - 1:
        prefix += (1 if nums[i] == 1 else -1)
        
        if prefix in first_seen:
            max_len = max(max_len, i - first_seen[prefix])
        else:
            first_seen[prefix] = i
            
    return max_len

```

### 7. Common Edge Cases & Traps

* **State Vector Hashing Collisions:** Custom roll-hash or tuple hashing required when tracking $K > 3$ distinct elements.

## Form 11: Pair Counting

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Pair Counting problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Handshakes**

```text
REAL WORLD
    |
    v
each new person pairs with previous people
    |
    v
4 people -> 0+1+2+3=6
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `each new person pairs with previous people`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Count how many earlier elements can pair with the current one.**


### 1. Concept & Mathematical Foundation

Count pairs $(i, j)$ with $i < j$ satisfying a specific relation $R(A[i], A[j])$.

Core Principle:
Use **Contribution Technique**. Instead of checking all $O(N^2)$ pairs, iterate through each element $A[j]$ and count how many valid $A[i]$ ($i < j$) exist in a dynamic data structure (Frequency Map, Fenwick Tree, Trie).

### 2. Recognition Triggers

* "Count pairs $(i, j)$ such that $A[i] + A[j] > K$ or $A[i] \oplus A[j] < K$."

### 3. Complexity Target

* $O(N)$ or $O(N \log N)$.

### 4. Dry-Run Walkthrough

**Problem:** Count pairs $i < j$ where $A[i] = A[j]$.

`A = [1, 2, 1, 1, 2]`

1. Process elements:

   * $i=0, A[0]=1$: count $+=$ `freq[1]` ($0$). `freq[1] = 1`.

   * $i=1, A[1]=2$: count $+=$ `freq[2]` ($0$). `freq[2] = 1`.

   * $i=2, A[2]=1$: count $+=$ `freq[1]` ($1$). `freq[1] = 2`.

   * $i=3, A[3]=1$: count $+=$ `freq[1]` ($2$). `freq[1] = 3`.

   * $i=4, A[4]=2$: count $+=$ `freq[2]` ($1$). `freq[2] = 2`.

2. Total Count $= 0 + 0 + 1 + 2 + 1 = 4$.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1311A / LeetCode 1512 - Number of Good Pairs](https://leetcode.com/problems/number-of-good-pairs/?utm_source=gemini)

### 6. Pseudocode

```
function numIdenticalPairs(nums):
    freq = map()
    ans = 0
    for x in nums:
        ans += freq.get(x, 0)
        freq[x] = freq.get(x, 0) + 1
    return ans

```

### 7. Common Edge Cases & Traps

* **Combinatorial Formula vs Iterative Addition:** Adding `freq[x]` on the fly automatically enforces $i < j$ without needing division by 2.

## Form 12: Complement Pair

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Complement Pair problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Exact bill**

```text
REAL WORLD
    |
    v
current + complement = target
    |
    v
target 10, current 4 -> need 6
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `current + complement = target`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Compute the unique missing complement and look it up.**


### 1. Concept & Mathematical Foundation

Pairs $(A[i], A[j])$ that combine to form a full bitwise target (e.g., $A[i] \text{ OR } A[j] = (2^k - 1)$ or $A[i] \oplus A[j] = \text{TARGET}$).

Bitwise Complement Rule:

$$
\text{Complement}(X) = \text{TARGET} \oplus X
$$

### 2. Recognition Triggers

* "Find two numbers whose XOR / OR forms a complete mask."

### 3. Complexity Target

* $O(N)$ or $O(N \cdot 2^B)$ where $B$ is bit length (SOS DP / Submask iteration).

### 4. Dry-Run Walkthrough

Target Mask $= 7$ (`111` in binary).

`A = [3, 4, 5, 2]` (`3` = `011`, `4` = `100`, `5` = `101`, `2` = `010`).

1. $A[0] = 3$ (`011`): Needed $= 7 \oplus 3 = 4$ (`100`).

2. $A[1] = 4$ (`100`): Complement `3` exists in array! Pair $(3, 4)$ is valid ($3 \oplus 4 = 7$).

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1658D1 / LeetCode 1835\] / \[Codeforces 1554C\]

### 6. Pseudocode

```
function countComplementPairs(nums, target_mask):
    freq = map()
    ans = 0
    for x in nums:
        needed = target_mask ^ x
        ans += freq.get(needed, 0)
        freq[x] = freq.get(x, 0) + 1
    return ans

```

### 7. Common Edge Cases & Traps

* Ensure the `target_mask` covers all active bit positions (e.g., $(1 \ll B) - 1$).

## Form 13: Difference Pair

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Difference Pair problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Temperature gap**

```text
REAL WORLD
    |
    v
high-low=K
    |
    v
9-5=4
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `high-low=K`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **For difference K, search x-K or x+K; remember subtraction has direction.**


### 1. Concept & Mathematical Foundation

Finding pairs where $A[i] - A[j] = K$.

Different from Sum Pairs:

$$
A[i] - A[j] = K \iff A[i] = A[j] + K \text{ or } A[j] = A[i] - K
$$


Because subtraction is non-commutative, two lookups are required if order matters, or one lookup during a single forward pass.

### 2. Recognition Triggers

* "Find pairs with absolute difference equal to $K$."

### 3. Complexity Target

* $O(N)$ with Hash Set, or $O(N \log N)$ with Sorting + Two Pointers.

### 4. Dry-Run Walkthrough

`A = [1, 5, 3, 4, 2]`, $K = 2$.

1. Hash Set $S = \{1, 5, 3, 4, 2\}$.

2. For each $x$: check if $x + K \in S$:

   * $1 + 2 = 3 \in S \implies (3, 1)$

   * $5 + 2 = 7 \notin S$

   * $3 + 2 = 5 \in S \implies (5, 3)$

   * $4 + 2 = 6 \notin S$

   * $2 + 2 = 4 \in S \implies (4, 2)$

3. Total Pairs $= 3$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 2006 - Count Number of Pairs With Absolute Difference K](https://leetcode.com/problems/count-number-of-pairs-with-absolute-difference-k/?utm_source=gemini)

### 6. Pseudocode

```
function countKDifference(nums, k):
    freq = map()
    ans = 0
    for x in nums:
        ans += freq.get(x - k, 0) + freq.get(x + k, 0)
        freq[x] = freq.get(x, 0) + 1
    return ans

```

### 7. Common Edge Cases & Traps

* $K = 0$**:** Special case. If $K=0$, look for duplicate elements ($\binom{\text{count}}{2}$).

## Form 14: Equal Remainders

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Equal Remainders problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Same weekday cycle**

```text
REAL WORLD
    |
    v
a mod M = b mod M
    |
    v
14 mod 6 = 20 mod 6 = 2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `a mod M = b mod M`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Equal remainders mean their difference is divisible by M.**


### 1. Concept & Mathematical Foundation

Equation: $A[i] \bmod M = A[j] \bmod M$.

Mathematical Equivalent:

$$
A[i] - A[j] \equiv 0 \pmod M \iff M \mid (A[i] - A[j])
$$


To maximize $M$ such that two elements have equal remainders, set $M = |A[i] - A[j]|$. To make multiple elements equal modulo $M$, $M$ must divide $\gcd$ of all pairwise differences.

### 2. Recognition Triggers

* "Find integer $M > 1$ such that all elements give the same remainder when divided by $M$."

### 3. Complexity Target

* $O(N + \log(\text{MAX}))$.

### 4. Dry-Run Walkthrough

`A = [14, 22, 38]`

1. Pairwise differences:

   * $22 - 14 = 8$

   * $38 - 22 = 16$

2. Compute $\gcd(8, 16) = 8$.

3. Possible values of $M$: Any divisor of $8$ (i.e., $2, 4, 8$). Maximum $M = 8$.

   * $14 \bmod 8 = 6$, $22 \bmod 8 = 6$, $38 \bmod 8 = 6$.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1582B / LeetCode 2447\] / \[Codeforces 1468J\]

### 6. Pseudocode

```
function findMaxM(A):
    sort(A)
    g = 0
    for i from 1 to length(A) - 1:
        g = gcd(g, A[i] - A[i-1])
    return g

```

### 7. Common Edge Cases & Traps

* **All elements equal:** Difference is $0$. Any $M$ works; answer is infinite or bounded by problem constraints.

## Form 15: Consecutive Values

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Consecutive Values problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — House numbers**

```text
REAL WORLD
    |
    v
next = current + 1
    |
    v
5,6,7,8
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `next = current + 1`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Consecutive distinct values satisfy max-min = count-1.**


### 1. Concept & Mathematical Foundation

Conditions where elements form consecutive sequences $[X, X+1, X+2, \dots]$.

Properties:

1. Max - Min Property: For a range without duplicates to be consecutive, $\max(L, R) - \min(L, R) = R - L$.

2. Sum Formula: $\sum_{i=0}^{k-1} (X+i) = k \cdot X + \frac{k(k-1)}{2}$.

### 2. Recognition Triggers

* "Reorder array into contiguous consecutive runs."

* "Find maximum contiguous consecutive segment."

### 3. Complexity Target

* $O(N)$ with Hash Set or $O(N \log N)$ with Sorting.

### 4. Dry-Run Walkthrough

`A = [100, 4, 200, 1, 3, 2]`

1. Insert into Set $S = \{1, 2, 3, 4, 100, 200\}$.

2. Iterate $x \in S$:

   * Check if $x-1 \in S$. If no, $x$ is the start of a sequence!

   * For $x = 1$: $0 \notin S \implies$ streak starts at $1$. Expand: $2, 3, 4 \in S \implies$ length $4$.

   * For $x = 100$: $99 \notin S \implies$ length $1$.

   * For $x = 200$: $199 \notin S \implies$ length $1$.

3. Max Length $= 4$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 128 - Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/?utm_source=gemini)

### 6. Pseudocode

```
function longestConsecutive(nums):
    num_set = set(nums)
    longest = 0
    
    for x in num_set:
        if (x - 1) not in num_set:
            current_num = x
            current_streak = 1
            
            while (current_num + 1) in num_set:
                current_num += 1
                current_streak += 1
                
            longest = max(longest, current_streak)
            
    return longest

```

### 7. Common Edge Cases & Traps

* **Duplicates:** Deduplicate using a set first to avoid $O(N^2)$ traversal behavior.

## Form 16: Arithmetic Progression

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Arithmetic Progression problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Saving fixed amount weekly**

```text
REAL WORLD
    |
    v
next = current + d
    |
    v
10,15,20,25; d=5
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `next = current + d`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **AP means constant first difference.**


### 1. Concept & Mathematical Foundation

An Arithmetic Progression (AP) is a sequence where $A[i+1] - A[i] = D$ (common difference).

Formulas:

* $A[i] = A[0] + i \cdot D$

* Range Sum: $S_N = \frac{N}{2} (2A[0] + (N-1)D)$

### 2. Recognition Triggers

* "Check if array can be rearranged into an arithmetic progression."

* "Find maximum length AP subsequence."

### 3. Complexity Target

* Check AP: $O(N)$.

* Longest AP Subsequence: $O(N^2)$ DP.

### 4. Dry-Run Walkthrough

`A = [3, 5, 1]`

1. Sort $A$: `[1, 3, 5]`

2. $D = A[1] - A[0] = 3 - 1 = 2$.

3. Check subsequent differences: $A[2] - A[1] = 5 - 3 = 2 == D$.

4. Valid AP!

### 5. Classic Example Problem

* **Problem:** [LeetCode 1502 - Can Make Arithmetic Progression From Sequence](https://leetcode.com/problems/can-make-arithmetic-progression-from-sequence/?utm_source=gemini)

### 6. Pseudocode

```
function canMakeArithmeticProgression(arr):
    sort(arr)
    diff = arr[1] - arr[0]
    for i from 2 to length(arr) - 1:
        if arr[i] - arr[i-1] != diff:
            return False
    return True

```

### 7. Common Edge Cases & Traps

* $D = 0$**:** Array with all identical elements is a valid AP ($D=0$).

## Form 17: Geometric / Doubling

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Geometric / Doubling problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Bacteria doubling**

```text
REAL WORLD
    |
    v
next = current * r
    |
    v
1,2,4,8,16
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `next = current * r`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Multiplicative growth becomes logarithmic in the number of steps.**


### 1. Concept & Mathematical Foundation

A Geometric Progression satisfies $A[i+1] = A[i] \cdot R$. Exponential growth implies sequence values exceed standard limits very quickly.

Key Principles:

1. **Binary Lifting / Doubling:** Jump steps in powers of $2$ ($1, 2, 4, 8, \dots, 2^k$) to perform range/tree queries in $O(\log N)$.

2. **Logarithmic Bounding:** $R^k \le M \implies k \le \log_R M$.

### 2. Recognition Triggers

* "Binary lifting on trees / functional graphs."

* "Subsequence where $A[i+1] / A[i] = R$."

### 3. Complexity Target

* $O(N \log N)$ precomputation, $O(\log N)$ query.

### 4. Dry-Run Walkthrough

Find $2^k$-th ancestor of node $u$ in a tree using Doubling.

`up[u][i]` stores the $2^i$-th ancestor of $u$.

Relation: `up[u][i] = up[up[u][i-1]][i-1]`

To find $13$-th ancestor ($13 = 8 + 4 + 1 = 2^3 + 2^2 + 2^0$):

1. Jump to $2^3 = 8$-th ancestor.

2. From there, jump to $2^2 = 4$-th ancestor.

3. From there, jump to $2^0 = 1$-st ancestor. Total steps = 13.

### 5. Classic Example Problem

* **Problem:** [LeetCode 1483 - Kth Ancestor of a Tree Node](https://leetcode.com/problems/kth-ancestor-of-a-tree-node/?utm_source=gemini)

### 6. Pseudocode

```
function buildBinaryLifting(parent_array, N, LOG):
    up = matrix of size N x LOG initialized to -1
    for i from 0 to N - 1:
        up[i][0] = parent_array[i]
        
    for j from 1 to LOG - 1:
        for i from 0 to N - 1:
            if up[i][j-1] != -1:
                up[i][j] = up[up[i][j-1]][j-1]
    return up

```

### 7. Common Edge Cases & Traps

* Jumping past root / out of bounds: Ensure `-1` check handles boundary conditions.

## Form 18: Median Optimization

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Median Optimization problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Choose meeting point on a street**

```text
REAL WORLD
    |
    v
minimize sum |xi-x|
    |
    v
homes 1,2,9 -> choose 2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `minimize sum |xi-x|`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Absolute-distance minimization points to the median.**


### 1. Concept & Mathematical Foundation

The Median minimizes the sum of absolute differences:

$$
f(x) = \sum_{i=1}^N \vert{}A[i] - x\vert{}
$$


The value $x$ that minimizes $f(x)$ is the **Median** of array $A$.

If $N$ is odd, $x = A[N/2]$. If $N$ is even, any value in $[A[N/2 - 1], A[N/2]]$ is optimal.

### 2. Recognition Triggers

* "Minimize total cost to make all array elements equal, where cost is $\vert{}A[i] - X\vert{}$."

### 3. Complexity Target

* $O(N \log N)$ via Sorting, or $O(N)$ via Quickselect.

### 4. Dry-Run Walkthrough

`A = [1, 2, 9]`

1. Median $= 2$.

2. Cost $= \vert{}1-2\vert{} + \vert{}2-2\vert{} + \vert{}9-2\vert{} = 1 + 0 + 7 = 8$.

3. If $x = 1$, Cost $= 0 + 1 + 8 = 9$.

4. If $x = 9$, Cost $= 8 + 7 + 0 = 15$.

5. Minimum cost achieved strictly at Median ($x = 2$).

### 5. Classic Example Problem

* **Problem:** [LeetCode 462 - Minimum Moves to Equal Array Elements II](https://leetcode.com/problems/minimum-moves-to-equal-array-elements-ii/?utm_source=gemini)

### 6. Pseudocode

```
function minMoves2(nums):
    sort(nums)
    median = nums[length(nums) // 2]
    cost = 0
    for x in nums:
        cost += abs(x - median)
    return cost

```

### 7. Common Edge Cases & Traps

* **Weighted Median:** If operations have weights $W[i]$ (cost $W[i] \cdot \vert{}A[i] - X\vert{}$), choose $X$ as the element where cumulative weight reaches $\frac{1}{2} \sum W_i$.

## Form 19: Prefix Equation

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Prefix Equation problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Bank balance history**

```text
REAL WORLD
    |
    v
range = prefixRight-prefixBeforeLeft
    |
    v
P[R]-P[L-1]
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `range = prefixRight-prefixBeforeLeft`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Turn a range condition into an equation between two prefix states.**


### 1. Concept & Mathematical Foundation

Transforming array expressions using Prefix Arrays (Prefix Sum, Prefix XOR, Prefix Min/Max).

Key Relations:

* Range Sum: $\sum_{i=L}^R A[i] = P[R] - P[L-1]$

* Range XOR: $\bigoplus_{i=L}^R A[i] = X[R] \oplus X[L-1]$

### 2. Recognition Triggers

* Frequent subsegment sum or XOR queries.

* "Count subarrays satisfying prefix condition."

### 3. Complexity Target

* Precomputation: $O(N)$. Query: $O(1)$.

### 4. Dry-Run Walkthrough

Find XOR sum of subarray from index $2$ to $4$ in `A = [3, 1, 2, 5, 4]` (1-indexed).

1. Build Prefix XOR array $X$:

   * $X[0] = 0$

   * $X[1] = 3$

   * $X[2] = 3 \oplus 1 = 2$

   * $X[3] = 2 \oplus 2 = 0$

   * $X[4] = 0 \oplus 5 = 5$

2. Query $(2, 4) = X[4] \oplus X[1] = 5 \oplus 3 = 6$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 1310 - XOR Queries of a Subarray](https://leetcode.com/problems/xor-queries-of-a-subarray/?utm_source=gemini)

### 6. Pseudocode

```
function xorQueries(arr, queries):
    N = length(arr)
    px = array of size N + 1 initialized to 0
    for i from 0 to N - 1:
        px[i+1] = px[i] ^ arr[i]
        
    res = []
    for (L, R) in queries:
        res.append(px[R+1] ^ px[L])
    return res

```

### 7. Common Edge Cases & Traps

* **Off-by-One Errors:** Always use 1-based indexing for prefix arrays ($P[0] = 0$) to cleanly handle range queries starting at $L=0$.

## Form 20: Contribution Counting

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Contribution Counting problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Street segments containing one shop**

```text
REAL WORLD
    |
    v
leftChoices * rightChoices
    |
    v
i=2,n=5 -> 3*3=9
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `leftChoices * rightChoices`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Ask how many global structures contain this one element.**


### 1. Concept & Mathematical Foundation

Instead of calculating the value for all $O(N^2)$ subarrays or $O(2^N)$ subsets, compute **how many times a single element** $A[i]$ **contributes** to the global answer.

Total Sum $= \sum_{i=1}^N A[i] \times (\text{number of valid structures containing } A[i])$.

Subarray Contribution Formula:
Element $A[i]$ (at index $i$, 0-indexed) appears in $(i + 1) \times (N - i)$ contiguous subarrays.

### 2. Recognition Triggers

* "Sum of (max - min) over all subarrays."

* "Sum of subarray sums over all possible contiguous segments."

### 3. Complexity Target

* $O(N)$ using Monotonic Stack / Combinatorics.

### 4. Dry-Run Walkthrough

Compute sum of all subarray minimums for `A = [2, 1, 3]`.

1. For element $A[1] = 1$:

   * Left bound (how far left is $1$ the min): index $0$.

   * Right bound (how far right is $1$ the min): index $2$.

   * Total subarrays where $1$ is min: $(1 - 0 + 1) \times (2 - 1 + 1) = 2 \times 2 = 4$.

   * Contribution of $1 = 1 \times 4 = 4$.

2. Repeat for all elements and sum results.

### 5. Classic Example Problem

* **Problem:** [LeetCode 907 - Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/?utm_source=gemini)

### 6. Pseudocode

```
function sumSubarrayMins(arr):
    N = length(arr)
    # Use monotonic stack to find previous smaller and next smaller indices
    ple = array of size N  # Previous Less Element
    nle = array of size N  # Next Less Element
    
    # ... (Populate ple and nle in O(N) using Monotonic Stack) ...
    
    total = 0
    MOD = 10**9 + 7
    for i from 0 to N - 1:
        left_count = i - ple[i]
        right_count = nle[i] - i
        total = (total + arr[i] * left_count * right_count) % MOD
        
    return total

```

### 7. Common Edge Cases & Traps

* **Duplicate Values in Subarray:** Use strict inequality ($<$) on one side and non-strict ($\le$) on the other side when building monotonic stacks to prevent double-counting equal elements.

## Form 21: Pigeonhole Principle

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Pigeonhole Principle problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Seats and people**

```text
REAL WORLD
    |
    v
more objects than states => collision
    |
    v
6 people,5 seats -> one seat gets >=2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `more objects than states => collision`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **If objects exceed possible states, a repeated state is guaranteed.**


### 1. Concept & Mathematical Foundation

If $N+1$ items are placed into $N$ boxes, at least one box contains $\ge 2$ items.

CP Applications:

1. **Subarray Sum Divisibility:** Among $N$ prefix sums $P[1] \dots P[N]$ modulo $N$, either one $P[i] \equiv 0 \pmod N$ or two prefix sums have identical remainders ($P[i] \equiv P[j] \pmod N$). Thus, a subarray sum divisible by $N$ **always exists**.

2. **Bounded Differences:** In any set of $N+1$ integers, there exist two whose difference is divisible by $N$.

### 2. Recognition Triggers

* "Prove or find a non-empty subarray whose sum is divisible by $N$."

* "Constructive guarantees when constraints exceed states."

### 3. Complexity Target

* $O(N)$ deterministic search.

### 4. Dry-Run Walkthrough

Find subarray divisible by $N=3$ in `A = [2, 4, 5]`.

1. Prefix sums: $P = [2, 6, 11]$

2. Modulo 3: $P \bmod 3 = [2, 0, 2]$

3. $P[1] \bmod 3 = 0 \implies$ Subarray `A[0...1] = [2, 4]` sums to $6$, which is divisible by 3.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1455D / Codeforces 618B\] / [Codeforces 1305C - Kuroni and Impossible Calculation](https://codeforces.com/problemset/problem/1305/C?utm_source=gemini)

### 6. Pseudocode

```
function findDivisibleSubarray(A, N):
    seen = {0: -1}
    prefix = 0
    
    for i from 0 to N - 1:
        prefix = (prefix + A[i]) % N
        if prefix in seen:
            left_idx = seen[prefix] + 1
            return A[left_idx : i + 1]
        seen[prefix] = i
        
    return []

```

### 7. Common Edge Cases & Traps

* $N > M$ **Trap:** In problems asking for $\prod_{i<j} \vert{}A[i] - A[j]\vert{} \bmod M$, if $N > M$, by Pigeonhole Principle at least two elements are congruent modulo $M$, so the answer is strictly $0$.

## Form 22: Inclusion-Exclusion

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Inclusion-Exclusion problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — People in two clubs**

```text
REAL WORLD
    |
    v
A or B = A+B-both
    |
    v
20+15-5=30
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `A or B = A+B-both`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Add singles, subtract double counts, then alternate.**


### 1. Concept & Mathematical Foundation

Calculates the size of the union of multiple sets:

$$
\vert{}A_1 \cup A_2 \cup \dots \cup A_k\vert{} = \sum \vert{}A_i\vert{} - \sum \vert{}A_i \cap A_j\vert{} + \sum \vert{}A_i \cap A_j \cap A_k\vert{} - \dots
$$

Principle:

* Add sizes of single sets.

* Subtract sizes of pairwise intersections.

* Add sizes of triple intersections, alternating signs based on odd/even size of subset.

### 2. Recognition Triggers

* "Count integers in range $[1, N]$ co-prime to $K$."

* "Count numbers divisible by at least one prime in a given set."

### 3. Complexity Target

* $O(2^K)$ where $K$ is the number of distinct prime factors of $N$ ($K \le 10$).

### 4. Dry-Run Walkthrough

Count numbers in $[1, 20]$ divisible by $2$ or $3$.

1. Sets: $A$ (divisible by 2), $B$ (divisible by 3).

2. $\vert{}A\vert{} = \lfloor 20/2 \rfloor = 10$.

3. $\vert{}B\vert{} = \lfloor 20/3 \rfloor = 6$.

4. $\vert{}A \cap B\vert{} = \lfloor 20 / \text{lcm}(2, 3) \rfloor = \lfloor 20/6 \rfloor = 3$.

5. Total $= 10 + 6 - 3 = 13$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 878 - Nth Magical Number](https://leetcode.com/problems/nth-magical-number/?utm_source=gemini)

### 6. Pseudocode

```
function countDivisible(N, primes):
    K = length(primes)
    total = 0
    
    for mask from 1 to (1 << K) - 1:
        lcm_val = 1
        bits = 0
        for i from 0 to K - 1:
            if (mask & (1 << i)):
                bits += 1
                lcm_val = lcm(lcm_val, primes[i])
                
        if bits % 2 == 1:
            total += N // lcm_val
        else:
            total -= N // lcm_val
            
    return total

```

### 7. Common Edge Cases & Traps

* **LCM Overflow:** When computing LCM of multiple primes, the product can exceed $10^{18}$. Stop early if $\text{lcm\_val} > N$.

## Form 23: Invariant

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Invariant problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Conserved total**

```text
REAL WORLD
    |
    v
operation changes state but preserves property
    |
    v
move 1 coin A->B: total unchanged
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `operation changes state but preserves property`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Find what every legal operation cannot change.**


### 1. Concept & Mathematical Foundation

An **Invariant** is a property or quantity that remains strictly **unchanged** under a defined set of allowed operations.

Common Invariants:

1. **Sum Parity:** Replacing $(a, b)$ with $a+b$ or $\vert{}a-b\vert{}$ preserves total sum modulo 2.

2. **Inversion Count Parity:** Swapping adjacent elements changes inversion count by exactly $\pm 1$.

3. **Grid Checkerboard Color:** Movement on grid alternates cell parity.

### 2. Recognition Triggers

* "Is it possible to reach Target State $B$ from Initial State $A$ using given operations?"

### 3. Complexity Target

* $O(1)$ or $O(N)$ evaluation of invariant state.

### 4. Dry-Run Walkthrough

Can we transform `A = [1, 2, 3]` to `[0, 0, 1]` by replacing $(a, b)$ with $\vert{}a-b\vert{}$?

1. Initial sum parity: $(1 + 2 + 3) \bmod 2 = 6 \bmod 2 = 0$ (Even).

2. Target sum parity: $(0 + 0 + 1) \bmod 2 = 1 \bmod 2 = 1$ (Odd).

3. Invariant Rule: Parity of sum modulo 2 cannot change under operation $\vert{}a-b\vert{}$.

4. Conclusion: Impossible (`NO`).

### 5. Classic Example Problem

* **Problem:** [Codeforces 1365F - Swaps in Permutation](https://codeforces.com/problemset/problem/1365/F?utm_source=gemini)

### 6. Pseudocode

```
function canTransform(A, B):
    # Check invariant property (e.g. sorted symmetric pairs)
    pairsA = []
    pairsB = []
    N = length(A)
    
    for i from 0 to (N // 2) - 1:
        pairsA.append(min(A[i], A[N-1-i]), max(A[i], A[N-1-i]))
        pairsB.append(min(B[i], B[N-1-i]), max(B[i], B[N-1-i]))
        
    sort(pairsA)
    sort(pairsB)
    
    return pairsA == pairsB

```

### 7. Common Edge Cases & Traps

* Missing subtle invariants like maintaining relative order of odd/even elements.

## Form 24: Monovariant

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Monovariant problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Remaining work**

```text
REAL WORLD
    |
    v
measure always moves one direction
    |
    v
tasks left: 5->4->3->2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `measure always moves one direction`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Find a bounded quantity that strictly increases/decreases to prove termination.**


### 1. Concept & Mathematical Foundation

A **Monovariant** is a quantity that strictly **increases** or strictly **decreases** after every operation.

CP Applications:

1. **Termination Proofs:** Proves that an iterative algorithm/game must terminate in finite steps.

2. **Potential Function:** Assign potential $\Phi(S)$ to state $S$. Show $\Phi(S_{t+1}) < \Phi(S_t)$.

### 2. Recognition Triggers

* "Show that the process always terminates."

* "Find state after executing operations until no longer possible."

### 3. Complexity Target

* Bounded by maximum initial potential $\Phi(S_0)$.

### 4. Dry-Run Walkthrough

Array `A = [3, 2, 1]`. Operation: If $A[i] > A[i+1]$, swap them.

1. Potential Function $\Phi = \text{Inversion Count}$.

2. Initial $\Phi([3, 2, 1]) = 3$.

3. Each swap reduces inversions by exactly $1$.

4. Process terminates after at most $3$ steps.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1486C1 / Codeforces 1551C\] / \[Codeforces 482B\]

### 6. Pseudocode

```
function processMonovariant(A):
    while stateHasValidOperation(A):
        i = findValidOperationIndex(A)
        applyOperation(A, i) # Decreases potential Phi(A)
    return A

```

### 7. Common Edge Cases & Traps

* **Infinite Loops:** If potential function does not strictly change ($\Delta \Phi = 0$), algorithm may loop infinitely.

## Form 25: Reachability

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Reachability problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Elevator step sizes**

```text
REAL WORLD
    |
    v
target-start must belong to reachable set
    |
    v
steps of 2: 0->2->4->6, never 5
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `target-start must belong to reachable set`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Characterize all states obtainable from the start instead of simulating blindly.**


### 1. Concept & Mathematical Foundation

Determining if a target state $T$ can be reached from initial state $S$ via standard moves or transitions.

Methods:

1. **BFS / DFS Traversal:** For small state spaces.

2. **Mathematical Range Intersection:** Checking if target lies within $[ \text{Min\_Possible}, \text{Max\_Possible} ]$ and satisfies parity/gcd constraints.

### 2. Recognition Triggers

* "Can you transform $A$ into $B$ in $\le K$ operations?"

### 3. Complexity Target

* Mathematical Check: $O(1)$.

* Graph Traversal: $O(V + E)$.

### 4. Dry-Run Walkthrough

Start at $(0, 0)$. Move $(x \pm 1, y)$ or $(x, y \pm 1)$. Can we reach $(2, 3)$ in $N = 7$ steps?

1. Minimum steps required (Manhattan distance) $= \vert{}2-0\vert{} + \vert{}3-0\vert{} = 5$.

2. Step check: $N = 7 \ge 5$.

3. Parity check: $(N - \text{Dist}) = 7 - 5 = 2$ (Even).

4. Since extra steps are even, extra moves can waste steps back-and-forth. Output: `YES`.

### 5. Classic Example Problem

* **Problem:** [Codeforces 723A / LeetCode 780 - Reaching Points](https://leetcode.com/problems/reaching-points/?utm_source=gemini)

### 6. Pseudocode

```
function reachingPoints(sx, sy, tx, ty):
    while tx >= sx and ty >= sy:
        if tx == sx and ty == sy: return True
        if tx > ty:
            if ty == sy: return (tx - sx) % ty == 0
            tx %= ty
        else:
            if tx == sx: return (ty - sy) % tx == 0
            ty %= tx
    return False

```

### 7. Common Edge Cases & Traps

* Direct subtraction in `Reaching Points` causes TLE when $tx \gg ty$. Use modulo operations instead.

## Form 26: Constructive Equation

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Constructive Equation problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Split a bill**

```text
REAL WORLD
    |
    v
build variables satisfying equation
    |
    v
a+b=10 -> choose a=4,b=6
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `build variables satisfying equation`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Do not search every answer: choose free variables, derive the rest, verify bounds.**


### 1. Concept & Mathematical Foundation

Problems asking to construct an array/matrix satisfying mathematical equations rather than merely counting or finding optimal values.

Common Strategies:

1. **Symmetry & Base Cases:** Fix $A[0], A[1]$ to convenient values (like $1, 2, 2^k$) to simplify terms.

2. **Telescoping Sums:** Set $A[i] = X_{i} - X_{i-1}$ so prefix sums resolve cleanly.

3. **Bitwise Independence:** Construct individual bits separately.

### 2. Recognition Triggers

* "Construct an array of size $N$ such that $A[i] + A[j] = A[k]$..."

* "Output ANY valid array satisfying constraints."

### 3. Complexity Target

* $O(N)$ linear construction.

### 4. Dry-Run Walkthrough

Construct array of size $N=4$ where sum of elements equals product of elements.

1. Let $A = [1, 1, 2, 4]$.

2. Sum $= 1 + 1 + 2 + 4 = 8$.

3. Product $= 1 \times 1 \times 2 \times 4 = 8$.

4. Valid construction!

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1515D / Codeforces 1714D\] / \[Codeforces 1334B\]

### 6. Pseudocode

```
function constructArray(N):
    # Example: Construct array where A[i] ^ A[i+1] is constant
    ans = []
    for i from 0 to N - 1:
        if i % 2 == 0: ans.append(1)
        else: ans.append(3)
    return ans

```

### 7. Common Edge Cases & Traps

* Violating problem bounds (e.g. elements must be distinct or positive).

## Form 27: Bounding

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Bounding problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Capacity limits**

```text
REAL WORLD
    |
    v
minimum possible <= answer <= maximum possible
    |
    v
5 boxes each 2..7 -> total 10..35
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `minimum possible <= answer <= maximum possible`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Before searching, trap the answer between unavoidable lower and upper bounds.**


### 1. Concept & Mathematical Foundation

Establishing tight lower and upper bounds on a target function to eliminate impossible states or reduce search space.

Key Inequalities:

1. **AM-GM Inequality:** $\frac{a+b}{2} \ge \sqrt{ab}$ (Sum minimal when terms equal).

2. **Cauchy-Schwarz Inequality:** $(\sum a_i b_i)^2 \le (\sum a_i^2)(\sum b_i^2)$.

3. **Triangle Inequality:** $\vert{}a + b\vert{} \le \vert{}a\vert{} + \vert{}b\vert{}$.

### 2. Recognition Triggers

* "Is it possible to achieve sum $S$ using $N$ elements bounded by $[L, R]$?"

### 3. Complexity Target

* $O(1)$ feasibility check.

### 4. Dry-Run Walkthrough

Can $N=3$ elements bounded by $[2, 5]$ sum to $S = 17$?

1. Minimum possible sum $= N \times L = 3 \times 2 = 6$.

2. Maximum possible sum $= N \times R = 3 \times 5 = 15$.

3. Target $S = 17 > \text{Max Sum } (15)$. Output: `NO`.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1538A / Codeforces 1354B\]

### 6. Pseudocode

```
function isPossibleSum(N, L, R, TargetSum):
    min_sum = N * L
    max_sum = N * R
    return min_sum <= TargetSum and TargetSum <= max_sum

```

### 7. Common Edge Cases & Traps

* Strict vs non-strict inequalities ($<$ vs $\le$).

## Form 28: Extremal Principle

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Extremal Principle problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Tallest person**

```text
REAL WORLD
    |
    v
inspect maximum/minimum first
    |
    v
max height cannot have a taller neighbor
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `inspect maximum/minimum first`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **An extreme element often removes one side of an inequality.**


### 1. Concept & Mathematical Foundation

Consider the **extremal elements** (the smallest, largest, leftmost, or rightmost elements) to force simplifications or find structural bottlenecks.

Why it works:
At the maximum element $A_{\max}$, conditions like $A[i] \le A_{\max}$ force upper bounds, simplifying inequalities.

### 2. Recognition Triggers

* "Choose elements satisfying local conditions across neighbors."

### 3. Complexity Target

* $O(N)$ pass to locate min/max.

### 4. Dry-Run Walkthrough

In a tournament graph where every pair has a directed edge, prove there exists a vertex that can reach all others in $\le 2$ steps.

1. Pick vertex $V$ with **maximum out-degree**.

2. All vertices not directly connected to $V$ must be reachable through a 2-step path via $V$'s neighbors.

3. Extremal choice ($V$ with max out-degree) guarantees the property.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1618C - Paint the Array](https://codeforces.com/problemset/problem/1618/C?utm_source=gemini)

### 6. Pseudocode

```
function solveExtremal(A):
    max_elem = max(A)
    min_elem = min(A)
    # Evaluate problem constraints specifically at max_elem and min_elem
    # ...

```

### 7. Common Edge Cases & Traps

* Multiple identical max/min elements: Ensure tie-breaking logic does not cause infinite loops.

## Form 29: Coordinate Transformation

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Coordinate Transformation problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — City-block navigation**

```text
REAL WORLD
    |
    v
u=x+y, v=x-y
    |
    v
(1,2)->(3,-1)
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `u=x+y, v=x-y`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Change coordinates until awkward geometry becomes simple max/min arithmetic.**


### 1. Concept & Mathematical Foundation

Rotating or mapping coordinates to simplify distance/range queries.

Key Transformations:

1. **Manhattan to Chebyshev Distance:**
   

   $$
   (x, y) \to (x + y, x - y)
   $$

   
   Manhattan Distance $\vert{}x_1 - x_2\vert{} + \vert{}y_1 - y_2\vert{}$ becomes Chebyshev Distance $\max(\vert{}x'_1 - x'_2\vert{}, \vert{}y'_1 - y'_2\vert{})$.

2. **1D Flattening:** Grid $(r, c)$ in $M \times N \implies \text{index} = r \cdot N + c$.

### 2. Recognition Triggers

* "Find maximum Manhattan distance between any pair of points."

### 3. Complexity Target

* $O(N)$ after transformation (from $O(N^2)$ pairwise checks).

### 4. Dry-Run Walkthrough

Max Manhattan distance between points $(1,2)$ and $(4,0)$.

1. Transform points $(x+y, x-y)$:

   * $P_1 = (1+2, 1-2) = (3, -1)$

   * $P_2 = (4+0, 4-0) = (4, 4)$

2. Chebyshev distance $= \max(\vert{}3-4\vert{}, \vert{}-1-4\vert{}) = \max(1, 5) = 5$.

3. Direct Manhattan $= \vert{}1-4\vert{} + \vert{}2-0\vert{} = 3 + 2 = 5$. Matches!

### 5. Classic Example Problem

* **Problem:** [LeetCode 1131 - Maximum Absolute Value Expression](https://leetcode.com/problems/maximum-absolute-value-expression/?utm_source=gemini)

### 6. Pseudocode

```
function maxManhattanDistance(points):
    # Transform coordinates
    X_prime = []
    Y_prime = []
    for (x, y) in points:
        X_prime.append(x + y)
        Y_prime.append(x - y)
        
    ans = max(max(X_prime) - min(X_prime), max(Y_prime) - min(Y_prime))
    return ans

```

### 7. Common Edge Cases & Traps

* Floating point transformations under rotation ($45^\circ$) can introduce rounding errors; stick to integer addition/subtraction transformations.

## Form 30: Bit Independence

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Bit Independence problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Rows of light switches**

```text
REAL WORLD
    |
    v
solve each bit position separately
    |
    v
010,011,101: inspect columns
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `solve each bit position separately`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **AND/OR/XOR have no carry, so each bit can be counted independently.**


### 1. Concept & Mathematical Foundation

Bitwise operations (`AND`, `OR`, `XOR`) operate **independently on each bit position** (0 through 30).

Strategy:
Break a bitwise problem into 30 independent single-bit problems. Solve for bit $k$, then aggregate results:

$$
\text{Total} = \sum_{k=0}^{30} 2^k \times (\text{ans for bit } k)
$$

### 2. Recognition Triggers

* "Calculate sum of $A[i] \oplus A[j]$ over all pairs."

* Bitwise array operations without carry-over addition.

### 3. Complexity Target

* $O(30 \cdot N)$ instead of $O(N^2)$.

### 4. Dry-Run Walkthrough

Sum of pairwise XOR for `A = [2, 3, 5]` (`2` = `010`, `3` = `011`, `5` = `101`).

1. **Bit 0:** values are `[0, 1, 1]` ($1$ count $= 2$, $0$ count $= 1$).

   * Pairs with different bit 0: $2 \times 1 = 2$. Contribution $= 2 \times 2^0 = 2$.

2. **Bit 1:** values are `[1, 1, 0]` ($1$ count $= 2$, $0$ count $= 1$).

   * Pairs with different bit 1: $2 \times 1 = 2$. Contribution $= 2 \times 2^1 = 4$.

3. **Bit 2:** values are `[0, 0, 1]` ($1$ count $= 1$, $0$ count $= 2$).

   * Pairs with different bit 2: $1 \times 2 = 2$. Contribution $= 2 \times 2^2 = 8$.

4. Total Sum $= 2 + 4 + 8 = 14$.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1362C / LeetCode 1835\] / \[Codeforces 468A\]

### 6. Pseudocode

```
function sumXorPairs(A):
    N = length(A)
    total_sum = 0
    
    for bit from 0 to 30:
        count_ones = 0
        for x in A:
            if (x & (1 << bit)) != 0:
                count_ones += 1
        count_zeros = N - count_ones
        
        # Pairs with differing bit position contribute to XOR
        total_sum += (count_ones * count_zeros) * (1 << bit)
        
    return total_sum

```

### 7. Common Edge Cases & Traps

* **Signed Shift:** In C++, shifting `1 << 31` causes undefined behavior with signed 32-bit integers. Use `1LL << bit`.

## Form 31: Prime Factor Independence

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Prime Factor Independence problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Ingredient prime powers**

```text
REAL WORLD
    |
    v
number = product of independent prime powers
    |
    v
12=2^2*3
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `number = product of independent prime powers`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **For divisibility/GCD/LCM, reason independently about each prime exponent.**


### 1. Concept & Mathematical Foundation

By the Fundamental Theorem of Arithmetic, any integer $N > 1$ decomposes uniquely into prime powers:

$$
N = p_1^{e_1} \cdot p_2^{e_2} \cdots p_k^{e_k}
$$


Divisibility and multiplicative functions break down independently for each prime factor $p_i$.

### 2. Recognition Triggers

* "Count divisors or construct numbers with specific GCD/LCM properties."

### 3. Complexity Target

* Factorization: $O(\sqrt{N})$ or $O(\log N)$ using SPF (Smallest Prime Factor Sieve).

### 4. Dry-Run Walkthrough

Find number of divisors of $N = 12$.

1. Factorize $12 = 2^2 \times 3^1$.

2. Number of divisors $= (e_1 + 1)(e_2 + 1) = (2 + 1)(1 + 1) = 3 \times 2 = 6$.

3. Divisors: $\{1, 2, 3, 4, 6, 12\}$.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1499D - Number of Pairs](https://codeforces.com/problemset/problem/1499/D?utm_source=gemini)

### 6. Pseudocode

```
function countDivisors(N):
    count = 0
    for d = 1 to sqrt(N):
        if N % d == 0:
            count += 1
            if d * d != N:
                count += 1
    return count

```

### 7. Common Edge Cases & Traps

* **Perfect Squares:** Avoid double-counting $d$ when $d = N/d$.

## Form 32: Frequency Compression

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Frequency Compression problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Inventory counts**

```text
REAL WORLD
    |
    v
replace repeated items by (value,count)
    |
    v
5,5,5,2,2 -> (5,3),(2,2)
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `replace repeated items by (value,count)`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **When identity repeats, process counts instead of individual copies.**


### 1. Concept & Mathematical Foundation

When array length $N$ is large ($10^5$) but distinct elements are few or elements can be grouped by frequency, compress the array into `(element, frequency)` pairs.

Properties:

* Sum of distinct frequencies $\le \sqrt{2N}$.

* Many repetitive operations simplify from $O(N)$ to $O(\text{Unique Elements})$.

### 2. Recognition Triggers

* "Array contains elements with high repetition."

### 3. Complexity Target

* Reduces time complexity from $O(N^2)$ to $O(U^2)$ or $O(N \sqrt{N})$ where $U$ is unique elements.

### 4. Dry-Run Walkthrough

`A = [5, 5, 5, 2, 2, 9]`

1. Compress to Frequency Map: `{5: 3, 2: 2, 9: 1}`

2. Process unique entries: $(5, 3), (2, 2), (9, 1)$.

3. Reduces operations from $6$ elements down to $3$ unique states.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1029B / LeetCode 1838\]

### 6. Pseudocode

```
function processFrequencyCompressed(A):
    freq = map()
    for x in A: freq[x] = freq.get(x, 0) + 1
    
    unique_pairs = []
    for (val, count) in freq.items():
        unique_pairs.append((val, count))
        
    # Process unique_pairs...

```

### 7. Common Edge Cases & Traps

* Forgetting to multiply total costs by the element's frequency `count`.

## Form 33: Permutation Mathematics

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Permutation Mathematics problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — People changing seats**

```text
REAL WORLD
    |
    v
permutation decomposes into cycles
    |
    v
1->3->2->1
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `permutation decomposes into cycles`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Cycles are the natural independent components of a permutation.**


### 1. Concept & Mathematical Foundation

A Permutation of size $N$ contains all integers from $1$ to $N$ exactly once.

Properties:

1. **Cycle Decomposition:** Every permutation decomposes into disjoint functional cycles.

2. **Inversions:** Pair $(i, j)$ where $i < j$ and $P[i] > P[j]$. Swapping adjacent elements changes inversion count by $\pm 1$.

3. **Parity:** Permutation parity is defined by $(\text{N} - \text{number of cycles}) \bmod 2$.

### 2. Recognition Triggers

* "Find minimum swaps to sort an array."

* "Functional graph jumps $P[P[i]]$."

### 3. Complexity Target

* Cycle Detection: $O(N)$.

* Inversion Counting: $O(N \log N)$ via Merge Sort / Fenwick Tree.

### 4. Dry-Run Walkthrough

Find minimum swaps to sort `P = [3, 1, 2]`.

1. Graph edges $i \to P[i]$: $1 \to 3 \to 2 \to 1$.

2. Cycle length $L = 3$.

3. Minimum swaps for cycle of length $L = L - 1 = 3 - 1 = 2$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 765 - Couples Holding Hands](https://leetcode.com/problems/couples-holding-hands/?utm_source=gemini)

### 6. Pseudocode

```
function minSwapsToSort(P):
    N = length(P)
    visited = array of size N initialized to False
    swaps = 0
    
    for i from 0 to N - 1:
        if visited[i] or P[i] == i:
            continue
            
        cycle_size = 0
        curr = i
        while not visited[curr]:
            visited[curr] = True
            curr = P[curr]
            cycle_size += 1
            
        if cycle_size > 0:
            swaps += (cycle_size - 1)
            
    return swaps

```

### 7. Common Edge Cases & Traps

* **1-based vs 0-based indexing:** Ensure permutation values match array indices properly during cycle traversal.

## Form 34: Mex Mathematics

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Mex Mathematics problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Missing ticket number**

```text
REAL WORLD
    |
    v
first nonnegative absent value
    |
    v
{0,1,3,4} -> MEX=2
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `first nonnegative absent value`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **To get MEX k, every 0..k-1 must exist and k must be absent.**


### 1. Concept & Mathematical Foundation

**MEX (Minimum Excluded Value):** The smallest non-negative integer not present in a set/array.

Properties:

1. $\text{MEX}(A) \le N$.

2. To have $\text{MEX}(A) = K$, the set **must** contain all integers from $0$ to $K-1$.

3. Insertion of an element $> \text{MEX}(A)$ does not change $\text{MEX}$.

### 2. Recognition Triggers

* "Find MEX of subsegments."

* "Games involving Sprague-Grundy theorem (Nim)."

### 3. Complexity Target

* Overall MEX tracking: $O(N)$ with set/frequency tracking or Segment Tree.

### 4. Dry-Run Walkthrough

`A = [0, 1, 3, 4]`

1. Contains $0$: Yes.

2. Contains $1$: Yes.

3. Contains $2$: No.

4. $\text{MEX}(A) = 2$.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1566D / Codeforces 1618D\] / \[Codeforces 1436C\]

* **Representative Problem:** \[Codeforces 1527A - Game with Cards / MEX problems\]

### 6. Pseudocode

```
function findMEX(A):
    seen = set(A)
    mex = 0
    while mex in seen:
        mex += 1
    return mex

```

### 7. Common Edge Cases & Traps

* **MEX = 0:** Occurs when $0$ is absent from the array.

## Form 35: Interval Mathematics

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Interval Mathematics problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Meeting schedules**

```text
REAL WORLD
    |
    v
overlap iff max(L1,L2)<=min(R1,R2)
    |
    v
[1,4] & [3,6] -> [3,4]
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `overlap iff max(L1,L2)<=min(R1,R2)`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Intervals reduce to endpoints: max of starts, min of ends.**


### 1. Concept & Mathematical Foundation

Operations over continuous ranges $[L_i, R_i]$.

Key Algorithms:

1. **Interval Overlap Check:** Intervals $[A, B]$ and $[C, D]$ overlap $\iff \max(A, C) \le \min(B, D)$.

2. **Merge Intervals:** Sort by start time $L_i$, maintain running maximum end time.

3. **Difference Array (Prefix Sweep):** Add $+1$ at $L_i$ and $-1$ at $R_i + 1$ to compute overlap counts at any point in $O(N + \text{MAX})$.

### 2. Recognition Triggers

* "Merge overlapping intervals."

* "Find maximum overlapping intervals at any point in time."

### 3. Complexity Target

* Sorting + Merge: $O(N \log N)$.

* Sweep-Line: $O(N \log N)$.

### 4. Dry-Run Walkthrough

Merge `[[1, 3], [2, 6], [8, 10]]`.

1. Sort by start: `[1, 3], [2, 6], [8, 10]`.

2. Merge `[1, 3]` and `[2, 6]` since $2 \le 3 \implies$ merged range `[1, max(3,6)] = [1, 6]`.

3. Compare `[1, 6]` and `[8, 10]`: $8 > 6 \implies$ no overlap.

4. Result: `[[1, 6], [8, 10]]`.

### 5. Classic Example Problem

* **Problem:** [LeetCode 56 - Merge Intervals](https://leetcode.com/problems/merge-intervals/?utm_source=gemini)

### 6. Pseudocode

```
function mergeIntervals(intervals):
    sort(intervals by start_time)
    merged = []
    
    for interval in intervals:
        if not merged or merged[-1].end < interval.start:
            merged.append(interval)
        else:
            merged[-1].end = max(merged[-1].end, interval.end)
            
    return merged

```

### 7. Common Edge Cases & Traps

* **Touching Intervals:** Decide whether $[1, 2]$ and $[2, 3]$ overlap based on problem statement ($R_1 \ge L_2$ vs $R_1 > L_2$).

## Form 36: Grid Parity

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Grid Parity problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Chessboard coloring**

```text
REAL WORLD
    |
    v
color=(r+c) mod 2
    |
    v
(0,0) even; one orthogonal move -> odd
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `color=(r+c) mod 2`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Orthogonal movement flips checkerboard parity every step.**


### 1. Concept & Mathematical Foundation

Grids forms a **Bipartite Graph** where cell $(r, c)$ has color/parity $(r + c) \bmod 2$.

Properties:

1. Every valid orthogonal step (up/down/left/right) strictly changes cell parity:
   

   $$
   (r, c) \to (r \pm 1, c) \implies (r + c \pm 1) \bmod 2
   $$

2. Knight moves change parity at every step.

### 2. Recognition Triggers

* "Movement on a chessboard/grid."

* "Coloring grid cells such that no adjacent cells share properties."

### 3. Complexity Target

* $O(R \cdot C)$ grid traversal / parity assignment.

### 4. Dry-Run Walkthrough

Can a path starting at $(0,0)$ reach $(2,2)$ in an odd number of steps?

1. Start parity: $(0 + 0) \bmod 2 = 0$.

2. End parity: $(2 + 2) \bmod 2 = 0$.

3. Reaching same parity cell requires an **even** number of steps.

4. An odd number of steps is strictly **impossible**.

### 5. Classic Example Problem

* **Problem:** \[Codeforces 1593B / Codeforces 1364A\]

### 6. Pseudocode

```
function isPathPossibleByParity(r1, c1, r2, c2, steps):
    dist = abs(r1 - r2) + abs(c1 - c2)
    if steps < dist:
        return False
    return (steps % 2) == (dist % 2)

```

### 7. Common Edge Cases & Traps

* **Diagonal Moves:** Diagonal jumps $(r \pm 1, c \pm 1)$ preserve parity! Parity analysis differs if diagonal moves are allowed.

## Form 37: Cyclic / Modulo Process

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Cyclic / Modulo Process problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Days of week**

```text
REAL WORLD
    |
    v
position=(start+steps) mod N
    |
    v
10 steps on 3 states -> 1
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `position=(start+steps) mod N`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **When states wrap, reduce huge step counts modulo the cycle length.**


### 1. Concept & Mathematical Foundation

Simulating processes that wrap around circularly ($i \to (i + 1) \bmod N$).

Key Concepts:

1. **Cycle Detection (Floyd's Tortoise & Hare):** Detect repeating states in functional graphs $f(x)$ within $O(\mu + \lambda)$ steps.

2. **Josephus Problem:** $J(n, k) = (J(n-1, k) + k) \bmod n$.

### 2. Recognition Triggers

* "Array wraps around endlessly."

* "Repeatedly apply state transformation $K$ times where $K \le 10^{18}$."

### 3. Complexity Target

* Cycle finding / Matrix Exponentiation: $O(N)$ or $O(\text{State} \cdot \log K)$.

### 4. Dry-Run Walkthrough

Cycle through `[A, B, C]` $K = 10$ times starting at index $0$.

1. Formula: $\text{Index} = K \bmod N = 10 \bmod 3 = 1$.

2. Value $= A[1] = \text{'B'}$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 1823 - Find the Winner of the Circular Game](https://leetcode.com/problems/find-the-winner-of-the-circular-game/?utm_source=gemini)

### 6. Pseudocode

```
function findTheWinner(n, k):
    winner = 0 # Base case J(1, k)
    for i from 2 to n:
        winner = (winner + k) % i
    return winner + 1 # Convert to 1-based index

```

### 7. Common Edge Cases & Traps

* **1-based Modular Arithmetic:** Convert to 0-based, apply `% N`, then convert back: `(idx - 1) % N + 1`.

## Form 38: Binary Search Equation

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Binary Search Equation problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Machine capacity**

```text
REAL WORLD
    |
    v
predicate can(capacity) is monotone
    |
    v
5 fails,6 works,7 works...
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `predicate can(capacity) is monotone`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Binary search the answer only after proving false...false,true...true monotonicity.**


### 1. Concept & Mathematical Foundation

Optimizing a function $f(X)$ over a monotonic predicate function $P(X) \in \{\text{True, False}\}$.

Monotonicity Principle:
If $P(X)$ is True for all $X \ge \text{target}$ and False for $X < \text{target}$, Binary Search guarantees finding $\text{target}$ in $O(\log(\text{RANGE}))$ steps.

### 2. Recognition Triggers

* "Minimize the maximum value..."

* "Maximize the minimum value..."

### 3. Complexity Target

* $O(N \log(\text{RANGE}))$.

### 4. Dry-Run Walkthrough

Find min capacity $C$ to transport weights `[1, 2, 3, 4, 5]` in $D = 3$ days.

1. Search space $C \in [5, 15]$.

2. Mid $C = 10$: Days needed $= 2$ ($\le 3 \implies \text{True}$). Search left: $C \in [5, 9]$.

3. Mid $C = 7$: Days needed $= 3$ ($\le 3 \implies \text{True}$). Search left: $C \in [5, 6]$.

4. Mid $C = 5$: Days needed $= 4$ ($> 3 \implies \text{False}$). Search right: $C \in [6, 6]$.

5. Optimal $C = 6$.

### 5. Classic Example Problem

* **Problem:** [LeetCode 1011 - Capacity To Ship Packages Within D Days](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/?utm_source=gemini)

### 6. Pseudocode

```
function shipWithinDays(weights, days):
    low = max(weights)
    high = sum(weights)
    ans = high
    
    while low <= high:
        mid = low + (high - low) // 2
        if canShip(weights, days, mid):
            ans = mid
            high = mid - 1 # Try smaller capacity
        else:
            low = mid + 1  # Capacity too small
            
    return ans

```

### 7. Common Edge Cases & Traps

* **Infinite Loops:** Incorrect mid calculation `(low + high) / 2` when dealing with floating points or boundary conditions.

## Form 39: Stars and Bars

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Stars and Bars problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Distribute candies**

```text
REAL WORLD
    |
    v
x1+...+xk=N, xi>=0
    |
    v
5 candies,3 kids -> C(7,2)
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `x1+...+xk=N, xi>=0`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Convert distribution into N stars separated by K-1 bars.**


### 1. Concept & Mathematical Foundation

Combinatorial method to count distributions of $N$ indistinguishable items into $K$ distinguishable bins.

Formulas:

1. **Non-negative integers (**$X_i \ge 0$**):**
   

   $$
   \binom{N + K - 1}{K - 1}
   $$

2. **Positive integers (**$X_i \ge 1$**):**
   

   $$
   \binom{N - 1}{K - 1}
   $$

### 2. Recognition Triggers

* "Count ways to choose $K$ integers non-negative integers that sum to $N$."

### 3. Complexity Target

* $O(K)$ or $O(1)$ after precomputing factorials.

### 4. Dry-Run Walkthrough

Count ways to distribute $N=5$ candies to $K=3$ kids (kids can get 0).

1. Formula: $\binom{5 + 3 - 1}{3 - 1} = \binom{7}{2}$.

2. Calculation: $\frac{7 \times 6}{2 \times 1} = 21$ ways.

### 5. Classic Example Problem

* **Problem:** [Codeforces 1288C - Two Arrays](https://codeforces.com/problemset/problem/1288/C?utm_source=gemini)

### 6. Pseudocode

```
function starsAndBars(N, K):
    # Calculate (N + K - 1) choose (K - 1)
    return nCr(N + K - 1, K - 1)

```

### 7. Common Edge Cases & Traps

* **Constraints on Max Bin Capacity:** Standard Stars and Bars assumes infinite bin capacity. If $X_i \le M$, apply Inclusion-Exclusion alongside Stars and Bars.

## Form 40: Diophantine Equation

### 0. Fast Intuition Layer — Story → Math → Pattern

**What this form means.** Diophantine Equation problems become easier when the story is reduced to a small mathematical relationship and the irrelevant narrative is discarded.

**Real-world scenario — Pay exact amount with fixed coin sizes**

```text
REAL WORLD
    |
    v
Ax+By=C
    |
    v
4x+6y=7 impossible because gcd=2 does not divide 7
```

**Contest modeling diagram**

```text
Problem statement
      |
      v
Remove story nouns
      |
      v
Write variables / state
      |
      v
Write the mathematical condition
      |
      v
Transform / normalize it
      |
      v
Match a data structure or algorithm
```

**Mini dry run.** Start from `Ax+By=C`. Substitute the tiny values shown above, simplify one operation at a time, and identify the quantity that is unknown or repeated. That quantity/state is what the algorithm should query, count, optimize, or construct.

**Contest memory rule:** **Integer linear equations start with the gcd divisibility test.**


### 1. Concept & Mathematical Foundation

Linear Diophantine Equation:

$$
A \cdot x + B \cdot y = C
$$

Solvability Condition:
A integer solution $(x, y)$ exists if and only if $\gcd(A, B)$ **divides** $C$.

Extended Euclidean Algorithm:
Finds a base solution $(x_0, y_0)$ to $A x + B y = \gcd(A, B)$. General solutions:

$$
x = x_0 \cdot \frac{C}{g} + k \cdot \frac{B}{g}
$$

$$
y = y_0 \cdot \frac{C}{g} - k \cdot \frac{A}{g}
$$


where $g = \gcd(A, B)$ and $k \in \mathbb{Z}$.

### 2. Recognition Triggers

* "Find integer combinations of step sizes $A$ and $B$ to hit position $C$."

### 3. Complexity Target

* $O(\log(\min(A, B)))$ via Extended GCD.

### 4. Dry-Run Walkthrough

Can we pay an exact amount $C = 7$ using coins of size $A = 4$ and $B = 6$?

1. Compute $g = \gcd(4, 6) = 2$.

2. Check divisibility: Does $2 \mid 7$? No.

3. Impossible to form total of $7$. Output: `NO`.

### 5. Classic Example Problem

* **Problem:** [Codeforces 633A - Ebony and Ivory](https://codeforces.com/problemset/problem/633/A?utm_source=gemini)

### 6. Pseudocode

```
function extGCD(a, b):
    if b == 0:
        return a, 1, 0
    g, x1, y1 = extGCD(b, a % b)
    x = y1
    y = x1 - (a // b) * y1
    return g, x, y

function hasNonNegativeSolution(A, B, C):
    g, x0, y0 = extGCD(A, B)
    if C % g != 0:
        return False
        
    # Scale base solution
    x0 *= (C // g)
    y0 *= (C // g)
    
    # Check if there exists integer k making both x >= 0 and y >= 0
    # x = x0 + k * (B / g) >= 0 => k >= -x0 * g / B
    # y = y0 - k * (A / g) >= 0 => k <= y0 * g / A
    
    k_min = ceil(-x0 * g / B)
    k_max = floor(y0 * g / A)
    
    return k_min <= k_max

```

### 7. Common Edge Cases & Traps

* **Negative Integer Solutions:** Standard Extended GCD returns signed values $(x_0, y_0)$. Check if positive solutions are required and shift $k$ accordingly.