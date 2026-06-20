# 009_Floor_Ceil_Observations.md

# Floor And Ceil Observations For Competitive Programming

---

# 1. Introduction

Floor and ceil are extremely important in CP.

They appear in:
- binary search
- greedy
- math
- bounds
- scheduling
- load balancing
- partitioning
- constructive problems

Most beginners make mistakes with:
- integer division
- ceil division
- off-by-one errors

Mastering these concepts gives huge advantage.

---

# 2. What Is Floor?

Floor means:

```text
largest integer <= x
```

Notation:

```text
floor(x)
```

Examples:

```text
floor(3.9) = 3
floor(7.1) = 7
floor(-2.3) = -3
```

---

# 3. What Is Ceil?

Ceil means:

```text
smallest integer >= x
```

Notation:

```text
ceil(x)
```

Examples:

```text
ceil(3.1) = 4
ceil(8.9) = 9
ceil(-2.3) = -2
```

---

# 4. Integer Division In C++

Important:

```cpp
7 / 3 = 2
```

because integer division automatically performs:

```text
floor division
```

---

# 5. Why Floor/Ceil Matter In CP

Recognition signals:
- grouping
- partitioning
- minimum containers
- minimum operations
- chunks
- servers
- batches
- pages

Usually means:

```text
ceil division
```

---

# 6. Most Important Formula — Ceil Division

VERY IMPORTANT CP FORMULA:

For positive integers:

```text
ceil(a / b)
=
(a + b - 1) / b
```

---

# Example

Find:

```text
ceil(10 / 3)
```

Normal division:

```text
3.333...
```

Ceil:

```text
4
```

Using formula:

```text
(10 + 3 - 1) / 3
= 12 / 3
= 4
```

---

# 7. Why Ceil Division Is Important

Suppose:
- 10 tasks
- each server handles 3 tasks

Need:

```text
ceil(10 / 3)
= 4 servers
```

Very common in:
- distributed systems
- scheduling
- batching
- pagination

---

# 8. Floor Division Intuition

Floor division means:

```text
How many full groups fit?
```

Example:

```text
10 / 3 = 3
```

Meaning:

```text
3 complete groups
```

---

# 9. Ceil Division Intuition

Ceil division means:

```text
How many groups needed total?
```

Example:

```text
10 items
group size 3
```

Need:

```text
4 groups
```

because last group partially filled.

---

# 10. Common CP Observation

When problem says:

```text
At least
minimum required
minimum containers
minimum operations
```

usually think:

```text
ceil division
```

---

# 11. Chunking Problems

Example:

```text
N files
chunk size K
```

Total chunks:

```text
ceil(N / K)
```

---

# Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long chunksNeeded(long long n, long long k) {

    return (n + k - 1) / k;
}
```

---

# 12. Binary Search + Floor/Ceil

Binary search often uses:

```text
mid = low + (high - low)/2
```

which is floor midpoint.

Sometimes upper-mid needed:

```text
(low + high + 1)/2
```

to avoid infinite loops.

---

# 13. Floor/Ceil And Greedy

Example:

```text
minimum buses needed
```

Formula:

```text
ceil(passengers / capacity)
```

Greedy often depends on ceil logic.

---

# 14. Floor/Ceil In Math Optimization

Example:

```text
How many multiples of k ≤ n?
```

Answer:

```text
floor(n / k)
```

---

# Example

```text
n = 17
k = 5

17 / 5 = 3
```

Multiples:

```text
5 10 15
```

3 numbers.

---

# 15. Floor/Ceil In Ranges

Important identity:

Count integers in:

```text
[L, R]
```

divisible by k:

```text
floor(R/k) - floor((L-1)/k)
```

Very common in:
- prefix counting
- number theory
- range queries

---

# 16. CP / FAANG Problem Forms

---

# Form 1 — Ceil Division

## Problem

Need minimum servers to process tasks.

---

## Step-by-Step Working

Example:

```text
10 tasks
3 tasks/server
```

Need:

```text
ceil(10/3)
= 4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long serversNeeded(
    long long tasks,
    long long cap
) {

    return (tasks + cap - 1) / cap;
}
```

---

# Form 2 — Count Multiples

## Problem

How many numbers divisible by k from 1 to n?

---

## Observation

Use floor division.

---

## Example

```text
n = 20
k = 6

20/6 = 3
```

Multiples:

```text
6 12 18
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countMultiples(
    long long n,
    long long k
) {

    return n / k;
}
```

---

# Form 3 — Range Divisibility

## Problem

Count divisible numbers in range [L,R].

---

## Formula

```text
floor(R/k) - floor((L-1)/k)
```

---

## Step-by-Step Working

Example:

```text
L = 5
R = 20
k = 4
```

Multiples:

```text
8 12 16 20
```

Count:

```text
4
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long rangeCount(
    long long L,
    long long R,
    long long k
) {

    return R / k
         - (L - 1) / k;
}
```

---

# Form 4 — Pagination

## Problem

Display N items with page size K.

Find pages needed.

---

## Observation

Ceil division.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pagesNeeded(
    long long n,
    long long pageSize
) {

    return (n + pageSize - 1)
            / pageSize;
}
```

---

# Form 5 — Binary Search Mid

## Problem

Avoid infinite loop in binary search.

---

## Observation

Use upper-mid sometimes.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int upperMid(int low, int high) {

    return low + (high - low + 1) / 2;
}
```

---

# Form 6 — Split Array Into Groups

## Problem

Minimum groups if each group size ≤ K.

---

## Observation

Ceil division.

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long groupsNeeded(
    long long n,
    long long k
) {

    return (n + k - 1) / k;
}
```

---

# 17. Real World Applications

| Real System | Usage |
|---|---|
| Distributed systems | shard allocation |
| Databases | pagination |
| Cloud systems | server count |
| Video streaming | chunk splitting |
| Load balancer | request partitioning |
| Networking | packet batching |
| Storage systems | block allocation |

---

# 18. Real Engineering Insight

Floor usually means:

```text
maximum full groups
```

Ceil usually means:

```text
minimum groups/resources required
```

This mental mapping is extremely useful.

---

# 19. Decision Tree

```text
Need complete groups?
→ floor division

Need minimum containers/resources?
→ ceil division

Need batching/pagination?
→ ceil division

Need count divisible?
→ floor division

Need binary search midpoint?
→ floor/upper-mid logic
```

---

# 20. Common Traps

```text
1. Using normal division accidentally
2. Forgetting ceil formula
3. Overflow in (a+b-1)
4. Infinite binary search loop
5. Negative number floor confusion
6. Off-by-one errors
7. Wrong midpoint formula
8. Wrong range divisibility formula
```

---

# 21. Final Checklist

Before solving:

```text
1. Is grouping involved?
2. Is minimum resource count needed?
3. Is ceil division required?
4. Is complete grouping needed?
5. Is pagination/chunking involved?
6. Is binary search midpoint safe?
7. Is divisibility counting involved?
8. Are there off-by-one risks?
```

---

# 22. Final Mental Shortcut

```text
Floor
=
Complete Groups

Ceil
=
Required Groups
+
Minimum Resources
```
