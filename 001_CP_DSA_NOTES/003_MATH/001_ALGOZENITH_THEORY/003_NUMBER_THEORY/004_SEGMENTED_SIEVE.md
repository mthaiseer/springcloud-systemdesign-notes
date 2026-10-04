# Segmented Sieve — Example-Driven, Don't Memorize

> **Goal:** Find all primes in a **small range `[L,R]`** when `R` itself can be huge.

Typical constraint:

```text
1 ≤ L ≤ R ≤ 10^12
R-L ≤ 10^6
```

We will use **one running example almost everywhere**:

```text
L = 10
R = 30
```

This makes every formula connect to an actual number.

---

## Table of Contents

1. [Why Do We Need Segmented Sieve?](#1-why-do-we-need-segmented-sieve)
2. [Core Mathematical Model](#2-core-mathematical-model)
3. [Phase 1 — Find Base Primes](#3-phase-1--find-base-primes)
4. [Phase 2 — Create Only the Required Segment](#4-phase-2--create-only-the-required-segment)
5. [Segment Index Mapping](#5-segment-index-mapping)
6. [Find the First Multiple of p Inside the Range](#6-find-the-first-multiple-of-p-inside-the-range)
7. [Why Start From max(p², first)?](#7-why-start-from-maxp-first)
8. [Full Dry Run — `[10,30]`](#8-full-dry-run--1030)
9. [C++ Code — Step by Step](#9-c-code--step-by-step)
10. [Code Dry Run](#10-code-dry-run)
11. [Complexity With Numbers](#11-complexity-with-numbers)
12. [When to Recognize Segmented Sieve](#12-when-to-recognize-segmented-sieve)
13. [Final Memory Model](#13-final-memory-model)

---

# 1. Why Do We Need Segmented Sieve?

Suppose the problem asks:

```text
Find all primes between:

L = 999,999,000,000
R = 1,000,000,000,000
```

Notice:

```text
R ≈ 10^12        → HUGE
R-L = 10^6       → relatively SMALL
```

## Bad idea — normal sieve to R

A normal sieve thinks:

```text
1 2 3 4 5 ............................... 10^12
|---------------------------------------------|
              store everything
```

But the problem only asks about:

```text
999999000000 ................ 1000000000000
|-----------------------------------------|
               ~10^6 numbers
```

So storing `1...10^12` is wasteful.

## Better question

Instead of asking:

```text
How can I sieve all numbers up to R?
```

ask:

```text
How can I eliminate composites
ONLY inside [L,R]?
```

That is exactly what segmented sieve does.

For our small running example:

```text
Need primes only in [10,30]

1 2 3 ... 9 | 10 11 12 ........ 30
             |===================|
                only care here
```

---

# 2. Core Mathematical Model

Why can a few **small primes** tell us which huge numbers are composite?

Take:

```text
x = 26
```

Factor it:

```text
26 = 2 × 13
```

One factor (`2`) is smaller than:

```text
√26 ≈ 5.09
```

Another example:

```text
x = 21
21 = 3 × 7
√21 ≈ 4.58
```

Again, one factor (`3`) is ≤ `√21`.

## General model

If:

```text
x = a × b
```

and both `a` and `b` were greater than `√x`, then:

```text
a × b > √x × √x
      > x
```

Impossible.

Therefore every composite `x` has at least one factor:

```text
≤ √x
```

Now every number in our target satisfies:

```text
x ≤ R
```

so:

```text
√x ≤ √R
```

Therefore:

```text
Every composite in [L,R]
has a prime factor ≤ √R.
```

## Running example

```text
L = 10
R = 30

√30 ≈ 5.47
```

Primes ≤ `√30`:

```text
2, 3, 5
```

Check the composites in `[10,30]`:

```text
10 = 2×5
12 = 2×6
14 = 2×7
15 = 3×5
16 = 2×8
18 = 2×9
20 = 2×10
21 = 3×7
22 = 2×11
24 = 2×12
25 = 5×5
26 = 2×13
27 = 3×9
28 = 2×14
30 = 2×15
```

Every one is caught by:

```text
2 or 3 or 5
```

So we do **not** need primes larger than `√R`.

---

# 3. Phase 1 — Find Base Primes

We first run an ordinary sieve only up to:

```text
√R
```

For:

```text
R = 30
```

we need:

```text
√30 ≈ 5.47
```

So consider integers:

```text
2 3 4 5
```

Sieve them:

```text
2 → prime
3 → prime
4 → composite
5 → prime
```

Base primes:

```text
[2, 3, 5]
```

ASCII picture:

```text
Target range:       [10 ..................... 30]
                                      ↑
                                     √30
                                      ≈5

Base area:
1  2  3  4  5
   P  P  X  P

basePrimes = {2,3,5}
```

These three primes are enough to clean the entire target interval.

---

# 4. Phase 2 — Create Only the Required Segment

For:

```text
L = 10
R = 30
```

number of values is:

```text
R-L+1
= 30-10+1
= 21
```

So create only 21 boolean cells:

```cpp
vector<bool> isPrime(21, true);
```

Initially we say:

```text
"Every number might be prime."
```

```text
10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓

21 22 23 24 25 26 27 28 29 30
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
```

Then base primes `2,3,5` will prove which cells are composite.

Think:

```text
Base primes = detectives
Target segment = suspects

2 removes multiples of 2
3 removes multiples of 3
5 removes multiples of 5

Whatever survives is prime.
```

---

# 5. Segment Index Mapping

This is often the first confusing part.

Our vector has only 21 cells:

```text
index:
0  1  2  3  4  ... 20
```

But these represent actual numbers:

```text
10 11 12 13 14 ... 30
```

Mapping:

```text
actual number = L + index
index         = actual number - L
```

For `L=10`:

```text
Actual    Index
------    -----
10          0
11          1
12          2
13          3
...
25         15
...
30         20
```

### Example — mark 24 composite

Actual number:

```text
x = 24
```

Convert to segment index:

```text
index = x-L
      = 24-10
      = 14
```

So:

```cpp
isPrime[14] = false;
```

Visual:

```text
number: 10 11 12 13 ... 24 ... 30
index :  0  1  2  3 ... 14 ... 20
                         ↑
                    mark false
```

This is why the code uses:

```cpp
isPrime[x - L] = false;
```

---

# 6. Find the First Multiple of p Inside the Range

Suppose:

```text
L = 17
R = 40
p = 5
```

Multiples of `5` are:

```text
5, 10, 15, 20, 25, 30, 35, 40
          ↑   ↑
       below  first one inside [17,40]
```

We need:

```text
20
```

## Mathematical model

We want the smallest integer `k` such that:

```text
k × p ≥ L
```

Divide by `p`:

```text
k ≥ L/p
```

Smallest integer `k`:

```text
k = ceil(L/p)
```

Therefore:

```text
first = ceil(L/p) × p
```

For:

```text
L=17, p=5
```

```text
ceil(17/5)
= ceil(3.4)
= 4

first = 4×5
      = 20
```

## Integer ceiling trick

C++ integer division:

```text
17/5 = 3
```

but we need `4`.

For positive integers:

```text
ceil(L/p) = (L+p-1)/p
```

Example:

```text
(17+5-1)/5
= 21/5
= 4
```

Then:

```text
4×5 = 20
```

So:

```cpp
long long first = ((L + p - 1) / p) * p;
```

---

# 7. Why Start From max(p², first)?

Suppose:

```text
L = 2
R = 20
p = 5
```

Formula for first multiple gives:

```text
first = 5
```

Should we mark `5` composite?

```text
NO
```

because `5` itself is prime.

Also:

```text
5×2 = 10 → already caught by 2
5×3 = 15 → already caught by 3
5×4 = 20 → already caught by 2
```

The first multiple where `5` itself is the smallest prime factor is:

```text
5×5 = 25 = p²
```

This is the same optimization used in normal sieve.

So we need **both conditions**:

```text
start must be inside [L,R]
AND
start should not be before p²
```

Hence:

```text
start = max(first, p²)
```

## Example A — range starts early

```text
L = 2
p = 5

first = 5
p²    = 25

start = max(5,25)
      = 25
```

We don't incorrectly remove `5`.

## Example B — range starts late

```text
L = 101
p = 5

first multiple ≥101:
105

p² = 25

start = max(105,25)
      = 105
```

Here `25` is far outside the segment, so start at `105`.

ASCII:

```text
first inside segment = ceil(L/p)×p
              \
               \
                MAX  ───→ start
               /
              /
            p²
```

---

# 8. Full Dry Run — `[10,30]`

We now combine everything.

## Step 1 — target

```text
L = 10
R = 30
```

## Step 2 — base primes

```text
√30 ≈ 5.47

base primes = [2,3,5]
```

## Step 3 — segment

```text
10 11 12 13 14 15 16 17 18 19 20
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓

21 22 23 24 25 26 27 28 29 30
✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
```

---

## Pass 1 — `p = 2`

First multiple:

```text
ceil(10/2)×2
= 5×2
= 10
```

And:

```text
p² = 4
```

Therefore:

```text
start = max(10,4)
      = 10
```

Jump by `2`:

```text
10 → 12 → 14 → 16 → 18 → 20
   → 22 → 24 → 26 → 28 → 30
```

After `p=2`:

```text
10 11 12 13 14 15 16 17 18 19 20
×  ✓  ×  ✓  ×  ✓  ×  ✓  ×  ✓  ×

21 22 23 24 25 26 27 28 29 30
✓  ×  ✓  ×  ✓  ×  ✓  ×  ✓  ×
```

---

## Pass 2 — `p = 3`

First multiple:

```text
ceil(10/3)×3
= 4×3
= 12
```

```text
p² = 9
```

Therefore:

```text
start = max(12,9)
      = 12
```

Jump:

```text
12 → 15 → 18 → 21 → 24 → 27 → 30
```

Some were already false. That's fine.

After `p=3`:

```text
10 11 12 13 14 15 16 17 18 19 20
×  ✓  ×  ✓  ×  ×  ×  ✓  ×  ✓  ×

21 22 23 24 25 26 27 28 29 30
×  ×  ✓  ×  ✓  ×  ×  ×  ✓  ×
```

---

## Pass 3 — `p = 5`

First multiple:

```text
ceil(10/5)×5
= 2×5
= 10
```

But:

```text
p² = 25
```

Therefore:

```text
start = max(10,25)
      = 25
```

Why skip `10,15,20`?

```text
10 → already caught by 2
15 → already caught by 3
20 → already caught by 2
```

Jump:

```text
25 → 30
```

Final:

```text
10 11 12 13 14 15 16 17 18 19 20
×  ✓  ×  ✓  ×  ×  ×  ✓  ×  ✓  ×

21 22 23 24 25 26 27 28 29 30
×  ×  ✓  ×  ×  ×  ×  ×  ✓  ×
```

Survivors:

```text
11, 13, 17, 19, 23, 29
```

---

# 9. C++ Code — Step by Step

## Part A — normal sieve for base primes

```cpp
vector<int> simpleSieve(long long limit) {
    vector<bool> isPrime(limit + 1, true);

    if (limit >= 0) isPrime[0] = false;
    if (limit >= 1) isPrime[1] = false;

    for (long long i = 2; i * i <= limit; ++i) {
        if (isPrime[i]) {
            for (long long j = i * i; j <= limit; j += i) {
                isPrime[j] = false;
            }
        }
    }

    vector<int> primes;

    for (int i = 2; i <= limit; ++i) {
        if (isPrime[i])
            primes.push_back(i);
    }

    return primes;
}
```

For our example:

```cpp
simpleSieve(5)
```

returns:

```text
[2,3,5]
```

---

## Part B — create the segment

```cpp
vector<bool> isPrime(R - L + 1, true);
```

For `[10,30]`:

```text
size = 30-10+1
     = 21
```

---

## Part C — process every base prime

```cpp
for (long long p : basePrimes) {

    long long first =
        ((L + p - 1) / p) * p;

    long long start =
        max(p * p, first);

    for (long long x = start; x <= R; x += p) {
        isPrime[x - L] = false;
    }
}
```

Read this in English:

```text
For every small prime p:

1. Find first multiple of p inside the segment.
2. Don't begin before p².
3. Walk through multiples by adding p.
4. Convert actual number x to index x-L.
5. Mark it composite.
```

---

## Complete Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> simpleSieve(long long limit) {
    vector<bool> isPrime(limit + 1, true);

    if (limit >= 0) isPrime[0] = false;
    if (limit >= 1) isPrime[1] = false;

    for (long long i = 2; i * i <= limit; ++i) {
        if (isPrime[i]) {
            for (long long j = i * i; j <= limit; j += i) {
                isPrime[j] = false;
            }
        }
    }

    vector<int> primes;

    for (int i = 2; i <= limit; ++i) {
        if (isPrime[i])
            primes.push_back(i);
    }

    return primes;
}

vector<long long> segmentedSieve(long long L, long long R) {
    long long limit = sqrtl((long double)R);

    // Correct possible floating-point rounding.
    while ((limit + 1) <= R / (limit + 1))
        ++limit;

    while (limit > 0 && limit > R / limit)
        --limit;

    vector<int> basePrimes = simpleSieve(limit);

    vector<bool> isPrime(R - L + 1, true);

    if (L == 1)
        isPrime[0] = false;

    for (long long p : basePrimes) {
        long long first =
            ((L + p - 1) / p) * p;

        long long start =
            max(p * p, first);

        for (long long x = start; x <= R; x += p) {
            isPrime[x - L] = false;
        }
    }

    vector<long long> answer;

    for (long long i = 0; i <= R - L; ++i) {
        if (isPrime[i])
            answer.push_back(L + i);
    }

    return answer;
}

int main() {
    long long L, R;
    cin >> L >> R;

    vector<long long> primes =
        segmentedSieve(L, R);

    for (long long p : primes)
        cout << p << ' ';
}
```

---

# 10. Code Dry Run

Input:

```text
10 30
```

### `limit`

```text
limit = floor(√30)
      = 5
```

### `basePrimes`

```text
simpleSieve(5)
→ [2,3,5]
```

### segment size

```text
R-L+1
= 21
```

### loop

For `p=2`:

```text
first = 10
start = 10

x:
10,12,14,...30

indices:
0,2,4,...20
```

For `p=3`:

```text
first = 12
start = 12

x:
12,15,18,21,24,27,30

indices:
2,5,8,11,14,17,20
```

For `p=5`:

```text
first = 10
p² = 25
start = 25

x:
25,30

indices:
15,20
```

Final `true` indices:

```text
1,3,7,9,13,19
```

Convert back:

```text
number = L+index
```

```text
10+1  = 11
10+3  = 13
10+7  = 17
10+9  = 19
10+13 = 23
10+19 = 29
```

Output:

```text
11 13 17 19 23 29
```

---

# 11. Complexity With Numbers

Let:

```text
W = R-L+1
```

For the classic constraints:

```text
R ≤ 10^12
W ≤ about 10^6
```

Then:

```text
√R ≤ 10^6
```

So Phase 1 sieves at most around:

```text
1,000,000 values
```

and Phase 2 stores at most around:

```text
1,000,001 values
```

Instead of:

```text
1,000,000,000,000 values
```

Complexity model:

```text
Phase 1:
O(√R log log √R)

Phase 2:
O(W log log R)

Memory:
O(√R + W)
```

The important contest intuition is not the exact logarithm.

Remember:

```text
work with ~√R
+
work with width of interval

NOT with all numbers up to R.
```

---

# 12. When to Recognize Segmented Sieve

| Problem shape | Technique |
|---|---|
| Find all primes up to `10^6` / `10^7` | Normal sieve |
| Check whether one large number is prime | Trial division / stronger primality method |
| `L,R` around `10^12`, but `R-L ≤ 10^6` | **Segmented sieve** |
| Need smallest prime factor for many small values | SPF sieve |

### Recognition example

Problem says:

```text
1 ≤ L ≤ R ≤ 10^12
R-L ≤ 10^5

Print every prime in [L,R].
```

Your observation should be:

```text
R is too large for normal sieve.
        +
interval width is small.
        +
composites can be detected using primes ≤ √R.

        ↓

SEGMENTED SIEVE
```

---

# 13. Final Memory Model

Don't memorize the code.

Remember this chain:

```text
Need primes in [L,R]
        ↓
R may be huge
        ↓
cannot sieve 1...R
        ↓
every composite x≤R has
a prime factor ≤√R
        ↓
normal sieve only to √R
        ↓
get base primes
        ↓
create only R-L+1 cells
        ↓
for each base prime p
        ↓
find first multiple inside segment
        ↓
start = max(p², ceil(L/p)×p)
        ↓
mark x-L as composite
        ↓
survivors = primes
```

## Three formulas to understand

```text
1. base primes needed only up to √R
```

```text
2. segment index = x-L
```

```text
3. start =
   max(
       p²,
       ceil(L/p)×p
   )
```

with:

```text
ceil(L/p) = (L+p-1)/p
```

## 10-second recall example

```text
[L,R] = [10,30]

√R ≈ 5
base primes = 2,3,5

2 removes evens
3 removes multiples of 3
5 starts at 25

survivors:
11 13 17 19 23 29
```

> **Mental trigger:** **Huge endpoint + small interval + need primes ⇒ sieve small primes to `√R`, then use them to clean only `[L,R]`.**
