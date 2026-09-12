# 08 --- Bitwise & XOR Patterns

## Codeforces Div2 A/B/C Pattern Recognition Handbook

**Goal:** make bitwise problems feel like small independent 0/1
decisions instead of mysterious tricks.

The main contest pipeline is:

``` text
INTEGER
   ↓
BINARY REPRESENTATION
   ↓
WHAT HAPPENS AT EACH BIT?
   ↓
AND / OR / XOR / SHIFT / POPCOUNT
   ↓
INVARIANT / PARITY / GREEDY / PREFIX / CONSTRUCTION
   ↓
SOLUTION
```

This guide targets the most useful patterns for Codeforces / CodeChef A,
B and C.

------------------------------------------------------------------------

# Table of Contents

1.  [Binary Mental Model](#1-binary-mental-model)
2.  [AND, OR, XOR --- What Each Operator
    Means](#2-and-or-xor--what-each-operator-means)
3.  [Bit Test, Set, Clear and Toggle](#3-bit-test-set-clear-and-toggle)
4.  [Power of Two Patterns](#4-power-of-two-patterns)
5.  [Lowbit and Removing the Lowest Set
    Bit](#5-lowbit-and-removing-the-lowest-set-bit)
6.  [Popcount and Bit Parity](#6-popcount-and-bit-parity)
7.  [XOR Cancellation](#7-xor-cancellation)
8.  [XOR as Parity per Bit](#8-xor-as-parity-per-bit)
9.  [Prefix XOR](#9-prefix-xor)
10. [XOR of 1 to N](#10-xor-of-1-to-n)
11. [Find the Unique / Missing
    Value](#11-find-the-unique--missing-value)
12. [Two Unique Values](#12-two-unique-values)
13. [Bit-by-Bit Counting](#13-bit-by-bit-counting)
14. [Pair XOR / Pair AND
    Contribution](#14-pair-xor--pair-and-contribution)
15. [Subarray XOR + Prefix
    Frequency](#15-subarray-xor--prefix-frequency)
16. [XOR Invariants Under
    Operations](#16-xor-invariants-under-operations)
17. [Constructive XOR](#17-constructive-xor)
18. [Greedy by Highest Significant
    Bit](#18-greedy-by-highest-significant-bit)
19. [AND Monotonicity](#19-and-monotonicity)
20. [OR Monotonicity](#20-or-monotonicity)
21. [Bitmask Enumeration](#21-bitmask-enumeration)
22. [Subset Masks and State
    Compression](#22-subset-masks-and-state-compression)
23. [SOS / Submask Thinking --- C
    Bridge](#23-sos--submask-thinking--c-bridge)
24. [XOR Basis Intuition --- Beyond Normal
    C](#24-xor-basis-intuition--beyond-normal-c)
25. [60-Second Recognition Map](#25-60-second-recognition-map)
26. [Common Wrong Ideas](#26-common-wrong-ideas)
27. [Practice Ladder](#27-practice-ladder)
28. [Contest Notebook Template](#28-contest-notebook-template)

------------------------------------------------------------------------

# 1. Binary Mental Model

A bit problem becomes easier when you stop seeing:

``` text
13
```

and start seeing:

``` text
13 = 1101₂
```

Each position answers a YES/NO question:

``` text
bit 3   bit 2   bit 1   bit 0
  1       1       0       1

 8        4       2       1
```

So:

``` text
13 = 8 + 4 + 1
```

## Real-world mapping --- switches

Think of an integer as a panel of switches.

``` text
bit:     3   2   1   0
switch: [ON][ON][OFF][ON]
```

Bitwise operations act on each switch independently.

That independence is the reason many C problems can be solved **bit by
bit**.

------------------------------------------------------------------------

# 2. AND, OR, XOR --- What Each Operator Means

Take:

``` text
a = 10 = 1010
b = 12 = 1100
```

## AND `&`

A bit survives only when both sides contain `1`.

``` text
  1010
& 1100
------
  1000 = 8
```

Real-world mapping:

``` text
two permissions must BOTH allow access
```

Truth table:

``` text
0 & 0 = 0
0 & 1 = 0
1 & 0 = 0
1 & 1 = 1
```

------------------------------------------------------------------------

## OR `|`

A bit becomes 1 if either side has it.

``` text
  1010
| 1100
------
  1110 = 14
```

Real-world mapping:

``` text
combine capabilities from both users
```

------------------------------------------------------------------------

## XOR `^`

A bit becomes 1 when the two bits are different.

``` text
  1010
^ 1100
------
  0110 = 6
```

Truth table:

``` text
0 ^ 0 = 0
0 ^ 1 = 1
1 ^ 0 = 1
1 ^ 1 = 0
```

Real-world mapping:

``` text
light switch toggled an odd number of times -> ON
light switch toggled an even number of times -> OFF
```

This parity interpretation is the most useful XOR intuition.

------------------------------------------------------------------------

# 3. Bit Test, Set, Clear and Toggle

For bit `k`, mask:

``` cpp
1LL << k
```

Suppose:

``` text
x = 10 = 1010
```

## Test bit

``` cpp
bool on = (x & (1LL << k)) != 0;
```

Diagram for `k=1`:

``` text
x     = 1010
mask  = 0010
AND   = 0010 != 0
```

------------------------------------------------------------------------

## Set bit

``` cpp
x |= (1LL << k);
```

Force switch ON.

------------------------------------------------------------------------

## Clear bit

``` cpp
x &= ~(1LL << k);
```

Force switch OFF.

------------------------------------------------------------------------

## Toggle bit

``` cpp
x ^= (1LL << k);
```

Flip:

``` text
0 -> 1
1 -> 0
```

### Recognition trigger

``` text
turn on/off
flip
toggle
binary state
subset membership
```

------------------------------------------------------------------------

# 4. Power of Two Patterns

A positive power of two has exactly one set bit.

``` text
1  = 0001
2  = 0010
4  = 0100
8  = 1000
16 = 10000
```

Subtract one:

``` text
8     = 1000
8 - 1 = 0111
```

AND:

``` text
1000
0111
----
0000
```

Therefore:

``` cpp
bool powerOfTwo = n > 0 && (n & (n - 1)) == 0;
```

## Why?

For a non-power:

``` text
12     = 1100
11     = 1011
AND    = 1000 != 0
```

### Recognition sentence

> "Exactly one binary component exists" usually means power of two.

This is the compact bit version of the factorization idea:

``` text
n = 2^k
```

------------------------------------------------------------------------

# 5. Lowbit and Removing the Lowest Set Bit

## `x & -x`

This isolates the lowest set bit.

Example:

``` text
x = 12 = 1100
```

Lowest set bit has value 4:

``` text
x & -x = 0100
```

This is commonly called:

``` text
lowbit(x)
```

``` cpp
long long lowbit(long long x) {
    return x & -x;
}
```

------------------------------------------------------------------------

## `x & (x-1)`

This removes the lowest set bit.

``` text
x      = 110100
x-1    = 110011
----------------
x&(x-1)= 110000
```

One `1` disappeared.

Repeatedly:

``` text
110100
110000
100000
000000
```

Number of repetitions = popcount.

``` cpp
int cnt = 0;
while (x) {
    x &= x - 1;
    ++cnt;
}
```

------------------------------------------------------------------------

# 6. Popcount and Bit Parity

`popcount(x)` = number of set bits.

``` text
13 = 1101
```

has:

``` text
3 set bits
```

C++:

``` cpp
__builtin_popcount(x);
__builtin_popcountll(x);
```

## Why useful?

Some problems do not care **which** bits are set, only:

``` text
how many?
odd or even?
at most k?
exactly k?
```

### Real-world mapping

If each `1` represents an active feature:

``` text
101101
```

then popcount is simply:

``` text
number of active features
```

------------------------------------------------------------------------

# 7. XOR Cancellation

Fundamental identities:

``` text
x ^ x = 0
x ^ 0 = x
```

Also XOR is associative and commutative:

``` text
a ^ b ^ c
=
c ^ a ^ b
```

So duplicates cancel regardless of order.

Example:

``` text
4 ^ 7 ^ 4 ^ 2 ^ 2
```

Reorder mentally:

``` text
(4^4) ^ (2^2) ^ 7
= 0 ^ 0 ^ 7
= 7
```

ASCII:

``` text
4 ----\
       XOR -> 0
4 ----/

2 ----\
       XOR -> 0
2 ----/

remaining -> 7
```

### Recognition sentence

> If every value appears an even number of times except one, think XOR
> immediately.

------------------------------------------------------------------------

# 8. XOR as Parity per Bit

This is more important than memorizing cancellation.

Suppose numbers are:

``` text
5 = 101
3 = 011
6 = 110
```

Look column by column:

``` text
         bit2 bit1 bit0
5          1    0    1
3          0    1    1
6          1    1    0
-----------------------
ones       2    2    2
parity     0    0    0
```

Therefore:

``` text
5 ^ 3 ^ 6 = 0
```

XOR stores:

``` text
parity of count of 1s at every bit
```

That viewpoint unlocks invariants and constructive problems.

------------------------------------------------------------------------

# 9. Prefix XOR

Prefix XOR is the XOR version of prefix sums.

Define:

``` text
pref[0] = 0
pref[i+1] = pref[i] ^ a[i]
```

Then:

``` text
xor(l..r) = pref[r+1] ^ pref[l]
```

## Why subtraction becomes XOR

For sums:

``` text
pref[r] - pref[l]
```

For XOR, duplicate prefix portions cancel:

``` text
(A ^ B) ^ A = B
```

Diagram:

``` text
pref[r+1]:
a0 ^ a1 ^ ... ^ a[l-1] ^ a[l] ^ ... ^ a[r]

pref[l]:
a0 ^ a1 ^ ... ^ a[l-1]

XOR them
        ↓
prefix part appears twice -> cancels
        ↓
a[l] ^ ... ^ a[r]
```

C++:

``` cpp
vector<long long> pref(n + 1, 0);
for (int i = 0; i < n; ++i)
    pref[i+1] = pref[i] ^ a[i];

long long rangeXor(int l, int r) {
    return pref[r+1] ^ pref[l];
}
```

### Recognition trigger

``` text
many range XOR queries
subarray XOR
XOR between l and r
```

------------------------------------------------------------------------

# 10. XOR of 1 to N

The value:

``` text
1 ^ 2 ^ 3 ^ ... ^ n
```

repeats with period 4.

``` text
n % 4 = 0 -> n
n % 4 = 1 -> 1
n % 4 = 2 -> n+1
n % 4 = 3 -> 0
```

Function:

``` cpp
long long xor1ToN(long long n) {
    if (n % 4 == 0) return n;
    if (n % 4 == 1) return 1;
    if (n % 4 == 2) return n + 1;
    return 0;
}
```

Then:

``` text
xor(L..R)
=
xor(1..R) ^ xor(1..L-1)
```

### Pattern combination

``` text
XOR + periodicity
```

This is exactly the kind of mixed observation common in A/B.

------------------------------------------------------------------------

# 11. Find the Unique / Missing Value

Suppose:

``` text
[1,2,3,5]
```

should contain:

``` text
1..5
```

Missing = 4.

XOR expected values:

``` text
1^2^3^4^5
```

XOR actual:

``` text
1^2^3^5
```

Combine:

``` text
1^1 ^ 2^2 ^ 3^3 ^ 5^5 ^ 4
= 4
```

### C++ pattern

``` cpp
long long ans = 0;

for (int i = 1; i <= n; ++i)
    ans ^= i;

for (long long x : a)
    ans ^= x;
```

------------------------------------------------------------------------

# 12. Two Unique Values

Suppose every number occurs twice except `x` and `y`.

XOR all:

``` text
allXor = x ^ y
```

Since `x != y`, `allXor` has at least one set bit.

Choose one differing bit:

``` cpp
long long bit = allXor & -allXor;
```

That bit separates x and y into different groups.

Example:

``` text
x = 5 = 101
y = 3 = 011

x^y = 110
```

Choose lowbit:

``` text
010
```

At that bit:

``` text
5 -> 0
3 -> 1
```

Partition every array value by this bit.

Duplicates go into the same bucket and cancel.

ASCII:

``` text
                 chosen bit
                     |
          +----------+----------+
          |                     |
       bit = 0                bit = 1
          |                     |
 duplicates cancel         duplicates cancel
          |                     |
          x                     y
```

This is a beautiful example of:

``` text
XOR + partition by a distinguishing bit
```

------------------------------------------------------------------------

# 13. Bit-by-Bit Counting

Many C problems become:

``` text
for every bit independently:
    count zeros
    count ones
    calculate contribution
```

Suppose:

``` text
A = [1,2,3]
```

Binary:

``` text
1 = 01
2 = 10
3 = 11
```

For bit 0:

``` text
ones = 2
zeros = 1
```

For bit 1:

``` text
ones = 2
zeros = 1
```

The full integer problem has become two tiny counting problems.

### Recognition sentence

> If the objective is a sum of AND/OR/XOR values over many pairs, try
> decomposing contribution by bit.

------------------------------------------------------------------------

# 14. Pair XOR / Pair AND Contribution

## Sum of XOR over unordered pairs

At bit `b`, XOR is 1 only when the pair has different bits.

If:

``` text
ones = c
zeros = n-c
```

number of pairs contributing this bit:

``` text
c * (n-c)
```

Each contributes:

``` text
2^b
```

So:

``` text
contribution =
c * (n-c) * 2^b
```

Diagram:

``` text
bit b

zeros group                ones group
0 0 0                      1 1
 \ | \                    / /
  every cross-group pair
       produces XOR bit 1
```

------------------------------------------------------------------------

## Sum of AND over pairs

AND bit is 1 only if both are 1.

Pairs:

``` text
C(c,2)
```

Contribution:

``` text
C(c,2) * 2^b
```

------------------------------------------------------------------------

## Sum of OR over pairs

OR bit is 0 only when both are zero.

Total pairs:

``` text
C(n,2)
```

zero-zero pairs:

``` text
C(zeros,2)
```

Contributing pairs:

``` text
C(n,2) - C(zeros,2)
```

This is a powerful C-level counting template.

------------------------------------------------------------------------

# 15. Subarray XOR + Prefix Frequency

Suppose you need number of subarrays with XOR exactly `K`.

For prefix XOR:

``` text
pref[r] ^ pref[l-1] = K
```

XOR both sides by `pref[r] ^ K`:

``` text
pref[l-1] = pref[r] ^ K
```

So while scanning current prefix `p`, you need an earlier prefix:

``` text
p ^ K
```

### Example structure

``` text
current prefix = p
target = K

need = p ^ K

answer += frequency[need]
frequency[p]++
```

C++:

``` cpp
unordered_map<long long,long long> freq;
freq[0] = 1;

long long pref = 0;
long long ans = 0;

for (long long x : a) {
    pref ^= x;
    ans += freq[pref ^ K];
    freq[pref]++;
}
```

### Compare with prefix modulo

Math guide:

``` text
pref[r] - pref[l] = K
```

XOR guide:

``` text
pref[r] ^ pref[l] = K
```

Same high-level pattern:

``` text
PREFIX STATE
    +
COMPLEMENT LOOKUP
    +
FREQUENCY MAP
```

------------------------------------------------------------------------

# 16. XOR Invariants Under Operations

Suppose an operation changes two elements by XORing both with `x`:

``` text
a[i] ^= x
a[j] ^= x
```

At each bit, two parities are toggled.

Sometimes the total XOR stays constrained.

The contest habit should be:

``` text
operation
   ↓
write algebraically
   ↓
compute effect on:
sum?
parity?
xor?
popcount parity?
```

### Example mental worksheet

``` text
BEFORE:
total = a1 ^ a2 ^ ... ^ an

OPERATION:
ai -> ai ^ x
aj -> aj ^ x

ASK:
what happens to total XOR?
```

Do not guess from samples---derive the invariant.

------------------------------------------------------------------------

# 17. Constructive XOR

Constructive problems may ask:

``` text
find numbers whose XOR is X
construct array with required XOR
```

Useful identities:

``` text
a ^ a = 0
a ^ 0 = a
```

So to make XOR `X`:

``` text
[X]
```

trivially works if one element is allowed.

To add neutral pairs:

``` text
X ^ y ^ y = X
```

ASCII:

``` text
target XOR = X

[X]                 -> X

[X, 7, 7]           -> X
[X, 7, 7, 12, 12]   -> X
```

This is the XOR version of a **base construction + neutral correction**.

### Recognition sentence

> In XOR construction, look for pairs/groups whose combined XOR is zero
> so they can be added without changing the target.

------------------------------------------------------------------------

# 18. Greedy by Highest Significant Bit

Binary numbers are lexicographically dominated by their highest
differing bit.

Example:

``` text
011111 = 31
100000 = 32
```

Even though the lower number has many 1s:

``` text
32 > 31
```

because bit 5 dominates all lower bits combined.

ASCII:

``` text
bit 5:
A = 0 x x x x x
B = 1 y y y y y
    ^
decision already made
```

This creates greedy strategies:

``` text
try highest bit first
if feasible, keep it
then move downward
```

Common in:

``` text
maximize AND
maximize OR under choices
construct maximum XOR
bitwise answer building
```

### Recognition sentence

> When maximizing an integer answer, decide high bits before low bits
> because the highest differing bit determines which value is larger.

------------------------------------------------------------------------

# 19. AND Monotonicity

When you AND more values, bits can only disappear.

``` text
x
x & a
x & a & b
x & a & b & c
```

Numerically:

``` text
non-increasing
```

Example:

``` text
1111 = 15
& 1101 = 13
& 1001 = 9
& 0001 = 1
```

Diagram:

``` text
1111
 ↓ bits may die
1101
 ↓
1001
 ↓
0001
```

A bit that becomes 0 can never return to 1 by adding more AND operands.

### Why useful?

For subarrays:

``` text
AND(l,r)
```

as `r` expands is monotonic in bit inclusion.

This can support:

``` text
two pointers in special cases
binary search
compressed distinct-AND states
```

------------------------------------------------------------------------

# 20. OR Monotonicity

Opposite behavior.

As more values are ORed, bits can only appear.

``` text
0001
 ↓
0101
 ↓
1101
 ↓
1111
```

A bit once set stays set.

Useful for:

``` text
minimum length subarray reaching an OR property
distinct OR states
bit coverage
```

### Key contrast

``` text
AND: 1 -> may become 0, never returns
OR:  0 -> may become 1, never disappears
XOR: can toggle repeatedly
```

Memorize this behavior, not just operator definitions.

------------------------------------------------------------------------

# 21. Bitmask Enumeration

For `n` small, each subset corresponds to an n-bit number.

Items:

``` text
A B C
```

Mask:

``` text
000 -> {}
001 -> {A}
010 -> {B}
011 -> {A,B}
100 -> {C}
101 -> {A,C}
110 -> {B,C}
111 -> {A,B,C}
```

So all subsets:

``` cpp
for (int mask = 0; mask < (1 << n); ++mask) {
    for (int i = 0; i < n; ++i) {
        if (mask & (1 << i)) {
            // item i selected
        }
    }
}
```

Complexity:

``` text
O(n * 2^n)
```

### Recognition trigger

``` text
n <= 20-ish
choose any subset
small number of categories
try every combination
```

------------------------------------------------------------------------

# 22. Subset Masks and State Compression

Masks are not only for brute force.

They can represent a state compactly.

Example: collected keys among A,B,C,D.

``` text
mask = 1010
```

means:

``` text
A? no
B? yes
C? no
D? yes
```

Operations become bit operations:

``` cpp
mask | (1<<k)       // add
mask & ~(1<<k)      // remove
mask ^ (1<<k)       // toggle
mask & otherMask    // common features
mask | otherMask    // union
```

This bridges toward:

``` text
bitmask DP
BFS with mask state
subset enumeration
```

which is usually beyond A/B but can appear around C/D depending on
constraints.

------------------------------------------------------------------------

# 23. SOS / Submask Thinking --- C Bridge

If you have a mask and need to enumerate all of its submasks:

``` cpp
for (int sub = mask; ; sub = (sub - 1) & mask) {
    // use sub
    if (sub == 0) break;
}
```

Example:

``` text
mask = 1101
```

Only positions already set in `mask` may appear in `sub`.

Conceptually:

``` text
mask bits:
1 1 0 1
| |   |
each set bit can be kept or removed
```

If mask has `k` set bits:

``` text
number of submasks = 2^k
```

### Recognition trigger

``` text
for each subset, inspect its subsets
compatible feature masks
partition selected bits
```

Do not prioritize SOS DP yet for normal A/B/C training; learn the
submask idea first.

------------------------------------------------------------------------

# 24. XOR Basis Intuition --- Beyond Normal C

A linear XOR basis is the XOR analogue of independent vectors.

Given numbers, we want a small set of independent bit patterns from
which every reachable XOR can be formed.

Example intuition:

``` text
0011
0101
0110
```

But:

``` text
0011 ^ 0101 = 0110
```

so the third value adds no new XOR capability.

The basis keeps only independent pivots.

Think:

``` text
Gaussian elimination
but over bits
and arithmetic is XOR
```

This is generally a later topic---often harder C/D and above---but
recognizing the phrase:

``` text
maximum subset XOR
all possible XOR combinations
```

should eventually trigger:

``` text
linear basis
```

For your current A/B/C target, master sections 1--23 first.

------------------------------------------------------------------------

# 25. 60-Second Recognition Map

``` text
                     BITWISE SIGNAL
                          |
        +-----------------+-----------------+
        |                                   |
    one integer                         array/subarray
        |                                   |
 power of 2?                           range XOR?
 popcount?                                 |
 set/clear bit?                        prefix XOR
 lowbit?                                   |
                                      target XOR?
                                           |
                                  frequency lookup
```

For pair/counting problems:

``` text
sum over many pairs
        |
 AND / OR / XOR?
        |
count zeros/ones at each bit
        |
bit contribution
```

For construction:

``` text
need target XOR
      |
neutral pairs y,y?
      |
base + zero-XOR additions
```

For maximize/minimize:

``` text
integer answer
      |
try highest bit first
      |
can this bit remain feasible?
```

For subset constraints:

``` text
n small
  |
2^n feasible?
  |
bitmask
```

------------------------------------------------------------------------

# 26. Common Wrong Ideas

## Wrong 1 --- treating XOR like addition

Generally:

``` text
a ^ b != a + b
```

XOR is addition **without carry** at each bit.

Example:

``` text
3 = 11
1 = 01

3 + 1 = 100 = 4
3 ^ 1 = 010 = 2
```

------------------------------------------------------------------------

## Wrong 2 --- precedence bug

Bad:

``` cpp
if (x & (x-1) == 0)
```

Write explicitly:

``` cpp
if ((x & (x - 1)) == 0)
```

Parentheses make the intended expression clear.

------------------------------------------------------------------------

## Wrong 3 --- shifting an `int` too far

Prefer:

``` cpp
1LL << bit
```

when working with `long long`.

------------------------------------------------------------------------

## Wrong 4 --- assuming XOR is monotonic

It is not.

``` text
1 ^ 2 = 3
3 ^ 3 = 0
0 ^ 7 = 7
```

It can rise, fall, and toggle.

AND/OR have useful monotonic bit behavior; XOR does not.

------------------------------------------------------------------------

## Wrong 5 --- checking whole integers when bits are independent

For sums over pairwise XOR/AND/OR, ask:

``` text
what does one bit contribute?
```

------------------------------------------------------------------------

## Wrong 6 --- O(n²) subarray XOR

Use prefix XOR.

For target counting:

``` text
prefix XOR + hashmap
```

------------------------------------------------------------------------

## Wrong 7 --- using bitmask brute force when n is large

``` text
2^40
```

is impossible for normal enumeration.

Always estimate:

``` text
2^20 ≈ 1,048,576
2^25 ≈ 33 million
2^30 ≈ 1 billion
```

------------------------------------------------------------------------

# 27. Practice Ladder

## Level 1 --- A: binary basics / powers of two

1.  CF 1475A --- Odd Divisor\
    https://codeforces.com/problemset/problem/1475/A

2.  CSES --- Bit Strings\
    https://cses.fi/problemset/task/1617

3.  CSES --- Missing Number\
    https://cses.fi/problemset/task/1083

4.  CF 1609A --- Divide and Multiply\
    https://codeforces.com/problemset/problem/1609/A

5.  CF 1095C --- Powers Of Two\
    https://codeforces.com/problemset/problem/1095/C

------------------------------------------------------------------------

## Level 2 --- A/B: XOR identities and construction

1.  CF 1325D --- Ehab the Xorcist\
    https://codeforces.com/problemset/problem/1325/D

2.  CF 1497C1 --- k-LCM (easy construction mindset)\
    https://codeforces.com/problemset/problem/1497/C1

3.  CF 1497C2 --- k-LCM (harder construction mindset)\
    https://codeforces.com/problemset/problem/1497/C2

4.  CSES --- Gray Code\
    https://cses.fi/problemset/task/2205

5.  CF 1625A --- Ancient Civilization\
    https://codeforces.com/problemset/problem/1625/A

------------------------------------------------------------------------

## Level 3 --- B/C: bit-by-bit decisions

1.  CF 1625A --- Ancient Civilization\
    https://codeforces.com/problemset/problem/1625/A

2.  CF 1527A --- And Then There Were K\
    https://codeforces.com/problemset/problem/1527/A

3.  CF 1368D --- AND, OR and square sum\
    https://codeforces.com/problemset/problem/1368/D

4.  CF 1395C --- Boboniu and Bit Operations\
    https://codeforces.com/problemset/problem/1395/C

5.  CF 1554C --- Mikasa\
    https://codeforces.com/problemset/problem/1554/C

------------------------------------------------------------------------

## Level 4 --- C: prefix XOR / subarray reasoning

1.  CSES --- Range Xor Queries\
    https://cses.fi/problemset/task/1650

2.  CSES --- Subarray XOR Queries / use prefix-XOR concept where
    applicable in XOR range tasks

3.  CF 1732C1 --- Sheikh (Easy Version)\
    https://codeforces.com/problemset/problem/1732/C1

4.  CF 1732C2 --- Sheikh (Hard Version)\
    https://codeforces.com/problemset/problem/1732/C2

5.  CF 1457C-adjacent practice: focus on prefix-state transformations
    rather than tags.

------------------------------------------------------------------------

## Level 5 --- C bridge: masks / advanced bit reasoning

1.  CF 1658D1 --- 388535 (Easy Version)\
    https://codeforces.com/problemset/problem/1658/D1

2.  CF 1658D2 --- 388535 (Hard Version)\
    https://codeforces.com/problemset/problem/1658/D2

3.  CF 1498B --- Box Fitting\
    https://codeforces.com/problemset/problem/1498/B

4.  CF 1395C --- Boboniu and Bit Operations\
    https://codeforces.com/problemset/problem/1395/C

5.  CF 1554C --- Mikasa\
    https://codeforces.com/problemset/problem/1554/C

6.  CF 1368D --- AND, OR and square sum\
    https://codeforces.com/problemset/problem/1368/D

------------------------------------------------------------------------

# 28. Contest Notebook Template

Use this after a useful bit problem:

``` text
Problem:
Rating:

RAW CONDITION:
________________________________

BINARY VERSION:
________________________________

WHICH OPERATOR?
[ ] AND
[ ] OR
[ ] XOR
[ ] SHIFT
[ ] POPCOUNT
[ ] MASK

DO BITS ACT INDEPENDENTLY?
________________________________

BIT b CONDITION:
________________________________

IS THIS REALLY:
[ ] parity
[ ] frequency
[ ] prefix state
[ ] invariant
[ ] construction
[ ] greedy high-bit first
[ ] subset mask

KEY IDENTITY:
________________________________

EXAMPLE IN BINARY:
________________________________

WHY IT WORKS:
________________________________

WRONG IDEA:
________________________________

SMALLEST COUNTEREXAMPLE:
________________________________

RECOGNITION SENTENCE:
"When I see __________________,
I will test __________________."
```

------------------------------------------------------------------------

# Final Recognition Summary

For Div2 A/B/C, the highest-value bit patterns are:

``` text
1. power of two: n&(n-1)
2. lowbit / remove lowest set bit
3. XOR cancellation
4. XOR = parity per bit
5. prefix XOR
6. target subarray XOR = prefix complement lookup
7. count pair contribution bit by bit
8. high-bit-first greedy
9. AND loses bits monotonically
10. OR gains bits monotonically
11. constructive XOR using neutral pairs
12. bitmask subset enumeration
```

The most important conceptual upgrade is:

``` text
DON'T ASK:
"What trick works on this integer?"

ASK:
"What happens independently at bit 0, bit 1, bit 2, ...?"
```

For C-level problems, then ask for the second technique:

``` text
BITWISE
   +
frequency / prefix / greedy / construction / binary search / masks
   =
C solution
```

Your contest pipeline:

``` text
STATEMENT
   ↓
WRITE SMALL VALUES IN BINARY
   ↓
IDENTIFY AND / OR / XOR BEHAVIOR
   ↓
CHECK EACH BIT INDEPENDENTLY
   ↓
LOOK FOR PARITY / CANCELLATION / MONOTONICITY
   ↓
COMBINE WITH PREFIX / COUNTING / GREEDY / CONSTRUCTION
   ↓
PROVE
   ↓
CODE
```
