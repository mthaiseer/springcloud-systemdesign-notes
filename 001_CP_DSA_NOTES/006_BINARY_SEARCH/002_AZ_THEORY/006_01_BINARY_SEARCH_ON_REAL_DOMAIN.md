# AlgoZenith — Binary Search on Real Domain
## Precision, EPS, and Fixed-Iteration Binary Search
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith lecture PDF — **Binary Search on Real Domain**.
>
> The lecture compares:
>
> ```text
> Binary Search on a discrete/integer domain
>                 vs
> Binary Search on a real-number domain
> ```
>
> The central change is:
>
> ```text
> Integer domain:
> high = mid - 1
> low  = mid + 1
>
> Real domain:
> high = mid
> low  = mid
> ```
>
> because between any two distinct real numbers there are infinitely many values.

---

# Table of Contents

- [1. What This Topic Is About](#1-what-this-topic-is-about)
- [2. Prerequisite — Discrete Binary Search](#2-prerequisite--discrete-binary-search)
- [3. What Changes on a Real Domain?](#3-what-changes-on-a-real-domain)
- [4. Why `mid ± 1` Is Wrong for Real Numbers](#4-why-mid--1-is-wrong-for-real-numbers)
- [5. Real-Domain Search Template](#5-real-domain-search-template)
- [6. EPS — When Do We Stop?](#6-eps--when-do-we-stop)
- [7. Compact EPS Dry Run](#7-compact-eps-dry-run)
- [8. Choosing EPS](#8-choosing-eps)
- [9. Alternative — Fixed Number of Iterations](#9-alternative--fixed-number-of-iterations)
- [10. Why Fixed Iterations Work](#10-why-fixed-iterations-work)
- [11. Compact Fixed-Iteration Dry Run](#11-compact-fixed-iteration-dry-run)
- [12. EPS Method vs Fixed Iterations](#12-eps-method-vs-fixed-iterations)
- [13. Properly Formatted C++17 Templates](#13-properly-formatted-c17-templates)
- [14. Common Mistakes](#14-common-mistakes)
- [15. Recognition Pattern](#15-recognition-pattern)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. What This Topic Is About

Normal Binary Search is often shown on integers:

```text
0 1 2 3 4 5 6 7 ...
```

But sometimes the answer can be a real number:

```text
0.0
0.5
0.75
0.8125
...
```

Example search space:

```text
0.0 ----------------------------- 1.0
                  ^
                  |
                answer
```

There may be no exact integer position to return.

Instead, we want an answer accurate up to some required precision.

So the problem becomes:

```text
keep shrinking the real interval
until it is sufficiently small
```

---

# 2. Prerequisite — Discrete Binary Search

In a discrete domain:

```text
low = 0
high = n - 1
```

Suppose the monotonic space is:

```text
0 0 0 1 1 1 1
      ^
      |
    first 1
```

A standard first-true Binary Search is:

```cpp
int low = 0;
int high = n - 1;
int answer = n;

while (low <= high) {
    int mid = low + (high - low) / 2;

    if (check(mid)) {
        answer = mid;
        high = mid - 1;
    } else {
        low = mid + 1;
    }
}
```

The important operations are:

```text
high = mid - 1
low  = mid + 1
```

Why?

Because integers have a unit step.

If:

```text
mid = 5
```

then the next integer is:

```text
6
```

and the previous integer is:

```text
4
```

So `mid ± 1` makes sense.

---

# 3. What Changes on a Real Domain?

Now suppose:

```text
low = 0.0
high = 1.0
```

Midpoint:

```text
mid = 0.5
```

Between:

```text
0.5 and 1.0
```

there are infinitely many real numbers:

```text
0.6
0.55
0.501
0.5001
...
```

There is no concept of:

```text
"next real number = mid + 1"
```

for the mathematical search logic.

Therefore we do not use:

```text
mid - 1
mid + 1
```

Instead:

```text
high = mid
```

or:

```text
low = mid
```

depending on the result of `check(mid)`.

---

# 4. Why `mid ± 1` Is Wrong for Real Numbers

Suppose:

```text
answer ≈ 0.73
```

and:

```text
mid = 0.50
```

If we wrote:

```text
low = mid + 1
```

we would get:

```text
low = 1.50
```

and completely skip the real answer:

```text
0.73
```

ASCII:

```text
0.0 -------- 0.50 ---- 0.73 ------------ 1.0
              mid       answer
```

Correct update:

```text
low = mid
```

New interval:

```text
0.50 ------------------------------ 1.0
```

The answer is still inside.

---

## Core Difference

```text
DISCRETE DOMAIN

... 3 4 5 6 7 ...

neighbor of 5:
4 and 6

so:
mid - 1
mid + 1
```

```text
REAL DOMAIN

... 0.5000 ... 0.50001 ... 0.500001 ...

infinitely many values near mid

so:
high = mid
low  = mid
```

---

# 5. Real-Domain Search Template

The lecture uses the first-true style:

```text
false false false ... true true true
```

Suppose:

```text
check(mid) = true
```

Then the answer can be:

```text
mid
```

or somewhere to the left.

So:

```cpp
high = mid;
```

Otherwise:

```cpp
low = mid;
```

Generic idea:

```cpp
long double low = 0;
long double high = n;

while (abs(high - low) >= EPS) {
    long double mid = (low + high) / 2;

    if (check(mid)) {
        high = mid;
    } else {
        low = mid;
    }
}

long double answer = (low + high) / 2;
```

Notice:

```text
NO ±1
```

---

# 6. EPS — When Do We Stop?

On integers, eventually:

```text
low > high
```

and Binary Search stops.

On real numbers, if we repeatedly do:

```text
low = mid
high = mid
```

the interval keeps shrinking but conceptually contains infinitely many numbers.

So we stop when:

```text
|high - low| < EPS
```

where:

```text
EPS = epsilon
```

is a small positive value.

Lecture condition:

```text
while (abs(high - low) >= EPS)
```

Meaning:

```text
keep searching while interval width
is still at least EPS
```

---

# 7. Compact EPS Dry Run

Suppose:

```text
low = 0
high = 1
EPS = 0.1
```

Assume the first true point is around:

```text
0.7
```

and:

```text
check(x) = true when x >= 0.7
```

Dry run:

```text
Iter 1: low=0.000, high=1.000 → mid=0.500 → false → low=0.500
Iter 2: low=0.500, high=1.000 → mid=0.750 → true  → high=0.750
Iter 3: low=0.500, high=0.750 → mid=0.625 → false → low=0.625
Iter 4: low=0.625, high=0.750 → mid=0.6875 → false → low=0.6875
```

Now:

```text
high - low
=
0.7500 - 0.6875
=
0.0625
```

Since:

```text
0.0625 < 0.1
```

we stop.

Approximate answer:

```text
(low + high) / 2

=
(0.6875 + 0.7500) / 2

=
0.71875
```

As `EPS` becomes smaller, the approximation becomes more precise.

---

# 8. Choosing EPS

The lecture emphasizes:

```text
Keep EPS smaller than the precision
required by the problem.
```

Example:

```text
required precision = 10^-5
```

Then do not choose an EPS that is too large.

The lecture suggests keeping extra precision, for example:

```text
EPS = 10^-7
```

or smaller depending on the requirement.

It also notes that `long double` itself has finite precision, so EPS should remain meaningful for the numeric type.

---

## Mental Rule

If the problem asks for approximately:

```text
10^-5
```

precision, search more accurately than that.

Example:

```text
required: 1e-5

use:
EPS = 1e-7
```

---

# 9. Alternative — Fixed Number of Iterations

The lecture gives another method.

Instead of writing:

```cpp
while (abs(high - low) >= EPS)
```

run Binary Search a fixed number of times:

```cpp
for (int i = 0; i < 40; i++) {
    ...
}
```

Each iteration halves the interval.

So after many iterations, the interval becomes extremely small.

Template:

```cpp
long double low = 0;
long double high = n;

for (int i = 0; i < 40; i++) {
    long double mid = (low + high) / 2;

    if (check(mid)) {
        high = mid;
    } else {
        low = mid;
    }
}

long double answer = (low + high) / 2;
```

The lecture notes:

```text
increase the iteration count
for better precision
```

---

# 10. Why Fixed Iterations Work

Initially suppose interval length is:

```text
R
```

After one iteration:

```text
R / 2
```

After two:

```text
R / 4
```

After `k` iterations:

```text
R / 2^k
```

The lecture highlights:

```text
2^10 ≈ 10^3
```

and:

```text
2^40 ≈ 10^12
```

So after 40 halvings:

```text
remaining interval
≈
R / 10^12
```

If more precision is required:

```text
increase iterations
```

---

# 11. Compact Fixed-Iteration Dry Run

Start:

```text
[0, 16]
```

Interval widths:

```text
before search: 16

after 1 iteration:  8
after 2 iterations:  4
after 3 iterations:  2
after 4 iterations:  1
after 5 iterations:  0.5
```

General:

```text
after k iterations:

width = 16 / 2^k
```

So Binary Search precision comes directly from repeated halving.

---

# 12. EPS Method vs Fixed Iterations

The lecture presents both valid approaches.

## Method 1 — EPS

Stop based on interval size:

```cpp
while (abs(high - low) >= EPS) {
    ...
}
```

Mental model:

```text
continue until interval becomes sufficiently small
```

---

## Method 2 — Fixed Iterations

Stop after a chosen number of halvings:

```cpp
for (int i = 0; i < iterations; i++) {
    ...
}
```

Mental model:

```text
halve the range enough times
to obtain the required precision
```

Both use the same core updates:

```text
high = mid
```

or:

```text
low = mid
```

---

# 13. Properly Formatted C++17 Templates

## Template 1 — EPS-Based Real Binary Search

```cpp
#include <bits/stdc++.h>
using namespace std;

using ld = long double;

bool check(ld x) {
    // Problem-specific monotonic condition.
    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    ld low = 0;
    ld high = 100;
    const ld EPS = 1e-7L;

    while (abs(high - low) >= EPS) {
        ld mid = (low + high) / 2;

        if (check(mid)) {
            high = mid;
        } else {
            low = mid;
        }
    }

    ld answer = (low + high) / 2;

    return 0;
}
```

---

## Template 2 — Fixed-Iteration Real Binary Search

```cpp
#include <bits/stdc++.h>
using namespace std;

using ld = long double;

bool check(ld x) {
    // Problem-specific monotonic condition.
    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    ld low = 0;
    ld high = 100;

    int iterations = 100;

    for (int i = 0; i < iterations; i++) {
        ld mid = (low + high) / 2;

        if (check(mid)) {
            high = mid;
        } else {
            low = mid;
        }
    }

    ld answer = (low + high) / 2;

    return 0;
}
```

---

## Core Block to Memorize

```cpp
for (int i = 0; i < iterations; i++) {
    ld mid = (low + high) / 2;

    if (check(mid)) {
        high = mid;
    } else {
        low = mid;
    }
}
```

Compare with integer Binary Search:

```cpp
if (check(mid)) {
    high = mid - 1;
} else {
    low = mid + 1;
}
```

The real-domain version removes:

```text
-1
+1
```

---

# 14. Common Mistakes

## Mistake 1 — Using `mid + 1` or `mid - 1`

Wrong for a real-valued search:

```cpp
high = mid - 1;
low = mid + 1;
```

Correct lecture form:

```cpp
high = mid;
low = mid;
```

---

## Mistake 2 — Waiting for `low > high`

For real numbers, use a precision-based stopping condition:

```cpp
abs(high - low) < EPS
```

or a fixed number of iterations.

---

## Mistake 3 — EPS Too Large

If required precision is:

```text
1e-5
```

and EPS is too coarse, the answer may not be sufficiently accurate.

Search with additional precision.

---

## Mistake 4 — EPS Unreasonably Tiny

Floating-point types have finite precision.

Do not blindly choose an EPS smaller than the useful precision supported by the numeric type.

---

## Mistake 5 — Forgetting Monotonicity

Real-domain Binary Search still requires:

```text
a monotonic check
```

Only the domain and stopping rule change.

The logical pattern remains:

```text
false false false true true true
```

or the opposite direction.

---

## Mistake 6 — Forgetting the Final Approximation

At the end:

```text
low
```

and:

```text
high
```

are extremely close.

The lecture uses:

```text
(low + high) / 2
```

as the final approximation.

---

# 15. Recognition Pattern

Think **Binary Search on Real Domain** when:

```text
answer is not necessarily an integer
```

and you can define a monotonic condition:

```text
check(x)
```

Typical recognition structure:

```text
real-valued answer
+
monotonic feasibility
+
required decimal precision
```

Then think:

```text
Binary Search
without ±1
```

and choose one stopping strategy:

```text
EPS
```

or:

```text
fixed iterations
```

---

# 16. Final Mental Model

```text
Binary Search
     |
     +--------------------------+
     |                          |
     v                          v
DISCRETE DOMAIN             REAL DOMAIN
     |                          |
     v                          v
integers                    real values
     |                          |
     v                          v
mid ± 1                     NO mid ± 1
     |                          |
     v                          v
low = mid + 1               low = mid
high = mid - 1              high = mid
     |                          |
     v                          v
stop when bounds cross      stop by precision
                                |
                         +------+------+
                         |             |
                         v             v
                        EPS      fixed iterations
```

The two most important ideas are:

```text
1. Real domain has no unit step.

   Therefore:
   low = mid
   high = mid

   not:
   low = mid + 1
   high = mid - 1
```

and:

```text
2. Stop when the interval is precise enough.

   Either:
   |high-low| < EPS

   or:
   run enough fixed iterations.
```

Compact memory template:

```cpp
for (int i = 0; i < iterations; i++) {
    ld mid = (low + high) / 2;

    if (check(mid)) {
        high = mid;
    } else {
        low = mid;
    }
}

ld answer = (low + high) / 2;
```
