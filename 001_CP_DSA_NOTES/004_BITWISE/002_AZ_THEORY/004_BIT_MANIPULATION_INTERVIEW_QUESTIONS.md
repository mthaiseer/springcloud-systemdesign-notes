# Bit Manipulation — Application Patterns
## XOR Cancellation, Bit Contribution, Missing Numbers, Hamming Distance, Minimum XOR, and Total Set Bits
### TLE-Style Self-Study Notes

> **Source:** supplied TLE/bit-manipulation problem sheet.
>
> The source contains one binary-printing foundation problem followed by ten bit-manipulation applications. This note keeps those techniques, but expands the missing reasoning with prerequisites, tiny examples, proofs, compact dry runs, and clean C++17.
>
> **Teaching expansion:** additional derivations below are explanatory expansions around the source techniques. Where the source code is narrow or awkward, the implementation is normalized while preserving the same idea.

---

# Table of Contents

- [0. Foundation — Print Binary Representation](#0-foundation--print-binary-representation)
- [1. Prerequisite — XOR Properties](#1-prerequisite--xor-properties)
- [2. Prerequisite — Bit-by-Bit Contribution](#2-prerequisite--bit-by-bit-contribution)
- [3. Pattern 1 — Every Number Appears 3 Times Except One](#3-pattern-1--every-number-appears-3-times-except-one)
- [4. Pattern 2 — One Number Has Odd Frequency](#4-pattern-2--one-number-has-odd-frequency)
- [5. Pattern 3 — Two Unique Numbers, Others Twice](#5-pattern-3--two-unique-numbers-others-twice)
- [6. Pattern 4 — XOR from 1 to N](#6-pattern-4--xor-from-1-to-n)
- [7. Pattern 5 — Missing Number from 1 to N](#7-pattern-5--missing-number-from-1-to-n)
- [8. Prerequisite — Addition vs XOR](#8-prerequisite--addition-vs-xor)
- [9. Pattern 6 — Count `i` such that `n+i = n^i`](#9-pattern-6--count-i-such-that-ni--ni)
- [10. Prerequisite — Contribution Technique for Subarrays](#10-prerequisite--contribution-technique-for-subarrays)
- [11. Pattern 7 — XOR of Elements of All Subarrays](#11-pattern-7--xor-of-elements-of-all-subarrays)
- [12. Pattern 8 — Sum of Pairwise Hamming Distances](#12-pattern-8--sum-of-pairwise-hamming-distances)
- [13. Pattern 9 — Minimum XOR Pair](#13-pattern-9--minimum-xor-pair)
- [14. Prerequisite — Periodicity of One Bit](#14-prerequisite--periodicity-of-one-bit)
- [15. Pattern 10 — Total Set Bits from 0 to N](#15-pattern-10--total-set-bits-from-0-to-n)
- [16. Comparing the Patterns](#16-comparing-the-patterns)
- [17. Common Mistakes](#17-common-mistakes)
- [18. Recognition Table](#18-recognition-table)
- [19. Practice Links](#19-practice-links)
- [20. Final Mental Model](#20-final-mental-model)

---

# 0. Foundation — Print Binary Representation

## What It Asks

Given a non-zero integer `x`, print its binary representation.

Examples from the source:

```text
7 → 111
4 → 100
```

## Method 1 — Scan Bits from MSB to LSB

The source scans all 32 bit positions. That naturally prints a fixed-width representation with leading zeros. To match the compact examples, start printing only after the first `1`.

```cpp
void printBinary(unsigned int x) {
    bool started = false;

    for (int bit = 31; bit >= 0; bit--) {
        bool set = (x & (1U << bit)) != 0;

        if (set) {
            started = true;
        }

        if (started) {
            cout << set;
        }
    }

    cout << '\n';
}
```

## Method 2 — Recursion

Key idea:

```text
x / 2 → remove the least-significant binary digit
x % 2 → current least-significant digit
```

To print MSB first:

```text
print higher bits recursively
then print current LSB
```

```cpp
void printBinaryRecursive(unsigned int x) {
    if (x > 1) {
        printBinaryRecursive(x / 2);
    }

    cout << x % 2;
}
```

Dry run for `4`:

```text
print(4)
→ print(2)
  → print(1)
    → print 1
  → print 0
→ print 0

answer = 100
```

---

# 1. Prerequisite — XOR Properties

Memorize these:

```text
x ^ x = 0
x ^ 0 = x
a ^ b = b ^ a
(a ^ b) ^ c = a ^ (b ^ c)
```

Example:

```text
5 ^ 7 ^ 5
=
5 ^ 5 ^ 7
=
0 ^ 7
=
7
```

This cancellation principle powers several problems below.

---

# 2. Prerequisite — Bit-by-Bit Contribution

Instead of reasoning about the whole number, solve one bit position independently.

Example:

```text
5 = 101
6 = 110
3 = 011
```

At bit 0:

```text
1,0,1
```

At bit 1:

```text
0,1,1
```

At bit 2:

```text
1,1,0
```

Ask:

```text
What does this one bit contribute?
```

Then sum across all bits.

---

# 3. Pattern 1 — Every Number Appears 3 Times Except One

## Problem

Every value occurs three times except one value that occurs once.

Example:

```text
[23,5,23,4,23,4,5,3,5,4]
→ 3
```

### Matched Practice

[LeetCode 137 — Single Number II](https://leetcode.com/problems/single-number-ii/)

## Core Observation

At each bit position:

```text
triplicated values contribute a multiple of 3 ones
```

Therefore:

```text
if onesAtBit % 3 != 0
→ unique value has this bit set
```

## Tiny Example

```text
arr = [5,5,5,3]

5 = 101
5 = 101
5 = 101
3 = 011
```

Bit counts:

```text
bit0: 4 → 4%3=1 → answer bit =1
bit1: 1 → 1%3=1 → answer bit =1
bit2: 3 → 3%3=0 → answer bit =0
```

So:

```text
011₂ = 3
```

## C++17

```cpp
int singleAmongTriples(const vector<int>& arr) {
    int answer = 0;

    for (int bit = 0; bit < 31; bit++) {
        int ones = 0;

        for (int value : arr) {
            ones += (value >> bit) & 1;
        }

        if (ones % 3 != 0) {
            answer |= (1 << bit);
        }
    }

    return answer;
}
```

Complexity:

```text
Time  = O(31N) = O(N)
Space = O(1)
```

Recognition:

```text
every value occurs K times except one
→ count each bit modulo K
```

---

# 4. Pattern 2 — One Number Has Odd Frequency

## Problem

Every element occurs an even number of times except one value that occurs an odd number of times.

Example:

```text
[23,15,23,4,23,4,15,4,23,15,15]
→ 4
```

### Matched Practice

[LeetCode 136 — Single Number](https://leetcode.com/problems/single-number/)

## Why XOR Works

Even copies cancel:

```text
x ^ x = 0
```

An odd count leaves one copy after pairing.

```cpp
int oddOccurrence(const vector<int>& arr) {
    int answer = 0;

    for (int value : arr) {
        answer ^= value;
    }

    return answer;
}
```

Compact dry run:

```text
[4,7,4,7,7]

0 ^4 =4
4 ^7 =3
3 ^4 =7
7 ^7 =0
0 ^7 =7
```

Answer:

```text
7
```

---

# 5. Pattern 3 — Two Unique Numbers, Others Twice

## Problem

Every number occurs twice except two unique values.

Example:

```text
[23,23,4,3,5,3,15,15]
→ 4,5
```

### Matched Practice

[LeetCode 260 — Single Number III](https://leetcode.com/problems/single-number-iii/)

## Step 1 — XOR Everything

If the two unique values are `a` and `b`:

```text
xorAll = a ^ b
```

All duplicate values disappear.

Because `a != b`:

```text
a ^ b != 0
```

So there exists a bit where they differ.

## Step 2 — Isolate One Differing Bit

Use the lowest set bit:

```cpp
int separatingBit = xorAll & -xorAll;
```

The source uses the equivalent:

```cpp
xorAll & ~(xorAll - 1)
```

Why does this help?

```text
that bit is set in exactly one of a and b
```

So partition the array into:

```text
bit set
bit not set
```

Duplicates stay together and cancel; `a` and `b` separate.

```cpp
pair<int, int> twoUniqueNumbers(const vector<int>& arr) {
    int xorAll = 0;

    for (int value : arr) {
        xorAll ^= value;
    }

    int separatingBit = xorAll & -xorAll;

    int first = 0;
    int second = 0;

    for (int value : arr) {
        if (value & separatingBit) {
            first ^= value;
        } else {
            second ^= value;
        }
    }

    return {first, second};
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 6. Pattern 4 — XOR from 1 to N

## Problem

Compute:

```text
1 ^ 2 ^ 3 ^ ... ^ N
```

Examples from the source:

```text
12 → 12
15 → 0
```

## Discover the Pattern

```text
N=0 → 0
N=1 → 1
N=2 → 3
N=3 → 0
N=4 → 4
N=5 → 1
N=6 → 7
N=7 → 0
```

It repeats every 4.

| `N % 4` | XOR `1..N` |
|---:|---|
| 0 | `N` |
| 1 | `1` |
| 2 | `N+1` |
| 3 | `0` |

```cpp
int xorFromOneToN(int n) {
    int remainder = n % 4;

    if (remainder == 0) {
        return n;
    }

    if (remainder == 1) {
        return 1;
    }

    if (remainder == 2) {
        return n + 1;
    }

    return 0;
}
```

Complexity:

```text
O(1)
```

---

# 7. Pattern 5 — Missing Number from 1 to N

## Problem

One number is missing from:

```text
1..N
```

Example:

```text
[1,2,5,6,3,7]
→ 4
```

### Matched Practice

[LeetCode 268 — Missing Number](https://leetcode.com/problems/missing-number/)

## XOR Derivation

Compute:

```text
expected = 1 ^ 2 ^ ... ^ N
actual   = XOR of array values
```

Then:

```text
expected ^ actual
```

cancels every present number and leaves the missing one.

```cpp
int missingNumberOneToN(const vector<int>& arr) {
    int n = arr.size() + 1;
    int answer = xorFromOneToN(n);

    for (int value : arr) {
        answer ^= value;
    }

    return answer;
}
```

Complexity:

```text
Time  = O(N)
Space = O(1)
```

---

# 8. Prerequisite — Addition vs XOR

Fundamental identity:

```text
a + b
=
(a ^ b) + 2(a & b)
```

Interpretation:

```text
XOR = addition without carry
AND = positions that generate carry
```

Example:

```text
a = 5 = 101
b = 3 = 011
```

```text
a^b = 110 = 6
a&b = 001 = 1
```

So:

```text
6 + 2*1 = 8
```

which equals:

```text
5+3
```

---

# 9. Pattern 6 — Count `i` such that `n+i = n^i`

## Problem

Count integers `i` satisfying:

```text
0 <= i <= n
```

and:

```text
n + i = n ^ i
```

Examples:

```text
10 → 4
18 → 8
```

### Matched Practice

[HackerRank — Sum vs XOR](https://www.hackerrank.com/challenges/sum-vs-xor/problem)

## Derivation

Using:

```text
n+i = (n^i) + 2(n&i)
```

For:

```text
n+i = n^i
```

we need:

```text
n&i = 0
```

Therefore `i` may set bits only where `n` has zero bits.

## Example: `n=10`

```text
10 = 1010₂
```

Within its significant width:

```text
1 0 1 0
```

Zero positions:

```text
2
```

Each zero position may independently become `0` or `1` in `i`:

```text
2^2 = 4
```

## Example: `n=18`

```text
18 = 10010₂
```

Zero positions:

```text
3
```

Answer:

```text
2^3 = 8
```

```cpp
long long countSumEqualsXor(int n) {
    int zeroBits = 0;

    while (n > 0) {
        if ((n & 1) == 0) {
            zeroBits++;
        }

        n >>= 1;
    }

    return 1LL << zeroBits;
}
```

Complexity:

```text
O(log N)
```

---

# 10. Prerequisite — Contribution Technique for Subarrays

How many subarrays contain index `i` in an array of length `N`?

Left endpoint choices:

```text
0..i
→ i+1 choices
```

Right endpoint choices:

```text
i..N-1
→ N-i choices
```

Therefore:

```text
frequency(i)
=
(i+1)(N-i)
```

Example:

```text
N=4, i=1
```

```text
left choices  =2
right choices =3
frequency     =6
```

---

# 11. Pattern 7 — XOR of Elements of All Subarrays

## Problem

Take every subarray, XOR its elements, then XOR all those subarray XOR values together.

Source example:

```text
[12,15,6,7,9,14,18]
→ 17
```

## Contribution View

`arr[i]` appears in:

```text
(i+1)(N-i)
```

subarrays.

For XOR:

```text
even frequency → cancels
odd frequency  → survives
```

So `arr[i]` contributes iff:

```text
(i+1)(N-i)
```

is odd.

A product is odd only if both factors are odd.

That happens exactly when:

```text
N is odd
and
i is even (0-based)
```

Therefore:

```text
N even → answer = 0
N odd  → arr[0] ^ arr[2] ^ arr[4] ...
```

```cpp
int xorOfAllSubarrays(const vector<int>& arr) {
    int n = arr.size();

    if (n % 2 == 0) {
        return 0;
    }

    int answer = 0;

    for (int i = 0; i < n; i += 2) {
        answer ^= arr[i];
    }

    return answer;
}
```

Complexity:

```text
O(N)
```

---

# 12. Pattern 8 — Sum of Pairwise Hamming Distances

## Problem

Define:

```text
f(X,Y)
=
number of bit positions where X and Y differ
```

Find the total over every ordered pair `(i,j)`.

Example from the source:

```text
[2,3,7,6,4]
→ 32
```

### Matched Practice

[LeetCode 477 — Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/)

> LeetCode counts unordered pairs; the source counts ordered pairs, so the source contribution is doubled.

## One-Bit Contribution

At one bit position:

```text
ones  = X
zeros = Y
```

An unordered differing pair is formed by:

```text
choose one from ones
choose one from zeros
```

Count:

```text
XY
```

For ordered pairs:

```text
1→0 and 0→1
```

so contribution is:

```text
2XY
```

```cpp
long long totalHammingDistanceOrdered(const vector<int>& arr) {
    const long long mod = 1000000007LL;
    int n = arr.size();

    long long answer = 0;

    for (int bit = 0; bit < 31; bit++) {
        long long ones = 0;

        for (int value : arr) {
            ones += (value >> bit) & 1;
        }

        long long zeros = n - ones;
        long long contribution = (2LL * ones % mod) * zeros % mod;

        answer = (answer + contribution) % mod;
    }

    return answer;
}
```

Recognition:

```text
sum over all pairs
+
bitwise difference
→ count ones and zeros independently at each bit
```

---

# 13. Pattern 9 — Minimum XOR Pair

## Problem

Find:

```text
min(a[i] ^ a[j])
```

over all distinct pairs.

Example:

```text
[2,3,7,6,4]
→ 1
```

### Matched Practice

[InterviewBit — Min XOR Value](https://www.interviewbit.com/problems/min-xor-value/)

## Key Observation

Sort the array.

The minimum XOR pair appears among adjacent values.

Why?

XOR magnitude is dominated by the highest bit where two numbers differ. Values close in sorted order share long binary prefixes. For any non-adjacent pair, an intermediate sorted value gives an adjacent candidate with at least as strong a common prefix.

So after sorting, only check:

```text
(a[0],a[1])
(a[1],a[2])
...
```

## Dry Run

```text
arr = [7,2,6,3,4]
```

Sort:

```text
[2,3,4,6,7]
```

Adjacent XORs:

```text
2^3 =1
3^4 =7
4^6 =2
6^7 =1
```

Answer:

```text
1
```

```cpp
int minimumXorPair(vector<int> arr) {
    sort(arr.begin(), arr.end());

    int answer = INT_MAX;

    for (int i = 1; i < int(arr.size()); i++) {
        answer = min(answer, arr[i] ^ arr[i - 1]);
    }

    return answer;
}
```

Complexity:

```text
O(N log N)
```

---

# 14. Prerequisite — Periodicity of One Bit

As integers increase, bit `i` follows a repeating pattern:

```text
2^i zeros
then
2^i ones
```

Full cycle length:

```text
2^(i+1)
```

Example for bit `1`:

```text
0 0 1 1 | 0 0 1 1 | ...
```

Example for bit `2`:

```text
0 0 0 0 1 1 1 1 | ...
```

This periodicity lets us count set bits over a huge numeric range without visiting every number.

---

# 15. Pattern 10 — Total Set Bits from 0 to N

## Problem

Count all `1` bits appearing in binary representations of:

```text
0,1,2,...,N
```

Examples:

```text
4 → 5
5 → 7
```

### Matched Practice

[InterviewBit — Count Total Set Bits](https://www.interviewbit.com/problems/count-total-set-bits/)

## Verify `N=4`

```text
0 = 000 → 0
1 = 001 → 1
2 = 010 → 1
3 = 011 → 2
4 = 100 → 1
```

Total:

```text
5
```

## Count One Bit Position

For bit `i`:

```text
halfCycle = 2^i
fullCycle = 2^(i+1)
```

There are:

```text
N+1
```

numbers from `0` to `N`.

Each complete cycle contributes:

```text
halfCycle
```

ones.

Let:

```text
totalNumbers = N+1
```

Then:

```text
completeCycles = totalNumbers / fullCycle
remainder      = totalNumbers % fullCycle
```

Contribution:

```text
completeCycles * halfCycle
+
max(0, remainder - halfCycle)
```

## Dry Run for `N=5`

There are:

```text
6 numbers: 0..5
```

Bit 0:

```text
half=1, cycle=2
6/2=3 complete cycles
contribution=3
```

Bit 1:

```text
half=2, cycle=4
1 complete cycle →2 ones
remainder=2 → no extra
contribution=2
```

Bit 2:

```text
half=4, cycle=8
0 complete cycles
remainder=6
extra=6-4=2
```

Total:

```text
3+2+2=7
```

## C++17

```cpp
long long totalSetBitsFromZeroToN(long long n) {
    long long answer = 0;
    long long totalNumbers = n + 1;

    for (int bit = 0; bit < 61; bit++) {
        long long halfCycle = 1LL << bit;

        if (halfCycle > n && bit > 0) {
            break;
        }

        long long fullCycle = halfCycle << 1;
        long long completeCycles = totalNumbers / fullCycle;
        long long remainder = totalNumbers % fullCycle;

        answer += completeCycles * halfCycle;
        answer += max(0LL, remainder - halfCycle);
    }

    return answer;
}
```

Complexity:

```text
Time  = O(log N)
Space = O(1)
```

---

# 16. Comparing the Patterns

| Pattern | Main Observation | Complexity |
|---|---|---:|
| Print binary | inspect bits / recurse on `x/2` | `O(log x)` |
| One among triples | bit count modulo 3 | `O(N)` |
| Odd occurrence | XOR cancellation | `O(N)` |
| Two unique values | XOR + separating bit | `O(N)` |
| XOR `1..N` | period 4 | `O(1)` |
| Missing number | expected XOR ^ actual XOR | `O(N)` |
| `n+i=n^i` | `n&i=0` | `O(log n)` |
| XOR all subarrays | index contribution parity | `O(N)` |
| Pairwise Hamming sum | ones × zeros per bit | `O(N)` |
| Minimum XOR pair | sort + adjacent | `O(N log N)` |
| Total set bits `0..N` | repeating bit cycles | `O(log N)` |

---

# 17. Common Mistakes

## 1. Confusing XOR and OR

```text
^ = XOR
| = OR
```

Cancellation needs XOR.

## 2. Forgetting `x ^ x = 0`

This identity drives:

```text
odd occurrence
missing number
two unique numbers
```

## 3. Partitioning Two Unique Numbers on an Arbitrary Bit

Use a bit from:

```text
a ^ b
```

because that guarantees the unique values differ there.

## 4. Counting Mask Values Instead of Conceptual 0/1 Bits

For modulo-frequency problems, the clearest form is:

```cpp
(value >> bit) & 1
```

## 5. Counting Leading Zeros in `n+i=n^i`

Count zero bits only within the significant binary width of positive `n`.

## 6. Enumerating All Subarrays for Pattern 7

Use:

```text
(i+1)(N-i)
```

instead.

## 7. Hamming Distance: Ordered vs Unordered

At one bit:

```text
unordered → ones*zeros
ordered   → 2*ones*zeros
```

## 8. Minimum XOR Pair: Checking Every Pair

After sorting, only adjacent pairs are required.

## 9. Total Set Bits: Forgetting the Range Has `N+1` Values

For:

```text
0..N
```

use:

```text
totalNumbers=N+1
```

---

# 18. Recognition Table

| Signal | Technique |
|---|---|
| duplicates cancel | XOR |
| each value occurs twice except one | XOR all |
| each value occurs twice except two | XOR + separating bit |
| each value occurs K times except one | bit counts mod K |
| XOR `1..N` | `N%4` pattern |
| one missing value from known range | expected XOR ^ actual XOR |
| `a+b == a^b` | `a&b == 0` |
| XOR over all subarrays | contribution parity |
| pairwise bit differences | ones × zeros per bit |
| minimum pair XOR | sort + adjacent |
| count all set bits in range | bit periodicity |

---

# 19. Practice Links

- [LeetCode 137 — Single Number II](https://leetcode.com/problems/single-number-ii/)
- [LeetCode 136 — Single Number](https://leetcode.com/problems/single-number/)
- [LeetCode 260 — Single Number III](https://leetcode.com/problems/single-number-iii/)
- [LeetCode 268 — Missing Number](https://leetcode.com/problems/missing-number/)
- [HackerRank — Sum vs XOR](https://www.hackerrank.com/challenges/sum-vs-xor/problem)
- [LeetCode 477 — Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/)
- [InterviewBit — Min XOR Value](https://www.interviewbit.com/problems/min-xor-value/)
- [InterviewBit — Count Total Set Bits](https://www.interviewbit.com/problems/count-total-set-bits/)

---

# 20. Final Mental Model

```text
DUPLICATES?
→ XOR cancellation

REPEATED K TIMES?
→ bit counts modulo K

TWO UNIQUE VALUES?
→ XOR all
→ isolate differing bit
→ partition

CONSECUTIVE XOR?
→ modulo-4 pattern

MISSING FROM KNOWN RANGE?
→ expected XOR ^ actual XOR

ADDITION EQUALS XOR?
→ no carry
→ AND must be zero

ALL SUBARRAYS?
→ contribution count per index

ALL PAIRS?
→ contribution count per bit

MINIMUM XOR?
→ sort
→ neighbors

TOTAL SET BITS 0..N?
→ periodic cycles per bit
```

## 60-Second Revision Card

```text
x ^ x = 0
x ^ 0 = x

two unique:
xorAll = a ^ b
separatingBit = xorAll & -xorAll

xor(1..N):
N%4=0 → N
N%4=1 → 1
N%4=2 → N+1
N%4=3 → 0

a+b = (a^b) + 2(a&b)

a+b = a^b
⇔
a&b = 0

subarray frequency of index i:
(i+1)(N-i)

Hamming contribution at one bit:
unordered → ones*zeros
ordered   → 2*ones*zeros

minimum XOR pair:
sort + adjacent

total set bits 0..N:
count each bit using repeating
0-block / 1-block cycles
```

The deeper rule is:

```text
When the operation is bitwise, try to separate the problem either:

1. by VALUE cancellation using XOR, or
2. by POSITION contribution one bit at a time.
```
