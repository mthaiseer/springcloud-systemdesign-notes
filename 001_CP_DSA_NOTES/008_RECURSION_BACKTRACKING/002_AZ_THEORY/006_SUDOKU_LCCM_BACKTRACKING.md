# Sudoku Solver Using LCCM — Backtracking Master Note

> **Goal:** Learn how to model Sudoku as a clean **LCCM backtracking problem** and understand both the simple scan-based solution and the faster bitmask version.
>
> **LCCM = Level → Choice → Check → Move**

---

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Prerequisites](#2-prerequisites)
   - [2.1 Backtracking Mental Model](#21-backtracking-mental-model)
   - [2.2 LCCM Framework](#22-lccm-framework)
   - [2.3 Sudoku Constraints](#23-sudoku-constraints)
   - [2.4 Row-Major Traversal](#24-row-major-traversal)
   - [2.5 Box Indexing Formula](#25-box-indexing-formula)
   - [2.6 Place → Recurse → Unplace](#26-place--recurse--unplace)
   - [2.7 Bitmask Basics](#27-bitmask-basics)
3. [State Design](#3-state-design)
4. [LCCM Mapping](#4-lccm-mapping)
5. [Scan-Based Check](#5-scan-based-check)
6. [Base Cases and Cell Transitions](#6-base-cases-and-cell-transitions)
7. [Complete Scan-Based C++17](#7-complete-scan-based-c17)
8. [Step-by-Step Dry Run](#8-step-by-step-dry-run)
9. [Recursion Tree View](#9-recursion-tree-view)
10. [Why Backtracking Works](#10-why-backtracking-works)
11. [Bitmask Optimization](#11-bitmask-optimization)
12. [Complete Bitmask C++17](#12-complete-bitmask-c17)
13. [Bitmask Dry Run](#13-bitmask-dry-run)
14. [Correctness](#14-correctness)
15. [Complexity](#15-complexity)
16. [Common Mistakes](#16-common-mistakes)
17. [Interview Variants](#17-interview-variants)
18. [Final LCCM Recognition Model](#18-final-lccm-recognition-model)

---

# 1. Problem Statement

You are given a partially filled Sudoku board.

Fill every empty cell so that:

- every row contains each allowed value exactly once,
- every column contains each allowed value exactly once,
- every smaller square box contains each allowed value exactly once,
- fixed clues must never be changed.

We use:

```text
0 = empty cell
1..boardSize = legal Sudoku values
```

For learning, use a **4 × 4 Sudoku**:

```text
boardSize = 4
cellSize  = 2
```

So:

```text
Allowed values = {1, 2, 3, 4}
Each box       = 2 × 2
```

Example:

```text
0 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

One valid completion is:

```text
4 1 | 2 3
2 3 | 4 1
----+----
3 4 | 1 2
1 2 | 3 4
```

---

# 2. Prerequisites

## 2.1 Backtracking Mental Model

Backtracking means:

```text
Make one decision
→ explore everything possible after it
→ undo the decision
→ try the next decision
```

Generic structure:

```text
for every choice:
    if choice is valid:
        make choice
        recurse
        undo choice
```

The important point is that recursion explores **one branch at a time**.

### Tiny example

Suppose we must build all length-2 strings using `{A, B}`.

```text
                     ""
                  /      \
                A          B
              /   \      /   \
            AA    AB    BA    BB
```

At the root:

```text
Level  = position 0
Choice = A or B
```

After choosing `A`:

```text
state = "A"
```

We recursively decide position 1.

After returning:

```text
undo A
try B
```

Sudoku follows exactly the same idea, except invalid choices are rejected.

---

## 2.2 LCCM Framework

For every backtracking problem, ask four questions.

| Part | Question |
|---|---|
| **Level** | What part of the answer am I deciding now? |
| **Choice** | What options can I try here? |
| **Check** | Which choices are currently legal? |
| **Move** | How do I apply the choice, recurse, and restore state? |

Generic template:

```text
rec(level):

    if complete:
        process answer
        return

    for each choice:
        if check(choice):
            make(choice)
            rec(next level)
            undo(choice)
```

For Sudoku:

```text
Level  = current cell
Choice = candidate number
Check  = row + column + box
Move   = place → recurse → unplace
```

---

## 2.3 Sudoku Constraints

A candidate value is legal at `(row, col)` only if it is absent from:

```text
1. the current row
2. the current column
3. the current small box
```

Example:

```text
0 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

Suppose we are deciding cell `(0, 0)`.

Row 0 contains:

```text
{1}
```

Column 0 contains:

```text
{}
```

Its 2 × 2 box contains:

```text
{1}
```

Therefore:

```text
1 is forbidden
2, 3, 4 are possible
```

---

## 2.4 Row-Major Traversal

We process cells left-to-right and top-to-bottom.

For a 4 × 4 board:

```text
(0,0) → (0,1) → (0,2) → (0,3)
                                ↓
(1,0) → (1,1) → (1,2) → (1,3)
                                ↓
(2,0) → ...
```

Equivalent single-index representation:

```text
level = row * boardSize + col
```

and:

```text
row = level / boardSize
col = level % boardSize
```

We will use `(row, col)` directly because Sudoku checks naturally use coordinates.

---

## 2.5 Box Indexing Formula

For a cell `(row, col)`, the top-left corner of its box is:

```cpp
int startRow = (row / cellSize) * cellSize;
int startCol = (col / cellSize) * cellSize;
```

### Example

For a 4 × 4 Sudoku:

```text
cellSize = 2
cell = (3, 2)
```

Then:

```text
startRow = (3 / 2) * 2
         = 1 * 2
         = 2

startCol = (2 / 2) * 2
         = 1 * 2
         = 2
```

So `(3,2)` belongs to this box:

```text
rows 2..3
cols 2..3
```

ASCII view:

```text
          col
        0 1 | 2 3
      +-----+-----+
row 0 |     |     |
row 1 |     |     |
      +-----+-----+
row 2 |     | X X |
row 3 |     | X X |
      +-----+-----+
              ^
              box containing (3,2)
```

---

## 2.6 Place → Recurse → Unplace

This is the heart of Sudoku backtracking.

```cpp
board[row][col] = choice;
rec(nextCell);
board[row][col] = 0;
```

Why undo?

Because sibling branches must start from the same original state.

Example:

```text
At cell (0,0)

Try 2
    explore everything beginning with 2
undo 2

Try 3
    explore everything beginning with 3
undo 3

Try 4
    explore everything beginning with 4
undo 4
```

Without undo:

```text
branch 2 would leak into branch 3
branch 3 would leak into branch 4
```

That would make the search incorrect.

---

## 2.7 Bitmask Basics

The optimized solution stores used values as bits.

For values `1..4`:

```text
value 1 → bit 1 → 00010
value 2 → bit 2 → 00100
value 3 → bit 3 → 01000
value 4 → bit 4 → 10000
```

Bit 0 is deliberately unused.

If a row contains `{1,3}`:

```text
rowMask = 01010
```

because bits 1 and 3 are set.

### Set a bit

```cpp
mask |= (1 << value);
```

### Test a bit

```cpp
if (mask & (1 << value)) {
    // value is already present
}
```

### Clear a bit

```cpp
mask &= ~(1 << value);
```

### Combine forbidden values

```cpp
taken = rowMask | colMask | boxMask;
```

Then legal values are the allowed bits not present in `taken`.

---

# 3. State Design

A **state** contains enough information to describe the current partial solution and determine future legal moves.

For the scan-based Sudoku solution:

```text
State =
    current board
    current cell (row, col)
```

The solution counter `ans` is accumulated globally, but it does not decide which moves are legal.

## Recursive contract

For:

```cpp
rec(row, col)
```

assume:

```text
1. Every earlier cell in row-major order has been processed.
2. All temporary choices currently on the board are mutually valid.
3. Fixed clues remain unchanged.
4. The function must explore every valid completion from this state.
5. Every temporary move made by this function must be undone before returning.
```

This contract makes the recursion easy to reason about.

---

# 4. LCCM Mapping

## Level

The current cell:

```text
(row, col)
```

---

## Choice

If the cell is empty:

```text
1, 2, ..., boardSize
```

If it already contains a clue:

```text
there is no choice
→ simply move forward
```

There is **no skip branch**.

A completed Sudoku must fill every cell.

---

## Check

For candidate `ch` at `(row, col)`:

```text
ch must not already appear in:
    row
    column
    box
```

---

## Move

For a legal candidate:

```text
place
↓
recurse to next cell
↓
unplace
```

Code shape:

```cpp
board[row][col] = ch;
rec(row, col + 1);
board[row][col] = 0;
```

---

## LCCM Diagram

```text
                  ┌────────────────────┐
                  │ LEVEL              │
                  │ Current cell       │
                  │ (row, col)         │
                  └─────────┬──────────┘
                            │
                            v
                  ┌────────────────────┐
                  │ CHOICE             │
                  │ Try 1..boardSize   │
                  └─────────┬──────────┘
                            │
                            v
                  ┌────────────────────┐
                  │ CHECK              │
                  │ Row?               │
                  │ Column?            │
                  │ Box?               │
                  └─────────┬──────────┘
                            │ valid
                            v
                  ┌────────────────────┐
                  │ MOVE               │
                  │ Place              │
                  │ Recurse            │
                  │ Unplace            │
                  └─────────┬──────────┘
                            │
                            └────→ next choice
```

---

# 5. Scan-Based Check

We first build the simple version.

## 5.1 Check the column

```cpp
for (int i = 0; i < boardSize; i++) {
    if (i != row && board[i][col] == ch) {
        return false;
    }
}
```

---

## 5.2 Check the row

```cpp
for (int i = 0; i < boardSize; i++) {
    if (i != col && board[row][i] == ch) {
        return false;
    }
}
```

---

## 5.3 Check the box

```cpp
int startRow = (row / cellSize) * cellSize;
int startCol = (col / cellSize) * cellSize;

for (int i = 0; i < cellSize; i++) {
    for (int j = 0; j < cellSize; j++) {
        int r = startRow + i;
        int c = startCol + j;

        if (r == row && c == col) {
            continue;
        }

        if (board[r][c] == ch) {
            return false;
        }
    }
}
```

---

## 5.4 Why exclude the current cell?

The same `check()` can be used to validate fixed clues before recursion starts.

If the cell already contains `ch`, then scanning without excluding itself would falsely report a conflict with itself.

---

# 6. Base Cases and Cell Transitions

Three transitions are important.

## Case 1 — all rows finished

```cpp
if (row == boardSize) {
    ans++;
    printBoard();
    return;
}
```

This is a complete valid board.

---

## Case 2 — current row finished

```cpp
if (col == boardSize) {
    rec(row + 1, 0);
    return;
}
```

ASCII:

```text
(0,3)
  |
  | col becomes 4
  v
(1,0)
```

---

## Case 3 — fixed clue

```cpp
if (board[row][col] != 0) {
    rec(row, col + 1);
    return;
}
```

We never change a clue.

---

## Empty cell

```cpp
for each candidate:
    if legal:
        place
        recurse
        undo
```

If no candidate is legal:

```text
no recursive call occurs
→ current function returns
→ caller undoes its previous move
```

This is how failure naturally triggers backtracking.

---

# 7. Complete Scan-Based C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

const int boardSize = 4;
const int cellSize = 2;

int board[boardSize][boardSize];
long long ans = 0;

bool check(int ch, int row, int col) {
    for (int i = 0; i < boardSize; i++) {
        if (i != row && board[i][col] == ch) {
            return false;
        }
    }

    for (int i = 0; i < boardSize; i++) {
        if (i != col && board[row][i] == ch) {
            return false;
        }
    }

    int startRow = (row / cellSize) * cellSize;
    int startCol = (col / cellSize) * cellSize;

    for (int i = 0; i < cellSize; i++) {
        for (int j = 0; j < cellSize; j++) {
            int r = startRow + i;
            int c = startCol + j;

            if (r == row && c == col) {
                continue;
            }

            if (board[r][c] == ch) {
                return false;
            }
        }
    }

    return true;
}

bool validInitialBoard() {
    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            int ch = board[row][col];

            if (ch < 0 || ch > boardSize) {
                return false;
            }

            if (ch != 0 && !check(ch, row, col)) {
                return false;
            }
        }
    }

    return true;
}

void printBoard() {
    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            if (col > 0) {
                cout << ' ';
            }

            cout << board[row][col];
        }

        cout << '\n';
    }

    cout << '\n';
}

void rec(int row, int col) {
    if (row == boardSize) {
        ans++;
        printBoard();
        return;
    }

    if (col == boardSize) {
        rec(row + 1, 0);
        return;
    }

    if (board[row][col] != 0) {
        rec(row, col + 1);
        return;
    }

    for (int ch = 1; ch <= boardSize; ch++) {
        if (check(ch, row, col)) {
            board[row][col] = ch;

            rec(row, col + 1);

            board[row][col] = 0;
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            cin >> board[row][col];
        }
    }

    if (!validInitialBoard()) {
        cout << 0 << '\n';
        return 0;
    }

    rec(0, 0);

    cout << ans << '\n';

    return 0;
}
```

---

# 8. Step-by-Step Dry Run

Use:

```text
0 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

At `(0,0)`:

```text
legal choices = {2,3,4}
```

---

## Attempt 1 — choose 2

```text
Level  : (0,0)
Choice : 2
Check  : valid
Move   : board[0][0] = 2
```

Board:

```text
2 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

Cell `(0,1)` is fixed `1`, so skip to `(0,2)`.

At `(0,2)`:

```text
candidate 1 → row conflict
candidate 2 → row conflict
candidate 3 → column conflict
candidate 4 → column conflict
```

No legal value.

Therefore:

```text
rec(0,2) returns
→ undo 2 at (0,0)
```

Back to:

```text
0 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

---

## Attempt 2 — choose 3

```text
(0,0) = 3
```

At `(0,2)`, choose `2`:

```text
3 1 | 2 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

At `(0,3)`:

```text
row needs 4
```

But the upper-right 2 × 2 box already contains `4`.

Dead end.

Compact trace:

```text
(0,0)=3
→ (0,2)=2
→ (0,3): no legal value
→ undo (0,2)=2
→ undo (0,0)=3
```

---

## Attempt 3 — choose 4

```text
(0,0)=4
```

The search eventually reaches:

```text
4 1 | 2 3
2 3 | 4 1
----+----
3 4 | 1 2
1 2 | 3 4
```

Then recursion moves beyond the final cell.

```text
row == boardSize
```

So:

```text
ans++
print board
```

The successful branch also unwinds and restores its temporary placements so other possible completions can be explored.

---

# 9. Recursion Tree View

The complete tree is huge, so visualize only meaningful early branches.

```text
rec(0,0)
│
├── choose 2
│   │
│   └── rec(0,1)
│       └── fixed 1
│           └── rec(0,2)
│               └── DEAD END
│
├── choose 3
│   │
│   └── rec(0,1)
│       └── fixed 1
│           └── rec(0,2)
│               └── choose 2
│                   └── rec(0,3)
│                       └── DEAD END
│
└── choose 4
    │
    └── rec(0,1)
        └── fixed 1
            └── rec(0,2)
                └── choose 2
                    └── rec(0,3)
                        └── choose 3
                            └── ...
                                └── COMPLETE BOARD
```

The tree shows the key backtracking idea:

```text
bad branch
→ return upward
→ restore state
→ try sibling branch
```

---

# 10. Why Backtracking Works

The checker answers only:

```text
"Is this move legal RIGHT NOW?"
```

It does **not** answer:

```text
"Will this move eventually produce a full solution?"
```

Example:

```text
(0,0)=2
```

is locally legal.

But several levels later:

```text
(0,2)
```

has no legal value.

So the algorithm must search.

This is the difference between:

```text
local feasibility
```

and:

```text
global solvability
```

Backtracking combines both:

```text
Check local feasibility
+
Recursively test global completion
```

---

# 11. Bitmask Optimization

Scanning the row, column, and box repeatedly is unnecessary.

We only need to know:

```text
Which values are already used in this row?
Which values are already used in this column?
Which values are already used in this box?
```

Store these sets as bitmasks.

---

## 11.1 Masks

```cpp
int takenRow[boardSize];
int takenCol[boardSize];
int takenGrid[cellSize][cellSize];
```

Meaning:

```text
takenRow[r]       = used values in row r
takenCol[c]       = used values in column c
takenGrid[br][bc] = used values in one box
```

---

## 11.2 Full mask

For a 4 × 4 Sudoku:

```text
valid values = 1,2,3,4
```

We want bits:

```text
11110
```

because bit 0 is unused.

Formula:

```cpp
const int fullMask = (1 << (boardSize + 1)) - 2;
```

For `boardSize = 4`:

```text
1 << 5 = 100000
100000 - 2 = 11110
```

---

## 11.3 Combine forbidden values

At `(row,col)`:

```cpp
int taken = takenRow[row]
          | takenCol[col]
          | takenGrid[row / cellSize][col / cellSize];
```

Example:

```text
row has    {1,2} → 00110
column has {3,4} → 11000
box has    {4}   → 10000
                     -----
OR                  11110
```

So every candidate is forbidden.

---

## 11.4 Compute available values

```cpp
int choices = fullMask & ~taken;
```

Why `& fullMask`?

Because `~taken` also turns irrelevant higher bits into `1`.

We must keep only valid Sudoku-value bits.

---

## 11.5 Extract only available choices

Suppose:

```text
choices = 11100
```

Available values are:

```text
2,3,4
```

Extract the lowest set bit:

```cpp
int bit = choices ^ (choices & (choices - 1));
```

Example:

```text
choices                  = 11100
choices & (choices - 1)  = 11000
XOR                      = 00100
```

`00100` represents value `2`.

Convert that bit to its position:

```cpp
int ch = __builtin_ffs(bit) - 1;
```

Then remove it:

```cpp
choices &= choices - 1;
```

Sequence:

```text
11100 → choose 2 → 11000
11000 → choose 3 → 10000
10000 → choose 4 → 00000
```

---

## 11.6 Move and undo all representations

Placement:

```cpp
board[row][col] = ch;
takenRow[row] |= bit;
takenCol[col] |= bit;
takenGrid[row / cellSize][col / cellSize] |= bit;
```

Undo:

```cpp
board[row][col] = 0;
takenRow[row] &= ~bit;
takenCol[col] &= ~bit;
takenGrid[row / cellSize][col / cellSize] &= ~bit;
```

Important invariant:

```text
If state is represented in 4 places,
undo must restore all 4 places.
```

---

# 12. Complete Bitmask C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

const int boardSize = 4;
const int cellSize = 2;

const int fullMask = (1 << (boardSize + 1)) - 2;

int board[boardSize][boardSize];

int takenRow[boardSize] = {};
int takenCol[boardSize] = {};
int takenGrid[cellSize][cellSize] = {};

long long ans = 0;

int getChoices(int row, int col) {
    int taken = takenRow[row]
              | takenCol[col]
              | takenGrid[row / cellSize][col / cellSize];

    return fullMask & ~taken;
}

void makeMove(int ch, int row, int col) {
    int bit = 1 << ch;

    board[row][col] = ch;
    takenRow[row] |= bit;
    takenCol[col] |= bit;
    takenGrid[row / cellSize][col / cellSize] |= bit;
}

void revertMove(int ch, int row, int col) {
    int bit = 1 << ch;

    board[row][col] = 0;
    takenRow[row] &= ~bit;
    takenCol[col] &= ~bit;
    takenGrid[row / cellSize][col / cellSize] &= ~bit;
}

bool initializeMasks() {
    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            int ch = board[row][col];

            if (ch == 0) {
                continue;
            }

            if (ch < 1 || ch > boardSize) {
                return false;
            }

            int bit = 1 << ch;

            int taken = takenRow[row]
                      | takenCol[col]
                      | takenGrid[row / cellSize][col / cellSize];

            if (taken & bit) {
                return false;
            }

            makeMove(ch, row, col);
        }
    }

    return true;
}

void printBoard() {
    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            if (col > 0) {
                cout << ' ';
            }

            cout << board[row][col];
        }

        cout << '\n';
    }

    cout << '\n';
}

void rec(int row, int col) {
    if (row == boardSize) {
        ans++;
        printBoard();
        return;
    }

    if (col == boardSize) {
        rec(row + 1, 0);
        return;
    }

    if (board[row][col] != 0) {
        rec(row, col + 1);
        return;
    }

    int choices = getChoices(row, col);

    while (choices != 0) {
        int bit = choices ^ (choices & (choices - 1));
        int ch = __builtin_ffs(bit) - 1;

        makeMove(ch, row, col);

        rec(row, col + 1);

        revertMove(ch, row, col);

        choices &= choices - 1;
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    for (int row = 0; row < boardSize; row++) {
        for (int col = 0; col < boardSize; col++) {
            cin >> board[row][col];
        }
    }

    if (!initializeMasks()) {
        cout << 0 << '\n';
        return 0;
    }

    rec(0, 0);

    cout << ans << '\n';

    return 0;
}
```

---

# 13. Bitmask Dry Run

Return to `(0,0)`.

Original board:

```text
0 1 | 0 0
0 0 | 4 0
----+----
0 4 | 0 0
0 0 | 3 0
```

Masks:

```text
takenRow[0]     = {1} = 00010
takenCol[0]     = {}  = 00000
takenGrid[0][0] = {1} = 00010
```

Combine:

```text
taken = 00010
```

Full mask:

```text
11110
```

Available:

```text
choices = 11110 & ~00010
        = 11100
```

So:

```text
choices = {2,3,4}
```

---

## Choose 2

Lowest set bit:

```text
choices = 11100
lowest  = 00100
value   = 2
```

After placement:

```text
board[0][0]     = 2
takenRow[0]     = 00110
takenCol[0]     = 00100
takenGrid[0][0] = 00110
```

At `(0,2)`:

```text
row     = {1,2} → 00110
column  = {3,4} → 11000
box     = {4}   → 10000

taken   = 11110
choices = 00000
```

No candidate.

Therefore:

```text
return
↓
revertMove(2,0,0)
```

Masks become exactly what they were before trying 2.

That restoration is the bitmask version of backtracking.

---

# 14. Correctness

## 14.1 Every reported board is valid

Initially, fixed clues are validated.

Every recursive placement passes:

```text
row check
column check
box check
```

Therefore each move preserves Sudoku validity.

At the success case:

```text
every cell has been processed
```

So the completed board is valid.

---

## 14.2 Every valid completion is explored

At each empty cell, the algorithm tries every currently legal value.

Take any valid Sudoku completion.

Its correct value at each empty cell is legal with respect to:

```text
earlier choices
+
all fixed clues
```

Therefore that value appears among the choices.

Following those values produces that completion.

---

## 14.3 No completion is counted twice

Cells are processed in one fixed order.

Each completed board corresponds to exactly one sequence:

```text
choice at cell 1
choice at cell 2
...
choice at final empty cell
```

So the same completed board cannot arise through two different root-to-leaf paths.

---

# 15. Complexity

Let:

```text
B = boardSize
E = number of initially empty cells
```

Ignoring pruning, each empty cell may have up to `B` choices.

So a coarse search bound is:

```text
O(B^E)
```

For ordinary 9 × 9 Sudoku:

```text
O(9^E)
```

This is only a worst-case search-space view. Sudoku constraints prune many branches.

---

## Scan-based version

Each candidate check scans:

```text
row + column + box
```

which is:

```text
O(B)
```

Trying up to `B` candidates gives roughly:

```text
O(B²)
```

local checking work per empty-cell state.

---

## Bitmask version

Combining:

```text
rowMask | colMask | boxMask
```

is:

```text
O(1)
```

when the mask fits in one machine word.

The search remains exponential, but each legality test is much cheaper.

---

## Space

Board:

```text
O(B²)
```

Recursion depth in this implementation can also be:

```text
O(B²)
```

because it recursively walks through board positions.

---

# 16. Common Mistakes

## Mistake 1 — Forgetting to unplace

Wrong:

```cpp
board[row][col] = ch;
rec(row, col + 1);
```

Correct:

```cpp
board[row][col] = ch;
rec(row, col + 1);
board[row][col] = 0;
```

---

## Mistake 2 — Changing a fixed clue

Never do:

```text
try alternatives for a nonzero input cell
```

Instead:

```cpp
if (board[row][col] != 0) {
    rec(row, col + 1);
    return;
}
```

---

## Mistake 3 — Forgetting the box check

Sudoku is not only:

```text
row + column
```

It is:

```text
row + column + box
```

---

## Mistake 4 — Wrong box start

Wrong intuition:

```text
startRow = row / cellSize
```

Correct:

```text
startRow = (row / cellSize) * cellSize
```

because division gives the box index; multiplication converts it back into a board coordinate.

---

## Mistake 5 — Using `~taken` directly

Wrong:

```cpp
choices = ~taken;
```

This makes unrelated high bits available.

Correct:

```cpp
choices = fullMask & ~taken;
```

---

## Mistake 6 — Updating only the board in bitmask solution

Wrong:

```text
board changes
masks stay stale
```

Correct:

```text
board + row mask + column mask + box mask
must all move together
```

---

## Mistake 7 — Confusing local validity with solvability

A move can pass `check()` and still lead to a dead end later.

That is expected.

```text
check() = locally legal
recursion = globally completable?
```

---

# 17. Interview Variants

The same LCCM structure can produce different outputs.

## Count all solutions

At completion:

```cpp
return 1;
```

and sum child answers.

---

## Find any one solution

Return `true` immediately when one branch succeeds.

Important:

```text
do not undo the successful path before returning
```

if the interviewer wants the solved board preserved.

---

## Print all solutions

At the base case:

```text
print board
continue search after returning
```

---

## Check uniqueness

Count only until:

```text
2 solutions
```

Interpretation:

```text
0 → no solution
1 → unique
2 → multiple solutions
```

---

## Stronger optimization — MRV

Instead of always choosing the next cell in row-major order:

```text
choose the unfilled cell with the fewest legal values
```

This is the **Minimum Remaining Values** heuristic.

Example:

```text
Cell A has 4 possibilities
Cell B has 1 possibility
Cell C has 3 possibilities

Choose B first.
```

Why?

```text
If B is impossible, fail immediately.
If B is forced, we reduce branching early.
```

Bitmasks reduce the **cost of checking**.

MRV reduces the **number of states explored**.

These are different optimizations.

---

# 18. Final LCCM Recognition Model

When you see:

```text
fill a board
assign values
respect constraints
try alternatives
undo failed decisions
```

think:

```text
BACKTRACKING
```

Then write:

```text
STATE
What information describes the partial solution?

LEVEL
What exactly am I deciding now?

CHOICE
What values/actions can I try?

CHECK
Which choices are legal right now?

MOVE
How do I apply the choice?

RECURSE
What is the smaller remaining problem?

UNDO
How do I restore the exact previous state?

SUCCESS
When is the solution complete?
```

For Sudoku:

```text
STATE
    board + current cell
    optimized: row/column/box masks too

LEVEL
    current cell

CHOICE
    candidate value 1..B

CHECK
    absent from row, column, and box

MOVE
    place
    recurse
    unplace

SUCCESS
    all cells processed
```

The entire solver reduces to one mental picture:

```text
                    CURRENT CELL
                         |
                         v
                 try candidate value
                         |
                         v
               row / col / box valid?
                  /             \
                no               yes
                |                 |
              skip             PLACE
                                  |
                                  v
                              RECURSE
                                  |
                                  v
                               UNDO
                                  |
                                  v
                           next candidate
```

That is the reusable LCCM pattern behind Sudoku and many other constraint-search problems.
