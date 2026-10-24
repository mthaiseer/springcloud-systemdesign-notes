# ABC Puzzle — Row-by-Row Backtracking Using LCCM

> **Goal:** Learn how to reduce a seemingly huge grid-search problem into two clean recursive phases:
>
> 1. generate only valid row patterns,
> 2. assemble the grid row-by-row using LCCM.
>
> **LCCM = Level → Choice → Check → Move**

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Why Brute Force Over Cells Is Too Large](#2-why-brute-force-over-cells-is-too-large)
3. [Prerequisites](#3-prerequisites)
   - [3.1 Backtracking Mental Model](#31-backtracking-mental-model)
   - [3.2 LCCM Framework](#32-lccm-framework)
   - [3.3 Unique Permutations with Frequencies](#33-unique-permutations-with-frequencies)
   - [3.4 First Nonempty Character](#34-first-nonempty-character)
   - [3.5 Bitmask Prerequisite](#35-bitmask-prerequisite)
   - [3.6 Why Traversal Order Matters](#36-why-traversal-order-matters)
4. [Key Design Decision — Recurse by Row](#4-key-design-decision--recurse-by-row)
5. [Phase 1 — Generate All Valid Row Patterns](#5-phase-1--generate-all-valid-row-patterns)
6. [Phase 1 Recursion Tree](#6-phase-1-recursion-tree)
7. [Phase 2 — Build the Grid Row by Row](#7-phase-2--build-the-grid-row-by-row)
8. [The Column-Started Bitmask](#8-the-column-started-bitmask)
9. [LCCM Mapping for Grid Search](#9-lccm-mapping-for-grid-search)
10. [Base Case and Final Column Validation](#10-base-case-and-final-column-validation)
11. [Step-by-Step Dry Run](#11-step-by-step-dry-run)
12. [Grid Search Recursion Tree](#12-grid-search-recursion-tree)
13. [Complete C++17 Solution](#13-complete-c17-solution)
14. [Why the Algorithm Is Correct](#14-why-the-algorithm-is-correct)
15. [Complexity](#15-complexity)
16. [Common Mistakes](#16-common-mistakes)
17. [Interview/CP Improvements](#17-interviewcp-improvements)
18. [Final Recognition Model](#18-final-recognition-model)

---

# 1. Problem Statement

You are given:

```text
N
R
C
```

where:

```text
3 <= N <= 5
R and C are strings of length N
R[i], C[i] ∈ {'A','B','C'}
```

We must fill an `N × N` grid using:

```text
'A', 'B', 'C', '.'
```

where `.` means empty.

The completed grid must satisfy:

```text
1. Every row contains exactly one A, one B, and one C.
2. Every column contains exactly one A, one B, and one C.
3. The first nonempty character from the LEFT in row i is R[i].
4. The first nonempty character from the TOP in column j is C[j].
```

We need to print:

```text
Yes
<one valid grid>
```

or:

```text
No
```

if no valid grid exists.

---

# 2. Why Brute Force Over Cells Is Too Large

Each cell could naively be one of:

```text
'.', 'A', 'B', 'C'
```

So there are:

```text
4^(N*N)
```

possible complete grids.

For `N = 5`:

```text
4^25
```

which is far too large.

But every valid row has a very strong structure:

```text
exactly one A
exactly one B
exactly one C
N - 3 dots
```

Therefore we should not build the grid cell-by-cell.

We should generate only rows that are already valid.

This is the first major reduction:

```text
BAD DESIGN
choose every cell independently

GOOD DESIGN
choose one complete valid row at each recursive level
```

---

# 3. Prerequisites

## 3.1 Backtracking Mental Model

Backtracking means:

```text
choose
→ recurse
→ undo
→ try next choice
```

Generic form:

```cpp
for (choice : choices) {
    if (check(choice)) {
        make(choice);

        rec(nextLevel);

        undo(choice);
    }
}
```

The key idea is:

```text
Each recursive call explores one branch.
Sibling branches must start from the same state.
```

---

## 3.2 LCCM Framework

For any backtracking problem, ask:

| Part | Question |
|---|---|
| **Level** | What part of the answer am I deciding now? |
| **Choice** | What can I try at this level? |
| **Check** | Which choices are legal? |
| **Move** | How do I apply the choice, recurse, and restore? |

Generic template:

```text
rec(level):

    if complete:
        process answer
        return

    for each choice:
        if check(choice):
            make choice
            rec(level + 1)
            undo choice
```

In this problem, we use **LCCM twice**:

```text
Phase 1:
    decide one character of a row

Phase 2:
    decide one whole row of the grid
```

That distinction is important.

---

## 3.3 Unique Permutations with Frequencies

Every valid row is a permutation of:

```text
A, B, C, '.', '.', ...
```

with exactly `N - 3` dots.

For example, when `N = 5`:

```text
multiset = {A, B, C, '.', '.'}
```

If we directly permute positions, repeated dots can generate duplicate branches.

Instead, maintain character frequencies:

```text
freq['A'] = 1
freq['B'] = 1
freq['C'] = 1
freq['.'] = N - 3
```

At each recursive position:

```text
choose a character whose remaining frequency > 0
decrease its frequency
append it
recurse
undo
```

### Tiny example

For:

```text
{A, B, .}
```

recursion begins:

```text
                           ""
                    /       |       \
                   A        B        .
                 /  \      / \      / \
                B    .    A   .    A   B
               /      ...
```

Because `.` is represented once in the frequency map, equal dots never create duplicate choices.

---

## 3.4 First Nonempty Character

The rule says:

```text
first NONEMPTY character
```

not:

```text
first cell
```

Example:

```text
..CAB
```

The first nonempty character is:

```text
C
```

not `.`.

Therefore, after generating a row, scan left-to-right:

```cpp
for (char ch : s) {
    if (ch != '.') {
        // this is the first nonempty character
        break;
    }
}
```

We group rows into three buckets:

```text
bucket A → first nonempty letter is A
bucket B → first nonempty letter is B
bucket C → first nonempty letter is C
```

Then row `level` only considers:

```text
bucket[R[level]]
```

This enforces the row-leftmost condition by construction.

---

## 3.5 Bitmask Prerequisite

We need to remember which columns have already received their **topmost nonempty character**.

Use one bit per column.

For `N = 5`:

```text
bit 0 → column 0
bit 1 → column 1
bit 2 → column 2
bit 3 → column 3
bit 4 → column 4
```

Example:

```text
done = 10011
```

means columns:

```text
0, 1, 4
```

have already started.

Important:

```text
done bit = 1
```

means only:

```text
"The topmost character in this column has already been determined."
```

It does NOT mean:

```text
column is full
column is valid
column already contains A,B,C
```

---

## 3.6 Why Traversal Order Matters

Rows are processed:

```text
top → bottom
```

Therefore, when a column receives its first nonempty character:

```text
that character permanently becomes the column's topmost character
```

Future rows are below it.

So we can check:

```text
if this is the first character inserted into column j,
it must equal C[j]
```

and never recheck the topmost rule for that column later.

This is a powerful general idea:

```text
Use traversal order to make a constraint permanent as soon as it becomes determined.
```

---

# 4. Key Design Decision — Recurse by Row

Instead of this:

```text
Level = one cell
```

use:

```text
Level = one complete row
```

Why?

Because row validity can be guaranteed beforehand.

Each generated row already has:

```text
exactly one A
exactly one B
exactly one C
```

Then the second recursion only needs to reason about columns.

This separates responsibilities:

| Constraint | Where enforced |
|---|---|
| One A/B/C per row | Row generation |
| Row leftmost = R[i] | Row bucket |
| Column topmost = C[j] | Incremental check |
| One A/B/C per column | Base-case validation |

This is much cleaner than checking all constraints at every cell.

---

# 5. Phase 1 — Generate All Valid Row Patterns

## State

We maintain:

```text
s      = row prefix already built
freq   = remaining character counts
level  = next position to fill
```

Invariant:

```text
s.size() == level
```

and `freq` exactly describes unused characters.

---

## LCCM for row generation

### Level

```text
current character position
```

### Choice

```text
one distinct character from freq
```

### Check

```text
remaining count > 0
```

### Move

```text
decrement count
append character
recurse
pop character
restore count
```

Code shape:

```cpp
remaining--;
s.push_back(ch);

generate(level + 1);

s.pop_back();
remaining++;
```

---

## Base case

When:

```text
level == N
```

we have a complete row.

Find its first nonempty character and store it in the proper bucket.

Example:

```text
AC..B → bucket A
.BA.C → bucket B
..CAB → bucket C
```

---

## How many unique rows?

The row contains:

```text
A, B, C, and N-3 identical dots
```

So:

```text
P = N! / (N-3)!
```

which simplifies to:

```text
P = N(N-1)(N-2)
```

Examples:

```text
N = 3 → 6 rows
N = 4 → 24 rows
N = 5 → 60 rows
```

Each first-letter bucket has:

```text
P / 3
```

rows.

For `N = 5`:

```text
60 / 3 = 20
```

So each grid level has at most 20 row choices.

---

# 6. Phase 1 Recursion Tree

Small illustration for `N = 4`:

```text
multiset = {A,B,C,.}
```

Partial tree:

```text
                           ""
               /            |            |            \
              A             B            C             .
           /  |  \       /  |  \      ...          / | \
          B   C   .      A   C   .                 A  B  C
         /         \
        C           B
       /             \
      .               C
      |               |
    ABC.            A.BC
```

Each leaf is one unique row.

Then leaves are grouped:

```text
ABC.  → A bucket
A.BC  → A bucket
.BAC  → B bucket
..CA? → based on first non-dot
```

The first recursion does not build the grid yet.

It builds the **choice library** for the second recursion.

---

# 7. Phase 2 — Build the Grid Row by Row

The recursive function is:

```cpp
rec(level, currDone)
```

State:

```text
level      = current row index
curr_sol   = rows already selected
currDone   = columns whose topmost letter is already fixed
```

Invariant before `rec(level, currDone)`:

```text
1. curr_sol contains exactly level rows.
2. Every selected row already satisfies its row rules.
3. If bit j of currDone is 1, column j already has a topmost letter.
4. That topmost letter equals C[j].
5. Column A/B/C multiplicity is NOT yet guaranteed.
```

That final point is important.

The partial columns are not necessarily fully valid yet.

---

# 8. The Column-Started Bitmask

Initially:

```cpp
int currDone = 0;
```

No column has started.

All columns started:

```cpp
int allDone = (1 << n) - 1;
```

For `N = 5`:

```text
allDone = 11111
```

---

## Checking one candidate row

For each column `pos`:

### Case 1 — row[pos] == '.'

Nothing happens.

```text
No letter is inserted.
```

---

### Case 2 — column already started

```text
done bit = 1
```

This new letter is below the already-fixed topmost character.

So no topmost check is needed.

---

### Case 3 — column has not started

```text
done bit = 0
```

This new letter becomes the topmost character.

Therefore it must satisfy:

```text
row[pos] == C[pos]
```

Rejection condition:

```cpp
if (row[pos] != '.' &&
    (done & (1 << pos)) == 0 &&
    row[pos] != C[pos]) {
    return false;
}
```

---

## Updating the mask

For every nonempty cell:

```cpp
done |= (1 << pos);
```

Use OR, not XOR.

Why?

Suppose bit 2 is already `1`.

Another letter later appears in column 2.

With OR:

```text
1 OR 1 = 1
```

Correct.

With XOR:

```text
1 XOR 1 = 0
```

Wrong — it would falsely say the column has not started.

---

# 9. LCCM Mapping for Grid Search

## Level

```text
current row index
```

At:

```text
level = 0 → choose row 0
level = 1 → choose row 1
...
```

---

## Choice

Only rows whose first nonempty letter matches:

```text
R[level]
```

So:

```cpp
all_positions[R[level] - 'A']
```

---

## Check

For every newly started column:

```text
candidate's letter must equal C[column]
```

---

## Move

```text
append row
recurse with updated mask
remove row
```

Code:

```cpp
curr_sol.push_back(row);

rec(level + 1, newDone(currDone, row));

curr_sol.pop_back();
```

Because `currDone` is passed by value, its restoration is automatic.

The vector must still be explicitly undone.

---

## LCCM Diagram

```text
                 ┌───────────────────────┐
                 │ LEVEL                 │
                 │ choose grid row i     │
                 └───────────┬───────────┘
                             │
                             v
                 ┌───────────────────────┐
                 │ CHOICE                │
                 │ row from bucket R[i]  │
                 └───────────┬───────────┘
                             │
                             v
                 ┌───────────────────────┐
                 │ CHECK                 │
                 │ new column start?     │
                 │ must match C[j]       │
                 └───────────┬───────────┘
                             │ valid
                             v
                 ┌───────────────────────┐
                 │ MOVE                  │
                 │ push row              │
                 │ update mask           │
                 │ recurse               │
                 │ pop row               │
                 └───────────────────────┘
```

---

# 10. Base Case and Final Column Validation

When:

```text
level == N
```

all rows are selected.

Now we must ensure every column contains:

```text
one A
one B
one C
```

The supplied approach inserts each non-dot column character into a set:

```cpp
set<char> letters;
```

and checks:

```cpp
letters.size() == 3
```

At first this looks suspicious.

A column such as:

```text
A A B C
```

also has three distinct letters.

Why is the check still sufficient?

Because of a global counting argument.

Each row contains exactly three letters.

There are `N` rows.

So total non-dot letters in the grid:

```text
3N
```

If every column contains all three distinct letters, then every column has at least three letters.

Across `N` columns, that requires at least:

```text
3N
```

letters.

But the entire grid contains exactly `3N`.

Therefore every column contains exactly three letters.

Since they are distinct:

```text
A, B, C
```

each occurs exactly once.

This is a good example of using an invariant to justify a seemingly weak final check.

---

# 11. Step-by-Step Dry Run

Use:

```text
N = 5
R = ABCBC
C = ACAAB
```

One successful grid is:

```text
AC..B
.BA.C
C.BA.
BA.C.
..CBA
```

We follow this path for learning.

Bits are written as:

```text
bit 4 ... bit 0
```

---

## Level 0

Required first row letter:

```text
R[0] = A
```

Choose:

```text
AC..B
```

Occupied columns:

```text
0, 1, 4
```

All are new.

Check:

```text
col 0 starts with A → C[0] = A ✓
col 1 starts with C → C[1] = C ✓
col 4 starts with B → C[4] = B ✓
```

Mask:

```text
10011
```

State:

```text
curr_sol =
AC..B
```

---

## Level 1

Required first letter:

```text
R[1] = B
```

Suppose we try some candidate that places `C` in column 2.

Column 2 has not started.

But:

```text
C[2] = A
```

So that candidate is rejected immediately.

Now choose:

```text
.BA.C
```

Nonempty positions:

```text
1, 2, 4
```

Columns 1 and 4 already started.

Column 2 is new:

```text
row[2] = A
C[2]   = A
```

Valid.

New mask:

```text
10111
```

---

## Level 2

Required first letter:

```text
R[2] = C
```

Choose:

```text
C.BA.
```

Newly started column:

```text
3
```

Check:

```text
row[3] = A
C[3]   = A
```

Valid.

New mask:

```text
11111
```

Now all columns have started.

Important:

```text
Search is NOT finished.
```

We still need rows 3 and 4.

The mask only means topmost characters are determined.

---

## Level 3

Required first letter:

```text
R[3] = B
```

Choose:

```text
BA.C.
```

No new column starts.

Mask stays:

```text
11111
```

---

## Level 4

Required first letter:

```text
R[4] = C
```

Choose:

```text
..CBA
```

Again:

```text
11111
```

---

## Base case

Now:

```text
level == 5
```

Check the columns:

```text
Column 0: A . C B . → {A,B,C}
Column 1: C B . A . → {A,B,C}
Column 2: . A B . C → {A,B,C}
Column 3: . . A C B → {A,B,C}
Column 4: B C . . A → {A,B,C}
```

Every column has three distinct letters.

Therefore:

```text
valid grid found
```

Save:

```cpp
final_sol = curr_sol;
sol_found = true;
```

---

# 12. Grid Search Recursion Tree

The actual tree can be large.

Conceptually:

```text
rec(level=0)
│
├── A-row candidate 1
│   │
│   └── rec(level=1)
│       │
│       ├── B-row candidate 1 → rejected
│       ├── B-row candidate 2
│       │   │
│       │   └── rec(level=2)
│       │       │
│       │       ├── C-row candidate ...
│       │       │
│       │       └── ...
│       │
│       └── ...
│
├── A-row candidate 2
│   └── ...
│
└── ...
```

A successful branch:

```text
[]
│
└── AC..B
    │
    └── .BA.C
        │
        └── C.BA.
            │
            └── BA.C.
                │
                └── ..CBA
                    │
                    └── VALID
```

Each edge means:

```text
choose one whole row
```

This is much more compact than a cell-by-cell search tree.

---

# 13. Complete C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
string R, C;

map<char, int> freq;
string currentRow;

vector<string> allPositions[3];

vector<string> currentSolution;
vector<string> finalSolution;

bool solutionFound = false;
int allDone;

void generateRows(int level) {
    if (level == n) {
        for (char ch : currentRow) {
            if (ch != '.') {
                allPositions[ch - 'A'].push_back(currentRow);
                return;
            }
        }

        return;
    }

    for (auto& entry : freq) {
        char ch = entry.first;
        int& remaining = entry.second;

        if (remaining == 0) {
            continue;
        }

        remaining--;
        currentRow.push_back(ch);

        generateRows(level + 1);

        currentRow.pop_back();
        remaining++;
    }
}

bool checkRow(const string& row, int done) {
    for (int pos = 0; pos < n; pos++) {
        if (row[pos] != '.' &&
            (done & (1 << pos)) == 0 &&
            row[pos] != C[pos]) {
            return false;
        }
    }

    return true;
}

int updateDone(int done, const string& row) {
    for (int pos = 0; pos < n; pos++) {
        if (row[pos] != '.') {
            done |= (1 << pos);
        }
    }

    return done;
}

bool isValid(const vector<string>& solution) {
    for (int col = 0; col < n; col++) {
        set<char> letters;

        for (int row = 0; row < n; row++) {
            if (solution[row][col] != '.') {
                letters.insert(solution[row][col]);
            }
        }

        if (letters.size() != 3) {
            return false;
        }
    }

    return true;
}

void rec(int level, int currentDone) {
    if (solutionFound) {
        return;
    }

    if (level == n) {
        if (currentDone == allDone && isValid(currentSolution)) {
            finalSolution = currentSolution;
            solutionFound = true;
        }

        return;
    }

    int firstChar = R[level] - 'A';

    for (const string& row : allPositions[firstChar]) {
        if (!checkRow(row, currentDone)) {
            continue;
        }

        currentSolution.push_back(row);

        rec(level + 1, updateDone(currentDone, row));

        currentSolution.pop_back();

        if (solutionFound) {
            return;
        }
    }
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> R >> C;

    freq['A'] = 1;
    freq['B'] = 1;
    freq['C'] = 1;
    freq['.'] = n - 3;

    generateRows(0);

    allDone = (1 << n) - 1;

    rec(0, 0);

    if (!solutionFound) {
        cout << "No\n";
        return 0;
    }

    cout << "Yes\n";

    for (const string& row : finalSolution) {
        cout << row << '\n';
    }

    return 0;
}
```

---

# 14. Why the Algorithm Is Correct

## 14.1 Every generated row is valid

The generator starts with exactly:

```text
1 A
1 B
1 C
N-3 dots
```

Every recursive choice consumes one remaining occurrence.

Therefore every completed generated row has exactly the required multiset.

Because equal dots are represented by one frequency entry:

```text
duplicate rows are not generated
```

---

## 14.2 Every chosen row satisfies R

Rows are grouped by first nonempty character.

At grid level `i`, we only choose from:

```text
allPositions[R[i] - 'A']
```

Therefore the first nonempty character of row `i` is exactly:

```text
R[i]
```

---

## 14.3 Every column's topmost character satisfies C

Rows are processed top-to-bottom.

The first time a letter is inserted into column `j`, its done bit is still 0.

The check requires:

```text
row[j] == C[j]
```

After that:

```text
done bit becomes 1
```

All future letters are below this one.

Therefore the topmost character can never change.

---

## 14.4 Every saved grid has valid columns

At the base case, every column contains all three distinct letters.

The whole grid contains exactly:

```text
3N
```

letters because every row contains exactly three.

Since each of `N` columns contains at least three letters, the total is at least `3N`.

The totals are equal, so every column has exactly three letters.

Thus every column contains exactly:

```text
one A
one B
one C
```

---

## 14.5 Every valid grid is reachable

Take any valid answer.

Every one of its rows is generated in phase 1.

Each row belongs to the bucket matching its required `R[i]`.

During phase 2, when that valid row first starts any column, its topmost letter equals `C[j]`.

Therefore the valid row is never incorrectly pruned.

Following all of its rows reaches the valid grid.

So if the search finishes without finding any solution:

```text
No
```

is correct.

---

# 15. Complexity

Let:

```text
P = number of unique row patterns
B = patterns per first-letter bucket
```

We have:

```text
P = N(N-1)(N-2)
B = P / 3
```

For `N = 5`:

```text
P = 60
B = 20
```

---

## Phase 1

Generating and storing every row:

```text
O(N * P)
```

space:

```text
O(N * P)
```

---

## Phase 2

Worst-case complete row combinations:

```text
B^N
```

For `N = 5`:

```text
20^5 = 3,200,000
```

Each row check/update costs:

```text
O(N)
```

Base-case column validation costs:

```text
O(N^2)
```

A conservative bound is:

```text
O(NP + N^2 * B^N)
```

The actual search is usually smaller because:

```text
topmost-letter checks reject many branches early
```

---

## Space

Stored row patterns:

```text
O(NP)
```

Current + final grid:

```text
O(N^2)
```

Recursive depth:

```text
O(N)
```

So overall:

```text
O(NP + N^2)
```

---

# 16. Common Mistakes

## Mistake 1 — Treating R[i] as column 0

Wrong:

```text
R[i] must be grid[i][0]
```

Correct:

```text
R[i] = first NONEMPTY character from the left
```

Leading dots are allowed.

---

## Mistake 2 — Treating C[j] as row 0

Wrong:

```text
C[j] must be grid[0][j]
```

Correct:

```text
C[j] = first NONEMPTY character from the top
```

Leading dots are allowed vertically.

---

## Mistake 3 — Rechecking C[j] after the column has started

Once a column has a topmost character:

```text
future lower letters do not need to match C[j]
```

Only the first insertion matters.

---

## Mistake 4 — Using XOR for done

Wrong:

```cpp
done ^= (1 << pos);
```

Correct:

```cpp
done |= (1 << pos);
```

A column must stay marked started.

---

## Mistake 5 — Thinking allDone means valid

```text
currDone == allDone
```

means only:

```text
every column has at least one non-dot cell
```

It does not mean each column has one A, one B, one C.

Final validation is still required.

---

## Mistake 6 — Forgetting dots are repeated values

If dots are treated as distinct objects during permutation generation, duplicate rows appear.

Use frequency-based generation.

---

## Mistake 7 — Returning before undo

We save the answer separately:

```cpp
finalSolution = currentSolution;
```

Therefore the working vector should still be restored:

```cpp
currentSolution.pop_back();
```

before success propagates upward.

---

## Mistake 8 — Memoizing only `(level, done)`

Two states may have the same:

```text
level
done mask
```

but different letters inside already-started columns.

Those differences affect the final column validation.

So:

```text
(level, done)
```

is not the complete logical state.

---

# 17. Interview/CP Improvements

The supplied solution is already strong for `N <= 5`.

Possible improvements:

## Improvement 1 — Track column letter masks incrementally

Instead of waiting until the base case, maintain:

```text
columnMask[j]
```

where:

```text
A → bit 0
B → bit 1
C → bit 2
```

Then reject a row immediately if it duplicates a letter in a column.

This strengthens pruning.

Conceptually:

```text
current approach:
    topmost condition early
    duplicate-letter condition late

stronger approach:
    both conditions early
```

---

## Improvement 2 — Construction is better than checking

The most important optimization is not bit tricks.

It is:

```text
Generate only structurally valid rows.
```

This changes the search space from:

```text
4^(N*N)
```

to roughly:

```text
B^N
```

for the grid-building phase.

That is the main modelling insight.

---

# 18. Final Recognition Model

When you see a problem with:

```text
small N
grid construction
row/column constraints
first/last visible item constraints
need one valid arrangement
```

ask:

```text
Can I make one dimension valid by construction?
```

Here:

```text
YES
```

Generate all valid rows first.

Then use LCCM.

---

## Phase 1

```text
STATE
    row prefix + remaining frequencies

LEVEL
    next character position

CHOICE
    one distinct available character

CHECK
    remaining frequency > 0

MOVE
    consume → append → recurse → pop → restore

BASE
    bucket row by first nonempty character
```

---

## Phase 2

```text
STATE
    chosen rows + current level + started-column mask

LEVEL
    current row

CHOICE
    one generated row matching R[level]

CHECK
    any newly started column must match C

MOVE
    append row
    recurse with updated mask
    remove row

BASE
    verify each column contains A, B, C
    save one answer
```

---

## Final Mental Picture

```text
               PRECOMPUTE VALID ROWS
                       |
                       v
              group by first letter
                       |
                       v
                 rec(row = 0)
                       |
                       v
                choose row pattern
                       |
                       v
          newly started columns valid?
                /               \
              no                 yes
              |                   |
            skip                PUSH
                                  |
                                  v
                              RECURSE
                                  |
                                  v
                                POP
                                  |
                                  v
                           next row pattern
```

The central lesson is:

```text
Choose the right recursive unit.

Do not recurse over cells if the problem structure lets you recurse over
whole valid rows.
```
