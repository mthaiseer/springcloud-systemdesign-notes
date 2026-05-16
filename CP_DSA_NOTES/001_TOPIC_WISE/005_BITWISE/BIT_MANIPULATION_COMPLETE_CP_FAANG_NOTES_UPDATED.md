# Bit Manipulation Complete Notes for CP + FAANG

> Built from your uploaded handwritten notes: bitmasking, `bitset`, cyclic property of bits, bit contribution technique, operation decoding/conservation of bits, and highest-bit-to-lowest-bit thinking.

---

## Clickable Index

### Part 0 — Master Map
1. [What is Bit Manipulation?](#1-what-is-bit-manipulation)
2. [Binary Representation](#2-binary-representation)
3. [Core Bit Operators](#3-core-bit-operators)
4. [Must-Remember Powers of Two](#4-must-remember-powers-of-two)
5. [Operator Precedence Trap](#5-operator-precedence-trap)

### Part 1 — Bitmasking Basics
6. [Representing a Subset as an Integer](#6-representing-a-subset-as-an-integer)
7. [Check Whether an Element Exists in a Subset](#7-check-whether-an-element-exists-in-a-subset)
8. [Generate All Subsets Using Bitmask](#8-generate-all-subsets-using-bitmask)
9. [Union and Intersection of Subsets](#9-union-and-intersection-of-subsets)

### Part 2 — C++ `bitset`
10. [C++ STL `bitset`](#10-c-stl-bitset)
11. [`bitset` Operations Quick Reference](#11-bitset-operations-quick-reference)

### Part 3 — Common Bit Tricks
12. [Set, Clear, Toggle, Check ith Bit](#12-set-clear-toggle-check-ith-bit)
13. [Count Set Bits](#13-count-set-bits)
14. [Lowest Set Bit](#14-lowest-set-bit)
15. [Power of Two Check](#15-power-of-two-check)

### Part 4 — Application Ideas from Your Notes
16. [Application 1: Cyclic Property of Bits](#16-application-1-cyclic-property-of-bits)
17. [Application 2: Contribution Technique of Bits](#17-application-2-contribution-technique-of-bits)
18. [Application 3: Operation Decoding / Conservation of Bits](#18-application-3-operation-decoding--conservation-of-bits)
19. [Application 4: Highest Bit to Lowest Bit](#19-application-4-highest-bit-to-lowest-bit)

### Part 5 — Problems
20. [Problem 1: Print All Subsets](#20-problem-1-print-all-subsets)
21. [Problem 2: Subset Union and Intersection](#21-problem-2-subset-union-and-intersection)
22. [Problem 3: Sum of XOR of All Pairs](#22-problem-3-sum-of-xor-of-all-pairs)
23. [Problem 4: Count Total Set Bits from 0 to N](#23-problem-4-count-total-set-bits-from-0-to-n)
24. [Problem 5: Find Kth Number in Binary Sequence by Total Set Bits](#24-problem-5-find-kth-number-in-binary-sequence-by-total-set-bits)
25. [Problem 6: Decode Repeated Operation `x = (x & y) + (x | y)`](#25-problem-6-decode-repeated-operation-x--x--y--x--y)
26. [Problem 7: Maximize AND of a Subsequence of Size K](#26-problem-7-maximize-and-of-a-subsequence-of-size-k)

---

# Part 0 — Master Map

## 1. What is Bit Manipulation?

Everything in computers is stored as `0` and `1`.

Bit manipulation means using operators that directly work on bits.

Why it matters:

| Area | Why Bits Help |
|---|---|
| Competitive Programming | Faster than arrays/sets in many subset problems |
| FAANG DSA | XOR, masks, contribution, trie, DP with states |
| System-level thinking | Flags, permissions, compression, memory-efficient state |
| Optimization | `O(1)` checks, fast subset iteration |

---

## 2. Binary Representation

Decimal number `13`:

| Division | Remainder |
|---:|---:|
| 13 / 2 = 6 | 1 |
| 6 / 2 = 3 | 0 |
| 3 / 2 = 1 | 1 |
| 1 / 2 = 0 | 1 |

Read remainders from bottom to top:

```text
13 decimal = 1101 binary
```

Bit positions from right to left:

```text
binary:  1 1 0 1
index:   3 2 1 0
value:   8 4 2 1
```

So:

```text
13 = 1*8 + 1*4 + 0*2 + 1*1
```

---

## 3. Core Bit Operators

| Operator | Name | Meaning | Example |
|---|---|---|---|
| `&` | AND | bit becomes `1` only if both bits are `1` | `1100 & 1010 = 1000` |
| `|` | OR | bit becomes `1` if at least one bit is `1` | `1100 | 1010 = 1110` |
| `^` | XOR | bit becomes `1` if bits are different | `1100 ^ 1010 = 0110` |
| `~` | NOT | flips all bits | `~x` |
| `<<` | left shift | multiply by power of 2 | `x << y = x * 2^y` |
| `>>` | right shift | divide by power of 2 | `x >> y = floor(x / 2^y)` |

### Bit-by-bit table for `12 & 10`

```text
12 = 1100
10 = 1010
---------
&  = 1000 = 8
```

| Bit Position | 12 | 10 | AND |
|---:|---:|---:|---:|
| 3 | 1 | 1 | 1 |
| 2 | 1 | 0 | 0 |
| 1 | 0 | 1 | 0 |
| 0 | 0 | 0 | 0 |

### Bit-by-bit table for `12 | 10`

```text
12 = 1100
10 = 1010
---------
|  = 1110 = 14
```

| Bit Position | 12 | 10 | OR |
|---:|---:|---:|---:|
| 3 | 1 | 1 | 1 |
| 2 | 1 | 0 | 1 |
| 1 | 0 | 1 | 1 |
| 0 | 0 | 0 | 0 |

### Bit-by-bit table for `12 ^ 10`

```text
12 = 1100
10 = 1010
---------
^  = 0110 = 6
```

| Bit Position | 12 | 10 | XOR |
|---:|---:|---:|---:|
| 3 | 1 | 1 | 0 |
| 2 | 1 | 0 | 1 |
| 1 | 0 | 1 | 1 |
| 0 | 0 | 0 | 0 |

---

## 4. Must-Remember Powers of Two

| Power | Value |
|---:|---:|
| `2^0` | 1 |
| `2^1` | 2 |
| `2^2` | 4 |
| `2^3` | 8 |
| `2^4` | 16 |
| `2^5` | 32 |
| `2^6` | 64 |
| `2^7` | 128 |
| `2^8` | 256 |
| `2^9` | 512 |
| `2^10` | 1024 |
| `2^16` | 65536 |
| `2^20` | about `10^6` |
| `2^30` | about `10^9` |
| `2^60` | about `10^18` |

### Memory rule

```text
2^10 ≈ 10^3
2^20 ≈ 10^6
2^30 ≈ 10^9
2^60 ≈ 10^18
```

---

## 5. Operator Precedence Trap

Your notes highlight this classic trap:

```cpp
1 << 2 + 3
```

This is not:

```cpp
(1 << 2) + 3 = 7
```

It is:

```cpp
1 << (2 + 3) = 32
```

Because `+` has higher precedence than `<<`.

Always write:

```cpp
1LL << i
```

or:

```cpp
1LL << (i + 1)
```

---

# Part 1 — Bitmasking Basics

## 6. Representing a Subset as an Integer

Given universal set:

```text
U = {1, 3, 7, 5, 10}
index: 0  1  2  3   4
```

Each bit says whether the element is selected.

Example subset:

```text
{1, 5, 10}
```

Selected indices:

```text
index 0 -> 1 selected
index 3 -> 5 selected
index 4 -> 10 selected
```

Binary mask:

```text
index:  4 3 2 1 0
bit:    1 1 0 0 1
```

So:

```text
mask = 11001 binary = 25 decimal
```

### Bit-by-bit subset table

| Index | Element | Selected? | Bit |
|---:|---:|---|---:|
| 0 | 1 | yes | 1 |
| 1 | 3 | no | 0 |
| 2 | 7 | no | 0 |
| 3 | 5 | yes | 1 |
| 4 | 10 | yes | 1 |

---

## 7. Check Whether an Element Exists in a Subset

To check whether index `i` is selected:

```cpp
if (mask & (1LL << i)) {
    // selected
}
```

Why?

`1LL << i` creates a number where only the `i`th bit is `1`.

Example:

```text
mask = 25 = 11001
i = 3
1 << 3 = 01000
AND:
11001
01000
-----
01000 != 0
```

So index `3` is selected.

---

## 8. Generate All Subsets Using Bitmask

For a set of size `n`, total subsets:

```text
2^n
```

Because each element has 2 choices:

```text
take / not take
```

For `{1, 3, 7}`:

| Mask | Binary | Subset |
|---:|---|---|
| 0 | `000` | `{}` |
| 1 | `001` | `{1}` |
| 2 | `010` | `{3}` |
| 3 | `011` | `{1, 3}` |
| 4 | `100` | `{7}` |
| 5 | `101` | `{1, 7}` |
| 6 | `110` | `{3, 7}` |
| 7 | `111` | `{1, 3, 7}` |

---

## 9. Union and Intersection of Subsets

Let:

```text
U = {1, 3, 7, 5, 10}

A = {1, 5, 10}
B = {3, 7, 5}
```

Masks:

```text
A = 11001 binary = 25
B = 01110 binary = 14
```

### Union

```text
A ∪ B = A | B
```

Bit operation:

```text
A = 11001
B = 01110
---------
| = 11111 = 31
```

So:

```text
A ∪ B = {1, 3, 7, 5, 10}
```

### Intersection

```text
A ∩ B = A & B
```

Bit operation:

```text
A = 11001
B = 01110
---------
& = 01000 = 8
```

So:

```text
A ∩ B = {5}
```

---

# Part 2 — C++ bitset

## 10. C++ STL `bitset`

`bitset` gives a fixed-size binary container.

Declaration:

```cpp
bitset<8> b;
```

Initialize with number:

```cpp
bitset<4> b(7);
cout << b.to_string(); // 0111
```

Access like array:

```cpp
cout << b[0]; // least significant bit
cout << b[1];
```

Important:

```text
b[0] is the rightmost bit
```

Example:

```text
bitset<4> b(11)
11 = 1011
b[0] = 1
b[1] = 1
b[2] = 0
b[3] = 1
```

---

## 11. bitset Operations Quick Reference

```cpp
bitset<8> b(13); // 00001101
```

| Operation | Meaning |
|---|---|
| `b.count()` | number of set bits |
| `b.any()` | at least one bit is `1` |
| `b.none()` | no bit is `1` |
| `b.all()` | all bits are `1` |
| `b.set(i)` | set ith bit |
| `b.reset(i)` | clear ith bit |
| `b.flip(i)` | toggle ith bit |
| `b.to_string()` | binary string |
| `b.to_ulong()` | convert to unsigned long |

Example:

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    bitset<8> b(13); // 00001101

    cout << b << "\n";
    cout << b.count() << "\n";

    b.set(1);   // set bit 1
    b.reset(2); // clear bit 2
    b.flip(3);  // toggle bit 3

    cout << b << "\n";
}
```

---

# Part 3 — Common Bit Tricks

## 12. Set, Clear, Toggle, Check ith Bit

Let:

```text
x = 13 = 1101
```

| Task | Formula | Meaning |
|---|---|---|
| Check ith bit | `x & (1LL << i)` | true if ith bit is 1 |
| Set ith bit | `x | (1LL << i)` | force ith bit to 1 |
| Clear ith bit | `x & ~(1LL << i)` | force ith bit to 0 |
| Toggle ith bit | `x ^ (1LL << i)` | flip ith bit |

### Example: set bit 1 in 13

```text
x        = 1101
1 << 1   = 0010
OR       = 1111 = 15
```

### Example: clear bit 2 in 13

```text
x        = 1101
1 << 2   = 0100
~mask    = 1011
AND      = 1001 = 9
```

### Example: toggle bit 3 in 13

```text
x        = 1101
1 << 3   = 1000
XOR      = 0101 = 5
```

---

## 13. Count Set Bits

### Brute force

Check every bit:

```cpp
int countBits(long long x) {
    int cnt = 0;
    for (int i = 0; i < 63; i++) {
        if (x & (1LL << i)) cnt++;
    }
    return cnt;
}
```

### Brian Kernighan trick

```cpp
int countBits(long long x) {
    int cnt = 0;
    while (x > 0) {
        x = x & (x - 1); // removes lowest set bit
        cnt++;
    }
    return cnt;
}
```

Dry run for `x = 13`:

```text
13 = 1101
12 = 1100
13 & 12 = 1100 = 12

12 = 1100
11 = 1011
12 & 11 = 1000 = 8

8 = 1000
7 = 0111
8 & 7 = 0000 = 0
```

Count = 3.

---

## 14. Lowest Set Bit

```cpp
x & -x
```

Example:

```text
x = 12 = 1100
-x in two's complement helps isolate lowest set bit
x & -x = 0100 = 4
```

Useful for:

- Fenwick tree
- subset iteration
- finding rightmost selected element

---

## 15. Power of Two Check

A number is power of two if it has exactly one set bit.

```cpp
bool isPowerOfTwo(long long x) {
    return x > 0 && (x & (x - 1)) == 0;
}
```

Example:

```text
8 = 1000
7 = 0111
8 & 7 = 0000
```

So 8 is power of two.

Example:

```text
10 = 1010
9  = 1001
10 & 9 = 1000
```

So 10 is not power of two.

---

# Part 4 — Application Ideas from Your Notes

## 16. Application 1: Cyclic Property of Bits

Bits repeat in cycles.

For bit position `i`, the pattern length is:

```text
2^(i+1)
```

Within each cycle:

```text
first 2^i numbers -> bit is 0
next  2^i numbers -> bit is 1
```

### Example: bit 0

```text
0 1 2 3 4 5 6 7
0 1 0 1 0 1 0 1
```

Cycle length = `2`.

### Example: bit 1

```text
0 1 2 3 4 5 6 7
0 0 1 1 0 0 1 1
```

Cycle length = `4`.

### Example: bit 2

```text
0 1 2 3 4 5 6 7
0 0 0 0 1 1 1 1
```

Cycle length = `8`.

### Count ones at bit `i` from `0` to `x`

```cpp
long long countOnesAtBit(long long x, int i) {
    long long len = 1LL << (i + 1);
    long long fullCycles = (x + 1) / len;
    long long rem = (x + 1) % len;

    long long ones = fullCycles * (1LL << i);
    ones += max(0LL, rem - (1LL << i));

    return ones;
}
```

Why `x + 1`?

Because range is:

```text
0, 1, 2, ..., x
```

That contains `x + 1` numbers.

---

## 17. Application 2: Contribution Technique of Bits

When expression is sum over many pairs/subarrays, do not compute each value directly.

Instead:

```text
Compute contribution of each bit independently.
```

For XOR:

At bit `b`, XOR gives `1` only when bits are different.

So if:

```text
cnt1 = number of elements with bit b = 1
cnt0 = number of elements with bit b = 0
```

Number of pairs with XOR bit `b = 1`:

```text
cnt1 * cnt0
```

Contribution:

```text
cnt1 * cnt0 * 2^b
```

### Example: A = [1, 3, 5]

Binary:

| Number | Binary |
|---:|---|
| 1 | `001` |
| 3 | `011` |
| 5 | `101` |

All pairs:

```text
1 ^ 3 = 2
1 ^ 5 = 4
3 ^ 5 = 6
Total = 12
```

Bit contribution:

| Bit | Values at this bit | cnt1 | cnt0 | Pairs different | Contribution |
|---:|---|---:|---:|---:|---:|
| 0 | 1,1,1 | 3 | 0 | 0 | 0 |
| 1 | 0,1,0 | 1 | 2 | 2 | `2 * 2 = 4` |
| 2 | 0,0,1 | 1 | 2 | 2 | `2 * 4 = 8` |

Total:

```text
4 + 8 = 12
```

---

## 18. Application 3: Operation Decoding / Conservation of Bits

Your notes show the identity:

```text
a + b = (a | b) + (a & b)
```

Why?

Look bit-by-bit.

| a bit | b bit | a OR b | a AND b | sum contribution |
|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 |
| 0 | 1 | 1 | 0 | 1 |
| 1 | 0 | 1 | 0 | 1 |
| 1 | 1 | 1 | 1 | 2 |

If both bits are `1`, normal addition creates value `2 * 2^i`, which is same as:

```text
OR contributes 1 * 2^i
AND contributes 1 * 2^i
total = 2 * 2^i
```

So the bit value is conserved.

This helps when a problem repeatedly applies operations like:

```text
x = (x & y) + (x | y)
```

You decode what stays constant instead of simulating blindly.

---

## 19. Application 4: Highest Bit to Lowest Bit

When maximizing a bitwise answer like AND/OR/XOR, often build the answer from highest bit to lowest bit.

Reason:

```text
A higher bit is more powerful than all lower bits combined.
```

Example:

```text
bit 3 value = 8
bits 0 + 1 + 2 = 1 + 2 + 4 = 7
```

So always try to set higher bits first.

Template:

```cpp
long long ans = 0;

for (int b = 60; b >= 0; b--) {
    long long candidate = ans | (1LL << b);

    if (can(candidate)) {
        ans = candidate;
    }
}
```

This is common in:

- maximum AND of K numbers
- maximum XOR with trie
- minimum XOR pair
- bitwise constraints
- greedy bit construction

---

# Part 5 — Problems

---

## 20. Problem 1: Print All Subsets

### Problem Statement

Given an array of `n` distinct integers, print all subsets.

### Input

```text
n = 3
arr = [1, 3, 7]
```

### Expected Output

```text
{}
{1}
{3}
{1, 3}
{7}
{1, 7}
{3, 7}
{1, 3, 7}
```

### Brute Force Thinking

For each element:

```text
choice 1: take it
choice 2: do not take it
```

This gives recursion with `2^n` leaves.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void generate(int idx, vector<int>& arr, vector<int>& cur) {
    if (idx == arr.size()) {
        cout << "{";
        for (int i = 0; i < cur.size(); i++) {
            if (i) cout << ", ";
            cout << cur[i];
        }
        cout << "}\n";
        return;
    }

    // not take
    generate(idx + 1, arr, cur);

    // take
    cur.push_back(arr[idx]);
    generate(idx + 1, arr, cur);
    cur.pop_back();
}

int main() {
    vector<int> arr = {1, 3, 7};
    vector<int> cur;
    generate(0, arr, cur);
}
```

### Brute Force Result

```text
Total subsets = 2^3 = 8
```

### Optimal Bitmask Idea

Instead of recursion, use numbers from:

```text
0 to 2^n - 1
```

Each number is one subset.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> arr = {1, 3, 7};
    int n = arr.size();

    for (int mask = 0; mask < (1 << n); mask++) {
        cout << "{";

        bool first = true;
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                if (!first) cout << ", ";
                cout << arr[i];
                first = false;
            }
        }

        cout << "}\n";
    }
}
```

### Index-by-Index Dry Run

Array:

```text
index: 0 1 2
value: 1 3 7
```

| mask | binary | i=0 | i=1 | i=2 | subset |
|---:|---|---|---|---|---|
| 0 | 000 | no | no | no | `{}` |
| 1 | 001 | yes | no | no | `{1}` |
| 2 | 010 | no | yes | no | `{3}` |
| 3 | 011 | yes | yes | no | `{1, 3}` |
| 4 | 100 | no | no | yes | `{7}` |
| 5 | 101 | yes | no | yes | `{1, 7}` |
| 6 | 110 | no | yes | yes | `{3, 7}` |
| 7 | 111 | yes | yes | yes | `{1, 3, 7}` |

### Complexity

```text
Time: O(n * 2^n)
Space: O(1) extra
```

---

## 21. Problem 2: Subset Union and Intersection

### Problem Statement

Given a universal set and two subsets represented as masks, compute:

```text
A union B
A intersection B
```

### Input

```text
U = [1, 3, 7, 5, 10]
A = {1, 5, 10}
B = {3, 7, 5}
```

### Expected Output

```text
A union B = {1, 3, 7, 5, 10}
A intersection B = {5}
```

### Bitmask Encoding

| Index | Element | In A? | In B? |
|---:|---:|---:|---:|
| 0 | 1 | 1 | 0 |
| 1 | 3 | 0 | 1 |
| 2 | 7 | 0 | 1 |
| 3 | 5 | 1 | 1 |
| 4 | 10 | 1 | 0 |

```text
A = 11001 = 25
B = 01110 = 14
```

### Brute Force Idea

Use actual sets/vectors.

For union:

```text
insert all elements of A
insert all elements of B
```

For intersection:

```text
check which elements are present in both
```

### Brute Force Result

```text
Union = {1, 3, 7, 5, 10}
Intersection = {5}
```

### Optimal Bitmask Idea

```text
Union        = A | B
Intersection = A & B
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

vector<int> decode(vector<int>& U, int mask) {
    vector<int> res;

    for (int i = 0; i < U.size(); i++) {
        if (mask & (1 << i)) {
            res.push_back(U[i]);
        }
    }

    return res;
}

void printSet(vector<int> s) {
    cout << "{";
    for (int i = 0; i < s.size(); i++) {
        if (i) cout << ", ";
        cout << s[i];
    }
    cout << "}\n";
}

int main() {
    vector<int> U = {1, 3, 7, 5, 10};

    int A = 0, B = 0;

    // A = {1, 5, 10}
    A |= (1 << 0);
    A |= (1 << 3);
    A |= (1 << 4);

    // B = {3, 7, 5}
    B |= (1 << 1);
    B |= (1 << 2);
    B |= (1 << 3);

    int uni = A | B;
    int inter = A & B;

    cout << "Union: ";
    printSet(decode(U, uni));

    cout << "Intersection: ";
    printSet(decode(U, inter));
}
```

### Index-by-Index Dry Run

| Index | Element | A bit | B bit | OR | AND |
|---:|---:|---:|---:|---:|---:|
| 0 | 1 | 1 | 0 | 1 | 0 |
| 1 | 3 | 0 | 1 | 1 | 0 |
| 2 | 7 | 0 | 1 | 1 | 0 |
| 3 | 5 | 1 | 1 | 1 | 1 |
| 4 | 10 | 1 | 0 | 1 | 0 |

```text
OR  = 11111 -> all elements
AND = 01000 -> only index 3 -> {5}
```

---

## 22. Problem 3: Sum of XOR of All Pairs

### Problem Statement

Given an array `A`, find:

```text
sum of A[i] ^ A[j] for all i < j
```

### Input

```text
A = [1, 3, 5]
```

### Expected Output

```text
12
```

Explanation:

```text
1 ^ 3 = 2
1 ^ 5 = 4
3 ^ 5 = 6
Total = 12
```

### Brute Force Idea

Try all pairs.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> A = {1, 3, 5};
    long long ans = 0;

    for (int i = 0; i < A.size(); i++) {
        for (int j = i + 1; j < A.size(); j++) {
            ans += (A[i] ^ A[j]);
        }
    }

    cout << ans << "\n";
}
```

### Brute Force Result

| Pair | XOR |
|---|---:|
| `1 ^ 3` | 2 |
| `1 ^ 5` | 4 |
| `3 ^ 5` | 6 |

```text
Total = 12
```

### Why Brute Force Fails

If `N = 10^5`, pairs are:

```text
N * (N - 1) / 2 ≈ 5 * 10^9
```

Too slow.

### Optimal Idea: Bit Contribution

For each bit independently:

```text
XOR bit is 1 when one number has 0 and another has 1.
```

At bit `b`:

```text
cnt1 = numbers with bit b = 1
cnt0 = numbers with bit b = 0

pairs contributing = cnt1 * cnt0
contribution = cnt1 * cnt0 * 2^b
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long sumXorPairs(vector<int>& A) {
    long long ans = 0;
    int n = A.size();

    for (int b = 0; b < 31; b++) {
        long long cnt1 = 0;

        for (int x : A) {
            if (x & (1 << b)) cnt1++;
        }

        long long cnt0 = n - cnt1;
        ans += cnt1 * cnt0 * (1LL << b);
    }

    return ans;
}

int main() {
    vector<int> A = {1, 3, 5};
    cout << sumXorPairs(A) << "\n";
}
```

### Bit-by-Bit Table

| Number | Binary |
|---:|---|
| 1 | `001` |
| 3 | `011` |
| 5 | `101` |

| Bit | 1 | 3 | 5 | cnt1 | cnt0 | pairs | contribution |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 0 | 1 | 1 | 1 | 3 | 0 | 0 | 0 |
| 1 | 0 | 1 | 0 | 1 | 2 | 2 | `2 * 2 = 4` |
| 2 | 0 | 0 | 1 | 1 | 2 | 2 | `2 * 4 = 8` |

Total:

```text
0 + 4 + 8 = 12
```

### Index-by-Index Dry Run

For bit `0`:

```text
A[0] = 1 -> bit 0 = 1
A[1] = 3 -> bit 0 = 1
A[2] = 5 -> bit 0 = 1
cnt1 = 3, cnt0 = 0
contribution = 0
```

For bit `1`:

```text
A[0] = 1 -> bit 1 = 0
A[1] = 3 -> bit 1 = 1
A[2] = 5 -> bit 1 = 0
cnt1 = 1, cnt0 = 2
contribution = 1 * 2 * 2 = 4
```

For bit `2`:

```text
A[0] = 1 -> bit 2 = 0
A[1] = 3 -> bit 2 = 0
A[2] = 5 -> bit 2 = 1
cnt1 = 1, cnt0 = 2
contribution = 1 * 2 * 4 = 8
```

Final:

```text
ans = 12
```

### Complexity

```text
Time: O(31 * N)
Space: O(1)
```

---

## 23. Problem 4: Count Total Set Bits from 0 to N

### Problem Statement

Given `N`, count total number of `1` bits in binary representation of all numbers from `0` to `N`.

### Input

```text
N = 7
```

### Expected Output

```text
12
```

Because:

| Number | Binary | Set Bits |
|---:|---|---:|
| 0 | `000` | 0 |
| 1 | `001` | 1 |
| 2 | `010` | 1 |
| 3 | `011` | 2 |
| 4 | `100` | 1 |
| 5 | `101` | 2 |
| 6 | `110` | 2 |
| 7 | `111` | 3 |

Total:

```text
0 + 1 + 1 + 2 + 1 + 2 + 2 + 3 = 12
```

### Brute Force Idea

For every number from `0` to `N`, count set bits.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long brute(long long N) {
    long long ans = 0;

    for (long long x = 0; x <= N; x++) {
        ans += __builtin_popcountll(x);
    }

    return ans;
}

int main() {
    cout << brute(7) << "\n";
}
```

### Brute Force Result

```text
12
```

### Why Brute Force Fails

If `N = 10^9`, iterating all numbers is too slow.

### Optimal Idea: Cyclic Property

For each bit `b`, count how many numbers from `0` to `N` have that bit set.

Pattern for bit `b`:

```text
0 repeated 2^b times, then 1 repeated 2^b times
cycle length = 2^(b+1)
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countOnesAtBit(long long N, int b) {
    long long totalNumbers = N + 1;
    long long block = 1LL << (b + 1);
    long long half = 1LL << b;

    long long fullBlocks = totalNumbers / block;
    long long rem = totalNumbers % block;

    long long ones = fullBlocks * half;
    ones += max(0LL, rem - half);

    return ones;
}

long long countTotalSetBits(long long N) {
    long long ans = 0;

    for (int b = 0; b <= 60; b++) {
        ans += countOnesAtBit(N, b);
    }

    return ans;
}

int main() {
    cout << countTotalSetBits(7) << "\n";
}
```

### Bit-by-Bit Table for `N = 7`

Numbers:

```text
0 1 2 3 4 5 6 7
```

| Bit | Pattern | Ones |
|---:|---|---:|
| 0 | `0 1 0 1 0 1 0 1` | 4 |
| 1 | `0 0 1 1 0 0 1 1` | 4 |
| 2 | `0 0 0 0 1 1 1 1` | 4 |

Total:

```text
4 + 4 + 4 = 12
```

### Index-by-Index Dry Run for bit 1

```text
b = 1
half = 2
block = 4
N = 7
totalNumbers = 8

fullBlocks = 8 / 4 = 2
rem = 8 % 4 = 0

ones = fullBlocks * half = 2 * 2 = 4
extra = max(0, rem - half) = max(0, 0 - 2) = 0

total ones at bit 1 = 4
```

### Complexity

```text
Time: O(log N)
Space: O(1)
```

---

## 24. Problem 5: Find Kth Number in Binary Sequence by Total Set Bits

### Problem Statement

Find the smallest number `x` such that total set bits from `0` to `x` is at least `k`.

This matches the pattern in your notes:

```text
check(x) = number of 1s in [0 ... x] >= k
```

### Input

```text
k = 12
```

### Expected Output

```text
7
```

Because total set bits from `0` to `7` is `12`.

### Brute Force Idea

Keep adding popcount:

```cpp
sum += popcount(x)
```

until `sum >= k`.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long brute(long long k) {
    long long sum = 0;

    for (long long x = 0; ; x++) {
        sum += __builtin_popcountll(x);
        if (sum >= k) return x;
    }
}
```

### Brute Force Result for `k = 12`

| x | binary | popcount | running total |
|---:|---|---:|---:|
| 0 | 000 | 0 | 0 |
| 1 | 001 | 1 | 1 |
| 2 | 010 | 1 | 2 |
| 3 | 011 | 2 | 4 |
| 4 | 100 | 1 | 5 |
| 5 | 101 | 2 | 7 |
| 6 | 110 | 2 | 9 |
| 7 | 111 | 3 | 12 |

Answer:

```text
7
```

### Optimal Idea: Binary Search on Answer

Function:

```text
f(x) = total set bits from 0 to x
```

`f(x)` is non-decreasing.

So binary search smallest `x` where:

```text
f(x) >= k
```

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countOnesAtBit(long long N, int b) {
    long long totalNumbers = N + 1;
    long long block = 1LL << (b + 1);
    long long half = 1LL << b;

    long long fullBlocks = totalNumbers / block;
    long long rem = totalNumbers % block;

    long long ones = fullBlocks * half;
    ones += max(0LL, rem - half);

    return ones;
}

long long totalSetBits(long long N) {
    long long ans = 0;
    for (int b = 0; b <= 60; b++) {
        ans += countOnesAtBit(N, b);
    }
    return ans;
}

long long kthBySetBits(long long k) {
    long long lo = 0, hi = 4e18;
    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (totalSetBits(mid) >= k) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}

int main() {
    cout << kthBySetBits(12) << "\n";
}
```

### Index-by-Index Dry Run with Small Search Space

For learning, use:

```text
lo = 0, hi = 15, k = 12
```

| Step | lo | hi | mid | totalSetBits(mid) | valid? | move |
|---:|---:|---:|---:|---:|---|---|
| 1 | 0 | 15 | 7 | 12 | yes | answer=7, hi=6 |
| 2 | 0 | 6 | 3 | 4 | no | lo=4 |
| 3 | 4 | 6 | 5 | 7 | no | lo=6 |
| 4 | 6 | 6 | 6 | 9 | no | lo=7 |

Final answer:

```text
7
```

### Complexity

```text
totalSetBits(x): O(log x)
Binary search: O(log answer)
Total: O(log^2 answer)
```

---

## 25. Problem 6: Decode Repeated Operation `x = (x & y) + (x | y)`

### Problem Statement

Given two numbers `x` and `y`, operation is:

```text
x = (x & y) + (x | y)
```

Find what happens after applying this operation many times.

### Input

```text
x = 12
y = 10
```

### Expected Output

```text
x becomes x + y = 22 after one operation
```

Because:

```text
x & y = 8
x | y = 14
8 + 14 = 22
```

And:

```text
x + y = 12 + 10 = 22
```

### Brute Force Idea

Actually compute the operation repeatedly.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    long long x = 12, y = 10;

    long long nx = (x & y) + (x | y);

    cout << nx << "\n";
}
```

### Brute Force Result

```text
22
```

### Optimal Idea: Decode the Operation

Identity:

```text
(x & y) + (x | y) = x + y
```

So the operation is just adding `y` to `x`.

If repeated `t` times:

```text
x_final = x + t * y
```

### Bit-by-Bit Proof

For each bit:

| x bit | y bit | x & y | x \| y | total |
|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 |
| 0 | 1 | 0 | 1 | 1 |
| 1 | 0 | 0 | 1 | 1 |
| 1 | 1 | 1 | 1 | 2 |

This is exactly the same contribution as normal addition at bit level.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long afterOperations(long long x, long long y, long long t) {
    return x + t * y;
}

int main() {
    long long x = 12, y = 10, t = 1;
    cout << afterOperations(x, y, t) << "\n";
}
```

### Index-by-Index Dry Run

```text
x = 12 = 1100
y = 10 = 1010
```

| Bit | x | y | AND | OR | AND+OR contribution |
|---:|---:|---:|---:|---:|---:|
| 3 | 1 | 1 | 1 | 1 | 2 * 8 = 16 |
| 2 | 1 | 0 | 0 | 1 | 1 * 4 = 4 |
| 1 | 0 | 1 | 0 | 1 | 1 * 2 = 2 |
| 0 | 0 | 0 | 0 | 0 | 0 |

Total:

```text
16 + 4 + 2 = 22
```

---

## 26. Problem 7: Maximize AND of a Subsequence of Size K

### Problem Statement

Given array `A` and integer `K`, choose exactly `K` numbers such that their bitwise AND is maximum.

### Input

```text
A = [1, 3, 5, 6]
K = 2
```

### Expected Output

```text
4
```

Choose:

```text
5 & 6 = 4
```

### Brute Force Idea

Try all subsequences of size `K`, compute AND.

### Brute Force Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    vector<int> A = {1, 3, 5, 6};
    int K = 2;
    int n = A.size();

    int best = 0;

    for (int mask = 0; mask < (1 << n); mask++) {
        if (__builtin_popcount(mask) != K) continue;

        int cur = -1; // all bits 1 in two's complement
        for (int i = 0; i < n; i++) {
            if (mask & (1 << i)) {
                cur &= A[i];
            }
        }

        best = max(best, cur);
    }

    cout << best << "\n";
}
```

### Brute Force Result

Subsequences of size 2:

| Pair | AND |
|---|---:|
| 1 & 3 | 1 |
| 1 & 5 | 1 |
| 1 & 6 | 0 |
| 3 & 5 | 1 |
| 3 & 6 | 2 |
| 5 & 6 | 4 |

Answer:

```text
4
```

### Optimal Idea: Highest Bit to Lowest Bit

Try to build answer bit-by-bit from high to low.

If we want answer to contain bit `b`, then we need at least `K` numbers that contain all bits in candidate mask.

Condition:

```text
(x & candidate) == candidate
```

If at least `K` numbers satisfy this, candidate is possible.

### C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxAndSubsequence(vector<int>& A, int K) {
    int ans = 0;

    for (int b = 30; b >= 0; b--) {
        int candidate = ans | (1 << b);

        int cnt = 0;
        for (int x : A) {
            if ((x & candidate) == candidate) {
                cnt++;
            }
        }

        if (cnt >= K) {
            ans = candidate;
        }
    }

    return ans;
}

int main() {
    vector<int> A = {1, 3, 5, 6};
    int K = 2;

    cout << maxAndSubsequence(A, K) << "\n";
}
```

### Bit-by-Bit Table

Numbers:

| Number | Binary |
|---:|---|
| 1 | `001` |
| 3 | `011` |
| 5 | `101` |
| 6 | `110` |

We only show bits 2 to 0.

### Dry Run

Start:

```text
ans = 0
```

#### Try bit 2

```text
candidate = 100 = 4
```

Check numbers:

| Number | Binary | Contains `100`? |
|---:|---|---|
| 1 | `001` | no |
| 3 | `011` | no |
| 5 | `101` | yes |
| 6 | `110` | yes |

Count = 2.

Since `count >= K`, keep bit 2.

```text
ans = 100 = 4
```

#### Try bit 1

```text
candidate = 110 = 6
```

Check numbers:

| Number | Binary | Contains `110`? |
|---:|---|---|
| 1 | `001` | no |
| 3 | `011` | no |
| 5 | `101` | no |
| 6 | `110` | yes |

Count = 1.

Since `count < K`, cannot keep bit 1.

```text
ans = 100 = 4
```

#### Try bit 0

```text
candidate = 101 = 5
```

Check numbers:

| Number | Binary | Contains `101`? |
|---:|---|---|
| 1 | `001` | no |
| 3 | `011` | no |
| 5 | `101` | yes |
| 6 | `110` | no |

Count = 1.

Since `count < K`, cannot keep bit 0.

```text
ans = 100 = 4
```

Final answer:

```text
4
```

### Why This Works

Bitwise AND keeps a bit as `1` only if all chosen numbers have that bit as `1`.

So if candidate mask is possible, we need at least `K` numbers that contain all candidate bits.

Highest bit first works because higher bits dominate lower bits.

### Complexity

```text
Time: O(31 * N)
Space: O(1)
```

---

# Final CP + FAANG Bit Manipulation Checklist

## When You See Subset / Mask

Think:

```text
Can each element be take/not-take?
Can I represent state using bits?
Can I iterate masks from 0 to 2^n - 1?
```

## When You See Pair XOR / Pair OR / Pair AND Sum

Think:

```text
Can I calculate contribution per bit?
```

For XOR:

```text
different bits contribute
```

For AND:

```text
both 1 contributes
```

For OR:

```text
at least one 1 contributes
```

## When You See Range 0 to N and Bits

Think:

```text
Cyclic property of bits
Pattern length at bit i = 2^(i+1)
```

## When You See Repeated Bit Operation

Think:

```text
Can I decode the operation?
Is something conserved?
Can I convert it into arithmetic?
```

## When You See Maximize / Minimize Bitwise Answer

Think:

```text
Build answer from highest bit to lowest bit
Try candidate
Check if candidate is possible
```

---

# Quick Templates

## Template 1: Check ith Bit

```cpp
bool isSet(long long x, int i) {
    return (x & (1LL << i)) != 0;
}
```

## Template 2: Generate Subsets

```cpp
for (int mask = 0; mask < (1 << n); mask++) {
    for (int i = 0; i < n; i++) {
        if (mask & (1 << i)) {
            // take arr[i]
        }
    }
}
```

## Template 3: Bit Contribution for XOR Pairs

```cpp
long long ans = 0;

for (int b = 0; b < 31; b++) {
    long long cnt1 = 0;

    for (int x : A) {
        if (x & (1 << b)) cnt1++;
    }

    long long cnt0 = A.size() - cnt1;
    ans += cnt1 * cnt0 * (1LL << b);
}
```

## Template 4: Count Ones at Bit b from 0 to N

```cpp
long long countOnesAtBit(long long N, int b) {
    long long total = N + 1;
    long long block = 1LL << (b + 1);
    long long half = 1LL << b;

    long long full = total / block;
    long long rem = total % block;

    return full * half + max(0LL, rem - half);
}
```

## Template 5: Highest Bit Greedy

```cpp
long long ans = 0;

for (int b = 60; b >= 0; b--) {
    long long candidate = ans | (1LL << b);

    if (can(candidate)) {
        ans = candidate;
    }
}
```

---

# Revision Plan

## Day 1

- Binary representation
- AND, OR, XOR
- shifts
- subset as mask

## Day 2

- generate subsets
- union/intersection
- check/set/clear/toggle bit

## Day 3

- bit contribution technique
- sum XOR pairs
- sum OR pairs
- sum AND pairs

## Day 4

- cyclic property
- count total set bits from 0 to N
- binary search with bit counting

## Day 5

- conservation of bits
- repeated operation decoding
- highest-bit greedy

## Day 6 and 7

- mixed random practice
- identify pattern within 5 seconds

---

# 5-Second Pattern Recognition

| Problem Wording | Pattern |
|---|---|
| subset, choose/not choose, small n | Bitmasking |
| all pairs XOR sum | Bit contribution |
| total set bits from 0 to N | Cyclic property |
| repeated bit operations | Operation decoding |
| maximize AND / OR / XOR | Highest-bit greedy |
| query kth bit / kth one | Binary search + bit count |
| each bit independently | Contribution technique |
| need store subset compactly | Mask as integer |


---

# EXTRA VISUAL SECTION — COLUMN WISE BIT THINKING

# Why Elite CP Programmers Think Column Wise

Instead of thinking:

```text
number by number
```

Think:

```text
bit column by bit column
```

Because:

```text
Each bit works independently
```

This is the foundation behind:

- XOR contribution
- OR contribution
- AND contribution
- Maximum AND
- Maximum XOR
- Bit DP
- Trie problems
- Greedy bit construction

---

# COLUMN WISE REPRESENTATION — MASTER EXAMPLE

Array:

```text
A = [1, 3, 5]
```

Binary:

```text
1 = 001
3 = 011
5 = 101
```

Represent vertically:

| Number | bit2 | bit1 | bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Now solve:

```text
(1 ^ 3) + (1 ^ 5) + (3 ^ 5)
```

WITHOUT directly computing XORs.

---

# COLUMN DRY RUN — BIT 0

Column:

```text
1
1
1
```

Count:

```text
ones = 3
zeros = 0
```

XOR becomes 1 only when bits differ.

Possible pairs:

```text
1 ^ 0
0 ^ 1
```

But here:

```text
all are 1
```

So:

```text
pairs = ones * zeros
      = 3 * 0
      = 0
```

Contribution:

```text
0 * (2^0)
= 0
```

---

# COLUMN DRY RUN — BIT 1

Column:

```text
0
1
0
```

Count:

```text
ones = 1
zeros = 2
```

Valid XOR pairs:

```text
1 with 0
```

Number of valid pairs:

```text
1 * 2 = 2
```

Each contributes:

```text
2^1 = 2
```

Total:

```text
2 * 2 = 4
```

Visual pairs:

| Pair | bit1 XOR |
|---|---|
| (1,3) | 1 |
| (1,5) | 0 |
| (3,5) | 1 |

Contribution:

```text
2 + 0 + 2 = 4
```

---

# COLUMN DRY RUN — BIT 2

Column:

```text
0
0
1
```

Count:

```text
ones = 1
zeros = 2
```

Pairs:

```text
1 * 2 = 2
```

Each contributes:

```text
2^2 = 4
```

Total:

```text
2 * 4 = 8
```

Visual:

| Pair | bit2 XOR |
|---|---|
| (1,3) | 0 |
| (1,5) | 1 |
| (3,5) | 1 |

Contribution:

```text
0 + 4 + 4 = 8
```

---

# FINAL CONTRIBUTION TABLE

| Bit | Ones | Zeros | Valid Pairs | Bit Value | Contribution |
|---|---|---|---|---|---|
| 0 | 3 | 0 | 0 | 1 | 0 |
| 1 | 1 | 2 | 2 | 2 | 4 |
| 2 | 1 | 2 | 2 | 4 | 8 |

Final Answer:

```text
0 + 4 + 8 = 12
```

---

# COLUMN WISE THINKING FOR AND

Example:

```text
A = [1, 3, 5]
```

Binary:

| Number | bit2 | bit1 | bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

For AND:

Bit becomes 1 only if BOTH bits are 1.

---

# BIT 0

Column:

```text
1
1
1
```

All pairs contribute.

Number of valid pairs:

```text
C(3,2) = 3
```

Contribution:

```text
3 * 1 = 3
```

---

# BIT 1

Column:

```text
0
1
0
```

Only one 1.

Need two 1s for AND.

Contribution:

```text
0
```

---

# BIT 2

Column:

```text
0
0
1
```

Only one 1.

Contribution:

```text
0
```

Final AND pair sum:

```text
3
```

---

# COLUMN WISE THINKING FOR OR

OR becomes 1 when:

```text
at least one bit is 1
```

---

# Example

Array:

```text
[1, 3, 5]
```

Binary:

| Number | bit2 | bit1 | bit0 |
|---|---|---|---|
| 1 | 0 | 0 | 1 |
| 3 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 |

Total pairs:

```text
3C2 = 3
```

---

# BIT 0

All numbers have 1.

So every pair contributes.

Contribution:

```text
3 * 1 = 3
```

---

# BIT 1

Column:

```text
0
1
0
```

Only pair without OR contribution:

```text
0 with 0
```

zero pairs:

```text
C(2,2) = 1
```

Valid pairs:

```text
3 - 1 = 2
```

Contribution:

```text
2 * 2 = 4
```

---

# BIT 2

Column:

```text
0
0
1
```

Zero pairs:

```text
C(2,2) = 1
```

Valid pairs:

```text
3 - 1 = 2
```

Contribution:

```text
2 * 4 = 8
```

Final OR pair sum:

```text
3 + 4 + 8 = 15
```

---

# ULTIMATE MENTAL MODEL

Whenever you see:

```text
sum over pairs
sum over subarrays
maximize xor
maximize and
```

ASK:

```text
Can I solve this COLUMN WISE?
```

That is the real breakthrough in Bit Manipulation.
