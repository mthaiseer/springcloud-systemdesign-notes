# AlgoZenith --- Product of Last K Numbers (STL)

> **Core idea:** Maintain **prefix products** for the current zero-free
> segment. A suffix product becomes one division. When `0` arrives,
> reset the segment.

## TOC

-   [1. Problem](#1-problem)
-   [2. Naive vs Optimal](#2-naive-vs-optimal)
-   [3. Prefix Product Idea](#3-prefix-product-idea)
-   [4. Algebraic Derivation](#4-algebraic-derivation)
-   [5. Why Zero Is Special](#5-why-zero-is-special)
-   [6. Operations](#6-operations)
-   [7. Dry Run](#7-dry-run)
-   [8. Complete C++](#8-complete-c)
-   [9. Complexity](#9-complexity)
-   [10. Common Mistakes](#10-common-mistakes)
-   [11. Mental Model](#11-mental-model)

------------------------------------------------------------------------

# 1. Problem

Maintain a stream supporting:

``` text
add(x)
getProduct(k)
```

Example:

``` text
stream = [3, 2, 5, 4]

getProduct(2)
→ 5 * 4
→ 20
```

Goal:

``` text
add(x)        → amortized O(1)
getProduct(k) → O(1)
```

------------------------------------------------------------------------

# 2. Naive vs Optimal

Naive query:

``` text
last k values
     ↓
multiply all
     ↓
    O(k)
```

Instead, do work during insertion:

``` text
incoming values
      ↓
prefix products
      ↓
one division per query
```

------------------------------------------------------------------------

# 3. Prefix Product Idea

For:

``` text
[3, 2, 5, 4]
```

store:

``` text
index       0   1   2    3     4
prefix     [1,  3,  6,  30,  120]
             │   │   │    │     │
             1   3  3·2  3·2·5  3·2·5·4
```

The leading `1` is the multiplicative identity.

It makes querying the entire segment work without a special case.

------------------------------------------------------------------------

# 4. Algebraic Derivation

Suppose:

``` text
values = [A, B, C, D]
```

Prefix:

``` text
P0 = 1
P1 = A
P2 = A·B
P3 = A·B·C
P4 = A·B·C·D
```

Want the last `2`:

``` text
C·D
```

We know:

``` text
P4 = A·B·C·D
P2 = A·B
```

Divide:

``` text
P4 / P2

= (A·B·C·D) / (A·B)

= C·D
```

General formula with `m` active values:

``` text
┌─────────────────────────────────────┐
│ last k product = prefix[m]          │
│                  ───────────        │
│                  prefix[m-k]        │
└─────────────────────────────────────┘
```

With a vector:

``` cpp
prefix.back()
/
prefix[prefix.size() - k - 1]
```

### Visual

``` text
[A  B | C  D]
 \___/   \___/
 remove   want

A·B·C·D
───────── = C·D
   A·B
```

------------------------------------------------------------------------

# 5. Why Zero Is Special

Suppose:

``` text
stream = [3, 2, 0, 4]
```

If we continued ordinary prefix multiplication:

``` text
[1, 3, 6, 0, 0]
```

Now asking for the last value should return:

``` text
4
```

but division would become:

``` text
0 / 0   ✗
```

So `0` divides the stream into independent segments.

``` text
3 ─ 2 ─ 0 ─ 4 ─ 5
        ↑
      RESET

before zero      current segment
[3,2]            [4,5]
                    ↓
              prefix [1,4,20]
```

On `add(0)`:

``` text
prefix = [1]
```

We only remember products **after the latest zero**.

Why is that enough?

``` text
query stays after zero
→ use prefix division

query crosses zero
→ product = 0
```

------------------------------------------------------------------------

# 6. Operations

## `add(x)`

### Nonzero

``` cpp
prefix.push_back(prefix.back() * x);
```

Example:

``` text
prefix = [1,3,6]

add(5)

prefix = [1,3,6,30]
```

### Zero

``` cpp
prefix.clear();
prefix.push_back(1);
```

Conceptually:

``` text
... 3 ─ 2 ─ 0
            ↑
       old segment ends

prefix
[1,3,6] → [1]
```

------------------------------------------------------------------------

## `getProduct(k)`

Let:

``` text
m = prefix.size() - 1
```

### Case 1 --- `k <= m`

Query stays inside current zero-free segment.

``` text
answer = prefix[m] / prefix[m-k]
```

### Case 2 --- `k > m`

Query extends beyond the current segment.

For a valid stream query, it must cross the latest zero:

``` text
answer = 0
```

Code:

``` cpp
if (k >= prefix.size())
    return 0;

return prefix.back()
     / prefix[prefix.size() - k - 1];
```

Note:

``` text
k = m
```

does **not** cross zero.

It divides by:

``` text
prefix[0] = 1
```

------------------------------------------------------------------------

# 7. Dry Run

Operations:

``` text
add(3)
add(2)
add(0)
add(4)
add(5)
```

### Build state

  Operation   Current segment   `prefix`
  ----------- ----------------- ------------
  start       `[]`              `[1]`
  `add(3)`    `[3]`             `[1,3]`
  `add(2)`    `[3,2]`           `[1,3,6]`
  `add(0)`    `[]`              `[1]`
  `add(4)`    `[4]`             `[1,4]`
  `add(5)`    `[4,5]`           `[1,4,20]`

Current:

``` text
full stream
3 ─ 2 ─ 0 ─ 4 ─ 5
        │   \_____/
        │   active segment
        │
      reset
```

### `getProduct(1)`

``` text
last 1 = [5]

20 / 4
= 5
```

### `getProduct(2)`

``` text
last 2 = [4,5]

20 / 1
= 20
```

### `getProduct(3)`

``` text
last 3 = [0,4,5]
```

Current zero-free segment length:

``` text
m = 2
```

Since:

``` text
3 > 2
```

the query crosses zero:

``` text
answer = 0
```

------------------------------------------------------------------------

# 8. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class ProductOfNumbers {
    // Prefix products after latest zero.
    vector<int> prefix{1};

public:

    void add(int num) {

        if (num == 0) {

            // Start a new zero-free segment.
            prefix.clear();
            prefix.push_back(1);

        } else {

            prefix.push_back(
                prefix.back() * num
            );
        }
    }

    int getProduct(int k) {

        // Query crosses latest zero.
        if (k >= (int)prefix.size())
            return 0;

        int m = prefix.size() - 1;

        return prefix[m]
             / prefix[m - k];
    }
};
```

The problem must guarantee that stored products fit the chosen numeric
type. Use a wider type if the constraints require it.

------------------------------------------------------------------------

# 9. Complexity

  Operation                               Time Reason
  ----------------- -------------------------- -----------------------
  `add(nonzero)`              amortized `O(1)` vector append
  `getProduct(k)`                       `O(1)` one division
  `add(0)`            amortized over additions reset current segment
  Space                                 `O(N)` prefix products

Optimization:

``` text
Before:

getProduct(k)
→ multiply k values
→ O(k)

After:

getProduct(k)
→ numerator / denominator
→ O(1)
```

------------------------------------------------------------------------

# 10. Common Mistakes

### 1. Forget leading `1`

Wrong:

``` text
[3,6,30]
```

Better:

``` text
[1,3,6,30]
```

Now querying the whole active segment divides by `1`.

### 2. Keep multiplying after zero

``` text
[1,3,6,0,0,0]   ✗
```

Reset:

``` text
add(0)
→ prefix = [1]
```

### 3. Wrong zero-crossing condition

Active values:

``` text
m = prefix.size()-1
```

Crosses zero only when:

``` text
k > m
```

Equivalent:

``` cpp
k >= prefix.size()
```

### 4. Use `>=` against `m`

Wrong:

``` text
k >= m → zero
```

because `k == m` means querying the entire active segment.

### 5. Overflow

Prefix products are intermediate state too.

Choose a type that can hold them under the problem's guarantees.

------------------------------------------------------------------------

# 11. Mental Model

Think of zero as a **wall**:

``` text
OLD SEGMENT          ACTIVE SEGMENT

3 ─ 2 ─ 7 ── 0 ┃ 4 ─ 5 ─ 2
                ┃
              ZERO WALL
```

We only store:

``` text
[4,5,2]

prefix:

1 ── 4 ── 20 ── 40
```

Query last `2`:

``` text
4 ─ 5 ─ 2
    \___/
     want

40 / 4 = 10
```

Query last `4`:

``` text
0 ─ 4 ─ 5 ─ 2
↑
crosses zero

answer = 0
```

Remember:

``` text
ADD nonzero
prefix.push_back(prefix.back() * x)

ADD zero
prefix = [1]

QUERY
if k > activeSegmentLength:
    return 0

return prefix[m] / prefix[m-k]
```

> **One sentence:** **Prefix product converts a suffix product into
> division; zero becomes a reset boundary because every suffix crossing
> it already has answer `0`.**
