# Form 3 — Multi-Sequence DP
## Different Strings / Diff Utility

> **Core idea:** We have two strings and want to generate a **shortest possible diff representation**.
>
> At every state, we can:
>
> - delete the current character from `X` → `-X[i]`
> - insert the current character from `Y` → `+Y[j]`
> - keep the character when `X[i] == Y[j]`
>
> Because both strings move independently, this is **Form 3 — Multi-Sequence DP**.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Understand the Problem](#1-understand-the-problem)
- [2. Why This Is Form 3](#2-why-this-is-form-3)
- [3. Decide the State](#3-decide-the-state)
- [4. Transition Design](#4-transition-design)
- [5. Base Case](#5-base-case)
- [6. Memoization](#6-memoization)
- [7. Complete Recurrence](#7-complete-recurrence)
- [8. Small Dry Run](#8-small-dry-run)
- [9. Time Complexity](#9-time-complexity)
- [10. Reconstruct One Optimal Diff](#10-reconstruct-one-optimal-diff)
- [11. C++17 — Minimum Diff Length](#11-c17--minimum-diff-length)
- [12. C++17 — Generate One Optimal Diff](#12-c17--generate-one-optimal-diff)
- [13. Common Mistakes](#13-common-mistakes)
- [14. Recognition Checklist](#14-recognition-checklist)
- [15. Final Mental Model](#15-final-mental-model)

---

# 0. Prerequisites

Before this problem, know these ideas.

## 0.1 Strings and indices

For:

```text
X = A A T U
```

indices are:

```text
index:  0   1   2   3
X:      A   A   T   U
```

If:

```text
i = 1
```

then:

```text
X[i...] = A T U
```

ASCII:

```text
X:

0   1   2   3
A   A   T   U
    ^
    i
    |--------->
      remaining
```

---

## 0.2 Suffix DP

A suffix DP asks:

```text
What is the best answer
using only the unprocessed part?
```

For two strings:

```text
X[i...]
Y[j...]
```

we need two indices:

```text
i
j
```

---

## 0.3 Form 3 — Multi-Sequence DP

Use Form 3 when:

```text
multiple sequences
+
each sequence can move independently
```

Mental picture:

```text
X:

[ ][ ][i][ ][ ][ ]
       |
       v
    remaining X


Y:

[ ][j][ ][ ][ ][ ]
    |
    v
 remaining Y
```

So the state naturally becomes:

```text
dp(i,j)
```

---

## 0.4 Minimization DP

This problem asks for the:

```text
smallest possible diff length
```

So initialize:

```text
ans = INF
```

and use:

```text
min(...)
```

not:

```text
max(...)
```

---

## 0.5 Memoization

The same `(i,j)` can be reached through different paths.

Without memoization:

```text
          dp(i,j)
          /     \
         /       \
   dp(i+1,j)   dp(i,j+1)
        \         /
         \       /
        repeated states
```

Memoization stores each answer once.

---

## 0.6 Reconstruction / Backtracking

Sometimes DP must return not only:

```text
minimum value
```

but also:

```text
one actual optimal sequence of choices
```

So while computing DP, store which transition won.

Example:

```text
back[i][j] = 0
-> delete X[i]

back[i][j] = 1
-> insert Y[j]

back[i][j] = 2
-> keep matching character
```

Later we follow these decisions from:

```text
(0,0)
```

until both strings are finished.

---

# 1. Understand the Problem

We have two strings:

```text
X
Y
```

We want to describe how to transform `X` into `Y`.

The diff output can contain three kinds of tokens.

---

## 1.1 Delete from X

If current character of `X` should disappear:

```text
-X[i]
```

Example:

```text
-A
```

Meaning:

```text
delete A from X
```

Only `X` moves.

```text
(i,j)
   |
   v
(i+1,j)
```

---

## 1.2 Insert from Y

If current character of `Y` must be added:

```text
+Y[j]
```

Example:

```text
+A
```

Meaning:

```text
insert A
```

Only `Y` moves.

```text
(i,j)
   |
   v
(i,j+1)
```

---

## 1.3 Keep a matching character

If:

```text
X[i] == Y[j]
```

we can keep that character unchanged.

Example:

```text
X[i] = T
Y[j] = T
```

Output:

```text
T
```

Both strings move:

```text
(i,j)
   |
   v
(i+1,j+1)
```

---

## 1.4 Every emitted token costs 1

Each decision adds one token to the diff output.

```text
-A      -> +1
+A      -> +1
A       -> +1
```

Therefore the objective is:

```text
minimize number of output tokens
```

---

# 2. Why This Is Form 3

We are simultaneously processing:

```text
X
Y
```

The two pointers do not always move together.

Example:

```text
delete X[i]
-> i moves
-> j stays

insert Y[j]
-> j moves
-> i stays

match
-> both move
```

ASCII:

```text
                     STATE
                    (i , j)
                       |
          +------------+------------+
          |            |            |
          v            v            v
       DELETE        INSERT        MATCH
          |            |          if equal
          v            v            v
      (i+1,j)       (i,j+1)     (i+1,j+1)
```

That independent movement is the main Form-3 signal.

---

# 3. Decide the State

Let:

```text
n = X.length()
m = Y.length()
```

Define:

```text
dp(i,j)
=
minimum length of an optimal diff string
for

X[i ... n-1]
and
Y[j ... m-1]
```

This is the exact mental picture:

```text
X:

0 1 2 ... i ........ n-1
          ^
          |
       start here
          |------------>
            X[i...]


Y:

0 1 ... j ........... m-1
        ^
        |
     start here
        |-------------->
          Y[j...]
```

The answer to the whole problem is:

```text
dp(0,0)
```

---

## 3.1 Why this state is enough

At `(i,j)`:

```text
everything before i in X
and
everything before j in Y
```

has already been handled.

The future only depends on:

```text
X[i...]
Y[j...]
```

So no additional history is needed.

---

# 4. Transition Design

At state:

```text
dp(i,j)
```

there are at most three transitions.

---

## 4.1 Transition 1 — Delete X[i]

Condition:

```text
i < n
```

Output token:

```text
-X[i]
```

Then solve:

```text
dp(i+1,j)
```

Cost:

```text
1 + dp(i+1,j)
```

ASCII:

```text
X:

... [X[i]] [ ][ ][ ]
       |
       | delete
       v
       -X[i]

Y pointer does not move


(i,j)
  |
  v
(i+1,j)
```

Formula:

```text
deleteCost
=
1 + dp(i+1,j)
```

---

## 4.2 Transition 2 — Insert Y[j]

Condition:

```text
j < m
```

Output token:

```text
+Y[j]
```

Then solve:

```text
dp(i,j+1)
```

Cost:

```text
1 + dp(i,j+1)
```

ASCII:

```text
Y:

... [Y[j]] [ ][ ][ ]
       |
       | insert
       v
       +Y[j]

X pointer does not move


(i,j)
  |
  v
(i,j+1)
```

Formula:

```text
insertCost
=
1 + dp(i,j+1)
```

---

## 4.3 Transition 3 — Keep matching character

Allowed only when:

```text
i < n
j < m
X[i] == Y[j]
```

Output:

```text
X[i]
```

Both indices move.

```text
(i,j)
   |
   v
(i+1,j+1)
```

Cost:

```text
1 + dp(i+1,j+1)
```

ASCII:

```text
X: ... [A] ...
          |
          | equal
          |
Y: ... [A] ...
          |
          v
        keep A
          |
          v
move both pointers
```

Formula:

```text
matchCost
=
1 + dp(i+1,j+1)
```

---

## 4.4 Why there is no direct replacement transition

The lecture formulation uses:

```text
delete
insert
keep if equal
```

If characters differ, transformation can be represented through:

```text
delete old character
+
insert new character
```

So the DP does not require a separate replacement operation.

---

# 5. Base Case

When are we completely finished?

Only when:

```text
i == n
AND
j == m
```

Then:

```text
nothing remains in X
nothing remains in Y
```

So:

```text
dp(n,m) = 0
```

ASCII:

```text
X: [processed completely] |
                           i=n

Y: [processed completely] |
                           j=m

nothing left
    |
    v
return 0
```

---

## 5.1 What if only X is finished?

Suppose:

```text
i == n
j < m
```

There are still characters in `Y`.

They must be inserted:

```text
+Y[j]
+Y[j+1]
...
```

The normal insert transition already handles this.

---

## 5.2 What if only Y is finished?

Suppose:

```text
j == m
i < n
```

Remaining `X` characters must be deleted:

```text
-X[i]
-X[i+1]
...
```

The normal delete transition handles this.

So we only need one terminal base case:

```text
if (i == n && j == m)
    return 0;
```

---

# 6. Memoization

Before computing transitions:

```text
if dp[i][j] already exists
    return it
```

Recursive structure:

```text
rec(i,j)
   |
   +--> Base Case
   |
   +--> Cache Check
   |
   +--> Compute minimum
   |
   +--> Save and Return
```

Use your fixed recursive order:

```text
1. Pruning
2. Base Case
3. Cache Check
4. Compute / Transition
5. Save and Return
```

For this problem:

```text
Pruning
-> none

Base Case
-> i==n && j==m

Cache Check
-> dp[i][j] != -1

Compute
-> delete
-> insert
-> match if equal

Save and Return
-> dp[i][j] = ans
```

---

# 7. Complete Recurrence

```text
dp(i,j)
=
minimum of available choices
```

More explicitly:

```text
ans = INF
```

If:

```text
i < n
```

then:

```text
ans = min(
    ans,
    1 + dp(i+1,j)
)
```

If:

```text
j < m
```

then:

```text
ans = min(
    ans,
    1 + dp(i,j+1)
)
```

If:

```text
i < n
j < m
X[i] == Y[j]
```

then:

```text
ans = min(
    ans,
    1 + dp(i+1,j+1)
)
```

ASCII recurrence:

```text
                            dp(i,j)
                               |
             +-----------------+-----------------+
             |                 |                 |
             v                 v                 v
        delete X[i]        insert Y[j]          match
             |                 |              if equal
             v                 v                 |
      1+dp(i+1,j)       1+dp(i,j+1)             v
                                      1+dp(i+1,j+1)

                    take MINIMUM
```

---

# 8. Small Dry Run

Use the lecture example:

```text
X = A A T U
Y = A T U A
```

We want one shortest diff representation.

---

## 8.1 Start

```text
i = 0
j = 0

X[i] = A
Y[j] = A
```

They match.

So a strong choice is:

```text
keep A
```

State moves:

```text
(0,0)
  |
  | A
  v
(1,1)
```

---

## 8.2 Next state

Now:

```text
X[1] = A
Y[1] = T
```

They do not match.

Choices:

```text
delete X[1]
or
insert Y[1]
```

The optimal diff shown in the lecture can delete this extra `A`.

Output:

```text
-A
```

Move:

```text
(1,1)
  |
  |-A
  v
(2,1)
```

---

## 8.3 Now T matches T

```text
X[2] = T
Y[1] = T
```

Keep:

```text
T
```

Move both:

```text
(2,1)
  |
  |T
  v
(3,2)
```

---

## 8.4 U matches U

```text
X[3] = U
Y[2] = U
```

Keep:

```text
U
```

Move:

```text
(3,2)
  |
  |U
  v
(4,3)
```

---

## 8.5 X is finished but Y has A remaining

```text
i = 4 = n
j = 3 < m
```

So insert:

```text
+A
```

Move:

```text
(4,3)
  |
  |+A
  v
(4,4)
```

Finished.

---

## 8.6 One optimal diff

```text
A  -A  T  U  +A
```

Length:

```text
5 tokens
```

ASCII transformation:

```text
X = A A T U
    | | | |
    | - | |
    |   | |
    A   T U
            \
             +A

Diff:

A  -A  T  U  +A

Result:
A T U A
```

The lecture also shows that more than one optimal diff may exist.

For example, the other identical `A` can be deleted instead.

The problem only needs:

```text
generate one optimal diff
```

---

# 9. Time Complexity

Let:

```text
|X| = N
|Y| = M
```

Possible states:

```text
(i,j)

i -> 0...N
j -> 0...M
```

Number of states:

```text
O(N × M)
```

Each state tries at most:

```text
3 transitions
```

So:

```text
O(N × M × 3)
=
O(N × M)
```

ASCII:

```text
#states
=
N × M

work/state
=
O(1)

total
=
O(N × M)
```

Space:

```text
dp table:
O(N × M)
```

If reconstruction is stored:

```text
back table:
O(N × M)
```

---

# 10. Reconstruct One Optimal Diff

Computing:

```text
dp(0,0)
```

only gives the minimum length.

But the problem wants:

```text
one actual diff string
```

So while computing DP, remember which transition produced the optimum.

---

## 10.1 Back choices

Use:

```text
0 -> delete X[i]
1 -> insert Y[j]
2 -> keep matching character
```

ASCII:

```text
back[i][j]

0
|
+--> print -X[i]
     move (i+1,j)


1
|
+--> print +Y[j]
     move (i,j+1)


2
|
+--> print X[i]
     move (i+1,j+1)
```

---

## 10.2 Save the decision during DP

For delete:

```text
candidate
=
1 + rec(i+1,j)

if candidate improves ans:
    ans = candidate
    back[i][j] = 0
```

For insert:

```text
candidate
=
1 + rec(i,j+1)

if candidate improves ans:
    ans = candidate
    back[i][j] = 1
```

For matching:

```text
if X[i] == Y[j]:

    candidate
    =
    1 + rec(i+1,j+1)

    if candidate improves ans:
        ans = candidate
        back[i][j] = 2
```

---

## 10.3 Generate from `(0,0)`

Start:

```text
generate(0,0)
```

Read:

```text
back[i][j]
```

Print the corresponding token.

Move to the next state.

Repeat until:

```text
i == n
AND
j == m
```

ASCII:

```text
(0,0)
  |
  v
read back[0][0]
  |
  v
print one token
  |
  v
move to next state
  |
  v
read back[next]
  |
  v
...
  |
  v
(n,m)
```

---

# 11. C++17 — Minimum Diff Length

This is the DP first, without reconstruction.

```cpp
#include <bits/stdc++.h>
using namespace std;

string x, y;
int n, m;

vector<vector<int>> dp;

int rec(int i, int j) {
    // Pruning
    // None

    // Base Case
    if (i == n && j == m) {
        return 0;
    }

    // Cache Check
    if (dp[i][j] != -1) {
        return dp[i][j];
    }

    // Compute / Transition
    int ans = 1e9;

    if (i < n) {
        ans = min(ans, 1 + rec(i + 1, j));
    }

    if (j < m) {
        ans = min(ans, 1 + rec(i, j + 1));
    }

    if (i < n && j < m && x[i] == y[j]) {
        ans = min(ans, 1 + rec(i + 1, j + 1));
    }

    // Save and Return
    return dp[i][j] = ans;
}

int main() {
    cin >> x >> y;

    n = x.size();
    m = y.size();

    dp.assign(n + 1, vector<int>(m + 1, -1));

    cout << rec(0, 0) << '\n';
}
```

---

# 12. C++17 — Generate One Optimal Diff

Now store the winning transition.

```cpp
#include <bits/stdc++.h>
using namespace std;

string x, y;
int n, m;

vector<vector<int>> dp;
vector<vector<int>> backChoice;

int rec(int i, int j) {
    // Pruning
    // None

    // Base Case
    if (i == n && j == m) {
        return 0;
    }

    // Cache Check
    if (dp[i][j] != -1) {
        return dp[i][j];
    }

    // Compute / Transition
    int ans = 1e9;

    if (i < n) {
        int candidate = 1 + rec(i + 1, j);

        if (candidate < ans) {
            ans = candidate;
            backChoice[i][j] = 0;
        }
    }

    if (j < m) {
        int candidate = 1 + rec(i, j + 1);

        if (candidate < ans) {
            ans = candidate;
            backChoice[i][j] = 1;
        }
    }

    if (i < n && j < m && x[i] == y[j]) {
        int candidate = 1 + rec(i + 1, j + 1);

        if (candidate < ans) {
            ans = candidate;
            backChoice[i][j] = 2;
        }
    }

    // Save and Return
    return dp[i][j] = ans;
}

void generate(int i, int j) {
    // Natural terminal guard for reconstruction.
    if (i == n && j == m) {
        return;
    }

    int choice = backChoice[i][j];

    if (choice == 0) {
        cout << '-' << x[i] << ' ';
        generate(i + 1, j);
    } else if (choice == 1) {
        cout << '+' << y[j] << ' ';
        generate(i, j + 1);
    } else {
        cout << x[i] << ' ';
        generate(i + 1, j + 1);
    }
}

int main() {
    cin >> x >> y;

    n = x.size();
    m = y.size();

    dp.assign(n + 1, vector<int>(m + 1, -1));
    backChoice.assign(n + 1, vector<int>(m + 1, -1));

    rec(0, 0);
    generate(0, 0);

    cout << '\n';
}
```

> The lecture screenshots show the reconstruction branches using the stored `back` choice.  
> The explicit terminal guard in `generate()` above is included so the standalone version stops safely when both strings are exhausted.

---

# 13. Common Mistakes

## 13.1 Using only one index

Wrong:

```text
dp(i)
```

because `X` and `Y` can move independently.

Correct:

```text
dp(i,j)
```

---

## 13.2 Maximizing instead of minimizing

This is not:

```text
longest subsequence
```

The state asks for:

```text
minimum diff length
```

So use:

```text
min
```

---

## 13.3 Matching unequal characters

The keep transition is valid only when:

```text
X[i] == Y[j]
```

Otherwise use delete or insert.

---

## 13.4 Moving both pointers on delete

Delete:

```text
-X[i]
```

means:

```text
i++
j unchanged
```

---

## 13.5 Moving both pointers on insert

Insert:

```text
+Y[j]
```

means:

```text
j++
i unchanged
```

---

## 13.6 Forgetting the +1 cost

Each output token contributes:

```text
1
```

So transitions are:

```text
1 + rec(...)
```

not just:

```text
rec(...)
```

---

## 13.7 Stopping when only one string ends

Wrong:

```text
if (i == n || j == m)
    return 0;
```

Why?

If one string still has characters, those characters must still be inserted or deleted.

Correct terminal condition:

```text
i == n && j == m
```

---

## 13.8 Computing the minimum but not reconstructing

If the problem asks for the actual diff:

```text
dp table alone
```

is not enough.

Store:

```text
backChoice[i][j]
```

while computing the optimum.

---

# 14. Recognition Checklist

Think of this Form-3 pattern when you see:

```text
two sequences
+
transform / align / compare
+
each pointer can move independently
```

Ask:

```text
1. What does i represent?
   -> current position in X

2. What does j represent?
   -> current position in Y

3. What does dp(i,j) mean?
   -> best answer for X[i...] and Y[j...]

4. Can I consume only X[i]?
   -> delete

5. Can I consume only Y[j]?
   -> insert

6. Can I consume both?
   -> only when characters match

7. Is this min or max DP?
   -> minimize diff length

8. What is the true terminal state?
   -> both strings exhausted

9. Need actual solution?
   -> store back choice

10. Complexity?
    -> O(N*M)
```

---

# 15. Final Mental Model

Problem:

```text
X suffix + Y suffix
        |
        v
     dp(i,j)
```

Transitions:

```text
                        dp(i,j)
                           |
           +---------------+---------------+
           |               |               |
           v               v               v
      DELETE X[i]      INSERT Y[j]        KEEP
           |               |            if equal
           v               v               v
      -X[i]            +Y[j]             X[i]
           |               |               |
           v               v               v
     dp(i+1,j)       dp(i,j+1)      dp(i+1,j+1)

                each transition costs 1

                         |
                         v
                       MIN
```

Reconstruction:

```text
DP computes:
minimum length

BACK table remembers:
which transition won

GENERATE follows:
back[0][0]
   |
   v
back[next state]
   |
   v
...
   |
   v
(n,m)
```

The one sentence to remember:

```text
Different Strings is Form-3 DP:
dp(i,j) is the minimum diff length for the two remaining suffixes,
with transitions delete X[i], insert Y[j], or keep both when equal.
```
