# Prefix Sums — 1D, 2D, Range Queries & 2D XOR
### Visual Self-Learning Notes — Step by Step with ASCII Diagrams

> Based on the uploaded TLE Level 2 **Prefix Sums** lecture.
>
> Goal of these notes:
>
> - understand **why** prefix sums work,
> - derive formulas instead of memorizing them,
> - answer 1D range-sum queries in `O(1)`,
> - answer 2D rectangle-sum queries in `O(1)`,
> - understand the 2D XOR variant,
> - recognize when prefix sums should be used in competitive programming.

---

# Table of Contents

1. [Prerequisites — Learn These First](#1-prerequisites--learn-these-first)
2. [What Problem Does Prefix Sum Solve?](#2-what-problem-does-prefix-sum-solve)
3. [1D Prefix Sum — Core Idea](#3-1d-prefix-sum--core-idea)
4. [Building a 1D Prefix Array](#4-building-a-1d-prefix-array)
5. [Why Prefix Sum Makes Queries Fast](#5-why-prefix-sum-makes-queries-fast)
6. [Deriving the Range Sum Formula](#6-deriving-the-range-sum-formula)
7. [1D Prefix Sum — Full Dry Run](#7-1d-prefix-sum--full-dry-run)
8. [1D Prefix Sum — C++17 Template](#8-1d-prefix-sum--c17-template)
9. [Time Complexity — 1D Prefix Sum](#9-time-complexity--1d-prefix-sum)
10. [2D Prefix Sum — Why We Need It](#10-2d-prefix-sum--why-we-need-it)
11. [Meaning of `prefix[i][j]`](#11-meaning-of-prefixij)
12. [Building a 2D Prefix Sum](#12-building-a-2d-prefix-sum)
13. [Deriving the 2D Prefix Formula](#13-deriving-the-2d-prefix-formula)
14. [2D Rectangle Query — Inclusion / Exclusion](#14-2d-rectangle-query--inclusion--exclusion)
15. [2D Rectangle Query — Full Dry Run](#15-2d-rectangle-query--full-dry-run)
16. [2D Prefix Sum — C++17 Template](#16-2d-prefix-sum--c17-template)
17. [Forest Queries Pattern](#17-forest-queries-pattern)
18. [Bonus — 2D Prefix XOR](#18-bonus--2d-prefix-xor)
19. [Common Mistakes](#19-common-mistakes)
20. [Pattern Recognition — When to Use Prefix Sum](#20-pattern-recognition--when-to-use-prefix-sum)
21. [Complexity Cheat Sheet](#21-complexity-cheat-sheet)
22. [Practice Checklist](#22-practice-checklist)
23. [Final Mental Model](#23-final-mental-model)

---

# 1. Prerequisites — Learn These First

Before learning prefix sums, be comfortable with the following ideas.

## 1.1 Arrays

Example:

```text
index:  0   1   2   3   4
arr:    4   6   9  12  11
```

`arr[i]` means:

```text
value stored at index i
```

For example:

```text
arr[0] = 4
arr[2] = 9
arr[4] = 11
```

---

## 1.2 Subarray

A **subarray** is a continuous part of an array.

Example:

```text
arr = [4, 6, 9, 12, 11]

subarray [1..3]:

index:  0  | 1   2   3 | 4
arr:    4  | 6   9  12 | 11
             <------->
```

The sum is:

```text
6 + 9 + 12 = 27
```

---

## 1.3 Range Query

A query may ask:

```text
Find the sum from index L to index R.
```

Example:

```text
L = 1
R = 3

arr = [4, 6, 9, 12, 11]

answer = 6 + 9 + 12
       = 27
```

---

## 1.4 Time Complexity

Suppose there are:

```text
N = number of array elements
Q = number of queries
```

If every query loops from `L` to `R`, one query can take:

```text
O(N)
```

So `Q` queries can take:

```text
O(N * Q)
```

If:

```text
N = 100000
Q = 100000
```

then worst-case work is roughly:

```text
10^5 * 10^5 = 10^10
```

That is too slow.

We want:

```text
preprocess once
      +
answer each query very quickly
```

This is exactly what prefix sums do.

---

## 1.5 2D Grid Basics

A grid uses row and column coordinates.

```text
            columns
          0   1   2
        +---+---+---+
row 0   | 2 | 6 | 5 |
        +---+---+---+
row 1   | 3 | 4 | 1 |
        +---+---+---+
row 2   | 2 | 7 | 3 |
        +---+---+---+
```

`grid[i][j]` means:

```text
row i
column j
```

Example:

```text
grid[1][2] = 1
```

---

## 1.6 Inclusion-Exclusion Intuition

A very important idea in 2D prefix sums is:

```text
add big region
- remove unwanted top region
- remove unwanted left region
+ add overlap back
```

Why add the overlap back?

Because it was removed twice.

Visual:

```text
+----------------------+
|          TOP         |
|                      |
|      +---------------+
| LEFT |   WANTED      |
|      |   RECTANGLE   |
|      |               |
+------+---------------+
```

When TOP and LEFT are both subtracted, their common overlap gets subtracted twice.

So:

```text
+ overlap once
```

This is the core of 2D rectangle queries.

---

# 2. What Problem Does Prefix Sum Solve?

Suppose:

```text
arr = [2, 9, 6, 3, 7, 2, 1]
```

And many queries ask:

```text
sum(2, 4)
sum(1, 3)
sum(3, 6)
...
```

The direct method is:

```text
for every query:
    loop from L to R
    add all elements
```

That repeats a lot of work.

Example:

```text
sum(2, 4) = 6 + 3 + 7

sum(1, 4) = 9 + 6 + 3 + 7
```

Notice that many prefixes of the array are being recomputed again and again.

Prefix sums say:

```text
"Compute cumulative sums once,
then reuse them."
```

---

# 3. 1D Prefix Sum — Core Idea

The lecture defines:

```text
prefix[i] = sum of all elements from index 0 to i
```

Suppose:

```text
arr = [1, 2, 9, -1, -2, 3]
```

Then:

```text
prefix[0] = 1
prefix[1] = 1 + 2
prefix[2] = 1 + 2 + 9
prefix[3] = 1 + 2 + 9 - 1
prefix[4] = 1 + 2 + 9 - 1 - 2
prefix[5] = 1 + 2 + 9 - 1 - 2 + 3
```

So:

```text
arr:     [1, 2, 9, -1, -2, 3]
prefix:  [1, 3,12, 11,  9,12]
```

Visual:

```text
arr
index:    0    1    2    3    4    5
        +----+----+----+----+----+----+
        |  1 |  2 |  9 | -1 | -2 |  3 |
        +----+----+----+----+----+----+
          |    |    |    |    |    |
          v    v    v    v    v    v

prefix
        +----+----+----+----+----+----+
        |  1 |  3 | 12 | 11 |  9 | 12 |
        +----+----+----+----+----+----+
```

---

# 4. Building a 1D Prefix Array

## 4.1 Slow way

For every index `i`, calculate:

```text
arr[0] + arr[1] + ... + arr[i]
```

Example:

```text
prefix[0] = arr[0]

prefix[1] = arr[0] + arr[1]

prefix[2] = arr[0] + arr[1] + arr[2]

prefix[3] = arr[0] + arr[1] + arr[2] + arr[3]
```

This repeats earlier work.

Worst case:

```text
O(N^2)
```

---

## 4.2 Observation

Look at:

```text
prefix[i - 1]
```

It already contains:

```text
arr[0] + arr[1] + ... + arr[i - 1]
```

So to obtain `prefix[i]`, we only need to add:

```text
arr[i]
```

Therefore:

```text
prefix[i] = prefix[i - 1] + arr[i]
```

Base case:

```text
prefix[0] = arr[0]
```

ASCII derivation:

```text
prefix[i - 1]
=
arr[0] + arr[1] + ... + arr[i - 1]

                    add arr[i]
                         |
                         v

prefix[i]
=
arr[0] + arr[1] + ... + arr[i - 1] + arr[i]
```

So building the entire prefix array takes:

```text
O(N)
```

---

# 5. Why Prefix Sum Makes Queries Fast

Suppose we want:

```text
sum(L, R)
```

Example:

```text
arr = [2, 9, 6, 3, 7, 2, 1]

L = 2
R = 4
```

Wanted:

```text
6 + 3 + 7 = 16
```

The prefix array is:

```text
index:    0   1   2   3   4   5   6
arr:      2   9   6   3   7   2   1
prefix:   2  11  17  20  27  29  30
```

Now look at:

```text
prefix[4] = 2 + 9 + 6 + 3 + 7
```

But we only want:

```text
6 + 3 + 7
```

The unwanted part is:

```text
2 + 9
```

That unwanted part is exactly:

```text
prefix[1]
```

Therefore:

```text
prefix[4] - prefix[1]
=
27 - 11
=
16
```

---

# 6. Deriving the Range Sum Formula

We want:

```text
sum(L, R)
```

Draw the array as two pieces:

```text
0 ........ L-1 | L ........ R
---------------+-------------
   unwanted    |   wanted
```

`prefix[R]` contains both pieces:

```text
prefix[R]
=
sum(0 ... L-1)
+
sum(L ... R)
```

`prefix[L - 1]` contains only:

```text
sum(0 ... L-1)
```

Subtract:

```text
prefix[R] - prefix[L - 1]
```

The unwanted part cancels:

```text
[unwanted + wanted] - [unwanted]
=
wanted
```

Therefore:

```text
sum(L, R) = prefix[R] - prefix[L - 1]
```

---

## 6.1 Special case: `L = 0`

If:

```text
L = 0
```

then there is nothing before `L`.

So:

```text
sum(0, R) = prefix[R]
```

We must not access:

```text
prefix[-1]
```

---

# 7. 1D Prefix Sum — Full Dry Run

Take:

```text
arr = [2, 9, 6, 3, 7, 2, 1]
```

## Step 1 — Build prefix

```text
prefix[0] = 2

prefix[1] = 2 + 9
          = 11

prefix[2] = 11 + 6
          = 17

prefix[3] = 17 + 3
          = 20

prefix[4] = 20 + 7
          = 27

prefix[5] = 27 + 2
          = 29

prefix[6] = 29 + 1
          = 30
```

Final:

```text
index:    0   1   2   3   4   5   6
arr:      2   9   6   3   7   2   1
prefix:   2  11  17  20  27  29  30
```

---

## Step 2 — Query `[2, 4]`

Wanted:

```text
arr[2] + arr[3] + arr[4]

= 6 + 3 + 7

= 16
```

Using prefix:

```text
sum(2,4)
=
prefix[4] - prefix[1]

=
27 - 11

=
16
```

---

## Step 3 — Query `[0, 3]`

Wanted:

```text
2 + 9 + 6 + 3
=
20
```

Since `L = 0`:

```text
sum(0,3)
=
prefix[3]
=
20
```

---

# 8. 1D Prefix Sum — C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<long long> arr(n), prefix(n);

    for (int i = 0; i < n; i++) {
        cin >> arr[i];
    }

    prefix[0] = arr[0];

    for (int i = 1; i < n; i++) {
        prefix[i] = prefix[i - 1] + arr[i];
    }

    int q;
    cin >> q;

    while (q--) {
        int l, r;
        cin >> l >> r;

        long long answer = prefix[r];

        if (l > 0) {
            answer -= prefix[l - 1];
        }

        cout << answer << '\n';
    }

    return 0;
}
```

---

## Cleaner 1-based version

For competitive programming, many people prefer an extra zero prefix:

```text
prefix[0] = 0
prefix[i] = sum of first i elements
```

Example:

```text
arr index:        1   2   3   4
arr:              5   8   2   7

prefix index:  0  1   2   3   4
prefix:        0  5  13  15  22
```

Then:

```text
sum(L,R) = prefix[R] - prefix[L - 1]
```

works without a special `L = 0` case.

C++:

```cpp
vector<long long> prefix(n + 1, 0);

for (int i = 1; i <= n; i++) {
    prefix[i] = prefix[i - 1] + arr[i];
}

long long answer = prefix[r] - prefix[l - 1];
```

---

# 9. Time Complexity — 1D Prefix Sum

Without prefix sums:

```text
one query      O(N)
Q queries      O(N * Q)
```

With prefix sums:

```text
build prefix   O(N)
one query      O(1)
Q queries      O(Q)

total          O(N + Q)
```

This matches the lecture's goal:

```text
preprocess once
answer each query in O(1)
```

---

# 10. 2D Prefix Sum — Why We Need It

Now consider a grid:

```text
2   6   5   9  10
3   4   1   6   1
2   7   3   2   2
1   3   5   4   3
6   7   9  11   4
```

Suppose many queries ask:

```text
Find the sum inside a rectangle.
```

Example rectangle:

```text
      c1       c2
       |        |
       v        v

    +----+----+----+----+----+
    |  2 |  6 |  5 |  9 | 10 |
    +----+----+----+----+----+
r1->|  3 | [4 |  1 |  6] | 1 |
    +----+----+----+----+----+
    |  2 | [7 |  3 |  2] | 2 |
    +----+----+----+----+----+
r2->|  1 | [3 |  5 |  4] | 3 |
    +----+----+----+----+----+
    |  6 |  7 |  9 | 11 |  4 |
    +----+----+----+----+----+
```

Directly looping inside every rectangle can be expensive.

2D prefix sums let us preprocess the grid and answer each rectangle query in:

```text
O(1)
```

---

# 11. Meaning of `prefix[i][j]`

The lecture defines:

```text
prefix[i][j]
=
sum of every grid[x][y]
such that:

x <= i
and
y <= j
```

In simpler language:

```text
prefix[i][j]
=
sum of the rectangle from (0,0) to (i,j)
```

ASCII:

```text
(0,0)
  |
  v
+-------------------+
| x x x x x         |
| x x x x x         |
| x x x x x         |
| x x x X           | <- (i,j)
+-------------------+

everything marked x/X
belongs to prefix[i][j]
```

---

# 12. Building a 2D Prefix Sum

For every cell `(i,j)`, we want the sum of:

```text
rectangle (0,0) -> (i,j)
```

Consider:

```text
prefix[i - 1][j]
```

This contains everything above `(i,j)`.

And:

```text
prefix[i][j - 1]
```

This contains everything left of `(i,j)`.

So we might try:

```text
prefix[i - 1][j]
+
prefix[i][j - 1]
+
grid[i][j]
```

But there is a problem.

The top-left overlap is counted twice.

That overlap is:

```text
prefix[i - 1][j - 1]
```

So subtract it once.

Therefore:

```text
prefix[i][j]
=
grid[i][j]
+
prefix[i - 1][j]
+
prefix[i][j - 1]
-
prefix[i - 1][j - 1]
```

---

# 13. Deriving the 2D Prefix Formula

Visualize four parts:

```text
+-----------------------+
|          A            |
|                       |
|          +------------+
|          |            |
|    B     |     X      |
|          |   (i,j)    |
+----------+------------+
```

Where:

```text
A = prefix[i - 1][j]
B = prefix[i][j - 1]
```

But:

```text
A and B overlap
```

Their overlap is:

```text
prefix[i - 1][j - 1]
```

So:

```text
A + B
```

counts overlap twice.

Correct it:

```text
A + B - overlap
```

Then add the current cell:

```text
grid[i][j]
```

Final formula:

```text
prefix[i][j]
=
grid[i][j]
+
prefix[i - 1][j]
+
prefix[i][j - 1]
-
prefix[i - 1][j - 1]
```

---

## 13.1 Safer 1-based indexing

The cleanest implementation uses:

```text
prefix[0][*] = 0
prefix[*][0] = 0
```

Then grid cell `(i,j)` is stored as 1-based.

Formula:

```text
prefix[i][j]
=
grid[i][j]
+
prefix[i - 1][j]
+
prefix[i][j - 1]
-
prefix[i - 1][j - 1]
```

No boundary `if` statements are required inside the formula.

---

# 14. 2D Rectangle Query — Inclusion / Exclusion

Suppose we want the rectangle:

```text
(r1,c1) -> (r2,c2)
```

Start with:

```text
prefix[r2][c2]
```

That includes everything from:

```text
(0,0) -> (r2,c2)
```

But it contains extra cells.

We must remove:

```text
everything above r1
everything left of c1
```

So:

```text
answer
=
prefix[r2][c2]
-
prefix[r1 - 1][c2]
-
prefix[r2][c1 - 1]
```

But the top-left corner was removed twice.

So add it back:

```text
+ prefix[r1 - 1][c1 - 1]
```

Final formula:

```text
answer
=
prefix[r2][c2]
-
prefix[r1 - 1][c2]
-
prefix[r2][c1 - 1]
+
prefix[r1 - 1][c1 - 1]
```

---

## 14.1 Visual cancellation

```text
Entire prefix[r2][c2]

+----------------------------+
| AAAAAAAAAAAAAAAAAAAAAAAA   |
| AAAAAAAAAAAAAAAAAAAAAAAA   |
| BBBB +------------------+  |
| BBBB |                  |  |
| BBBB |      WANTED      |  |
| BBBB |                  |  |
| BBBB +------------------+  |
+----------------------------+
```

Remove top:

```text
- prefix[r1 - 1][c2]
```

Remove left:

```text
- prefix[r2][c1 - 1]
```

But corner overlap was removed twice:

```text
+ prefix[r1 - 1][c1 - 1]
```

Mental formula:

```text
BIG
- TOP
- LEFT
+ OVERLAP
```

---

# 15. 2D Rectangle Query — Full Dry Run

Take this grid:

```text
1   2   0   1
3   3   0   2
0   3   1   0
3   0   2   1
```

Use 1-based prefix indexing by adding a zero row and column.

Suppose we want rectangle:

```text
rows    2..4
columns 2..4
```

Wanted cells:

```text
3   0   2
3   1   0
0   2   1
```

Direct sum:

```text
3 + 0 + 2
+ 3 + 1 + 0
+ 0 + 2 + 1

= 12
```

Using the rectangle formula:

```text
answer
=
prefix[4][4]
-
prefix[1][4]
-
prefix[4][1]
+
prefix[1][1]
```

The exact numbers depend on the computed prefix table, but the cancellation pattern is always:

```text
full rectangle to bottom-right
- area above target
- area left of target
+ top-left overlap
```

---

# 16. 2D Prefix Sum — C++17 Template

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    cin >> n >> m;

    vector<vector<long long>> grid(n + 1, vector<long long>(m + 1, 0));
    vector<vector<long long>> prefix(n + 1, vector<long long>(m + 1, 0));

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            cin >> grid[i][j];

            prefix[i][j] =
                grid[i][j]
                + prefix[i - 1][j]
                + prefix[i][j - 1]
                - prefix[i - 1][j - 1];
        }
    }

    int q;
    cin >> q;

    while (q--) {
        int r1, c1, r2, c2;
        cin >> r1 >> c1 >> r2 >> c2;

        long long answer =
            prefix[r2][c2]
            - prefix[r1 - 1][c2]
            - prefix[r2][c1 - 1]
            + prefix[r1 - 1][c1 - 1];

        cout << answer << '\n';
    }

    return 0;
}
```

---

## Preferred compact formula style

The same important lines can be written compactly:

```cpp
prefix[i][j] = grid[i][j] + prefix[i - 1][j]
             + prefix[i][j - 1] - prefix[i - 1][j - 1];

long long answer = prefix[r2][c2] - prefix[r1 - 1][c2]
                 - prefix[r2][c1 - 1] + prefix[r1 - 1][c1 - 1];
```

The important thing is not memorizing the symbols.

Remember:

```text
build:
current + top + left - overlap

query:
big - top - left + overlap
```

---

# 17. Forest Queries Pattern

The lecture points to **Forest Queries** as a standard 2D prefix-sum problem.

Typical model:

```text
'.' = empty
'*' = tree
```

Convert:

```text
'.' -> 0
'*' -> 1
```

Example:

```text
original grid

.*..
**..
...*
.*..

convert to numbers

0 1 0 0
1 1 0 0
0 0 0 1
0 1 0 0
```

Now a rectangle query asking:

```text
"How many trees are inside this rectangle?"
```

becomes:

```text
"Find the rectangle sum."
```

So the same 2D prefix formula works.

---

## Forest Queries mental transformation

```text
characters
    |
    v
0 / 1 grid
    |
    v
2D prefix sum
    |
    v
rectangle query in O(1)
```

---

# 18. Bonus — 2D Prefix XOR

The lecture gives a homework extension:

```text
Given a 2D grid,
find XOR of all elements in a rectangle.
```

The rectangle structure is similar to 2D prefix sums.

Define:

```text
xorPrefix[i][j]
=
XOR of rectangle (0,0) -> (i,j)
```

For query:

```text
(r1,c1) -> (r2,c2)
```

the lecture gives:

```text
xorPrefix[r2][c2]
^ xorPrefix[r1 - 1][c2]
^ xorPrefix[r2][c1 - 1]
^ xorPrefix[r1 - 1][c1 - 1]
```

Why do all operations use XOR?

Because:

```text
x ^ x = 0
```

So duplicate regions cancel automatically.

---

## XOR intuition

For addition:

```text
BIG - TOP - LEFT + OVERLAP
```

For XOR:

```text
BIG ^ TOP ^ LEFT ^ OVERLAP
```

ASCII:

```text
SUM
---
add
subtract
subtract
add overlap

XOR
---
xor
xor
xor
xor overlap
```

---

# 19. Common Mistakes

## Mistake 1 — Using the wrong side of `L`

Wrong:

```text
prefix[R] - prefix[L]
```

Correct:

```text
prefix[R] - prefix[L - 1]
```

Why?

Because we want to remove everything **before** `L`.

---

## Mistake 2 — Forgetting `L = 0`

With 0-based prefix arrays:

```cpp
if (l == 0) {
    answer = prefix[r];
}
```

Safer alternative:

```text
use a prefix array with an extra zero
```

---

## Mistake 3 — Forgetting the overlap in 2D

Wrong:

```text
BIG - TOP - LEFT
```

This removes the top-left overlap twice.

Correct:

```text
BIG - TOP - LEFT + OVERLAP
```

---

## Mistake 4 — Mixing indexing conventions

Do not mix:

```text
0-based array
1-based formula
```

without carefully converting indices.

A good CP habit:

```text
Use 1-based prefix arrays with a zero boundary.
```

---

## Mistake 5 — Using `int` when sums can be large

Example:

```text
N = 2 * 10^5
arr[i] = 10^9
```

Total can be:

```text
2 * 10^14
```

That does not fit in 32-bit `int`.

Use:

```cpp
long long
```

for prefix sums when values can accumulate.

---

# 20. Pattern Recognition — When to Use Prefix Sum

Think **prefix sum** when the statement contains signals such as:

```text
many queries
+
static array
+
sum over [L,R]
```

or:

```text
many rectangle queries
+
static grid
+
sum/count inside rectangle
```

Recognition examples:

```text
"sum between L and R"
        |
        v
1D prefix sum

"count number of trees in rectangle"
        |
        v
convert to 0/1
        |
        v
2D prefix sum

"sum of rectangle (r1,c1) to (r2,c2)"
        |
        v
2D prefix sum

"XOR of a rectangle"
        |
        v
2D prefix XOR
```

---

## 60-second decision checklist

Ask:

```text
1. Is the data mostly static?
2. Are there many range queries?
3. Does each query ask for a cumulative operation?
4. Can the answer be written as:
       total-to-right - total-before-left
   ?
5. In 2D, can the answer be written as:
       big - top - left + overlap
   ?
```

If yes, prefix sums are a strong candidate.

---

# 21. Complexity Cheat Sheet

```text
1D prefix construction       O(N)
1D range query               O(1)
Q 1D range queries           O(Q)
Total 1D                     O(N + Q)

2D prefix construction       O(N * M)
2D rectangle query           O(1)
Q rectangle queries          O(Q)
Total 2D                     O(N * M + Q)
```

Memory:

```text
1D prefix                    O(N)
2D prefix                    O(N * M)
```

---

# 22. Practice Checklist

You should be able to derive these without looking at the note:

- [ ] Meaning of `prefix[i]`
- [ ] `prefix[i] = prefix[i - 1] + arr[i]`
- [ ] Why `sum(L,R) = prefix[R] - prefix[L - 1]`
- [ ] Why a zero prefix makes indexing easier
- [ ] Meaning of `prefix[i][j]` in 2D
- [ ] `current + top + left - overlap`
- [ ] `big - top - left + overlap`
- [ ] Why the overlap is added back
- [ ] Why 2D rectangle queries become `O(1)`
- [ ] How Forest Queries becomes a 0/1 grid problem
- [ ] Why XOR uses `^` on all four prefix terms
- [ ] Time complexity of 1D prefix sums
- [ ] Time complexity of 2D prefix sums
- [ ] When `long long` is necessary

---

# 23. Final Mental Model

```text
1D PREFIX SUM
=============

array
  |
  v
cumulative sums
  |
  v

prefix[R]
-
prefix[L-1]
  |
  v
sum(L,R)
```

```text
2D PREFIX SUM
=============

grid
  |
  v
prefix rectangle from (0,0)
  |
  v

BIG
- TOP
- LEFT
+ OVERLAP
  |
  v
rectangle answer
```

```text
FOREST QUERIES
==============

'.' -> 0
'*' -> 1
   |
   v
2D prefix sum
   |
   v
count trees in rectangle
```

```text
2D PREFIX XOR
=============

BIG
^ TOP
^ LEFT
^ OVERLAP
   |
   v
rectangle XOR
```

The most important idea is:

```text
Do expensive repeated work once.

Store cumulative information.

Then answer each query by combining
a small number of precomputed states.
```

---

## Source Coverage

The uploaded lecture covers:

```text
1D Prefix Sum
1D Subarray Sum Queries
2D Prefix Sum
2D Rectangle Queries
Forest Queries
2D Prefix XOR as bonus/homework
```

The uploaded 26-page lecture does **not** develop difference arrays, so they are intentionally not added to this note.
