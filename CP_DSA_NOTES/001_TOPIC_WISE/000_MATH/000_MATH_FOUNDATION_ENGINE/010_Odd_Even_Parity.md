# 010_Odd_Even_Parity.md

# Odd Even Parity For Competitive Programming

---

# 1. Introduction

Parity is one of the MOST important observations in CP.

Parity means:

```text
Odd or Even behavior
```

Many seemingly difficult problems become easy after checking:
- odd/even
- parity preservation
- parity changes
- parity invariants

Parity appears heavily in:
- adhoc
- constructive
- greedy
- bitwise
- games
- graph coloring
- number theory

---

# 2. What Is Even Number?

A number is even if:

```text
n % 2 == 0
```

Examples:

```text
2 4 6 8 10
```

Important property:

```text
Even numbers divisible by 2.
```

---

# 3. What Is Odd Number?

A number is odd if:

```text
n % 2 == 1
```

Examples:

```text
1 3 5 7 9
```

---

# 4. Core Parity Rules

MOST IMPORTANT RULES:

---

# A. Even + Even

```text
even + even = even
```

Example:

```text
4 + 8 = 12
```

---

# B. Odd + Odd

```text
odd + odd = even
```

Example:

```text
3 + 5 = 8
```

---

# C. Odd + Even

```text
odd + even = odd
```

Example:

```text
7 + 2 = 9
```

---

# D. Even × Anything

```text
even × x = even
```

---

# E. Odd × Odd

```text
odd × odd = odd
```

---

# 5. Why Parity Is Powerful

Parity reduces huge values into:

```text
2 states only:
odd/even
```

This simplification often solves:
- impossible cases
- game problems
- transformations
- invariants

---

# 6. Parity And Binary

Least significant bit determines parity.

```text
Even → last bit = 0
Odd  → last bit = 1
```

Example:

```text
6 = 110
7 = 111
```

Thus:

```text
n & 1
```

checks parity.

---

# 7. Fast Odd/Even Check

---

# Modulo Method

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isEven(int n) {

    return n % 2 == 0;
}
```

---

# Bitwise Method

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isOdd(int n) {

    return n & 1;
}
```

---

# 8. Parity Invariants

Invariant means:

```text
Something never changes.
```

Parity often remains invariant after operations.

Example:

```text
Add 2 repeatedly
```

Parity never changes.

---

# 9. Example — Reachability Problem

Can we transform:

```text
2 → 7
```

using:
```text
+2 only
```

Observation:

```text
Parity preserved.
```

2 is even.
7 is odd.

Impossible.

---

# 10. Parity In Array Sum

Observation:

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

odd.

---

# 11. XOR And Parity

Very important.

Properties:

```text
x ^ x = 0
x ^ 0 = x
```

Parity-like cancellation appears.

Used in:
- missing number
- single number
- bitmasking

---

# 12. Parity In Chessboard / Graph

Chessboard coloring:

```text
black/white alternation
```

is parity.

Coordinates:

```text
(x+y)%2
```

determine color.

Used heavily in:
- BFS
- graph coloring
- bipartite graphs

---

# 13. Parity In Permutations

Swaps often change parity.

Important in:
- inversion count
- permutation games
- puzzles

---

# 14. CP / FAANG Problem Forms

---

# Form 1 — Check Odd Or Even

## Problem

Determine parity.

---

## Observation

Use modulo or bitwise.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool isEven(int n) {

    return n % 2 == 0;
}
```

---

# Form 2 — Sum Parity

## Problem

Determine if array sum odd.

---

## Observation

Track odd count parity.

---

## Step-by-Step Working

Example:

```text
1 2 3
```

Odd count:

```text
2
```

Even count of odd numbers.

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

# Form 3 — Reachability By +2

## Problem

Can transform a → b using +2 only?

---

## Observation

Parity invariant.

---

## Step-by-Step Working

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

    return (a % 2) == (b % 2);
}
```

---

# Form 4 — Count Odd Numbers

## Problem

Count odd numbers in range.

---

## Observation

Every alternate number odd.

---

## Formula

```text
(n + 1)/2
```

for:
```text
1 to n
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countOdd(long long n) {

    return (n + 1) / 2;
}
```

---

# Form 5 — Chessboard Coloring

## Problem

Determine board color.

---

## Observation

Parity of coordinates.

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

# Form 6 — XOR Single Number

## Problem

Every number appears twice except one.

Find unique number.

---

## Observation

Pairs cancel.

```text
x ^ x = 0
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

# Form 7 — Parity Game

## Problem

Players remove stones.
Determine winner.

---

## Observation

Often parity of moves determines winner.

---

## Example

If only remove 1 stone:

```text
even stones → second wins
odd stones → first wins
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

# 15. Real World Applications

| Real System | Usage |
|---|---|
| Error correction | parity bits |
| Networking | parity checks |
| Databases | checksum validation |
| Graphics | chessboard coloring |
| Cryptography | binary parity |
| Distributed systems | consistent hashing |
| Hardware | memory parity |

---

# 16. Real Engineering Insight

Parity usually means:

```text
State compression
```

Many complex systems reduce to:

```text
2-state behavior
```

This simplification is powerful.

---

# 17. Decision Tree

```text
Odd/even mentioned?
→ parity

Repeated +2/-2?
→ parity invariant

Chessboard/grid coloring?
→ parity coloring

XOR cancellation?
→ parity-like behavior

Transformation impossible?
→ parity check first
```

---

# 18. Common Traps

```text
1. Negative modulo confusion
2. Forgetting parity invariants
3. Using brute force unnecessarily
4. Missing XOR parity logic
5. Off-by-one in odd count
6. Confusing parity with divisibility
7. Overflow in sums
```

---

# 19. Final Checklist

Before solving:

```text
1. Is odd/even behavior important?
2. Is parity preserved?
3. Is transformation impossible due parity?
4. Is XOR involved?
5. Is chessboard coloring involved?
6. Can parity reduce states?
7. Is there invariant?
8. Is game outcome parity-based?
```

---

# 20. Final Mental Shortcut

```text
Parity
=
Odd/Even State
+
Invariants
+
Binary Thinking
+
2-State Compression
```
