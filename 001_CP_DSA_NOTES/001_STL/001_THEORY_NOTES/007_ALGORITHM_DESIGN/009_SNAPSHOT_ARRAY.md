# AlgoZenith — Snapshot Array (STL)
## Visual Self-Learning Version

> **Core idea:** Do not copy the whole array on every snapshot.  
> Give each index its own timeline of `{snapId, value}` changes.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Why the Naive Approach Is Slow](#2-why-the-naive-approach-is-slow)
- [3. Core Version-Timeline Idea](#3-core-version-timeline-idea)
- [4. State Design](#4-state-design)
- [5. set(index,val) — Step by Step](#5-setindexval--step-by-step)
- [6. snap() — Step by Step](#6-snap--step-by-step)
- [7. get(index,snapId) — Step by Step](#7-getindexsnapid--step-by-step)
- [8. Binary Search / Predecessor Query](#8-binary-search--predecessor-query)
- [9. Full Dry Run](#9-full-dry-run)
- [10. Complete C++17](#10-complete-c17)
- [11. Complexity](#11-complexity)
- [12. Common Mistakes](#12-common-mistakes)
- [13. Recognition Checklist](#13-recognition-checklist)
- [14. Final Mental Model](#14-final-mental-model)

---

# 0. Prerequisites

## 0.1 Array

A normal array stores one current value per index.

```text
index:  0   1   2
value: 10  20  30
```

If we change index `1`:

```text
20 -> 99
```

the old value is normally lost.

Snapshot Array must preserve old versions.

---

## 0.2 Version / Snapshot

A snapshot is a saved logical version.

Example:

```text
Version 0
Version 1
Version 2
...
```

You may later ask:

```text
What was index i at version s?
```

---

## 0.3 Change Point

Suppose index `0` behaves like:

```text
snap 0 -> 10
snap 1 -> 10
snap 2 -> 20
snap 3 -> 20
snap 4 -> 20
snap 5 -> 7
```

We do not need to store every snapshot separately.

Only store where value changes:

```text
(0,10)
(2,20)
(5,7)
```

These are **change points**.

---

## 0.4 Binary Search

Each index history is stored in increasing snapshot ID order.

Example:

```text
(0,10)  (2,20)  (5,7)
```

For query:

```text
snapId = 3
```

we need:

```text
latest stored ID <= 3
```

That is a predecessor search.

Binary search makes it:

```text
O(log H)
```

where `H` is that index's history length.

---

# 1. Problem

Support:

```text
set(index, val)
snap()
get(index, snapId)
```

Meaning:

```text
set(index,val)
-> change current value

snap()
-> save current version
-> return its snapshot ID

get(index,snapId)
-> value at that saved version
```

Example:

```text
set(0,5)
snap()      -> 0
set(0,8)
get(0,0)   -> 5
```

---

# 2. Why the Naive Approach Is Slow

Naive idea:

```text
snap()
  |
  v
copy complete array
  |
  v
store that copy
```

If array length is:

```text
N
```

then each snapshot costs:

```text
O(N)
```

If there are many snapshots, this is expensive.

---

## 2.1 Better Observation

Most indices usually do not change between snapshots.

So instead of storing:

```text
whole array for every version
```

store:

```text
only the changes
```

Example:

```text
index 0:
(0,10) -> (2,20) -> (5,7)

index 1:
(0,0) -> (4,9)

index 2:
(0,0)
```

Each index has its own independent timeline.

---

# 3. Core Version-Timeline Idea

For every index, store:

```text
{snapId, value}
```

Meaning:

```text
value becomes active starting at snapId
and remains active until the next change
```

---

## 3.1 Example

History:

```text
history[0] =
[(0,10), (2,20), (5,7)]
```

Timeline:

```text
snap:    0   1   2   3   4   5
value:  10  10  20  20  20   7
```

ASCII:

```text
snap 0          snap 2             snap 5
  |               |                  |
  v               v                  v
 10  ----------> 20  --------------> 7
 |                |                   |
 valid 0..1       valid 2..4          valid 5...
```

---

## 3.2 Why Every Index Starts With `{0,0}`

Default array value is:

```text
0
```

So every index begins with:

```text
(0,0)
```

This guarantees that every valid query has some record at or before its snapshot.

---

# 4. State Design

Maintain:

```cpp
vector<vector<pair<int,int>>> history;
int currentSnapId = 0;
```

Meaning:

```text
history[i]
=
change timeline for index i
```

and:

```text
currentSnapId
=
version currently being edited
```

---

## 4.1 Visual State

Suppose:

```text
history[0] = [(0,10), (2,20)]
history[1] = [(0,0),  (1,5)]
history[2] = [(0,0)]
```

ASCII:

```text
INDEX 0
snap 0          snap 2
  |               |
  v               v
 10  ----------> 20


INDEX 1
snap 0    snap 1
  |          |
  v          v
  0 -------> 5


INDEX 2
snap 0
  |
  v
  0
```

---

# 5. set(index,val) — Step by Step

There are two cases.

---

## 5.1 Case 1 — Same Current Version

Suppose:

```text
currentSnapId = 0
```

Call:

```text
set(0,5)
set(0,10)
```

No snapshot happened between them.

Only final value matters.

So:

```text
(0,5)
```

should become:

```text
(0,10)
```

not:

```text
(0,5), (0,10)
```

---

## 5.2 Step-by-Step

Get this index's records:

```cpp
auto& records = history[index];
```

Check last stored snapshot ID:

```cpp
records.back().first
```

If equal to:

```text
currentSnapId
```

overwrite value:

```cpp
records.back().second = val;
```

---

## 5.3 ASCII Overwrite

Before:

```text
currentSnapId = 0

history[0]:
(0,5)
```

Call:

```text
set(0,10)
```

After:

```text
history[0]:
(0,10)
```

Reason:

```text
snapshot 0 is still open
```

---

## 5.4 Case 2 — Later Version

Suppose history is:

```text
(0,10)
```

and:

```text
currentSnapId = 2
```

Call:

```text
set(0,20)
```

Now we must preserve old version.

Append:

```text
(0,10) -> (2,20)
```

Code:

```cpp
records.push_back({currentSnapId, val});
```

---

## 5.5 set() Flow

```text
set(index,val)
      |
      v
records = history[index]
      |
      v
last record snapId == currentSnapId ?
       /                         \
     yes                         no
      |                           |
      v                           v
overwrite value              append record
```

---

# 6. snap() — Step by Step

`snap()` does not copy anything.

It only closes the current logical version.

---

## 6.1 Example

Before:

```text
currentSnapId = 0
```

Call:

```text
snap()
```

Return:

```text
0
```

Then advance:

```text
currentSnapId = 1
```

Code:

```cpp
return currentSnapId++;
```

---

## 6.2 ASCII Version Boundary

```text
Version 0            Version 1
   OPEN                 FUTURE

------|------------------->
      ^
      |
    snap()
```

After `snap()`:

```text
Version 0 = closed/saved
Version 1 = current editable version
```

---

## 6.3 Important Observation

If no values changed:

```text
snap()
```

still works.

No extra records are required.

That is why this design stores changes instead of full versions.

---

# 7. get(index,snapId) — Step by Step

Need:

```text
value of index
at requested snapshot
```

Suppose history is:

```text
(0,10)
(2,20)
(5,7)
```

Query:

```text
get(index,3)
```

Which record applies?

Need:

```text
latest record with snapId <= 3
```

That is:

```text
(2,20)
```

Answer:

```text
20
```

---

## 7.1 Why Exact Match Is Wrong

There may be no record with ID `3`.

But value at snapshot `3` still exists.

Timeline:

```text
snap:   0   1   2   3   4   5
value: 10  10  20  20  20   7
```

So:

```text
get(index,3)
```

must return value from the latest earlier change.

---

# 8. Binary Search / Predecessor Query

We need:

```text
largest stored snapId <= target
```

A convenient STL method is:

```cpp
upper_bound()
```

Find:

```text
first record with snapId > target
```

Then move one step left.

---

## 8.1 Visual

Records:

```text
(0,10)    (2,20)       (5,7)
```

Target:

```text
3
```

ASCII:

```text
0          2          5
|----------|----------|
           ^          ^
           |          |
        answer     upper_bound(3)
```

So:

```text
upper_bound finds 5
step left -> 2
answer -> 20
```

---

## 8.2 C++ with pair

```cpp
auto it = upper_bound(
    records.begin(),
    records.end(),
    make_pair(snapId, INT_MAX)
);
```

Why:

```text
{snapId, INT_MAX}
```

?

Pairs compare:

```text
first
then second
```

So this key moves past all records whose first value is:

```text
snapId
```

Then:

```cpp
--it;
```

gives:

```text
largest snap ID <= target
```

---

## 8.3 Simpler Mental Model

Do not memorize `INT_MAX` blindly.

Remember:

```text
GET
=
predecessor search
=
latest change at or before target snapshot
```

That is the real idea.

---

# 9. Full Dry Run

Start:

```text
currentSnapId = 0

history[0] = [(0,0)]
```

---

## Step 1 — set(0,5)

Last record has same ID:

```text
0 == currentSnapId
```

Overwrite:

```text
[(0,0)]
    |
    v
[(0,5)]
```

---

## Step 2 — set(0,10)

Still snapshot 0.

Overwrite again:

```text
[(0,5)]
    |
    v
[(0,10)]
```

Only final pending value matters.

---

## Step 3 — snap()

Return:

```text
0
```

Advance:

```text
currentSnapId:
0 -> 1
```

History remains:

```text
[(0,10)]
```

---

## Step 4 — snap()

No changes.

Return:

```text
1
```

Advance:

```text
currentSnapId:
1 -> 2
```

History still:

```text
[(0,10)]
```

No duplicate snapshot storage.

---

## Step 5 — set(0,5)

Now:

```text
currentSnapId = 2
```

Last record ID:

```text
0
```

Different version, so append:

```text
[(0,10), (2,5)]
```

---

## Step 6 — get(0,1)

Timeline:

```text
snap:    0          1          2
         |----------|----------|
value:  10         10          5
                    ^
                    |
                  query
```

Stored changes:

```text
(0,10)               (2,5)
```

Need:

```text
largest ID <= 1
```

That is:

```text
0
```

Answer:

```text
10
```

---

## 9.1 State Transition Table

| Step | Operation | `currentSnapId` after op | `history[0]` | Result |
|---:|---|---:|---|---|
| 0 | Start | 0 | `[(0,0)]` | — |
| 1 | `set(0,5)` | 0 | `[(0,5)]` | — |
| 2 | `set(0,10)` | 0 | `[(0,10)]` | — |
| 3 | `snap()` | 1 | `[(0,10)]` | `0` |
| 4 | `snap()` | 2 | `[(0,10)]` | `1` |
| 5 | `set(0,5)` | 2 | `[(0,10),(2,5)]` | — |
| 6 | `get(0,1)` | 2 | unchanged | `10` |

---

# 10. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class SnapshotArray {
    int currentSnapId = 0;

    vector<vector<pair<int, int>>> history;

public:
    SnapshotArray(int length)
        : history(
            length,
            vector<pair<int, int>>{{0, 0}}
        ) {}

    void set(int index, int val) {
        auto& records = history[index];

        if (records.back().first == currentSnapId) {
            // Same pending version -> overwrite.
            records.back().second = val;
        } else {
            // New version -> append change point.
            records.push_back({currentSnapId, val});
        }
    }

    int snap() {
        return currentSnapId++;
    }

    int get(int index, int snapId) {
        const auto& records = history[index];

        auto it = upper_bound(
            records.begin(),
            records.end(),
            make_pair(snapId, INT_MAX)
        );

        --it;

        return it->second;
    }
};
```

---

# 11. Complexity

Let:

```text
H
=
number of stored change records
for the queried index
```

| Operation | Complexity |
|---|---:|
| Constructor | `O(length)` |
| `set()` | amortized `O(1)` |
| `snap()` | `O(1)` |
| `get()` | `O(log H)` |
| Space | `O(length + stored changes)` |

Why `get()` is logarithmic:

```text
history per index
is sorted by snapId
        |
        v
binary search
```

---

# 12. Common Mistakes

## Mistake 1 — Copy Whole Array on snap()

That makes:

```text
snap() = O(length)
```

Instead:

```text
snap() only advances version ID
```

---

## Mistake 2 — Search Only Exact snapId

Wrong.

Example:

```text
records:
(0,10), (2,20)

get(...,1)
```

There is no exact record `1`.

But correct answer is:

```text
10
```

Need:

```text
largest ID <= target
```

---

## Mistake 3 — Overwrite Completed Snapshot

Only overwrite when:

```text
records.back().first == currentSnapId
```

Older records must never be modified.

---

## Mistake 4 — Store Every set() in Same Version

Example:

```text
set(0,5)
set(0,10)
```

before a snapshot.

Do not store both:

```text
(0,5), (0,10)
```

Only final observable value matters:

```text
(0,10)
```

---

## Mistake 5 — Forget Initial `{0,0}`

Without a default record, early queries may have no predecessor.

Initialize every index with:

```text
(0,0)
```

---

## Mistake 6 — Confuse Current Snap ID with Last Saved Snap ID

Think:

```text
currentSnapId
=
version currently being edited
```

`snap()` returns that ID and then advances it.

---

# 13. Recognition Checklist

Think Snapshot Array when you see:

```text
versioned array
+
many snapshots
+
historical point queries
```

Ask:

```text
1. Do I really need to copy the whole array?
   -> usually no

2. Which positions actually changed?
   -> store change points

3. Can each index keep its own history?
   -> yes

4. What does one record mean?
   -> {snapId, value}

5. Multiple set() calls in same version?
   -> overwrite last record

6. New version set()?
   -> append record

7. What does snap() do?
   -> return currentSnapId++

8. What does get() need?
   -> latest change <= target snapshot

9. What search is that?
   -> predecessor query

10. How to find predecessor quickly?
    -> binary search / upper_bound
```

---

# 14. Final Mental Model

Think of every index as its own version timeline.

```text
INDEX 0

snap 0             snap 2             snap 5
  |                  |                  |
  v                  v                  v
 10 ---------------> 20 -------------> 7
 |                    |                 |
 valid 0..1           valid 2..4        valid 5...
```

Operations:

```text
SET
 |
 +-- same current version?
 |      |
 |      v
 |   overwrite
 |
 +-- later version?
        |
        v
      append
```

```text
SNAP
 |
 v
return currentSnapId
 |
 v
currentSnapId++
```

```text
GET
 |
 v
find first stored ID > target
 |
 v
step left
 |
 v
latest stored ID <= target
 |
 v
return its value
```

Remember:

```text
SET  -> store a change point
SNAP -> advance version
GET  -> predecessor search
```

The one sentence to remember:

```text
Snapshot Array is a per-index version history:
store only change points, and binary-search the latest change
at or before the requested snapshot.
```
