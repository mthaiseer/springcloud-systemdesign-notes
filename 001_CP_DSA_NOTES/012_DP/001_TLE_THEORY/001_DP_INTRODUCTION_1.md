# 002 — Dynamic Programming Introduction 1
## Divide & Conquer → Overlapping Subproblems → Memoization → State → Transition

> **Goal:** Build the DP mindset step by step from recursion.  
> This note follows the lecture flow and is written as a self-study replacement for repeatedly watching the video.

---

## Table of Contents

1. [What You Should Know Before DP](#1-what-you-should-know-before-dp)
2. [Divide & Conquer](#2-divide--conquer)
3. [Example: Sum of an Array](#3-example-sum-of-an-array)
4. [Fibonacci with Plain Recursion](#4-fibonacci-with-plain-recursion)
5. [The Real Problem: Overlapping Subproblems](#5-the-real-problem-overlapping-subproblems)
6. [What Dynamic Programming Means](#6-what-dynamic-programming-means)
7. [Memoization = Recursive DP](#7-memoization--recursive-dp)
8. [How Do We Store DP Answers?](#8-how-do-we-store-dp-answers)
9. [Fibonacci with Memoization](#9-fibonacci-with-memoization)
10. [DP Complexity: Count States, Not Recursion Calls](#10-dp-complexity-count-states-not-recursion-calls)
11. [Grid Minimum Path Sum](#11-grid-minimum-path-sum)
12. [Grid Problem — Recursive Formulation](#12-grid-problem--recursive-formulation)
13. [Grid Problem — Why DP Works](#13-grid-problem--why-dp-works)
14. [Important DP Term: State](#14-important-dp-term-state)
15. [Important DP Term: Transition](#15-important-dp-term-transition)
16. [DP Direction: Suffix State vs Prefix State](#16-dp-direction-suffix-state-vs-prefix-state)
17. [Problem: Maximum Sum with No Adjacent Picks](#17-problem-maximum-sum-with-no-adjacent-picks)
18. [Solution 1 — One-State DP](#18-solution-1--one-state-dp)
19. [Solution 2 — Two-State DP](#19-solution-2--two-state-dp)
20. [The DP Construction Recipe](#20-the-dp-construction-recipe)
21. [Common Mistakes](#21-common-mistakes)
22. [Final Mental Model](#22-final-mental-model)

---

# 1. What You Should Know Before DP

DP is much easier when these recursion ideas are already clear.

## 1.1 A function solves one clearly defined problem

Example:

```text
fib(5)
```

should have one exact meaning:

```text
fib(5) = 5th Fibonacci number
```

Similarly:

```text
solve(i, j)
```

must have one exact meaning.

For example:

```text
solve(i, j)
= minimum path sum from cell (i, j)
  to the bottom-right cell
```

This sentence is extremely important.

> **Before writing DP code, first write the meaning of the function/state in English.**

---

## 1.2 Recursion needs a smaller problem

Suppose:

```text
fib(n) = fib(n - 1) + fib(n - 2)
```

The big problem `fib(n)` is reduced into smaller problems:

```text
fib(n - 1)
fib(n - 2)
```

Eventually we reach trivial problems:

```text
fib(1) = 1
fib(2) = 1
```

These are the **base cases**.

---

## 1.3 Base case

A base case is a problem whose answer is already known.

Without a base case:

```text
f(n)
 → f(n-1)
   → f(n-2)
     → ...
```

would continue forever.

Example:

```cpp
if (n == 1 || n == 2) {
    return 1;
}
```

---

## 1.4 Arrays and indexing

We will frequently use an array like:

```text
index:  0   1   2   3   4
value:  7   3   9   2   5
```

`a[i]` means the value at index `i`.

In DP, another array often stores answers:

```text
dp[i] = answer of subproblem i
```

So:

```text
a[i]  = input information
dp[i] = computed answer
```

Do not confuse them.

---

## 1.5 2D indexing

For a grid:

```text
        column
          0   1   2
       +---+---+---+
row 0  | 1 | 5 | 8 |
       +---+---+---+
row 1  | 6 | 2 | 7 |
       +---+---+---+
row 2  | 9 | 3 | 4 |
       +---+---+---+
```

A cell is identified by:

```text
(row, column)
```

Examples:

```text
grid[0][0] = 1
grid[1][1] = 2
grid[2][2] = 4
```

---

## 1.6 `min` and `max`

DP often asks us to combine answers.

Minimum:

```cpp
min(a, b)
```

Maximum:

```cpp
max(a, b)
```

Example:

```text
down path  = 15
right path = 11

best = min(15, 11)
     = 11
```

---

## 1.7 `INF` for an impossible move

Suppose a grid only allows moving right or down.

From the last row, moving down is invalid.

We want:

```text
min(validPath, invalidPath)
```

to always choose the valid path.

Therefore represent an invalid path by a huge value:

```text
INF
```

Example:

```text
min(14, INF) = 14
```

In C++:

```cpp
const long long INF = 4e18;
```

---

## 1.8 Sentinel value: "not calculated yet"

Memoization needs to know:

```text
Have I already solved this state?
```

A common idea is:

```text
dp[i] = -1
```

meaning:

```text
not calculated yet
```

But this is safe only when `-1` cannot be a valid answer.

For general DP, a separate `visited` array or an impossible sentinel is safer.

---

# 2. Divide & Conquer

The lecture starts from **Divide & Conquer**.

## Core idea

Given one large problem:

```text
BIG PROBLEM
```

break it into smaller problems:

```text
             BIG PROBLEM
              /       \
         smaller     smaller
          /   \       /   \
       tiny   tiny  tiny   tiny
```

Then combine their answers.

The important requirement is:

```text
answers of smaller problems
          ↓
must be usable to construct
          ↓
answer of the bigger problem
```

Continue until the problem becomes trivial.

---

# 3. Example: Sum of an Array

Suppose:

```text
a = [1, 2, 3, 4, 5, 6, 7, 8]
```

We want the total sum.

Instead of thinking only about a loop, formulate it recursively.

```text
Sum[1..8]
├── Sum[1..4]
│   ├── Sum[1..2]
│   │   ├── 1
│   │   └── 2
│   └── Sum[3..4]
│       ├── 3
│       └── 4
└── Sum[5..8]
    ├── Sum[5..6]
    │   ├── 5
    │   └── 6
    └── Sum[7..8]
        ├── 7
        └── 8
```

Now build answers upward.

```text
Sum[1..2] = 1 + 2 = 3
Sum[3..4] = 3 + 4 = 7

Sum[1..4] = 3 + 7 = 10
```

Likewise:

```text
Sum[5..8] = 26
```

Finally:

```text
Sum[1..8] = 10 + 26
          = 36
```

This is the central recursive mindset:

```text
solve big problem
    ↓
solve smaller problems
    ↓
combine their answers
```

---

# 4. Fibonacci with Plain Recursion

The lecture next uses Fibonacci.

## 4.1 Definition used in the lecture

```text
F(1) = 1
F(2) = 1
```

For `n > 2`:

```text
F(n) = F(n - 1) + F(n - 2)
```

Sequence:

```text
1, 1, 2, 3, 5, 8, 13, ...
```

---

## 4.2 Example: `F(5)`

```text
F(5)
= F(4) + F(3)
```

Now:

```text
F(4)
= F(3) + F(2)
```

and:

```text
F(3)
= F(2) + F(1)
= 1 + 1
= 2
```

Therefore:

```text
F(4) = 2 + 1 = 3

F(5) = 3 + 2 = 5
```

---

## 4.3 Plain recursive code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long fib(int n) {
    if (n == 1 || n == 2) {
        return 1;
    }

    return fib(n - 1) + fib(n - 2);
}

int main() {
    int n;
    cin >> n;

    cout << fib(n) << '\n';
    return 0;
}
```

The code is mathematically correct.

But it is computationally expensive.

Why?

---

# 5. The Real Problem: Overlapping Subproblems

Draw the recursion tree for `fib(6)`.

```text
                         fib(6)
                    /              \
               fib(5)              fib(4)
              /      \             /     \
         fib(4)    fib(3)      fib(3)   fib(2)
         /   \      /   \       /   \
    fib(3) fib(2) fib(2) fib(1) ...
```

Look carefully.

`fib(4)` appears more than once.

`fib(3)` appears many times.

`fib(2)` appears even more times.

We are repeatedly solving the **same subproblem**.

That is the key observation.

```text
fib(4)
  ↓ calculate

later...

fib(4)
  ↓ calculate AGAIN  ← waste
```

---

## 5.1 What should we do instead?

The first time:

```text
fib(4) = 3
```

store it.

Later:

```text
Need fib(4)?
    ↓
already stored
    ↓
return 3 immediately
```

This is the bridge from recursion to DP.

---

# 6. What Dynamic Programming Means

For this lecture, the central DP idea is:

> **Calculate the answer of an overlapping subproblem once, store it, and reuse it whenever the same subproblem is needed again.**

Ask these two questions whenever you write a recursive divide-and-conquer solution:

```text
1. Am I solving the same subproblem again and again?

2. Can I calculate it once and store its answer?
```

If yes, DP may remove the repeated work.

---

## Important distinction

Not every recursive problem automatically needs DP.

### No useful overlap

```text
        A
      /   \
     B     C
    / \   / \
   D  E  F  G
```

Every state is different.

Memoization may not help much.

### Overlap exists

```text
        A
      /   \
     B     C
      \   /
        D
```

Both branches need `D`.

Now storing `D` is useful.

---

# 7. Memoization = Recursive DP

When we keep the recursive solution but store already calculated answers, it is called:

```text
Memoization
```

Think of it as a cache.

```text
subproblem key  →  answer
```

Example:

```text
1 → fib(1)
2 → fib(2)
3 → fib(3)
4 → fib(4)
5 → fib(5)
```

When `fib(4)` is requested again:

```text
check cache
   ↓
found
   ↓
return stored answer
```

No second recursion tree is needed.

---

# 8. How Do We Store DP Answers?

To store an answer we need two things:

```text
KEY → VALUE
```

where:

```text
KEY   = uniquely identifies the subproblem
VALUE = answer of that subproblem
```

---

## 8.1 Fibonacci key

For Fibonacci:

```text
fib(n)
```

the only changing parameter is `n`.

Therefore:

```text
key = n
```

Examples:

```text
n = 3 → fib(3)
n = 5 → fib(5)
```

A one-dimensional DP array is enough:

```text
dp[n]
```

---

## 8.2 Grid key

For a grid state:

```text
solve(i, j)
```

two values identify the subproblem:

```text
i = row
j = column
```

Therefore:

```text
key = (i, j)
```

We naturally use:

```text
dp[i][j]
```

---

## 8.3 Array vs map

Conceptually we could store:

```text
key → answer
```

in a map.

Example:

```text
1 → F(1)
2 → F(2)
3 → F(3)
...
```

But if keys are small dense integers:

```text
0, 1, 2, 3, ..., n
```

an array/vector is simpler and gives direct indexing.

```text
dp[0]
dp[1]
dp[2]
...
```

### Practical rule

```text
Dense integer state
→ vector / array is usually best

Sparse or complicated state key
→ map / unordered_map may be useful
```

---

# 9. Fibonacci with Memoization

Now add a DP array.

## 9.1 Meaning of the state

```text
dp[n] = nth Fibonacci number
```

---

## 9.2 Logic

When solving `fib(n)`:

```text
Step 1: Is n a base case?
        yes → return 1

Step 2: Was dp[n] already calculated?
        yes → return dp[n]

Step 3: Otherwise calculate:
        fib(n-1) + fib(n-2)

Step 4: Store the answer in dp[n]

Step 5: Return it
```

---

## 9.3 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll fib(int n, vector<ll>& dp) {
    if (n == 1 || n == 2) {
        return 1;
    }

    if (dp[n] != -1) {
        return dp[n];
    }

    dp[n] = fib(n - 1, dp) + fib(n - 2, dp);
    return dp[n];
}

int main() {
    int n;
    cin >> n;

    vector<ll> dp(n + 1, -1);

    cout << fib(n, dp) << '\n';
    return 0;
}
```

---

## 9.4 Compact dry run: `fib(6)`

Initially:

```text
dp = [-1, -1, -1, -1, -1, -1, -1]
```

Then:

```text
fib(6)
→ needs fib(5), fib(4)

fib(5)
→ needs fib(4), fib(3)

fib(4)
→ needs fib(3), fib(2)

fib(3)
→ fib(2) + fib(1)
→ 1 + 1
→ dp[3] = 2

fib(4)
→ dp[3] + 1
→ 2 + 1
→ dp[4] = 3

fib(5)
→ dp[4] + dp[3]
→ 3 + 2
→ dp[5] = 5

back at fib(6):
fib(4) is requested again
→ dp[4] already exists
→ return 3 immediately

dp[6] = 5 + 3 = 8
```

The repeated recursion is gone.

---

# 10. DP Complexity: Count States, Not Recursion Calls

This is one of the most useful DP habits.

After memoization:

```text
each distinct state is solved once
```

So think:

```text
Time
≈ number of states
  × work done per state
```

---

## Fibonacci

States:

```text
fib(1), fib(2), ..., fib(n)
```

Number of states:

```text
O(n)
```

Work per state:

```text
O(1)
```

Therefore:

```text
Time = O(n)
```

DP storage:

```text
O(n)
```

Recursive call stack:

```text
O(n)
```

This is dramatically better than the naive recursion tree.

---

# 11. Grid Minimum Path Sum

Now the lecture moves from 1D DP to a 2D state.

## Problem

Given an `N x M` grid, start at:

```text
(0, 0)
```

and reach:

```text
(n - 1, m - 1)
```

Allowed moves:

```text
RIGHT → (i, j + 1)
DOWN  → (i + 1, j)
```

Minimize the sum of values on the chosen path.

Example:

```text
        0   1   2
      +---+---+---+
  0   | 1 | 5 | 8 |
      +---+---+---+
  1   | 6 | 2 | 7 |
      +---+---+---+
  2   | 9 | 3 | 4 |
      +---+---+---+
```

Start:

```text
(0,0) = 1
```

Destination:

```text
(2,2) = 4
```

---

## 11.1 One valid path

```text
(0,0) → (0,1) → (1,1) → (2,1) → (2,2)

   1       5        2        3        4
```

Sum:

```text
1 + 5 + 2 + 3 + 4 = 15
```

We must find the minimum among all valid paths.

---

# 12. Grid Problem — Recursive Formulation

Stand at cell:

```text
(i, j)
```

You have two choices:

```text
           (i, j)
          /      \
       DOWN      RIGHT
      /             \
(i + 1, j)       (i, j + 1)
```

So the problem naturally follows the same recursion mindset.

---

## 12.1 Define the function first

Let:

```text
f(i, j)
= minimum path sum from cell (i, j)
  to cell (n - 1, m - 1)
```

This is the **state meaning**.

Do not write the recurrence before this sentence is clear.

---

## 12.2 Transition derivation

Standing at `(i,j)`:

```text
current cell cost = grid[i][j]
```

After paying that cost, choose the cheaper future:

```text
down  = f(i + 1, j)
right = f(i, j + 1)
```

Therefore:

```text
f(i, j)
= grid[i][j] + min(
      f(i + 1, j),
      f(i, j + 1)
  )
```

Compact form:

```text
f(i,j) = grid[i][j] + min(f(i+1,j), f(i,j+1))
```

---

## 12.3 Base case

When we are already at the destination:

```text
(i, j) = (n - 1, m - 1)
```

there is nowhere else to go.

Therefore:

```text
f(n-1, m-1) = grid[n-1][m-1]
```

---

## 12.4 Invalid state

If:

```text
i >= n
or
j >= m
```

we moved outside the grid.

That path must never be chosen.

So:

```text
f(i,j) = INF
```

for an invalid position.

---

# 13. Grid Problem — Why DP Works

Plain recursion generates repeated states.

From `(0,0)`:

```text
                  (0,0)
                 /     \
              (1,0)   (0,1)
                 \     /
                  (1,1)
```

State `(1,1)` can be reached in two ways:

```text
(0,0) → (1,0) → (1,1)

(0,0) → (0,1) → (1,1)
```

Without DP:

```text
f(1,1)
```

is recalculated.

With memoization:

```text
first request:
calculate f(1,1)
store it

second request:
return stored f(1,1)
```

That is an overlapping subproblem.

---

## 13.1 Number of complete paths

For an `N x N` grid:

To go from `(0,0)` to `(n-1,n-1)` we need exactly:

```text
n - 1 down moves
n - 1 right moves
```

Total moves:

```text
2n - 2
```

Choose which `n - 1` positions are down moves:

```text
C(2n - 2, n - 1)
```

So naive exploration grows exponentially.

---

## 13.2 But how many unique DP states?

Possible cells are:

```text
n × m
```

Therefore unique states:

```text
O(nm)
```

At each cell we do constant work:

```text
two recursive choices
+ min
+ addition
```

Hence memoized complexity:

```text
Time  = O(nm)
Space = O(nm)
```

plus recursion stack.

---

## 13.3 Memoized C++ solution

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = 4e18;

ll solve(int row, int col, const vector<vector<ll>>& grid,
         vector<vector<ll>>& dp, vector<vector<bool>>& seen) {
    int n = grid.size();
    int m = grid[0].size();

    if (row >= n || col >= m) {
        return INF;
    }

    if (row == n - 1 && col == m - 1) {
        return grid[row][col];
    }

    if (seen[row][col]) {
        return dp[row][col];
    }

    seen[row][col] = true;

    ll down = solve(row + 1, col, grid, dp, seen);
    ll right = solve(row, col + 1, grid, dp, seen);

    dp[row][col] = grid[row][col] + min(down, right);
    return dp[row][col];
}

int main() {
    int n, m;
    cin >> n >> m;

    vector<vector<ll>> grid(n, vector<ll>(m));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cin >> grid[i][j];
        }
    }

    vector<vector<ll>> dp(n, vector<ll>(m));
    vector<vector<bool>> seen(n, vector<bool>(m, false));

    cout << solve(0, 0, grid, dp, seen) << '\n';
    return 0;
}
```

---

## 13.4 Detailed dry run on the lecture grid

Grid:

```text
      +---+---+---+
      | 1 | 5 | 8 |
      +---+---+---+
      | 6 | 2 | 7 |
      +---+---+---+
      | 9 | 3 | 4 |
      +---+---+---+
```

Start from the destination.

```text
f(2,2) = 4
```

Last row:

```text
f(2,1) = 3 + f(2,2)
       = 3 + 4
       = 7

f(2,0) = 9 + f(2,1)
       = 9 + 7
       = 16
```

Last column:

```text
f(1,2) = 7 + f(2,2)
       = 7 + 4
       = 11

f(0,2) = 8 + f(1,2)
       = 8 + 11
       = 19
```

Middle:

```text
f(1,1)
= 2 + min(f(2,1), f(1,2))
= 2 + min(7, 11)
= 9
```

Then:

```text
f(1,0)
= 6 + min(16, 9)
= 15

f(0,1)
= 5 + min(9, 19)
= 14
```

Finally:

```text
f(0,0)
= 1 + min(15, 14)
= 15
```

Answer:

```text
15
```

One minimum path is:

```text
1 → 5
    ↓
    2
    ↓
    3 → 4
```

---

# 14. Important DP Term: State

The lecture formally introduces **State** here.

## Definition

A state is:

> the meaning of a subproblem together with the parameters that uniquely identify that subproblem.

---

## Fibonacci state

```text
dp[i]
= ith Fibonacci number
```

Parameter:

```text
i
```

Number of states:

```text
O(n)
```

---

## Grid state

```text
dp[i][j]
= minimum path sum from (i,j)
  to (n-1,m-1)
```

Parameters:

```text
i, j
```

Number of states:

```text
O(nm)
```

---

## Good state sentence test

If someone asks:

```text
What exactly does dp[i][j] mean?
```

you should be able to answer in one sentence.

If you cannot, the state is not yet clear enough.

---

# 15. Important DP Term: Transition

A **transition** tells us how to calculate one state using smaller states.

Think:

```text
smaller state answers
        ↓
combine them
        ↓
current state answer
```

---

## Fibonacci transition

State:

```text
dp[i] = ith Fibonacci number
```

Transition:

```text
dp[i] = dp[i-1] + dp[i-2]
```

---

## Grid transition

State:

```text
dp[i][j]
= min path sum from (i,j) to destination
```

Transition:

```text
dp[i][j]
= grid[i][j]
  + min(dp[i+1][j], dp[i][j+1])
```

---

## State vs transition

Do not mix these.

```text
STATE
= What does dp[...] mean?

TRANSITION
= How do I compute that dp[...]?
```

Example:

```text
dp[i][j]
```

by itself tells us almost nothing.

But:

```text
dp[i][j]
= minimum cost from (i,j) to destination
```

is a proper state definition.

---

# 16. DP Direction: Suffix State vs Prefix State

The lecture also shows that the same problem can be described in different directions.

## Direction A — current cell to destination

State:

```text
f(i,j)
= min cost from (i,j) to (n-1,m-1)
```

Transition:

```text
f(i,j)
= grid[i][j]
  + min(f(i+1,j), f(i,j+1))
```

We look:

```text
DOWN / RIGHT
```

---

## Direction B — start to current cell

State:

```text
g(i,j)
= min cost from (0,0) to (i,j)
```

Transition:

```text
g(i,j)
= grid[i][j]
  + min(g(i-1,j), g(i,j-1))
```

We look:

```text
UP / LEFT
```

Both are valid.

The transition must match the state meaning.

### Never do this

```text
State:
minimum cost from (i,j) to destination

Transition:
use (i-1,j) and (i,j-1)
```

That mixes two different directions.

---

# 17. Problem: Maximum Sum with No Adjacent Picks

The lecture next applies DP to an array problem.

## Problem

Given an integer array containing positive and negative values:

```text
[1, 4, 2, -10, 10, 5]
```

choose a subsequence such that:

```text
no two selected elements are adjacent
```

and maximize the selected sum.

---

## Example

Choose:

```text
4 and 10
```

Indices:

```text
0   1   2    3    4   5
1   4   2   -10  10   5
    ↑              ↑
```

Sum:

```text
4 + 10 = 14
```

Another valid choice:

```text
1 + 2 + 10 = 13
```

So the better value is:

```text
14
```

---

## 17.1 The decision at index `i`

At each element:

```text
             index i
             /     \
          PICK     SKIP
```

If we **pick** `a[i]`:

```text
i - 1 cannot be picked
```

So after picking `i`, the remaining compatible prefix ends at:

```text
i - 2
```

If we **skip** `a[i]`:

```text
i - 1 may still be used
```

This directly creates the transition.

---

# 18. Solution 1 — One-State DP

## 18.1 Define the state

Let:

```text
dp[i]
= maximum sum obtainable from indices [0 ... i]
  with no two adjacent selected
```

We do **not** care whether index `i` itself was selected.

That information is already summarized by the best value.

---

## 18.2 Two choices

### Choice 1 — pick `i`

If we pick `a[i]`, we cannot pick `i-1`.

So:

```text
pick = a[i] + dp[i-2]
```

### Choice 2 — skip `i`

Then:

```text
skip = dp[i-1]
```

Take the better one:

```text
dp[i] = max(
    a[i] + dp[i-2],
    dp[i-1]
)
```

---

## 18.3 Base cases

Allowing the empty subsequence:

```text
dp[-1] = 0
```

For index `0`:

```text
dp[0] = max(0, a[0])
```

For recursion, we can simply say:

```text
if i < 0:
    return 0
```

---

## 18.4 Top-down memoized code

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll solve(int index, const vector<ll>& a, vector<ll>& dp) {
    if (index < 0) {
        return 0;
    }

    if (dp[index] != LLONG_MIN) {
        return dp[index];
    }

    ll skip = solve(index - 1, a, dp);
    ll pick = a[index] + solve(index - 2, a, dp);

    dp[index] = max(skip, pick);
    return dp[index];
}

int main() {
    int n;
    cin >> n;

    vector<ll> a(n);

    for (ll& x : a) {
        cin >> x;
    }

    vector<ll> dp(n, LLONG_MIN);

    cout << solve(n - 1, a, dp) << '\n';
    return 0;
}
```

---

## 18.5 Detailed dry run

Array:

```text
a = [1, 4, 2, -10, 10, 5]
```

Use:

```text
dp[i]
= best answer from 0..i
```

### `i = 0`

```text
pick = 1
skip = 0

dp[0] = 1
```

### `i = 1`

```text
pick = 4 + dp[-1]
     = 4

skip = dp[0]
     = 1

dp[1] = 4
```

### `i = 2`

```text
pick = 2 + dp[0]
     = 2 + 1
     = 3

skip = dp[1]
     = 4

dp[2] = 4
```

### `i = 3`

```text
pick = -10 + dp[1]
     = -10 + 4
     = -6

skip = dp[2]
     = 4

dp[3] = 4
```

### `i = 4`

```text
pick = 10 + dp[2]
     = 10 + 4
     = 14

skip = dp[3]
     = 4

dp[4] = 14
```

### `i = 5`

```text
pick = 5 + dp[3]
     = 5 + 4
     = 9

skip = dp[4]
     = 14

dp[5] = 14
```

Final:

```text
answer = dp[5] = 14
```

---

# 19. Solution 2 — Two-State DP

The lecture also shows another valid state design.

Sometimes we explicitly keep whether the current item is selected.

Define:

```text
dp[i][0]
= maximum sum from indices 0..i
  when index i is NOT picked

dp[i][1]
= maximum sum from indices 0..i
  when index i IS picked
```

---

## 19.1 Transition when `i` is not picked

If we do not pick `i`, the previous item may have been:

```text
picked
or
not picked
```

Therefore:

```text
dp[i][0]
= max(dp[i-1][0], dp[i-1][1])
```

---

## 19.2 Transition when `i` is picked

If we pick `i`, then `i-1` must not be picked.

Therefore:

```text
dp[i][1]
= a[i] + dp[i-1][0]
```

---

## 19.3 Base case

For index `0`:

```text
dp[0][0] = 0
dp[0][1] = a[0]
```

---

## 19.4 Final answer

At the final index we do not care whether it was picked.

So:

```text
answer
= max(dp[n-1][0], dp[n-1][1])
```

---

## 19.5 Visual transition

```text
Previous index i-1                Current index i

 NOT PICKED  -------------------> NOT PICKED
      \                              ↑
       \-----------------------------|
        \
 PICKED   ------------------------> NOT PICKED


 NOT PICKED  --------------------> PICKED
                      + a[i]

 PICKED  --------X---------------> PICKED
          invalid: adjacent
```

This is the same problem as the one-state DP, just with more explicit state information.

---

## 19.6 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    int n;
    cin >> n;

    vector<ll> a(n);

    for (ll& x : a) {
        cin >> x;
    }

    if (n == 0) {
        cout << 0 << '\n';
        return 0;
    }

    vector<array<ll, 2>> dp(n);

    dp[0][0] = 0;
    dp[0][1] = a[0];

    for (int i = 1; i < n; i++) {
        dp[i][0] = max(dp[i - 1][0], dp[i - 1][1]);
        dp[i][1] = a[i] + dp[i - 1][0];
    }

    cout << max(dp[n - 1][0], dp[n - 1][1]) << '\n';
    return 0;
}
```

---

# 20. The DP Construction Recipe

This is the most important section to remember.

When you face a new DP problem, do not start by guessing a `dp` array.

Follow this order.

---

## Step 1 — Write the recursive problem

Ask:

```text
What smaller problems can solve my current problem?
```

Example:

```text
fib(n)
→ fib(n-1), fib(n-2)
```

Grid:

```text
f(i,j)
→ f(i+1,j), f(i,j+1)
```

No-adjacent sum:

```text
f(i)
→ f(i-1), f(i-2)
```

---

## Step 2 — Define the STATE in English

Examples:

```text
fib:
dp[i] = ith Fibonacci number
```

```text
grid:
dp[i][j]
= minimum path sum from (i,j) to destination
```

```text
no-adjacent:
dp[i]
= maximum valid sum using indices 0..i
```

---

## Step 3 — Identify the choices

Grid:

```text
DOWN
RIGHT
```

No-adjacent:

```text
PICK
SKIP
```

---

## Step 4 — Write the transition

Grid:

```text
dp[i][j]
= grid[i][j]
  + min(dp[i+1][j], dp[i][j+1])
```

No-adjacent:

```text
dp[i]
= max(
    a[i] + dp[i-2],
    dp[i-1]
)
```

---

## Step 5 — Write base cases

Examples:

```text
fib:
F(1) = F(2) = 1
```

```text
grid destination:
f(n-1,m-1) = grid[n-1][m-1]
```

```text
no-adjacent:
f(i) = 0 when i < 0
```

---

## Step 6 — Identify invalid states

Example grid:

```text
row >= n
or
col >= m

→ INF
```

---

## Step 7 — Check for overlap

Ask:

```text
Can the same state be reached from multiple recursive branches?
```

Examples:

```text
fib(4) repeatedly appears
```

```text
grid state (1,1)
can be reached from (1,0) and (0,1)
```

If yes:

```text
memoize it
```

---

## Step 8 — Choose storage

```text
one integer parameter
→ dp[i]

two integer parameters
→ dp[i][j]
```

Use a map only when direct array indexing is inconvenient or impossible.

---

## Step 9 — Analyze complexity

Use:

```text
number of distinct states
×
work per state
```

Examples:

```text
Fibonacci:
O(n) states × O(1)
= O(n)
```

```text
Grid:
O(nm) states × O(1)
= O(nm)
```

---

# 21. Common Mistakes

## Mistake 1 — Writing transition before defining state

Bad:

```text
dp[i] = max(dp[i-1], a[i] + dp[i-2])
```

Question:

```text
What does dp[i] mean?
```

If you cannot answer, you are memorizing rather than modelling.

---

## Mistake 2 — Mixing state directions

If:

```text
dp[i][j]
= answer from (i,j) to destination
```

then use future cells:

```text
(i+1,j)
(i,j+1)
```

If:

```text
dp[i][j]
= answer from start to (i,j)
```

then use previous cells:

```text
(i-1,j)
(i,j-1)
```

---

## Mistake 3 — Memoizing something that is not uniquely identified

Suppose the answer depends on:

```text
index
remainingTime
```

Then:

```text
dp[index]
```

is insufficient.

Two calls can have the same `index` but different `remainingTime`.

State parameters must contain enough information to uniquely define the subproblem.

---

## Mistake 4 — Using `-1` as "uncomputed" when `-1` is a valid answer

If a DP state can genuinely equal `-1`, this becomes ambiguous:

```text
dp[i] == -1
```

Does it mean:

```text
not computed
```

or:

```text
computed answer is -1
```

Use a separate `seen` array or a safe impossible sentinel.

---

## Mistake 5 — Forgetting why DP is faster

DP is not magic.

The speedup comes from:

```text
many recursive calls
        ↓
but only a small number of UNIQUE states
        ↓
calculate each unique state once
```

---

# 22. Final Mental Model

Keep this picture in your head:

```text
                 BIG PROBLEM
                      |
                      v
              DIVIDE INTO STATES
                      |
                      v
              RECURSIVE CHOICES
                      |
                      v
        SAME STATE APPEARS AGAIN?
               /             \
             NO               YES
             |                 |
        recursion          MEMOIZE
                               |
                               v
                    calculate state once
                               |
                               v
                         store answer
                               |
                               v
                           reuse it
```

And for every DP problem, think:

```text
1. STATE
   What exactly does dp[...] mean?

2. CHOICES
   What decisions can I make?

3. TRANSITION
   How do smaller state answers build this answer?

4. BASE CASE
   Which state already has an obvious answer?

5. INVALID CASE
   Which states/moves are impossible?

6. MEMO
   Is this state already solved?

7. ANSWER
   Which state represents the original problem?

8. COMPLEXITY
   number of states × work per state
```

---

# One-Line Recognition Summary

```text
Recursion + repeated subproblems + stored answers
                    =
             Dynamic Programming
```

The most important skill is **not memorizing formulas**.

It is learning to say:

```text
"This function/state means ______."
```

Once the state meaning is correct, the transition usually becomes much easier to derive.
