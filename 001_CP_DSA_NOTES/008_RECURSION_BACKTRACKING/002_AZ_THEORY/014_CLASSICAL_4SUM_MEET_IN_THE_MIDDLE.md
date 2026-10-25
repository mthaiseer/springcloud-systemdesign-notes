# Classical 4-Sum — Meet-in-the-Middle
## Three Variants: Reusable Indices, Distinct Ordered Indices, and Recovering the Quadruple
### TLE-Style Self-Study Note — Prerequisites, ASCII Diagrams, Step-by-Step Dry Runs, Invariants, Proof, and C++17

> **Main transformation**
>
> Instead of thinking about four numbers at once,
>
> ```text
> A + B + C + D = TARGET
> ```
>
> group them into two pairs:
>
> ```text
> (A + B) + (C + D) = TARGET
> ```
>
> If:
>
> ```text
> X = A + B
> Y = C + D
> ```
>
> then:
>
> ```text
> X + Y = TARGET
> Y = TARGET - X
> ```
>
> This turns a four-choice search into a **pair-sum + complement-search** problem.

---

# Table of Contents

1. [Problem Family](#1-problem-family)
2. [Prerequisite 1 — Why Brute Force Is Too Slow](#2-prerequisite-1--why-brute-force-is-too-slow)
3. [Prerequisite 2 — Two-Sum Complement Thinking](#3-prerequisite-2--two-sum-complement-thinking)
4. [Prerequisite 3 — Compress Two Decisions into One Pair Sum](#4-prerequisite-3--compress-two-decisions-into-one-pair-sum)
5. [Prerequisite 4 — Map / Hash Map as a Lookup Structure](#5-prerequisite-4--map--hash-map-as-a-lookup-structure)
6. [The Core 4-Sum Transformation](#6-the-core-4-sum-transformation)
7. [Variant 1 — Indices May Repeat](#7-variant-1--indices-may-repeat)
8. [Variant 1 — Step-by-Step Algorithm](#8-variant-1--step-by-step-algorithm)
9. [Variant 1 — Dry Run](#9-variant-1--dry-run)
10. [Variant 1 — C++17](#10-variant-1--c17)
11. [Why Variant 1 Logic Fails for Distinct Ordered Indices](#11-why-variant-1-logic-fails-for-distinct-ordered-indices)
12. [Variant 2 — Require `a < b < c < d`](#12-variant-2--require-a--b--c--d)
13. [The Key Sweep Invariant](#13-the-key-sweep-invariant)
14. [Why We Sweep `b` from Right to Left](#14-why-we-sweep-b-from-right-to-left)
15. [Query First, Insert Later](#15-query-first-insert-later)
16. [Variant 2 — Full Dry Run](#16-variant-2--full-dry-run)
17. [Variant 2 — C++17](#17-variant-2--c17)
18. [Variant 3 — Return the Actual Four Indices](#18-variant-3--return-the-actual-four-indices)
19. [Variant 3 — C++17](#19-variant-3--c17)
20. [Extension — Count All Valid Quadruples](#20-extension--count-all-valid-quadruples)
21. [Correctness Proof](#21-correctness-proof)
22. [Complexity](#22-complexity)
23. [Map vs `unordered_map`](#23-map-vs-unordered_map)
24. [Overflow and Data Types](#24-overflow-and-data-types)
25. [Common Mistakes](#25-common-mistakes)
26. [Recognition Checklist](#26-recognition-checklist)
27. [General Meet-in-the-Middle Pattern](#27-general-meet-in-the-middle-pattern)
28. [Final Mental Model](#28-final-mental-model)

---

# 1. Problem Family

We study the classical equation:

```text
arr[a] + arr[b] + arr[c] + arr[d] = TARGET
```

with:

```text
N <= 1000
```

The important point is that there are **different variants** of the problem.

---

## Variant 1

Indices are allowed to repeat:

```text
0 <= a,b,c,d <= N-1
```

For example:

```text
a = b
```

is allowed.

We only need to answer:

```text
Does some quadruple exist?
```

---

## Variant 2

All indices must be distinct and ordered:

```text
0 <= a < b < c < d <= N-1
```

We need:

```text
true / false
```

---

## Variant 3

The same ordered-index condition:

```text
a < b < c < d
```

but now we must actually return:

```text
a, b, c, d
```

not merely say whether they exist.

---

# 2. Prerequisite 1 — Why Brute Force Is Too Slow

The most direct solution is:

```cpp
for every a
    for every b
        for every c
            for every d
                check the sum
```

Number of combinations is roughly:

```text
N^4
```

For:

```text
N = 1000
```

that becomes:

```text
1000^4 = 10^12
```

Far too large.

---

## Brute-force picture

```text
choose a
   |
   +-- choose b
          |
          +-- choose c
                 |
                 +-- choose d
                        |
                        check sum
```

Four independent loops create the `N^4` explosion.

We need to **compress two choices together**.

---

# 3. Prerequisite 2 — Two-Sum Complement Thinking

Before 4-Sum, remember 2-Sum.

Suppose:

```text
A + B = TARGET
```

If we fix `A`, then:

```text
B = TARGET - A
```

So instead of trying every `(A,B)` blindly, we can ask:

```text
Have I seen TARGET - A?
```

This is the **complement idea**.

Example:

```text
TARGET = 10
A = 4
```

Then:

```text
B = 10 - 4 = 6
```

We only need to search for `6`.

---

# 4. Prerequisite 3 — Compress Two Decisions into One Pair Sum

For 4-Sum:

```text
A + B + C + D = TARGET
```

group:

```text
(A + B) + (C + D) = TARGET
```

Define:

```text
X = A + B
Y = C + D
```

Now:

```text
X + Y = TARGET
```

which gives:

```text
Y = TARGET - X
```

So the problem becomes:

```text
Generate / store pair sums.
For another pair sum X,
search for TARGET - X.
```

---

## Visual transformation

```text
A + B + C + D = TARGET

|---X---| |---Y---|

       X + Y = TARGET

             ↓

       Y = TARGET - X
```

That is the entire Meet-in-the-Middle idea for this problem.

---

# 5. Prerequisite 4 — Map / Hash Map as a Lookup Structure

Suppose we have generated pair sums:

```text
5, 8, 12, 5
```

For an existence problem, we only care whether a sum exists.

We can store:

```text
5  -> exists
8  -> exists
12 -> exists
```

Then a complement query is fast.

With:

```cpp
map<long long, bool>
```

lookup is:

```text
O(log M)
```

where `M` is the number of stored sums.

With:

```cpp
unordered_set<long long>
```

average lookup is approximately:

```text
O(1)
```

---

## What if we need the indices?

Then storing only:

```text
sum -> true
```

is not enough.

We instead store:

```text
sum -> (c,d)
```

Example:

```cpp
map<long long, pair<int,int>> pos;
```

This lets us reconstruct the quadruple later.

---

# 6. The Core 4-Sum Transformation

Suppose:

```text
arr[a] = A
arr[b] = B
arr[c] = C
arr[d] = D
```

We need:

```text
A + B + C + D = TARGET
```

Group:

```text
(A + B) + (C + D) = TARGET
```

Let:

```text
X = A + B
Y = C + D
```

Then:

```text
X + Y = TARGET
```

Therefore:

```text
Y = TARGET - X
```

---

## Algorithmic translation

```text
1. Build possible pair sums Y.
2. Enumerate another pair producing X.
3. Search for TARGET - X.
```

The only complication is:

```text
What index relationships are allowed?
```

That determines how pair sums may be stored.

---

# 7. Variant 1 — Indices May Repeat

Condition:

```text
0 <= a,b,c,d <= N-1
```

There is no requirement that the indices are distinct.

Therefore we can generate **every possible pair sum independently**.

---

## Pair-sum table

For every:

```text
i = 0 ... N-1
j = 0 ... N-1
```

store:

```text
arr[i] + arr[j]
```

Then again enumerate every pair:

```text
arr[i] + arr[j]
```

and search:

```text
TARGET - arr[i] - arr[j]
```

---

## Diagram

```text
All possible first pairs        All possible second pairs

(i,j)                           (c,d)
  |                               |
  v                               v
arr[i] + arr[j]              arr[c] + arr[d]
      X                            Y

             X + Y = TARGET
```

Because repeated indices are allowed, there is no index-overlap problem.

---

# 8. Variant 1 — Step-by-Step Algorithm

### Step 1

Create a map/set of all pair sums.

```text
for i:
    for j:
        store arr[i] + arr[j]
```

There are:

```text
N^2
```

pairs.

---

### Step 2

Enumerate every possible `(i,j)` again.

Compute:

```text
current = arr[i] + arr[j]
```

Required complementary sum:

```text
need = TARGET - current
```

---

### Step 3

If:

```text
need
```

exists among the stored pair sums, the answer is `true`.

---

# 9. Variant 1 — Dry Run

Suppose:

```text
arr = [1, 3, 5]
TARGET = 12
```

Generate some pair sums:

```text
1+1 = 2
1+3 = 4
1+5 = 6

3+1 = 4
3+3 = 6
3+5 = 8

5+1 = 6
5+3 = 8
5+5 = 10
```

Stored sums:

```text
{2,4,6,8,10}
```

Now take pair:

```text
3 + 5 = 8
```

Need:

```text
12 - 8 = 4
```

`4` exists:

```text
1 + 3 = 4
```

Therefore:

```text
3 + 5 + 1 + 3 = 12
```

Valid.

Notice that the same array index could theoretically participate more than once in this variant.

That is permitted.

---

# 10. Variant 1 — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool isFourSumPossible(const vector<ll>& arr, ll target) {
    int n = arr.size();
    map<ll, bool> pos;

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            pos[arr[i] + arr[j]] = true;
        }
    }

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            ll need = target - arr[i] - arr[j];

            if (pos[need]) {
                return true;
            }
        }
    }

    return false;
}
```

---

## Complexity with `map`

Generate:

```text
N^2
```

pair sums.

Each insertion:

```text
O(log N^2)
```

and:

```text
log(N^2) = 2 log N = O(log N)
```

So:

```text
O(N^2 log N)
```

Searching another `N^2` pairs also costs:

```text
O(N^2 log N)
```

Overall:

```text
Time = O(N^2 log N)
Space = O(N^2)
```

---

# 11. Why Variant 1 Logic Fails for Distinct Ordered Indices

Now suppose the condition becomes:

```text
a < b < c < d
```

If we simply store every pair sum globally, a matching pair may overlap with `(a,b)`.

Example:

```text
left pair  = (1,3)
stored pair = (3,5)
```

Index `3` is reused.

Or worse:

```text
stored pair = (0,2)
```

which does not satisfy:

```text
b < c
```

So for ordered distinct indices we need more than:

```text
pair sum exists
```

We need:

```text
pair sum exists on the RIGHT side of b
```

---

# 12. Variant 2 — Require `a < b < c < d`

Condition:

```text
0 <= a < b < c < d <= N-1
```

We want:

```text
arr[a] + arr[b] + arr[c] + arr[d] = TARGET
```

Again:

```text
(arr[a] + arr[b]) + (arr[c] + arr[d]) = TARGET
```

But now the two pairs must satisfy:

```text
a < b < c < d
```

---

## Geometric view of the array

```text
0 1 2 3 4 5 6 7 ... N-1
      a b | c     d
          ^
        boundary
```

For a chosen `b`:

```text
a must lie left of b
c,d must lie right of b
```

More precisely:

```text
a < b < c < d
```

This suggests a sweep.

---

# 13. The Key Sweep Invariant

We process:

```text
b
```

from **right to left**.

At the moment we query pairs `(a,b)`, the map must contain exactly the sums of pairs:

```text
(c,d)
```

that satisfy:

```text
b < c < d
```

This is the key invariant.

---

## Invariant picture

At current `b`:

```text
[ left side ] b [ right side                    ]
     a          c               d

a < b          b < c < d
```

Map contains:

```text
arr[c] + arr[d]
```

for valid right-side pairs only.

Therefore any complement found automatically satisfies:

```text
a < b < c < d
```

---

# 14. Why We Sweep `b` from Right to Left

Suppose:

```text
N = 6
indices = 0 1 2 3 4 5
```

Start:

```text
b = N - 2 = 4
```

There cannot yet be a valid `(c,d)` with:

```text
4 < c < d <= 5
```

because only one index remains.

So query map is empty.

After querying, insert pairs beginning at:

```text
c = b = 4
```

giving:

```text
(4,5)
```

---

Next:

```text
b = 3
```

Before querying, map already contains:

```text
(4,5)
```

and thus:

```text
3 < 4 < 5
```

Exactly what we need.

After querying `b=3`, add:

```text
(3,4)
(3,5)
```

---

Next:

```text
b = 2
```

Before querying, map contains pair sums whose first index is:

```text
c >= 3
```

So every stored pair satisfies:

```text
2 < c < d
```

Again correct.

---

## Sweep diagram

```text
b moves ←

0  1  2  3  4  5
         b |---- stored right pairs ---->

After processing b:
            ^
            add pairs starting at c = b
```

This is an elegant **offline sweep + pair-sum lookup**.

---

# 15. Query First, Insert Later

This order is extremely important.

At each `b`:

```text
1. QUERY using pairs (a,b)
2. THEN insert pairs (c=b,d>b)
```

Why not insert first?

Because if we inserted:

```text
(c=b,d)
```

before querying, then the query `(a,b)` could match a stored pair using the same `b`.

That would create:

```text
a < b = c < d
```

instead of:

```text
a < b < c < d
```

---

## Correct order

```text
Current boundary = b

LEFT QUERY:
    a < b

RIGHT MAP BEFORE INSERT:
    c > b

Therefore:
    a < b < c < d
```

Then move the boundary left by adding pairs with:

```text
c = b
```

for use in the **next** iteration.

---

# 16. Variant 2 — Full Dry Run

Use the lecture-style example:

```text
arr = [5,1,2,4,3,6]
index  0 1 2 3 4 5

TARGET = 9
```

We need:

```text
a < b < c < d
```

---

## Start

```text
pos = {}
```

---

## Iteration 1: `b = 4`

Possible `a`:

```text
0,1,2,3
```

But:

```text
pos is empty
```

so no solution can yet be found.

After querying, set:

```text
c = 4
```

Possible `d`:

```text
5
```

Store:

```text
arr[4] + arr[5]
= 3 + 6
= 9
```

Now:

```text
pos = {9}
```

representing pair:

```text
(4,5)
```

---

## Iteration 2: `b = 3`

Current value:

```text
arr[3] = 4
```

Map contains only right pair:

```text
(4,5)
sum = 9
```

Try all `a < 3`.

### `a = 2`

```text
arr[2] = 2
```

Left pair sum:

```text
2 + 4 = 6
```

Need:

```text
9 - 6 = 3
```

`3` is not in `pos`.

---

### `a = 1`

```text
1 + 4 = 5
need = 9 - 5 = 4
```

No.

---

### `a = 0`

```text
5 + 4 = 9
need = 0
```

No.

After queries, insert pairs starting with:

```text
c = 3
```

Pairs:

```text
(3,4): 4+3 = 7
(3,5): 4+6 = 10
```

Now:

```text
pos = {7,9,10}
```

---

## Iteration 3: `b = 2`

```text
arr[2] = 2
```

Stored right-side sums come from:

```text
(3,4) -> 7
(3,5) -> 10
(4,5) -> 9
```

Every stored pair satisfies:

```text
2 < c < d
```

Try `a < 2`.

### `a = 1`

```text
arr[1] + arr[2]
= 1 + 2
= 3
```

Need:

```text
9 - 3 = 6
```

No stored right pair has sum `6`.

### `a = 0`

```text
5 + 2 = 7
need = 2
```

No.

Afterward insert:

```text
(2,3): 2+4 = 6
(2,4): 2+3 = 5
(2,5): 2+6 = 8
```

---

## Iteration 4: `b = 1`

Now:

```text
a = 0
```

Left pair:

```text
arr[0] + arr[1]
= 5 + 1
= 6
```

Need:

```text
TARGET - 6
= 3
```

Depending on the values, if a right pair sum `3` existed it would be found here.

The important part of the dry run is not the particular hit—it is the invariant:

```text
when querying (a,b),
all stored (c,d) satisfy b < c < d
```

Therefore any found match is automatically a valid ordered quadruple.

---

# 17. Variant 2 — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

bool isFourSumPossible(const vector<ll>& arr, ll target) {
    int n = arr.size();
    map<ll, bool> pos;

    for (int b = n - 2; b >= 1; b--) {
        for (int a = b - 1; a >= 0; a--) {
            ll need = target - arr[a] - arr[b];

            if (pos[need]) {
                return true;
            }
        }

        int c = b;

        for (int d = c + 1; d < n; d++) {
            pos[arr[c] + arr[d]] = true;
        }
    }

    return false;
}
```

---

## Read this code as an invariant, not as loops

```text
for each boundary b moving right -> left:

    QUERY:
        try every a < b
        search for a right-pair complement

    INSERT:
        make current b become a future c
        by inserting every pair (b,d), d > b
```

That is much easier to remember than memorizing the exact loops.

---

# 18. Variant 3 — Return the Actual Four Indices

Now suppose the problem asks:

```text
Find a,b,c,d
```

rather than only:

```text
Does a solution exist?
```

Then the map must remember which pair produced each sum.

Instead of:

```cpp
map<ll, bool> pos;
```

use:

```cpp
map<ll, pair<int,int>> pos;
```

Meaning:

```text
pos[sum] = {c,d}
```

When the complement is found, return:

```text
{a,b,c,d}
```

---

## Diagram

```text
sum 7  ─────> (3,4)
sum 9  ─────> (4,5)
sum 10 ─────> (3,5)
```

Query:

```text
need = TARGET - arr[a] - arr[b]
```

If:

```text
pos contains need
```

then:

```text
(c,d) = pos[need]
```

and answer is:

```text
(a,b,c,d)
```

---

# 19. Variant 3 — C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

vector<int> findFourSum(const vector<ll>& arr, ll target) {
    int n = arr.size();
    map<ll, pair<int,int>> pos;

    for (int b = n - 2; b >= 1; b--) {
        for (int a = b - 1; a >= 0; a--) {
            ll need = target - arr[a] - arr[b];

            if (pos.find(need) != pos.end()) {
                auto [c, d] = pos[need];
                return {a, b, c, d};
            }
        }

        int c = b;

        for (int d = c + 1; d < n; d++) {
            pos[arr[c] + arr[d]] = {c, d};
        }
    }

    return {};
}
```

The ordering remains guaranteed by the same invariant:

```text
a < b < c < d
```

---

# 20. Extension — Count All Valid Quadruples

Suppose we need the **number** of ordered quadruples:

```text
a < b < c < d
```

satisfying:

```text
arr[a] + arr[b] + arr[c] + arr[d] = TARGET
```

Now we need multiplicities.

Use:

```cpp
map<ll, long long> count;
```

Meaning:

```text
count[s]
=
number of currently valid right-side pairs (c,d)
whose sum is s
```

For each left pair `(a,b)`:

```text
need = TARGET - arr[a] - arr[b]
```

Add:

```text
count[need]
```

to the answer.

Then insert current right pairs.

---

## C++17 counting version

```cpp
#include <bits/stdc++.h>
using namespace std;

using ll = long long;

ll countFourSum(const vector<ll>& arr, ll target) {
    int n = arr.size();
    map<ll, ll> pos;

    ll answer = 0;

    for (int b = n - 2; b >= 1; b--) {
        for (int a = b - 1; a >= 0; a--) {
            ll need = target - arr[a] - arr[b];
            answer += pos[need];
        }

        int c = b;

        for (int d = c + 1; d < n; d++) {
            pos[arr[c] + arr[d]]++;
        }
    }

    return answer;
}
```

---

## Why multiplicity matters

Suppose two different right pairs both have sum:

```text
10
```

```text
(c1,d1) -> 10
(c2,d2) -> 10
```

For a left pair needing:

```text
10
```

there are:

```text
2
```

valid quadruples, not one.

So existence uses:

```text
bool
```

counting uses:

```text
frequency
```

and reconstruction uses:

```text
pair<int,int>
```

Same Meet-in-the-Middle structure; only the stored payload changes.

---

# 21. Correctness Proof

We prove the ordered distinct-index algorithm.

---

## 21.1 Every reported quadruple has the correct sum

For current pair `(a,b)`, we calculate:

```text
need = TARGET - arr[a] - arr[b]
```

We report success only if a stored pair `(c,d)` satisfies:

```text
arr[c] + arr[d] = need
```

Therefore:

```text
arr[a] + arr[b] + arr[c] + arr[d]
=
arr[a] + arr[b] + need
=
TARGET
```

So the sum is correct.

---

## 21.2 Every reported quadruple has ordered distinct indices

When `(a,b)` is queried:

```text
a < b
```

by construction.

The map invariant says every stored pair satisfies:

```text
b < c < d
```

Therefore:

```text
a < b < c < d
```

All four indices are distinct.

---

## 21.3 Every valid quadruple is eventually considered

Take any valid solution:

```text
a < b < c < d
```

When the sweep reaches this exact `b`:

- the pair `(c,d)` has already been inserted, because `c > b`;
- `(a,b)` is among the queried left pairs because `a < b`;
- their sums are complementary.

Hence the algorithm finds the quadruple.

Therefore no valid solution is missed.

---

# 22. Complexity

We process each ordered pair only a constant number of times.

Number of pairs:

```text
O(N^2)
```

---

## Using `map`

Each insertion / lookup:

```text
O(log M)
```

with:

```text
M <= O(N^2)
```

Therefore:

```text
log M = O(log N)
```

Total:

```text
Time = O(N^2 log N)
Space = O(N^2)
```

For:

```text
N = 1000
```

roughly:

```text
N^2 = 10^6
```

pair operations are realistic.

Compare with brute force:

```text
N^4 = 10^12
```

This is the enormous improvement.

---

# 23. Map vs `unordered_map`

The lecture-style solution uses:

```cpp
map
```

which gives deterministic:

```text
O(log N)
```

lookup.

You can often use:

```cpp
unordered_map
```

for average:

```text
O(1)
```

lookup.

That can make the expected total complexity:

```text
O(N^2)
```

But competitive-programming considerations include:

```text
hash quality
collision attacks
constant factors
memory
```

For learning the technique, `map` keeps the complexity argument simple and deterministic.

---

# 24. Overflow and Data Types

If:

```text
arr[i] <= 10^9
```

then a pair sum can reach:

```text
2 * 10^9
```

and a four-number sum can reach:

```text
4 * 10^9
```

which may exceed signed 32-bit `int`.

Therefore use:

```cpp
long long
```

for:

```text
array values
pair sums
target
complements
answer counts
```

Indices can remain:

```cpp
int
```

---

# 25. Common Mistakes

## Mistake 1 — Applying Variant 1 to `a < b < c < d`

A global set of pair sums does not guarantee non-overlapping ordered indices.

For ordered quadruples, preserve:

```text
b < c
```

through the sweep invariant.

---

## Mistake 2 — Inserting before querying

Wrong:

```text
insert pairs starting with c = b
query (a,b)
```

This allows:

```text
b = c
```

Correct:

```text
query first
insert afterward
```

---

## Mistake 3 — Forgetting that the map represents only RIGHT pairs

For current `b`, do not mentally read:

```text
pos = all pair sums
```

Read:

```text
pos = all valid pair sums (c,d)
      with b < c < d
```

This invariant is the algorithm.

---

## Mistake 4 — Storing only a boolean when indices are required

If output requires:

```text
a,b,c,d
```

store:

```text
sum -> pair(c,d)
```

---

## Mistake 5 — Storing only one pair when counting all answers

For counting:

```text
sum -> frequency
```

is required.

---

## Mistake 6 — Using `int` for values/sums

Prefer:

```cpp
long long
```

when values may be around `10^9`.

---

## Mistake 7 — Writing `pos[key]` just to test existence

With `map`:

```cpp
pos[key]
```

creates the key if it is missing.

For clean existence checks, prefer:

```cpp
pos.find(key) != pos.end()
```

The lecture's `pos[key]` boolean style is concise, but `find` avoids accidental insertions.

---

# 26. Recognition Checklist

Think of this pattern when you see:

```text
choose 4 values
N ≈ 1000
brute force O(N^4)
equation combines additively
```

Then ask:

```text
Can I group 4 choices as 2 + 2?
```

For example:

```text
A + B + C + D = T
```

becomes:

```text
(A+B) + (C+D) = T
```

and therefore:

```text
C+D = T - (A+B)
```

---

## If indices may overlap

Use:

```text
all pair sums
+
complement lookup
```

---

## If indices must satisfy `a < b < c < d`

Use:

```text
sweep a boundary b
+
query left pair
+
lookup right pair
+
maintain b < c invariant
```

---

# 27. General Meet-in-the-Middle Pattern

This lesson shows a second flavor of Meet-in-the-Middle.

The previous subset-sum MITM was:

```text
N decisions
↓
split elements into two groups
↓
enumerate 2^(N/2) states on each side
```

This 4-Sum version is:

```text
4 choices
↓
group them into 2 choices + 2 choices
↓
enumerate O(N^2) pair states
↓
combine with complement lookup
```

Both have the same philosophy:

```text
Do not enumerate the complete product directly.

Compress one half of the choices,
store their result,
then query the other half.
```

---

## Generic equation view

Suppose:

```text
f(a,b) + g(c,d) = TARGET
```

Then:

```text
g(c,d) = TARGET - f(a,b)
```

This suggests:

```text
store g(c,d)
query TARGET - f(a,b)
```

That pattern appears far beyond literal 4-Sum.

---

# 28. Final Mental Model

Remember the lecture in three layers.

---

## Layer 1 — Algebra

```text
A + B + C + D = T

(A+B) + (C+D) = T

X + Y = T

Y = T - X
```

---

## Layer 2 — Complexity

```text
4 nested loops
= O(N^4)

pair sums
= O(N^2) states

pair-sum lookup
= O(N^2 log N) with map
```

---

## Layer 3 — Index constraints

For reusable indices:

```text
store every pair globally
```

For:

```text
a < b < c < d
```

maintain a moving boundary:

```text
        current b
            |
            v
[ a ... b ] [ c ........ d ]
  query        stored map

a < b          b < c < d
```

At each `b`:

```text
1. Query all a < b.
2. Search complement in stored right pairs.
3. Only after querying, insert (b,d) for d > b.
4. Move b left.
```

The most important invariant is:

```text
Before querying current b,
the map contains only pairs (c,d)
with b < c < d.
```

Once that invariant is understood, the code becomes natural rather than memorized.

---

# One-Minute Revision

```text
4-Sum:
A+B+C+D=T

Split:
X=A+B
Y=C+D

Need:
Y=T-X

Variant 1:
indices can repeat
→ store all pair sums

Variant 2:
a<b<c<d
→ sweep b right-to-left
→ map stores only (c,d) with b<c<d
→ query before inserting c=b

Variant 3:
need actual indices
→ map sum -> (c,d)

Counting:
→ map sum -> frequency

Complexity:
map: O(N^2 log N)
space: O(N^2)
```
