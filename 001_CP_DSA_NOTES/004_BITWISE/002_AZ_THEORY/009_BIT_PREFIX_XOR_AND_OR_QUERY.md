# Bit Manipulation — XOR, OR and AND Range Query
## Per-Bit Contribution + Prefix Counts — TLE-Style Self-Study Notes

---

# Table of Contents

- [1. Problem](#1-problem)
- [2. What Makes This Problem Difficult?](#2-what-makes-this-problem-difficult)
- [3. Prerequisite 1 — Think Bit by Bit](#3-prerequisite-1--think-bit-by-bit)
- [4. Prerequisite 2 — Contribution of One Bit](#4-prerequisite-2--contribution-of-one-bit)
- [5. Prerequisite 3 — Prefix Count of Set Bits](#5-prerequisite-3--prefix-count-of-set-bits)
- [6. Query 1 — Smallest X1 Maximizing XOR Sum](#6-query-1--smallest-x1-maximizing-xor-sum)
- [7. Query 2 — Smallest X2 Maximizing OR Sum](#7-query-2--smallest-x2-maximizing-or-sum)
- [8. Query 3 — Smallest X3 Maximizing AND Sum](#8-query-3--smallest-x3-maximizing-and-sum)
- [9. Master Decision Table](#9-master-decision-table)
- [10. Full Step-by-Step Dry Run](#10-full-step-by-step-dry-run)
- [11. Why Every Bit Can Be Solved Independently](#11-why-every-bit-can-be-solved-independently)
- [12. Clean C++17 Solution](#12-clean-c17-solution)
- [13. Complexity](#13-complexity)
- [14. Common Mistakes](#14-common-mistakes)
- [15. Pattern Recognition](#15-pattern-recognition)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Problem

We are given an array:

```text
A[1 ... N]
```

and many range queries:

```text
[L, R]
```

For every query, we must find three integers:

```text
X1, X2, X3
```

such that:

### XOR

`X1` is the **smallest** integer for which

```text
(A[L] XOR X1)
+
(A[L+1] XOR X1)
+
...
+
(A[R] XOR X1)
```

is maximum possible.

### OR

`X2` is the **smallest** integer for which

```text
(A[L] OR X2)
+
(A[L+1] OR X2)
+
...
+
(A[R] OR X2)
```

is maximum possible.

### AND

`X3` is the **smallest** integer for which

```text
(A[L] AND X3)
+
(A[L+1] AND X3)
+
...
+
(A[R] AND X3)
```

is maximum possible.

The required output for the query is:

```text
X1 + X2 + X3
```

The lecture works with 31 relevant bits, so we process:

```text
bit 0 ... bit 30
```

---

# 2. What Makes This Problem Difficult?

A brute-force idea would be:

```text
try many possible X
compute the whole range sum
take the best X
```

That is impossible because `X` can have up to 31 bits.

There are roughly:

```text
2^31
```

possible values.

The key observation is:

> XOR, OR and AND operate independently at every bit position.

Therefore we do **not** construct `X` as one huge integer decision.

Instead:

```text
solve bit 0
solve bit 1
solve bit 2
...
solve bit 30
```

and combine those decisions.

---

# 3. Prerequisite 1 — Think Bit by Bit

Suppose:

```text
a = 5 = 101₂
x = 3 = 011₂
```

Then:

```text
a XOR x

  101
^ 011
-----
  110
```

Each output bit depends only on the corresponding input bits.

The same is true for OR and AND.

So for bit `j`, we only need to know:

```text
How many numbers in [L,R] have bit j = 0?
How many numbers in [L,R] have bit j = 1?
```

Let:

```text
ones  = number of values with bit j = 1
zeros = number of values with bit j = 0
```

and:

```text
len = R - L + 1
```

Then:

```text
zeros = len - ones
```

This pair:

```text
(zeros, ones)
```

contains everything needed to decide bit `j` of `X1`, `X2`, and `X3`.

---

# 4. Prerequisite 2 — Contribution of One Bit

Bit `j` has numerical value:

```text
2^j
```

If an operation produces `1` at bit `j` for `c` numbers, then this bit contributes:

```text
c * 2^j
```

to the total sum.

This is the contribution technique.

---

## Tiny Example

Suppose at bit `2`:

```text
3 result numbers have bit 2 = 1
```

Since:

```text
2^2 = 4
```

the contribution of this bit is:

```text
3 * 4 = 12
```

We do this independently for every bit.

---

# 5. Prerequisite 3 — Prefix Count of Set Bits

We have many range queries.

Scanning every element in `[L,R]` for every bit would be too slow.

So we preprocess:

```text
prefix[bit][i]
=
number of values among A[1 ... i]
whose bit is 1
```

Then for a query `[L,R]`:

```text
ones
=
prefix[bit][R]
-
prefix[bit][L-1]
```

and:

```text
len = R - L + 1
zeros = len - ones
```

---

## Tiny Prefix Example

Suppose:

```text
A = [1, 3, 4, 7]
```

Look only at bit `0`.

Binary:

```text
1 = 001  → bit0 = 1
3 = 011  → bit0 = 1
4 = 100  → bit0 = 0
7 = 111  → bit0 = 1
```

Prefix count:

```text
index       0  1  2  3  4
prefix[0]   0  1  2  2  3
```

For query:

```text
L = 2
R = 4
```

we get:

```text
ones
=
prefix[0][4] - prefix[0][1]
=
3 - 1
=
2
```

Range length:

```text
3
```

Therefore:

```text
zeros = 3 - 2 = 1
```

So bit `0` in range `[2,4]` contains:

```text
two 1s
one 0
```

Now we can decide the best bit of `X`.

---

# 6. Query 1 — Smallest X1 Maximizing XOR Sum

We want to maximize:

```text
Σ (A[i] XOR X1)
```

Consider only one bit.

XOR truth table:

| `A bit` | `X bit` | Result |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 0 |

---

## Case 1 — Choose `X bit = 0`

Then XOR does not flip the bit.

So the number of resulting `1`s is:

```text
ones
```

Contribution:

```text
ones * 2^bit
```

---

## Case 2 — Choose `X bit = 1`

Now XOR flips every bit.

Original zeros become ones.

Original ones become zeros.

Therefore the number of resulting ones becomes:

```text
zeros
```

Contribution:

```text
zeros * 2^bit
```

---

## Which Choice Is Better?

Compare:

```text
ones
vs
zeros
```

If:

```text
zeros > ones
```

choose:

```text
X1 bit = 1
```

because flipping creates more ones.

If:

```text
ones > zeros
```

choose:

```text
X1 bit = 0
```

because keeping the bits creates more ones.

---

## What If They Are Equal?

Suppose:

```text
ones = zeros
```

Both choices give the same maximum sum.

But the problem asks for the:

```text
smallest X1
```

Therefore choose:

```text
X1 bit = 0
```

So the final XOR rule is:

```text
if zeros > ones:
    X1 bit = 1
else:
    X1 bit = 0
```

---

## Tiny XOR Example

At some bit:

```text
ones  = 2
zeros = 5
```

If `X bit = 0`:

```text
2 result ones
```

If `X bit = 1`:

```text
5 result ones
```

So:

```text
X bit = 1
```

---

# 7. Query 2 — Smallest X2 Maximizing OR Sum

We want to maximize:

```text
Σ (A[i] OR X2)
```

OR truth table:

| `A bit` | `X bit` | Result |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 1 |
| 1 | 0 | 1 |
| 1 | 1 | 1 |

---

## Case 1 — Choose `X bit = 0`

The result keeps the original bits.

Number of resulting ones:

```text
ones
```

---

## Case 2 — Choose `X bit = 1`

Every result gets `1` at this bit.

Number of resulting ones:

```text
len
```

Therefore setting the bit to `1` is useful whenever at least one value currently has `0`.

That condition is:

```text
zeros > 0
```

---

## What If Every Number Already Has 1?

If:

```text
zeros = 0
```

then every element already has this bit set.

Choosing:

```text
X bit = 0
```

or:

```text
X bit = 1
```

produces exactly the same OR sum.

Since we need the smallest `X2`, choose:

```text
X2 bit = 0
```

So:

```text
if zeros > 0:
    X2 bit = 1
else:
    X2 bit = 0
```

---

## Tiny OR Example

Suppose the bits in the range are:

```text
1 1 0 1
```

There is one zero.

With:

```text
X bit = 0
```

result:

```text
1 1 0 1
```

With:

```text
X bit = 1
```

result:

```text
1 1 1 1
```

The second choice gives a larger sum.

So:

```text
X2 bit = 1
```

---

# 8. Query 3 — Smallest X3 Maximizing AND Sum

We want to maximize:

```text
Σ (A[i] AND X3)
```

AND truth table:

| `A bit` | `X bit` | Result |
|---:|---:|---:|
| 0 | 0 | 0 |
| 0 | 1 | 0 |
| 1 | 0 | 0 |
| 1 | 1 | 1 |

---

## Case 1 — Choose `X bit = 0`

Every result becomes `0` at this bit.

Contribution:

```text
0
```

---

## Case 2 — Choose `X bit = 1`

Original 1s survive.

Original 0s remain 0.

So the number of result ones is:

```text
ones
```

Contribution:

```text
ones * 2^bit
```

Therefore if even one array element contains `1`:

```text
ones > 0
```

we should set this bit in `X3`.

---

## What If No Number Has This Bit?

If:

```text
ones = 0
```

then:

```text
X bit = 0
```

and:

```text
X bit = 1
```

both produce zero contribution.

Because we need the smallest `X3`, choose:

```text
X3 bit = 0
```

Thus:

```text
if ones > 0:
    X3 bit = 1
else:
    X3 bit = 0
```

---

# 9. Master Decision Table

This is the most important table in the chapter.

For each bit in range `[L,R]`:

```text
ones  = number of 1s
zeros = len - ones
```

Then:

| Operation | Set bit in X when... | Why? |
|---|---|---|
| XOR | `zeros > ones` | XOR with 1 flips zeros into ones |
| OR | `zeros > 0` | OR with 1 repairs every zero |
| AND | `ones > 0` | AND with 1 preserves existing ones |

Tie/minimum rules are already built into these conditions.

Therefore:

```cpp
if (zeros > ones) {
    x1 |= (1LL << bit);
}

if (zeros > 0) {
    x2 |= (1LL << bit);
}

if (ones > 0) {
    x3 |= (1LL << bit);
}
```

That is the entire problem after preprocessing.

---

# 10. Full Step-by-Step Dry Run

Consider this teaching example:

```text
A = [1, 2, 3, 6]

Query:
L = 2
R = 4
```

So the range is:

```text
[2, 3, 6]
```

Binary with 3 relevant bits:

```text
2 = 010
3 = 011
6 = 110
```

Arrange them column-wise:

```text
          bit2  bit1  bit0

2 = 010     0     1     0
3 = 011     0     1     1
6 = 110     1     1     0
```

Range length:

```text
len = 3
```

---

## Bit 2

Column:

```text
0
0
1
```

So:

```text
ones  = 1
zeros = 2
```

### XOR

```text
zeros > ones
2 > 1
```

Therefore:

```text
X1 bit2 = 1
```

### OR

```text
zeros > 0
```

Therefore:

```text
X2 bit2 = 1
```

### AND

```text
ones > 0
```

Therefore:

```text
X3 bit2 = 1
```

Current masks:

```text
X1 = 100
X2 = 100
X3 = 100
```

---

## Bit 1

Column:

```text
1
1
1
```

So:

```text
ones  = 3
zeros = 0
```

### XOR

```text
zeros > ones ?
0 > 3 → no
```

Therefore:

```text
X1 bit1 = 0
```

### OR

```text
zeros > 0 ?
0 > 0 → no
```

Every value already has this bit.

To get the smallest maximizing `X2`:

```text
X2 bit1 = 0
```

### AND

```text
ones > 0
```

Therefore:

```text
X3 bit1 = 1
```

Masks:

```text
X1 = 100
X2 = 100
X3 = 110
```

---

## Bit 0

Column:

```text
0
1
0
```

So:

```text
ones  = 1
zeros = 2
```

### XOR

```text
2 > 1
```

Set:

```text
X1 bit0 = 1
```

### OR

There are zeros:

```text
X2 bit0 = 1
```

### AND

There is at least one one:

```text
X3 bit0 = 1
```

Final:

```text
X1 = 101₂ = 5
X2 = 101₂ = 5
X3 = 111₂ = 7
```

Required query output:

```text
X1 + X2 + X3
=
5 + 5 + 7
=
17
```

---

## Verify XOR

```text
2 XOR 5 = 010 XOR 101 = 111 = 7
3 XOR 5 = 011 XOR 101 = 110 = 6
6 XOR 5 = 110 XOR 101 = 011 = 3
```

Sum:

```text
7 + 6 + 3 = 16
```

Per-bit contribution:

```text
bit2: 2 ones * 4 = 8
bit1: 3 ones * 2 = 6
bit0: 2 ones * 1 = 2

total = 8 + 6 + 2 = 16
```

---

## Verify OR

```text
2 OR 5 = 111 = 7
3 OR 5 = 111 = 7
6 OR 5 = 111 = 7
```

Sum:

```text
21
```

`X2 = 5` is the smallest mask that makes every missing range bit become 1.

---

## Verify AND

```text
2 AND 7 = 2
3 AND 7 = 3
6 AND 7 = 6
```

Sum:

```text
11
```

Choosing `X3 = 7` preserves every set bit that appears anywhere in the range.

---

# 11. Why Every Bit Can Be Solved Independently

Every integer can be written as:

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

Therefore a sum of integers can also be rewritten as:

```text
contribution of bit0
+
contribution of bit1
+
contribution of bit2
+
...
```

XOR, OR, and AND never move information from one bit position to another.

There is:

```text
no carry
```

between bits.

So deciding bit `j` of `X` changes only the `2^j` contribution.

It does not affect any other bit.

That is why each bit can be optimized independently.

---

# 12. Clean C++17 Solution

This follows the compact C++ style used in the other notes.

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const int BITS = 31;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int t;
    cin >> t;

    while (t--) {
        int n;
        cin >> n;

        vector<ll> arr(n + 1);

        for (int i = 1; i <= n; i++) {
            cin >> arr[i];
        }

        vector<vector<int>> prefix(BITS, vector<int>(n + 1, 0));

        for (int bit = 0; bit < BITS; bit++) {
            for (int i = 1; i <= n; i++) {
                prefix[bit][i] = prefix[bit][i - 1] + ((arr[i] >> bit) & 1LL);
            }
        }

        int q;
        cin >> q;

        while (q--) {
            int left, right;
            cin >> left >> right;

            int length = right - left + 1;

            ll x1 = 0;
            ll x2 = 0;
            ll x3 = 0;

            for (int bit = 0; bit < BITS; bit++) {
                int ones = prefix[bit][right] - prefix[bit][left - 1];
                int zeros = length - ones;

                if (zeros > ones) {
                    x1 |= (1LL << bit);
                }

                if (zeros > 0) {
                    x2 |= (1LL << bit);
                }

                if (ones > 0) {
                    x3 |= (1LL << bit);
                }
            }

            cout << x1 + x2 + x3 << '\n';
        }
    }

    return 0;
}
```

---

# 13. Complexity

Let:

```text
B = 31
```

### Preprocessing

For every bit, scan all `N` elements:

```text
O(B * N)
=
O(31N)
```

### Each Query

For every query, inspect 31 bits:

```text
O(B)
=
O(31)
```

So for `Q` queries:

```text
O(B * N + B * Q)
```

Since `B = 31` is constant:

```text
O(N + Q)
```

with a small constant factor.

Memory:

```text
O(B * N)
```

for the prefix-bit table.

---

# 14. Common Mistakes

## Mistake 1 — Solving the Whole Integer at Once

Do not try to directly reason about:

```text
X = some full integer
```

Start with:

```text
one bit
```

and derive the best choice there.

---

## Mistake 2 — Forgetting "Smallest X"

This affects ties.

For XOR:

```text
zeros == ones
```

both `X bit = 0` and `1` give the same maximum contribution.

Choose:

```text
0
```

because we need the smallest `X`.

For OR:

```text
zeros == 0
```

setting the bit gives no improvement.

Choose `0`.

For AND:

```text
ones == 0
```

setting the bit gives no improvement.

Choose `0`.

---

## Mistake 3 — XOR Majority Direction Reversed

For XOR:

```text
X bit = 1
```

flips:

```text
0 → 1
1 → 0
```

Therefore choose `1` when:

```text
zeros > ones
```

not when `ones > zeros`.

---

## Mistake 4 — Recomputing the Range Every Query

Without prefix counts, each query costs:

```text
O(31 * rangeLength)
```

With prefix counts:

```text
O(31)
```

---

## Mistake 5 — Using `1 << bit`

Prefer:

```cpp
1LL << bit
```

so the shift is done using `long long`.

---

## Mistake 6 — Using `int` for the Final Sum

Each `X` can require many bits.

Also the answer is:

```text
X1 + X2 + X3
```

Use:

```cpp
long long
```

---

# 15. Pattern Recognition

When a problem says:

```text
many range queries
+
bitwise XOR / OR / AND
+
maximize or minimize a sum
+
choose one common X for the whole range
```

think:

```text
BITWISE RANGE OPTIMIZATION
          ↓
split by bit position
          ↓
need count of zeros / ones in [L,R]
          ↓
prefix count for each bit
          ↓
O(bits) per query
```

For this exact problem:

```text
          ones, zeros
              |
      +-------+-------+
      |       |       |
     XOR      OR      AND
      |       |       |
zeros>ones  zeros>0  ones>0
      |       |       |
   set X1   set X2   set X3
```

---

# 16. Final Mental Model

Remember only this:

```text
For every bit in [L,R]:

ones  = prefix[bit][R] - prefix[bit][L-1]
zeros = length - ones
```

Then:

```text
XOR:
    set X bit if zeros > ones

OR:
    set X bit if zeros > 0

AND:
    set X bit if ones > 0
```

Why?

```text
XOR with 1 → swaps zero/one
OR  with 1 → forces one
AND with 1 → preserves one
```

And because the problem asks for the **smallest** maximizing integer:

```text
every tie chooses bit 0
```

Compact contest template:

```cpp
for (int bit = 0; bit < 31; bit++) {
    int ones = prefix[bit][right] - prefix[bit][left - 1];
    int zeros = length - ones;

    if (zeros > ones) {
        x1 |= (1LL << bit);
    }

    if (zeros > 0) {
        x2 |= (1LL << bit);
    }

    if (ones > 0) {
        x3 |= (1LL << bit);
    }
}
```

That is the complete reusable pattern:

```text
PER-BIT CONTRIBUTION
        +
RANGE PREFIX BIT COUNTS
        +
SMALLEST-X TIE HANDLING
```
