# Bit Manipulation Complete Notes — CP + FAANG

> Based on your uploaded notes: Bitmasking, BitSet STL, Cyclic Property of Bits, Contribution Technique, Operation Decoding, Highest-to-Lowest Bit, and Bit Manipulation 1/2.

---

## Clickable Index

### Part A — Foundations
1. [Mental Model: Everything is 0/1](#1-mental-model-everything-is-01)
2. [Decimal to Binary Conversion](#2-decimal-to-binary-conversion)
3. [Bit Positions: LSB to MSB](#3-bit-positions-lsb-to-msb)
4. [Core Bit Operators](#4-core-bit-operators)
5. [Shift Operators and Precedence Trap](#5-shift-operators-and-precedence-trap)
6. [Bit Check / Set / Clear / Toggle](#6-bit-check--set--clear--toggle)
7. [C++ `bitset` STL](#7-c-bitset-stl)

### Part B — Bitmasking / Subsets
8. [Representing a Set as an Integer Mask](#8-representing-a-set-as-an-integer-mask)
9. [Union and Intersection of Subsets](#9-union-and-intersection-of-subsets)
10. [Problem 1: Print All Subsets](#10-problem-1-print-all-subsets)

### Part C — Application Ideas
11. [Application 1: Cyclic Property of Bits](#11-application-1-cyclic-property-of-bits)
12. [Problem 2: Count Total Set Bits from 0 to X](#12-problem-2-count-total-set-bits-from-0-to-x)
13. [Problem 3: Find Minimum X Such That Total Set Bits from 0 to X >= K](#13-problem-3-find-minimum-x-such-that-total-set-bits-from-0-to-x--k)
14. [Application 2: Contribution Technique of Bits](#14-application-2-contribution-technique-of-bits)
15. [Problem 4: Sum of XOR of All Pairs](#15-problem-4-sum-of-xor-of-all-pairs)
16. [Application 3: Operation Decoding / Conservation of Bits](#16-application-3-operation-decoding--conservation-of-bits)
17. [Problem 5: Decode Repeated XOR of Shifted Number](#17-problem-5-decode-repeated-xor-of-shifted-number)
18. [Application 4: Highest Bit to Lowest Bit](#18-application-4-highest-bit-to-lowest-bit)
19. [Problem 6: Maximum AND Pair](#19-problem-6-maximum-and-pair)

### Part D — Quick CP Pattern Map
20. [Bit Pattern Recognition Checklist](#20-bit-pattern-recognition-checklist)
21. [Common Mistakes](#21-common-mistakes)
22. [Final Revision Sheet](#22-final-revision-sheet)

---

# 1. Mental Model: Everything is 0/1

Computer stores numbers using bits.

A bit has only two states:

| Meaning | Bit |
|---|---:|
| Not take / off / false | 0 |
| Take / on / true | 1 |

This is why bit manipulation is useful for:

- representing subsets
- checking membership
- fast multiplication/division by powers of 2
- pair/subarray contribution by each bit
- decoding operations where bits are conserved
- greedily building answer from highest bit to lowest bit

---

# 2. Decimal to Binary Conversion

## Example: Convert 13 to binary

Repeatedly divide by 2 and write remainders.

| Step | Number | Number / 2 | Remainder |
|---:|---:|---:|---:|
| 1 | 13 | 6 | 1 |
| 2 | 6 | 3 | 0 |
| 3 | 3 | 1 | 1 |
| 4 | 1 | 0 | 1 |

Read remainders from bottom to top:

```text
13 = 1101₂
```

## Column-wise LSB to MSB

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| Power | 2⁰ | 2¹ | 2² | 2³ |
| Value | 1 | 2 | 4 | 8 |
| Bit of 13 | 1 | 0 | 1 | 1 |

So:

```text
13 = 1*2⁰ + 0*2¹ + 1*2² + 1*2³
   = 1 + 0 + 4 + 8
   = 13
```

---

# 3. Bit Positions: LSB to MSB

## Important powers of 2

| Bit index | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 2^i | 1 | 2 | 4 | 8 | 16 | 32 | 64 | 128 | 256 | 512 | 1024 |

| Bit index | 11 | 12 | 13 | 14 | 15 | 16 |
|---|---:|---:|---:|---:|---:|---:|
| 2^i | 2048 | 4096 | 8192 | 16384 | 32768 | 65536 |

Remember:

```text
2^10 = 1024
2^16 = 65536
2^20 ≈ 10^6
2^30 ≈ 10^9
2^40 ≈ 10^12
2^60 ≈ 10^18
```

---

# 4. Core Bit Operators

Use two numbers:

```text
a = 12 = 1100₂
b = 10 = 1010₂
```

## Column-wise LSB to MSB

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| a = 12 | 0 | 0 | 1 | 1 |
| b = 10 | 0 | 1 | 0 | 1 |

## AND `&`

Rule: 1 only if both bits are 1.

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| a | 0 | 0 | 1 | 1 |
| b | 0 | 1 | 0 | 1 |
| a & b | 0 | 0 | 0 | 1 |

```text
1100 & 1010 = 1000 = 8
```

## OR `|`

Rule: 1 if at least one bit is 1.

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| a | 0 | 0 | 1 | 1 |
| b | 0 | 1 | 0 | 1 |
| a \\| b | 0 | 1 | 1 | 1 |

```text
1100 | 1010 = 1110 = 14
```

## XOR `^`

Rule: 1 if bits are different, 0 if bits are same.

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| a | 0 | 0 | 1 | 1 |
| b | 0 | 1 | 0 | 1 |
| a ^ b | 0 | 1 | 1 | 0 |

```text
1100 ^ 1010 = 0110 = 6
```

Useful XOR facts:

```text
x ^ x = 0
x ^ 0 = x
x ^ y = y ^ x
(a ^ b) ^ c = a ^ (b ^ c)
```

## NOT `~`

`~x` flips all bits.

Important: in C++, `int` is usually 32 bits and `long long` is 64 bits. So `~x` flips all 32/64 bits, not only visible bits.

---

# 5. Shift Operators and Precedence Trap

## Left shift `<<`

```text
x << y = x * 2^y
```

Example:

| Expression | Binary | Decimal |
|---|---|---:|
| 1011 << 1 | 10110 | 22 |
| 1011 << 2 | 101100 | 44 |

## Right shift `>>`

```text
x >> y = floor(x / 2^y)
```

Example:

| Expression | Binary | Decimal |
|---|---|---:|
| 1011 >> 1 | 101 | 5 |
| 1011 >> 2 | 10 | 2 |
| 1011 >> 3 | 1 | 1 |

## Precedence trap

```cpp
1 << 2 + 3
```

`+` has higher precedence than `<<`, so this is:

```cpp
1 << (2 + 3) = 1 << 5 = 32
```

Not:

```cpp
(1 << 2) + 3 = 7
```

Always use parentheses.

---

# 6. Bit Check / Set / Clear / Toggle

Let:

```text
x = 13 = 1101₂
```

Column-wise LSB to MSB:

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| x = 13 | 1 | 0 | 1 | 1 |

## Check i-th bit

```cpp
bool isSet = (x & (1 << i)) != 0;
```

Example: check bit 2.

```text
x        = 1101
1 << 2   = 0100
x & mask = 0100 != 0, so bit 2 is set
```

## Set i-th bit

```cpp
x = x | (1 << i);
```

Example: set bit 1 of 13.

```text
x        = 1101
1 << 1   = 0010
x | mask = 1111 = 15
```

## Clear i-th bit

```cpp
x = x & ~(1 << i);
```

Example: clear bit 2 of 13.

```text
x         = 1101
1 << 2    = 0100
~(1 << 2) = 1011  // considering only 4 visible bits
x & mask  = 1001 = 9
```

## Toggle i-th bit

```cpp
x = x ^ (1 << i);
```

Example: toggle bit 0 of 13.

```text
x        = 1101
1 << 0   = 0001
x ^ mask = 1100 = 12
```

---

# 7. C++ `bitset` STL

`bitset<N>` is fixed-size bit storage.

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    bitset<4> b(13);
    cout << b << "\n";             // 1101
    cout << b[0] << "\n";          // 1, LSB
    cout << b[1] << "\n";          // 0
    cout << b.count() << "\n";     // number of 1 bits
    cout << b.to_string() << "\n"; // string representation
}
```

Important:

| Operation | Meaning |
|---|---|
| `bitset<d> x;` | create d-bit bitset |
| `bitset<d> x(num);` | initialize from number |
| `x[i]` | access bit i, where i=0 is LSB |
| `x.count()` | number of set bits |
| `x.to_string()` | print as binary string MSB to LSB |

---

# 8. Representing a Set as an Integer Mask

Suppose the universal set is:

```text
U = {1, 3, 7, 5, 10}
index: 0  1  2  3   4
```

A subset can be represented by a bitmask.

- bit = 1 means take the element
- bit = 0 means do not take the element

## Example subset `{1, 5, 10}`

Elements by index:

| Index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| Element | 1 | 3 | 7 | 5 | 10 |
| Take? | 1 | 0 | 0 | 1 | 1 |

Column-wise LSB to MSB mask:

| Bit index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| Bit | 1 | 0 | 0 | 1 | 1 |
| Power value | 1 | 2 | 4 | 8 | 16 |

```text
mask = 1*2⁰ + 0*2¹ + 0*2² + 1*2³ + 1*2⁴
     = 1 + 8 + 16
     = 25
```

So subset `{1,5,10}` is represented by integer `25`.

## Example subset `{3,7,5}`

| Index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| Element | 1 | 3 | 7 | 5 | 10 |
| Take? | 0 | 1 | 1 | 1 | 0 |

```text
mask = 0*1 + 1*2 + 1*4 + 1*8 + 0*16 = 14
```

---

# 9. Union and Intersection of Subsets

Let:

```text
A = {1, 5, 10} -> mask 25 = 11001₂
B = {3, 7, 5} -> mask 14 = 01110₂
```

## Column-wise LSB to MSB

| Bit index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| Element | 1 | 3 | 7 | 5 | 10 |
| A mask | 1 | 0 | 0 | 1 | 1 |
| B mask | 0 | 1 | 1 | 1 | 0 |

## Union: use OR

```text
A ∪ B = A | B
```

| Bit index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| A | 1 | 0 | 0 | 1 | 1 |
| B | 0 | 1 | 1 | 1 | 0 |
| A OR B | 1 | 1 | 1 | 1 | 1 |

```text
25 | 14 = 31
Subset = {1,3,7,5,10}
```

## Intersection: use AND

```text
A ∩ B = A & B
```

| Bit index | 0 | 1 | 2 | 3 | 4 |
|---|---:|---:|---:|---:|---:|
| A | 1 | 0 | 0 | 1 | 1 |
| B | 0 | 1 | 1 | 1 | 0 |
| A AND B | 0 | 0 | 0 | 1 | 0 |

```text
25 & 14 = 8
Subset = {5}
```

---

# 10. Problem 1: Print All Subsets

## Problem Statement

Given an array of `n` distinct elements, print all subsets.

## Input

```text
n = 3
arr = [1, 3, 7]
```

## Expected Output

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

Order can differ.

## Observation

For every element, we have 2 choices:

1. Take it
2. Do not take it

So total subsets:

```text
2 * 2 * 2 ... n times = 2^n
```

## Bitmask Idea

Run mask from `0` to `(1 << n) - 1`.

Each mask tells which elements are taken.

## Bit-by-bit table: LSB to MSB

For `arr = [1,3,7]`:

| Mask decimal | bit0 for 1 | bit1 for 3 | bit2 for 7 | Subset |
|---:|---:|---:|---:|---|
| 0 | 0 | 0 | 0 | `{}` |
| 1 | 1 | 0 | 0 | `{1}` |
| 2 | 0 | 1 | 0 | `{3}` |
| 3 | 1 | 1 | 0 | `{1,3}` |
| 4 | 0 | 0 | 1 | `{7}` |
| 5 | 1 | 0 | 1 | `{1,7}` |
| 6 | 0 | 1 | 1 | `{3,7}` |
| 7 | 1 | 1 | 1 | `{1,3,7}` |

## Brute Force Idea

Use recursion: at each index choose take / not take.

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
        cout << "}" << '\n';
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

## Optimal Bitmask Code

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

        cout << "}" << '\n';
    }
}
```

## Index-by-index dry run

`arr = [1,3,7]`

### mask = 5

```text
5 = 101₂
```

Column-wise LSB to MSB:

| i | arr[i] | 1 << i | mask & (1 << i) | Take? |
|---:|---:|---:|---:|---|
| 0 | 1 | 001 | 001 | yes |
| 1 | 3 | 010 | 000 | no |
| 2 | 7 | 100 | 100 | yes |

Result:

```text
{1, 7}
```

---

# 11. Application 1: Cyclic Property of Bits

When numbers increase from `0,1,2,3,...`, every bit follows a cycle.

For bit `i`:

```text
cycle length = 2^(i+1)
zero block length = 2^i
one block length  = 2^i
```

## Example numbers 0 to 7

Column-wise LSB to MSB:

| Number | bit0 | bit1 | bit2 |
|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 |
| 1 | 1 | 0 | 0 |
| 2 | 0 | 1 | 0 |
| 3 | 1 | 1 | 0 |
| 4 | 0 | 0 | 1 |
| 5 | 1 | 0 | 1 |
| 6 | 0 | 1 | 1 |
| 7 | 1 | 1 | 1 |

Pattern:

| Bit | Pattern | Repeats every |
|---:|---|---:|
| bit0 | 0,1,0,1,... | 2 |
| bit1 | 0,0,1,1,... | 4 |
| bit2 | 0,0,0,0,1,1,1,1,... | 8 |

---

# 12. Problem 2: Count Total Set Bits from 0 to X

## Problem Statement

Given `x`, count the total number of `1` bits in binary representation of all numbers from `0` to `x`.

## Input

```text
x = 7
```

## Expected Output

```text
12
```

Because:

| Number | bit0 | bit1 | bit2 | set bits |
|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 |
| 1 | 1 | 0 | 0 | 1 |
| 2 | 0 | 1 | 0 | 1 |
| 3 | 1 | 1 | 0 | 2 |
| 4 | 0 | 0 | 1 | 1 |
| 5 | 1 | 0 | 1 | 2 |
| 6 | 0 | 1 | 1 | 2 |
| 7 | 1 | 1 | 1 | 3 |
| **Total** | 4 | 4 | 4 | **12** |

## Brute Force

Count set bits for every number.

```cpp
long long bruteCountSetBits(long long x) {
    long long ans = 0;
    for (long long num = 0; num <= x; num++) {
        ans += __builtin_popcountll(num);
    }
    return ans;
}
```

Complexity:

```text
O(x log x)
```

## Optimal Idea: Count contribution of each bit

For each bit `i`, count how many numbers in `[0..x]` have bit `i` set.

For bit `i`:

```text
block = 2^(i+1)
ones = 2^i per full block
```

Let:

```text
N = x + 1  // count of numbers from 0 to x
fullBlocks = N / block
remainder = N % block
extraOnes = max(0, remainder - 2^i)
countOnesAtBitI = fullBlocks * 2^i + extraOnes
```

## Example: x = 13

Numbers count:

```text
N = 14 numbers: 0..13
```

Column-wise result:

| Bit i | 2^i | Block length 2^(i+1) | Full blocks | Remainder | Ones from full blocks | Extra ones | Total ones |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 0 | 1 | 2 | 7 | 0 | 7 | 0 | 7 |
| 1 | 2 | 4 | 3 | 2 | 6 | 0 | 6 |
| 2 | 4 | 8 | 1 | 6 | 4 | 2 | 6 |
| 3 | 8 | 16 | 0 | 14 | 0 | 6 | 6 |

Total:

```text
7 + 6 + 6 + 6 = 25
```

## Optimal Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSetBitsFrom0ToX(long long x) {
    long long n = x + 1; // numbers count: 0..x
    long long ans = 0;

    for (int i = 0; i < 63; i++) {
        long long half = 1LL << i;
        long long block = 1LL << (i + 1);

        long long fullBlocks = n / block;
        long long rem = n % block;

        long long ones = fullBlocks * half + max(0LL, rem - half);
        ans += ones;
    }

    return ans;
}

int main() {
    cout << countSetBitsFrom0ToX(13) << '\n'; // 25
}
```

## Index-by-index dry run for x = 13

| i | half | block | n/block | n%block | Formula | ones |
|---:|---:|---:|---:|---:|---|---:|
| 0 | 1 | 2 | 7 | 0 | 7*1 + max(0,0-1) | 7 |
| 1 | 2 | 4 | 3 | 2 | 3*2 + max(0,2-2) | 6 |
| 2 | 4 | 8 | 1 | 6 | 1*4 + max(0,6-4) | 6 |
| 3 | 8 | 16 | 0 | 14 | 0*8 + max(0,14-8) | 6 |

---

# 13. Problem 3: Find Minimum X Such That Total Set Bits from 0 to X >= K

## Problem Statement

Given `K`, find the smallest `x` such that total number of set bits in all numbers from `0` to `x` is at least `K`.

## Input

```text
K = 12
```

## Expected Output

```text
7
```

Because total set bits from `0..7` is `12`.

## Observation

Function:

```text
f(x) = total set bits from 0 to x
```

is non-decreasing.

If `f(x) >= K`, then every bigger number may also satisfy.

So we can binary search on answer.

## Brute Force

```cpp
long long brute(long long k) {
    long long sum = 0;
    for (long long x = 0; ; x++) {
        sum += __builtin_popcountll(x);
        if (sum >= k) return x;
    }
}
```

## Optimal Idea

Use:

1. `countSetBitsFrom0ToX(x)` as check function
2. binary search for first `x` where check is true

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long countSetBitsFrom0ToX(long long x) {
    long long n = x + 1;
    long long ans = 0;

    for (int i = 0; i < 63; i++) {
        long long half = 1LL << i;
        long long block = 1LL << (i + 1);
        long long fullBlocks = n / block;
        long long rem = n % block;
        ans += fullBlocks * half + max(0LL, rem - half);
    }

    return ans;
}

long long minXForAtLeastKSetBits(long long k) {
    long long lo = 0, hi = 1;

    while (countSetBitsFrom0ToX(hi) < k) {
        hi *= 2;
    }

    long long ans = hi;
    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (countSetBitsFrom0ToX(mid) >= k) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}

int main() {
    cout << minXForAtLeastKSetBits(12) << '\n'; // 7
}
```

## Dry run: K = 12

Known values:

| x | f(x) |
|---:|---:|
| 0 | 0 |
| 1 | 1 |
| 2 | 2 |
| 3 | 4 |
| 4 | 5 |
| 5 | 7 |
| 6 | 9 |
| 7 | 12 |

Binary search example with `lo=0, hi=8`:

| Step | lo | hi | mid | f(mid) | Decision |
|---:|---:|---:|---:|---:|---|
| 1 | 0 | 8 | 4 | 5 | too small, lo=5 |
| 2 | 5 | 8 | 6 | 9 | too small, lo=7 |
| 3 | 7 | 8 | 7 | 12 | valid, ans=7, hi=6 |

Answer:

```text
7
```

---

# 14. Application 2: Contribution Technique of Bits

Many bit problems become easy when solved independently for each bit.

Key idea:

```text
Total answer = sum of contribution from bit0 + bit1 + bit2 + ...
```

For XOR:

```text
0 ^ 0 = 0
1 ^ 1 = 0
0 ^ 1 = 1
1 ^ 0 = 1
```

So XOR gives 1 only when bits are different.

---

# 15. Problem 4: Sum of XOR of All Pairs

## Problem Statement

Given array `A`, compute:

```text
sum of A[i] ^ A[j] for all i < j
```

## Input

```text
A = [1, 3, 5]
```

## Expected Output

```text
12
```

Because:

```text
1 ^ 3 = 2
1 ^ 5 = 4
3 ^ 5 = 6
Total = 12
```

## Bit-by-bit table: LSB to MSB

Binary:

```text
1 = 001
3 = 011
5 = 101
```

| Bit index | 0 | 1 | 2 |
|---|---:|---:|---:|
| 1 | 1 | 0 | 0 |
| 3 | 1 | 1 | 0 |
| 5 | 1 | 0 | 1 |
| Count of 1s | 3 | 1 | 1 |
| Count of 0s | 0 | 2 | 2 |

For each bit:

```text
pairs with different bit = countOnes * countZeros
contribution = countOnes * countZeros * 2^bit
```

| Bit | ones | zeros | different pairs | bit value | contribution |
|---:|---:|---:|---:|---:|---:|
| 0 | 3 | 0 | 0 | 1 | 0 |
| 1 | 1 | 2 | 2 | 2 | 4 |
| 2 | 1 | 2 | 2 | 4 | 8 |
| **Total** |  |  |  |  | **12** |

## Brute Force Code

```cpp
long long brutePairXorSum(vector<int>& a) {
    long long ans = 0;
    int n = a.size();

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            ans += (a[i] ^ a[j]);
        }
    }

    return ans;
}
```

Complexity:

```text
O(n^2)
```

## Optimal Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long pairXorSum(vector<int>& a) {
    long long ans = 0;
    int n = a.size();

    for (int bit = 0; bit < 31; bit++) {
        long long ones = 0;

        for (int x : a) {
            if (x & (1 << bit)) ones++;
        }

        long long zeros = n - ones;
        ans += ones * zeros * (1LL << bit);
    }

    return ans;
}

int main() {
    vector<int> a = {1, 3, 5};
    cout << pairXorSum(a) << '\n'; // 12
}
```

## Index-by-index dry run

`A = [1,3,5]`

### bit = 0

| index | value | bit0 |
|---:|---:|---:|
| 0 | 1 | 1 |
| 1 | 3 | 1 |
| 2 | 5 | 1 |

```text
ones=3, zeros=0, contribution=3*0*1=0
```

### bit = 1

| index | value | bit1 |
|---:|---:|---:|
| 0 | 1 | 0 |
| 1 | 3 | 1 |
| 2 | 5 | 0 |

```text
ones=1, zeros=2, contribution=1*2*2=4
```

### bit = 2

| index | value | bit2 |
|---:|---:|---:|
| 0 | 1 | 0 |
| 1 | 3 | 0 |
| 2 | 5 | 1 |

```text
ones=1, zeros=2, contribution=1*2*4=8
```

Final:

```text
0 + 4 + 8 = 12
```

---

# 16. Application 3: Operation Decoding / Conservation of Bits

Sometimes after operations, some bit-level quantity is conserved.

Your notes highlight this identity:

```text
a + b = (a | b) + (a & b)
```

## Why this works bit by bit

For each bit:

| a bit | b bit | a+b at this bit meaning | a OR b | a AND b | OR + AND contribution |
|---:|---:|---|---:|---:|---|
| 0 | 0 | no value | 0 | 0 | 0 |
| 0 | 1 | one value | 1 | 0 | 1 |
| 1 | 0 | one value | 1 | 0 | 1 |
| 1 | 1 | two values/carry effect | 1 | 1 | 2 units |

So `(a|b)` keeps one copy of every set bit, and `(a&b)` keeps the extra copy when both have 1.

Example:

```text
a = 12 = 1100
b = 10 = 1010
```

Column-wise LSB to MSB:

| Bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| a | 0 | 0 | 1 | 1 |
| b | 0 | 1 | 0 | 1 |
| a OR b | 0 | 1 | 1 | 1 |
| a AND b | 0 | 0 | 0 | 1 |

```text
a | b = 1110 = 14
a & b = 1000 = 8
(a|b) + (a&b) = 14 + 8 = 22
a + b = 12 + 10 = 22
```

## Pattern Recognition

When a problem says:

```text
Apply operation many times.
Find final value / invariant / possible value.
```

Ask:

1. What happens to each bit?
2. Is count of 1s preserved?
3. Is sum preserved?
4. Is OR/AND/XOR preserved?
5. Can I reverse the operation using XOR properties?

---

# 17. Problem 5: Decode Repeated XOR of Shifted Number

## Problem Statement

Given:

```text
y = x ^ (x << 1) ^ (x << 2) ^ ... ^ (x << k)
```

Recover `x`.

## Input

```text
y = 110001₂
k = 2
```

## Expected Output

```text
x = 1011₂ = 11
```

Because:

```text
x       = 001011
x << 1  = 010110
x << 2  = 101100
XOR     = 110001
```

## Bit-by-bit observation

Let `x` bits from low to high be:

```text
x = ... d c b a   // a is bit0, b is bit1, c is bit2, d is bit3
```

For `k=2`:

```text
y = x ^ (x << 1) ^ (x << 2)
```

Each bit of `y` becomes XOR of nearby lower bits of `x`.

From LSB upward:

```text
y0 = x0
y1 = x1 ^ x0
y2 = x2 ^ x1 ^ x0
y3 = x3 ^ x2 ^ x1
...
```

So if we process from low bit to high bit, we can recover `x`.

## Brute Force

Try all possible `x`.

```cpp
long long bruteDecode(long long y, int k, int maxBits) {
    long long limit = 1LL << maxBits;

    for (long long x = 0; x < limit; x++) {
        long long cur = 0;
        for (int s = 0; s <= k; s++) {
            cur ^= (x << s);
        }
        if (cur == y) return x;
    }

    return -1;
}
```

## Optimal Idea

Recover bits from LSB to MSB.

For bit `i`:

```text
y_i = x_i ^ x_{i-1} ^ x_{i-2} ... x_{i-k}
```

So:

```text
x_i = y_i ^ x_{i-1} ^ x_{i-2} ... x_{i-k}
```

because XORing the known previous bits cancels them.

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

long long decodeShiftXor(long long y, int k) {
    vector<int> xb(64, 0);

    for (int i = 0; i < 64; i++) {
        int yi = (y >> i) & 1;
        int val = yi;

        for (int back = 1; back <= k; back++) {
            if (i - back >= 0) {
                val ^= xb[i - back];
            }
        }

        xb[i] = val;
    }

    long long x = 0;
    for (int i = 0; i < 63; i++) {
        if (xb[i]) x |= (1LL << i);
    }

    return x;
}

int main() {
    long long y = 0b110001;
    int k = 2;
    cout << decodeShiftXor(y, k) << '\n'; // 11
}
```

## Index-by-index dry run

`y = 110001₂`, LSB to MSB:

| Bit index | 0 | 1 | 2 | 3 | 4 | 5 |
|---|---:|---:|---:|---:|---:|---:|
| y bit | 1 | 0 | 0 | 0 | 1 | 1 |

For `k=2`:

| i | Formula | Calculation | x_i |
|---:|---|---|---:|
| 0 | x0 = y0 | 1 | 1 |
| 1 | x1 = y1 ^ x0 | 0 ^ 1 | 1 |
| 2 | x2 = y2 ^ x1 ^ x0 | 0 ^ 1 ^ 1 | 0 |
| 3 | x3 = y3 ^ x2 ^ x1 | 0 ^ 0 ^ 1 | 1 |
| 4 | x4 = y4 ^ x3 ^ x2 | 1 ^ 1 ^ 0 | 0 |
| 5 | x5 = y5 ^ x4 ^ x3 | 1 ^ 0 ^ 1 | 0 |

Recovered `x` bits LSB to MSB:

| Bit index | 0 | 1 | 2 | 3 | 4 | 5 |
|---|---:|---:|---:|---:|---:|---:|
| x bit | 1 | 1 | 0 | 1 | 0 | 0 |

So:

```text
x = 001011₂ = 11
```

---

# 18. Application 4: Highest Bit to Lowest Bit

When we want to maximize an answer bitwise, build from highest bit to lowest bit.

Why?

```text
One 1 at a higher bit is more powerful than all lower bits combined.
```

Example:

```text
2^3 = 8
2^2 + 2^1 + 2^0 = 7
```

So bit 3 being 1 is better than all lower bits being 1.

## General greedy pattern

```cpp
ans = 0;
for (bit = HIGH; bit >= 0; bit--) {
    candidate = ans | (1 << bit);
    if (candidate is possible) {
        ans = candidate;
    }
}
```

---

# 19. Problem 6: Maximum AND Pair

## Problem Statement

Given an array, find maximum value of `A[i] & A[j]` for any pair `i < j`.

## Input

```text
A = [1, 3, 5, 6]
```

## Expected Output

```text
4
```

Because:

```text
5 & 6 = 4
```

## Bit-by-bit table: LSB to MSB

| Number | bit0 | bit1 | bit2 |
|---:|---:|---:|---:|
| 1 | 1 | 0 | 0 |
| 3 | 1 | 1 | 0 |
| 5 | 1 | 0 | 1 |
| 6 | 0 | 1 | 1 |

Pair AND values:

| Pair | Binary AND | Decimal |
|---|---|---:|
| 1 & 3 | 001 & 011 = 001 | 1 |
| 1 & 5 | 001 & 101 = 001 | 1 |
| 1 & 6 | 001 & 110 = 000 | 0 |
| 3 & 5 | 011 & 101 = 001 | 1 |
| 3 & 6 | 011 & 110 = 010 | 2 |
| 5 & 6 | 101 & 110 = 100 | 4 |

## Brute Force Code

```cpp
int bruteMaxAndPair(vector<int>& a) {
    int ans = 0;
    int n = a.size();

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            ans = max(ans, a[i] & a[j]);
        }
    }

    return ans;
}
```

Complexity:

```text
O(n^2)
```

## Optimal Idea

For a bit to be present in pair AND, at least two numbers must have that bit.

Build answer from highest bit to lowest bit.

At each step:

```text
candidate = ans with current bit turned on
```

Check how many numbers contain all bits of candidate:

```text
(x & candidate) == candidate
```

If count >= 2, candidate is possible.

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int maxAndPair(vector<int>& a) {
    int ans = 0;

    for (int bit = 30; bit >= 0; bit--) {
        int candidate = ans | (1 << bit);
        int count = 0;

        for (int x : a) {
            if ((x & candidate) == candidate) {
                count++;
            }
        }

        if (count >= 2) {
            ans = candidate;
        }
    }

    return ans;
}

int main() {
    vector<int> a = {1, 3, 5, 6};
    cout << maxAndPair(a) << '\n'; // 4
}
```

## Dry run for A = [1,3,5,6]

Use only bits 2,1,0.

### Start

```text
ans = 0
```

### Try bit 2

```text
candidate = 100₂ = 4
```

| x | binary | (x & 4) == 4? |
|---:|---|---|
| 1 | 001 | no |
| 3 | 011 | no |
| 5 | 101 | yes |
| 6 | 110 | yes |

Count = 2, so possible.

```text
ans = 4
```

### Try bit 1

```text
candidate = 110₂ = 6
```

| x | binary | (x & 6) == 6? |
|---:|---|---|
| 1 | 001 | no |
| 3 | 011 | no |
| 5 | 101 | no |
| 6 | 110 | yes |

Count = 1, not possible.

```text
ans remains 4
```

### Try bit 0

```text
candidate = 101₂ = 5
```

| x | binary | (x & 5) == 5? |
|---:|---|---|
| 1 | 001 | no |
| 3 | 011 | no |
| 5 | 101 | yes |
| 6 | 110 | no |

Count = 1, not possible.

```text
ans = 4
```

Final answer:

```text
4
```

---

# 20. Bit Pattern Recognition Checklist

## When you see subsets

Think:

```text
mask from 0 to (1<<n)-1
bit i tells whether arr[i] is taken
```

## When you see pair XOR / pair AND / pair OR sum

Think:

```text
Can I compute contribution bit by bit?
```

For XOR pair sum:

```text
ones * zeros * 2^bit
```

## When you see count bits from 1 to N or 0 to X

Think:

```text
cyclic pattern of each bit
0/1 blocks of length 2^i
cycle length 2^(i+1)
```

## When you see maximum/minimum bitwise answer

Think:

```text
try highest bit first
check if candidate is possible
```

## When you see repeated operations

Think:

```text
what is conserved?
can I decode from LSB to MSB or MSB to LSB?
```

## When you see queries on bit ranges

Think:

```text
prefix count per bit
segment tree per bit
lazy propagation per bit
```

---

# 21. Common Mistakes

## Mistake 1: Thinking bits are written LSB first in normal binary string

Normal binary string is written MSB to LSB:

```text
13 = 1101
```

But bit index table is easier LSB to MSB:

| bit index | 0 | 1 | 2 | 3 |
|---|---:|---:|---:|---:|
| bit | 1 | 0 | 1 | 1 |

## Mistake 2: Forgetting parentheses

Wrong:

```cpp
if (x & 1 << i == 0)
```

Correct:

```cpp
if ((x & (1 << i)) == 0)
```

## Mistake 3: Integer overflow in shifts

Use `1LL` for large shifts:

```cpp
1LL << i
```

not:

```cpp
1 << i
```

when `i >= 31`.

## Mistake 4: Using distributive law wrongly for XOR

This is not generally true:

```text
a ^ (b + c) != (a ^ b) + (a ^ c)
```

But XOR has associative and commutative properties:

```text
(a ^ b) ^ c = a ^ (b ^ c)
a ^ b = b ^ a
```

---

# 22. Final Revision Sheet

## Operators

| Operator | Meaning | Example |
|---|---|---|
| `&` | AND | both 1 -> 1 |
| `|` | OR | at least one 1 -> 1 |
| `^` | XOR | different bits -> 1 |
| `~` | NOT | flips all bits |
| `<<` | left shift | multiply by 2^k |
| `>>` | right shift | divide by 2^k |

## Must-know formulas

```cpp
// Check bit
(x & (1LL << i)) != 0

// Set bit
x | (1LL << i)

// Clear bit
x & ~(1LL << i)

// Toggle bit
x ^ (1LL << i)

// Count set bits
__builtin_popcountll(x)

// Pair XOR contribution
ones * zeros * (1LL << bit)

// Count ones at bit i from 0..x
n = x + 1
half = 1LL << i
block = 1LL << (i + 1)
ones = (n / block) * half + max(0LL, n % block - half)
```

## CP/FAANG bit learning order

1. Binary conversion and bit positions
2. Check/set/clear/toggle
3. Bitmask subset generation
4. Bitset STL
5. Cyclic bit counting
6. Pair contribution technique
7. Highest-to-lowest greedy
8. Operation decoding/invariants
9. Prefix count per bit
10. Segment tree/lazy per bit

---

## Practice Problems to Add Next

Use this template for each future problem:

```text
Problem Statement
Input
Expected Output
Brute Force
Bit-by-bit table LSB -> MSB
Observation
Optimal Idea
C++ Code
Index-by-index dry run
Mistakes
Pattern tag
```

Recommended pattern tags:

- `BITMASK_SUBSET`
- `BIT_CONTRIBUTION`
- `CYCLIC_BITS`
- `HIGHEST_TO_LOWEST_GREEDY`
- `XOR_DECODING`
- `BIT_PREFIX_SUM`
- `BIT_RANGE_QUERY`
