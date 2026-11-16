# AlgoZenith — Product of Last K Numbers (STL)
## Visual Self-Learning Version

> **Core idea:** Maintain **prefix products** for the current zero-free segment.  
> A suffix product becomes one division. When `0` arrives, reset the segment.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Naive vs Optimal](#2-naive-vs-optimal)
- [3. Prefix Product Idea](#3-prefix-product-idea)
- [4. Algebraic Derivation](#4-algebraic-derivation)
- [5. Why Zero Is Special](#5-why-zero-is-special)
- [6. State Design](#6-state-design)
- [7. add(x) — Step by Step](#7-addx--step-by-step)
- [8. getProduct(k) — Step by Step](#8-getproductk--step-by-step)
- [9. Full Dry Run](#9-full-dry-run)
- [10. Complete C++17](#10-complete-c17)
- [11. Complexity](#11-complexity)
- [12. Common Mistakes](#12-common-mistakes)
- [13. Recognition Checklist](#13-recognition-checklist)
- [14. Final Mental Model](#14-final-mental-model)

---

# 0. Prerequisites

## 0.1 Product

```text
[3,2,5]

product
=
3 × 2 × 5
=
30
```

---

## 0.2 Suffix

A suffix ends at the final position.

```text
a = [3,2,5,4]

last 1 = [4]
last 2 = [5,4]
last 3 = [2,5,4]
```

So `getProduct(k)` asks for a **suffix product**.

---

## 0.3 Prefix Product

For:

```text
[3,2,5]
```

store cumulative products:

```text
prefix = [1,3,6,30]
```

Meaning:

```text
1
3
3×2
3×2×5
```

---

## 0.4 Why Start With 1?

`1` is the multiplicative identity:

```text
1 × x = x
```

It lets us query the entire current segment without a special case.

---

## 0.5 Division Cancels Earlier Factors

```text
3×2×5×4
---------
   3×2

=
5×4
```

That cancellation is the key observation.

---

# 1. Problem

Support:

```text
add(x)
getProduct(k)
```

Example:

```text
stream = [3,2,5,4]

getProduct(2)
-> 5×4
-> 20
```

Target:

```text
add(x)        -> amortized O(1)
getProduct(k) -> O(1)
```

---

# 2. Naive vs Optimal

## 2.1 Naive

For every query:

```text
take last k values
      |
      v
multiply all k
      |
      v
O(k)
```

Example:

```text
last 4 = [5,4,7,6]

5×4×7×6
```

---

## 2.2 Better

Do work when values are added:

```text
new values
   |
   v
prefix products
   |
   v
query last k
   |
   v
one division
```

Goal:

```text
O(1) query
```

---

# 3. Prefix Product Idea

Take:

```text
values = [3,2,5,4]
```

Start:

```text
prefix = [1]
```

### add(3)

```text
1×3 = 3

prefix = [1,3]
```

### add(2)

```text
3×2 = 6

prefix = [1,3,6]
```

### add(5)

```text
6×5 = 30

prefix = [1,3,6,30]
```

### add(4)

```text
30×4 = 120

prefix = [1,3,6,30,120]
```

ASCII:

```text
values:

      3      2      5      4
      |      |      |      |
      v      v      v      v

prefix:

1 --> 3 --> 6 --> 30 --> 120
      3      3×2    3×2×5   3×2×5×4
```

---

# 4. Algebraic Derivation

Suppose:

```text
values = [A,B,C,D]
```

Prefix products:

```text
P0 = 1
P1 = A
P2 = A×B
P3 = A×B×C
P4 = A×B×C×D
```

Want last `2`:

```text
C×D
```

Use:

```text
P4 / P2
```

Because:

```text
A×B×C×D
---------
   A×B

=
C×D
```

ASCII:

```text
[A] [B] | [C] [D]
 \___/      \___/
 remove       want
```

General formula:

```text
m = number of values in current segment

last k product
=
prefix[m] / prefix[m-k]
```

Example:

```text
prefix = [1,3,6,30,120]
m = 4
k = 2

120 / 6
=
20
```

---

# 5. Why Zero Is Special

Suppose:

```text
stream = [3,2,0,4]
```

If we keep multiplying:

```text
prefix = [1,3,6,0,0]
```

Now `getProduct(1)` should return:

```text
4
```

but division would involve:

```text
0 / 0
```

which is invalid.

So zero must be handled differently.

---

## 5.1 Zero as a Wall

```text
OLD SEGMENT          ACTIVE SEGMENT

3 --- 2 --- 0 | 4 --- 5
              ^
              |
          ZERO WALL
```

Anything crossing zero has product:

```text
0
```

Anything fully after zero can use prefix division.

---

## 5.2 Reset on Zero

When:

```text
add(0)
```

reset:

```text
prefix = [1]
```

For:

```text
3,2,0,4,5
```

we only keep the active segment:

```text
[4,5]
```

and:

```text
prefix = [1,4,20]
```

---

## 5.3 Two Query Cases

### Query stays after zero

```text
last 2 = [4,5]

use prefix division
```

### Query crosses zero

```text
last 3 = [0,4,5]

answer = 0
```

---

# 6. State Design

Maintain:

```cpp
vector<long long> prefix{1};
```

Meaning:

```text
prefix stores products
after the latest zero
```

Example:

```text
full stream:
3 --- 2 --- 0 | 4 --- 5

stored:
prefix = [1,4,20]
```

---

# 7. add(x) — Step by Step

Two cases.

## 7.1 Nonzero

```text
newPrefix
=
prefix.back() × x
```

Code:

```cpp
prefix.push_back(prefix.back() * x);
```

Example:

```text
prefix = [1,3,6]

add(5)

6×5 = 30

prefix = [1,3,6,30]
```

---

## 7.2 Zero

Zero ends the current segment.

```cpp
prefix.clear();
prefix.push_back(1);
```

ASCII:

```text
before:

3 --- 2
prefix = [1,3,6]

add(0)

3 --- 2 --- 0
              ^
              |
            reset

after:

prefix = [1]
```

---

## 7.3 Flow

```text
add(x)
  |
  v
x == 0 ?
 /      \
yes      no
 |        |
 v        v
reset    prefix.back() × x
[1]        |
            v
        append result
```

---

# 8. getProduct(k) — Step by Step

Let:

```text
m = prefix.size() - 1
```

`m` is the number of values after the latest zero.

---

## 8.1 Case 1 — k <= m

The query stays inside the current zero-free segment.

```text
answer
=
prefix[m] / prefix[m-k]
```

---

## 8.2 Case 2 — k > m

The query needs more values than exist after the latest zero.

Therefore it crosses zero.

```text
answer = 0
```

---

## 8.3 Why k == m Is Valid

Suppose:

```text
active segment = [4,5]
m = 2
```

Query:

```text
getProduct(2)
```

This is the whole active segment.

Formula:

```text
prefix[2] / prefix[0]
=
20 / 1
=
20
```

So zero-crossing condition is:

```text
k > m
```

not:

```text
k >= m
```

Equivalent:

```cpp
if (k >= (int)prefix.size()) {
    return 0;
}
```

because:

```text
prefix.size() = m + 1
```

---

## 8.4 ASCII Query

```text
active segment:

[4] [5] [2]

prefix:

1   4   20   40
```

Last `2`:

```text
[4] | [5] [2]
      \_____/
       want
```

Compute:

```text
40 / 4
=
10
```

Correct:

```text
5×2
=
10
```

---

# 9. Full Dry Run

Operations:

```text
add(3)
add(2)
add(0)
add(4)
add(5)
```

### Start

```text
prefix = [1]
```

### add(3)

```text
prefix = [1,3]
```

### add(2)

```text
prefix = [1,3,6]
```

### add(0)

Reset:

```text
prefix = [1]
```

### add(4)

```text
prefix = [1,4]
```

### add(5)

```text
prefix = [1,4,20]
```

Full stream:

```text
3 --- 2 --- 0 | 4 --- 5
```

Current segment:

```text
[4,5]
```

---

## getProduct(1)

```text
last 1 = [5]

20 / 4
=
5
```

---

## getProduct(2)

```text
last 2 = [4,5]

20 / 1
=
20
```

---

## getProduct(3)

Current segment length:

```text
m = 2
```

But:

```text
k = 3
```

Therefore:

```text
k > m
```

Query crosses zero:

```text
[0,4,5]
```

Answer:

```text
0
```

---

## State Table

| Operation | Active Segment | Prefix |
|---|---|---|
| Start | `[]` | `[1]` |
| `add(3)` | `[3]` | `[1,3]` |
| `add(2)` | `[3,2]` | `[1,3,6]` |
| `add(0)` | `[]` | `[1]` |
| `add(4)` | `[4]` | `[1,4]` |
| `add(5)` | `[4,5]` | `[1,4,20]` |

---

# 10. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class ProductOfNumbers {
    // Prefix products after latest zero.
    vector<long long> prefix{1};

public:
    void add(long long num) {
        if (num == 0) {
            prefix.clear();
            prefix.push_back(1);
            return;
        }

        prefix.push_back(
            prefix.back() * num
        );
    }

    long long getProduct(int k) {
        int m = (int)prefix.size() - 1;

        if (k > m) {
            return 0;
        }

        return prefix[m] / prefix[m - k];
    }
};
```

Use a numeric type that safely fits the products allowed by the problem constraints.

---

# 11. Complexity

| Operation | Time |
|---|---:|
| `add(nonzero)` | amortized `O(1)` |
| `add(0)` | amortized `O(1)` |
| `getProduct(k)` | `O(1)` |
| Space | `O(N)` for current segment |

Optimization:

```text
Before:
getProduct(k)
-> multiply k values
-> O(k)

After:
getProduct(k)
-> one division
-> O(1)
```

---

# 12. Common Mistakes

## 12.1 Forget leading 1

Wrong:

```text
[3,6,30]
```

Better:

```text
[1,3,6,30]
```

So whole-segment query can divide by `1`.

---

## 12.2 Continue multiplying after zero

Wrong:

```text
[1,3,6,0,0,0]
```

Correct:

```text
add(0)
-> prefix = [1]
```

---

## 12.3 Wrong zero-crossing condition

Let:

```text
m = prefix.size() - 1
```

Crosses zero only when:

```text
k > m
```

---

## 12.4 Use k >= m

Wrong.

When:

```text
k == m
```

the query is exactly the whole current segment.

---

## 12.5 Forget overflow

Prefix products can become large.

Use a type compatible with the problem guarantees.

---

# 13. Recognition Checklist

Think of this pattern when you see:

```text
stream
+
product of last k
+
many queries
```

Ask:

```text
1. Is the query a suffix product?

2. Can prefix products turn it into division?

3. What is the multiplicative identity?
   -> 1

4. What breaks prefix division?
   -> zero

5. Can zero be a reset boundary?
   -> yes

6. What state is needed?
   -> prefix products after latest zero

7. How many active values are after zero?
   -> prefix.size() - 1

8. When does query cross zero?
   -> k > active length

9. What if k == active length?
   -> divide by prefix[0] = 1

10. Are products safe in the chosen type?
```

---

# 14. Final Mental Model

Think of zero as a wall:

```text
OLD SEGMENT              ACTIVE SEGMENT

3 --- 2 --- 7 --- 0 | 4 --- 5 --- 2
                    ^
                    |
                ZERO WALL
```

Store only:

```text
[4,5,2]
```

Prefix:

```text
1 --> 4 --> 20 --> 40
```

Query last `2`:

```text
[4] | [5] [2]
      \_____/
       want

40 / 4
=
10
```

Query last `4`:

```text
0 | 4 5 2
^
|
crosses zero

answer = 0
```

Remember:

```text
ADD nonzero
prefix.push_back(prefix.back() * x)

ADD zero
prefix = [1]

QUERY
m = prefix.size() - 1

if k > m:
    return 0

return prefix[m] / prefix[m-k]
```

The one sentence to remember:

```text
Prefix product converts a suffix product into division;
zero becomes a reset wall because every query crossing it has product 0.
```
