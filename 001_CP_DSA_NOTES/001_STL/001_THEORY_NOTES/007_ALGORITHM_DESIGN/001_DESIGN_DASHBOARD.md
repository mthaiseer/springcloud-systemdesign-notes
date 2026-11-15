# AlgoZenith — Structuring State: Dynamic Dashboard
## Visual Self-Learning Version

> **Core idea:** If a query is expensive to recompute but easy to update,
> **store that answer as state and maintain it after every modification**.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Design Backward from Queries](#2-design-backward-from-queries)
- [3. Choosing the State](#3-choosing-the-state)
- [4. State Invariants](#4-state-invariants)
- [5. Insert](#5-insert)
- [6. Remove](#6-remove)
- [7. Sum Query](#7-sum-query)
- [8. Get Maximum](#8-get-maximum)
- [9. Get Distinct](#9-get-distinct)
- [10. Complete C++17](#10-complete-c17)
- [11. Full Dry Run](#11-full-dry-run)
- [12. Complexity](#12-complexity)
- [13. Common Mistakes](#13-common-mistakes)
- [14. Recognition Checklist](#14-recognition-checklist)
- [15. Final Mental Model](#15-final-mental-model)

---

# 0. Prerequisites

Before this pattern, understand these small ideas.

## 0.1 Frequency Counting

If active values are:

```text
[5, 3, 5, 8]
```

frequencies are:

```text
3 -> 1
5 -> 2
8 -> 1
```

Store them as:

```cpp
map<int, int> mp;
```

Meaning:

```text
mp[x] = number of active copies of x
```

---

## 0.2 Why Duplicates Matter

For:

```text
[5,5,5]
```

a `set` only remembers:

```text
5 exists
```

But after removing one `5`, two copies must remain.

So we need:

```text
5 -> 3
```

not only presence/absence.

---

## 0.3 Ordered Map

`std::map` keeps keys sorted.

Example:

```text
3 -> 1
5 -> 2
8 -> 1
```

Visual:

```text
smallest ---------------------- largest

   3             5               8
   ^                             ^
   |                             |
begin()                       rbegin()
```

Therefore:

```cpp
mp.rbegin()->first
```

gives the largest active key.

---

## 0.4 Cached State

Suppose we repeatedly ask for the total sum.

Naive:

```text
scan everything
recompute sum
```

Better:

```text
maintain curSum
```

On insert:

```text
curSum += x
```

On remove:

```text
curSum -= x
```

Then:

```text
Sum() = O(1)
```

This is **cached state**.

---

## 0.5 Invariant

An invariant is a condition that must remain true after every operation.

Example:

```text
curSum
=
sum of all active occurrences
```

If an update breaks this, later queries become wrong.

---

# 1. Problem

Maintain a dynamic collection supporting:

```text
Insert(x)       -> add one x
Remove(x)       -> remove one x
Sum()           -> sum of all active values
GetMax()        -> largest active value
GetDistinct()   -> number of distinct active values
```

Duplicates are allowed.

Target query time:

```text
Sum()         -> O(1)
GetMax()      -> O(1)
GetDistinct() -> O(1)
```

---

## 1.1 Tiny Example

Active values:

```text
[3,5,5,8]
```

Queries:

```text
Sum        = 21
Max        = 8
Distinct   = 3
```

Frequency representation:

```text
3 -> 1
5 -> 2
8 -> 1
```

---

# 2. Design Backward from Queries

Do **not** start with:

```text
"Which STL container should I use?"
```

Start with:

```text
"What information must each query know?"
```

---

## 2.1 Query -> Required Information

```text
Sum()
  |
  v
need total of all active occurrences
  |
  v
running sum


GetMax()
  |
  v
need largest active value
  |
  v
ordered keys


GetDistinct()
  |
  v
need number of active different values
  |
  v
one key per active value


Insert / Remove
  |
  v
need multiplicity
  |
  v
frequency map
```

So maintain:

```cpp
long long curSum = 0;
map<int, int> mp;
```

---

# 3. Choosing the State

Meaning:

```text
curSum
=
sum of all active occurrences

mp[x]
=
frequency of x
```

This small state is enough to answer every operation.

---

## 3.1 Visual State

For:

```text
[3,5,5,8]
```

we store:

```text
curSum = 21

mp:
3 -> 1
5 -> 2
8 -> 1
```

ASCII:

```text
                 Dashboard
                    |
         +----------+----------+
         |                     |
         v                     v
     curSum = 21          map<x,freq>
                              |
                    +---------+---------+
                    |                   |
                    v                   v
              rbegin()->first         size()
                    |                   |
                    v                   v
                  max=8             distinct=3
```

---

# 4. State Invariants

The data structure must always preserve these rules.

```text
1. curSum = sum of all active occurrences

2. every stored key has positive frequency

3. mp.size() = number of distinct active values

4. mp.rbegin()->first = maximum active value
   when map is non-empty
```

---

## 4.1 Most Important Invariant

```text
frequency becomes 0
        |
        v
ERASE THE KEY
```

Why?

Because leaving:

```text
5 -> 0
```

would make both:

```text
GetDistinct()
GetMax()
```

potentially wrong.

---

## 4.2 Visual Example

Invalid state:

```text
3 -> 1
5 -> 0
8 -> 1
```

`5` is not active anymore.

Correct state:

```text
3 -> 1
8 -> 1
```

Now:

```text
distinct = 2
max      = 8
```

---

# 5. Insert

Insert one occurrence of `x`.

## 5.1 What Changes?

```text
sum increases by x
frequency of x increases by 1
```

Algebra:

```text
S' = S + x
f'(x) = f(x) + 1
```

---

## 5.2 Step-by-Step

```text
Insert(x)

Step 1:
curSum += x

Step 2:
mp[x]++
```

That is all.

---

## 5.3 ASCII Dry Run

Before:

```text
curSum = 8

mp:
3 -> 1
5 -> 1
```

Operation:

```text
Insert(3)
```

Update sum:

```text
8 + 3 = 11
```

Update frequency:

```text
3 -> 1
becomes
3 -> 2
```

After:

```text
curSum = 11

mp:
3 -> 2
5 -> 1
```

Distinct remains:

```text
2
```

because `3` already existed.

---

## 5.4 New Value vs Duplicate

New value:

```text
Insert(8)

before:
3 -> 1
5 -> 2

after:
3 -> 1
5 -> 2
8 -> 1

distinct:
2 -> 3
```

Duplicate:

```text
Insert(5)

5 -> 2
becomes
5 -> 3

distinct unchanged
```

---

## 5.5 C++17

```cpp
void insert(int x) {
    curSum += x;
    mp[x]++;
}
```

---

# 6. Remove

Remove **one occurrence** of `x`.

This needs more care.

---

## 6.1 Step 1 — Check Existence

Never update sum before checking.

```cpp
auto it = mp.find(x);

if (it == mp.end()) {
    return false;
}
```

---

## 6.2 State Transition

If `x` exists:

```text
S' = S - x
f'(x) = f(x) - 1
```

Then:

```text
if f'(x) == 0
erase x
```

---

## 6.3 Step-by-Step

```text
Remove(x)

Step 1:
find x

Step 2:
if missing -> return false

Step 3:
curSum -= x

Step 4:
frequency--

Step 5:
if frequency == 0
erase key

Step 6:
return true
```

---

## 6.4 ASCII Dry Run

Start:

```text
curSum = 11

mp:
3 -> 2
5 -> 1
```

### Remove(3)

```text
sum:
11 - 3 = 8

frequency:
3 -> 2
becomes
3 -> 1
```

State:

```text
3 -> 1
5 -> 1
```

Do not erase `3`.

---

### Remove(3) Again

```text
sum:
8 - 3 = 5

frequency:
3 -> 1
becomes
3 -> 0
```

Now:

```text
erase 3
```

Final:

```text
mp:
5 -> 1

sum      = 5
max      = 5
distinct = 1
```

---

## 6.5 Why Erasing at Zero Matters

Bad:

```text
3 -> 0
5 -> 1
```

Then:

```text
mp.size() = 2
```

but active distinct count is:

```text
1
```

So zero-frequency keys must not stay.

---

## 6.6 C++17

```cpp
bool remove(int x) {
    auto it = mp.find(x);

    if (it == mp.end()) {
        return false;
    }

    curSum -= x;
    it->second--;

    if (it->second == 0) {
        mp.erase(it);
    }

    return true;
}
```

---

# 7. Sum Query

Naive:

```text
scan map
value × frequency
add everything
```

Cost:

```text
O(D)
```

where:

```text
D = number of distinct active values
```

Better:

```text
maintain curSum during updates
```

Then:

```cpp
long long sum() const {
    return curSum;
}
```

Complexity:

```text
O(1)
```

---

## 7.1 Core Cached-State Pattern

```text
expensive to recompute
        +
cheap to update
        |
        v
STORE IT
```

This is the main design lesson.

---

# 8. Get Maximum

`map` keeps keys ordered:

```text
smallest .................... largest

begin()                      rbegin()
```

Therefore:

```cpp
int getMax() const {
    return mp.rbegin()->first;
}
```

---

## 8.1 ASCII Example

```text
mp:

2 -> 2
5 -> 1
8 -> 3

keys:

2   5   8
        ^
        |
      maximum
```

So:

```text
GetMax() = 8
```

---

## 8.2 Important Precondition

The map must not be empty.

If the problem allows empty state, handle that case explicitly.

---

# 9. Get Distinct

Because zero-frequency keys are erased:

```text
one map key
=
one active distinct value
```

Therefore:

```cpp
int getDistinct() const {
    return (int)mp.size();
}
```

---

## 9.1 ASCII Example

Active:

```text
[2,2,5,8,8,8]
```

Map:

```text
2 -> 2
5 -> 1
8 -> 3
```

Number of keys:

```text
3
```

Therefore:

```text
Distinct = 3
```

---

# 10. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

struct Dashboard {
    long long curSum = 0;
    map<int, int> mp;

    void insert(int x) {
        curSum += x;
        mp[x]++;
    }

    bool remove(int x) {
        auto it = mp.find(x);

        if (it == mp.end()) {
            return false;
        }

        curSum -= x;
        it->second--;

        if (it->second == 0) {
            mp.erase(it);
        }

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

---

# 11. Full Dry Run

Start:

```text
curSum = 0
mp = {}
```

## Step 1 — Insert(5)

```text
curSum = 0 + 5 = 5

mp:
5 -> 1

Max      = 5
Distinct = 1
```

---

## Step 2 — Insert(3)

```text
curSum = 5 + 3 = 8

mp:
3 -> 1
5 -> 1

Max      = 5
Distinct = 2
```

---

## Step 3 — Insert(5)

```text
curSum = 8 + 5 = 13

5:
1 -> 2

mp:
3 -> 1
5 -> 2

Max      = 5
Distinct = 2
```

Observation:

```text
duplicate inserted
-> frequency changes
-> distinct unchanged
```

---

## Step 4 — Insert(8)

```text
curSum = 13 + 8 = 21

mp:
3 -> 1
5 -> 2
8 -> 1

Max      = 8
Distinct = 3
```

---

## Step 5 — Remove(5)

```text
curSum = 21 - 5 = 16

5:
2 -> 1

mp:
3 -> 1
5 -> 1
8 -> 1

Max      = 8
Distinct = 3
```

---

## Step 6 — Remove(5)

```text
curSum = 16 - 5 = 11

5:
1 -> 0
```

Erase key:

```text
5
```

Final state:

```text
mp:
3 -> 1
8 -> 1

curSum   = 11
Max      = 8
Distinct = 2
```

---

## 11.1 State Transition Table

| Step | Operation | Sum | Map | Max | Distinct |
|---:|---|---:|---|---:|---:|
| 0 | Start | 0 | `{}` | — | 0 |
| 1 | `Insert(5)` | 5 | `{5:1}` | 5 | 1 |
| 2 | `Insert(3)` | 8 | `{3:1,5:1}` | 5 | 2 |
| 3 | `Insert(5)` | 13 | `{3:1,5:2}` | 5 | 2 |
| 4 | `Insert(8)` | 21 | `{3:1,5:2,8:1}` | 8 | 3 |
| 5 | `Remove(5)` | 16 | `{3:1,5:1,8:1}` | 8 | 3 |
| 6 | `Remove(5)` | 11 | `{3:1,8:1}` | 8 | 2 |

---

# 12. Complexity

Let:

```text
D = number of distinct active values
```

| Operation | Complexity |
|---|---:|
| `Insert(x)` | `O(log D)` |
| `Remove(x)` | `O(log D)` |
| `Sum()` | `O(1)` |
| `GetMax()` | `O(1)` |
| `GetDistinct()` | `O(1)` |
| Space | `O(D)` |

Why updates are logarithmic:

```text
std::map
-> ordered balanced tree
-> find / insert / erase = O(log D)
```

---

# 13. Common Mistakes

## Mistake 1 — Recalculate Sum

Bad:

```cpp
long long sum = 0;

for (auto [x, freq] : mp) {
    sum += 1LL * x * freq;
}
```

This makes every sum query:

```text
O(D)
```

Better:

```text
curSum += x
curSum -= x
```

---

## Mistake 2 — Keep Zero Frequencies

Bad:

```text
5 -> 0
```

Then:

```text
mp.size()
```

is not the number of active distinct values.

Correct:

```cpp
if (it->second == 0) {
    mp.erase(it);
}
```

---

## Mistake 3 — Remove Missing Value

Bad:

```cpp
curSum -= x;
mp[x]--;
```

If missing, `mp[x]` creates a new zero entry and changes it to:

```text
-1
```

The sum is corrupted too.

Correct:

```cpp
auto it = mp.find(x);

if (it == mp.end()) {
    return false;
}
```

---

## Mistake 4 — Use `set`

A set stores:

```text
present / absent
```

But duplicates require:

```text
frequency
```

For:

```text
[5,5,5]
```

we need:

```text
5 -> 3
```

---

## Mistake 5 — Use `int` for Sum

Individual values may fit in `int`, but their total may not.

Prefer:

```cpp
long long curSum;
```

---

## Mistake 6 — Get Max from Empty State

This is invalid on an empty map:

```cpp
mp.rbegin()->first
```

Handle emptiness if the problem does not guarantee at least one active value.

---

# 14. Recognition Checklist

Think of this pattern when you see:

```text
dynamic updates
+
many repeated aggregate queries
```

Ask:

```text
1. What queries must be fast?

2. What information does each query need?

3. Which answer is expensive to recompute?

4. Is it cheap to update after Insert/Remove?

5. Can I cache that answer?

6. Do duplicates matter?

7. Do I need ordered keys for min/max?

8. What invariants must stay true?

9. What must happen when frequency becomes zero?

10. Am I designing state from queries backward?
```

---

# 15. Final Mental Model

```text
             QUERY REQUIREMENTS
                     |
                     v
          DESIGN STATE BACKWARD
                     |
        +------------+------------+
        |                         |
        v                         v
 cached aggregate          structural state
    curSum                 map<value,freq>
        |                         |
        |               +---------+---------+
        |               |                   |
        v               v                   v
      Sum()          GetMax()          GetDistinct()
```

Every update:

```text
Insert / Remove
      |
      v
update cached state
      |
      v
update frequencies
      |
      v
restore invariants
      |
      v
queries remain cheap
```

The sentence to remember:

```text
If an answer is expensive to recompute
but cheap to update,
store it as state and maintain it.
```
