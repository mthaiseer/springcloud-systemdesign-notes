# 018_Diophantine_Equation.md — MiniMathEngine

# Linear Diophantine Equation

> Linear Diophantine equations are integer equations of the form `ax + by = c`.  
> The key idea is: **integer solution exists only if gcd(a,b) divides c**.

---

## Clickable Index

1. [What Is A Diophantine Equation?](#1-what-is-a-diophantine-equation)
2. [Why This Topic Matters](#2-why-this-topic-matters)
3. [Core Problem Form](#3-core-problem-form)
4. [Existence Condition](#4-existence-condition)
5. [Extended Euclid Connection](#5-extended-euclid-connection)
6. [General Solution Formula](#6-general-solution-formula)
7. [Step-by-Step Solving Process](#7-step-by-step-solving-process)
8. [Problem Form 1 — Check If Solution Exists](#8-problem-form-1--check-if-solution-exists)
9. [Problem Form 2 — Find One Integer Solution](#9-problem-form-2--find-one-integer-solution)
10. [Problem Form 3 — Generate All Solutions](#10-problem-form-3--generate-all-solutions)
11. [Problem Form 4 — Coin Equation](#11-problem-form-4--coin-equation)
12. [Problem Form 5 — Non-Negative Solutions](#12-problem-form-5--non-negative-solutions)
13. [Problem Form 6 — Minimum Positive Solution](#13-problem-form-6--minimum-positive-solution)
14. [Dry Run](#14-dry-run)
15. [Common Mistakes](#15-common-mistakes)
16. [Decision Tree](#16-decision-tree)
17. [Real World Mapping](#17-real-world-mapping)
18. [Complexity](#18-complexity)
19. [Reusable C++ Template](#19-reusable-c-template)
20. [CP / FAANG Problem Forms](#20-cp--faang-problem-forms)
21. [Practice Checklist](#21-practice-checklist)
22. [Next Step](#22-next-step)

---

## 1. What Is A Diophantine Equation?

A Diophantine equation is an equation where we need:

```text
integer solutions
```

Most common CP form:

```text
ax + by = c
```

Where:

```text
a, b, c are integers
x, y must be integers
```

Example:

```text
6x + 9y = 30
```

One solution:

```text
x = 5, y = 0
```

because:

```text
6*5 + 9*0 = 30
```

---

## 2. Why This Topic Matters

Linear Diophantine equations appear in:

- number theory
- modular arithmetic
- extended Euclid
- coin problems
- reachability problems
- integer equation solving
- CRT internals
- scheduling cycles
- resource balancing
- CP math problems

Common problem statements:

```text
Can we make amount c using coin sizes a and b?
```

```text
Find integers x and y such that ax + by = c.
```

```text
Can one number be expressed as a linear combination of two numbers?
```

---

## 3. Core Problem Form

The standard equation:

```text
ax + by = c
```

Goal:

```text
find integer x and y
```

or check whether such integers exist.

---

## 4. Existence Condition

The equation:

```text
ax + by = c
```

has integer solution iff:

```text
gcd(a,b) divides c
```

That means:

```text
c % gcd(a,b) == 0
```

Example:

```text
6x + 9y = 30
```

Compute:

```text
gcd(6,9)=3
```

Check:

```text
30 % 3 = 0
```

So solution exists.

Another:

```text
6x + 9y = 20
```

```text
gcd(6,9)=3
20 % 3 != 0
```

No integer solution.

---

## 5. Extended Euclid Connection

Extended Euclid gives:

```text
ax + by = gcd(a,b)
```

If:

```text
g = gcd(a,b)
```

and Extended Euclid gives:

```text
a*x0 + b*y0 = g
```

Then multiply both sides by:

```text
c/g
```

So:

```text
a*(x0*c/g) + b*(y0*c/g) = c
```

Therefore one solution is:

```text
x = x0 * (c/g)
y = y0 * (c/g)
```

---

## 6. General Solution Formula

If one solution is:

```text
x0, y0
```

for:

```text
ax + by = c
```

and:

```text
g = gcd(a,b)
```

Then all solutions are:

```text
x = x0 + k * (b/g)
y = y0 - k * (a/g)
```

where:

```text
k is any integer
```

---

## 7. Step-by-Step Solving Process

To solve:

```text
ax + by = c
```

Steps:

```text
1. Compute g = gcd(a,b)
2. Check c % g == 0
3. If not divisible -> no solution
4. Use Extended Euclid to solve ax + by = g
5. Scale x and y by c/g
6. Generate all solutions using parameter k
```

---

## 8. Problem Form 1 — Check If Solution Exists

### Problem

Check whether:

```text
14x + 21y = 35
```

has integer solution.

---

### Step-by-Step Working

Step 1:

```text
gcd(14,21)=7
```

Step 2:

```text
35 % 7 = 0
```

So:

```text
solution exists
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long a = 14;
    long long b = 21;
    long long c = 35;

    long long g = gcd(a, b);

    if (c % g == 0) {
        cout << "Solution exists\n";
    } else {
        cout << "No solution\n";
    }

    return 0;
}
```

---

## 9. Problem Form 2 — Find One Integer Solution

### Problem

Find one integer solution for:

```text
6x + 9y = 30
```

---

### Step-by-Step Working

Step 1:

```text
gcd(6,9)=3
```

Step 2:

```text
30 % 3 = 0
```

Solution exists.

Step 3:

Use Extended Euclid:

```text
6*(-1) + 9*(1) = 3
```

So:

```text
x0 = -1
y0 = 1
```

Step 4:

Scale by:

```text
c/g = 30/3 = 10
```

One solution:

```text
x = -10
y = 10
```

Check:

```text
6*(-10) + 9*(10)
=
-60 + 90
=
30
```

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long extendedGcd(long long a, long long b, long long &x, long long &y) {

    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;

    long long g = extendedGcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

int main() {

    long long a = 6, b = 9, c = 30;

    long long x, y;

    long long g = extendedGcd(a, b, x, y);

    if (c % g != 0) {
        cout << "No solution\n";
        return 0;
    }

    long long scale = c / g;

    x *= scale;
    y *= scale;

    cout << x << " " << y << endl;

    return 0;
}
```

---

## 10. Problem Form 3 — Generate All Solutions

### Problem

Find all integer solutions form for:

```text
6x + 9y = 30
```

---

### One Solution

From previous section:

```text
x0 = -10
y0 = 10
```

```text
g = 3
```

General solution:

```text
x = x0 + k * (b/g)
y = y0 - k * (a/g)
```

Substitute:

```text
x = -10 + k * 3
y = 10 - k * 2
```

where:

```text
k is any integer
```

---

### Check

For:

```text
k = 5
```

```text
x = -10 + 15 = 5
y = 10 - 10 = 0
```

Check:

```text
6*5 + 9*0 = 30
```

Valid.

---

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long extendedGcd(long long a, long long b, long long &x, long long &y) {

    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;

    long long g = extendedGcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

int main() {

    long long a = 6, b = 9, c = 30;

    long long x0, y0;

    long long g = extendedGcd(a, b, x0, y0);

    if (c % g != 0) {
        cout << "No solution\n";
        return 0;
    }

    x0 *= c / g;
    y0 *= c / g;

    cout << "One solution: " << x0 << " " << y0 << "\n";

    cout << "Some generated solutions:\n";

    for (long long k = -3; k <= 3; k++) {

        long long x = x0 + k * (b / g);
        long long y = y0 - k * (a / g);

        cout << "k=" << k << " -> x=" << x << ", y=" << y << "\n";
    }

    return 0;
}
```

---

## 11. Problem Form 4 — Coin Equation

### Problem

Can we form amount `c` using coins of sizes `a` and `b`?

Equation:

```text
ax + by = c
```

where:

```text
x, y are number of coins
```

If negative coin counts are allowed mathematically, use Diophantine existence.

If coin counts must be non-negative:

```text
x >= 0
y >= 0
```

then extra range constraints are needed.

---

### Step-by-Step Working

Example:

```text
coin sizes = 6 and 9
amount = 30
```

Equation:

```text
6x + 9y = 30
```

gcd:

```text
gcd(6,9)=3
```

Check:

```text
30 % 3 = 0
```

Integer solution exists.

One non-negative solution:

```text
x=5, y=0
```

---

## 12. Problem Form 5 — Non-Negative Solutions

### Problem

Find whether:

```text
ax + by = c
```

has solution with:

```text
x >= 0, y >= 0
```

---

### Idea

First find one integer solution:

```text
x0, y0
```

Then use:

```text
x = x0 + k*(b/g)
y = y0 - k*(a/g)
```

Need:

```text
x >= 0
y >= 0
```

This creates bounds on `k`.

---

### Step-by-Step Working

For:

```text
6x + 9y = 30
```

General solution:

```text
x = -10 + 3k
y = 10 - 2k
```

Need:

```text
-10 + 3k >= 0
```

So:

```text
k >= 4
```

Need:

```text
10 - 2k >= 0
```

So:

```text
k <= 5
```

Valid k:

```text
4, 5
```

Solutions:

For `k=4`:

```text
x=2, y=2
```

For `k=5`:

```text
x=5, y=0
```

---

## 13. Problem Form 6 — Minimum Positive Solution

### Problem

Find one solution where:

```text
x >= 0
y >= 0
```

and maybe minimize:

```text
x + y
```

or minimize `x`.

---

### Method

Use general solution:

```text
x = x0 + k*(b/g)
y = y0 - k*(a/g)
```

Then find valid interval of `k`.

Evaluate boundary values because linear objective is optimized at interval edges.

---

## 14. Dry Run

Solve:

```text
15x + 25y = 100
```

Step 1:

```text
gcd(15,25)=5
```

Step 2:

```text
100 % 5 = 0
```

Solution exists.

Step 3:

Extended Euclid gives:

```text
15*2 + 25*(-1) = 5
```

Step 4:

Scale by:

```text
100/5 = 20
```

One solution:

```text
x = 40
y = -20
```

Step 5:

General solution:

```text
x = 40 + k*(25/5)
  = 40 + 5k

y = -20 - k*(15/5)
  = -20 - 3k
```

Step 6:

Find non-negative solution.

Need:

```text
40 + 5k >= 0
=> k >= -8
```

Need:

```text
-20 - 3k >= 0
=> k <= -7
```

Valid:

```text
k = -8, -7
```

For `k=-8`:

```text
x=0
y=4
```

For `k=-7`:

```text
x=5
y=1
```

Both valid.

---

## 15. Common Mistakes

### Mistake 1 — Checking only gcd but not constructing solution

Existence:

```text
c % gcd(a,b) == 0
```

Construction:

```text
needs Extended Euclid
```

---

### Mistake 2 — Forgetting to scale

Extended Euclid gives solution for:

```text
ax + by = gcd(a,b)
```

But target is:

```text
ax + by = c
```

So multiply by:

```text
c/g
```

---

### Mistake 3 — Wrong general solution signs

Correct:

```text
x = x0 + k*(b/g)
y = y0 - k*(a/g)
```

---

### Mistake 4 — Confusing integer solution with non-negative solution

Integer solution may exist, but non-negative solution may not.

Example:

```text
4x + 10y = 6
```

Integer solution exists because gcd is 2 and 6 divisible by 2.

But non-negative solution does not exist.

---

### Mistake 5 — Overflow

When `a,b,c` are large:

```text
x * c/g
```

may overflow.

Use:

```cpp
long long
```

or:

```cpp
__int128
```

for very large constraints.

---

## 16. Decision Tree

```text
Equation ax + by = c?
|
+-- Need integer x,y?
|   |
|   +-- Compute g = gcd(a,b)
|       |
|       +-- c % g != 0
|       |   |
|       |   +-- No solution
|       |
|       +-- c % g == 0
|           |
|           +-- Use Extended Euclid
|           |
|           +-- Scale by c/g
|           |
|           +-- Need all solutions?
|           |   |
|           |   +-- Use k parameter
|           |
|           +-- Need x,y >= 0?
|               |
|               +-- Convert to k interval
```

---

## 17. Real World Mapping

| Real World Scenario | Diophantine Mapping |
|---|---|
| Coin combinations | `ax + by = amount` |
| Package sizing | combine pack sizes to target |
| Scheduling cycles | align periodic jobs |
| Inventory batching | batch sizes reaching target |
| Resource allocation | two resource units forming total |
| Payment denominations | amount formation |
| Load balancing chunks | combine chunk sizes |
| Network packet sizing | payload split into fixed blocks |

---

## 18. Complexity

Extended Euclid:

```text
O(log min(a,b))
```

Finding one solution:

```text
O(log min(a,b))
```

Generating solutions:

```text
O(number of printed solutions)
```

---

## 19. Reusable C++ Template

```cpp
#include <bits/stdc++.h>
using namespace std;

long long extendedGcd(long long a, long long b, long long &x, long long &y) {

    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;

    long long g = extendedGcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

// Finds one solution to ax + by = c
bool findAnySolution(long long a, long long b, long long c,
                     long long &x, long long &y, long long &g) {

    g = extendedGcd(abs(a), abs(b), x, y);

    if (c % g != 0) {
        return false;
    }

    x *= c / g;
    y *= c / g;

    if (a < 0) x = -x;
    if (b < 0) y = -y;

    return true;
}

int main() {

    long long a = 6, b = 9, c = 30;

    long long x, y, g;

    if (!findAnySolution(a, b, c, x, y, g)) {
        cout << "No solution\n";
        return 0;
    }

    cout << "One solution: " << x << " " << y << "\n";

    cout << "General solution:\n";
    cout << "x = " << x << " + k*" << b/g << "\n";
    cout << "y = " << y << " - k*" << a/g << "\n";

    return 0;
}
```

---

## 20. CP / FAANG Problem Forms

---

### Problem 1 — Check If Amount Can Be Formed

#### Problem

Given two coin values `a` and `b`, check whether amount `c` can be formed as:

```text
ax + by = c
```

where `x,y` are integers.

---

#### Pattern

```text
gcd divisibility
```

---

#### Step-by-Step Working

Example:

```text
a = 6
b = 9
c = 30
```

Step 1:

```text
gcd(6,9)=3
```

Step 2:

```text
30 % 3 = 0
```

Answer:

```text
possible
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long a = 6, b = 9, c = 30;

    long long g = gcd(a, b);

    // ax + by = c has integer solution iff gcd(a,b) divides c.
    if (c % g == 0) {
        cout << "YES\n";
    } else {
        cout << "NO\n";
    }

    return 0;
}
```

---

### Problem 2 — Find One Solution To ax + by = c

#### Problem

Find one integer pair `(x,y)` satisfying:

```text
6x + 9y = 30
```

---

#### Pattern

```text
Extended Euclid + scale
```

---

#### Step-by-Step Working

Step 1:

```text
Extended Euclid gives:
6*(-1) + 9*(1) = 3
```

Step 2:

Scale by:

```text
30/3 = 10
```

Step 3:

```text
x = -10
y = 10
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long egcd(long long a, long long b, long long &x, long long &y) {

    if (b == 0) {
        x = 1;
        y = 0;
        return a;
    }

    long long x1, y1;

    long long g = egcd(b, a % b, x1, y1);

    x = y1;
    y = x1 - (a / b) * y1;

    return g;
}

int main() {

    long long a = 6, b = 9, c = 30;

    long long x, y;

    long long g = egcd(a, b, x, y);

    if (c % g != 0) {
        cout << "No solution\n";
        return 0;
    }

    // Scale solution of ax + by = gcd(a,b)
    // to solution of ax + by = c.
    x *= c / g;
    y *= c / g;

    cout << x << " " << y << endl;

    return 0;
}
```

---

### Problem 3 — Generate All Solutions

#### Problem

Generate solutions of:

```text
6x + 9y = 30
```

---

#### Pattern

```text
general solution using k
```

---

#### Step-by-Step Working

One solution:

```text
x0 = -10
y0 = 10
```

General:

```text
x = -10 + 3k
y = 10 - 2k
```

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long x0 = -10;
    long long y0 = 10;

    long long a = 6;
    long long b = 9;
    long long g = gcd(a, b);

    for (long long k = -3; k <= 5; k++) {

        long long x = x0 + k * (b / g);
        long long y = y0 - k * (a / g);

        cout << "k=" << k << " x=" << x << " y=" << y << "\n";
    }

    return 0;
}
```

---

### Problem 4 — Find Non-Negative Solution

#### Problem

Find whether:

```text
6x + 9y = 30
```

has:

```text
x >= 0, y >= 0
```

---

#### Pattern

```text
general solution + k range
```

---

#### Step-by-Step Working

General:

```text
x = -10 + 3k
y = 10 - 2k
```

Need:

```text
x >= 0 -> k >= 4
y >= 0 -> k <= 5
```

Valid:

```text
k = 4 or 5
```

So non-negative solution exists.

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {

    long long x0 = -10;
    long long y0 = 10;

    long long a = 6;
    long long b = 9;
    long long g = gcd(a, b);

    long long stepX = b / g;
    long long stepY = a / g;

    bool found = false;

    for (long long k = -100; k <= 100; k++) {

        long long x = x0 + k * stepX;
        long long y = y0 - k * stepY;

        if (x >= 0 && y >= 0) {
            cout << "Found: x=" << x << ", y=" << y << "\n";
            found = true;
        }
    }

    if (!found) {
        cout << "No non-negative solution in checked range\n";
    }

    return 0;
}
```

---

### Problem 5 — Minimum Coin Count

#### Problem

Given coin sizes `a` and `b`, form amount `c` using minimum number of coins:

```text
minimize x + y
```

subject to:

```text
ax + by = c
x >= 0, y >= 0
```

---

#### Pattern

```text
Diophantine + optimize boundary k
```

---

#### Step-by-Step Working

General solution:

```text
x = x0 + k*(b/g)
y = y0 - k*(a/g)
```

Objective:

```text
x + y
```

Since both are linear in `k`, minimum occurs at boundary of valid k range.

---

#### Commented C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

// For teaching simplicity, this example brute checks k range.
// For large constraints, compute k interval mathematically.

int main() {

    long long a = 6, b = 9, c = 30;

    long long best = LLONG_MAX;
    pair<long long,long long> ans = {-1, -1};

    for (long long x = 0; x <= c / a; x++) {

        long long remaining = c - a * x;

        if (remaining < 0) continue;

        if (remaining % b == 0) {

            long long y = remaining / b;

            if (x + y < best) {
                best = x + y;
                ans = {x, y};
            }
        }
    }

    if (best == LLONG_MAX) {
        cout << "No solution\n";
    } else {
        cout << ans.first << " " << ans.second << "\n";
    }

    return 0;
}
```

---

## 21. Practice Checklist

Before solving:

```text
1. Is the equation ax + by = c?
2. Do x,y need to be integers?
3. Compute gcd(a,b).
4. Does gcd divide c?
5. Need one solution?
6. Use Extended Euclid.
7. Need all solutions?
8. Use k parameter.
9. Need x,y >= 0?
10. Convert to k interval.
11. Need optimize x+y?
12. Check interval boundaries.
```

---

## 22. Next Step

```text
019_Floor_Sum.md
```

Used in:

```text
floor division patterns
sum of floors
number theory optimization
lattice counting
AtCoder-style math
```
