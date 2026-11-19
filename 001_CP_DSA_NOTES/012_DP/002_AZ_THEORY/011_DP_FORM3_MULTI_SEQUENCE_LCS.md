# Form 3 — Multi-Sequence DP
## LCS with 2 Sequences and 3 Sequences

> **Key learning:** When multiple sequences must be matched together, use one DP index for each sequence.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Decide the Form](#1-decide-the-form)
- [2. Two-Sequence LCS](#2-two-sequence-lcs)
- [3. State Space Formulation](#3-state-space-formulation)
- [4. Transition Design](#4-transition-design)
- [5. Base Case and Memoization](#5-base-case-and-memoization)
- [6. Time Complexity](#6-time-complexity)
- [7. C++17 — Two Sequences](#7-c17--two-sequences)
- [8. Three-Sequence LCS](#8-three-sequence-lcs)
- [9. Three-Sequence State Space](#9-three-sequence-state-space)
- [10. Three-Sequence Transitions](#10-three-sequence-transitions)
- [11. Three-Sequence Complexity](#11-three-sequence-complexity)
- [12. C++17 — Three Sequences](#12-c17--three-sequences)
- [13. Recognition Checklist](#13-recognition-checklist)
- [14. Final Mental Model](#14-final-mental-model)

---

# 0. Prerequisites

## 0.1 Subsequences

A subsequence keeps relative order but may skip elements.

```text
A = A G G T A B

one subsequence:

A G T B
| |   | |
A G   T B
```

You may skip characters, but cannot reorder them.

## 0.2 LCS

LCS = Longest Common Subsequence.

Example:

```text
A = A G G T A B
B = G X T X A Y B

common subsequence:
G T A B

length = 4
```

## 0.3 Recursion on suffixes

At positions `i` and `j`, only the remaining parts matter:

```text
A:
A G G T A B
    ^
    i
    |------------>
      A[i...n-1]

B:
G X T X A Y B
      ^
      j
      |---------->
        B[j...m-1]
```

## 0.4 Memoization

The same `(i,j)` state can be reached many times.

```text
first visit
    |
    v
compute
    |
    v
store dp[i][j]

later visit
    |
    v
return stored answer
```

## 0.5 Form idea

```text
Form 1:
dp(index, x)
-> answer for suffix [index...N-1]
   given previous state x

Form 2:
dp(index, x)
-> best answer starting/ending at index

Form 3:
multiple sequences
-> one moving index per sequence
```

---

# 1. Decide the Form

Use Form 3 when:

```text
multiple sequences
+
need to match / align them
```

Mental picture:

```text
A:
[ ][ ][i][ ][ ][ ]
       |
       v
     suffix

B:
[ ][j][ ][ ][ ][ ]
    |
    v
  suffix
```

Two sequences:

```text
dp(i,j)
```

Three sequences:

```text
dp(i,j,k)
```

---

# 2. Two-Sequence LCS

Goal:

```text
find longest common subsequence
of A and B
```

Example:

```text
A = A G G T A B
B = G X T X A Y B

LCS = G T A B
length = 4
```

---

# 3. State Space Formulation

Define:

```text
dp(i,j)
=
LCS length of

A[i...n-1]
and
B[j...m-1]
```

Initial answer:

```text
dp(0,0)
```

Why two indices?

```text
we may skip A[i]
without moving j

or

skip B[j]
without moving i
```

So both positions must be remembered.

State count:

```text
N × M
```

---

# 4. Transition Design

At:

```text
dp(i,j)
```

we have three useful choices.

## 4.1 Skip A[i]

```text
dp(i+1,j)
```

ASCII:

```text
A:
[i] [ ][ ][ ]
 |
 skip
 |
 v
i+1
```

## 4.2 Skip B[j]

```text
dp(i,j+1)
```

## 4.3 Match

If:

```text
A[i] == B[j]
```

take the character:

```text
1 + dp(i+1,j+1)
```

ASCII:

```text
A[i] == B[j]
      |
      v
   take +1
      |
      v
move both indices
      |
      v
dp(i+1,j+1)
```

## 4.4 Complete recurrence

```text
                    dp(i,j)
                       |
          +------------+------------+
          |            |            |
          v            v            v
      skip A        skip B        match
          |            |         if equal
          v            v            |
     dp(i+1,j)     dp(i,j+1)        v
                              1+dp(i+1,j+1)
```

Formula:

```text
dp(i,j)
=
max(
    dp(i+1,j),
    dp(i,j+1),
    1 + dp(i+1,j+1) if A[i] == B[j]
)
```

---

# 5. Base Case and Memoization

Use the fixed recursive order:

```text
Pruning
Base Case
Cache Check
Compute / Transition
Save and Return
```

For LCS:

```text
Pruning:
none

Base Case:
if i >= n OR j >= m
return 0

Cache Check:
if dp[i][j] != -1
return dp[i][j]
```

Why base case is `0`:

```text
one sequence finished
       |
       v
nothing left to match
       |
       v
answer = 0
```

---

# 6. Time Complexity

States:

```text
N × M
```

Transitions per state:

```text
at most 3
```

Therefore:

```text
O(N × M × 3)
=
O(N × M)
```

Space:

```text
DP table: O(N × M)
recursion stack: O(N + M)
```

---

# 7. C++17 — Two Sequences

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m;
string a, b;

int dp[1001][1001];

int rec(int i, int j) {
    // Pruning
    // None

    // Base Case
    if (i >= n || j >= m) {
        return 0;
    }

    // Cache Check
    if (dp[i][j] != -1) {
        return dp[i][j];
    }

    // Compute / Transition
    int ans = 0;

    ans = max(ans, rec(i + 1, j));
    ans = max(ans, rec(i, j + 1));

    if (a[i] == b[j]) {
        ans = max(ans, 1 + rec(i + 1, j + 1));
    }

    // Save and Return
    return dp[i][j] = ans;
}

int main() {
    cin >> a >> b;

    n = a.size();
    m = b.size();

    memset(dp, -1, sizeof(dp));

    cout << rec(0, 0) << '\n';
}
```

---

# 8. Three-Sequence LCS

Now suppose we have:

```text
A
B
C
```

and need one common subsequence shared by all three.

Key learning:

```text
multiple sequences
+
need to match all
      |
      v
Form 3
```

ASCII:

```text
A:
[ ][i][ ][ ][ ]
    |
    v

B:
[ ][ ][j][ ][ ]
       |
       v

C:
[k][ ][ ][ ][ ]
 |
 v
```

So state becomes:

```text
dp(i,j,k)
```

---

# 9. Three-Sequence State Space

Define:

```text
dp(i,j,k)
=
LCS length of

A[i...n-1]
B[j...m-1]
C[k...x-1]
```

Initial answer:

```text
dp(0,0,0)
```

States:

```text
N × M × X
```

---

# 10. Three-Sequence Transitions

From:

```text
dp(i,j,k)
```

we may skip one current character from any sequence.

```text
skip A:
dp(i+1,j,k)

skip B:
dp(i,j+1,k)

skip C:
dp(i,j,k+1)
```

If:

```text
A[i] == B[j]
AND
B[j] == C[k]
```

take the common character:

```text
1 + dp(i+1,j+1,k+1)
```

ASCII:

```text
                        dp(i,j,k)
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
       skip A            skip B            skip C
          |                 |                 |
          v                 v                 v
   dp(i+1,j,k)       dp(i,j+1,k)       dp(i,j,k+1)

                            |
                 if A[i]=B[j]=C[k]
                            |
                            v
                  1+dp(i+1,j+1,k+1)
```

Recurrence:

```text
dp(i,j,k)
=
max(
    dp(i+1,j,k),
    dp(i,j+1,k),
    dp(i,j,k+1),
    1+dp(i+1,j+1,k+1) if all three equal
)
```

Base case:

```text
if any sequence is finished:
return 0
```

---

# 11. Three-Sequence Complexity

States:

```text
N × M × X
```

Transitions:

```text
at most 4
```

So:

```text
O(N × M × X × 4)
=
O(N × M × X)
```

Space:

```text
O(N × M × X)
```

---

# 12. C++17 — Three Sequences

```cpp
#include <bits/stdc++.h>
using namespace std;

int n, m, x;
string a, b, c;

int dp[101][101][101];

int rec(int i, int j, int k) {
    // Pruning
    // None

    // Base Case
    if (i >= n || j >= m || k >= x) {
        return 0;
    }

    // Cache Check
    if (dp[i][j][k] != -1) {
        return dp[i][j][k];
    }

    // Compute / Transition
    int ans = 0;

    ans = max(ans, rec(i + 1, j, k));
    ans = max(ans, rec(i, j + 1, k));
    ans = max(ans, rec(i, j, k + 1));

    if (a[i] == b[j] && b[j] == c[k]) {
        ans = max(ans, 1 + rec(i + 1, j + 1, k + 1));
    }

    // Save and Return
    return dp[i][j][k] = ans;
}

int main() {
    cin >> a >> b >> c;

    n = a.size();
    m = b.size();
    x = c.size();

    memset(dp, -1, sizeof(dp));

    cout << rec(0, 0, 0) << '\n';
}
```

> Adjust DP dimensions according to the constraints.

---

# 13. Recognition Checklist

Use Form 3 when you see:

```text
two or more sequences
+
matching / alignment / common subsequence
```

Ask:

```text
1. How many sequences move independently?

2. Do I need one index per sequence?

3. What suffix does each index represent?

4. What does dp(i,j,...) return?

5. Can I skip the current item of one sequence?

6. When can current elements be matched?

7. If matched, which indices advance?

8. What happens when any required sequence ends?

9. Number of states =
   product of sequence lengths?

10. Are repeated states memoized?
```

---

# 14. Final Mental Model

Two sequences:

```text
A[i...]        B[j...]
   |              |
   +------+-------+
          |
          v
       dp(i,j)
          |
   +------+------+
   |      |      |
skip A  skip B  match
                if equal
```

Three sequences:

```text
A[i...]
B[j...]
C[k...]
   |
   v
dp(i,j,k)
   |
   +--> skip A
   +--> skip B
   +--> skip C
   +--> match all if equal
```

Remember:

```text
MULTI-SEQUENCE DP
=
one DP index per independently moving sequence
```

For LCS:

```text
STATE
dp(i,j,...)
=
best common subsequence
from remaining suffixes

TRANSITION
skip one current element
or
match all current elements when equal
```
