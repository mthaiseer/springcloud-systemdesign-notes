# AlgoZenith --- LRU Cache (STL)

> **Core idea:** `unordered_map` finds a key fast; `list` maintains
> **MRU → LRU** order. Keep both synchronized.

## TOC

-   [1. Problem](#1-problem)
-   [2. STL List You Need](#2-stl-list-you-need)
-   [3. Design](#3-design)
-   [4. get(key)](#4-getkey)
-   [5. put(key,value)](#5-putkeyvalue)
-   [6. Dry Run](#6-dry-run)
-   [7. Complete C++](#7-complete-c)
-   [8. Complexity & Mistakes](#8-complexity--mistakes)
-   [9. Mental Model](#9-mental-model)

------------------------------------------------------------------------

# 1. Problem

LRU = **Least Recently Used**.

``` text
get(key)       → return value + mark MRU
put(key,value) → insert/update + mark MRU
capacity full  → evict LRU
```

Target: average `O(1)` for `get` and `put`.

Why two structures?

  Need                 STL
  -------------------- -----------------
  Find key             `unordered_map`
  Keep recency order   `list`
  Move node to front   `list::splice`
  Remove oldest        `pop_back()`

------------------------------------------------------------------------

# 2. STL List You Need

`std::list` is a doubly linked list.

``` cpp
list<pair<int,int>> dll;
```

For LRU:

  Method                       Use                    Time
  ---------------------------- ------------------ --------
  `push_front(x)`              add MRU              `O(1)`
  `begin()`                    iterator to MRU      `O(1)`
  `back()`                     access LRU           `O(1)`
  `pop_back()`                 remove LRU           `O(1)`
  `splice(begin(), dll, it)`   move node to MRU     `O(1)`

### `splice`

``` text
before: [3] [2] [1]
         MRU     LRU

access 1

after : [1] [3] [2]
         MRU     LRU
```

``` cpp
dll.splice(dll.begin(), dll, it);
```

The existing node is moved; it is not recreated.

------------------------------------------------------------------------

# 3. Design

``` cpp
list<pair<int,int>> dll;

unordered_map<
    int,
    list<pair<int,int>>::iterator
> mp;
```

Meaning:

``` text
dll:

front                         back
 ↓                             ↓
[3,30] ⇄ [1,10] ⇄ [2,20]
 MRU                           LRU

mp[3] ──→ [3,30]
mp[1] ──→ [1,10]
mp[2] ──→ [2,20]
```

Invariants:

``` text
front = MRU
back  = LRU

one key → one list node
mp[key] → that node

size <= capacity
```

------------------------------------------------------------------------

# 4. get(key)

### Step 1 --- Find

``` cpp
auto it = mp.find(key);

if (it == mp.end())
    return -1;
```

### Step 2 --- Move to MRU

``` cpp
dll.splice(
    dll.begin(),
    dll,
    it->second
);
```

### Step 3 --- Return value

Node stores:

``` text
{key,value}
```

so:

``` cpp
return it->second->second;
```

Mini dry run:

``` text
before: 2 → 1
             ↑ get(1)

move 1 to front

after : 1 → 2

return 10
```

------------------------------------------------------------------------

# 5. put(key,value)

## Existing Key

``` text
find
 ↓
update value
 ↓
move same node to front
```

``` cpp
if (it != mp.end()) {
    it->second->second = value;
    dll.splice(dll.begin(), dll, it->second);
    return;
}
```

No new node, so no eviction.

## New Key

Insert as MRU:

``` cpp
dll.push_front({key, value});
mp[key] = dll.begin();
```

If capacity exceeded:

``` text
back = LRU
```

``` cpp
int oldKey = dll.back().first;

mp.erase(oldKey);
dll.pop_back();
```

Eviction must remove from **both** structures.

------------------------------------------------------------------------

# 6. Dry Run

``` text
capacity = 2
```

### `put(1,10)`

``` text
MRU → 1 ← LRU
mp = {1}
```

### `put(2,20)`

``` text
MRU → 2 → 1 ← LRU
mp = {1,2}
```

### `get(1)`

`1` exists, so promote it:

``` text
before: 2 → 1
after : 1 → 2

return 10
```

### `put(3,30)`

Insert `3` as MRU:

``` text
3 → 1 → 2
```

Now:

``` text
size = 3
capacity = 2
```

LRU is `2`, so evict it:

``` text
mp.erase(2)
pop_back()

MRU → 3 → 1 ← LRU
```

### `get(2)`

``` text
2 not in mp
→ return -1
```

Final:

``` text
3 → 1
```

------------------------------------------------------------------------

# 7. Complete C++

``` cpp
#include <bits/stdc++.h>
using namespace std;

class LRUCache {
    int capacity;

    // front = MRU, back = LRU
    list<pair<int,int>> dll;

    // key -> node in list
    unordered_map<
        int,
        list<pair<int,int>>::iterator
    > mp;

public:
    LRUCache(int cap) : capacity(max(0, cap)) {}

    int get(int key) {
        auto it = mp.find(key);

        if (it == mp.end())
            return -1;

        // Used now -> make MRU
        dll.splice(dll.begin(), dll, it->second);

        return it->second->second;
    }

    void put(int key, int value) {
        if (capacity == 0)
            return;

        auto it = mp.find(key);

        // Existing key
        if (it != mp.end()) {
            it->second->second = value;
            dll.splice(dll.begin(), dll, it->second);
            return;
        }

        // New key -> MRU
        dll.push_front({key, value});
        mp[key] = dll.begin();

        // Too many entries -> evict LRU
        if ((int)dll.size() > capacity) {
            int oldKey = dll.back().first;

            mp.erase(oldKey);
            dll.pop_back();
        }
    }
};
```

------------------------------------------------------------------------

# 8. Complexity & Mistakes

  Operation         Complexity
  ----------- ----------------
  `get`         Average `O(1)`
  `put`         Average `O(1)`
  `splice`              `O(1)`
  Space          `O(capacity)`

`unordered_map` operations are average `O(1)`.

Common mistakes:

``` text
get hit but don't move to front   ✗
evict from front                  ✗
remove only from list             ✗
create duplicate existing key     ✗
map stores only value             ✗
```

Correct:

``` text
get hit        → promote to front
existing put   → update + promote
new put        → push_front
overflow       → erase map key + pop_back
```

------------------------------------------------------------------------

# 9. Mental Model

``` text
              unordered_map
              key → iterator
                    │
                    ↓

MRU                                      LRU
 ↓                                        ↓
[A] ⇄ [B] ⇄ [C] ⇄ [D]
 ↑                  ↑
used key          evict here
moves here
```

``` text
GET
find → splice to front → return

PUT existing
find → update → splice to front

PUT new
push_front → map it
              ↓
         capacity exceeded?
              ↓
       erase back from map
              ↓
           pop_back
```

> **Remember:** `unordered_map` finds the node; `list` remembers
> recency; `splice` promotes a used node to MRU in `O(1)`.
