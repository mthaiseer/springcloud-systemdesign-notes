# AlgoZenith — Ternary Search
## Unimodal Functions + AtCoder D — Freefall
### TLE-Style Self-Study Notes

> Source: supplied AlgoZenith lecture **Ternary Search**.
>
> Main idea:
>
> ```text
> one hill / one valley
> + numeric search space
> → Ternary Search
> ```

---

# Table of Contents

- [1. What Ternary Search Is For](#1-what-ternary-search-is-for)
- [2. Prerequisite — Unimodal Function](#2-prerequisite--unimodal-function)
- [3. Two Internal Points](#3-two-internal-points)
- [4. Which Side Do We Remove?](#4-which-side-do-we-remove)
- [5. Integer vs Real Domain](#5-integer-vs-real-domain)
- [6. Integer Ternary Search Template](#6-integer-ternary-search-template)
- [7. Problem — Freefall](#7-problem--freefall)
- [8. Story Removed](#8-story-removed)
- [9. Derive the Cost Function](#9-derive-the-cost-function)
- [10. Why There Is One Minimum](#10-why-there-is-one-minimum)
- [11. Calculus Derivation from the Lecture](#11-calculus-derivation-from-the-lecture)
- [12. Search-Space Bound](#12-search-space-bound)
- [13. Compact Ternary Dry Run](#13-compact-ternary-dry-run)
- [14. Proper C++17 — Ternary Search](#14-proper-c17--ternary-search)
- [15. Why Brute Force the Final Few Values?](#15-why-brute-force-the-final-few-values)
- [16. Alternative — Binary Search on `f(mid)` vs `f(mid+1)`](#16-alternative--binary-search-on-fmid-vs-fmid1)
- [17. Proper C++17 — Binary Search Alternative](#17-proper-c17--binary-search-alternative)
- [18. Debugging Trick — `assert`](#18-debugging-trick--assert)
- [19. Complexity](#19-complexity)
- [20. Common Mistakes](#20-common-mistakes)
- [21. Recognition Pattern](#21-recognition-pattern)
- [22. Final Mental Model](#22-final-mental-model)

---

# 1. What Ternary Search Is For

Ternary Search is useful when a function has a single optimum.

### Maximum / hill

```text
value
  ^
  |             /\
  |           /    \
  |         /        \
  |_______/____________\______> x
                 ^
               maximum
```

### Minimum / valley

```text
value
  ^
  |       \              /
  |        \            /
  |         \__________/
  |_____________^______________> x
                |
              minimum
```

The lecture describes these as:

```text
maximum → convex-down shape
minimum → convex-up shape
```

The important property is that the function is **unimodal**.

---

# 2. Prerequisite — Unimodal Function

For minimization:

```text
decreasing → minimum → increasing
```

Example:

```text
x:     0  1  2  3  4  5  6
f(x): 10  8  6  4  5  7  9
                  ^
                  |
               minimum
```

For maximization:

```text
increasing → maximum → decreasing
```

Ternary Search works because comparing two internal points tells us one side cannot contain the optimum.

---

# 3. Two Internal Points

Current interval:

```text
low ------------------------------- high
```

Split it into three parts:

```text
low -------- mid1 -------- mid2 -------- high
```

Lecture formulas:

```text
mid1 = (2*low + high) / 3
mid2 = (low + 2*high) / 3
```

Equivalent intuition:

```text
mid1 ≈ low  + (high-low)/3
mid2 ≈ high - (high-low)/3
```

---

# 4. Which Side Do We Remove?

For a **minimum**:

## Case 1

```text
f(mid1) > f(mid2)
```

The function is still going downward:

```text
       *
        \
         *
       mid1 mid2
```

So the minimum is toward the right.

```cpp
low = mid1;
```

## Case 2

```text
f(mid1) <= f(mid2)
```

The minimum is not beyond `mid2`.

```cpp
high = mid2;
```

Compact rule:

```cpp
if (f(mid1) > f(mid2)) {
    low = mid1;
} else {
    high = mid2;
}
```

For a maximum, reverse the comparison logic.

---

# 5. Integer vs Real Domain

The lecture distinguishes two cases.

## Integer domain

Candidates are discrete:

```text
0 1 2 3 4 5 ...
```

Shrink until only a few integers remain:

```text
high - low < 3
```

Then directly evaluate all remaining values.

## Real domain

Candidates are continuous:

```text
0.0
0.1
0.123
0.123456
...
```

Ternary Search is naturally compatible with floating-point precision.

---

# 6. Integer Ternary Search Template

For minimization:

```cpp
while (high - low >= 3) {
    ll mid1 = (2 * low + high) / 3;
    ll mid2 = (low + 2 * high) / 3;

    if (f(mid1) > f(mid2)) {
        low = mid1;
    } else {
        high = mid2;
    }
}

for (ll x = low; x <= high; x++) {
    answer = min(answer, f(x));
}
```

Mental model:

```text
Ternary Search
→ shrink quickly

few integer candidates remain
→ brute force exactly
```

---

# 7. Problem — Freefall

The lecture applies Ternary Search to **D — Freefall**.

## What It Asks

Initially:

```text
gravity g = 1
```

One operation:

```text
increase g by 1
```

costs:

```text
B units of time
```

After stopping operations, falling takes:

```text
A / sqrt(g)
```

We may perform the operation any number of times.

Goal:

```text
minimum total time
```

---

## Small Dry Run from the Lecture

For:

```text
A = 10
B = 1
```

### 0 operations

```text
g = 1

total
=
0*1 + 10/sqrt(1)
=
10
```

### 1 operation

```text
g = 2

total
=
1 + 10/sqrt(2)
≈
8.07
```

### 2 operations

```text
g = 3

total
=
2 + 10/sqrt(3)
≈
7.77
```

### 3 operations

```text
g = 4

total
=
3 + 10/sqrt(4)
=
8
```

So:

```text
10 → 8.07 → 7.77 → 8
               ^
               |
            minimum
```

The values decrease and then increase.

That is a Ternary Search shape.

---

# 8. Story Removed

Let:

```text
operations = number of times we increase gravity
```

Then:

```text
gravity = operations + 1
```

Operation time:

```text
B * operations
```

Falling time:

```text
A / sqrt(operations + 1)
```

Therefore:

```text
f(operations)
=
B*operations + A/sqrt(operations+1)
```

Problem:

```text
find integer operations >= 0
that minimizes f(operations)
```

---

# 9. Derive the Cost Function

Suppose:

```text
operations = 2
```

Then:

```text
gravity = 3
```

Operation cost:

```text
2B
```

Falling cost:

```text
A / sqrt(3)
```

Total:

```text
f(2)
=
2B + A/sqrt(3)
```

Generalization:

```text
operations = x

gravity = x+1

f(x)
=
xB + A/sqrt(x+1)
```

ASCII:

```text
TOTAL TIME

+---------------------------+
| operation time = x*B      |
+---------------------------+
| fall time = A/sqrt(x+1)   |
+---------------------------+
```

---

# 10. Why There Is One Minimum

Two terms move in opposite directions.

## Operation term

```text
xB
```

increases:

```text
time
 ^
 |        /
 |      /
 |    /
 |___/________> x
```

## Falling term

```text
A/sqrt(x+1)
```

decreases:

```text
time
 ^
 |\
 | \
 |  \
 |   \____
 |__________> x
```

Their sum forms a valley:

```text
decreasing
    \
     \
      \__
         \__
            \___
                \__
                   /
                  /
                 /
                /
           increasing
```

Therefore there is one minimizing region.

---

# 11. Calculus Derivation from the Lecture

The lecture also derives the continuous minimum.

Here it uses:

```text
x = gravity
```

Since gravity starts at `1`:

```text
number of operations = x - 1
```

So:

```text
f(x)
=
(x-1)B + A/sqrt(x)
```

Rewrite:

```text
f(x)
=
(x-1)B + A*x^(-1/2)
```

Differentiate:

```text
f'(x)
=
B - (A/2)x^(-3/2)
```

At the minimum:

```text
f'(x) = 0
```

Therefore:

```text
B - (A/2)x^(-3/2) = 0
```

Move the second term:

```text
B
=
(A/2)x^(-3/2)
```

So:

```text
2B/A
=
x^(-3/2)
```

Take reciprocal:

```text
x^(3/2)
=
A/(2B)
```

Raise both sides to `2/3`:

```text
x
=
(A/(2B))^(2/3)
```

This shows mathematically why a minimum exists.

### Important notation difference

Lecture calculus section:

```text
x = gravity
```

Lecture code:

```text
operations = number of operations
```

So keep these distinct:

```text
gravity = operations + 1
```

---

# 12. Search-Space Bound

Initial total time with no operation:

```text
A
```

because:

```text
A/sqrt(1) = A
```

If operation cost alone becomes greater than `A`:

```text
B * operations > A
```

then spending that much time only on operations does not make sense compared with the initial baseline.

So the lecture keeps the search space around:

```text
0 <= operations <= A/B
```

ASCII:

```text
0 --------------------------- A/B -------------------->
|<------ useful region ------>|
```

The Ternary Search code uses:

```cpp
ll low = 0;
ll high = a / b;
```

---

# 13. Compact Ternary Dry Run

Teaching example:

```text
x:     0  1  2  3  4  5  6  7  8
f(x): 10  8  6  4  5  7  9 11 13
                  ^
                  |
               minimum
```

Start:

```text
low = 0
high = 8
```

### Iteration 1

```text
mid1 = (2*0 + 8)/3 = 2
mid2 = (0 + 2*8)/3 = 5

f(2) = 6
f(5) = 7
```

Since:

```text
6 <= 7
```

discard right side after `mid2`:

```text
high = 5
```

### Iteration 2

```text
low = 0
high = 5

mid1 = 1
mid2 = 3

f(1) = 8
f(3) = 4
```

Since:

```text
8 > 4
```

minimum is toward the right:

```text
low = 1
```

When the interval becomes small, evaluate all remaining integers directly.

---

# 14. Proper C++17 — Ternary Search

Code is formatted in the compact style used in the earlier notes.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using ld = long double;

ll a, b;

ld func(ll operations) {
    return ld(b) * operations + ld(a) / sqrt(ld(operations + 1));
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> a >> b;

    ll low = 0;
    ll high = a / b;

    while (high - low >= 3) {
        ll mid1 = (2 * low + high) / 3;
        ll mid2 = (low + 2 * high) / 3;

        if (func(mid1) > func(mid2)) {
            low = mid1;
        } else {
            high = mid2;
        }
    }

    ld answer = a;

    for (ll operations = low; operations <= high; operations++) {
        answer = min(answer, func(operations));
    }

    cout << fixed << setprecision(15) << answer << '\n';

    return 0;
}
```

### Core function

```cpp
ld func(ll operations) {
    return ld(b) * operations + ld(a) / sqrt(ld(operations + 1));
}
```

Read:

```text
operation cost
+
falling cost
```

### Core Ternary Search

```cpp
while (high - low >= 3) {
    ll mid1 = (2 * low + high) / 3;
    ll mid2 = (low + 2 * high) / 3;

    if (func(mid1) > func(mid2)) {
        low = mid1;
    } else {
        high = mid2;
    }
}
```

---

# 15. Why Brute Force the Final Few Values?

When:

```text
high - low < 3
```

only a few integers remain.

So simply do:

```cpp
for (ll operations = low; operations <= high; operations++) {
    answer = min(answer, func(operations));
}
```

This avoids delicate integer boundary handling.

---

# 16. Alternative — Binary Search on `f(mid)` vs `f(mid+1)`

The lecture also shows an integer Binary Search trick.

For a valley:

```text
decreasing → minimum → increasing
```

compare:

```text
f(mid)
```

with:

```text
f(mid+1)
```

## If

```text
f(mid) < f(mid+1)
```

we are on the increasing side or at the turning point.

So the minimum is:

```text
mid or to the left
```

Use:

```cpp
answer = mid;
high = mid - 1;
```

## Else

```text
f(mid) >= f(mid+1)
```

the function is still decreasing.

So:

```cpp
low = mid + 1;
```

This creates a monotonic Boolean condition:

```text
f(x) < f(x+1) ?

0 0 0 0 1 1 1 1
        ^
        |
   first turning point
```

---

# 17. Proper C++17 — Binary Search Alternative

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;
using ld = long double;

ll a, b;

ld func(ll operations) {
    return ld(b) * operations + ld(a) / sqrt(ld(operations + 1));
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> a >> b;

    ll low = 0;
    ll high = (a + b - 1) / b;
    ll answer = -1;

    while (low <= high) {
        ll mid = low + (high - low) / 2;

        if (func(mid) < func(mid + 1)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    assert(answer != -1);

    cout << fixed << setprecision(15) << func(answer) << '\n';

    return 0;
}
```

---

# 18. Debugging Trick — `assert`

The lecture shows:

```cpp
assert(answer != -1);
```

Meaning:

```text
I expect a valid answer to have been found.
```

If:

```text
answer == -1
```

the assertion fails and the program stops.

This is a debugging/validation trick, not part of Ternary Search itself.

---

# 19. Complexity

Let the search range size be:

```text
R
```

Each Ternary Search step removes a constant fraction.

Therefore:

```text
Time: O(log R)
```

Each function evaluation is:

```text
O(1)
```

Final brute force checks only a constant number of candidates.

Memory:

```text
O(1)
```

For the lecture problem:

```text
R ≈ A/B
```

---

# 20. Common Mistakes

## 1. Using Ternary Search without a one-hill/one-valley shape

Wrong:

```text
up → down → up → down
```

Ternary Search needs a single optimum region.

---

## 2. Reversing the comparison

For minimum:

```cpp
if (func(mid1) > func(mid2)) {
    low = mid1;
} else {
    high = mid2;
}
```

---

## 3. Forgetting integer cleanup

After shrinking:

```text
check every remaining integer
```

---

## 4. Mixing the two meanings of `x`

Calculus:

```text
x = gravity
```

Code:

```text
operations = number of operations
```

Relation:

```text
gravity = operations + 1
```

---

## 5. Forgetting output precision

The answer is real-valued.

Lecture code prints:

```cpp
cout << fixed << setprecision(15) << answer << '\n';
```

---

## 6. Searching an unnecessarily huge range

The lecture derives a useful upper scale around:

```text
A/B
```

Always try to mathematically bound the optimum.

---

# 21. Recognition Pattern

Think **Ternary Search** when you see:

```text
choose numeric x
+
optimize f(x)
+
f(x) has one valley or one hill
```

Recognition:

```text
one variable
    |
    v
objective f(x)
    |
    v
decrease then increase?
or
increase then decrease?
    |
   YES
    |
    v
Ternary Search
```

For an integer valley also remember:

```text
f(mid) vs f(mid+1)
```

may convert the problem into Binary Search.

---

# 22. Final Mental Model

```text
                    f(x)
                     |
          +----------+----------+
          |                     |
          v                     v
       one hill              one valley
       MAXIMUM               MINIMUM
          |                     |
          +----------+----------+
                     |
                     v
               TERNARY SEARCH
                     |
                     v
      low ---- mid1 ---- mid2 ---- high
                     |
                     v
              compare f(mid1)
                  and f(mid2)
                     |
             discard one side
                     |
                     v
               repeat shrinking
                     |
                     v
          few integers remain
                     |
                     v
               brute force
```

For Freefall:

```text
f(operations)
=
B*operations
+
A/sqrt(operations+1)
```

The first term increases.

The second term decreases.

Their sum forms one valley.

Therefore:

```text
Ternary Search → minimum total time
```

And the lecture's integer alternative is:

```text
compare f(mid) and f(mid+1)
→ Binary Search the turning point
```

### 60-second recognition

```text
One numeric variable
+
one hill / one valley
=
Ternary Search
```
