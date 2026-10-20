# Cyclic Property of Bits
## Prefix Counting of Set Bits, Infinite Binary String, K-th `1`, and Binary Search on Answer
### TLE-Style Self-Study Notes

> **Source basis:** the supplied AlgoZenith/TLE lecture screenshots on **Cyclic Property of Bits**.
>
> The lecture moves from an infinite concatenation of binary representations to a reusable counting function:
>
> ```text
> count number of 1-bits in [0 ... X]
> ```
>
> Once that prefix function is available, it becomes a monotonic `check(X)` and can be combined with **Binary Search on Answer**.
>
> The lecture then applies the same cyclic-bit counting idea to:
>
> **[LeetCode 3007 — Maximum Number That Sum of the Prices Is Less Than or Equal to K](https://leetcode.com/problems/maximum-number-that-sum-of-the-prices-is-less-than-or-equal-to-k/)**
>
> Teaching dry runs and explicit derivations below are expanded from the lecture so the note can be used without replaying the video.

---

# Table of Contents

- [1. Prerequisite 1 — Binary Representation](#1-prerequisite-1--binary-representation)
- [2. Prerequisite 2 — Bit Positions](#2-prerequisite-2--bit-positions)
- [3. Prerequisite 3 — Bit Length](#3-prerequisite-3--bit-length)
- [4. The Core Cyclic Property](#4-the-core-cyclic-property)
- [5. Why Every Bit Has a Cycle](#5-why-every-bit-has-a-cycle)
- [6. Counting `1`s at One Bit Position in `[0...X]`](#6-counting-1s-at-one-bit-position-in-0x)
- [7. Deriving the Formula Step by Step](#7-deriving-the-formula-step-by-step)
- [8. Detailed Dry Run — Count `1`s in `[0...9]`](#8-detailed-dry-run--count-1s-in-09)
- [9. Prefix Function — Total Set Bits in `[0...X]`](#9-prefix-function--total-set-bits-in-0x)
- [10. Why the Prefix Function Is Monotonic](#10-why-the-prefix-function-is-monotonic)
- [11. Lecture Application — Infinite Binary String](#11-lecture-application--infinite-binary-string)
- [12. Brute Force and Why It Fails](#12-brute-force-and-why-it-fails)
- [13. Problem 1 — Find the Number Containing the K-th `1`](#13-problem-1--find-the-number-containing-the-k-th-1)
- [14. Binary Search for the First True](#14-binary-search-for-the-first-true)
- [15. Detailed Dry Run — K = 10](#15-detailed-dry-run--k--10)
- [16. Prerequisite 4 — Prefix Length of the Infinite String](#16-prerequisite-4--prefix-length-of-the-infinite-string)
- [17. Find the Exact Index of the K-th `1`](#17-find-the-exact-index-of-the-k-th-1)
- [18. Complete C++17 — K-th `1` Position](#18-complete-c17--k-th-1-position)
- [19. Problem 2 — LeetCode 3007](#19-problem-2--leetcode-3007)
- [20. Understanding the Price Function](#20-understanding-the-price-function)
- [21. Count Valuable `1`s up to `num`](#21-count-valuable-1s-up-to-num)
- [22. Detailed Dry Run — `x = 2`](#22-detailed-dry-run--x--2)
- [23. Binary Search for the Maximum Valid Number](#23-binary-search-for-the-maximum-valid-number)
- [24. Complete C++17 — LeetCode 3007](#24-complete-c17--leetcode-3007)
- [25. Complexity](#25-complexity)
- [26. First-True vs Last-True](#26-first-true-vs-last-true)
- [27. Common Mistakes](#27-common-mistakes)
- [28. Recognition Table](#28-recognition-table)
- [29. Final Mental Model](#29-final-mental-model)

---

# 1. Prerequisite 1 — Binary Representation

Every non-negative integer has a binary representation.

```text
0 → 0
1 → 1
2 → 10
3 → 11
4 → 100
5 → 101
6 → 110
7 → 111
8 → 1000
9 → 1001
```

The lecture builds the infinite string:

```text
S =
"0" + "1" + "10" + "11" + "100" + "101" + ...
```

So visually:

```text
number:   0   1    2    3     4      5      6      7       8
binary:   0   1   10   11   100    101    110    111    1000

S = 0110111001011101111000...
```

We will later ask:

```text
Where is the K-th '1' in this infinite string?
```

---

# 2. Prerequisite 2 — Bit Positions

In code, bit positions are normally **0-based from the right**.

Example:

```text
13 = 1101
```

```text
bit:    3 2 1 0
value:  1 1 0 1
```

So:

```text
bit 0 → 1
bit 1 → 0
bit 2 → 1
bit 3 → 1
```

Check bit `i`:

```cpp
(value >> i) & 1LL
```

or:

```cpp
(value & (1LL << i)) != 0
```

---

# 3. Prerequisite 3 — Bit Length

Numbers with the same binary length come in power-of-two blocks.

```text
1-bit:
1

2-bit:
2 ... 3

3-bit:
4 ... 7

4-bit:
8 ... 15

5-bit:
16 ... 31
```

General form:

```text
L-bit numbers:

2^(L-1)
...
2^L - 1
```

Count:

```text
2^(L-1)
```

This power-of-two grouping appears repeatedly in bit problems.

---

# 4. The Core Cyclic Property

The key lecture idea is:

> For a fixed bit position, the pattern of `0`s and `1`s repeats periodically as numbers increase.

Consider numbers:

```text
0 1 2 3 4 5 6 7 8 9 ...
```

---

## Bit 0

```text
0 1 0 1 0 1 0 1 ...
```

Pattern:

```text
0 | 1 | 0 | 1 | ...
```

Cycle length:

```text
2
```

---

## Bit 1

```text
0 0 1 1 0 0 1 1 ...
```

Pattern:

```text
00 | 11 | 00 | 11 | ...
```

Cycle length:

```text
4
```

---

## Bit 2

```text
0 0 0 0 1 1 1 1 0 0 0 0 1 1 1 1 ...
```

Pattern:

```text
0000 | 1111 | 0000 | 1111 | ...
```

Cycle length:

```text
8
```

---

# 5. Why Every Bit Has a Cycle

For 0-based bit `i`:

```text
0-block length = 2^i
1-block length = 2^i
```

Therefore:

```text
complete cycle length
=
2^i + 2^i
=
2^(i+1)
```

Inside each complete cycle:

```text
number of ones
=
2^i
```

This gives the entire counting formula.

---

# 6. Counting `1`s at One Bit Position in `[0...X]`

We want:

```text
How many numbers from 0 through X
have bit i set?
```

Number of integers in `[0...X]`:

```text
total = X + 1
```

For bit `i`:

```text
halfCycle = 2^i

fullCycle = 2^(i+1)
```

Every complete cycle contributes:

```text
2^i
```

ones.

---

# 7. Deriving the Formula Step by Step

For bit `i`:

```text
total = X + 1
```

### Step 1 — Number of complete cycles

```text
full =
floor(total / 2^(i+1))
```

---

### Step 2 — Ones from complete cycles

Each full cycle contributes:

```text
2^i
```

ones.

So:

```text
onesFromFullCycles
=
full * 2^i
```

---

### Step 3 — Remaining numbers

```text
left =
total % 2^(i+1)
```

Inside the partial cycle:

```text
first 2^i numbers
→ bit is 0

next 2^i numbers
→ bit is 1
```

Therefore extra ones are:

```text
extra =
max(left - 2^i, 0)
```

---

### Final Formula

```text
onesAtBit(i, X)

=
floor((X+1) / 2^(i+1)) * 2^i

+

max(
    (X+1) % 2^(i+1) - 2^i,
    0
)
```

This is the central formula of the lecture.

---

# 8. Detailed Dry Run — Count `1`s in `[0...9]`

We want:

```text
f(9)
=
total number of set bits
in binary representations of 0...9
```

There are:

```text
total = 9 + 1 = 10
```

---

## Bit 0

Pattern:

```text
0 1 0 1 0 1 0 1 0 1
```

```text
halfCycle = 1
fullCycle = 2

full = 10 / 2 = 5
left = 10 % 2 = 0
```

Contribution:

```text
5 * 1 + 0
=
5
```

---

## Bit 1

Pattern:

```text
0 0 1 1 0 0 1 1 0 0
```

```text
halfCycle = 2
fullCycle = 4

full = 10 / 4 = 2
left = 10 % 4 = 2
```

Contribution:

```text
2 * 2
+
max(2-2, 0)

=
4
```

---

## Bit 2

Pattern:

```text
0 0 0 0 1 1 1 1 0 0
```

```text
halfCycle = 4
fullCycle = 8

full = 10 / 8 = 1
left = 10 % 8 = 2
```

Contribution:

```text
1 * 4
+
max(2-4, 0)

=
4
```

---

## Bit 3

For `0...9`:

```text
0 0 0 0 0 0 0 0 1 1
```

```text
halfCycle = 8
fullCycle = 16

full = 0
left = 10
```

Contribution:

```text
0
+
max(10-8, 0)

=
2
```

---

## Total

```text
bit 0 → 5
bit 1 → 4
bit 2 → 4
bit 3 → 2

total = 5+4+4+2
      = 15
```

Check directly:

```text
0    → 0
1    → 1
10   → 1
11   → 2
100  → 1
101  → 2
110  → 2
111  → 3
1000 → 1
1001 → 2

sum = 15
```

Correct.

---

# 9. Prefix Function — Total Set Bits in `[0...X]`

Now sum the contribution of every bit position.

```cpp
long long countOnesPrefix(long long x) {
    if (x < 0) {
        return 0;
    }

    long long total = x + 1;
    long long answer = 0;

    for (int bit = 0; bit < 62; bit++) {
        long long halfCycle = 1LL << bit;

        if (halfCycle > total) {
            break;
        }

        long long fullCycle = halfCycle << 1;
        long long full = total / fullCycle;
        long long left = total % fullCycle;

        answer += full * halfCycle;
        answer += max(0LL, left - halfCycle);
    }

    return answer;
}
```

Mental model:

```text
for every bit:
    count complete 0/1 cycles
    +
    count extra 1s from partial cycle
```

---

# 10. Why the Prefix Function Is Monotonic

Define:

```text
f(X)
=
number of `1`s
in binary representations of all numbers 0...X
```

Then:

```text
f(X+1)
=
f(X)
+
popcount(X+1)
```

Since:

```text
popcount(X+1) >= 1
```

for positive `X+1`:

```text
f(X+1) > f(X)
```

So:

```text
f(X)
```

is monotonic increasing.

That makes it perfect for:

```text
Binary Search on Answer
```

---

# 11. Lecture Application — Infinite Binary String

The lecture constructs:

```text
S =
binary(0)
+
binary(1)
+
binary(2)
+
...
```

Example:

```text
0 | 1 | 10 | 11 | 100 | 101 | 110 | 111 | 1000 | ...
```

Suppose a query gives:

```text
K
```

and asks about the:

```text
K-th `1`
```

in this conceptual infinite string.

The string itself is far too large to generate for big queries.

The key is:

```text
do not build S
```

Instead count how many `1`s occur before a candidate number.

---

# 12. Brute Force and Why It Fails

A direct simulation could:

```text
num = 0

while number of 1s < K:
    convert num to binary
    append it to S
    count new 1s
    num++
```

This is useful only for tiny constraints.

For large `K`:

```text
the required number can be huge
```

and constructing the string wastes both:

```text
time
memory
```

The lecture replaces this with:

```text
counting + binary search
```

---

# 13. Problem 1 — Find the Number Containing the K-th `1`

We need the smallest number `X` such that:

```text
f(X) >= K
```

where:

```text
f(X)
=
number of 1s in binary(0), binary(1), ..., binary(X)
```

So define:

```text
check(X):
    f(X) >= K
```

Because `f(X)` is monotonic:

```text
false false false false true true true ...
```

we need:

```text
FIRST TRUE
```

---

# 14. Binary Search for the First True

```cpp
long long numberContainingKthOne(long long k) {
    long long low = 1;
    long long high = k;
    long long answer = k;

    while (low <= high) {
        long long mid = low + (high - low) / 2;

        if (countOnesPrefix(mid) >= k) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}
```

Why is:

```text
high = K
```

enough?

Every positive integer has at least one set bit.

Therefore:

```text
f(K) >= K
```

so a valid answer exists no later than `K`.

---

# 15. Detailed Dry Run — K = 10

First compute a few prefix counts:

```text
f(1)=1
f(2)=2
f(3)=4
f(4)=5
f(5)=7
f(6)=9
f(7)=12
```

We need:

```text
first X such that f(X) >= 10
```

So:

```text
X = 7
```

because:

```text
f(6)=9  <10
f(7)=12 >=10
```

Therefore the 10-th `1` occurs inside:

```text
binary(7)
=
111
```

How far inside?

Number of `1`s before 7:

```text
f(6)=9
```

So inside `111`, we need:

```text
10 - 9
=
1st `1`
```

The first character of `"111"` is already `1`.

So the desired `1` is at offset:

```text
0
```

inside `binary(7)`.

---

# 16. Prerequisite 4 — Prefix Length of the Infinite String

To get the **global character index**, not just the containing number, we also need:

```text
total number of binary digits
in representations 0...X
```

Call it:

```text
digitsPrefix(X)
```

Numbers with bit length `L` are:

```text
2^(L-1)
...
2^L - 1
```

There are:

```text
2^(L-1)
```

such numbers.

Their digit contribution is:

```text
L * 2^(L-1)
```

---

## Example — Digits Through Number 6

```text
0    → 1 digit
1    → 1 digit
2,3  → 2 digits each
4,5,6 → 3 digits each
```

Total:

```text
1
+1
+2*2
+3*3

=
1+1+4+9

=
15
```

So:

```text
binary(7)
```

starts at global 0-based index:

```text
15
```

---

## C++17

```cpp
long long binaryDigitsPrefix(long long x) {
    if (x < 0) {
        return 0;
    }

    if (x == 0) {
        return 1;
    }

    long long answer = 1;  // representation of 0
    long long start = 1;
    long long length = 1;

    while (start <= x) {
        long long end;

        if (start > x / 2) {
            end = x;
        } else {
            end = min(x, start * 2 - 1);
        }

        answer += (end - start + 1) * length;

        if (end == x) {
            break;
        }

        start *= 2;
        length++;
    }

    return answer;
}
```

---

# 17. Find the Exact Index of the K-th `1`

After finding:

```text
num = smallest X with f(X) >= K
```

we know the K-th `1` lies inside:

```text
binary(num)
```

---

## Step 1 — Rank of the desired `1` inside `num`

```text
onesBeforeNum
=
f(num-1)
```

So:

```text
need
=
K - f(num-1)
```

`need` is 1-based.

---

## Step 2 — Find its offset in `binary(num)`

Scan binary digits:

```text
MSB → LSB
```

Every time we see `1`:

```text
need--
```

When:

```text
need == 0
```

we found the desired position.

---

## Step 3 — Convert to global index

Start index of `binary(num)`:

```text
digitsPrefix(num-1)
```

Therefore:

```text
answer
=
digitsPrefix(num-1)
+
offsetInsideNum
```

---

## K = 10 Again

We found:

```text
num = 7
binary(7) = 111
```

Before `7`:

```text
f(6)=9
```

So:

```text
need = 10-9 = 1
```

The first `1` in `"111"` has offset:

```text
0
```

Digits before 7:

```text
digitsPrefix(6)=15
```

Hence:

```text
global index
=
15+0
=
15
```

So the 10-th `1` is at:

```text
0-based index 15
```

This matches the lecture's configuration example.

---

# 18. Complete C++17 — K-th `1` Position

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll countOnesPrefix(ll x) {
    if (x < 0) {
        return 0;
    }

    ll total = x + 1;
    ll answer = 0;

    for (int bit = 0; bit < 62; bit++) {
        ll halfCycle = 1LL << bit;

        if (halfCycle > total) {
            break;
        }

        ll fullCycle = halfCycle << 1;
        ll full = total / fullCycle;
        ll left = total % fullCycle;

        answer += full * halfCycle;
        answer += max(0LL, left - halfCycle);
    }

    return answer;
}

ll binaryDigitsPrefix(ll x) {
    if (x < 0) {
        return 0;
    }

    if (x == 0) {
        return 1;
    }

    ll answer = 1;
    ll start = 1;
    ll length = 1;

    while (start <= x) {
        ll end;

        if (start > x / 2) {
            end = x;
        } else {
            end = min(x, start * 2 - 1);
        }

        answer += (end - start + 1) * length;

        if (end == x) {
            break;
        }

        start *= 2;
        length++;
    }

    return answer;
}

ll findContainingNumber(ll k) {
    ll low = 1;
    ll high = k;
    ll answer = k;

    while (low <= high) {
        ll mid = low + (high - low) / 2;

        if (countOnesPrefix(mid) >= k) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }

    return answer;
}

ll kthOneIndex(ll k) {
    ll num = findContainingNumber(k);
    ll need = k - countOnesPrefix(num - 1);

    int highestBit = 63 - __builtin_clzll(num);
    ll offset = 0;

    for (int bit = highestBit; bit >= 0; bit--) {
        if ((num >> bit) & 1LL) {
            need--;

            if (need == 0) {
                break;
            }
        }

        offset++;
    }

    return binaryDigitsPrefix(num - 1) + offset;
}

int main() {
    ll k;
    cin >> k;

    cout << kthOneIndex(k) << '\n';

    return 0;
}
```

For:

```text
K = 10
```

output:

```text
15
```

using 0-based indexing of the infinite string.

---

# 19. Problem 2 — LeetCode 3007

## Problem Link

**[LeetCode 3007 — Maximum Number That Sum of the Prices Is Less Than or Equal to K](https://leetcode.com/problems/maximum-number-that-sum-of-the-prices-is-less-than-or-equal-to-k/)**

---

## What It Asks

You are given:

```text
k
x
```

For every positive integer:

```text
num
```

define its **price** as the number of set bits at positions:

```text
x, 2x, 3x, ...
```

where bit positions are counted:

```text
1-based from the least-significant bit
```

Then define:

```text
accumulatedPrice(num)
=
price(1)
+
price(2)
+
...
+
price(num)
```

A number is **cheap** when:

```text
accumulatedPrice(num) <= k
```

We need:

```text
the greatest cheap number
```

---

## Example Price Table

| `x` | `num` | Binary Representation | Price |
|---:|---:|:---:|---:|
| 1 | 13 | `000001101` | 3 |
| 2 | 13 | `000001101` | 1 |
| 2 | 233 | `011101001` | 3 |
| 3 | 13 | `000001101` | 1 |
| 3 | 362 | `101101010` | 2 |

---

# 20. Understanding the Price Function

The most important detail is the position convention.

The problem says:

```text
positions x, 2x, 3x, ...
```

using:

```text
1-based indexing from the LSB
```

Example:

```text
num = 13
binary = 1101
```

Write 1-based bit positions:

```text
position: 4 3 2 1
bit:      1 1 0 1
```

If:

```text
x = 1
```

valuable positions are:

```text
1,2,3,4,...
```

Set valuable positions in `1101`:

```text
1,3,4
```

So:

```text
price(13) = 3
```

If:

```text
x = 2
```

valuable positions are:

```text
2,4,6,...
```

For `13 = 1101`:

```text
position 2 → 0
position 4 → 1
```

So:

```text
price(13) = 1
```

---

## 1-Based Problem Position vs 0-Based C++ Bit

Problem position:

```text
1,2,3,4,...
```

C++ bit index:

```text
0,1,2,3,...
```

So:

```text
problem position p
↔
C++ bit index p-1
```

Therefore valuable C++ bit indexes are:

```text
x-1,
2x-1,
3x-1,
...
```

This conversion is one of the easiest places to make an off-by-one error.

---

# 21. Prerequisite — Cyclic Count for One Column

Before solving the whole problem, solve this smaller question:

> How many `1`s appear at one fixed bit position among all numbers from `0` to `num`?

Take one-based bit position:

```text
i
```

Its C++ zero-based index is:

```text
i-1
```

The pattern is:

```text
2^(i-1) zeros
then
2^(i-1) ones
```

So the complete cycle length is:

```text
2^i
```

ASCII:

```text
bit position i

0 0 0 ... 0 | 1 1 1 ... 1 | 0 0 ...
<--2^(i-1)--> <--2^(i-1)-->

whole cycle = 2^i
```

Example for position `3`:

```text
0 0 0 0 | 1 1 1 1 | 0 0 0 0 | ...
```

because:

```text
2^(3-1) = 4
```

---

# 22. Deriving `calc(num, x)` Step by Step

We want:

```text
accumulatedPrice(num)
```

Instead of looping over every number:

```text
1 ... num
```

we reverse the viewpoint:

```text
for each valuable bit position,
count how many numbers up to num
have that bit set
```

This is **bit contribution**.

---

## Step 1 — Include Row 0 in the Counting Formula

There are:

```text
num + 1
```

numbers in:

```text
0 ... num
```

So define:

```text
total = num + 1
```

`0` contributes no set bits, so including it does not change the accumulated price.

---

## Step 2 — Choose a Valuable Position

Only positions satisfying:

```text
i % x == 0
```

are valuable.

Example:

```text
x = 3
```

valuable positions:

```text
3,6,9,12,...
```

---

## Step 3 — Count Full Cycles

At 1-based position `i`:

```text
cycle length = 2^i
ones per full cycle = 2^(i-1)
```

Number of complete cycles:

```text
fullCycles
=
total / 2^i
```

Contribution from complete cycles:

```text
fullCycles * 2^(i-1)
```

---

## Step 4 — Count the Remaining Partial Cycle

Remaining rows:

```text
left
=
total % 2^i
```

The first:

```text
2^(i-1)
```

rows of a cycle have this bit:

```text
0
```

Only rows after that contribute `1`.

Therefore:

```text
extra
=
max(
    left - 2^(i-1),
    0
)
```

---

## Step 5 — Final Formula for One Valuable Position

```text
onesAtPosition(i)

=
(total / 2^i) * 2^(i-1)

+

max(
    total % 2^i - 2^(i-1),
    0
)
```

Then:

```text
accumulatedPrice(num)
=
sum of onesAtPosition(i)
for all i where i%x==0
```

This is exactly the cyclic-property formula used in the supplied solution.

---

# 23. Detailed Dry Run — `num = 9`, `x = 2`

Valuable positions:

```text
2,4,6,...
```

For numbers up to `9`, only positions:

```text
2 and 4
```

matter.

There are:

```text
total = 9+1 = 10
```

rows from `0...9`.

---

## Position 2

Pattern:

```text
00 11 00 11 00 ...
```

Here:

```text
cycle = 2^2 = 4
ones per cycle = 2^(2-1) = 2
```

Complete cycles:

```text
10 / 4 = 2
```

Contribution:

```text
2 * 2 = 4
```

Remaining:

```text
10 % 4 = 2
```

Extra ones:

```text
max(2 - 2, 0)
=
0
```

Position-2 contribution:

```text
4
```

---

## Position 4

Pattern:

```text
00000000 11111111 ...
```

Here:

```text
cycle = 2^4 = 16
ones per cycle = 2^3 = 8
```

Complete cycles:

```text
10 / 16 = 0
```

Remaining:

```text
10
```

Extra ones:

```text
max(10 - 8, 0)
=
2
```

Position-4 contribution:

```text
2
```

---

## Total Accumulated Price

```text
4 + 2
=
6
```

Therefore:

```text
accumulatedPrice(9, x=2)
=
6
```

Now check `10`.

```text
10 = 1010
```

At valuable positions:

```text
position 2 = 1
position 4 = 1
```

So:

```text
price(10)=2
```

Hence:

```text
accumulatedPrice(10)
=
6 + 2
=
8
```

So if:

```text
k = 7
x = 2
```

then:

```text
9  → cheap
10 → not cheap
```

Answer:

```text
9
```

---

# 24. Why Binary Search Works

Define:

```text
cost(num)
=
accumulatedPrice(num)
```

When `num` increases:

```text
cost(num+1)
=
cost(num)
+
price(num+1)
```

Since:

```text
price(num+1) >= 0
```

we have:

```text
cost(num+1) >= cost(num)
```

Therefore `cost(num)` is monotonic non-decreasing.

Cheapness:

```text
cost(num) <= k
```

looks like:

```text
TRUE TRUE TRUE TRUE FALSE FALSE FALSE ...
```

We need:

```text
the LAST TRUE
```

So Binary Search on Answer is valid.

---

## Binary Search Movement

If:

```text
calc(mid, x) <= k
```

then:

```text
mid is cheap
```

but maybe there is a larger cheap number.

So:

```text
search RIGHT
low = mid + 1
```

Otherwise:

```text
mid is too expensive
```

so:

```text
search LEFT
high = mid - 1
```

At the end:

```text
high
```

is the greatest cheap value.

---

## Compact Binary Search Template

```cpp
long long low = 1;
long long high = 1000000000000000LL;

while (low <= high) {
    long long mid = low + (high - low) / 2;

    if (calc(mid, x) <= k) {
        low = mid + 1;
    } else {
        high = mid - 1;
    }
}

return high;
```

This is a classic:

```text
LAST TRUE
```

template.

---

# 24.1 Source-Style `calc()` Implementation

The supplied solution uses 1-based column numbering directly.

That makes the formula line up very naturally with the lecture derivation:

```cpp
long long calc(long long n, int x) {
    int bitLength = 0;
    long long temp = n;

    while (temp > 0) {
        bitLength++;
        temp >>= 1;
    }

    long long total = n + 1;
    long long price = 0;

    for (int position = bitLength; position >= 1; position--) {
        if (position % x != 0) {
            continue;
        }

        long long cycle = 1LL << position;
        long long onesBlock = 1LL << (position - 1);

        long long fullCycles = total / cycle;
        long long left = total % cycle;

        price += fullCycles * onesBlock;
        price += max(0LL, left - onesBlock);
    }

    return price;
}
```

This corresponds directly to:

```text
(total / 2^i) * 2^(i-1)
+
max(total % 2^i - 2^(i-1), 0)
```

for every valuable 1-based position `i`.

---

# 24.2 Cleaner CP Version — Iterate Only Valuable Bits

We do not actually need to scan every column and test:

```text
position % x == 0
```

We can jump directly through:

```text
x, 2x, 3x, ...
```

Using zero-based C++ bit indexes:

```text
x-1,
2x-1,
3x-1,
...
```

```cpp
long long accumulatedPrice(long long num, int x) {
    long long total = num + 1;
    long long answer = 0;

    for (int bit = x - 1; bit < 62; bit += x) {
        long long halfCycle = 1LL << bit;
        long long fullCycle = halfCycle << 1;

        long long fullCycles = total / fullCycle;
        long long left = total % fullCycle;

        answer += fullCycles * halfCycle;
        answer += max(0LL, left - halfCycle);
    }

    return answer;
}
```

Both implementations use the same mathematics.

The second version is slightly cleaner for contest use because:

```text
we visit only valuable positions
```

---

# 24.3 Complete C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

class Solution {
public:
    long long accumulatedPrice(long long num, int x) {
        long long total = num + 1;
        long long answer = 0;

        for (int bit = x - 1; bit < 62; bit += x) {
            long long halfCycle = 1LL << bit;
            long long fullCycle = halfCycle << 1;

            long long fullCycles = total / fullCycle;
            long long left = total % fullCycle;

            answer += fullCycles * halfCycle;
            answer += max(0LL, left - halfCycle);
        }

        return answer;
    }

    long long findMaximumNumber(long long k, int x) {
        long long low = 1;
        long long high = 1000000000000000LL;

        while (low <= high) {
            long long mid = low + (high - low) / 2;

            if (accumulatedPrice(mid, x) <= k) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return high;
    }
};
```

---

# 24.4 Why `return high` Is Correct

At termination:

```text
low > high
```

The invariant is:

```text
everything <= high
has not been ruled out as too expensive

everything >= low
is beyond the last valid boundary
```

Because this is a:

```text
TRUE TRUE TRUE FALSE FALSE
```

search, the final boundary is:

```text
high = last TRUE
```

Therefore:

```cpp
return high;
```

returns the greatest cheap number.

---

# 24.5 Complexity of This Problem

Binary search range:

```text
1 ... 10^15
```

Number of iterations:

```text
O(log 10^15)
≈ 50
```

For one `calc()`:

```text
we inspect at most about 60/x valuable bit positions
```

So:

```text
calc
=
O(log num)
```

in the simple analysis.

Overall:

```text
O(log(range) * log(num))
```

which matches the supplied explanation.

More concretely:

```text
≈ 50 * at most 60 bit checks
```

which is very small.

---

# 24.6 Recognition Model

When you see:

```text
"sum a bit-based cost
for all numbers from 1 to N"
```

do not think:

```text
iterate every number
```

Think:

```text
FIX ONE BIT POSITION
        ↓
find its repeating cycle
        ↓
count its contribution in [0...N]
        ↓
sum required bit positions
```

If the resulting prefix cost is monotonic and the problem asks:

```text
largest N under budget K
```

then:

```text
Cyclic Bit Contribution
+
Binary Search on Answer
+
LAST TRUE
```

is the pattern.

---

# 25. Complexity

## Prefix Count of All `1`s

At most about:

```text
60 bit positions
```

So:

```text
countOnesPrefix(X)
=
O(log X)
```

with a tiny constant.

---

## K-th `1` Query

Binary search:

```text
O(log K)
```

checks.

Each check:

```text
O(log K)
```

bit positions.

Total:

```text
O(log² K)
```

plus one short scan of the final binary number.

---

## LeetCode 3007

Binary search over approximately:

```text
0 ... 10^15
```

takes about:

```text
50 checks
```

Each check visits only valuable positions:

```text
x-1, 2x-1, 3x-1, ...
```

At most about:

```text
62/x
```

positions.

So:

```text
O(log(10^15) * 62/x)
```

which is effectively very small.

---

# 26. First-True vs Last-True

These two applications are excellent examples of the two Binary Search on Answer forms.

| Application | `check(mid)` | Shape | Search |
|---|---|---|---|
| K-th `1` | `countOnesPrefix(mid) >= K` | `F F F T T T` | **First True** |
| LeetCode 3007 | `accumulatedPrice(mid) <= K` | `T T T F F F` | **Last True** |

Remember:

```text
FIRST TRUE:
valid → move LEFT

LAST TRUE:
valid → move RIGHT
```

---

# 27. Common Mistakes

## Mistake 1 — Use `X` Instead of `X+1`

Range:

```text
[0...X]
```

contains:

```text
X+1
```

numbers.

The cyclic formula is built around:

```text
total = X+1
```

---

## Mistake 2 — Forget the Half-Cycle of Zeros

At bit `i`:

```text
first 2^i positions  → 0
next  2^i positions  → 1
```

Therefore:

```text
extra
=
max(left - 2^i, 0)
```

not simply:

```text
left
```

---

## Mistake 3 — Count the Whole Integer Instead of One Bit

The cyclic formula works:

```text
one bit position at a time
```

Then contributions are added.

---

## Mistake 4 — Mix 1-Based and 0-Based Bit Positions

LeetCode 3007 says valuable positions:

```text
x, 2x, 3x...
```

using 1-based positions.

C++ bit indexes are:

```text
x-1,
2x-1,
3x-1...
```

---

## Mistake 5 — Use the Wrong Binary Search Direction

K-th `1`:

```text
first prefix with enough ones
→ FIRST TRUE
```

LeetCode 3007:

```text
largest prefix still within budget
→ LAST TRUE
```

---

## Mistake 6 — Generate the Infinite String

The lecture's point is to avoid:

```text
building the conceptual string
```

Use:

```text
prefix counting
```

instead.

---

## Mistake 7 — Forget Binary Digit Prefix When Exact String Index Is Asked

Finding the containing number is not yet the final position.

Need:

```text
digits before num
+
offset inside binary(num)
```

---

## Mistake 8 — Shift Too Far

Use:

```cpp
1LL << bit
```

and keep `bit` safely below the signed `long long` sign bit.

The implementations above use:

```text
bit < 62
```

for simple overflow safety.

---

# 28. Recognition Table

| Problem Signal | Think |
|---|---|
| Count set bits over huge range `0...N` | Cyclic property per bit |
| Need prefix number of `1`s | `full cycles + partial cycle` |
| Repeated `00..0011..11` pattern | Bit periodicity |
| Need K-th event in huge conceptual sequence | Prefix count + first-true BS |
| Need maximum `N` under cumulative bit cost | Prefix count + last-true BS |
| Selected bit positions only | Sum cyclic contribution only for those bits |
| Binary strings of consecutive integers | Group by powers of two |
| Need length of concatenated binary representations | Count digits by bit-length blocks |

---

# 29. Final Mental Model

The complete technique is:

```text
FIX ONE BIT
    ↓
its pattern is periodic
    ↓
0...0 | 1...1
 2^i     2^i
    ↓
cycle length = 2^(i+1)
```

Therefore for `[0...X]`:

```text
total = X+1

full cycles
=
total / 2^(i+1)

ones from full cycles
=
full * 2^i

remaining
=
total % 2^(i+1)

extra ones
=
max(remaining - 2^i, 0)
```

So:

```text
onesAtBit
=
full * 2^i
+
extra
```

Then:

```text
sum the required bit positions
```

and if the result is monotonic:

```text
BINARY SEARCH ON ANSWER
```

---

## 60-Second Revision Card

```text
BIT i:

0-block = 2^i
1-block = 2^i

cycle = 2^(i+1)

total = X+1

full = total / cycle
left = total % cycle

ones =
full * 2^i
+
max(left - 2^i, 0)
```

### K-th `1`

```text
f(X) = total ones in [0...X]

check(X):
f(X) >= K

→ FIRST TRUE
```

### Maximum value under bit-price budget

```text
cost(X) = selected bit contributions in [0...X]

check(X):
cost(X) <= K

→ LAST TRUE
```

The deepest recognition rule is:

```text
huge range
+
bit property
+
repeating powers-of-two pattern

→ count contribution per bit
→ never enumerate the numbers
```
