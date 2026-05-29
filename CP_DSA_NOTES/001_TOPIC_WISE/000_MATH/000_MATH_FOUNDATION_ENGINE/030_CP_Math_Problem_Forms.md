# 030_CP_Math_Problem_Forms.md

# CP Math Problem Forms For Competitive Programming

---

# 1. Introduction

This file summarizes the MOST IMPORTANT mathematical problem forms used in CP.

Goal:

```text
Recognize Math Patterns Fast
```

Strong contestants do NOT memorize random formulas.

They recognize:

```text
problem forms
+
hidden observations
+
mathematical reductions
```

This file acts as:
- quick revision
- interview prep
- contest pattern guide
- FAANG math observation handbook

---

# 2. Core CP Math Mindset

Strong contestants constantly ask:

```text
1. Is there hidden math?
2. Can brute force reduce mathematically?
3. Is parity involved?
4. Is modulo repeating?
5. Can counting simplify?
6. Is invariant hidden?
7. Is binary math involved?
8. Can geometry become algebra?
```

This mindset is critical.

---

# 3. Most Important CP Math Forms

---

# FORM 1 — Parity Problems

## Recognition Signals

```text
1. odd/even operations
2. +2 / -2 operations
3. swap parity
4. coloring
5. alternating patterns
```

---

## Core Observation

Parity remains invariant.

---

## Example

Can number become:
```text
odd from even
```

using:
```text
+2 operations
```

Answer:
```text
NO
```

Parity unchanged.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool sameParity(
    int a,
    int b
) {

    return (a % 2)
        == (b % 2);
}
```

---

# FORM 2 — GCD Problems

## Recognition Signals

```text
1. divisibility
2. repeated subtraction
3. common factors
4. pair reductions
5. synchronization
```

---

## Core Observation

GCD captures:
```text
shared divisibility structure
```

---

## Example

Repeated subtraction eventually reaches:
```text
gcd(a,b)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int gcdValue(
    int a,
    int b
) {

    while (b) {

        a %= b;

        swap(a, b);
    }

    return a;
}
```

---

# FORM 3 — Modulo Cycle Problems

## Recognition Signals

```text
1. huge exponent
2. repeating pattern
3. cyclic states
4. periodic behavior
```

---

## Core Observation

Modulo creates:
```text
finite states
→ cycles
```

---

## Example

Last digit of:
```text
2^100
```

Cycle:

```text
2 4 8 6
```

Length:
```text
4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int lastDigit2(long long n) {

    vector<int> cycle =
        {6,2,4,8};

    return cycle[n % 4];
}
```

---

# FORM 4 — Invariant Problems

## Recognition Signals

```text
1. operations repeated
2. transformations
3. impossible states
4. games
5. swaps
```

---

## Core Observation

Something remains unchanged.

---

## Example

Sum parity invariant.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool invariant(
    int before,
    int after
) {

    return (before % 2)
        == (after % 2);
}
```

---

# FORM 5 — Counting Problems

## Recognition Signals

```text
1. subsets
2. arrangements
3. selections
4. combinations
5. distributions
```

---

## Core Observation

Count systematically.

---

## Example

Subsets of:
```text
n elements
```

Answer:

```text
2^n
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long subsets(
    int n
) {

    return 1LL << n;
}
```

---

# FORM 6 — Pigeonhole Problems

## Recognition Signals

```text
1. duplicates guaranteed
2. collisions
3. modulo classes
4. repeated states
```

---

## Core Observation

More objects than states.

---

## Example

13 people:
```text
same birth month guaranteed
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool collision(
    int objects,
    int boxes
) {

    return objects > boxes;
}
```

---

# FORM 7 — Inclusion Exclusion

## Recognition Signals

```text
1. OR conditions
2. overlapping sets
3. duplicate counting
4. divisibility unions
```

---

## Core Observation

Subtract overlaps.

---

## Example

Count divisible by:
```text
2 or 3
```

Formula:

```text
count(2)
+
count(3)
-
count(6)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long count23(
    long long n
) {

    return n/2
         + n/3
         - n/6;
}
```

---

# FORM 8 — Probability Problems

## Recognition Signals

```text
1. random choices
2. expected outcomes
3. games
4. probabilities
5. selections
```

---

## Core Observation

Probability:

```text
favorable / total
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double probability(
    int favorable,
    int total
) {

    return (double) favorable
         / total;
}
```

---

# FORM 9 — Expected Value Problems

## Recognition Signals

```text
1. average random outcome
2. repeated random process
3. expected score
4. stochastic process
```

---

## Core Observation

Weighted average.

---

## Example

Expected dice value:

```text
3.5
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double expectedDice() {

    return 3.5;
}
```

---

# FORM 10 — Conditional Probability

## Recognition Signals

```text
1. given condition
2. restricted sample space
3. dependent events
```

---

## Core Observation

Condition shrinks sample space.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double conditional(
    double intersection,
    double given
) {

    return intersection / given;
}
```

---

# FORM 11 — XOR Cancellation

## Recognition Signals

```text
1. pairs
2. odd occurrences
3. unique element
4. parity behavior
```

---

## Core Observation

Equal pairs cancel.

---

## Example

```text
2^3^2^4^4 = 3
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int singleNumber(
    vector<int>& a
) {

    int x = 0;

    for (int v : a)
        x ^= v;

    return x;
}
```

---

# FORM 12 — Power Of Two Problems

## Recognition Signals

```text
1. binary
2. shifts
3. masks
4. powers of 2
```

---

## Core Observation

Power of two:
```text
one set bit
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool power2(int n) {

    return n > 0
        && (n & (n - 1)) == 0;
}
```

---

# FORM 13 — Distance Problems

## Recognition Signals

```text
1. coordinates
2. nearest points
3. shortest path
4. geometry
```

---

## Core Observation

Pythagorean distance.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double distance2D(
    int x1,
    int y1,
    int x2,
    int y2
) {

    int dx = x2 - x1;
    int dy = y2 - y1;

    return sqrt(
        dx*dx + dy*dy
    );
}
```

---

# FORM 14 — Orientation Problems

## Recognition Signals

```text
1. turns
2. convex hull
3. geometry
4. collinearity
```

---

## Core Observation

Cross product sign.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long orientation(
    int x1,
    int y1,
    int x2,
    int y2,
    int x3,
    int y3
) {

    return 1LL * (x2-x1)
         * (y3-y1)

         -

           1LL * (y2-y1)
         * (x3-x1);
}
```

---

# FORM 15 — Binary Search On Answer

## Recognition Signals

```text
1. minimize maximum
2. maximize minimum
3. monotonic property
4. feasibility check
```

---

## Core Observation

Answer space monotonic.

---

## Example

Minimum speed problems.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool possible(int x) {

    return x >= 10;
}
```

---

# 4. Real World Applications

| Math Form | Real Usage |
|---|---|
| modulo | hashing |
| parity | distributed systems |
| probability | ML models |
| geometry | maps/navigation |
| XOR | encryption |
| expected value | finance |
| binary | compression |
| orientation | graphics |
| gcd | synchronization |
| counting | analytics |

---

# 5. Ultimate CP Observation Tree

```text
Repeated pattern?
→ modulo cycle

Odd/even?
→ parity

Pairs cancel?
→ XOR

Operations preserve something?
→ invariant

Overlapping counts?
→ inclusion-exclusion

Random outcomes?
→ probability

Average random result?
→ expected value

Coordinates?
→ geometry

Subsets?
→ counting / bitmask

Huge answer space?
→ binary search
```

---

# 6. Common Traps

```text
1. Missing hidden invariant
2. Ignoring modulo cycles
3. Floating-point precision
4. Double counting
5. Wrong parity assumption
6. Missing XOR cancellation
7. Overcomplicated brute force
8. Ignoring monotonicity
```

---

# 7. Final Checklist

Before solving:

```text
1. Is there hidden math?
2. Can brute force reduce?
3. Is modulo involved?
4. Is parity important?
5. Is counting easier?
6. Is geometry hidden?
7. Is XOR useful?
8. Is invariant hidden?
9. Is monotonicity present?
10. Can observations simplify?
```

---

# 8. Final Mental Shortcut

```text
Strong CP Math
=
Observation
+
Reduction
+
Pattern Recognition
+
Mathematical Simplification
```
