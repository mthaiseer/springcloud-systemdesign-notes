# AlgoZenith — LRU Cache (STL)
## Visual Self-Learning Version

> **Core idea:** `unordered_map` finds a key fast; `list` maintains **MRU → LRU** order. Keep both synchronized.

# Table of Contents

- [0. Prerequisites](#0-prerequisites)
- [1. Problem](#1-problem)
- [2. Why Two Structures Are Needed](#2-why-two-structures-are-needed)
- [3. STL List + Iterator + Splice](#3-stl-list--iterator--splice)
- [4. Final Design](#4-final-design)
- [5. State Invariants](#5-state-invariants)
- [6. get(key) — Step by Step](#6-getkey--step-by-step)
- [7. put(key,value) — Existing Key](#7-putkeyvalue--existing-key)
- [8. put(key,value) — New Key](#8-putkeyvalue--new-key)
- [9. Full Dry Run](#9-full-dry-run)
- [10. Complete C++17](#10-complete-c17)
- [11. Complexity](#11-complexity)
- [12. Common Mistakes](#12-common-mistakes)
- [13. Recognition Checklist](#13-recognition-checklist)
- [14. Final Mental Model](#14-final-mental-model)

---

# 0. Prerequisites

## 0.1 What Is a Cache?

A cache stores useful key-value data for fast access.

```text
key -> value

1 -> 10
2 -> 20
3 -> 30
```

A cache usually has limited capacity.

```text
capacity = 3
```

If a new key arrives when the cache is full, one existing key must be removed.

---

## 0.2 What Does LRU Mean?

LRU means:

```text
Least Recently Used
```

The least recently used key is the one that has not been accessed for the longest time.

Example:

```text
MRU                         LRU
 |                           |
 v                           v

[3] -> [1] -> [2]
```

Meaning:

```text
3 = most recently used
2 = least recently used
```

If the cache is full and a new key arrives:

```text
evict 2
```

---

## 0.3 MRU vs LRU

```text
MRU = Most Recently Used
LRU = Least Recently Used
```

In this design:

```text
front = MRU
back  = LRU
```

ASCII:

```text
front                                      back
  |                                          |
  v                                          v

[MRU] <-> [...] <-> [...] <-> [LRU]
```

---

## 0.4 unordered_map Basics

We need fast lookup:

```text
Does key exist?
Where is its cache node?
```

`unordered_map` gives average:

```text
O(1)
```

lookup.

But a hash map does not remember usage order.

---

## 0.5 std::list Basics

`std::list` is a doubly linked list.

```text
[A] <-> [B] <-> [C]
```

Each node links to the previous and next node.

Useful because an existing node can be removed or moved in `O(1)` when we already have its iterator.

---

## 0.6 What Is an Iterator?

An iterator is a pointer-like handle to a container node.

Example:

```text
list:

[1,10] <-> [2,20] <-> [3,30]
              ^
              |
           iterator
```

If the map stores that iterator:

```text
mp[2] -> [2,20]
```

we can reach the exact node directly.

No list scan is needed.

---

# 1. Problem

Support:

```text
get(key)
put(key, value)
```

Rules:

```text
get(key)
-> return value
-> mark key as MRU

put(key,value)
-> insert/update
-> mark key as MRU

capacity exceeded
-> evict LRU
```

Target:

```text
get -> average O(1)
put -> average O(1)
```

---

## 1.1 Tiny Example

```text
capacity = 2
```

After:

```text
put(1,10)
put(2,20)
```

order:

```text
MRU         LRU

2 -> 1
```

Now:

```text
get(1)
```

makes `1` most recently used:

```text
1 -> 2
```

Now:

```text
put(3,30)
```

cache is full, so evict `2`:

```text
3 -> 1
```

---

# 2. Why Two Structures Are Needed

Do not start with:

```text
"Which STL container should I use?"
```

Start with:

```text
"What operations must be fast?"
```

---

## 2.1 Fast Lookup

For:

```text
get(key)
```

we need:

```text
find key quickly
```

Use:

```cpp
unordered_map
```

---

## 2.2 Fast Recency Updates

We also need:

```text
move used key to MRU
remove LRU
```

Use:

```cpp
list
```

---

## 2.3 Why unordered_map Alone Is Not Enough

It can answer:

```text
Does key exist?
```

But not efficiently:

```text
Which key is least recently used?
```

---

## 2.4 Why list Alone Is Not Enough

A list remembers order:

```text
MRU -> ... -> LRU
```

But finding a key requires scanning:

```text
O(n)
```

---

## 2.5 Combined Design

```text
unordered_map
    |
    +--> WHERE is the key?

list
    |
    +--> HOW RECENT is the key?
```

ASCII:

```text
get(key)
   |
   +----> unordered_map ----> exact node
                             |
                             v
                           list
                             |
                             v
                      move to MRU
```

---

# 3. STL List + Iterator + Splice

Use:

```cpp
list<pair<int,int>> dll;
```

Each node stores:

```text
{key, value}
```

---

## 3.1 Operations We Need

| Operation | Meaning | Time |
|---|---|---:|
| `push_front(x)` | add new MRU | `O(1)` |
| `begin()` | iterator to MRU | `O(1)` |
| `back()` | access LRU | `O(1)` |
| `pop_back()` | remove LRU | `O(1)` |
| `splice(begin(), dll, it)` | move node to MRU | `O(1)` |

---

## 3.2 Why splice Is Perfect

Before:

```text
MRU                   LRU

[3] <-> [2] <-> [1]
                  ^
                  |
                access
```

After accessing `1`:

```text
[1] <-> [3] <-> [2]
```

We do not need to delete and recreate the node.

Use:

```cpp
dll.splice(dll.begin(), dll, it);
```

---

## 3.3 ASCII splice

```text
BEFORE
======

front                         back
  |                             |
  v                             v

[3] <-> [2] <-> [1]
                  ^
                  |
                 it


MOVE NODE TO FRONT
==================

[1] <-> [3] <-> [2]
 ^
 |
new MRU
```

The iterator remains valid because the node itself is moved, not recreated.

---

# 4. Final Design

Use:

```cpp
list<pair<int,int>> dll;

unordered_map<
    int,
    list<pair<int,int>>::iterator
> mp;
```

---

## 4.1 Meaning

List:

```text
front = MRU
back  = LRU
```

Example:

```text
front                              back
  |                                  |
  v                                  v

[3,30] <-> [1,10] <-> [2,20]
  MRU                              LRU
```

Map:

```text
mp[3] -> [3,30]
mp[1] -> [1,10]
mp[2] -> [2,20]
```

---

## 4.2 Full ASCII Architecture

```text
                unordered_map
              key -> iterator
                    |
      +-------------+-------------+
      |             |             |
      v             v             v

mp[3] --------> [3,30]
mp[1] --------------------> [1,10]
mp[2] --------------------------------> [2,20]


                 LIST ORDER

MRU                                        LRU
 |                                          |
 v                                          v

[3,30] <--------> [1,10] <--------> [2,20]
```

---

# 5. State Invariants

After every operation:

```text
1. front = MRU

2. back = LRU

3. every active key appears exactly once in list

4. mp[key] points to that exact list node

5. mp.size() == dll.size()

6. size <= capacity
```

---

## 5.1 Why Synchronization Matters

Suppose we remove node `2` from the list but forget:

```cpp
mp.erase(2);
```

Then:

```text
map says 2 exists
```

but the iterator points to a removed node.

The two structures disagree.

So every insertion/eviction must keep both synchronized.

---

# 6. get(key) — Step by Step

## Step 1 — Find Key

```cpp
auto it = mp.find(key);
```

If missing:

```cpp
if (it == mp.end()) {
    return -1;
}
```

---

## Step 2 — Move Node to MRU

The key was just used.

So move its existing list node to the front:

```cpp
dll.splice(
    dll.begin(),
    dll,
    it->second
);
```

---

## Step 3 — Return Value

The node stores:

```text
{key, value}
```

So:

```cpp
it->second->second
```

means:

```text
map iterator
   -> list iterator
      -> pair.second
         -> value
```

---

## 6.1 ASCII Dry Run

Before:

```text
MRU         LRU

2 -> 1
     ^
     |
   get(1)
```

Find:

```text
mp[1] -> node 1
```

Promote:

```text
before:
2 -> 1

after:
1 -> 2
```

Return:

```text
10
```

---

## 6.2 get Flow

```text
get(key)
   |
   v
find key in map
   |
   +---- missing ----> return -1
   |
   v
get iterator to list node
   |
   v
splice node to front
   |
   v
node becomes MRU
   |
   v
return value
```

---

## 6.3 C++17

```cpp
int get(int key) {
    auto it = mp.find(key);

    if (it == mp.end()) {
        return -1;
    }

    dll.splice(
        dll.begin(),
        dll,
        it->second
    );

    return it->second->second;
}
```

---

# 7. put(key,value) — Existing Key

If the key already exists:

```text
do not create a new node
```

Instead:

```text
1. update value
2. move same node to front
3. return
```

---

## 7.1 Step-by-Step

```text
put(existing key, value)

Step 1:
find key

Step 2:
update node value

Step 3:
splice node to front

Step 4:
return
```

---

## 7.2 ASCII Example

Before:

```text
MRU                           LRU

[3,30] -> [1,10] -> [2,20]
```

Call:

```text
put(1,100)
```

Update:

```text
[1,10]
   |
   v
[1,100]
```

Promote:

```text
MRU                           LRU

[1,100] -> [3,30] -> [2,20]
```

No eviction because the number of nodes did not increase.

---

## 7.3 C++17

```cpp
if (it != mp.end()) {
    it->second->second = value;

    dll.splice(
        dll.begin(),
        dll,
        it->second
    );

    return;
}
```

---

# 8. put(key,value) — New Key

If the key does not exist:

```text
insert it as MRU
```

---

## Step 1 — Add to Front

```cpp
dll.push_front({key, value});
```

New node is now MRU.

---

## Step 2 — Save Iterator

```cpp
mp[key] = dll.begin();
```

Now the map knows exactly where the node is.

---

## Step 3 — Check Capacity

If:

```text
size > capacity
```

we must evict the LRU.

LRU lives at:

```text
back
```

---

## Step 4 — Remove LRU from Both Structures

Get LRU key:

```cpp
int oldKey = dll.back().first;
```

Remove from map:

```cpp
mp.erase(oldKey);
```

Remove from list:

```cpp
dll.pop_back();
```

---

## 8.1 ASCII Example

Before:

```text
capacity = 2

MRU         LRU

1 -> 2
```

Call:

```text
put(3,30)
```

Insert new MRU:

```text
3 -> 1 -> 2
```

Now:

```text
size = 3
capacity = 2
```

Evict back:

```text
2
```

Final:

```text
3 -> 1
```

---

## 8.2 New-Key Flow

```text
put(new key,value)
        |
        v
push_front({key,value})
        |
        v
mp[key] = begin()
        |
        v
size > capacity ?
    /       \
   no       yes
   |         |
 done        v
        oldKey = back().first
             |
             v
        erase oldKey from map
             |
             v
          pop_back()
```

---

# 9. Full Dry Run

```text
capacity = 2
```

Start:

```text
dll = []
mp  = {}
```

---

## Step 1 — put(1,10)

```text
MRU
 |
 v

[1,10]
```

Map:

```text
1 -> node [1,10]
```

Order:

```text
MRU -> 1 <- LRU
```

---

## Step 2 — put(2,20)

New node goes to front:

```text
[2,20] -> [1,10]
```

Order:

```text
MRU -> 2 -> 1 <- LRU
```

---

## Step 3 — get(1)

Find node `1`.

Before:

```text
2 -> 1
```

Promote:

```text
1 -> 2
```

Return:

```text
10
```

Now:

```text
MRU -> 1 -> 2 <- LRU
```

---

## Step 4 — put(3,30)

Insert at front:

```text
3 -> 1 -> 2
```

Overflow:

```text
size = 3
capacity = 2
```

LRU:

```text
2
```

Remove from map and list.

Final:

```text
MRU -> 3 -> 1 <- LRU
```

---

## Step 5 — get(2)

```text
2 not in map
```

Return:

```text
-1
```

---

## 9.1 State Table

| Step | Operation | MRU → LRU | Result |
|---:|---|---|---|
| 0 | Start | `[]` | — |
| 1 | `put(1,10)` | `1` | — |
| 2 | `put(2,20)` | `2 -> 1` | — |
| 3 | `get(1)` | `1 -> 2` | `10` |
| 4 | `put(3,30)` | `3 -> 1` | evict `2` |
| 5 | `get(2)` | `3 -> 1` | `-1` |

---

# 10. Complete C++17

```cpp
#include <bits/stdc++.h>
using namespace std;

class LRUCache {
    int capacity;

    // front = MRU, back = LRU
    list<pair<int, int>> dll;

    // key -> node in list
    unordered_map<
        int,
        list<pair<int, int>>::iterator
    > mp;

public:
    LRUCache(int cap) : capacity(max(0, cap)) {}

    int get(int key) {
        auto it = mp.find(key);

        if (it == mp.end()) {
            return -1;
        }

        // Used now -> make MRU
        dll.splice(
            dll.begin(),
            dll,
            it->second
        );

        return it->second->second;
    }

    void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        auto it = mp.find(key);

        // Existing key
        if (it != mp.end()) {
            it->second->second = value;

            dll.splice(
                dll.begin(),
                dll,
                it->second
            );

            return;
        }

        // New key -> MRU
        dll.push_front({key, value});
        mp[key] = dll.begin();

        // Overflow -> evict LRU
        if ((int)dll.size() > capacity) {
            int oldKey = dll.back().first;

            mp.erase(oldKey);
            dll.pop_back();
        }
    }
};
```

---

# 11. Complexity

| Operation | Complexity |
|---|---:|
| `get` | average `O(1)` |
| `put` | average `O(1)` |
| `splice` | `O(1)` |
| `push_front` | `O(1)` |
| `pop_back` | `O(1)` |
| Space | `O(capacity)` |

Why?

```text
unordered_map lookup
-> average O(1)

list move/remove
-> O(1)
```

---

# 12. Common Mistakes

## Mistake 1 — get Hit but Do Not Promote

Wrong:

```text
get(1)
return value
leave order unchanged
```

Correct:

```text
get hit
-> move to front
-> return
```

---

## Mistake 2 — Evict from Front

Wrong:

```text
pop_front()
```

because front is MRU.

Correct:

```text
pop_back()
```

because back is LRU.

---

## Mistake 3 — Remove Only from List

Wrong:

```text
pop_back()
```

without removing the key from the map.

Correct:

```text
oldKey = back().first
mp.erase(oldKey)
pop_back()
```

---

## Mistake 4 — Create Duplicate Existing Key

For:

```text
put(existingKey, newValue)
```

do not create a second node.

Correct:

```text
update existing node
+
splice it to front
```

---

## Mistake 5 — Map Stores Only Value

This is insufficient for O(1) promotion:

```cpp
unordered_map<int,int>
```

We need:

```text
key -> list iterator
```

so we can move the node directly.

---

## Mistake 6 — Scan the List

Searching the list by key costs:

```text
O(n)
```

That defeats the LRU design.

Use the map to jump directly to the node.

---

## Mistake 7 — Forget capacity = 0

If capacity is zero:

```text
no inserted key should remain
```

Handle it explicitly.

---

# 13. Recognition Checklist

Think LRU Cache when you see:

```text
limited capacity
+
recently used order
+
evict least recently used
+
fast get/put
```

Ask:

```text
1. Need fast lookup?
   -> unordered_map

2. Need recency order?
   -> list

3. Where should MRU be?
   -> front

4. Where should LRU be?
   -> back

5. How do I move an existing node?
   -> splice

6. What should the map store?
   -> iterator to list node

7. get hit?
   -> move to front

8. existing put?
   -> update + move to front

9. new put?
   -> push_front + save iterator

10. overflow?
    -> erase back key from map
    -> pop_back from list
```

---

# 14. Final Mental Model

```text
                 unordered_map
                 key -> iterator
                       |
                       v

MRU                                         LRU
 |                                           |
 v                                           v

[A] <-> [B] <-> [C] <-> [D]

 ^                                           ^
 |                                           |
used key                                  evict here
moves here
```

Operations:

```text
GET
===
find
  |
  v
splice to front
  |
  v
return value
```

```text
PUT EXISTING
============
find
  |
  v
update value
  |
  v
splice to front
```

```text
PUT NEW
=======
push_front
  |
  v
store iterator in map
  |
  v
capacity exceeded?
  |
  v
erase back key from map
  |
  v
pop_back
```

The one sentence to remember:

```text
unordered_map finds the node;
list remembers recency;
splice moves a used node to MRU in O(1).
```
