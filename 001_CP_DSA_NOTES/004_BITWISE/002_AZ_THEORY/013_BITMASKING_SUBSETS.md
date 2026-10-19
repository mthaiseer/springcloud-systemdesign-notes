# Bitmasking — Subset Representation and Enumeration
## TLE-Style Self-Study Notes

> Based on the supplied Bit Manipulation / Bitmasking notes.
>
> Core flow:
>
> `binary → one bit per item → mask → subset → enumerate masks → enumerate submasks`

---

# Table of Contents

- [1. Prerequisites](#1-prerequisites)
- [2. What Is a Bitmask?](#2-what-is-a-bitmask)
- [3. Mapping a Set to Bits](#3-mapping-a-set-to-bits)
- [4. Core Operations](#4-core-operations)
- [5. Why There Are 2^N Subsets](#5-why-there-are-2n-subsets)
- [6. Enumerating All Subsets](#6-enumerating-all-subsets)
- [7. Problem Form — Count Subsets with Sum ≥ X](#7-problem-form--count-subsets-with-sum--x)
- [8. Submasks](#8-submasks)
- [9. Why `(sub-1)&mask` Works](#9-why-sub-1mask-works)
- [10. All Mask–Submask Pairs and 3^N](#10-all-masksubmask-pairs-and-3n)
- [11. Range Masks](#11-range-masks)
- [12. Rightmost Set Bit](#12-rightmost-set-bit)
- [13. Practice Problems](#13-practice-problems)
- [14. Common Mistakes](#14-common-mistakes)
- [15. Recognition Table](#15-recognition-table)
- [16. Final Mental Model](#16-final-mental-model)

---

# 1. Prerequisites

Before bitmasking, be comfortable with:

```text
bit position i      → value 2^i
one-bit mask        → 1LL << i

SET bit i           → x | (1LL << i)
CLEAR bit i         → x & ~(1LL << i)
CHECK bit i         → (x & (1LL << i)) != 0
TOGGLE bit i        → x ^ (1LL << i)
COUNT set bits      → __builtin_popcountll(x)
```

Tiny example:

```text
x = 01010
```

Set bit 0:

```text
01010 | 00001
=
01011
```

Clear bit 1:

```text
01010 & 11101
=
01000
```

Check bit 3:

```text
01010 & 01000
=
01000 ≠ 0
```

So bit 3 is present.

---

# 2. What Is a Bitmask?

A **bitmask** is an integer whose binary bits represent a group of boolean choices.

For a set of `N` items:

```text
bit 0 ↔ item 0
bit 1 ↔ item 1
...
bit N-1 ↔ item N-1
```

Meaning:

```text
1 → item selected
0 → item not selected
```

Example:

```text
mask = 01010₂
```

Set bits are:

```text
1 and 3
```

So with 0-based indexing the represented subset is:

```text
{1,3}
```

If the problem labels items from `1`, the same mask represents labels:

```text
{2,4}
```

---

# 3. Mapping a Set to Bits

Suppose:

```text
S = {A,B,C,D}
```

Map:

```text
A → bit 0
B → bit 1
C → bit 2
D → bit 3
```

For:

```text
mask = 1101
```

we get:

```text
bit:       3   2   1   0
element:   D   C   B   A
mask:      1   1   0   1
```

Therefore:

```text
subset = {A,C,D}
```

This is the most important mental conversion:

```text
SUBSET
↔
BINARY NUMBER
```

---

# 4. Core Operations

Let `mask` represent the current subset.

## Add item `i`

```cpp
mask |= (1LL << i);
```

## Remove item `i`

```cpp
mask &= ~(1LL << i);
```

## Check item `i`

```cpp
bool present = (mask & (1LL << i)) != 0;
```

## Toggle item `i`

```cpp
mask ^= (1LL << i);
```

## Count selected items

```cpp
int selected = __builtin_popcountll(mask);
```

### Important C++ correction

For clearing a bit, use:

```cpp
~
```

not logical NOT:

```cpp
!
```

Correct:

```cpp
mask & ~(1LL << i)
```

---

# 5. Why There Are 2^N Subsets

Every element has exactly two choices:

```text
0 → not selected
1 → selected
```

For `N` elements:

```text
2 × 2 × ... × 2
=
2^N
```

Therefore all subsets correspond exactly to masks:

```text
0 ... 2^N - 1
```

Example for `N=3`:

| Mask | Binary | Subset of `{1,2,3}` |
|---:|:---:|---|
| 0 | `000` | `{}` |
| 1 | `001` | `{1}` |
| 2 | `010` | `{2}` |
| 3 | `011` | `{1,2}` |
| 4 | `100` | `{3}` |
| 5 | `101` | `{1,3}` |
| 6 | `110` | `{2,3}` |
| 7 | `111` | `{1,2,3}` |

---

# 6. Enumerating All Subsets

Master template:

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    for (int i = 0; i < n; i++) {
        if (mask & (1 << i)) {
            // item i belongs to this subset
        }
    }
}
```

Meaning:

```text
outer loop
→ choose one subset

inner loop
→ decode its selected elements
```

## C++17 — Print All Subsets

```cpp
#include <bits/stdc++.h>
using namespace std;

void printAllSubsets(const vector<int>& arr) {
    int n = arr.size();

    for (int mask = 0; mask < (1 << n); mask++) {
        cout << "{ ";

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                cout << arr[i] << ' ';
            }
        }

        cout << "}\n";
    }
}
```

Complexity:

```text
2^N masks
×
N inspected positions

=
O(N * 2^N)
```

This is why direct bitmask enumeration is common around:

```text
N <= 20
```

depending on the work per mask.

---

# 7. Problem Form — Count Subsets with Sum ≥ X

The source includes this basic application:

```text
Given N values,
count subsets whose sum >= X.
```

Example:

```text
arr = [2,5,7]
X = 7
```

Dry run:

```text
000 → {}        → 0  → no
001 → {2}       → 2  → no
010 → {5}       → 5  → no
011 → {2,5}     → 7  → YES
100 → {7}       → 7  → YES
101 → {2,7}     → 9  → YES
110 → {5,7}     → 12 → YES
111 → {2,5,7}   → 14 → YES
```

Answer:

```text
5
```

C++:

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll countSubsetsAtLeast(const vector<int>& arr, ll target) {
    int n = arr.size();
    ll answer = 0;

    for (int mask = 0; mask < (1 << n); mask++) {
        ll sum = 0;

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                sum += arr[i];
            }
        }

        if (sum >= target) {
            answer++;
        }
    }

    return answer;
}
```

---

# 8. Submasks

A **submask** of `mask` uses only bits already set in `mask`.

Example:

```text
mask = 10110
```

Valid submasks include:

```text
10110
10100
10010
10000
00110
00100
00010
00000
```

A useful test:

```cpp
(sub & mask) == sub
```

Classic enumeration:

```cpp
for (int sub = mask; sub; sub = (sub - 1) & mask) {
    // process non-zero submask
}
```

---

# 9. Why `(sub-1)&mask` Works

Take:

```text
mask = 10110
sub  = 10110
```

Subtract one:

```text
10110 - 1
=
10101
```

Some newly turned-on bits may not belong to the original mask.

Filter them:

```text
  10101
& 10110
-------
  10100
```

Next submask:

```text
10100
```

Again:

```text
10100 - 1
=
10011

10011 & 10110
=
10010
```

So the sequence is:

```text
10110
10100
10010
10000
00110
00100
00010
00000
```

To include zero:

```cpp
int sub = mask;

while (true) {
    // process sub

    if (sub == 0) {
        break;
    }

    sub = (sub - 1) & mask;
}
```

---

# 10. All Mask–Submask Pairs and 3^N

A common advanced pattern:

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    for (int sub = mask; sub; sub = (sub - 1) & mask) {
        // process (mask, sub)
    }
}
```

Total complexity is:

```text
O(3^N)
```

not `O(4^N)`.

Why?

For every element there are three possible states:

```text
1. outside mask
2. inside mask but outside submask
3. inside submask
```

Each of `N` elements has 3 choices:

```text
3^N
```

If you also scan all `N` positions for every pair:

```text
O(N * 3^N)
```

---

# 11. Range Masks

To create ones over bit positions:

```text
L ... R
```

first create:

```text
R-L+1
```

lower ones:

```cpp
(1LL << (R - L + 1)) - 1
```

then shift them by `L`:

```cpp
long long rangeMask = ((1LL << (R - L + 1)) - 1) << L;
```

Example:

```text
L=2, R=5

base mask = 1111
shift 2   = 111100
```

Toggle this range:

```cpp
x ^= rangeMask;
```

---

# 12. Rightmost Set Bit

Two important identities:

```cpp
x & -x
```

isolates the rightmost set bit.

```cpp
x & (x - 1)
```

removes the rightmost set bit.

Example:

```text
x = 12 = 1100
```

Isolate:

```text
x & -x
=
0100
=
4
```

Remove:

```text
1100
&
1011
=
1000
```

These patterns become important later in:

```text
Fenwick Tree
subset tricks
bit decomposition
```

---

# 13. Practice Problems

## 1. Subsets

[LeetCode 78 — Subsets](https://leetcode.com/problems/subsets/)

Recognition:

```text
generate all subsets
→ enumerate masks
```

## 2. Maximum Product of Word Lengths

[LeetCode 318 — Maximum Product of Word Lengths](https://leetcode.com/problems/maximum-product-of-word-lengths/)

Represent characters of each word as a mask.

Two words share no character iff:

```cpp
(maskA & maskB) == 0
```

## 3. Number of Wonderful Substrings

[LeetCode 1915 — Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/)

Practice:

```text
parity mask
XOR
toggle bits
```

## 4. Elevator Rides

[CSES — Elevator Rides](https://cses.fi/problemset/task/1653)

Next progression:

```text
DP state = subset mask
```

## 5. Minimum XOR Sum of Two Arrays

[LeetCode 1879 — Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/)

Classic:

```text
bitmask DP
used elements represented by mask
```

---

# 14. Common Mistakes

### Mistake 1 — `!` instead of `~`

Wrong:

```cpp
mask & !(1 << i)
```

Correct:

```cpp
mask & ~(1 << i)
```

### Mistake 2 — Wrong mask loop bound

Wrong:

```cpp
mask <= (1 << n)
```

Correct:

```cpp
mask < (1 << n)
```

Last valid mask is:

```text
2^N - 1
```

### Mistake 3 — Mixing item labels and bit indices

Problem may say:

```text
items 1...N
```

Code bits are usually:

```text
0...N-1
```

### Mistake 4 — Forget zero submask

The compact submask loop stops before processing zero.

Handle it when zero matters.

### Mistake 5 — Use `1 << i` with larger `long long` masks

Prefer:

```cpp
1LL << i
```

### Mistake 6 — Think all `(mask,submask)` pairs cost `4^N`

Total pairs:

```text
3^N
```

because each element has only three valid roles.

---

# 15. Recognition Table

| Signal | Pattern |
|---|---|
| `N` is small and every item is chosen/not chosen | Bitmask |
| Need all subsets | `mask = 0 ... 2^N-1` |
| Need membership of item `i` | `mask & (1<<i)` |
| Add item | `mask | (1<<i)` |
| Remove item | `mask & ~(1<<i)` |
| Toggle item | `mask ^ (1<<i)` |
| Need all submasks of one state | `(sub-1)&mask` |
| DP state is “which elements are used?” | Bitmask DP |
| Need split of a mask into two groups | mask/submask enumeration |
| Need set of letters/features | one bit per feature |

---

# 16. Final Mental Model

```text
N items
  ↓
N yes/no decisions
  ↓
N bits
  ↓
one integer MASK
```

Core operations:

```text
SET:
mask | (1<<i)

REMOVE:
mask & ~(1<<i)

CHECK:
mask & (1<<i)

TOGGLE:
mask ^ (1<<i)
```

All subsets:

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    ...
}
```

All submasks:

```cpp
for (int sub = mask; sub; sub = (sub - 1) & mask) {
    ...
}
```

60-second recognition:

```text
N ≲ 20
+
selected / not selected state
=
BITMASK
```

Then ask:

```text
all subsets?
→ enumerate masks

only subsets of current state?
→ enumerate submasks

DP over used elements?
→ mask is the state
```

The key conversion to master is:

```text
SUBSET
↔
BINARY MASK
```
