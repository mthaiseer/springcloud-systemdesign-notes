# 032_Common_Math_Traps.md

# Common Math Traps In Competitive Programming

---

# 1. Introduction

Many CP failures happen NOT because algorithms are hard.

They happen because of:

```text
small mathematical mistakes
```

Strong contestants avoid:
- overflow
- precision issues
- modulo mistakes
- parity mistakes
- counting errors
- invariant violations

This file summarizes the MOST COMMON math traps in:
- CP
- FAANG interviews
- implementation rounds

---

# 2. Why Math Traps Matter

Weak contestants:
```text
know algorithms
```

Strong contestants:
```text
avoid hidden mistakes
```

This difference is HUGE in contests/interviews.

---

# 3. Most Common Math Trap Categories

```text
1. Overflow
2. Floating-point precision
3. Modulo mistakes
4. Off-by-one errors
5. Counting mistakes
6. XOR misunderstandings
7. Binary representation issues
8. Geometry precision
9. Invariant violations
10. Integer division bugs
```

---

# 4. Trap 1 — Integer Overflow

MOST COMMON CP BUG.

---

# Problem

```cpp
int x = 100000 * 100000;
```

Overflow occurs.

---

# Why?

```text
int limit ≈ 2e9
```

But:

```text
1e5 × 1e5 = 1e10
```

Too large.

---

# Fix

Use:

```cpp
long long
```

and:
```cpp
1LL
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long x =
    1LL * 100000 * 100000;
```

---

# 5. Trap 2 — Integer Division

Very common interview bug.

---

# Problem

```cpp
5 / 2 = 2
```

NOT:
```text
2.5
```

because:
```text
integer division
```

---

# Fix

Use:
```cpp
double
```

or:
```cpp
1.0 * a / b
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double ans =
    1.0 * 5 / 2;
```

---

# 6. Trap 3 — Modulo Negative Values

VERY IMPORTANT.

---

# Problem

```cpp
-1 % 5
```

can behave unexpectedly.

---

# Fix

Normalize:

```cpp
((x % mod) + mod) % mod
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int normalize(
    int x,
    int mod
) {

    return ((x % mod)
            + mod)
            % mod;
}
```

---

# 7. Trap 4 — Floating Point Precision

Very common geometry issue.

---

# Problem

```cpp
0.1 + 0.2 != 0.3
```

exactly.

---

# Fix

Use:
```text
EPS comparison
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

const double EPS = 1e-9;

bool equalDouble(
    double a,
    double b
) {

    return abs(a - b)
         < EPS;
}
```

---

# 8. Trap 5 — Off-By-One Errors

MOST COMMON implementation trap.

---

# Problem

Loop boundaries wrong.

Example:

```cpp
for(int i=0;i<=n;i++)
```

when array size:
```text
n
```

Out of bounds.

---

# Fix

Carefully define:
- inclusive
- exclusive

ranges.

---

# 9. Trap 6 — Prefix Sum Index Mistakes

Very common FAANG bug.

---

# Problem

Wrong formula:

```text
prefix[r]-prefix[l]
```

---

# Correct

```text
prefix[r]-prefix[l-1]
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int rangeSum(
    vector<int>& p,
    int l,
    int r
) {

    if (l == 0)
        return p[r];

    return p[r]
         - p[l-1];
}
```

---

# 10. Trap 7 — XOR Confusion

Very common bitwise mistake.

---

# Problem

Confusing:
```text
XOR
```

with:
```text
OR
```

---

# Observation

XOR:
```text
same bits → 0
different bits → 1
```

---

# Example

```text
5 ^ 5 = 0
```

NOT:
```text
5
```

---

# 11. Trap 8 — Wrong Power Assumptions

Very common math bug.

---

# Problem

Using:

```cpp
pow(2, 10)
```

for integers.

May cause:
- floating precision
- rounding issues

---

# Fix

Use:
```cpp
1LL << 10
```

for powers of 2.

---

# 12. Trap 9 — Geometry Precision

Very important.

---

# Problem

Comparing:
```text
distance doubles
```

directly.

---

# Fix

Use:
- squared distance
- EPS comparisons

---

## Better Approach

```text
compare:
dx²+dy²
```

instead of:
```text
sqrt(dx²+dy²)
```

---

# 13. Trap 10 — Modulo Overflow

Very common in CP.

---

# Problem

```cpp
(a*b)%mod
```

may overflow BEFORE modulo.

---

# Fix

Use:
```cpp
1LL * a * b % mod
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long mulmod(
    long long a,
    long long b,
    long long mod
) {

    return (1LL * a * b)
            % mod;
}
```

---

# 14. Trap 11 — Binary Search Mid Overflow

Classic interview bug.

---

# Problem

```cpp
mid = (l+r)/2
```

can overflow.

---

# Fix

Use:

```cpp
mid = l + (r-l)/2
```

---

## Correct Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int mid(
    int l,
    int r
) {

    return l + (r-l)/2;
}
```

---

# 15. Trap 12 — Infinite Loops

Very common in:
- binary search
- modulo cycles
- simulations

---

# Problem

Bounds not changing.

---

# Fix

Always ensure:
```text
progress happens
```

---

# 16. Trap 13 — Wrong Counting Formula

Very common combinatorics bug.

---

# Problem

Pairs formula confusion.

---

# Correct

```text
n*(n-1)/2
```

NOT:
```text
n²
```

---

# 17. Trap 14 — Using Floating Point For Combinatorics

Very dangerous.

---

# Problem

Factorials grow huge.

Floating-point loses precision.

---

# Fix

Use:
- long long
- modular arithmetic
- combinatorics formulas

---

# 18. Trap 15 — Signed/Unsigned Bugs

Very common in C++.

---

# Problem

```cpp
size_t
```

vs:
```cpp
int
```

can create unexpected behavior.

---

# Fix

Be careful mixing:
- signed
- unsigned

types.

---

# 19. Trap 16 — Missing Edge Cases

VERY IMPORTANT.

---

# Common Missing Cases

```text
1. n=0
2. n=1
3. empty arrays
4. duplicates
5. all equal
6. negative numbers
7. overflow bounds
8. modulo zero
```

---

# 20. Trap 17 — Wrong Complexity Assumptions

Very common interview failure.

---

# Problem

Nested loops assumed fast.

---

# Observation

```text
1e5²
```

too slow.

---

# Fix

Always estimate:
```text
time complexity
```

before coding.

---

# 21. Trap 18 — Precision Loss In Geometry

Very important.

---

# Problem

Using:
```text
sqrt repeatedly
```

causes precision accumulation.

---

# Fix

Prefer:
- squared values
- vector formulas
- integer geometry

---

# 22. Trap 19 — Modulo Division Mistake

Very important advanced trap.

---

# Problem

```text
(a/b)%mod
```

INVALID directly.

---

# Fix

Need:
```text
modular inverse
```

Very important number theory concept.

---

# 23. Trap 20 — Assuming Greedy Works

Very common FAANG mistake.

---

# Problem

Local optimum:
```text
does NOT imply
global optimum
```

---

# Fix

Need:
- proof
- invariant
- exchange argument

---

# 24. Ultimate CP Trap Recognition Tree

```text
Wrong answers on big numbers?
→ overflow

Wrong decimals?
→ precision issue

Negative modulo?
→ normalize modulo

Binary search failing?
→ mid overflow / infinite loop

Geometry WA?
→ floating-point issue

Subarray off by one?
→ prefix indexing

Bitwise bug?
→ XOR vs OR confusion
```

---

# 25. Real World Engineering Mapping

| Trap | Real System Impact |
|---|---|
| overflow | production crashes |
| precision | financial bugs |
| modulo errors | hashing issues |
| binary search bugs | infinite loops |
| geometry precision | map inaccuracies |
| counting bugs | analytics errors |
| signed/unsigned | memory corruption |
| edge cases | production incidents |

---

# 26. Final Contest Checklist

Before submitting:

```text
1. Any overflow?
2. Any precision issue?
3. Any modulo normalization needed?
4. Any edge cases missing?
5. Any off-by-one issue?
6. Any infinite loop possible?
7. Any signed/unsigned bug?
8. Any invalid assumptions?
9. Any complexity issue?
10. Any brute force too slow?
```

---

# 27. Final Mental Shortcut

```text
Strong CP
=
Correctness
+
Precision
+
Edge Cases
+
Mathematical Safety
```
