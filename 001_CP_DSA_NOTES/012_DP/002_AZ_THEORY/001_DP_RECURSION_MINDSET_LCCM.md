# 001 — DP Foundation: Recursion Mindset + Level / Choice / Check / Move

> **Goal:** Build the thinking required before Dynamic Programming.
>
> The main idea from the lecture is:
>
> **Level → Choice → Check → Move**
>
> If you can model a recursive problem using these four questions, converting it to DP later becomes much easier.

---

## Table of Contents

- [1. Why Recursion Comes Before DP](#1-why-recursion-comes-before-dp)
- [2. Prerequisite 1 — What Is Recursion?](#2-prerequisite-1--what-is-recursion)
- [3. Prerequisite 2 — Base Case and Recursive Case](#3-prerequisite-2--base-case-and-recursive-case)
- [4. Factorial: Single-Branch Recursion](#4-factorial-single-branch-recursion)
- [5. Fibonacci: Multi-Branch Recursion](#5-fibonacci-multi-branch-recursion)
- [6. The Main Framework — Level / Choice / Check / Move](#6-the-main-framework--level--choice--check--move)
- [7. Example 1 — Ways to Reach Stair N](#7-example-1--ways-to-reach-stair-n)
- [8. Example 2 — N-Queens](#8-example-2--n-queens)
- [9. Example 3 — Take / Not Take Under Constraints](#9-example-3--take--not-take-under-constraints)
- [10. Backtracking: Place → Explore → Undo](#10-backtracking-place--explore--undo)
- [11. How This Becomes Dynamic Programming](#11-how-this-becomes-dynamic-programming)
- [12. Recursion Recognition Checklist](#12-recursion-recognition-checklist)
- [13. Common Mistakes](#13-common-mistakes)
- [14. Final Mental Model](#14-final-mental-model)

---

# 1. Why Recursion Comes Before DP

Dynamic Programming is not mainly about writing a `dp[]` array.

First, we must understand how to describe the problem recursively.

Typical flow:

```text
Problem
   ↓
Define the current state
   ↓
List all choices
   ↓
Reject invalid choices
   ↓
Move to smaller subproblem
   ↓
Recursion
   ↓
Repeated states?
   ↓ yes
Memoization / DP
```

So the first skill is:

> **Convert the problem into states and transitions.**

The lecture uses a very useful framework:

```text
LEVEL  → Where am I now?
CHOICE → What can I do from here?
CHECK  → Is this choice valid?
MOVE   → What is the next state?
```

---

# 2. Prerequisite 1 — What Is Recursion?

A recursive function solves a problem by calling itself on a **smaller version of the same problem**.

General shape:

```cpp
ReturnType solve(State state) {
    if (baseCase) {
        return baseAnswer;
    }

    // solve smaller states
    return ... solve(smallerState) ...;
}
```

The important idea is not "function calling itself".

The important idea is:

```text
Current problem
     ↓
Smaller problem
     ↓
Even smaller problem
     ↓
Base case
```

Example:

```text
factorial(4)
   ↓
factorial(3)
   ↓
factorial(2)
   ↓
factorial(1)
   ↓
factorial(0)
```

Every recursive call must make progress toward a stopping condition.

---

# 3. Prerequisite 2 — Base Case and Recursive Case

Every correct recursion needs two parts.

## 3.1 Base case

The smallest problem whose answer is already known.

Example:

```text
0! = 1
```

So:

```cpp
if (x == 0) {
    return 1;
}
```

## 3.2 Recursive case

Express the current answer using a smaller state.

```text
x! = x × (x-1)!
```

So:

```cpp
return x * factorial(x - 1);
```

## 3.3 Why base case is mandatory

Without a base case:

```text
f(4)
→ f(3)
→ f(2)
→ f(1)
→ f(0)
→ f(-1)
→ f(-2)
→ ...
```

The recursion never stops.

---

# 4. Factorial: Single-Branch Recursion

## 4.1 Mathematical definition

```text
n! = n × (n-1) × (n-2) × ... × 1
```

Recursive relation:

```text
fact(n) = n × fact(n-1)
```

Base case:

```text
fact(0) = 1
```

## 4.2 C++

```cpp
long long factorial(int x) {
    if (x == 0) {
        return 1;
    }

    return 1LL * x * factorial(x - 1);
}
```

## 4.3 Visual call chain

For `factorial(4)`:

```text
factorial(4)
    |
    | 4 × factorial(3)
    v
factorial(3)
    |
    | 3 × factorial(2)
    v
factorial(2)
    |
    | 2 × factorial(1)
    v
factorial(1)
    |
    | 1 × factorial(0)
    v
factorial(0) = 1
```

Now answers return upward:

```text
factorial(0) = 1
factorial(1) = 1 × 1 = 1
factorial(2) = 2 × 1 = 2
factorial(3) = 3 × 2 = 6
factorial(4) = 4 × 6 = 24
```

## 4.4 What is the state?

Only `x` is needed to describe the remaining problem.

```text
state = x
```

This is a **1-dimensional state**.

---

# 5. Fibonacci: Multi-Branch Recursion

Fibonacci introduces a more important recursion idea because one state creates **multiple choices/subproblems**.

## 5.1 Relation

```text
fib(n) = fib(n-1) + fib(n-2)
```

Base cases:

```text
fib(0) = 1
fib(1) = 1
```

This matches the lecture convention shown in the screenshots.

## 5.2 C++

```cpp
long long fib(int n) {
    if (n == 0 || n == 1) {
        return 1;
    }

    return fib(n - 1) + fib(n - 2);
}
```

## 5.3 Recursion tree

For `fib(4)`:

```text
                    fib(4)
                   /      \
              fib(3)      fib(2)
             /     \      /    \
        fib(2)   fib(1) fib(1) fib(0)
        /   \
   fib(1) fib(0)
```

Notice:

```text
fib(2) is calculated more than once.
fib(1) is calculated many times.
```

This idea becomes extremely important in DP.

> Repeated recursive states = possible DP opportunity.

---

# 6. The Main Framework — Level / Choice / Check / Move

This is the central framework from the lecture.

## 6.1 Level

**Question:** What part of the solution am I currently deciding?

Another way to think:

> What information tells me where I currently am in the search?

Examples:

```text
Stairs        → current stair
N-Queens      → current row
Take/Not Take → current item/problem
```

---

## 6.2 Choice

**Question:** What decisions can I make at the current level?

Examples:

```text
Stairs        → jump 1, 2, or 3 steps
N-Queens      → choose a column
Take/Not Take → take item / skip item
```

---

## 6.3 Check

Not every possible choice is valid.

**Question:** Is this choice allowed from the current partial solution?

Examples:

```text
Stairs
→ do not jump beyond N

N-Queens
→ no previous queen may attack this square

Task selection
→ total time ≤ X
→ number selected ≤ K
```

---

## 6.4 Move

After choosing a valid option:

**Question:** What state do I recursively solve next?

Examples:

```text
Stairs
current → current + step

N-Queens
row → row + 1

Take/Not Take
item → item + 1
```

---

## 6.5 Complete mental picture

```text
                    CURRENT STATE
                         |
                     [ LEVEL ]
                         |
              +----------+----------+
              |          |          |
          Choice 1   Choice 2   Choice 3
              |          |          |
            CHECK      CHECK      CHECK
              |          |          |
            valid?     valid?     valid?
              |          |          |
             MOVE       MOVE       MOVE
              |          |          |
          next state  next state  next state
```

Every recursive problem in this note will follow this same model.

---

# 7. Example 1 — Ways to Reach Stair N

## 7.1 Problem

You start at stair `0`.

You want to reach stair `N`.

At every position you may move:

```text
1 step
2 steps
3 steps
```

Find the number of different ways to reach exactly `N`.

---

## 7.2 Story → recursive model

Suppose we are currently at stair `level`.

### Level

```text
level = current stair
```

### Choice

```text
step = 1, 2, or 3
```

### Check

The next stair must not go beyond `N`.

```text
level + step <= N
```

### Move

```text
level → level + step
```

---

## 7.3 Visual state graph

For `N = 4`:

```text
                            (0)
                    /        |        \
                  +1        +2        +3
                  /          |          \
                (1)         (2)         (3)
              /  |  \      /   \         |
            +1  +2  +3   +1    +2       +1
            /    |    \   /      \        \
          (2)   (3)   (4)(3)     (4)      (4)
          / \     |       |
        +1 +2    +1      +1
        /   \     |       |
      (3)  (4)   (4)     (4)
       |
      +1
       |
      (4)
```

Every path from `0` to `4` is one valid way.

---

## 7.4 Base case

When we reach exactly `N`, we found one complete valid way.

```cpp
if (level == n) {
    return 1;
}
```

## 7.5 Pruning

If we somehow go beyond `N`, that branch is invalid.

```cpp
if (level > n) {
    return 0;
}
```

With the explicit check `level + step <= n`, this pruning is not strictly necessary, but it is useful for understanding recursion.

---

## 7.6 Step-by-step recurrence

Let:

```text
ways(level) = number of ways to reach N starting from level
```

At `level`, try all three possible steps:

```text
ways(level)
= ways(level + 1)
+ ways(level + 2)
+ ways(level + 3)
```

Only include a move if it remains valid.

---

## 7.7 Compact dry run — N = 4

```text
ways(4) = 1

ways(3):
step 1 → ways(4)=1
answer = 1

ways(2):
step 1 → ways(3)=1
step 2 → ways(4)=1
answer = 2

ways(1):
step 1 → ways(2)=2
step 2 → ways(3)=1
step 3 → ways(4)=1
answer = 4

ways(0):
step 1 → ways(1)=4
step 2 → ways(2)=2
step 3 → ways(3)=1
answer = 7
```

So:

```text
Number of ways = 7
```

---

## 7.8 C++ — lecture-style recursion

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;

long long rec(int level) {
    if (level > n) {
        return 0;
    }

    if (level == n) {
        return 1;
    }

    long long ans = 0;

    for (int step = 1; step <= 3; step++) {
        if (level + step <= n) {
            long long ways = rec(level + step);
            ans += ways;
        }
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;
    cout << rec(0) << '\n';

    return 0;
}
```

---

## 7.9 Recognition model

If the problem says:

```text
count ways
move forward using a small set of jumps
reach exactly a target
```

Think:

```text
Level  = current position
Choice = allowed jumps
Check  = stay valid
Move   = current + jump
```

---

# 8. Example 2 — N-Queens

## 8.1 Problem

Place `N` queens on an `N × N` chessboard such that no two queens attack each other.

A queen attacks along:

```text
same row
same column
diagonal
```

We place exactly one queen in every row.

Therefore the recursive decision can be made **row by row**.

---

## 8.2 Level / Choice / Check / Move

### Level

```text
level = current row where we must place a queen
```

### Choice

```text
choose one column from 0 ... N-1
```

### Check

The new queen must not attack any queen already placed in previous rows.

We only need to check:

```text
same column
diagonal
```

Why not same row?

Because we deliberately place only one queen per row.

### Move

After placing the queen:

```text
row → row + 1
```

---

## 8.3 Better visualization

Suppose we already placed queens in previous rows:

```text
columns →   0   1   2   3

row 0       .   Q   .   .
row 1       .   .   .   Q
row 2       ?   ?   ?   ?   ← current level
row 3       .   .   .   .
```

For row `2`, each column is a **choice**.

Before placing a queen, check whether any previous queen attacks that position.

---

## 8.4 How to represent placed queens

Use:

```cpp
queen[row] = column;
```

Example:

```text
queen[0] = 1
queen[1] = 3
```

means:

```text
row 0 → queen at column 1
row 1 → queen at column 3
```

---

## 8.5 Column attack condition

Previous queen:

```text
(previousRow, previousCol)
```

Candidate queen:

```text
(row, col)
```

Same column if:

```text
previousCol == col
```

---

## 8.6 Diagonal attack condition

Two cells lie on the same diagonal when:

```text
|row1 - row2| = |col1 - col2|
```

So:

```cpp
abs(col - previousCol) == abs(row - previousRow)
```

---

## 8.7 Check function

```cpp
bool check(int row, int col) {
    for (int previousRow = 0; previousRow < row; previousRow++) {
        int previousCol = queen[previousRow];

        if (previousCol == col) {
            return false;
        }

        if (abs(col - previousCol) == abs(row - previousRow)) {
            return false;
        }
    }

    return true;
}
```

---

## 8.8 Recursion tree idea

```text
                         row 0
                    /   /   \   \
                  c0   c1   c2   c3
                  |     |    |    |
                row1  row1 row1 row1
                /...   ...  ...  ...
```

Each path represents one partial board configuration.

Invalid branches are stopped immediately using `check()`.

---

## 8.9 Base case

When:

```text
level == N
```

all rows already have valid queens.

Therefore:

```cpp
return 1;
```

One valid arrangement has been found.

---

## 8.10 Place → Explore → Undo

This is crucial.

```text
1. Place queen
2. Explore recursively
3. Remove queen
4. Try next column
```

Code pattern:

```cpp
queen[level] = col;
ans += rec(level + 1);
queen[level] = -1;
```

This is called **backtracking**.

---

## 8.11 C++

```cpp
#include <bits/stdc++.h>
using namespace std;

int n;
vector<int> queen;

bool check(int row, int col) {
    for (int previousRow = 0; previousRow < row; previousRow++) {
        int previousCol = queen[previousRow];

        if (previousCol == col) {
            return false;
        }

        if (abs(col - previousCol) == abs(row - previousRow)) {
            return false;
        }
    }

    return true;
}

long long rec(int level) {
    if (level == n) {
        return 1;
    }

    long long ans = 0;

    for (int col = 0; col < n; col++) {
        if (check(level, col)) {
            queen[level] = col;
            ans += rec(level + 1);
            queen[level] = -1;
        }
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;
    queen.assign(n, -1);

    cout << rec(0) << '\n';

    return 0;
}
```

---

## 8.12 Recognition model

When the problem says:

```text
construct a valid configuration
make one decision at a time
future choices depend on previous choices
undo after exploring
```

Think:

```text
Backtracking
```

and model:

```text
Level  = what object/position am I deciding now?
Choice = what values can I assign?
Check  = is current partial configuration valid?
Move   = go to next object/position
```

---

# 9. Example 3 — Take / Not Take Under Constraints

The lecture introduces a problem of the following form.

There are `N` problems/items.

Each item has values like:

```text
(Ti, Si)
```

where conceptually:

```text
Ti = time required
Si = skill/value gained
```

You have constraints such as:

```text
maximum total time = X
maximum number of selected items = K
```

Goal:

```text
maximize total skill/value
```

This is one of the most important DP-recursion patterns.

---

## 9.1 Example data

From the lecture-style example:

```text
items:
(3,3) (5,4) (4,1) (2,3) (1,1)

X = 6
K = 2
```

Interpretation:

```text
(time, skill)
```

We may choose at most `2` items and use at most `6` total time.

---

## 9.2 Level / Choice / Check / Move

### Level

```text
level = current item/problem
```

### Choice

For every item:

```text
Choice 1 → do not take it
Choice 2 → take it
```

This creates the classic binary decision tree:

```text
                           item 0
                         /        \
                     skip          take
                      /              \
                   item 1          item 1
                  /    \           /    \
               skip   take      skip   take
                ...    ...       ...    ...
```

---

## 9.3 What information does the check need?

Before taking item `level`, we need to know:

```text
How much time was already used?
How many items were already taken?
```

The lecture's initial recursive implementation recomputes this information by looking at the previously selected items.

Example conceptual check:

```text
usedTime + time[level] <= X
usedCount + 1 <= K
```

---

## 9.4 Choice 1 — Do not take

No constraint check is required.

Simply move to the next item:

```text
answer = rec(level + 1)
```

---

## 9.5 Choice 2 — Take

Only allowed when constraints remain valid.

```text
if canTake(level):
    mark item as taken
    answer = max(answer, skill[level] + rec(level + 1))
    unmark item
```

---

## 9.6 Why the `skill[level]` is added

Suppose the current item has:

```text
skill = 4
```

If we take it, the final answer from this branch is:

```text
current item's skill
+
best skill obtainable from remaining items
```

Therefore:

```text
skill[level] + rec(level + 1)
```

---

## 9.7 Small dry run

Consider:

```text
items = [(3,3), (2,3), (1,1)]
X = 4
K = 2
```

At item 0 `(3,3)`:

```text
Option A: skip
→ solve items 1..2

Option B: take
→ time = 3
→ count = 1
→ skill = 3
→ solve items 1..2 with remaining capacity
```

If item 0 was taken:

```text
item 1 requires time 2
3 + 2 = 5 > X
→ cannot take

item 2 requires time 1
3 + 1 = 4 <= X
count becomes 2 <= K
→ can take
```

So this branch yields:

```text
3 + 1 = 4 skill
```

The recursion compares this with all other branches and keeps the maximum.

---

## 9.8 Basic lecture-style backtracking implementation

This version intentionally follows the recursive thinking shown in the screenshots before introducing DP-state optimization.

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, xLimit, kLimit;
vector<int> timeNeeded;
vector<int> skill;
vector<int> taken;

bool check(int level) {
    int timeTaken = 0;
    int itemTaken = 0;

    for (int i = 0; i < level; i++) {
        if (taken[i]) {
            timeTaken += timeNeeded[i];
            itemTaken++;
        }
    }

    timeTaken += timeNeeded[level];
    itemTaken++;

    return timeTaken <= xLimit && itemTaken <= kLimit;
}

int rec(int level) {
    if (level == n) {
        return 0;
    }

    int ans = rec(level + 1);

    if (check(level)) {
        taken[level] = 1;
        ans = max(ans, skill[level] + rec(level + 1));
        taken[level] = 0;
    }

    return ans;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n >> xLimit >> kLimit;

    timeNeeded.resize(n);
    skill.resize(n);
    taken.assign(n, 0);

    for (int i = 0; i < n; i++) {
        cin >> timeNeeded[i] >> skill[i];
    }

    cout << rec(0) << '\n';

    return 0;
}
```

---

## 9.9 Important observation for later DP

The previous version repeatedly scans all previously selected items to calculate:

```text
time already used
number of items already taken
```

But those values can be carried directly as part of the recursive state.

Instead of only:

```text
rec(level)
```

we can eventually think in terms of:

```text
rec(level, timeUsed, countTaken)
```

This is exactly the kind of transition from **plain recursion → state-based DP** that we will learn next.

---

# 10. Backtracking: Place → Explore → Undo

Backtracking is recursion where we temporarily modify the current solution.

General pattern:

```cpp
for (Choice choice : choices) {
    if (valid(choice)) {
        apply(choice);
        answer += rec(nextState);
        undo(choice);
    }
}
```

Visual:

```text
current state
     |
   choose
     |
   APPLY
     |
   recurse
     |
    UNDO
     |
try next choice
```

Why undo?

Because the next branch must start from the original state.

Example N-Queens:

```text
Place queen at column 2
        ↓
Explore all solutions below it
        ↓
Remove queen from column 2
        ↓
Try column 3
```

Without undoing, information from one branch leaks into another branch.

---

# 11. How This Becomes Dynamic Programming

The recursion mindset comes first.

DP starts when we observe that the recursive search is repeatedly solving the **same state**.

Fibonacci is the simplest example:

```text
                    fib(5)
                  /        \
              fib(4)      fib(3)
             /     \      /    \
         fib(3)   fib(2) fib(2) fib(1)
```

`fib(3)` and `fib(2)` appear repeatedly.

Instead of recomputing:

```text
solve state
solve same state again
solve same state again
```

store the answer:

```text
state → answer
```

That is memoization.

---

## 11.1 Recursion first

```cpp
long long fib(int n) {
    if (n <= 1) {
        return 1;
    }

    return fib(n - 1) + fib(n - 2);
}
```

## 11.2 Later: memoization

Conceptually:

```text
Before computing fib(n):

Have I already solved n?
    YES → return stored answer
    NO  → calculate and store it
```

The important lesson:

> Do not start by guessing a DP table. First understand the recursive state and transition.

---

# 12. Recursion Recognition Checklist

When reading a problem, ask these questions in this exact order.

## Step 1 — What is the Level?

```text
What decision am I currently making?
```

Typical answers:

```text
index
row
position
current item
current day
current node
remaining amount
```

## Step 2 — What are the Choices?

```text
What can I do from this state?
```

Typical choices:

```text
take / skip
left / right
1 / 2 / 3 jumps
choose one column
choose one partition
buy / don't buy
```

## Step 3 — What must I Check?

```text
Which choices are legal?
```

Examples:

```text
inside bounds
capacity not exceeded
no collision
budget not exceeded
constraint still satisfied
```

## Step 4 — How do I Move?

```text
How does the state change after the choice?
```

Examples:

```text
index + 1
position + jump
row + 1
remainingCapacity - value
```

## Step 5 — What is the Base Case?

```text
When is the solution complete?
```

Examples:

```text
index == n
row == n
position == target
remaining == 0
```

## Step 6 — What should recursion return?

This depends on the problem.

```text
Counting problem     → return number of ways
Maximum problem      → return maximum value
Minimum problem      → return minimum value
Existence problem    → return true/false
Construction problem → build/store solution
```

---

# 13. Common Mistakes

## Mistake 1 — Writing recursion before defining the state

Bad approach:

```text
I know recursion, so I will start coding rec(...)
```

Better:

```text
1. Define Level
2. Define Choices
3. Define Check
4. Define Move
5. Define Base Case
6. Then code
```

---

## Mistake 2 — Confusing Level with Choice

For N-Queens:

```text
Level  = row
Choice = column
```

Not:

```text
Level = row and column
```

unless the problem really requires both dimensions as independent state information.

---

## Mistake 3 — Forgetting undo in backtracking

Wrong:

```cpp
queen[level] = col;
ans += rec(level + 1);
```

Correct:

```cpp
queen[level] = col;
ans += rec(level + 1);
queen[level] = -1;
```

---

## Mistake 4 — Missing invalid-state pruning

Stairs example:

```text
level > N
```

must not count as a valid solution.

---

## Mistake 5 — Adding current value in the wrong branch

For take/not-take:

```text
skip → rec(next)

take → value[current] + rec(next)
```

Only the **take branch** receives the current value.

---

## Mistake 6 — Trying DP before understanding recursion

If you cannot clearly explain:

```text
What does rec(state) mean?
```

then defining:

```text
dp[state]
```

will also be confusing.

The recursive meaning should come first.

---

# 14. Final Mental Model

The most important takeaway from this lecture is not factorial, Fibonacci, stairs, or N-Queens individually.

It is this framework:

```text
                  RECURSIVE PROBLEM
                         |
                         v
                +----------------+
                |     LEVEL      |
                | Where am I?    |
                +----------------+
                         |
                         v
                +----------------+
                |    CHOICES     |
                | What can I do? |
                +----------------+
                         |
                         v
                +----------------+
                |     CHECK      |
                | Is it valid?   |
                +----------------+
                         |
                         v
                +----------------+
                |      MOVE      |
                | Next state?    |
                +----------------+
                         |
                         v
                     RECURSE
```

Then determine what the recursion returns:

```text
Count ways?     → sum branches
Maximum value?  → max(branches)
Minimum value?  → min(branches)
Possible?       → OR branches
```

Finally ask:

```text
Are the same states being solved repeatedly?
```

If yes:

```text
Recursion
   ↓
Memoization
   ↓
Dynamic Programming
```

---

# One-Line Revision

```text
DP starts with recursion modelling:
Level = current decision,
Choice = available actions,
Check = validity,
Move = next state,
and repeated states later become memoization/DP.
```
