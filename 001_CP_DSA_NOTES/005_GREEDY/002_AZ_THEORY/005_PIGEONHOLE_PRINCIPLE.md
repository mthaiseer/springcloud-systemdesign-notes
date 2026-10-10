# AlgoZenith Pigeonhole Principle
## Optimized TLE-Style Notes — Prefix Remainders + Guaranteed Collision

> **Source:** supplied AlgoZenith Pigeonhole Principle lecture screenshots.
>
> **Goal:** understand why `N+1` prefix states and only `N` modulo states force a collision, and how that collision gives a divisible-sum subarray.
>
> **Problem flow:**
>
> ```text
> What it asks
> → small dry run
> → core observation
> → proof / derivation
> → one detailed dry run
> → algorithm
> → C++17
> → complexity
> → recognition
> ```
>
> **Variable rule**
>
> - Explanations/code: descriptive names such as `prefixRemainder`, `firstIndexOfRemainder`, `numberOfValidSubarrays`.
> - Proofs: shorter readable names such as `prefixI`, `prefixJ`, `rem`.

---

# Clickable Table of Contents

- [0. Class Map](#0-class-map)
- [1. Prerequisites](#1-prerequisites)
  - [1.1 Pigeonhole Principle](#11-pigeonhole-principle)
  - [1.2 Modulo States](#12-modulo-states)
  - [1.3 Prefix Sum](#13-prefix-sum)
  - [1.4 Subarray From Two Prefix Sums](#14-subarray-from-two-prefix-sums)
  - [1.5 Equal-Remainder Theorem](#15-equal-remainder-theorem)
  - [1.6 Empty Prefix](#16-empty-prefix)
  - [1.7 What to Store](#17-what-to-store)
  - [1.8 Overflow and `long long`](#18-overflow-and-long-long)
  - [1.9 Recognition Checklist](#19-recognition-checklist)
- [2. Problem 1 — Find One Non-Empty Subarray With Sum Divisible by N](#2-problem-1--find-one-non-empty-subarray-with-sum-divisible-by-n)
  - [2.1 What It Asks](#21-what-it-asks)
  - [2.2 Why Brute Force Stops Working](#22-why-brute-force-stops-working)
  - [2.3 Small Dry Run First](#23-small-dry-run-first)
  - [2.4 Core Observation](#24-core-observation)
  - [2.5 Pigeonhole Proof](#25-pigeonhole-proof)
  - [2.6 Detailed Dry Run](#26-detailed-dry-run)
  - [2.7 Algorithm](#27-algorithm)
  - [2.8 C++17](#28-c17)
  - [2.9 Complexity](#29-complexity)
  - [2.10 Recognition Model](#210-recognition-model)
- [3. Problem 2 — Count All Divisible-Sum Subarrays](#3-problem-2--count-all-divisible-sum-subarrays)
  - [3.1 What It Asks](#31-what-it-asks)
  - [3.2 Small Dry Run First](#32-small-dry-run-first)
  - [3.3 Core Observation](#33-core-observation)
  - [3.4 Algorithm](#34-algorithm)
  - [3.5 C++17](#35-c17)
  - [3.6 Complexity](#36-complexity)
- [4. Problem 3 — Enumerate All Valid Subarrays](#4-problem-3--enumerate-all-valid-subarrays)
  - [4.1 What It Asks](#41-what-it-asks)
  - [4.2 Core Idea](#42-core-idea)
  - [4.3 C++17 Skeleton](#43-c17-skeleton)
  - [4.4 Complexity](#44-complexity)
- [Final Mental Model](#final-mental-model)

---

# 0. Class Map

```text
PIGEONHOLE PRINCIPLE
│
├── Small N
│   └── N <= 20
│       → subset brute force is possible
│
└── Large N
    └── N up to about 1e5
        → arbitrary-subset brute force is impossible
        → use contiguous subarray
        → prefix sum modulo N
        → repeated remainder
        → divisible difference
```

Once prefix remainders are available:

```text
same remainder seen before
        |
        +---------------------------+
        |             |             |
        v             v             v
   find one       count all     enumerate all
 first index      frequency      all indices
```

---

# 1. Prerequisites

## 1.1 Pigeonhole Principle

If:

```text
number of objects
>
number of boxes
```

then at least one box contains at least two objects.

Example:

```text
4 balls
3 boxes
```

```text
● ● ● ●

[ ] [ ] [ ]
```

At least one box must contain 2 or more balls.

### In this lecture

Objects:

```text
N+1 prefix states
```

Boxes:

```text
remainders:
0, 1, 2, ..., N-1
```

So:

```text
N+1 objects
>
N boxes
```

Therefore two prefix states must share the same remainder.

---

## 1.2 Modulo States

For any integer:

```text
value % N
```

the remainder is one of:

```text
0, 1, 2, ..., N-1
```

Example:

```text
N = 5
```

Possible states:

```text
0, 1, 2, 3, 4
```

Modulo compresses a huge value range into only `N` states.

---

## 1.3 Prefix Sum

For:

```text
values =
[a0, a1, a2, ..., aN-1]
```

define:

```text
prefix[i]
=
a0 + a1 + ... + ai
```

Example:

```text
values:
[2, 3, 1, 2]

prefix:
[2, 5, 6, 8]
```

For divisibility, we often only need:

```text
prefixRemainder
=
prefixSum % N
```

---

## 1.4 Subarray From Two Prefix Sums

If:

```text
j < i
```

then:

```text
sum(j+1 ... i)
=
prefix[i] - prefix[j]
```

Example:

```text
values:
[2, 3, 1, 2]

prefix:
[2, 5, 6, 8]
```

Subarray:

```text
[1,2]
```

at indices:

```text
2 ... 3
```

has sum:

```text
prefix[3] - prefix[1]

=
8 - 5

=
3
```

---

## 1.5 Equal-Remainder Theorem

This is the one theorem used everywhere later.

Suppose:

```text
prefixI % N
=
prefixJ % N
=
rem
```

Write:

```text
prefixI
=
multipleI*N + rem

prefixJ
=
multipleJ*N + rem
```

Subtract:

```text
prefixI - prefixJ

=
(multipleI*N + rem)
-
(multipleJ*N + rem)
```

Cancel `rem`:

```text
=
multipleI*N
-
multipleJ*N
```

Factor `N`:

```text
=
(multipleI - multipleJ) * N
```

Therefore:

```text
(prefixI - prefixJ) % N
=
0
```

Since:

```text
prefixI - prefixJ
=
sum(j+1 ... i)
```

we get:

```text
sum(j+1 ... i) % N
=
0
```

### Inline Example

```text
N = 6

prefixJ = 5
prefixI = 17
```

Remainders:

```text
5 % 6  = 5
17 % 6 = 5
```

Difference:

```text
17 - 5
=
12
```

and:

```text
12 % 6
=
0
```

### Recognition Formula

```text
equal prefix remainder
→ divisible prefix difference
→ divisible subarray
```

---

## 1.6 Empty Prefix

Before reading any element:

```text
prefix sum = 0
```

Treat this as:

```text
index = -1
remainder = 0
```

Why?

If:

```text
prefix[i] % N
=
0
```

then the valid subarray should be:

```text
[0 ... i]
```

Using:

```text
previousIndex = -1
```

the normal formula gives:

```text
previousIndex + 1
=
0
```

So no special case is needed.

For finding one:

```text
firstIndexOfRemainder[0] = -1
```

For counting:

```text
remainderFrequency[0] = 1
```

---

## 1.7 What to Store

The mathematics is the same in all three versions.

Only the stored information changes:

```text
FIND ONE
remainder → first previous index

COUNT ALL
remainder → frequency

ENUMERATE ALL
remainder → vector of previous indices
```

This is enough as prerequisite; each problem applies the correct storage form later.

---

## 1.8 Overflow and `long long`

Use:

```cpp
long long
```

for:

```text
array values
answer count
large accumulated quantities
```

When only divisibility matters, keep:

```text
prefixRemainder
=
(prefixRemainder + value % N) % N
```

instead of storing a large complete prefix sum.

---

## 1.9 Recognition Checklist

When you see:

```text
N values
+
divisibility by N
+
prefix/subarray sums
+
need to prove something must exist
```

think:

```text
N elements
→ N+1 prefix states

modulo N
→ only N remainder states

N+1 > N
→ collision guaranteed
```

Then apply the equal-remainder theorem.

---

# 2. Problem 1 — Find One Non-Empty Subarray With Sum Divisible by N

## 2.1 What It Asks

Given:

```text
N numbers:
a1, a2, ..., aN
```

find a non-empty selection whose sum satisfies:

```text
sum % N
=
0
```

The lecture shows:

```text
P1:
N <= 20
```

and a larger version:

```text
P2:
N up to about 1e5
```

For the large version, the key stronger fact is:

```text
a non-empty contiguous subarray
with sum divisible by N
is guaranteed to exist
```

Since a contiguous subarray is also a valid selection, this solves the existence problem.

---

## 2.2 Why Brute Force Stops Working

For:

```text
N <= 20
```

there are:

```text
2^N
```

subsets.

Example:

```text
2^20
≈
1,048,576
```

manageable.

But for:

```text
N = 100000
```

subset enumeration is impossible.

So the large constraints force a structural approach:

```text
contiguous subarray
+
prefix modulo
+
Pigeonhole Principle
```

---

## 2.3 Small Dry Run First

Example:

```text
N = 5

values:
[2, 3, 1, 4, 2]
```

Immediately:

```text
2 + 3
=
5
```

and:

```text
5 % 5
=
0
```

So:

```text
subarray [0 ... 1]
```

works.

The real task is to prove:

```text
some valid subarray
must always exist
```

---

## 2.4 Core Observation

For `N` values, consider:

```text
empty prefix
+
prefix after 1 value
+
...
+
prefix after N values
```

Total:

```text
N+1 prefix states
```

Take each prefix modulo `N`.

Only these remainders exist:

```text
0 ... N-1
```

Total:

```text
N remainder states
```

So a duplicate remainder must occur.

Once it does, the equal-remainder theorem immediately gives a divisible subarray.

---

## 2.5 Pigeonhole Proof

```text
N+1 prefix states
placed into
N remainder buckets
```

Therefore:

```text
two prefix states
share one remainder
```

Suppose:

```text
prefixI % N
=
prefixJ % N
```

with:

```text
j < i
```

By Section 1.5:

```text
(prefixI - prefixJ) % N
=
0
```

and:

```text
prefixI - prefixJ
=
sum(j+1 ... i)
```

Therefore:

```text
sum(j+1 ... i) % N
=
0
```

So a valid non-empty subarray is guaranteed.

ASCII:

```text
PREFIX STATES

P0  P1  P2  ...  PN
 |   |   |         |
 v   v   v         v

REMAINDER BUCKETS

[0] [1] [2] ... [N-1]

N+1 prefixes
N buckets

      ↓

collision

      ↓

equal remainder

      ↓

divisible subarray
```

---

## 2.6 Detailed Dry Run

Use:

```text
N = 6

values:
[2, 3, 1, 2, 1, 3]
```

Start with empty prefix:

```text
index = -1
remainder = 0
```

| Index | Value | Prefix sum | Prefix `% 6` | Seen before? |
|---:|---:|---:|---:|---|
| `-1` | — | `0` | `0` | initialize |
| `0` | `2` | `2` | `2` | no |
| `1` | `3` | `5` | `5` | no |
| `2` | `1` | `6` | `0` | **yes: at -1** |

At index `2`:

```text
remainder 0
```

was already seen at:

```text
-1
```

So:

```text
left  = -1 + 1 = 0
right = 2
```

Answer:

```text
[0 ... 2]
```

Subarray:

```text
[2,3,1]
```

Sum:

```text
6
```

and:

```text
6 % 6
=
0
```

Visual:

```text
index:      -1      0      1      2
value:               2      3      1
prefix:      0       2      5      6
mod 6:       0       2      5      0
             ^                     ^
             |_____________________|
                same remainder
```

---

## 2.7 Algorithm

```text
firstIndexOfRemainder[0] = -1
prefixRemainder = 0

for each index:

    prefixRemainder =
        (prefixRemainder + value[index]) % N

    if this remainder was seen before:

        left =
            firstIndexOfRemainder[prefixRemainder] + 1

        right =
            index

        return [left, right]

    firstIndexOfRemainder[prefixRemainder] = index
```

Under the exact lecture model:

```text
N values
modulo N
```

a collision is guaranteed.

---

## 2.8 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    vector<long long> values(numberOfValues);
    for (long long& value : values) cin >> value;

    map<int, int> firstIndexOfRemainder;
    firstIndexOfRemainder[0] = -1;

    int prefixRemainder = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        prefixRemainder =
            (prefixRemainder + values[index] % numberOfValues)
            % numberOfValues;

        if (firstIndexOfRemainder.count(prefixRemainder)) {
            int left =
                firstIndexOfRemainder[prefixRemainder] + 1;

            cout << left << ' ' << index << '\n';
            return 0;
        }

        firstIndexOfRemainder[prefixRemainder] = index;
    }

    cout << -1 << '\n';
    return 0;
}
```

---

## 2.9 Complexity

Using ordered `map`:

```text
Time:
O(N log N)

Space:
O(N)
```

The modulo state itself has only:

```text
N possible remainders
```

---

## 2.10 Recognition Model

Signal:

```text
N elements
+
modulo N
+
need a divisible-sum subarray
or proof of existence
```

Think:

```text
N+1 prefixes
vs
N remainders
→ Pigeonhole
→ equal remainder
→ subtract prefixes
```

---

# 3. Problem 2 — Count All Divisible-Sum Subarrays

## 3.1 What It Asks

Count all subarrays satisfying:

```text
subarraySum % N
=
0
```

Same theorem as before.

Only the stored information changes:

```text
remainder
→
frequency
```

---

## 3.2 Small Dry Run First

Use:

```text
N = 3

values:
[1, 2, 3]
```

Prefix states including empty prefix:

```text
prefix sums:
0, 1, 3, 6

mod 3:
0, 1, 0, 0
```

Remainder `0` appears three times.

Pairs of equal remainder states:

```text
empty ↔ prefix 2
empty ↔ prefix 3
prefix 2 ↔ prefix 3
```

So there are three valid subarrays:

```text
[1,2]
[1,2,3]
[3]
```

---

## 3.3 Core Observation

Suppose current prefix remainder is:

```text
rem
```

and it has already appeared:

```text
frequency[rem]
```

times.

Every previous equal remainder creates one valid starting point.

Therefore:

```text
new subarrays ending here
=
frequency[rem]
```

So:

```text
answer += frequency[rem]
frequency[rem]++
```

Initialize:

```text
frequency[0] = 1
```

for the empty prefix.

---

## 3.4 Algorithm

```text
remainderFrequency[0] = 1
prefixRemainder = 0
answer = 0

for each value:

    prefixRemainder =
        (prefixRemainder + value) % N

    answer +=
        remainderFrequency[prefixRemainder]

    remainderFrequency[prefixRemainder]++
```

---

## 3.5 C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    map<int, long long> remainderFrequency;
    remainderFrequency[0] = 1;

    int prefixRemainder = 0;
    long long numberOfValidSubarrays = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        long long value;
        cin >> value;

        prefixRemainder =
            (prefixRemainder + value % numberOfValues)
            % numberOfValues;

        numberOfValidSubarrays +=
            remainderFrequency[prefixRemainder];

        remainderFrequency[prefixRemainder]++;
    }

    cout << numberOfValidSubarrays << '\n';
    return 0;
}
```

---

## 3.6 Complexity

With ordered `map`:

```text
Time:
O(N log N)

Space:
O(N)
```

Use:

```cpp
long long
```

for the answer because the count can be quadratic in `N`.

---

# 4. Problem 3 — Enumerate All Valid Subarrays

## 4.1 What It Asks

Output every subarray satisfying:

```text
subarraySum % N
=
0
```

Again, same theorem.

Now store:

```text
remainder
→
all previous indices
```

---

## 4.2 Core Idea

Initialize:

```text
positions[0]
=
[-1]
```

At current index `i` with remainder `rem`:

```text
for every previousIndex in positions[rem]:

    valid interval
    =
    [previousIndex + 1 ... i]
```

Example:

```text
positions[2]
=
[1,4]
```

Current:

```text
index = 7
remainder = 2
```

Valid intervals:

```text
[2 ... 7]
[5 ... 7]
```

Then append:

```text
7
```

to:

```text
positions[2]
```

---

## 4.3 C++17 Skeleton

```cpp
#include <bits/stdc++.h>
using namespace std;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    int numberOfValues;
    cin >> numberOfValues;

    map<int, vector<int>> positionsOfRemainder;
    positionsOfRemainder[0].push_back(-1);

    int prefixRemainder = 0;

    for (int index = 0; index < numberOfValues; ++index) {
        long long value;
        cin >> value;

        prefixRemainder =
            (prefixRemainder + value % numberOfValues)
            % numberOfValues;

        for (int previousIndex :
             positionsOfRemainder[prefixRemainder]) {

            cout << previousIndex + 1
                 << ' '
                 << index
                 << '\n';
        }

        positionsOfRemainder[prefixRemainder].push_back(index);
    }

    return 0;
}
```

---

## 4.4 Complexity

Map maintenance:

```text
O(N log N)
```

plus output cost.

If the number of valid intervals is:

```text
A
```

then:

```text
Time:
O(N log N + A)

Space:
O(N)
```

The output itself may be as large as:

```text
O(N^2)
```

---

# Final Mental Model

```text
                  PIGEONHOLE + PREFIX MOD
                           |
                           v
                   N array elements
                           |
                           v
                N+1 prefix states
                including empty prefix
                           |
                           v
                 take every prefix % N
                           |
                           v
               only N remainder states
                           |
                           v
                     PIGEONHOLE
                           |
                           v
              two equal prefix remainders
                           |
                           v
              subtract the two prefix sums
                           |
                           v
             subarray sum is divisible by N
```

Core theorem:

```text
prefixI % N
=
prefixJ % N

        ↓

(prefixI - prefixJ) % N
=
0

        ↓

sum(j+1 ... i) % N
=
0
```

Then choose storage based on the problem:

```text
FIND ONE
→ first index

COUNT ALL
→ frequency

ENUMERATE ALL
→ all indices
```

> **Core lesson:** the proof is only written once. The three problem versions differ only in what information we store for each remainder.
