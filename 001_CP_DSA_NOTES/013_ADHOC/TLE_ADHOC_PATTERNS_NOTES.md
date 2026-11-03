# Ad-hoc & Patterns — Level 2

> Pattern-wise Competitive Programming revision notes.  
> Format: **Pattern Overview → Recognition → Problem Summary → Core Invariant → Step-by-Step Logic → ASCII Dry Run → Pseudocode → C++**.  
> Formula style: renderer-safe inline code such as `O(N log N)`, `n % 4`, and `2^(x - 1)`.

---

## Table of Contents

- [Pattern Overview — Ad-hoc & Patterns](#pattern-overview--ad-hoc--patterns)
- [Modulo Summation (Modulo Extremum / AtCoder / ABC103 C)](#modulo-summation-modulo-extremum--atcoder--abc103-c)
- [Rectangle Filling (Boundary Invariant / Codeforces / Div. 2 B)](#rectangle-filling-boundary-invariant--codeforces--div-2-b)
- [Missing Coin Sum (Greedy Reachable Range / CSES / Sorting + Greedy)](#missing-coin-sum-greedy-reachable-range--cses--sorting-greedy)
- [Multiple Powers of Two (Bit Pattern + Query Dominance / HackerRank / Bit Manipulation)](#multiple-powers-of-two-bit-pattern-query-dominance--hackerrank--bit-manipulation)
- [Stick Lengths (Median / CSES / Greedy-Math)](#stick-lengths-median--cses--greedy-math)
- [Odd Grasshopper (Periodicity / Codeforces / 900)](#odd-grasshopper-periodicity--codeforces--900)
- [Final Pattern Recognition Sheet](#final-pattern-recognition-sheet)

---

# Pattern Overview — Ad-hoc & Patterns

Ad-hoc problems usually do **not** require a heavy data structure or a standard algorithm such as Dijkstra or DP. The main challenge is finding a small mathematical observation, invariant, boundary condition, greedy rule, bit property, or repeating pattern that collapses the brute-force solution.

### Main Recognition Flow

```text
Read the statement
      |
      v
What would brute force simulate / enumerate?
      |
      v
Look for a property that does NOT require simulation
      |
      +--> Mathematical upper/lower bound?
      |
      +--> Boundary / corner invariant?
      |
      +--> Continuous reachable interval?
      |
      +--> Binary / divisibility pattern?
      |
      +--> Median / absolute-distance property?
      |
      +--> Periodicity / modulo cycle?
      |
      v
Turn the observation into O(N), O(N log N), or O(1)
```

### Forms Covered in This Note

| Form | Recognition Signal | Representative Problem |
|---|---|---|
| Modulo Extremum | Maximize a sum of remainders | Modulo Summation |
| Boundary / Corner Invariant | Grid operation looks expensive to simulate | Rectangle Filling |
| Greedy Reachable Range | Smallest positive value that cannot be formed | Missing Coin Sum |
| Powers of Two / Query Dominance | Repeated divisibility by `2^x` | Multiple Powers of Two |
| Median / Absolute Difference | Minimize `sum(abs(ai - x))` | Stick Lengths |
| Periodicity / Modulo Cycle | Huge deterministic process | Odd Grasshopper |

### Contest Checklist

```text
1. Can I write the brute force?
2. What makes the brute force repetitive?
3. Is there an upper/lower bound that is achievable?
4. Is only the boundary important?
5. Can I maintain a reachable interval instead of all subset sums?
6. Does divisibility by powers of two reveal a bit pattern?
7. Is the objective sum(abs(ai - x))? -> median.
8. Does the process repeat every 2 / 4 / 8 steps?
```

---

Problem Link: [AtCoder ABC103 C — Modulo Summation](https://atcoder.jp/contests/abc103/tasks/abc103_c)

**Problem Summary:** Given `N` positive integers `a[i]`, maximize `sum(m % a[i])` over a positive integer `m` and output the maximum possible sum. **Input/Output:** read `N` and the array, then print one integer; the supplied lecture notes do not reproduce the official numeric constraints, so use the linked statement for exact limits.

<a id="modulo-summation-modulo-extremum--atcoder--abc103-c"></a>

### Modulo Summation (Modulo Extremum / AtCoder / ABC103 C)

* **Core Invariant / Key Insight:** For every `a[i]`, the largest possible remainder is `a[i] - 1`. All these maxima can be achieved simultaneously by choosing a common multiple of all `a[i]` minus `1`, so the answer is simply `sum(a[i] - 1)`.

* **Step-by-Step Logic:**
1. For each value `a[i]`, observe that `m % a[i] <= a[i] - 1`.
2. A value such as `(a[0] * a[1] * ... * a[n-1]) - 1` is congruent to `-1` modulo every `a[i]`, so every remainder can simultaneously become `a[i] - 1`.
3. Add `a[i] - 1` for every element and output the sum in `O(N)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        a = [3, 4, 6]

Step 1:         Maximum remainder for each value

                a[i] = 3  --> max remainder = 2
                a[i] = 4  --> max remainder = 3
                a[i] = 6  --> max remainder = 5

Step 2:         Conceptually choose a common multiple - 1

                m = (3 * 4 * 6) - 1
                  = 71

Step 3:         Check each remainder

                71 % 3 = 2
                71 % 4 = 3
                71 % 6 = 5

Final Answer:   2 + 3 + 5 = 10

Shortcut:       sum(a[i] - 1)
                = (3 - 1) + (4 - 1) + (6 - 1)
                = 10
```

* **Pseudocode:**

```text
read N

answer = 0

for i = 0 to N - 1:
    read x
    answer = answer + (x - 1)

print answer
```

* **Complexity:** `O(N)` time and `O(1)` extra space.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    long long answer = 0;

    for (int i = 0; i < n; ++i) {
        long long x;
        cin >> x;

        // Maximum possible contribution of x is x - 1.
        answer += x - 1;
    }

    cout << answer << '\n';

    return 0;
}
```

---

Problem Link: [Codeforces 1966B — Rectangle Filling](https://codeforces.com/contest/1966/problem/B)

**Problem Summary:** For each test case, given an `N x M` grid containing only `W` and `B`, determine whether rectangle-coloring operations can eventually make the entire grid one color and output `YES` or `NO`. **Input/Output:** read the test cases and grids, then print one answer per case; exact numeric constraints are not reproduced in the supplied notes.

<a id="rectangle-filling-boundary-invariant--codeforces--div-2-b"></a>

### Rectangle Filling (Boundary Invariant / Codeforces / Div. 2 B)

* **Core Invariant / Key Insight:** Do not simulate rectangle operations. The impossible cases are determined by opposite extreme borders: if the first and last rows are monochromatic with different colors, or the first and last columns are monochromatic with different colors, the required color cannot bridge that dimension.

* **Step-by-Step Logic:**
1. Check whether the first row is monochromatic and whether the last row is monochromatic.
2. Do the same for the first and last columns; if either opposite-border pair is forced to different colors, output `NO`.
3. Otherwise output `YES`, because the blocking boundary configuration does not exist.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial Grid:

                BBB
                BWB
                WWW

Step 1:         First row = BBB
                --> monochromatic B

Step 2:         Last row  = WWW
                --> monochromatic W

Step 3:         Opposite extreme rows are forced
                to different colors

                BBB
                ...
                WWW
                ^^^
                cannot bridge the full height
                using same-colored endpoints

Final Answer:   NO
```

Another quick case:

```text
Initial Grid:

                BWW
                WBW
                WWB

Step 1:         No opposite monochromatic border
                creates the blocking condition.

Final Answer:   YES
```

* **Pseudocode:**

```text
for each test case:

    read N, M
    read grid

    topSame = true
    bottomSame = true

    for each column j:
        if grid[0][j] != grid[0][0]:
            topSame = false

        if grid[N-1][j] != grid[N-1][0]:
            bottomSame = false

    leftSame = true
    rightSame = true

    for each row i:
        if grid[i][0] != grid[0][0]:
            leftSame = false

        if grid[i][M-1] != grid[0][M-1]:
            rightSame = false

    badRows =
        topSame AND
        bottomSame AND
        grid[0][0] != grid[N-1][0]

    badCols =
        leftSame AND
        rightSame AND
        grid[0][0] != grid[0][M-1]

    if badRows OR badCols:
        print NO
    else:
        print YES
```

* **Complexity:** `O(NM)` time to read/process the grid and `O(NM)` storage for the grid.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, m;
        cin >> n >> m;

        vector<string> grid(n);

        for (string &row : grid) {
            cin >> row;
        }

        bool topSame = true;
        bool bottomSame = true;

        for (int j = 1; j < m; ++j) {
            if (grid[0][j] != grid[0][0]) {
                topSame = false;
            }

            if (grid[n - 1][j] != grid[n - 1][0]) {
                bottomSame = false;
            }
        }

        bool leftSame = true;
        bool rightSame = true;

        for (int i = 1; i < n; ++i) {
            if (grid[i][0] != grid[0][0]) {
                leftSame = false;
            }

            if (grid[i][m - 1] != grid[0][m - 1]) {
                rightSame = false;
            }
        }

        bool badRows =
            topSame &&
            bottomSame &&
            grid[0][0] != grid[n - 1][0];

        bool badCols =
            leftSame &&
            rightSame &&
            grid[0][0] != grid[0][m - 1];

        cout << ((badRows || badCols) ? "NO" : "YES") << '\n';
    }

    return 0;
}
```

---

Problem Link: [CSES 2183 — Missing Coin Sum](https://cses.fi/problemset/task/2183)

**Problem Summary:** Given `N` positive coin values, with each coin usable at most once, find and output the smallest positive sum that cannot be formed by a subset. **Input/Output:** read `N` and the coin array, then print one integer; exact numeric constraints are not reproduced in the supplied notes.

<a id="missing-coin-sum-greedy-reachable-range--cses--sorting-greedy"></a>

### Missing Coin Sum (Greedy Reachable Range / CSES / Sorting + Greedy)

* **Core Invariant / Key Insight:** Maintain `X` as the smallest positive value that cannot currently be formed. If processed coins can form every value in `[1, X - 1]`, then a new coin `a[i] <= X` extends the reachable interval to `[1, X + a[i] - 1]`; if `a[i] > X`, then `X` is the first unavoidable gap.

* **Step-by-Step Logic:**
1. Sort the coins and initialize `X = 1`, meaning no positive value is currently guaranteed reachable.
2. For each coin, if `coin > X`, stop because `X` cannot be formed; otherwise update `X += coin`.
3. After the scan, output `X` as the smallest missing positive subset sum.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        coins = [1, 2, 2, 7, 9]

Step 1:         Sort
                [1, 2, 2, 7, 9]

                X = 1
                reachable = [1, X - 1] = empty

Step 2:         coin = 1
                1 <= X(1)

                reachable expands:
                [1, 0] --> [1, 1]

                X = 1 + 1 = 2

Step 3:         coin = 2
                2 <= X(2)

                reachable:
                [1, 1] --> [1, 3]

                X = 2 + 2 = 4

Step 4:         coin = 2
                2 <= X(4)

                reachable:
                [1, 3] --> [1, 5]

                X = 4 + 2 = 6

Step 5:         coin = 7
                7 > X(6)

                GAP FOUND:
                1..5 are constructible
                6 is not constructible
                next coin already starts at 7

Final Answer:   6
```

* **Pseudocode:**

```text
read N
read array A

sort A

X = 1

for coin in A:

    if coin > X:
        break

    X = X + coin

print X
```

* **Complexity:** `O(N log N)` time because of sorting and `O(N)` space for the coin array.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> coins(n);

    for (long long &coin : coins) {
        cin >> coin;
    }

    sort(coins.begin(), coins.end());

    // All values in [1, missing - 1] are constructible.
    long long missing = 1;

    for (long long coin : coins) {

        // If coin is larger than the first missing value,
        // this gap can never be filled.
        if (coin > missing) {
            break;
        }

        // Extend:
        // [1, missing - 1]
        // to
        // [1, missing + coin - 1].
        missing += coin;
    }

    cout << missing << '\n';

    return 0;
}
```

---

Problem Link: [HackerRank — Multiple Powers of Two](https://www.hackerrank.com/contests/dcc-lab-30-jan/challenges/multiple-powers-of-two/problem)

**Problem Summary:** Process repeated power-of-two queries over an array. For an effective exponent `x`, values divisible by `2^x` receive `+2^(x - 1)`, and the final array is output. **Input/Output:** the lecture shows test cases with `N`, `Q`, an array, and query exponents; the supplied notes do not contain a complete clean statement or exact constraints, so no unsupported limits are invented here.

<a id="multiple-powers-of-two-bit-pattern-query-dominance--hackerrank--bit-manipulation"></a>

### Multiple Powers of Two (Bit Pattern + Query Dominance / HackerRank / Bit Manipulation)

* **Core Invariant / Key Insight:** Divisibility by `2^x` means the binary number has at least `x` trailing zero bits. After applying exponent `x`, affected values gain bit `x - 1`, so future queries with exponent `>= x` are redundant; only a strictly decreasing sequence of query exponents needs full processing.

* **Step-by-Step Logic:**
1. Maintain `minProcessed`, the smallest effective exponent processed so far.
2. For query `x`, skip it when `x >= minProcessed`; otherwise scan the array, and for every `a[i]` divisible by `2^x`, add `2^(x - 1)`.
3. Set `minProcessed = x`; because effective exponents strictly decrease, only a small number of full scans are needed.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        value = 24
                x = 3

Binary:         24 = 11000
                         ^^^
                3 trailing zero bits

Step 1:         divisor = 2^3 = 8

                24 % 8 = 0
                --> query affects 24

Step 2:         add = 2^(3 - 1)
                    = 2^2
                    = 4

Binary Add:     11000     (24)
              + 00100     (4)
              -------
                11100     (28)

Step 3:         value becomes 28

                minProcessed = 3

Later Query:    x = 4

                4 >= minProcessed(3)
                --> redundant
                --> skip

Later Query:    x = 2

                2 < minProcessed(3)
                --> effective
                --> process

Final Idea:     effective query exponents are
                strictly decreasing
```

Query filtering example:

```text
Queries:        29  30  31   4   6   2
Effective?:      Y   N   N   Y   N   Y

Kept:           29 -> 4 -> 2
                strictly decreasing
```

* **Pseudocode:**

```text
read N, Q
read array A

minProcessed = INF

repeat Q times:

    read x

    if x >= minProcessed:
        continue

    divisor = 2^x
    add = 2^(x - 1)

    for i = 0 to N - 1:

        if A[i] % divisor == 0:
            A[i] = A[i] + add

    minProcessed = x

print A
```

* **Complexity:** If there are `B` relevant bit positions, only `B` effective exponents can survive, giving approximately `O(N * B + Q)` time and `O(1)` extra working space apart from the input array.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, q;
        cin >> n >> q;

        vector<long long> a(n);

        for (long long &value : a) {
            cin >> value;
        }

        // Larger/equal future exponents become redundant.
        int minProcessed = 31;

        while (q--) {
            int x;
            cin >> x;

            if (x >= minProcessed) {
                continue;
            }

            long long divisor = 1LL << x;
            long long add = 1LL << (x - 1);

            for (long long &value : a) {
                if (value % divisor == 0) {
                    value += add;
                }
            }

            minProcessed = x;
        }

        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                cout << ' ';
            }

            cout << a[i];
        }

        cout << '\n';
    }

    return 0;
}
```

> **Source-note limitation:** The supplied lecture material is diagram-heavy for this problem and does not expose a complete clean text statement. The presentation above preserves the transformation and optimization shown in the lecture rather than inventing unsupported statement details.

---

Problem Link: [CSES 1074 — Stick Lengths](https://cses.fi/problemset/task/1074)

**Problem Summary:** Given `N` stick lengths, choose one final length for all sticks so that the total number of unit increases/decreases is minimum, and output that minimum cost. **Input/Output:** read `N` and the lengths, then print one integer; exact numeric constraints are not reproduced in the supplied notes.

<a id="stick-lengths-median--cses--greedy-math"></a>

### Stick Lengths (Median / CSES / Greedy-Math)

* **Core Invariant / Key Insight:** The target minimizing `sum(abs(a[i] - x))` is a median of the array. After sorting, choosing `a[N / 2]` is sufficient.

* **Step-by-Step Logic:**
1. Sort all stick lengths and select the median `a[N / 2]`.
2. For every stick, calculate `abs(a[i] - median)` and add it to the total cost.
3. Output the accumulated cost in `O(N log N)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        a = [2, 3, 1, 5, 2]

Step 1:         Sort

                [1, 2, 2, 3, 5]

Step 2:         Pick median

                index  = N / 2
                       = 5 / 2
                       = 2

                median = a[2]
                       = 2

Step 3:         Compute absolute distances

                |1 - 2| = 1
                |2 - 2| = 0
                |2 - 2| = 0
                |3 - 2| = 1
                |5 - 2| = 3

                total = 1 + 0 + 0 + 1 + 3
                      = 5

Final Answer:   5
Final State:    all sticks conceptually become length 2
```

Why not the average?

```text
Objective:      sum(abs(a[i] - x))
                ^^^^^^^^^^^^^^^^^^
                absolute distance / L1

Recognition:    L1 minimization --> MEDIAN
```

* **Pseudocode:**

```text
read N
read array A

sort A

median = A[N / 2]

answer = 0

for value in A:
    answer = answer + abs(value - median)

print answer
```

* **Complexity:** `O(N log N)` time due to sorting and `O(N)` storage for the input array.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> a(n);

    for (long long &x : a) {
        cin >> x;
    }

    sort(a.begin(), a.end());

    // A median minimizes the sum of absolute deviations.
    long long median = a[n / 2];

    long long answer = 0;

    for (long long x : a) {
        answer += llabs(x - median);
    }

    cout << answer << '\n';

    return 0;
}
```

---

Problem Link: [Codeforces 1607B — Odd Grasshopper](https://codeforces.com/problemset/problem/1607/B)

**Problem Summary:** For each test case, a grasshopper starts at coordinate `x0`; on jump `i`, it moves left by `i` from an even coordinate and right by `i` from an odd coordinate. Output its coordinate after `n` jumps; the supplied notes do not reproduce the exact official numeric limits.

<a id="odd-grasshopper-periodicity--codeforces--900"></a>

### Odd Grasshopper (Periodicity / Codeforces / 900)

* **Core Invariant / Key Insight:** The movement has a cycle of length `4`, so simulating all `n` jumps is unnecessary. Determine the displacement from `n % 4`, then reverse its sign when the initial coordinate is odd.

* **Step-by-Step Logic:**
1. Compute `r = n % 4` and derive the displacement for an even starting coordinate: `0`, `-n`, `+1`, or `n + 1`.
2. If `x0` is odd, reverse the sign of that displacement because the left/right behavior is inverted.
3. Output `x0 + displacement` in `O(1)` time.

* **ASCII Execution Trace / Visual Dry Run:**

```text
Initial:        x0 = 0
                n  = 7

Step 1:         Observe the first 4 jumps

                jump 1:
                0 is even
                0 - 1 = -1

                jump 2:
                -1 is odd
                -1 + 2 = 1

                jump 3:
                1 is odd
                1 + 3 = 4

                jump 4:
                4 is even
                4 - 4 = 0

                after 4 jumps -> back to relative start

Step 2:         n % 4 = 7 % 4 = 3

                for remainder 3:
                displacement = n + 1
                             = 8

Step 3:         x0 is even
                --> keep displacement sign

                answer = x0 + 8
                       = 8

Final Answer:   8
```

Remainder pattern:

```text
For EVEN x0:

n % 4 = 0   --> displacement = 0
n % 4 = 1   --> displacement = -n
n % 4 = 2   --> displacement = +1
n % 4 = 3   --> displacement = +(n + 1)

For ODD x0:

flip the sign of the displacement.
```

Odd-start example:

```text
Initial:        x0 = 5
                n  = 3

Step 1:         n % 4 = 3

Step 2:         even-start displacement would be:
                n + 1 = 4

Step 3:         x0 is odd
                --> flip sign
                displacement = -4

Final Answer:   5 - 4 = 1
```

* **Pseudocode:**

```text
read x0, n

r = n % 4

if r == 0:
    displacement = 0

else if r == 1:
    displacement = -n

else if r == 2:
    displacement = 1

else:
    displacement = n + 1

if x0 is odd:
    displacement = -displacement

answer = x0 + displacement

print answer
```

* **Complexity:** `O(1)` time and `O(1)` extra space per test case.

* **Complete C++ Code:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        long long x0, n;
        cin >> x0 >> n;

        long long displacement = 0;

        if (n % 4 == 0) {
            displacement = 0;
        }
        else if (n % 4 == 1) {
            displacement = -n;
        }
        else if (n % 4 == 2) {
            displacement = 1;
        }
        else {
            displacement = n + 1;
        }

        // Odd starting coordinate reverses the movement direction.
        if (x0 & 1LL) {
            displacement = -displacement;
        }

        cout << x0 + displacement << '\n';
    }

    return 0;
}
```

---

# Final Pattern Recognition Sheet

| Problem | Pattern | One-Line Recognition |
|---|---|---|
| Modulo Summation | Modulo Extremum | Each remainder has maximum `a[i] - 1`; ask whether all maxima can happen together |
| Rectangle Filling | Boundary Invariant | Do not simulate grid operations; inspect impossible opposite-border states |
| Missing Coin Sum | Reachable Range Greedy | Maintain the first missing value `X` and extend `[1, X - 1]` |
| Multiple Powers of Two | Bit / Query Dominance | `2^x` divisibility = trailing zeros; effective exponents strictly decrease |
| Stick Lengths | Median | `sum(abs(a[i] - x))` is minimized by a median |
| Odd Grasshopper | Periodicity | Write the first few moves and reduce the process using `n % 4` |

## 30-Second Revision Map

```text
MAXIMUM MODULO SUM
    -> individual max remainder
    -> common multiple - 1

GRID RECTANGLE OPERATIONS
    -> inspect borders
    -> find impossible invariant

SMALLEST MISSING SUBSET SUM
    -> sort
    -> reachable [1, X - 1]
    -> coin <= X ? extend : gap

POWERS OF TWO QUERIES
    -> trailing zeros
    -> only decreasing effective exponents

MINIMUM SUM OF ABSOLUTE DIFFERENCES
    -> median

HUGE PARITY-BASED MOVEMENT
    -> find cycle
    -> modulo 4
```
