# 016_Derangements.md — MiniMathEngine

# Derangements

> Derangement is the counting pattern for permutations where **no element stays in its original position**.

---

## Clickable Index

1. [What Is Derangement?](#1-what-is-derangement)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Intuition](#3-core-intuition)
4. [Derangement Formula](#4-derangement-formula)
5. [Recursive Formula](#5-recursive-formula)
6. [DP Formula](#6-dp-formula)
7. [Inclusion-Exclusion View](#7-inclusion-exclusion-view)
8. [Derangement Sequence](#8-derangement-sequence)
9. [When To Use Derangement](#9-when-to-use-derangement)
10. [Problem Form 1 — Secret Santa](#10-problem-form-1--secret-santa)
11. [Problem Form 2 — No Fixed Point Permutation](#11-problem-form-2--no-fixed-point-permutation)
12. [Problem Form 3 — Hat Check Problem](#12-problem-form-3--hat-check-problem)
13. [Problem Form 4 — Forbidden Position Counting](#13-problem-form-4--forbidden-position-counting)
14. [Problem Form 5 — Probability Of No Match](#14-problem-form-5--probability-of-no-match)
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

## 1. What Is Derangement?

A derangement is a permutation where:

```text
no element appears in its original position
```

Example:

Original:

```text
1 2 3
```

Valid derangements:

```text
2 3 1
3 1 2
```

Invalid permutations:

```text
1 3 2  -> 1 is still in position 1
2 1 3  -> 3 is still in position 3
3 2 1  -> 2 is still in position 2
```

So for `n = 3`:

```text
!3 = 2
```

---

## 2. Why This Topic Matters

Derangement appears in:

- permutation counting
- forbidden positions
- Secret Santa problems
- probability of mismatch
- inclusion-exclusion
- combinatorics
- matching problems
- assignment problems
- randomized algorithms

It is a very common CP/FAANG counting pattern when the statement says:

```text
Nobody gets their own item
```

or:

```text
No element remains fixed
```

---

## 3. Core Intuition

Normal permutations:

```text
n!
```

Derangements are stricter:

```text
permutations with zero fixed points
```

A fixed point means:

```text
position i contains element i
```

Derangement means:

```text
for every i:
perm[i] != i
```

---

## 4. Derangement Formula

The inclusion-exclusion formula:

```text
!n = n! * (1 - 1/1! + 1/2! - 1/3! + ... + (-1)^n / n!)
```

Equivalent:

```text
!n = Σ (-1)^k * C(n,k) * (n-k)!
```

for:

```text
k = 0 to n
```

Meaning:

```text
choose k people who are fixed
subtract/add using inclusion-exclusion
```

---

## 5. Recursive Formula

Derangement recurrence:

```text
D(n) = (n - 1) * (D(n - 1) + D(n - 2))
```

Base cases:

```text
D(0) = 1
D(1) = 0
D(2) = 1
```

---

## 6. DP Formula

We can compute derangements bottom-up:

```text
dp[0] = 1
dp[1] = 0

dp[n] = (n - 1) * (dp[n - 1] + dp[n - 2])
```

This is the most common CP implementation.

---

## 7. Inclusion-Exclusion View

Let:

```text
Ai = permutations where person i gets own item
```

We need:

```text
total permutations - permutations with at least one fixed point
```

Using inclusion-exclusion:

```text
D(n)
=
n!
- C(n,1)(n-1)!
+ C(n,2)(n-2)!
- C(n,3)(n-3)!
+ ...
```

---

## 8. Derangement Sequence

```text
n : !n

0 : 1
1 : 0
2 : 1
3 : 2
4 : 9
5 : 44
6 : 265
7 : 1854
8 : 14833
9 : 133496
10: 1334961
```

---

## 9. When To Use Derangement

Use derangement when you see:

```text
no one gets own item
no fixed point
no element remains in same position
wrong envelope problem
hat check problem
Secret Santa
permutation with forbidden self-match
```

---

## 10. Problem Form 1 — Secret Santa

### Problem

There are `n` people.

Each person gives a gift to exactly one person.

Nobody can give a gift to themselves.

Count the number of valid assignments.

---

### Mapping

This is:

```text
derangement of n people
```

Answer:

```text
!n
```

---

### Step-by-Step Working

Example:

```text
n = 4
```

People:

```text
A B C D
```

Invalid if:

```text
A -> A
B -> B
C -> C
D -> D
```

Need no self assignment.

Derangement value:

```text
!4 = 9
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    int n = 4;

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        // Derangement recurrence:
        // D(i) = (i-1) * (D(i-1) + D(i-2))
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]);
    }

    cout << dp[n] << endl; // 9

    return 0;
}
```

---

## 11. Problem Form 2 — No Fixed Point Permutation

### Problem

Count permutations of numbers `1...n` such that:

```text
perm[i] != i
```

for every position `i`.

---

### Pattern

```text
direct derangement
```

---

### Step-by-Step Working

Example:

```text
n = 3
```

All permutations:

```text
123
132
213
231
312
321
```

Check each:

```text
123 -> invalid, all fixed
132 -> invalid, 1 fixed
213 -> invalid, 3 fixed
231 -> valid
312 -> valid
321 -> invalid, 2 fixed
```

Answer:

```text
2
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long derangement(int n) {

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]);
    }

    return dp[n];
}

int main() {

    int n = 3;

    cout << derangement(n) << endl; // 2

    return 0;
}
```

---

## 12. Problem Form 3 — Hat Check Problem

### Problem

`n` people give their hats to a counter.

The hats are returned randomly.

Count the number of ways such that nobody gets their own hat.

---

### Mapping

Each hat assignment is a permutation.

Nobody gets own hat means:

```text
no fixed point
```

Answer:

```text
!n
```

---

### Step-by-Step Working

For:

```text
n = 5
```

Derangement sequence:

```text
!5 = 44
```

Answer:

```text
44
```

---

## 13. Problem Form 4 — Forbidden Position Counting

### Problem

Count permutations where each item cannot go to its own original position.

This is a special case of:

```text
forbidden positions
```

If every item has exactly one forbidden position, its own position, then:

```text
derangement
```

---

### Step-by-Step Working

Example:

```text
items = [1,2,3,4]
positions = [1,2,3,4]
```

Forbidden:

```text
1 cannot go position 1
2 cannot go position 2
3 cannot go position 3
4 cannot go position 4
```

Answer:

```text
!4 = 9
```

---

## 14. Problem Form 5 — Probability Of No Match

### Problem

If `n` people randomly receive `n` hats, what is the probability nobody gets their own hat?

---

### Formula

```text
Probability = !n / n!
```

As `n` grows large:

```text
!n / n! ≈ 1/e
```

Approximately:

```text
0.367879...
```

---

### Step-by-Step Working

Example:

```text
n = 4
```

Total permutations:

```text
4! = 24
```

Derangements:

```text
!4 = 9
```

Probability:

```text
9 / 24 = 0.375
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long derangement(int n) {

    vector<long long> dp(n + 1);

    dp[0] = 1;
    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]);
    }

    return dp[n];
}

long long factorial(int n) {

    long long ans = 1;

    for (int i = 1; i <= n; i++) {
        ans *= i;
    }

    return ans;
}

int main() {

    int n = 4;

    long long good = derangement(n);
    long long total = factorial(n);

    double probability = (double)good / total;

    cout << fixed << setprecision(6) << probability << endl;

    return 0;
}
```

---

## 15. Dry Run

Compute:

```text
!4
```

Using recurrence:

```text
D(0)=1
D(1)=0
D(2)=1
```

Now:

```text
D(3) = (3-1) * (D(2)+D(1))
     = 2 * (1+0)
     = 2
```

Then:

```text
D(4) = (4-1) * (D(3)+D(2))
     = 3 * (2+1)
     = 9
```

Answer:

```text
!4 = 9
```

---

## 16. Common Mistakes

### Mistake 1 — Confusing derangement with permutation

Permutation:

```text
n!
```

Derangement:

```text
!n
```

Derangement is smaller because fixed positions are forbidden.

---

### Mistake 2 — Wrong base case

Correct:

```text
D(0)=1
D(1)=0
D(2)=1
```

Why `D(0)=1`?

Because empty assignment has one valid way.

---

### Mistake 3 — Using `n * D(n-1)`

Wrong:

```text
D(n) = n * D(n-1)
```

Correct:

```text
D(n) = (n - 1) * (D(n-1) + D(n-2))
```

---

### Mistake 4 — Forgetting modulo

For large `n`, answer grows very fast.

Use modulo in CP.

---

## 17. Decision Tree

```text
Permutation counting problem?
|
+-- Does order/assignment matter?
|   |
|   +-- Yes
|       |
|       +-- Is original position forbidden?
|           |
|           +-- Yes
|               |
|               +-- Is every item forbidden from its own position?
|                   |
|                   +-- Yes
|                       |
|                       +-- Derangement
|
+-- Secret Santa?
|
+-- Hat check?
|
+-- No fixed point?
|
+-- Nobody gets own item?
```

---

## 18. Real World Mapping

| Real World Scenario | Derangement Mapping |
|---|---|
| Secret Santa | nobody gifts themselves |
| Randomized assignments | no self assignment |
| Hat check problem | no owner gets own hat |
| Testing shuffle | avoid same original position |
| Load redistribution | no task stays on same worker |
| Security token rotation | user cannot receive same token |
| Interview scheduling | no interviewer gets own candidate |
| A/B assignment | no user remains in same bucket |

---

## 19. Complexity

DP method:

```text
Time: O(n)
Space: O(n)
```

Optimized DP:

```text
Time: O(n)
Space: O(1)
```

Inclusion-exclusion formula:

```text
Time: O(n)
```

---

## 20. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1'000'000'007;

// O(n) space derangement
long long derangementDP(int n) {

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {

        // D(i) = (i-1) * (D(i-1) + D(i-2))
        dp[i] = ((i - 1) * ((dp[i - 1] + dp[i - 2]) % MOD)) % MOD;
    }

    return dp[n];
}

// O(1) space derangement
long long derangementOptimized(int n) {

    if (n == 0) return 1;
    if (n == 1) return 0;

    long long d0 = 1;
    long long d1 = 0;
    long long d2 = 1;

    for (int i = 2; i <= n; i++) {

        d2 = ((i - 1) * ((d1 + d0) % MOD)) % MOD;

        d0 = d1;
        d1 = d2;
    }

    return d1;
}

int main() {

    int n = 5;

    cout << derangementDP(n) << endl;
    cout << derangementOptimized(n) << endl;

    return 0;
}
```

---

## 21. CP / FAANG Problem Forms

---

### Problem 1 — Count Secret Santa Assignments

#### Problem

There are `n` employees.

Each employee must give a gift to one other employee.

Nobody can give to themselves.

Count valid assignments.

---

#### Pattern

```text
Derangement
```

---

#### Step-by-Step Working

Example:

```text
n = 5
```

This is:

```text
!5
```

Using recurrence:

```text
D(3)=2
D(4)=9
D(5)=4*(9+2)=44
```

Answer:

```text
44
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1'000'000'007;

int main() {

    int n = 5;

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {

        // i-th person has (i-1) choices to swap/conflict resolve.
        // Recurrence covers two cases:
        // 1. pair swap
        // 2. chain placement
        dp[i] = ((i - 1) * ((dp[i - 1] + dp[i - 2]) % MOD)) % MOD;
    }

    cout << dp[n] << endl;

    return 0;
}
```

---

### Problem 2 — Count Permutations With No Fixed Point

#### Problem

Given `n`, count permutations `p` such that:

```text
p[i] != i
```

for all `i`.

---

#### Pattern

```text
Direct derangement
```

---

#### Step-by-Step Working

For:

```text
n = 4
```

Derangement sequence:

```text
!4 = 9
```

Answer:

```text
9
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long derangement(int n) {

    if (n == 0) return 1;
    if (n == 1) return 0;

    long long prev2 = 1; // D(0)
    long long prev1 = 0; // D(1)
    long long cur = 0;

    for (int i = 2; i <= n; i++) {

        // D(i) = (i-1) * (D(i-1) + D(i-2))
        cur = (i - 1) * (prev1 + prev2);

        prev2 = prev1;
        prev1 = cur;
    }

    return prev1;
}

int main() {

    int n = 4;

    cout << derangement(n) << endl;

    return 0;
}
```

---

### Problem 3 — Probability Nobody Gets Own Hat

#### Problem

`n` people randomly receive `n` hats.

Find probability that nobody gets their own hat.

---

#### Pattern

```text
Derangement / total permutation
```

---

#### Step-by-Step Working

For:

```text
n = 4
```

Total:

```text
4! = 24
```

Good:

```text
!4 = 9
```

Probability:

```text
9/24 = 0.375
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long factorial(int n) {

    long long ans = 1;

    for (int i = 1; i <= n; i++) {
        ans *= i;
    }

    return ans;
}

long long derangement(int n) {

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {
        dp[i] = (i - 1) * (dp[i - 1] + dp[i - 2]);
    }

    return dp[n];
}

int main() {

    int n = 4;

    long long good = derangement(n);
    long long total = factorial(n);

    double probability = (double)good / total;

    cout << fixed << setprecision(6) << probability << endl;

    return 0;
}
```

---

### Problem 4 — Derangement With Modulo

#### Problem

Count derangements of `n` modulo:

```text
1e9 + 7
```

---

#### Pattern

```text
DP + modulo
```

---

#### Step-by-Step Working

For large `n`, exact value overflows.

Use:

```text
dp[i] = (i-1) * (dp[i-1] + dp[i-2]) % MOD
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1'000'000'007;

int main() {

    int n;
    cin >> n;

    vector<long long> dp(n + 1);

    dp[0] = 1;

    if (n >= 1) dp[1] = 0;

    for (int i = 2; i <= n; i++) {

        long long sum = (dp[i - 1] + dp[i - 2]) % MOD;

        dp[i] = ((i - 1) * sum) % MOD;
    }

    cout << dp[n] << endl;

    return 0;
}
```

---

### Problem 5 — Inclusion-Exclusion Derangement

#### Problem

Count no fixed-point permutations using inclusion-exclusion.

---

#### Pattern

```text
n! - C(n,1)(n-1)! + C(n,2)(n-2)! - ...
```

---

#### Step-by-Step Working

For:

```text
n = 4
```

```text
D(4)
=
4!
- C(4,1)3!
+ C(4,2)2!
- C(4,3)1!
+ C(4,4)0!
```

Compute:

```text
24 - 24 + 12 - 4 + 1 = 9
```

Answer:

```text
9
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fact(int n) {

    long long ans = 1;

    for (int i = 1; i <= n; i++) {
        ans *= i;
    }

    return ans;
}

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

    int n = 4;

    long long ans = 0;

    for (int fixed = 0; fixed <= n; fixed++) {

        // choose people who are fixed
        long long chooseFixed = nCr(n, fixed);

        // remaining people can be permuted freely
        long long remainingPermutations = fact(n - fixed);

        long long term = chooseFixed * remainingPermutations;

        if (fixed % 2 == 0) ans += term;
        else ans -= term;
    }

    cout << ans << endl;

    return 0;
}
```

---

## 22. Practice Checklist

Before solving:

```text
1. Is this a permutation problem?
2. Is original position forbidden?
3. Does statement say nobody gets own item?
4. Does statement say no fixed point?
5. Is it Secret Santa / hat check?
6. Need exact answer or modulo?
7. Is n small enough for direct factorial?
8. Should I use DP recurrence?
9. Should I use inclusion-exclusion?
10. Is probability asked?
```

---

## 23. Next Step

```text
017_CRT.md
```

CRT is used for:

```text
solving multiple modulo equations
calendar cycles
clock problems
distributed hashing patterns
number theory reconstruction
```
