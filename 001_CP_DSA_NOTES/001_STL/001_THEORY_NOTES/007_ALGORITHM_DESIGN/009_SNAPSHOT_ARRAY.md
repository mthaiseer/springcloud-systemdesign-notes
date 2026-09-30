# AlgoZenith --- Snapshot Array (STL)

> **Core idea:** Do not copy the whole array on every snapshot. Give
> each index its own timeline of `{snapId, value}` changes.

## TOC

-   [1. Problem](#1-problem)
-   [2. Naive vs Optimal](#2-naive-vs-optimal)
-   [3. Core Design](#3-core-design)
-   [4. Operations](#4-operations)
-   [5. Binary Search](#5-binary-search)
-   [6. Dry Run](#6-dry-run)
-   [7. Complete C++](#7-complete-c)
-   [8. Complexity & Mistakes](#8-complexity--mistakes)
-   [9. Mental Model](#9-mental-model)

------------------------------------------------------------------------

# 1. Problem

``` text
set(index,val)      → change current value
snap()              → save state; return snapshot ID
get(index,snapId)   → value at that saved version
```

Example:

``` text
set(0,5)
snap()      → 0
set(0,8)
get(0,0)   → 5
```

------------------------------------------------------------------------

# 2. Naive vs Optimal

Naive:

``` text
snap()
  ↓
copy complete array
  ↓
O(length) per snapshot
```

Better: store only changes.

``` text
index 0 → (0,10) ─────► (2,20) ─────► (5,7)
index 1 → (0,0)  ────────────────────► (4,9)
index 2 → (0,0)
```

Each index has its own version history.

------------------------------------------------------------------------

# 3. Core Design

``` cpp
vector<vector<pair<int,int>>> history;
int currentSnapId = 0;
```

A record:

``` text
{snapId, value}
```

means:

``` text
value becomes active starting at snapId
until the next record
```

Every index starts with:

``` text
{0,0}
```

Example:

``` text
history[0] = [(0,10), (2,20), (5,7)]

snapshot:   0   1   2   3   4   5
value:     10  10  20  20  20   7
```

------------------------------------------------------------------------

# 4. Operations

## `set(index,val)`

### Same pending version → overwrite

``` text
currentSnapId = 0

set(0,5)
set(0,10)

(0,5)
   ↓ overwrite
(0,10)
```

Only the final value can be observed by the future snapshot.

``` cpp
if (records.back().first == currentSnapId)
    records.back().second = val;
```

### Later version → append

``` text
saved: (0,10)

currentSnapId = 2
set(0,20)

(0,10) ─────► (2,20)
```

``` cpp
else
    records.push_back({currentSnapId, val});
```

------------------------------------------------------------------------

## `snap()`

No array copying.

``` cpp
return currentSnapId++;
```

Visual:

``` text
Version 0          Version 1
  CLOSED              OPEN
─────│────────────────│─────►
   snap()
```

------------------------------------------------------------------------

## `get(index,snapId)`

Need:

``` text
latest record with:

record.snapId <= requested snapId
```

Example:

``` text
records:

(0,10) ───── (2,20) ───── (5,7)

target = 3
                 ↑
latest ID <= 3 is 2

answer = 20
```

This is a **predecessor query**.

------------------------------------------------------------------------

# 5. Binary Search

Use:

``` cpp
upper_bound()
```

Find:

``` text
first record with ID > target
```

then step left.

``` text
target = 3

(0,10)    (2,20)       (5,7)
              ↑           ↑
            answer     upper_bound
```

C++:

``` cpp
auto it = upper_bound(
    records.begin(),
    records.end(),
    make_pair(snap_id, INT_MAX)
);

--it;
return it->second;
```

Why `INT_MAX`?

Pairs compare:

``` text
first  → snap ID
second → value
```

`{snap_id, INT_MAX}` moves past every record with the requested ID.

------------------------------------------------------------------------

# 6. Dry Run

Start:

``` text
currentSnapId = 0
history[0] = [(0,0)]
```

### `set(0,5)`

``` text
[(0,0)]
    ↓
[(0,5)]
```

### `set(0,10)`

Same version:

``` text
[(0,5)]
    ↓
[(0,10)]
```

### `snap()`

``` text
returns 0

currentSnapId:
0 → 1
```

History:

``` text
[(0,10)]
```

### `snap()`

No changes:

``` text
returns 1

currentSnapId:
1 → 2
```

History still:

``` text
[(0,10)]
```

### `set(0,5)`

New version:

``` text
[(0,10), (2,5)]
```

### `get(0,1)`

Timeline:

``` text
snap       0          1          2
           │──────────│──────────│
value     10         10          5
                       ↑
                     query
```

Stored changes:

``` text
(0,10)               (2,5)
    \_________________/
          gap
```

`upper_bound(1)` finds `(2,5)`.

Step left:

``` text
(0,10)
```

Answer:

``` text
10
```

------------------------------------------------------------------------

# 7. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class SnapshotArray {
    int currentSnapId = 0;

    vector<vector<pair<int,int>>> history;

public:
    SnapshotArray(int length)
        : history(
            length,
            vector<pair<int,int>>{{0,0}}
          ) {}

    void set(int index, int val) {
        auto& records = history[index];

        if (records.back().first == currentSnapId) {
            // Same pending version.
            records.back().second = val;
        } else {
            // Preserve old snapshots.
            records.push_back({currentSnapId, val});
        }
    }

    int snap() {
        return currentSnapId++;
    }

    int get(int index, int snap_id) {
        const auto& records = history[index];

        auto it = upper_bound(
            records.begin(),
            records.end(),
            make_pair(snap_id, INT_MAX)
        );

        --it;
        return it->second;
    }
};
```

------------------------------------------------------------------------

# 8. Complexity & Mistakes

  Operation                               Time
  ------------- ------------------------------
  Constructor                      `O(length)`
  `set()`                     amortized `O(1)`
  `snap()`                              `O(1)`
  `get()`                           `O(log H)`
  Space           `O(length + stored changes)`

`H` = history size of the queried index.

Common mistakes:

``` text
copy complete array on snap()              ✗
search only exact snap ID                  ✗
overwrite completed snapshot               ✗
store every set in same version            ✗
forget initial {0,0}                       ✗
```

Important:

``` text
get(index,3)
```

does not need a record with ID `3`.

It needs:

``` text
largest stored ID <= 3
```

------------------------------------------------------------------------

# 9. Mental Model

Think of every index as its own **version timeline**:

``` text
INDEX 0

snap 0             snap 2             snap 5
  │                  │                  │
  ▼                  ▼                  ▼
 10 ───────────────► 20 ─────────────► 7
 │                   │                  │
 └─ valid 0..1       └─ valid 2..4      └─ valid 5...
```

Operations:

``` text
SET
 │
 ├─ same current version?
 │       └─ overwrite
 │
 └─ later version?
         └─ append


SNAP
 │
 └─ return currentSnapId++


GET
 │
 ├─ upper_bound(target)
 ├─ step left
 └─ return value
```

Remember:

``` text
SET  → store change point
SNAP → advance version
GET  → predecessor search
```

> **One sentence:** Snapshot Array is a per-index version history: `set`
> stores change points, `snap` advances the version, and `get`
> binary-searches the latest change at or before the requested snapshot.
