# 031_FAANG_Math_Problem_Forms.md

# FAANG Math Problem Forms For Coding Interviews

---

# 1. Introduction

FAANG math problems are NOT olympiad-style mathematics.

Most FAANG math questions are actually:

```text
observation problems
+
optimization problems
+
pattern recognition problems
```

Strong candidates solve them by recognizing:

```text
1. parity
2. prefix math
3. modulo behavior
4. counting
5. binary math
6. invariants
7. geometry observations
8. probability intuition
```

This file focuses on:
- FAANG interview style
- practical math patterns
- optimization mindset
- hidden observations

---

# 2. FAANG Math Mindset

Strong interview candidates constantly ask:

```text
1. Can brute force reduce mathematically?
2. Is there hidden parity?
3. Is modulo repeating?
4. Is binary representation useful?
5. Can prefix math help?
6. Is there a counting shortcut?
7. Can geometry simplify?
8. Is there invariant behavior?
```

This mindset is critical.

---

# 3. Most Important FAANG Math Forms

---

# FORM 1 — Prefix Sum Math

## Recognition Signals

```text
1. subarray sums
2. ranges
3. cumulative operations
4. interval queries
```

---

## Core Observation

Transform repeated range queries into:

```text
O(1)
```

using:
```text
prefix accumulation
```

---

## Example

```text
sum(l..r)
=
prefix[r]
-
prefix[l-1]
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> prefixSum(
    vector<int>& a
) {

    int n = a.size();

    vector<int> p(n);

    p[0] = a[0];

    for (int i = 1;
         i < n;
         i++) {

        p[i] =
            p[i-1] + a[i];
    }

    return p;
}
```

---

# FORM 2 — Modulo Hashing Problems

## Recognition Signals

```text
1. divisible subarrays
2. cyclic behavior
3. remainder grouping
4. periodic states
```

---

## Core Observation

Equal remainders imply:
```text
divisible difference
```

---

## Example

Subarray divisible by:
```text
k
```

when:

```text
prefix[i] % k
==
prefix[j] % k
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool divisibleSubarray(
    vector<int>& a,
    int k
) {

    unordered_set<int> seen;

    int prefix = 0;

    seen.insert(0);

    for (int x : a) {

        prefix =
            (prefix + x) % k;

        if (seen.count(prefix))
            return true;

        seen.insert(prefix);
    }

    return false;
}
```

---

# FORM 3 — Parity Transformation

## Recognition Signals

```text
1. odd/even operations
2. transformations
3. impossible states
4. move operations
```

---

## Core Observation

Parity often invariant.

---

## Example

Convert even to odd using:
```text
+2
```

Impossible.

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

# FORM 4 — Binary Representation Problems

## Recognition Signals

```text
1. subsets
2. bitmasks
3. powers of 2
4. compression
```

---

## Core Observation

Bits represent states.

---

## Example

```text
n bits
→
2^n subsets
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

# FORM 5 — XOR Cancellation

## Recognition Signals

```text
1. unique element
2. pair cancellation
3. odd occurrences
4. binary parity
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

# FORM 6 — Counting Shortcuts

## Recognition Signals

```text
1. combinations
2. pair counting
3. arrangements
4. selections
```

---

## Core Observation

Mathematical counting avoids brute force.

---

## Example

Pairs from:
```text
n elements
```

Answer:

```text
n*(n-1)/2
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pairs(
    long long n
) {

    return n * (n - 1) / 2;
}
```

---

# FORM 7 — Greedy Math Observations

## Recognition Signals

```text
1. median minimization
2. balancing
3. equalization
4. optimal local choice
```

---

## Core Observation

Math often proves greedy optimality.

---

## Example

Median minimizes:
```text
sum of absolute differences
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int median(
    vector<int>& a
) {

    sort(a.begin(),
         a.end());

    return a[a.size()/2];
}
```

---

# FORM 8 — Geometry Reduction

## Recognition Signals

```text
1. coordinates
2. nearest points
3. grids
4. movement
```

---

## Core Observation

Geometry often reduces to:
- algebra
- vectors
- distance math

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long squaredDistance(
    int x1,
    int y1,
    int x2,
    int y2
) {

    long long dx = x2 - x1;
    long long dy = y2 - y1;

    return dx*dx + dy*dy;
}
```

---

# FORM 9 — Probability Expectations

## Recognition Signals

```text
1. random process
2. average outcome
3. expected value
4. probabilistic transitions
```

---

## Core Observation

Expected value:
```text
weighted average
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

# FORM 10 — Monotonic Answer Space

## Recognition Signals

```text
1. minimize maximum
2. maximize minimum
3. threshold feasibility
4. capacity optimization
```

---

## Core Observation

Answer space monotonic.

Foundation of:
```text
binary search on answer
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

bool feasible(int x) {

    return x >= 10;
}
```

---

# FORM 11 — Invariant Problems

## Recognition Signals

```text
1. transformations
2. repeated operations
3. impossible states
4. game operations
```

---

## Core Observation

Something remains unchanged.

---

## Example

Parity invariant.

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

# FORM 12 — Hashing + Frequency Math

## Recognition Signals

```text
1. duplicates
2. frequencies
3. pair sums
4. grouping
```

---

## Core Observation

Math over frequencies simplifies counting.

---

## Example

Pairs with equal values.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long equalPairs(
    vector<int>& a
) {

    unordered_map<int,int> f;

    for (int x : a)
        f[x]++;

    long long ans = 0;

    for (auto &p : f) {

        long long c = p.second;

        ans += c * (c - 1) / 2;
    }

    return ans;
}
```

---

# 4. Real FAANG Interview Mapping

| Interview Topic | Hidden Math |
|---|---|
| Arrays | prefix algebra |
| Hashing | modulo math |
| Sliding window | counting |
| Greedy | optimization proof |
| Bitwise | binary math |
| Graphs | parity/invariants |
| DP | counting/probability |
| Geometry | vector algebra |
| Binary search | monotonic math |

---

# 5. Most Important Interview Observation Tree

```text
Subarray?
→ prefix math

Repeated states?
→ modulo/hash

Odd/even?
→ parity

Unique element?
→ XOR

Huge search space?
→ binary search

Coordinates?
→ geometry

Random outcome?
→ probability

Transformations?
→ invariant

Subsets?
→ bitmask math
```

---

# 6. FAANG Interview Optimization Mindset

Weak candidates:
```text
simulate everything
```

Strong candidates:
```text
reduce mathematically
```

This is one of the BIGGEST interview differences.

---

# 7. Common FAANG Math Traps

```text
1. Missing hidden invariant
2. Ignoring modulo repetition
3. Floating-point precision
4. Overcomplicated brute force
5. Missing counting shortcut
6. Ignoring binary representation
7. Wrong parity assumptions
8. Missing monotonicity
```

---

# 8. Final Interview Checklist

Before solving:

```text
1. Is there hidden math?
2. Can brute force reduce?
3. Is modulo involved?
4. Is parity useful?
5. Is counting easier?
6. Is binary representation useful?
7. Is invariant hidden?
8. Is geometry reducible?
9. Is answer monotonic?
10. Can observations simplify?
```

---

# 9. Final Mental Shortcut

```text
FAANG Math Problems
=
Observation
+
Reduction
+
Optimization
+
Pattern Recognition
```
