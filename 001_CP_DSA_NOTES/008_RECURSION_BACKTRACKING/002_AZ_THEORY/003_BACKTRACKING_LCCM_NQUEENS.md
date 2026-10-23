# Backtracking Foundation — LCCM Framework with N-Queens

> **Goal:** Learn backtracking as a reusable recursive design pattern using **LCCM**:
>
> **L = Level → C = Choice → C = Check → M = Move**
>
> The N-Queens problem is used as the main example because it shows all four parts clearly.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
   - [1.1 Recursion contract](#11-recursion-contract)
   - [1.2 State and partial solution](#12-state-and-partial-solution)
   - [1.3 Recursion tree](#13-recursion-tree)
   - [1.4 Backtracking vs brute force](#14-backtracking-vs-brute-force)
   - [1.5 Coordinates on a chessboard](#15-coordinates-on-a-chessboard)
   - [1.6 How diagonals work](#16-how-diagonals-work)
   - [1.7 Place → Recurse → Undo](#17-place--recurse--undo)
2. [Introduction to the LCCM Framework](#2-introduction-to-the-lccm-framework)
3. [The N-Queens Problem](#3-the-n-queens-problem)
4. [What Is the State?](#4-what-is-the-state)
5. [The Recursive Invariant](#5-the-recursive-invariant)
6. [Understanding LCCM for N-Queens](#6-understanding-lccm-for-n-queens)
7. [Level — What Part Are We Deciding?](#7-level--what-part-are-we-deciding)
8. [Choice — What Can We Try?](#8-choice--what-can-we-try)
9. [Check — Is This Choice Valid?](#9-check--is-this-choice-valid)
10. [Move — Place, Recurse, Unplace](#10-move--place-recurse-unplace)
11. [General LCCM Template](#11-general-lccm-template)
12. [Complete C++17 Implementation](#12-complete-c17-implementation)
13. [Step-by-Step Dry Run for N = 4](#13-step-by-step-dry-run-for-n--4)
14. [Recursion Tree for N = 4](#14-recursion-tree-for-n--4)
15. [Why the Algorithm Is Correct](#15-why-the-algorithm-is-correct)
16. [Complexity](#16-complexity)
17. [O(1) Check Optimization](#17-o1-check-optimization)
18. [Changing What the Recursion Produces](#18-changing-what-the-recursion-produces)
19. [Common Mistakes](#19-common-mistakes)
20. [LCCM Recognition Checklist](#20-lccm-recognition-checklist)
21. [Final Mental Model](#21-final-mental-model)

---

# 1. Prerequisites

Before studying backtracking, make sure these ideas are comfortable.

---

## 1.1 Recursion contract

A recursive function should have a precise meaning.

For N-Queens we will use:

```text
rec(level)

= number of valid ways to complete rows
  level, level + 1, ..., N - 1

given that rows
0, 1, ..., level - 1
have already been placed validly.
```

This sentence is extremely important.

It tells us:

- what `level` means,
- what has already been decided,
- what still remains,
- what the function returns.

### Tiny example

Suppose:

```text
N = 4
queens = [1, 3, -1, -1]
level = 2
```

Interpretation:

```text
row 0 → queen at column 1
row 1 → queen at column 3
row 2 → undecided
row 3 → undecided
```

So:

```text
rec(2)
```

means:

> Count all valid ways to finish rows `2` and `3`, assuming the first two placements are already valid.

---

## 1.2 State and partial solution

A **state** contains all information needed to continue the search.

A **partial solution** is a solution that has been built only up to the current recursion level.

For N-Queens, a full `N × N` board is unnecessary.

Instead we store:

```text
queens[row] = column where the queen of this row is placed
```

Example:

```text
queens = [1, 3, -1, -1]
```

means:

```text
row 0 → col 1
row 1 → col 3
row 2 → undecided
row 3 → undecided
```

ASCII board:

```text
        col
        0 1 2 3
      +---------
row 0 | . Q . .
row 1 | . . . Q
row 2 | . . . .
row 3 | . . . .
```

This is called **state compression**:

```text
N × N board
     ↓
1D array of size N
```

because exactly one queen will be placed in every row.

---

## 1.3 Recursion tree

A recursion tree shows **all calls / choices** created by the recursive search.

Example idea:

```text
rec(0)
├── choose col 0
│   └── rec(1)
├── choose col 1
│   └── rec(1)
├── choose col 2
│   └── rec(1)
└── choose col 3
    └── rec(1)
```

Each node represents a state.

Each edge represents one choice.

A branch stops when:

```text
1. a choice becomes invalid
or
2. a complete solution is reached
```

This is the natural visualization for backtracking.

---

## 1.4 Backtracking vs brute force

Brute force:

```text
generate every possible complete candidate
→ check it afterward
```

Backtracking:

```text
build the candidate step by step
→ reject an invalid partial candidate immediately
```

Example for N-Queens:

### Pure brute force idea

Choose a column for every row:

```text
N choices per row
N rows

rough search space = N^N
```

Then validate the whole board.

### Backtracking

After placing each queen:

```text
check immediately
```

If invalid:

```text
do not generate anything below that state
```

This is called **pruning**.

ASCII:

```text
                 start
              /    |    \
             /     |     \
          valid  invalid  valid
            |       X       |
            v               v
         explore          explore
```

The `X` branch disappears completely.

---

## 1.5 Coordinates on a chessboard

We use zero-based coordinates:

```text
(row, col)
```

For a 4 × 4 board:

```text
       col
       0   1   2   3

row 0 (0,0)(0,1)(0,2)(0,3)
row 1 (1,0)(1,1)(1,2)(1,3)
row 2 (2,0)(2,1)(2,2)(2,3)
row 3 (3,0)(3,1)(3,2)(3,3)
```

Because our recursion places exactly one queen in each row:

```text
row = level
```

So the only decision is:

```text
which column?
```

---

## 1.6 How diagonals work

Two cells:

```text
(r1, c1)
(r2, c2)
```

are on the same diagonal exactly when:

```text
abs(r1 - r2) == abs(c1 - c2)
```

### Example 1

```text
(0, 1)
(2, 3)
```

Row difference:

```text
abs(0 - 2) = 2
```

Column difference:

```text
abs(1 - 3) = 2
```

Therefore:

```text
same diagonal
```

### Example 2

```text
(0, 1)
(2, 0)
```

Differences:

```text
row diff = 2
col diff = 1
```

Therefore:

```text
not same diagonal
```

ASCII:

```text
Q . . .        queen at (0,0)
. Q . .        queen at (1,1)
. . Q .
. . . Q

row difference == column difference
```

---

## 1.7 Place → Recurse → Undo

This is the heart of backtracking.

Suppose row `level` tries column `choice`.

### Step 1 — Place

```cpp
queens[level] = choice;
```

The shared state now includes this decision.

### Step 2 — Recurse

```cpp
ans += rec(level + 1);
```

Explore every full solution that begins with this choice.

### Step 3 — Undo

```cpp
queens[level] = -1;
```

Restore the exact old state before trying the next choice.

ASCII:

```text
state before
[-1, -1, -1, -1]
       |
       | place row 0 at col 1
       v
[ 1, -1, -1, -1]
       |
       | recurse
       v
   explore subtree
       |
       | return
       v
[ 1, -1, -1, -1]
       |
       | undo
       v
[-1, -1, -1, -1]
```

Why undo?

Because the next sibling branch must start from the same parent state.

```text
             parent
            /      \
      choice A    choice B
```

If choice A is not undone, choice B inherits incorrect state.

---

# 2. Introduction to the LCCM Framework

Backtracking problems can look very different:

- place queens,
- solve Sudoku,
- generate permutations,
- choose subsets,
- build paths,
- construct strings,
- partition arrays.

But the recursive structure is often the same.

We organize it as:

```text
L → Level
C → Choice
C → Check
M → Move
```

Meaning:

```text
1. LEVEL
   Decide which part of the solution is being built now.

2. CHOICE
   Enumerate every possible decision for this level.

3. CHECK
   Reject choices that cannot belong to a valid solution.

4. MOVE
   Apply the choice → recurse → undo.
```

Core picture:

```text
                   LEVEL
                     |
                     v
                  CHOICE
                     |
                     v
                  CHECK
                 /     \
              false    true
                X        |
                         v
                        MOVE
              place → recurse → undo
```

---

# 3. The N-Queens Problem

Given an:

```text
N × N chessboard
```

place exactly:

```text
N queens
```

such that no two queens attack each other.

A queen attacks another queen if they share:

```text
1. the same row
2. the same column
3. the same diagonal
```

We want to count the number of valid arrangements.

---

## Key construction decision

Instead of placing queens in arbitrary cells, place:

```text
exactly one queen in every row
```

Then:

```text
same-row conflict becomes impossible automatically
```

So when placing a new queen, only check:

```text
same column
same diagonal
```

This is an important algorithm-design principle:

> Choose a representation / construction that makes some constraints automatically true.

---

# 4. What Is the State?

A direct representation would store:

```text
board[N][N]
```

But that stores much more information than recursion needs.

Because one queen is placed per row, use:

```cpp
queens[row] = chosenColumn;
```

Unfinished rows contain:

```text
-1
```

Example:

```text
N = 4

queens = [1, 3, -1, -1]
```

Board:

```text
       0 1 2 3
     +---------
  0  | . Q . .
  1  | . . . Q
  2  | . . . .
  3  | . . . .
```

Array:

```text
row:      0   1   2   3
          ↓   ↓   ↓   ↓
queens = [1,  3, -1, -1]
          ↑   ↑
         col col
```

So:

```text
index = row
value = queen's column
```

---

# 5. The Recursive Invariant

Before:

```text
rec(level)
```

starts, assume:

```text
1. rows 0 ... level - 1 are already decided
2. each decided row contains exactly one queen
3. those queens do not attack each other
4. rows level ... N - 1 are undecided
```

The job of:

```text
rec(level)
```

is:

```text
count every valid way to complete rows
level ... N - 1
```

This invariant makes the entire algorithm easier to reason about.

---

# 6. Understanding LCCM for N-Queens

| Part | Question | N-Queens answer |
|---|---|---|
| **Level** | What are we deciding now? | Current row |
| **Choice** | What can we try here? | Every column `0 ... N-1` |
| **Check** | Is the choice valid? | No same column / diagonal with earlier queens |
| **Move** | How do we explore it? | Place → recurse → unplace |

Visual:

```text
                   LEVEL
               current row
                    |
                    v
                  CHOICE
             try every column
                    |
                    v
                   CHECK
          safe column + diagonal?
             /             \
           no               yes
           X                 |
                             v
                            MOVE
                  queens[level] = col
                         rec(level+1)
                  queens[level] = -1
```

---

# 7. Level — What Part Are We Deciding?

Use:

```text
level = current row
```

Therefore:

```text
rec(0) → decide row 0
rec(1) → decide row 1
rec(2) → decide row 2
...
rec(N) → all rows are finished
```

Progress is automatic:

```text
level
  |
  v
level + 1
```

So recursion always moves toward the base case.

---

# 8. Choice — What Can We Try?

At the current row, the queen could potentially be placed in any column:

```text
0, 1, 2, ..., N - 1
```

Code:

```cpp
for (int choice = 0; choice < n; choice++) {
    ...
}
```

Important:

```text
CHOICE means "candidate decision"
not "guaranteed valid decision"
```

Every candidate is tried.

The `check()` decides which ones survive.

---

# 9. Check — Is This Choice Valid?

Suppose we want to place a queen at:

```text
(row, col)
```

Only earlier rows contain queens:

```text
0 ... row - 1
```

For each:

```text
(previousRow, previousCol)
```

test two conflicts.

---

## 9.1 Same column

Conflict if:

```text
previousCol == col
```

Example:

```text
Q at (0, 2)
new Q at (3, 2)

same column 2
→ invalid
```

ASCII:

```text
. . Q .
. . . .
. . . .
. . Q .
    ↑
 same col
```

---

## 9.2 Same diagonal

Conflict if:

```text
abs(row - previousRow) == abs(col - previousCol)
```

Example:

```text
previous = (0, 1)
current  = (2, 3)

row difference = 2
col difference = 2

→ diagonal conflict
```

ASCII:

```text
. Q . .
. . Q .
. . . Q
. . . .
```

---

## 9.3 Why no row check?

Because:

```text
one recursion level = one row
```

and we place at most one queen before moving to the next row.

So same-row conflict is impossible by construction.

---

## Check function

```cpp
bool check(int row, int col) {
    for (int previousRow = 0; previousRow < row; previousRow++) {
        int previousCol = queens[previousRow];

        if (previousCol == col) {
            return false;
        }

        if (abs(row - previousRow) == abs(col - previousCol)) {
            return false;
        }
    }

    return true;
}
```

---

# 10. Move — Place, Recurse, Unplace

Once:

```text
check(level, choice) == true
```

we perform:

```text
PLACE
RECURSE
UNPLACE
```

Code:

```cpp
queens[level] = choice;
ans += rec(level + 1);
queens[level] = -1;
```

---

## Why this is called "Move"

The state changes from:

```text
before choice
```

to:

```text
after choice
```

Then recursion explores that new state.

Finally, backtracking restores the old state.

ASCII:

```text
Parent state
[1, -1, -1, -1]

try col = 3
      |
      v
PLACE
[1, 3, -1, -1]
      |
      v
RECURSE
rec(2)
      |
      v
all descendants explored
      |
      v
UNPLACE
[1, -1, -1, -1]
      |
      v
try next column
```

This restoration is the **essence of backtracking**.

---

# 11. General LCCM Template

## Conceptual template

```text
rec(level):

    if all levels are finished:
        return contribution of one complete solution

    answer = 0

    for every choice at this level:

        if choice is valid:

            place choice

            answer += rec(level + 1)

            undo choice

    return answer
```

---

## Compact template

```text
rec(level):

    BASE CASE

    for each CHOICE:

        if CHECK(choice):

            MOVE / place

            rec(nextLevel)

            UNDO
```

---

## Tree interpretation

```text
                         rec(level)
                       /     |      \
                    ch0     ch1      ch2
                     |       X        |
                    check             check
                     |                 |
                  rec(level+1)      rec(level+1)
```

The loop creates siblings.

The recursive call creates the next depth.

---

# 12. Complete C++17 Implementation

```cpp
#include <bits/stdc++.h>
using namespace std;

const int MAX_N = 10;

int n;
int queens[MAX_N];

bool check(int row, int col) {
    for (int previousRow = 0; previousRow < row; previousRow++) {
        int previousCol = queens[previousRow];

        if (previousCol == col) {
            return false;
        }

        if (abs(row - previousRow) == abs(col - previousCol)) {
            return false;
        }
    }

    return true;
}

long long rec(int level) {
    // Meaning:
    // Rows [0 ... level - 1] are already valid.
    // Count all valid ways to finish rows [level ... n - 1].

    if (level == n) {
        return 1;
    }

    long long ans = 0;

    for (int choice = 0; choice < n; choice++) {
        if (check(level, choice)) {
            queens[level] = choice;

            ans += rec(level + 1);

            queens[level] = -1;
        }
    }

    return ans;
}

void solve() {
    cin >> n;

    fill(queens, queens + MAX_N, -1);

    cout << rec(0) << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    solve();

    return 0;
}
```

---

## Code → LCCM mapping

```cpp
rec(level)
```

means:

```text
LEVEL = level / current row
```

This loop:

```cpp
for (int choice = 0; choice < n; choice++)
```

means:

```text
CHOICE = every possible column
```

This condition:

```cpp
if (check(level, choice))
```

means:

```text
CHECK = verify column + diagonal safety
```

These lines:

```cpp
queens[level] = choice;
ans += rec(level + 1);
queens[level] = -1;
```

mean:

```text
MOVE =
place
→ recurse
→ unplace
```

---

# 13. Step-by-Step Dry Run for N = 4

For:

```text
N = 4
```

there are two solutions:

```text
[1, 3, 0, 2]
[2, 0, 3, 1]
```

Meaning:

```text
[1,3,0,2]

row 0 → col 1
row 1 → col 3
row 2 → col 0
row 3 → col 2
```

Board:

```text
. Q . .
. . . Q
Q . . .
. . Q .
```

---

## 13.1 Initial state

```text
queens = [-1, -1, -1, -1]

rec(0)
```

At:

```text
level = 0
```

every column is initially possible.

---

## 13.2 A successful branch

We trace the first solution:

```text
[1, 3, 0, 2]
```

Compact trace:

```text
Level 0: try col 1
→ valid
→ queens = [1, -1, -1, -1]
→ rec(1)

Level 1: try col 0
→ diagonal conflict with (0,1)
→ reject

Level 1: try col 1
→ same column
→ reject

Level 1: try col 2
→ diagonal conflict with (0,1)
→ reject

Level 1: try col 3
→ valid
→ queens = [1, 3, -1, -1]
→ rec(2)

Level 2: try col 0
→ valid
→ queens = [1, 3, 0, -1]
→ rec(3)

Level 3: try col 2
→ valid
→ queens = [1, 3, 0, 2]
→ rec(4)

Level 4 == N
→ complete solution
→ return 1
```

---

## 13.3 Why base case returns 1

At:

```text
rec(4)
```

we do not need to check the whole board again.

Why?

Because every placement was checked before being made.

So reaching:

```text
level == n
```

already guarantees:

```text
one valid complete arrangement
```

Therefore:

```cpp
return 1;
```

---

## 13.4 Backtracking after finding the solution

After:

```text
rec(4) returns 1
```

execution goes back to:

```text
rec(3)
```

State immediately before undo:

```text
[1, 3, 0, 2]
```

Undo row 3:

```text
[1, 3, 0, -1]
```

Then try more columns for row 3.

When row 3 is exhausted:

```text
rec(3) returns
```

Then row 2 is undone:

```text
[1, 3, -1, -1]
```

This continues upward.

Visual:

```text
[1, 3, 0, 2]   complete
       |
       | undo row 3
       v
[1, 3, 0, -1]
       |
       | row 3 exhausted
       v
[1, 3, -1, -1]  undo row 2
       |
       v
continue trying row 2 choices
```

---

# 14. Recursion Tree for N = 4

A full tree is large, so focus on the important structure.

```text
rec(0)
│
├── row0 = 0
│   └── ...
│       └── 0 solutions
│
├── row0 = 1
│   │
│   └── row1 = 3
│       │
│       └── row2 = 0
│           │
│           └── row3 = 2
│               │
│               └── SOLUTION
│                   [1,3,0,2]
│
├── row0 = 2
│   │
│   └── row1 = 0
│       │
│       └── row2 = 3
│           │
│           └── row3 = 1
│               │
│               └── SOLUTION
│                   [2,0,3,1]
│
└── row0 = 3
    └── ...
        └── 0 solutions
```

Root summary:

```text
rec(0)
├── col 0 → 0 solutions
├── col 1 → 1 solution: [1,3,0,2]
├── col 2 → 1 solution: [2,0,3,1]
└── col 3 → 0 solutions

total = 2
```

---

## Recursion-tree meaning

At every node:

```text
node = partial state
```

At every edge:

```text
edge = one feasible queen placement
```

A dead end:

```text
no safe columns remain
```

A leaf at:

```text
level == N
```

means:

```text
one complete valid solution
```

---

# 15. Why the Algorithm Is Correct

We prove three things.

---

## 15.1 Every counted arrangement is valid

Initially:

```text
no queens are placed
```

so the state is valid.

Before placing each new queen:

```cpp
check(level, choice)
```

ensures it conflicts with none of the earlier queens.

Therefore after every move:

```text
all currently placed queens remain mutually non-attacking
```

The base case occurs only after all N rows contain queens.

Therefore every counted arrangement is valid.

---

## 15.2 Every valid arrangement is considered

At every row:

```text
all columns 0 ... N - 1
```

are considered.

For any real valid solution, its correct column at the current row cannot conflict with the already chosen prefix.

Therefore that choice passes `check()`.

So there exists a root-to-leaf recursion path corresponding to every valid arrangement.

---

## 15.3 No arrangement is counted twice

A complete arrangement is represented uniquely by:

```text
queens[0], queens[1], ..., queens[N - 1]
```

Two different root-to-leaf paths must differ at some row's column choice.

Therefore they cannot represent the same arrangement.

So each valid solution is counted exactly once.

---

# 16. Complexity

At row 0:

```text
up to N choices
```

At row 1, same-column rejection leaves at most:

```text
N - 1
```

At row 2:

```text
N - 2
```

and so on.

Ignoring diagonal pruning, the number of candidate arrangements is bounded by:

```text
N!
```

The basic `check()` scans earlier rows:

```text
O(N)
```

Therefore a conservative upper bound is:

```text
O(N × N!)
```

Auxiliary state:

```text
queens array = O(N)
```

Maximum recursion depth:

```text
N
```

so recursion stack:

```text
O(N)
```

---

# 17. O(1) Check Optimization

The lecture version uses:

```text
scan all previous rows
```

which is excellent for learning.

Later, the check can be improved from:

```text
O(N)
```

to:

```text
O(1)
```

by storing which columns and diagonals are occupied.

---

## 17.1 Column identifier

```text
column = col
```

Use:

```cpp
usedCol[col]
```

---

## 17.2 Main diagonal identifier

Cells on the same `\` diagonal share:

```text
row - col
```

Because it may be negative, shift it:

```text
row - col + N - 1
```

---

## 17.3 Anti-diagonal identifier

Cells on the same `/` diagonal share:

```text
row + col
```

---

## O(1) checker

```cpp
bool usedCol[20];
bool usedDiag1[40];
bool usedDiag2[40];

bool checkFast(int row, int col) {
    int d1 = row - col + n - 1;
    int d2 = row + col;

    return !usedCol[col] && !usedDiag1[d1] && !usedDiag2[d2];
}
```

Move:

```cpp
usedCol[col] = true;
usedDiag1[d1] = true;
usedDiag2[d2] = true;

rec(row + 1);

usedCol[col] = false;
usedDiag1[d1] = false;
usedDiag2[d2] = false;
```

Notice that the LCCM structure does **not change**.

Only the implementation of:

```text
CHECK
```

becomes faster.

---

# 18. Changing What the Recursion Produces

The same LCCM search can solve several variants.

---

## 18.1 Count all solutions

Base case:

```cpp
return 1;
```

Combine children:

```cpp
ans += rec(level + 1);
```

---

## 18.2 Find any one solution

Return:

```text
bool
```

and stop after the first successful child.

Concept:

```text
if child succeeds:
    return true
```

---

## 18.3 Print every solution

At the base case:

```text
print queens[]
```

then continue exploring.

---

## 18.4 Find the best solution

Instead of:

```text
sum child answers
```

use:

```text
min(...)
```

or:

```text
max(...)
```

depending on the objective.

The search skeleton remains:

```text
LEVEL
CHOICE
CHECK
MOVE
```

What changes is mainly:

```text
base-case contribution
+
how child answers are combined
```

---

# 19. Common Mistakes

## Mistake 1 — Returning 0 at a complete solution

Wrong:

```cpp
if (level == n) {
    return 0;
}
```

For a counting problem, one complete valid configuration contributes:

```text
1
```

Correct:

```cpp
return 1;
```

---

## Mistake 2 — Forgetting to undo

Wrong:

```cpp
queens[level] = choice;
ans += rec(level + 1);
```

Missing:

```cpp
queens[level] = -1;
```

Without undo:

```text
state from one branch leaks into another
```

---

## Mistake 3 — Wrong diagonal formula

Correct:

```cpp
abs(row - previousRow) == abs(col - previousCol)
```

---

## Mistake 4 — Checking future rows

Only rows:

```text
0 ... level - 1
```

have queens.

Rows:

```text
level ... N - 1
```

are still `-1`.

---

## Mistake 5 — Not defining `rec(level)`

If you cannot say:

```text
"rec(level) returns ..."
```

in one clear sentence, the recursive code is likely to become confusing.

---

## Mistake 6 — Confusing tree size with recursion depth

The recursion tree can have many nodes:

```text
~ N!
```

while the maximum active stack path is only:

```text
N
```

These are different:

```text
time → total explored states
space → maximum simultaneously active states
```

---

## Mistake 7 — Putting the undo in the wrong place

Correct:

```cpp
for each choice {
    if (valid) {
        place;
        recurse;
        undo;
    }
}
```

Not:

```cpp
place;
for all choices...
undo;
```

The undo belongs to the exact choice whose state change it reverses.

---

# 20. LCCM Recognition Checklist

When you see a recursion / backtracking problem, ask:

1. **State** — What information completely describes my current partial solution?
2. **Level** — What single decision am I making now?
3. **Choice** — What are all candidate decisions?
4. **Check** — Which candidates are still feasible?
5. **Move** — What state changes when I choose one?
6. **Undo** — What exact inverse operation restores the parent state?
7. **Base case** — When is the solution complete?
8. **Return meaning** — What exactly does the recursive function return?
9. **Combination** — Sum, OR, min, max, first successful child, or printing?
10. **Pruning** — Can I reject a branch earlier?

---

# 21. Final Mental Model

Do not memorize N-Queens code.

Memorize the structure:

```text
BACKTRACKING
│
├── STATE
│   └── what has already been decided?
│
├── LEVEL
│   └── what part am I deciding now?
│
├── CHOICE
│   └── what can I try?
│
├── CHECK
│   └── can this choice still lead to a valid answer?
│
└── MOVE
    ├── place
    ├── recurse
    └── undo
```

For N-Queens:

```text
STATE  = queens[row] = selected column
LEVEL  = current row
CHOICE = every column
CHECK  = no same column / diagonal
MOVE   = place → rec(next row) → unplace
```

The recursion tree then becomes:

```text
                    current state
                   /      |      \
              choice 0 choice 1 choice 2 ...
                 |       X        |
              recurse           recurse
                 |                |
              deeper           deeper
```

And the most important backtracking rule is:

```text
Every recursive branch must receive
the exact state that belongs to that branch.

PLACE → RECURSE → UNDO
```

Once this pattern becomes automatic, the same LCCM framework transfers directly to:

```text
subsets
permutations
Sudoku
word search
combination sum
graph/path search
partitioning
palindrome partitioning
N-Queens
many constructive DFS problems
```
