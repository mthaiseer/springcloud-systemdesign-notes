# 020_Inclusion_Exclusion_Intro.md

# Inclusion Exclusion Principle Intro For Competitive Programming

---

# 1. Introduction

Inclusion-Exclusion Principle is one of the MOST important counting techniques in CP.

It helps solve problems involving:
- overlapping sets
- multiple conditions
- divisible numbers
- union counting
- probability
- combinatorics

Strong contestants constantly ask:

```text
Am I double counting?
```

That is exactly inclusion-exclusion thinking.

---

# 2. Core Idea

Suppose:

```text
Count elements in:
A OR B
```

Naive counting:

```text
|A| + |B|
```

Problem:

```text
Common elements counted twice.
```

Need correction.

---

# 3. Inclusion-Exclusion Formula

For 2 sets:

```text
|A ∪ B|
=
|A| + |B| - |A ∩ B|
```

Meaning:
- include A
- include B
- remove overlap

---

# 4. Simple Example

Numbers from:
```text
1 to 10
```

Count divisible by:
```text
2 OR 3
```

---

# Divisible By 2

```text
2 4 6 8 10
```

Count:
```text
5
```

---

# Divisible By 3

```text
3 6 9
```

Count:
```text
3
```

---

# Problem

```text
6
```

counted twice.

Need subtract once.

---

# Final Answer

```text
5 + 3 - 1
=
7
```

Numbers:

```text
2 3 4 6 8 9 10
```

---

# 5. Why Inclusion-Exclusion Matters

Without it:
- overcounting occurs
- duplicates appear
- probability becomes wrong

Very important in:
- counting
- combinatorics
- number theory

---

# 6. Three Set Formula

For sets:
```text
A B C
```

Formula:

```text
|A∪B∪C|
=
|A| + |B| + |C|
- |A∩B|
- |B∩C|
- |A∩C|
+ |A∩B∩C|
```

Pattern:
- add singles
- subtract pairs
- add triples

Alternating signs.

---

# 7. Why Signs Alternate

Reason:
- overlaps counted multiple times
- corrections needed repeatedly

Pattern:

```text
+ single sets
- pair overlaps
+ triple overlaps
- quadruple overlaps
```

Very important intuition.

---

# 8. Divisibility Problems

Most common CP usage.

Count numbers divisible by:
- a
- b
- c

Use:
- floor division
- lcm
- inclusion-exclusion

---

# Example

Count numbers from:
```text
1..100
```

divisible by:
```text
2 OR 5
```

---

# Divisible By 2

```text
100 / 2 = 50
```

---

# Divisible By 5

```text
100 / 5 = 20
```

---

# Double Counted

Divisible by:
```text
lcm(2,5)=10
```

Count:
```text
100 / 10 = 10
```

---

# Final

```text
50 + 20 - 10
=
60
```

---

# 9. Probability Usage

Probability often uses:

```text
P(A OR B)
=
P(A) + P(B) - P(A∩B)
```

Very important in:
- probability
- statistics

---

# 10. Bitmask Connection

Advanced inclusion-exclusion uses:
- subsets
- bitmask enumeration

Very important later in:
- DP
- advanced combinatorics

---

# 11. CP Observation Mindset

Strong contestants think:

```text
Count everything
→ remove overcounting
```

This is core inclusion-exclusion intuition.

---

# 12. CP / FAANG Problem Forms

---

# Form 1 — Divisible By A Or B

## Problem

Count numbers divisible by:
```text
a OR b
```

---

## Observation

Use inclusion-exclusion.

Formula:

```text
count(a)
+
count(b)
-
count(lcm(a,b))
```

---

## Step-by-Step Working

Count:
```text
1..20
```

divisible by:
```text
2 or 3
```

Divisible by 2:
```text
10
```

Divisible by 3:
```text
6
```

Divisible by 6:
```text
3
```

Answer:
```text
10 + 6 - 3 = 13
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countDivisible(
    long long n,
    long long a,
    long long b
) {

    long long l =
        lcm(a, b);

    return n / a
         + n / b
         - n / l;
}
```

---

# Form 2 — Set Union Size

## Problem

Find union size of two sets.

---

## Observation

Subtract overlap.

Formula:

```text
|A| + |B| - |A∩B|
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int unionSize(
    int A,
    int B,
    int overlap
) {

    return A + B - overlap;
}
```

---

# Form 3 — Probability OR

## Problem

Compute:
```text
P(A OR B)
```

---

## Observation

Subtract common probability.

Formula:

```text
P(A)+P(B)-P(A∩B)
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

double probabilityOR(
    double A,
    double B,
    double overlap
) {

    return A + B - overlap;
}
```

---

# Form 4 — Three Set Inclusion Exclusion

## Problem

Count elements satisfying:
```text
A or B or C
```

---

## Observation

Alternate signs.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int threeSetUnion(
    int A,
    int B,
    int C,
    int AB,
    int AC,
    int BC,
    int ABC
) {

    return A + B + C
         - AB - AC - BC
         + ABC;
}
```

---

# Form 5 — Duplicate Removal

## Problem

Avoid double counting.

---

## Observation

Subtract overlaps once.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int correctedCount(
    int countA,
    int countB,
    int overlap
) {

    return countA
         + countB
         - overlap;
}
```

---

# Form 6 — Divisibility With Multiple Conditions

## Problem

Count numbers divisible by:
```text
2 or 3 or 5
```

---

## Observation

Use:
- singles
- pairs
- triples

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long count235(
    long long n
) {

    return n/2 + n/3 + n/5
         - n/6 - n/10 - n/15
         + n/30;
}
```

---

# Form 7 — Bitmask Inclusion Exclusion

## Problem

Advanced multiple-set counting.

---

## Observation

Enumerate subsets.

Odd subset size:
```text
add
```

Even subset size:
```text
subtract
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void subsetSigns(int n) {

    for (int mask = 1;
         mask < (1 << n);
         mask++) {

        int bits =
            __builtin_popcount(mask);

        if (bits % 2 == 1)
            cout << "ADD\n";
        else
            cout << "SUBTRACT\n";
    }
}
```

---

# 13. Real World Applications

| Real System | Usage |
|---|---|
| Databases | overlapping query counts |
| Analytics | user overlap counting |
| Search engines | document unions |
| Networking | packet classification |
| Recommendation systems | overlap analysis |
| Probability systems | event unions |
| Data science | set intersection analysis |

---

# 14. Real Engineering Insight

Inclusion-Exclusion means:

```text
Avoid overcounting overlaps
```

This is extremely important in:
- analytics
- distributed systems
- database aggregation
- recommendation engines

---

# 15. Observation Recognition Signals

Look for:

```text
1. OR conditions
2. overlapping sets
3. duplicate counting
4. union counting
5. divisibility unions
6. intersections
7. probability unions
8. multiple conditions
```

---

# 16. Decision Tree

```text
OR conditions?
→ inclusion-exclusion

Double counting?
→ subtract overlap

Multiple divisibility?
→ lcm + IE

Probability OR?
→ inclusion-exclusion

Overlapping sets?
→ intersections matter
```

---

# 17. Common Traps

```text
1. Forgetting overlaps
2. Double counting
3. Wrong lcm usage
4. Missing triple overlap
5. Wrong sign alternation
6. Overflow in lcm
7. Counting duplicates multiple times
8. Missing subset parity logic
```

---

# 18. Final Checklist

Before solving:

```text
1. Are sets overlapping?
2. Is double counting happening?
3. Are OR conditions involved?
4. Is lcm needed?
5. Are intersections important?
6. Is probability union involved?
7. Are signs alternating correctly?
8. Is inclusion-exclusion simplifying counting?
```

---

# 19. Final Mental Shortcut

```text
Inclusion-Exclusion
=
Count Everything
-
Remove Overlaps
+
Fix Overcorrection
```
