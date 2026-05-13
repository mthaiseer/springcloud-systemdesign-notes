# 001 — Prefix Sum & Partial Sum Complete Guide

A CP/DSA guide built from the uploaded handwritten PDF and cleaned into an interview/contest style note.

This file intentionally has **no diagrams**. Everything is explained using:

- problem statement
- input/output format
- intuition
- formula
- index-by-index dry run
- C++ code
- complexity
- common mistakes

---

# Clickable Index

## Core Mental Model

1. [How to Think About Prefix Sum](#1-how-to-think-about-prefix-sum)
2. [Prefix Sum vs Difference Array](#2-prefix-sum-vs-difference-array)
3. [0-Indexed vs 1-Indexed Prefix](#3-0-indexed-vs-1-indexed-prefix)

## Part A — 1D Prefix Sum

4. [Problem 1 — Static Range Sum Query](#4-problem-1--static-range-sum-query)
5. [Index-by-Index Dry Run — 1D Prefix Build](#5-index-by-index-dry-run--1d-prefix-build)
6. [Index-by-Index Dry Run — 1D Range Query](#6-index-by-index-dry-run--1d-range-query)
7. [C++ Code — 1D Prefix Sum](#7-c-code--1d-prefix-sum)
8. [Common Mistakes in 1D Prefix Sum](#8-common-mistakes-in-1d-prefix-sum)

## Part B — 1D Difference Array / Partial Sum

9. [Problem 2 — Range Add Queries and Final Array](#9-problem-2--range-add-queries-and-final-array)
10. [Why Brute Force is Slow](#10-why-brute-force-is-slow)
11. [Difference Array Intuition](#11-difference-array-intuition)
12. [Index-by-Index Dry Run — Applying Updates](#12-index-by-index-dry-run--applying-updates)
13. [Index-by-Index Dry Run — Rebuilding Final Array](#13-index-by-index-dry-run--rebuilding-final-array)
14. [C++ Code — 1D Difference Array](#14-c-code--1d-difference-array)
15. [Common Mistakes in 1D Difference Array](#15-common-mistakes-in-1d-difference-array)

## Part C — 2D Prefix Sum

16. [Problem 3 — Static Rectangle Sum Query](#16-problem-3--static-rectangle-sum-query)
17. [Why Row Prefix is Better but Not Best](#17-why-row-prefix-is-better-but-not-best)
18. [2D Prefix Sum Intuition](#18-2d-prefix-sum-intuition)
19. [Index-by-Index Dry Run — Build 2D Prefix](#19-index-by-index-dry-run--build-2d-prefix)
20. [Index-by-Index Dry Run — Rectangle Query](#20-index-by-index-dry-run--rectangle-query)
21. [C++ Code — 2D Prefix Sum](#21-c-code--2d-prefix-sum)
22. [Common Mistakes in 2D Prefix Sum](#22-common-mistakes-in-2d-prefix-sum)

## Part D — 2D Difference Array / 2D Partial Sum

23. [Problem 4 — Rectangle Add Queries and Final Matrix](#23-problem-4--rectangle-add-queries-and-final-matrix)
24. [2D Difference Array Intuition](#24-2d-difference-array-intuition)
25. [Index-by-Index Dry Run — Applying Rectangle Updates](#25-index-by-index-dry-run--applying-rectangle-updates)
26. [Index-by-Index Dry Run — Rebuilding Final Matrix](#26-index-by-index-dry-run--rebuilding-final-matrix)
27. [C++ Code — 2D Difference Array](#27-c-code--2d-difference-array)
28. [Common Mistakes in 2D Difference Array](#28-common-mistakes-in-2d-difference-array)

## Final Revision

29. [Decision Table — Which Technique to Use](#29-decision-table--which-technique-to-use)
30. [Contest Templates](#30-contest-templates)
31. [Final CP Notes](#31-final-cp-notes)

---

# 1. How to Think About Prefix Sum

Prefix sum is not something to memorize.

Think like this:

```text
Normal array tells value at one index.
Prefix array tells total value from the start up to that index.
```

For array:

```text
a = [4, 2, 3, 1, -5, 6]
```

Prefix sum means:

```text
pref[0] = a[0]
pref[1] = a[0] + a[1]
pref[2] = a[0] + a[1] + a[2]
...
```

So:

```text
pref = [4, 6, 9, 10, 5, 11]
```

Main idea:

```text
If I know total from 0 to r,
and I remove total from 0 to l-1,
then only l to r remains.
```

Formula:

```text
sum(l, r) = pref[r] - pref[l - 1]
```

If `l = 0`:

```text
sum(0, r) = pref[r]
```

---

# 2. Prefix Sum vs Difference Array

These two are connected but solve opposite-style problems.

| Problem Type | Technique | Meaning |
|---|---|---|
| Many range sum queries | Prefix sum | Precompute totals, answer query fast |
| Many range add updates | Difference array | Mark start/stop of updates, rebuild later |
| Many rectangle sum queries | 2D prefix sum | Precompute top-left rectangle sums |
| Many rectangle add updates | 2D difference array | Mark four corners, rebuild later |

Mental shortcut:

```text
Prefix Sum      = answer repeated range sum queries.
Difference Array = apply repeated range updates cheaply.
```

---

# 3. 0-Indexed vs 1-Indexed Prefix

## 0-indexed prefix

Array:

```text
a[0], a[1], ..., a[n-1]
```

Prefix:

```text
pref[i] = a[0] + a[1] + ... + a[i]
```

Query:

```text
if l == 0: sum = pref[r]
else     : sum = pref[r] - pref[l-1]
```

## 1-indexed prefix

Create one extra cell:

```text
pref[0] = 0
pref[1] = a[0]
pref[2] = a[0] + a[1]
...
pref[i] = sum of first i elements
```

Query for 0-indexed array range `[l, r]`:

```text
sum(l, r) = pref[r + 1] - pref[l]
```

Why this is better in CP:

```text
No special case for l = 0.
```

---

# 4. Problem 1 — Static Range Sum Query

## Problem Statement

You are given an array `a` of size `n` and `q` queries.

Each query contains two integers `l` and `r`.

For every query, print:

```text
a[l] + a[l+1] + ... + a[r]
```

Assume all indexes are `0-indexed` and inclusive.

## Input Format

```text
n q
a0 a1 a2 ... a(n-1)
l1 r1
l2 r2
...
lq rq
```

## Output Format

For each query, print the range sum.

## Sample Input

```text
6 2
4 2 3 1 -5 6
1 3
2 4
```

## Sample Output

```text
6
-1
```

## Explanation

```text
Query 1: sum(1, 3) = 2 + 3 + 1 = 6
Query 2: sum(2, 4) = 3 + 1 + (-5) = -1
```

---

# 5. Index-by-Index Dry Run — 1D Prefix Build

Input:

```text
a = [4, 2, 3, 1, -5, 6]
```

We build 1-indexed prefix:

```text
pref size = n + 1
pref[0] = 0
```

Initial:

```text
index:  0   1   2   3   4   5
array: [4,  2,  3,  1, -5,  6]

pref : [0, 0, 0, 0, 0, 0, 0]
         0  1  2  3  4  5  6
```

## i = 1

```text
Current array element = a[0] = 4
pref[1] = pref[0] + a[0]
pref[1] = 0 + 4 = 4

pref = [0, 4, 0, 0, 0, 0, 0]
```

## i = 2

```text
Current array element = a[1] = 2
pref[2] = pref[1] + a[1]
pref[2] = 4 + 2 = 6

pref = [0, 4, 6, 0, 0, 0, 0]
```

## i = 3

```text
Current array element = a[2] = 3
pref[3] = pref[2] + a[2]
pref[3] = 6 + 3 = 9

pref = [0, 4, 6, 9, 0, 0, 0]
```

## i = 4

```text
Current array element = a[3] = 1
pref[4] = pref[3] + a[3]
pref[4] = 9 + 1 = 10

pref = [0, 4, 6, 9, 10, 0, 0]
```

## i = 5

```text
Current array element = a[4] = -5
pref[5] = pref[4] + a[4]
pref[5] = 10 + (-5) = 5

pref = [0, 4, 6, 9, 10, 5, 0]
```

## i = 6

```text
Current array element = a[5] = 6
pref[6] = pref[5] + a[5]
pref[6] = 5 + 6 = 11

pref = [0, 4, 6, 9, 10, 5, 11]
```

Final prefix:

```text
pref = [0, 4, 6, 9, 10, 5, 11]
```

---

# 6. Index-by-Index Dry Run — 1D Range Query

Using:

```text
a    = [4, 2, 3, 1, -5, 6]
pref = [0, 4, 6, 9, 10, 5, 11]
```

## Query 1: l = 1, r = 3

We need:

```text
a[1] + a[2] + a[3]
= 2 + 3 + 1
= 6
```

Using 1-indexed prefix:

```text
sum(l, r) = pref[r + 1] - pref[l]
sum(1, 3) = pref[4] - pref[1]
          = 10 - 4
          = 6
```

Meaning:

```text
pref[4] = sum of first 4 elements = 4 + 2 + 3 + 1 = 10
pref[1] = sum before index 1      = 4
remove 4, remaining = 2 + 3 + 1
```

## Query 2: l = 2, r = 4

We need:

```text
a[2] + a[3] + a[4]
= 3 + 1 + (-5)
= -1
```

Using prefix:

```text
sum(2, 4) = pref[5] - pref[2]
          = 5 - 6
          = -1
```

Meaning:

```text
pref[5] = 4 + 2 + 3 + 1 - 5 = 5
pref[2] = 4 + 2 = 6
remove first two values, remaining = 3 + 1 - 5 = -1
```

---

# 7. C++ Code — 1D Prefix Sum

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> a(n);
    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    // 1-indexed prefix array
    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    while (q--) {
        int l, r;
        cin >> l >> r;

        long long ans = pref[r + 1] - pref[l];
        cout << ans << '\n';
    }

    return 0;
}
```

## Complexity

```text
Build prefix: O(n)
Each query  : O(1)
Total       : O(n + q)
```

---

# 8. Common Mistakes in 1D Prefix Sum

## Mistake 1: Forgetting the extra prefix cell

For 1-indexed prefix:

```cpp
vector<long long> pref(n + 1, 0);
```

Not:

```cpp
vector<long long> pref(n, 0);
```

## Mistake 2: Using wrong formula

For 1-indexed prefix with 0-indexed query:

```cpp
pref[r + 1] - pref[l]
```

Not:

```cpp
pref[r] - pref[l - 1]
```

That formula is for 0-indexed prefix.

## Mistake 3: Using int for large sums

Use:

```cpp
long long
```

Because values and sums can overflow `int`.

---

# 9. Problem 2 — Range Add Queries and Final Array

## Problem Statement

You are given an array of size `n`, initially filled with zeros.

You are given `q` queries of the form:

```text
L R X
```

For each query, add `X` to every element from index `L` to index `R`.

After all queries, print the final array.

All indexes are `0-indexed` and inclusive.

## Input Format

```text
n q
L1 R1 X1
L2 R2 X2
...
Lq Rq Xq
```

## Output Format

```text
final array after all updates
```

## Sample Input

```text
6 3
2 4 1
1 5 3
0 2 2
```

## Sample Output

```text
2 5 6 4 4 3
```

---

# 10. Why Brute Force is Slow

Brute force:

```cpp
for each query:
    for i from L to R:
        a[i] += X
```

Worst case each query touches all `n` elements.

So total complexity:

```text
O(q * n)
```

If:

```text
n = 200000
q = 200000
```

Then:

```text
q * n = 40,000,000,000 operations
```

Too slow.

---

# 11. Difference Array Intuition

For range update:

```text
Add X from L to R
```

Do not update all cells.

Only mark:

```text
diff[L]     += X   // start adding X from here
diff[R + 1] -= X   // stop adding X after R
```

Then at the end, take prefix sum of `diff`.

Why?

```text
Prefix sum carries the effect forward.
+X starts at L.
-X at R+1 cancels it after R.
```

This is why difference array is also called partial sum technique in many CP courses.

---

# 12. Index-by-Index Dry Run — Applying Updates

Given:

```text
n = 6
queries:
1) add 1 on [2, 4]
2) add 3 on [1, 5]
3) add 2 on [0, 2]
```

Initial difference array:

```text
index:  0  1  2  3  4  5
diff : [0, 0, 0, 0, 0, 0]
```

## Query 1: L = 2, R = 4, X = 1

Rule:

```text
diff[L] += X
diff[R + 1] -= X
```

Apply:

```text
diff[2] += 1
diff[5] -= 1
```

Result:

```text
index:  0  1  2  3  4   5
diff : [0, 0, 1, 0, 0, -1]
```

Meaning:

```text
Start +1 at index 2.
Stop +1 after index 4, so put -1 at index 5.
```

## Query 2: L = 1, R = 5, X = 3

Apply:

```text
diff[1] += 3
R + 1 = 6 is outside array, so no subtraction.
```

Before:

```text
[0, 0, 1, 0, 0, -1]
```

After:

```text
index:  0  1  2  3  4   5
diff : [0, 3, 1, 0, 0, -1]
```

Meaning:

```text
Start +3 at index 1.
It continues until the last index.
No stop marker needed inside array.
```

## Query 3: L = 0, R = 2, X = 2

Apply:

```text
diff[0] += 2
diff[3] -= 2
```

Before:

```text
[0, 3, 1, 0, 0, -1]
```

After:

```text
index:  0  1  2   3  4   5
diff : [2, 3, 1, -2, 0, -1]
```

Meaning:

```text
Start +2 at index 0.
Stop +2 after index 2, so put -2 at index 3.
```

Final difference array before prefix:

```text
diff = [2, 3, 1, -2, 0, -1]
```

---

# 13. Index-by-Index Dry Run — Rebuilding Final Array

Now take prefix sum of `diff`.

```text
diff = [2, 3, 1, -2, 0, -1]
```

We build final values in-place.

## i = 0

```text
current running sum = 2
final[0] = 2
```

Array so far:

```text
[2, _, _, _, _, _]
```

## i = 1

```text
running sum = previous running sum + diff[1]
running sum = 2 + 3 = 5
final[1] = 5
```

Array so far:

```text
[2, 5, _, _, _, _]
```

## i = 2

```text
running sum = 5 + 1 = 6
final[2] = 6
```

Array so far:

```text
[2, 5, 6, _, _, _]
```

## i = 3

```text
running sum = 6 + (-2) = 4
final[3] = 4
```

Array so far:

```text
[2, 5, 6, 4, _, _]
```

## i = 4

```text
running sum = 4 + 0 = 4
final[4] = 4
```

Array so far:

```text
[2, 5, 6, 4, 4, _]
```

## i = 5

```text
running sum = 4 + (-1) = 3
final[5] = 3
```

Final array:

```text
[2, 5, 6, 4, 4, 3]
```

This matches the sample output.

---

# 14. C++ Code — 1D Difference Array

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, q;
    cin >> n >> q;

    vector<long long> diff(n, 0);

    while (q--) {
        int L, R;
        long long X;
        cin >> L >> R >> X;

        diff[L] += X;
        if (R + 1 < n) {
            diff[R + 1] -= X;
        }
    }

    for (int i = 1; i < n; i++) {
        diff[i] += diff[i - 1];
    }

    for (int i = 0; i < n; i++) {
        cout << diff[i] << ' ';
    }
    cout << '\n';

    return 0;
}
```

## Complexity

```text
Each update: O(1)
Rebuild    : O(n)
Total      : O(q + n)
```

---

# 15. Common Mistakes in 1D Difference Array

## Mistake 1: Using R - 1 instead of R + 1

For inclusive range `[L, R]`, stop after `R`:

```cpp
diff[R + 1] -= X;
```

Not:

```cpp
diff[R - 1] -= X;
```

## Mistake 2: Forgetting boundary check

When `R = n - 1`, `R + 1` is outside.

So:

```cpp
if (R + 1 < n) diff[R + 1] -= X;
```

## Mistake 3: Trying to answer online queries directly

Difference array is best when:

```text
all updates first, final array later
```

If updates and queries are mixed online, use:

```text
Fenwick Tree or Segment Tree
```

---

# 16. Problem 3 — Static Rectangle Sum Query

## Problem Statement

You are given a matrix `a` of size `n x m`.

You are given `q` queries.

Each query contains:

```text
U D L R
```

Find the sum of all cells inside the rectangle:

```text
rows    U to D
columns L to R
```

All indexes are `0-indexed` and inclusive.

## Input Format

```text
n m q
matrix values
U1 D1 L1 R1
U2 D2 L2 R2
...
```

## Output Format

Print rectangle sum for each query.

## Sample Input

```text
3 3 2
1 2 3
4 5 6
7 8 9
1 2 1 2
0 1 0 1
```

## Sample Output

```text
28
12
```

## Explanation

Query 1:

```text
rows 1 to 2, cols 1 to 2
values = 5, 6, 8, 9
sum = 28
```

Query 2:

```text
rows 0 to 1, cols 0 to 1
values = 1, 2, 4, 5
sum = 12
```

---

# 17. Why Row Prefix is Better but Not Best

For each row, we can build 1D prefix sums.

Then for a rectangle query:

```text
for each row from U to D:
    get sum from L to R in O(1)
```

If rectangle height is `h`, query cost is:

```text
O(h)
```

Worst case:

```text
O(n)
```

This is better than checking every cell, but still not best.

2D prefix sum gives:

```text
O(1) per rectangle query
```

---

# 18. 2D Prefix Sum Intuition

Define:

```text
pref[i][j] = sum of rectangle from (0,0) to (i-1,j-1)
```

This is 1-indexed prefix style.

So:

```text
pref has size (n + 1) x (m + 1)
pref[0][*] = 0
pref[*][0] = 0
```

Build formula:

```text
pref[i][j] = a[i-1][j-1]
           + pref[i-1][j]
           + pref[i][j-1]
           - pref[i-1][j-1]
```

Why subtract diagonal?

```text
pref[i-1][j] includes upper area.
pref[i][j-1] includes left area.
Their overlap is counted twice.
So subtract pref[i-1][j-1] once.
```

---

# 19. Index-by-Index Dry Run — Build 2D Prefix

Matrix:

```text
a =
1 2 3
4 5 6
7 8 9
```

Create `pref[4][4]` initialized with zero.

```text
pref initially:
0 0 0 0
0 0 0 0
0 0 0 0
0 0 0 0
```

Formula:

```text
pref[i][j] = a[i-1][j-1] + pref[i-1][j] + pref[i][j-1] - pref[i-1][j-1]
```

## i = 1, j = 1

```text
a[0][0] = 1
pref[1][1] = 1 + pref[0][1] + pref[1][0] - pref[0][0]
           = 1 + 0 + 0 - 0
           = 1
```

```text
pref:
0 0 0 0
0 1 0 0
0 0 0 0
0 0 0 0
```

## i = 1, j = 2

```text
a[0][1] = 2
pref[1][2] = 2 + pref[0][2] + pref[1][1] - pref[0][1]
           = 2 + 0 + 1 - 0
           = 3
```

```text
pref:
0 0 0 0
0 1 3 0
0 0 0 0
0 0 0 0
```

## i = 1, j = 3

```text
a[0][2] = 3
pref[1][3] = 3 + 0 + 3 - 0 = 6
```

```text
pref:
0 0 0 0
0 1 3 6
0 0 0 0
0 0 0 0
```

## i = 2, j = 1

```text
a[1][0] = 4
pref[2][1] = 4 + pref[1][1] + pref[2][0] - pref[1][0]
           = 4 + 1 + 0 - 0
           = 5
```

```text
pref:
0 0 0 0
0 1 3 6
0 5 0 0
0 0 0 0
```

## i = 2, j = 2

```text
a[1][1] = 5
pref[2][2] = 5 + pref[1][2] + pref[2][1] - pref[1][1]
           = 5 + 3 + 5 - 1
           = 12
```

```text
pref:
0 0 0 0
0 1 3 6
0 5 12 0
0 0 0 0
```

## i = 2, j = 3

```text
a[1][2] = 6
pref[2][3] = 6 + pref[1][3] + pref[2][2] - pref[1][2]
           = 6 + 6 + 12 - 3
           = 21
```

```text
pref:
0 0 0 0
0 1 3 6
0 5 12 21
0 0 0 0
```

## i = 3, j = 1

```text
a[2][0] = 7
pref[3][1] = 7 + pref[2][1] + pref[3][0] - pref[2][0]
           = 7 + 5 + 0 - 0
           = 12
```

```text
pref:
0 0 0 0
0 1 3 6
0 5 12 21
0 12 0 0
```

## i = 3, j = 2

```text
a[2][1] = 8
pref[3][2] = 8 + pref[2][2] + pref[3][1] - pref[2][1]
           = 8 + 12 + 12 - 5
           = 27
```

```text
pref:
0 0 0 0
0 1 3 6
0 5 12 21
0 12 27 0
```

## i = 3, j = 3

```text
a[2][2] = 9
pref[3][3] = 9 + pref[2][3] + pref[3][2] - pref[2][2]
           = 9 + 21 + 27 - 12
           = 45
```

Final prefix:

```text
pref:
0 0 0 0
0 1 3 6
0 5 12 21
0 12 27 45
```

---

# 20. Index-by-Index Dry Run — Rectangle Query

Query:

```text
U = 1, D = 2, L = 1, R = 2
```

This means:

```text
rows 1..2
cols 1..2
```

Cells:

```text
5 6
8 9
```

Expected sum:

```text
5 + 6 + 8 + 9 = 28
```

Using 1-indexed prefix:

```text
answer = pref[D + 1][R + 1]
       - pref[U][R + 1]
       - pref[D + 1][L]
       + pref[U][L]
```

Substitute:

```text
answer = pref[3][3]
       - pref[1][3]
       - pref[3][1]
       + pref[1][1]
```

From final prefix:

```text
pref[3][3] = 45
pref[1][3] = 6
pref[3][1] = 12
pref[1][1] = 1
```

So:

```text
answer = 45 - 6 - 12 + 1
       = 28
```

Meaning:

```text
Take big rectangle from top-left to bottom-right.
Remove area above target.
Remove area left of target.
Add back overlap removed twice.
```

---

# 21. C++ Code — 2D Prefix Sum

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, q;
    cin >> n >> m >> q;

    vector<vector<long long>> a(n, vector<long long>(m));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cin >> a[i][j];
        }
    }

    vector<vector<long long>> pref(n + 1, vector<long long>(m + 1, 0));

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            pref[i][j] = a[i - 1][j - 1]
                       + pref[i - 1][j]
                       + pref[i][j - 1]
                       - pref[i - 1][j - 1];
        }
    }

    while (q--) {
        int U, D, L, R;
        cin >> U >> D >> L >> R;

        long long ans = pref[D + 1][R + 1]
                      - pref[U][R + 1]
                      - pref[D + 1][L]
                      + pref[U][L];

        cout << ans << '\n';
    }

    return 0;
}
```

## Complexity

```text
Build prefix: O(n * m)
Each query  : O(1)
Total       : O(n * m + q)
```

---

# 22. Common Mistakes in 2D Prefix Sum

## Mistake 1: Forgetting the plus overlap

Correct query formula:

```cpp
pref[D + 1][R + 1]
- pref[U][R + 1]
- pref[D + 1][L]
+ pref[U][L]
```

The last term is `+`, not `-`.

## Mistake 2: Mixing row and column order

Be consistent:

```text
matrix[row][col]
row = i
col = j
```

Query:

```text
U, D = row range
L, R = column range
```

## Mistake 3: Not using padding

With padding:

```cpp
pref(n + 1, m + 1)
```

You avoid boundary checks.

---

# 23. Problem 4 — Rectangle Add Queries and Final Matrix

## Problem Statement

You are given an `n x m` matrix initially filled with zero.

You are given `q` queries.

Each query contains:

```text
U D L R X
```

For every query, add `X` to all cells inside rectangle:

```text
rows    U to D
columns L to R
```

After all queries, print the final matrix.

All indexes are `0-indexed` and inclusive.

## Input Format

```text
n m q
U1 D1 L1 R1 X1
U2 D2 L2 R2 X2
...
```

## Output Format

Print the final matrix after all updates.

## Sample Input

```text
4 5 2
1 3 1 3 5
0 2 0 1 2
```

## Sample Output

```text
2 2 0 0 0
2 7 5 5 0
2 7 5 5 0
0 5 5 5 0
```

---

# 24. 2D Difference Array Intuition

In 1D difference array:

```text
+X starts at L
-X stops after R
```

In 2D rectangle update:

```text
+X starts at top-left corner
-X stops after right boundary
-X stops after bottom boundary
+X fixes the double-subtracted bottom-right outside area
```

For rectangle:

```text
rows U..D
cols L..R
```

Apply:

```text
diff[U][L]         += X
diff[U][R + 1]     -= X
diff[D + 1][L]     -= X
diff[D + 1][R + 1] += X
```

After all queries, take 2D prefix sum of `diff` to get final matrix.

---

# 25. Index-by-Index Dry Run — Applying Rectangle Updates

Given:

```text
n = 4, m = 5
initial matrix all zero
```

Queries:

```text
1) add 5 on rows 1..3, cols 1..3
2) add 2 on rows 0..2, cols 0..1
```

We use padded diff of size `(n + 1) x (m + 1)`:

```text
5 rows x 6 cols
```

Initial diff:

```text
0 0 0 0 0 0
0 0 0 0 0 0
0 0 0 0 0 0
0 0 0 0 0 0
0 0 0 0 0 0
```

## Query 1: U = 1, D = 3, L = 1, R = 3, X = 5

Apply four-corner rule:

```text
diff[1][1] += 5
diff[1][4] -= 5
diff[4][1] -= 5
diff[4][4] += 5
```

Diff after query 1:

```text
0  0 0 0  0 0
0  5 0 0 -5 0
0  0 0 0  0 0
0  0 0 0  0 0
0 -5 0 0  5 0
```

Meaning:

```text
+5 begins at row 1 col 1.
-5 at col 4 stops it after column 3.
-5 at row 4 stops it after row 3.
+5 at row 4 col 4 fixes overlap.
```

## Query 2: U = 0, D = 2, L = 0, R = 1, X = 2

Apply:

```text
diff[0][0] += 2
diff[0][2] -= 2
diff[3][0] -= 2
diff[3][2] += 2
```

Diff before query 2:

```text
0  0 0 0  0 0
0  5 0 0 -5 0
0  0 0 0  0 0
0  0 0 0  0 0
0 -5 0 0  5 0
```

Diff after query 2:

```text
 2  0 -2 0  0 0
 0  5  0 0 -5 0
 0  0  0 0  0 0
-2  0  2 0  0 0
 0 -5  0 0  5 0
```

Now all updates are stored as corner markers only.

---

# 26. Index-by-Index Dry Run — Rebuilding Final Matrix

Now compute 2D prefix on `diff`.

Formula:

```text
diff[i][j] = diff[i][j]
           + up
           + left
           - diagonal
```

Where:

```text
up       = diff[i-1][j]
left     = diff[i][j-1]
diagonal = diff[i-1][j-1]
```

We only print actual `n x m` area: rows `0..3`, cols `0..4`.

Starting diff markers:

```text
 2  0 -2 0  0 0
 0  5  0 0 -5 0
 0  0  0 0  0 0
-2  0  2 0  0 0
 0 -5  0 0  5 0
```

## Row 0 rebuild

### Cell (0,0)

```text
value = 2 + 0 + 0 - 0 = 2
```

### Cell (0,1)

```text
value = 0 + 0 + 2 - 0 = 2
```

### Cell (0,2)

```text
value = -2 + 0 + 2 - 0 = 0
```

### Cell (0,3)

```text
value = 0 + 0 + 0 - 0 = 0
```

### Cell (0,4)

```text
value = 0 + 0 + 0 - 0 = 0
```

Row 0 final:

```text
2 2 0 0 0
```

## Row 1 rebuild

Current prefix values from row 0 are available.

### Cell (1,0)

```text
marker = 0
up = 2
left = 0
diag = 0
value = 0 + 2 + 0 - 0 = 2
```

### Cell (1,1)

```text
marker = 5
up = 2
left = 2
diag = 2
value = 5 + 2 + 2 - 2 = 7
```

### Cell (1,2)

```text
marker = 0
up = 0
left = 7
diag = 2
value = 0 + 0 + 7 - 2 = 5
```

### Cell (1,3)

```text
marker = 0
up = 0
left = 5
diag = 0
value = 5
```

### Cell (1,4)

```text
marker = -5
up = 0
left = 5
diag = 0
value = 0
```

Row 1 final:

```text
2 7 5 5 0
```

## Row 2 rebuild

### Cell (2,0)

```text
value = 0 + up(2) + left(0) - diag(0) = 2
```

### Cell (2,1)

```text
value = 0 + up(7) + left(2) - diag(2) = 7
```

### Cell (2,2)

```text
value = 0 + up(5) + left(7) - diag(7) = 5
```

### Cell (2,3)

```text
value = 0 + up(5) + left(5) - diag(5) = 5
```

### Cell (2,4)

```text
value = 0 + up(0) + left(5) - diag(5) = 0
```

Row 2 final:

```text
2 7 5 5 0
```

## Row 3 rebuild

### Cell (3,0)

```text
marker = -2
up = 2
value = -2 + 2 = 0
```

### Cell (3,1)

```text
marker = 0
up = 7
left = 0
diag = 2
value = 0 + 7 + 0 - 2 = 5
```

### Cell (3,2)

```text
marker = 2
up = 5
left = 5
diag = 7
value = 2 + 5 + 5 - 7 = 5
```

### Cell (3,3)

```text
marker = 0
up = 5
left = 5
diag = 5
value = 5
```

### Cell (3,4)

```text
marker = 0
up = 0
left = 5
diag = 5
value = 0
```

Row 3 final:

```text
0 5 5 5 0
```

Final matrix:

```text
2 2 0 0 0
2 7 5 5 0
2 7 5 5 0
0 5 5 5 0
```

---

# 27. C++ Code — 2D Difference Array

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m, q;
    cin >> n >> m >> q;

    // Padding allows R+1 and D+1 safely.
    vector<vector<long long>> diff(n + 1, vector<long long>(m + 1, 0));

    while (q--) {
        int U, D, L, R;
        long long X;
        cin >> U >> D >> L >> R >> X;

        diff[U][L] += X;
        diff[U][R + 1] -= X;
        diff[D + 1][L] -= X;
        diff[D + 1][R + 1] += X;
    }

    // Build 2D prefix in-place.
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            long long up = (i > 0 ? diff[i - 1][j] : 0);
            long long left = (j > 0 ? diff[i][j - 1] : 0);
            long long diag = (i > 0 && j > 0 ? diff[i - 1][j - 1] : 0);

            diff[i][j] += up + left - diag;
        }
    }

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cout << diff[i][j] << ' ';
        }
        cout << '\n';
    }

    return 0;
}
```

## Complexity

```text
Each rectangle update: O(1)
Final rebuild        : O(n * m)
Total                : O(q + n * m)
```

---

# 28. Common Mistakes in 2D Difference Array

## Mistake 1: Not allocating enough padding

Use:

```cpp
vector<vector<long long>> diff(n + 1, vector<long long>(m + 1, 0));
```

Because you will access:

```text
D + 1
R + 1
```

When `D = n - 1`, `D + 1 = n`, which is valid only if size is `n + 1`.

## Mistake 2: Wrong signs

Correct signs:

```text
+ at top-left
- at top-right outside
- at bottom-left outside
+ at bottom-right outside
```

Code:

```cpp
diff[U][L] += X;
diff[U][R + 1] -= X;
diff[D + 1][L] -= X;
diff[D + 1][R + 1] += X;
```

## Mistake 3: Printing padded row/column

Only print:

```text
rows 0..n-1
cols 0..m-1
```

Do not print row `n` or column `m`.

---

# 29. Decision Table — Which Technique to Use

| Situation | Use | Why |
|---|---|---|
| Static array + many range sum queries | 1D Prefix Sum | O(1) per query |
| Zero array + many range add updates + final array | 1D Difference Array | O(1) per update |
| Static matrix + many rectangle sum queries | 2D Prefix Sum | O(1) per rectangle query |
| Zero matrix + many rectangle add updates + final matrix | 2D Difference Array | O(1) per rectangle update |
| Updates and queries mixed online | Fenwick / Segment Tree | Prefix/diff alone not enough |

Recognition signals:

```text
"sum from l to r many times"       -> prefix sum
"add x to l..r many times"         -> difference array
"sum rectangle many times"         -> 2D prefix sum
"add x to rectangle many times"    -> 2D difference array
```

---

# 30. Contest Templates

## Template 1 — 1D Prefix Sum

```cpp
vector<long long> buildPrefix(const vector<long long>& a) {
    int n = (int)a.size();
    vector<long long> pref(n + 1, 0);

    for (int i = 1; i <= n; i++) {
        pref[i] = pref[i - 1] + a[i - 1];
    }

    return pref;
}

long long rangeSum(const vector<long long>& pref, int l, int r) {
    return pref[r + 1] - pref[l];
}
```

## Template 2 — 1D Difference Array

```cpp
vector<long long> rangeAddFinalArray(
    int n,
    const vector<tuple<int, int, long long>>& queries
) {
    vector<long long> diff(n, 0);

    for (auto [L, R, X] : queries) {
        diff[L] += X;
        if (R + 1 < n) diff[R + 1] -= X;
    }

    for (int i = 1; i < n; i++) {
        diff[i] += diff[i - 1];
    }

    return diff;
}
```

## Template 3 — 2D Prefix Sum

```cpp
vector<vector<long long>> build2DPrefix(const vector<vector<long long>>& a) {
    int n = (int)a.size();
    int m = (int)a[0].size();

    vector<vector<long long>> pref(n + 1, vector<long long>(m + 1, 0));

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            pref[i][j] = a[i - 1][j - 1]
                       + pref[i - 1][j]
                       + pref[i][j - 1]
                       - pref[i - 1][j - 1];
        }
    }

    return pref;
}

long long rectangleSum(
    const vector<vector<long long>>& pref,
    int U, int D, int L, int R
) {
    return pref[D + 1][R + 1]
         - pref[U][R + 1]
         - pref[D + 1][L]
         + pref[U][L];
}
```

## Template 4 — 2D Difference Array

```cpp
vector<vector<long long>> rectangleAddFinalMatrix(
    int n,
    int m,
    const vector<tuple<int, int, int, int, long long>>& queries
) {
    vector<vector<long long>> diff(n + 1, vector<long long>(m + 1, 0));

    for (auto [U, D, L, R, X] : queries) {
        diff[U][L] += X;
        diff[U][R + 1] -= X;
        diff[D + 1][L] -= X;
        diff[D + 1][R + 1] += X;
    }

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            long long up = (i > 0 ? diff[i - 1][j] : 0);
            long long left = (j > 0 ? diff[i][j - 1] : 0);
            long long diag = (i > 0 && j > 0 ? diff[i - 1][j - 1] : 0);
            diff[i][j] += up + left - diag;
        }
    }

    vector<vector<long long>> ans(n, vector<long long>(m));
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            ans[i][j] = diff[i][j];
        }
    }

    return ans;
}
```

---

# 31. Final CP Notes

## One-line understanding

```text
Prefix sum stores accumulated values.
Difference array stores change points.
```

## 1D prefix formula

```text
pref[i] = pref[i - 1] + a[i - 1]
sum(l, r) = pref[r + 1] - pref[l]
```

## 1D difference formula

```text
diff[L] += X
diff[R + 1] -= X
```

Then prefix sum gives final array.

## 2D prefix formula

```text
pref[i][j] = a[i-1][j-1]
           + pref[i-1][j]
           + pref[i][j-1]
           - pref[i-1][j-1]
```

Rectangle sum:

```text
pref[D+1][R+1] - pref[U][R+1] - pref[D+1][L] + pref[U][L]
```

## 2D difference formula

```text
diff[U][L]         += X
diff[U][R + 1]     -= X
diff[D + 1][L]     -= X
diff[D + 1][R + 1] += X
```

Then 2D prefix gives final matrix.

## Best mindset

Do not memorize formulas blindly.

Ask:

```text
Do I need fast sum query?
    -> precompute prefix.

Do I need fast range update?
    -> mark start and stop using difference array.

Is it a grid?
    -> same idea, but in 2D.
```
