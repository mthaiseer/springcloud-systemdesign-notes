# K Knights — Backtracking Using the LCCM Framework

> **TLE / AlgoZenith model used in this note**
>
> - **Level** = number of knights already placed
> - **Choice** = choose the next board cell for the next knight
> - **Check** = is that cell empty and safe from all already placed knights?
> - **Move** = place → recurse → unplace
>
> The implementation follows the lecture structure visible in the shared screenshot: a global `board[14][14]`, eight knight directions using `dx[] / dy[]`, `check1(i, j)`, and recursion where `level` means **number of knights placed**.
>
> One small addition is made for correctness with **identical knights**: `startCell`. It forces cells to be chosen in increasing row-major order, so the same final board is not generated in `K!` different placement orders.

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Prerequisites](#2-prerequisites)
   - [2.1 How a knight moves](#21-how-a-knight-moves)
   - [2.2 Board representation](#22-board-representation)
   - [2.3 Flattening a 2D board](#23-flattening-a-2d-board)
   - [2.4 Identical knights means combinations](#24-identical-knights-means-combinations)
   - [2.5 Backtracking = Change → Recurse → Undo](#25-backtracking--change--recurse--undo)
3. [The Recursive State](#3-the-recursive-state)
4. [Recursive Invariant](#4-recursive-invariant)
5. [LCCM Framework](#5-lccm-framework)
6. [Understanding `check1(i, j)`](#6-understanding-check1i-j)
7. [Why We Check All 8 Knight Directions](#7-why-we-check-all-8-knight-directions)
8. [Why `startCell` Is Important](#8-why-startcell-is-important)
9. [Recursion Tree](#9-recursion-tree)
10. [Step-by-Step Dry Run — N = 3, K = 2](#10-step-by-step-dry-run--n--3-k--2)
11. [Sam/TLE-Style C++17 Implementation](#11-samtle-style-c17-implementation)
12. [Code Walkthrough](#12-code-walkthrough)
13. [Why the Algorithm Is Correct](#13-why-the-algorithm-is-correct)
14. [Complexity](#14-complexity)
15. [Common Mistakes](#15-common-mistakes)
16. [LCCM Recognition Checklist](#16-lccm-recognition-checklist)
17. [Final Mental Model](#17-final-mental-model)

---

# 1. Problem Statement

You are given:

```text
N = size of the chessboard
K = number of identical knights
```

The board is:

```text
N × N
```

Count the number of ways to place **exactly `K` knights** such that no two knights attack each other.

Important conditions:

```text
1. A cell contains at most one knight.
2. Knights are identical.
3. Two boards are different only when their occupied cells differ.
```

For example, on a `3 × 3` board with `K = 2`:

```text
Total pairs of cells = C(9, 2) = 36
Attacking pairs       = 8
Valid arrangements    = 36 - 8 = 28
```

So:

```text
N = 3, K = 2
answer = 28
```

---

# 2. Prerequisites

Before writing recursion, understand the five ideas below.

---

## 2.1 How a knight moves

A knight moves in an **L shape**:

```text
2 cells in one direction
+
1 cell perpendicular
```

From `(i, j)`, the eight possible destinations are:

```text
(i + 1, j + 2)
(i + 2, j + 1)
(i + 2, j - 1)
(i + 1, j - 2)
(i - 1, j - 2)
(i - 2, j - 1)
(i - 2, j + 1)
(i - 1, j + 2)
```

This is exactly what the lecture arrays encode:

```cpp
int dx[] = {1, 2, 2, 1, -1, -2, -2, -1};
int dy[] = {2, 1, -1, -2, -2, -1, 1, 2};
```

### Visual

```text
        X       X
    X               X

            K

    X               X
        X       X
```

`K` is the current knight.

Every `X` is a cell that knight can attack.

### Tiny example

Suppose:

```text
N = 4
current cell = (1, 1)
```

Try all eight offsets.

Some generated coordinates are outside the board, so they are ignored.

Valid attacked cells include:

```text
(2, 3)
(3, 2)
(3, 0)
(0, 3)
```

The important pattern is:

```text
GENERATE neighbour
→ CHECK boundary
→ inspect board[nx][ny]
```

---

## 2.2 Board representation

The lecture uses:

```cpp
int board[14][14];
```

Interpretation:

```text
board[i][j] = 0  → cell is empty
board[i][j] = 1  → a knight is placed here
```

Example:

```text
0 0 1
0 0 0
1 0 0
```

means knights are at:

```text
(0,2)
(2,0)
```

The board itself is the **partial solution**.

---

## 2.3 Flattening a 2D board

Although Sam's code works with `(i, j)`, using one integer to order cells is extremely useful.

For `N = 3`:

```text
2D board                 flat cell number

(0,0) (0,1) (0,2)          0  1  2
(1,0) (1,1) (1,2)    →     3  4  5
(2,0) (2,1) (2,2)          6  7  8
```

Convert flat cell → `(row, col)`:

```cpp
int row = cell / n;
int col = cell % n;
```

Example:

```text
N = 3
cell = 7

row = 7 / 3 = 2
col = 7 % 3 = 1

cell 7 = (2,1)
```

Why do we use this?

Because it gives the candidate cells a fixed order:

```text
0 < 1 < 2 < ... < N² - 1
```

That will prevent duplicate arrangements.

---

## 2.4 Identical knights means combinations

This point is essential.

Suppose the final board contains knights at cells:

```text
{1, 6}
```

These are **not** two different answers:

```text
Knight A → cell 1
Knight B → cell 6
```

and:

```text
Knight A → cell 6
Knight B → cell 1
```

The knights are identical.

The board is the same.

Therefore, conceptually we are choosing:

```text
K cells from N² cells
```

not assigning names to `K` different knights.

Ignoring attacks, the number of possible boards is:

```text
C(N², K)
```

not:

```text
P(N², K)
```

### Canonical order trick

Force chosen cells to increase:

```text
first chosen cell < second chosen cell < third chosen cell < ...
```

For example:

```text
{1, 6}
```

is generated only as:

```text
1 → 6
```

Never:

```text
6 → 1
```

That is why the implementation carries:

```text
startCell
```

---

## 2.5 Backtracking = Change → Recurse → Undo

Suppose `(i, j)` is safe.

### Change

```cpp
board[i][j] = 1;
```

### Recurse

```cpp
rec(level + 1, cell + 1);
```

### Undo

```cpp
board[i][j] = 0;
```

ASCII:

```text
Parent state
    |
    | place knight
    v
Changed state
    |
    | recurse
    v
Explore complete subtree
    |
    | return
    v
Undo placement
    |
    v
Parent state restored
```

This is the central backtracking pattern:

```text
DO
RECURSE
UNDO
```

Without `UNDO`, one recursion branch contaminates the next branch.

---

# 3. The Recursive State

The lecture's key statement is:

```text
level = number of knights placed
```

We keep exactly that meaning.

Our recursive call is:

```text
rec(level, startCell)
```

where:

```text
level     = number of knights already placed
startCell = smallest flat cell index that may be chosen next
board     = exact locations of those placed knights
```

### Recursive contract

> `rec(level, startCell)` explores every valid way to place the remaining `K - level` knights using cells from `startCell` onward, while preserving all knights already stored in `board`.

This sentence is the most important part of the solution.

---

# 4. Recursive Invariant

Before `rec(level, startCell)` begins:

```text
1. Exactly level knights are on board.
2. No two currently placed knights attack each other.
3. Every previously selected cell is < startCell.
4. Future choices are restricted to startCell ... N²-1.
```

Visual:

```text
0                    startCell                 N²-1
|-----------------------|-------------------------|
 old / unavailable              future choices

board contains exactly level valid knights
```

Example:

```text
N = 4
level = 2
startCell = 7
```

means:

```text
2 valid knights have already been placed.
The next knight may only be placed in cells 7 ... 15.
```

Notice:

```text
level ≠ cell number
```

They describe two completely different things.

---

# 5. LCCM Framework

For this exact solution:

| LCCM Part | Question | K Knights answer |
|---|---|---|
| **Level** | How much of the solution has been built? | Number of knights already placed |
| **Choice** | What can we choose now? | Any candidate cell from `startCell` onward |
| **Check** | Is the choice legal? | Cell is empty and no existing knight attacks it |
| **Move** | What happens after a valid choice? | Place → recurse → unplace |

---

## 5.1 Level

```text
level = number of knights placed
```

So:

```text
rec(0, ...) → 0 knights placed
rec(1, ...) → 1 knight placed
rec(2, ...) → 2 knights placed
...
rec(K, ...) → complete solution
```

Therefore the base case is natural:

```cpp
if (level == k) {
    ans++;
    return;
}
```

At that moment:

```text
board itself is one complete solution
```

This matches the comment visible in the TLE/AlgoZenith code.

---

## 5.2 Choice

At each recursive level, choose the position of the **next knight**.

Conceptually:

```text
for every candidate cell:
    try putting the next knight there
```

But because knights are identical, use only:

```text
cell >= startCell
```

So the choice loop is:

```cpp
for (int cell = startCell; cell < n * n; cell++) {
    ...
}
```

---

## 5.3 Check

Before placing at `(i, j)`:

```text
1. board[i][j] must be empty
2. none of its 8 knight-neighbour cells may contain a knight
```

That is the responsibility of:

```cpp
check1(i, j)
```

---

## 5.4 Move

For a safe choice:

```text
PLACE
   ↓
RECURSE
   ↓
UNPLACE
```

Code:

```cpp
board[i][j] = 1;
rec(level + 1, cell + 1);
board[i][j] = 0;
```

The last line is the backtracking step.

---

# 6. Understanding `check1(i, j)`

The lecture code begins with:

```cpp
if (board[i][j] == 1) {
    return false;
}
```

Why?

Because we cannot put two knights in one cell.

Then inspect all eight knight moves.

For each direction:

```cpp
int nx = i + dx[pos];
int ny = j + dy[pos];
```

ASCII:

```text
current cell = (i,j)

           (nx,ny)
              X
               \
                \
                 K  = (i,j)
```

The generated cell matters only when it lies inside the board:

```cpp
nx >= 0 && nx < n
ny >= 0 && ny < n
```

If a valid attacked cell already contains a knight:

```cpp
board[nx][ny] == 1
```

then the new placement is illegal.

So:

```cpp
return false;
```

Otherwise, after all eight directions:

```cpp
return true;
```

---

## Important correction to the visible screenshot

The screenshot's attack condition appears to show `board[i][j] == 1` again after computing `nx` and `ny`.

At that point the intended cell to inspect must be:

```cpp
board[nx][ny]
```

because `(nx, ny)` is the attacked neighbour we just generated.

Correct form:

```cpp
if (nx < n && nx >= 0 && ny < n && ny >= 0 && board[nx][ny] == 1) {
    return false;
}
```

Otherwise the function would never actually inspect the neighbouring knight.

---

# 7. Why We Check All 8 Knight Directions

The earlier version of this note used a different recursion design:

```text
Level = current board cell
Choice = place / skip
```

With strict row-major cell processing, only already-processed directions had to be checked.

Sam/TLE's design is different:

```text
Level = number of knights placed
Choice = next candidate cell
```

The board is treated as the state, so the clearest check is:

```text
look at all 8 knight neighbours
```

This exactly matches the lecture's:

```cpp
for (int pos = 0; pos < 8; pos++)
```

The fixed `startCell` ordering prevents duplicate combinations; the eight-direction check independently guarantees attack safety.

---

# 8. Why `startCell` Is Important

The screenshot clearly shows:

```cpp
void rec(int level)
```

and:

```text
level = no of knights placed
```

Only the beginning of the function is visible in the shared screenshot, so the lecture's later duplicate-handling code is not visible.

For identical knights, blindly trying every board cell at every level would generate permutations of the same board.

Example:

```text
Target occupied cells = {1, 5}
```

Without ordering:

```text
1 → 5
5 → 1
```

both reach the same final board.

That would double-count it.

For `K = 3`, one board could appear:

```text
3! = 6 times
```

So this note makes the canonical ordering explicit:

```cpp
rec(level, startCell)
```

After choosing `cell`:

```cpp
rec(level + 1, cell + 1);
```

Therefore:

```text
chosenCell1 < chosenCell2 < ... < chosenCellK
```

Every set of occupied cells has exactly one generation order.

### Visual

```text
Wrong / duplicate-generating

{} 
├── choose 1
│   └── choose 5  → {1,5}
└── choose 5
    └── choose 1  → {1,5} again
```

Canonical version:

```text
{} 
├── choose 1
│   └── choose 5  → {1,5}
└── choose 5
    └── only cells > 5 are allowed

{1,5} appears once
```

---

# 9. Recursion Tree

Suppose:

```text
N = 3
K = 2
```

Flat board:

```text
0 1 2
3 4 5
6 7 8
```

At `level = 0`, choose the first knight.

Partial tree:

```text
                              rec(0, 0)
                     level = 0, board empty

        ┌────────────────┬───────────────┬───────────────┐
        │                │               │               │
     choose 0         choose 1        choose 2         ...
        │                │               │
        v                v               v
   board={0}         board={1}       board={2}
   rec(1,1)          rec(1,2)        rec(1,3)
        │
        ├── choose 1 → {0,1} → rec(2,2) → ANSWER +1
        ├── choose 2 → {0,2} → rec(2,3) → ANSWER +1
        ├── choose 3 → {0,3} → rec(2,4) → ANSWER +1
        ├── choose 4 → {0,4} → rec(2,5) → ANSWER +1
        ├── choose 5 → ATTACKS 0 → reject
        ├── choose 6 → {0,6} → answer
        ├── choose 7 → ATTACKS 0 → reject
        └── choose 8 → {0,8} → answer
```

Each node represents:

```text
current board state
+
number of knights already placed
+
where future choices may begin
```

Each valid edge represents:

```text
place the next knight in one safe cell
```

---

## Tree depth

The maximum depth is:

```text
K
```

because one recursive level corresponds to placing one knight.

This is an important difference from the old cell-level design, whose depth could be `N²`.

---

# 10. Step-by-Step Dry Run — N = 3, K = 2

Use:

```text
N = 3
K = 2
```

Board cells:

```text
0 1 2
3 4 5
6 7 8
```

Initial state:

```text
level = 0
startCell = 0
board = empty
ans = 0
```

---

## Step 1 — choose cell 0

```text
cell = 0
(i,j) = (0,0)
```

`check1(0,0)`:

```text
cell is empty
no existing knights
→ true
```

Place:

```text
K . .
. . .
. . .
```

Now:

```text
level = 1
next startCell = 1
```

Call:

```text
rec(1, 1)
```

---

## Step 2 — try cell 1

```text
cell = 1
(i,j) = (0,1)
```

A knight at `(0,1)` does not attack `(0,0)`.

So:

```text
check1(0,1) = true
```

Place:

```text
K K .
. . .
. . .
```

Call:

```text
rec(2, 2)
```

Now:

```text
level == K
```

Therefore:

```text
ans++
```

So:

```text
ans = 1
```

Return.

---

## Step 3 — backtrack

We return to the parent call.

Undo cell `1`:

```text
K . .
. . .
. . .
```

This is crucial.

Now the parent can try another second-knight position.

---

## Step 4 — try cell 2

```text
cell = 2
(i,j) = (0,2)
```

Safe from `(0,0)`.

Place:

```text
K . K
. . .
. . .
```

Call:

```text
rec(2, 3)
```

Again:

```text
level == K
→ ans++
```

Now:

```text
ans = 2
```

Undo cell `2`.

---

## Step 5 — eventually try cell 5

Cell `5` is:

```text
(1,2)
```

Existing knight:

```text
(0,0)
```

Difference:

```text
row difference = 1
col difference = 2
```

That is a knight move.

During `check1(1,2)`, one generated neighbour is:

```text
nx = 0
ny = 0
```

and:

```text
board[0][0] == 1
```

Therefore:

```text
check1(1,2) = false
```

This choice is rejected immediately.

No recursive call is made for it.

---

## Compact trace

```text
rec(0,0)
→ choose 0, place
  rec(1,1)
  → choose 1, place
    rec(2,2) → ans = 1
    undo 1
  → choose 2, place
    rec(2,3) → ans = 2
    undo 2
  → ...
  → choose 5 → check fails, skip
  → ...
undo 0

then level 0 tries first knight at cell 1, 2, 3, ...
```

Because of `startCell`, a pair such as:

```text
{0,2}
```

is never regenerated later as:

```text
{2,0}
```

Final answer:

```text
28
```

---

# 11. Sam/TLE-Style C++17 Implementation

The following keeps the lecture's structure while making the duplicate-handling explicit and correcting the neighbour lookup inside `check1()`.

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, k;
long long ans;

int board[14][14];

int dx[] = {1, 2, 2, 1, -1, -2, -2, -1};
int dy[] = {2, 1, -1, -2, -2, -1, 1, 2};

bool check1(int i, int j) {
    if (board[i][j] == 1) {
        return false;
    }

    for (int pos = 0; pos < 8; pos++) {
        int nx = i + dx[pos];
        int ny = j + dy[pos];

        if (nx < n && nx >= 0 && ny < n && ny >= 0 && board[nx][ny] == 1) {
            return false;
        }
    }

    return true;
}

void rec(int level, int startCell) {
    // level = number of knights already placed.
    if (level == k) {
        ans++;
        // board gives one complete solution.
        return;
    }

    int totalCells = n * n;
    int knightsNeeded = k - level;
    int cellsRemaining = totalCells - startCell;

    if (cellsRemaining < knightsNeeded) {
        return;
    }

    // Choice: position of the next knight.
    for (int cell = startCell; cell < totalCells; cell++) {
        int i = cell / n;
        int j = cell % n;

        // Check.
        if (!check1(i, j)) {
            continue;
        }

        // Move: place.
        board[i][j] = 1;

        // Explore. Future cells must come after this cell.
        rec(level + 1, cell + 1);

        // Backtrack: unplace.
        board[i][j] = 0;
    }
}

void solve() {
    cin >> n >> k;

    memset(board, 0, sizeof(board));
    ans = 0;

    if (k > n * n) {
        cout << 0 << '\n';
        return;
    }

    rec(0, 0);

    cout << ans << '\n';
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    solve();

    return 0;
}
```

---

# 12. Code Walkthrough

## 12.1 Globals

```cpp
int n, k;
long long ans;
int board[14][14];
```

Meaning:

```text
n     = board dimension
k     = required knights
ans   = number of valid complete boards
board = current partial placement
```

`ans` is written as `long long` here because the number of arrangements can grow much larger than an `int`.

---

## 12.2 Direction arrays

```cpp
int dx[] = {1, 2, 2, 1, -1, -2, -2, -1};
int dy[] = {2, 1, -1, -2, -2, -1, 1, 2};
```

Pair them by index:

```text
pos  dx  dy
0     1   2
1     2   1
2     2  -1
3     1  -2
4    -1  -2
5    -2  -1
6    -2   1
7    -1   2
```

Each row is one legal knight movement.

---

## 12.3 `check1(i, j)`

Its contract is:

> Return `true` exactly when a new knight can be placed at `(i,j)` without colliding with or attacking any knight currently on the board.

Step by step:

```text
1. Is this cell already occupied?
   yes → false

2. Generate all 8 knight-neighbour coordinates.

3. Ignore coordinates outside the board.

4. If any valid neighbour already has a knight:
   false

5. Otherwise:
   true
```

---

## 12.4 `rec(level, startCell)`

Its contract is:

> We already have `level` valid knights. Choose positions for the remaining knights using only cells from `startCell` onward.

### Base case

```cpp
if (level == k) {
    ans++;
    return;
}
```

Exactly `K` valid knights exist.

One complete board has been found.

---

## 12.5 Remaining-cell prune

```cpp
int knightsNeeded = k - level;
int cellsRemaining = totalCells - startCell;

if (cellsRemaining < knightsNeeded) {
    return;
}
```

Example:

```text
knights still needed = 3
cells still available = 2
```

Even if both remaining cells are safe:

```text
2 < 3
```

so completion is impossible.

Return immediately.

---

## 12.6 Choice loop

```cpp
for (int cell = startCell; cell < totalCells; cell++) {
```

This means:

```text
choose the position of the next knight
```

Convert to coordinates:

```cpp
int i = cell / n;
int j = cell % n;
```

---

## 12.7 Check

```cpp
if (!check1(i, j)) {
    continue;
}
```

Unsafe candidate:

```text
reject only this choice
```

Do **not** return from the whole recursive function.

There may be another safe cell later in the loop.

---

## 12.8 Place

```cpp
board[i][j] = 1;
```

The state now contains one additional knight.

---

## 12.9 Recurse

```cpp
rec(level + 1, cell + 1);
```

Two values advance:

```text
level + 1
```

because one more knight was placed.

And:

```text
cell + 1
```

because future choices must come after this chosen cell.

---

## 12.10 Undo

```cpp
board[i][j] = 0;
```

This returns the state to exactly what it was before this choice.

Diagram:

```text
before choice
board = S

place
board = S + {(i,j)}

recurse
explore every solution containing (i,j)

undo
board = S
```

Now the loop may safely try its next candidate.

---

# 13. Why the Algorithm Is Correct

A backtracking proof is easiest from the recursive invariant.

---

## 13.1 Every counted board has exactly K knights

The answer is incremented only when:

```cpp
level == k
```

and `level` counts the number of placed knights.

Therefore every counted board has exactly `K` knights.

---

## 13.2 Every counted board is non-attacking

Initially:

```text
board is empty
```

so it is valid.

Before every placement:

```cpp
check1(i, j)
```

verifies that the new knight attacks none of the existing knights.

Therefore, after each successful placement, all placed knights remain mutually non-attacking.

So every board counted at the base case is valid.

---

## 13.3 Every valid arrangement can be generated

Take any valid set of `K` board cells.

Sort those cell indices:

```text
c1 < c2 < ... < cK
```

At level `0`, the loop can choose `c1`.

At level `1`, because the next search begins from `c1 + 1`, it can choose `c2`.

Continue similarly.

Because the target arrangement is valid, each `check1()` succeeds.

Therefore the recursion contains a path that builds every valid arrangement.

---

## 13.4 No valid arrangement is counted twice

Because recursive choices satisfy:

```text
c1 < c2 < ... < cK
```

one occupied-cell set has exactly one legal generation order.

Hence the same board cannot be reached by two different recursion paths.

---

# 14. Complexity

Let:

```text
M = N²
```

Ignoring attack rejection, the recursion generates combinations of cells up to depth `K`.

The number of partial states is related to:

```text
C(M,0) + C(M,1) + ... + C(M,K)
```

At each state, the loop may inspect multiple remaining cells, and each `check1()` examines exactly 8 directions:

```text
check1 = O(8) = O(1)
```

A safe conservative bound is:

```text
O(M × Σ C(M,d))
       d=0..K-1
```

For fixed small `K`, the important search-space idea is:

```text
choose K cells from M cells
≈ C(M,K)
```

Attack checks prune many branches before reaching depth `K`.

### Recursion space

Depth:

```text
O(K)
```

because one recursive call places one knight.

### Board space

```text
O(N²)
```

Therefore auxiliary storage is:

```text
O(N² + K)
```

which is dominated by:

```text
O(N²)
```

---

# 15. Common Mistakes

## Mistake 1 — thinking `level` means board cell

In this lecture code:

```text
level = number of knights placed
```

not:

```text
level = current cell index
```

These are different backtracking formulations.

---

## Mistake 2 — checking `board[i][j]` instead of `board[nx][ny]`

After generating:

```cpp
int nx = i + dx[pos];
int ny = j + dy[pos];
```

the attacked neighbour is:

```text
(nx, ny)
```

So inspect:

```cpp
board[nx][ny]
```

---

## Mistake 3 — forgetting bounds

Never access:

```cpp
board[nx][ny]
```

until verifying:

```text
0 <= nx < n
0 <= ny < n
```

---

## Mistake 4 — forgetting to unplace

Wrong:

```cpp
board[i][j] = 1;
rec(...);
```

Correct:

```cpp
board[i][j] = 1;
rec(...);
board[i][j] = 0;
```

Without undo, sibling branches inherit the previous branch's knight.

---

## Mistake 5 — using `return` when one choice is unsafe

Wrong:

```cpp
if (!check1(i, j)) {
    return;
}
```

That kills every later choice.

Correct:

```cpp
if (!check1(i, j)) {
    continue;
}
```

Only this candidate cell is bad.

---

## Mistake 6 — generating the same set in different orders

If every level loops over **all** cells again:

```text
{1,5}
```

can be generated as:

```text
1 → 5
5 → 1
```

Use increasing cell order:

```cpp
rec(level + 1, cell + 1);
```

---

## Mistake 7 — confusing board state with recursion state

The complete recursive state is not only:

```text
level
```

It includes:

```text
board
startCell
level
```

The board records **which** knights were placed.

`level` records **how many**.

`startCell` records **which cells may be considered next**.

---

## Mistake 8 — using `int` for a potentially large answer

Counting problems can grow quickly.

Prefer:

```cpp
long long ans;
```

unless the problem explicitly guarantees a smaller range.

---

# 16. LCCM Recognition Checklist

When you see a new placement/backtracking problem, ask:

### Level

```text
What does one recursion level mean?
```

Here:

```text
one more knight has been placed
```

### Choice

```text
What are all possibilities for the next decision?
```

Here:

```text
choose the next candidate cell
```

### Check

```text
Can this choice still belong to a valid final answer?
```

Here:

```text
cell must be empty
+
no existing knight may attack it
```

### Move

```text
What state do I change?
How do I recurse?
What exactly must I undo?
```

Here:

```text
board[i][j] = 1
rec(level + 1, cell + 1)
board[i][j] = 0
```

---

# 17. Final Mental Model

Do not memorize the complete code.

Remember this picture:

```text
                K KNIGHTS
                    |
                    v
      level = knights already placed
                    |
                    v
          choose next board cell
                    |
                    v
              check1(i,j)
          /                     \
       unsafe                  safe
         X                       |
                                 v
                              PLACE
                                 |
                                 v
                         rec(level + 1)
                                 |
                                 v
                              UNDO
```

And remember why `startCell` exists:

```text
identical knights
      ↓
order must not matter
      ↓
choose cells in increasing order
      ↓
no duplicate permutations
```

The reusable backtracking template is:

```text
LEVEL   → how much of the solution is built?
CHOICE  → what can I try now?
CHECK   → is this choice feasible?
MOVE    → change → recurse → undo
```

For this problem:

```text
LEVEL   = number of knights placed
CHOICE  = next candidate cell
CHECK   = no collision / no knight attack
MOVE    = place → recurse → unplace
```

That is the Sam/TLE-style LCCM model to carry into later backtracking problems.
