# 013_Stars_And_Bars.md — MiniMathEngine

> **Topic:** Stars and Bars  
> **Use case:** Counting non-negative / positive integer solutions, distributions, combinations with repetition  
> **Core skill:** Convert word problems into equation-counting forms

---

## Clickable Index

1. [What Is Stars and Bars?](#1-what-is-stars-and-bars)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Mental Model](#3-mental-model)
4. [Core Formula 1 — Non-Negative Solutions](#4-core-formula-1--non-negative-solutions)
5. [Core Formula 2 — Positive Solutions](#5-core-formula-2--positive-solutions)
6. [Visual Intuition](#6-visual-intuition)
7. [When To Use Stars and Bars](#7-when-to-use-stars-and-bars)
8. [When NOT To Use Stars and Bars](#8-when-not-to-use-stars-and-bars)
9. [Problem Form 1 — Distribute Identical Items Into Boxes](#9-problem-form-1--distribute-identical-items-into-boxes)
10. [Problem Form 2 — Positive Distribution](#10-problem-form-2--positive-distribution)
11. [Problem Form 3 — At Least K In Each Box](#11-problem-form-3--at-least-k-in-each-box)
12. [Problem Form 4 — Upper Bound Constraint](#12-problem-form-4--upper-bound-constraint)
13. [Problem Form 5 — Combination With Repetition](#13-problem-form-5--combination-with-repetition)
14. [Modular Combination Helper](#14-modular-combination-helper)
15. [Dry Run](#15-dry-run)
16. [Common Mistakes](#16-common-mistakes)
17. [Decision Tree](#17-decision-tree)
18. [Real World Mapping](#18-real-world-mapping)
19. [Complexity](#19-complexity)
20. [Reusable C++ Template](#20-reusable-c-template)
21. [CP / FAANG Problem Forms](#21-cp--faang-problem-forms)
22. [Practice Checklist](#22-practice-checklist)
23. [Next Step](#23-next-step)

---

## 1. What Is Stars and Bars?

Stars and Bars is a counting technique used when:

- You have **identical items**
- You distribute them into **distinct groups / boxes**
- You need to count how many distributions are possible

Example:

```text
Distribute 5 identical candies among 3 children.
```

This means:

```text
x1 + x2 + x3 = 5
x1, x2, x3 >= 0
```

Where:

```text
x1 = candies child 1 gets
x2 = candies child 2 gets
x3 = candies child 3 gets
```

---

## 2. Why This Topic Matters

Stars and Bars appears in:

- CP combinatorics
- Counting paths
- Distribution problems
- DP optimization counting
- Probability problems
- FAANG-style counting questions
- Generating combinations with repetition

It converts a problem from:

```text
"How many ways?"
```

into:

```text
"How many integer solutions?"
```

---

## 3. Mental Model

Suppose we distribute 5 candies among 3 people.

Represent candies as stars:

```text
* * * * *
```

We need 2 bars to split them into 3 groups:

```text
* * | * | * *
```

Meaning:

```text
person1 = 2
person2 = 1
person3 = 2
```

Total symbols:

```text
5 stars + 2 bars = 7 positions
```

Choose positions for bars:

```text
C(7, 2)
```

Or choose positions for stars:

```text
C(7, 5)
```

Both are same.

---

## 4. Core Formula 1 — Non-Negative Solutions

For:

```text
x1 + x2 + ... + xk = n
xi >= 0
```

Answer:

```text
C(n + k - 1, k - 1)
```

Also equal to:

```text
C(n + k - 1, n)
```

### Example

```text
x1 + x2 + x3 = 5
xi >= 0
```

Answer:

```text
C(5 + 3 - 1, 3 - 1)
= C(7, 2)
= 21
```

---

## 5. Core Formula 2 — Positive Solutions

For:

```text
x1 + x2 + ... + xk = n
xi >= 1
```

First give 1 item to each variable.

Let:

```text
yi = xi - 1
```

Then:

```text
y1 + y2 + ... + yk = n - k
yi >= 0
```

Answer:

```text
C((n - k) + k - 1, k - 1)
= C(n - 1, k - 1)
```

### Example

```text
x1 + x2 + x3 = 5
xi >= 1
```

Answer:

```text
C(5 - 1, 3 - 1)
= C(4, 2)
= 6
```

---

## 6. Visual Intuition

### Non-negative case

```text
5 candies, 3 boxes

Arrangement:
* * | | * * *

Box1 = 2
Box2 = 0
Box3 = 3
```

Empty boxes are allowed because bars can be adjacent.

### Positive case

Positive means no empty box.

```text
* | * * | * *
```

Each section must contain at least one star.

So first reserve:

```text
1 candy per box
```

Then distribute remaining candies freely.

---

## 7. When To Use Stars and Bars

Use it when the problem says:

```text
Number of ways to distribute N identical items into K groups
```

or:

```text
Number of non-negative integer solutions
```

or:

```text
Choose K items from N types with repetition allowed
```

Common signals:

```text
identical objects
non-negative solutions
at least one
at least k
sum equals n
repetition allowed
unlimited supply
```

---

## 8. When NOT To Use Stars and Bars

Do not directly use basic Stars and Bars when:

| Case | Why |
|---|---|
| Items are distinct | Need permutations / Stirling / inclusion-exclusion |
| Boxes are identical | Different counting model |
| Upper bounds exist | Need inclusion-exclusion or DP |
| Order matters | Usually sequence/permutation problem |
| Variables have complex constraints | May need DP / generating functions |

---

## 9. Problem Form 1 — Distribute Identical Items Into Boxes

### Problem

How many ways to distribute `n` identical balls into `k` distinct boxes?

Each box can get zero or more balls.

### Equation

```text
x1 + x2 + ... + xk = n
xi >= 0
```

### Formula

```text
C(n + k - 1, k - 1)
```

### Step-by-Step Working

Example:

```text
n = 5, k = 3
```

Step 1:

```text
Need 5 stars
```

Step 2:

```text
Need k - 1 = 2 bars
```

Step 3:

```text
Total positions = 5 + 2 = 7
```

Step 4:

```text
Choose positions of 2 bars from 7 positions
```

Step 5:

```text
Answer = C(7, 2) = 21
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 5; // identical balls
    int k = 3; // distinct boxes

    // Non-negative solutions:
    // x1 + x2 + x3 = 5
    // xi >= 0
    long long ways = nCr(n + k - 1, k - 1);

    cout << ways << endl; // 21
    return 0;
}
```

---

## 10. Problem Form 2 — Positive Distribution

### Problem

How many ways to distribute `n` identical balls into `k` distinct boxes if every box must get at least one ball?

### Equation

```text
x1 + x2 + ... + xk = n
xi >= 1
```

### Formula

```text
C(n - 1, k - 1)
```

### Step-by-Step Working

Example:

```text
n = 5, k = 3
```

Step 1:

```text
Give 1 ball to each box
```

Remaining:

```text
5 - 3 = 2
```

Step 2:

```text
Now distribute 2 remaining balls freely among 3 boxes
```

Equation:

```text
y1 + y2 + y3 = 2
yi >= 0
```

Step 3:

```text
Answer = C(2 + 3 - 1, 3 - 1)
       = C(4, 2)
       = 6
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 5;
    int k = 3;

    // Positive solutions:
    // x1 + x2 + x3 = 5
    // xi >= 1
    long long ways = nCr(n - 1, k - 1);

    cout << ways << endl; // 6
    return 0;
}
```

---

## 11. Problem Form 3 — At Least K In Each Box

### Problem

How many ways to distribute `n` identical balls into `m` boxes if each box must get at least `low` balls?

### Equation

```text
x1 + x2 + ... + xm = n
xi >= low
```

### Transformation

Let:

```text
yi = xi - low
```

Then:

```text
y1 + y2 + ... + ym = n - low * m
yi >= 0
```

### Formula

```text
C((n - low * m) + m - 1, m - 1)
```

### Step-by-Step Working

Example:

```text
n = 10
m = 3
low = 2
```

Step 1:

```text
Give 2 balls to each box
```

Used:

```text
2 * 3 = 6
```

Remaining:

```text
10 - 6 = 4
```

Step 2:

```text
Distribute remaining 4 balls freely into 3 boxes
```

Step 3:

```text
Answer = C(4 + 3 - 1, 3 - 1)
       = C(6, 2)
       = 15
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 10;
    int boxes = 3;
    int low = 2;

    int remaining = n - low * boxes;

    if (remaining < 0) {
        cout << 0 << endl;
        return 0;
    }

    long long ways = nCr(remaining + boxes - 1, boxes - 1);

    cout << ways << endl; // 15
    return 0;
}
```

---

## 12. Problem Form 4 — Upper Bound Constraint

### Problem

How many non-negative solutions exist?

```text
x1 + x2 + ... + xk = n
0 <= xi <= limit
```

Basic Stars and Bars does not handle upper bound directly.

We use:

```text
total solutions - invalid solutions
```

This is usually solved using inclusion-exclusion.

### Inclusion-Exclusion Formula

For all variables with same upper bound `limit`:

```text
answer =
sum over j from 0 to k:
(-1)^j * C(k, j) * C(n - j(limit + 1) + k - 1, k - 1)
```

Only include terms where:

```text
n - j(limit + 1) >= 0
```

### Step-by-Step Working

Example:

```text
x1 + x2 + x3 = 5
0 <= xi <= 2
```

Without upper bound:

```text
C(5 + 3 - 1, 3 - 1)
= C(7, 2)
= 21
```

Invalid means at least one variable is greater than 2.

For one variable invalid:

```text
xi >= 3
```

Let:

```text
yi = xi - 3
```

Remaining sum:

```text
5 - 3 = 2
```

Number of solutions for one chosen invalid variable:

```text
C(2 + 3 - 1, 3 - 1)
= C(4, 2)
= 6
```

There are 3 variables:

```text
3 * 6 = 18
```

Two variables invalid:

```text
x1 >= 3 and x2 >= 3
```

Minimum needed:

```text
6 > 5
```

Impossible.

Answer:

```text
21 - 18 = 3
```

Valid solutions:

```text
(1,2,2)
(2,1,2)
(2,2,1)
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 5;
    int k = 3;
    int limit = 2;

    long long ans = 0;

    // Inclusion-exclusion:
    // choose j variables that violate xi <= limit
    for (int j = 0; j <= k; j++) {
        int remaining = n - j * (limit + 1);

        if (remaining < 0) continue;

        long long waysToChooseBadVariables = nCr(k, j);
        long long waysAfterFixingBadVariables =
            nCr(remaining + k - 1, k - 1);

        if (j % 2 == 0) {
            ans += waysToChooseBadVariables * waysAfterFixingBadVariables;
        } else {
            ans -= waysToChooseBadVariables * waysAfterFixingBadVariables;
        }
    }

    cout << ans << endl; // 3
    return 0;
}
```

---

## 13. Problem Form 5 — Combination With Repetition

### Problem

How many ways to choose `r` items from `n` types if repetition is allowed?

Example:

```text
Choose 4 fruits from:
apple, banana, orange
```

You can choose:

```text
4 apples
2 apples + 1 banana + 1 orange
0 apples + 4 bananas
...
```

Let:

```text
x1 = number of apples
x2 = number of bananas
x3 = number of oranges
```

Equation:

```text
x1 + x2 + x3 = 4
xi >= 0
```

### Formula

```text
C(n + r - 1, r)
```

or:

```text
C(n + r - 1, n - 1)
```

### Step-by-Step Working

Example:

```text
n = 3 types
r = 4 selected items
```

Step 1:

```text
x1 + x2 + x3 = 4
```

Step 2:

```text
Non-negative solution count
```

Step 3:

```text
Answer = C(3 + 4 - 1, 4)
       = C(6, 4)
       = 15
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int types = 3;
    int choose = 4;

    // Combination with repetition:
    // x1 + x2 + x3 = 4
    // xi >= 0
    long long ways = nCr(types + choose - 1, choose);

    cout << ways << endl; // 15
    return 0;
}
```

---

## 14. Modular Combination Helper

In CP, answers are often huge.

Use modulo:

```text
MOD = 1e9 + 7
```

For combination:

```text
C(n, r) mod MOD
```

We precompute:

```text
fact[i]
invFact[i]
```

Using Fermat inverse:

```text
a^(MOD - 2) mod MOD
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1'000'000'007;
const int MAXN = 1'000'000;

long long fact[MAXN + 1];
long long invFact[MAXN + 1];

long long modPow(long long a, long long e) {
    long long res = 1;

    while (e > 0) {
        if (e & 1) res = (res * a) % MOD;
        a = (a * a) % MOD;
        e >>= 1;
    }

    return res;
}

void buildFactorials() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = (fact[i - 1] * i) % MOD;
    }

    invFact[MAXN] = modPow(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
    }
}

long long nCrMod(int n, int r) {
    if (r < 0 || r > n) return 0;

    return (((fact[n] * invFact[r]) % MOD) * invFact[n - r]) % MOD;
}

long long starsBarsNonNegative(int sum, int variables) {
    // x1 + x2 + ... + x_variables = sum
    // xi >= 0
    return nCrMod(sum + variables - 1, variables - 1);
}

int main() {
    buildFactorials();

    int n = 5;
    int k = 3;

    cout << starsBarsNonNegative(n, k) << endl; // 21

    return 0;
}
```

---

## 15. Dry Run

Problem:

```text
How many ways to distribute 4 candies among 3 children?
```

Equation:

```text
x1 + x2 + x3 = 4
xi >= 0
```

Stars:

```text
* * * *
```

Bars needed:

```text
2
```

Example arrangements:

```text
* * | * | *
x1 = 2, x2 = 1, x3 = 1

| * * | * *
x1 = 0, x2 = 2, x3 = 2

* * * * | |
x1 = 4, x2 = 0, x3 = 0
```

Total symbols:

```text
4 stars + 2 bars = 6
```

Choose bar positions:

```text
C(6, 2) = 15
```

Answer:

```text
15
```

---

## 16. Common Mistakes

### Mistake 1 — Confusing identical and distinct items

```text
Identical candies -> Stars and Bars
Distinct candies -> Different counting
```

### Mistake 2 — Forgetting positive constraint

Wrong:

```text
x1 + x2 + x3 = 5, xi >= 1
Answer = C(7, 2)
```

Correct:

```text
Answer = C(4, 2)
```

### Mistake 3 — Applying basic formula with upper bound

For:

```text
0 <= xi <= limit
```

Basic formula is not enough.

Need:

```text
inclusion-exclusion
```

### Mistake 4 — Using permutation formula

Stars and Bars does not count order of individual items.

It counts final distribution.

---

## 17. Decision Tree

```text
Counting problem?
|
+-- Are items identical?
|   |
|   +-- No -> Not basic Stars and Bars
|   |
|   +-- Yes
|       |
|       +-- Are boxes/groups distinct?
|           |
|           +-- No -> Different partition problem
|           |
|           +-- Yes
|               |
|               +-- Is it x1 + x2 + ... + xk = n?
|                   |
|                   +-- xi >= 0
|                   |   |
|                   |   +-- Answer = C(n+k-1, k-1)
|                   |
|                   +-- xi >= 1
|                   |   |
|                   |   +-- Answer = C(n-1, k-1)
|                   |
|                   +-- xi >= low
|                   |   |
|                   |   +-- Reduce n by low*k
|                   |
|                   +-- xi <= limit
|                       |
|                       +-- Use inclusion-exclusion / DP
```

---

## 18. Real World Mapping

| Real World Scenario | Stars and Bars Mapping |
|---|---|
| Distribute budget across departments | `x1 + x2 + ... + xk = budget` |
| Allocate identical servers to services | `xi >= 0` |
| Password character count distribution | count of each character type |
| Inventory distribution | items into warehouses |
| Choose products with repetition | combination with repetition |
| Split workload units among workers | non-negative distribution |
| Resource allocation with minimum quota | `xi >= low` |
| Capacity-limited allocation | upper bound + inclusion-exclusion |

---

## 19. Complexity

For direct formula:

```text
O(1)
```

if `nCr` is precomputed.

For modular precomputation:

```text
Build factorials: O(MAXN)
Each query: O(1)
```

For inclusion-exclusion upper bound:

```text
O(k)
```

---

## 20. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1'000'000'007;
const int MAXN = 1'000'000;

long long fact[MAXN + 1];
long long invFact[MAXN + 1];

long long modPow(long long a, long long e) {
    long long res = 1;

    while (e > 0) {
        if (e & 1) res = (res * a) % MOD;
        a = (a * a) % MOD;
        e >>= 1;
    }

    return res;
}

void buildFactorials() {
    fact[0] = 1;

    for (int i = 1; i <= MAXN; i++) {
        fact[i] = (fact[i - 1] * i) % MOD;
    }

    invFact[MAXN] = modPow(fact[MAXN], MOD - 2);

    for (int i = MAXN - 1; i >= 0; i--) {
        invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
    }
}

long long nCrMod(int n, int r) {
    if (r < 0 || r > n) return 0;

    return (((fact[n] * invFact[r]) % MOD) * invFact[n - r]) % MOD;
}

// x1 + x2 + ... + xk = n, xi >= 0
long long nonNegativeSolutions(int n, int k) {
    return nCrMod(n + k - 1, k - 1);
}

// x1 + x2 + ... + xk = n, xi >= 1
long long positiveSolutions(int n, int k) {
    if (n < k) return 0;
    return nCrMod(n - 1, k - 1);
}

// x1 + x2 + ... + xk = n, xi >= low
long long atLeastLowSolutions(int n, int k, int low) {
    int remaining = n - low * k;

    if (remaining < 0) return 0;

    return nonNegativeSolutions(remaining, k);
}

// x1 + ... + xk = n, 0 <= xi <= limit
long long upperBoundSolutions(int n, int k, int limit) {
    long long ans = 0;

    for (int j = 0; j <= k; j++) {
        int remaining = n - j * (limit + 1);

        if (remaining < 0) continue;

        long long term = nCrMod(k, j) *
                         nCrMod(remaining + k - 1, k - 1) % MOD;

        if (j % 2 == 0) ans = (ans + term) % MOD;
        else ans = (ans - term + MOD) % MOD;
    }

    return ans;
}

int main() {
    buildFactorials();

    cout << nonNegativeSolutions(5, 3) << "\n"; // 21
    cout << positiveSolutions(5, 3) << "\n";    // 6
    cout << atLeastLowSolutions(10, 3, 2) << "\n"; // 15
    cout << upperBoundSolutions(5, 3, 2) << "\n";  // 3

    return 0;
}
```

---

## 21. CP / FAANG Problem Forms

---

### Problem 1 — Number of Non-Negative Integer Solutions

#### Problem

Count the number of solutions:

```text
x1 + x2 + ... + xk = n
xi >= 0
```

#### Pattern

```text
Stars and Bars direct formula
```

#### Step-by-Step Working

Example:

```text
n = 7
k = 4
```

Step 1:

```text
We need distribute 7 identical units into 4 variables
```

Step 2:

```text
Use 7 stars
```

Step 3:

```text
Need 3 bars to split into 4 groups
```

Step 4:

```text
Total positions = 7 + 3 = 10
```

Step 5:

```text
Choose 3 bar positions
```

Answer:

```text
C(10, 3) = 120
```

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        // Multiply next numerator term and divide by denominator.
        // This works for small values without modulo.
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 7; // total sum
    int k = 4; // number of variables

    // Formula:
    // x1 + x2 + ... + xk = n, xi >= 0
    // ways = C(n + k - 1, k - 1)
    long long ways = nCr(n + k - 1, k - 1);

    cout << ways << endl;
    return 0;
}
```

---

### Problem 2 — Number of Positive Integer Solutions

#### Problem

Count the number of solutions:

```text
x1 + x2 + ... + xk = n
xi >= 1
```

#### Pattern

```text
Reserve 1 for each variable, then apply Stars and Bars
```

#### Step-by-Step Working

Example:

```text
n = 9
k = 4
```

Step 1:

```text
Give 1 to each variable
```

Used:

```text
4
```

Step 2:

```text
Remaining = 9 - 4 = 5
```

Step 3:

```text
Now solve:
y1 + y2 + y3 + y4 = 5
yi >= 0
```

Step 4:

```text
Answer = C(5 + 4 - 1, 4 - 1)
       = C(8, 3)
       = 56
```

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 9;
    int k = 4;

    // If n < k, we cannot give at least 1 to every variable.
    if (n < k) {
        cout << 0 << endl;
        return 0;
    }

    // Formula:
    // positive solutions = C(n - 1, k - 1)
    long long ways = nCr(n - 1, k - 1);

    cout << ways << endl;
    return 0;
}
```

---

### Problem 3 — Distribute Candies With Minimum Quota

#### Problem

There are `n` identical candies and `k` children.

Each child must get at least `m` candies.

Count the number of valid distributions.

#### Pattern

```text
Lower bound transformation
```

#### Step-by-Step Working

Example:

```text
n = 20
k = 5
m = 2
```

Step 1:

```text
Give 2 candies to each child
```

Used:

```text
5 * 2 = 10
```

Step 2:

```text
Remaining = 20 - 10 = 10
```

Step 3:

```text
Distribute remaining 10 candies freely among 5 children
```

Equation:

```text
y1 + y2 + y3 + y4 + y5 = 10
yi >= 0
```

Step 4:

```text
Answer = C(10 + 5 - 1, 5 - 1)
       = C(14, 4)
       = 1001
```

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 20; // total candies
    int k = 5;  // children
    int m = 2;  // minimum candies per child

    // First reserve m candies for every child.
    int remaining = n - k * m;

    // If remaining is negative, even minimum quota is impossible.
    if (remaining < 0) {
        cout << 0 << endl;
        return 0;
    }

    // Now distribute remaining candies freely.
    long long ways = nCr(remaining + k - 1, k - 1);

    cout << ways << endl;
    return 0;
}
```

---

### Problem 4 — Choose Items With Repetition

#### Problem

You have `n` item types.

You need to choose exactly `r` items.

Each type can be chosen multiple times.

Count the number of ways.

#### Pattern

```text
Combination with repetition
```

#### Step-by-Step Working

Example:

```text
n = 4 item types
r = 6 chosen items
```

Step 1:

```text
Let xi = how many items chosen from type i
```

Step 2:

```text
x1 + x2 + x3 + x4 = 6
xi >= 0
```

Step 3:

```text
Answer = C(n + r - 1, r)
       = C(4 + 6 - 1, 6)
       = C(9, 6)
       = 84
```

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 4; // item types
    int r = 6; // number of selected items

    // Choosing r items from n types with repetition:
    // x1 + x2 + ... + xn = r
    // xi >= 0
    long long ways = nCr(n + r - 1, r);

    cout << ways << endl;
    return 0;
}
```

---

### Problem 5 — Bounded Candies Distribution

#### Problem

Count solutions:

```text
x1 + x2 + ... + xk = n
0 <= xi <= limit
```

#### Pattern

```text
Stars and Bars + Inclusion-Exclusion
```

#### Step-by-Step Working

Example:

```text
n = 8
k = 4
limit = 3
```

Step 1:

```text
Count all non-negative solutions without upper bound
```

```text
C(8 + 4 - 1, 4 - 1)
= C(11, 3)
= 165
```

Step 2:

```text
Subtract cases where at least one xi >= 4
```

For one bad variable:

```text
remaining = 8 - 4 = 4
ways = C(4 + 4 - 1, 4 - 1)
     = C(7, 3)
     = 35
```

Choose bad variable:

```text
C(4, 1) = 4
```

Subtract:

```text
4 * 35 = 140
```

Step 3:

```text
Add back cases where two variables are bad
```

Two bad variables need:

```text
4 + 4 = 8
```

Remaining:

```text
0
```

Ways:

```text
C(0 + 4 - 1, 4 - 1)
= C(3, 3)
= 1
```

Choose two bad variables:

```text
C(4, 2) = 6
```

Add:

```text
6 * 1 = 6
```

Step 4:

```text
Answer = 165 - 140 + 6 = 31
```

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long nCr(int n, int r) {
    if (r < 0 || r > n) return 0;

    r = min(r, n - r);
    long long ans = 1;

    for (int i = 1; i <= r; i++) {
        ans = ans * (n - r + i) / i;
    }

    return ans;
}

int main() {
    int n = 8;
    int k = 4;
    int limit = 3;

    long long ans = 0;

    // Inclusion-exclusion:
    // j = number of variables violating xi <= limit
    for (int j = 0; j <= k; j++) {
        // If a variable violates limit, it has at least limit + 1.
        int remaining = n - j * (limit + 1);

        if (remaining < 0) continue;

        // Choose which j variables are bad.
        long long chooseBad = nCr(k, j);

        // Count ways after reducing those bad variables by limit + 1.
        long long ways =
            nCr(remaining + k - 1, k - 1);

        // Inclusion-exclusion sign.
        if (j % 2 == 0) ans += chooseBad * ways;
        else ans -= chooseBad * ways;
    }

    cout << ans << endl; // 31
    return 0;
}
```

---

## 22. Practice Checklist

Before solving a problem, ask:

```text
1. Are items identical?
2. Are groups distinct?
3. Is the equation sum fixed?
4. Are variables non-negative?
5. Are variables positive?
6. Is there a lower bound?
7. Is there an upper bound?
8. Does repetition mean count per type?
9. Is answer large and modulo needed?
10. Is inclusion-exclusion required?
```

---

## 23. Next Step

Next MiniMathEngine file:

```text
014_Catalan_Numbers.md
```

This topic is used in:

```text
balanced parentheses
BST count
non-crossing chords
valid mountain structures
stack-valid sequences
```
