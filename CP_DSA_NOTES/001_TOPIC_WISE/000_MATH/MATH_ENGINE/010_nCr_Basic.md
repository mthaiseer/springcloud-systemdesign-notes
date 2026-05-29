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
→ C++ code
```

---

## Form 1 — Choose Team Members

### Problem

Choose:

```text
3 members from 8 people
```

---

### Pattern

Combination.

---

### Step-by-Step Working

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{8 \\choose 3} = \\frac{8!}{3!5!}"}}

Compute:

```cpp
56
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll nCr(ll n, ll r) {

    r = min(r, n-r);

    ll result = 1;

    for (ll i = 1; i <= r; i++) {

        result = result * (n-r+i) / i;
    }

    return result;
}

int main() {

    cout << nCr(8,3) << "\n";

    return 0;
}
```

---

## Form 2 — Count Subsets Of Size K

### Problem

How many subsets of size:

```cpp
4
```

exist in array of size:

```cpp
10
```

---

### Pattern

```cpp
10C4
```

---

### Step-by-Step Working

Formula:

```cpp
10!/(4!*6!)
```

Answer:

```cpp
210
```

---

## Form 3 — Pascal Triangle Query

### Problem

Find:

```cpp
7C3
```

using DP.

---

### Pattern

Pascal recurrence.

---

### Step-by-Step Working

Use:

```cpp
7C3
=
6C2 + 6C3
=
15 + 20
=
35
```

---

## Form 4 — Probability Counting

### Problem

Choose 2 cards from 52 cards.

---

### Pattern

Combination.

---

### Step-by-Step Working

Formula:

```cpp
52C2
```

Result:

```cpp
1326
```

---

## Form 5 — Grid Path Counting

### Problem

Move from:

```text
top-left to bottom-right
```

in grid using:

```text
only right/down
```

---

### Pattern

Combination.

---

### Step-by-Step Working

For grid:

```cpp
3x3
```

Need:

```text
2 right + 2 down
```

Total moves:

```text
4
```

Choose positions of 2 rights:

```cpp
4C2 = 6
```

Very important pattern.

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
