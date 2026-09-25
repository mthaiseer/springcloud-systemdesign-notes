# BITSETS & BITMASKING — PATTERN-WISE CONTEST NOTES

> Based on the uploaded **TLE Level 2 — Bitsets and Bitmasking** material.  
> Goal: turn the lecture into a fast-revision pattern library with **recognition signals, visual dry runs, pseudocode, and C++ templates**.

---

## Table of Contents

- [0. Mental Model](#0-mental-model)
- [1. Built-in Bit Functions](#1-built-in-bit-functions)
  - [Form 1A — Popcount](#form-1a--popcount)
  - [Form 1B — Leading Zeros / MSB](#form-1b--leading-zeros--msb)
  - [Form 1C — Trailing Zeros / Lowest Set Bit Position](#form-1c--trailing-zeros--lowest-set-bit-position)
- [2. C++ bitset](#2-c-bitset)
  - [Form 2A — Declare / Initialize](#form-2a--declare--initialize)
  - [Form 2B — Set / Reset / Flip / Test](#form-2b--set--reset--flip--test)
  - [Form 2C — count / any / none / all](#form-2c--count--any--none--all)
  - [Form 2D — Bitset as a Compact Presence Set](#form-2d--bitset-as-a-compact-presence-set)
- [3. Bitmasking — Set Representation](#3-bitmasking--set-representation)
  - [Form 3A — Encode a Subset](#form-3a--encode-a-subset)
  - [Form 3B — Check Membership](#form-3b--check-membership)
  - [Form 3C — Add / Remove / Toggle an Element](#form-3c--add--remove--toggle-an-element)
  - [Form 3D — Enumerate All Subsets](#form-3d--enumerate-all-subsets)
- [4. Problem 1 — Print All Subsets](#4-problem-1--print-all-subsets)
- [5. Problem 2 — Maximum Good People Based on Statements](#5-problem-2--maximum-good-people-based-on-statements)
- [6. Recognition Map](#6-recognition-map)
- [7. Complexity Cheat Sheet](#7-complexity-cheat-sheet)
- [8. Contest Templates](#8-contest-templates)
- [9. Final Revision Sheet](#9-final-revision-sheet)

---

# 0. Mental Model

A bitmask stores many **YES / NO states** inside one integer.

For `n = 4` objects:

```text
index:      3 2 1 0
mask:       1 0 1 1

bit 0 = 1  -> object 0 selected
bit 1 = 1  -> object 1 selected
bit 2 = 0  -> object 2 not selected
bit 3 = 1  -> object 3 selected

Selected set = {0, 1, 3}
```

The key mapping is:

```text
object i  <->  bit i
```

Therefore:

```text
0  -> 0000 -> {}
1  -> 0001 -> {0}
2  -> 0010 -> {1}
3  -> 0011 -> {0,1}
...
15 -> 1111 -> {0,1,2,3}
```

For `n` objects there are exactly:

```text
2^n subsets
```

So the masks are:

```text
0 ... (2^n - 1)
```

---

# 1. Built-in Bit Functions

## Form 1A — Popcount

### Recognition Signal

Use when the problem asks:

- number of selected elements in a mask
- number of `1` bits
- size of a represented subset
- parity of number of set bits

### Core Operation

```cpp
__builtin_popcount(x);       // int
__builtin_popcountll(x);     // long long
```

### Visual Dry Run

Take:

```text
x = 22

22 = 10110
     |||||
     10110
```

Count the `1`s:

```text
1 + 0 + 1 + 1 + 0
        ↓
3 set bits
```

Therefore:

```cpp
__builtin_popcount(22) == 3
```

### C++ Example

```cpp
int x = 22;
cout << __builtin_popcount(x);   // 3
```

For `long long`:

```cpp
long long x = 1000000000000LL;
cout << __builtin_popcountll(x);
```

---

## Form 1B — Leading Zeros / MSB

### Recognition Signal

Useful when you need information related to:

- the highest set bit
- approximate `floor(log2(x))`
- binary length

### Core Operation

```cpp
__builtin_clz(x);       // int
__builtin_clzll(x);     // long long
```

For a positive 32-bit integer:

```text
highest_set_bit_index = 31 - __builtin_clz(x)
```

### Visual Dry Run

```text
x = 4

32-bit representation:

00000000000000000000000000000100
|||||||||||||||||||||||||||||^^^
<--------- 29 leading zeros ---->
```

Highest set bit is at zero-based position:

```text
31 - 29 = 2
```

And:

```text
4 = 2^2
```

### C++ Example

```cpp
int x = 4;

int leadingZeros = __builtin_clz(x);
int msbIndex = 31 - leadingZeros;

cout << leadingZeros << '\n'; // 29
cout << msbIndex << '\n';     // 2
```

> Avoid calling `__builtin_clz(0)`.

---

## Form 1C — Trailing Zeros / Lowest Set Bit Position

### Recognition Signal

Use when you need:

- position of the lowest set bit
- number of factors of 2 in a positive integer
- first active bit from the right

### Core Operation

```cpp
__builtin_ctz(x);       // int
__builtin_ctzll(x);     // long long
```

### Visual Dry Run

```text
x = 40

40 = 101000
           ↑↑↑
           3 trailing zeros
```

Therefore:

```text
__builtin_ctz(40) = 3
```

The lowest set bit is at index `3`.

```text
40 = 5 * 2^3
```

### C++ Example

```cpp
int x = 40;
cout << __builtin_ctz(x);   // 3
```

> Avoid calling `__builtin_ctz(0)`.

---

# 2. C++ `bitset`

A `bitset<N>` stores a fixed number of Boolean states as bits. The size `N` must be known at compile time.

```cpp
#include <bitset>
```

---

## Form 2A — Declare / Initialize

### 1. All Zeros

```cpp
bitset<8> b;
```

Visual:

```text
b = 00000000
```

### 2. Initialize from Decimal

```cpp
bitset<8> b(13);
```

Dry run:

```text
13 = 8 + 4 + 1
   = 1101

bitset<8>:
00001101
```

### 3. Initialize from Binary String

```cpp
bitset<8> b("101101");
```

Result:

```text
00101101
```

### C++ Example

```cpp
bitset<8> a;
bitset<8> b(13);
bitset<8> c("101101");

cout << a << '\n'; // 00000000
cout << b << '\n'; // 00001101
cout << c << '\n'; // 00101101
```

---

## Form 2B — Set / Reset / Flip / Test

Start with:

```text
b = 00101000
```

Bit indices:

```text
index: 7 6 5 4 3 2 1 0
bits : 0 0 1 0 1 0 0 0
```

### `set(i)`

Set bit `i` to `1`.

```cpp
b.set(1);
```

```text
Before: 00101000
After : 00101010
               ^
```

### `reset(i)`

Set bit `i` to `0`.

```cpp
b.reset(3);
```

```text
Before: 00101010
After : 00100010
             ^
```

### `flip(i)`

Toggle bit `i`.

```cpp
b.flip(5);
```

```text
Before: 00100010
After : 00000010
          ^
1 -> 0
```

### Access / Test

```cpp
if (b[1]) {
    // bit 1 is set
}
```

---

## Form 2C — `count / any / none / all`

Take:

```text
b = 00101010
```

### `count()`

```cpp
b.count();
```

```text
00101010
  ^ ^ ^
3 ones
```

Result:

```text
3
```

### `any()`

```cpp
b.any()
```

Is **at least one** bit `1`?

```text
00101010 -> true
```

### `none()`

Are **all** bits `0`?

```text
00000000 -> true
00101010 -> false
```

### `all()`

Are **all** bits `1`?

```text
11111111 -> true
11101111 -> false
```

### C++ Example

```cpp
bitset<8> b("00101010");

cout << b.count() << '\n'; // 3
cout << b.any()   << '\n'; // 1
cout << b.none()  << '\n'; // 0
cout << b.all()   << '\n'; // 0
cout << b.size()  << '\n'; // 8
```

---

## Form 2D — Bitset as a Compact Presence Set

### Recognition Signal

Think `bitset` when:

```text
Values come from a small fixed universe
+
You only need present / absent
```

Example:

```text
0 <= a[i] <= 40
```

Instead of conceptually storing:

```text
present[0], present[1], ..., present[40]
```

you can use:

```cpp
bitset<41> present;
```

### Dry Run

Array:

```text
a = [4, 5, 2, 1, 3, 5, 4]
```

Start:

```text
present = 000000...
```

Process:

```text
4 -> set(4)
5 -> set(5)
2 -> set(2)
1 -> set(1)
3 -> set(3)
5 -> already 1
4 -> already 1
```

Relevant bits:

```text
index:    5 4 3 2 1 0
present:  1 1 1 1 1 0
```

Distinct values:

```text
present.count() = 5
```

### C++ Template

```cpp
bitset<41> present;

for (int x : a) {
    present.set(x);
}

cout << present.count();
```

---

# 3. Bitmasking — Set Representation

## Form 3A — Encode a Subset

Suppose:

```text
items = [A, B, C]
index =  0  1  2
```

A mask tells whether each item is selected.

```text
mask = 5

5 = 101
```

Read bits from right to left:

```text
bit 0 = 1 -> A selected
bit 1 = 0 -> B not selected
bit 2 = 1 -> C selected

subset = {A, C}
```

### Fundamental Rule

```text
bit i = 1  -> include i
bit i = 0  -> exclude i
```

---

## Form 3B — Check Membership

### Question

Is object `i` selected in `mask`?

### Formula

```cpp
mask & (1 << i)
```

### Why?

Create a number containing only bit `i`:

```text
1 << i
```

Example:

```text
mask = 10110
i = 2

mask      = 10110
1 << 2    = 00100
             ^
AND       = 00100
```

Non-zero means bit `2` is set.

### C++ Template

```cpp
if (mask & (1 << i)) {
    // i is selected
}
```

For larger masks:

```cpp
if (mask & (1LL << i)) {
    // i is selected
}
```

---

## Form 3C — Add / Remove / Toggle an Element

Take:

```text
mask = 10100
```

### Add element `i`

```cpp
mask |= (1 << i);
```

Example `i = 1`:

```text
10100
00010
-----
10110
```

### Remove element `i`

```cpp
mask &= ~(1 << i);
```

Example remove `i = 2`:

```text
mask        = 10110
1 << 2      = 00100
~(1 << 2)   = 11011
AND         = 10010
```

### Toggle element `i`

```cpp
mask ^= (1 << i);
```

Example toggle `i = 1`:

```text
10010
00010
-----
10000
```

### Quick Table

| Operation | Formula |
|---|---|
| Check bit `i` | `mask & (1LL << i)` |
| Set bit `i` | `mask \|= (1LL << i)` |
| Clear bit `i` | `mask &= ~(1LL << i)` |
| Toggle bit `i` | `mask ^= (1LL << i)` |

---

## Form 3D — Enumerate All Subsets

### Recognition Signal

The statement says something like:

- choose any subset
- every person can be type A or type B
- each item is selected or not selected
- `n` is small (often around 15–25 depending on work per mask)
- try every assignment

Then think:

```text
2 choices per object
=> 2^n configurations
=> bitmask enumeration
```

### Generic Loop

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    for (int i = 0; i < n; i++) {
        if (mask & (1 << i)) {
            // object i belongs to this subset
        }
    }
}
```

### Visual Dry Run — `n = 3`

```text
items = [1, 2, 3]

mask   binary   chosen indices   subset
----------------------------------------
0      000      {}               {}
1      001      {0}              {1}
2      010      {1}              {2}
3      011      {0,1}            {1,2}
4      100      {2}              {3}
5      101      {0,2}            {1,3}
6      110      {1,2}            {2,3}
7      111      {0,1,2}          {1,2,3}
```

---

# 4. Problem 1 — Print All Subsets

**Problem Summary:** Given an array of `N` elements, print every possible subset.

---

### Print All Subsets (Bitmask Enumeration)

* **Core Invariant / Key Insight:**  
  Every subset corresponds to exactly one `N`-bit number. Bit `i = 1` means `a[i]` is included.

* **Step-by-Step Logic:**
  1. There are `2^N` possible masks.
  2. Iterate `mask` from `0` to `(1 << N) - 1`.
  3. For every index `i`, check whether bit `i` is set.
  4. If set, print `a[i]`.

* **ASCII Execution Trace / Visual Dry Run:**

```text
a = [1, 2, 3]
n = 3

All masks: 0 ... 7

mask = 0 = 000
             |||
             000
             none selected
subset = []

mask = 1 = 001
             |||
index:       210
             001
               ^
               a[0]
subset = [1]

mask = 2 = 010
             ^
             a[1]
subset = [2]

mask = 3 = 011
            ^^
          a[1], a[0]
subset = [1,2]

mask = 4 = 100
           ^
           a[2]
subset = [3]

mask = 5 = 101
           ^ ^
        a[2] a[0]
subset = [1,3]

mask = 6 = 110
           ^^
        a[2],a[1]
subset = [2,3]

mask = 7 = 111
           ^^^
subset = [1,2,3]
```

* **Pseudocode:**

```text
for mask = 0 to (2^n - 1):
    for i = 0 to n-1:
        if bit i is set in mask:
            output a[i]
    newline
```

* **C++:**

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int n;
    cin >> n;

    vector<int> a(n);
    for (int &x : a) cin >> x;

    for (int mask = 0; mask < (1 << n); mask++) {
        cout << "[ ";

        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                cout << a[i] << ' ';
            }
        }

        cout << "]\n";
    }
}
```

* **Complexity:**

```text
Number of masks = 2^N
Check N bits per mask

Time  = O(N * 2^N)
Space = O(1) extra, excluding output
```

---

# 5. Problem 2 — Maximum Good People Based on Statements

Problem Link: [Maximum Good People Based on Statements — LeetCode](https://leetcode.com/problems/maximum-good-people-based-on-statements/)

**Problem Summary:** Each person is either good or bad. Good people always make truthful statements; statements from bad people cannot be trusted. Find the maximum possible number of good people consistent with all statements made by people assumed good.

---

### Maximum Good People Based on Statements (Bitmask / Exhaustive Assignment / LeetCode)

* **Core Invariant / Key Insight:**  
  A mask represents one complete hypothesis about who is good. **Only statements made by people whose bits are `1` need to be enforced.** If every good person's statement agrees with the mask, the mask is valid.

* **State Meaning:**

```text
bit i = 1 -> person i assumed GOOD
bit i = 0 -> person i assumed BAD
```

For `n` people:

```text
0 ... 2^n - 1
```

enumerates every possible good/bad assignment.

---

## Why Bad People's Statements Are Ignored

If person `i` is assumed bad:

```text
mask bit i = 0
```

their statement can be true **or** false.

So:

```cpp
if ((mask & (1 << i)) == 0) continue;
```

Only assumed-good people impose constraints.

---

## Statement Meaning

```text
statements[i][j] = 0 -> person i says person j is BAD
statements[i][j] = 1 -> person i says person j is GOOD
statements[i][j] = 2 -> no statement / unknown
```

For a candidate mask:

```cpp
int good = (mask & (1 << j)) ? 1 : 0;
```

This gives what **our current hypothesis** says about person `j`.

Then compare it with what good person `i` says.

---

## Contradiction Rule

If assumed-good person `i` says:

```text
statements[i][j] = 1
```

but the mask says:

```text
j is bad
```

then the mask is impossible.

Similarly:

```text
statement = 0
mask says j is good
```

is also a contradiction.

Compactly:

```cpp
if (statements[i][j] != 2 &&
    statements[i][j] != good) {
    invalid
}
```

---

## ASCII Execution Trace / Visual Dry Run

Use this small example:

```text
statements =
[
  [2, 1, 2],
  [1, 2, 0],
  [2, 0, 2]
]

People: 0, 1, 2
```

Meaning:

```text
person 0 says person 1 is GOOD
person 1 says person 0 is GOOD
person 1 says person 2 is BAD
person 2 says person 1 is BAD
```

Try:

```text
mask = 011
```

Remember:

```text
bits: person 2 person 1 person 0
        0        1        1

person 0 = GOOD
person 1 = GOOD
person 2 = BAD
```

### Check person 0

```text
person 0 is GOOD
=> their statements MUST be true

statement[0][1] = 1
=> person 1 must be GOOD

mask says person 1 = GOOD
MATCH ✓
```

### Check person 1

```text
person 1 is GOOD
=> their statements MUST be true

statement[1][0] = 1
mask says person 0 = GOOD
MATCH ✓

statement[1][2] = 0
mask says person 2 = BAD
MATCH ✓
```

### Check person 2

```text
person 2 is BAD
=> ignore all their statements
```

Therefore:

```text
mask 011 is VALID
number of good people = popcount(011) = 2
```

---

## Now Try an Invalid Mask

```text
mask = 111

person 0 = GOOD
person 1 = GOOD
person 2 = GOOD
```

Person `1` says:

```text
person 2 is BAD
```

But mask says:

```text
person 2 is GOOD
```

Contradiction:

```text
statement = 0
mask value = 1

0 != 1
=> INVALID MASK
```

---

## Pseudocode

```text
answer = 0

for every mask from 0 to 2^n - 1:

    valid = true

    for every person i:

        if i is BAD in mask:
            continue

        for every person j:

            if statement[i][j] == 2:
                continue

            actual = whether j is GOOD in mask

            if statement[i][j] != actual:
                valid = false
                break

        if not valid:
            break

    if valid:
        answer = max(answer, number of set bits in mask)

return answer
```

---

## C++ — Clear Contest Version

```cpp
class Solution {
public:
    int maximumGood(vector<vector<int>>& statements) {
        int n = statements.size();
        int ans = 0;

        for (int mask = 0; mask < (1 << n); mask++) {

            bool valid = true;

            for (int i = 0; i < n && valid; i++) {

                // Only GOOD people's statements must be truthful.
                if ((mask & (1 << i)) == 0) {
                    continue;
                }

                for (int j = 0; j < n; j++) {

                    // 2 = no statement.
                    if (statements[i][j] == 2) {
                        continue;
                    }

                    int assumedGood =
                        (mask & (1 << j)) ? 1 : 0;

                    if (statements[i][j] != assumedGood) {
                        valid = false;
                        break;
                    }
                }
            }

            if (valid) {
                ans = max(ans, __builtin_popcount(mask));
            }
        }

        return ans;
    }
};
```

---

## Bit-by-Bit Meaning of the Key Line

```cpp
int assumedGood = (mask & (1 << j)) ? 1 : 0;
```

Example:

```text
mask = 011
j = 1

1 << 1 = 010

  011
& 010
-----
  010   != 0

Therefore:
assumedGood = 1
```

For `j = 2`:

```text
1 << 2 = 100

  011
& 100
-----
  000

Therefore:
assumedGood = 0
```

---

## Why `popcount(mask)` Gives Number of Good People

```text
mask = 101101

1 bits:
       ^ ^^ ^
       4 total

Each 1 represents one GOOD person.

__builtin_popcount(mask) = 4
```

---

## Complexity

There are:

```text
2^N masks
```

For each mask:

```text
up to N good people
x
up to N statements
```

Therefore:

```text
Time  = O(2^N * N^2)
Space = O(1) extra
```

This works because the problem deliberately keeps `N` small enough for exhaustive bitmasking.

---

# 6. Recognition Map

```text
BITSET / BITMASK QUESTION
        |
        +-- Need number of 1 bits?
        |      |
        |      +--> popcount
        |
        +-- Need highest active bit?
        |      |
        |      +--> clz / MSB
        |
        +-- Need lowest active bit position?
        |      |
        |      +--> ctz
        |
        +-- Fixed-size Boolean/presence universe?
        |      |
        |      +--> bitset<N>
        |
        +-- Small N + each object has YES/NO state?
        |      |
        |      +--> integer bitmask
        |
        +-- Need every subset?
        |      |
        |      +--> mask = 0 ... (1<<N)-1
        |
        +-- Need test whether object i is chosen?
        |      |
        |      +--> mask & (1<<i)
        |
        +-- Need maximize/minimize over every assignment?
               |
               +--> enumerate masks
                    validate each mask
                    update answer
```

---

# 7. Complexity Cheat Sheet

| Technique | Typical Complexity |
|---|---:|
| Check one bit | `O(1)` |
| Set / clear / toggle one bit | `O(1)` |
| `popcount` | machine/compiler operation; treat as very fast |
| Scan all `N` bits of one mask | `O(N)` |
| Enumerate all masks | `O(2^N)` |
| Enumerate masks + scan bits | `O(N * 2^N)` |
| Enumerate masks + validate `N x N` relation matrix | `O(N^2 * 2^N)` |
| `bitset<N>` bulk bitwise operation | word-parallel, roughly proportional to `N / word_size` |

---

# 8. Contest Templates

## Template A — Check a Bit

```cpp
bool selected = mask & (1LL << i);
```

---

## Template B — Set a Bit

```cpp
mask |= (1LL << i);
```

---

## Template C — Clear a Bit

```cpp
mask &= ~(1LL << i);
```

---

## Template D — Toggle a Bit

```cpp
mask ^= (1LL << i);
```

---

## Template E — Enumerate Every Subset

```cpp
for (int mask = 0; mask < (1 << n); mask++) {

    for (int i = 0; i < n; i++) {

        if (mask & (1 << i)) {
            // i belongs to subset
        }
    }
}
```

---

## Template F — Maximize Over Valid Assignments

```cpp
int ans = 0;

for (int mask = 0; mask < (1 << n); mask++) {

    bool valid = true;

    // validate(mask)

    if (valid) {
        ans = max(ans, __builtin_popcount(mask));
    }
}
```

---

## Template G — Fixed Presence Set

```cpp
bitset<MAX_VALUE + 1> seen;

for (int x : a) {
    seen.set(x);
}

cout << seen.count();
```

---

# 9. Final Revision Sheet

```text
============================================================
                 BITMASKING IN 60 SECONDS
============================================================

Object i <-> bit i

CHECK:
    mask & (1LL << i)

SET:
    mask |= (1LL << i)

CLEAR:
    mask &= ~(1LL << i)

TOGGLE:
    mask ^= (1LL << i)

COUNT SELECTED:
    __builtin_popcount(mask)
    __builtin_popcountll(mask)

ALL SUBSETS:
    for mask = 0 ... (1<<n)-1

NUMBER OF SUBSETS:
    2^n

MASK INTERPRETATION:
    bit = 1 -> selected
    bit = 0 -> not selected

------------------------------------------------------------

WHEN TO THINK BITMASK?

Small N
+
each item has 2 states
+
need combinations / subsets / assignments

Examples:
    chosen / not chosen
    good / bad
    on / off
    visited / not visited
    included / excluded

------------------------------------------------------------

MAXIMUM GOOD PEOPLE PATTERN

mask = hypothesis

bit i = 1 -> person i GOOD
bit i = 0 -> person i BAD

For every assumed GOOD person:
    check every known statement

statement = 2:
    ignore

statement != state represented by mask:
    reject mask

valid mask:
    ans = max(ans, popcount(mask))

Complexity:
    O(2^N * N^2)

============================================================
