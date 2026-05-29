# 010_nCr_Basic.md

# MiniMathEngine — nCr Basics (Combinations)

> Goal: build deep intuition for:
>
> ```text
> combinations
> choosing objects
> counting selections
> Pascal triangle
> factorial mathematics
> ```
>
> This topic is the foundation of:
>
> - combinatorics
> - probability
> - DP counting
> - inclusion-exclusion
> - graph counting
> - advanced CP mathematics

---

# Clickable Index

- [1. Why This Topic Matters](#1-why-this-topic-matters)
- [2. What Is nCr](#2-what-is-ncr)
- [3. Combination vs Permutation](#3-combination-vs-permutation)
- [4. Core Mental Model](#4-core-mental-model)
- [5. Why Order Does Not Matter](#5-why-order-does-not-matter)
- [6. nCr Formula](#6-ncr-formula)
- [7. Why nCr Formula Works](#7-why-ncr-formula-works)
- [8. Step-by-Step Working — 5C2](#8-step-by-step-working--5c2)
- [9. Step-by-Step Working — 6C3](#9-step-by-step-working--6c3)
- [10. Pascal Triangle Intuition](#10-pascal-triangle-intuition)
- [11. Pascal Identity](#11-pascal-identity)
- [12. Pattern Detector](#12-pattern-detector)
- [13. C++ Code 1 — Basic Factorial](#13-c-code-1--basic-factorial)
- [14. C++ Code 2 — Basic nCr Using Factorial](#14-c-code-2--basic-ncr-using-factorial)
- [15. C++ Code 3 — Optimized nCr Without Overflow](#15-c-code-3--optimized-ncr-without-overflow)
- [16. C++ Code 4 — Pascal Triangle DP](#16-c-code-4--pascal-triangle-dp)
- [17. C++ Code 5 — Precompute nCr Table](#17-c-code-5--precompute-ncr-table)
- [18. C++ Code 6 — Count Subsets Of Size K](#18-c-code-6--count-subsets-of-size-k)
- [19. Dry Run Table](#19-dry-run-table)
- [20. Java Logic Comments](#20-java-logic-comments)
- [21. Common Mistakes](#21-common-mistakes)
- [22. CP / FAANG Problem Forms](#22-cp--faang-problem-forms)
- [23. Real World Mapping](#23-real-world-mapping)
- [24. Practice Problems](#24-practice-problems)
- [25. Final Mental Summary](#25-final-mental-summary)
- [26. Next Step](#26-next-step)

---

# 1. Why This Topic Matters

nCr is one of the MOST IMPORTANT counting concepts.

It appears everywhere:

- subset counting
- probability
- graph theory
- DP
- combinatorics
- interview counting problems

If you master nCr deeply:

many advanced math problems become easier.

---

# 2. What Is nCr

nCr means:

```text
Choose r objects from n objects
where order DOES NOT matter
```

Notation:

```cpp
nCr
```

Example:

```cpp
5C2
```

means:

```text
choose 2 objects from 5
```

Suppose:

```text
A B C D E
```

Possible selections:

```text
AB
AC
AD
AE
BC
BD
BE
CD
CE
DE
```

Count:

```cpp
10
```

Therefore:

```cpp
5C2 = 10
```

---

# 3. Combination vs Permutation

Very important distinction.

Combination:

```text
order DOES NOT matter
```

Permutation:

```text
order matters
```

Example:

Choosing captain and vice-captain:

```text
AB != BA
```

Permutation.

Choosing 2 team members:

```text
AB == BA
```

Combination.

---

# 4. Core Mental Model

Combination means:

```text
unique groups
```

without caring about arrangement.

Think:

```text
How many UNIQUE selections exist?
```

Not:

```text
How many orderings?
```

---

# 5. Why Order Does Not Matter

Suppose:

```text
A B
```

and:

```text
B A
```

represent same group.

Permutation counts both separately.

Combination removes duplicate arrangements.

This is why factorial division appears.

---

# 6. nCr Formula

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{n \\choose r} = \\frac{n!}{r!(n-r)!}"}}

Meaning:

- `n!` counts all arrangements
- divide by `r!` because chosen items can rearrange internally
- divide by `(n-r)!` for remaining items

---

# 7. Why nCr Formula Works

Suppose:

```cpp
5C2
```

First count permutations:

```cpp
5P2 = 5*4 = 20
```

But:

```text
AB and BA
```

are same selection.

Each pair counted:

```cpp
2! times
```

Therefore:

```cpp
20 / 2! = 10
```

Combination removes duplicate ordering.

---

# 8. Step-by-Step Working — 5C2

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{5 \\choose 2} = \\frac{5!}{2!3!}"}}

Compute:

```cpp
5! = 120
2! = 2
3! = 6
```

Result:

```cpp
120 / (2*6)
=
120 / 12
=
10
```

Answer:

```cpp
10
```

---

# 9. Step-by-Step Working — 6C3

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{6 \\choose 3} = \\frac{6!}{3!3!}"}}

Compute:

```cpp
6! = 720
3! = 6
```

Result:

```cpp
720 / (6*6)
=
20
```

Answer:

```cpp
20
```

---

# 10. Pascal Triangle Intuition

Pascal triangle:

```text
            1
          1   1
        1   2   1
      1   3   3   1
    1   4   6   4   1
  1   5  10  10  5  1
```

Each value:

```text
top-left + top-right
```

Example:

```cpp
6 = 3 + 3
```

These are nCr values.

Example:

```cpp
5C2 = 10
```

---

# 11. Pascal Identity

Very important identity:

genui{"math_block_widget_always_prefetch_v2":{"content":"{n \\choose r} = {n-1 \\choose r-1} + {n-1 \\choose r}"}}

Meaning:

For element `x`:

- choose sets INCLUDING x
- choose sets EXCLUDING x

This identity is foundation for:

- DP
- Pascal triangle
- recursive counting

---

# 12. Pattern Detector

If you see:

```text
choose r items from n
```

think:

```text
nCr
```

If you see:

```text
subsets of size k
```

think:

```text
nCr
```

If you see:

```text
order doesn't matter
```

think:

```text
combination
```

If you see:

```text
Pascal recurrence
```

think:

```text
nCr DP
```

---

# 13. C++ Code 1 — Basic Factorial

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Basic factorial
//
// n! = 1*2*3*...*n
// ---------------------------------------------------
ll factorial(ll n) {

    ll result = 1;

    for (ll x = 2; x <= n; x++) {

        result *= x;
    }

    return result;
}

int main() {

    cout << factorial(5) << "\n";

    return 0;
}
```

---

# 14. C++ Code 2 — Basic nCr Using Factorial

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll factorial(ll n) {

    ll result = 1;

    for (ll x = 2; x <= n; x++) {

        result *= x;
    }

    return result;
}

// ---------------------------------------------------
// Basic nCr formula
//
// Works for small numbers only
// due to overflow
// ---------------------------------------------------
ll nCr(ll n, ll r) {

    return factorial(n) /
           (factorial(r) * factorial(n - r));
}

int main() {

    cout << nCr(5, 2) << "\n";
    cout << nCr(6, 3) << "\n";

    return 0;
}
```

---

# 15. C++ Code 3 — Optimized nCr Without Overflow

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Optimized nCr
//
// Avoids huge factorial values
//
// Formula:
// nCr = product form
// ---------------------------------------------------
ll nCr(ll n, ll r) {

    if (r > n)
        return 0;

    // Symmetry optimization
    r = min(r, n - r);

    ll result = 1;

    for (ll i = 1; i <= r; i++) {

        result = result * (n - r + i) / i;
    }

    return result;
}

int main() {

    cout << nCr(50, 5) << "\n";

    return 0;
}
```

---

# 16. C++ Code 4 — Pascal Triangle DP

```cpp
#include <bits/stdc++.h>
using namespace std;

// ---------------------------------------------------
// Build Pascal Triangle
//
// dp[n][r] = nCr
// ---------------------------------------------------
vector<vector<long long>> buildPascal(int n) {

    vector<vector<long long>> dp(
        n + 1,
        vector<long long>(n + 1, 0)
    );

    for (int row = 0; row <= n; row++) {

        dp[row][0] = 1;
        dp[row][row] = 1;

        for (int col = 1; col < row; col++) {

            dp[row][col] =
                dp[row - 1][col - 1]
                +
                dp[row - 1][col];
        }
    }

    return dp;
}

int main() {

    auto pascal = buildPascal(10);

    cout << pascal[5][2] << "\n";

    return 0;
}
```

---

# 17. C++ Code 5 — Precompute nCr Table

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const int MAXN = 1000;

ll nCrTable[MAXN + 1][MAXN + 1];

// ---------------------------------------------------
// Precompute all nCr values
// ---------------------------------------------------
void buildTable() {

    for (int n = 0; n <= MAXN; n++) {

        nCrTable[n][0] = 1;
        nCrTable[n][n] = 1;

        for (int r = 1; r < n; r++) {

            nCrTable[n][r] =
                nCrTable[n - 1][r - 1]
                +
                nCrTable[n - 1][r];
        }
    }
}

int main() {

    buildTable();

    cout << nCrTable[10][3] << "\n";

    return 0;
}
```

---

# 18. C++ Code 6 — Count Subsets Of Size K

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll nCr(ll n, ll r) {

    r = min(r, n - r);

    ll result = 1;

    for (ll i = 1; i <= r; i++) {

        result = result * (n - r + i) / i;
    }

    return result;
}

// ---------------------------------------------------
// Count subsets of size k
// ---------------------------------------------------
int main() {

    int n = 8;
    int k = 3;

    cout << nCr(n, k) << "\n";

    return 0;
}
```

---

# 19. Dry Run Table

Compute:

```cpp
5C2
```

| Step | Value |
|---|---:|
| 5! | 120 |
| 2! | 2 |
| 3! | 6 |
| denominator | 12 |
| final | 10 |

---

# 20. Java Logic Comments

```text
nCr means:
choose r objects from n objects

order does not matter
```

Formula:

```text
n! / (r! * (n-r)!)
```

Pascal identity:

```text
nCr =
(n-1)C(r-1)
+
(n-1)Cr
```

---

# 21. Common Mistakes

## Mistake 1 — Confusing Combination And Permutation

Combination:

```text
order does NOT matter
```

Permutation:

```text
order matters
```

---

## Mistake 2 — Overflow Using Factorials

```cpp
20! already very large
```

Use optimized product form or modular arithmetic later.

---

## Mistake 3 — Forgetting Symmetry

Very important optimization:

genui{"math_block_widget_always_prefetch_v2":{"content":"{n \\choose r} = {n \\choose n-r}"}}

Use:

```cpp
r = min(r, n-r)
```

---

## Mistake 4 — Wrong Pascal Indices

Correct:

```cpp
dp[n][0]=1
dp[n][n]=1
```

---

# 22. CP / FAANG Problem Forms

This section includes:

```text
Problem
→ Pattern
→ Step-by-step working
→ C++ code with comments
```

---

## Form 1 — Choose Team Members

### Problem

There are:

```text
8 people
```

You need to choose:

```text
3 members
```

How many different teams can be formed?

---

### Pattern

This is:

```text
choose 3 from 8
```

Order does not matter.

So use:

```cpp
8C3
```

---

### Step-by-Step Working

Formula:

```cpp
8C3 = 8! / (3! * 5!)
```

Instead of calculating full factorials:

```text
8C3 = (8 * 7 * 6) / (3 * 2 * 1)
```

Compute:

```text
8 * 7 * 6 = 336
3 * 2 * 1 = 6
336 / 6 = 56
```

Answer:

```cpp
56
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Compute nCr using product form
//
// Why product form?
// It avoids very large factorial values.
// ---------------------------------------------------
ll nCr(ll n, ll r) {

    // Impossible case
    if (r > n)
        return 0;

    // nCr = nC(n-r)
    // Use smaller r to reduce loop work
    r = min(r, n - r);

    ll result = 1;

    // Example:
    // 8C3 = (6*7*8)/(1*2*3)
    for (ll i = 1; i <= r; i++) {

        result = result * (n - r + i);

        result = result / i;
    }

    return result;
}

int main() {

    ll n = 8;
    ll r = 3;

    cout << nCr(n, r) << "\n";

    return 0;
}
```

---

## Form 2 — Count Subsets Of Size K

### Problem

Given an array of size:

```cpp
10
```

How many subsets of size:

```cpp
4
```

can be selected?

---

### Pattern

A subset does not care about order.

So this is:

```cpp
10C4
```

---

### Step-by-Step Working

Formula:

```cpp
10C4 = 10! / (4! * 6!)
```

Product form:

```text
10C4 = (10 * 9 * 8 * 7) / (4 * 3 * 2 * 1)
```

Compute:

```text
10 * 9 * 8 * 7 = 5040
4 * 3 * 2 * 1 = 24
5040 / 24 = 210
```

Answer:

```cpp
210
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll nCr(ll n, ll r) {

    if (r > n)
        return 0;

    r = min(r, n - r);

    ll answer = 1;

    for (ll i = 1; i <= r; i++) {

        // Pick next numerator term
        answer *= (n - r + i);

        // Divide by denominator term
        answer /= i;
    }

    return answer;
}

int main() {

    int arraySize = 10;
    int subsetSize = 4;

    cout << nCr(arraySize, subsetSize) << "\n";

    return 0;
}
```

---

## Form 3 — Pascal Triangle Query

### Problem

Find:

```cpp
7C3
```

using Pascal triangle DP.

---

### Pattern

Use Pascal recurrence:

```cpp
nCr = (n-1)C(r-1) + (n-1)Cr
```

Meaning:

```text
Either choose current item
or do not choose current item.
```

---

### Step-by-Step Working

```cpp
7C3 = 6C2 + 6C3
```

Known values:

```cpp
6C2 = 15
6C3 = 20
```

So:

```cpp
7C3 = 15 + 20 = 35
```

Answer:

```cpp
35
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Build Pascal DP table
//
// dp[n][r] = nCr
// ---------------------------------------------------
vector<vector<ll>> buildNcrTable(int maxN) {

    vector<vector<ll>> dp(
        maxN + 1,
        vector<ll>(maxN + 1, 0)
    );

    for (int n = 0; n <= maxN; n++) {

        // Base cases:
        // nC0 = 1
        // nCn = 1
        dp[n][0] = 1;
        dp[n][n] = 1;

        for (int r = 1; r < n; r++) {

            // Pascal recurrence
            dp[n][r] =
                dp[n - 1][r - 1]
                +
                dp[n - 1][r];
        }
    }

    return dp;
}

int main() {

    auto dp = buildNcrTable(10);

    cout << dp[7][3] << "\n";

    return 0;
}
```

---

## Form 4 — Probability Counting

### Problem

From a deck of:

```cpp
52 cards
```

How many ways can you choose:

```cpp
2 cards
```

?

---

### Pattern

Order does not matter.

Choosing card A then B is same as choosing B then A.

So use:

```cpp
52C2
```

---

### Step-by-Step Working

Formula:

```cpp
52C2 = 52! / (2! * 50!)
```

Product form:

```text
52C2 = (52 * 51) / 2
```

Compute:

```text
52 * 51 = 2652
2652 / 2 = 1326
```

Answer:

```cpp
1326
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll nCr(ll n, ll r) {

    if (r > n)
        return 0;

    r = min(r, n - r);

    ll result = 1;

    for (ll i = 1; i <= r; i++) {

        result *= (n - r + i);

        result /= i;
    }

    return result;
}

int main() {

    ll totalCards = 52;
    ll chooseCards = 2;

    cout << nCr(totalCards, chooseCards) << "\n";

    return 0;
}
```

---

## Form 5 — Grid Path Counting

### Problem

You are in a:

```text
3 x 3 grid
```

Start:

```text
top-left
```

End:

```text
bottom-right
```

Allowed moves:

```text
Right or Down only
```

How many paths exist?

---

### Pattern

To move from top-left to bottom-right in `3x3` grid:

```text
Need 2 Right moves
Need 2 Down moves
```

Total moves:

```text
4
```

Choose positions of Right moves:

```cpp
4C2
```

or choose positions of Down moves:

```cpp
4C2
```

---

### Step-by-Step Working

Moves are:

```text
R R D D
```

Any ordering of these 4 moves is valid.

Number of unique arrangements:

```cpp
4! / (2! * 2!)
```

Same as:

```cpp
4C2
```

Compute:

```text
4C2 = 6
```

Possible paths:

```text
RRDD
RDRD
RDDR
DRRD
DRDR
DDRR
```

Answer:

```cpp
6
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll nCr(ll n, ll r) {

    if (r > n)
        return 0;

    r = min(r, n - r);

    ll result = 1;

    for (ll i = 1; i <= r; i++) {

        result *= (n - r + i);

        result /= i;
    }

    return result;
}

// ---------------------------------------------------
// Count paths in rows x cols grid
//
// Need:
// rows-1 down moves
// cols-1 right moves
//
// Total moves:
// rows+cols-2
//
// Choose down positions:
// totalMoves C downMoves
// ---------------------------------------------------
ll countGridPaths(int rows, int cols) {

    int downMoves = rows - 1;
    int rightMoves = cols - 1;

    int totalMoves = downMoves + rightMoves;

    return nCr(totalMoves, downMoves);
}

int main() {

    cout << countGridPaths(3, 3) << "\n";

    return 0;
}
```

---

## Form 6 — Number Of Pairs

### Problem

Given:

```cpp
n = 6
```

How many unordered pairs can be formed?

---

### Pattern

Choose 2 people/items from `n`.

Use:

```cpp
nC2
```

---

### Step-by-Step Working

For:

```cpp
6C2
```

Formula:

```text
6 * 5 / 2 = 15
```

Answer:

```cpp
15
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Number of unordered pairs from n items
//
// Formula:
// nC2 = n*(n-1)/2
// ---------------------------------------------------
ll countPairs(ll n) {

    return n * (n - 1) / 2;
}

int main() {

    cout << countPairs(6) << "\n";

    return 0;
}
```

---

## Form 7 — Number Of Triplets

### Problem

Given:

```cpp
n = 5
```

How many unordered triplets can be formed?

---

### Pattern

Choose 3 items.

Use:

```cpp
nC3
```

---

### Step-by-Step Working

For:

```cpp
5C3
```

Formula:

```text
5 * 4 * 3 / (3 * 2 * 1)
```

Compute:

```text
60 / 6 = 10
```

Answer:

```cpp
10
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

// ---------------------------------------------------
// Number of unordered triplets
//
// Formula:
// nC3 = n*(n-1)*(n-2)/6
// ---------------------------------------------------
ll countTriplets(ll n) {

    if (n < 3)
        return 0;

    return n * (n - 1) * (n - 2) / 6;
}

int main() {

    cout << countTriplets(5) << "\n";

    return 0;
}
```

---

# 23. Real World Mapping

## Probability Systems

Lottery and probability calculations rely on combinations.

---

## Analytics Systems

Combinations appear in pair/triple counting analytics.

---

## Recommendation Systems

Combination generation appears in candidate selection.

---

## Machine Learning

Feature subset selection uses combinations.

---

## Distributed Systems

Replica combinations and quorum selections relate to combinatorics.

---

# 24. Practice Problems

Easy:

```text
1. Compute nCr
2. Pascal triangle
3. Count subsets
4. Grid path counting
```

Medium:

```text
5. Probability counting
6. Combination DP
7. Stars and bars
8. Multiset combinations
```

Hard:

```text
9. Catalan numbers
10. Inclusion exclusion
11. Burnside
12. Combinatorial DP
```

---

# 25. Final Mental Summary

Combination means:

```text
unique selections
```

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{n \\choose r} = \\frac{n!}{r!(n-r)!}"}}

Key intuition:

```text
remove duplicate orderings
```

Pascal identity:

genui{"math_block_widget_always_prefetch_v2":{"content":"{n \\choose r} = {n-1 \\choose r-1} + {n-1 \\choose r}"}}

This becomes foundation for:

- combinatorics
- DP
- probability
- counting problems

---

# 26. Next Step

```text
011_nCr_Modulo_Prime.md
```

Next intuition:

```text
Compute nCr under MOD efficiently.
```
