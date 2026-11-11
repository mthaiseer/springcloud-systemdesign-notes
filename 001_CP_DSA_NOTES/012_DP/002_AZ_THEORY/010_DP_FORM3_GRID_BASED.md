# 021 — DP Grid-Based Applications
## Form 1 + Form 2 | Path Counting, Maximum-Sum Path, Maximum Square

# Table of Contents

- [1. Prerequisites](#1-prerequisites)
- [2. Grid DP Core Idea](#2-grid-dp-core-idea)
- [3. Decide the DP Form](#3-decide-the-dp-form)
- [4. Problem 1 — Count Paths with Blocked Cells](#4-problem-1--count-paths-with-blocked-cells)
- [5. Problem 2 — Maximum-Sum Path in a Grid](#5-problem-2--maximum-sum-path-in-a-grid)
- [6. Problem 3 — Maximum Area Square of 1s](#6-problem-3--maximum-area-square-of-1s)
- [7. Recognition Patterns](#7-recognition-patterns)
- [8. Common Mistakes](#8-common-mistakes)
- [9. Final Revision](#9-final-revision)

---

# 1. Prerequisites

Know these before starting:

```text
2D arrays / matrices
row and column indexing
recursion
memoization
bottom-up DP
Form 1 DP
Form 2 / Ending Form DP
counting paths
maximum/minimum transitions
invalid states
```

Grid coordinates:

```text
(i, j)

i = row
j = column
```

Example:

```text
        columns
        0   1   2   3

row 0  [ ] [ ] [ ] [ ]
row 1  [ ] [ ] [ ] [ ]
row 2  [ ] [ ] [ ] [ ]
```

Typical movement in this lesson:

```text
RIGHT
DOWN
```

---

# 2. Grid DP Core Idea

In grid DP, first ask:

```text
What exactly does dp(i,j) mean?
```

Possible meanings:

```text
number of paths ending at (i,j)

maximum sum starting from (i,j)

largest square ending at (i,j)
```

That one sentence decides the transition.

ASCII:

```text
             UP
              |
              v
LEFT -----> (i,j) -----> RIGHT
              |
              v
             DOWN
```

---

# 3. Decide the DP Form

## Form 1

State means:

```text
answer STARTING from (i,j)
```

Look forward:

```text
(i,j)
  |
  +------> (i,j+1)
  |
  v
(i+1,j)
```

Typical recurrence:

```text
dp(i,j)
=
current contribution
+
best future state
```

---

## Form 2 / Ending Form

State means:

```text
answer ENDING at (i,j)
```

Look backward:

```text
          (i-1,j)
              |
              v
(i,j-1) --> (i,j)
```

Typical recurrence:

```text
dp(i,j)
=
combine answers
from previous valid states
```

---

# 4. Problem 1 — Count Paths with Blocked Cells

## 4.1 Problem

Start:

```text
(1,1)
```

Destination:

```text
(N,M)
```

Allowed moves:

```text
RIGHT
DOWN
```

Some cells are blocked:

```text
#
```

Find:

```text
number of valid paths
```

Example:

```text
S . . . . .
. # . . # .
. # . . . .
. . . # . #
. . . . . .
. # . . . D
```

---

## 4.2 Decide Form

We want:

```text
number of paths ENDING at (i,j)
```

So this is:

```text
Form 2
```

---

## 4.3 State Space Formulation

Define:

```text
dp(i,j)
=
number of valid paths
from start
to cell (i,j)
```

To reach `(i,j)`, the previous move must come from:

```text
UP
or
LEFT
```

ASCII:

```text
          dp(i-1,j)
              |
              v
          +-------+
          | (i,j) |
          +-------+
              ^
              |
          dp(i,j-1)
```

---

## 4.4 Transition Design

If blocked:

```text
dp(i,j) = 0
```

Otherwise:

```text
dp(i,j)
=
dp(i-1,j)
+
dp(i,j-1)
```

Reason:

```text
all paths from UP
+
all paths from LEFT
```

---

## 4.5 Base Cases

Start cell:

```text
dp(1,1) = 1
```

if it is not blocked.

Outside grid:

```text
0 ways
```

Blocked cell:

```text
0 ways
```

---

## 4.6 Dry Run

Grid:

```text
S . .
. # .
. . D
```

DP:

```text
1 1 1
1 0 1
1 1 2
```

Destination:

```text
dp(3,3)
=
dp(2,3) + dp(3,2)
=
1 + 1
=
2
```

So:

```text
answer = 2
```

---

## 4.7 Time Complexity

States:

```text
N * M
```

Transitions per state:

```text
2
```

Therefore:

```text
Time  = O(N*M)
Space = O(N*M)
```

---

## 4.8 C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long MOD = 1000000007LL;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<string> grid(n);

    for (int i = 0; i < n; i++) {
        cin >> grid[i];
    }

    vector<vector<long long>> dp(n, vector<long long>(m, 0));

    if (grid[0][0] != '#') {
        dp[0][0] = 1;
    }

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (grid[i][j] == '#') {
                dp[i][j] = 0;
                continue;
            }

            if (i == 0 && j == 0) {
                continue;
            }

            if (i > 0) {
                dp[i][j] += dp[i - 1][j];
            }

            if (j > 0) {
                dp[i][j] += dp[i][j - 1];
            }

            dp[i][j] %= MOD;
        }
    }

    cout << dp[n - 1][m - 1] << '\n';

    return 0;
}
```

---

# 5. Problem 2 — Maximum-Sum Path in a Grid

## 5.1 Problem

Given an `N x M` grid of values.

Start:

```text
(1,1)
```

Destination:

```text
(N,M)
```

Moves:

```text
RIGHT
DOWN
```

Find the maximum path sum.

---

## 5.2 Decide Form

The lecture defines:

```text
dp(i,j)
=
maximum sum path
from (i,j)
to (N,M)
```

This looks forward.

So use:

```text
Form 1
```

ASCII:

```text
            RIGHT
     (i,j) -------> (i,j+1)
       |
       |
       v
    (i+1,j)
      DOWN
```

---

## 5.3 State Space Formulation

```text
dp(i,j)
=
maximum sum obtainable
starting from (i,j)
and finishing at destination
```

Current cell must be included.

So:

```text
dp(i,j)
=
grid[i][j]
+
best future path
```

---

## 5.4 Transition Design

Possible next states:

```text
dp(i+1,j)
dp(i,j+1)
```

Choose the maximum:

```text
dp(i,j)
=
grid[i][j]
+
max(
    dp(i+1,j),
    dp(i,j+1)
)
```

ASCII:

```text
             (i,j)
             /   \
            /     \
         DOWN     RIGHT
          /         \
   dp(i+1,j)     dp(i,j+1)

             |
             v

           MAX
```

---

## 5.5 Code Structure

For recursive DP, keep this exact order:

```text
1. Pruning
2. Base Case
3. Cache Check
4. Compute / Transition
5. Save and Return
```

### Pruning

Outside grid:

```text
return -INF
```

Do not return `0`, because values can be negative.

### Base Case

At destination:

```text
return grid[n-1][m-1]
```

---

## 5.6 Dry Run

Example:

```text
1 2 3
4 5 6
7 8 9
```

Destination:

```text
dp(2,2) = 9
```

Then:

```text
dp(2,1)
=
8 + 9
=
17
```

```text
dp(1,2)
=
6 + 9
=
15
```

For the center:

```text
dp(1,1)
=
5 + max(dp(2,1), dp(1,2))
=
5 + max(17,15)
=
22
```

Continue until:

```text
dp(0,0)
```

which is the answer.

---

## 5.7 Time Complexity

```text
States = N*M
Transitions per state = 2

Time = O(N*M)
Space = O(N*M)
Recursion depth = O(N+M)
```

---

## 5.8 C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long NEG_INF = -(1LL << 60);

int n, m;
vector<vector<long long>> grid;
vector<vector<long long>> dp;
vector<vector<int>> visited;

long long rec(int i, int j) {
    // 1. Pruning
    if (i >= n || j >= m) {
        return NEG_INF;
    }

    // 2. Base Case
    if (i == n - 1 && j == m - 1) {
        return grid[i][j];
    }

    // 3. Cache Check
    if (visited[i][j]) {
        return dp[i][j];
    }

    // 4. Compute / Transition
    long long down = rec(i + 1, j);
    long long right = rec(i, j + 1);

    long long ans = grid[i][j] + max(down, right);

    // 5. Save and Return
    visited[i][j] = 1;
    return dp[i][j] = ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> m;

    grid.assign(n, vector<long long>(m));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cin >> grid[i][j];
        }
    }

    dp.assign(n, vector<long long>(m, 0));
    visited.assign(n, vector<int>(m, 0));

    cout << rec(0, 0) << '\n';

    return 0;
}
```

---

# 6. Problem 3 — Maximum Area Square of 1s

## 6.1 Problem

Given a binary matrix containing:

```text
0
1
```

find the area of the largest square containing only `1`s.

Example:

```text
0 1 1 0 0
1 1 1 1 0
1 1 1 1 0
0 1 1 1 1
```

---

## 6.2 Decide Form

The lecture idea is:

```text
dp(i,j)
=
side length of the largest
all-1 square
with (i,j) as the bottom-right corner
```

This is an:

```text
Ending Form
```

ASCII:

```text
+-------+
| 1 1 1 |
| 1 1 1 |
| 1 1 X |
+-------+
        ^
        |
      (i,j)
```

---

## 6.3 State Space Formulation

If:

```text
grid[i][j] == 0
```

then:

```text
dp(i,j) = 0
```

If:

```text
grid[i][j] == 1
```

then inspect:

```text
top
left
top-left
```

---

## 6.4 Transition Design

ASCII:

```text
          top-left     top
              \         |
               \        |
                v       v
              +---+---+
              | A | B |
              +---+---+
              | C | X |
              +---+---+
                ^   ^
                |   |
              left current
```

Let:

```text
A = dp(i-1,j-1)
B = dp(i-1,j)
C = dp(i,j-1)
```

Then:

```text
dp(i,j)
=
1 + min(A,B,C)
```

Full recurrence:

```text
if grid[i][j] == 0:

    dp(i,j) = 0

else:

    dp(i,j)
    =
    1 + min(
        dp(i-1,j),
        dp(i,j-1),
        dp(i-1,j-1)
    )
```

---

## 6.5 Why `min`?

Suppose:

```text
top      = 3
left     = 3
diagonal = 2
```

Can current cell create a square of side `4`?

No.

The diagonal only supports a square of side `2`.

So:

```text
dp(i,j)
=
1 + min(3,3,2)
=
3
```

The weakest required neighbour limits the square.

---

## 6.6 Dry Run

Grid:

```text
1 1 0 1
1 1 1 1
0 1 1 1
```

DP becomes:

```text
1 1 0 1
1 2 1 1
0 1 2 2
```

For cell `(1,1)`:

```text
top      = 1
left     = 1
diagonal = 1
```

So:

```text
dp(1,1)
=
1 + min(1,1,1)
=
2
```

Meaning:

```text
largest square ending here
has SIDE LENGTH = 2
```

ASCII:

```text
1 1
1 X
```

Maximum side in the DP table:

```text
2
```

Therefore area:

```text
2 * 2 = 4
```

Important:

```text
dp stores side length

answer asks area
```

---

## 6.7 Time Complexity

```text
States = N*M
Transitions per state = 3

Time = O(N*M)
Space = O(N*M)
```

---

## 6.8 C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<int>> grid(n, vector<int>(m));

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cin >> grid[i][j];
        }
    }

    vector<vector<int>> dp(n, vector<int>(m, 0));

    int maxSide = 0;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (grid[i][j] == 0) {
                dp[i][j] = 0;
                continue;
            }

            if (i == 0 || j == 0) {
                dp[i][j] = 1;
            } else {
                dp[i][j] = 1 + min({
                    dp[i - 1][j],
                    dp[i][j - 1],
                    dp[i - 1][j - 1]
                });
            }

            maxSide = max(maxSide, dp[i][j]);
        }
    }

    cout << maxSide * maxSide << '\n';

    return 0;
}
```

---

# 7. Recognition Patterns

When you see:

```text
"How many ways can I reach this cell?"
```

think:

```text
Form 2

dp(i,j)
=
sum of previous valid states
```

When you see:

```text
"What is the best answer from this cell to destination?"
```

think:

```text
Form 1

dp(i,j)
=
current contribution
+
best future state
```

When you see:

```text
"Largest square ending here"
```

think:

```text
top
left
diagonal
```

and usually:

```text
1 + min(...)
```

---

# 8. Common Mistakes

## 1. State meaning is unclear

Always write first:

```text
dp(i,j) = ...
```

in plain English.

---

## 2. Mixing Form 1 and Form 2

```text
STARTING from (i,j)
→ look forward

ENDING at (i,j)
→ look backward
```

---

## 3. Invalid max path returns 0

Wrong:

```cpp
return 0;
```

Use:

```cpp
return NEG_INF;
```

for invalid max-sum states.

---

## 4. Blocked cell contributes paths

Wrong.

Blocked cell:

```text
dp = 0
```

---

## 5. Maximum square uses `max`

Wrong.

Correct:

```text
1 + min(top,left,diagonal)
```

---

## 6. Returning side instead of area

If:

```text
maxSide = 4
```

answer is:

```text
16
```

not `4`.

---

# 9. Final Revision

```text
GRID DP
══════════════════════════════════

FIRST QUESTION:

What does dp(i,j) mean?


FORM 1
──────────────────────────────────

dp(i,j)
=
answer STARTING from (i,j)

Look forward:

dp(i+1,j)
dp(i,j+1)


FORM 2
──────────────────────────────────

dp(i,j)
=
answer ENDING at (i,j)

Look backward:

dp(i-1,j)
dp(i,j-1)


COUNT PATHS
──────────────────────────────────

dp(i,j)
=
dp(i-1,j)
+
dp(i,j-1)

Blocked:

dp(i,j) = 0

Answer:

dp(N,M)

Time:

O(N*M)


MAXIMUM-SUM PATH
──────────────────────────────────

dp(i,j)
=
grid[i][j]
+
max(
    dp(i+1,j),
    dp(i,j+1)
)

Invalid:

-INF

Answer:

dp(0,0)

Time:

O(N*M)


RECURSIVE CODE ORDER
──────────────────────────────────

1. Pruning
2. Base Case
3. Cache Check
4. Compute / Transition
5. Save and Return


MAXIMUM SQUARE OF 1s
──────────────────────────────────

dp(i,j)
=
largest square side
ending at (i,j)

If grid[i][j] == 0:

dp(i,j) = 0

Else:

dp(i,j)
=
1 + min(
    dp(i-1,j),
    dp(i,j-1),
    dp(i-1,j-1)
)

Area:

maxSide * maxSide

Time:

O(N*M)
```
