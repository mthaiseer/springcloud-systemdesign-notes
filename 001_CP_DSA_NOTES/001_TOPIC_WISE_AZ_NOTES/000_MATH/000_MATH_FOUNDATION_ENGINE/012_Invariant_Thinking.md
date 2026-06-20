# 012_Invariant_Thinking.md

# Invariant Thinking For Competitive Programming

---

# 1. Introduction

Invariant Thinking is one of the MOST powerful hidden skills in CP.

Strong contestants constantly ask:

```text
What never changes?
```

That unchanging property is called:

```text
Invariant
```

Many hard problems become simple once the invariant is discovered.

Invariant thinking appears heavily in:
- adhoc
- constructive
- greedy
- games
- graph transformations
- parity problems
- number theory

---

# 2. What Is An Invariant?

Invariant means:

```text
A property that remains unchanged
after operations.
```

Example:

Operation:
```text
+2
```

Parity never changes.

Thus:
```text
odd/even
```

is invariant.

---

# 3. Why Invariants Are Powerful

Invariants help:
- prove impossible cases
- reduce complexity
- avoid brute force
- derive greedy proofs
- solve games
- solve transformations

Very often:

```text
If invariant differs
→ transformation impossible
```

---

# 4. Most Common Invariants

| Invariant | Common Problems |
|---|---|
| parity | transformations/games |
| gcd | repeated subtraction |
| xor | pair cancellation |
| modulo | cyclic behavior |
| connected components | graph operations |
| sum parity | array operations |
| inversion parity | permutation swaps |

---

# 5. Parity Invariant

Classic example.

Operation:

```text
+2
```

Parity preserved.

Example:

```text
2 → 4 → 6 → 8
```

Always even.

Cannot reach odd.

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool reachable(int a, int b) {

    return (a % 2)
        == (b % 2);
}
```

---

# 6. GCD Invariant

Important observation:

```text
gcd(a,b)
=
gcd(a-b,b)
```

Repeated subtraction preserves gcd.

Used in:
- Euclid algorithm
- constructive
- games

---

# Example

```text
gcd(18,12)
=
gcd(6,12)
=
6
```

---

# 7. XOR Invariant

Properties:

```text
x ^ x = 0
x ^ 0 = x
```

Pairs cancel.

Very common in:
- single number
- bitmask
- parity games

---

# Example

```text
2 3 2 4 4
```

Remaining:

```text
3
```

---

# Code

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

# 8. Sum Invariants

Some operations preserve total sum.

Example:

```text
move 1 from A to B
```

Total sum unchanged.

Useful in:
- balancing problems
- equalization
- simulations

---

# 9. Modulo Invariants

Operations may preserve modulo.

Example:

```text
+3 repeatedly
```

Modulo 3 remains same.

Used in:
- cyclic behavior
- constructive
- games

---

# 10. Graph Connectivity Invariant

Adding/removing certain edges may preserve:
- connected components
- parity of degree
- bipartiteness

Very common in graph problems.

---

# 11. Chessboard Coloring Invariant

Grid color determined by:

```text
(x+y)%2
```

Moves may preserve or flip parity.

Used heavily in:
- BFS
- shortest path
- knight problems

---

# 12. Invariants In Games

Most game problems reduce to:

```text
What property always remains?
```

Example:
- parity
- xor
- modulo

Very important in:
- Nim
- impartial games
- turn games

---

# 13. Constructive Problems And Invariants

Constructive problems often solved by:

```text
Identify invariant
→ prove impossible/possible
```

This is one of the strongest CP techniques.

---

# 14. CP / FAANG Problem Forms

---

# Form 1 — Parity Transformation

## Problem

Can transform:

```text
a → b
```

using:
```text
+2 only
```

---

## Observation

Parity invariant.

---

## Step-by-Step Working

Example:

```text
2 → 4 → 6
```

Always even.

Cannot reach odd.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool possible(int a, int b) {

    return (a % 2)
        == (b % 2);
}
```

---

# Form 2 — Single Number XOR

## Problem

Every number appears twice except one.

---

## Observation

Pairs cancel.

Invariant:

```text
x ^ x = 0
```

---

## Step-by-Step Working

```text
2 3 2 4 4

2^2 = 0
4^4 = 0

Remaining = 3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int uniqueValue(vector<int>& a) {

    int x = 0;

    for (int v : a) {

        x ^= v;
    }

    return x;
}
```

---

# Form 3 — Sum Preservation

## Problem

Redistribute values between boxes.

Can total sum change?

---

## Observation

Moving values preserves sum.

Invariant:
```text
total sum
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long totalSum(vector<int>& a) {

    long long sum = 0;

    for (int x : a) {

        sum += x;
    }

    return sum;
}
```

---

# Form 4 — GCD Invariant

## Problem

Repeatedly subtract smaller from larger.

Find final value.

---

## Observation

GCD invariant.

Final value:

```text
gcd(a,b)
```

---

## Step-by-Step Working

Example:

```text
18 12

18-12 = 6

12-6 = 6

6-6 = 0
```

Final non-zero:
```text
6
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int finalValue(int a, int b) {

    return gcd(a, b);
}
```

---

# Form 5 — Chessboard Coloring

## Problem

Determine reachable color.

---

## Observation

Coordinate parity invariant.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string cellColor(int x, int y) {

    if ((x + y) % 2 == 0)
        return "BLACK";

    return "WHITE";
}
```

---

# Form 6 — Game Winner

## Problem

Players remove 1 stone alternately.

Who wins?

---

## Observation

Move parity invariant.

---

## Step-by-Step Working

Odd stones:
```text
first wins
```

Even stones:
```text
second wins
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

# Form 7 — Modulo Transformation

## Problem

Can reach target using +3 operations?

---

## Observation

Modulo 3 invariant.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool reachableMod3(int a, int b) {

    return (a % 3)
        == (b % 3);
}
```

---

# 15. Real World Applications

| Real System | Invariant |
|---|---|
| Databases | consistency constraints |
| Networking | checksum parity |
| Distributed systems | replication invariants |
| Blockchain | ledger invariants |
| Graphics | coordinate parity |
| Compression | frequency preservation |
| Game engines | state invariants |

---

# 16. Real Engineering Insight

Strong engineers constantly think:

```text
What property must NEVER break?
```

That is invariant thinking.

This applies heavily in:
- distributed systems
- databases
- transactions
- fault tolerance

---

# 17. Decision Tree

```text
Transformation problem?
→ check invariant

Repeated operations?
→ what remains unchanged?

Impossible cases?
→ compare invariants

Pair cancellation?
→ XOR invariant

Repeated subtraction?
→ GCD invariant

Alternating states?
→ parity invariant
```

---

# 18. Common Traps

```text
1. Missing hidden invariant
2. Brute force simulation
3. Confusing changing value with invariant
4. Ignoring parity preservation
5. Missing modulo preservation
6. Wrong XOR reasoning
7. Forgetting gcd invariant
8. Assuming operations arbitrary
```

---

# 19. Final Checklist

Before solving:

```text
1. What never changes?
2. Does operation preserve parity?
3. Does gcd remain same?
4. Does modulo remain same?
5. Does XOR cancel?
6. Does sum remain constant?
7. Can invariant prove impossible?
8. Can invariant reduce complexity?
```

---

# 20. Final Mental Shortcut

```text
Invariant Thinking
=
Find What Never Changes
→ Simplify Problem
→ Prove Impossible/Possible
```
