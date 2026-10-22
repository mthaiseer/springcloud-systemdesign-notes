# Bit Contribution + Greedy Construction
## Maximize Sum of Squares using OR / AND Operations — TLE-Style Notes

# Table of Contents
- [1. Problem](#1-problem)
- [2. Prerequisite — OR / AND at One Bit](#2-prerequisite--or--and-at-one-bit)
- [3. Invariant — Count of Ones per Bit Never Changes](#3-invariant--count-of-ones-per-bit-never-changes)
- [4. Useful Identity — Sum Is Preserved](#4-useful-identity--sum-is-preserved)
- [5. Why Squares Prefer Concentration](#5-why-squares-prefer-concentration)
- [6. Core Greedy Idea](#6-core-greedy-idea)
- [7. Step-by-Step Example](#7-step-by-step-example)
- [8. Why the Greedy Is Optimal](#8-why-the-greedy-is-optimal)
- [9. Clean C++17](#9-clean-c17)
- [10. Relation to the Lecture Simulation](#10-relation-to-the-lecture-simulation)
- [11. Complexity](#11-complexity)
- [12. Common Mistakes](#12-common-mistakes)
- [13. Recognition Model](#13-recognition-model)

---

# 1. Problem

Given an array of `N` positive integers, we may repeatedly choose two elements:

```text
a = A[x]
b = A[y]
```

and replace them by:

```text
A[x] = a | b
A[y] = a & b
```

We want to maximize:

```text
Σ A[i]^2
```

The operation looks complicated at the number level, so the TLE-style approach is:

```text
Do not study the whole number first.
Study ONE BIT first.
```

---

# 2. Prerequisite — OR / AND at One Bit

For one bit, the operation is:

```text
(aBit, bBit)
      ↓
(aBit | bBit, aBit & bBit)
```

Truth table:

| Before | After `(OR,AND)` | Ones Before | Ones After |
|---|---|---:|---:|
| `0,0` | `0,0` | 0 | 0 |
| `0,1` | `1,0` | 1 | 1 |
| `1,0` | `1,0` | 1 | 1 |
| `1,1` | `1,1` | 2 | 2 |

The important observation is immediate:

```text
At every bit position,
the number of 1s is preserved.
```

This is the main invariant of the problem.

---

# 3. Invariant — Count of Ones per Bit Never Changes

For each bit `j`, define:

```text
count[j]
=
number of array elements whose j-th bit is 1
```

No valid operation changes `count[j]`.

Example:

```text
A = [1,3,5,6]
```

Binary:

```text
1 = 001
3 = 011
5 = 101
6 = 110
```

Column view:

```text
          bit2 bit1 bit0
1           0    0    1
3           0    1    1
5           1    0    1
6           1    1    0
```

So:

```text
bit0 → 3 ones
bit1 → 2 ones
bit2 → 2 ones
```

Therefore every reachable final array must still contain:

```text
count = [3,2,2]
```

The 1s may move between numbers, but their count in each column cannot change.

This lets us stop thinking about:

```text
which original index owns which bit?
```

and instead think only about:

```text
How should the available bit copies be redistributed?
```

---

# 4. Useful Identity — Sum Is Preserved

Another useful property is:

```text
a + b = (a | b) + (a & b)
```

Example:

```text
a = 5 = 101
b = 3 = 011

a|b = 111 = 7
a&b = 001 = 1

7 + 1 = 8
5 + 3 = 8
```

So every operation preserves:

```text
Σ A[i]
```

But the objective is not the ordinary sum.

We want:

```text
Σ A[i]^2
```

That changes when the same total value is distributed differently.

---

# 5. Why Squares Prefer Concentration

Suppose:

```text
small <= large
```

and we move some positive value `d` from `small` to `large`.

Before:

```text
small^2 + large^2
```

After:

```text
(small-d)^2 + (large+d)^2
```

Difference:

```text
= 2*d*(large-small) + 2*d^2
```

Since:

```text
d > 0
large >= small
```

the difference is positive.

So:

```text
with the same total sum,
more concentration gives a larger sum of squares.
```

Tiny example:

```text
2^2 + 2^2 = 8
1^2 + 3^2 = 10
0^2 + 4^2 = 16
```

All have sum `4`, but the most concentrated distribution has the largest square sum.

That tells us the greedy direction:

```text
pack available bits together
instead of spreading them out.
```

---

# 6. Core Greedy Idea

We know:

```text
count[bit]
```

is fixed.

To maximize squares, construct the final numbers as large as possible.

For one final number:

```text
current = 0
```

For every bit:

```text
if count[bit] > 0:
    put this bit into current
    count[bit]--
```

So the first constructed number receives:

```text
one copy of EVERY available bit
```

The second receives:

```text
one copy of every bit that still has a second copy
```

and so on.

Generic pattern:

```text
repeat N times:

    current = 0

    for every bit:
        if count[bit] > 0:
            current |= 2^bit
            count[bit]--

    answer += current^2
```

This directly constructs the most concentrated arrangement allowed by the invariant.

---

# 7. Step-by-Step Example

Use:

```text
A = [1,3,5,6]
```

We already found:

```text
count[0] = 3
count[1] = 2
count[2] = 2
```

Initial state:

```text
count = [3,2,2]
```

## Construct Number 1

All three bits are available:

```text
bit2 → take
bit1 → take
bit0 → take
```

So:

```text
current = 111₂ = 7
```

Remaining:

```text
count = [2,1,1]
```

Contribution:

```text
7^2 = 49
```

## Construct Number 2

All three bits are still available:

```text
current = 111₂ = 7
```

Remaining:

```text
count = [1,0,0]
```

Contribution:

```text
7^2 = 49
```

Running answer:

```text
98
```

## Construct Number 3

Only bit0 remains:

```text
current = 001₂ = 1
```

Remaining:

```text
count = [0,0,0]
```

Contribution:

```text
1^2 = 1
```

Running answer:

```text
99
```

## Construct Number 4

No bits remain:

```text
current = 0
```

Final constructed multiset:

```text
[7,7,1,0]
```

Check the bit counts:

```text
7 = 111
7 = 111
1 = 001
0 = 000
```

So:

```text
bit0 → 3
bit1 → 2
bit2 → 2
```

Exactly the same invariant as the original array.

Final answer:

```text
7^2 + 7^2 + 1^2 + 0^2
=
49 + 49 + 1
=
99
```

---

# 8. Why the Greedy Is Optimal

There are two useful ways to understand the proof.

## View 1 — Convexity

The square function rewards concentration.

Therefore, among all distributions that preserve the available bit inventory, we should make some numbers as large as possible instead of distributing the bits evenly.

Our construction does exactly that:

```text
final[0] gets every available bit
final[1] gets every bit with at least 2 copies
final[2] gets every bit with at least 3 copies
...
```

For any bit with:

```text
count[bit] = c
```

that bit appears in:

```text
final[0], final[1], ..., final[c-1]
```

So every bit occupies a prefix of the final array.

This aligns set bits together.

---

## View 2 — Positive Cross Terms

If one number contains two bit values:

```text
2^p
and
2^q
```

then squaring the number creates:

```text
(2^p + 2^q)^2
```

which contains the positive cross term:

```text
2 * 2^p * 2^q
```

If those two bits are placed in different numbers, that positive cross term disappears.

Therefore:

```text
placing available set bits together
can only help the square sum.
```

So the optimal arrangement maximizes overlap between the available 1-bits.

The prefix-style construction does exactly that.

---

# 9. Clean C++17

The lecture code explicitly tracks indices and simulates OR/AND transfers.

For contests, the invariant gives a much cleaner implementation:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int n;
    cin >> n;

    vector<ll> arr(n);

    for (ll& value : arr) {
        cin >> value;
    }

    const int MAX_BITS = 20;
    vector<int> bitCount(MAX_BITS, 0);

    for (ll value : arr) {
        for (int bit = 0; bit < MAX_BITS; bit++) {
            if ((value >> bit) & 1LL) {
                bitCount[bit]++;
            }
        }
    }

    ll answer = 0;

    for (int i = 0; i < n; i++) {
        ll current = 0;

        for (int bit = 0; bit < MAX_BITS; bit++) {
            if (bitCount[bit] > 0) {
                current |= (1LL << bit);
                bitCount[bit]--;
            }
        }

        answer += current * current;
    }

    cout << answer << '\n';

    return 0;
}
```

If the problem allows larger values, increase:

```cpp
const int MAX_BITS = 20;
```

according to the constraints.

For common non-negative `long long` constraints:

```cpp
const int MAX_BITS = 61;
```

is typical.

---

# 10. Relation to the Lecture Simulation

The supplied lecture implementation keeps:

```cpp
vector<ll> bit[20];
```

and stores indices of numbers having each bit.

Then it greedily selects a number and tries to enrich it using lower set bits from other numbers, explicitly applying:

```text
OR
AND
```

That version is useful for understanding:

```text
how the operation physically moves bits
```

But for solving the problem, we can use the stronger observation:

```text
the exact indices are unnecessary

only the number of available copies
of each bit matters
```

So the shorter template is:

```text
count bits
→ rebuild concentrated numbers
→ sum squares
```

Both approaches have the same greedy goal:

```text
concentrate bits into fewer, larger values.
```

---

# 11. Complexity

Let:

```text
B = number of relevant bits
```

Counting the bits:

```text
O(N * B)
```

Reconstructing the final numbers:

```text
O(N * B)
```

Total:

```text
O(N * B)
```

With `B = 20`, `31`, or `61` treated as a fixed machine-width constant:

```text
O(N)
```

with respect to `N`.

Extra space:

```text
O(B)
```

apart from the input array.

---

# 12. Common Mistakes

## 1. Missing the invariant

Do not try to follow every bit movement first.

Start with:

```text
At each bit,
#ones before = #ones after
```

That removes most of the difficulty.

## 2. Spreading bits evenly

The objective is:

```text
Σ A[i]^2
```

not:

```text
Σ A[i]
```

Squares prefer concentration.

## 3. Taking only the highest bit

When building `current`, take:

```text
every bit whose count is still positive
```

not just the highest one.

## 4. Preserving original ownership

The original index that held a bit is not important for the optimized construction.

Think:

```text
bit inventory
```

rather than:

```text
bit owner
```

## 5. Using `int` for squares

Use:

```cpp
long long
```

for:

```text
values
current
answer
```

and prefer:

```cpp
1LL << bit
```

---

# 13. Recognition Model

When a problem says:

```text
choose a,b
replace them by

a|b
a&b
```

immediately inspect one bit.

You should recognize:

```text
OR/AND transformation
        ↓
count of ones per bit is invariant
        ↓
objective decides how bits should be redistributed
```

For this problem:

```text
objective = sum of squares
```

and squares are convex, so:

```text
pack bits together
```

Final reusable pattern:

```text
OR/AND OPERATION
      ↓
PER-BIT COUNT INVARIANT
      ↓
CONVEX OBJECTIVE
      ↓
CONCENTRATE 1-BITS
      ↓
GREEDY RECONSTRUCTION
```

For the example:

```text
[1,3,5,6]
```

remember:

```text
bit counts = [3,2,2]
        ↓
construct
        ↓
[7,7,1,0]
        ↓
answer = 99
```

That is the core idea to carry into future problems.
