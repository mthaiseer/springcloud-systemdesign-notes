# AlgoZenith — Ideas Based on Fractions
## Optimized TLE-Style Notes — Reduced Fractions + Pair Counting + Maximum Collinear Points

> **Source:** supplied AlgoZenith **Ideas Based on Fractions** lecture screenshots.
>
> **Main lecture idea:** whenever equality depends on a ratio, avoid floating point. Convert every ratio to one **canonical reduced fraction** and use it as a map key.
>
> **Problem flow used throughout:**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → derivation
> → one detailed dry run
> → algorithm
> → C++17
> → complexity
> → recognition model
> ```
>
> **Variable rule**
>
> - Explanations/code: descriptive names such as `fractionKey`, `fractionFrequency`, `slopeFrequency`, `duplicatePointCount`.
> - Proofs: shorter readable names such as `a`, `b`, `i`, `j`, `dy`, `dx`.
> - Use `long long`.
> - No floating-point ratio comparison.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Fraction Equality](#11-fraction-equality)
  - [1.2 Cross Multiplication](#12-cross-multiplication)
  - [1.3 Why `double` Is a Bad Map Key](#13-why-double-is-a-bad-map-key)
  - [1.4 Reduced Fraction / Canonical Form](#14-reduced-fraction--canonical-form)
  - [1.5 GCD Reduction](#15-gcd-reduction)
  - [1.6 Sign Normalization](#16-sign-normalization)
  - [1.7 Zero Numerator / Zero Denominator](#17-zero-numerator--zero-denominator)
  - [1.8 `pair<long long,long long>` as a Map Key](#18-pairlong-longlong-long-as-a-map-key)
  - [1.9 Online Pair Counting](#19-online-pair-counting)
  - [1.10 Slope as a Fraction](#110-slope-as-a-fraction)
  - [1.11 Fix One Point — Equal Slope Means Same Line](#111-fix-one-point--equal-slope-means-same-line)
  - [1.12 Duplicate Points](#112-duplicate-points)
  - [1.13 Overflow](#113-overflow)
  - [1.14 Recognition Checklist](#114-recognition-checklist)
- [2. Pattern 1 — Count Pairs With `A[i] * j = A[j] * i`](#2-pattern-1--count-pairs-with-ai--j--aj--i)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Small Dry Run First](#22-small-dry-run-first)
  - [2.3 Core Observation](#23-core-observation)
  - [2.4 Derivation — Product Equality to Fraction Equality](#24-derivation--product-equality-to-fraction-equality)
  - [2.5 Why Reduced Fractions Solve the Grouping Problem](#25-why-reduced-fractions-solve-the-grouping-problem)
  - [2.6 Online Counting Derivation](#26-online-counting-derivation)
  - [2.7 Detailed Dry Run](#27-detailed-dry-run)
  - [2.8 Algorithm](#28-algorithm)
  - [2.9 C++17](#29-c17)
  - [2.10 Complexity](#210-complexity)
  - [2.11 Recognition Model](#211-recognition-model)
- [3. Pattern 2 — Maximum Points on One Line](#3-pattern-2--maximum-points-on-one-line)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run First](#32-small-dry-run-first)
  - [3.3 Core Observation](#33-core-observation)
  - [3.4 Derivation — Same Anchor + Same Slope](#34-derivation--same-anchor--same-slope)
  - [3.5 Why Slope Must Be Reduced](#35-why-slope-must-be-reduced)
  - [3.6 Vertical / Horizontal / Duplicate Cases](#36-vertical--horizontal--duplicate-cases)
  - [3.7 Detailed Dry Run](#37-detailed-dry-run)
  - [3.8 Algorithm](#38-algorithm)
  - [3.9 C++17](#39-c17)
  - [3.10 Complexity](#310-complexity)
  - [3.11 Recognition Model](#311-recognition-model)
- [Final Mental Model](#final-mental-model)

---

# 0. Class Map

The lecture develops one reusable technique:

```text
RATIO EQUALITY
     |
     v
do NOT store decimal value
     |
     v
reduce numerator / denominator
using GCD
     |
     v
normalize sign
     |
     v
canonical pair:
(numerator, denominator)
     |
     +----------------------------+
     |                            |
     v                            v
count equal ratios          count equal slopes
     |                            |
     v                            v
pair counting             maximum collinear points
```

Two applications shown:

```text
1. A[i] * j = A[j] * i
   → A[i] / i = A[j] / j
   → count equal reduced fractions

2. Maximum points on one line
   → fix one point
   → slope = dy / dx
   → count equal reduced slopes
```

---

# 1. Prerequisites

## 1.1 Fraction Equality

Two fractions:

```text
a / b
```

and:

```text
c / d
```

represent the same ratio if they reduce to the same canonical fraction.

Example:

```text
3 / 2
=
150 / 100
```

because:

```text
150 / 100
```

reduces by GCD `50`:

```text
=
3 / 2
```

So both should produce the same map key:

```text
(3,2)
```

---

## 1.2 Cross Multiplication

For nonzero denominators:

```text
a / b
=
c / d
```

is equivalent to:

```text
a*d
=
c*b
```

### Example

```text
3 / 2
=
6 / 4
```

Cross multiply:

```text
3*4
=
6*2

12
=
12
```

This exact algebra appears in Pattern 1.

---

## 1.3 Why `double` Is a Bad Map Key

Tempting:

```cpp
double ratio = numerator / denominator;
```

Problems:

```text
1. integer division may already lose information
2. floating-point values are approximate
3. logically equal fractions may be unsafe to compare as decimals
4. vertical slope needs division by zero handling
```

Better:

```text
6 / 4
→ reduce
→ 3 / 2
→ key (3,2)
```

No precision issue.

---

## 1.4 Reduced Fraction / Canonical Form

A fraction key must have exactly one representation.

Bad:

```text
2 / 4
3 / 6
50 / 100
```

These are mathematically equal, but as raw pairs:

```text
(2,4)
(3,6)
(50,100)
```

they look different.

Canonical form:

```text
2 / 4   → 1 / 2
3 / 6   → 1 / 2
50 / 100 → 1 / 2
```

So all become:

```text
(1,2)
```

That is what makes map counting possible.

---

## 1.5 GCD Reduction

For:

```text
numerator = 150
denominator = 100
```

find:

```text
gcd(150,100)
=
50
```

Divide both:

```text
150 / 50 = 3
100 / 50 = 2
```

Canonical fraction:

```text
3 / 2
```

General:

```text
common = gcd(|numerator|, |denominator|)

reducedNumerator   = numerator / common
reducedDenominator = denominator / common
```

---

## 1.6 Sign Normalization

These all mean the same value:

```text
-2 / 3
2 / -3
```

But raw pairs differ:

```text
(-2,3)
(2,-3)
```

We need one convention.

Use:

```text
denominator >= 0
```

So:

```text
-2 / 3
→ (-2,3)

2 / -3
→ (-2,3)
```

And:

```text
-2 / -3
→ (2,3)
```

ASCII:

```text
sign lives only in numerator

(-,+) → negative
(+,-) → negative
(-,-) → positive
```

---

## 1.7 Zero Numerator / Zero Denominator

The lecture helper explicitly distinguishes special cases.

### Both zero

```text
0 / 0
```

Lecture key:

```text
(0,0)
```

### Numerator zero

```text
0 / b
```

for nonzero `b`:

```text
(0,1)
```

### Denominator zero

```text
a / 0
```

for nonzero `a`:

```text
(1,0)
```

This is especially useful for slopes:

```text
dx = 0
→ vertical line
→ key (1,0)
```

and:

```text
dy = 0
→ horizontal line
→ key (0,1)
```

### Lecture-Style Helper

```cpp
pair<long long, long long> reduceFraction(
    long long numerator,
    long long denominator
) {
    if (numerator == 0 && denominator == 0)
        return {0, 0};

    if (numerator == 0)
        return {0, 1};

    if (denominator == 0)
        return {1, 0};

    long long sign = 1;

    if (numerator < 0) {
        sign *= -1;
        numerator *= -1;
    }

    if (denominator < 0) {
        sign *= -1;
        denominator *= -1;
    }

    long long common =
        gcd(numerator, denominator);

    return {
        sign * numerator / common,
        denominator / common
    };
}
```

---

## 1.8 `pair<long long,long long>` as a Map Key

After normalization:

```text
ratio
→
(reducedNumerator, reducedDenominator)
```

So we can use:

```cpp
map<pair<long long, long long>, long long> frequency;
```

Example keys:

```text
(3,2)  → 4 occurrences
(1,0)  → 2 occurrences
(0,1)  → 5 occurrences
```

A `pair` has lexicographic ordering, so it works directly as a key in `std::map`.

---

## 1.9 Online Pair Counting

Suppose the same key appears `k` times before the current item.

The current item forms:

```text
k
```

new pairs.

So:

```text
answer += frequency[key]
frequency[key]++
```

### Tiny Example

Keys arrive:

```text
X, X, X
```

Processing:

```text
first X:
previous = 0
answer += 0

second X:
previous = 1
answer += 1

third X:
previous = 2
answer += 2
```

Total:

```text
0 + 1 + 2
=
3
```

Same as:

```text
3 choose 2
=
3
```

This is the exact counting pattern used in Pattern 1.

---

## 1.10 Slope as a Fraction

For two points:

```text
(x1,y1)
(x2,y2)
```

slope is:

```text
dy / dx
```

where:

```text
dy = y2 - y1
dx = x2 - x1
```

Example:

```text
(1,1)
(3,5)
```

Then:

```text
dy = 4
dx = 2
```

Slope:

```text
4 / 2
=
2 / 1
```

Canonical key:

```text
(2,1)
```

---

## 1.11 Fix One Point — Equal Slope Means Same Line

A line can be written:

```text
y = m*x + c
```

If one anchor point is fixed:

```text
anchor = (x0,y0)
```

then `c` is determined once slope `m` is chosen.

Therefore, relative to the same anchor:

```text
same slope
→ same line through the anchor
```

This converts a geometry problem into frequency counting.

ASCII:

```text
           P3
          /
         /
anchor--/---- P2
       /
      P1

from anchor:

slope(anchor,P1)
=
slope(anchor,P2)
=
slope(anchor,P3)

→ all on the same line
```

---

## 1.12 Duplicate Points

If:

```text
point[j]
=
point[i]
```

then:

```text
dy = 0
dx = 0
```

Slope is not a real direction.

The lecture handles exact duplicates separately:

```text
samePointCount++
```

Why?

A duplicate point can belong to **every line through the anchor**.

So for an anchor:

```text
candidate answer
=
duplicatePointCount
+
numberWithSameSlope
```

The loop includes the anchor itself in `duplicatePointCount`, because comparing the anchor with itself gives the same coordinates.

---

## 1.13 Overflow

The lecture shows very large values in the fraction-pair problem.

Use:

```cpp
long long
```

for:

```text
array values
coordinates
differences
counts
```

The fraction method is also useful because it avoids cross-multiplying huge values just to create a map key.

---

## 1.14 Recognition Checklist

When you see:

```text
x1 / y1 = x2 / y2
```

or:

```text
x1*y2 = x2*y1
```

think:

```text
canonical reduced fraction
```

When you see:

```text
same direction
same rate
same slope
same normalized ratio
```

think:

```text
GCD normalize
→ pair key
→ frequency map
```

When you see geometry:

```text
maximum points on one line
```

think:

```text
fix anchor
→ reduced slopes
→ count equal keys
```

---

# 2. Pattern 1 — Count Pairs With `A[i] * j = A[j] * i`

## 2.1 What It Asks

The lecture gives an array:

```text
A[0 ... N-1]
```

and asks for the number of index pairs:

```text
(i,j)
```

satisfying:

```text
i < j
```

and:

```text
A[i] * j
=
A[j] * i
```

The board shows large constraints, including approximately:

```text
N <= 1e5
A[i] <= 1e18
```

So checking all pairs:

```text
O(N^2)
```

is too slow.

We need to transform the condition.

---

## 2.2 Small Dry Run First

Take two nonzero indices:

```text
i = 2
A[i] = 3

j = 4
A[j] = 6
```

Check the original condition:

```text
A[i] * j
=
3 * 4
=
12
```

and:

```text
A[j] * i
=
6 * 2
=
12
```

So the pair is valid.

Now inspect ratios:

```text
A[i] / i
=
3 / 2
```

and:

```text
A[j] / j
=
6 / 4
=
3 / 2
```

Same fraction.

So instead of comparing every pair directly:

```text
group indices by reduced A[index] / index
```

---

## 2.3 Core Observation

Start from:

```text
A[i] * j
=
A[j] * i
```

This is exactly the cross-multiplication form of:

```text
A[i] / i
=
A[j] / j
```

So two indices form a valid pair precisely when their lecture-defined normalized fraction key is the same.

Therefore:

```text
index
→ A[index] / index
→ reduce
→ map key
```

Then count equal keys.

---

## 2.4 Derivation — Product Equality to Fraction Equality

Start:

```text
A[i] * j
=
A[j] * i
```

For ordinary nonzero denominators, divide both sides by:

```text
i*j
```

Left:

```text
(A[i] * j) / (i*j)
=
A[i] / i
```

Right:

```text
(A[j] * i) / (i*j)
=
A[j] / j
```

Therefore:

```text
A[i] / i
=
A[j] / j
```

### Inline Example

```text
A[i] = 3
i = 2

A[j] = 6
j = 4
```

Original:

```text
3*4
=
6*2

12
=
12
```

Fraction form:

```text
3/2
=
6/4
=
3/2
```

Same condition, easier to group.

### Index-Zero Convention

The lecture code calls:

```text
reduceFraction(A[index], index)
```

even when:

```text
index = 0
```

and uses the special fraction keys from Section 1.7:

```text
(0,0)
(0,1)
(1,0)
```

The notes preserve that lecture convention rather than replacing it with decimal division.

---

## 2.5 Why Reduced Fractions Solve the Grouping Problem

Suppose these raw ratios occur:

```text
3/2
6/4
150/100
```

Without reduction:

```text
(3,2)
(6,4)
(150,100)
```

all look different.

Reduce:

```text
3/2
→ (3,2)

6/4
→ (3,2)

150/100
→ (3,2)
```

Now equality becomes:

```text
pair key equality
```

So one map bucket represents one ratio class.

---

## 2.6 Online Counting Derivation

Suppose the current reduced key is:

```text
key
```

and:

```text
fractionFrequency[key]
=
k
```

That means:

```text
k earlier indices
```

already have the same ratio.

Every earlier one forms a valid pair with the current index.

So new pairs:

```text
k
```

Therefore:

```text
answer += fractionFrequency[key]
fractionFrequency[key]++
```

### Inline Example

Suppose ratio `3/2` appears three times.

Arrival:

```text
1st occurrence:
previous = 0
new pairs = 0

2nd occurrence:
previous = 1
new pairs = 1

3rd occurrence:
previous = 2
new pairs = 2
```

Total:

```text
0 + 1 + 2
=
3
```

which equals:

```text
C(3,2)
=
3
```

This is why we can count pairs in one pass.

---

## 2.7 Detailed Dry Run

To focus on the fraction grouping, suppose the generated normalized keys are:

```text
index:      0      1      2      3      4
key:       X      A      B      A      A
```

We process left to right.

| Index | Key | Previous frequency | New pairs | Running answer |
|---:|---|---:|---:|---:|
| `0` | `X` | `0` | `0` | `0` |
| `1` | `A` | `0` | `0` | `0` |
| `2` | `B` | `0` | `0` | `0` |
| `3` | `A` | `1` | `1` | `1` |
| `4` | `A` | `2` | `2` | `3` |

The three `A` positions create:

```text
3 pairs
```

without explicitly checking:

```text
all O(N^2) index pairs
```

---

## 2.8 Algorithm

```text
fractionFrequency = empty map
answer = 0

for index from 0 to N-1:

    read value

    fractionKey =
        reduceFraction(value, index)

    answer +=
        fractionFrequency[fractionKey]

    fractionFrequency[fractionKey]++

output answer
```

Mental meaning:

```text
current item asks:
"How many equivalent fractions
have I already seen?"
```

---

## 2.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<long long, long long> reduceFraction(
    long long numerator,
    long long denominator
) {
    if (numerator == 0 && denominator == 0)
        return {0, 0};

    if (numerator == 0)
        return {0, 1};

    if (denominator == 0)
        return {1, 0};

    long long sign = 1;

    if (numerator < 0) {
        sign *= -1;
        numerator *= -1;
    }

    if (denominator < 0) {
        sign *= -1;
        denominator *= -1;
    }

    long long common =
        gcd(numerator, denominator);

    return {
        sign * numerator / common,
        denominator / common
    };
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    map<
        pair<long long, long long>,
        long long
    > fractionFrequency;

    long long numberOfPairs = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        long long value;
        cin >> value;

        auto fractionKey =
            reduceFraction(value, index);

        numberOfPairs +=
            fractionFrequency[fractionKey];

        fractionFrequency[fractionKey]++;
    }

    cout << numberOfPairs << '\n';
    return 0;
}
```

---

## 2.10 Complexity

For each of `N` elements:

```text
GCD:
O(log value)
```

Ordered map access:

```text
O(log N)
```

Overall:

```text
O(N log N + N log value)
```

Usually summarized as:

```text
O(N log N)
```

with an additional logarithmic GCD factor.

Space:

```text
O(N)
```

for distinct fraction keys.

---

## 2.11 Recognition Model

When you see:

```text
A[i] * j
=
A[j] * i
```

do not immediately think:

```text
nested loops
```

Transform:

```text
A[i] / i
=
A[j] / j
```

Then:

```text
ratio equality
→ reduce fraction
→ map equal keys
→ online pair counting
```

Recognition chain:

```text
cross product equality
→ hidden fraction equality
→ canonical representation
→ frequency map
```

---

# 3. Pattern 2 — Maximum Points on One Line

## 3.1 What It Asks

Given:

```text
N points

(x1,y1)
(x2,y2)
...
(xN,yN)
```

find a line passing through the maximum number of points.

The lecture writes the line model:

```text
y = m*x + c
```

The key challenge:

```text
there are many possible lines
```

We need a counting representation.

---

## 3.2 Small Dry Run First

Points:

```text
P0 = (0,0)
P1 = (1,1)
P2 = (2,2)
P3 = (1,2)
```

Fix:

```text
anchor = P0
```

Slopes:

```text
P0 → P1:
(1-0)/(1-0)
=
1/1

P0 → P2:
(2-0)/(2-0)
=
2/2
=
1/1

P0 → P3:
(2-0)/(1-0)
=
2/1
```

Slope key frequencies:

```text
(1,1) → 2
(2,1) → 1
```

So through `P0`, the strongest direction contains:

```text
P0 + P1 + P2
=
3 points
```

---

## 3.3 Core Observation

Trying every line directly is awkward.

Instead:

```text
fix one point as anchor
```

Every other point defines one direction from that anchor.

For a fixed anchor:

```text
same reduced slope
→ same line through anchor
```

So for each anchor:

```text
count slope frequencies
```

Then take the maximum over all anchors.

ASCII:

```text
                 P3
                /
               /
      P2 -----/
      /
     /
anchor ----------- other direction
     \
      \
       P1
```

For the anchor:

```text
points sharing one slope key
belong to one line
```

---

## 3.4 Derivation — Same Anchor + Same Slope

Fix:

```text
anchor = (x0,y0)
```

Take two points:

```text
P1 = (x1,y1)
P2 = (x2,y2)
```

If they lie on the same line through the anchor:

```text
(y1-y0)/(x1-x0)
=
(y2-y0)/(x2-x0)
```

Cross multiply:

```text
(y1-y0)*(x2-x0)
=
(y2-y0)*(x1-x0)
```

So the same geometric line condition becomes another ratio-equality problem.

### Inline Example

Anchor:

```text
(0,0)
```

Points:

```text
P1 = (1,2)
P2 = (3,6)
```

Slopes:

```text
2/1
```

and:

```text
6/3
=
2/1
```

Reduced keys:

```text
(2,1)
(2,1)
```

Therefore both lie on the same line through the anchor.

---

## 3.5 Why Slope Must Be Reduced

Without reduction:

```text
P1 slope:
2/1

P2 slope:
6/3
```

Raw keys:

```text
(2,1)
(6,3)
```

different.

But geometrically:

```text
2/1
=
6/3
```

So reduce:

```text
(6,3)
→
(2,1)
```

Now map counting groups the points correctly.

This is exactly the same normalization idea as Pattern 1.

---

## 3.6 Vertical / Horizontal / Duplicate Cases

### Vertical Line

If:

```text
dx = 0
dy != 0
```

lecture helper gives:

```text
(1,0)
```

So all vertical directions use one key.

Example:

```text
anchor = (2,1)

points:
(2,5)
(2,9)
```

Both:

```text
dx = 0
```

so both map to:

```text
(1,0)
```

---

### Horizontal Line

If:

```text
dy = 0
dx != 0
```

key:

```text
(0,1)
```

Example:

```text
anchor = (1,4)

points:
(3,4)
(10,4)
```

Both map to:

```text
(0,1)
```

---

### Duplicate Point

If:

```text
dx = 0
dy = 0
```

the points are identical.

Do not treat this as a direction.

Instead:

```text
duplicatePointCount++
```

A duplicate belongs to every line through the anchor.

So:

```text
points on candidate line
=
duplicatePointCount
+
slopeFrequency[slope]
```

---

## 3.7 Detailed Dry Run

Points:

```text
P0 = (0,0)
P1 = (1,1)
P2 = (2,2)
P3 = (0,2)
P4 = (0,0)   // duplicate of P0
```

Fix:

```text
anchor = P0 = (0,0)
```

Initialize:

```text
duplicatePointCount = 0
slopeFrequency = {}
```

The lecture loop compares the anchor against all points, including itself.

### `j = 0`

```text
P0 == anchor
```

So:

```text
duplicatePointCount = 1
```

This includes the anchor itself.

---

### `j = 1`

```text
dy = 1
dx = 1

slope = 1/1
key = (1,1)
```

Map:

```text
(1,1) → 1
```

---

### `j = 2`

```text
dy = 2
dx = 2

2/2
→
1/1
```

Map:

```text
(1,1) → 2
```

---

### `j = 3`

```text
dy = 2
dx = 0

vertical
→
(1,0)
```

Map:

```text
(1,1) → 2
(1,0) → 1
```

---

### `j = 4`

```text
P4 == anchor
```

So:

```text
duplicatePointCount = 2
```

This consists of:

```text
anchor itself
+
one exact duplicate
```

Now strongest slope:

```text
(1,1) → 2 nonduplicate points
```

Candidate:

```text
duplicatePointCount
+
slopeCount

=
2 + 2

=
4
```

Those are:

```text
P0
P4
P1
P2
```

all lying on the same geometric line.

---

## 3.8 Algorithm

```text
answer = 0

for each anchor i:

    slopeFrequency = empty map
    duplicatePointCount = 0

    for every point j:

        if point[j] == point[i]:
            duplicatePointCount++

        else:
            dy = y[i] - y[j]
            dx = x[i] - x[j]

            slopeKey =
                reduceFraction(dy, dx)

            slopeFrequency[slopeKey]++

    bestForAnchor =
        duplicatePointCount

    for every slope bucket:

        bestForAnchor =
            max(
                bestForAnchor,
                duplicatePointCount + slopeCount
            )

    answer =
        max(answer, bestForAnchor)
```

### Robustness Note

The lecture screenshot computes:

```text
answer =
max(answer, duplicatePointCount + slopeCount)
```

while iterating slope buckets.

The cleaned version below also initializes:

```text
bestForAnchor = duplicatePointCount
```

so the all-identical-points case is handled even if the slope map is empty.

This does not change the main lecture method.

---

## 3.9 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

pair<long long, long long> reduceFraction(
    long long numerator,
    long long denominator
) {
    if (numerator == 0 && denominator == 0)
        return {0, 0};

    if (numerator == 0)
        return {0, 1};

    if (denominator == 0)
        return {1, 0};

    long long sign = 1;

    if (numerator < 0) {
        sign *= -1;
        numerator *= -1;
    }

    if (denominator < 0) {
        sign *= -1;
        denominator *= -1;
    }

    long long common =
        gcd(numerator, denominator);

    return {
        sign * numerator / common,
        denominator / common
    };
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfPoints;
    cin >> numberOfPoints;

    vector<long long> x(numberOfPoints);
    vector<long long> y(numberOfPoints);

    for (int index = 0; index < numberOfPoints; ++index)
        cin >> x[index] >> y[index];

    long long maximumPointsOnLine = 0;

    for (int anchor = 0; anchor < numberOfPoints; ++anchor) {
        map<
            pair<long long, long long>,
            long long
        > slopeFrequency;

        long long duplicatePointCount = 0;

        for (int point = 0; point < numberOfPoints; ++point) {
            if (x[point] == x[anchor] &&
                y[point] == y[anchor]) {

                duplicatePointCount++;
                continue;
            }

            long long deltaY =
                y[anchor] - y[point];

            long long deltaX =
                x[anchor] - x[point];

            auto slopeKey =
                reduceFraction(deltaY, deltaX);

            slopeFrequency[slopeKey]++;
        }

        long long bestForAnchor =
            duplicatePointCount;

        for (const auto& entry : slopeFrequency) {
            long long slopeCount =
                entry.second;

            bestForAnchor =
                max(
                    bestForAnchor,
                    duplicatePointCount + slopeCount
                );
        }

        maximumPointsOnLine =
            max(maximumPointsOnLine, bestForAnchor);
    }

    cout << maximumPointsOnLine << '\n';
    return 0;
}
```

---

## 3.10 Complexity

There are:

```text
N anchors
```

For each anchor:

```text
N points
```

Each ordered-map operation costs:

```text
O(log N)
```

So:

```text
Time:
O(N^2 log N)
```

Space per anchor:

```text
O(N)
```

The lecture board shows this problem with roughly `N <= 1000`, where the quadratic anchor approach is the intended pattern.

---

## 3.11 Recognition Model

When you see:

```text
maximum points on one line
```

do not try to enumerate arbitrary line equations first.

Think:

```text
fix one anchor
```

Then every other point becomes:

```text
direction from anchor
=
dy/dx
```

Now:

```text
same reduced slope
→ same line through anchor
```

So:

```text
geometry
→ fraction normalization
→ map frequency
```

Recognition chain:

```text
collinearity
→ fix anchor
→ slope equality
→ reduced (dy,dx)
→ count equal directions
```

---

# Final Mental Model

```text
                    IDEAS BASED ON FRACTIONS
                              |
                              v
                   hidden ratio equality
                              |
                              v
                   avoid floating point
                              |
                              v
               normalize numerator/denominator
                              |
              +---------------+---------------+
              |                               |
              v                               v
        divide by GCD                  normalize sign
              |                               |
              +---------------+---------------+
                              |
                              v
                    canonical pair key
                              |
                 +------------+------------+
                 |                         |
                 v                         v
        A[i]/i grouping               slope grouping
                 |                         |
                 v                         v
        count valid pairs          max points on a line
```

The reusable technique is:

```text
1. Find the ratio hidden in the condition.

2. Represent it as:
   (numerator, denominator)

3. Reduce by GCD.

4. Normalize signs.

5. Handle:
   0/0
   0/x
   x/0

6. Use the reduced pair as a map key.

7. Count equal keys.
```

For Pattern 1:

```text
A[i] * j = A[j] * i

        ↓

A[i] / i = A[j] / j

        ↓

same fraction key

        ↓

online pair counting
```

For Pattern 2:

```text
same line through fixed anchor

        ↓

same dy/dx

        ↓

same reduced slope key

        ↓

maximum frequency
+
duplicate points
```

> **Core lesson:** many problems that look like multiplication, ratios, or geometry become simple frequency problems once every fraction has one canonical representation.
