# Bit Contribution Technique — Pairwise XOR Sum
## TLE-Style Self-Study Notes

> **Source basis:** the supplied AlgoZenith/TLE lecture screenshots and the supplied pairwise XOR code/explanation.
>
> The lecture idea is **bit contribution**:
>
> ```text
> Instead of evaluating every pair,
> fix one bit position
> and count how many pairs make that bit contribute.
> ```
>
> The screenshots also connect the same thinking to pairwise `AND` / `OR` and the identity:
>
> ```text
> a + b = (a | b) + (a & b)
> ```

---

# Table of Contents

- [1. Problem](#1-problem)
- [2. Prerequisite 1 — Pair Convention](#2-prerequisite-1--pair-convention)
- [3. Prerequisite 2 — XOR Truth Table](#3-prerequisite-2--xor-truth-table)
- [4. Prerequisite 3 — Value of One Bit](#4-prerequisite-3--value-of-one-bit)
- [5. Brute Force](#5-brute-force)
- [6. The Contribution-Technique Shift](#6-the-contribution-technique-shift)
- [7. Deriving the XOR Contribution Formula](#7-deriving-the-xor-contribution-formula)
- [8. Detailed Dry Run — `arr = [1,3,5]`](#8-detailed-dry-run--arr--135)
- [9. Clean C++17 Solution](#9-clean-c17-solution)
- [10. Why the Algorithm Is Correct](#10-why-the-algorithm-is-correct)
- [11. Complexity](#11-complexity)
- [12. Ordered-Pair Variant](#12-ordered-pair-variant)
- [13. Extension — Pairwise AND](#13-extension--pairwise-and)
- [14. Extension — Pairwise OR](#14-extension--pairwise-or)
- [15. Property — `a+b = (a|b) + (a&b)`](#15-property--ab--ab--ab)
- [16. Deriving the Identity Bit by Bit](#16-deriving-the-identity-bit-by-bit)
- [17. Pair-Sum Connection](#17-pair-sum-connection)
- [18. Common Mistakes](#18-common-mistakes)
- [19. Recognition Table](#19-recognition-table)
- [20. Final Mental Model](#20-final-mental-model)

---

# 1. Problem

Given an array:

```text
a[0], a[1], ..., a[n-1]
```

find:

```text
Σ (a[i] XOR a[j])
```

over every pair:

```text
0 <= i < j < n
```

The lecture screenshots use the same pair structure:

```text
i
j = i+1 ... n-1
```

so each unordered pair is counted exactly once.

---

## Example

```text
arr = [1,3,5]
```

All pairs:

```text
1 XOR 3 = 2
1 XOR 5 = 4
3 XOR 5 = 6
```

Therefore:

```text
answer
=
2+4+6
=
12
```

---

# 2. Prerequisite 1 — Pair Convention

For:

```text
n
```

elements, the number of unordered pairs is:

```text
C(n,2)
=
n(n-1)/2
```

Example:

```text
n = 3
```

pairs:

```text
(0,1)
(0,2)
(1,2)
```

Count:

```text
3
```

This matters because the contribution formula:

```text
cnt0 * cnt1
```

counts each pair once.

---

# 3. Prerequisite 2 — XOR Truth Table

For one bit:

| `a` | `b` | `a XOR b` |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 0 |

The only important observation is:

```text
XOR bit = 1
exactly when
the two bits are DIFFERENT
```

So at any fixed bit position:

```text
0 with 1
or
1 with 0
```

creates contribution.

Pairs with:

```text
0 with 0
1 with 1
```

contribute nothing at that bit.

---

# 4. Prerequisite 3 — Value of One Bit

If bit `j` is set, its value is:

```text
2^j
```

In C++:

```cpp
1LL << j
```

Example:

```text
bit 0 → 1
bit 1 → 2
bit 2 → 4
bit 3 → 8
```

Therefore, if:

```text
P
```

pairs have XOR bit `j = 1`, their total contribution from this bit is:

```text
P * 2^j
```

This is the key bridge from:

```text
counting pairs
```

to:

```text
summing XOR values
```

---

# 5. Brute Force

The direct solution evaluates every pair.

```cpp
long long answer = 0;

for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        answer += arr[i] ^ arr[j];
    }
}
```

Number of pairs:

```text
n(n-1)/2
```

So:

```text
Time = O(N^2)
```

For large `N`, this is too slow.

---

# 6. The Contribution-Technique Shift

Instead of thinking:

```text
for every pair
    calculate the whole XOR
```

reverse the loops mentally:

```text
for every BIT
    count how many pairs
    make this bit equal to 1
```

ASCII view:

```text
                bit 2   bit 1   bit 0

1 = 001            0       0       1
3 = 011            0       1       1
5 = 101            1       0       1
                   ↑       ↑       ↑
                solve each column independently
```

This works because a number is just the sum of its bit contributions:

```text
value
=
bit0 * 2^0
+
bit1 * 2^1
+
bit2 * 2^2
+
...
```

Therefore:

```text
sum of XOR values
=
sum of contribution of every bit
```

---

# 7. Deriving the XOR Contribution Formula

Fix one bit:

```text
j
```

Count:

```text
cnt0 = number of array elements whose j-th bit is 0
cnt1 = number of array elements whose j-th bit is 1
```

For XOR to have bit `j = 1`, choose:

```text
one number from the 0-group
and
one number from the 1-group
```

Number of such unordered pairs:

```text
cnt0 * cnt1
```

Each contributes:

```text
2^j
```

Therefore:

```text
contribution(j)
=
cnt0 * cnt1 * 2^j
```

Finally:

```text
answer
=
Σ contribution(j)
```

So:

```text
answer
=
Σ [cnt0(j) * cnt1(j) * 2^j]
```

That is the complete optimization.

---

# 8. Detailed Dry Run — `arr = [1,3,5]`

Binary:

```text
1 = 001
3 = 011
5 = 101
```

Arrange by columns:

```text
          bit2 bit1 bit0
1 =        0    0    1
3 =        0    1    1
5 =        1    0    1
```

## Bit 0

```text
column = [1,1,1]
cnt1 = 3
cnt0 = 0
pairs = 3*0 = 0
contribution = 0 * 2^0 = 0
```

## Bit 1

```text
column = [0,1,0]
cnt1 = 1
cnt0 = 2
pairs = 1*2 = 2
contribution = 2 * 2^1 = 4
```

## Bit 2

```text
column = [0,0,1]
cnt1 = 1
cnt0 = 2
pairs = 1*2 = 2
contribution = 2 * 2^2 = 8
```

## Final Answer

```text
answer
=
0+4+8
=
12
```

Check directly:

```text
1 XOR 3 = 2
1 XOR 5 = 4
3 XOR 5 = 6

2+4+6 = 12
```

Correct.

---

# 9. Clean C++17 Solution

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll pairwiseXorSum(const vector<ll>& arr) {
    ll answer = 0;

    for (int bit = 0; bit < 61; bit++) {
        ll countZero = 0;
        ll countOne = 0;

        for (ll value : arr) {
            if ((value >> bit) & 1LL) {
                countOne++;
            } else {
                countZero++;
            }
        }

        ll differentPairs = countZero * countOne;
        answer += differentPairs * (1LL << bit);
    }

    return answer;
}

int main() {
    int n;
    cin >> n;

    vector<ll> arr(n);

    for (ll& value : arr) {
        cin >> value;
    }

    cout << pairwiseXorSum(arr) << '\n';

    return 0;
}
```

For ordinary non-negative 32-bit integers:

```text
31 bits
```

is enough.

Using:

```text
61 bits
```

makes the template suitable for common `long long` constraints.

---

# 10. Why the Algorithm Is Correct

Every XOR result can be decomposed by bit.

At bit `j`, the XOR is `1` exactly when the two elements have different bit values.

If:

```text
cnt0
```

elements have `0`, and:

```text
cnt1
```

elements have `1`, then exactly:

```text
cnt0 * cnt1
```

unordered pairs differ at that bit.

Each such pair contributes:

```text
2^j
```

Therefore the exact contribution of bit `j` is:

```text
cnt0 * cnt1 * 2^j
```

Summing over every bit counts every bit contribution of every pairwise XOR exactly once.

---

# 11. Complexity

Let:

```text
B = number of inspected bits
```

For every bit, scan all `N` values:

```text
Time = O(B*N)
```

For fixed-width integers:

```text
B = 31
```

or:

```text
B = 61
```

so with respect to `N` this is effectively:

```text
O(N)
```

Extra space:

```text
O(1)
```

excluding the input array.

---

# 12. Ordered-Pair Variant

The current formula is for:

```text
i < j
```

If a problem asks for ordered pairs:

```text
i != j
```

then both directions are counted:

```text
zero → one
one  → zero
```

So XOR contribution becomes:

```text
2 * cnt0 * cnt1 * 2^j
```

Always identify the pair convention before using the formula.

---

# 13. Extension — Pairwise AND

The same contribution technique works for:

```text
Σ (a[i] & a[j])
```

For one bit:

```text
1 & 1 = 1
```

and all other combinations produce `0`.

So if:

```text
cnt1
```

numbers have this bit set, the number of unordered pairs contributing is:

```text
C(cnt1,2)
=
cnt1(cnt1-1)/2
```

Therefore:

```text
AND contribution(j)
=
C(cnt1,2) * 2^j
```

## Example — `[1,3,5]`

```text
1 = 001
3 = 011
5 = 101
```

At bit 0:

```text
cnt1 = 3
C(3,2) = 3
contribution = 3 * 1 = 3
```

At bits 1 and 2:

```text
cnt1 = 1
```

so there is no pair of ones.

Therefore:

```text
pairwise AND sum = 3
```

Direct check:

```text
1&3 = 1
1&5 = 1
3&5 = 1

sum = 3
```

C++ contribution line:

```cpp
ll pairs = countOne * (countOne - 1) / 2;
answer += pairs * (1LL << bit);
```

---

# 14. Extension — Pairwise OR

For OR:

```text
0 | 0 = 0
```

and every other bit combination produces `1`.

Total unordered pairs:

```text
C(n,2)
```

Pairs producing `0`:

```text
C(cnt0,2)
```

Therefore:

```text
OR-one pairs
=
C(n,2) - C(cnt0,2)
```

and:

```text
OR contribution(j)
=
[C(n,2) - C(cnt0,2)] * 2^j
```

Equivalent form:

```text
cnt0*cnt1 + C(cnt1,2)
```

---

# 15. Property — `a+b = (a|b) + (a&b)`

The lecture next introduces:

```text
a+b
=
(a|b)+(a&b)
```

Example:

```text
a = 5 = 101
b = 3 = 011
```

Then:

```text
a|b = 111 = 7
a&b = 001 = 1
```

So:

```text
(a|b)+(a&b)
=
7+1
=
8
```

and:

```text
a+b
=
5+3
=
8
```

---

# 16. Deriving the Identity Bit by Bit

For one bit:

| `a` | `b` | Local sum | `a|b` | `a&b` | OR + AND |
|---:|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 | 0 |
| 0 | 1 | 1 | 1 | 0 | 1 |
| 1 | 0 | 1 | 1 | 0 | 1 |
| 1 | 1 | 2 | 1 | 1 | 2 |

When both bits are `1`:

```text
1+1 = 2
```

OR gives one copy of the current bit weight, and AND gives the second copy.

Therefore:

```text
a+b
=
(a|b)+(a&b)
```

Another standard identity is:

```text
a+b
=
(a^b)+2(a&b)
```

So remember:

```text
SUM = OR + AND

SUM = XOR + 2*AND
```

---

# 17. Pair-Sum Connection

For every pair:

```text
a[i]+a[j]
=
(a[i]|a[j])
+
(a[i]&a[j])
```

Therefore:

```text
Σ pair sums
=
Σ pairwise OR
+
Σ pairwise AND
```

For:

```text
arr = [1,3,5]
```

Pair sums:

```text
1+3 = 4
1+5 = 6
3+5 = 8

total = 18
```

Pairwise OR:

```text
1|3 = 3
1|5 = 5
3|5 = 7

OR sum = 15
```

Pairwise AND:

```text
1&3 = 1
1&5 = 1
3&5 = 1

AND sum = 3
```

Check:

```text
15+3
=
18
```

So the identity also holds after summing over every pair.

---

# 18. Common Mistakes

### Counting equal bits for XOR

XOR contributes when bits are different:

```text
cnt0 * cnt1
```

`C(cnt1,2)` belongs to pairwise AND.

### Forgetting the bit weight

The number of contributing pairs is not yet the numeric contribution.

Always multiply by:

```text
2^j
```

### Using `int` for the answer

Pair counts and their weighted contributions can be large.

Prefer:

```cpp
long long
```

and:

```cpp
1LL << bit
```

### Mixing ordered and unordered pairs

For:

```text
i < j
```

use:

```text
cnt0*cnt1
```

For ordered `i != j` XOR pairs:

```text
2*cnt0*cnt1
```

### Enumerating pairs before trying contribution

If the expression is bitwise and the brute force is `O(N^2)`, first ask:

```text
Can I fix one bit and count?
```

---

# 19. Recognition Table

| Problem | Pairs producing bit `1` |
|---|---|
| Pairwise XOR sum | `cnt0 * cnt1` |
| Pairwise AND sum | `C(cnt1,2)` |
| Pairwise OR sum | `C(n,2) - C(cnt0,2)` |
| Ordered XOR pairs | `2 * cnt0 * cnt1` |
| Pairwise Hamming distance | same differing-bit count as XOR |

Useful identities:

```text
a+b = (a|b)+(a&b)

a+b = (a^b)+2(a&b)
```

---

# 20. Final Mental Model

```text
O(N^2) PAIRWISE BIT EXPRESSION
             ↓
         FIX BIT j
             ↓
      COUNT 0s AND 1s
             ↓
 ask: when does operation produce 1?
             ↓
       COUNT THOSE PAIRS
             ↓
        MULTIPLY BY 2^j
             ↓
        SUM OVER ALL BITS
```

For XOR:

```text
different bits
→ cnt0 * cnt1
```

For AND:

```text
both are 1
→ C(cnt1,2)
```

For OR:

```text
anything except 0,0
→ C(n,2)-C(cnt0,2)
```

This is the reusable **bit contribution** pattern:

```text
change the viewpoint from PAIRS to BITS
```

and reduce:

```text
O(N^2)
```

to:

```text
O(B*N)
```

where `B` is only about `31` or `61`.
