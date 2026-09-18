# Pattern 1 — Basic Range Sum / Static Queries

## Core Concept
For static array range sum queries where the array elements do not change, recalculating sums sequentially takes $\mathcal{O}(N)$ per query, leading to an overall complexity of $\mathcal{O}(Q \times N)$ for $Q$ queries. 

By constructing a **Prefix Sum Array** $P$ of length $N+1$ where $P[i] = \sum_{j=0}^{i-1} A[j]$ (with $P[0] = 0$), any range sum query for range $[L, R]$ (0-indexed inclusive) can be answered in $\mathcal{O}(1)$ time using the relation:

$$\text{RangeSum}(L, R) = P[R + 1] - P[L]$$

Preprocessing time: $\mathcal{O}(N)$  
Query time: $\mathcal{O}(1)$  
Space complexity: $\mathcal{O}(N)$

---

## Standard Pseudocode

### 1D Range Sum Query Precomputation & Query
```text
function BUILD-PREFIX-SUM(A, N):
    P = array of size N + 1 filled with 0
    for i from 0 to N - 1:
        P[i + 1] = P[i] + A[i]
    return P

function QUERY-RANGE-SUM(P, L, R):
    // Returns sum of A[L..R] inclusive
    return P[R + 1] - P[L]
```

### 2D Range Sum Query (Matrix) Precomputation & Query
```text
function BUILD-2D-PREFIX-SUM(M, R, C):
    P = 2D array of size (R + 1) x (C + 1) filled with 0
    for r from 0 to R - 1:
        for c from 0 to C - 1:
            P[r + 1][c + 1] = M[r][c] + P[r][c + 1] + P[r + 1][c] - P[r][c]
    return P

function QUERY-2D-RANGE-SUM(P, r1, c1, r2, c2):
    // Returns sum of submatrix from top-left (r1, c1) to bottom-right (r2, c2)
    return P[r2 + 1][c2 + 1] - P[r1][c2 + 1] - P[r2 + 1][c1] + P[r1][c1]
```

---

Problem Link: [LeetCode 303 - Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)

**Problem Summary:** Given an integer array `nums`, handle multiple queries asking for the sum of elements between indices `left` and `right` inclusive. Return the range sum for each query.

---

### Range Sum Query - Immutable (Prefix Sum / LeetCode / Easy)

* **Core Invariant / Key Insight:** Construct a 1-indexed prefix sum array $P$ where $P[i]$ holds the sum of elements up to index $i-1$. The sum of subarray $[L, R]$ is computed in $\mathcal{O}(1)$ via $P[R+1] - P[L]$.

* **Step-by-Step Logic:**
1. Allocate a prefix array `P` of size `N + 1` initialized with `P[0] = 0`.
2. Compute `P[i + 1] = P[i] + nums[i]` for all $0 \le i < N$.
3. For each query $(L, R)$, calculate and return `P[R + 1] - P[L]`.

* **Pseudocode:**
```text
function INIT(nums):
    P = array of size length(nums) + 1
    P[0] = 0
    for i from 0 to length(nums) - 1:
        P[i + 1] = P[i] + nums[i]

function SUMRANGE(left, right):
    return P[right + 1] - P[left]
```

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        nums = [-2, 0, 3, -5, 2, -1]

Step 1 (Build): P = [0, -2, -2, 1, -4, -2, -3]

Step 2 (Q: 0,2): P[3] - P[0]  --> 1 - 0 = 1
Step 3 (Q: 2,5): P[6] - P[2]  --> -3 - (-2) = -1
Step 4 (Q: 0,5): P[6] - P[0]  --> -3 - 0 = -3

Final Answer:   [1, -1, -3]
Final State:    P = [0, -2, -2, 1, -4, -2, -3]
```

---

Problem Link: [LeetCode 304 - Range Sum Query 2D - Immutable](https://leetcode.com/problems/range-sum-query-2d-immutable/)

**Problem Summary:** Given a 2D matrix `matrix`, answer multiple submatrix sum queries defined by top-left coordinate `(row1, col1)` and bottom-right coordinate `(row2, col2)`.

---

### Range Sum Query 2D - Immutable (2D Prefix Sum / LeetCode / Medium)

* **Core Invariant / Key Insight:** Extend prefix sums to 2D using Inclusion-Exclusion: $P[r+1][c+1] = M[r][c] + P[r][c+1] + P[r+1][c] - P[r][c]$. Submatrix sum is $P[r2+1][c2+1] - P[r1][c2+1] - P[r2+1][c1] + P[r1][c1]$.

* **Step-by-Step Logic:**
1. Construct 1-indexed 2D array `P` of size `(R+1) x (C+1)`.
2. Fill `P` by accumulating top and left regions while subtracting the doubly-counted top-left overlap.
3. Compute submatrix query using Inclusion-Exclusion formula in $\mathcal{O}(1)$ time.

* **Pseudocode:**
```text
function INIT(matrix):
    R = rows(matrix), C = cols(matrix)
    P = 2D array (R + 1) x (C + 1) filled with 0
    for r from 0 to R - 1:
        for c from 0 to C - 1:
            P[r+1][c+1] = matrix[r][c] + P[r][c+1] + P[r+1][c] - P[r][c]

function SUMREGION(r1, c1, r2, c2):
    return P[r2+1][c2+1] - P[r1][c2+1] - P[r2+1][c1] + P[r1][c1]
```

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial Matrix:
3  0  1
5  6  3

Step 1 (Build Prefix Matrix P):
0  0  0  0
0  3  3  4
0  8 14 18

Step 2 (Query Region (0,1) to (1,2)):
Area = P[2][3] - P[0][3] - P[2][1] + P[0][1]
     = 18 - 0 - 8 + 0 = 10

Final Answer:   10
Final State:    P constructed, queries evaluated in O(1)
```

---

Problem Link: [Codeforces 433B - Kuriyama Mirai's Stones](https://codeforces.com/problemset/problem/433/B)

**Problem Summary:** Answer range sum queries on an array $v$. Type 1 queries ask for range sum on the original array, while Type 2 queries ask for range sum on the array sorted in non-decreasing order.

---

### Kuriyama Mirai's Stones (Prefix Sum & Sorting / Codeforces / 1100)

* **Core Invariant / Key Insight:** Maintain two separate prefix sum arrays: one for the original sequence and one for the sorted sequence to answer both query types in $\mathcal{O}(1)$ time.

* **Step-by-Step Logic:**
1. Compute prefix sum array `P1` for original array `v`.
2. Create array `u` by sorting `v` in non-decreasing order.
3. Compute prefix sum array `P2` for sorted array `u`.
4. Answer Type 1 using `P1[R] - P1[L-1]` and Type 2 using `P2[R] - P2[L-1]`.

* **Pseudocode:**
```text
function SOLVE(N, v, Q, queries):
    P1 = BUILD-PREFIX-SUM(v, N)
    u = SORT(v)
    P2 = BUILD-PREFIX-SUM(u, N)
    
    for each (type, l, r) in queries:
        if type == 1:
            print P1[r] - P1[l - 1]
        else:
            print P2[r] - P2[l - 1]
```

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial Array v: [6, 4, 2, 7, 2]
Sorted Array u:  [2, 2, 4, 6, 7]

Step 1 (P1 Build): P1 = [0, 6, 10, 12, 19, 21]
Step 2 (P2 Build): P2 = [0, 2,  4,  8, 14, 21]

Step 3 (Q Type 1, range [2, 4]): P1[4] - P1[1] --> 19 - 6 = 13
Step 4 (Q Type 2, range [2, 4]): P2[4] - P2[1] --> 14 - 2 = 12

Final Answer:   13, 12
Final State:    Prefix arrays ready for static O(1) retrieval
```

---

Problem Link: [Codeforces 313B - Ilya and Queries](https://codeforces.com/problemset/problem/313/B)

**Problem Summary:** Given a string $s$, answer $m$ queries $(l, r)$ requesting the count of indices $i$ such that $l \le i < r$ and $s[i] == s[i+1]$.

---

### Ilya and Queries (Transformed Prefix Sum / Codeforces / 1100)

* **Core Invariant / Key Insight:** Convert adjacent character comparisons into a binary indicator array $A$ where $A[i] = 1$ if $s[i] == s[i+1]$ else $0$, then query range sums over $A$.

* **Step-by-Step Logic:**
1. Construct binary array `A` of size `N-1` where `A[i] = (s[i] == s[i+1] ? 1 : 0)`.
2. Compute prefix sum array `P` over `A`.
3. For query `(l, r)`, answer using range sum `P[r - 1] - P[l - 1]` (using 1-based indexing).

* **Pseudocode:**
```text
function SOLVE(s, m, queries):
    N = length(s)
    A = array of size N
    for i from 1 to N - 1:
        A[i] = (s[i] == s[i - 1] ? 1 : 0)
        
    P = BUILD-PREFIX-SUM(A, N)
    
    for each (l, r) in queries:
        // sum from index l to r-1
        print P[r - 1] - P[l - 1]
```

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial String: s = "# . . # # . ."  (1-indexed length 7)

Step 1 (Matches A): [0, 0, 1, 0, 1, 0, 1]  (1 if s[i] == s[i-1])
Step 2 (Prefix P):  [0, 0, 1, 1, 2, 2, 3]

Step 3 (Q: l=1, r=3): P[3] - P[1]  --> 1 - 0 = 1  (".")
Step 4 (Q: l=2, r=5): P[5] - P[2]  --> 2 - 0 = 2  ("..", "##")

Final Answer:   1, 2
Final State:    P = [0, 0, 1, 1, 2, 2, 3]
```