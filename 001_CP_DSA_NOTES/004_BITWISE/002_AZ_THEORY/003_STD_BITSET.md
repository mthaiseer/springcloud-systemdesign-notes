# C++ `std::bitset` — Fundamentals
## Fixed-Size Bit Containers, Operations, Masks, Queries, and Practical CP Usage
### TLE-Style Self-Study Notes

> This note is based on the supplied `std::bitset` material and reorganized for self-study.
>
> Core flow:
>
> ```text
> binary bits
>   ↓
> fixed-size bit container
>   ↓
> set / reset / test / flip
>   ↓
> any / none / all / count
>   ↓
> bitwise operations
>   ↓
> conversion
>   ↓
> practical CP patterns
> ```

---

# Table of Contents

- [1. Prerequisite 1 — Bits and Bit Positions](#1-prerequisite-1--bits-and-bit-positions)
- [2. Prerequisite 2 — Integer Masks vs `bitset`](#2-prerequisite-2--integer-masks-vs-bitset)
- [3. What Is `std::bitset`?](#3-what-is-stdbitset)
- [4. Why Use `bitset`?](#4-why-use-bitset)
- [5. Construction](#5-construction)
- [6. Indexing Convention](#6-indexing-convention)
- [7. Set a Bit](#7-set-a-bit)
- [8. Reset a Bit](#8-reset-a-bit)
- [9. Test a Bit](#9-test-a-bit)
- [10. Flip Bits](#10-flip-bits)
- [11. Query the Bitset State](#11-query-the-bitset-state)
- [12. Bitwise Operations](#12-bitwise-operations)
- [13. Shift Operations](#13-shift-operations)
- [14. Conversion to Integer](#14-conversion-to-integer)
- [15. Conversion to String](#15-conversion-to-string)
- [16. Detailed Dry Run](#16-detailed-dry-run)
- [17. Complete C++17 Example](#17-complete-c17-example)
- [18. Prerequisite 3 — `test()` vs `operator[]`](#18-prerequisite-3--test-vs-operator)
- [19. Common CP Pattern — Character Set](#19-common-cp-pattern--character-set)
- [20. Common CP Pattern — Hamming Distance](#20-common-cp-pattern--hamming-distance)
- [21. Common CP Pattern — Dynamic Bit DP / Reachability](#21-common-cp-pattern--dynamic-bit-dp--reachability)
- [22. `bitset` vs Integer Bitmask](#22-bitset-vs-integer-bitmask)
- [23. Caveats](#23-caveats)
- [24. Common Mistakes](#24-common-mistakes)
- [25. Recognition Table](#25-recognition-table)
- [26. Final Mental Model](#26-final-mental-model)

---

# 1. Prerequisite 1 — Bits and Bit Positions

A bit can hold only:

```text
0
or
1
```

Example:

```text
10110110
```

Bit positions are counted from the **right** starting at `0`.

```text
printed:    1   0   1   1   0   1   1   0
position:   7   6   5   4   3   2   1   0
```

So:

```text
bit 0 = 0
bit 1 = 1
bit 2 = 1
bit 3 = 0
...
```

This same indexing rule is used by:

```cpp
std::bitset<N>
```

---

# 2. Prerequisite 2 — Integer Masks vs `bitset`

With an integer, we manually use formulas such as:

```cpp
x |= (1LL << i);
x &= ~(1LL << i);
bool set = (x & (1LL << i)) != 0;
x ^= (1LL << i);
```

With `bitset`, the same ideas become named operations:

```cpp
bits.set(i);
bits.reset(i);
bits.test(i);
bits.flip(i);
```

Mental mapping:

```text
INTEGER MASK              BITSET

x | (1<<i)                bits.set(i)

x & ~(1<<i)               bits.reset(i)

x & (1<<i)                bits.test(i)

x ^ (1<<i)                bits.flip(i)
```

So `bitset` is not a different bit theory.

It is a convenient fixed-size C++ container built around the same bit ideas.

---

# 3. What Is `std::bitset`?

`std::bitset<N>` represents exactly:

```text
N bits
```

Example:

```cpp
bitset<8> bits;
```

creates:

```text
00000000
```

The size:

```text
8
```

is known at compile time.

This is important:

```cpp
bitset<N>
```

is **fixed-size**.

You cannot dynamically resize it later.

---

# 4. Why Use `bitset`?

## 1. Compact Boolean Storage

Instead of:

```cpp
bool used[1000];
```

you can represent fixed binary state as:

```cpp
bitset<1000> used;
```

Each position conceptually represents one flag.

---

## 2. Easy Bit-Level Operations

Instead of manually constructing masks:

```cpp
x |= (1LL << i);
```

you can write:

```cpp
bits.set(i);
```

---

## 3. Whole-Bitset Operations

You can directly perform:

```text
AND
OR
XOR
NOT
SHIFT
```

between large fixed-size bit sequences.

This is where `bitset` becomes especially useful in competitive programming.

---

# 5. Construction

Include:

```cpp
#include <bitset>
```

---

## Default Construction

```cpp
bitset<8> bits;
```

Result:

```text
00000000
```

---

## Construct from Integer

```cpp
bitset<8> bits(0b10101010);
```

Result:

```text
10101010
```

Binary literals such as:

```cpp
0b10101010
```

are available in C++14+.

---

## Construct from String

```cpp
bitset<8> bits("1101");
```

The string initializes the lower positions and is padded on the left:

```text
00001101
```

---

# 6. Indexing Convention

This is a common source of confusion.

For:

```cpp
bitset<8> bits("10110010");
```

printed form:

```text
10110010
```

positions:

```text
index:    7 6 5 4 3 2 1 0
bits:     1 0 1 1 0 0 1 0
```

Therefore:

```cpp
bits[0]
```

is the **rightmost** bit.

And:

```cpp
bits[7]
```

is the **leftmost** bit.

---

# 7. Set a Bit

Use:

```cpp
bits.set(i);
```

This forces bit `i` to:

```text
1
```

Example:

```text
bits = 10101010
```

Set bit `2`:

```cpp
bits.set(2);
```

Before:

```text
10101010
```

Bit 2 is `0`.

After:

```text
10101110
```

---

## Set All Bits

Calling:

```cpp
bits.set();
```

sets every bit to `1`.

Example:

```text
00010010
→
11111111
```

---

# 8. Reset a Bit

Use:

```cpp
bits.reset(i);
```

This forces bit `i` to:

```text
0
```

Example:

```text
bits = 10111110
```

Reset bit `3`:

```cpp
bits.reset(3);
```

Result:

```text
10110110
```

---

## Reset All Bits

```cpp
bits.reset();
```

Result:

```text
00000000
```

---

# 9. Test a Bit

Use:

```cpp
bool value = bits.test(i);
```

It returns:

```text
true  → bit is 1
false → bit is 0
```

Example:

```text
bits = 10110110
```

Then:

```cpp
bits.test(2)
```

returns:

```text
true
```

because bit 2 is `1`.

---

## Alternative: `operator[]`

```cpp
bool value = bits[i];
```

This is shorter.

Important difference:

```text
test(i)
→ bounds checked

bits[i]
→ no bounds checking
```

---

# 10. Flip Bits

## Flip One Bit

```cpp
bits.flip(i);
```

Meaning:

```text
0 → 1
1 → 0
```

Example:

```text
bits = 10101010
```

Flip bit `5`.

Bit 5 is currently:

```text
1
```

So it becomes:

```text
0
```

Correct result:

```text
10001010
```

---

## Flip All Bits

```cpp
bits.flip();
```

Example:

```text
10101010
→
01010101
```

---

# 11. Query the Bitset State

Suppose:

```cpp
bitset<8> bits("10110010");
```

Useful functions:

```cpp
bits.any();
bits.none();
bits.all();
bits.count();
bits.size();
```

---

## `any()`

```cpp
bits.any()
```

Returns true if at least one bit is `1`.

For:

```text
10110010
```

result:

```text
true
```

---

## `none()`

Returns true if every bit is `0`.

```text
00000000 → true
10110010 → false
```

---

## `all()`

Returns true if every bit is `1`.

```text
11111111 → true
10110010 → false
```

---

## `count()`

Counts set bits.

For:

```text
10110010
```

set bits:

```text
4
```

So:

```cpp
bits.count()
```

returns:

```text
4
```

---

## `size()`

For:

```cpp
bitset<8> bits;
```

```cpp
bits.size()
```

returns:

```text
8
```

---

# 12. Bitwise Operations

Suppose:

```cpp
bitset<8> a("10101010");
bitset<8> b("11001010");
```

---

## AND

```cpp
a &= b;
```

Bit by bit:

```text
  10101010
& 11001010
----------
  10001010
```

---

## OR

```cpp
a |= b;
```

```text
  10101010
| 11001010
----------
  11101010
```

---

## XOR

```cpp
a ^= b;
```

```text
  10101010
^ 11001010
----------
  01100000
```

---

## NOT

```cpp
~a
```

flips every bit.

---

## AND NOT

```cpp
a &= ~b;
```

Mental meaning:

```text
keep bits present in A
but absent from B
```

Set interpretation:

```text
A \ B
```

---

# 13. Shift Operations

`bitset` also supports:

```cpp
bits << k
bits >> k
bits <<= k
bits >>= k
```

Example:

```cpp
bitset<8> bits("00010110");
```

Left shift 2:

```text
00010110
→
01011000
```

Right shift 2:

```text
00010110
→
00000101
```

The size remains:

```text
8
```

Bits shifted outside the fixed width are discarded.

New positions are filled with zero.

---

# 14. Conversion to Integer

Use:

```cpp
bits.to_ulong();
bits.to_ullong();
```

Example:

```cpp
bitset<8> bits("10111110");
```

Binary:

```text
10111110₂
```

Decimal:

```text
190
```

So:

```cpp
unsigned long value = bits.to_ulong();
```

gives:

```text
190
```

---

## Overflow Caveat

If the bitset contains a value too large for the target integer type:

```cpp
to_ulong()
```

or:

```cpp
to_ullong()
```

can throw:

```text
std::overflow_error
```

This matters when working with very large bitsets.

---

# 15. Conversion to String

Use:

```cpp
string s = bits.to_string();
```

Example:

```cpp
bitset<8> bits("10111110");
```

Then:

```cpp
bits.to_string()
```

returns:

```text
"10111110"
```

Useful when:

```text
printing
debugging
storing textual binary representation
```

---

# 16. Detailed Dry Run

Start with:

```cpp
bitset<8> bits(0b10101010);
```

State:

```text
10101010
```

Positions:

```text
index: 7 6 5 4 3 2 1 0
bits:  1 0 1 0 1 0 1 0
```

---

## Step 1 — Set Bit 2

```cpp
bits.set(2);
```

Bit 2:

```text
0 → 1
```

Result:

```text
10101110
```

---

## Step 2 — Flip Bit 5

```cpp
bits.flip(5);
```

Current bit 5 is:

```text
1
```

Flip:

```text
1 → 0
```

Result:

```text
10001110
```

---

## Step 3 — Test Bit 3

```cpp
bits.test(3)
```

Current bit 3 is:

```text
1
```

Result:

```text
true
```

---

## Step 4 — Count Set Bits

```text
10001110
```

Set bits:

```text
4
```

So:

```cpp
bits.count()
```

returns:

```text
4
```

---

## Step 5 — Convert to Integer

```text
10001110₂
```

Decimal:

```text
128 + 8 + 4 + 2
=
142
```

So:

```cpp
bits.to_ulong()
```

returns:

```text
142
```

> Note: this dry run corrects the arithmetic implied by the supplied sample sequence. After `set(2)`, flipping bit `5` turns that bit from `1` to `0`, producing `10001110`.

---

# 17. Complete C++17 Example

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    bitset<8> bits(0b10101010);

    cout << "Initial: " << bits << '\n';

    bits.set(2);
    cout << "After set(2): " << bits << '\n';

    bits.flip(5);
    cout << "After flip(5): " << bits << '\n';

    cout << "Bit 3: " << bits.test(3) << '\n';

    cout << "Any: " << bits.any() << '\n';
    cout << "All: " << bits.all() << '\n';
    cout << "None: " << bits.none() << '\n';

    cout << "Count: " << bits.count() << '\n';
    cout << "Size: " << bits.size() << '\n';

    cout << "As integer: " << bits.to_ulong() << '\n';
    cout << "As string: " << bits.to_string() << '\n';

    return 0;
}
```

Expected important states:

```text
Initial:
10101010

After set(2):
10101110

After flip(5):
10001110

bit 3:
1

count:
4

decimal:
142
```

---

# 18. Prerequisite 3 — `test()` vs `operator[]`

Both can read a bit:

```cpp
bits.test(i);
bits[i];
```

Difference:

| Operation | Bounds checked? | Typical use |
|---|---:|---|
| `bits.test(i)` | Yes | safer access |
| `bits[i]` | No | concise contest code |

Example:

```cpp
bitset<8> bits;
```

This is safe:

```cpp
bits.test(20);
```

in the sense that it detects invalid access and throws:

```text
std::out_of_range
```

But:

```cpp
bits[20]
```

does not provide the same bounds checking guarantee.

---

# 19. Common CP Pattern — Character Set

Suppose strings use lowercase English letters:

```text
'a' ... 'z'
```

We can use:

```cpp
bitset<26>
```

Example:

```cpp
bitset<26> present;

for (char ch : s) {
    present.set(ch - 'a');
}
```

Then:

```cpp
present.test('c' - 'a')
```

checks whether `'c'` appeared.

---

## Example

```text
s = "banana"
```

Present letters:

```text
a
b
n
```

So only those three bit positions are set.

Distinct count:

```cpp
present.count()
```

returns:

```text
3
```

---

# 20. Common CP Pattern — Hamming Distance

Suppose:

```cpp
bitset<8> a("10110110");
bitset<8> b("10000111");
```

Positions that differ:

```text
a ^ b
```

Then:

```cpp
(a ^ b).count()
```

gives the number of differing bit positions.

Template:

```cpp
int distance = (a ^ b).count();
```

Recognition:

```text
"how many binary positions differ?"
→ XOR + count
```

---

# 21. Common CP Pattern — Dynamic Bit DP / Reachability

A powerful `bitset` pattern is:

```text
reachable sums
```

Suppose:

```text
reachable[s] = 1
```

means sum `s` is possible.

Start:

```cpp
bitset<10001> reachable;
reachable[0] = 1;
```

For value:

```text
x
```

update:

```cpp
reachable |= (reachable << x);
```

Why?

If old sum `s` was reachable:

```text
reachable[s] = 1
```

then after adding `x`:

```text
s+x
```

becomes reachable.

The shift:

```text
reachable << x
```

moves every reachable sum by `x`.

OR combines:

```text
old sums
+
new sums
```

This is one of the most important advanced competitive-programming uses of `bitset`.

---

## Tiny Example

Values:

```text
[2,3]
```

Start:

```text
reachable:
sum 0 = true
```

After `2`:

```text
0
2
```

After `3`:

```text
0
2
3
5
```

Because:

```text
0+3 = 3
2+3 = 5
```

---

# 22. `bitset` vs Integer Bitmask

Use an integer mask when:

```text
you need around <= 64 bits
and want compact arithmetic / subset enumeration
```

Example:

```cpp
long long mask;
```

Use `bitset<N>` when:

```text
N is fixed at compile time
and larger than normal integer width
or
you want convenient bulk bit operations
```

Comparison:

| Feature | Integer mask | `bitset<N>` |
|---|---|---|
| Typical width | 32/64 bits | arbitrary compile-time `N` |
| Individual bit operations | manual masks | member functions |
| `count()` | builtin popcount | `.count()` |
| print binary | manual | direct `cout << bits` |
| large bulk XOR/AND | limited by integer width | convenient |
| subset enumeration | excellent | less natural |
| bitset DP | limited | excellent |

---

# 23. Caveats

## Fixed Size

This:

```cpp
bitset<N>
```

requires `N` at compile time.

You cannot do:

```cpp
int n;
cin >> n;
bitset<n> bits;
```

because `n` is runtime data.

---

## Integer Conversion Limits

A large bitset may not fit into:

```cpp
unsigned long
```

or:

```cpp
unsigned long long
```

Then conversion can throw:

```text
overflow_error
```

---

## Index Direction

Printed string:

```text
MSB ... LSB
```

but indexing:

```text
0 = LSB
```

This is the most common conceptual mistake.

---

# 24. Common Mistakes

## Mistake 1 — Read Printed Position Left-to-Right

For:

```text
10110010
```

`bits[0]` is not the leftmost `1`.

It is the rightmost bit.

---

## Mistake 2 — Think `flip(i)` Means Set

`flip(i)` means:

```text
0→1
1→0
```

If you always want `1`, use:

```cpp
set(i)
```

---

## Mistake 3 — Assume `.count()` Returns Zero/One

`.count()` returns:

```text
number of set bits
```

not whether any bit is set.

For a yes/no check use:

```cpp
any()
```

---

## Mistake 4 — Convert Huge `bitset` to Integer

If only bit operations are needed, keep it as a bitset.

Do not call:

```cpp
to_ullong()
```

unless the value actually fits.

---

## Mistake 5 — Use `bitset` When Size Is Runtime-Dynamic

`std::bitset<N>` needs compile-time `N`.

For runtime-size bit collections, another structure is needed.

---

## Mistake 6 — Forget Whole-Bitset Shifts Are Powerful

Instead of manually moving every set bit:

```text
use:
bits << k
```

This is especially valuable in subset-sum style DP.

---

# 25. Recognition Table

| Problem Signal | Think |
|---|---|
| Fixed number of boolean flags | `bitset<N>` |
| Need easy set/reset/test/flip | `bitset` member functions |
| Count set bits | `.count()` |
| Any bit set? | `.any()` |
| No bits set? | `.none()` |
| All bits set? | `.all()` |
| Compare binary states | XOR |
| Count differing bits | `(a ^ b).count()` |
| Fixed alphabet membership | `bitset<26>` |
| Reachability / subset-sum acceleration | `bits |= bits << value` |
| Need >64 fixed bits | `bitset<N>` over integer mask |

---

# 26. Final Mental Model

Think of `bitset<N>` as:

```text
an array of N boolean bits
+
built-in bit operations
```

Core operations:

```text
SET:
bits.set(i)

CLEAR:
bits.reset(i)

CHECK:
bits.test(i)

TOGGLE:
bits.flip(i)

COUNT:
bits.count()
```

Whole-state operations:

```text
AND:
a & b

OR:
a | b

XOR:
a ^ b

NOT:
~a

SHIFT:
a << k
a >> k
```

---

## 60-Second Revision Card

```text
bitset<N>
=
fixed N-bit container

index 0
=
rightmost / least-significant bit

set(i)
→ bit i = 1

reset(i)
→ bit i = 0

test(i)
→ read bit i safely

flip(i)
→ toggle bit i

any()
→ at least one 1

none()
→ zero 1s

all()
→ all 1s

count()
→ number of 1s

to_string()
→ binary string

to_ulong()/to_ullong()
→ integer if it fits
```

Most important CP patterns:

```text
DIFFERING BITS:
(a ^ b).count()

CHARACTER SET:
bitset<26>

SUBSET-SUM / REACHABILITY:
reachable |= reachable << value
```

The key idea is:

```text
integer bit manipulation
and
std::bitset

use the same binary reasoning.

bitset simply gives that reasoning
a convenient fixed-size container interface.
```
