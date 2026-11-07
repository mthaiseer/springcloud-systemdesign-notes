# 018 — DP Form 2: Ending Form
## Longest Increasing Subsequence + Maximum-Sum Grid Path


<!-- TOC START -->
# Table of Contents

  - [Longest Increasing Subsequence + Maximum-Sum Grid Path](#longest-increasing-subsequence-maximum-sum-grid-path)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Subsequence](#11-subsequence)
  - [1.2 Increasing subsequence](#12-increasing-subsequence)
  - [1.3 Memoized-DP complexity rule](#13-memoized-dp-complexity-rule)
- [2. What Is Form 2?](#2-what-is-form-2)
- [3. Problem 1 — Longest Increasing Subsequence](#3-problem-1-longest-increasing-subsequence)
- [4. First State Idea — `dp(level, lastTaken)`](#4-first-state-idea-dplevel-lasttaken)
- [5. Ending-Form Observation](#5-ending-form-observation)
- [6. Step 1 — Decide the Form](#6-step-1-decide-the-form)
- [7. Step 2 — LIS State Space Formulation](#7-step-2-lis-state-space-formulation)
- [8. Step 3 — LIS Transition Design](#8-step-3-lis-transition-design)
- [9. LIS Recurrence](#9-lis-recurrence)
- [10. LIS Dry Run](#10-lis-dry-run)
- [11. Step 4 — LIS Time Complexity](#11-step-4-lis-time-complexity)
- [12. LIS C++17 Code](#12-lis-c17-code)
- [13. Why Final Answer Is `max(dp(i))`](#13-why-final-answer-is-maxdpi)
- [14. Problem 2 — Maximum-Sum Grid Path](#14-problem-2-maximum-sum-grid-path)
- [15. Step 1 — Decide the Form for Grid](#15-step-1-decide-the-form-for-grid)
- [16. Step 2 — Grid State Space Formulation](#16-step-2-grid-state-space-formulation)
- [17. Step 3 — Grid Transition Design](#17-step-3-grid-transition-design)
- [18. Grid Recurrence](#18-grid-recurrence)
- [19. Grid Base Case](#19-grid-base-case)
- [20. Step 4 — Grid Time Complexity](#20-step-4-grid-time-complexity)
- [21. Grid C++17 Code](#21-grid-c17-code)
- [22. Form 2 Recognition Rule](#22-form-2-recognition-rule)
- [23. Form 1 vs Form 2](#23-form-1-vs-form-2)
- [24. Common Mistakes](#24-common-mistakes)
  - [Mistake 1 — Wrong LIS state meaning](#mistake-1-wrong-lis-state-meaning)
  - [Mistake 2 — Forgetting current element](#mistake-2-forgetting-current-element)
  - [Mistake 3 — Wrong inequality](#mistake-3-wrong-inequality)
  - [Mistake 4 — Returning only `rec(n-1)`](#mistake-4-returning-only-recn-1)
  - [Mistake 5 — Wrong direction of thinking](#mistake-5-wrong-direction-of-thinking)
  - [Mistake 6 — Grid answer initialized to zero](#mistake-6-grid-answer-initialized-to-zero)
- [25. Contest Checklist](#25-contest-checklist)
  - [1. Decide the Form](#1-decide-the-form)
  - [2. State Space Formulation](#2-state-space-formulation)
  - [3. Transition Design](#3-transition-design)
  - [4. Time Complexity](#4-time-complexity)
  - [5. Code](#5-code)
- [26. Final Revision Box](#26-final-revision-box)

<!-- TOC END -->

> **Lecture theme:** Sometimes a DP problem is easier if we define:
>
> ```text
> dp(state)
> =
> best answer ENDING at this state
> ```
>
> rather than carrying the complete history in the state.
>
> The lecture calls this:
>
> ```text
> Form 2 — Ending Form
> ```
>
> The two main examples in the lecture are:
>
> 1. **Longest Increasing Subsequence (LIS)**
> 2. **Maximum-sum path in a grid**
>
> ---
>
> # Fixed DP Framework
>
> ```text
> 1. Prerequisites
> 2. Decide the Form
> 3. State Space Formulation
> 4. Transition Design
> 5. Time Complexity
> 6. C++17 Code
> ```
>
> # Fixed Recursive Code Structure
>
> ```text
> 1. Pruning
> 2. Base Case
> 3. Cache Check
> 4. Compute / Transition
> 5. Save and Return
> ```
>
> **Formatting rule:** keep simple transitions on one readable line.
>
> ```cpp
> ans = max(ans, 1 + rec(prevTaken));
> ```

---

# 1. Prerequisites

Before this lecture, be comfortable with:

```text
recursion
memoization
subsequence
take / not-take DP
maximum / optimization DP
2D grid coordinates
```

## 1.1 Subsequence

A subsequence keeps the original order but may skip elements.

Example:

```text
array:
3 2 5 4 5 7 8 1 11 9
```

A valid subsequence is:

```text
2 4 5 7 8 11
```

because the indices move left to right.

## 1.2 Increasing subsequence

A subsequence is increasing when:

```text
next value > previous value
```

Example:

```text
2 < 4 < 5 < 7 < 8 < 11
```

## 1.3 Memoized-DP complexity rule

```text
Time
=
Number of States
×
Average Transitions per State
```

---

# 2. What Is Form 2?

The key idea is:

```text
Instead of asking:

"What is the best answer from here onward?"

ask:

"What is the best answer ENDING here?"
```

This is especially useful when:

```text
current state depends on
previous positions / previous cells / previous elements
```

The lecture's final recognition idea is:

```text
If you see path / sequence / subarray dependencies,
the problem may fit Form 2.
```

---

# 3. Problem 1 — Longest Increasing Subsequence

Given:

```text
a[0], a[1], ..., a[n-1]
```

find the length of the:

```text
Longest Increasing Subsequence
```

Lecture example:

```text
3 2 5 4 5 7 8 1 11 9
```

One LIS shown is:

```text
2 4 5 7 8 11
```

Length:

```text
6
```

Remember:

```text
subsequence != subarray
```

The values do not need to be contiguous.

---

# 4. First State Idea — `dp(level, lastTaken)`

A natural Form-1 approach is:

```text
dp(level, lastTaken)
```

Meaning:

```text
best LIS we can build
from [level ... n-1]

when the last selected element/index
is represented by lastTaken
```

At every level:

```text
TAKE
or
DON'T TAKE
```

Transition idea:

```text
don'tTake = dp(level + 1, lastTaken)
```

If:

```text
a[level] > a[lastTaken]
```

then:

```text
take = 1 + dp(level + 1, level)
```

Final:

```text
answer = max(don'tTake, take)
```

This works, but the lecture introduces a cleaner Form-2 viewpoint.

---

# 5. Ending-Form Observation

Suppose we force the LIS to end at:

```text
index = level
```

Then:

```text
a[level]
```

must be included.

What can come before it?

Any index:

```text
prevTaken < level
```

with:

```text
a[prevTaken] < a[level]
```

Therefore:

```text
best LIS ending at level
```

can be created from:

```text
best LIS ending at a valid previous index
+
current element
```

This removes the `lastTaken` dimension.

---

# 6. Step 1 — Decide the Form

For LIS, use:

```text
Form 2 — Ending Form
```

Ask:

```text
What is the best increasing subsequence
ending exactly at this index?
```

---

# 7. Step 2 — LIS State Space Formulation

Define:

```text
dp(level)
=
length of the best increasing subsequence
ending exactly at index `level`
```

Important:

```text
a[level] MUST be included.
```

This is not:

```text
best LIS somewhere in [0 ... level]
```

It is:

```text
best LIS that ends at level
```

---

# 8. Step 3 — LIS Transition Design

At current index:

```text
level
```

try every previous index:

```text
prevTaken
```

such that:

```text
0 <= prevTaken < level
```

and:

```text
a[prevTaken] < a[level]
```

Then:

```text
dp(prevTaken)
```

can be extended by the current value.

Candidate:

```text
1 + dp(prevTaken)
```

Take the maximum.

---

# 9. LIS Recurrence

Every element alone gives length:

```text
1
```

So start:

```text
answer = 1
```

Then:

```text
for every prevTaken < level:

    if a[prevTaken] < a[level]:

        answer = max(answer, 1 + dp(prevTaken))
```

Compact:

```text
dp(level)
=
max(
    1,
    1 + dp(prevTaken)
)
```

over all valid previous indices.

---

# 10. LIS Dry Run

Use:

```text
2 1 5 3 6
```

State values:

```text
dp(0) = 1
```

because:

```text
[2]
```

is the only LIS ending at index 0.

```text
dp(1) = 1
```

because:

```text
2 < 1
```

is false.

For index 2, value `5`:

```text
2 < 5
1 < 5
```

Both can precede `5`.

So:

```text
dp(2) = 2
```

For index 3, value `3`:

```text
2 < 3
1 < 3
```

So:

```text
dp(3) = 2
```

For index 4, value `6`:

best valid previous LIS has length:

```text
2
```

Therefore:

```text
dp(4) = 3
```

---

# 11. Step 4 — LIS Time Complexity

States:

```text
N
```

For every state, scan previous indices:

```text
O(N)
```

Therefore:

```text
Time = O(N²)
```

Memo:

```text
O(N)
```

Recursion stack:

```text
O(N)
```

---

# 12. LIS C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> arr, dp;

int rec(int level) {
    // 1. Pruning
    if (level < 0) return 0;

    // 2. Base Case
    // Every single element itself gives LIS length 1.

    // 3. Cache Check
    int &ans = dp[level];
    if (ans != -1) return ans;

    // 4. Compute / Transition
    ans = 1;

    for (int prevTaken = 0; prevTaken < level; prevTaken++) {
        if (arr[prevTaken] < arr[level]) {
            ans = max(ans, 1 + rec(prevTaken));
        }
    }

    // 5. Save and Return
    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;

    arr.resize(n);
    dp.assign(n, -1);

    for (int i = 0; i < n; i++) {
        cin >> arr[i];
    }

    int best = 0;

    for (int i = 0; i < n; i++) {
        best = max(best, rec(i));
    }

    cout << best << '\n';

    return 0;
}
```

---

# 13. Why Final Answer Is `max(dp(i))`

`dp(i)` means:

```text
best LIS ending exactly at i
```

But the overall LIS may end anywhere.

So final answer is:

```text
max(
    dp(0),
    dp(1),
    ...,
    dp(n-1)
)
```

That is why the lecture code does:

```cpp
int best = 0;

for (int i = 0; i < n; i++) {
    best = max(best, rec(i));
}
```

A common Ending-Form pattern is:

```text
local state = answer ending here
global answer = max/min over all ending states
```

---

# 14. Problem 2 — Maximum-Sum Grid Path

The lecture next applies the same ending-form idea to a 2D grid.

We want the:

```text
maximum-sum path
```

from:

```text
(0,0)
```

to a cell:

```text
(r,c)
```

The previous cell can come from:

```text
top
```

or:

```text
left
```

Equivalent forward movement:

```text
down
or
right
```

---

# 15. Step 1 — Decide the Form for Grid

This is a:

```text
path dependency
```

problem.

Ask:

```text
What is the best path ending at this cell?
```

So use:

```text
Form 2 — Ending Form
```

---

# 16. Step 2 — Grid State Space Formulation

Define:

```text
dp(r,c)
=
maximum path sum
ending exactly at cell (r,c)

starting from (0,0)
```

The current value:

```text
grid[r][c]
```

must be included.

---

# 17. Step 3 — Grid Transition Design

To end at:

```text
(r,c)
```

the previous cell can be:

```text
(r-1,c)
```

or:

```text
(r,c-1)
```

From above:

```text
rec(r - 1, c) + grid[r][c]
```

From left:

```text
rec(r, c - 1) + grid[r][c]
```

Take the maximum.

---

# 18. Grid Recurrence

```text
answer = -INF

if r != 0:
    answer = max(answer, dp(r-1,c) + grid[r][c])

if c != 0:
    answer = max(answer, dp(r,c-1) + grid[r][c])
```

Compact:

```text
dp(r,c)
=
max(
    dp(r-1,c) + grid[r][c],
    dp(r,c-1) + grid[r][c]
)
```

using only valid predecessor cells.

---

# 19. Grid Base Case

At:

```text
(0,0)
```

the path contains only the starting cell.

So:

```text
dp(0,0) = grid[0][0]
```

---

# 20. Step 4 — Grid Time Complexity

For:

```text
R rows
C columns
```

states:

```text
R × C
```

Transitions per state:

```text
at most 2
```

Therefore:

```text
Time = O(RC)
```

Memo:

```text
O(RC)
```

---

# 21. Grid C++17 Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const long long NEG_INF = -(1LL << 60);

int rows, cols;
vector<vector<long long>> grid, dp;
vector<vector<int>> done;

long long rec(int r, int c) {
    // 1. Pruning
    if (r < 0 || c < 0) return NEG_INF;

    // 2. Base Case
    if (r == 0 && c == 0) return grid[0][0];

    // 3. Cache Check
    if (done[r][c]) return dp[r][c];

    // 4. Compute / Transition
    long long ans = NEG_INF;

    if (r != 0) {
        ans = max(ans, rec(r - 1, c) + grid[r][c]);
    }

    if (c != 0) {
        ans = max(ans, rec(r, c - 1) + grid[r][c]);
    }

    // 5. Save and Return
    done[r][c] = 1;
    return dp[r][c] = ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> rows >> cols;

    grid.assign(rows, vector<long long>(cols));
    dp.assign(rows, vector<long long>(cols, 0));
    done.assign(rows, vector<int>(cols, 0));

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            cin >> grid[r][c];
        }
    }

    cout << rec(rows - 1, cols - 1) << '\n';

    return 0;
}
```

---

# 22. Form 2 Recognition Rule

The lecture's key idea:

```text
If you see:

path
sequence
subarray

with dependencies on previous positions,

it may be Form 2.
```

Think:

```text
best answer ENDING at this state
```

Typical examples:

```text
LIS
Longest chain
Maximum-sum subsequence
Maximum / minimum path
Grid DP
DAG path DP
```

---

# 23. Form 1 vs Form 2

| Feature | Form 1 | Form 2 |
|---|---|---|
| Main viewpoint | What choices do I make from here? | What is the best answer ending here? |
| Typical decision | Take / skip | Choose predecessor |
| Typical state | `dp(level, extraState)` | `dp(index)` / `dp(r,c)` |
| Examples | Knapsack, Subset Sum | LIS, Grid Path |
| Direction | Current → future | Previous → current |

---

# 24. Common Mistakes

## Mistake 1 — Wrong LIS state meaning

Wrong:

```text
dp(i)
=
best LIS anywhere in [0...i]
```

for this recurrence.

Correct:

```text
dp(i)
=
best LIS ending exactly at i
```

---

## Mistake 2 — Forgetting current element

Every LIS ending at `i` includes:

```text
arr[i]
```

So:

```text
dp(i) >= 1
```

---

## Mistake 3 — Wrong inequality

For strictly increasing LIS:

```text
arr[prev] < arr[i]
```

not:

```text
arr[prev] <= arr[i]
```

---

## Mistake 4 — Returning only `rec(n-1)`

The overall LIS may end before the final array position.

Correct:

```text
max rec(i)
over every i
```

---

## Mistake 5 — Wrong direction of thinking

Ending form asks:

```text
Which previous states can transition INTO the current state?
```

LIS:

```text
previous index
```

Grid:

```text
top / left cell
```

---

## Mistake 6 — Grid answer initialized to zero

If grid values may be negative:

```text
0
```

could incorrectly represent a nonexistent path.

Use:

```text
NEG_INF
```

for maximum problems.

---

# 25. Contest Checklist

Use this framework.

## 1. Decide the Form

Ask:

```text
Is take / skip natural?
```

or:

```text
Would "best ending here" be simpler?
```

---

## 2. State Space Formulation

LIS:

```text
dp(i)
=
best LIS ending at i
```

Grid:

```text
dp(r,c)
=
best path ending at (r,c)
```

---

## 3. Transition Design

Find every legal predecessor.

LIS:

```text
prev < i
and
arr[prev] < arr[i]
```

Grid:

```text
(r-1,c)
(r,c-1)
```

---

## 4. Time Complexity

LIS:

```text
N states × N transitions
=
O(N²)
```

Grid:

```text
R × C states × 2 transitions
=
O(RC)
```

---

## 5. Code

Always keep:

```text
Pruning
Base Case
Cache Check
Compute / Transition
Save and Return
```

---

# 26. Final Revision Box

```text
FORM 2 — ENDING FORM
────────────────────────────────────

Main question:

"What is the best answer
ENDING at this state?"


WHEN TO THINK FORM 2
────────────────────────────────────

Path dependency

Sequence dependency

Subarray / positional dependency

Previous states naturally
transition into current state


LIS
────────────────────────────────────

State:

dp(level)

=

best LIS ending exactly at level


Transition:

for prevTaken < level:

    if arr[prevTaken] < arr[level]:

        ans = max(ans, 1 + dp(prevTaken))


Base value:

1

because current element alone
forms an LIS of length 1


Final answer:

max dp(i)
for all i


LIS COMPLEXITY
────────────────────────────────────

States:

N

Transitions/state:

O(N)

Time:

O(N²)

Space:

O(N)


GRID MAX-SUM PATH
────────────────────────────────────

State:

dp(r,c)

=

maximum-sum path
ending at (r,c)
from (0,0)


Transitions:

from top:

dp(r-1,c) + grid[r][c]

from left:

dp(r,c-1) + grid[r][c]


Base:

dp(0,0) = grid[0][0]


GRID COMPLEXITY
────────────────────────────────────

States:

R × C

Transitions/state:

2

Time:

O(RC)

Space:

O(RC)


FORM 1 VS FORM 2
────────────────────────────────────

Form 1:

"What choices do I make
from here onward?"

Form 2:

"Which previous state
can lead into me?"


CORE RECOGNITION QUESTION
────────────────────────────────────

"If I force the solution
to END at this index/cell,

can I build it from
smaller predecessor states?"

If yes,
Ending Form may be the clean DP.
```