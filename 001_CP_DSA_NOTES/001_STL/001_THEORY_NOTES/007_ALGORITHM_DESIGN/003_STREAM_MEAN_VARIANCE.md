# AlgoZenith --- Algorithmic Design: Stream Mean & Variance

> **Core idea:** A stream keeps growing. Do not rescan its history.
> Maintain the **smallest mathematical summary** from which each query
> can be rebuilt.

## TOC

-   [1. Problem](#1-problem)
-   [2. Derive the State](#2-derive-the-state)
-   [3. Mean](#3-mean)
-   [4. Variance --- Algebraic
    Derivation](#4-variance--algebraic-derivation)
-   [5. Maintained State & Invariants](#5-maintained-state--invariants)
-   [6. Dry Run](#6-dry-run)
-   [7. C++](#7-c)
-   [8. Complexity](#8-complexity)
-   [9. Important Details & Mistakes](#9-important-details--mistakes)
-   [10. Final Mental Model](#10-final-mental-model)

------------------------------------------------------------------------

# 1. Problem

For a stream of integers support:

``` text
add(x)      → append x
mean()      → mean of all values
variance()  → population variance
```

Target:

``` text
Each operation → O(1)
Extra space    → O(1)
```

We do **not** store the complete stream.

------------------------------------------------------------------------

# 2. Derive the State

Ask:

``` text
What information does each formula actually need?
```

The complete answer is only:

``` text
count     = N
sum       = Σx
squareSum = Σx²
```

After adding `x`:

``` cpp
count++;
sum       += x;
squareSum += x * x;
```

These three values summarize everything required by the queries.

------------------------------------------------------------------------

# 3. Mean

For:

``` text
x1, x2, ..., xN
```

Mean:

``` text
μ = (x1 + x2 + ... + xN) / N
```

Therefore:

``` text
μ = sum / count
```

So mean requires only:

``` text
count
sum
```

Example:

``` text
10, 20

count = 2
sum   = 30

mean = 30 / 2
     = 15
```

------------------------------------------------------------------------

# 4. Variance --- Simple Step-by-Step Algebra

We start with the population variance formula:

``` text
variance = average of (value - mean)²
```

Mathematically:

``` text
σ² = Σ(xᵢ - μ)² / N
```

The problem is:

``` text
μ changes whenever a new value arrives.
```

We want to rewrite the formula using values we can maintain easily.

## Step 1 --- Expand the Square

Remember:

``` text
(a-b)² = a² - 2ab + b²
```

Therefore:

``` text
(xᵢ-μ)²
=
xᵢ² - 2xᵢμ + μ²
```

Put this back into variance:

``` text
σ²
=
Σ(xᵢ² - 2xᵢμ + μ²) / N
```

------------------------------------------------------------------------

## Step 2 --- Split the Sum

``` text
Σ(xᵢ² - 2xᵢμ + μ²)
```

becomes:

``` text
Σxᵢ² - 2μΣxᵢ + Σμ²
```

Since `μ` is constant for the current stream:

``` text
Σμ² = Nμ²
```

So:

``` text
σ²
=
(Σxᵢ² - 2μΣxᵢ + Nμ²) / N
```

------------------------------------------------------------------------

## Step 3 --- Divide Every Term by N

``` text
σ²
=
Σxᵢ²/N
-
2μΣxᵢ/N
+
Nμ²/N
```

Simplify the last term:

``` text
Nμ²/N = μ²
```

Therefore:

``` text
σ²
=
Σxᵢ²/N
-
2μ(Σxᵢ/N)
+
μ²
```

------------------------------------------------------------------------

## Step 4 --- Use the Mean Formula

We already know:

``` text
μ = Σxᵢ/N
```

So replace:

``` text
Σxᵢ/N
```

with:

``` text
μ
```

Now:

``` text
σ²
=
Σxᵢ²/N
-
2μ × μ
+
μ²
```

Therefore:

``` text
σ²
=
Σxᵢ²/N
-
2μ²
+
μ²
```

------------------------------------------------------------------------

## Step 5 --- Simplify

``` text
-2μ² + μ²
=
-μ²
```

So:

``` text
┌───────────────────────┐
│ σ² = Σxᵢ²/N - μ²      │
└───────────────────────┘
```

Now define:

``` text
squareSum = Σxᵢ²
count     = N
mean      = μ
```

Final formula:

``` text
┌────────────────────────────────┐
│ variance = squareSum / count   │
│            - mean²             │
└────────────────────────────────┘
```

## Tiny Example --- `10, 20`

First calculate the maintained values:

``` text
count = 2

sum
= 10 + 20
= 30

squareSum
= 10² + 20²
= 100 + 400
= 500
```

Mean:

``` text
mean
= sum/count
= 30/2
= 15
```

Variance:

``` text
variance
= squareSum/count - mean²

= 500/2 - 15²

= 250 - 225

= 25
```

### Verify with the Original Formula

``` text
variance
= ((10-15)² + (20-15)²) / 2

= (25 + 25) / 2

= 25
```

Both formulas give:

``` text
25 ✓
```

## Why This Transformation Matters

Before:

``` text
variance
= Σ(xᵢ-mean)² / N
```

This appears to require all previous `xᵢ`.

After algebra:

``` text
variance
= squareSum/count - mean²
```

We only need:

``` text
count
sum
squareSum
```

So old stream values never need to be scanned again.

------------------------------------------------------------------------

# 5. Maintained State & Invariants

``` cpp
long long count;
long double sum;
long double squareSum;
```

After every `add(x)`:

``` text
count     = number of values seen
sum       = Σx
squareSum = Σx²
```

Queries:

``` text
mean
= sum / count
```

``` text
variance
= squareSum / count - mean²
```

Visual:

``` text
                   x
             ┌─────┼─────┐
             ↓     ↓     ↓
          count   sum  squareSum
            +1    +x      +x²
             │     │       │
             └─────┴───────┘
                    ↓
        ┌──────────────────────┐
        │ mean = sum / count   │
        └──────────────────────┘

        ┌─────────────────────────────┐
        │ variance = squareSum/count  │
        │            - mean²          │
        └─────────────────────────────┘
```

------------------------------------------------------------------------

# 6. Dry Run

Stream:

``` text
10, 20
```

### Start

``` text
count     = 0
sum       = 0
squareSum = 0
```

### Add `10`

Update:

``` text
count     = 1
sum       = 10
squareSum = 100
```

Mean:

``` text
10 / 1 = 10
```

Variance:

``` text
100/1 - 10²
= 100 - 100
= 0
```

State:

``` text
count=1 | sum=10 | squareSum=100
mean=10 | variance=0
```

### Add `20`

Update:

``` text
count     = 2
sum       = 30
squareSum = 100 + 400
          = 500
```

Mean:

``` text
30 / 2
= 15
```

Variance:

``` text
500/2 - 15²
= 250 - 225
= 25
```

Final:

``` text
count=2 | sum=30 | squareSum=500
mean=15 | variance=25
```

------------------------------------------------------------------------

# 7. C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class StreamTracker {
private:
    long long count = 0;
    long double sum = 0.0L;
    long double squareSum = 0.0L;

public:
    void add(long long x) {
        long double value =
            static_cast<long double>(x);

        count++;
        sum += value;
        squareSum += value * value;
    }

    long double mean() const {
        if (count == 0)
            return 0.0L;

        return sum / count;
    }

    long double variance() const {
        if (count == 0)
            return 0.0L;

        long double mu = mean();

        return squareSum / count
             - mu * mu;
    }
};
```

Example:

``` cpp
StreamTracker st;

st.add(10);
st.add(20);

cout << st.mean() << '\n';      // 15
cout << st.variance() << '\n';  // 25
```

------------------------------------------------------------------------

# 8. Complexity

  Operation          Time   Extra Space
  -------------- -------- -------------
  `add(x)`         `O(1)`        `O(1)`
  `mean()`         `O(1)`        `O(1)`
  `variance()`     `O(1)`        `O(1)`

No matter how long the stream becomes:

``` text
stored state = 3 values
```

------------------------------------------------------------------------

# 9. Important Details & Mistakes

## Population vs Sample Variance

This note computes:

``` text
population variance
```

using:

``` text
N
```

not:

``` text
N-1
```

------------------------------------------------------------------------

## Empty Stream

Here:

``` cpp
count == 0 → return 0.0L;
```

This is an **API choice**.

Mathematically, mean and variance of an empty collection are undefined.

------------------------------------------------------------------------

## Cast Before Squaring

Bad:

``` cpp
squareSum += x * x;
```

`x*x` may overflow as integer arithmetic **before** conversion.

Correct:

``` cpp
long double value =
    static_cast<long double>(x);

squareSum += value * value;
```

------------------------------------------------------------------------

## Variance ≠ Standard Deviation

``` text
variance = σ²
```

Standard deviation would be:

``` text
sqrt(variance)
```

------------------------------------------------------------------------

## Floating-Point Precision

The formula:

``` text
E[X²] - E[X]²
```

is excellent for this O(1)-state design, but floating-point arithmetic
can introduce rounding error.

So a mathematically zero variance may occasionally appear extremely
close to zero.

------------------------------------------------------------------------

# 10. Final Mental Model

Start from the query formula:

``` text
MEAN
μ = Σx / N
     ↓
need:
count + sum
```

For variance, rewrite:

``` text
σ² = (1/N)Σ(x-μ)²
          ↓ expand
σ² = Σx²/N - μ²
          ↓
need:
count + sum + squareSum
```

So:

``` text
               STREAM x
                   ↓
       ┌───────────┼───────────┐
       ↓           ↓           ↓
     count        sum      squareSum
       +1          +x          +x²
       │            │           │
       └────────────┴───────────┘
                    ↓
             O(1) queries
```

## One Sentence to Remember

> **Rewrite the query formula until it depends only on aggregates that
> are cheap to update.**

``` text
count + sum + squareSum
        ↓
mean     = sum/count
variance = squareSum/count - mean²
```
