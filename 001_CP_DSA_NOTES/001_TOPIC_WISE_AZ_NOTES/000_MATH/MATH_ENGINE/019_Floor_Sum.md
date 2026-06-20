# 019_Floor_Sum.md — MiniMathEngine

# Floor Sum

> Floor Sum is one of the most important advanced math optimization patterns in CP.  
> It appears in:
>
> ```text
> Σ floor((a*i+b)/m)
> ```
>
> problems and enables huge optimizations from:
>
> ```text
> O(n)
> ```
>
> to:
>
> ```text
> O(log m)
> ```

---

# Clickable Index

1. What Is Floor Sum?
2. Why This Topic Matters
3. Core Formula
4. Simple Intuition
5. Visual Understanding
6. Basic Floor Properties
7. Recursive Reduction Idea
8. AtCoder Floor Sum Pattern
9. Problem Form 1 — Sum Of Floors
10. Problem Form 2 — Count Lattice Points
11. Problem Form 3 — Divisor Bucket Optimization
12. Problem Form 4 — Range Compression
13. Problem Form 5 — Euclidean Reduction
14. Dry Run
15. Common Mistakes
16. Decision Tree
17. Real World Mapping
18. Complexity
19. Reusable C++ Template
20. CP / FAANG Problem Forms
21. Practice Checklist
22. Next Step

---

# 1. What Is Floor Sum?

Floor sum problems involve expressions like:

```text
Σ floor((a*i+b)/m)
```

Usually:

```text
i = 0 to n-1
```

Example:

```text
Σ floor(i/2)
for i = 0 to 5
```

Compute:

```text
floor(0/2)=0
floor(1/2)=0
floor(2/2)=1
floor(3/2)=1
floor(4/2)=2
floor(5/2)=2
```

Answer:

```text
0+0+1+1+2+2 = 6
```

---

# 2. Why This Topic Matters

Floor sums appear in:

- number theory
- geometry
- lattice counting
- divisor optimization
- Euclidean reduction
- harmonic complexity
- advanced combinatorics
- AtCoder math
- Codeforces number theory

Very common in:

- AtCoder Library Practice
- ICPC
- Codeforces 1900+
- advanced math interviews

---

# 3. Core Formula

Canonical form:

```text
S(n,m,a,b)
=
Σ floor((a*i+b)/m)
```

for:

```text
i = 0 to n-1
```

---

# 4. Simple Intuition

Floor means:

```text
largest integer <= value
```

Example:

```text
floor(7/3)=2
floor(8/3)=2
floor(9/3)=3
```

Floor sum asks:

```text
how many complete blocks fit
```

across many values.

---

# 5. Visual Understanding

Suppose:

```text
floor(i/3)
```

Values:

```text
i : floor(i/3)

0 : 0
1 : 0
2 : 0
3 : 1
4 : 1
5 : 1
6 : 2
7 : 2
8 : 2
```

Notice:

```text
same values repeat in blocks
```

This structure enables optimization.

---

# 6. Basic Floor Properties

Useful identities:

```text
floor((x+y)/m)
```

can often split into parts.

Another important identity:

```text
Σ floor(n/i)
```

has only:

```text
O(sqrt(n))
```

distinct values.

Very important for divisor problems.

---

# 7. Recursive Reduction Idea

The advanced floor sum trick reduces:

```text
(a,m)
```

using Euclidean-style recursion.

Core insight:

```text
large parameters shrink recursively
```

similar to:

```text
gcd recursion
```

---

# 8. AtCoder Floor Sum Pattern

The famous implementation computes:

```text
Σ floor((a*i+b)/m)
```

in:

```text
O(log m)
```

instead of:

```text
O(n)
```

using recursive reduction.

---

# 9. Problem Form 1 — Sum Of Floors

## Problem

Compute:

```text
Σ floor(i/2)
for i=0..5
```

---

## Step-by-Step Working

Values:

```text
0/2 -> 0
1/2 -> 0
2/2 -> 1
3/2 -> 1
4/2 -> 2
5/2 -> 2
```

Sum:

```text
0+0+1+1+2+2 = 6
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long ans = 0;

    for (int i = 0; i <= 5; i++) {

        ans += i / 2;
    }

    cout << ans << endl;

    return 0;
}
```

---

# 10. Problem Form 2 — Count Lattice Points

## Problem

Count integer points below line:

```text
y = (a*x+b)/m
```

This becomes:

```text
Σ floor((a*i+b)/m)
```

---

## Example

Count integer points under:

```text
y = x/2
```

for:

```text
x = 0..5
```

Values:

```text
0,0,1,1,2,2
```

Total points:

```text
6
```

---

# 11. Problem Form 3 — Divisor Bucket Optimization

## Problem

Compute:

```text
Σ floor(n/i)
```

efficiently.

Naive:

```text
O(n)
```

Optimized:

```text
O(sqrt(n))
```

because quotient changes only at bucket boundaries.

---

## Step-by-Step Working

Example:

```text
n = 10
```

Values:

```text
10/1 = 10
10/2 = 5
10/3 = 3
10/4 = 2
10/5 = 2
10/6 = 1
...
```

Distinct quotients are few.

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long n = 10;

    long long ans = 0;

    for (long long l = 1; l <= n; ) {

        long long q = n / l;

        long long r = n / q;

        ans += q * (r - l + 1);

        l = r + 1;
    }

    cout << ans << endl;

    return 0;
}
```

---

# 12. Problem Form 4 — Range Compression

## Idea

If:

```text
floor(n/i)
```

stays same for range:

```text
[l...r]
```

process whole block together.

This is harmonic optimization.

---

## Example

For:

```text
n = 100
```

Value:

```text
floor(100/i)=1
```

for:

```text
i = 51..100
```

Process together.

---

# 13. Problem Form 5 — Euclidean Reduction

## Problem

Compute:

```text
Σ floor((a*i+b)/m)
```

with huge constraints.

---

## Core Idea

Reduce recursively:

```text
(a,m)
```

similar to Euclid algorithm.

This creates:

```text
O(log m)
```

complexity.

---

# 14. Dry Run

Compute:

```text
Σ floor(i/3)
for i=0..8
```

Values:

```text
0
0
0
1
1
1
2
2
2
```

Sum:

```text
9
```

---

# 15. Common Mistakes

## Mistake 1 — Using floating point

Wrong:

```cpp
floor((double)x/y)
```

Correct:

```cpp
x / y
```

for integers.

---

## Mistake 2 — Forgetting harmonic optimization

Naive:

```text
O(n)
```

Optimized:

```text
O(sqrt(n))
```

or:

```text
O(log n)
```

---

## Mistake 3 — Overflow

Expression:

```text
a*i+b
```

may overflow.

Use:

```cpp
long long
```

---

## Mistake 4 — Wrong interval update

When compressing ranges:

```text
r = n / q
```

must be used correctly.

---

# 16. Decision Tree

```text
Expression contains floor?
|
+-- floor(n/i)?
|   |
|   +-- Use divisor bucket optimization
|
+-- floor((a*i+b)/m)?
|   |
|   +-- Large constraints?
|       |
|       +-- Use recursive floor sum
|
+-- Repeated quotient ranges?
    |
    +-- Use harmonic compression
```

---

# 17. Real World Mapping

| Real World System | Floor Sum Mapping |
|---|---|
| Load batching | complete blocks |
| Memory paging | page division |
| Database sharding | bucket counts |
| Packet segmentation | chunk counting |
| Grid geometry | lattice counting |
| Distributed partitions | interval compression |
| Resource allocation | grouped division |
| Compression systems | block grouping |

---

# 18. Complexity

Naive floor sum:

```text
O(n)
```

Harmonic optimization:

```text
O(sqrt(n))
```

Recursive floor sum:

```text
O(log m)
```

---

# 19. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

// AtCoder Library style floor sum
// Computes:
// sum floor((a*i+b)/m)
// for i=0..n-1

long long floor_sum(long long n,
                     long long m,
                     long long a,
                     long long b) {

    long long ans = 0;

    while (true) {

        if (a >= m) {

            ans += (n - 1) * n * (a / m) / 2;

            a %= m;
        }

        if (b >= m) {

            ans += n * (b / m);

            b %= m;
        }

        long long yMax = a * n + b;

        if (yMax < m) break;

        n = yMax / m;
        b = yMax % m;

        swap(m, a);
    }

    return ans;
}

int main() {

    cout << floor_sum(6, 2, 1, 0) << endl;

    return 0;
}
```

---

# 20. CP / FAANG Problem Forms

## Problem 1 — Sum Of floor(n/i)

### Pattern

```text
Harmonic optimization
```

### Complexity

```text
O(sqrt(n))
```

### Step-by-Step Working

For:

```text
n = 10
```

Instead of iterating all `i`:

Process ranges where quotient same.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long n = 10;

    long long ans = 0;

    for (long long l = 1; l <= n; ) {

        // current quotient
        long long q = n / l;

        // largest r with same quotient
        long long r = n / q;

        // add contribution for entire block
        ans += q * (r - l + 1);

        l = r + 1;
    }

    cout << ans << endl;

    return 0;
}
```

---

## Problem 2 — Lattice Point Counting

### Pattern

```text
Floor sum geometry
```

### Step-by-Step Working

Count:

```text
Σ floor(i/2)
```

for:

```text
i=0..5
```

Values:

```text
0,0,1,1,2,2
```

Answer:

```text
6
```

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long ans = 0;

    for (int i = 0; i <= 5; i++) {

        // number of integer y values
        // below line y=i/2
        ans += i / 2;
    }

    cout << ans << endl;

    return 0;
}
```

---

## Problem 3 — Recursive Floor Sum

### Pattern

```text
AtCoder recursive reduction
```

### Complexity

```text
O(log m)
```

### Step-by-Step Working

Reduce:

```text
(a,m)
```

using Euclidean recursion.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long floor_sum(long long n,
                     long long m,
                     long long a,
                     long long b) {

    long long ans = 0;

    while (true) {

        if (a >= m) {

            // contribution from full quotient blocks
            ans += (n - 1) * n * (a / m) / 2;

            a %= m;
        }

        if (b >= m) {

            ans += n * (b / m);

            b %= m;
        }

        long long yMax = a * n + b;

        if (yMax < m) break;

        // recursive reduction
        n = yMax / m;
        b = yMax % m;

        swap(m, a);
    }

    return ans;
}

int main() {

    cout << floor_sum(10, 7, 3, 4) << endl;

    return 0;
}
```

---

## Problem 4 — Distinct Quotients

### Pattern

```text
Range compression
```

### Step-by-Step Working

For:

```text
floor(100/i)
```

many `i` share same quotient.

Compress ranges.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long n = 100;

    for (long long l = 1; l <= n; ) {

        long long q = n / l;

        long long r = n / q;

        cout << "quotient=" << q
             << " range=[" << l << "," << r << "]\n";

        l = r + 1;
    }

    return 0;
}
```

---

## Problem 5 — Sum Of Divisor Counts

### Pattern

```text
floor division optimization
```

### Step-by-Step Working

Observation:

```text
count of multiples of i
=
floor(n/i)
```

Use harmonic grouping.

---

### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long n = 10;

    long long ans = 0;

    for (long long i = 1; i <= n; i++) {

        // how many multiples of i exist up to n
        ans += n / i;
    }

    cout << ans << endl;

    return 0;
}
```

---

# 21. Practice Checklist

Before solving:

```text
1. Does expression contain floor?
2. Is there repeated quotient behavior?
3. Can ranges be compressed?
4. Is complexity too high?
5. Can harmonic optimization help?
6. Is expression floor(n/i)?
7. Is expression floor((a*i+b)/m)?
8. Need lattice counting?
9. Need recursive Euclid reduction?
10. Need AtCoder floor sum?
```

---

# 22. Next Step

```text
020_Math_Pattern_Decision_Engine.md
```

This becomes the:

```text
ultimate math pattern recognition guide
```

for:

```text
FAANG
Codeforces
AtCoder
ICPC
high-level CP interviews
```
