# AlgoZenith --- Structuring State: Dynamic Dashboard

> **Core idea:** If a query is expensive to recompute but easy to
> update, **store that answer as state and maintain it after every
> modification**.

## Table of Contents

-   [1. Problem](#1-problem)
-   [2. Design Backward from Queries](#2-design-backward-from-queries)
-   [3. State and Invariants](#3-state-and-invariants)
-   [4. Insert](#4-insert)
-   [5. Remove](#5-remove)
-   [6. Sum](#6-sum)
-   [7. Get Maximum](#7-get-maximum)
-   [8. Get Distinct](#8-get-distinct)
-   [9. Complete C++](#9-complete-c)
-   [10. Dry Run](#10-dry-run)
-   [11. Complexity](#11-complexity)
-   [12. Common Mistakes](#12-common-mistakes)
-   [13. Final Mental Model](#13-final-mental-model)

------------------------------------------------------------------------

# 1. Problem

Maintain a dynamic collection supporting:

``` text
Insert(x)       → add one x
Remove(x)       → remove one x
Sum()           → sum of all active values
GetMax()        → largest active value
GetDistinct()   → number of distinct active values
```

Duplicates are allowed.

Target:

``` text
Sum()         → O(1)
GetMax()      → O(1)
GetDistinct() → O(1)
```

------------------------------------------------------------------------

# 2. Design Backward from Queries

Do **not** start with:

``` text
"Which STL container should I use?"
```

Start with:

``` text
"What information must each query know?"
```

  Query             Required State
  ----------------- -------------------------
  `Sum()`           running sum
  `GetMax()`        values kept ordered
  `GetDistinct()`   active distinct keys
  `Insert/Remove`   frequency of each value

Therefore maintain:

``` cpp
long long curSum;
map<int,int> mp;
```

Meaning:

``` text
curSum = sum of all active occurrences
mp[x]  = frequency of x
```

Why `map`?

``` text
ordered keys
+ frequencies
+ largest key through rbegin()
```

------------------------------------------------------------------------

# 3. State and Invariants

The data structure must always preserve:

``` text
1. curSum = sum of all active occurrences

2. mp[x] > 0 for every stored key

3. mp.size() = number of distinct active values

4. mp.rbegin()->first = maximum active value
```

The crucial rule is:

``` text
frequency becomes 0
        ↓
erase the key
```

Otherwise both:

``` text
GetDistinct()
GetMax()
```

can become wrong.

------------------------------------------------------------------------

# 4. Insert

Insert one occurrence of `x`.

## State Transition

``` text
sum increases by x
frequency of x increases by 1
```

Algebra:

``` text
S' = S + x
f'(x) = f(x) + 1
```

C++:

``` cpp
void insert(int x) {
    curSum += x;
    mp[x]++;
}
```

Example:

``` text
before:
sum = 8
mp = {3:1, 5:1}

Insert(3)

after:
sum = 11
mp = {3:2, 5:1}
```

Distinct count stays `2`.

------------------------------------------------------------------------

# 5. Remove

Remove **one occurrence** of `x`.

First verify that `x` exists.

## State Transition

``` text
S' = S - x
f'(x) = f(x) - 1
```

If:

``` text
f'(x) = 0
```

erase `x`.

## C++

``` cpp
bool remove(int x) {
    auto it = mp.find(x);

    if (it == mp.end())
        return false;

    curSum -= x;
    it->second--;

    if (it->second == 0)
        mp.erase(it);

    return true;
}
```

## Dry Run

``` text
sum = 11
mp = {3:2, 5:1}
```

`Remove(3)`:

``` text
sum = 8
mp[3] = 1

mp = {3:1, 5:1}
```

Remove `3` again:

``` text
sum = 5
mp[3] = 0
```

Erase key:

``` text
mp = {5:1}
```

Now:

``` text
distinct = 1
max = 5
```

------------------------------------------------------------------------

# 6. Sum

Naive:

``` text
scan every value
→ O(n)
```

Better:

``` text
maintain sum during every update
```

Then:

``` cpp
long long sum() const {
    return curSum;
}
```

Complexity:

``` text
O(1)
```

This is **cached state**:

``` text
expensive to recompute
+
cheap to update
        ↓
store it
```

------------------------------------------------------------------------

# 7. Get Maximum

`map` keeps keys sorted:

``` text
smallest ................ largest
begin()                  rbegin()
```

Therefore:

``` cpp
int getMax() const {
    return mp.rbegin()->first;
}
```

Complexity:

``` text
O(1)
```

Precondition:

``` text
map must not be empty
```

------------------------------------------------------------------------

# 8. Get Distinct

Because zero-frequency keys are erased:

``` text
one map key
=
one distinct active value
```

Therefore:

``` cpp
int getDistinct() const {
    return mp.size();
}
```

Complexity:

``` text
O(1)
```

Example:

``` text
values = [2,2,5,8,8,8]

mp:
2 → 2
5 → 1
8 → 3

mp.size() = 3
```

So:

``` text
distinct = 3
```

------------------------------------------------------------------------

# 9. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

struct Dashboard {

    long long curSum = 0;
    map<int,int> mp;

    void insert(int x) {
        curSum += x;
        mp[x]++;
    }

    bool remove(int x) {

        auto it = mp.find(x);

        if (it == mp.end())
            return false;

        curSum -= x;
        it->second--;

        if (it->second == 0)
            mp.erase(it);

        return true;
    }

    long long sum() const {
        return curSum;
    }

    int getMax() const {
        return mp.rbegin()->first;
    }

    int getDistinct() const {
        return (int)mp.size();
    }
};
```

------------------------------------------------------------------------

# 10. Dry Run

Operations:

``` text
Insert(5)
Insert(3)
Insert(5)
Insert(8)
Remove(5)
```

  Operation       `curSum` `mp`                Max   Distinct
  ------------- ---------- ----------------- ----- ----------
  start                  0 `{}`                ---          0
  `Insert(5)`            5 `{5:1}`               5          1
  `Insert(3)`            8 `{3:1,5:1}`           5          2
  `Insert(5)`           13 `{3:1,5:2}`           5          2
  `Insert(8)`           21 `{3:1,5:2,8:1}`       8          3
  `Remove(5)`           16 `{3:1,5:1,8:1}`       8          3

Now remove `5` again:

``` text
frequency[5]: 1 → 0
```

Erase it:

``` text
curSum = 11
mp = {3:1, 8:1}

max      = 8
distinct = 2
```

------------------------------------------------------------------------

# 11. Complexity

Let:

``` text
D = number of distinct active values
```

  Operation           Complexity
  ----------------- ------------
  `Insert(x)`         `O(log D)`
  `Remove(x)`         `O(log D)`
  `Sum()`                 `O(1)`
  `GetMax()`              `O(1)`
  `GetDistinct()`         `O(1)`
  Space                   `O(D)`

Why updates are `O(log D)`:

``` text
std::map
→ ordered balanced-tree structure
→ find / insert / erase = O(log D)
```

------------------------------------------------------------------------

# 12. Common Mistakes

## Mistake 1 --- Recalculate Sum

Bad:

``` cpp
for (auto [x, freq] : mp)
    sum += 1LL * x * freq;
```

That makes every query:

``` text
O(D)
```

Instead maintain:

``` cpp
curSum += x;
curSum -= x;
```

------------------------------------------------------------------------

## Mistake 2 --- Keep Zero Frequencies

Bad:

``` cpp
mp[x]--;
```

and leave:

``` text
x → 0
```

Then:

``` text
mp.size()
```

is no longer the number of active distinct values.

Also `x` may incorrectly affect maximum.

Correct:

``` cpp
if (it->second == 0)
    mp.erase(it);
```

------------------------------------------------------------------------

## Mistake 3 --- Remove Missing Value

Bad:

``` cpp
curSum -= x;
mp[x]--;
```

If `x` does not exist, `mp[x]` creates it with `0`.

Then:

``` text
0 → -1
```

and `curSum` is corrupted.

Correct:

``` cpp
auto it = mp.find(x);

if (it == mp.end())
    return false;
```

------------------------------------------------------------------------

## Mistake 4 --- Use `set`

A set stores only:

``` text
present / absent
```

It loses frequency.

For:

``` text
[5,5,5]
```

we need:

``` text
mp[5] = 3
```

because removing one `5` must leave two copies.

------------------------------------------------------------------------

## Mistake 5 --- Use `int` for Sum

Even if:

``` text
x fits in int
```

the total may overflow.

Prefer:

``` cpp
long long curSum;
```

------------------------------------------------------------------------

# 13. Final Mental Model

Start from the required operations:

``` text
Need Sum in O(1)?
      ↓
maintain curSum

Need duplicates?
      ↓
maintain frequency

Need Max quickly?
      ↓
keep keys ordered

Need Distinct in O(1)?
      ↓
one key per active value
      ↓
erase frequency 0
```

So the final state is:

``` text
        Dashboard
            │
     ┌──────┴──────┐
     │             │
  curSum       map<x,freq>
     │             │
     │             ├── rbegin() → maximum
     │             │
     │             └── size()   → distinct
     │
     └── sum() → total
```

## One Sentence to Remember

> **Design the state from the queries backward: cache aggregates that
> are cheap to update, and maintain invariants after every
> modification.**
