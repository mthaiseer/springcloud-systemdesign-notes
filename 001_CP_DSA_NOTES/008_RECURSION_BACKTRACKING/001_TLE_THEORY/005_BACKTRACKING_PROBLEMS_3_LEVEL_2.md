# Backtracking Problems 3 — Level 2
## Tower of Hanoi + Unique Paths III
### TLE-Style Self-Study Notes with Prerequisites, Recursion Trees, LCCM, Dry Runs, and C++17

> **Lecture scope**
>
> This lecture contains two recursive/backtracking problems:
>
> 1. **Tower of Hanoi**
> 2. **Unique Paths III**
>
> The lecture sketches the recursive movement of disks in Tower of Hanoi and the exact-once cell-visiting requirement in Unique Paths III.
>
> The prerequisite explanations, ASCII diagrams, recursion-tree expansion, LCCM mapping, correctness arguments, dry runs, pruning notes, and polished C++17 below are **self-study expansions** built around that lecture.

---

# Table of Contents

1. [Big Picture](#1-big-picture)
2. [Prerequisite 1 — Define What the Recursive Function Means](#2-prerequisite-1--define-what-the-recursive-function-means)
3. [Prerequisite 2 — Recursion Tree vs Recursion Stack](#3-prerequisite-2--recursion-tree-vs-recursion-stack)
4. [Prerequisite 3 — State](#4-prerequisite-3--state)
5. [Prerequisite 4 — Base Case](#5-prerequisite-4--base-case)
6. [Prerequisite 5 — Work Before and After Recursion](#6-prerequisite-5--work-before-and-after-recursion)
7. [Problem 1 — Tower of Hanoi](#7-problem-1--tower-of-hanoi)
8. [Tower of Hanoi — Rules](#8-tower-of-hanoi--rules)
9. [Tower of Hanoi — What the Function Means](#9-tower-of-hanoi--what-the-function-means)
10. [Tower of Hanoi — The Core Observation](#10-tower-of-hanoi--the-core-observation)
11. [Tower of Hanoi — Why the Middle Rod Changes Roles](#11-tower-of-hanoi--why-the-middle-rod-changes-roles)
12. [Tower of Hanoi — Recursive Formula](#12-tower-of-hanoi--recursive-formula)
13. [Tower of Hanoi — Recursion Tree](#13-tower-of-hanoi--recursion-tree)
14. [Tower of Hanoi — Step-by-Step Dry Run for N = 3](#14-tower-of-hanoi--step-by-step-dry-run-for-n--3)
15. [Tower of Hanoi — Move Count Derivation](#15-tower-of-hanoi--move-count-derivation)
16. [Tower of Hanoi — C++17](#16-tower-of-hanoi--c17)
17. [Tower of Hanoi — Why It Is Correct](#17-tower-of-hanoi--why-it-is-correct)
18. [Tower of Hanoi — Complexity](#18-tower-of-hanoi--complexity)
19. [Tower of Hanoi — Recognition Model](#19-tower-of-hanoi--recognition-model)
20. [Bridge — From Pure Recursion to Backtracking](#20-bridge--from-pure-recursion-to-backtracking)
21. [Problem 2 — Unique Paths III](#21-problem-2--unique-paths-iii)
22. [Unique Paths III — Grid Meaning](#22-unique-paths-iii--grid-meaning)
23. [Unique Paths III — What Makes It Different from Normal Path Counting](#23-unique-paths-iii--what-makes-it-different-from-normal-path-counting)
24. [Unique Paths III — State Design](#24-unique-paths-iii--state-design)
25. [Unique Paths III — Exact-Once Requirement](#25-unique-paths-iii--exact-once-requirement)
26. [Unique Paths III — LCCM Mapping](#26-unique-paths-iii--lccm-mapping)
27. [Unique Paths III — Move and Undo](#27-unique-paths-iii--move-and-undo)
28. [Unique Paths III — Search Tree](#28-unique-paths-iii--search-tree)
29. [Unique Paths III — Base Case](#29-unique-paths-iii--base-case)
30. [Unique Paths III — Step-by-Step Dry Run](#30-unique-paths-iii--step-by-step-dry-run)
31. [Unique Paths III — C++17](#31-unique-paths-iii--c17)
32. [Unique Paths III — Why It Is Correct](#32-unique-paths-iii--why-it-is-correct)
33. [Unique Paths III — Complexity](#33-unique-paths-iii--complexity)
34. [Common Mistakes](#34-common-mistakes)
35. [Tower of Hanoi vs Unique Paths III](#35-tower-of-hanoi-vs-unique-paths-iii)
36. [Backtracking Design Checklist](#36-backtracking-design-checklist)
37. [Final Mental Model](#37-final-mental-model)
38. [One-Minute Revision](#38-one-minute-revision)

---

# 1. Big Picture

These two problems look very different:

```text
Tower of Hanoi
→ move disks between rods
```

```text
Unique Paths III
→ walk through a grid
```

But both are built from the same recursive thinking:

```text
Define a state
      ↓
Know exactly what the recursive function promises
      ↓
Reduce the current problem to smaller states
      ↓
Stop at a precise base case
```

The important difference is:

```text
Tower of Hanoi
=
the recursive structure itself completely determines the legal sequence
```

while:

```text
Unique Paths III
=
we must TRY several possible moves
and BACKTRACK when a move does not lead to a valid complete path
```

So:

```text
Tower of Hanoi → pure recursive decomposition

Unique Paths III → recursive search + backtracking
```

---

# 2. Prerequisite 1 — Define What the Recursive Function Means

Before writing recursive code, write one sentence.

For Tower of Hanoi:

```text
solve(n, from, to, aux)

=
move the top n disks
from rod `from`
to rod `to`
using rod `aux`
according to the rules.
```

For Unique Paths III:

```text
dfs(row, col, remaining)

=
count all valid paths starting from (row, col)
that visit every still-unvisited required cell exactly once
and finish at the destination.
```

If this sentence is unclear, the recursion usually becomes confusing.

---

# 3. Prerequisite 2 — Recursion Tree vs Recursion Stack

These are different views.

## Recursion tree

Shows:

```text
ALL recursive calls generated
```

Example:

```text
solve(3)
├── solve(2)
│   ├── solve(1)
│   └── solve(1)
└── solve(2)
    ├── solve(1)
    └── solve(1)
```

---

## Recursion stack

Shows only:

```text
the calls currently active
```

Example during one deep path:

```text
solve(3)
  solve(2)
    solve(1)
```

The stack depth is not the same thing as the total number of recursive calls.

---

# 4. Prerequisite 3 — State

A recursive state must contain enough information to describe the unfinished problem.

Examples:

## Tower of Hanoi

```text
n
from
to
aux
```

No board array is actually necessary because the legal recursive structure determines the movement.

---

## Unique Paths III

We need:

```text
current row
current column
which cells are already used
how many required cells remain
```

The grid itself can store visited state temporarily.

---

# 5. Prerequisite 4 — Base Case

A recursive function must eventually reach a directly solvable state.

Tower of Hanoi:

```text
n == 0
→ nothing to move
```

or equivalently:

```text
n == 1
→ move one disk directly
```

Unique Paths III:

```text
we arrive at the ending cell
```

But that alone is NOT enough.

We must also verify:

```text
every required non-obstacle cell has been visited exactly once
```

---

# 6. Prerequisite 5 — Work Before and After Recursion

One of the most important recursion ideas is:

```text
recursive call
does not mean "the whole function disappears"
```

The current call may still have work to do after the child call returns.

Example:

```cpp
solve(n - 1);

doCurrentMove();

solve(n - 1);
```

Visual:

```text
parent call
   |
   +-- child recursion
   |
   +-- work after child returns
   |
   +-- another recursion
```

Tower of Hanoi is a perfect example of this.

---

# 7. Problem 1 — Tower of Hanoi

We have three rods:

```text
A = starting rod
B = auxiliary / middle rod
C = destination rod
```

Initially:

```text
A                B                C

|                |                |
|      1         |                |
|     222        |                |
|    33333       |                |
+--------        +--------        +--------
```

Goal:

```text
move all disks from A to C
```

while following the Tower of Hanoi rules.

---

# 8. Tower of Hanoi — Rules

The classic rules are:

```text
1. Move only one disk at a time.

2. Only the top disk of a rod can be moved.

3. A larger disk can never be placed on a smaller disk.
```

For `n` disks:

```text
1 is smallest
n is largest
```

---

# 9. Tower of Hanoi — What the Function Means

Define:

```text
hanoi(n, from, to, aux)
```

Meaning:

```text
Move n disks
from `from`
to `to`
using `aux`.
```

This function has four pieces of state:

```text
n
from
to
aux
```

Example:

```text
hanoi(3, A, C, B)
```

means:

```text
Move 3 disks
from A to C
using B.
```

---

# 10. Tower of Hanoi — The Core Observation

Suppose the bottom disk is disk `n`.

We cannot move disk `n` until every smaller disk above it is removed.

Therefore:

```text
Step 1:
move n-1 smaller disks
from START to AUXILIARY
using DESTINATION
```

Now the largest disk becomes free.

```text
Step 2:
move disk n
from START to DESTINATION
```

Then the `n-1` smaller disks must be put on top of it.

```text
Step 3:
move n-1 smaller disks
from AUXILIARY to DESTINATION
using START
```

That is the entire recursive strategy.

ASCII:

```text
Initial:

A                 B                 C

[1]               |                 |
[2]               |                 |
[3]               |                 |

------------------------------------------

Step 1: move top n-1 from A → B

A                 B                 C

[3]              [1]                |
                 [2]                |

------------------------------------------

Step 2: move largest disk A → C

A                 B                 C

|                [1]               [3]
                 [2]

------------------------------------------

Step 3: move n-1 from B → C

A                 B                 C

|                 |                [1]
|                 |                [2]
|                 |                [3]
```

---

# 11. Tower of Hanoi — Why the Middle Rod Changes Roles

This is where many beginners get confused.

Initial call:

```text
hanoi(n, A, C, B)

from = A
to   = C
aux  = B
```

For the first recursive subproblem:

```text
move n-1 from A to B
```

Now:

```text
from = A
to   = B
aux  = C
```

So the call becomes:

```text
hanoi(n - 1, A, B, C)
```

After moving the largest disk:

```text
move n-1 from B to C
```

Now:

```text
from = B
to   = C
aux  = A
```

So:

```text
hanoi(n - 1, B, C, A)
```

The rods do not have permanent "start/middle/end" identities.

Their roles change per recursive call.

---

# 12. Tower of Hanoi — Recursive Formula

Define:

```text
T(n)
=
number of moves required for n disks
```

For `n > 0`:

```text
move n-1 disks
+
move largest disk
+
move n-1 disks
```

Therefore:

```text
T(n)
=
T(n-1) + 1 + T(n-1)
```

So:

```text
T(n) = 2T(n-1) + 1
```

Base:

```text
T(0) = 0
```

or:

```text
T(1) = 1
```

---

# 13. Tower of Hanoi — Recursion Tree

For `n = 3`:

```text
                     H(3, A, C, B)
                    /              \
          H(2, A, B, C)          H(2, B, C, A)
             /      \                /       \
      H(1,A,C,B) H(1,C,B,A)  H(1,B,A,C) H(1,A,C,B)
```

There is also one direct largest-disk move inside every non-base call.

A more explicit form:

```text
H(3,A,C,B)
|
|-- H(2,A,B,C)
|    |
|    |-- H(1,A,C,B)
|    |      A → C
|    |
|    |-- move disk 2: A → B
|    |
|    `-- H(1,C,B,A)
|           C → B
|
|-- move disk 3: A → C
|
`-- H(2,B,C,A)
     |
     |-- H(1,B,A,C)
     |      B → A
     |
     |-- move disk 2: B → C
     |
     `-- H(1,A,C,B)
            A → C
```

---

# 14. Tower of Hanoi — Step-by-Step Dry Run for N = 3

Call:

```text
hanoi(3, A, C, B)
```

Meaning:

```text
move 3 disks A → C using B
```

---

## Step 1 — first recursive call

```text
hanoi(2, A, B, C)
```

To move 2 disks A → B:

### 1.1

```text
hanoi(1, A, C, B)
```

Move:

```text
disk 1: A → C
```

### 1.2

Move disk 2:

```text
A → B
```

### 1.3

```text
hanoi(1, C, B, A)
```

Move:

```text
disk 1: C → B
```

Now:

```text
A: [3]
B: [1,2]
C: []
```

---

## Step 2 — move the largest disk

```text
disk 3: A → C
```

Now:

```text
A: []
B: [1,2]
C: [3]
```

---

## Step 3 — second recursive call

```text
hanoi(2, B, C, A)
```

Move two disks B → C using A.

### 3.1

```text
disk 1: B → A
```

### 3.2

```text
disk 2: B → C
```

### 3.3

```text
disk 1: A → C
```

Final:

```text
A: []
B: []
C: [1,2,3]
```

---

## Complete move sequence

```text
1. disk 1: A → C
2. disk 2: A → B
3. disk 1: C → B
4. disk 3: A → C
5. disk 1: B → A
6. disk 2: B → C
7. disk 1: A → C
```

Answer:

```text
7 moves
```

---

# 15. Tower of Hanoi — Move Count Derivation

We have:

```text
T(n) = 2T(n-1) + 1
```

Expand:

```text
T(n)
= 2T(n-1) + 1
```

Substitute:

```text
T(n-1)
= 2T(n-2) + 1
```

So:

```text
T(n)
= 2[2T(n-2) + 1] + 1
= 4T(n-2) + 3
```

Continue:

```text
T(n)
= 8T(n-3) + 7
```

Pattern:

```text
T(n)
= 2^k T(n-k) + (2^k - 1)
```

Take:

```text
k = n
```

Then:

```text
T(0) = 0
```

Therefore:

```text
T(n)
= 2^n × 0 + (2^n - 1)
```

Hence:

```text
T(n) = 2^n - 1
```

Examples:

```text
n=1 → 1
n=2 → 3
n=3 → 7
n=4 → 15
```

---

# 16. Tower of Hanoi — C++17

## Version 1 — print moves and return move count

```cpp
#include <bits/stdc++.h>
using namespace std;

long long hanoi(int n, int from, int to, int aux) {
    if (n == 0) {
        return 0;
    }

    long long moves = 0;

    moves += hanoi(n - 1, from, aux, to);

    cout << "Move disk " << n << " from rod " << from << " to rod " << to << '\n';
    moves++;

    moves += hanoi(n - 1, aux, to, from);

    return moves;
}

int main() {
    int n = 3;

    long long moves = hanoi(n, 1, 3, 2);

    cout << "Total moves = " << moves << '\n';

    return 0;
}
```

---

## Version 2 — recurrence only

If only the number of moves is required:

```cpp
long long towerMoves(int n) {
    if (n == 0) {
        return 0;
    }

    return 2 * towerMoves(n - 1) + 1;
}
```

This mirrors:

```text
T(n) = 2T(n-1) + 1
```

---

# 17. Tower of Hanoi — Why It Is Correct

We prove recursively.

Assume:

```text
hanoi(n-1, ...)
correctly moves n-1 disks.
```

To move `n` disks:

### Part 1

The first recursive call moves:

```text
top n-1 disks
away from the largest disk
```

so disk `n` becomes accessible.

### Part 2

Disk `n` is now the only disk on the source rod, so moving it to the destination is legal.

### Part 3

The second recursive call moves:

```text
the n-1 smaller disks
onto disk n
```

Since all of them are smaller than disk `n`, the final arrangement is legal.

Therefore the complete `n`-disk move is correct.

---

# 18. Tower of Hanoi — Complexity

Number of moves:

```text
2^n - 1
```

Therefore:

```text
Time = O(2^n)
```

Maximum active recursion depth:

```text
n
```

Therefore:

```text
Auxiliary recursion stack = O(n)
```

Do not confuse:

```text
total calls ≈ exponential
```

with:

```text
maximum active depth = linear
```

---

# 19. Tower of Hanoi — Recognition Model

Think of Tower of Hanoi when the problem has:

```text
solve size n
by
solve n-1
do one central action
solve n-1 again
```

Mental template:

```text
smaller problem
    ↓
central operation
    ↓
smaller problem
```

---

# 20. Bridge — From Pure Recursion to Backtracking

Tower of Hanoi has no genuine "choice search".

The recursive decomposition tells us exactly what must happen.

Unique Paths III is different.

At a grid cell we may have:

```text
up
down
left
right
```

Only some lead to complete valid paths.

So we need:

```text
try
→ mark
→ recurse
→ undo
→ try another
```

That is backtracking.

---

# 21. Problem 2 — Unique Paths III

We are given a grid containing:

```text
1  = starting cell
2  = ending cell
0  = empty cell that must be visited
-1 = obstacle
```

Goal:

```text
Count paths from start to end
that visit EVERY non-obstacle square exactly once.
```

This "exactly once" condition is the key difficulty.

---

# 22. Unique Paths III — Grid Meaning

Example:

```text
1  0  0  0
0  0  0  0
0  0  2 -1
```

Legend:

```text
1  start
2  end
0  required walkable cell
-1 blocked
```

A valid path must:

```text
start at 1
end at 2
visit every 0 exactly once
not enter -1
not revisit any used cell
```

---

# 23. Unique Paths III — What Makes It Different from Normal Path Counting

A normal path problem may allow:

```text
reach destination by any route
```

Unique Paths III requires much more:

```text
reach destination
AND
cover all required cells
AND
cover them exactly once
```

So arriving at the destination too early is invalid.

Example:

```text
some empty cells still unvisited
      ↓
reach end
      ↓
NOT a valid answer
```

---

# 24. Unique Paths III — State Design

A good state is:

```text
dfs(row, col, remaining)
```

where:

```text
remaining
=
number of required cells that still need to be consumed
```

There are two clean conventions.

---

## Convention A

Count:

```text
start + all zeros + end
```

Then every visited cell decrements `remaining`.

Success:

```text
current cell is end
AND
remaining == 1
```

because the end itself is the final cell being consumed.

---

## Convention B

Count:

```text
start + all zeros
```

Do not count end.

Then reaching end is valid only if:

```text
remaining == 0
```

Both are correct.

We will use Convention A because it makes the state transition uniform.

---

# 25. Unique Paths III — Exact-Once Requirement

Suppose we move:

```text
A → B → C
```

We must not later use:

```text
B
```

again on the same path.

So mark the current cell as visited.

A convenient in-place technique:

```text
save its value
replace it with -1
recurse
restore original value
```

Why can we reuse `-1`?

Because:

```text
-1 already means blocked / unavailable
```

and a visited cell should also be temporarily unavailable.

---

# 26. Unique Paths III — LCCM Mapping

Use:

```text
L = Level
C = Choice
C = Check
M = Move
```

---

## Level

A useful conceptual level is:

```text
how many required cells have been visited
```

or equivalently:

```text
remaining cells
```

---

## Choice

From `(row,col)` try:

```text
up
down
left
right
```

---

## Check

A next cell must satisfy:

```text
inside grid
not obstacle
not already visited
```

---

## Move

```text
mark current cell
remaining--
recurse into a neighbor
restore current cell
```

ASCII:

```text
                      current (r,c)
                           |
                        CHOICES
                  /        |       \
                up       right     down ...
                 |
               CHECK
        in bounds + available?
                 |
                yes
                 |
               MARK
                 |
        dfs(next, remaining-1)
                 |
              RESTORE
```

---

# 27. Unique Paths III — Move and Undo

This is the core backtracking pattern.

Suppose:

```text
grid[row][col] = 0
```

Save:

```cpp
int saved = grid[row][col];
```

Mark:

```cpp
grid[row][col] = -1;
```

Recurse:

```cpp
dfs(...);
```

Restore:

```cpp
grid[row][col] = saved;
```

Visual:

```text
before:

0 0 0
0 X 0
0 0 2

choose X
   ↓

during recursion:

0 0 0
0 # 0
0 0 2

return
   ↓

restore:

0 0 0
0 X 0
0 0 2
```

Each sibling branch receives the same original parent state.

---

# 28. Unique Paths III — Search Tree

Suppose from the start there are two legal directions:

```text
                     START
                    /     \
                 RIGHT    DOWN
                  /         \
             more moves   more moves
                ...          ...
```

At each cell:

```text
up to 4 possibilities
```

but many are rejected because they are:

```text
outside
blocked
visited
```

A tiny abstract tree:

```text
                         S
                    /         \
                   A           B
                /   \        /   \
               C     X      D     E
              /             |
             F              X
             |
           END ✓
```

`X` means pruned / invalid.

---

# 29. Unique Paths III — Base Case

Suppose we reach the end cell.

Question:

```text
Did we visit everything exactly once?
```

Using Convention A:

```text
remaining includes the end cell.
```

So when standing on end:

```text
remaining must equal 1.
```

Therefore:

```cpp
if (grid[row][col] == 2) {
    return remaining == 1;
}
```

Why `1`?

Because:

```text
the end itself is the one final required cell
that has not yet been decremented.
```

---

# 30. Unique Paths III — Step-by-Step Dry Run

Use a very small teaching grid:

```text
1 0
0 2
```

All four cells are walkable.

Initial count:

```text
remaining = 4
```

Start:

```text
dfs(0,0,4)
```

Current:

```text
1
```

Mark start visited.

Remaining for child:

```text
3
```

Try RIGHT.

```text
dfs(0,1,3)
```

Cell:

```text
0
```

Mark.

Remaining for child:

```text
2
```

From `(0,1)`:

```text
left  = visited
up    = outside
right = outside
down  = end
```

Move DOWN:

```text
dfs(1,1,2)
```

But:

```text
grid[1][1] == 2
remaining = 2
```

There is still one unvisited required cell:

```text
(1,0)
```

Therefore:

```text
this path is INVALID
```

Return and restore `(0,1)`.

---

## Try the other branch from start

Move DOWN:

```text
dfs(1,0,3)
```

Mark `(1,0)`.

Then RIGHT reaches end too early:

```text
remaining = 2
```

Again invalid because `(0,1)` remains unvisited.

So this 2×2 grid has:

```text
0 valid Hamiltonian-style start-to-end paths
```

under this start/end arrangement.

This dry run is useful because it shows:

```text
"reached end"
does NOT automatically mean
"found a valid path"
```

---

## Another conceptual valid-path shape

If a grid allows:

```text
S → a → b
        ↓
d ← c
↓
E
```

and every non-obstacle cell appears exactly once, then when `E` is reached:

```text
remaining == 1
```

and we count one solution.

---

# 31. Unique Paths III — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int rows;
    int cols;

    int dr[4] = {-1, 1, 0, 0};
    int dc[4] = {0, 0, -1, 1};

    int dfs(int row, int col, int remaining, vector<vector<int>>& grid) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return 0;
        }

        if (grid[row][col] == -1) {
            return 0;
        }

        if (grid[row][col] == 2) {
            return remaining == 1 ? 1 : 0;
        }

        int saved = grid[row][col];
        grid[row][col] = -1;

        int paths = 0;

        for (int d = 0; d < 4; d++) {
            int nextRow = row + dr[d];
            int nextCol = col + dc[d];

            paths += dfs(nextRow, nextCol, remaining - 1, grid);
        }

        grid[row][col] = saved;

        return paths;
    }

    int uniquePathsIII(vector<vector<int>>& grid) {
        rows = grid.size();
        cols = grid[0].size();

        int startRow = -1;
        int startCol = -1;
        int remaining = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] != -1) {
                    remaining++;
                }

                if (grid[row][col] == 1) {
                    startRow = row;
                    startCol = col;
                }
            }
        }

        return dfs(startRow, startCol, remaining, grid);
    }
};
```

---

## 31.1 The important backtracking lines

```cpp
int saved = grid[row][col];

grid[row][col] = -1;    // mark

...

paths += dfs(...);      // recurse

...

grid[row][col] = saved; // undo
```

Remember:

```text
MARK
RECURSE
RESTORE
```

---

# 32. Unique Paths III — Why It Is Correct

We need to prove three things.

---

## 32.1 Every counted path is valid

A cell is changed to:

```text
-1
```

while it belongs to the current path.

Therefore:

```text
the same cell cannot be visited twice
```

The recursion only moves to:

```text
inside-grid
non--1
```

cells.

When the end is reached, we count the path only if:

```text
remaining == 1
```

Therefore every non-obstacle cell has been used exactly once.

So every counted path is valid.

---

## 32.2 Every valid path is explored

At each cell the DFS tries:

```text
all four directions
```

Every legal next move of any valid path is therefore considered.

Since a valid path never uses an obstacle and never repeats a cell, its complete move sequence appears as some root-to-leaf DFS branch.

So every valid path is explored.

---

## 32.3 Backtracking does not destroy other branches

Before recursion:

```text
cell is marked unavailable
```

After recursion:

```text
original value is restored
```

Therefore a cell used in one candidate path remains available to sibling candidate paths.

So branches do not interfere with each other.

---

# 33. Unique Paths III — Complexity

Let:

```text
K = number of non-obstacle cells
```

At each step there can be up to:

```text
4 choices
```

and the maximum path depth is:

```text
K
```

A simple safe upper bound is:

```text
O(4^K)
```

In practice the search is much smaller because:

```text
cannot revisit cells
obstacles prune
boundaries prune
```

A tighter informal model often resembles:

```text
O(3^K)
```

after the first move because the immediate previous cell is already unavailable.

But for simple analysis:

```text
Time = O(4^K)
```

is a safe upper bound.

Recursion depth:

```text
O(K)
```

Since visited state is stored in the grid itself:

```text
Auxiliary space = O(K)
```

from the recursion stack.

---

# 34. Common Mistakes

## Mistake 1 — Tower of Hanoi parameter order

Wrong mental model:

```text
rods have permanent roles
```

Correct:

```text
from, to, aux
change roles in each recursive call
```

Remember:

```text
hanoi(n-1, from, aux, to)
move largest from → to
hanoi(n-1, aux, to, from)
```

---

## Mistake 2 — Moving the largest disk first

You cannot move disk `n` until:

```text
all n-1 smaller disks are removed
```

---

## Mistake 3 — Thinking Tower of Hanoi is backtracking search

It is recursive, but there is no search over arbitrary alternatives.

The legal decomposition is deterministic.

---

## Mistake 4 — Unique Paths: success immediately on reaching end

Wrong:

```cpp
if (grid[row][col] == 2) {
    return 1;
}
```

This counts paths that skipped required cells.

Correct:

```cpp
if (grid[row][col] == 2) {
    return remaining == 1;
}
```

---

## Mistake 5 — Not marking visited cells

Without visited state, the DFS can:

```text
A → B → A → B → ...
```

and revisit cells illegally.

---

## Mistake 6 — Forgetting restore

Wrong:

```cpp
grid[row][col] = -1;
dfs(...);

// no restore
```

Then the next sibling branch sees the wrong state.

Correct:

```cpp
int saved = grid[row][col];
grid[row][col] = -1;

dfs(...);

grid[row][col] = saved;
```

---

## Mistake 7 — Counting only zeros

If using the `remaining == 1 at end` convention, count:

```text
start
zeros
end
```

i.e. every non-obstacle cell.

---

## Mistake 8 — Recursion depth = time complexity

Unique Paths III may have:

```text
depth = K
```

but the number of explored paths can be exponential.

Similarly Tower of Hanoi:

```text
stack depth = O(n)
time = O(2^n)
```

---

# 35. Tower of Hanoi vs Unique Paths III

| Property | Tower of Hanoi | Unique Paths III |
|---|---|---|
| Main technique | Recursion | Backtracking DFS |
| State | `n, from, to, aux` | `row, col, remaining, visited` |
| Choices | structurally fixed | up to 4 moves |
| Pruning | not really needed | bounds, obstacles, visited |
| Undo state | no | yes |
| Tree shape | 2 recursive subproblems | branching grid search |
| Base condition | `n == 0` | reach end with exact remaining count |
| Time | `O(2^n)` | exponential, safe `O(4^K)` |
| Stack | `O(n)` | `O(K)` |

---

# 36. Backtracking Design Checklist

When facing a new recursion/backtracking problem, ask:

```text
1. What does solve(state) mean?

2. What exactly is the state?

3. What is the base case?

4. What choices exist?

5. Which choices are invalid?

6. Can I reject invalid states early?

7. What changes when a choice is made?

8. Does that state need to be restored?

9. What is the exact inverse operation?

10. Does reaching the destination alone mean success?

11. Is there an "exactly once" requirement?

12. Do I need:
    existence?
    count?
    one path?
    all paths?

13. What is the branching factor?

14. What is the maximum depth?

15. What is the worst-case total number of states?
```

---

# 37. Final Mental Model

## Tower of Hanoi

```text
Move n disks from A to C using B

1. Move n-1:
   A → B using C

2. Move largest:
   A → C

3. Move n-1:
   B → C using A
```

Formula:

```text
T(n) = 2T(n-1) + 1
```

Result:

```text
T(n) = 2^n - 1
```

---

## Unique Paths III

```text
At every current cell:

1. Check validity.
2. If end:
      count only if all required cells were visited.
3. Mark current cell.
4. Try 4 directions.
5. Restore current cell.
6. Return total paths.
```

Core pattern:

```text
CHECK
→ MARK
→ RECURSE
→ RESTORE
```

---

# 38. One-Minute Revision

```text
TOWER OF HANOI
==============

Function meaning:

hanoi(n, from, to, aux)

=
move n disks
from from → to
using aux

Recurrence:

hanoi(n-1, from, aux, to)
move largest from → to
hanoi(n-1, aux, to, from)

Move count:

T(n)=2T(n-1)+1
T(n)=2^n-1

TC:
O(2^n)

Stack:
O(n)

----------------------------------

UNIQUE PATHS III
================

Grid:

1  start
2  end
0  required empty
-1 blocked

Need:
visit every non-obstacle cell
exactly once

State:

dfs(row, col, remaining)

Choices:
4 directions

Check:
inside grid
not blocked
not visited

Move:
save
mark -1
recurse
restore

Base:

if current is end:
    valid only if remaining == 1

TC:
safe upper bound O(4^K)

Stack:
O(K)

----------------------------------

MASTER DIFFERENCE

Tower of Hanoi:
recursive decomposition

Unique Paths III:
recursive search + backtracking
```
