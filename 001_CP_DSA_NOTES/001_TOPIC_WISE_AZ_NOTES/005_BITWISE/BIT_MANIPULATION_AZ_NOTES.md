# Bit Manipulation Complete Notes — Basic to Advanced CP + FAANG

> Style: detailed handbook like sliding-window notes  
> Goal: learn concept → pattern → brute force → optimal idea → C++ → bit-by-bit dry run  
> Source: your handwritten bit manipulation notes.

---

# Clickable Index

## Part A — Foundations

1. [What is Bit Manipulation?](#1-what-is-bit-manipulation)
2. [Decimal to Binary Conversion](#2-decimal-to-binary-conversion)
3. [Bitwise Operators](#3-bitwise-operators)
4. [Shift Operators](#4-shift-operators)
5. [Must-Remember Powers of Two](#5-must-remember-powers-of-two)
6. [Check / Set / Clear / Toggle ith Bit](#6-check--set--clear--toggle-ith-bit)
7. [Count Set Bits](#7-count-set-bits)
8. [Power of Two Check](#8-power-of-two-check)

## Part B — Bitmasking and Sets

9. [Representing a Set as an Integer](#9-representing-a-set-as-an-integer)
10. [Union and Intersection of Sets](#10-union-and-intersection-of-sets)
11. [Generate All Subsets](#11-generate-all-subsets)
12. [Subset Membership using Mask](#12-subset-membership-using-mask)

## Part C — XOR Core

13. [XOR Properties](#13-xor-properties)
14. [Problem 1 — Single Number](#14-problem-1--single-number)
15. [Problem 2 — Missing Number](#15-problem-2--missing-number)
16. [Problem 3 — Two Unique Numbers](#16-problem-3--two-unique-numbers)

## Part D — Bit Contribution Technique

17. [Why Bit Contribution Works](#17-why-bit-contribution-works)
18. [Problem 4 — Sum of XOR of All Pairs](#18-problem-4--sum-of-xor-of-all-pairs)
19. [Problem 5 — Sum of OR of All Pairs](#19-problem-5--sum-of-or-of-all-pairs)
20. [Problem 6 — Sum of AND of All Pairs](#20-problem-6--sum-of-and-of-all-pairs)

## Part E — Cyclic Property of Bits

21. [Cyclic Property Mental Model](#21-cyclic-property-mental-model)
22. [Problem 7 — Count Set Bits from 1 to N](#22-problem-7--count-set-bits-from-1-to-n)
23. [Problem 8 — Kth One in Infinite Binary String](#23-problem-8--kth-one-in-infinite-binary-string)

## Part F — Operation Decoding / Conservation of Bits

24. [Conservation of Bits](#24-conservation-of-bits)
25. [Problem 9 — Decode Addition using OR and AND](#25-problem-9--decode-addition-using-or-and-and)
26. [Problem 10 — XOR Shift Expression](#26-problem-10--xor-shift-expression)

## Part G — Highest Bit to Lowest Bit

27. [Highest Bit Greedy Mental Model](#27-highest-bit-greedy-mental-model)
28. [Problem 11 — Maximum AND Subset](#28-problem-11--maximum-and-subset)
29. [Problem 12 — Maximum XOR Pair](#29-problem-12--maximum-xor-pair)

## Part H — STL Bitset

30. [C++ STL bitset](#30-c-stl-bitset)
31. [Problem 13 — Print Binary using bitset](#31-problem-13--print-binary-using-bitset)

## Part I — Final Pattern Map

32. [Recognition Signals](#32-recognition-signals)
33. [CP + FAANG Problem Ladder](#33-cp--faang-problem-ladder)
34. [Final Revision Sheet](#34-final-revision-sheet)

---

# 1. What is Bit Manipulation?

Everything inside a computer is stored using bits.

A bit has only two values:

| Value | Meaning |
|---|---|
| 0 | off / false / not selected |
| 1 | on / true / selected |

Bit manipulation means using operators already provided by the computer to directly work with bits.

---

## Why CP and FAANG use bits

Bit manipulation is useful when:

| Problem Signal | Bit Idea |
|---|---|
| choose / not choose | mask |
| subset of small n | bitmask |
| unique element | XOR |
| pair XOR / OR / AND sum | contribution per bit |
| count set bits from 1 to n | cyclic property |
| maximize XOR / AND | highest bit greedy |
| repeated operations preserve bits | conservation of bits |
| small state DP | bitmask DP |

---

# 2. Decimal to Binary Conversion

## Problem Statement

Given an integer `n`, convert it to binary representation.

---

## Input

```text
13
```

## Expected Output

```text
1101
```

---

## Brute Force Idea

Repeatedly divide the number by 2.

Each remainder becomes one binary digit.

Important: remainders come from right to left, so reverse at the end.

---

## Bit-by-Bit Example

13 divided by 2:

| Step | Current n | n / 2 | Remainder |
|---|---:|---:|---:|
| 1 | 13 | 6 | 1 |
| 2 | 6 | 3 | 0 |
| 3 | 3 | 1 | 1 |
| 4 | 1 | 0 | 1 |

Remainders from top to bottom:

```text
1 0 1 1
```

Reverse:

```text
1101
```

---

## Why 1101 is 13

| Binary Digit | Power | Value |
|---|---:|---:|
| 1 | 2^3 | 8 |
| 1 | 2^2 | 4 |
| 0 | 2^1 | 0 |
| 1 | 2^0 | 1 |

Total:

```text
8 + 4 + 0 + 1 = 13
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

string toBinary(long long n) {
    if (n == 0) return "0";

    string ans = "";

    while (n > 0) {
        int rem = n % 2;
        ans.push_back(char('0' + rem));
        n /= 2;
    }

    reverse(ans.begin(), ans.end());
    return ans;
}

int main() {
    long long n;
    cin >> n;

    cout << toBinary(n) << "\n";
}
```

---

## Index-by-Index Dry Run

Input:

```text
n = 13
```

| Iteration | n before | rem = n % 2 | ans before reverse | n after |
|---|---:|---:|---|---:|
| 1 | 13 | 1 | 1 | 6 |
| 2 | 6 | 0 | 10 | 3 |
| 3 | 3 | 1 | 101 | 1 |
| 4 | 1 | 1 | 1011 | 0 |

Reverse:

```text
1011 -> 1101
```

---

# 3. Bitwise Operators

## Operator Table

| Operator | Name | Meaning |
|---|---|---|
| `&` | AND | 1 only if both bits are 1 |
| `|` | OR | 1 if at least one bit is 1 |
| `^` | XOR | 1 if bits are different |
| `~` | NOT | flips all bits |
| `<<` | left shift | multiply by power of 2 |
| `>>` | right shift | divide by power of 2 |

---

## AND Example

```text
12 = 1100
10 = 1010
---------
&  = 1000 = 8
```

Bit table:

| Bit Position | 12 | 10 | AND |
|---|---:|---:|---:|
| 3 | 1 | 1 | 1 |
| 2 | 1 | 0 | 0 |
| 1 | 0 | 1 | 0 |
| 0 | 0 | 0 | 0 |

---

## OR Example

```text
12 = 1100
10 = 1010
---------
|  = 1110 = 14
```

| Bit Position | 12 | 10 | OR |
|---|---:|---:|---:|
| 3 | 1 | 1 | 1 |
| 2 | 1 | 0 | 1 |
| 1 | 0 | 1 | 1 |
| 0 | 0 | 0 | 0 |

---

## XOR Example

```text
12 = 1100
10 = 1010
---------
^  = 0110 = 6
```

| Bit Position | 12 | 10 | XOR |
|---|---:|---:|---:|
| 3 | 1 | 1 | 0 |
| 2 | 1 | 0 | 1 |
| 1 | 0 | 1 | 1 |
| 0 | 0 | 0 | 0 |

---

# 4. Shift Operators

# Left Shift

## Formula

```cpp
x << y = x * 2^y
```

---

## Example

```cpp
11 << 1
```

Binary:

```text
1011 << 1 = 10110
```

Decimal:

```text
11 * 2 = 22
```

---

## Another Example

```cpp
11 << 2
```

Binary:

```text
1011 << 2 = 101100
```

Decimal:

```text
11 * 4 = 44
```

---

# Right Shift

## Formula

```cpp
x >> y = floor(x / 2^y)
```

---

## Example

```cpp
11 >> 1
```

Binary:

```text
1011 >> 1 = 101
```

Decimal:

```text
5
```

---

## Example 2

```cpp
11 >> 2
```

Binary:

```text
1011 >> 2 = 10
```

Decimal:

```text
2
```

---

## Operator Priority Trap

Question:

```cpp
1 << 2 + 3
```

Correct interpretation:

```cpp
1 << (2 + 3)
```

Because `+` has higher precedence than `<<`.

Answer:

```text
1 << 5 = 32
```

---

# 5. Must-Remember Powers of Two

| k | 2^k |
|---:|---:|
| 0 | 1 |
| 1 | 2 |
| 2 | 4 |
| 3 | 8 |
| 4 | 16 |
| 5 | 32 |
| 6 | 64 |
| 7 | 128 |
| 8 | 256 |
| 9 | 512 |
| 10 | 1024 |
| 11 | 2048 |
| 12 | 4096 |
| 13 | 8192 |
| 14 | 16384 |
| 15 | 32768 |
| 16 | 65536 |
| 20 | about 10^6 |
| 30 | about 10^9 |

---

# 6. Check / Set / Clear / Toggle ith Bit

Given integer `x`.

We use mask:

```cpp
1 << i
```

---

## Check ith Bit

```cpp
bool isSet = x & (1 << i);
```

---

## Set ith Bit

```cpp
x = x | (1 << i);
```

---

## Clear ith Bit

```cpp
x = x & ~(1 << i);
```

---

## Toggle ith Bit

```cpp
x = x ^ (1 << i);
```

---

## Example

x = 13

Binary:

```text
1101
```

Check bit 2:

```text
1101
0100
----
0100
```

Bit 2 is set.

---

# 7. Count Set Bits

## Problem Statement

Given integer `n`, count how many 1 bits exist in its binary representation.

---

## Input

```text
13
```

## Expected Output

```text
3
```

Because:

```text
13 = 1101
```

---

## Brute Force

Check every bit from 0 to 31.

---

## Code

```cpp
int countBits(int n) {
    int cnt = 0;

    for (int bit = 0; bit < 31; bit++) {
        if (n & (1 << bit)) {
            cnt++;
        }
    }

    return cnt;
}
```

---

## Faster Trick

```cpp
int countBitsFast(int n) {
    int cnt = 0;

    while (n > 0) {
        n = n & (n - 1);
        cnt++;
    }

    return cnt;
}
```

---

## Dry Run

n = 13

```text
13 = 1101
12 = 1100

1101 & 1100 = 1100
```

One set bit removed.

| Step | n before | n after n & (n-1) | cnt |
|---|---|---|---:|
| 1 | 1101 | 1100 | 1 |
| 2 | 1100 | 1000 | 2 |
| 3 | 1000 | 0000 | 3 |

Answer = 3

---

# 8. Power of Two Check

## Problem Statement

Given `n`, check whether it is power of two.

---

## Input

```text
16
```

## Expected Output

```text
YES
```

---

## Brute Force

Keep dividing by 2.

---

## Optimal Idea

Power of two has exactly one set bit.

Example:

```text
1  = 0001
2  = 0010
4  = 0100
8  = 1000
16 = 10000
```

So:

```cpp
n & (n - 1) == 0
```

---

## Code

```cpp
bool isPowerOfTwo(int n) {
    if (n <= 0) return false;
    return (n & (n - 1)) == 0;
}
```

---

# 9. Representing a Set as an Integer

Your note shows:

Set:

```text
{1, 3, 5, 7, 10}
```

Subset:

```text
{1, 5, 10}
```

Each element index maps to one bit.

---

## Example Universe

| Index | Element |
|---:|---:|
| 0 | 1 |
| 1 | 3 |
| 2 | 5 |
| 3 | 7 |
| 4 | 10 |

Subset:

```text
{1, 5, 10}
```

Bits:

| Element | Index | Take? | Bit |
|---|---:|---|---:|
| 1 | 0 | yes | 1 |
| 3 | 1 | no | 0 |
| 5 | 2 | yes | 1 |
| 7 | 3 | no | 0 |
| 10 | 4 | yes | 1 |

Mask:

```text
10101
```

Decimal:

```text
1 + 4 + 16 = 21
```

---

## Core Idea

Instead of storing subset as array/list, store it as one integer.

This gives:

- O(1) union
- O(1) intersection
- easy subset generation
- useful for DP

---

# 10. Union and Intersection of Sets

## Problem Statement

Given two subset masks, find union and intersection.

---

## Input

```text
A = {1,5,10}
B = {3,5,7}
```

Universe:

```text
{1,3,5,7,10}
```

---

## Convert to Masks

A:

| Element | Take? |
|---|---|
| 1 | yes |
| 3 | no |
| 5 | yes |
| 7 | no |
| 10 | yes |

Mask:

```text
10101 = 21
```

B:

| Element | Take? |
|---|---|
| 1 | no |
| 3 | yes |
| 5 | yes |
| 7 | yes |
| 10 | no |

Mask:

```text
01110 = 14
```

---

## Union

```cpp
A | B
```

```text
10101
01110
-----
11111
```

Answer:

```text
{1,3,5,7,10}
```

---

## Intersection

```cpp
A & B
```

```text
10101
01110
-----
00100
```

Answer:

```text
{5}
```

---

# 11. Generate All Subsets

## Problem Statement

Print all subsets of given array.

---

## Input

```text
3
1 3 7
```

---

## Expected Output

```text
{}
{1}
{3}
{1,3}
{7}
{1,7}
{3,7}
{1,3,7}
```

---

## Brute Force Thinking

Every element has 2 choices:

1. Take
2. Not take

For `n` elements:

```text
2^n subsets
```

---

## Bitmask Optimal Idea

Loop all masks from:

```text
0 to 2^n - 1
```

For each mask, check each bit.

If ith bit is 1, take nums[i].

---

## Complete Mask Table

nums = [1,3,7]

| Mask Decimal | Binary | nums[0]=1 | nums[1]=3 | nums[2]=7 | Subset |
|---:|---|---|---|---|---|
| 0 | 000 | no | no | no | {} |
| 1 | 001 | yes | no | no | {1} |
| 2 | 010 | no | yes | no | {3} |
| 3 | 011 | yes | yes | no | {1,3} |
| 4 | 100 | no | no | yes | {7} |
| 5 | 101 | yes | no | yes | {1,7} |
| 6 | 110 | no | yes | yes | {3,7} |
| 7 | 111 | yes | yes | yes | {1,3,7} |

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

void printSubsets(vector<int>& nums) {
    int n = nums.size();

    for (int mask = 0; mask < (1 << n); mask++) {

        cout << "{";

        bool first = true;

        for (int i = 0; i < n; i++) {

            if (mask & (1 << i)) {

                if (!first) cout << ",";
                cout << nums[i];
                first = false;
            }
        }

        cout << "}\n";
    }
}

int main() {
    vector<int> nums = {1, 3, 7};
    printSubsets(nums);
}
```

---

## Index-by-Index Dry Run

mask = 6

Binary:

```text
110
```

| i | nums[i] | 1 << i | mask & (1 << i) | Action |
|---:|---:|---:|---:|---|
| 0 | 1 | 001 | 000 | skip |
| 1 | 3 | 010 | 010 | take 3 |
| 2 | 7 | 100 | 100 | take 7 |

Subset:

```text
{3,7}
```

---

# 12. Subset Membership using Mask

## Problem Statement

Given mask and index `i`, check whether element at index `i` exists in subset.

---

## Input

```text
mask = 5
i = 2
```

Binary:

```text
mask = 101
```

---

## Expected Output

```text
YES
```

Because bit 2 is set.

---

## Code

```cpp
bool contains(int mask, int i) {
    return (mask & (1 << i)) != 0;
}
```

---

## Dry Run

```text
mask = 101
1<<2 = 100
```

AND:

```text
101
100
---
100
```

Non-zero, so element exists.

---

# 13. XOR Properties

## Important XOR Rules

| Rule | Meaning |
|---|---|
| `a ^ a = 0` | same values cancel |
| `a ^ 0 = a` | zero does nothing |
| `a ^ b = b ^ a` | commutative |
| `(a ^ b) ^ c = a ^ (b ^ c)` | associative |

---

## Very Important

XOR is useful when duplicates cancel.

---

# 14. Problem 1 — Single Number

## Problem Statement

Every element appears twice except one element. Find that element.

---

## Input

```text
5
1 2 3 2 1
```

---

## Expected Output

```text
3
```

---

## Brute Force

Use frequency map.

| Number | Frequency |
|---:|---:|
| 1 | 2 |
| 2 | 2 |
| 3 | 1 |

Answer = 3

TC: O(n)

SC: O(n)

---

## Optimal XOR Idea

All duplicate values cancel:

```text
1 ^ 2 ^ 3 ^ 2 ^ 1
= (1 ^ 1) ^ (2 ^ 2) ^ 3
= 0 ^ 0 ^ 3
= 3
```

---

## C++ Code

```cpp
int singleNumber(vector<int>& nums) {
    int ans = 0;

    for (int x : nums) {
        ans ^= x;
    }

    return ans;
}
```

---

## Index-by-Index Dry Run

| Index | nums[i] | ans before | ans after |
|---:|---:|---:|---:|
| 0 | 1 | 0 | 1 |
| 1 | 2 | 1 | 3 |
| 2 | 3 | 3 | 0 |
| 3 | 2 | 0 | 2 |
| 4 | 1 | 2 | 3 |

Answer = 3

---

# 15. Problem 2 — Missing Number

## Problem Statement

Array contains numbers from 0 to n with one missing number. Find missing number.

---

## Input

```text
n = 5
arr = [0,1,2,4,5]
```

## Expected Output

```text
3
```

---

## Brute Force

Use visited array.

TC: O(n)

SC: O(n)

---

## Optimal XOR Idea

XOR all numbers from 0 to n.

XOR all array values.

Common numbers cancel.

Missing remains.

---

## Code

```cpp
int missingNumber(vector<int>& a, int n) {
    int ans = 0;

    for (int i = 0; i <= n; i++) {
        ans ^= i;
    }

    for (int x : a) {
        ans ^= x;
    }

    return ans;
}
```

---

## Dry Run

Expected numbers:

```text
0 1 2 3 4 5
```

Array:

```text
0 1 2 4 5
```

Everything cancels except 3.

---

# 16. Problem 3 — Two Unique Numbers

## Problem Statement

Every number appears twice except two numbers. Find those two unique numbers.

---

## Input

```text
2 4 7 9 2 4
```

## Expected Output

```text
7 9
```

---

## Brute Force

Use map frequency.

---

## Optimal XOR Idea

XOR all numbers:

```text
xorAll = 7 ^ 9
```

Since 7 and 9 are different, xorAll has at least one set bit.

Use one set bit to divide numbers into two groups.

---

## Code

```cpp
pair<int,int> twoUnique(vector<int>& a) {
    int xr = 0;

    for (int x : a) xr ^= x;

    int setBit = xr & -xr;

    int g1 = 0, g2 = 0;

    for (int x : a) {
        if (x & setBit) g1 ^= x;
        else g2 ^= x;
    }

    return {g1, g2};
}
```

---

## Dry Run

Input:

```text
2 4 7 9 2 4
```

Duplicates cancel:

```text
xr = 7 ^ 9
```

7 = 0111

9 = 1001

xr = 1110

Rightmost set bit = 0010

Group by this bit.

| Number | Has bit 1? | Group |
|---:|---|---|
| 2 | yes | g1 |
| 4 | no | g2 |
| 7 | yes | g1 |
| 9 | no | g2 |
| 2 | yes | g1 |
| 4 | no | g2 |

After XOR inside group:

g1 = 7

g2 = 9

---

# 17. Why Bit Contribution Works

Bitwise expressions can often be solved bit-by-bit.

Reason:

Each bit contributes independently to final decimal value.

Example:

```text
binary 101 = 1*2^2 + 0*2^1 + 1*2^0
```

So for sums like:

```text
Σ Ai XOR Aj
```

We can ask:

How many pairs produce 1 at this bit?

Then multiply by bit value.

---

# 18. Problem 4 — Sum of XOR of All Pairs

## Problem Statement

Given array, find sum of XOR of all unordered pairs.

---

## Input

```text
3
1 3 5
```

---

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

---

## Brute Force

```cpp
long long ans = 0;

for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        ans += a[i] ^ a[j];
    }
}
```

TC: O(n²)

---

## Optimal Idea

For each bit:

- XOR bit is 1 only when one number has 0 and other has 1.
- If ones = count of numbers with this bit set
- zeros = n - ones

Then number of pairs producing 1 at this bit:

```text
ones * zeros
```

Contribution:

```text
ones * zeros * 2^bit
```

---

## Bit-by-Bit Table

Array:

```text
1 = 001
3 = 011
5 = 101
```

| Bit | Bits of all nums | ones | zeros | pairs | contribution |
|---:|---|---:|---:|---:|---:|
| 0 | 1,1,1 | 3 | 0 | 0 | 0 |
| 1 | 0,1,0 | 1 | 2 | 2 | 2 * 2 = 4 |
| 2 | 0,0,1 | 1 | 2 | 2 | 2 * 4 = 8 |

Total:

```text
12
```

---

## C++ Code

```cpp
long long sumXorPairs(vector<int>& a) {
    int n = a.size();
    long long ans = 0;

    for (int bit = 0; bit < 31; bit++) {
        long long ones = 0;

        for (int x : a) {
            if (x & (1LL << bit)) {
                ones++;
            }
        }

        long long zeros = n - ones;
        ans += ones * zeros * (1LL << bit);
    }

    return ans;
}
```

---

## Index-by-Index Dry Run

For bit = 1:

| Index | a[i] | Binary | bit 1 |
|---:|---:|---|---:|
| 0 | 1 | 001 | 0 |
| 1 | 3 | 011 | 1 |
| 2 | 5 | 101 | 0 |

ones = 1

zeros = 2

Pairs:

- 3 with 1
- 3 with 5

Contribution:

```text
2 * 2^1 = 4
```

---

# 19. Problem 5 — Sum of OR of All Pairs

## Problem Statement

Find sum of OR of all unordered pairs.

---

## Input

```text
1 3 5
```

---

## Expected Output

```text
1|3 = 3
1|5 = 5
3|5 = 7

Total = 15
```

---

## Brute Force

Check all pairs.

TC: O(n²)

---

## Optimal Idea

For each bit:

OR gives 0 only when both numbers have 0.

Total pairs:

```text
n * (n - 1) / 2
```

zero-zero pairs:

```text
zeros * (zeros - 1) / 2
```

Pairs where OR bit is 1:

```text
totalPairs - zeroZeroPairs
```

Contribution:

```text
pairsWithOne * 2^bit
```

---

## Code

```cpp
long long sumOrPairs(vector<int>& a) {
    int n = a.size();
    long long totalPairs = 1LL * n * (n - 1) / 2;
    long long ans = 0;

    for (int bit = 0; bit < 31; bit++) {
        long long zeros = 0;

        for (int x : a) {
            if ((x & (1LL << bit)) == 0) {
                zeros++;
            }
        }

        long long zeroZeroPairs = zeros * (zeros - 1) / 2;
        long long onePairs = totalPairs - zeroZeroPairs;

        ans += onePairs * (1LL << bit);
    }

    return ans;
}
```

---

# 20. Problem 6 — Sum of AND of All Pairs

## Problem Statement

Find sum of AND of all unordered pairs.

---

## Input

```text
1 3 5
```

---

## Expected Output

```text
1&3 = 1
1&5 = 1
3&5 = 1

Total = 3
```

---

## Optimal Idea

AND gives 1 only when both numbers have 1.

For each bit:

```text
ones choose 2
```

Contribution:

```text
ones * (ones - 1) / 2 * 2^bit
```

---

## Code

```cpp
long long sumAndPairs(vector<int>& a) {
    long long ans = 0;

    for (int bit = 0; bit < 31; bit++) {
        long long ones = 0;

        for (int x : a) {
            if (x & (1LL << bit)) {
                ones++;
            }
        }

        long long pairs = ones * (ones - 1) / 2;
        ans += pairs * (1LL << bit);
    }

    return ans;
}
```

---

# 21. Cyclic Property Mental Model

Bits repeat in cycles.

---

## Bit 0 Pattern

```text
0 1 0 1 0 1 0 1
```

Cycle length:

```text
2
```

---

## Bit 1 Pattern

```text
0 0 1 1 0 0 1 1
```

Cycle length:

```text
4
```

---

## Bit 2 Pattern

```text
0 0 0 0 1 1 1 1
```

Cycle length:

```text
8
```

---

## General Rule

For bit `i`:

```text
cycle length = 2^(i+1)
zeros first = 2^i
ones next = 2^i
```

---

# 22. Problem 7 — Count Set Bits from 1 to N

## Problem Statement

Count total number of set bits in binary representation of all numbers from 1 to n.

---

## Input

```text
7
```

## Expected Output

```text
12
```

Because:

| Number | Binary | Set Bits |
|---:|---|---:|
| 1 | 001 | 1 |
| 2 | 010 | 1 |
| 3 | 011 | 2 |
| 4 | 100 | 1 |
| 5 | 101 | 2 |
| 6 | 110 | 2 |
| 7 | 111 | 3 |

Total:

```text
12
```

---

## Brute Force

For every number from 1 to n, count bits.

TC:

```text
O(n log n)
```

---

## Optimal Cyclic Idea

For each bit, count how many numbers from 0 to n have that bit set.

For bit `i`:

```text
cycle = 2^(i+1)
fullCycles = (n + 1) / cycle
remainder = (n + 1) % cycle
onesFromFullCycles = fullCycles * 2^i
onesFromRemainder = max(0, remainder - 2^i)
```

---

## Code

```cpp
long long countSetBits1ToN(long long n) {
    long long ans = 0;

    for (int bit = 0; bit < 60; bit++) {
        long long half = 1LL << bit;
        long long cycle = 1LL << (bit + 1);

        long long totalNumbers = n + 1;

        long long fullCycles = totalNumbers / cycle;
        long long rem = totalNumbers % cycle;

        long long ones = fullCycles * half + max(0LL, rem - half);

        ans += ones;
    }

    return ans;
}
```

---

## Dry Run for n = 7

Numbers considered:

```text
0 to 7
```

| bit | pattern | ones |
|---:|---|---:|
| 0 | 01010101 | 4 |
| 1 | 00110011 | 4 |
| 2 | 00001111 | 4 |

Total:

```text
12
```

---

# 23. Problem 8 — Kth One in Infinite Binary String

## Problem Statement

Imagine writing binary representation of all numbers:

```text
0, 1, 10, 11, 100, 101, ...
```

Find where kth one appears / count number of ones up to x.

This is from your cyclic property notes.

---

## Core Function

Define:

```text
f(x) = number of 1s in binary representations from 0 to x
```

Then use binary search if asked for kth one.

---

## Brute Force

Generate binary strings for all numbers.

Too slow for:

```text
k <= 10^9
```

---

## Optimal Idea

Use cyclic property to compute:

```text
f(x)
```

in O(log x).

Then binary search the smallest x such that:

```text
f(x) >= k
```

---

## Code

```cpp
long long countOnesUpto(long long x) {
    long long ans = 0;

    for (int bit = 0; bit < 60; bit++) {
        long long half = 1LL << bit;
        long long cycle = 1LL << (bit + 1);

        long long total = x + 1;
        long long full = total / cycle;
        long long rem = total % cycle;

        ans += full * half + max(0LL, rem - half);
    }

    return ans;
}

long long kthOne(long long k) {
    long long lo = 0, hi = 1;

    while (countOnesUpto(hi) < k) {
        hi *= 2;
    }

    long long ans = hi;

    while (lo <= hi) {
        long long mid = lo + (hi - lo) / 2;

        if (countOnesUpto(mid) >= k) {
            ans = mid;
            hi = mid - 1;
        } else {
            lo = mid + 1;
        }
    }

    return ans;
}
```

---

# 24. Conservation of Bits

Your notes show this identity:

```text
a + b = (a | b) + (a & b)
```

This works because bits are conserved across OR and AND.

---

## Why?

Consider one bit:

| a | b | a OR b | a AND b | sum contribution |
|---:|---:|---:|---:|---:|
| 0 | 0 | 0 | 0 | 0 |
| 0 | 1 | 1 | 0 | 1 |
| 1 | 0 | 1 | 0 | 1 |
| 1 | 1 | 1 | 1 | 2 |

So:

```text
a + b = (a | b) + (a & b)
```

---

## Related Identity

```text
a + b = (a ^ b) + 2 * (a & b)
```

XOR stores sum without carry.

AND stores carry positions.

---

# 25. Problem 9 — Decode Addition using OR and AND

## Problem Statement

Given two numbers `a` and `b`, verify:

```text
a + b = (a | b) + (a & b)
```

---

## Input

```text
a = 12
b = 10
```

---

## Expected Output

```text
22
```

---

## Bit-by-Bit Example

```text
a = 12 = 1100
b = 10 = 1010
```

OR:

```text
1100
1010
----
1110 = 14
```

AND:

```text
1100
1010
----
1000 = 8
```

OR + AND:

```text
14 + 8 = 22
```

a + b:

```text
12 + 10 = 22
```

---

## Code

```cpp
int decodeAddition(int a, int b) {
    return (a | b) + (a & b);
}
```

---

# 26. Problem 10 — XOR Shift Expression

## Problem Statement

Evaluate:

```text
x ^ (x << 1) ^ (x << 2) ^ ... ^ (x << k)
```

---

## Example

Let:

```text
x = 1101
k = 2
```

Expression:

```text
x ^ (x << 1) ^ (x << 2)
```

---

## Bit-by-Bit Table

```text
x        = 001101
x << 1   = 011010
x << 2   = 110100
----------------
xor      = 100011
```

---

## Important Idea

Each final bit is XOR of some diagonal column of original x.

This is operation decoding.

You do not simulate blindly; you understand how each operation moves bits.

---

## Code

```cpp
long long xorShiftExpression(long long x, int k) {
    long long ans = 0;

    for (int shift = 0; shift <= k; shift++) {
        ans ^= (x << shift);
    }

    return ans;
}
```

---

## Dry Run

x = 13

Binary:

```text
1101
```

k = 2

| shift | value | binary |
|---:|---:|---|
| 0 | 13 | 001101 |
| 1 | 26 | 011010 |
| 2 | 52 | 110100 |

XOR:

```text
001101
011010
110100
------
100011
```

Answer:

```text
35
```

---

# 27. Highest Bit Greedy Mental Model

Higher bits are more valuable than all lower bits combined.

Example:

```text
2^5 = 32
2^4 + 2^3 + 2^2 + 2^1 + 2^0 = 31
```

So if you can set a higher bit, it dominates all lower bits.

---

## Pattern

Process bits from high to low:

```cpp
for (int bit = 30; bit >= 0; bit--) {
    // try to keep or set this bit
}
```

---

# 28. Problem 11 — Maximum AND Subset

## Problem Statement

Given array, find maximum possible AND value of any non-empty subset.

---

## Input

```text
4
1 3 5 6
```

---

## Expected Output

```text
6
```

Because subset `{6}` has AND = 6.

If subset size is constrained, the same idea still applies with filtering.

---

## Brute Force

Generate all subsets and compute AND.

TC:

```text
O(2^n * n)
```

---

## Optimal Highest-Bit Idea

Try to build answer from highest bit to lowest bit.

For a bit to stay 1 in AND, every selected number must have that bit.

So filter numbers that contain current candidate bits.

---

## Code

```cpp
int maxAndSubset(vector<int>& a) {
    int ans = 0;

    for (int bit = 30; bit >= 0; bit--) {
        int candidate = ans | (1 << bit);

        bool possible = false;

        for (int x : a) {
            if ((x & candidate) == candidate) {
                possible = true;
                break;
            }
        }

        if (possible) {
            ans = candidate;
        }
    }

    return ans;
}
```

---

## Dry Run

Array:

```text
1 = 001
3 = 011
5 = 101
6 = 110
```

Try bit 2:

candidate = 100

Numbers with bit 2:

```text
5, 6
```

Possible, ans = 100.

Try bit 1:

candidate = 110

Numbers containing 110:

```text
6
```

Possible, ans = 110.

Try bit 0:

candidate = 111

No number contains 111.

Skip.

Answer:

```text
110 = 6
```

---

# 29. Problem 12 — Maximum XOR Pair

## Problem Statement

Given array, find maximum XOR of any pair.

---

## Input

```text
3 10 5 25 2 8
```

---

## Expected Output

```text
28
```

Because:

```text
5 ^ 25 = 28
```

---

## Brute Force

Try all pairs.

TC:

```text
O(n²)
```

---

## Optimal Highest-Bit Idea

Build answer from high bit to low bit.

At each bit, check if candidate is possible using prefixes.

---

## Code

```cpp
int findMaximumXOR(vector<int>& nums) {
    int ans = 0;
    int mask = 0;

    for (int bit = 30; bit >= 0; bit--) {
        mask |= (1 << bit);

        unordered_set<int> prefixes;

        for (int x : nums) {
            prefixes.insert(x & mask);
        }

        int candidate = ans | (1 << bit);

        bool possible = false;

        for (int p : prefixes) {
            if (prefixes.count(p ^ candidate)) {
                possible = true;
                break;
            }
        }

        if (possible) {
            ans = candidate;
        }
    }

    return ans;
}
```

---

## Why This Works

If:

```text
a ^ b = candidate
```

Then:

```text
a = b ^ candidate
```

So for each prefix `p`, we check whether:

```text
p ^ candidate
```

also exists.

---

# 30. C++ STL Bitset

`bitset` is available in STL.

---

## Declaration

```cpp
bitset<d> x;
```

`d` = number of bits.

---

## Constructor

```cpp
bitset<4> x(7);
```

7 in binary:

```text
0111
```

---

## Print

```cpp
cout << x.to_string();
```

---

## Access like Array

```cpp
x[0]
x[1]
```

Important:

`x[0]` is least significant bit.

---

## Common Functions

| Function | Meaning |
|---|---|
| `count()` | number of 1 bits |
| `any()` | at least one bit is 1 |
| `none()` | all bits are 0 |
| `set(i)` | make bit i = 1 |
| `reset(i)` | make bit i = 0 |
| `flip(i)` | toggle bit i |
| `to_string()` | convert to string |

---

# 31. Problem 13 — Print Binary using bitset

## Problem Statement

Given number, print its binary representation using fixed width.

---

## Input

```text
13
```

## Expected Output

```text
00001101
```

---

## Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    int x;
    cin >> x;

    bitset<8> b(x);

    cout << b.to_string() << "\n";
}
```

---

## Dry Run

x = 13

Binary:

```text
1101
```

8-bit representation:

```text
00001101
```

---

# 32. Recognition Signals

| Problem Statement Contains | Pattern |
|---|---|
| each element appears twice except one | XOR cancellation |
| all subsets | bitmask |
| n <= 20 and DP | bitmask DP |
| pair XOR / OR / AND sum | contribution |
| count set bits from 1 to n | cyclic property |
| maximize XOR | trie / highest bit |
| repeated bit operations | operation decoding |
| OR + AND relation | conservation of bits |
| choose set of elements with max AND | highest bit greedy |

---

# 33. CP + FAANG Problem Ladder

## Phase 1 — Easy Foundation

1. Decimal to Binary
2. Count Set Bits
3. Check Power of Two
4. Check ith Bit
5. Set / Clear / Toggle ith Bit

---

## Phase 2 — XOR Foundation

1. Single Number
2. Missing Number
3. Two Unique Numbers
4. XOR from 1 to n
5. Find duplicate using XOR

---

## Phase 3 — Subset Bitmasking

1. Generate All Subsets
2. Count subsets with given sum
3. Subset OR
4. Subset XOR
5. Bitmask recursion comparison

---

## Phase 4 — Contribution

1. Sum of XOR of all pairs
2. Sum of OR of all pairs
3. Sum of AND of all pairs
4. Sum of XOR of all subarrays
5. Bit contribution with modulo

---

## Phase 5 — Cyclic Property

1. Count set bits from 1 to n
2. Count ones in range L to R
3. kth one in binary sequence
4. periodic bit query
5. prefix count per bit

---

## Phase 6 — Highest Bit Greedy

1. Maximum AND subset
2. Maximum XOR pair
3. Minimum XOR pair
4. Maximize X under constraints
5. XOR trie

---

## Phase 7 — Advanced CP

1. SOS DP
2. XOR Basis
3. Bitmask DP TSP
4. Meet in the Middle
5. Digit DP with bits

---

# 34. Final Revision Sheet

## Operators

```cpp
&  AND
|  OR
^  XOR
~  NOT
<< left shift
>> right shift
```

---

## Core Formulas

```cpp
x << y = x * 2^y
x >> y = floor(x / 2^y)
x & (x-1) removes rightmost set bit
x & -x gives rightmost set bit
```

---

## XOR Rules

```cpp
a ^ a = 0
a ^ 0 = a
a ^ b ^ a = b
```

---

## Contribution Formulas

XOR pair contribution:

```cpp
ones * zeros * (1LL << bit)
```

OR pair contribution:

```cpp
(totalPairs - zeroZeroPairs) * (1LL << bit)
```

AND pair contribution:

```cpp
(ones choose 2) * (1LL << bit)
```

---

## Cyclic Bit Formula

For bit `i`:

```cpp
cycle = 2^(i+1)
half = 2^i
ones = fullCycles * half + max(0, rem - half)
```

---

## Highest Bit Rule

Always try high bit first:

```cpp
for (int bit = 30; bit >= 0; bit--)
```

Because:

```text
one higher bit > all lower bits combined
```

---

# End

This note covers all major ideas from your handwritten bit manipulation notes in the same structured style as the sliding-window notes:

- basics
- bit tables
- brute force
- optimized idea
- C++ code
- index-by-index dry run
- CP + FAANG pattern map
