# Bit Manipulation — Fundamentals
## Binary, Operators, Shifts, Masks, Set/Unset/Check/Flip, Popcount, LSB/MSB
### TLE-Style Self-Study Notes

> **Source:** supplied AlgoZenith/TLE lecture screenshots on fundamental bit operations.
>
> Study flow:
>
> ```text
> Binary representation
> → Bitwise operators
> → Shifts
> → Masks
> → Set / Unset / Check / Flip
> → Complement / Two's Complement
> → Count set bits
> → LSB / MSB
> → Practice
> ```

---

# Table of Contents

- [1. Binary Prerequisite](#1-binary-prerequisite)
- [2. Bit Positions and Powers of Two](#2-bit-positions-and-powers-of-two)
- [3. Bitwise vs Logical Operators](#3-bitwise-vs-logical-operators)
- [4. XOR `^`](#4-xor-)
- [5. OR `|`](#5-or-)
- [6. AND `&`](#6-and-)
- [7. Left Shift `<<`](#7-left-shift-)
- [8. Right Shift `>>`](#8-right-shift-)
- [9. Mask of K Ones](#9-mask-of-k-ones)
- [10. Why Parentheses Matter](#10-why-parentheses-matter)
- [11. Bitwise NOT `~`](#11-bitwise-not-)
- [12. Two's Complement](#12-twos-complement)
- [13. Set the i-th Bit](#13-set-the-i-th-bit)
- [14. Unset the i-th Bit](#14-unset-the-i-th-bit)
- [15. Check the i-th Bit](#15-check-the-i-th-bit)
- [16. Flip the i-th Bit](#16-flip-the-i-th-bit)
- [17. Compact C++ Helpers](#17-compact-c-helpers)
- [18. Traverse Bits](#18-traverse-bits)
- [19. Count Set Bits — Shift Method](#19-count-set-bits--shift-method)
- [20. Count Set Bits — Recursive Method](#20-count-set-bits--recursive-method)
- [21. Brian Kernighan Trick](#21-brian-kernighan-trick)
- [22. Why `x & (x-1)` Works](#22-why-x--x-1-works)
- [23. Built-in Popcount](#23-built-in-popcount)
- [24. Lowest Set Bit](#24-lowest-set-bit)
- [25. Most Significant Bit](#25-most-significant-bit)
- [26. Practice Problems](#26-practice-problems)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Recognition Table](#28-recognition-table)
- [29. Final Mental Model](#29-final-mental-model)

---

# 1. Binary Prerequisite

Every integer is stored as bits:

```text
0 and 1
```

Example:

```text
13 = 1101₂
```

Break it by position:

```text
bit:      3   2   1   0
weight:   8   4   2   1
binary:   1   1   0   1
```

Therefore:

```text
13
=
8 + 4 + 1
```

Another example:

```text
10 = 1010₂ = 8 + 2
```

---

# 2. Bit Positions and Powers of Two

Bits are indexed from the right starting at `0`.

```text
binary:     1   0   1   1   0   1
position:   5   4   3   2   1   0
weight:    32  16   8   4   2   1
```

Bit `i` has value:

```text
2^i
```

So:

```cpp
1LL << i
```

creates a mask with only bit `i` set.

Example:

```text
1 << 3

0001
→
1000
=
8
```

This is the basic mask used throughout bit manipulation.

---

# 3. Bitwise vs Logical Operators

The lecture separates:

```text
BITWISE:
^   |   &   <<   >>   ~

LOGICAL:
!   ||  &&
```

Logical operators reason about:

```text
true / false
```

Bitwise operators work independently on each binary position.

Example:

```cpp
if (a != 0 && b != 0) {
    // logical AND
}

int c = a & b;   // bitwise AND
```

---

# 4. XOR `^`

Truth table:

| A | B | `A ^ B` |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 0 |

Mental model:

```text
same      → 0
different → 1
```

## Lecture Example

```text
5 = 101₂
6 = 110₂
```

```text
  101
^ 110
-----
  011
```

So:

```text
5 ^ 6 = 3
```

Another lecture-style example:

```text
12 = 1100
10 = 1010
```

```text
  1100
^ 1010
------
  0110
```

Therefore:

```text
12 ^ 10 = 6
```

Important identities:

```text
x ^ 0 = x
x ^ x = 0
a ^ b = b ^ a
```

---

# 5. OR `|`

Truth table:

| A | B | `A | B` |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 1 |

Example:

```text
12 = 1100
10 = 1010
```

```text
  1100
| 1010
------
  1110
```

So:

```text
12 | 10 = 14
```

Use OR when you want to force selected bits to `1`.

---

# 6. AND `&`

Truth table:

| A | B | `A & B` |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 0 |
| 1 | 0 | 0 |
| 1 | 1 | 1 |

Example:

```text
12 = 1100
10 = 1010
```

```text
  1100
& 1010
------
  1000
```

So:

```text
12 & 10 = 8
```

Mental model:

```text
AND = FILTER

mask bit 1 → keep original bit
mask bit 0 → erase original bit
```

---

# 7. Left Shift `<<`

Left shift moves bits left.

Example:

```text
8 = 001000
```

```text
8 << 2
=
100000
=
32
```

For non-overflowing non-negative values:

```text
x << y
=
x * 2^y
```

Example:

```text
13 << 2
=
13 * 4
=
52
```

---

# 8. Right Shift `>>`

Right shift moves bits right.

```text
8 = 1000
```

```text
8 >> 2
=
0010
=
2
```

Another:

```text
13 = 1101
```

```text
13 >> 2
=
0011
=
3
```

For non-negative integers:

```text
x >> y
=
floor(x / 2^y)
```

So:

```text
13 >> 2
=
floor(13/4)
=
3
```

---

# 9. Mask of K Ones

A very important construction:

```cpp
(1LL << k) - 1
```

Example:

```text
k = 4
```

```text
1 << 4
=
10000
```

Subtract one:

```text
10000
-
00001
=
01111
```

So:

```text
(1 << k) - 1
```

creates the lowest `k` bits as `1`.

---

## Keep Only the Lowest K Bits

```cpp
x & ((1LL << k) - 1)
```

For non-negative `x`, this is equivalent to:

```text
x mod 2^k
```

Example:

```text
x = 109
k = 3
```

```text
109 = 1101101₂
mask = 0000111₂
```

```text
  1101101
& 0000111
---------
  0000101
```

Result:

```text
5
```

And:

```text
109 % 8 = 5
```

---

# 10. Why Parentheses Matter

The lecture explicitly emphasizes brackets around shift expressions.

To create three `1` bits:

```cpp
((1LL << 3) - 1)
```

Dry run:

```text
1 << 3
=
8

8 - 1
=
7
=
111₂
```

Recommended contest habit:

```cpp
(1LL << i)
((1LL << k) - 1)
x & (1LL << i)
x & ~(1LL << i)
```

Do not make yourself reason about operator precedence during a contest.

---

# 11. Bitwise NOT `~`

NOT flips every bit:

```text
0 → 1
1 → 0
```

Example using eight displayed bits:

```text
x  = 01010010
~x = 10101101
```

Important:

```text
C++ flips every bit of the integer type,
not only the bits you wrote on paper.
```

Therefore `~x` for signed positive `x` is typically negative.

---

# 12. Two's Complement

The lecture connects negation with complement.

For two's-complement integers:

```text
-x
=
~x + 1
```

Equivalent:

```text
~x
=
-x - 1
```

## Tiny 4-Bit Example

```text
+6
=
0110
```

Complement:

```text
1001
```

Add one:

```text
1001
+
0001
=
1010
```

In 4-bit two's complement:

```text
1010
=
-6
```

So:

```text
-x = ~x + 1
```

For a fixed width of `B` bits, the representation of `-x` is also:

```text
2^B - x
```

Example:

```text
B=8
x=17

256-17
=
239
```

The 8-bit bit pattern of `239` represents `-17`.

---

# 13. Set the i-th Bit

Goal:

```text
bit i → 1
```

Formula:

```cpp
x | (1LL << i)
```

Why OR?

```text
0 | 1 = 1
1 | 1 = 1
```

Other mask positions are zero, so other bits remain unchanged.

## Example

```text
x = 8
=
1000
```

Set bit `1`:

```text
mask = 0010
```

```text
  1000
| 0010
------
  1010
```

Result:

```text
10
```

---

# 14. Unset the i-th Bit

Goal:

```text
bit i → 0
```

Formula:

```cpp
x & ~(1LL << i)
```

Reason:

```text
1 << i
```

has one `1`.

After NOT:

```text
every position = 1
except bit i = 0
```

AND keeps all other bits and clears bit `i`.

## Example

```text
x = 14
=
1110
```

Clear bit `2`:

```text
mask     = 0100
~mask    = 1011   // only four visible bits shown
```

```text
  1110
& 1011
------
  1010
```

Result:

```text
10
```

---

# 15. Check the i-th Bit

Two common methods.

## Method 1 — Shift Then AND

```cpp
((x >> i) & 1LL)
```

Example:

```text
x = 13 = 1101
i = 2
```

```text
1101 >> 2
=
0011

0011 & 0001
=
1
```

So bit `2` is set.

## Method 2 — Direct Mask

```cpp
(x & (1LL << i)) != 0
```

Both are valid.

---

# 16. Flip the i-th Bit

Goal:

```text
0 → 1
1 → 0
```

Use XOR:

```cpp
x ^ (1LL << i)
```

Because:

```text
0 ^ 1 = 1
1 ^ 1 = 0
```

Other positions XOR with zero:

```text
x ^ 0 = x
```

## Example

```text
x = 10
=
1010
```

Flip bit `1`:

```text
  1010
^ 0010
------
  1000
```

Result:

```text
8
```

Flip the same bit again:

```text
1000 ^ 0010
=
1010
```

Back to:

```text
10
```

---

# 17. Compact C++ Helpers

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll setBit(ll x, int i) {
    return x | (1LL << i);
}

ll clearBit(ll x, int i) {
    return x & ~(1LL << i);
}

bool checkBit(ll x, int i) {
    return ((x >> i) & 1LL) != 0;
}

ll flipBit(ll x, int i) {
    return x ^ (1LL << i);
}
```

The formulas to remember:

```text
SET:
x | (1<<i)

UNSET:
x & ~(1<<i)

CHECK:
(x>>i)&1

FLIP:
x ^ (1<<i)
```

---

# 18. Traverse Bits

For a 32-bit value:

```cpp
for (int i = 0; i < 32; i++) {
    if ((x >> i) & 1U) {
        // bit i is set
    }
}
```

For `long long`, use an appropriate bit range.

For positive `x`, another option is to repeatedly shift until:

```text
x == 0
```

---

# 19. Count Set Bits — Shift Method

Example:

```text
x = 13
=
1101₂
```

Number of `1` bits:

```text
3
```

Code:

```cpp
int countSetBits(unsigned int x) {
    int answer = 0;

    while (x > 0) {
        answer += x & 1U;
        x >>= 1;
    }

    return answer;
}
```

Each iteration consumes one binary digit.

Complexity:

```text
O(log x)
```

for value-based analysis.

For a fixed-width integer, the number of inspected positions is bounded by the machine word size.

---

# 20. Count Set Bits — Recursive Method

The lecture shows the recurrence:

```text
count(x)
=
count(x >> 1)
+
(x & 1)
```

Base:

```text
count(0)=0
```

Code:

```cpp
int countSetBitsRecursive(unsigned int x) {
    if (x == 0) {
        return 0;
    }

    return countSetBitsRecursive(x >> 1) + (x & 1U);
}
```

## Dry Run

```text
x = 13 = 1101
```

```text
count(13)
=
count(6) + 1

count(6)
=
count(3) + 0

count(3)
=
count(1) + 1

count(1)
=
count(0) + 1

count(0)
=
0
```

Total:

```text
3
```

---

# 21. Brian Kernighan Trick

The lecture also shows:

```cpp
while (x) {
    answer++;
    x &= (x - 1);
}
```

Every iteration removes exactly one set bit.

Therefore:

```text
number of loop iterations
=
number of 1 bits
```

If popcount is `P`:

```text
Time = O(P)
```

## Example

```text
x = 13
=
1101
```

Iteration 1:

```text
1101
&
1100
=
1100
```

Iteration 2:

```text
1100
&
1011
=
1000
```

Iteration 3:

```text
1000
&
0111
=
0000
```

Three iterations:

```text
popcount = 3
```

---

# 22. Why `x & (x-1)` Works

Suppose:

```text
x = xxxxx1000...000
          ^
          lowest set bit
```

Then:

```text
x-1
=
xxxxx0111...111
```

AND:

```text
xxxxx1000...000
&
xxxxx0111...111
----------------
xxxxx0000...000
```

So the lowest set bit disappears.

This is one of the most important bitwise transformations in CP.

---

# 23. Built-in Popcount

For `int`:

```cpp
__builtin_popcount(x)
```

For `long long`:

```cpp
__builtin_popcountll(x)
```

Example:

```cpp
long long x = 13;

cout << __builtin_popcountll(x) << '\n';
```

Output:

```text
3
```

Use the built-in when you only need the answer.

Still understand Kernighan because many harder tricks build on:

```text
x & (x-1)
```

---

# 24. Lowest Set Bit

The lowest set bit is the least significant `1`.

Example:

```text
x = 12
=
1100
```

Lowest set bit value:

```text
0100
=
4
```

Two's-complement identity:

```cpp
x & -x
```

isolates it.

Example:

```text
12 & -12
=
4
```

This becomes useful later in:

```text
Fenwick Tree
bit decomposition
subset tricks
```

---

# 25. Most Significant Bit

The most significant bit is the highest set bit.

Example:

```text
x = 109
=
1101101₂
```

Highest set bit position:

```text
6
```

because:

```text
2^6 = 64
```

and:

```text
64 <= 109 < 128
```

Therefore for positive `x`:

```text
MSB position
=
floor(log2(x))
```

Simple code:

```cpp
int msbPosition(unsigned int x) {
    int position = -1;

    while (x > 0) {
        position++;
        x >>= 1;
    }

    return position;
}
```

---

# 26. Practice Problems

These are practice problems matched to the lecture patterns.

## Problem 1 — Number of 1 Bits

🔗 [LeetCode 191 — Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/)

Pattern:

```text
popcount
→ built-in or x &= x-1
```

---

## Problem 2 — Counting Bits

🔗 [LeetCode 338 — Counting Bits](https://leetcode.com/problems/counting-bits/)

Useful recurrence:

```text
bits[x]
=
bits[x >> 1]
+
(x & 1)
```

This is the lecture's recursive popcount idea reused as DP.

---

## Problem 3 — Power of Two

🔗 [LeetCode 231 — Power of Two](https://leetcode.com/problems/power-of-two/)

A positive power of two contains exactly one set bit.

```text
1 = 0001
2 = 0010
4 = 0100
8 = 1000
```

So:

```cpp
n > 0 && (n & (n - 1)) == 0
```

---

## Problem 4 — Single Number

🔗 [LeetCode 136 — Single Number](https://leetcode.com/problems/single-number/)

Pattern:

```text
x ^ x = 0
x ^ 0 = x
```

XOR all values.

Pairs disappear.

Unique value remains.

---

## Problem 5 — Missing Number

🔗 [LeetCode 268 — Missing Number](https://leetcode.com/problems/missing-number/)

Pattern:

```text
XOR all expected values
with
all present values
```

Matching values cancel.

The missing value remains.

---

## Problem 6 — Reverse Bits

🔗 [LeetCode 190 — Reverse Bits](https://leetcode.com/problems/reverse-bits/)

Practice:

```text
check bit
set bit
shift
bit positions
```

---

## Problem 7 — Fedor and New Game

🔗 [Codeforces 467B — Fedor and New Game](https://codeforces.com/problemset/problem/467/B)

To find positions where two masks differ:

```text
a ^ b
```

has a `1` exactly at every differing bit.

Then:

```text
popcount(a ^ b)
```

gives Hamming distance.

Recognition:

```text
"different bit positions"
→ XOR + popcount
```

---

# 27. Common Mistakes

## Mistake 1 — `&&` vs `&`

```cpp
a && b
```

is logical.

```cpp
a & b
```

is bitwise.

Likewise:

```text
|| ≠ |
```

---

## Mistake 2 — Use `1 << i` for Large `long long` Bits

Prefer:

```cpp
1LL << i
```

because plain `1` is an `int`.

---

## Mistake 3 — Forget Parentheses

Prefer:

```cpp
(1LL << i)
((1LL << k) - 1)
x & (1LL << i)
x & ~(1LL << i)
```

---

## Mistake 4 — Check Bit with `== 1`

Wrong:

```cpp
(x & (1LL << i)) == 1
```

For `i > 0`, the result when set is:

```text
2^i
```

not `1`.

Use:

```cpp
(x & (1LL << i)) != 0
```

or:

```cpp
((x >> i) & 1LL)
```

---

## Mistake 5 — Forget `~` Flips the Entire Integer Width

If you want to flip only the lowest `k` bits:

```cpp
x ^ ((1LL << k) - 1)
```

---

## Mistake 6 — Blind Shift Arithmetic on Negative Signed Values

The easy identities:

```text
x << k = x*2^k
x >> k ≈ x/2^k
```

should be used as simple mental models for non-negative values.

Do not casually derive signed-negative behavior from them.

---

## Mistake 7 — Popcount Loop on Signed Negative Numbers

Prefer unsigned types or built-ins when you want to reason about raw bit patterns.

---

# 28. Recognition Table

| Signal | Think |
|---|---|
| Turn bit `i` on | `x | (1<<i)` |
| Turn bit `i` off | `x & ~(1<<i)` |
| Test bit `i` | `(x>>i)&1` |
| Toggle bit `i` | `x ^ (1<<i)` |
| Lowest `k` bits | `x & ((1<<k)-1)` |
| Multiply by power of two | left shift |
| Divide non-negative value by power of two | right shift |
| Count ones | popcount / Kernighan |
| Remove lowest set bit | `x & (x-1)` |
| Isolate lowest set bit | `x & -x` |
| Bit positions differ | XOR |
| Unique value among pairs | XOR cancellation |

---

# 29. Final Mental Model

Reduce most elementary bit questions to:

```text
TARGET BIT
+
MASK
+
OPERATOR
```

Mask:

```text
1 << i
```

Then:

```text
SET
x | mask

CLEAR
x & ~mask

CHECK
x & mask

FLIP
x ^ mask
```

ASCII:

```text
                    BIT i
                      |
                      v
                 (1 << i)
                      |
       +--------------+--------------+
       |              |              |
       v              v              v
      OR             AND            XOR
       |              |              |
       v              v              v
      SET            FILTER         TOGGLE
```

Counting:

```text
shift one bit at a time
→ inspect every bit

x & (x-1)
→ remove one set bit at a time
```

Lower-bit mask:

```text
(1 << k) - 1
=
000...00111...111
        k ones
```

Two's complement:

```text
-x = ~x + 1
```

---

## 60-Second Revision Card

```text
XOR:
same→0, different→1

OR:
either 1→1

AND:
both 1→1

LEFT SHIFT:
x<<k ≈ x*2^k

RIGHT SHIFT:
x>>k ≈ x/2^k for non-negative x

SET:
x | (1<<i)

CLEAR:
x & ~(1<<i)

CHECK:
(x>>i)&1

FLIP:
x ^ (1<<i)

K LOWER ONES:
(1<<k)-1

REMOVE LSB:
x & (x-1)

ISOLATE LSB:
x & -x

NEGATIVE:
-x = ~x + 1

COUNT ONES:
__builtin_popcount / Kernighan
```

The best habit is:

```text
Do not memorize the formula first.

Draw the mask in binary,
then ask what the operator does
at the target bit.
```
