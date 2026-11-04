# 003 — Dynamic Programming Introduction 2
## Complexity → Recursive vs Iterative DP → Converting Top-Down to Bottom-Up → General DP Recipe

> **Goal:** Continue directly from **DP Introduction 1** and learn how to analyze DP correctly, how to choose recursive vs iterative DP, and how to construct a DP solution in the exact order:
>
> **State → Transition → Base Case → Final Subproblem**

---

## Table of Contents

1. [Prerequisites Before This Note](#1-prerequisites-before-this-note)
2. [Quick Recall: What Memoization Changed](#2-quick-recall-what-memoization-changed)
3. [The Main DP Complexity Formula](#3-the-main-dp-complexity-formula)
4. [Fibonacci Complexity Step by Step](#4-fibonacci-complexity-step-by-step)
5. [Grid DP Complexity Step by Step](#5-grid-dp-complexity-step-by-step)
6. [How to Count the Number of States](#6-how-to-count-the-number-of-states)
7. [How to Find Transition Time](#7-how-to-find-transition-time)
8. [Complexity Example 1 — 2D State, O(1) Transition](#8-complexity-example-1--2d-state-o1-transition)
9. [Complexity Example 2 — 3D State, O(i) Transition](#9-complexity-example-2--3d-state-oi-transition)
10. [Estimate vs Exact Complexity](#10-estimate-vs-exact-complexity)
11. [Complexity Example 3 — Logarithmic Transition](#11-complexity-example-3--logarithmic-transition)
12. [Variable Transition Example — O(n / i)](#12-variable-transition-example--oni)
13. [Prime-Count Transition Example](#13-prime-count-transition-example)
14. [Factor-Count Transition Example](#14-factor-count-transition-example)
15. [Recursive DP vs Iterative DP](#15-recursive-dp-vs-iterative-dp)
16. [How Recursive DP Decides the Order Automatically](#16-how-recursive-dp-decides-the-order-automatically)
17. [How Iterative DP Must Choose the Correct Order](#17-how-iterative-dp-must-choose-the-correct-order)
18. [Convert Fibonacci from Recursive to Iterative](#18-convert-fibonacci-from-recursive-to-iterative)
19. [Convert Grid DP from Recursive to Iterative](#19-convert-grid-dp-from-recursive-to-iterative)
20. [General Technique to Solve Any DP Problem](#20-general-technique-to-solve-any-dp-problem)
21. [A Complete Worked DP Formulation](#21-a-complete-worked-dp-formulation)
22. [How to Read a New DP Problem](#22-how-to-read-a-new-dp-problem)
23. [Common Mistakes](#23-common-mistakes)
24. [Final Mental Model](#24-final-mental-model)

---

# 1. Prerequisites Before This Note

This lecture assumes the ideas from DP Introduction 1.

You do **not** need advanced mathematics.

You only need the following concepts.

---

## 1.1 Recursion

A recursive function solves a large problem using smaller versions of the same problem.

Example:

```text
fib(n)
    ↓
fib(n-1) + fib(n-2)
```

The smaller states must eventually reach a base case.

```text
fib(2) = 1
fib(1) = 1
```

---

## 1.2 State

A **state** is the exact subproblem that we want to solve.

Example:

```text
dp[i] = ith Fibonacci number
```

Here the state is uniquely identified by:

```text
i
```

Grid example:

```text
dp[i][j]
= answer for grid position (i,j)
```

Here the state is identified by:

```text
(i, j)
```

---

## 1.3 Transition

A **transition** explains how one state is calculated from already-defined smaller states.

Fibonacci:

```text
dp[i] = dp[i-1] + dp[i-2]
```

Grid path count:

```text
dp[i][j]
= dp[i+1][j] + dp[i][j+1]
```

---

## 1.4 Memoization

Memoization means:

```text
solve a state once
      ↓
store its answer
      ↓
reuse it later
```

So repeated recursion:

```text
        f(5)
       /    \
    f(4)    f(3)
    /  \
 f(3) f(2)
```

does **not** mean we repeatedly recompute `f(3)` after memoization.

---

## 1.5 Big-O basics

You should recognize:

```text
O(1)       constant
O(log n)   logarithmic
O(n)       linear
O(n log n)
O(n²)
O(nm)
```

The lecture's central DP complexity rule will combine:

```text
number of states
```

and:

```text
work required to calculate one state
```

---

## 1.6 Tiny summation facts

We will use a few simple sums.

### Arithmetic sum

```text
1 + 2 + 3 + ... + n
= n(n+1) / 2
= O(n²)
```

### Harmonic sum

```text
1 + 1/2 + 1/3 + ... + 1/n
= O(log n)
```

Therefore:

```text
n/1 + n/2 + n/3 + ... + n/n
= n(1 + 1/2 + ... + 1/n)
= O(n log n)
```

These appear when different DP states have different transition costs.

---

# 2. Quick Recall: What Memoization Changed

Suppose a recursion tree repeatedly reaches the same states.

Without memoization:

```text
                         P
                    /    |    \
                  P1     P2     P3
                / | \   /|\    /|\
              P4 P5 P6 P6 P7  P7 P8 ...
```

Notice:

```text
P6 appears again
P7 appears again
```

Naive recursion solves them repeatedly.

Memoization changes the model:

```text
P6 → solve once → store answer
P7 → solve once → store answer
```

After that, every repeated request is just a lookup.

So when analyzing memoized DP, do **not** count all recursion-tree nodes.

Instead count:

```text
unique subproblems
=
unique DP states
```

---

# 3. The Main DP Complexity Formula

The lecture gives two useful ways to think about time complexity.

## 3.1 Fast estimate

```text
Time Complexity
≈
Number of States
×
Transition Time for one state
```

This works well when every state takes roughly the same amount of work.

---

## 3.2 Exact viewpoint

When different states require different transition times:

```text
Total Time
=
sum of transition time over all states
```

In mathematical form:

```text
T = Σ transitionTime(state)
```

This is more accurate.

---

## 3.3 Space complexity

The lecture's basic DP-table estimate is:

```text
Space Complexity
=
Number of States
×
Space required per state
```

Usually one state stores one integer / long long:

```text
space per state = O(1)
```

Therefore in many DP problems:

```text
space ≈ number of states
```

Examples:

```text
dp[n]       → O(n)
dp[n][m]    → O(nm)
dp[n][m][k] → O(nmk)
```

For recursive DP, also remember the recursion stack can use extra memory.

---

# 4. Fibonacci Complexity Step by Step

Recall:

```text
dp[i] = ith Fibonacci number
```

Transition:

```text
dp[i] = dp[i-1] + dp[i-2]
```

---

## Step 1 — Count states

Possible states are:

```text
dp[1]
dp[2]
dp[3]
...
dp[n]
```

Number of states:

```text
n
```

So:

```text
#states = O(n)
```

---

## Step 2 — Work for one state

For each `dp[i]`:

```text
read dp[i-1]
read dp[i-2]
add them
store answer
```

That is constant work.

```text
transition time = O(1)
```

---

## Step 3 — Multiply

```text
Time
=
O(n) × O(1)
=
O(n)
```

Space for the table:

```text
O(n)
```

---

## Visual model

```text
state          work

dp[1]    →     O(1)
dp[2]    →     O(1)
dp[3]    →     O(1)
  .
  .
  .
dp[n]    →     O(1)

n states × constant work
        =
       O(n)
```

This is why memoized Fibonacci becomes linear even though the original recursion tree was huge.

---

# 5. Grid DP Complexity Step by Step

Suppose:

```text
dp[i][j]
```

represents a subproblem for one grid cell.

For an `n × m` grid:

```text
i = 0 ... n-1
j = 0 ... m-1
```

---

## Step 1 — Count states

Every pair `(i,j)` identifies one state.

```text
row 0:   m states
row 1:   m states
...
row n-1: m states
```

Therefore:

```text
#states = n × m
```

So:

```text
O(nm)
```

---

## Step 2 — Transition work

Example transition:

```text
dp[i][j]
= grid[i][j]
  + min(dp[i+1][j], dp[i][j+1])
```

For one state we do:

```text
2 table accesses
1 min
1 addition
```

Constant work:

```text
O(1)
```

---

## Step 3 — Total

```text
Time
=
O(nm) × O(1)
=
O(nm)
```

Space:

```text
O(nm)
```

---

# 6. How to Count the Number of States

This is one of the most important DP skills.

Look at the parameters that identify `dp`.

---

## 6.1 One parameter

```text
dp[i]
1 <= i <= n
```

Possible values of `i`:

```text
n
```

Therefore:

```text
#states = O(n)
```

---

## 6.2 Two parameters

```text
dp[i][j]

1 <= i <= n
1 <= j <= m
```

For every `i`, there are `m` choices of `j`.

```text
n × m
```

Therefore:

```text
#states = O(nm)
```

---

## 6.3 Three parameters

```text
dp[i][j][k]

1 <= i <= n
1 <= j <= m
1 <= k <= 10
```

Number of possible combinations:

```text
n × m × 10
```

Therefore:

```text
#states = O(nm)
```

because `10` is a constant.

But conceptually keep:

```text
n × m × 10
```

until the final simplification.

---

## 6.4 General rule

If:

```text
dp[a][b][c]
```

and:

```text
a has A possible values
b has B possible values
c has C possible values
```

then:

```text
#states = A × B × C
```

provided all combinations are possible/relevant.

---

# 7. How to Find Transition Time

After counting the states, look at the recurrence.

Ask:

> **How much work does it take to calculate one state if all states on the right-hand side are already known?**

This phrase is important.

Do **not** recursively re-count the cost of those already-computed states.

---

## Example A

```text
dp[i]
= dp[i-1] + dp[i-2]
```

Right-side values are already known.

Work:

```text
2 reads + 1 addition
```

So:

```text
O(1)
```

---

## Example B

```text
dp[i]
= max(
    dp[1],
    dp[2],
    ...
    dp[i-1]
)
```

To calculate `dp[i]`, scan approximately `i` previous states.

So:

```text
transition time = O(i)
```

Worst case:

```text
O(n)
```

---

## Example C

```text
dp[i]
= dp[i/2] + dp[i/4] + dp[i/8] + ...
```

Denominators double:

```text
2, 4, 8, 16, ...
```

How many until the index becomes zero?

Approximately:

```text
log₂(i)
```

So:

```text
transition time = O(log i)
```

---

# 8. Complexity Example 1 — 2D State, O(1) Transition

Lecture example:

```text
State:
dp[i][j]

1 <= i <= n
1 <= j <= m
```

Transition:

```text
dp[i][j]
= dp[i-1][j] + dp[i][j-1]
```

---

## Step 1 — States

```text
n choices for i
m choices for j
```

Therefore:

```text
#states = nm
```

---

## Step 2 — Transition

One addition of two known values:

```text
O(1)
```

---

## Step 3 — Complexity

```text
T = nm × O(1)
  = O(nm)
```

Space:

```text
O(nm)
```

---

# 9. Complexity Example 2 — 3D State, O(i) Transition

Lecture state:

```text
dp[i][j][k]

1 <= i <= n
1 <= j <= m
1 <= k <= 10
```

Transition concept:

```text
dp[i][j][k]
=
dp[i-1][j][k]
+ dp[i-2][j][k]
+ ...
+ dp[1][j][k]
```

---

## 9.1 Count states

```text
n × m × 10
```

So:

```text
#states = O(nm)
```

with the constant `10` suppressed.

---

## 9.2 Transition for one state

For fixed `(i,j,k)`:

```text
dp[i][j][k]
```

uses:

```text
i - 1
```

previous values.

So:

```text
transition time = O(i)
```

Worst case:

```text
O(n)
```

---

## 9.3 Quick estimate

```text
#states × maximum transition time

= (n × m × 10) × n

= O(mn²)
```

---

## 9.4 Exact summation

Not every state costs `n`.

For:

```text
i = 1 → about 1 work
i = 2 → about 2 work
...
i = n → about n work
```

For every `i`, there are:

```text
m × 10
```

choices for `(j,k)`.

Therefore:

```text
Total
=
m × 10 × (1 + 2 + 3 + ... + n)
```

Use:

```text
1 + 2 + ... + n
= n(n+1)/2
= O(n²)
```

So:

```text
Total
=
O(mn²)
```

Here the quick estimate and exact asymptotic result agree.

---

# 10. Estimate vs Exact Complexity

This distinction is important.

## Estimate

Use:

```text
#states × worst transition time
```

This is usually fast and safe.

---

## Exact

Use:

```text
Σ transition time of each state
```

This can produce a tighter complexity when transition cost changes greatly from state to state.

---

## Example

Suppose:

```text
dp[1] costs O(1)
dp[2] costs O(2)
...
dp[n] costs O(n)
```

Quick estimate:

```text
n states × O(n)
= O(n²)
```

Exact:

```text
1 + 2 + ... + n
= O(n²)
```

Same result.

But this will not always happen.

---

# 11. Complexity Example 3 — Logarithmic Transition

Lecture transition:

```text
dp[i]
=
dp[i/2]
+ dp[i/4]
+ dp[i/8]
+ ...
```

---

## 11.1 How many terms?

For `i = 32`:

```text
32/2  = 16
32/4  = 8
32/8  = 4
32/16 = 2
32/32 = 1
```

Only about:

```text
log₂(32) = 5
```

terms.

Therefore:

```text
transition(dp[i]) = O(log i)
```

---

## 11.2 Quick estimate

There are:

```text
n states
```

Worst transition:

```text
O(log n)
```

So:

```text
O(n log n)
```

---

## 11.3 Exact viewpoint

Total:

```text
log 1 + log 2 + ... + log n
```

Using:

```text
log a + log b = log(ab)
```

we get:

```text
log(1 × 2 × 3 × ... × n)
=
log(n!)
```

And:

```text
log(n!) = O(n log n)
```

Therefore again:

```text
T = O(n log n)
```

---

# 12. Variable Transition Example — O(n / i)

Lecture example:

```text
State:
dp[i], 1 <= i <= n

Transition time:
O(n / i)
```

The transition cost changes with `i`.

---

## 12.1 Loose estimate

Number of states:

```text
n
```

Maximum transition time occurs near `i = 1`:

```text
O(n)
```

So a very loose estimate is:

```text
O(n²)
```

But we can do better using exact total work.

---

## 12.2 Exact total

```text
T
=
n/1 + n/2 + n/3 + ... + n/n
```

Factor out `n`:

```text
T
=
n(1 + 1/2 + 1/3 + ... + 1/n)
```

The harmonic sum is:

```text
O(log n)
```

Therefore:

```text
T = O(n log n)
```

This is an important lesson:

> When transition work varies by state, summing the actual work can be much tighter than multiplying by the worst case.

---

# 13. Prime-Count Transition Example

Lecture example:

```text
State:
dp[i], 1 <= i <= n
```

Transition work:

```text
O(number of primes <= i)
```

---

## What should you do in a contest?

First write the exact structure:

```text
T
=
Σ countPrimes(i)
```

for:

```text
i = 1 ... n
```

If you only need a safe upper bound:

```text
number of primes <= i
<= i
```

So:

```text
transition(i) <= O(i)
```

Then:

```text
T
<= 1 + 2 + ... + n
= O(n²)
```

This is a valid upper-bound analysis without requiring advanced prime-number theory.

The key lesson from the lecture is not the prime theorem.

It is:

```text
transition time can depend on the current state
```

and therefore:

```text
sum the work when needed
```

---

# 14. Factor-Count Transition Example

Lecture example:

```text
State:
dp[i], 1 <= i <= n
```

Transition work:

```text
O(number of factors of i)
```

Let:

```text
d(i) = number of divisors/factors of i
```

Then the exact work expression is:

```text
T = Σ d(i)
```

for:

```text
i = 1 ... n
```

---

## 14.1 Easy per-state upper bound

A factor larger than `sqrt(i)` is paired with a factor smaller than `sqrt(i)`.

Example:

```text
36

1 × 36
2 × 18
3 × 12
4 × 9
6 × 6
```

So divisor enumeration by factor pairs can be done in:

```text
O(sqrt(i))
```

A simple DP estimate is therefore:

```text
n states × O(sqrt(n))
=
O(n sqrt(n))
```

For this introductory lecture, the important modelling step is:

```text
#states = n

transition(i)
depends on number of factors of i
```

If a later number-theory problem needs a tighter total bound, analyze the sum separately.

---

# 15. Recursive DP vs Iterative DP

DP can be implemented in two main ways.

```text
Recursive DP
=
Top-Down + Memoization
```

```text
Iterative DP
=
Bottom-Up + Table Filling
```

---

## 15.1 Comparison

| Idea | Recursive / Top-Down | Iterative / Bottom-Up |
|---|---|---|
| How states are reached | Recursion decides naturally | We choose loop order |
| Memo/table | Cache states when needed | Fill table directly |
| Runtime overhead | More due to function calls | Usually lower |
| State order | Usually no need to manually derive | Must be valid |
| Unnecessary states | May avoid unreachable states | Standard loops often fill all states |
| Optimization | Often harder | Often easier |
| Stack depth | Uses recursion stack | No recursion stack |

---

## Main practical point

Recursive DP is often easier when learning or formulating a new DP.

Why?

Because after writing the recurrence:

```text
f(i)
= ...
f(smaller state)
...
```

recursion automatically goes to dependencies first.

In iterative DP, **you must manually choose an order** that guarantees dependencies are already available.

---

# 16. How Recursive DP Decides the Order Automatically

Suppose:

```text
f(n)
=
f(n-1) + f(n-2)
```

Recursive execution naturally behaves like:

```text
f(n)
│
├── need f(n-1)
│   │
│   ├── need smaller states
│   └── calculate f(n-1)
│
├── need f(n-2)
│
└── calculate f(n)
```

You never manually say:

```text
calculate f(1)
then f(2)
then ...
```

The call stack handles the dependency flow.

---

## Dependency picture

```text
f(6)
↑
depends on

f(5), f(4)
↑
depend on

f(4), f(3), f(2)
↑
...
```

Memoization ensures each state is actually calculated once.

---

# 17. How Iterative DP Must Choose the Correct Order

This is the lecture's conversion rule:

> **All states that a particular state depends on must be evaluated before that state.**

This is the entire idea behind bottom-up ordering.

---

## Dependency arrow

If:

```text
dp[current]
depends on
dp[previous]
```

then:

```text
previous must be computed first
```

Think:

```text
dependency  →  dependent
```

not the other way around.

---

# 18. Convert Fibonacci from Recursive to Iterative

State:

```text
dp[i] = ith Fibonacci number
```

Transition:

```text
dp[i] = dp[i-1] + dp[i-2]
```

To compute `dp[i]`, we need:

```text
dp[i-1]
dp[i-2]
```

Therefore lower indices must come first.

Correct order:

```text
dp[1]
dp[2]
dp[3]
dp[4]
...
dp[n]
```

---

## 18.1 Bottom-up visual

```text
dp[1]   dp[2]   dp[3]   dp[4]   dp[5]   dp[6]
  1       1
   \     /
    \   /
    dp[3]
      \    dp[2]
       \   /
       dp[4]
         ...
```

Simpler:

```text
1 → 2 → 3 → 4 → 5 → ... → n
```

because every state depends only on smaller indices.

---

## 18.2 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    int n;
    cin >> n;

    vector<ll> dp(n + 1);

    dp[1] = 1;

    if (n >= 2) {
        dp[2] = 1;
    }

    for (int i = 3; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }

    cout << dp[n] << '\n';
    return 0;
}
```

---

## 18.3 Compact dry run: `n = 6`

```text
dp[1] = 1
dp[2] = 1

i=3 → dp[3] = dp[2] + dp[1] = 1 + 1 = 2
i=4 → dp[4] = dp[3] + dp[2] = 2 + 1 = 3
i=5 → dp[5] = dp[4] + dp[3] = 3 + 2 = 5
i=6 → dp[6] = dp[5] + dp[4] = 5 + 3 = 8

answer = 8
```

---

# 19. Convert Grid DP from Recursive to Iterative

This is where loop direction becomes important.

Suppose our state is:

```text
dp[i][j]
= number of paths from (i,j)
  to (n-1,m-1)
```

Allowed moves:

```text
DOWN
RIGHT
```

Transition:

```text
dp[i][j]
=
dp[i+1][j]
+
dp[i][j+1]
```

---

## 19.1 Dependency picture

For cell `X = (i,j)`:

```text
            X = (i,j)
             /      \
            v        v
       (i+1,j)    (i,j+1)
          DOWN       RIGHT
```

Before computing `X`, we need:

```text
cell below X
cell right of X
```

Therefore bottom and right states must already exist.

---

## 19.2 Which corner should be calculated first?

Destination:

```text
(n-1, m-1)
```

is the natural base case.

So fill from:

```text
bottom-right
```

toward:

```text
top-left
```

---

## 19.3 Visual fill direction

```text
         columns
       0   1   2   3
     +---+---+---+---+
  0  | ← | ← | ← | ↑ |
     +---+---+---+---+
  1  | ← | ← | ← | ↑ |
     +---+---+---+---+
  2  | ← | ← | ← | ↑ |
     +---+---+---+---+
  3  | ← | ← | ← | B |
     +---+---+---+---+

B = base / destination
```

A clean implementation is:

```text
for row = n-1 down to 0
    for col = m-1 down to 0
```

because:

```text
dp[row+1][col]
```

and:

```text
dp[row][col+1]
```

have already been processed.

---

## 19.4 C++17 example

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<ll>> dp(n, vector<ll>(m, 0));

    dp[n - 1][m - 1] = 1;

    for (int row = n - 1; row >= 0; row--) {
        for (int col = m - 1; col >= 0; col--) {
            if (row == n - 1 && col == m - 1) {
                continue;
            }

            if (row + 1 < n) {
                dp[row][col] += dp[row + 1][col];
            }

            if (col + 1 < m) {
                dp[row][col] += dp[row][col + 1];
            }
        }
    }

    cout << dp[0][0] << '\n';
    return 0;
}
```

---

## 19.5 Why forward loops would be wrong for this state

Suppose you try:

```cpp
for (int row = 0; row < n; row++) {
    for (int col = 0; col < m; col++) {
        // compute dp[row][col]
    }
}
```

But the recurrence needs:

```text
dp[row+1][col]
dp[row][col+1]
```

Those future cells have not necessarily been calculated.

So the loop order contradicts the recurrence.

---

## Important rule

Do not memorize:

```text
"Grid DP always goes backward."
```

That is false.

Instead ask:

```text
Which states does my current state depend on?
```

Then process those dependency states first.

---

# 20. General Technique to Solve Any DP Problem

This is the lecture's final DP recipe.

Follow this order.

---

## Step 1 — STATE

> Clearly define the subproblem.

Before writing any formula, complete this sentence:

```text
dp[ ... ] means __________________________.
```

Examples:

```text
dp[i]
= ith Fibonacci number
```

```text
dp[i][j]
= number of paths from (i,j)
  to the destination
```

```text
dp[i]
= maximum sum obtainable from first i elements
```

If this sentence is unclear, do not move forward.

---

## Step 2 — TRANSITION

> Define the relation between states.

Pretend every state on the right side has magically already been solved.

Then ask:

```text
How can those answers construct my current answer?
```

Example Fibonacci:

```text
dp[i]
= dp[i-1] + dp[i-2]
```

Grid:

```text
dp[i][j]
= dp[i+1][j] + dp[i][j+1]
```

At this stage:

> **Do not worry about how `dp[i+1][j]` itself is calculated. Assume it is already correct.**

This separation makes DP much easier.

---

## Step 3 — BASE CASE

Ask:

```text
Where does the transition stop working?
```

Those are usually your base cases.

Fibonacci:

```text
dp[1] = 1
dp[2] = 1
```

Grid path count:

```text
dp[n-1][m-1] = 1
```

The base case is a state whose answer is known directly.

---

## Step 4 — FINAL SUBPROBLEM

Ask:

```text
Which DP state is the original problem asking for?
```

Examples:

Fibonacci:

```text
answer = dp[n]
```

Grid from top-left:

```text
answer = dp[0][0]
```

Maximum prefix problem:

```text
answer = dp[n-1]
```

Do not assume the final answer is always the last table cell.

It depends on your **state definition**.

---

# 21. A Complete Worked DP Formulation

Let's use a tiny example and apply the exact four lecture steps.

## Problem

A person stands on stair `0`.

They want to reach stair `n`.

At every move they may climb:

```text
1 stair
or
2 stairs
```

Find the number of ways to reach `n`.

---

## Step 1 — State

Define:

```text
dp[i]
= number of ways to reach stair i
```

Now `dp[i]` has a precise meaning.

---

## Step 2 — Transition

How can we arrive at stair `i`?

The final move must be either:

```text
from i-1 using a 1-step move
```

or:

```text
from i-2 using a 2-step move
```

Therefore:

```text
dp[i]
=
dp[i-1] + dp[i-2]
```

Visual:

```text
(i-2) --------2 steps--------> (i)
   \
    \
(i-1) --------1 step---------> (i)
```

---

## Step 3 — Base cases

For stair `0`:

```text
dp[0] = 1
```

There is one way to be at the starting position: do nothing.

For stair `1`:

```text
dp[1] = 1
```

Only:

```text
0 → 1
```

---

## Step 4 — Final subproblem

We need ways to reach stair `n`.

Therefore:

```text
answer = dp[n]
```

---

## Complexity

States:

```text
0 ... n
→ O(n)
```

Transition per state:

```text
O(1)
```

Therefore:

```text
Time  = O(n)
Space = O(n)
```

---

## C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    int n;
    cin >> n;

    vector<ll> dp(n + 1, 0);

    dp[0] = 1;

    if (n >= 1) {
        dp[1] = 1;
    }

    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }

    cout << dp[n] << '\n';
    return 0;
}
```

---

# 22. How to Read a New DP Problem

When you see an unfamiliar DP problem, do **not** immediately ask:

```text
"What is the formula?"
```

Instead use this thinking sequence.

```text
Problem
  ↓
What smaller subproblem would be useful?
  ↓
STATE
  ↓
What choices connect states?
  ↓
TRANSITION
  ↓
Where does transition stop?
  ↓
BASE CASE
  ↓
Which state represents the asked result?
  ↓
FINAL SUBPROBLEM
  ↓
How many states?
  ↓
How much work per state?
  ↓
COMPLEXITY
```

This is much more reliable than memorizing hundreds of DP formulas.

---

# 23. Common Mistakes

## Mistake 1 — Counting recursion-tree calls after memoization

Wrong thinking:

```text
Each node branches into 2
→ O(2^n)
```

after the solution has memoization.

Correct thinking:

```text
How many UNIQUE states exist?
```

Then:

```text
#states × work per state
```

---

## Mistake 2 — Multiplying by the number of dependencies recursively

For:

```text
dp[i] = dp[i-1] + dp[i-2]
```

transition time is:

```text
O(1)
```

not:

```text
time(dp[i-1]) + time(dp[i-2])
```

because those DP states are already computed/cached.

---

## Mistake 3 — Always using worst transition time when costs vary

If:

```text
transition(i) = n / i
```

using:

```text
n states × n work
= O(n²)
```

is safe but loose.

Exact total:

```text
Σ n/i
= O(n log n)
```

can be much better.

---

## Mistake 4 — Writing bottom-up loops before studying dependencies

Wrong process:

```text
"I have dp[i][j], so I will loop i=0..n and j=0..m."
```

Correct process:

```text
1. Write transition.
2. Draw dependency arrows.
3. Determine which states must come first.
4. Choose loop direction.
```

---

## Mistake 5 — Changing recursive to iterative too early

The lecture explicitly notes that you do not need to force bottom-up DP if it is not intuitive yet.

A good learning workflow is:

```text
recursive formulation
      ↓
memoization
      ↓
understand state dependencies
      ↓
then convert to iterative
```

Correct top-down DP is better than a confused bottom-up solution.

---

## Mistake 6 — State has no English meaning

Bad:

```text
dp[i][j][k]
```

with no explanation.

Good:

```text
dp[i][j][k]
= minimum cost after processing first i items,
  using j units of capacity,
  while condition k is ...
```

Every parameter must have a purpose.

---

# 24. Final Mental Model

## DP formulation

```text
┌──────────────────────────────────────────┐
│ 1. STATE                                 │
│ What exactly does dp[...] mean?          │
└─────────────────────┬────────────────────┘
                      ↓
┌──────────────────────────────────────────┐
│ 2. TRANSITION                            │
│ How do already-solved states build it?   │
└─────────────────────┬────────────────────┘
                      ↓
┌──────────────────────────────────────────┐
│ 3. BASE CASE                             │
│ Where does the transition stop/fail?     │
└─────────────────────┬────────────────────┘
                      ↓
┌──────────────────────────────────────────┐
│ 4. FINAL SUBPROBLEM                      │
│ Which state is the requested answer?     │
└─────────────────────┬────────────────────┘
                      ↓
┌──────────────────────────────────────────┐
│ 5. COMPLEXITY                            │
│ #states × transition work                │
│ or Σ transition work over all states     │
└──────────────────────────────────────────┘
```

---

## Recursive vs iterative

```text
TOP-DOWN / MEMOIZATION

ask for final state
        ↓
recursion asks dependencies
        ↓
dependencies solved first
        ↓
cache each state
        ↓
return final answer
```

```text
BOTTOM-UP / TABULATION

study dependency arrows
        ↓
choose valid processing order
        ↓
fill base states
        ↓
fill dependent states
        ↓
reach final answer
```

---

## The one sentence to remember

> **First define what a state means. Then derive its transition. Complexity comes from how many such states exist and how much work each transition performs.**

That mindset is the foundation for the harder DP forms that follow.
