# Kth Move in Tower of Hanoi — Count-and-Skip Recursion with LCCM Thinking

> **Core idea:** The full Tower of Hanoi sequence has `2^N - 1` moves, but to find only the **Kth move**, we do not generate the first `K - 1` moves.
>
> We use the recursive structure to:
>
> **COUNT → LOCATE → SKIP → DESCEND**
>
> This is not classical backtracking because there is no trial-and-error and no undo.  
> However, the same LCCM discipline is useful for understanding the recursive state.

---

# Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Prerequisite 1 — Standard Tower of Hanoi](#2-prerequisite-1--standard-tower-of-hanoi)
3. [Prerequisite 2 — Recursive Function Meaning](#3-prerequisite-2--recursive-function-meaning)
4. [Prerequisite 3 — Count the Number of Moves](#4-prerequisite-3--count-the-number-of-moves)
5. [Prerequisite 4 — Sequence Blocks](#5-prerequisite-4--sequence-blocks)
6. [Prerequisite 5 — Local K After Skipping](#6-prerequisite-5--local-k-after-skipping)
7. [Prerequisite 6 — Rod Roles Change](#7-prerequisite-6--rod-roles-change)
8. [LCCM-Style Mapping](#8-lccm-style-mapping)
9. [Recursive Decision Tree](#9-recursive-decision-tree)
10. [Step-by-Step Dry Run — N = 3, K = 6](#10-step-by-step-dry-run--n--3-k--6)
11. [Dry Run Through Left and Right Blocks](#11-dry-run-through-left-and-right-blocks)
12. [Why We Do Not Simulate the Rods](#12-why-we-do-not-simulate-the-rods)
13. [Clean C++17 Solution](#13-clean-c17-solution)
14. [Correctness Proof](#14-correctness-proof)
15. [Complexity](#15-complexity)
16. [Common Mistakes](#16-common-mistakes)
17. [Reusable Kth-Generation Pattern](#17-reusable-kth-generation-pattern)
18. [Final Mental Model](#18-final-mental-model)

---

# 1. Problem Statement

There are three rods:

```text
1, 2, 3
```

and `N` disks:

```text
disk 1 = smallest
disk N = largest
```

Initially, every disk is on rod `1`.

We want to move the whole tower to rod `3`, using rod `2` as temporary storage.

Rules:

```text
1. Move only one disk at a time.
2. Move only the top disk of a rod.
3. Never place a larger disk on top of a smaller disk.
```

We follow the **standard recursive Tower of Hanoi procedure**.

Given:

```text
N
K
```

find the **Kth move**.

`K` is 1-based.

Example:

```text
N = 3
K = 4
```

Answer:

```text
disk 3 moves from rod 1 to rod 3
```

The important requirement is:

```text
Find ONE indexed move.
Do not generate the complete sequence.
```

---

# 2. Prerequisite 1 — Standard Tower of Hanoi

To move `N` disks from:

```text
source → target
```

using:

```text
auxiliary
```

the standard recursive solution is:

```text
1. Move N-1 disks:
      source → auxiliary

2. Move disk N:
      source → target

3. Move N-1 disks:
      auxiliary → target
```

ASCII view:

```text
                move(N, S, T, A)
                       |
          +------------+------------+
          |                         |
          v                         v
 move(N-1, S, A, T)          move(N-1, A, T, S)
          \                         /
           \                       /
            +---- move disk N ----+
                    S → T
```

Or as three consecutive blocks:

```text
+----------------------+-------------+----------------------+
| LEFT BLOCK           | MIDDLE      | RIGHT BLOCK          |
| move N-1 disks       | move disk N | move N-1 disks       |
| S → A                | S → T       | A → T                |
+----------------------+-------------+----------------------+
```

Classic recursive code:

```cpp
void move(int n, int source, int target, int aux) {
    if (n == 1) {
        cout << source << " -> " << target << '\n';
        return;
    }

    move(n - 1, source, aux, target);

    cout << source << " -> " << target << '\n';

    move(n - 1, aux, target, source);
}
```

This prints **every move**.

For the Kth-move problem, that is too much work.

---

# 3. Prerequisite 2 — Recursive Function Meaning

Before writing recursive code, define exactly what the function returns.

We use:

```text
kthMove(n, source, target, aux, k)
```

Meaning:

```text
Return the Kth move in the standard Tower of Hanoi sequence
that moves disks 1..n from source to target using aux.
```

This recursive contract is extremely important.

Every recursive call must still represent a valid smaller Hanoi problem.

State:

```text
n       = number of active disks
source  = where this smaller tower currently starts
target  = where this smaller tower must go
aux     = spare rod
k       = desired move index INSIDE this subproblem
```

Notice:

```text
k becomes a LOCAL index.
```

This is the heart of the solution.

---

# 4. Prerequisite 3 — Count the Number of Moves

Let:

```text
M(n) = number of moves required for n disks
```

For one disk:

```text
M(1) = 1
```

For `n` disks:

```text
M(n)
=
M(n - 1)       // left recursive block
+ 1            // move disk n
+ M(n - 1)     // right recursive block
```

Therefore:

```text
M(n) = 2 * M(n - 1) + 1
```

---

## 4.1 Derive the closed form

Start:

```text
M(n) = 2M(n-1) + 1
```

Expand once:

```text
M(n)
= 2[2M(n-2) + 1] + 1
= 4M(n-2) + 2 + 1
```

Expand again:

```text
M(n)
= 4[2M(n-3) + 1] + 2 + 1
= 8M(n-3) + 4 + 2 + 1
```

Pattern:

```text
M(n)
= 2^j * M(n-j)
  + (2^(j-1) + ... + 2 + 1)
```

Choose:

```text
j = n - 1
```

Then:

```text
M(n-j) = M(1) = 1
```

So:

```text
M(n)
= 2^(n-1)
  + (2^(n-2) + ... + 2 + 1)
```

The geometric sum is:

```text
2^(n-1) - 1
```

Therefore:

```text
M(n)
= 2^(n-1) + 2^(n-1) - 1
= 2^n - 1
```

Final formula:

```text
M(n) = 2^n - 1
```

Examples:

| N | Moves |
|---:|---:|
| 0 | 0 |
| 1 | 1 |
| 2 | 3 |
| 3 | 7 |
| 4 | 15 |
| 5 | 31 |

This formula allows us to know a recursive block's size **without executing it**.

---

# 5. Prerequisite 4 — Sequence Blocks

For a Hanoi problem with `n` disks:

```text
LEFT  = move n-1 disks
MID   = move disk n
RIGHT = move n-1 disks
```

Each smaller block has:

```text
M(n-1) = 2^(n-1) - 1
```

Let:

```text
L = 2^(n-1) - 1
```

Then:

```text
LEFT   occupies positions 1 ... L
MIDDLE occupies position  L + 1
RIGHT  occupies positions L + 2 ... 2L + 1
```

Define:

```text
mid = L + 1
```

Therefore:

```text
mid = 2^(n-1)
```

This is the most important boundary.

---

## Example: n = 4

Total moves:

```text
2^4 - 1 = 15
```

Smaller block size:

```text
2^3 - 1 = 7
```

Middle position:

```text
mid = 8
```

So:

```text
moves 1..7   → LEFT
move 8       → disk 4
moves 9..15  → RIGHT
```

Diagram:

```text
1                    7 8 9                    15
|--------------------| | |--------------------|
        LEFT          MID        RIGHT

        7 moves        1          7 moves
```

---

# 6. Prerequisite 5 — Local K After Skipping

We compare `K` with:

```text
mid = 2^(n-1)
```

Three cases.

---

## Case 1 — K < mid

The answer lies in the left block.

Nothing occurs before the left block.

Therefore:

```text
newK = K
```

Recursive call:

```cpp
kthMove(n - 1, source, aux, target, k);
```

---

## Case 2 — K == mid

This is exactly the central move.

Answer:

```text
disk n moves source → target
```

Stop immediately.

---

## Case 3 — K > mid

The answer lies in the right block.

Before the right block there are:

```text
left block = mid - 1 moves
middle     = 1 move
```

Total skipped:

```text
mid
```

Therefore:

```text
newK = K - mid
```

Recursive call:

```cpp
kthMove(n - 1, aux, target, source, k - mid);
```

---

## Tiny boundary example

Suppose:

```text
mid = 8
K = 9
```

Right block begins at original move `9`.

Inside the right block it must become:

```text
local K = 1
```

Formula:

```text
9 - 8 = 1
```

Correct.

If we incorrectly subtract only the left block size:

```text
9 - 7 = 2
```

wrong.

So remember:

```text
RIGHT BLOCK:
newK = K - mid
```

not:

```text
K - (mid - 1)
```

---

# 7. Prerequisite 6 — Rod Roles Change

This is one of the easiest parts to get wrong.

Original problem:

```text
move n disks:
source = S
target = T
aux    = A
```

---

## Left block

We move:

```text
n - 1 disks
S → A
```

using `T` as auxiliary.

So:

```text
source = S
target = A
aux    = T
```

Call:

```cpp
kthMove(n - 1, source, aux, target, k);
```

---

## Right block

After moving disk `n`, the smaller tower sits on `A`.

Now move:

```text
n - 1 disks
A → T
```

using `S` as auxiliary.

So:

```text
source = A
target = T
aux    = S
```

Call:

```cpp
kthMove(n - 1, aux, target, source, k - mid);
```

---

## Rod-role diagram

```text
Current:
    S → T
    spare = A

LEFT child:
    S → A
    spare = T

RIGHT child:
    A → T
    spare = S
```

Memorize the **meaning**, not the parameter order.

---

# 8. LCCM-Style Mapping

This is not search/backtracking, but LCCM thinking still helps.

| Part | Meaning here |
|---|---|
| **Level** | Current disk count `n` |
| **Choice** | Which block contains K: left, middle, right |
| **Check** | Compare `k` with `mid = 2^(n-1)` |
| **Move** | Re-map rod roles and possibly convert `k` to a local index |

Important difference from backtracking:

```text
N-Queens:
    many choices may need exploration
    place → recurse → unplace

Kth Hanoi:
    exactly ONE block contains K
    choose block → recurse once
```

So there is:

```text
NO undo
NO sibling exploration
NO shared mutable board
```

---

## LCCM diagram

```text
                  kthMove(n, S, T, A, K)
                           |
                           v
                 mid = 2^(n - 1)
                           |
                +----------+----------+
                |          |          |
                v          v          v
             K < mid    K == mid    K > mid
                |          |          |
                |          |          |
                v          v          v
         LEFT BLOCK      ANSWER     RIGHT BLOCK
         n-1 disks       disk n     n-1 disks
          S → A           S → T      A → T
                |                     |
                v                     v
       same local K              K = K - mid
                |                     |
                +----------+----------+
                           |
                           v
                    recurse once
```

---

# 9. Recursive Decision Tree

For `N = 4`, the conceptual sequence tree is:

```text
                         Hanoi(4)
                  positions 1 ... 15
                         mid = 8
                   /        |        \
                  /         |         \
                 /          |          \
        Hanoi(3)         disk 4        Hanoi(3)
        positions          #8           positions
          1..7                           9..15
          /|\                              /|\
         ...                              ...
```

But the optimized query follows **one path only**.

Example:

```text
K = 5

Hanoi(4), mid=8
    |
    +-- K < 8 → LEFT
           |
       Hanoi(3), local K=5, mid=4
           |
           +-- K > 4 → RIGHT
                  |
              Hanoi(2), local K=1, mid=2
                  |
                  +-- K < 2 → LEFT
                         |
                     Hanoi(1)
```

Instead of exploring a full exponential tree:

```text
follow one root-to-leaf path
```

---

# 10. Step-by-Step Dry Run — N = 3, K = 6

Initial call:

```text
kthMove(3, source=1, target=3, aux=2, k=6)
```

---

## Iteration 1

```text
n = 3

mid = 2^(3-1)
    = 4
```

So:

```text
moves 1..3 → LEFT
move 4     → disk 3
moves 5..7 → RIGHT
```

Our:

```text
K = 6
```

Since:

```text
6 > 4
```

go RIGHT.

Skip:

```text
4 moves
```

New local index:

```text
K = 6 - 4
  = 2
```

Rod roles become:

```text
source = old aux    = 2
target = old target = 3
aux    = old source = 1
```

New call:

```text
kthMove(2, 2, 3, 1, 2)
```

---

## Iteration 2

```text
n = 2

mid = 2^(2-1)
    = 2
```

Current local:

```text
K = 2
```

Therefore:

```text
K == mid
```

So this is the central move of disk `2`.

Answer:

```text
disk 2 moves from rod 2 to rod 3
```

We did not generate moves:

```text
1, 2, 3, 4, 5
```

We located move 6 using only two recursive states.

---

# 11. Dry Run Through Left and Right Blocks

Consider:

```text
N = 4
K = 5
```

Initial rods:

```text
source = 1
target = 3
aux    = 2
```

Compact trace:

```text
Call 1:
n=4, S=1, T=3, A=2, K=5
mid=8
5 < 8
→ LEFT
→ kthMove(3, 1, 2, 3, 5)

Call 2:
n=3, S=1, T=2, A=3, K=5
mid=4
5 > 4
→ RIGHT
→ newK = 5 - 4 = 1
→ kthMove(2, 3, 2, 1, 1)

Call 3:
n=2, S=3, T=2, A=1, K=1
mid=2
1 < 2
→ LEFT
→ kthMove(1, 3, 1, 2, 1)

Call 4:
n=1
→ only move
→ disk 1: 3 → 1
```

Answer:

```text
Moving disk 1 from 3 to 1
```

Two things changed independently during recursion:

```text
1. rod roles
2. local K
```

That is why both must be tracked explicitly in the recursive state.

---

# 12. Why We Do Not Simulate the Rods

A natural question:

```text
If we skip earlier moves, how do we know where the disks are?
```

Because each recursive call already describes a complete legal transfer:

```text
kthMove(n, source, target, aux, k)
```

The rod parameters encode the current recursive situation.

We are not simulating the physical board.

We are asking:

```text
Within this mathematically defined smaller Hanoi sequence,
which move has local index k?
```

The standard recursive procedure guarantees the move structure.

That is enough.

---

# 13. Clean C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Move {
    int disk;
    int from;
    int to;
};

Move kthMove(int n, int source, int target, int aux, unsigned long long k) {
    if (n == 1) {
        return {1, source, target};
    }

    unsigned long long mid = 1ULL << (n - 1);

    if (k < mid) {
        return kthMove(n - 1, source, aux, target, k);
    }

    if (k == mid) {
        return {n, source, target};
    }

    return kthMove(n - 1, aux, target, source, k - mid);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    unsigned long long k;
    cin >> n >> k;

    if (n < 1 || n > 63) {
        cout << "Invalid input\n";
        return 0;
    }

    unsigned long long totalMoves = (1ULL << n) - 1;

    if (k < 1 || k > totalMoves) {
        cout << "Invalid input\n";
        return 0;
    }

    Move answer = kthMove(n, 1, 3, 2, k);

    cout << "Moving disk " << answer.disk
         << " from " << answer.from
         << " to " << answer.to << '\n';

    return 0;
}
```

---

## Why `1ULL`?

This:

```cpp
1ULL << n
```

means the shift is performed using an:

```text
unsigned long long
```

value.

Do not write:

```cpp
2 ^ n
```

because in C++:

```text
^ = XOR
```

not exponentiation.

Also avoid:

```cpp
pow(2, n)
```

for exact integer boundaries when a bit shift directly expresses the value.

---

# 14. Correctness Proof

We prove by induction on `n`.

---

## Base case

For:

```text
n = 1
```

there is exactly one legal move:

```text
disk 1: source → target
```

The only valid:

```text
k = 1
```

So the function returns the correct move.

---

## Induction hypothesis

Assume:

```text
kthMove(n - 1, ...)
```

correctly returns the requested move for every valid smaller Hanoi problem.

---

## Inductive step

For `n` disks, the complete move sequence is:

```text
LEFT BLOCK
MIDDLE MOVE
RIGHT BLOCK
```

with:

```text
mid = 2^(n-1)
```

---

### Case 1 — k < mid

The requested move is inside the left block.

The left block is exactly:

```text
move n-1 disks:
source → aux
using target
```

Its local index is still:

```text
k
```

So:

```cpp
kthMove(n - 1, source, aux, target, k)
```

is correct by the induction hypothesis.

---

### Case 2 — k == mid

The requested move is exactly the central move:

```text
disk n:
source → target
```

So returning:

```cpp
{n, source, target}
```

is correct.

---

### Case 3 — k > mid

The requested move lies inside the right block.

Exactly:

```text
mid
```

moves occur before it.

So its local index is:

```text
k - mid
```

The right block is exactly:

```text
move n-1 disks:
aux → target
using source
```

Thus:

```cpp
kthMove(n - 1, aux, target, source, k - mid)
```

is correct by the induction hypothesis.

---

The three cases:

```text
k < mid
k = mid
k > mid
```

are disjoint and cover every valid `k`.

Therefore the algorithm is correct.

---

# 15. Complexity

## Full generation

Classic Hanoi generates:

```text
2^N - 1
```

moves.

Therefore:

```text
Time = Θ(2^N)
```

If stored:

```text
Space = Θ(2^N)
```

If streamed:

```text
Recursive stack = O(N)
```

---

## Kth move query

At every recursive state:

```text
we choose at most one child
```

So:

```text
Q(n) = Q(n - 1) + O(1)
```

Therefore:

```text
Time = O(N)
```

Maximum recursion depth:

```text
O(N)
```

So:

```text
Auxiliary space = O(N)
```

---

## Important contrast

Full recursion:

```text
        node
       /    \
    child  child
```

Time recurrence:

```text
T(n) = 2T(n-1) + O(1)
```

Kth query:

```text
        node
          \
        ONE child
```

Time recurrence:

```text
Q(n) = Q(n-1) + O(1)
```

This difference is the entire optimization.

---

# 16. Common Mistakes

## Mistake 1 — Using 0-based K accidentally

Here:

```text
K is 1-based
```

Middle move:

```text
mid = 2^(n-1)
```

Do not mix with a 0-based formulation.

---

## Mistake 2 — Right-block index = K - leftSize

Wrong:

```text
K - (mid - 1)
```

Correct:

```text
K - mid
```

because the middle move is also skipped.

---

## Mistake 3 — Not changing rod roles

Wrong:

```cpp
kthMove(n - 1, source, target, aux, ...);
```

for both branches.

Correct:

```text
LEFT:
source → aux
spare = target

RIGHT:
aux → target
spare = source
```

---

## Mistake 4 — Exploring both recursive branches

If you recurse into both children, you return to exponential generation.

The whole point is:

```text
K belongs to exactly one block.
```

Follow only that block.

---

## Mistake 5 — Recomputing M(n) recursively

Do not use another full recursive function to compute:

```text
M(n-1)
```

We already derived:

```text
M(n-1) = 2^(n-1) - 1
```

Use the formula.

---

## Mistake 6 — Writing `2 ^ n`

In C++:

```cpp
2 ^ n
```

means XOR.

Use:

```cpp
1ULL << n
```

for powers of two within the supported range.

---

## Mistake 7 — Thinking memoization is needed

It is not.

A single query visits:

```text
one state at each smaller n
```

No repeated recursive state is being recomputed.

So this is not a DP problem.

---

## Mistake 8 — Confusing move identity with board state

This problem asks:

```text
What is the Kth move?
```

It does not ask:

```text
What does the whole board look like after K moves?
```

Those are different problems.

---

# 17. Reusable Kth-Generation Pattern

This idea appears far beyond Tower of Hanoi.

Whenever a recursively generated ordering is divided into blocks:

```text
BLOCK 1
BLOCK 2
BLOCK 3
...
```

and you can calculate each block's size without generating it, you can find the Kth object.

General procedure:

```text
1. Fix the generation order.

2. Count the size of every recursive block.

3. Find which block contains K.

4. Skip all previous blocks.

5. Convert global K to local K.

6. Recurse only into that block.
```

Generic pseudocode:

```text
findKth(state, K):

    for each block in generation order:

        blockSize = count(block)

        if K > blockSize:
            K -= blockSize
            continue

        if block itself is one output:
            return answer

        return findKth(blockState, K)
```

Examples where the same thinking may appear:

```text
Kth permutation
Kth binary string
Kth valid bracket sequence
Kth recursively generated string
Kth combinatorial object
Kth move/action in a recursive process
```

---

# 18. Final Mental Model

## Full Tower of Hanoi

```text
move(n)

        move(n-1)
            |
        move disk n
            |
        move(n-1)
```

This generates:

```text
2^n - 1
```

moves.

---

## Kth move version

```text
kthMove(n, K)

mid = 2^(n-1)

        K < mid
           |
           v
      LEFT child

        K = mid
           |
           v
      ANSWER NOW

        K > mid
           |
           v
      RIGHT child
      K -= mid
```

Only one branch survives.

---

## LCCM-style summary

```text
STATE
    n, source, target, aux, local K

LEVEL
    current disk count n

CHOICE
    left block / middle move / right block

CHECK
    compare K with mid = 2^(n-1)

MOVE
    LEFT:
        roles = (source, aux, target)
        K unchanged

    MIDDLE:
        return disk n, source → target

    RIGHT:
        roles = (aux, target, source)
        K = K - mid

UNDO
    none

BASE
    n == 1
```

---

## 60-Second Recognition Rule

When a recursion problem asks:

```text
"Find only the Kth generated thing"
```

do NOT immediately generate everything.

Ask:

```text
Can I count how many outputs each recursive branch produces?
```

If yes:

```text
COUNT → SKIP → DESCEND
```

That transforms Tower of Hanoi here from:

```text
Θ(2^N)
```

generation into:

```text
O(N)
```

query time.
