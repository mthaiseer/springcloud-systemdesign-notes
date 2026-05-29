# 018_Permutation_Combination_Intuition.md

# Permutation And Combination Intuition For Competitive Programming

---

# 1. Introduction

Permutation and Combination are among the MOST important counting concepts in CP.

They appear heavily in:
- combinatorics
- DP
- probability
- graph counting
- strings
- bitmasking
- constructive problems

Strong contestants focus less on formulas and more on:

```text
What exactly are we counting?
```

This intuition is critical.

---

# 2. Core Difference

MOST IMPORTANT IDEA:

---

# Permutation

```text
Order matters
```

Example:

```text
AB ≠ BA
```

Count separately.

---

# Combination

```text
Order does NOT matter
```

Example:

```text
AB = BA
```

Count once.

---

# 3. Simple Example

Choose 2 letters from:

```text
A B C
```

---

# Permutations

Possible:

```text
AB
BA
AC
CA
BC
CB
```

Total:
```text
6
```

---

# Combinations

Possible:

```text
AB
AC
BC
```

Total:
```text
3
```

---

# 4. Permutation Formula

Number of ways to arrange:

```text
r items from n items
```

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_nP_r = \\frac{n!}{(n-r)!}"}}

---

# 5. Combination Formula

Number of ways to choose:

```text
r items from n items
```

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_nC_r = \\frac{n!}{r!(n-r)!}"}}

---

# 6. Why Combination Divides By r!

In combinations:

```text
AB
BA
```

considered same.

But permutations count all orderings.

Thus divide by:

genui{"math_block_widget_always_prefetch_v2":{"content":"r!"}}

to remove duplicates.

---

# 7. Factorial Intuition

Factorial means:

```text
total arrangements
```

Definition:

genui{"math_block_widget_always_prefetch_v2":{"content":"n! = n \\times (n-1) \\times (n-2) \\dots 1"}}

---

# Example

```text
4!
=
4×3×2×1
=
24
```

Meaning:
24 arrangements.

---

# 8. Permutation Intuition

Permutation asks:

```text
Which order?
```

Recognition signals:
- arrangement
- ordering
- ranking
- sequence
- positions

---

# Example

Arrange:
```text
A B C
```

Ways:

```text
ABC
ACB
BAC
BCA
CAB
CBA
```

Total:

genui{"math_block_widget_always_prefetch_v2":{"content":"3! = 6"}}

---

# 9. Combination Intuition

Combination asks:

```text
Which selection?
```

Recognition signals:
- choose
- select
- group
- team
- subset

Order irrelevant.

---

# 10. Counting Teams

Example:

Choose 2 people from 5.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_5C_2 = \\frac{5!}{2!3!} = 10"}}

---

# 11. Binary Choice Intuition

Each element:
- take
- not take

2 choices.

Thus subsets:

genui{"math_block_widget_always_prefetch_v2":{"content":"2^n"}}

Very important counting observation.

---

# 12. Pascal Triangle Observation

Important identity:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_nC_r = {}_{n-1}C_{r-1} + {}_{n-1}C_r"}}

Interpretation:

```text
take current item
+
skip current item
```

Foundation of:
- DP
- combinatorics

---

# 13. Permutation With Repetition

If repetition allowed:

```text
n choices
for each position
```

Total:

genui{"math_block_widget_always_prefetch_v2":{"content":"n^r"}}

---

# Example

3 digits:
- each from 0-9

Total:

genui{"math_block_widget_always_prefetch_v2":{"content":"10^3 = 1000"}}

---

# 14. Circular Permutation

Circular arrangements:

genui{"math_block_widget_always_prefetch_v2":{"content":"(n-1)!"}}

because rotations become identical.

Very common interview problem.

---

# 15. CP / FAANG Problem Forms

---

# Form 1 — Arrange Characters

## Problem

How many ways to arrange n distinct characters?

---

## Observation

Permutation of all elements.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"n!"}}

---

## Step-by-Step Working

Example:

```text
ABC
```

Arrangements:

```text
ABC
ACB
BAC
BCA
CAB
CBA
```

Total:
```text
6
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long factorial(int n) {

    long long ans = 1;

    for (int i = 2; i <= n; i++) {

        ans *= i;
    }

    return ans;
}
```

---

# Form 2 — Choose Team

## Problem

Choose r people from n.

---

## Observation

Combination.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_nC_r"}}

---

## Step-by-Step Working

Example:

```text
5 choose 2
```

Teams:

```text
AB AC AD AE
BC BD BE
CD CE
DE
```

Total:
```text
10
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {

    if (r > n) return 0;

    long long ans = 1;

    for (int i = 1; i <= r; i++) {

        ans =
            ans * (n - i + 1) / i;
    }

    return ans;
}
```

---

# Form 3 — Permutation Count

## Problem

Choose and arrange r items from n.

---

## Observation

Order matters.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"{}_nP_r"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nPr(int n, int r) {

    long long ans = 1;

    for (int i = 0; i < r; i++) {

        ans *= (n - i);
    }

    return ans;
}
```

---

# Form 4 — Subset Count

## Problem

Count subsets.

---

## Observation

Each element:
- take
- skip

2 choices.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"2^n"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subsets(int n) {

    return 1LL << n;
}
```

---

# Form 5 — Pascal Identity

## Problem

Compute combinations recursively.

---

## Observation

Take/skip recurrence.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCrDP(int n, int r) {

    vector<vector<long long>>
    dp(n + 1,
       vector<long long>(r + 1));

    for (int i = 0; i <= n; i++) {

        dp[i][0] = 1;

        for (int j = 1;
             j <= min(i, r);
             j++) {

            dp[i][j] =
                dp[i - 1][j - 1]
              + dp[i - 1][j];
        }
    }

    return dp[n][r];
}
```

---

# Form 6 — Circular Arrangement

## Problem

Arrange people around table.

---

## Observation

Rotations identical.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"(n-1)!"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long circularWays(int n) {

    long long ans = 1;

    for (int i = 2; i <= n - 1; i++) {

        ans *= i;
    }

    return ans;
}
```

---

# Form 7 — Repetition Allowed

## Problem

Create password length r using n symbols.

---

## Observation

Every position independent.

Formula:

genui{"math_block_widget_always_prefetch_v2":{"content":"n^r"}}

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countPasswords(
    int n,
    int r
) {

    long long ans = 1;

    for (int i = 0; i < r; i++) {

        ans *= n;
    }

    return ans;
}
```

---

# 16. Real World Applications

| Real System | Usage |
|---|---|
| Password generation | permutations |
| Recommendation systems | combinations |
| Scheduling | arrangements |
| Databases | query combinations |
| Machine learning | feature subsets |
| Networking | routing combinations |
| Cryptography | key arrangements |

---

# 17. Real Engineering Insight

Permutation usually means:

```text
Arrangement
```

Combination usually means:

```text
Selection
```

This mental mapping is critical.

---

# 18. Observation Recognition Signals

Look for:

```text
1. arrange
2. order
3. ranking
4. choose
5. select
6. team
7. subset
8. positions
9. sequence
10. grouping
```

---

# 19. Decision Tree

```text
Order matters?
→ permutation

Only selection matters?
→ combination

Take/skip choices?
→ subsets

Independent positions?
→ multiplication principle

Rotations identical?
→ circular permutation

Repeated choices allowed?
→ powers
```

---

# 20. Common Traps

```text
1. Confusing permutation vs combination
2. Overflow in factorial
3. Ordered vs unordered counting
4. Forgetting divide by r!
5. Duplicate counting
6. Wrong subset interpretation
7. Missing repetition rule
8. Off-by-one in loops
```

---

# 21. Final Checklist

Before solving:

```text
1. Does order matter?
2. Is selection enough?
3. Are positions independent?
4. Is repetition allowed?
5. Are rotations identical?
6. Is subset logic useful?
7. Can Pascal identity help?
8. Can counting replace brute force?
```

---

# 22. Final Mental Shortcut

```text
Permutation
=
Arrangement

Combination
=
Selection
```
