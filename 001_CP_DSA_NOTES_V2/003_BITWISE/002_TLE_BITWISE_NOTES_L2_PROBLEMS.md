# Bit Manipulation — Problem Solving 3

> Pattern-wise revision notes based on the uploaded **TLE Level 2 — Bit Manipulation, Problem Solving 3** lecture.
>
> Focus: **recognition → bit-by-bit derivation → dry run → pseudocode → C++**.

---

# Table of Contents

1. [Pattern 1 — Bit Contribution / Choose K Bits](#pattern-1--bit-contribution--choose-k-bits)
   - [Problem 1 — Maximum AND](#problem-1--maximum-and)
2. [Pattern 2 — Count Set Bits from 0 to N](#pattern-2--count-set-bits-from-0-to-n)
   - [Problem 2 — Masked Popcount](#problem-2--masked-popcount)
3. [Final Pattern Recognition Sheet](#final-pattern-recognition-sheet)

---

# Pattern 1 — Bit Contribution / Choose K Bits

## When should I recognize this pattern?

Look for:

```text
Choose/build a number X
X must contain exactly K set bits
Objective contains:

(A1 & X) + (A2 & X) + ... + (AN & X)
```

The important observation is:

> Do **not** try possible values of `X`.  
> Analyze the answer **one bit position at a time**.

---

## Core Transformation

Suppose bit `j` is set in `X`.

For one number `Ai`:

```text
Ai bit j    X bit j     contribution of bit j in (Ai & X)

    0           1                       0
    1           1                      2^j
```

Therefore if `cnt[j]` array values contain bit `j`:

```text
contribution[j] = cnt[j] * 2^j
```

So the original problem becomes:

```text
Choose exactly K bit positions
having maximum total contribution.
```

If multiple choices produce the same maximum sum, choose the bit positions that produce the **smallest X**.

That means:

```text
higher contribution first

tie in contribution
    ↓
smaller bit position first
```

---

# Problem 1 — Maximum AND

Problem Link: [CodeChef — Maximum AND](https://www.codechef.com/problems/MAXAND18)

**Problem Summary:** Given `N` numbers, construct a number `X` with exactly `K` set bits so that

```text
(A1 & X) + (A2 & X) + ... + (AN & X)
```

is maximized. If several values of `X` give the same maximum sum, choose the smallest `X`.

---

## Core Invariant / Key Insight

Each bit contributes independently.

For bit `j`:

```text
cnt[j] = number of Ai having bit j set

contribution[j] = cnt[j] * 2^j
```

Choose the `K` largest contributions.

For equal contribution:

```text
prefer smaller j
```

because `2^j` is smaller, making `X` smaller.

---

## Why `cnt[j] * 2^j`?

Take bit:

```text
j = 2

2^2 = 4
```

Suppose:

```text
5 array elements have bit 2 set.
```

If bit `2` is NOT selected in `X`:

```text
X bit 2 = 0

Ai & X at bit 2 = 0
for every Ai

contribution = 0
```

If bit `2` IS selected:

```text
X bit 2 = 1
```

Every `Ai` whose bit 2 is `1` contributes `4`.

```text
5 numbers × 4
= 20
```

Hence:

```text
contribution[2] = 5 * 2^2
                = 20
```

---

## Bit-by-Bit Dry Run

Use:

```text
A = [5, 3, 10, 7]
K = 2
```

Binary:

```text
decimal       bit3 bit2 bit1 bit0

5   = 0101      0    1    0    1
3   = 0011      0    0    1    1
10  = 1010      1    0    1    0
7   = 0111      0    1    1    1
```

---

### Step 1 — Count bit 0

```text
bit 0 column:

5   -> 1
3   -> 1
10  -> 0
7   -> 1

cnt[0] = 3
```

Value of bit:

```text
2^0 = 1
```

Contribution:

```text
3 × 1 = 3
```

So:

```text
bit 0 -> contribution 3
```

---

### Step 2 — Count bit 1

```text
bit 1 column:

5   -> 0
3   -> 1
10  -> 1
7   -> 1

cnt[1] = 3
```

Value:

```text
2^1 = 2
```

Contribution:

```text
3 × 2 = 6
```

So:

```text
bit 1 -> contribution 6
```

---

### Step 3 — Count bit 2

```text
bit 2 column:

5   -> 1
3   -> 0
10  -> 0
7   -> 1

cnt[2] = 2
```

Value:

```text
2^2 = 4
```

Contribution:

```text
2 × 4 = 8
```

So:

```text
bit 2 -> contribution 8
```

---

### Step 4 — Count bit 3

```text
bit 3 column:

5   -> 0
3   -> 0
10  -> 1
7   -> 0

cnt[3] = 1
```

Value:

```text
2^3 = 8
```

Contribution:

```text
1 × 8 = 8
```

So:

```text
bit 3 -> contribution 8
```

---

## Contribution Table

```text
bit j       count       2^j       contribution

  0           3          1             3
  1           3          2             6
  2           2          4             8
  3           1          8             8
```

Need:

```text
K = 2 bits
```

Largest contributions:

```text
bit 2 -> 8
bit 3 -> 8
```

Tie:

```text
8 == 8
```

For the tie, smaller bit position comes first:

```text
bit 2 before bit 3
```

But since `K = 2`, both are selected.

Therefore:

```text
X = (1 << 2) | (1 << 3)

  0100
| 1000
------
  1100
```

Thus:

```text
X = 12
```

---

## Verify the Objective

```text
X = 12 = 1100
```

Now:

```text
5  & 12

0101
1100
----
0100 = 4
```

```text
3 & 12

0011
1100
----
0000 = 0
```

```text
10 & 12

1010
1100
----
1000 = 8
```

```text
7 & 12

0111
1100
----
0100 = 4
```

Total:

```text
4 + 0 + 8 + 4
= 16
```

Contribution view gives the same result:

```text
bit 2 contribution = 8
bit 3 contribution = 8

total = 16
```

This is why bit contribution avoids repeatedly computing every `Ai & X`.

---

## Tie-Break Dry Run

Suppose:

```text
bit 1 contribution = 8
bit 3 contribution = 8

K = 1
```

Both produce the same objective contribution.

Possible choices:

```text
choose bit 1:
X = 0010 = 2

choose bit 3:
X = 1000 = 8
```

Need smallest `X`:

```text
2 < 8
```

So choose:

```text
bit 1
```

Hence tie rule:

```text
same contribution
      ↓
smaller bit index
```

---

## Step-by-Step Algorithm

1. Create a list of `(contribution, bitPosition)`.
2. For every relevant bit `j`, count how many `Ai` have bit `j` set.
3. Compute:

```text
contribution = count × 2^j
```

4. Sort by:
   - contribution descending
   - bit position ascending
5. Take the first `K` bit positions.
6. Set those bits in `X`.
7. Output `X`.

---

## Pseudocode

```text
read N, K
read array A

bits = empty list

for j = 0 to MAX_BIT:
    count = 0

    for each x in A:
        if bit j of x is set:
            count++

    contribution = count * 2^j

    add (contribution, j) to bits

sort bits by:
    contribution descending
    if equal:
        bit position ascending

X = 0

for first K entries:
    j = bit position
    X = X OR (1 << j)

print X
```

---

## C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int T;
    cin >> T;

    while (T--) {
        int n, k;
        cin >> n >> k;

        vector<long long> a(n);

        for (long long &x : a) {
            cin >> x;
        }

        vector<pair<long long, int>> bits;

        for (int bit = 0; bit <= 30; bit++) {

            long long cnt = 0;

            for (long long x : a) {
                if (x & (1LL << bit)) {
                    cnt++;
                }
            }

            long long contribution =
                cnt * (1LL << bit);

            bits.push_back({contribution, bit});
        }

        sort(bits.begin(), bits.end(),
             [](const auto &a, const auto &b) {

                 if (a.first != b.first) {
                     return a.first > b.first;
                 }

                 return a.second < b.second;
             });

        long long X = 0;

        for (int i = 0; i < k; i++) {
            int bit = bits[i].second;
            X |= (1LL << bit);
        }

        cout << X << '\n';
    }
}
```

---

## Pattern Recognition

```text
SUM of:
(Ai & X)

      ↓

AND contribution

      ↓

Think bit-by-bit

      ↓

For each bit j:
count Ai with bit j = 1

      ↓

contribution[j]
= count[j] * 2^j

      ↓

choose best K bits
```

---

# Pattern 2 — Count Set Bits from 0 to N

## When should I recognize this pattern?

Look for:

```text
0 <= k <= N

sum popcount(k & M)
```

or:

```text
For every number from 0 to N,
count how often some bit is set.
```

Constraints may be huge:

```text
N up to around 2^60
```

So this is impossible:

```cpp
for (long long k = 0; k <= N; k++)
```

Instead:

> Process every bit independently and count how many numbers from `0` through `N` contain that bit.

---

# Problem 2 — Masked Popcount

Problem Link: [AtCoder ABC356 D — Masked Popcount](https://atcoder.jp/contests/abc356/tasks/abc356_d)

**Problem Summary:** Compute the sum of the number of set bits in `k & M` for every integer `k` from `0` through `N`, modulo `998244353`.

Conceptually:

```text
answer =
popcount(0 & M)
+ popcount(1 & M)
+ ...
+ popcount(N & M)
```

---

## Core Invariant / Key Insight

Only bit positions that are set in `M` can contribute.

For each set bit `i` of `M`:

```text
count how many numbers in [0, N]
have bit i = 1
```

Add that count to the answer.

Why?

Each such number contributes exactly one set bit at position `i` to:

```text
popcount(k & M)
```

---

# Part 1 — Why Process Bits Independently?

Suppose:

```text
M = 1010
```

Only these bits matter:

```text
bit 3 = 1
bit 1 = 1
```

Bits `2` and `0` of `M` are zero.

For any `k`:

```text
k & M
```

can only retain:

```text
bit 3
bit 1
```

Therefore:

```text
answer
=
count of k where bit 3 is 1
+
count of k where bit 1 is 1
```

for:

```text
0 <= k <= N
```

---

# Part 2 — Pattern of One Bit

Take:

```text
i = 2
```

Bit value:

```text
2^2 = 4
```

Write bit `2` for consecutive integers:

```text
number       bit 2

0              0
1              0
2              0
3              0

4              1
5              1
6              1
7              1

8              0
9              0
10             0
11             0

12             1
13             1
14             1
15             1
```

Pattern:

```text
0000 1111 0000 1111 ...
^^^^ ^^^^
 4     4
```

So for bit `i`:

```text
zeros length = 2^i
ones length  = 2^i

cycle length = 2^(i+1)
```

This repeating cycle is the whole trick.

---

# Part 3 — Count Complete Cycles

Let:

```text
total = N + 1
```

Why `N + 1`?

Because range:

```text
0 ... N
```

contains:

```text
N + 1 numbers
```

For bit `i`:

```text
half  = 2^i
cycle = 2^(i+1)
```

Every complete cycle contains:

```text
half
```

numbers where bit `i` is `1`.

Therefore:

```text
fullCycles = total / cycle

ones from full cycles
=
fullCycles * half
```

---

# Part 4 — Count the Remaining Partial Cycle

After complete cycles:

```text
remainder = total % cycle
```

The remainder starts with:

```text
half zeros
```

and only then contains ones.

So:

```text
extra ones =
max(0, remainder - half)
```

Total ones at bit `i`:

```text
ones =
(total / cycle) * half
+
max(0, total % cycle - half)
```

This is the central formula.

---

# Detailed Bit-by-Bit Dry Run

Use:

```text
N = 10
M = 5
```

Binary:

```text
N = 10 = 1010
M =  5 = 0101
```

`M` has:

```text
bit 0 = 1
bit 2 = 1
```

Therefore only bits `0` and `2` contribute.

Range:

```text
0 ... 10
```

contains:

```text
11 numbers
```

So:

```text
total = N + 1 = 11
```

---

## Bit 0

```text
i = 0

half  = 2^0 = 1
cycle = 2^1 = 2
```

Pattern:

```text
number:  0 1 2 3 4 5 6 7 8 9 10
bit 0 :  0 1 0 1 0 1 0 1 0 1  0
```

Complete cycles:

```text
11 / 2 = 5
```

Each cycle contributes:

```text
1 one
```

Therefore:

```text
5 × 1 = 5
```

Remainder:

```text
11 % 2 = 1
```

Extra:

```text
max(0, 1 - 1)
= 0
```

Total:

```text
bit 0 count = 5
```

---

## Bit 2

```text
i = 2

half  = 2^2 = 4
cycle = 2^3 = 8
```

Pattern:

```text
number:  0 1 2 3 | 4 5 6 7 | 8 9 10
bit 2 :  0 0 0 0 | 1 1 1 1 | 0 0  0
```

Complete cycles:

```text
11 / 8 = 1
```

Each full cycle contains:

```text
4 ones
```

Contribution:

```text
1 × 4 = 4
```

Remainder:

```text
11 % 8 = 3
```

The remaining `3` positions are still inside the zero section:

```text
000
```

Extra:

```text
max(0, 3 - 4)
= 0
```

Therefore:

```text
bit 2 count = 4
```

---

## Final Answer

Only bits set in `M` contribute:

```text
bit 0 -> 5
bit 2 -> 4
```

Therefore:

```text
answer = 5 + 4
       = 9
```

---

# Direct Verification

For learning only, calculate every value:

```text
M = 5 = 0101

k      binary     k & M       popcount

0      0000       0000           0
1      0001       0001           1
2      0010       0000           0
3      0011       0001           1
4      0100       0100           1
5      0101       0101           2
6      0110       0100           1
7      0111       0101           2
8      1000       0000           0
9      1001       0001           1
10     1010       0000           0
                                ---
                                 9
```

Matches:

```text
bit contribution method = 9
```

---

# Second Dry Run — Partial Cycle Produces Extra Ones

Use:

```text
N = 13
```

Count numbers from:

```text
0 ... 13
```

having bit:

```text
i = 2
```

Total numbers:

```text
total = 14
```

For bit `2`:

```text
half  = 4
cycle = 8
```

Full cycles:

```text
14 / 8 = 1
```

Contribution:

```text
1 × 4 = 4
```

Remainder:

```text
14 % 8 = 6
```

A partial cycle looks like:

```text
positions: 0 1 2 3 4 5
bit 2:     0 0 0 0 1 1
```

First:

```text
4 positions are zeros
```

Remaining:

```text
6 - 4 = 2
```

are ones.

So:

```text
extra = 2
```

Total:

```text
4 + 2 = 6
```

Verify:

```text
bit 2 is set in:

4,5,6,7
12,13

= 6 numbers
```

---

# Visual Formula

For bit `i`:

```text
half = 2^i

cycle:

|------ 2^i ------|------ 2^i ------|
|      ZERO       |       ONE       |
|-----------------|-----------------|

total cycle length = 2^(i+1)
```

For range size:

```text
total = N + 1
```

Then:

```text
complete cycles
        |
        v
(total / 2^(i+1)) * 2^i

+

partial cycle
        |
        v
max(0, total % 2^(i+1) - 2^i)
```

Hence:

```text
countOnes(i) =
((N + 1) / 2^(i+1)) * 2^i
+
max(0,
    ((N + 1) % 2^(i+1)) - 2^i)
```

---

# Step-by-Step Algorithm

1. Set `total = N + 1`.
2. Iterate through bit positions.
3. Skip bit `i` if bit `i` is not set in `M`.
4. Let:

```text
half  = 2^i
cycle = 2^(i+1)
```

5. Count ones from complete cycles.
6. Count extra ones from the remainder.
7. Add this count to the answer.
8. Apply modulo `998244353`.

---

# Pseudocode

```text
MOD = 998244353

answer = 0
total = N + 1

for i = 0 to 60:

    if bit i of M is not set:
        continue

    half = 2^i
    cycle = 2^(i+1)

    fullCycles = total / cycle

    ones = fullCycles * half

    remainder = total % cycle

    extra = max(0, remainder - half)

    ones += extra

    answer += ones
    answer %= MOD

print answer
```

---

# C++ Code

```cpp
#include <bits/stdc++.h>
using namespace std;

using int64 = long long;
using i128 = __int128_t;

static const long long MOD = 998244353;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    unsigned long long N, M;
    cin >> N >> M;

    long long ans = 0;

    // __int128 avoids overflow when forming N + 1 or 2^(i+1).
    i128 total = (i128)N + 1;

    for (int i = 0; i < 60; i++) {

        if ((M & (1ULL << i)) == 0) {
            continue;
        }

        i128 half = (i128)1 << i;
        i128 cycle = (i128)1 << (i + 1);

        i128 fullCycles = total / cycle;

        i128 ones = fullCycles * half;

        i128 remainder = total % cycle;

        if (remainder > half) {
            ones += remainder - half;
        }

        ans += (long long)(ones % MOD);
        ans %= MOD;
    }

    cout << ans << '\n';
}
```

---

# Alternative Way to Understand the Remainder

Instead of memorizing:

```text
max(0, remainder - half)
```

visualize one cycle.

For `i = 3`:

```text
half = 8
cycle = 16

positions inside cycle:

0 1 2 3 4 5 6 7 | 8 9 10 11 12 13 14 15
----------------|------------------------
0 0 0 0 0 0 0 0 | 1 1  1  1  1  1  1  1
```

If remainder is:

```text
5
```

we consumed only:

```text
00000
```

Extra ones:

```text
0
```

If remainder is:

```text
11
```

we consumed:

```text
00000000 111
```

Extra ones:

```text
11 - 8 = 3
```

That directly explains:

```text
max(0, remainder - half)
```

---

# Why the Formula Uses `N + 1`

This is a common off-by-one trap.

If:

```text
N = 5
```

the numbers are:

```text
0,1,2,3,4,5
```

Count:

```text
6
```

Therefore:

```text
range length = N + 1
```

not:

```text
N
```

---

# Complexity

We inspect about `60` bit positions.

```text
Time  = O(log N)
Space = O(1)
```

There is no loop from:

```text
0 ... N
```

---

# Pattern Recognition

```text
sum popcount(k & M)
for k = 0 ... N

          ↓

AND with M means
only set bits of M matter

          ↓

process each bit independently

          ↓

for bit i:
count numbers in [0,N]
whose bit i is 1

          ↓

bit pattern repeats:

0...0 1...1
 2^i   2^i

          ↓

cycle length = 2^(i+1)

          ↓

full cycles + remainder
```

---

# Final Pattern Recognition Sheet

## Pattern A — Contribution of a Chosen Bit

```text
Expression:

Σ (Ai & X)

X has exactly K set bits

        ↓

For each bit j:

cnt[j]
= how many Ai have bit j set

contribution[j]
= cnt[j] * 2^j

        ↓

Choose K best contributions

Tie:
smaller bit position
to minimize X
```

### Trigger Words

```text
construct X
exactly K set bits
maximize sum
Ai & X
minimum X on tie
```

---

## Pattern B — Count a Bit Over `[0, N]`

```text
Need:

how many x in [0,N]
have bit i set?

half  = 2^i
cycle = 2^(i+1)
total = N + 1

ones =
(total / cycle) * half
+
max(0, total % cycle - half)
```

### Trigger Words

```text
0 to N
huge N
sum of popcounts
masked popcount
count set bit frequency
```

---

# 60-Second Revision

```text
============================================================
                 BIT CONTRIBUTION
============================================================

Σ(Ai & X)

For bit j:

cnt[j] = count(Ai where bit j = 1)

value of bit j:
2^j

contribution:
cnt[j] * 2^j

Choose K largest.

Tie contribution?
Choose smaller bit index if X must be minimum.

============================================================
            COUNT BIT i FROM 0 TO N
============================================================

total = N + 1

half  = 2^i
cycle = 2^(i+1)

pattern:

0000....0000 1111....1111
<---half---> <---half--->

full:
(total / cycle) * half

remaining:
rem = total % cycle

extra:
max(0, rem - half)

count:
full + extra

============================================================
               MASKED POPCOUNT
============================================================

For every bit i:

if M bit i == 0:
    skip

if M bit i == 1:
    answer += count of numbers 0..N
              having bit i set

Time:
O(60)

============================================================
