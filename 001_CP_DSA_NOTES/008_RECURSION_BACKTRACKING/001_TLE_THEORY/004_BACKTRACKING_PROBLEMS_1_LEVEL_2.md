# Backtracking Problems 1 — Level 2
## TLE-Style Self-Study Notes with Prerequisites, LCCM, Search Trees, Pruning, Dry Runs, and C++17

> **Lecture scope**
>
> The lecture introduces backtracking as incremental candidate construction with abandonment of impossible partial candidates, then uses:
>
> 1. **Subset Sum**
> 2. **Word Search**
> 3. **Combination Sum III**
>
> The lecture provides the core problem statements and visual search ideas.  
> The prerequisite derivations, LCCM mapping, detailed dry runs, correctness notes, pruning rules, and polished C++17 below are **self-study expansions** built around that lecture structure.

---

# Table of Contents

1. [What Backtracking Really Is](#1-what-backtracking-really-is)
2. [Prerequisite 1 — Recursion vs Backtracking](#2-prerequisite-1--recursion-vs-backtracking)
3. [Prerequisite 2 — Search Tree](#3-prerequisite-2--search-tree)
4. [Prerequisite 3 — State](#4-prerequisite-3--state)
5. [Prerequisite 4 — Choice](#5-prerequisite-4--choice)
6. [Prerequisite 5 — Check / Pruning](#6-prerequisite-5--check--pruning)
7. [Prerequisite 6 — Move and Undo](#7-prerequisite-6--move-and-undo)
8. [The LCCM Framework](#8-the-lccm-framework)
9. [General Backtracking Template](#9-general-backtracking-template)
10. [How to Analyze Backtracking Complexity](#10-how-to-analyze-backtracking-complexity)
11. [Problem 1 — Subset Sum](#11-problem-1--subset-sum)
12. [Subset Sum — What Is a Subset?](#12-subset-sum--what-is-a-subset)
13. [Subset Sum — Brute Force Idea](#13-subset-sum--brute-force-idea)
14. [Subset Sum — Recursive State](#14-subset-sum--recursive-state)
15. [Subset Sum — LCCM Mapping](#15-subset-sum--lccm-mapping)
16. [Subset Sum — Search Tree](#16-subset-sum--search-tree)
17. [Subset Sum — Pruning](#17-subset-sum--pruning)
18. [Subset Sum — Detailed Dry Run](#18-subset-sum--detailed-dry-run)
19. [Subset Sum — C++17](#19-subset-sum--c17)
20. [Subset Sum — Why It Is Correct](#20-subset-sum--why-it-is-correct)
21. [Subset Sum — Complexity](#21-subset-sum--complexity)
22. [Problem 2 — Word Search](#22-problem-2--word-search)
23. [Word Search — Grid Prerequisites](#23-word-search--grid-prerequisites)
24. [Word Search — Why DFS / Backtracking](#24-word-search--why-dfs--backtracking)
25. [Word Search — Recursive Contract](#25-word-search--recursive-contract)
26. [Word Search — State](#26-word-search--state)
27. [Word Search — LCCM Mapping](#27-word-search--lccm-mapping)
28. [Word Search — The Visited-Cell Problem](#28-word-search--the-visited-cell-problem)
29. [Word Search — Place, Recurse, Restore](#29-word-search--place-recurse-restore)
30. [Word Search — Search Tree](#30-word-search--search-tree)
31. [Word Search — Detailed Dry Run](#31-word-search--detailed-dry-run)
32. [Word Search — C++17](#32-word-search--c17)
33. [Word Search — Why It Is Correct](#33-word-search--why-it-is-correct)
34. [Word Search — Complexity](#34-word-search--complexity)
35. [Problem 3 — Combination Sum III](#35-problem-3--combination-sum-iii)
36. [Combination Prerequisites](#36-combination-prerequisites)
37. [Why `start` Is Needed](#37-why-start-is-needed)
38. [Combination Sum III — State](#38-combination-sum-iii--state)
39. [Combination Sum III — LCCM Mapping](#39-combination-sum-iii--lccm-mapping)
40. [Combination Sum III — Search Tree](#40-combination-sum-iii--search-tree)
41. [Combination Sum III — Pruning](#41-combination-sum-iii--pruning)
42. [Combination Sum III — Detailed Dry Run](#42-combination-sum-iii--detailed-dry-run)
43. [Combination Sum III — C++17](#43-combination-sum-iii--c17)
44. [Combination Sum III — Why It Is Correct](#44-combination-sum-iii--why-it-is-correct)
45. [Combination Sum III — Complexity](#45-combination-sum-iii--complexity)
46. [Three Problems Compared](#46-three-problems-compared)
47. [Common Backtracking Mistakes](#47-common-backtracking-mistakes)
48. [Recognition Patterns](#48-recognition-patterns)
49. [Backtracking Design Checklist](#49-backtracking-design-checklist)
50. [Final Mental Model](#50-final-mental-model)
51. [One-Minute Revision](#51-one-minute-revision)

---

# 1. What Backtracking Really Is

Backtracking is not merely:

```text
"recursion with loops"
```

The useful mental model is:

```text
Build a partial candidate
        ↓
Try one choice
        ↓
Check whether it can still lead to a solution
        ↓
If yes:
    move deeper
If no:
    abandon this branch
        ↓
Restore state
        ↓
Try the next choice
```

Visual:

```text
                         state
                    /      |      \
                 choice   choice   choice
                   |        |        |
                 valid    invalid    valid
                   |        X         |
                recurse   prune     recurse
                   |                  |
                 undo                undo
```

The key word is:

```text
PRUNE
```

A branch is stopped as soon as we know it cannot produce a valid answer.

---

# 2. Prerequisite 1 — Recursion vs Backtracking

## Recursion

Recursion means:

```text
a function calls itself on a smaller state
```

Example:

```text
factorial(n)
→ factorial(n-1)
```

There may be only one path.

---

## Backtracking

Backtracking usually means:

```text
one state
→ several possible choices
→ explore one
→ return
→ restore
→ explore another
```

Example:

```text
current subset
        |
   +----+----+
   |         |
 take x    skip x
```

So:

```text
Recursion
=
mechanism

Backtracking
=
search strategy often implemented using recursion
```

---

# 3. Prerequisite 2 — Search Tree

A backtracking execution is easiest to understand as a tree.

Each node:

```text
one partial state
```

Each edge:

```text
one decision / choice
```

Each leaf:

```text
complete candidate
or dead end
```

Example:

```text
choose / do not choose
```

for two elements:

```text
                         {}
                       /    \
                    take1   skip1
                    {1}       {}
                   /  \       / \
              take2 skip2 take2 skip2
              {1,2} {1}   {2}    {}
```

---

# 4. Prerequisite 3 — State

A recursive state must contain enough information to answer:

```text
"What decisions have already been made,
and what remains to be decided?"
```

Examples:

### Subset Sum

```text
index
remaining target
```

### Word Search

```text
row
column
word index
visited cells
```

### Combination Sum III

```text
next candidate number
numbers chosen
remaining target
```

Good state design often determines whether the solution is simple or confusing.

---

# 5. Prerequisite 4 — Choice

At each level ask:

```text
"What options can I try now?"
```

Examples:

### Subset Sum

For `arr[index]`:

```text
TAKE
SKIP
```

### Word Search

Move:

```text
up
down
left
right
```

### Combination Sum III

Choose one number from:

```text
start ... 9
```

---

# 6. Prerequisite 5 — Check / Pruning

A **check** decides whether a branch is worth exploring.

Examples:

### Subset Sum with non-negative values

```text
remaining < 0
→ impossible
→ prune
```

### Word Search

```text
outside grid
wrong character
already visited
→ prune
```

### Combination Sum III

```text
candidate > remaining
→ impossible
→ prune
```

Pruning changes:

```text
generate everything, test at the end
```

into:

```text
reject impossible states early
```

---

# 7. Prerequisite 6 — Move and Undo

Many backtracking problems modify shared state.

Pattern:

```cpp
makeChoice();

recurse();

undoChoice();
```

ASCII:

```text
state S
   |
   | choose x
   v
state S'
   |
   | recurse
   v
deeper search
   |
   | return
   v
state S'
   |
   | undo x
   v
state S
```

This restoration lets sibling branches start from the same original parent state.

---

# 8. The LCCM Framework

Use:

```text
L = Level
C = Choice
C = Check
M = Move
```

---

## Level

What part of the answer are we deciding now?

---

## Choice

What options exist at this level?

---

## Check

Is this choice allowed / useful?

---

## Move

Apply:

```text
choose
→ recurse
→ undo
```

---

## Master diagram

```text
                         LEVEL
             "What am I deciding now?"
                            |
                            v
                         CHOICE
                 "What can I try?"
                            |
                            v
                          CHECK
                  "Is it still valid?"
                     /             \
                   no              yes
                   |                |
                 PRUNE              v
                                  MOVE
                           apply → recurse
                                  ↓
                                 undo
```

---

# 9. General Backtracking Template

```cpp
void backtrack(State& state) {
    if (isComplete(state)) {
        saveAnswer(state);
        return;
    }

    for (Choice choice : getChoices(state)) {
        if (!isValid(state, choice)) {
            continue;
        }

        apply(state, choice);
        backtrack(state);
        undo(state, choice);
    }
}
```

For boolean existence:

```cpp
bool backtrack(State& state) {
    if (goalReached(state)) {
        return true;
    }

    for (Choice choice : choices) {
        if (!valid(choice)) {
            continue;
        }

        apply(choice);

        if (backtrack(state)) {
            return true;
        }

        undo(choice);
    }

    return false;
}
```

---

# 10. How to Analyze Backtracking Complexity

Do not use only:

```text
recursion depth
```

Time depends on:

```text
number of explored states
×
work per state
```

A useful upper-bound model:

```text
branching factor = B
depth = D

nodes ≈ O(B^D)
```

Examples:

```text
Subset Sum:
B = 2
D = N

upper bound:
O(2^N)
```

```text
Word Search:
B <= 4
D = word length L

upper bound:
O(4^L)
per starting cell
```

```text
Combination Sum III:
searches combinations from only 9 numbers
```

---

# 11. Problem 1 — Subset Sum

## Lecture problem

Given an array of non-negative integers and a target sum, determine whether some subset has sum exactly equal to the target.

Lecture example:

```text
arr = [3, 34, 4, 12, 5, 2]
target = 9
```

Expected:

```text
true
```

One valid subset:

```text
4 + 5 = 9
```

---

# 12. Subset Sum — What Is a Subset?

For each array element we have two possibilities:

```text
include it
or
exclude it
```

Example:

```text
arr = [3, 4]
```

Subsets:

```text
{}
{3}
{4}
{3,4}
```

Count:

```text
2^N
```

because each element contributes two choices.

---

# 13. Subset Sum — Brute Force Idea

For each element:

```text
TAKE
SKIP
```

Then at the end test the sum.

Naive tree:

```text
                             []
                    /                   \
                take 3                  skip 3
                 [3]                      []
             /        \              /        \
         take 4      skip 4       take 4      skip 4
        [3,4]          [3]          [4]          []
```

This is already a recursive structure.

---

# 14. Subset Sum — Recursive State

There are two common states.

---

## Form A — Current sum

```text
solve(index, currentSum)
```

Meaning:

```text
Can elements from index onward
complete currentSum to target?
```

---

## Form B — Remaining target

Cleaner:

```text
solve(index, remaining)
```

Meaning:

```text
Can elements from index onward
produce exactly remaining?
```

We will use Form B.

Initial:

```text
solve(0, target)
```

If we take `arr[index]`:

```text
remaining
→ remaining - arr[index]
```

If we skip:

```text
remaining stays unchanged
```

---

# 15. Subset Sum — LCCM Mapping

| LCCM | Subset Sum |
|---|---|
| Level | `index` |
| Choice | take / skip `arr[index]` |
| Check | because values are non-negative, prune if `remaining < 0` |
| Move | recurse to `index + 1` |

Visual:

```text
solve(index, remaining)
            |
       current value x
        /          \
     TAKE          SKIP
      |              |
remaining-x      remaining
      |              |
 index+1          index+1
```

---

# 16. Subset Sum — Search Tree

Use a tiny teaching example:

```text
arr = [3, 4, 5]
target = 9
```

State notation:

```text
(index, remaining)
```

Tree:

```text
                         (0,9)
                     x = 3
                   /       \
                TAKE       SKIP
                (1,6)      (1,9)
               x=4          x=4
              /   \        /   \
           (2,2) (2,6)  (2,5) (2,9)
            x=5   x=5    x=5    x=5
            / \    / \    / \    / \
        (-3)  2   1   6   0  5   4   9
                         ^
                         |
                     SUCCESS
```

The successful branch is:

```text
skip 3
take 4
take 5

4 + 5 = 9
```

---

# 17. Subset Sum — Pruning

The lecture restricts the array to:

```text
non-negative integers
```

That gives an important pruning rule.

If:

```text
remaining < 0
```

then adding more non-negative values can never bring it back to zero.

So:

```text
remaining < 0
→ false immediately
```

Also:

```text
remaining == 0
→ success immediately
```

We do not need to continue choosing elements.

---

## 17.1 Base cases

```text
remaining == 0
→ true
```

```text
index == N
and remaining != 0
→ false
```

```text
remaining < 0
→ false
```

---

# 18. Subset Sum — Detailed Dry Run

Lecture input:

```text
arr = [3,34,4,12,5,2]
target = 9
```

Use remaining target.

Start:

```text
solve(0,9)
value=3
```

Try TAKE 3:

```text
solve(1,6)
value=34
```

Taking 34:

```text
remaining = 6 - 34 = -28
→ prune
```

So try SKIP 34:

```text
solve(2,6)
value=4
```

Take 4:

```text
solve(3,2)
value=12
```

Take 12:

```text
2 - 12 < 0
→ prune
```

Skip 12:

```text
solve(4,2)
value=5
```

Take 5:

```text
2 - 5 < 0
→ prune
```

Skip 5:

```text
solve(5,2)
value=2
```

Take 2:

```text
solve(6,0)
→ remaining == 0
→ true
```

So one solution found by this DFS order is:

```text
3 + 4 + 2 = 9
```

Another valid subset is:

```text
4 + 5 = 9
```

---

## 18.1 Compact trace

```text
(0,9) take 3  → (1,6)
(1,6) take 34 → remaining<0 → prune
(1,6) skip 34 → (2,6)
(2,6) take 4  → (3,2)
(3,2) take 12 → remaining<0 → prune
(3,2) skip 12 → (4,2)
(4,2) take 5  → remaining<0 → prune
(4,2) skip 5  → (5,2)
(5,2) take 2  → (6,0) → SUCCESS
```

---

# 19. Subset Sum — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

bool subsetSum(int index, int remaining, const vector<int>& arr) {
    if (remaining == 0) {
        return true;
    }

    if (index == (int)arr.size() || remaining < 0) {
        return false;
    }

    if (subsetSum(index + 1, remaining - arr[index], arr)) {
        return true;
    }

    return subsetSum(index + 1, remaining, arr);
}

int main() {
    vector<int> arr = {3, 34, 4, 12, 5, 2};
    int target = 9;

    cout << (subsetSum(0, target, arr) ? "true" : "false") << '\n';

    return 0;
}
```

---

## 19.1 Same code with the choices made explicit

```cpp
bool subsetSum(int index, int remaining, const vector<int>& arr) {
    if (remaining == 0) {
        return true;
    }

    if (index == (int)arr.size() || remaining < 0) {
        return false;
    }

    bool take = subsetSum(index + 1, remaining - arr[index], arr);
    bool skip = subsetSum(index + 1, remaining, arr);

    return take || skip;
}
```

The first version can stop early after finding a valid subset.

---

# 20. Subset Sum — Why It Is Correct

At every index exactly two possibilities exist:

```text
the current element belongs to the subset
or
it does not
```

The recursion explores both.

Therefore every possible subset corresponds to exactly one root-to-leaf path.

If any subset sums to the target:

```text
the corresponding path reaches remaining = 0
```

If no path reaches zero:

```text
no subset satisfies the target
```

So the algorithm is complete.

---

# 21. Subset Sum — Complexity

Without pruning:

```text
2 choices per element
N levels
```

Maximum leaves:

```text
2^N
```

Therefore:

```text
Time = O(2^N)
```

Recursion depth:

```text
N
```

Therefore:

```text
Auxiliary stack = O(N)
```

The pruning may greatly reduce explored branches, but the worst-case upper bound remains exponential.

---

## 21.1 Recognition

Think Subset Sum recursion when you see:

```text
Choose some elements
Each element used at most once
Need target property
```

Mental model:

```text
TAKE / SKIP
```

---

# 22. Problem 2 — Word Search

The lecture next uses the classic Word Search grid problem.

Goal:

```text
Given a character grid and a word,
determine whether the word can be formed
using horizontally/vertically adjacent cells,
without reusing the same cell in one path.
```

Typical board:

```text
A B C E
S F C S
A D E E
```

Word:

```text
ABCCED
```

One valid path exists.

---

# 23. Word Search — Grid Prerequisites

For cell:

```text
(row, col)
```

the four neighbors are:

```text
up    = (row-1, col)
down  = (row+1, col)
left  = (row, col-1)
right = (row, col+1)
```

Direction arrays:

```cpp
int dr[4] = {-1, 1, 0, 0};
int dc[4] = {0, 0, -1, 1};
```

Then:

```cpp
nextRow = row + dr[d];
nextCol = col + dc[d];
```

---

## 23.1 Boundary check

A cell is valid only if:

```text
0 <= row < rows
0 <= col < cols
```

---

# 24. Word Search — Why DFS / Backtracking

Suppose we currently matched:

```text
ABC
```

at some cell.

The next required character may be reachable through several neighboring directions.

So:

```text
current cell
   /   |   |   \
 up  down left right
```

Each possible direction is a search branch.

If a direction fails:

```text
return to current cell
try another direction
```

That is exactly backtracking.

---

# 25. Word Search — Recursive Contract

Define:

```text
dfs(row, col, index)
```

to mean:

```text
Can word[index ... end]
be matched starting from board[row][col]?
```

This sentence tells us all base cases and transitions.

---

# 26. Word Search — State

We need:

```text
row
col
index in word
visited status of cells in current path
```

`index` means:

```text
which character of the word
must match the current cell
```

Example:

```text
word = "ABCCED"

index 0 → A
index 1 → B
index 2 → C
index 3 → C
index 4 → E
index 5 → D
```

---

# 27. Word Search — LCCM Mapping

| LCCM | Word Search |
|---|---|
| Level | `index` in the word |
| Choice | one of 4 neighboring cells |
| Check | in bounds, correct character, not visited |
| Move | mark current cell → recurse neighbor → restore |

Diagram:

```text
                  current cell
                      |
                 need word[i]
                      |
            +---------+---------+
            |         |         |
           up       right      down ...
            |
         CHECK
   bounds + char + unvisited
            |
           yes
            |
         recurse
            |
          restore
```

---

# 28. Word Search — The Visited-Cell Problem

A cell cannot be used twice in the same word path.

Example:

```text
A B
C D
```

A path cannot do:

```text
A → B → A
```

using the same `A` again.

So before exploring deeper:

```text
mark cell as used
```

After returning:

```text
restore it
```

---

## 28.1 Why restore?

Because "visited" is only for:

```text
the current DFS path
```

A different starting path should be allowed to use the same board cell.

---

# 29. Word Search — Place, Recurse, Restore

Suppose current cell contains:

```text
C
```

and it matches.

Temporarily replace:

```text
C
```

with:

```text
#
```

to mark visited.

Then explore neighbors.

Finally restore:

```text
#
→ C
```

Pattern:

```cpp
char saved = board[row][col];

board[row][col] = '#';

bool found = recurse(...);

board[row][col] = saved;
```

This is the backtracking move/undo pattern.

---

# 30. Word Search — Search Tree

Suppose from one `A` we want:

```text
ABCCED
```

Conceptually:

```text
                           A
                 /      /      \      \
               up    right     down    left
                       |
                       B
                  / /  |  \ \
                         ...
                         C
                         |
                         C
                         |
                         E
                         |
                         D
                         |
                      SUCCESS
```

Most branches terminate early because:

```text
wrong character
out of bounds
already visited
```

So the real tree is heavily pruned.

---

# 31. Word Search — Detailed Dry Run

Board:

```text
A B C E
S F C S
A D E E
```

Word:

```text
ABCCED
```

Coordinates:

```text
(0,0) A
(0,1) B
(0,2) C
(1,2) C
(2,2) E
(2,1) D
```

Trace:

```text
Start at (0,0)
board[0][0] = A
word[0] = A
match
mark visited
```

Move right:

```text
(0,1)
B == word[1]
match
mark
```

Move right:

```text
(0,2)
C == word[2]
match
mark
```

From `(0,2)`:

```text
next needed = C
```

Try neighbors.

Down:

```text
(1,2)
C == word[3]
match
mark
```

Next needed:

```text
E
```

Down:

```text
(2,2)
E == word[4]
match
mark
```

Next needed:

```text
D
```

Left:

```text
(2,1)
D == word[5]
match
```

Now:

```text
index == word.length
```

Therefore:

```text
SUCCESS
```

Path:

```text
A → B → C
        ↓
        C
        ↓
    D ← E
```

---

## 31.1 Backtracking behavior on a failed branch

Suppose a chosen neighbor does not lead to the next character.

Then:

```text
1. recursive call returns false
2. current cell is restored
3. try the next direction
```

The board must look exactly as it did before that branch.

---

# 32. Word Search — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    int rows;
    int cols;

    int dr[4] = {-1, 1, 0, 0};
    int dc[4] = {0, 0, -1, 1};

    bool dfs(int row, int col, int index, vector<vector<char>>& board, const string& word) {
        if (index == (int)word.size()) {
            return true;
        }

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        if (board[row][col] != word[index]) {
            return false;
        }

        char saved = board[row][col];
        board[row][col] = '#';

        for (int d = 0; d < 4; d++) {
            int nextRow = row + dr[d];
            int nextCol = col + dc[d];

            if (dfs(nextRow, nextCol, index + 1, board, word)) {
                board[row][col] = saved;
                return true;
            }
        }

        board[row][col] = saved;
        return false;
    }

    bool exist(vector<vector<char>>& board, string word) {
        rows = board.size();
        cols = board[0].size();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (dfs(row, col, 0, board, word)) {
                    return true;
                }
            }
        }

        return false;
    }
};
```

---

## 32.1 Why restoring before `return true` is good practice

When a successful path is found, we still restore:

```cpp
board[row][col] = saved;
```

before returning.

That keeps the function's side effects clean.

Even though the caller may immediately stop, the board remains unchanged.

---

# 33. Word Search — Why It Is Correct

The algorithm tries every possible starting cell.

For each starting cell:

```text
it checks every legal next neighboring cell
matching the next required character
```

A valid word path is therefore represented by some DFS path.

The visited marking guarantees:

```text
no cell is reused in one candidate path
```

Whenever a branch fails:

```text
the cell is restored
```

so other paths remain available.

Therefore:

```text
if a valid path exists, DFS will find it
```

and:

```text
if DFS exhausts all legal paths,
no valid path exists
```

---

# 34. Word Search — Complexity

Let:

```text
R = rows
C = columns
L = word length
```

There are:

```text
R × C
```

possible starting cells.

At a general step there are at most:

```text
4 directions
```

So a safe upper bound is:

```text
Time = O(R × C × 4^L)
```

A tighter common observation:

```text
after the first move,
we cannot immediately revisit the previous cell
```

so the effective branching is often at most 3:

```text
O(R × C × 3^(L-1))
```

But:

```text
O(R × C × 4^L)
```

is the simple safe bound.

Recursion depth:

```text
L
```

so:

```text
Auxiliary stack = O(L)
```

when using in-place marking.

---

# 35. Problem 3 — Combination Sum III

The lecture's third problem is **Combination Sum III**.

Goal:

```text
Choose exactly k distinct numbers
from 1 to 9
whose sum is n.
```

Each number:

```text
may be used at most once
```

and combinations should not be duplicated.

Example:

```text
k = 3
n = 7
```

Answer:

```text
[1,2,4]
```

because:

```text
1 + 2 + 4 = 7
```

---

# 36. Combination Prerequisites

## Combination vs permutation

Combination:

```text
[1,2,4]
```

is the same chosen set as:

```text
[4,2,1]
[2,1,4]
```

We want it only once.

So we force choices in increasing order.

---

## Why only 1 to 9?

Problem domain is fixed:

```text
1,2,3,4,5,6,7,8,9
```

Each can be chosen once.

---

# 37. Why `start` Is Needed

Suppose we choose:

```text
2
```

The next candidate should only come from:

```text
3,4,5,...,9
```

not:

```text
1 or 2
```

This guarantees:

```text
strictly increasing combination
```

and therefore:

```text
no duplicate permutations
```

Example:

```text
[1,2,4]
```

can be generated.

But after choosing `2` first:

```text
1 is no longer allowed
```

so:

```text
[2,1,4]
```

is never generated.

---

# 38. Combination Sum III — State

Use:

```text
start
remaining
current
```

Meaning:

```text
start
=
smallest number allowed next

remaining
=
sum still needed

current
=
numbers already selected
```

Recursive contract:

```text
backtrack(start, remaining)
generates every valid completion
of current using numbers start...9.
```

---

# 39. Combination Sum III — LCCM Mapping

| LCCM | Combination Sum III |
|---|---|
| Level | `current.size()` |
| Choice | candidate `x` in `start ... 9` |
| Check | enough slots, `x <= remaining`, no reuse |
| Move | push `x` → recurse with `x+1` → pop |

Diagram:

```text
                    current combination
                           |
                        LEVEL
                  how many chosen?
                           |
                        CHOICE
                     x = start..9
                           |
                         CHECK
                  can x still work?
                           |
                          MOVE
                     push x
                        ↓
                 recurse(x+1)
                        ↓
                      pop x
```

---

# 40. Combination Sum III — Search Tree

Use:

```text
k = 3
target = 7
```

Root:

```text
[]
```

Partial tree:

```text
                           []
              /             |             \
            [1]            [2]            [3] ...
          /  |  \
       [1,2][1,3][1,4]...
         |
      [1,2,3]  sum=6
      [1,2,4]  sum=7  ✓
      [1,2,5]  sum=8  prune
```

Since numbers increase:

```text
once candidate exceeds remaining,
all larger candidates also exceed it
```

so we can stop that loop.

---

# 41. Combination Sum III — Pruning

Several useful rules exist.

---

## Rule 1 — Too many numbers

If:

```text
current.size() > k
```

invalid.

Our recursion structure avoids this by stopping at exactly `k`.

---

## Rule 2 — Chosen k numbers but wrong sum

If:

```text
current.size() == k
```

then:

```text
remaining must be 0
```

Otherwise this branch fails.

---

## Rule 3 — Candidate exceeds remaining

Because all candidates are positive:

```text
candidate > remaining
```

means:

```text
candidate cannot fit
```

and larger numbers cannot fit either.

So:

```cpp
break;
```

---

## Rule 4 — Not enough numbers remain

Suppose:

```text
need = k - current.size()
```

but fewer than `need` candidates remain.

Then prune.

This is optional optimization.

---

## Rule 5 — Minimum possible future sum

If we still need:

```text
need
```

numbers beginning at `start`,
the smallest possible sum is:

```text
start + (start+1) + ... for need terms
```

If that already exceeds `remaining`:

```text
prune
```

This is a stronger optional optimization.

---

# 42. Combination Sum III — Detailed Dry Run

Input:

```text
k = 3
n = 7
```

Start:

```text
current=[]
start=1
remaining=7
```

Choose `1`:

```text
current=[1]
start=2
remaining=6
```

Choose `2`:

```text
current=[1,2]
start=3
remaining=4
```

Try `3`:

```text
current=[1,2,3]
remaining=1
size=3
remaining!=0
→ fail
```

Undo:

```text
current=[1,2]
```

Try `4`:

```text
current=[1,2,4]
remaining=0
size=3
→ valid answer
```

Save:

```text
[1,2,4]
```

Undo `4`:

```text
current=[1,2]
```

Next candidate would be `5`:

```text
5 > remaining(4)
```

So:

```text
break
```

This whole sibling range is pruned.

---

## 42.1 Compact trace

```text
[]              rem=7
→ choose 1
[1]             rem=6
→ choose 2
[1,2]           rem=4
→ choose 3
[1,2,3]         rem=1, size=3 → fail
← undo 3
→ choose 4
[1,2,4]         rem=0, size=3 → SAVE
← undo 4
candidate 5 > rem 4 → break
```

---

# 43. Combination Sum III — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    void backtrack(int start, int k, int remaining, vector<int>& current, vector<vector<int>>& answer) {
        if ((int)current.size() == k) {
            if (remaining == 0) {
                answer.push_back(current);
            }

            return;
        }

        for (int value = start; value <= 9; value++) {
            if (value > remaining) {
                break;
            }

            current.push_back(value);

            backtrack(value + 1, k, remaining - value, current, answer);

            current.pop_back();
        }
    }

    vector<vector<int>> combinationSum3(int k, int n) {
        vector<vector<int>> answer;
        vector<int> current;

        backtrack(1, k, n, current, answer);

        return answer;
    }
};
```

---

## 43.1 Move / undo highlighted

```cpp
current.push_back(value);                 // choose

backtrack(value + 1, k, remaining - value, current, answer);

current.pop_back();                       // undo
```

That three-line pattern is the heart of backtracking.

---

# 44. Combination Sum III — Why It Is Correct

At every state:

```text
the loop considers every allowed next number
from start to 9
```

Because recursion uses:

```text
value + 1
```

each number is used at most once.

Because choices are strictly increasing:

```text
the same combination cannot appear in another order
```

Every size-`k` increasing subset of `{1..9}` therefore corresponds to exactly one root-to-leaf path.

We save exactly those paths whose sum is `n`.

Hence the algorithm returns:

```text
all valid combinations
and no duplicates
```

---

# 45. Combination Sum III — Complexity

The candidate universe contains only:

```text
9 numbers
```

The total number of subsets of `{1..9}` is:

```text
2^9 = 512
```

So for this exact problem the search space is small and bounded.

A combinational view:

```text
at most C(9,k)
```

complete size-`k` candidates.

With result copying:

```text
O(C(9,k) × k)
```

is a useful output-related estimate.

Recursion depth:

```text
k
```

so:

```text
Auxiliary stack = O(k)
```

excluding the returned answers.

---

# 46. Three Problems Compared

| Problem | Level | Choice | Check / Prune | Undo? | Main Pattern |
|---|---|---|---|---|---|
| Subset Sum | array index | take / skip | remaining < 0 | not required with value-state | include/exclude |
| Word Search | word index / cell | 4 directions | bounds, char, visited | yes | grid DFS |
| Combination Sum III | chosen count | number `start..9` | positive sum bounds | yes | combination generation |

---

## 46.1 Search-tree shapes

### Subset Sum

```text
binary tree

             state
             /   \
          take   skip
```

---

### Word Search

```text
up to 4-way tree

                cell
          /      |      |      \
        up     down    left    right
```

---

### Combination Sum III

```text
shrinking choice set

[]
├─ 1
│  ├─ 2
│  │  ├─ 3
│  │  ├─ 4
│  │  └─ ...
│  ├─ 3
│  └─ ...
├─ 2
└─ ...
```

---

# 47. Common Backtracking Mistakes

## Mistake 1 — No clear state definition

Do not write recursion before saying:

```text
solve(state) means ...
```

---

## Mistake 2 — Forgetting undo

Typical bug:

```cpp
current.push_back(value);
backtrack(...);

// forgot pop_back()
```

Then sibling branches inherit the previous choice.

Correct:

```cpp
current.push_back(value);
backtrack(...);
current.pop_back();
```

---

## Mistake 3 — Marking a grid cell and never restoring it

Word Search requires:

```text
mark
recurse
restore
```

The visited state belongs only to one path.

---

## Mistake 4 — Checking too late

Bad idea:

```text
generate every complete candidate
then test validity
```

Better:

```text
prune as soon as the partial candidate is impossible
```

---

## Mistake 5 — Generating duplicate combinations

If each recursive call starts again from `1`:

```text
[1,2,4]
[2,1,4]
[4,2,1]
```

may all appear.

Use:

```text
next start = chosen value + 1
```

---

## Mistake 6 — Confusing visited globally with visited in current path

Word Search needs:

```text
path-local visited
```

not permanent global visited.

A cell can appear in different candidate paths.

---

## Mistake 7 — Wrong base-case order

Subset Sum:

```text
remaining == 0
```

should immediately succeed.

Combination Sum III:

```text
size == k
```

must verify:

```text
remaining == 0
```

---

## Mistake 8 — Using pruning assumptions when negatives exist

The Subset Sum lecture specifies:

```text
non-negative integers
```

Therefore:

```text
remaining < 0
```

is safe pruning.

If negative values were allowed, that pruning would not generally be correct.

---

# 48. Recognition Patterns

## Pattern 1 — Each item chosen or not chosen

Think:

```text
TAKE / SKIP
```

Examples:

```text
subset sum
subsets
0/1 knapsack recursion
partition
```

---

## Pattern 2 — Build a path in a grid

Think:

```text
DFS + visited + undo
```

Examples:

```text
word search
maze paths
grid path matching
```

---

## Pattern 3 — Choose k items without duplicates

Think:

```text
start index
loop choices
push
recurse(start after chosen)
pop
```

Examples:

```text
combinations
combination sum III
choose k numbers
```

---

## Pattern 4 — Local partial state can already fail

Think:

```text
PRUNE EARLY
```

Examples:

```text
sum exceeded
wrong grid character
not enough choices left
```

---

# 49. Backtracking Design Checklist

Before writing code:

```text
1. What exactly is one state?

2. What is the LEVEL?

3. What choices exist at this level?

4. Which choices are illegal?

5. Which choices can never lead to an answer?

6. Can I prune those immediately?

7. What changes when I make a choice?

8. What is the exact inverse operation?

9. What is the base case?

10. Does the base case mean:
    - found one solution?
    - built one complete solution?
    - exhausted all choices?

11. Do I need:
    - boolean existence,
    - count,
    - one solution,
    - all solutions?

12. Could different paths generate the same solution?

13. If yes, what ordering rule removes duplicates?

14. What is the branching factor?

15. What is the maximum depth?

16. What is the worst-case number of states?
```

---

# 50. Final Mental Model

```text
BACKTRACKING
=
SEARCH TREE
+
PRUNING
+
STATE RESTORATION
```

Master structure:

```text
                    CURRENT STATE
                          |
                        LEVEL
                          |
                +---------+---------+
                |         |         |
             choice1   choice2   choice3
                |         |         |
              check     check     check
                |         X         |
              move      prune      move
                |                   |
             recurse             recurse
                |                   |
               undo                undo
```

For the three lecture problems:

```text
SUBSET SUM
-----------
Level  = array index
Choice = take / skip
Check  = remaining >= 0
Move   = index + 1
Goal   = remaining == 0
```

```text
WORD SEARCH
-----------
Level  = word index
Choice = 4 neighbors
Check  = bounds + matching char + unvisited
Move   = mark → recurse → restore
Goal   = matched all characters
```

```text
COMBINATION SUM III
-------------------
Level  = number of chosen elements
Choice = candidate from start...9
Check  = value <= remaining
Move   = push → recurse(value+1) → pop
Goal   = size == k and remaining == 0
```

---

# 51. One-Minute Revision

```text
BACKTRACKING

Build candidate
→ choose
→ check
→ recurse
→ undo

If impossible:
→ prune

----------------------------

LCCM

L = Level
C = Choice
C = Check
M = Move

----------------------------

SUBSET SUM

state:
(index, remaining)

choices:
take / skip

take:
remaining -= arr[index]

goal:
remaining == 0

prune:
remaining < 0
because values are non-negative

TC:
O(2^N)

----------------------------

WORD SEARCH

state:
(row, col, wordIndex)

choices:
4 directions

checks:
in bounds
correct character
not visited

move:
mark
recurse
restore

TC:
O(R*C*4^L) safe upper bound

stack:
O(L)

----------------------------

COMBINATION SUM III

state:
(start, remaining, current)

choices:
start..9

move:
push x
recurse(x+1)
pop x

why x+1?
no reuse
no permutations
no duplicates

goal:
current.size()==k
and remaining==0

----------------------------

MASTER QUESTION

"What does this recursive state mean?"

If that sentence is clear,
the backtracking code becomes much easier.
```
