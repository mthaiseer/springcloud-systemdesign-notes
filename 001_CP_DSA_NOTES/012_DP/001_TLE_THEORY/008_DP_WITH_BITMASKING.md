# 009 — DP with Bitmasking
## DP Advanced — Subset Representation → Matching → Permutation DP

> **Self-study goal:** Learn how to use a single integer `mask` to represent a subset inside a DP state.
>
> This lecture builds the idea in this order:
>
> **Normal subset storage → Bitmask representation → Bit operations → DP state with mask → Permutation DP → AtCoder DP O Matching → State elimination with `popcount` → Minimum-cost point ordering**
>
> Use the same DP construction flow:
>
> **Problem Asking → Story Removed → Observation → State → Choices → Transition → Base Case → Final Subproblem → Complexity → Optimization → Dry Run → C++17**

---

# Problem / Lecture Links

1. **AtCoder Educational DP Contest O — Matching**  
   https://atcoder.jp/contests/dp/tasks/dp_o

2. **Lecture Problem 2 — Minimum-distance permutation of points**  
   Given `N <= 15` points, rearrange them so that the sum of Manhattan distances between adjacent points is minimum.

---

# Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Why Bitmasking Appears in DP](#2-why-bitmasking-appears-in-dp)
3. [Normal Subset Representation](#3-normal-subset-representation)
4. [Operations We Need on a Subset](#4-operations-we-need-on-a-subset)
5. [Why Vector / Set Storage Is Expensive](#5-why-vector--set-storage-is-expensive)
6. [Represent a Subset as Bits](#6-represent-a-subset-as-bits)
7. [Binary Mask to Decimal Integer](#7-binary-mask-to-decimal-integer)
8. [Core Bit Operations](#8-core-bit-operations)
9. [Set Operations with Bitmasks](#9-set-operations-with-bitmasks)
10. [Important Limitation on N](#10-important-limitation-on-n)
11. [Using Bitmasks Inside DP](#11-using-bitmasks-inside-dp)
12. [Warm-up — Counting Permutations](#12-warm-up--counting-permutations)
13. [State Elimination with Popcount](#13-state-elimination-with-popcount)
14. [Problem 1 — AtCoder DP O Matching](#14-problem-1--atcoder-dp-o-matching)
15. [Matching — State](#15-matching--state)
16. [Matching — Transition](#16-matching--transition)
17. [Matching — Dry Run](#17-matching--dry-run)
18. [Matching — Optimized DP with Only Mask](#18-matching--optimized-dp-with-only-mask)
19. [Matching — C++17](#19-matching--c17)
20. [Problem 2 — Minimum-Cost Ordering of Points](#20-problem-2--minimum-cost-ordering-of-points)
21. [Manhattan Distance Prerequisite](#21-manhattan-distance-prerequisite)
22. [Naive State with Index + Mask + Last](#22-naive-state-with-index--mask--last)
23. [Eliminate Index Using Popcount](#23-eliminate-index-using-popcount)
24. [Optimized State `dp[mask][last]`](#24-optimized-state-dpmasklast)
25. [Point Ordering — Transition](#25-point-ordering--transition)
26. [Point Ordering — Dry Run](#26-point-ordering--dry-run)
27. [Point Ordering — C++17](#27-point-ordering--c17)
28. [Pattern Comparison](#28-pattern-comparison)
29. [Contest Recognition Checklist](#29-contest-recognition-checklist)
30. [Final Mental Model](#30-final-mental-model)

---

# 1. Prerequisites

Before DP with bitmasking, make sure these ideas are comfortable.

---

## 1.1 Binary numbers

A binary number uses only:

```text
0
1
```

Example:

```text
1011₂
```

means:

```text
1×2³ + 0×2² + 1×2¹ + 1×2⁰
=
8 + 0 + 2 + 1
=
11
```

Each binary position is called a:

```text
bit
```

---

## 1.2 Powers of two

Important values:

```text
2⁰ = 1
2¹ = 2
2² = 4
2³ = 8
2⁴ = 16
...
```

In C++:

```cpp
1 << i
```

represents:

```text
2^i
```

Example:

```cpp
1 << 0   // 1
1 << 1   // 2
1 << 2   // 4
1 << 3   // 8
```

---

## 1.3 Bitwise AND

Operator:

```cpp
&
```

Bit result is `1` only when both bits are `1`.

```text
1 & 1 = 1
1 & 0 = 0
0 & 1 = 0
0 & 0 = 0
```

Useful for:

```text
checking whether a bit is set
```

---

## 1.4 Bitwise OR

Operator:

```cpp
|
```

Result is `1` when at least one bit is `1`.

Useful for:

```text
setting a bit
union of subsets
```

---

## 1.5 Bitwise XOR

Operator:

```cpp
^
```

Result is `1` when the two bits are different.

Useful in this lecture for:

```text
elements present in exactly one of two subsets
```

---

## 1.6 Bitwise NOT

Operator:

```cpp
~
```

Flips bits.

It is useful when clearing a specific bit:

```cpp
mask &= ~(1 << i);
```

---

## 1.7 Popcount

`popcount(mask)` means:

```text
number of set bits in mask
```

Example:

```text
mask = 10110
```

contains:

```text
3 set bits
```

C++:

```cpp
__builtin_popcount(mask)
```

For `long long`:

```cpp
__builtin_popcountll(mask)
```

This becomes very important later because:

```text
number of selected elements
=
number of set bits
```

---

## 1.8 DP state meaning

As always:

```text
mask is not the answer.
```

It is only part of the state.

Example:

```text
dp[mask]
=
number of ways to complete the assignment
when the elements represented by `mask`
have already been used
```

Always give the state an English meaning.

---

# 2. Why Bitmasking Appears in DP

A lot of DP problems need to remember:

```text
which elements have already been selected?
```

or:

```text
which elements are still available?
```

Suppose:

```text
N = 4
```

and elements are:

```text
[10, 20, 30, 40]
```

A state may need to distinguish:

```text
{10, 30}
```

from:

```text
{20, 30}
```

because the available future choices are different.

This is exactly where bitmasking becomes useful.

---

# 3. Normal Subset Representation

For:

```text
Arr = [10, 20, 30]
```

all subsets are:

```text
{}
{10}
{20}
{30}
{10,20}
{10,30}
{20,30}
{10,20,30}
```

Number of subsets:

```text
2^N
```

because every element has two possibilities:

```text
take
skip
```

---

## 3.1 Why normal storage is wasteful

If every subset is explicitly stored as a vector containing up to `N` elements:

```text
number of subsets = 2^N
```

and each subset may use:

```text
O(N)
```

space.

So total storage can become roughly:

```text
O(N × 2^N)
```

The lecture introduces bitmasks to make each subset representation compact.

---

# 4. Operations We Need on a Subset

Suppose:

```text
Arr = [10,20,30,40,50,60]
```

and current subset is:

```text
{30,40,60}
```

We may need to:

```text
1. Insert arr[i]

2. Check whether arr[i] is already present

3. Delete arr[i]
```

These operations happen repeatedly inside subset DP.

So their cost matters.

---

# 5. Why Vector / Set Storage Is Expensive

## 5.1 Vector

If a subset is stored as:

```cpp
vector<int>
```

then:

```text
search → O(N)

insert at end → O(1)

delete → usually O(N)
```

If this happens for many of the:

```text
2^N
```

subsets, the overhead becomes large.

---

## 5.2 Set

If using:

```cpp
set<int>
```

then operations are approximately:

```text
search → O(log N)

insert → O(log N)

erase  → O(log N)
```

This improves operations, but each subset is still represented as a container.

The lecture therefore asks:

```text
Can the complete subset itself
be represented using one number?
```

Yes.

---

# 6. Represent a Subset as Bits

For an array of size:

```text
N
```

use an `N`-bit binary number.

For every index `i`:

```text
bit i = 1
```

means:

```text
arr[i] is inside the subset
```

and:

```text
bit i = 0
```

means:

```text
arr[i] is not inside the subset
```

---

## 6.1 Example

```text
Arr = [10,20,30,40]
index   0  1  2  3
```

Subset:

```text
{10,30}
```

contains indices:

```text
0 and 2
```

So mask:

```text
0101
```

because:

```text
bit 0 = 1 → 10 present
bit 1 = 0 → 20 absent
bit 2 = 1 → 30 present
bit 3 = 0 → 40 absent
```

---

## 6.2 More examples from the lecture

```text
{}              → 0000

{10}            → 0001

{20}            → 0010

{10,20}         → 0011

{30}            → 0100

{10,30}         → 0101

{20,30}         → 0110

{10,20,30}      → 0111

{40}            → 1000

{30,40}         → 1100

{10,20,30,40}   → 1111
```

Now every subset is represented by one integer mask.

---

# 7. Binary Mask to Decimal Integer

The binary masks can be stored directly as ordinary integers.

For `N = 4`:

```text
0000 → 0
0001 → 1
0010 → 2
0011 → 3
0100 → 4
0101 → 5
0110 → 6
0111 → 7
1000 → 8
...
1111 → 15
```

Therefore all subsets correspond exactly to:

```text
mask = 0 ... (1<<N)-1
```

Number of masks:

```text
2^N
```

---

## 7.1 Empty subset

```text
mask = 0
```

---

## 7.2 Full subset

```text
mask = (1 << N) - 1
```

Example:

```text
N = 4

1 << 4 = 10000₂

(1 << 4) - 1
=
01111₂
=
1111₂
```

So:

```text
fullMask = 15
```

---

# 8. Core Bit Operations

These are the operations used repeatedly in bitmask DP.

---

## 8.1 Check whether index `i` is present

Use:

```cpp
mask & (1 << i)
```

If result is non-zero:

```text
bit i is set
```

Example:

```text
mask = 0101
```

Check index `2`:

```text
0101
0100
----
0100 ≠ 0
```

So index `2` is present.

C++:

```cpp
if (mask & (1 << i)) {
    // i is already used
}
```

---

## 8.2 Check whether index `i` is NOT present

```cpp
if ((mask & (1 << i)) == 0) {
    // i is free
}
```

This is one of the most common checks in subset DP.

---

## 8.3 Insert / set index `i`

Use OR:

```cpp
mask | (1 << i)
```

or modify in place:

```cpp
mask |= (1 << i);
```

Example:

```text
mask = 0001

set bit 2:

0001
0100
----
0101
```

---

## 8.4 Delete / clear index `i`

Use:

```cpp
mask & ~(1 << i)
```

or:

```cpp
mask &= ~(1 << i);
```

This forces bit `i` to zero.

---

## 8.5 Toggle index `i`

Use:

```cpp
mask ^ (1 << i)
```

If bit was:

```text
0 → becomes 1
1 → becomes 0
```

Use this only when toggle behavior is actually intended.

---

# 9. Set Operations with Bitmasks

The lecture gives three useful operations.

---

## 9.1 Union

```text
S1 ∪ S2
```

becomes:

```cpp
S1 | S2
```

Bits present in either subset remain present.

---

## 9.2 Intersection

```text
S1 ∩ S2
```

becomes:

```cpp
S1 & S2
```

Only bits present in both remain.

---

## 9.3 Elements present in exactly one subset

Symmetric difference:

```cpp
S1 ^ S2
```

A bit survives only when it appears in exactly one mask.

---

# 10. Important Limitation on N

Bitmasking represents:

```text
2^N
```

possible subsets.

The bit operations themselves are fast.

The real problem is:

```text
the number of masks grows exponentially
```

Examples:

```text
N = 10
2^N = 1,024

N = 15
2^N = 32,768

N = 20
2^N = 1,048,576
```

This is why DP with bitmasking typically appears when:

```text
N is small
```

The lecture examples use constraints around:

```text
N <= 15
N <= 20/21
```

---

## 10.1 Important distinction

This is fine:

```text
one mask operation = O(1)
```

But iterating all masks costs:

```text
O(2^N)
```

And if every mask tries all `N` choices:

```text
O(N × 2^N)
```

That is the standard bitmask-DP complexity pattern.

---

# 11. Using Bitmasks Inside DP

The lecture motivation is:

```text
Sometimes a DP state needs to know
which elements have been picked
and which are still free.
```

Then mask becomes a state parameter.

Generic form:

```text
dp[i][mask]
```

Meaning:

```text
we are currently at position / stage i
and `mask` stores the set of already-used elements
```

---

## 11.1 Typical transition

For each candidate element:

```text
j = 0 ... N-1
```

if bit `j` is not set:

```text
choose j
```

and move to:

```text
newMask
=
mask | (1 << j)
```

This gives the classic template:

```cpp
for (int j = 0; j < n; j++) {

    if ((mask & (1 << j)) == 0) {

        int newMask
            =
            mask | (1 << j);

        // transition
    }
}
```

---

# 12. Warm-up — Counting Permutations

The lecture first asks:

```text
How many permutations can be formed
from N unique elements?
```

Mathematically:

```text
N!
```

But the purpose is to understand how permutation construction can be simulated using:

```text
position
+
used-elements mask
```

---

## 12.1 Naive permutation generation

A naive approach can enumerate all permutations.

There are:

```text
N!
```

permutations.

If each permutation operation/processing costs `O(N)`, total work can become:

```text
O(N × N!)
```

This grows extremely quickly.

---

## 12.2 Bitmask-recursion state

Define:

```text
f(index, mask)
```

where:

```text
index
=
position currently being filled
```

and:

```text
mask
=
elements already used
```

---

## 12.3 Base case

When:

```text
index == N
```

all positions have been filled.

Therefore:

```text
return 1
```

for one completed permutation.

---

## 12.4 Transition

Try every unused element:

```text
for j = 0 ... N-1
```

If:

```text
j-th bit is not set
```

then choose it:

```text
f(
    index + 1,
    mask | (1 << j)
)
```

Sum all choices.

---

## 12.5 State meaning

```text
dp[index][mask]
=
number of ways to fill
positions index ... N-1
when the elements in `mask`
have already been used
```

---

## 12.6 Complexity before optimization

Possible masks:

```text
2^N
```

For every mask/stage, try:

```text
N
```

elements.

So the useful-state complexity is around:

```text
O(N × 2^N)
```

per transition layer formulation, rather than factorial enumeration.

The more important idea is that many different permutation prefixes may lead to the same:

```text
set of used elements
```

and therefore the same future subproblem.

That overlap is what DP memoizes.

---

# 13. State Elimination with Popcount

This is one of the most important observations in the lecture.

Suppose state is:

```text
dp[index][mask]
```

But every time we choose one element:

```text
index increases by 1
```

and:

```text
one new bit is set
```

Therefore:

```text
index
=
number of selected elements
=
popcount(mask)
```

So `index` is not independent.

It can be calculated from `mask`.

---

## 13.1 Before elimination

```text
dp[index][mask]
```

---

## 13.2 Dependency

```text
index
=
__builtin_popcount(mask)
```

---

## 13.3 After elimination

```text
dp[mask]
```

This reduces memory from conceptually:

```text
O(N × 2^N)
```

to:

```text
O(2^N)
```

for problems where no other state parameter is needed.

This exact idea is used in the lecture's optimized Matching solution.

---

# 14. Problem 1 — AtCoder DP O Matching

The lecture's first full bitmask-DP problem is the AtCoder Matching problem.

There are:

```text
N men
N women
```

Compatibility matrix:

```text
a[i][j]
```

where:

```text
a[i][j] = 1
```

means:

```text
man i can be paired with woman j
```

Goal:

```text
count the number of perfect matchings
```

such that:

```text
every man gets exactly one woman

every woman is used exactly once

all chosen pairs are compatible
```

The lecture notes:

```text
N <= 21
```

which strongly suggests subset DP.

---

# 15. Matching — State

## 15.1 Straightforward state

Define:

```text
dp[index][mask]
```

where:

```text
index
=
current man to match
```

and:

```text
mask
=
women already used
```

State meaning:

```text
dp[index][mask]
=
number of valid ways to match
men index ... N-1

when the set of already-used women
is represented by `mask`
```

---

## 15.2 Why mask represents women

Each woman can be used only once.

For woman `j`:

```text
bit j = 1
```

means:

```text
woman j is already matched
```

and:

```text
bit j = 0
```

means:

```text
woman j is still available
```

---

# 16. Matching — Transition

At state:

```text
(index, mask)
```

we want to match:

```text
man index
```

Try every woman:

```text
j = 0 ... N-1
```

Two conditions must hold.

---

## 16.1 Woman `j` must be unused

```cpp
(mask & (1 << j)) == 0
```

---

## 16.2 Pair must be compatible

```cpp
compat[index][j] == 1
```

---

## 16.3 If both are true

Pair:

```text
man index
with
woman j
```

and move to:

```text
index + 1
```

New used-women mask:

```text
mask | (1 << j)
```

Therefore:

```text
ways
+=
dp[index+1][mask | (1<<j)]
```

---

## 16.4 BASE CASE

When:

```text
index == N
```

all men have been matched.

Return:

```text
1
```

because we completed one valid matching.

---

## 16.5 FINAL SUBPROBLEM

Initially:

```text
no women are used
```

so:

```text
mask = 0
```

and first man is:

```text
index = 0
```

Therefore:

```text
answer
=
dp[0][0]
```

---

# 17. Matching — Dry Run

Take:

```text
N = 3
```

Suppose compatibility allows all pairs.

Start:

```text
index = 0
mask  = 000
```

---

## 17.1 Match man 0

Available women:

```text
0
1
2
```

Choose woman `0`:

```text
newMask = 001
```

State becomes:

```text
(index=1, mask=001)
```

---

## 17.2 Match man 1

Used:

```text
woman 0
```

Free:

```text
woman 1
woman 2
```

Choose woman `1`:

```text
newMask = 011
```

State:

```text
(index=2, mask=011)
```

---

## 17.3 Match man 2

Only woman `2` remains.

Choose:

```text
newMask = 111
```

Then:

```text
index = 3 = N
```

Return:

```text
1
```

This path represents one matching:

```text
(0,0)
(1,1)
(2,2)
```

Other branches generate other permutations/matchings.

---

# 18. Matching — Optimized DP with Only Mask

The lecture then removes `index`.

Observe:

```text
every matched man consumes exactly one woman
```

Therefore:

```text
current man index
=
number of women already used
=
popcount(mask)
```

So:

```cpp
int index = __builtin_popcount(mask);
```

Now state becomes:

```text
dp[mask]
```

Meaning:

```text
number of ways to complete matching
when the used women are exactly `mask`
```

The current man is automatically:

```text
popcount(mask)
```

---

## 18.1 Why many `(index,mask)` combinations are impossible

For `N = 3`, this state is valid:

```text
index = 2
mask  = 011
```

because mask has:

```text
2 set bits
```

But this is impossible:

```text
index = 2
mask  = 001
```

because only one woman has been used.

So storing all combinations:

```text
index × mask
```

contains many useless states.

This is exactly the state-elimination observation from the lecture.

---

## 18.2 Complexity

Number of masks:

```text
2^N
```

For each mask, try up to:

```text
N
```

women.

Therefore:

```text
Time
=
O(N × 2^N)
```

Space:

```text
O(2^N)
```

The lecture explicitly shows this optimized complexity.

---

# 19. Matching — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll MOD = 1000000007LL;

int n;
vector<vector<int>> compatible;
vector<ll> memo;

ll solve(int mask) {

    int man
        =
        __builtin_popcount(
            static_cast<unsigned int>(mask)
        );

    if (man == n) {
        return 1;
    }

    ll& answer = memo[mask];

    if (answer != -1) {
        return answer;
    }

    answer = 0;

    for (int woman = 0;
         woman < n;
         woman++) {

        bool alreadyUsed
            =
            mask & (1 << woman);

        if (alreadyUsed) {
            continue;
        }

        if (!compatible[man][woman]) {
            continue;
        }

        int nextMask
            =
            mask | (1 << woman);

        answer += solve(nextMask);

        if (answer >= MOD) {
            answer %= MOD;
        }
    }

    return answer %= MOD;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;

    compatible.assign(
        n,
        vector<int>(n)
    );

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            cin >> compatible[i][j];
        }
    }

    int totalMasks = 1 << n;

    memo.assign(
        totalMasks,
        -1
    );

    cout << solve(0) << '\n';

    return 0;
}
```

---

## 19.1 Recognition model

When you see:

```text
N people/items on the left
N choices/items on the right

each right-side item can be used once

compatibility / assignment restrictions

N around 20
```

think:

```text
bitmask DP
```

State:

```text
mask = used right-side items
```

Current left-side position:

```text
popcount(mask)
```

---

# 20. Problem 2 — Minimum-Cost Ordering of Points

The lecture's second problem gives:

```text
N points in a 2D plane
```

Constraint:

```text
N <= 15
```

We may rearrange the points in any order.

Goal:

```text
minimize the sum of distances
between adjacent points
in the final permutation
```

Example points:

```text
(0,0)
(5,6)
(1,2)
```

A good permutation is:

```text
(0,0)
→ (1,2)
→ (5,6)
```

The lecture uses Manhattan distance.

---

# 21. Manhattan Distance Prerequisite

For points:

```text
P1 = (x1,y1)
P2 = (x2,y2)
```

Manhattan distance is:

```text
|x1-x2|
+
|y1-y2|
```

C++:

```cpp
long long distance(
    pair<long long,long long> a,
    pair<long long,long long> b
) {
    return
        llabs(a.first - b.first)
        +
        llabs(a.second - b.second);
}
```

---

# 22. Naive State with Index + Mask + Last

To build a permutation, we need to know:

```text
1. how many positions have been filled

2. which points have already been used

3. which point was placed last
```

A direct state is:

```text
dp[index][mask][last]
```

Meaning:

```text
minimum additional distance needed
to fill positions index ... N-1

when:
- `mask` contains used points
- `last` is the most recently placed point
```

The lecture's first solution uses this kind of state.

---

## 22.1 Why `last` is necessary

The cost of choosing the next point depends on:

```text
distance(last, next)
```

So knowing only:

```text
mask
```

is not enough.

Two permutations may have the same used set but different last points.

Example:

```text
used = {A,B,C}
```

but one prefix ends at:

```text
B
```

and another ends at:

```text
C
```

The next transition cost differs.

Therefore:

```text
last
```

is an independent state parameter.

---

# 23. Eliminate Index Using Popcount

But `index` is not independent.

Every used point fills exactly one position.

Therefore:

```text
index
=
number of used points
=
popcount(mask)
```

So:

```text
dp[index][mask][last]
```

can become:

```text
dp[mask][last]
```

This is the same state-elimination technique used in Matching.

---

## 23.1 Before

```text
index
mask
last
```

---

## 23.2 Dependency

```text
index = popcount(mask)
```

---

## 23.3 After

```text
mask
last
```

This reduces one DP dimension.

The lecture gives the optimized form:

```text
dp[bitmask][last element]
```

---

# 24. Optimized State `dp[mask][last]`

Define:

```text
dp[mask][last]
=
minimum additional distance required
to place all unused points

when:
mask = points already used
last = most recently placed point
```

For the initial state, no point has been chosen yet.

A convenient implementation uses a sentinel:

```text
last = N
```

meaning:

```text
there is no previous point yet
```

For the first chosen point:

```text
added distance = 0
```

because there is no adjacent predecessor.

---

# 25. Point Ordering — Transition

At state:

```text
(mask,last)
```

try every point:

```text
next = 0 ... N-1
```

If point `next` is already used:

```text
skip
```

Otherwise choose it.

---

## 25.1 New mask

```text
newMask
=
mask | (1 << next)
```

---

## 25.2 Added cost

If:

```text
last == N
```

this is the first point:

```text
addedCost = 0
```

Otherwise:

```text
addedCost
=
distance(last,next)
```

---

## 25.3 Recurrence

```text
dp[mask][last]
=
min over every unused `next`:

addedCost(last,next)
+
dp[
    mask | (1<<next)
][next]
```

---

## 25.4 Base case

When all points are used:

```text
mask == (1<<N)-1
```

there is nothing left to place.

Therefore:

```text
return 0
```

---

## 25.5 Final subproblem

Initially:

```text
mask = 0
```

and:

```text
last = sentinel
```

Therefore:

```text
answer
=
solve(0, N)
```

---

## 25.6 Complexity

Number of masks:

```text
2^N
```

Possible `last` states:

```text
O(N)
```

For every state, try:

```text
O(N)
```

next points.

Therefore:

```text
Time
=
O(N² × 2^N)
```

Space:

```text
O(N × 2^N)
```

This matches the optimized complexity highlighted in the lecture.

---

# 26. Point Ordering — Dry Run

Points:

```text
A = (0,0)
B = (5,6)
C = (1,2)
```

---

## Start

```text
mask = 000
last = none
```

We may choose:

```text
A
B
or
C
```

as the first point.

First choice has:

```text
cost = 0
```

---

## Choose A first

```text
mask = 001
last = A
```

Remaining:

```text
B
C
```

Distances:

```text
dist(A,B)
=
|0-5| + |0-6|
=
11
```

```text
dist(A,C)
=
|0-1| + |0-2|
=
3
```

Choosing `C` next is cheaper locally.

State:

```text
mask = 101
last = C
```

Then only `B` remains.

```text
dist(C,B)
=
|1-5| + |2-6|
=
8
```

Total:

```text
3 + 8
=
11
```

This gives:

```text
A → C → B
```

which is the lecture example.

The DP still checks all valid states and guarantees the minimum.

---

# 27. Point Ordering — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

const ll INF = (1LL << 62);

int n;
vector<pair<ll,ll>> points;

vector<vector<ll>> memo;

ll manhattan(int a, int b) {

    return
        llabs(points[a].first - points[b].first)
        +
        llabs(points[a].second - points[b].second);
}

ll solve(int mask, int last) {

    int fullMask
        =
        (1 << n) - 1;

    if (mask == fullMask) {
        return 0;
    }

    ll& answer = memo[mask][last];

    if (answer != -1) {
        return answer;
    }

    answer = INF;

    for (int next = 0;
         next < n;
         next++) {

        if (mask & (1 << next)) {
            continue;
        }

        ll addedCost = 0;

        // last == n means
        // no point has been chosen yet.
        if (last != n) {
            addedCost
                =
                manhattan(last, next);
        }

        int nextMask
            =
            mask | (1 << next);

        answer
            =
            min(
                answer,
                addedCost
                +
                solve(
                    nextMask,
                    next
                )
            );
    }

    return answer;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    cin >> n;

    points.resize(n);

    for (auto& [x,y] : points) {
        cin >> x >> y;
    }

    int totalMasks
        =
        1 << n;

    // last = 0 ... n-1
    // last = n means no previous point yet.
    memo.assign(
        totalMasks,
        vector<ll>(n + 1, -1)
    );

    cout << solve(0, n) << '\n';

    return 0;
}
```

---

# 28. Pattern Comparison

| Pattern | State | Why mask? | Complexity |
|---|---|---|---:|
| Permutation warm-up | `dp[index][mask]` | remember used elements | about `O(N·2^N)` useful subset states/transitions |
| Matching | `dp[mask]` | remember used women | `O(N·2^N)` |
| Point ordering | `dp[mask][last]` | used points + last point | `O(N²·2^N)` |

---

# 29. Contest Recognition Checklist

## 29.1 First question

Ask:

```text
Does the future depend on
exactly WHICH elements were selected,
not just HOW MANY?
```

If yes, a mask may be useful.

---

## 29.2 Typical bitmask-DP signals

```text
N around 15–22
```

plus words like:

```text
subset
assignment
matching
permutation
visit every item
used / unused
choose each item once
```

Strongly suggest:

```text
bitmask DP
```

---

## 29.3 Basic mask template

```cpp
for (int next = 0; next < n; next++) {

    if (mask & (1 << next)) {
        continue;
    }

    int nextMask
        =
        mask | (1 << next);

    // transition
}
```

Memorize the idea, not only the syntax:

```text
check unused
→ choose
→ set bit
→ recurse / transition
```

---

## 29.4 Ask whether index is redundant

If:

```text
each transition sets exactly one new bit
```

then:

```text
index
=
popcount(mask)
```

So try removing:

```text
index
```

from the DP state.

This is one of the strongest recurring patterns in subset DP.

---

## 29.5 When is `last` needed?

If the next transition cost depends on:

```text
which element was chosen most recently
```

then `mask` alone is insufficient.

Use:

```text
dp[mask][last]
```

Examples:

```text
path cost
distance between consecutive points
traveling-salesman-style transitions
```

---

## 29.6 When mask alone is enough

If future choices depend only on:

```text
which elements are used
```

and the current stage is derivable from:

```text
popcount(mask)
```

then:

```text
dp[mask]
```

may be enough.

Matching is exactly this pattern.

---

## 29.7 Complexity decoding

If state is:

```text
dp[mask]
```

and each mask tries every item:

```text
O(N × 2^N)
```

If state is:

```text
dp[mask][last]
```

and every state tries every next item:

```text
O(N² × 2^N)
```

These are classic subset-DP complexity shapes.

---

## 29.8 Bit operation recognition table

| Need | Operation |
|---|---|
| Check element `i` | `mask & (1<<i)` |
| Add element `i` | `mask \| (1<<i)` |
| Remove element `i` | `mask & ~(1<<i)` |
| Toggle element `i` | `mask ^ (1<<i)` |
| Count selected | `__builtin_popcount(mask)` |
| Empty subset | `0` |
| Full subset | `(1<<n)-1` |
| Union | `A \| B` |
| Intersection | `A & B` |
| Symmetric difference | `A ^ B` |

---

# 30. Final Mental Model

## Bitmask = subset compressed into one integer

```text
Array indices:
0 1 2 3

Subset:
{0,2}

Bits:
0 1 0 1

Mask:
0101
```

---

## Core transition

```text
CURRENT MASK
     |
     v
try each unused element
     |
     v
set its bit
     |
     v
NEXT MASK
```

---

## Matching

```text
mask
=
women already used

man
=
popcount(mask)

for each free compatible woman:
    set her bit
```

State:

```text
dp[mask]
```

---

## Point-ordering DP

```text
mask
=
points already used

last
=
last chosen point
```

State:

```text
dp[mask][last]
```

Transition cost:

```text
distance(last,next)
```

---

# Final Revision Box

```text
BITMASK BASICS
────────────────────────────────────

bit i = 1
→ element i is selected

bit i = 0
→ element i is not selected


CHECK
mask & (1 << i)


ADD
mask | (1 << i)


REMOVE
mask & ~(1 << i)


FULL MASK
(1 << n) - 1


COUNT SELECTED
__builtin_popcount(mask)


WHY DP WITH BITMASK?
────────────────────────────────────

Future depends on:

WHICH elements were used

not only:

HOW MANY elements were used


STATE ELIMINATION
────────────────────────────────────

If exactly one bit is added per stage:

index = popcount(mask)

Therefore:

dp[index][mask]
        ↓
dp[mask]


MATCHING
────────────────────────────────────

mask = used women

man = popcount(mask)

Try every woman j:

unused?
compatible?

YES
→ nextMask = mask | (1<<j)


POINT PERMUTATION
────────────────────────────────────

mask = used points
last = previous point

dp[mask][last]

try unused next:

distance(last,next)
+
dp[nextMask][next]


COMPLEXITIES
────────────────────────────────────

dp[mask]:
O(N × 2^N)

dp[mask][last]:
O(N² × 2^N)


CORE CONTEST QUESTION
────────────────────────────────────

N is small (~15–21)

AND

I must remember exactly which
items are already used.

→ THINK BITMASK DP
```
