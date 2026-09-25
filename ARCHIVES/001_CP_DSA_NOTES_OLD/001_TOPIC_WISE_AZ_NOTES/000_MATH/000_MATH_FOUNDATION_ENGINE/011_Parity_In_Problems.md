# 011_Parity_In_Problems.md

# Parity In Problems For Competitive Programming

---

# 1. Introduction

The previous chapter explained:

```text
What parity is
```

This chapter focuses on:

```text
How parity solves real CP problems
```

Parity is one of the MOST powerful hidden observations in:
- adhoc
- constructive
- greedy
- games
- graph coloring
- bitwise
- transformations

Strong contestants constantly check:

```text
Is parity preserved?
```

before doing anything else.

---

# 2. Core Parity Mindset

Many problems reduce to:

```text
Only odd/even matters
```

instead of actual values.

This massively simplifies complexity.

---

# 3. Most Important Observation

Operations often preserve parity.

Examples:
- +2
- -2
- XOR same bit
- swapping positions
- pair removals

This creates:

```text
Parity Invariants
```

---

# 4. Transformation Problems

Very common CP pattern.

Problem type:

```text
Can we transform A → B?
```

Observation:

```text
Check parity first.
```

---

# Example

Operation:

```text
+2 only
```

Can:

```text
2 → 8
```

Yes.

Can:

```text
2 → 7
```

No.

Why?

```text
Parity preserved.
```

---

# 5. Array Sum Parity

Important observation:

```text
Sum parity depends on count of odd numbers.
```

Rules:

```text
Even count of odd numbers
→ sum even

Odd count of odd numbers
→ sum odd
```

---

# Example

```text
1 3 5
```

3 odd numbers.

Sum:

```text
9
```

Odd.

---

# 6. Pair Cancellation Problems

Classic parity pattern.

Example:

```text
Every number appears twice except one.
```

Observation:

```text
Pairs cancel.
```

Using XOR:

```text
x ^ x = 0
```

---

# 7. Grid Coloring Problems

Chessboard coloring:

```text
black white black white
```

Parity determines color.

Formula:

```text
(x + y) % 2
```

Used in:
- BFS
- graph coloring
- bipartite graphs

---

# 8. Parity In Games

Many game problems reduce to:

```text
odd moves
vs
even moves
```

Example:

Remove 1 stone per turn.

If:
```text
n odd
```

First player wins.

Else:
```text
second player wins
```

---

# 9. Parity In Swaps

Important observation:

```text
Every swap changes permutation parity.
```

Used in:
- inversion count
- permutation puzzles
- graph transformations

---

# 10. Parity In Graphs

Bipartite graph:

```text
2-color graph
```

based on alternating parity levels.

BFS layers:

```text
even depth
odd depth
```

Very important in:
- graph coloring
- shortest path parity
- cycle detection

---

# 11. Parity In Binary

Parity corresponds to:

```text
least significant bit
```

Example:

```text
Even → last bit 0
Odd  → last bit 1
```

Thus:

```cpp
n & 1
```

checks parity quickly.

---

# 12. CP / FAANG Problem Forms

---

# Form 1 — Reachability Using +2

## Problem

Can transform:

```text
a → b
```

using only:
```text
+2
```

---

## Observation

Parity invariant.

---

## Step-by-Step Working

Example:

```text
2 → 4 → 6 → 8
```

Always even.

Cannot reach odd.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool reachable(int a, int b) {

    return (a % 2)
        == (b % 2);
}
```

---

# Form 2 — Sum Parity

## Problem

Determine if array sum odd.

---

## Observation

Only odd-count parity matters.

---

## Step-by-Step Working

Example:

```text
1 2 3
```

Odd numbers:

```text
1 and 3
```

2 odd numbers.

Even count.

Thus:
```text
sum even
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool oddSum(vector<int>& a) {

    long long sum = 0;

    for (int x : a) {

        sum += x;
    }

    return sum % 2 == 1;
}
```

---

# Form 3 — Single Number XOR

## Problem

Every element appears twice except one.

Find unique number.

---

## Observation

Pairs cancel:

```text
x ^ x = 0
```

---

## Step-by-Step Working

Example:

```text
2 3 2 4 4
```

XOR:

```text
2^2 = 0
4^4 = 0
```

Remaining:

```text
3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int singleNumber(vector<int>& a) {

    int x = 0;

    for (int v : a) {

        x ^= v;
    }

    return x;
}
```

---

# Form 4 — Chessboard Coloring

## Problem

Determine color of cell.

---

## Observation

Parity of:

```text
x + y
```

---

## Step-by-Step Working

Example:

```text
(1,1)
→ even parity

(1,2)
→ odd parity
```

Alternating colors.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string color(int x, int y) {

    if ((x + y) % 2 == 0)
        return "BLACK";

    return "WHITE";
}
```

---

# Form 5 — Stone Game

## Problem

Players remove 1 stone alternately.

Who wins?

---

## Observation

Parity of moves.

---

## Step-by-Step Working

If:
```text
n odd
```

First player takes last move.

Else:
```text
second player wins
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string winner(int n) {

    if (n % 2 == 1)
        return "FIRST";

    return "SECOND";
}
```

---

# Form 6 — Count Odd Numbers

## Problem

Count odd numbers from 1 to n.

---

## Observation

Every alternate number odd.

---

## Formula

```text
(n + 1) / 2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long oddCount(long long n) {

    return (n + 1) / 2;
}
```

---

# Form 7 — Bipartite Graph

## Problem

Check graph bipartite.

---

## Observation

Alternate parity levels.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool bipartite(
    int n,
    vector<vector<int>>& adj
) {

    vector<int> color(n, -1);

    queue<int> q;

    color[0] = 0;

    q.push(0);

    while (!q.empty()) {

        int u = q.front();

        q.pop();

        for (int v : adj[u]) {

            if (color[v] == -1) {

                color[v] =
                    color[u] ^ 1;

                q.push(v);

            } else if (
                color[v] == color[u]
            ) {

                return false;
            }
        }
    }

    return true;
}
```

---

# 13. Real World Applications

| Real System | Usage |
|---|---|
| Networking | parity bits |
| Error correction | checksum parity |
| Graph systems | bipartite matching |
| Databases | hashing parity |
| Distributed systems | binary partitioning |
| Hardware | parity memory |
| Graphics | alternating grids |

---

# 14. Real Engineering Insight

Parity often means:

```text
State compression
```

Complex systems reduce to:

```text
2-state logic
```

This simplification is powerful.

---

# 15. Decision Tree

```text
Odd/even involved?
→ parity

Transformation operations?
→ invariant check

Pairs cancel?
→ XOR parity

Chessboard/grid?
→ coordinate parity

Alternating turns?
→ move parity

BFS layers?
→ parity coloring
```

---

# 16. Common Traps

```text
1. Forgetting invariant
2. Using brute force unnecessarily
3. Negative modulo confusion
4. Missing XOR cancellation
5. Wrong parity count
6. Confusing parity with divisibility
7. Incorrect chessboard coloring
8. Wrong game parity logic
```

---

# 17. Final Checklist

Before solving:

```text
1. Does odd/even matter?
2. Is parity preserved?
3. Can parity prove impossible?
4. Is XOR involved?
5. Is there alternating behavior?
6. Is there 2-coloring?
7. Is game move parity important?
8. Can states reduce to 2?
```

---

# 18. Final Mental Shortcut

```text
Parity In Problems
=
Invariants
+
2-State Thinking
+
Cancellation
+
Alternation
```
