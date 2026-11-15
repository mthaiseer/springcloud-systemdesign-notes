# AlgoZenith — Algorithmic Design: Stream Mean & Variance
## Visual Self-Learning Version

> **Core idea:** A stream keeps growing.  
> Do not rescan its history. Maintain the **smallest mathematical summary** from which every query can be rebuilt.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Design Backward from the Formulas](#2-design-backward-from-the-formulas)
- [3. Derive the Maintained State](#3-derive-the-maintained-state)
- [4. Mean — Step by Step](#4-mean--step-by-step)
- [5. Variance — Step-by-Step Algebra](#5-variance--step-by-step-algebra)
- [6. State Invariants](#6-state-invariants)
- [7. Add(x) — Step by Step](#7-addx--step-by-step)
- [8. Full Dry Run](#8-full-dry-run)
- [9. Complete C++17](#9-complete-c17)
- [10. Complexity](#10-complexity)
- [11. Important Details & Common Mistakes](#11-important-details--common-mistakes)
- [12. Recognition Checklist](#12-recognition-checklist)
- [13. Final Mental Model](#13-final-mental-model)

---

# 0. Prerequisites

Before this pattern, understand these ideas.

## 0.1 What Is a Stream?

A stream means values arrive one by one.

Example:

```text
time 1: 10
time 2: 20
time 3: 15
time 4: 30
...
```

The stream may keep growing.

We want to answer queries without rescanning all old values.

---

## 0.2 Running Sum

For:

```text
10, 20, 15
```

sum is:

```text
10 + 20 + 15 = 45
```

Instead of recomputing:

```text
10 + 20 + 15
```

after every new value, maintain:

```text
sum
```

Update:

```text
sum += x
```

---

## 0.3 Count

We also maintain:

```text
count = number of values seen
```

Every new value:

```text
count++
```

---

## 0.4 Square of a Number

For value `x`:

```text
x² = x × x
```

Examples:

```text
10² = 100
20² = 400
```

We will maintain:

```text
squareSum = Σx²
```

---

## 0.5 Mean

For values:

```text
x1, x2, ..., xN
```

mean is:

```text
sum of all values
-----------------
number of values
```

Formula:

```text
μ = Σx / N
```

---

## 0.6 Population Variance

Population variance measures how far values spread around the mean.

Formula:

```text
σ² = Σ(xᵢ - μ)² / N
```

Important:

```text
population variance
uses N

sample variance
uses N-1
```

This note uses population variance.

---

## 0.7 Algebra Identity

We will use:

```text
(a-b)²
=
a² - 2ab + b²
```

Example:

```text
(5-2)²

= 5² - 2×5×2 + 2²
= 25 - 20 + 4
= 9
```

---

# 1. Problem

For a stream of integers, support:

```text
add(x)
mean()
variance()
```

Target:

```text
add(x)      -> O(1)
mean()      -> O(1)
variance()  -> O(1)

extra space -> O(1)
```

We do **not** want to store or rescan the entire stream.

---

## 1.1 Why the Naive Approach Is Expensive

Suppose stream is:

```text
10, 20, 15, 30, ...
```

Naive mean:

```text
scan all values
add them
divide by N
```

Naive variance:

```text
compute mean
scan all values again
compute squared differences
```

If there are many queries, this repeatedly processes old data.

Goal:

```text
replace history
with a tiny summary
```

---

# 2. Design Backward from the Formulas

Do not start with:

```text
"What container should I use?"
```

Start with:

```text
"What information does each formula require?"
```

---

## 2.1 Mean Requirement

Mean:

```text
μ = Σx / N
```

So mean needs only:

```text
count
sum
```

---

## 2.2 Variance Requirement

Original variance:

```text
σ² = Σ(xᵢ - μ)² / N
```

At first this seems to require:

```text
all old xᵢ values
```

But after algebra, we can rewrite it as:

```text
σ²
=
Σxᵢ² / N
-
μ²
```

So variance needs only:

```text
count
sum
squareSum
```

---

## 2.3 Visual Derivation of State

```text
QUERY
 |
 +--> mean()
 |      |
 |      v
 |   needs count + sum
 |
 +--> variance()
        |
        v
     after algebra
        |
        v
  needs count + sum + squareSum
```

Therefore maintain exactly:

```text
count
sum
squareSum
```

---

# 3. Derive the Maintained State

We keep:

```cpp
long long count;
long double sum;
long double squareSum;
```

Meaning:

```text
count
=
number of values seen

sum
=
Σx

squareSum
=
Σx²
```

---

## 3.1 Why This State Is Minimal

For mean:

```text
count + sum
```

For variance:

```text
count + squareSum + mean
```

and mean already comes from:

```text
count + sum
```

So no extra historical information is needed.

---

## 3.2 ASCII Data Flow

When new value `x` arrives:

```text
                    x
                    |
        +-----------+-----------+
        |           |           |
        v           v           v
      count        sum      squareSum
       +1           +x          +x²
        |           |           |
        +-----------+-----------+
                    |
                    v
         +--------------------+
         | mean = sum/count   |
         +--------------------+
                    |
                    v
   +----------------------------------+
   | variance = squareSum/count       |
   |            - mean²               |
   +----------------------------------+
```

---

# 4. Mean — Step by Step

For:

```text
x1, x2, ..., xN
```

mean is:

```text
μ
=
(x1 + x2 + ... + xN) / N
```

But:

```text
x1 + x2 + ... + xN
=
sum
```

and:

```text
N
=
count
```

Therefore:

```text
μ
=
sum / count
```

---

## 4.1 Tiny Example

Stream:

```text
10, 20
```

State:

```text
count = 2
sum   = 30
```

Mean:

```text
30 / 2
=
15
```

---

## 4.2 ASCII View

```text
values:

10     20
 \     /
  \   /
   \ /
   sum = 30
      |
      v
 divide by count = 2
      |
      v
    mean = 15
```

---

# 5. Variance — Simple Step-by-Step

Variance tells us:

```text
How spread out are the values
from their mean?
```

Think of:

```text
10 -------- 15 -------- 20
            ^
           mean
```

Both values are `5` away from the mean.

```text
10 -> distance = -5
20 -> distance = +5
```

If we simply add distances:

```text
-5 + 5 = 0
```

they cancel.

So variance uses **squared distances**:

```text
(-5)² = 25
(+5)² = 25
```

Average:

```text
(25 + 25) / 2
=
25
```

Therefore:

```text
variance = 25
```

---

## 5.1 Original Variance Formula

For values:

```text
x1, x2, ..., xN
```

with mean:

```text
μ
```

population variance is:

```text
variance
=
average of squared distance from mean
```

Formula:

```text
σ²
=
Σ(xᵢ - μ)² / N
```

Visual:

```text
value
  |
  v
xᵢ -------- μ
     distance

distance
   |
   v
xᵢ - μ
   |
   v
square it
   |
   v
(xᵢ - μ)²
   |
   v
average all squared distances
   |
   v
variance
```

This formula is easy to understand.

But for a stream it has one problem:

```text
mean changes when every new value arrives
```

We do not want to revisit all old values.

So we use a shortcut formula.

---

## 5.2 Shortcut Formula

The useful streaming formula is:

```text
variance
=
average of squares
-
square of average
```

Using our maintained state:

```text
average of squares
=
squareSum / count
```

and:

```text
square of average
=
mean²
```

Therefore:

```text
+----------------------------------+
| variance = squareSum / count     |
|            - mean²               |
+----------------------------------+
```

This is the formula to remember for this note.

---

## 5.3 Why Does the Shortcut Work?

Start with:

```text
variance
=
average of (x - mean)²
```

### Step 1 — Expand One Square

Remember:

```text
(x - mean)²

=
x² - 2×x×mean + mean²
```

So:

```text
variance
=
average(x² - 2×x×mean + mean²)
```

---

### Step 2 — Take the Average of Each Part

```text
variance

=
average(x²)
- 2×mean×average(x)
+ mean²
```

But:

```text
average(x)
=
mean
```

So:

```text
variance

=
average(x²)
- 2×mean²
+ mean²
```

---

### Step 3 — Simplify

```text
-2×mean² + mean²
=
-mean²
```

Therefore:

```text
variance
=
average(x²)
-
mean²
```

And because:

```text
average(x²)
=
squareSum / count
```

we get:

```text
variance
=
squareSum / count
-
mean²
```

That is the whole derivation.

---

## 5.4 ASCII Memory Diagram

```text
VALUES
  |
  +-------------------+
  |                   |
  v                   v
average(x)         average(x²)
  |                   |
  v                   v
 mean              squareSum/count
  |
  v
 mean²
  |                   |
  +---------+---------+
            |
            v
 variance
 =
 average(x²) - mean²
```

Short memory:

```text
VARIANCE
=
AVERAGE OF SQUARES
-
SQUARE OF AVERAGE
```

---

## 5.5 Tiny Example — `10, 20`

### Method 1 — Think About Distance from Mean

Values:

```text
10, 20
```

Mean:

```text
(10 + 20) / 2
=
15
```

ASCII:

```text
10 -------- 15 -------- 20
            ^
           mean
```

Distances:

```text
10 - 15 = -5
20 - 15 =  5
```

Squared distances:

```text
(-5)² = 25
5²    = 25
```

Average:

```text
(25 + 25) / 2
=
25
```

So:

```text
variance = 25
```

---

### Method 2 — Streaming Shortcut

Maintain:

```text
count = 2

sum
=
10 + 20
=
30

squareSum
=
10² + 20²
=
100 + 400
=
500
```

Mean:

```text
mean
=
30 / 2
=
15
```

Average of squares:

```text
squareSum / count

=
500 / 2

=
250
```

Square of average:

```text
mean²

=
15²

=
225
```

Variance:

```text
250 - 225
=
25
```

Same answer:

```text
25
```

---

## 5.6 Why This Helps in a Stream

Without the shortcut:

```text
new value arrives
      |
      v
mean changes
      |
      v
would need to recompute
every old (x - mean)²
```

With the shortcut:

```text
new value x
     |
     +--> count += 1
     |
     +--> sum += x
     |
     +--> squareSum += x²
```

Then immediately:

```text
mean
=
sum / count
```

and:

```text
variance
=
squareSum / count - mean²
```

No old value needs to be visited again.

---

# 6. State Invariants

After every `add(x)` these must remain true:

```text
count
=
number of values seen

sum
=
Σx

squareSum
=
Σx²
```

Queries:

```text
mean
=
sum / count
```

```text
variance
=
squareSum / count
-
mean²
```

---

## 6.1 ASCII Invariant Diagram

```text
                 STREAM
                   |
                   v
               add(x)
                   |
      +------------+------------+
      |            |            |
      v            v            v
   count        sum         squareSum
    +1           +x            +x²
      |            |            |
      +------------+------------+
                   |
                   v
             query formulas
```

---

# 7. Add(x) — Step by Step

When `x` arrives:

```text
Step 1:
count++

Step 2:
sum += x

Step 3:
squareSum += x²
```

That is all.

---

## 7.1 Important Numeric Detail

Do not blindly compute:

```cpp
x * x
```

when `x` is an integer type that could overflow before conversion.

Safer:

```cpp
long double value = x;

squareSum += value * value;
```

---

## 7.2 ASCII Example — Add 20

Before:

```text
count     = 1
sum       = 10
squareSum = 100
```

Add:

```text
x = 20
```

Update:

```text
count:
1 -> 2

sum:
10 -> 30

squareSum:
100 -> 100 + 400
     -> 500
```

After:

```text
count     = 2
sum       = 30
squareSum = 500
```

---

# 8. Full Dry Run

Stream:

```text
10, 20
```

---

## 8.1 Start

```text
count     = 0
sum       = 0
squareSum = 0
```

---

## 8.2 Add 10

Update:

```text
count
0 -> 1

sum
0 -> 10

squareSum
0 -> 100
```

Mean:

```text
10 / 1
=
10
```

Variance:

```text
100/1 - 10²

=
100 - 100

=
0
```

State:

```text
count     = 1
sum       = 10
squareSum = 100
mean      = 10
variance  = 0
```

---

## 8.3 Add 20

Update:

```text
count
1 -> 2

sum
10 -> 30

squareSum
100 -> 500
```

Mean:

```text
30 / 2
=
15
```

Variance:

```text
500/2 - 15²

=
250 - 225

=
25
```

Final:

```text
count     = 2
sum       = 30
squareSum = 500
mean      = 15
variance  = 25
```

---

## 8.4 State Transition Table

| Step | Added | Count | Sum | Square Sum | Mean | Variance |
|---:|---:|---:|---:|---:|---:|---:|
| 0 | — | 0 | 0 | 0 | — | — |
| 1 | 10 | 1 | 10 | 100 | 10 | 0 |
| 2 | 20 | 2 | 30 | 500 | 15 | 25 |

---

# 9. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class StreamTracker {
private:
    long long count = 0;
    long double sum = 0.0L;
    long double squareSum = 0.0L;

public:
    void add(long long x) {
        long double value = x;

        count++;
        sum += value;
        squareSum += value * value;
    }

    long double mean() const {
        if (count == 0) {
            return 0.0L;
        }

        return sum / count;
    }

    long double variance() const {
        if (count == 0) {
            return 0.0L;
        }

        long double mu = mean();

        return squareSum / count - mu * mu;
    }
};
```

Example:

```cpp
StreamTracker st;

st.add(10);
st.add(20);

cout << st.mean() << '\n';      // 15
cout << st.variance() << '\n';  // 25
```

---

# 10. Complexity

| Operation | Time | Extra Space |
|---|---:|---:|
| `add(x)` | `O(1)` | `O(1)` |
| `mean()` | `O(1)` | `O(1)` |
| `variance()` | `O(1)` | `O(1)` |

No matter how long the stream becomes:

```text
stored state
=
3 values
```

Conceptually:

```text
count
sum
squareSum
```

---

# 11. Important Details & Common Mistakes

## 11.1 Population vs Sample Variance

This note computes:

```text
population variance
```

using:

```text
N
```

Sample variance would use:

```text
N-1
```

These are not interchangeable.

---

## 11.2 Empty Stream

The code returns:

```cpp
0.0L
```

when:

```text
count == 0
```

This is an API choice.

Mathematically:

```text
mean of empty stream
variance of empty stream
```

are undefined.

---

## 11.3 Convert Before Squaring

Avoid:

```cpp
squareSum += x * x;
```

when `x` is an integer.

Why?

```text
x * x
```

is calculated using integer arithmetic first, so a large value may overflow before it reaches `squareSum`.

Use a floating-point temporary:

```cpp
long double value = x;

squareSum += value * value;
```

Now the multiplication happens as `long double`.

---

## 11.4 Variance Is Not Standard Deviation

```text
variance
=
σ²
```

Standard deviation:

```text
σ
=
sqrt(variance)
```

Different quantities.

---

## 11.5 Floating-Point Precision

Formula:

```text
E[X²] - E[X]²
```

is ideal for this compact O(1)-state derivation, but floating-point rounding may produce tiny numerical error.

For example, a mathematically zero variance may appear as:

```text
-1e-18
```

or:

```text
1e-18
```

depending on the data and floating-point operations.

---

## 11.6 Do Not Store the Whole Stream Unnecessarily

If all required queries depend only on:

```text
count
sum
squareSum
```

then storing:

```text
vector<long long> allValues
```

is unnecessary for this design.

---

# 12. Recognition Checklist

Think of this pattern when you see:

```text
stream / online updates
+
aggregate statistical queries
```

Ask:

```text
1. What formula defines the query?

2. Can I rewrite it algebraically?

3. What aggregates does the rewritten formula need?

4. Can each aggregate be updated in O(1)?

5. Do I need old individual values?

6. Can I replace the entire history
   with a small fixed-size summary?

7. Do integer operations risk overflow?

8. Is the problem asking population
   or sample variance?

9. What should happen for an empty stream?

10. Is floating-point precision relevant?
```

---

# 13. Final Mental Model

Start from formulas.

```text
MEAN

μ = Σx / N
      |
      v
need:
count + sum
```

For variance:

```text
σ² = Σ(x-μ)² / N
          |
          v
     expand algebra
          |
          v
σ² = Σx²/N - μ²
          |
          v
need:
count + sum + squareSum
```

Final stream summary:

```text
                 NEW x
                   |
       +-----------+-----------+
       |           |           |
       v           v           v
     count        sum      squareSum
      +1           +x          +x²
       |           |           |
       +-----------+-----------+
                   |
                   v
              O(1) queries
```

The one sentence to remember:

```text
Rewrite the query formula
until it depends only on aggregates
that are cheap to update.
```
